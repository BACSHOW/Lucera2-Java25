/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;

public class DoorAI
extends CharacterAI {
    public DoorAI(DoorInstance doorInstance) {
        super(doorInstance);
    }

    public void onEvtTwiceClick(Player player) {
    }

    public void onEvtOpen(Player player) {
    }

    public void onEvtClose(Player player) {
    }

    @Override
    public DoorInstance getActor() {
        return (DoorInstance)super.getActor();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        DoorInstance doorInstance;
        if (creature == null || (doorInstance = this.getActor()) == null) {
            return;
        }
        Player player = creature.getPlayer();
        if (player == null) {
            return;
        }
        SiegeEvent siegeEvent = player.getEvent(SiegeEvent.class);
        SiegeEvent siegeEvent2 = doorInstance.getEvent(SiegeEvent.class);
        if (siegeEvent == null || siegeEvent == siegeEvent2 && siegeEvent.getSiegeClan("attackers", player.getClan()) != null) {
            for (NpcInstance npcInstance : doorInstance.getAroundNpc(900, 200)) {
                if (!npcInstance.isSiegeGuard()) continue;
                if (Rnd.chance(20)) {
                    npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, 10000);
                    continue;
                }
                npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, 2000);
            }
        }
    }
}

