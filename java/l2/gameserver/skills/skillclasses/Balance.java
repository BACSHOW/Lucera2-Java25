/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

public class Balance
extends Skill {
    public Balance(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        double d = 0.0;
        int n = 0;
        for (Creature creature2 : list) {
            if (creature2 == null || creature2.isAlikeDead()) continue;
            d += creature2.getCurrentHp();
            n += creature2.getMaxHp();
        }
        double d2 = d / (double)n;
        for (Creature creature3 : list) {
            if (creature3 == null || creature3.isAlikeDead()) continue;
            double d3 = (double)creature3.getMaxHp() * d2;
            if (d3 > creature3.getCurrentHp()) {
                double d4 = creature3.calcStat(Stats.HP_LIMIT, null, null) * (double)creature3.getMaxHp() / 100.0;
                if (creature3.getCurrentHp() < d4) {
                    creature3.setCurrentHp(Math.min(d3, d4), false);
                }
            } else {
                creature3.setCurrentHp(Math.max(1.01, d3), false);
            }
            this.getEffects(creature, creature3, this.getActivateRate() > 0, false);
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

