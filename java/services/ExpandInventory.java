/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class ExpandInventory
extends Functions {
    public void get() {
        Player player = this.getSelf();
        if (player == null || !ExpandInventory.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_EXPAND_INVENTORY_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.getInventoryLimit() >= Config.SERVICES_EXPAND_INVENTORY_MAX) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/expand_inventory_max.htm"));
            return;
        }
        if (ItemFunctions.getItemCount(player, Config.SERVICES_EXPAND_INVENTORY_ITEM) < (long)Config.SERVICES_EXPAND_INVENTORY_PRICE) {
            if (Config.SERVICES_EXPAND_INVENTORY_ITEM == 57) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            }
            return;
        }
        if (ItemFunctions.removeItem(player, Config.SERVICES_EXPAND_INVENTORY_ITEM, Config.SERVICES_EXPAND_INVENTORY_PRICE, true) >= (long)Config.SERVICES_EXPAND_INVENTORY_PRICE) {
            player.setExpandInventory(player.getExpandInventory() + Config.SERVICES_EXPAND_INVENTORY_SLOT_AMOUNT);
            player.setVar("ExpandInventory", String.valueOf(player.getExpandInventory()), -1L);
            Log.service("ExpandInventory", player, "increased inventory " + Config.SERVICES_EXPAND_INVENTORY_SLOT_AMOUNT + " slots for " + Config.SERVICES_EXPAND_INVENTORY_ITEM + " amount " + Config.SERVICES_EXPAND_INVENTORY_PRICE);
        }
        this.show();
    }

    public void show() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_EXPAND_INVENTORY_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/expand_inventory.htm");
        npcHtmlMessage.replace("%inven_cap_now%", String.valueOf(player.getInventoryLimit()));
        npcHtmlMessage.replace("%inven_limit%", String.valueOf(Config.SERVICES_EXPAND_INVENTORY_MAX));
        npcHtmlMessage.replace("%inven_exp_price%", String.valueOf(Config.SERVICES_EXPAND_INVENTORY_PRICE));
        npcHtmlMessage.replace("%inven_exp_item%", String.valueOf(Config.SERVICES_EXPAND_INVENTORY_ITEM));
        npcHtmlMessage.replace("%inven_exp_slot_amount%", String.valueOf(Config.SERVICES_EXPAND_INVENTORY_SLOT_AMOUNT));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }
}

