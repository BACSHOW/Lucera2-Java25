/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Log;

public class OlympiadPointsReset
extends Functions
implements ScriptFile {
    public void olympiad_point_sell_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_OLY_POINTS_RESET) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.isInPeaceZone()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_peace_zone.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/reset_olympiad_point_sell.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_OLY_POINTS_RESET_ITEM_ID));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_OLY_POINTS_RESET_ITEM_AMOUNT));
        npcHtmlMessage.replace("%reset_point_count%", String.valueOf(Config.SERVICES_OLY_POINTS_REWARD));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void olympiad_point_sell() {
        Player player = this.getSelf();
        if (player == null || !OlympiadPointsReset.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_OLY_POINTS_RESET) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.isNoble()) {
            this.show("You are mpt a noble", player);
            return;
        }
        if (player.isSubClassActive()) {
            this.show("You can reset points only on main class.", player);
            return;
        }
        if (NoblesController.getInstance().getPointsOf(player.getObjectId()) >= Config.SERVICES_OLY_POINTS_THRESHOLD) {
            this.show("Your olympiad noble points count is to high.", player);
            return;
        }
        if (Functions.removeItem(player, Config.SERVICES_OLY_POINTS_RESET_ITEM_ID, Config.SERVICES_OLY_POINTS_RESET_ITEM_AMOUNT) < (long)Config.SERVICES_OLY_POINTS_RESET_ITEM_AMOUNT) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        NoblesController.getInstance().setPointsOf(player.getObjectId(), Config.SERVICES_OLY_POINTS_REWARD);
        player.sendMessage(new CustomMessage("services.OlympiadPointsReset.PointsAdd", player, new Object[0]));
        Log.service("OlympiadPointsReset", player, "Olympiad point reset to amount " + Config.SERVICES_OLY_POINTS_REWARD + " for " + Config.SERVICES_NOBLESS_SELL_ITEM + " " + Config.SERVICES_NOBLESS_SELL_PRICE);
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

