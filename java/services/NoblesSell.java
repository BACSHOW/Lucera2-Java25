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
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;

public class NoblesSell
extends Functions {
    public void show() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_NOBLESS_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.isNoble()) {
            player.sendMessage(new CustomMessage("services.NoblesSell.AlreadyHave", player, new Object[0]));
            return;
        }
        if (Config.SERVICES_NOBLESS_SELL_WITHOUT_SUBCLASS && player.getClassId().getLevel() < 4) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/service_noble_sell_need_3_job.htm");
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            return;
        }
        if (!Config.SERVICES_NOBLESS_SELL_WITHOUT_SUBCLASS && player.getSubLevel() < Config.NOBLESS_LEVEL_FOR_SELL) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/service_noble_sell_need_sub_class.htm");
            npcHtmlMessage.replace("%noble_level%", String.valueOf(Config.NOBLESS_LEVEL_FOR_SELL));
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            return;
        }
        if (player.getLevel() < Config.NOBLESS_LEVEL_FOR_SELL) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/service_noble_sell_need_level.htm");
            npcHtmlMessage.replace("%noble_level%", String.valueOf(Config.NOBLESS_LEVEL_FOR_SELL));
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/noble_sell.htm");
        npcHtmlMessage.replace("%noble_level%", String.valueOf(Config.NOBLESS_LEVEL_FOR_SELL));
        npcHtmlMessage.replace("%noble_sell_price%", String.valueOf(Config.SERVICES_NOBLESS_SELL_PRICE));
        npcHtmlMessage.replace("%noble_sell_item_id%", String.valueOf(Config.SERVICES_NOBLESS_SELL_ITEM));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void get() {
        Player player = this.getSelf();
        if (player == null || !NoblesSell.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_NOBLESS_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (NoblesSell.getItemCount(player, Config.SERVICES_NOBLESS_SELL_ITEM) < (long)Config.SERVICES_NOBLESS_SELL_PRICE) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        if (player.isNoble()) {
            player.sendMessage(new CustomMessage("services.NoblesSell.AlreadyHave", player, new Object[0]));
            return;
        }
        if (player.getLevel() < Config.NOBLESS_LEVEL_FOR_SELL) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_noble_sell_need_level.htm"));
            return;
        }
        NoblesSell.removeItem(player, Config.SERVICES_NOBLESS_SELL_ITEM, Config.SERVICES_NOBLESS_SELL_PRICE);
        NoblesSell.addItem(player, 7694, 1L);
        NoblesController.getInstance().addNoble(player);
        player.setNoble(true);
        player.updatePledgeClass();
        player.updateNobleSkills();
        player.sendPacket((IStaticPacket)new SkillList(player));
        player.broadcastUserInfo(true);
        Log.service("NoblesSell", player, "Buy Nobles status bought for " + Config.SERVICES_NOBLESS_SELL_ITEM + " " + Config.SERVICES_NOBLESS_SELL_PRICE);
    }
}

