/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;

public class EffectCallSkills
extends Effect {
    public EffectCallSkills(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        int[] nArray = this.getTemplate().getParam().getIntegerArray("skillIds");
        int[] nArray2 = this.getTemplate().getParam().getIntegerArray("skillLevels");
        for (int i = 0; i < nArray.length; ++i) {
            Skill skill = SkillTable.getInstance().getInfo(nArray[i], nArray2[i]);
            for (Creature creature : skill.getTargets(this.getEffector(), this.getEffected(), false)) {
                this.getEffector().broadcastPacket(new MagicSkillUse(this.getEffector(), creature, nArray[i], nArray2[i], 0, 0L));
            }
            this.getEffector().callSkill(skill, skill.getTargets(this.getEffector(), this.getEffected(), false), false);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

