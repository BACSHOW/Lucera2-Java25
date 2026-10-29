/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.tables;

import gnu.trove.TIntIntHashMap;
import java.util.Map;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.SkillsEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillTable {
    private static final Logger _log = LoggerFactory.getLogger(SkillTable.class);
    private static final SkillTable aqS = new SkillTable();
    private Map<Integer, Map<Integer, Skill>> _skills;
    private TIntIntHashMap aqT;
    private TIntIntHashMap aqU;

    public static final SkillTable getInstance() {
        return aqS;
    }

    public void load() {
        this._skills = SkillsEngine.getInstance().loadAllSkills();
        this.dx();
    }

    public void reload() {
        this.load();
    }

    public Skill getInfo(int n, int n2) {
        Map<Integer, Skill> map = this._skills.get(n);
        if (map == null) {
            return null;
        }
        return map.get(n2);
    }

    public int getMaxLevel(int n) {
        return this.aqT.get(n);
    }

    public int getBaseLevel(int n) {
        return this.aqU.get(n);
    }

    public Map<Integer, Map<Integer, Skill>> getSkills() {
        return this._skills;
    }

    private void dx() {
        this.aqT = new TIntIntHashMap();
        this.aqU = new TIntIntHashMap();
        for (Map<Integer, Skill> map : this._skills.values()) {
            for (Skill skill : map.values()) {
                int n;
                int n2 = skill.getId();
                int n3 = skill.getLevel();
                if (n3 > (n = this.aqT.get(n2))) {
                    this.aqT.put(n2, n3);
                }
                if (this.aqU.get(n2) != 0) continue;
                this.aqU.put(n2, skill.getBaseLevel());
            }
        }
    }
}

