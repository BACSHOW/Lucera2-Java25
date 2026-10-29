/*
 * Decompiled with CFR 0.152.
 */
package events.Christmas;

import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Christmas
extends Functions
implements OnDeathListener,
OnPlayerEnterListener,
ScriptFile {
    private static String gL = "[christmas_event]";
    private static String gM = "[christmas_event_chest]";
    private static final Logger _log = LoggerFactory.getLogger(Christmas.class);
    private static int[][] gN = new int[][]{{5556, 20}, {5557, 20}, {5558, 50}, {5559, 5}};
    private static boolean _active = false;

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (Christmas.isActive()) {
            _active = true;
            this.spawnEventManagers();
            _log.info("Loaded Event: Christmas [state: activated]");
        } else {
            _log.debug("Loaded Event: Christmas [state: deactivated]");
        }
    }

    private static boolean isActive() {
        return Christmas.IsActive("Christmas");
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (Christmas.SetActive("Christmas", true)) {
            this.spawnEventManagers();
            System.out.println("Event 'Christmas' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.Christmas.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'Christmas' already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (Christmas.SetActive("Christmas", false)) {
            this.unSpawnEventManagers();
            System.out.println("Event 'Christmas' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.Christmas.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'Christmas' not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    private void spawnEventManagers() {
        SpawnManager.getInstance().spawn(gM);
        SpawnManager.getInstance().spawn(gL);
    }

    private void unSpawnEventManagers() {
        SpawnManager.getInstance().despawn(gL);
        SpawnManager.getInstance().despawn(gM);
    }

    @Override
    public void onReload() {
        this.unSpawnEventManagers();
    }

    @Override
    public void onShutdown() {
        this.unSpawnEventManagers();
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (_active && Christmas.SimpleCheckDrop(creature, creature2)) {
            int n = 0;
            for (int[] nArray : gN) {
                if (!Rnd.chance((double)nArray[1] * creature2.getPlayer().getRateItems() * Config.EVENT_CHRISTMAS_CHANCE * 0.1)) continue;
                ((NpcInstance)creature).dropItem(creature2.getPlayer(), nArray[0], 1L);
                if (++n > 2) break;
            }
        }
    }

    public void exchange(String[] stringArray) {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300.0) {
            return;
        }
        if (stringArray[0].equalsIgnoreCase("0")) {
            if (Christmas.getItemCount(player, 5556) >= 4L && Christmas.getItemCount(player, 5557) >= 4L && Christmas.getItemCount(player, 5558) >= 10L && Christmas.getItemCount(player, 5559) >= 1L) {
                Christmas.removeItem(player, 5556, 4L, false);
                Christmas.removeItem(player, 5557, 4L, false);
                Christmas.removeItem(player, 5558, 10L, false);
                Christmas.removeItem(player, 5559, 1L, false);
                Christmas.addItem(player, 5560, 1L);
                return;
            }
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
        }
        if (stringArray[0].equalsIgnoreCase("1")) {
            if (Christmas.getItemCount(player, 5560) >= 10L) {
                Christmas.removeItem(player, 5560, 10L, false);
                Christmas.addItem(player, 5561, 1L);
                return;
            }
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
        }
        if (stringArray[0].equalsIgnoreCase("2")) {
            if (Christmas.getItemCount(player, 5560) >= 10L) {
                Christmas.removeItem(player, 5560, 10L, false);
                Christmas.addItem(player, 7836, 1L);
                return;
            }
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
        }
        if (stringArray[0].equalsIgnoreCase("3")) {
            if (Christmas.getItemCount(player, 5560) >= 10L) {
                Christmas.removeItem(player, 5560, 10L, false);
                Christmas.addItem(player, 8936, 1L);
                return;
            }
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.Christmas.AnnounceEventStarted", null);
        }
    }
}

