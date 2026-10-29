/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

public class TeleportNpc
extends Skill {
    public TeleportNpc(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null || creature2.isDead()) continue;
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false);
            creature2.abortAttack(true, true);
            creature2.abortCast(true, true);
            creature2.stopMove();
            int n = creature.getX();
            int n2 = creature.getY();
            int n3 = creature.getZ();
            int n4 = creature.getHeading();
            int n5 = (int)(creature.getColRadius() + creature2.getColRadius());
            int n6 = (int)Math.sqrt(n5 * n5 / 2);
            if (n4 < 16384) {
                n += n6;
                n2 += n6;
            } else if (n4 > 16384 && n4 <= 32768) {
                n -= n6;
                n2 += n6;
            } else if (n4 < 32768 && n4 <= 49152) {
                n -= n6;
                n2 -= n6;
            } else if (n4 > 49152) {
                n += n6;
                n2 -= n6;
            }
            creature2.setXYZ(n, n2, n3);
            creature2.validateLocation(1);
        }
    }
}

