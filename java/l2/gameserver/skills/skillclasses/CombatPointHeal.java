/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

public class CombatPointHeal
extends Skill {
    private final boolean aoK;

    public CombatPointHeal(StatsSet statsSet) {
        super(statsSet);
        this.aoK = statsSet.getBool("ignoreCpEff", false);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null || creature2.isDead() || creature2.isHealBlocked()) continue;
            double d = this._power * (!this.aoK ? creature2.calcStat(Stats.CPHEAL_EFFECTIVNESS, 100.0, creature, this) : 100.0) / 100.0;
            double d2 = Math.max(0.0, Math.min(d, creature2.calcStat(Stats.CP_LIMIT, null, null) * (double)creature2.getMaxCp() / 100.0 - creature2.getCurrentCp()));
            if (d2 > 0.0) {
                creature2.setCurrentCp(d2 + creature2.getCurrentCp());
            }
            creature2.sendPacket((IStaticPacket)new SystemMessage(1405).addNumber((long)d2));
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false);
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

