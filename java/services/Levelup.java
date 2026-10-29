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

public class Levelup
extends Functions {
    public void levelup_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_LEVEL_UP_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/levelup_change.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_LEVEL_UP_SELL_ITEM));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_LEVEL_UP_SELL_PRICE));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void levelup() {
        Player player = this.getSelf();
        if (player == null || !Levelup.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_LEVEL_UP_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.getVarB("NoExp")) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/exp_freezed_levelup.htm"));
            return;
        }
        if (player.getLevel() >= (player.isSubClassActive() ? Experience.getMaxSubLevel() : Experience.getMaxLevel()) || player.getExp() > player.getMaxExp()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/level_upchange_max_level.htm"));
            return;
        }
        if (Levelup.getItemCount(player, Config.SERVICES_LEVEL_UP_SELL_ITEM) < (long)Config.SERVICES_LEVEL_UP_SELL_PRICE) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        int n = player.getLevel();
        Levelup.removeItem(player, Config.SERVICES_LEVEL_UP_SELL_ITEM, Config.SERVICES_LEVEL_UP_SELL_PRICE);
        player.addExpAndSp(Experience.LEVEL[Math.min(player.getLevel() + Config.SERVICES_LEVEL_UP_COUNT, Experience.LEVEL.length - 1)] - player.getExp(), 0L, false, false);
        Log.service("Levelup", player, "Levelup on - " + Config.SERVICES_LEVEL_UP_COUNT + " and old level - " + n + " bought for " + Config.SERVICES_LEVEL_UP_SELL_ITEM + " amount " + Config.SERVICES_LEVEL_UP_SELL_PRICE);
        this.levelup_page();
    }
}

