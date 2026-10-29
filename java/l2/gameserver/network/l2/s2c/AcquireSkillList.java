/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class AcquireSkillList
extends L2GameServerPacket {
    private AcquireType abb;
    private final List<Skill> _skills;

    public AcquireSkillList(AcquireType acquireType, int n) {
        this._skills = new ArrayList<Skill>(n);
        this.abb = acquireType;
    }

    public void addSkill(int n, int n2, int n3, int n4, int n5, int n6) {
        this._skills.add(new Skill(n, n2, n3, n4, n5, n6));
    }

    public void addSkill(int n, int n2, int n3, int n4, int n5) {
        this._skills.add(new Skill(n, n2, n3, n4, n5, 0));
    }

    @Override
    protected final void writeImpl() {
        this.writeC(138);
        this.writeD(this.abb.ordinal());
        this.writeD(this._skills.size());
        for (Skill skill : this._skills) {
            this.writeD(skill.id);
            this.writeD(skill.nextLevel);
            this.writeD(skill.maxLevel);
            this.writeD(skill.cost);
            this.writeD(skill.requirements);
        }
    }

    class Skill {
        public int id;
        public int nextLevel;
        public int maxLevel;
        public int cost;
        public int requirements;
        public int subUnit;

        Skill(int n, int n2, int n3, int n4, int n5, int n6) {
            this.id = n;
            this.nextLevel = n2;
            this.maxLevel = n3;
            this.cost = n4;
            this.requirements = n5;
            this.subUnit = n6;
        }
    }
}

