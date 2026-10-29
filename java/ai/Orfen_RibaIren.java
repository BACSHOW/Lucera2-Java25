/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class Orfen_RibaIren
extends Fighter {
    private static final int cA = 29014;

    public Orfen_RibaIren(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean createNewTask() {
        return this.defaultNewTask();
    }

    @Override
    protected void onEvtClanAttacked(Creature creature, Creature creature2, int n) {
        super.onEvtClanAttacked(creature, creature2, n);
        NpcInstance npcInstance = this.getActor();
        if (this._healSkills.length == 0) {
            return;
        }
        if (creature.isDead() || npcInstance.isDead() || creature.getCurrentHpPercents() > 50.0) {
            return;
        }
        int n2 = 0;
        if (creature.getNpcId() == npcInstance.getNpcId()) {
            n2 = creature.getObjectId() == npcInstance.getObjectId() ? 100 : 0;
        } else {
            int n3 = n2 = creature.getNpcId() == 29014 ? 90 : 10;
        }
        if (Rnd.chance(n2) && this.canUseSkill(this._healSkills[0], creature, -1.0)) {
            this.addTaskAttack(creature, this._healSkills[0], 1000000);
        }
    }
}

