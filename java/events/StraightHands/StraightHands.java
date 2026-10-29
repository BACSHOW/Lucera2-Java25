/*
 * Decompiled with CFR 0.152.
 */
package events.StraightHands;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StraightHands
extends Functions
implements ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(StraightHands.class);
    private static final String EVENT_NAME = "StraightHands";
    private static final List<Integer> it = new ArrayList<Integer>();
    private static int[] iu;
    private static boolean _active;

    private static boolean isActive() {
        return StraightHands.IsActive(EVENT_NAME);
    }

    private static void W() {
        for (ItemTemplate itemTemplate : ItemHolder.getInstance().getAllTemplates()) {
            if (itemTemplate == null) continue;
            for (int n : iu) {
                if (itemTemplate.getItemId() != n) continue;
                itemTemplate.setStatDisabled(true);
                it.add(itemTemplate.getItemId());
                _log.info("Event: StraightHands funcs of " + itemTemplate.getName() + " removed.");
            }
        }
    }

    private static void X() {
        for (int n : it) {
            ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
            if (itemTemplate != null) {
                itemTemplate.setStatDisabled(false);
            }
            _log.info("Event: StraightHands funcs of " + itemTemplate.getName() + " restored.");
        }
    }

    private static void a(Player player) {
        for (ItemInstance itemInstance : player.getInventory().getItems()) {
            for (int n : iu) {
                if (itemInstance.getItemId() != n) continue;
                if (itemInstance.isEquipped()) {
                    player.getInventory().unEquipItem(itemInstance);
                }
                player.sendMessage(new CustomMessage("scripts.events.StraightHands.ItemS1StatsRemoved", player, new Object[0]).addItemName(n));
            }
        }
    }

    private static void start() {
        iu = (int[])Config.EVENT_StraightHands_Items.clone();
        StraightHands.W();
        for (Player player : GameObjectsStorage.getAllPlayers()) {
            StraightHands.a(player);
        }
    }

    private static void stop() {
        StraightHands.X();
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (StraightHands.SetActive(EVENT_NAME, true)) {
            StraightHands.start();
            _log.info("Event: StraightHands started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.StraightHands.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event StraightHands already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (StraightHands.SetActive(EVENT_NAME, false)) {
            StraightHands.stop();
            _log.info("Event: StraightHands stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.StraightHands.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event StraightHands not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    public static void OnPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.StraightHands.AnnounceEventStarted", null);
            StraightHands.a(player);
        }
    }

    @Override
    public void onLoad() {
        if (StraightHands.isActive()) {
            _active = true;
            StraightHands.start();
            _log.info("Loaded Event: StraightHands [state: activated]");
        } else {
            _log.debug("Loaded Event: StraightHands [state: deactivated]");
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    static {
        _active = false;
    }
}

