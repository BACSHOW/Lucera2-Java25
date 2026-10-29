/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.FinishRotating;
import l2.gameserver.network.l2.s2c.StartRotating;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

public class PDam
extends Skill {
    private final boolean apm;
    private final boolean apn;
    private final boolean apo;
    private final boolean app;

    public PDam(StatsSet statsSet) {
        super(statsSet);
        this.apm = statsSet.getBool("onCrit", false);
        this.apn = statsSet.getBool("directHp", false);
        this.apo = statsSet.getBool("turner", false);
        this.app = statsSet.getBool("blow", false);
    }

    @Override
    public boolean isBlowSkill() {
        return this.app;
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        boolean bl = creature.getChargedSoulShot() && this.isSSPossible();
        for (Creature creature2 : list) {
            boolean bl2;
            if (creature2 == null || creature2.isDead()) continue;
            if (this.apo && !creature2.isInvul()) {
                creature2.broadcastPacket(new StartRotating(creature2, creature2.getHeading(), 1, 65535));
                creature2.broadcastPacket(new FinishRotating(creature2, creature.getHeading(), 65535));
                creature2.setHeading(creature.getHeading());
                creature2.sendPacket((IStaticPacket)new SystemMessage(110).addSkillName(this._displayId, this._displayLevel));
            }
            Creature creature3 = (bl2 = creature2.checkReflectSkill(creature, this)) ? creature : creature2;
            Formulas.AttackInfo attackInfo = Formulas.calcPhysDam(creature, creature3, this, false, this.app, bl, this.apm);
            if (attackInfo.lethal_dmg > 0.0) {
                creature3.reduceCurrentHp(attackInfo.lethal_dmg, creature, this, true, true, false, false, false, false, false);
            }
            if (!attackInfo.miss || attackInfo.damage >= 1.0) {
                creature3.reduceCurrentHp(attackInfo.damage, creature, this, true, true, attackInfo.lethal ? false : this.apn, true, false, false, this.getPower() != 0.0);
            }
            if (!bl2) {
                creature3.doCounterAttack(this, creature, this.app);
            }
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false, bl2);
        }
        if (this.isSuicideAttack()) {
            creature.doDie(null);
        } else if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

