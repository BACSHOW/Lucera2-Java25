/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SkillList
extends L2GameServerPacket {
    private List<SkillListRecord> amC;

    public SkillList(Player player) {
        Collection<Skill> collection = player.getAllSkills();
        this.amC = new ArrayList<SkillListRecord>(collection.size());
        for (Skill skill : collection) {
            this.amC.add(new SkillListRecord(player, skill));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(88);
        this.writeD(this.amC.size());
        for (SkillListRecord skillListRecord : this.amC) {
            skillListRecord.writeRecord();
        }
    }

    private class SkillListRecord
    implements Comparable<SkillListRecord> {
        private final int _id;
        private final int acS;
        private final boolean Ai;
        private final int _order;

        public SkillListRecord(Player player, Skill skill) {
            this._id = skill.getDisplayId();
            this.acS = skill.getDisplayLevel();
            this.Ai = player.isUnActiveSkill(skill.getId());
            this._order = skill.isActive() || skill.isToggle() ? 0 : 1;
        }

        public void writeRecord() {
            SkillList.this.writeD(this._order);
            SkillList.this.writeD(this.acS);
            SkillList.this.writeD(this._id);
            SkillList.this.writeC(this.Ai ? 1 : 0);
        }

        @Override
        public int compareTo(SkillListRecord skillListRecord) {
            return this._id - skillListRecord._id;
        }
    }
}

