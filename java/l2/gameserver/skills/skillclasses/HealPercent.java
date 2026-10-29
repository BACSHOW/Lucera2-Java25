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

public class HealPercent
extends Skill {
    public HealPercent(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null || creature2.isHealBlocked()) continue;
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false);
            double d = this._power * (double)creature2.getMaxHp() / 100.0;
            double d2 = Math.max(0.0, Math.min(d, creature2.calcStat(Stats.HP_LIMIT, null, null) * (double)creature2.getMaxHp() / 100.0 - creature2.getCurrentHp()));
            if (d2 > 0.0) {
                creature2.setCurrentHp(d2 + creature2.getCurrentHp(), false);
            }
            if (!creature2.isPlayer()) continue;
            if (creature != creature2) {
                creature2.sendPacket((IStaticPacket)new SystemMessage(1067).addString(creature.getName()).addNumber(Math.round(d2)));
                continue;
            }
            creature.sendPacket((IStaticPacket)new SystemMessage(1066).addNumber(Math.round(d2)));
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

