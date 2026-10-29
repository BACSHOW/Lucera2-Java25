/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import bosses.ValakasManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public final class ValakasGatekeeperInstance
extends NpcInstance {
    private static final int FLOATING_STONE = 7267;
    private static final Location ayl = new Location(183831, -115457, -3296);

    public ValakasGatekeeperInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!ValakasGatekeeperInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equalsIgnoreCase("request_passage")) {
            if (!ValakasManager.isEnableEnterToLair()) {
                player.sendMessage(new CustomMessage("scripts.ValakasGatekeeperInstance.StillReborn", player, new Object[0]));
                return;
            }
            if (player.getInventory().getCountOf(7267) < 1L) {
                player.sendMessage(new CustomMessage("scripts.ValakasGatekeeperInstance.FlotaingStoneNeed", player, new Object[0]));
                return;
            }
            player.getInventory().destroyItemByItemId(7267, 1L);
            player.teleToLocation(ayl);
            return;
        }
        if (string.equalsIgnoreCase("request_valakas")) {
            ValakasManager.enterTheLair(player);
            return;
        }
        super.onBypassFeedback(player, string);
    }
}

