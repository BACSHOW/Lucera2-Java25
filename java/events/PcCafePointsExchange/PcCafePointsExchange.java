/*
 * Decompiled with CFR 0.152.
 */
package events.PcCafePointsExchange;

import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PcCafePointsExchange
extends Functions
implements ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(PcCafePointsExchange.class);
    private static final String EVENT_NAME = "PcCafePointsExchange";
    private static final String hQ = "[pc_cafe_exchange_spawn]";

    private void spawnEventManagers() {
        SpawnManager.getInstance().spawn(hQ);
    }

    private void unSpawnEventManagers() {
        SpawnManager.getInstance().despawn(hQ);
    }

    private static boolean isActive() {
        return PcCafePointsExchange.IsActive(EVENT_NAME);
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (PcCafePointsExchange.SetActive(EVENT_NAME, true)) {
            this.spawnEventManagers();
            System.out.println("Event: 'PcCafePointsExchange' started.");
        } else {
            player.sendMessage("Event 'PcCafePointsExchange' already started.");
        }
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (PcCafePointsExchange.SetActive(EVENT_NAME, false)) {
            this.unSpawnEventManagers();
            System.out.println("Event: 'PcCafePointsExchange' stopped.");
        } else {
            player.sendMessage("Event: 'PcCafePointsExchange' not started.");
        }
        this.show("admin/events/events.htm", player);
    }

    @Override
    public void onLoad() {
        if (PcCafePointsExchange.isActive()) {
            this.spawnEventManagers();
            _log.info("Loaded Event: PcCafePointsExchange [state: activated]");
        } else {
            _log.debug("Loaded Event: PcCafePointsExchange [state: deactivated]");
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

