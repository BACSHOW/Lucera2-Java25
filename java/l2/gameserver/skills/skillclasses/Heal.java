/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.residences.SiegeFlagInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

public class Heal
extends Skill {
    private final boolean aoJ;
    private final boolean apf;

    public Heal(StatsSet statsSet) {
        super(statsSet);
        this.aoJ = statsSet.getBool("ignoreHpEff", false);
        this.apf = statsSet.getBool("staticPower", this.isHandler());
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        if (creature2 == null || creature2.isDoor() || creature2 instanceof SiegeFlagInstance) {
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        double d = this.getPower();
        if (!this.apf) {
            int n = creature.getMAtk(null, this);
            int n2 = 1;
            int n3 = 0;
            if (this.isSSPossible()) {
                switch (creature.getChargedSpiritShot()) {
                    case 2: {
                        n2 = 4;
                        n3 = this.X(n);
                        break;
                    }
                    case 1: {
                        n2 = 2;
                        n3 = this.X(n) / 2;
                    }
                }
            }
            d += Math.sqrt(n2 * n) + (double)n3;
            if (Config.HEAL_CRIT_POSSIBLE && Formulas.calcMCrit(creature, null, 4.5)) {
                d *= 3.0;
            }
        }
        for (Creature creature2 : list) {
            double d2;
            if (creature2 == null || creature2.isHealBlocked() || creature2 != creature && (creature2.isPlayer() && creature2.isCursedWeaponEquipped() || creature.isPlayer() && creature.isCursedWeaponEquipped())) continue;
            if (this.apf) {
                d2 = this._power;
            } else {
                d2 = d;
                if (!this.isHandler()) {
                    d2 += creature.calcStat(Stats.HEAL_POWER, creature, this);
                    d2 *= (!this.aoJ ? creature2.calcStat(Stats.HEAL_EFFECTIVNESS, 100.0, creature, this) : 100.0) / 100.0;
                }
            }
            d2 = Math.max(0.0, Math.min(d2, creature2.calcStat(Stats.HP_LIMIT, null, null) * (double)creature2.getMaxHp() / 100.0 - creature2.getCurrentHp()));
            if (d2 > 0.0) {
                creature2.setCurrentHp(d2 + creature2.getCurrentHp(), false);
            }
            if (creature2.isPlayer()) {
                if (this.getId() == 4051) {
                    creature2.sendPacket((IStaticPacket)Msg.REJUVENATING_HP);
                } else if (creature == creature2) {
                    creature.sendPacket((IStaticPacket)new SystemMessage(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addNumber(Math.round(d2)));
                } else {
                    creature2.sendPacket((IStaticPacket)new SystemMessage(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(creature).addNumber(Math.round(d2)));
                }
            }
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false);
        }
        if (this.isSSPossible() && this.isMagic()) {
            creature.unChargeShots(this.isMagic());
        }
    }

    private final int X(int n) {
        double d = this.getPower();
        double d2 = this.getPower() / 4.0;
        if ((double)n < d2) {
            return 0;
        }
        double d3 = this.getPower() / 3.1;
        if ((double)n > this.getPower()) {
            return (int)d3;
        }
        n = (int)((double)n - d2);
        return (int)(d3 * ((double)n / (d - d2)));
    }
}

