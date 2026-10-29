/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import bosses.BaiumManager;
import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;

public final class BaiumGatekeeperInstance
extends NpcInstance {
    private static final int axw = 29020;
    private static final int axx = 29025;
    private static final int axy = 4295;
    private static final Location et = new Location(113100, 14500, 10077);

    public BaiumGatekeeperInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!BaiumGatekeeperInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.startsWith("request_entrance")) {
            if (ItemFunctions.getItemCount(player, 4295) > 0L) {
                NpcInstance npcInstance = GameObjectsStorage.getByNpcId(29020);
                if (npcInstance != null) {
                    this.showChatWindow(player, "default/31862-1.htm", new Object[0]);
                    return;
                }
                NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(29025);
                if (npcInstance2 == null) {
                    this.showChatWindow(player, "default/31862-2.htm", new Object[0]);
                    return;
                }
                ItemFunctions.removeItem(player, 4295, 1L, true);
                if (Config.BAIUM_CHECK_QUEST_ON_AWAKE) {
                    player.setVar("baiumPermission", "granted", -1L);
                }
                player.teleToLocation(et);
            } else {
                this.showChatWindow(player, "default/31862-3.htm", new Object[0]);
            }
        } else if (string.startsWith("request_wakeup")) {
            if (!player.isInZone(Zone.ZoneType.epic)) {
                return;
            }
            if (Config.BAIUM_CHECK_QUEST_ON_AWAKE && (player.getVar("baiumPermission") == null || !player.getVar("baiumPermission").equalsIgnoreCase("granted"))) {
                this.showChatWindow(player, "default/29025-1.htm", new Object[0]);
                return;
            }
            if (this.isBusy()) {
                this.showChatWindow(player, "default/29025-2.htm", new Object[0]);
            }
            this.setBusy(true);
            Functions.npcSay(this, "You called my name! Now you gonna die!");
            BaiumManager.spawnBaium(this, player);
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}

