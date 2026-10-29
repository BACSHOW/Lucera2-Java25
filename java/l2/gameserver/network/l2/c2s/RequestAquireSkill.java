/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.VillageMasterInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.c2s.RequestExEnchantSkill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.SkillTable;

public class RequestAquireSkill
extends L2GameClientPacket {
    private static final AcquireType[] aba = new AcquireType[]{AcquireType.NORMAL, AcquireType.FISHING, AcquireType.CLAN};
    private AcquireType abb;
    private int _id;
    private int _level;

    @Override
    protected void readImpl() {
        this._id = this.readD();
        this._level = this.readD();
        this.abb = ArrayUtils.valid(aba, this.readD());
    }

    @Override
    protected void runImpl() {
        ClassId classId;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || player.getTransformation() != 0 || player.isCursedWeaponEquipped() || this.abb == null) {
            return;
        }
        NpcInstance npcInstance = player.getLastNpc();
        if (npcInstance == null || !npcInstance.isInActingRange(player)) {
            return;
        }
        Skill skill = SkillTable.getInstance().getInfo(this._id, this._level);
        if (skill == null) {
            return;
        }
        int n = player.getVarInt("AcquireSkillClassId", player.getClassId().getId());
        ClassId classId2 = classId = n >= 0 && n < ClassId.VALUES.length ? ClassId.VALUES[n] : null;
        if (!SkillAcquireHolder.getInstance().isSkillPossible(player, classId, skill, this.abb)) {
            return;
        }
        SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, classId, this._id, this._level, this.abb);
        if (skillLearn == null) {
            return;
        }
        if (!RequestAquireSkill.a(player, skillLearn)) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
            return;
        }
        switch (this.abb) {
            case NORMAL: {
                RequestAquireSkill.a(player, skillLearn, skill);
                if (npcInstance == null) break;
                npcInstance.showSkillList(player, classId);
                break;
            }
            case FISHING: {
                RequestAquireSkill.b(player, skillLearn, skill);
                if (npcInstance == null) break;
                NpcInstance.showFishingSkillList(player);
                break;
            }
            case CLAN: {
                RequestAquireSkill.a(player, skillLearn, npcInstance, skill);
            }
        }
    }

    private static void a(Player player, SkillLearn skillLearn, Skill skill) {
        int n = player.getSkillLevel(skillLearn.getId(), 0);
        if (n != skillLearn.getLevel() - 1) {
            return;
        }
        RequestAquireSkill.c(player, skillLearn, skill);
    }

    private static void b(Player player, SkillLearn skillLearn, Skill skill) {
        int n = player.getSkillLevel(skillLearn.getId(), 0);
        if (n != skillLearn.getLevel() - 1) {
            return;
        }
        RequestAquireSkill.d(player, skillLearn, skill);
    }

    private static void c(Player player, SkillLearn skillLearn, Skill skill) {
        if (player.getSp() < (long)skillLearn.getCost()) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
            return;
        }
        if (!Config.ALT_DISABLE_SPELLBOOKS && skillLearn.getItemId() > 0 && !player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount())) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
            return;
        }
        player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));
        player.setSp(player.getSp() - (long)skillLearn.getCost());
        player.addSkill(skill, true);
        player.sendUserInfo();
        player.updateStats();
        player.sendPacket((IStaticPacket)new SkillList(player));
        RequestExEnchantSkill.updateSkillShortcuts(player, skill.getId(), skill.getLevel());
    }

    private static void d(Player player, SkillLearn skillLearn, Skill skill) {
        if (player.getSp() < (long)skillLearn.getCost()) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
            return;
        }
        if (skillLearn.getItemId() > 0 && !player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount())) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
            return;
        }
        player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));
        player.setSp(player.getSp() - (long)skillLearn.getCost());
        player.addSkill(skill, true);
        player.sendUserInfo();
        player.updateStats();
        player.sendPacket((IStaticPacket)new SkillList(player));
        RequestExEnchantSkill.updateSkillShortcuts(player, skill.getId(), skill.getLevel());
    }

    private static void a(Player player, SkillLearn skillLearn, NpcInstance npcInstance, Skill skill) {
        if (!(npcInstance instanceof VillageMasterInstance)) {
            return;
        }
        if (!player.isClanLeader()) {
            player.sendPacket((IStaticPacket)SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }
        Clan clan = player.getClan();
        int n = clan.getSkillLevel(skillLearn.getId(), 0);
        if (n != skillLearn.getLevel() - 1) {
            return;
        }
        if (clan.getReputationScore() < skillLearn.getCost()) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
            return;
        }
        if (skillLearn.getItemId() != 0 && !player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount())) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
            return;
        }
        clan.incReputation(-skillLearn.getCost(), false, "AquireSkill: " + skillLearn.getId() + ", lvl " + skillLearn.getLevel());
        clan.addSkill(skill, true);
        clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill)});
        NpcInstance.showClanSkillList(player);
    }

    private static boolean a(Player player, SkillLearn skillLearn) {
        if (Config.ALT_DISABLE_SPELLBOOKS) {
            return true;
        }
        if (skillLearn.getItemId() == 0) {
            return true;
        }
        if (skillLearn.isClicked()) {
            return false;
        }
        return player.getInventory().getCountOf(skillLearn.getItemId()) >= skillLearn.getItemCount();
    }
}

