/*
 * Decompiled with CFR 0.152.
 */
package events.TheFallHarvest;

import java.io.File;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheFallHarvest
extends Functions
implements OnDeathListener,
OnPlayerEnterListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(TheFallHarvest.class);
    private static boolean _active = false;
    private static boolean iP = false;
    private static String iQ = "[the_fall_harvest_spawn]";
    private static File iR = new File(Config.DATAPACK_ROOT, "data/html-en/scripts/events/TheFallHarvest/31255.xml");

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (TheFallHarvest.isActive()) {
            _active = true;
            TheFallHarvest.Y();
            this.spawnEventManagers();
            _log.info("Loaded Event: The Fall Harvest [state: activated]");
        } else {
            _log.debug("Loaded Event: The Fall Harvest [state: deactivated]");
        }
    }

    private static boolean isActive() {
        return TheFallHarvest.IsActive("TheFallHarvest");
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (TheFallHarvest.SetActive("TheFallHarvest", true)) {
            TheFallHarvest.Y();
            this.spawnEventManagers();
            System.out.println("Event 'The Fall Harvest' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TheFallHarvest.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'The Fall Harvest' already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (TheFallHarvest.SetActive("TheFallHarvest", false)) {
            this.unSpawnEventManagers();
            System.out.println("Event 'The Fall Harvest' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TheFallHarvest.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'The Fall Harvest' not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    private void spawnEventManagers() {
        SpawnManager.getInstance().spawn(iQ);
    }

    private void unSpawnEventManagers() {
        SpawnManager.getInstance().despawn(iQ);
    }

    private static void Y() {
        if (iP) {
            return;
        }
        MultiSellHolder.getInstance().parseFile(iR);
        iP = true;
    }

    @Override
    public void onReload() {
        this.unSpawnEventManagers();
        if (iP) {
            MultiSellHolder.getInstance().remove(iR);
            iP = false;
        }
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (_active && TheFallHarvest.SimpleCheckDrop(creature, creature2) && Rnd.chance(Config.EVENT_TFH_POLLEN_CHANCE * creature2.getPlayer().getRateItems() * ((NpcInstance)creature).getTemplate().rateHp)) {
            ((NpcInstance)creature).dropItem(creature2.getPlayer(), 6391, 1L);
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.TheFallHarvest.AnnounceEventStarted", null);
        }
    }
}

