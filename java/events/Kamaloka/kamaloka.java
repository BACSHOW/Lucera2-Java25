/*
 * Decompiled with CFR 0.152.
 */
package events.Kamaloka;

import l2.gameserver.Announcements;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class kamaloka
extends Functions
implements ScriptFile {
    private static boolean _active = false;
    private static final Logger _log = LoggerFactory.getLogger(kamaloka.class);
    private static String hP = "[event_kamaloka_spawn_list]";

    private void spawnEventManagers() {
        SpawnManager.getInstance().spawn(hP);
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (kamaloka.SetActive("kamaloka", true)) {
            this.spawnEventManagers();
            System.out.println("Event 'Kamaloka' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.Kamaloka.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'Kamaloka' already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (kamaloka.SetActive("kamaloka", false)) {
            this.unSpawnEventManagers();
            System.out.println("Event 'Kamaloka' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.Kamaloka.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'Kamaloka' not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    private static boolean isActive() {
        return kamaloka.IsActive("kamaloka");
    }

    private void unSpawnEventManagers() {
        SpawnManager.getInstance().despawn(hP);
    }

    @Override
    public void onLoad() {
        if (kamaloka.isActive()) {
            _active = true;
            this.spawnEventManagers();
            _log.info("Loaded Event: Kamaloka [state: activated]");
        } else {
            _log.debug("Loaded Event: Kamaloka [state: deactivated]");
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
}

