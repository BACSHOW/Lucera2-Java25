/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.HennaEquipList;
import l2.gameserver.network.l2.s2c.HennaUnequipList;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PackageToList;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.WarehouseFunctions;

public class ItemMallInstance
extends MerchantInstance {
    public ItemMallInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    private boolean ag(Player player) {
        if (player.getEnchantScroll() != null) {
            Log.add("Player " + player.getName() + " trying to use enchant exploit[CastleWarehouse], ban this player!", "illegal-actions");
            player.kick();
            return false;
        }
        return true;
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!ItemMallInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equals("Draw")) {
            player.sendPacket((IStaticPacket)new HennaEquipList(player));
            return;
        }
        if (string.equals("RemoveList")) {
            player.sendPacket((IStaticPacket)new HennaUnequipList(player));
            return;
        }
        if (string.equalsIgnoreCase("deposit_items")) {
            player.sendPacket((IStaticPacket)new PackageToList(player));
            return;
        }
        if (string.equalsIgnoreCase("withdraw_items")) {
            WarehouseFunctions.showFreightWindow(player);
            return;
        }
        if ((string.startsWith("Withdraw") || string.startsWith("Deposit")) && this.ag(player)) {
            if (string.startsWith("WithdrawP")) {
                int n = Integer.parseInt(string.substring(10));
                if (n == 99) {
                    NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                    npcHtmlMessage.setFile("warehouse/personal.htm");
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
                    player.sendPacket((IStaticPacket)npcHtmlMessage);
                } else {
                    WarehouseFunctions.showWithdrawWindowClan(player, n);
                }
            } else if (string.equals("DepositC")) {
                WarehouseFunctions.showDepositWindowClan(player);
            }
            return;
        }
        super.onBypassFeedback(player, string);
    }
}

