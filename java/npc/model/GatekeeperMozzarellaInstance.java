/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class GatekeeperMozzarellaInstance
extends MerchantInstance {
    private static final Location[] cD = new Location[]{new Location(17776, 113968, -11671), new Location(17680, 113968, -11671)};

    public GatekeeperMozzarellaInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        this.showChatWindow(player, "teleporter/gatekeeper_mozzarella001.htm", new Object[0]);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!GatekeeperMozzarellaInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equals("teleport_request")) {
            if (player.getLevel() > Config.KRUMA_TELEPORT_MAX_LEVEL) {
                this.showChatWindow(player, "teleporter/gatekeeper_mozzarella005.htm", new Object[0]);
            } else {
                player.teleToLocation(Location.findPointToStay(Rnd.get(cD), 100, player.getGeoIndex()));
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}

