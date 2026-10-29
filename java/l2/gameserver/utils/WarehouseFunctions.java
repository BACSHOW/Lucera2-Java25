/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.WareHouseDepositList;
import l2.gameserver.network.l2.s2c.WareHouseWithdrawList;
import l2.gameserver.templates.item.ItemTemplate;

public final class WarehouseFunctions {
    private WarehouseFunctions() {
    }

    public static void showFreightWindow(Player player) {
        if (!WarehouseFunctions.canShowWarehouseWithdrawList(player, Warehouse.WarehouseType.FREIGHT)) {
            player.sendActionFailed();
            return;
        }
        player.setUsingWarehouseType(Warehouse.WarehouseType.FREIGHT);
        player.sendPacket((IStaticPacket)new WareHouseWithdrawList(player, Warehouse.WarehouseType.FREIGHT, ItemTemplate.ItemClass.ALL));
    }

    public static void showRetrieveWindow(Player player, int n) {
        if (!WarehouseFunctions.canShowWarehouseWithdrawList(player, Warehouse.WarehouseType.PRIVATE)) {
            player.sendActionFailed();
            return;
        }
        player.setUsingWarehouseType(Warehouse.WarehouseType.PRIVATE);
        player.sendPacket((IStaticPacket)new WareHouseWithdrawList(player, Warehouse.WarehouseType.PRIVATE, ItemTemplate.ItemClass.values()[n]));
    }

    public static void showDepositWindow(Player player) {
        if (!WarehouseFunctions.canShowWarehouseDepositList(player, Warehouse.WarehouseType.PRIVATE)) {
            player.sendActionFailed();
            return;
        }
        player.setUsingWarehouseType(Warehouse.WarehouseType.PRIVATE);
        player.sendPacket((IStaticPacket)new WareHouseDepositList(player, Warehouse.WarehouseType.PRIVATE));
    }

    public static void showDepositWindowClan(Player player) {
        if (!WarehouseFunctions.canShowWarehouseDepositList(player, Warehouse.WarehouseType.CLAN)) {
            player.sendActionFailed();
            return;
        }
        if (!(player.isClanLeader() || (Config.ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE || player.getVarB("canWhWithdraw")) && (player.getClanPrivileges() & 8) == 8)) {
            player.sendPacket((IStaticPacket)Msg.ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE);
        }
        player.setUsingWarehouseType(Warehouse.WarehouseType.CLAN);
        player.sendPacket((IStaticPacket)new WareHouseDepositList(player, Warehouse.WarehouseType.CLAN));
    }

    public static void showWithdrawWindowClan(Player player, int n) {
        if (!WarehouseFunctions.canShowWarehouseWithdrawList(player, Warehouse.WarehouseType.CLAN)) {
            player.sendActionFailed();
            return;
        }
        if (player.getClan() == null || player.getClan().getLevel() == 0) {
            player.sendPacket((IStaticPacket)Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE);
            return;
        }
        player.setUsingWarehouseType(Warehouse.WarehouseType.CLAN);
        player.sendPacket((IStaticPacket)new WareHouseWithdrawList(player, Warehouse.WarehouseType.CLAN, ItemTemplate.ItemClass.values()[n]));
    }

    public static boolean canShowWarehouseWithdrawList(Player player, Warehouse.WarehouseType warehouseType) {
        if (!player.getPlayerAccess().UseWarehouse) {
            return false;
        }
        Warehouse warehouse = null;
        switch (warehouseType) {
            case PRIVATE: {
                warehouse = player.getWarehouse();
                break;
            }
            case FREIGHT: {
                warehouse = player.getFreight();
                break;
            }
            case CLAN: 
            case CASTLE: {
                if (player.getClan() == null || player.getClan().getLevel() == 0) {
                    player.sendPacket((IStaticPacket)Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE);
                    return false;
                }
                boolean bl = false;
                if (player.getClan() != null && (player.getClanPrivileges() & 8) == 8) {
                    bl = true;
                }
                if (!bl) {
                    player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
                    return false;
                }
                warehouse = player.getClan().getWarehouse();
                break;
            }
            default: {
                return false;
            }
        }
        if (warehouse.getSize() == 0) {
            player.sendPacket(warehouseType == Warehouse.WarehouseType.FREIGHT ? SystemMsg.NO_PACKAGES_HAVE_ARRIVED : Msg.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
            return false;
        }
        return true;
    }

    public static boolean canShowWarehouseDepositList(Player player, Warehouse.WarehouseType warehouseType) {
        if (!player.getPlayerAccess().UseWarehouse) {
            return false;
        }
        switch (warehouseType) {
            case PRIVATE: {
                return true;
            }
            case CLAN: 
            case CASTLE: {
                if (player.getClan() == null || player.getClan().getLevel() == 0) {
                    player.sendPacket((IStaticPacket)Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE);
                    return false;
                }
                if (player.getClan() == null) break;
                return true;
            }
        }
        return false;
    }
}

