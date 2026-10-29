/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

public class StealBuff
extends Skill {
    private final int apv;
    private final int apw;

    public StealBuff(StatsSet statsSet) {
        super(statsSet);
        this.apv = statsSet.getInteger("StealCount", 1);
        this.apw = statsSet.getInteger("ChanceMod", 0);
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        if (creature2 == null || !creature2.isPlayer()) {
            creature.sendPacket((IStaticPacket)Msg.THAT_IS_THE_INCORRECT_TARGET);
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null) continue;
            double d = 1.0 - creature2.calcStat(Stats.CANCEL_RESIST, 0.0, null, null) * 0.01;
            Effect[] effectArray = creature2.getEffectList().getAllFirstEffects();
            LinkedList<Effect> linkedList = new LinkedList<Effect>();
            for (Effect.EEffectSlot eEffectSlot : Effect.EEffectSlot.VALUES) {
                for (Effect effect3 : effectArray) {
                    Skill skill;
                    if (effect3 == null || effect3.getTemplate()._applyOnCaster || effect3.getEffectSlot() != eEffectSlot || !(skill = effect3.getSkill()).isCancelable() || !skill.isActive() || skill.isOffensive() || skill.isToggle() || skill.isTrigger()) continue;
                    linkedList.add(effect3);
                }
            }
            boolean bl = false;
            Iterator iterator = linkedList.descendingIterator();
            int n = 0;
            while (iterator.hasNext() && n++ < this.apv) {
                Effect effect = (Effect)iterator.next();
                if (!StealBuff.calcSkillCancel(this, effect, this.apw, d, true)) continue;
                Skill skill = effect.getSkill();
                for (Effect effect2 : creature2.getEffectList().getEffectsBySkill(skill)) {
                    Effect effect3;
                    if (effect2 == null) continue;
                    effect3 = effect2.getTemplate().getEffect(new Env(creature, creature, skill));
                    effect3.setCount(effect2.getCount());
                    if (effect2.getCount() == 1) {
                        effect3.setPeriod(effect2.getPeriod() - effect2.getTime());
                    } else {
                        effect3.setPeriod(effect2.getPeriod());
                    }
                    bl = true;
                    effect2.exit();
                    creature.getEffectList().addEffect(effect3);
                }
                creature2.sendPacket((IStaticPacket)new SystemMessage(92).addSkillName(skill.getId(), skill.getLevel()));
            }
            if (bl) {
                creature2.sendChanges();
                creature2.updateEffectIcons();
                creature.sendChanges();
                creature.updateEffectIcons();
            }
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false);
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }

    public static boolean calcSkillCancel(Skill skill, Effect effect, int n, double d, boolean bl) {
        int n2 = Math.max(0, skill.getMagicLevel() - effect.getSkill().getMagicLevel());
        int n3 = (int)((double)((long)(2 * n2 + n) + effect.getPeriod() * (long)effect.getCount() / 120000L) * d);
        return Rnd.chance(Math.max(Config.SKILLS_DISPEL_MOD_MIN, Math.min(Config.SKILLS_DISPEL_MOD_MAX, n3)));
    }
}

