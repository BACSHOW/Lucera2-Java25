/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Log;

public final class TradeHelper {
    private TradeHelper() {
    }

    public static boolean checksIfCanOpenStore(Player player, int n) {
        String string;
        if (!player.getPlayerAccess().UseTrade) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
            return false;
        }
        if (player.getLevel() < Config.SERVICES_TRADE_MIN_LEVEL) {
            player.sendMessage(new CustomMessage("trade.NotHavePermission", player, new Object[0]).addNumber(Config.SERVICES_TRADE_MIN_LEVEL));
            return false;
        }
        if (player.isTradeBannedByGM()) {
            player.sendPacket((IStaticPacket)Msg.YOU_ARE_CURRENTLY_BANNED_FROM_ACTIVITIES_RELATED_TO_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP);
            return false;
        }
        if (player.getPrivateStoreType() != 0 && player.getPrivateStoreType() != n) {
            player.sendMessage(new CustomMessage("trade.YouCanOpenStoreOnMove", player, new Object[0]));
            return false;
        }
        String string2 = string = n == 5 ? "open_private_workshop" : "open_private_store";
        if (player.isActionBlocked(string) && (!Config.SERVICES_NO_TRADE_ONLY_OFFLINE || Config.SERVICES_NO_TRADE_ONLY_OFFLINE && player.isInOfflineMode())) {
            player.sendPacket((IStaticPacket)(n == 5 ? new SystemMessage(1297) : Msg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_IN_THIS_AREA));
            return false;
        }
        if (player.isCastingNow()) {
            player.sendPacket((IStaticPacket)Msg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
            return false;
        }
        if (player.isInCombat()) {
            player.sendPacket((IStaticPacket)Msg.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            return false;
        }
        if (player.isMoving() && !Config.ALLOW_TRADE_ON_THE_MOVE) {
            player.sendMessage(new CustomMessage("trade.YouCanOpenStoreOnMove", player, new Object[0]));
            return false;
        }
        if (player.isActionsDisabled() || player.isMounted() || player.isOlyParticipant() || player.isInDuel() || player.isProcessingRequest()) {
            return false;
        }
        if (Config.SERVICES_TRADE_ONLY_FAR) {
            boolean bl = false;
            for (Player player2 : World.getAroundPlayers(player, Config.SERVICES_TRADE_RADIUS, 200)) {
                if (!player2.isInStoreMode()) continue;
                bl = true;
                break;
            }
            if (World.getAroundNpc(player, Config.SERVICES_TRADE_RADIUS + 100, 200).size() > 0) {
                bl = true;
            }
            if (bl) {
                player.sendMessage(new CustomMessage("trade.OtherTradersNear", player, new Object[0]));
                return false;
            }
        }
        return true;
    }

    public static final void purchaseItem(Player player, Player player2, TradeItem tradeItem) {
        long l = tradeItem.getCount() * tradeItem.getOwnersPrice();
        if (!tradeItem.getItem().isStackable()) {
            if (tradeItem.getEnchantLevel() > 0) {
                player2.sendPacket((IStaticPacket)new SystemMessage(1155).addString(player.getName()).addNumber(tradeItem.getEnchantLevel()).addItemName(tradeItem.getItemId()).addNumber(l));
                player.sendPacket((IStaticPacket)new SystemMessage(1156).addString(player2.getName()).addNumber(tradeItem.getEnchantLevel()).addItemName(tradeItem.getItemId()).addNumber(l));
            } else {
                player2.sendPacket((IStaticPacket)new SystemMessage(1151).addString(player.getName()).addItemName(tradeItem.getItemId()).addNumber(l));
                player.sendPacket((IStaticPacket)new SystemMessage(1153).addString(player2.getName()).addItemName(tradeItem.getItemId()).addNumber(l));
            }
        } else {
            player2.sendPacket((IStaticPacket)new SystemMessage(1152).addString(player.getName()).addItemName(tradeItem.getItemId()).addNumber(tradeItem.getCount()).addNumber(l));
            player.sendPacket((IStaticPacket)new SystemMessage(1154).addString(player2.getName()).addItemName(tradeItem.getItemId()).addNumber(tradeItem.getCount()).addNumber(l));
        }
        Log.LogItem(player2, Log.ItemLog.PrivateStoreSell, tradeItem.getItemId(), tradeItem.getCount(), tradeItem.getOwnersPrice());
        Log.LogItem(player, Log.ItemLog.PrivateStoreBuy, tradeItem.getItemId(), tradeItem.getCount(), tradeItem.getOwnersPrice());
    }

    public static long getTax(Player player, long l) {
        long l2 = (long)((double)l * Config.SERVICES_TRADE_TAX / 100.0);
        if (player.isInZone(Zone.ZoneType.offshore)) {
            l2 = (long)((double)l * Config.SERVICES_OFFSHORE_TRADE_TAX / 100.0);
        }
        if (Config.SERVICES_TRADE_TAX_ONLY_OFFLINE && !player.isInOfflineMode()) {
            l2 = 0L;
        }
        if (Config.SERVICES_GIRAN_HARBOR_NOTAX && player.getReflection() == ReflectionManager.GIRAN_HARBOR) {
            l2 = 0L;
        }
        return l2;
    }

    public static void cancelStore(Player player) {
        player.setPrivateStoreType(0);
        if (player.isInOfflineMode()) {
            player.setOfflineMode(false);
            player.kick();
        }
    }
}

