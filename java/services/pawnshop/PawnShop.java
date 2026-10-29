/*
 * Decompiled with CFR 0.152.
 */
package services.pawnshop;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.dbutils.DbUtils;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.listener.game.OnCharacterDeleteListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.templates.OptionDataTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PawnShop
extends Functions
implements OnCharacterDeleteListener,
ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(PawnShop.class);
    private static final String bXG = "mods/pawnshop";
    private static final String bXH = "-h scripts_services.pawnshop.PawnShop:";
    private static final AtomicInteger bYL = new AtomicInteger(0);
    private static final Comparator<PawnShopItem> bYM = new Comparator<PawnShopItem>(){

        @Override
        public int compare(PawnShopItem pawnShopItem, PawnShopItem pawnShopItem2) {
            if (pawnShopItem.getCurrencyItemId() == pawnShopItem2.getCurrencyItemId()) {
                return pawnShopItem.getPrice() - pawnShopItem2.getPrice();
            }
            return pawnShopItem2.getCurrencyItemId() - pawnShopItem.getCurrencyItemId();
        }
    };
    private static CopyOnWriteArrayList<PawnShopItem> bYN = new CopyOnWriteArrayList();
    private static Map<String, Set<Integer>> bYO = new HashMap<String, Set<Integer>>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void eb() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        LinkedList<PawnShopItem> linkedList = new LinkedList<PawnShopItem>();
        int n = 0;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            GameServer.getInstance().getDbmsStructureSynchronizer(connection).synchronize("pawnshop");
            statement = connection.createStatement();
            resultSet = statement.executeQuery("{CALL `lip_ex_PawnShopLoadItems`()}");
            while (resultSet.next()) {
                PawnShopItem pawnShopItem = new PawnShopItem(resultSet.getInt("id"), resultSet.getInt("ownerId"), resultSet.getInt("itemType"), resultSet.getInt("amount"), resultSet.getInt("enchantLevel"), resultSet.getInt("currency"), resultSet.getInt("price"), resultSet.getInt("varOpt1"), resultSet.getInt("varOpt2"));
                linkedList.add(0, pawnShopItem);
                if (pawnShopItem.getId() <= n) continue;
                n = pawnShopItem.getId();
            }
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        catch (SQLException sQLException) {
            LOG.error("PawnShop: Can't load items", sQLException);
        }
        finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        int n2 = bYL.get();
        if (n > n2) {
            bYL.compareAndSet(n2, n);
        }
        bYN = new CopyOnWriteArrayList(linkedList);
    }

    public static void showStartPage(Player player, NpcInstance npcInstance) {
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        PawnShop.a(player, npcInstance, 0, "");
    }

    private static boolean b(Player player, NpcInstance npcInstance) {
        if (player == null || npcInstance == null) {
            return false;
        }
        return !player.isActionsDisabled() && (Config.ALLOW_TALK_WHILE_SITTING || !player.isSitting()) && npcInstance.isInActingRange(player);
    }

    private static void a(String[] stringArray, int[] nArray) {
        PawnShop.a(stringArray, nArray, null);
    }

    private static void a(String[] stringArray, int[] nArray, StringBuilder stringBuilder) {
        int n = 0;
        if (nArray != null) {
            for (int i = 0; n < stringArray.length && i < nArray.length; ++n, ++i) {
                String string = stringArray[n];
                try {
                    long l = Long.parseLong(string);
                    nArray[i] = (int)(l & 0xFFFFFFFFL);
                    continue;
                }
                catch (Exception exception) {
                    LOG.warn("Can't parse int arg \"" + string + "\"");
                }
            }
        }
        if (stringBuilder != null && n < stringArray.length) {
            stringBuilder.append(stringArray[n]);
            ++n;
            while (n < stringArray.length) {
                stringBuilder.append(' ').append(stringArray[n]);
                ++n;
            }
        }
    }

    private static void a(Player player, NpcInstance npcInstance, int n, String string) {
        int n2 = Config.PAWNSHOP_ITEMS_PER_PAGE * n;
        String string2 = PawnShop.L(string);
        Object[] objectArray = PawnShop.u(player, string2);
        StringBuilder stringBuilder = new StringBuilder();
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, npcInstance);
        npcHtmlMessage.setFile("mods/pawnshop/buy_list.htm");
        int n3 = n2 + Config.PAWNSHOP_ITEMS_PER_PAGE;
        for (; n2 < n3 && n2 < objectArray.length; ++n2) {
            PawnShopItem pawnShopItem = (PawnShopItem)objectArray[n2];
            stringBuilder.append(PawnShop.a(player, "pawnshop.buy_item_element", String.format("-h scripts_services.pawnshop.PawnShop:buyItem %d %d %s", pawnShopItem.getId(), n, string2), pawnShopItem.getItemTemplate(), pawnShopItem.getEnchantLevel(), pawnShopItem.getAmount(), ItemHolder.getInstance().getTemplate(pawnShopItem.getCurrencyItemId()), pawnShopItem.getPrice(), pawnShopItem.getOwnerName(), pawnShopItem.getVariationSkill()));
        }
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        npcHtmlMessage.replace("%paging%", PawnShop.a(player, n, objectArray, "buyList", string2));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private static PawnShopItem[] u(Player player, String string) {
        LinkedList<PawnShopItem> linkedList = new LinkedList<PawnShopItem>();
        boolean bl = true;
        if (!string.isEmpty()) {
            if (string.charAt(0) == '+') {
                String string2 = string.substring(1);
                if (StringUtils.isNumeric(string2)) {
                    try {
                        int n = Integer.parseInt(string2);
                        for (PawnShopItem pawnShopItem : bYN) {
                            if (!PawnShop.a(pawnShopItem) || pawnShopItem.getEnchantLevel() != n) continue;
                            linkedList.add(pawnShopItem);
                        }
                        bl = false;
                    }
                    catch (Exception exception) {
                        LOG.warn("PawnShop: Can't process item enchant level query \"" + string + "\"", exception);
                    }
                } else if (string.length() > 2 && string.charAt(1) == '+') {
                    try {
                        String string3 = string.substring(2);
                        Set<Integer> set = bYO.get(string3);
                        if (set != null) {
                            for (PawnShopItem pawnShopItem : bYN) {
                                if (!PawnShop.a(pawnShopItem) || !set.contains(pawnShopItem.getItemTypeId())) continue;
                                linkedList.add(pawnShopItem);
                            }
                            bl = false;
                        } else if ("augmented".equals(string3)) {
                            for (PawnShopItem pawnShopItem : bYN) {
                                if (!PawnShop.a(pawnShopItem) || !pawnShopItem.isAugmented()) continue;
                                linkedList.add(pawnShopItem);
                            }
                            bl = false;
                        }
                    }
                    catch (Exception exception) {
                        LOG.warn("PawnShop: Can't process item subgroup query \"" + string + "\"", exception);
                    }
                }
            }
            if (bl && StringUtils.isNumeric(string)) {
                try {
                    int n = Integer.parseInt(string);
                    for (PawnShopItem pawnShopItem : bYN) {
                        if (!PawnShop.a(pawnShopItem) || pawnShopItem.getItemTypeId() != n) continue;
                        linkedList.add(pawnShopItem);
                    }
                    bl = false;
                }
                catch (Exception exception) {
                    LOG.warn("PawnShop: Can't process item id query \"" + string + "\"", exception);
                }
            }
            if (bl) {
                if (string.length() >= Config.PAWNSHOP_MIN_QUERY_LENGTH) {
                    for (PawnShopItem pawnShopItem : bYN) {
                        if (!PawnShop.a(pawnShopItem) || !StringUtils.containsIgnoreCase(pawnShopItem.getNameForQuery(), string)) continue;
                        linkedList.add(pawnShopItem);
                    }
                    bl = false;
                } else {
                    player.sendMessage(new CustomMessage("pawnshop.query_to_short", player, new Object[0]));
                }
            }
            if (!bl && Config.PAWNSHOP_PRICE_SORT) {
                linkedList.sort(bYM);
            }
        }
        if (bl) {
            for (PawnShopItem pawnShopItem : bYN) {
                if (!PawnShop.a(pawnShopItem)) continue;
                linkedList.add(pawnShopItem);
            }
        }
        return linkedList.toArray(new PawnShopItem[linkedList.size()]);
    }

    private static void w(Player player, int n) {
        if (!player.getPlayerAccess().UseTrade || player.isTradeBannedByGM()) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            return;
        }
        if (n <= 0) {
            player.sendPacket((IStaticPacket)SystemMsg.SYSTEM_ERROR);
            return;
        }
        if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10) {
            player.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
            return;
        }
        PawnShopItem pawnShopItem = null;
        for (PawnShopItem pawnShopItem2 : bYN) {
            if (pawnShopItem2.getId() != n) continue;
            pawnShopItem = pawnShopItem2;
        }
        if (pawnShopItem == null || !PawnShop.a(pawnShopItem)) {
            return;
        }
        int n2 = pawnShopItem.getCurrencyItemId();
        long l = pawnShopItem.getPrice();
        int n3 = pawnShopItem.getOwnerId();
        if (n3 == player.getObjectId()) {
            player.sendMessage(new CustomMessage("pawnshop.owner_cant_buy_own_item", player, new Object[0]));
            return;
        }
        if (n2 <= 0 || l <= 0L || player.getInventory().getCountOf(n2) < l) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS, new SystemMessage(1959).addNumber(l).addItemName(n2));
            return;
        }
        if (pawnShopItem.getDeleted().compareAndSet(false, true)) {
            try {
                if (n2 <= 0 || l <= 0L || ItemFunctions.removeItem(player, n2, l, true) < l) {
                    pawnShopItem.getDeleted().set(false);
                    player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS, new SystemMessage(1959).addNumber(l).addItemName(n2));
                    return;
                }
            }
            catch (Exception exception) {
                LOG.error("PawnShop: Can't buy pawnshop item " + n, exception);
                pawnShopItem.getDeleted().set(false);
                return;
            }
            bYN.remove(pawnShopItem);
            pawnShopItem.delete();
            ItemInstance itemInstance = ItemFunctions.createItem(pawnShopItem.getItemTypeId());
            itemInstance.setCount(pawnShopItem.getAmount());
            itemInstance.setEnchantLevel(pawnShopItem.getEnchantLevel());
            itemInstance.setVariationStat1(pawnShopItem.getVarOpt1());
            itemInstance.setVariationStat2(pawnShopItem.getVarOpt2());
            Log.LogItem(player, Log.ItemLog.AuctionBuy, itemInstance);
            player.getInventory().addItem(itemInstance);
            player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(itemInstance.getItemId(), itemInstance.getCount(), itemInstance.getEnchantLevel()));
            Player player2 = World.getPlayer(n3);
            if (player2 != null && player2.isOnline()) {
                ItemFunctions.addItem((Playable)player2, n2, l, true);
                Log.LogItem(player2, Log.ItemLog.AuctionMoneyReceived, n2, l);
            } else {
                DelayedItemsManager.getInstance().addDelayed(n3, n2, (int)l, 0, "Reward for pawnshop item " + n + " bought by " + player + " item_obj_id=" + itemInstance.getObjectId());
            }
        }
    }

    private static void b(Player player, NpcInstance npcInstance, int n) {
        int n2 = Config.PAWNSHOP_ITEMS_PER_PAGE * n;
        Object[] objectArray = PawnShop.aM(player);
        StringBuilder stringBuilder = new StringBuilder();
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, npcInstance);
        npcHtmlMessage.setFile("mods/pawnshop/refund_list.htm");
        int n3 = n2 + Config.PAWNSHOP_ITEMS_PER_PAGE;
        for (; n2 < n3 && n2 < objectArray.length; ++n2) {
            PawnShopItem pawnShopItem = (PawnShopItem)objectArray[n2];
            stringBuilder.append(PawnShop.a(player, "pawnshop.refund_item_element", String.format("-h scripts_services.pawnshop.PawnShop:refundItem %d %d", pawnShopItem.getId(), n), pawnShopItem.getItemTemplate(), pawnShopItem.getEnchantLevel(), pawnShopItem.getAmount(), ItemHolder.getInstance().getTemplate(pawnShopItem.getCurrencyItemId()), pawnShopItem.getPrice(), pawnShopItem.getOwnerName(), pawnShopItem.getVariationSkill()));
        }
        npcHtmlMessage.replace("%paging%", PawnShop.a(player, n, objectArray, "refundList"));
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private static void x(Player player, int n) {
        if (!player.getPlayerAccess().UseTrade || player.isTradeBannedByGM()) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            return;
        }
        if (n <= 0) {
            player.sendPacket((IStaticPacket)SystemMsg.SYSTEM_ERROR);
            return;
        }
        if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10) {
            player.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
            return;
        }
        PawnShopItem pawnShopItem = null;
        for (PawnShopItem pawnShopItem2 : bYN) {
            if (pawnShopItem2.getId() != n) continue;
            pawnShopItem = pawnShopItem2;
        }
        if (pawnShopItem == null || !PawnShop.a(pawnShopItem)) {
            return;
        }
        if (Config.PAWNSHOP_REFUND_ITEM_ID > 0 && Config.PAWNSHOP_REFUND_ITEM_COUNT > 0L && player.getInventory().getCountOf(Config.PAWNSHOP_REFUND_ITEM_ID) < Config.PAWNSHOP_REFUND_ITEM_COUNT) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS, new SystemMessage(1959).addNumber(Config.PAWNSHOP_REFUND_ITEM_COUNT).addItemName(Config.PAWNSHOP_REFUND_ITEM_ID));
            return;
        }
        if (pawnShopItem.getDeleted().compareAndSet(false, true)) {
            try {
                if (Config.PAWNSHOP_REFUND_ITEM_ID > 0 && Config.PAWNSHOP_REFUND_ITEM_COUNT > 0L && ItemFunctions.removeItem(player, Config.PAWNSHOP_REFUND_ITEM_ID, Config.PAWNSHOP_REFUND_ITEM_COUNT, true) < Config.PAWNSHOP_REFUND_ITEM_COUNT) {
                    pawnShopItem.getDeleted().set(false);
                    player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS, new SystemMessage(1959).addNumber(Config.PAWNSHOP_REFUND_ITEM_COUNT).addItemName(Config.PAWNSHOP_REFUND_ITEM_ID));
                    return;
                }
            }
            catch (Exception exception) {
                LOG.error("PawnShop: Can't refund pawnshop item " + n, exception);
                pawnShopItem.getDeleted().set(false);
                return;
            }
            bYN.remove(pawnShopItem);
            pawnShopItem.delete();
            ItemInstance itemInstance = ItemFunctions.createItem(pawnShopItem.getItemTypeId());
            itemInstance.setCount(pawnShopItem.getAmount());
            itemInstance.setEnchantLevel(pawnShopItem.getEnchantLevel());
            itemInstance.setVariationStat1(pawnShopItem.getVarOpt1());
            itemInstance.setVariationStat2(pawnShopItem.getVarOpt2());
            Log.LogItem(player, Log.ItemLog.AuctionItemRefundReturn, itemInstance);
            player.getInventory().addItem(itemInstance);
            player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(itemInstance.getItemId(), itemInstance.getCount(), itemInstance.getEnchantLevel()));
        }
    }

    private static PawnShopItem[] aM(Player player) {
        LinkedList<PawnShopItem> linkedList = new LinkedList<PawnShopItem>();
        for (PawnShopItem pawnShopItem : bYN) {
            if (pawnShopItem.getOwnerId() != player.getObjectId() || !PawnShop.a(pawnShopItem)) continue;
            linkedList.add(pawnShopItem);
        }
        return linkedList.toArray(new PawnShopItem[linkedList.size()]);
    }

    private static void c(Player player, NpcInstance npcInstance, int n) {
        int n2 = Config.PAWNSHOP_ITEMS_PER_PAGE * n;
        Object[] objectArray = PawnShop.aN(player);
        StringBuilder stringBuilder = new StringBuilder();
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, npcInstance);
        npcHtmlMessage.setFile("mods/pawnshop/sell_list.htm");
        int n3 = n2 + Config.PAWNSHOP_ITEMS_PER_PAGE;
        for (; n2 < n3 && n2 < objectArray.length; ++n2) {
            ItemInstance itemInstance = (ItemInstance)objectArray[n2];
            stringBuilder.append(PawnShop.a(player, "pawnshop.sell_item_element", String.format("-h scripts_services.pawnshop.PawnShop:sellItem %d %d", itemInstance.getObjectId(), n), itemInstance.getTemplate(), itemInstance.getEnchantLevel(), (int)itemInstance.getCount(), PawnShop.m(itemInstance.getVariationStat1(), itemInstance.getVariationStat2())));
        }
        npcHtmlMessage.replace("%paging%", PawnShop.a(player, n, objectArray, "sellList"));
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private static void a(Player player, NpcInstance npcInstance, int n, int n2) {
        if (!player.getPlayerAccess().UseTrade) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            return;
        }
        if (n <= 0) {
            player.sendPacket((IStaticPacket)SystemMsg.SYSTEM_ERROR);
            return;
        }
        ItemInstance[] itemInstanceArray = PawnShop.aN(player);
        ItemInstance itemInstance = null;
        for (ItemInstance itemInstance2 : itemInstanceArray) {
            if (itemInstance2.getObjectId() != n) continue;
            itemInstance = itemInstance2;
        }
        if (itemInstance == null || !PawnShop.c(itemInstance)) {
            player.sendPacket((IStaticPacket)SystemMsg.SYSTEM_ERROR);
            return;
        }
        if (itemInstance.isAugmented() && Config.PAWNSHOP_TAX_AUGMENTED_ITEM_ID > 0 && Config.PAWNSHOP_TAX_AUGMENTED_ITEM_COUNT > 0L && player.getInventory().getCountOf(Config.PAWNSHOP_TAX_AUGMENTED_ITEM_ID) < Config.PAWNSHOP_TAX_AUGMENTED_ITEM_COUNT) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS, new SystemMessage(1959).addNumber(Config.PAWNSHOP_TAX_AUGMENTED_ITEM_COUNT).addItemName(Config.PAWNSHOP_TAX_AUGMENTED_ITEM_ID));
            return;
        }
        if (!itemInstance.isAugmented() && Config.PAWNSHOP_TAX_ITEM_ID > 0 && Config.PAWNSHOP_TAX_ITEM_COUNT > 0L && player.getInventory().getCountOf(Config.PAWNSHOP_TAX_ITEM_ID) < Config.PAWNSHOP_TAX_ITEM_COUNT) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS, new SystemMessage(1959).addNumber(Config.PAWNSHOP_TAX_ITEM_COUNT).addItemName(Config.PAWNSHOP_TAX_ITEM_ID));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, npcInstance);
        npcHtmlMessage.setFile("mods/pawnshop/sell_item.htm");
        npcHtmlMessage.replace("%back_page%", String.valueOf(n2));
        npcHtmlMessage.replace("%sell_item_name%", itemInstance.getName());
        npcHtmlMessage.replace("%currency_list%", PawnShop.ec());
        npcHtmlMessage.replace("%item%", PawnShop.a(player, "pawnshop.sell_item", null, itemInstance.getTemplate(), itemInstance.getEnchantLevel(), (int)itemInstance.getCount(), PawnShop.m(itemInstance.getVariationStat1(), itemInstance.getVariationStat2())));
        npcHtmlMessage.replace("%item_obj_id%", String.valueOf(itemInstance.getObjectId()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private static void a(Player player, NpcInstance npcInstance, int n, ItemTemplate itemTemplate, int n2) {
        if (!player.getPlayerAccess().UseTrade || player.isTradeBannedByGM()) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            return;
        }
        if (n <= 0 || itemTemplate == null) {
            player.sendPacket((IStaticPacket)SystemMsg.SYSTEM_ERROR);
            return;
        }
        ItemInstance[] itemInstanceArray = PawnShop.aN(player);
        ItemInstance itemInstance = null;
        for (ItemInstance itemInstance2 : itemInstanceArray) {
            if (itemInstance2.getObjectId() != n) continue;
            itemInstance = itemInstance2;
        }
        if (itemInstance == null || !PawnShop.c(itemInstance) || SafeMath.mulAndCheck((long)n2, itemInstance.getCount()) <= 0L) {
            player.sendPacket((IStaticPacket)SystemMsg.SYSTEM_ERROR);
            return;
        }
        if (itemInstance.isAugmented() && Config.PAWNSHOP_TAX_AUGMENTED_ITEM_ID > 0 && Config.PAWNSHOP_TAX_AUGMENTED_ITEM_COUNT > 0L && ItemFunctions.removeItem(player, Config.PAWNSHOP_TAX_AUGMENTED_ITEM_ID, Config.PAWNSHOP_TAX_AUGMENTED_ITEM_COUNT, true) < Config.PAWNSHOP_TAX_AUGMENTED_ITEM_COUNT) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS, new SystemMessage(1959).addNumber(Config.PAWNSHOP_TAX_AUGMENTED_ITEM_COUNT).addItemName(Config.PAWNSHOP_TAX_AUGMENTED_ITEM_ID));
            return;
        }
        if (!itemInstance.isAugmented() && Config.PAWNSHOP_TAX_ITEM_ID > 0 && Config.PAWNSHOP_TAX_ITEM_COUNT > 0L && ItemFunctions.removeItem(player, Config.PAWNSHOP_TAX_ITEM_ID, Config.PAWNSHOP_TAX_ITEM_COUNT, true) < Config.PAWNSHOP_TAX_ITEM_COUNT) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS, new SystemMessage(1959).addNumber(Config.PAWNSHOP_TAX_ITEM_COUNT).addItemName(Config.PAWNSHOP_TAX_ITEM_ID));
            return;
        }
        int n3 = (int)itemInstance.getCount();
        PawnShopItem pawnShopItem = new PawnShopItem(bYL.incrementAndGet(), player.getObjectId(), itemInstance.getItemId(), n3, itemInstance.getEnchantLevel(), itemTemplate.getItemId(), n2, itemInstance.getVariationStat1(), itemInstance.getVariationStat2());
        Log.LogItem(player, Log.ItemLog.AuctionPlaceItem, itemInstance, (long)n2, (long)n3, itemTemplate.getItemId());
        if (!player.getInventory().destroyItem(itemInstance)) {
            return;
        }
        player.sendPacket((IStaticPacket)SystemMessage2.removeItems(pawnShopItem.getItemTypeId(), n3));
        bYN.add(0, pawnShopItem);
        pawnShopItem.store();
    }

    private static ItemInstance[] aN(Player player) {
        ItemInstance[] itemInstanceArray;
        ArrayList<ItemInstance> arrayList = new ArrayList<ItemInstance>();
        for (ItemInstance itemInstance : itemInstanceArray = player.getInventory().getItems()) {
            if (!PawnShop.c(itemInstance) || itemInstance.getOwnerId() != player.getObjectId()) continue;
            arrayList.add(itemInstance);
        }
        return arrayList.toArray(new ItemInstance[arrayList.size()]);
    }

    private static String L(String string) {
        return string.replaceAll("[!\"#\\$%&'\\(\\)\\*,\\-\\./:;<=>\\?@\\[\\\\\\]\\^_`{\\|}~]+", "").replaceAll("[^\\w\\d+]+", " ").trim();
    }

    private static boolean c(ItemInstance itemInstance) {
        if (itemInstance == null) {
            return false;
        }
        if (itemInstance.getTemplate().isQuest()) {
            return false;
        }
        if (itemInstance.getLocation() != ItemInstance.ItemLocation.INVENTORY) {
            return false;
        }
        if (itemInstance.isAugmented() && !Config.PAWNSHOP_ALLOW_SELL_AUGMENTED_ITEMS) {
            return false;
        }
        if (itemInstance.isWeapon() && itemInstance.getEnchantLevel() < Config.PAWNSHOP_MIN_ENCHANT_LEVEL) {
            return false;
        }
        return PawnShop.a(itemInstance.getTemplate());
    }

    private static boolean a(PawnShopItem pawnShopItem) {
        if (pawnShopItem == null || pawnShopItem.getDeleted().get()) {
            return false;
        }
        if (!ArrayUtils.contains(Config.PAWNSHOP_CURRENCY_ITEM_IDS, pawnShopItem.getCurrencyItemId())) {
            return false;
        }
        return PawnShop.a(pawnShopItem.getItemTemplate());
    }

    private static boolean a(ItemTemplate itemTemplate) {
        if (itemTemplate == null) {
            return false;
        }
        if (itemTemplate.isTemporal() || itemTemplate.isShadowItem() || itemTemplate.isQuest()) {
            return false;
        }
        if (!ArrayUtils.contains((Object[])Config.PAWNSHOP_ITEMS_CLASSES, (Object)itemTemplate.getItemClass())) {
            return false;
        }
        if (itemTemplate.getItemGrade().gradeOrd() < Config.PAWNSHOP_MIN_GRADE.gradeOrd()) {
            return false;
        }
        return !ArrayUtils.contains(Config.PAWNSHOP_PROHIBITED_ITEM_IDS, itemTemplate.getItemId());
    }

    public static String formatOptSkillText(Skill skill) {
        if (skill == null) {
            return "";
        }
        if (skill.isActive()) {
            return "Active " + skill.getName() + " Lv." + skill.getLevel();
        }
        if (!skill.getTriggerList().isEmpty()) {
            return "Chance " + skill.getName() + " Lv." + skill.getLevel();
        }
        if (skill.isPassive()) {
            return "Passive " + skill.getName() + " Lv." + skill.getLevel();
        }
        return skill.getName();
    }

    private static String a(Player player, String string, String string2, ItemTemplate itemTemplate, int n, int n2, Skill skill) {
        String string3 = StringHolder.getInstance().getNotNull(player, string);
        string3 = string3.replace("%bypass%", string2 != null ? string2 : "");
        string3 = string3.replace("%item_id%", String.format("%d", itemTemplate.getItemId()));
        string3 = string3.replace("%item_name%", itemTemplate.getName());
        string3 = string3.replace("%item_add_name%", itemTemplate.getAdditionalName());
        string3 = string3.replace("%item_icon%", itemTemplate.getIcon());
        string3 = string3.replace("%item_enchant%", n > 0 ? String.format("(+%d)", n) : "");
        string3 = player.isLangRus() ? string3.replace("%item_amount%", n2 > 1 ? String.format("\u041a\u043e\u043b-\u0432\u043e: %d", n2) : "") : string3.replace("%item_amount%", n2 > 1 ? String.format("Amount: %d", n2) : "");
        string3 = string3.replace("%option%", PawnShop.formatOptSkillText(skill));
        return string3;
    }

    private static String a(Player player, String string, String string2, ItemTemplate itemTemplate, int n, int n2, ItemTemplate itemTemplate2, long l, String string3, Skill skill) {
        String string4 = StringHolder.getInstance().getNotNull(player, string);
        string4 = string4.replace("%bypass%", string2 != null ? string2 : "");
        string4 = string4.replace("%item_id%", String.valueOf(itemTemplate.getItemId()));
        string4 = string4.replace("%item_name%", itemTemplate.getName());
        string4 = string4.replace("%item_add_name%", itemTemplate.getAdditionalName());
        string4 = string4.replace("%item_icon%", itemTemplate.getIcon());
        string4 = string4.replace("%item_enchant%", n > 0 ? String.format("(+%d)", n) : "");
        string4 = player.isLangRus() ? string4.replace("%item_amount%", n2 > 1 ? String.format("\u041a\u043e\u043b-\u0432\u043e: %d", n2) : "") : string4.replace("%item_amount%", n2 > 1 ? String.format("Amount: %d", n2) : "");
        string4 = string4.replace("%option%", PawnShop.formatOptSkillText(skill));
        if (itemTemplate2 != null) {
            string4 = string4.replace("%currency_id%", String.valueOf(itemTemplate2.getItemId()));
            string4 = string4.replace("%currency_name%", itemTemplate2.getName());
            string4 = string4.replace("%price%", String.valueOf(l));
        } else {
            string4 = string4.replace("%currency_id%", "");
            string4 = string4.replace("%currency_name%", "");
            string4 = string4.replace("%price%", "");
        }
        string4 = string4.replace("%owner%", string3 != null ? string3 : "");
        return string4;
    }

    private static String ec() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Config.PAWNSHOP_CURRENCY_ITEM_IDS.length; ++i) {
            ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(Config.PAWNSHOP_CURRENCY_ITEM_IDS[i]);
            if (itemTemplate == null) continue;
            if (stringBuilder.length() > 0) {
                stringBuilder.append(';');
            }
            stringBuilder.append(itemTemplate.getName());
        }
        return stringBuilder.toString();
    }

    private static ItemTemplate O(String string) {
        string = PawnShop.L(string);
        for (int i = 0; i < Config.PAWNSHOP_CURRENCY_ITEM_IDS.length; ++i) {
            ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(Config.PAWNSHOP_CURRENCY_ITEM_IDS[i]);
            if (itemTemplate == null || !string.equalsIgnoreCase(PawnShop.L(itemTemplate.getName()))) continue;
            return itemTemplate;
        }
        return null;
    }

    private static boolean l(int n, int n2) {
        return (n2 + 1) * Config.PAWNSHOP_ITEMS_PER_PAGE < n;
    }

    private static String a(Player player, int n, Object[] objectArray, String string) {
        return PawnShop.a(player, n, objectArray, string, null);
    }

    private static String a(Player player, int n, Object[] objectArray, String string, String string2) {
        String string3 = bXH + string + (string2 != null ? " %d " + string2 : " %d");
        return PawnShop.a(player, n > 0 ? String.format(string3, n - 1).trim() : null, n, PawnShop.l(objectArray.length, n) ? String.format(string3, n + 1).trim() : null);
    }

    private static String a(Player player, String string, int n, String string2) {
        String string3 = StringHolder.getInstance().getNotNull(player, "pawnshop.paging");
        string3 = string3.replace("%prev_button%", string != null ? "<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">".replace("%prev_bypass%", string) : "");
        string3 = string3.replace("%curr_page%", Integer.toString(n + 1));
        string3 = string3.replace("%next_button%", string2 != null ? "<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">".replace("%next_bypass%", string2) : "");
        return string3;
    }

    private static Skill m(int n, int n2) {
        Skill skill = null;
        if (n > 0 || n2 > 0) {
            OptionDataTemplate optionDataTemplate = OptionDataHolder.getInstance().getTemplate(n);
            OptionDataTemplate optionDataTemplate2 = OptionDataHolder.getInstance().getTemplate(n2);
            if (optionDataTemplate2 != null && !optionDataTemplate2.getSkills().isEmpty()) {
                skill = optionDataTemplate2.getSkills().get(0);
            }
            if (optionDataTemplate != null && !optionDataTemplate.getSkills().isEmpty()) {
                skill = optionDataTemplate.getSkills().get(0);
            }
        }
        return skill;
    }

    private static void ed() {
        for (ItemTemplate itemTemplate : ItemHolder.getInstance().getAllTemplates()) {
            String string = null;
            if (itemTemplate == null) continue;
            if (itemTemplate.isWeapon()) {
                string = "weapons";
            } else if (itemTemplate.isAccessory()) {
                string = "accessories";
            } else if (itemTemplate.isArmor()) {
                string = "armors";
            }
            if (StringUtils.isBlank(string)) continue;
            Set<Integer> set = bYO.get(string);
            if (set == null) {
                set = new HashSet<Integer>();
                bYO.put(string, set);
            }
            set.add(itemTemplate.getItemId());
        }
    }

    @Override
    public void onLoad() {
        if (Config.PAWNSHOP_ENABLED) {
            PawnShop.eb();
            PawnShop.ed();
            CharacterDAO.getInstance().getCharacterDeleteListenerList().add(this);
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
    }

    public void buyList() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        PawnShop.a(player, npcInstance, 0, "");
    }

    public void buyList(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int[] nArray = new int[]{0};
        PawnShop.a(stringArray, nArray, stringBuilder);
        PawnShop.a(player, npcInstance, nArray[0], stringBuilder.toString());
    }

    public void buyItem(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int[] nArray = new int[]{-1, 0};
        PawnShop.a(stringArray, nArray, stringBuilder);
        PawnShop.w(player, nArray[0]);
        PawnShop.a(player, npcInstance, nArray[1], stringBuilder.toString());
    }

    public void refundList() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        PawnShop.b(player, npcInstance, 0);
    }

    public void refundList(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        int[] nArray = new int[]{0};
        PawnShop.a(stringArray, nArray);
        PawnShop.b(player, npcInstance, nArray[0]);
    }

    public void refundItem(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        int[] nArray = new int[]{-1, 0};
        PawnShop.a(stringArray, nArray);
        PawnShop.x(player, nArray[0]);
        PawnShop.b(player, npcInstance, nArray[1]);
    }

    public void sellList() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        PawnShop.c(player, npcInstance, 0);
    }

    public void sellList(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        int[] nArray = new int[]{0};
        PawnShop.a(stringArray, nArray);
        PawnShop.c(player, npcInstance, nArray[0]);
    }

    public void sellItem(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        int[] nArray = new int[]{-1, 0};
        PawnShop.a(stringArray, nArray);
        PawnShop.a(player, npcInstance, nArray[0], nArray[1]);
    }

    public void sellApply(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (!Config.PAWNSHOP_ENABLED || !PawnShop.b(player, npcInstance)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int[] nArray = new int[]{0, 0, -1};
        PawnShop.a(stringArray, nArray, stringBuilder);
        PawnShop.a(player, npcInstance, nArray[1], PawnShop.O(stringBuilder.toString()), nArray[2]);
        PawnShop.c(player, npcInstance, nArray[0]);
    }

    @Override
    public void onCharacterDelate(int n) {
        ArrayList<PawnShopItem> arrayList = new ArrayList<PawnShopItem>();
        for (PawnShopItem pawnShopItem : bYN) {
            if (pawnShopItem.getOwnerId() != n) continue;
            arrayList.add(pawnShopItem);
        }
        for (PawnShopItem pawnShopItem : arrayList) {
            if (!pawnShopItem.getDeleted().compareAndSet(false, true)) continue;
            bYN.remove(pawnShopItem);
            pawnShopItem.delete();
        }
    }

    private static class PawnShopItem {
        private final int _id;
        private final int _ownerId;
        private final int bYP;
        private final int bYQ;
        private final int aqn;
        private final int bYR;
        private final int _price;
        private final int bYS;
        private final int bYT;
        private final AtomicBoolean bYU;
        private String _ownerName;
        private Skill bYV;
        private String bYW;

        private PawnShopItem(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9) {
            this._id = n;
            this._ownerId = n2;
            this.bYP = n3;
            this.bYQ = n4;
            this.aqn = n5;
            this.bYR = n6;
            this._price = n7;
            this.bYS = n8;
            this.bYT = n9;
            this.bYV = PawnShop.m(n8, n9);
            this.bYU = new AtomicBoolean(false);
        }

        public AtomicBoolean getDeleted() {
            return this.bYU;
        }

        public String getOwnerName() {
            if (this._ownerName == null) {
                Player player = World.getPlayer(this.getOwnerId());
                if (player != null) {
                    this._ownerName = player.getName();
                } else {
                    this._ownerName = CharacterDAO.getInstance().getNameByObjectId(this.getOwnerId());
                    if (this._ownerName == null) {
                        this._ownerName = "";
                    }
                }
            }
            return this._ownerName;
        }

        public int getId() {
            return this._id;
        }

        public int getOwnerId() {
            return this._ownerId;
        }

        public int getItemTypeId() {
            return this.bYP;
        }

        public int getAmount() {
            return this.bYQ;
        }

        public int getEnchantLevel() {
            return this.aqn;
        }

        public int getCurrencyItemId() {
            return this.bYR;
        }

        public int getPrice() {
            return this._price;
        }

        public int getVarOpt1() {
            return this.bYS;
        }

        public int getVarOpt2() {
            return this.bYT;
        }

        public Skill getVariationSkill() {
            return this.bYV;
        }

        public boolean isAugmented() {
            return this.getVarOpt1() > 0 || this.getVarOpt2() > 0;
        }

        public ItemTemplate getItemTemplate() {
            return ItemHolder.getInstance().getTemplate(this.getItemTypeId());
        }

        public String getNameForQuery() {
            if (this.bYW == null) {
                ItemTemplate itemTemplate = this.getItemTemplate();
                this.bYW = itemTemplate != null ? PawnShop.L(itemTemplate.getName()) : "";
            }
            return this.bYW;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void store() {
            Connection connection = null;
            CallableStatement callableStatement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                callableStatement = connection.prepareCall("{CALL `lip_ex_PawnShopStoreItem`(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
                callableStatement.setInt(1, this._id);
                callableStatement.setInt(2, this._ownerId);
                callableStatement.setInt(3, this.bYP);
                callableStatement.setInt(4, this.bYQ);
                callableStatement.setInt(5, this.aqn);
                callableStatement.setInt(6, this.bYR);
                callableStatement.setInt(7, this._price);
                callableStatement.setInt(8, this.bYS);
                callableStatement.setInt(9, this.bYT);
                callableStatement.execute();
            }
            catch (SQLException sQLException) {
                try {
                    LOG.error("", sQLException);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, callableStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, callableStatement);
            }
            DbUtils.closeQuietly(connection, callableStatement);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void delete() {
            Connection connection = null;
            CallableStatement callableStatement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                callableStatement = connection.prepareCall("{CALL `lip_ex_PawnShopDeleteItem`(?)}");
                callableStatement.setInt(1, this._id);
                callableStatement.execute();
            }
            catch (SQLException sQLException) {
                try {
                    LOG.error("", sQLException);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, callableStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, callableStatement);
            }
            DbUtils.closeQuietly(connection, callableStatement);
            this.bYU.set(true);
        }
    }
}

