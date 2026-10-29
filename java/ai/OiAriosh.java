/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

public class OiAriosh
extends Fighter {
    private static final int cx = 18556;
    private int cy = 0;
    private static final int[] cz = new int[]{80, 60, 40, 30, 20, 10, 5, -5};

    public OiAriosh(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (!npcInstance.isDead() && npcInstance.getCurrentHpPercents() < (double)cz[this.cy]) {
            this.a(creature);
            ++this.cy;
        }
        super.onEvtAttacked(creature, n);
    }

    private void a(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        try {
            SimpleSpawner simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(18556));
            simpleSpawner.setLoc(Location.findPointToStay(npcInstance, 100, 120));
            simpleSpawner.setReflection(npcInstance.getReflection());
            NpcInstance npcInstance2 = simpleSpawner.doSpawn(true);
            npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, 100);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.cy = 0;
        super.onEvtDead(creature);
    }
}

