/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.Map;
import l2.gameserver.data.xml.holder.EnchantSkillHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.Experience;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExEnchantSkillInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.SkillEnchant;

public class RequestExEnchantSkillInfo
extends L2GameClientPacket {
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
        } else if (skillEnchant2.getEnchantLevel() != 1) {
            player.sendPacket((IStaticPacket)new SystemMessage(1438));
            return;
        }
        int[] nArray = skillEnchant2.getChances();
        int n2 = Experience.LEVEL.length - nArray.length - 1;
        if (player.getLevel() < n2) {
            this.sendPacket((L2GameServerPacket)new SystemMessage(607).addNumber(n2));
            return;
        }
        int n3 = Math.max(0, Math.min(player.getLevel() - n2, nArray.length - 1));
        int n4 = nArray[n3];
        ExEnchantSkillInfo exEnchantSkillInfo = new ExEnchantSkillInfo(skillEnchant2.getSkillId(), skillEnchant2.getSkillLevel(), skillEnchant2.getSp(), skillEnchant2.getExp(), n4);
        if (skillEnchant2.getItemId() > 0 && skillEnchant2.getItemCount() > 0L) {
            exEnchantSkillInfo.addNeededItem(skillEnchant2.getItemId(), skillEnchant2.getItemCount());
        }
        player.sendPacket((IStaticPacket)exEnchantSkillInfo);
    }
}

