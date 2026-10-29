/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.castle;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.WarehouseFunctions;

public class WarehouseInstance
extends NpcInstance {
    protected static final int COND_ALL_FALSE = 0;
    protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
    protected static final int COND_OWNER = 2;

    public WarehouseInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!WarehouseInstance.canBypassCheck(player, this)) {
            return;
        }
        if ((player.getClanPrivileges() & 0x40000) != 262144) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        if (player.getEnchantScroll() != null) {
            Log.add("Player " + player.getName() + " trying to use enchant exploit[CastleWarehouse], ban this player!", "illegal-actions");
            player.kick();
            return;
        }
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
        } else if (string.startsWith("Chat")) {
            int n = 0;
            try {
                n = Integer.parseInt(string.substring(5));
            }
            catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            this.showChatWindow(player, n, new Object[0]);
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        player.sendActionFailed();
        String string = "castle/warehouse/castlewarehouse-no.htm";
        int n2 = this.validateCondition(player);
        if (n2 > 0) {
            if (n2 == 1) {
                string = "castle/warehouse/castlewarehouse-busy.htm";
            } else if (n2 == 2) {
                string = n == 0 ? "castle/warehouse/castlewarehouse.htm" : "castle/warehouse/castlewarehouse-" + n + ".htm";
            }
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile(string);
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    protected int validateCondition(Player player) {
        if (player.isGM()) {
            return 2;
        }
        if (this.getCastle() != null && this.getCastle().getId() > 0 && player.getClan() != null) {
            if (((SiegeEvent)this.getCastle().getSiegeEvent()).isInProgress()) {
                return 1;
            }
            if (this.getCastle().getOwnerId() == player.getClanId()) {
                return 2;
            }
        }
        return 0;
    }

    @Override
    protected boolean canInteractWithKarmaPlayer() {
        return true;
    }
}

