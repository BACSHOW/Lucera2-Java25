/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;

public class GuardianAltar
extends DefaultAI {
    private static final int cg = 18808;

    public GuardianAltar(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.setIsInvul(true);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature == null) {
            return;
        }
        Player player = creature.getPlayer();
        if (Rnd.chance(40) && player.getInventory().destroyItemByItemId(14848, 1L)) {
            List<NpcInstance> list = npcInstance.getAroundNpc(1500, 300);
            if (list != null && !list.isEmpty()) {
                for (NpcInstance npcInstance2 : list) {
                    if (npcInstance2.getNpcId() != 18808) continue;
                    Functions.npcSay(npcInstance, "I can sense the presence of Dark Shaman already!");
                    return;
                }
            }
            try {
                SimpleSpawner simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(18808));
                simpleSpawner.setLoc(Location.findPointToStay(npcInstance, 400, 420));
                NpcInstance eventOwner = simpleSpawner.doSpawn(true);
                if (creature.isPet() || creature.isSummon()) {
                    eventOwner.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(2, 100));
                }
                eventOwner.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature.getPlayer(), Rnd.get(1, 100));
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (Rnd.chance(5)) {
            List<NpcInstance> list = npcInstance.getAroundNpc(1000, 300);
            if (list != null && !list.isEmpty()) {
                for (NpcInstance exception : list) {
                    if (exception.getNpcId() != 22702) continue;
                    return;
                }
            }
            for (int i = 0; i < 2; ++i) {
                try {
                    SimpleSpawner simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(22702));
                    simpleSpawner.setLoc(Location.findPointToStay(npcInstance, 150, 160));
                    NpcInstance npcInstance2 = simpleSpawner.doSpawn(true);
                    if (creature.isPet() || creature.isSummon()) {
                        npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(2, 100));
                    }
                    npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature.getPlayer(), Rnd.get(1, 100));
                    continue;
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}

