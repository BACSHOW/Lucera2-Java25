/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class ExpandCWH
extends Functions {
    public void get() {
        Player player = this.getSelf();
        if (player == null || !ExpandCWH.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_EXPAND_CWH_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/expand_cwh_clanrestriction.htm"));
            return;
        }
        if (clan.getWhBonus() >= Config.SEVICES_EXPAND_CWH_SLOT_LIMITS) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/expand_cwh_slotlimits.htm"));
            return;
        }
        if (ItemFunctions.getItemCount(player, Config.SERVICES_EXPAND_CWH_ITEM) < (long)Config.SERVICES_EXPAND_CWH_PRICE) {
            if (Config.SERVICES_EXPAND_CWH_ITEM == 57) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            }
            return;
        }
        if (ItemFunctions.removeItem(player, Config.SERVICES_EXPAND_CWH_ITEM, Config.SERVICES_EXPAND_CWH_PRICE, true) >= (long)Config.SERVICES_EXPAND_CWH_PRICE) {
            clan.setWhBonus(clan.getWhBonus() + Config.SERVICES_EXPAND_CWH_SLOT_AMOUNT);
            player.sendMessage(new CustomMessage("services.ExpandCWH.CapacityNow", player, new Object[0]).addNumber(Config.WAREHOUSE_SLOTS_CLAN + player.getClan().getWhBonus()));
            clan.updateClanInDB();
            Log.service("ExpandCWH", player, "increased clan warehouse " + Config.SERVICES_EXPAND_CWH_SLOT_AMOUNT + " slots for " + Config.SERVICES_EXPAND_CWH_ITEM + " amount " + Config.SERVICES_EXPAND_CWH_PRICE);
        }
        this.show();
    }

    public void show() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_EXPAND_CWH_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.getClan() == null) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/expand_cwh_clanrestriction.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/expand_cwh.htm");
        npcHtmlMessage.replace("%cwh_cap_now%", String.valueOf(Config.WAREHOUSE_SLOTS_CLAN + player.getClan().getWhBonus()));
        npcHtmlMessage.replace("%cwh_exp_price%", String.valueOf(Config.SERVICES_EXPAND_CWH_PRICE));
        npcHtmlMessage.replace("%cwh_exp_item%", String.valueOf(Config.SERVICES_EXPAND_CWH_ITEM));
        npcHtmlMessage.replace("%cwh_slot_amount%", String.valueOf(Config.SERVICES_EXPAND_CWH_SLOT_AMOUNT));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }
}

