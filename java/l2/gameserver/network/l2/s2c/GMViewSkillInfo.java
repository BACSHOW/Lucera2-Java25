/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Collection;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class GMViewSkillInfo
extends L2GameServerPacket {
    private String agb;
    private Collection<Skill> ajk;
    private Player ajl;

    public GMViewSkillInfo(Player player) {
        this.agb = player.getName();
        this.ajk = player.getAllSkills();
        this.ajl = player;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(145);
        this.writeS(this.agb);
        this.writeD(this.ajk.size());
        for (Skill skill : this.ajk) {
            this.writeD(skill.isPassive() ? 1 : 0);
            this.writeD(skill.getDisplayLevel());
            this.writeD(skill.getId());
            this.writeC(this.ajl.isUnActiveSkill(skill.getId()) ? 1 : 0);
        }
    }
}

