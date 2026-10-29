/*
 * Decompiled with CFR 0.152.
 */
package ai.isle_of_prayer;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.entity.events.EventOwner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

public class DarkWaterDragon
extends Fighter {
    private int dR = 0;
    private static final int dS = 18482;
    private static final int dT = 22268;
    private static final int dU = 22269;
    private static final int[] MOBS = new int[]{22268, 22269};
    private static final int cd = 5;
    private static final int dV = 9596;

    public DarkWaterDragon(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (!npcInstance.isDead()) {
            switch (this.dR) {
                case 0: {
                    this.dR = 1;
                    this.c(creature);
                    break;
                }
                case 1: {
                    if (!(npcInstance.getCurrentHp() < (double)(npcInstance.getMaxHp() / 2))) break;
                    this.dR = 2;
                    this.c(creature);
                }
            }
        }
        super.onEvtAttacked(creature, n);
    }

    private void c(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        for (int i = 0; i < 5; ++i) {
            try {
                SimpleSpawner simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(MOBS[Rnd.get(MOBS.length)]));
                simpleSpawner.setLoc(Location.findPointToStay(npcInstance, 100, 120));
                NpcInstance npcInstance2 = simpleSpawner.doSpawn(true);
                npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(1, 100));
                continue;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        EventOwner eventOwner;
        this.dR = 0;
        NpcInstance npcInstance = this.getActor();
        try {
            eventOwner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(18482));
            ((SimpleSpawner)eventOwner).setLoc(Location.findPointToStay(npcInstance, 100, 120));
            ((SimpleSpawner)eventOwner).doSpawn(true);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (creature != null && (eventOwner = creature.getPlayer()) != null && Rnd.chance(77)) {
            npcInstance.dropItem((Player)eventOwner, 9596, 1L);
        }
        super.onEvtDead(creature);
    }

    @Override
    protected boolean randomWalk() {
        return this.dR == 0;
    }
}

