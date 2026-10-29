/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.StatTemplate;

public class OptionDataTemplate
extends StatTemplate {
    private final List<Skill> _skills = new ArrayList<Skill>(0);
    private final int _id;

    public OptionDataTemplate(int n) {
        this._id = n;
    }

    public void addSkill(Skill skill) {
        this._skills.add(skill);
    }

    public List<Skill> getSkills() {
        return this._skills;
    }

    public int getId() {
        return this._id;
    }
}

