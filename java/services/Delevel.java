/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Experience;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;

public class Delevel
extends Functions {
    public void delevel_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_DELEVEL_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/level_change.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_DELEVEL_SELL_ITEM));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_DELEVEL_SELL_PRICE));
        npcHtmlMessage.replace("%level_reduction%", String.valueOf(Config.SERVICES_DELEVEL_REDUCTION));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void delevel() {
        Player player = this.getSelf();
        if (player == null || !Delevel.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_DELEVEL_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.getVarB("NoExp")) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/exp_freezed_delevel.htm"));
            return;
        }
        if (player.getLevel() < 3 || (long)player.getLevel() > player.getMaxExp()) {
            return;
        }
        if (!player.getActiveClass().isBase() && !Config.SERVICES_DELEVEL_ALLOW_FOR_SUBCLASS) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/changebase_err02.htm"));
            return;
        }
        if (Delevel.getItemCount(player, Config.SERVICES_DELEVEL_SELL_ITEM) < (long)Config.SERVICES_DELEVEL_SELL_PRICE) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        int n = player.getLevel();
        Delevel.removeItem(player, Config.SERVICES_DELEVEL_SELL_ITEM, Config.SERVICES_DELEVEL_SELL_PRICE);
        player.addExpAndSp(Experience.LEVEL[player.getLevel() - Config.SERVICES_DELEVEL_REDUCTION] - player.getExp(), 0L, false, false);
        Log.service("Delevel", player, "Reset level on - " + Config.SERVICES_DELEVEL_REDUCTION + " and old level - " + n + " bought for " + Config.SERVICES_DELEVEL_SELL_ITEM + " amount " + Config.SERVICES_DELEVEL_SELL_PRICE);
    }
}

