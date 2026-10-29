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

public class ManaHealPercent
extends Skill {
    private final boolean aoL;

    public ManaHealPercent(StatsSet statsSet) {
        super(statsSet);
        this.aoL = statsSet.getBool("ignoreMpEff", true);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null || creature2.isDead() || creature2.isHealBlocked()) continue;
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false);
            double d = this._power * (double)creature2.getMaxMp() / 100.0;
            double d2 = d * (!this.aoL ? creature2.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100.0, creature, this) : 100.0) / 100.0;
            double d3 = Math.max(0.0, Math.min(d2, creature2.calcStat(Stats.MP_LIMIT, null, null) * (double)creature2.getMaxMp() / 100.0 - creature2.getCurrentMp()));
            if (d3 > 0.0) {
                creature2.setCurrentMp(creature2.getCurrentMp() + d3);
            }
            if (!creature2.isPlayer()) continue;
            if (creature != creature2) {
                creature2.sendPacket((IStaticPacket)new SystemMessage(1069).addString(creature.getName()).addNumber(Math.round(d3)));
                continue;
            }
            creature.sendPacket((IStaticPacket)new SystemMessage(1068).addNumber(Math.round(d3)));
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

