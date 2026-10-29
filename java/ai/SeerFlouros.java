/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Mystic;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

public class SeerFlouros
extends Mystic {
    private int cy = 0;
    private static final int cx = 18560;
    private static final int cd = 2;
    private static final int[] cz = new int[]{80, 60, 40, 30, 20, 10, 5, -5};

    public SeerFlouros(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (!npcInstance.isDead() && npcInstance.getCurrentHpPercents() < (double)cz[this.cy]) {
            this.b(creature);
            ++this.cy;
        }
        super.onEvtAttacked(creature, n);
    }

    private void b(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        for (int i = 0; i < 2; ++i) {
            try {
                SimpleSpawner simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(18560));
                simpleSpawner.setLoc(Location.findPointToStay(npcInstance, 100, 120));
                simpleSpawner.setReflection(npcInstance.getReflection());
                NpcInstance npcInstance2 = simpleSpawner.doSpawn(true);
                npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, 100);
                continue;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.cy = 0;
        super.onEvtDead(creature);
    }
}

