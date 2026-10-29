/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.EnchantSkillHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.base.Experience;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExEnchantSkillList;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.ShortCutRegister;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.SkillEnchant;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestExEnchantSkill
extends L2GameClientPacket {
    private static final Logger LOG = LoggerFactory.getLogger(RequestExEnchantSkill.class);
    private int _skillId;
    private int abO;

    @Override
    protected void readImpl() {
        this._skillId = this.readD();
        this.abO = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getClassId().getLevel() < 4 || player.getLevel() < 76) {
            player.sendPacket((IStaticPacket)new SystemMessage(1438));
            return;
        }
        Skill skill = player.getKnownSkill(this._skillId);
        if (skill == null) {
            player.sendPacket((IStaticPacket)new SystemMessage(1438));
            return;
        }
        int n = skill.getLevel();
        int n2 = skill.getBaseLevel();
        Map<Integer, SkillEnchant> map = EnchantSkillHolder.getInstance().getLevelsOf(this._skillId);
        if (map == null || map.isEmpty()) {
            player.sendPacket((IStaticPacket)new SystemMessage(1438));
            return;
        }
        SkillEnchant skillEnchant = map.get(n);
        SkillEnchant skillEnchant2 = map.get(this.abO);
        if (skillEnchant2 == null) {
            player.sendPacket((IStaticPacket)new SystemMessage(1438));
            return;
        }
        if (skillEnchant != null) {
            if (skillEnchant.getRouteId() != skillEnchant2.getRouteId() || skillEnchant2.getEnchantLevel() != skillEnchant.getEnchantLevel() + 1) {
                player.sendPacket((IStaticPacket)new SystemMessage(1438));
                return;
            }
        } else if (skillEnchant2.getEnchantLevel() != 1 || n != n2) {
            player.sendPacket((IStaticPacket)new SystemMessage(1438));
            LOG.warn("Player \"" + player.toString() + "\" trying to use enchant  exploit" + skill.toString() + " to " + this.abO + "(enchant level " + skillEnchant2.getEnchantLevel() + ")");
            return;
        }
        int[] nArray = skillEnchant2.getChances();
        int n3 = Experience.LEVEL.length - nArray.length - 1;
        if (player.getLevel() < n3) {
            this.sendPacket((L2GameServerPacket)new SystemMessage(607).addNumber(n3));
            return;
        }
        if (player.getSp() < (long)skillEnchant2.getSp()) {
            this.sendPacket((L2GameServerPacket)new SystemMessage(1443));
            return;
        }
        if (player.getExp() < skillEnchant2.getExp()) {
            this.sendPacket((L2GameServerPacket)new SystemMessage(1444));
            return;
        }
        if (skillEnchant2.getItemId() > 0 && skillEnchant2.getItemCount() > 0L && Functions.removeItem(player, skillEnchant2.getItemId(), skillEnchant2.getItemCount()) < skillEnchant2.getItemCount()) {
            this.sendPacket((L2GameServerPacket)Msg.ITEMS_REQUIRED_FOR_SKILL_ENCHANT_ARE_INSUFFICIENT);
            return;
        }
        int n4 = Math.max(0, Math.min(player.getLevel() - n3, nArray.length - 1));
        int n5 = nArray[n4];
        player.addExpAndSp(-1L * skillEnchant2.getExp(), -1 * skillEnchant2.getSp());
        player.sendPacket((IStaticPacket)new SystemMessage(538).addNumber(skillEnchant2.getSp()));
        player.sendPacket((IStaticPacket)new SystemMessage(539).addNumber(skillEnchant2.getExp()));
        TimeStamp timeStamp = player.getSkillReuse(skill);
        Skill skill2 = null;
        if (Rnd.chance(n5)) {
            skill2 = SkillTable.getInstance().getInfo(skillEnchant2.getSkillId(), skillEnchant2.getSkillLevel());
            player.sendPacket((IStaticPacket)new SystemMessage(1440).addSkillName(this._skillId, this.abO));
            Log.add(player.getName() + "|Successfully enchanted|" + this._skillId + "|to+" + this.abO + "|" + n5, "enchant_skills");
        } else {
            skill2 = SkillTable.getInstance().getInfo(skill.getId(), skill.getBaseLevel());
            player.sendPacket((IStaticPacket)new SystemMessage(1441).addSkillName(this._skillId, this.abO));
            Log.add(player.getName() + "|Failed to enchant|" + this._skillId + "|to+" + this.abO + "|" + n5, "enchant_skills");
        }
        if (timeStamp != null && timeStamp.hasNotPassed()) {
            player.disableSkill(skill2, timeStamp.getReuseCurrent());
        }
        player.addSkill(skill2, true);
        player.sendPacket((IStaticPacket)new SkillList(player));
        RequestExEnchantSkill.updateSkillShortcuts(player, this._skillId, this.abO);
        player.sendPacket((IStaticPacket)ExEnchantSkillList.packetFor(player));
    }

    protected static void updateSkillShortcuts(Player player, int n, int n2) {
        for (ShortCut shortCut : player.getAllShortCuts()) {
            if (shortCut.getId() != n || shortCut.getType() != 2) continue;
            ShortCut shortCut2 = new ShortCut(shortCut.getSlot(), shortCut.getPage(), shortCut.getType(), shortCut.getId(), n2, 1);
            player.sendPacket((IStaticPacket)new ShortCutRegister(player, shortCut2));
            player.registerShortCut(shortCut2);
        }
    }
}

