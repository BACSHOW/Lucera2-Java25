/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

public class Continuous
extends Skill {
    private final int apb;
    private final int apc;

    public Continuous(StatsSet statsSet) {
        super(statsSet);
        this.apb = statsSet.getInteger("lethal1", 0);
        this.apc = statsSet.getInteger("lethal2", 0);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null || this.getSkillType() == Skill.SkillType.BUFF && creature2 != creature && (creature2.isCursedWeaponEquipped() || creature.isCursedWeaponEquipped())) continue;
            boolean bl = creature2.checkReflectSkill(creature, this);
            Creature creature3 = bl ? creature : creature2;
            double d = 0.01 * creature3.calcStat(Stats.DEATH_VULNERABILITY, creature, this);
            double d2 = (double)this.apb * d;
            double d3 = (double)this.apc * d;
            if (d2 > 0.0 && Rnd.chance(d2)) {
                if (creature3.isPlayer()) {
                    creature3.reduceCurrentHp(creature3.getCurrentCp(), creature, this, true, true, false, true, false, false, true);
                    creature3.sendPacket((IStaticPacket)SystemMsg.LETHAL_STRIKE);
                    creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                } else if (creature3.isNpc() && !creature3.isLethalImmune()) {
                    creature3.reduceCurrentHp(creature3.getCurrentHp() / 2.0, creature, this, true, true, false, true, false, false, true);
                    creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                }
            } else if (d3 > 0.0 && Rnd.chance(d3)) {
                if (creature3.isPlayer()) {
                    creature3.reduceCurrentHp(creature3.getCurrentHp() + creature3.getCurrentCp() - 1.0, creature, this, true, true, false, true, false, false, true);
                    creature3.sendPacket((IStaticPacket)SystemMsg.LETHAL_STRIKE);
                    creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                } else if (creature3.isNpc() && !creature3.isLethalImmune()) {
                    creature3.reduceCurrentHp(creature3.getCurrentHp() - 1.0, creature, this, true, true, false, true, false, false, true);
                    creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                }
            }
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false, bl);
        }
        if (this.isSSPossible() && (!Config.SAVING_SPS || this._skillType != Skill.SkillType.BUFF)) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

