/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Experience;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;

public class ResetLevelService
extends Functions {
    private static final String bXL = "ServiceResetLevelCount";
    private static final String bXM = "@ResetLevelService_ReuseDelay";

    public void reset_service_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICE_RESET_LEVEL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/reset_level_services.htm");
        npcHtmlMessage.replace("%reward_item_id%", String.valueOf(Config.SERVICE_RESET_LEVEL_ITEM_ID));
        npcHtmlMessage.replace("%reward_item_count%", String.valueOf(Config.SERVICE_RESET_LEVEL_ITEM_COUNT));
        npcHtmlMessage.replace("%current_level%", String.valueOf(player.getLevel()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void reset_service() {
        Player player = this.getSelf();
        if (player == null || !ResetLevelService.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICE_RESET_LEVEL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.getActiveClass().isBase() && !Config.SERVICE_RESET_ALLOW_FOR_SUBCLASS) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/changebase_err02.htm"));
            return;
        }
        if (player.getLevel() < Config.SERVICE_RESET_LEVEL_NEED || (long)player.getLevel() > player.getMaxExp()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/reset_level_services_low.htm").replace("%required_level%", String.valueOf(Config.SERVICE_RESET_LEVEL_NEED)));
            return;
        }
        if (Config.SERVICE_RESET_LEVEL_LIMIT > 0 && player.getVarInt(bXL, 0) >= Config.SERVICE_RESET_LEVEL_LIMIT) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/reset_level_services_limit.htm").replace("%reset_count_limit%", String.valueOf(Config.SERVICE_RESET_LEVEL_LIMIT)));
            return;
        }
        if (Config.SERVICE_RESET_REUSE_DELAY > 0L && player.getVarLong(bXM, 0L) > System.currentTimeMillis()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/reset_level_service_time_not_come.htm").replace("%reuse_delay%", String.valueOf(Math.max(player.getVarLong(bXM, 0L) - System.currentTimeMillis(), 0L) / 60000L)));
            return;
        }
        if (Config.SERVICE_RESET_LEVEL_LIMIT >= 0) {
            player.setVar(bXL, player.getVarInt(bXL, 0) + 1, -1L);
        }
        if (Config.SERVICE_RESET_REUSE_DELAY > 0L) {
            player.setVar(bXM, System.currentTimeMillis() + Config.SERVICE_RESET_REUSE_DELAY, -1L);
        }
        ResetLevelService.addItem(player, Config.SERVICE_RESET_LEVEL_ITEM_ID, Config.SERVICE_RESET_LEVEL_ITEM_COUNT);
        player.addExpAndSp(Experience.LEVEL[player.getLevel() - Config.SERVICE_RESET_LEVEL_REMOVE] - player.getExp(), 0L, false, false);
        Log.service("ResetLevelService", player, "Level reset on " + Config.SERVICE_RESET_LEVEL_REMOVE + " and received an award" + Config.SERVICE_RESET_LEVEL_ITEM_ID + " amount " + Config.SERVICE_RESET_LEVEL_ITEM_COUNT);
    }
}

