/*
 * Decompiled with CFR 0.152.
 */
package events.TheFallHarvest;

import events.TheFallHarvest.SquashAI;
import handler.items.ScriptItemHandler;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import npc.model.SquashInstance;

public class Seed
extends ScriptItemHandler {
    private static int[] iv = new int[]{6389, 6390};
    private static int[] hd = new int[]{12774, 12777};

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        Player player = (Player)playable;
        if (player.isInZone(Zone.ZoneType.RESIDENCE)) {
            return false;
        }
        if (player.isOlyParticipant()) {
            player.sendMessage(new CustomMessage("scripts.events.TheFallHarvest.Seed.CantSeedInStadium", player, new Object[0]));
            return false;
        }
        if (!player.getReflection().isDefault()) {
            player.sendMessage(new CustomMessage("scripts.events.TheFallHarvest.Seed.CantSeedInInstance", player, new Object[0]));
            return false;
        }
        NpcTemplate npcTemplate = null;
        int n = itemInstance.getItemId();
        for (int i = 0; i < iv.length; ++i) {
            if (iv[i] != n) continue;
            npcTemplate = NpcHolder.getInstance().getTemplate(hd[i]);
            break;
        }
        if (npcTemplate == null) {
            return false;
        }
        if (!player.getInventory().destroyItem(itemInstance, 1L)) {
            return false;
        }
        SimpleSpawner simpleSpawner = new SimpleSpawner(npcTemplate);
        simpleSpawner.setLoc(Location.findPointToStay(player, 30, 70));
        NpcInstance npcInstance = simpleSpawner.doSpawn(true);
        npcInstance.setAI(new SquashAI(npcInstance));
        ((SquashInstance)npcInstance).setSpawner(player);
        ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(simpleSpawner), 180000L);
        return true;
    }

    @Override
    public int[] getItemIds() {
        return iv;
    }

    public class DeSpawnScheduleTimerTask
    extends RunnableImpl {
        SimpleSpawner spawnedPlant = null;

        public DeSpawnScheduleTimerTask(SimpleSpawner simpleSpawner) {
            this.spawnedPlant = simpleSpawner;
        }

        @Override
        public void runImpl() throws Exception {
            this.spawnedPlant.deleteAll();
        }
    }
}

