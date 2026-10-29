/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import l2.gameserver.model.Skill;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeSkillList
extends L2GameServerPacket {
    private List<SkillInfo> aly = Collections.emptyList();
    private List<UnitSkillInfo> alz = new ArrayList<UnitSkillInfo>();

    public PledgeSkillList(Clan clan) {
        Collection<Skill> collection = clan.getSkills();
        this.aly = new ArrayList<SkillInfo>(collection.size());
        for (Skill object : collection) {
            this.aly.add(new SkillInfo(object.getId(), object.getLevel()));
        }
        for (SubUnit subUnit : clan.getAllSubUnits()) {
            for (Skill skill : subUnit.getSkills()) {
                this.alz.add(new UnitSkillInfo(subUnit.getType(), skill.getId(), skill.getLevel()));
            }
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(57);
        this.writeD(this.aly.size() + this.alz.size());
        for (SkillInfo skillInfo : this.aly) {
            this.writeD(skillInfo._id);
            this.writeD(skillInfo._level);
        }
        for (UnitSkillInfo unitSkillInfo : this.alz) {
            this.writeD(unitSkillInfo._id);
            this.writeD(unitSkillInfo._level);
        }
    }

    static class UnitSkillInfo
    extends SkillInfo {
        private int _type;

        public UnitSkillInfo(int n, int n2, int n3) {
            super(n2, n3);
            this._type = n;
        }
    }

    static class SkillInfo {
        public int _id;
        public int _level;

        public SkillInfo(int n, int n2) {
            this._id = n;
            this._level = n2;
        }
    }
}

