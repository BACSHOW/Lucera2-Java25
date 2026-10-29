/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.HashMap;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;

public class BuffCharger
extends Skill {
    private int aoY;

    public BuffCharger(StatsSet statsSet) {
        super(statsSet);
        this.aoY = statsSet.getInteger("invokeSkillId", 0);
    }

    private void a(Creature creature, Creature creature2, int n, boolean bl) {
        Object object;
        if (creature2 == null) {
            return;
        }
        List<Effect> list = creature2.getEffectList().getEffectsBySkillId(n);
        HashMap<EffectType, EffectState> hashMap = new HashMap<EffectType, EffectState>();
        Integer n2 = null;
        if (list != null) {
            for (Effect object22 : list) {
                n2 = object22.getSkill().getLevel();
                hashMap.put(object22.getEffectType(), new EffectState(object22));
                object22.exit();
            }
        }
        if (n2 == null) {
            if (!bl) {
                return;
            }
            object = SkillTable.getInstance().getInfo(n, 1);
            ((Skill)object).getEffects(creature, creature2, false, false, false);
            return;
        }
        if (bl) {
            object = n2;
            Integer n3 = n2 = Integer.valueOf(n2 + 1);
        } else {
            object = n2;
            Integer n4 = n2 = Integer.valueOf(n2 - 1);
        }
        if (n2 <= 0) {
            return;
        }
        n2 = Math.min(n2, SkillTable.getInstance().getMaxLevel(n));
        object = SkillTable.getInstance().getInfo(n, n2);
        Env env = new Env(creature, creature2, (Skill)object);
        for (EffectTemplate effectTemplate : ((Skill)object).getEffectTemplates()) {
            Effect effect;
            EffectState effectState;
            if (effectTemplate == null || (effectState = (EffectState)hashMap.get((Object)effectTemplate.getEffectType())) == null || (effect = effectTemplate.getEffect(env)) == null || effect.isOneTime()) continue;
            effectState.restore(effect);
            creature2.getEffectList().addEffect(effect);
        }
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null) continue;
            this.a(creature, creature2, this.aoY, true);
        }
    }

    @Override
    public void onAbortCast(Creature creature, Creature creature2) {
        super.onAbortCast(creature, creature2);
        if (creature2 != null && this.isUsingWhileCasting()) {
            this.a(creature, creature2, this.aoY, false);
        }
    }

    private static final class EffectState {
        private final int count;
        private final long time;
        private final long aoH;

        private EffectState(int n, long l, long l2) {
            this.count = n;
            this.time = l;
            this.aoH = l2;
        }

        public EffectState(Effect effect) {
            this(effect.getCount(), effect.getTime(), effect.getPeriod());
        }

        public void restore(Effect effect) {
            effect.setCount(this.count);
            effect.setPeriod(this.count == 1 ? this.aoH - this.time : this.aoH);
        }
    }
}

