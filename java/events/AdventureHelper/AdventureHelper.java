/*
 * Decompiled with CFR 0.152.
 */
package events.AdventureHelper;

import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdventureHelper
extends Functions
implements ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(AdventureHelper.class);
    private static final String gK = "[start_weapon]";

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (AdventureHelper.SetActive("AdventureHelper", true)) {
            this.spawnEventManagers();
            _log.info("Event 'Adventure Helper' started.");
            player.sendMessage("Event 'Adventure Helper' already started.");
            if (player.isLangRus()) {
                player.sendMessage("\u0412 \u0441\u0442\u0430\u0440\u0442\u043e\u0432\u044b\u0445 \u0433\u043e\u0440\u043e\u0434\u0430\u0445 \u043f\u043e\u044f\u0432\u0438\u043b\u0438\u0441\u044c \u041d\u041f\u0426 Miss Queen \u0438 \u043d\u043e\u0432\u0438\u0447\u043a\u0438 \u043c\u043e\u0433\u0443\u0442 \u043f\u043e\u043b\u0443\u0447\u0430\u0442\u044c \u043e\u0440\u0443\u0436\u0438\u0435");
            } else {
                player.sendMessage("The Miss Queen NPC appeared in the starting cities and Newbies can receiving weapons.");
            }
        } else {
            player.sendMessage("Event 'Adventure Helper' already started.");
        }
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (AdventureHelper.SetActive("AdventureHelper", false)) {
            this.unSpawnEventManagers();
            _log.info("Event 'Adventure Helper' stopped.");
            if (player.isLangRus()) {
                player.sendMessage("NPC Miss Queen \u0438\u0441\u0447\u0435\u0437\u043b\u0438");
            } else {
                player.sendMessage("NPC Miss Queen disappeared.");
            }
        } else {
            _log.info("Event 'Adventure Helper' not started.");
        }
        this.show("admin/events/events.htm", player);
    }

    private void unSpawnEventManagers() {
        SpawnManager.getInstance().despawn(gK);
    }

    private void spawnEventManagers() {
        SpawnManager.getInstance().spawn(gK);
    }

    private static boolean isActive() {
        return AdventureHelper.IsActive("AdventureHelper");
    }

    @Override
    public void onLoad() {
        if (AdventureHelper.isActive()) {
            this.spawnEventManagers();
            _log.info("Loaded Event: Adventure Helper [state: activated]");
        } else {
            _log.debug("Loaded Event: Adventure Helper [state: deactivated]");
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

