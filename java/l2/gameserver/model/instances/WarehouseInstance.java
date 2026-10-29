/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PackageToList;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.WarehouseFunctions;

public class WarehouseInstance
extends NpcInstance {
    public WarehouseInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = "";
        string = n2 == 0 ? "" + n : n + "-" + n2;
        if (this.getTemplate().getHtmRoot() != null) {
            return this.getTemplate().getHtmRoot() + string + ".htm";
        }
        return "warehouse/" + string + ".htm";
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!WarehouseInstance.canBypassCheck(player, this)) {
            return;
        }
        if (player.getEnchantScroll() != null) {
            Log.add("Player " + player.getName() + " trying to use enchant exploit[Warehouse], ban this player!", "illegal-actions");
            player.setEnchantScroll(null);
            return;
        }
        if (string.startsWith("deposit_items")) {
            player.sendPacket((IStaticPacket)new PackageToList(player));
        } else if (string.startsWith("withdraw_items")) {
            WarehouseFunctions.showFreightWindow(player);
        } else if (string.startsWith("WithdrawP")) {
            int n = Integer.parseInt(string.substring(10));
            if (n == 99) {
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                npcHtmlMessage.setFile("warehouse/personal.htm");
                npcHtmlMessage.replace("%npcname%", this.getName());
                player.sendPacket((IStaticPacket)npcHtmlMessage);
            } else {
                WarehouseFunctions.showRetrieveWindow(player, n);
            }
        } else if (string.equals("DepositP")) {
            WarehouseFunctions.showDepositWindow(player);
        } else if (string.startsWith("WithdrawC")) {
            int n = Integer.parseInt(string.substring(10));
            if (n == 99) {
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                npcHtmlMessage.setFile("warehouse/clan.htm");
                npcHtmlMessage.replace("%npcname%", this.getName());
                player.sendPacket((IStaticPacket)npcHtmlMessage);
            } else {
                WarehouseFunctions.showWithdrawWindowClan(player, n);
            }
        } else if (string.equals("DepositC")) {
            WarehouseFunctions.showDepositWindowClan(player);
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}

