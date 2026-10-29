/*
 * Decompiled with CFR 0.152.
 */
package events.CofferofShadows;

import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CofferofShadows
extends Functions
implements OnPlayerEnterListener,
ScriptFile {
    private static int gP = 50000;
    private static int gQ = 8659;
    private static String gR = "[event_coffer_of_shadows]";
    private static final Logger _log = LoggerFactory.getLogger(CofferofShadows.class);
    private static boolean _active = false;
    private static int[] gS = new int[]{1, 5, 10, 50};

    private void spawnEventManagers() {
        SpawnManager.getInstance().spawn(gR);
    }

    private void unSpawnEventManagers() {
        SpawnManager.getInstance().despawn(gR);
    }

    private static boolean isActive() {
        return CofferofShadows.IsActive("CofferofShadows");
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (CofferofShadows.SetActive("CofferofShadows", true)) {
            this.spawnEventManagers();
            System.out.println("Event: Coffer of Shadows started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'Coffer of Shadows' already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (CofferofShadows.SetActive("CofferofShadows", false)) {
            this.unSpawnEventManagers();
            System.out.println("Event: Coffer of Shadows stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'Coffer of Shadows' not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    public void buycoffer(String[] stringArray) {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (!NpcInstance.canBypassCheck(player, player.getLastNpc())) {
            return;
        }
        int n = 1;
        try {
            n = Integer.valueOf(stringArray[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
        long l = (long)((double)gP * Config.EVENT_CofferOfShadowsPriceRate * (double)n);
        if (player.getAdena() < l) {
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }
        player.reduceAdena(l, true);
        Functions.addItem(player, gQ, n);
    }

    public String DialogAppend_32091(Integer n) {
        if (n != 0) {
            return "";
        }
        String string = "";
        for (int n2 : gS) {
            String string2 = Util.formatAdena((long)((double)gP * Config.EVENT_CofferOfShadowsPriceRate * (double)n2));
            string = string + "<a action=\"bypass -h scripts_events.CofferofShadows.CofferofShadows:buycoffer " + n2 + "\">";
            string = n2 == 1 ? string + new CustomMessage("scripts.events.CofferofShadows.buycoffer", this.getSelf(), new Object[0]).addString(string2) : string + new CustomMessage("scripts.events.CofferofShadows.buycoffers", this.getSelf(), new Object[0]).addNumber(n2).addString(string2);
            string = string + "</a><br>";
        }
        return string;
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (CofferofShadows.isActive()) {
            _active = true;
            this.spawnEventManagers();
            _log.info("Loaded Event: Coffer of Shadows [state: activated]");
        } else {
            _log.debug("Loaded Event: Coffer of Shadows [state: deactivated]");
        }
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
    public void onPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.CofferofShadows.AnnounceEventStarted", null);
        }
    }
}

