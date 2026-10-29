/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private static final Logger awN = LoggerFactory.getLogger("chat");
    private static final Logger awO = LoggerFactory.getLogger("gmactions");
    private static final Logger awP = LoggerFactory.getLogger("item");
    private static final Logger awQ = LoggerFactory.getLogger("game");
    private static final Logger awR = LoggerFactory.getLogger("debug");
    private static final Logger awS = LoggerFactory.getLogger("network");
    private static final Logger awT = LoggerFactory.getLogger("service");

    public static void service(String string, Player player, String string2) {
        if (!Config.LOG_SERVICES) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        stringBuilder.append(string);
        stringBuilder.append(']');
        stringBuilder.append(' ');
        stringBuilder.append('[');
        stringBuilder.append(player.getName());
        stringBuilder.append('-');
        stringBuilder.append(player.getObjectId());
        stringBuilder.append(']');
        stringBuilder.append(' ');
        stringBuilder.append(string2);
        awT.info(stringBuilder.toString());
    }

    public static void add(String string, String string2, Player player) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string2);
        if (player != null) {
            stringBuilder.append(' ');
            stringBuilder.append(player);
        }
        stringBuilder.append(' ');
        stringBuilder.append(string);
        awQ.info(stringBuilder.toString());
    }

    public static void add(String string, String string2) {
        Log.add(string, string2, null);
    }

    public static void debug(String string) {
        awR.debug(string);
    }

    public static void network(String string) {
        awS.error(string);
    }

    public static void network(String string, Throwable throwable) {
        awS.error(string, throwable);
    }

    public static void debug(String string, Throwable throwable) {
        awR.debug(string, throwable);
    }

    public static void LogChat(String string, String string2, String string3, String string4, int n) {
        if (!Config.LOG_CHAT) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        if (n > 0) {
            stringBuilder.append(' ');
            stringBuilder.append(n);
        }
        stringBuilder.append(' ');
        stringBuilder.append('[');
        stringBuilder.append(string2);
        if (string3 != null) {
            stringBuilder.append(" -> ");
            stringBuilder.append(string3);
        }
        stringBuilder.append(']');
        stringBuilder.append(' ');
        stringBuilder.append(string4);
        awN.info(stringBuilder.toString());
    }

    public static void LogCommand(Player player, GameObject gameObject, String string, boolean bl) {
        if (!Config.LOG_GM) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (bl) {
            stringBuilder.append("SUCCESS");
        } else {
            stringBuilder.append("FAIL   ");
        }
        stringBuilder.append(' ');
        stringBuilder.append(player);
        if (gameObject != null) {
            stringBuilder.append(" -> ");
            stringBuilder.append(gameObject);
        }
        stringBuilder.append(' ');
        stringBuilder.append(string);
        awO.info(stringBuilder.toString());
    }

    public static void LogItem(Player player, ItemLog itemLog, ItemInstance itemInstance) {
        Log.a(player, itemLog, itemInstance, itemInstance.getItemId(), itemInstance.getCount(), 0L, 0);
    }

    public static void LogItem(Player player, ItemLog itemLog, ItemInstance itemInstance, long l) {
        Log.a(player, itemLog, itemInstance, itemInstance.getItemId(), l, 0L, 0);
    }

    public static void LogItem(Player player, ItemLog itemLog, ItemInstance itemInstance, long l, long l2) {
        Log.a(player, itemLog, itemInstance, itemInstance.getItemId(), l, l2, 0);
    }

    public static void LogItem(Player player, ItemLog itemLog, ItemInstance itemInstance, long l, long l2, int n) {
        Log.a(player, itemLog, itemInstance, itemInstance.getItemId(), l, l2, n);
    }

    public static void LogItem(Player player, ItemLog itemLog, int n, long l) {
        Log.a(player, itemLog, null, n, l, 0L, 0);
    }

    public static void LogItem(Player player, ItemLog itemLog, int n, long l, long l2) {
        Log.a(player, itemLog, null, n, l, l2, 0);
    }

    public static void LogItem(Player player, ItemLog itemLog, int n, long l, long l2, int n2) {
        Log.a(player, itemLog, null, n, l, l2, n2);
    }

    private static void a(Player player, ItemLog itemLog, ItemInstance itemInstance, int n, long l, long l2, int n2) {
        if (!Config.LOG_ITEM) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((Object)itemLog);
        stringBuilder.append(' ');
        stringBuilder.append(player.getName());
        stringBuilder.append('[').append(player.getObjectId()).append(']').append(' ');
        stringBuilder.append('(').append("IP: ").append(player.getIP()).append(' ').append("Account: ").append(player.getAccountName()).append(')').append(' ');
        stringBuilder.append('(').append("X: ").append(player.getX()).append(' ').append("Y: ").append(player.getY()).append(' ').append("Z: ").append(player.getZ()).append(')');
        stringBuilder.append(' ');
        stringBuilder.append(n);
        stringBuilder.append(' ');
        if (itemInstance != null) {
            if (itemInstance.getEnchantLevel() > 0) {
                stringBuilder.append('+');
                stringBuilder.append(itemInstance.getEnchantLevel());
                stringBuilder.append(' ');
            }
            stringBuilder.append(itemInstance.getTemplate().getName());
            if (!itemInstance.getTemplate().getAdditionalName().isEmpty()) {
                stringBuilder.append(' ');
                stringBuilder.append('<').append(itemInstance.getTemplate().getAdditionalName()).append('>');
            }
            stringBuilder.append(' ');
            if (itemInstance.getAttributes().getValue() > 0) {
                stringBuilder.append('(');
                stringBuilder.append("Fire: ");
                stringBuilder.append(itemInstance.getAttributes().getFire());
                stringBuilder.append(' ');
                stringBuilder.append("Water: ");
                stringBuilder.append(itemInstance.getAttributes().getWater());
                stringBuilder.append(' ');
                stringBuilder.append("Wind: ");
                stringBuilder.append(itemInstance.getAttributes().getWind());
                stringBuilder.append(' ');
                stringBuilder.append("Earth: ");
                stringBuilder.append(itemInstance.getAttributes().getEarth());
                stringBuilder.append(' ');
                stringBuilder.append("Holy: ");
                stringBuilder.append(itemInstance.getAttributes().getHoly());
                stringBuilder.append(' ');
                stringBuilder.append("Unholy: ");
                stringBuilder.append(itemInstance.getAttributes().getUnholy());
                stringBuilder.append(')');
                stringBuilder.append(' ');
            }
            stringBuilder.append('(');
            stringBuilder.append(itemInstance.getCount());
            stringBuilder.append(')');
            stringBuilder.append('[');
            stringBuilder.append(itemInstance.getObjectId());
            stringBuilder.append(']');
        } else {
            ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
            stringBuilder.append(itemTemplate.getName());
            if (!itemTemplate.getAdditionalName().isEmpty()) {
                stringBuilder.append(' ');
                stringBuilder.append('<').append(itemTemplate.getAdditionalName()).append('>');
            }
        }
        stringBuilder.append(' ');
        stringBuilder.append("Count: ").append(l);
        switch (itemLog) {
            case CraftCreate: 
            case CraftDelete: {
                stringBuilder.append(' ');
                stringBuilder.append("Recipe: ").append(n2);
                break;
            }
            case PrivateStoreBuy: 
            case PrivateStoreSell: 
            case RecipeShopBuy: 
            case RecipeShopSell: {
                stringBuilder.append(' ');
                stringBuilder.append("Price: ").append(l2);
                if (l <= 1L) break;
                stringBuilder.append(' ').append("Total: ").append(SafeMath.mulAndLimit(l2, l));
                break;
            }
            case MultiSellIngredient: 
            case MultiSellProduct: {
                stringBuilder.append(' ');
                stringBuilder.append("MultiSell: ").append(n2);
                break;
            }
            case NpcBuy: {
                stringBuilder.append(' ');
                stringBuilder.append("BuyList: ").append(n2);
                stringBuilder.append(' ');
                stringBuilder.append("Price: ").append(l2);
                break;
            }
            case NpcCreate: 
            case NpcDelete: {
                stringBuilder.append(' ');
                stringBuilder.append("NPC: ").append(n2);
                break;
            }
            case QuestCreate: 
            case QuestDelete: {
                stringBuilder.append(' ');
                stringBuilder.append("QuestId: ").append(n2);
                break;
            }
            case EventCreate: 
            case EventDelete: {
                stringBuilder.append(' ');
                stringBuilder.append("Event: ").append(n2);
                break;
            }
            case AuctionPlaceItem: {
                stringBuilder.append(' ');
                stringBuilder.append("currency: ").append(n2);
                break;
            }
            case ChangeAppearance: {
                stringBuilder.append(' ');
                stringBuilder.append("Changed Appearance to: ").append(n2);
                break;
            }
            case EnchantCrystallize: 
            case EnchantFail: 
            case EnchantReset: {
                stringBuilder.append(' ');
                stringBuilder.append("Enchant Scroll_Id: ").append(n2);
            }
        }
        awP.info(stringBuilder.toString());
    }

    public static void LogPetition(Player player, Integer n, String string) {
    }

    public static void LogAudit(Player player, String string, String string2) {
    }

    public static enum ItemLog {
        Create,
        Delete,
        RemoveItem,
        Drop,
        PvPDrop,
        Crystalize,
        EnchantFail,
        EnchantReset,
        EnchantCrystallize,
        EnchantSuccess,
        Pickup,
        PetPickup,
        PartyPickup,
        PrivateStoreBuy,
        PrivateStoreSell,
        RecipeShopBuy,
        RecipeShopSell,
        CraftCreate,
        CraftDelete,
        TradeBuy,
        CastleManorSeedBuy,
        TradeSell,
        FromPet,
        ToPet,
        AuctionPlaceItem,
        AuctionSell,
        AuctionBuy,
        AuctionMoneyReceived,
        AuctionMoneyReduce,
        AuctionItemRefundReturn,
        AchievementReceived,
        PostRecieve,
        PostSend,
        PostCancel,
        PostExpire,
        PostPrice,
        RefundSell,
        RefundReturn,
        WarehouseDeposit,
        WarehouseWithdraw,
        FreightWithdraw,
        FreightDeposit,
        ClanWarehouseDeposit,
        ClanWarehouseWithdraw,
        ExtractCreate,
        ExtractDelete,
        NpcBuy,
        NpcCreate,
        NpcDelete,
        MultiSellIngredient,
        MultiSellProduct,
        QuestCreate,
        QuestDelete,
        EventCreate,
        EventDelete,
        ChangeAppearance,
        RemoveAppearance;

    }
}

