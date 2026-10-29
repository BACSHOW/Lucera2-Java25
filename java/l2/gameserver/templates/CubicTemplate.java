/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

import gnu.trove.TIntIntHashMap;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import l2.gameserver.model.Skill;

public class CubicTemplate {
    private final int _id;
    private final int _level;
    private final int arE;
    private List<Map.Entry<Integer, List<SkillInfo>>> _skills = new ArrayList<Map.Entry<Integer, List<SkillInfo>>>(3);

    public CubicTemplate(int n, int n2, int n3) {
        this._id = n;
        this._level = n2;
        this.arE = n3;
    }

    public void putSkills(int n, List<SkillInfo> list) {
        this._skills.add(new AbstractMap.SimpleImmutableEntry<Integer, List<SkillInfo>>(n, list));
    }

    public Iterable<Map.Entry<Integer, List<SkillInfo>>> getSkills() {
        return this._skills;
    }

    public int getDelay() {
        return this.arE;
    }

    public int getId() {
        return this._id;
    }

    public int getLevel() {
        return this._level;
    }

    public static enum ActionType {
        ATTACK,
        DEBUFF,
        CANCEL,
        HEAL;

    }

    public static class SkillInfo {
        private final Skill _skill;
        private final int cJ;
        private final ActionType arG;
        private final boolean arH;
        private final int arI;
        private final int arJ;
        private final TIntIntHashMap arK;

        public SkillInfo(Skill skill, int n, ActionType actionType, boolean bl, int n2, int n3, TIntIntHashMap tIntIntHashMap) {
            this._skill = skill;
            this.cJ = n;
            this.arG = actionType;
            this.arH = bl;
            this.arI = n2;
            this.arJ = n3;
            this.arK = tIntIntHashMap;
        }

        public int getChance() {
            return this.cJ;
        }

        public ActionType getActionType() {
            return this.arG;
        }

        public Skill getSkill() {
            return this._skill;
        }

        public boolean isCanAttackDoor() {
            return this.arH;
        }

        public int getMinHp() {
            return this.arI;
        }

        public int getMinHpPercent() {
            return this.arJ;
        }

        public int getChance(int n) {
            return this.arK.get(n);
        }
    }
}

