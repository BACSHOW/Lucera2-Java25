/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Mystic;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class WizardSaint
extends Mystic {
    private boolean bE = false;
    private final int cJ;
    private final NpcTemplate cI;

    public WizardSaint(NpcInstance npcInstance) {
        super(npcInstance);
        this.cI = NpcHolder.getInstance().getTemplate(npcInstance.getParameter("CreateOnePrivateOnAttack", 0));
        this.cJ = npcInstance.getParameter("CreateOnePrivateOnAttackChance", 0);
    }

    @Override
    protected void onEvtSpawn() {
        this.bE = false;
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (Rnd.get(100) < this.cJ && !this.bE) {
            this.bE = true;
            SimpleSpawner simpleSpawner = new SimpleSpawner(this.cI);
            simpleSpawner.setLoc(Location.findPointToStay(npcInstance, 100, 100));
            NpcInstance npcInstance2 = simpleSpawner.doSpawn(true);
            if (creature.isPet() || creature.isSummon()) {
                npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(2, 100));
            }
            npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature.getPlayer(), Rnd.get(1, 100));
            simpleSpawner.stopRespawn();
        }
        super.onEvtAttacked(creature, n);
    }
}

