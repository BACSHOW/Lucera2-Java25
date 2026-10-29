/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class WatchmanMonster
extends Fighter {
    private long dj = 0L;
    private boolean dk = false;
    private HardReference<? extends Creature> dl = HardReferences.emptyRef();
    static final String[] flood = new String[]{"I'll be back", "You are stronger than expected"};
    static final String[] flood2 = new String[]{"Help me!", "Alarm! We are under attack!"};

    public WatchmanMonster(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature != null && !npcInstance.getFaction().isNone() && npcInstance.getCurrentHpPercents() < 50.0 && this.dj < System.currentTimeMillis() - 15000L) {
            this.dj = System.currentTimeMillis();
            this.dl = creature.getRef();
            if (this.m()) {
                return;
            }
            Functions.npcSay(npcInstance, "Anyone, help me!");
        }
        super.onEvtAttacked(creature, n);
    }

    private boolean m() {
        this.dk = false;
        NpcInstance npcInstance = this.getActor();
        Creature creature = this.dl.get();
        if (creature == null) {
            return false;
        }
        for (NpcInstance npcInstance2 : npcInstance.getAroundNpc(1000, 150)) {
            if (npcInstance.isDead() || !npcInstance2.isInFaction(npcInstance) || npcInstance2.isInCombat()) continue;
            this.clearTasks();
            this.dk = true;
            this.addTaskMove(npcInstance2.getLoc(), true);
            Functions.npcSay(npcInstance, flood[Rnd.get(flood.length)]);
            return true;
        }
        return false;
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.dj = 0L;
        this.dl = HardReferences.emptyRef();
        this.dk = false;
        super.onEvtDead(creature);
    }

    @Override
    protected void onEvtArrived() {
        NpcInstance npcInstance = this.getActor();
        if (this.dk) {
            Creature creature = this.dl.get();
            if (creature != null) {
                Functions.npcSay(npcInstance, flood2[Rnd.get(flood2.length)]);
                this.notifyFriends(creature, 100);
            }
            this.dk = false;
            this.notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, 100);
        } else {
            super.onEvtArrived();
        }
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
        if (!this.dk) {
            super.onEvtAggression(creature, n);
        }
    }
}

