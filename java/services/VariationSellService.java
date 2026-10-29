/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.GameServer;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.VariationGroupHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.ShortCutRegister;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.support.VariationGroupData;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariationSellService
extends Functions
implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(VariationSellService.class);
    private static Map<Integer, VariationSellServiceTemplate> bYr;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Map<Integer, VariationSellServiceTemplate> ea() {
        HashMap<Integer, VariationSellServiceTemplate> hashMap = new HashMap<Integer, VariationSellServiceTemplate>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            GameServer.getInstance().getDbmsStructureSynchronizer(connection).synchronize("variation_sell_service");
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT `menuId` AS `menuId`,  `variationOption1` AS `variationOption1`,  `variationOption2` AS `variationOption2`,  `consumeList` AS `consumeList` FROM `variation_sell_service_template`");
            while (resultSet.next()) {
                int n = resultSet.getInt("menuId");
                int n2 = resultSet.getInt("variationOption1");
                int n3 = resultSet.getInt("variationOption2");
                String string = resultSet.getString("consumeList").trim();
                List<Pair<ItemTemplate, Long>> list = Functions.parseItemIdAmountList(string);
                hashMap.put(n, new VariationSellServiceTemplate(n, n2, n3, list));
            }
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        catch (Exception exception) {
            LOG.error("VariationSellService: Cant load variation sell template data.", exception);
        }
        finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        return Collections.unmodifiableMap(hashMap);
    }

    public void buyVariation(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null || !NpcInstance.canBypassCheck(player, npcInstance) || stringArray == null || stringArray.length == 0) {
            return;
        }
        VariationSellServiceTemplate variationSellServiceTemplate = null;
        try {
            variationSellServiceTemplate = bYr.get(Integer.parseInt(stringArray[0]));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (variationSellServiceTemplate == null) {
            return;
        }
        ItemInstance itemInstance = null;
        itemInstance = variationSellServiceTemplate.process(player);
        if (itemInstance == null) {
            player.sendMessage(new CustomMessage("services.VariationSellService.failed", player, new Object[0]));
            return;
        }
        player.sendMessage(new CustomMessage("services.VariationSellService.success", player, itemInstance));
    }

    @Override
    public void onLoad() {
        bYr = VariationSellService.ea();
    }

    @Override
    public void onReload() {
        bYr = VariationSellService.ea();
    }

    @Override
    public void onShutdown() {
    }

    private static class VariationSellServiceTemplate {
        private final int bYs;
        private final int bYt;
        private final int bYu;
        private final List<Pair<ItemTemplate, Long>> bYv;

        private VariationSellServiceTemplate(int n, int n2, int n3, List<Pair<ItemTemplate, Long>> list) {
            this.bYs = n;
            this.bYt = n2;
            this.bYu = n3;
            this.bYv = list;
        }

        private boolean aE(Player player) {
            for (Pair<ItemTemplate, Long> pair : this.bYv) {
                if (player.getInventory().getCountOf(pair.getLeft().getItemId()) >= pair.getRight()) continue;
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                return false;
            }
            return true;
        }

        private boolean consume(Player player) {
            PcInventory pcInventory = player.getInventory();
            for (Pair<ItemTemplate, Long> pair : this.bYv) {
                if (ItemFunctions.removeItem(player, pair.getLeft().getItemId(), pair.getRight(), true) >= pair.getRight()) continue;
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                return false;
            }
            return true;
        }

        public ItemInstance process(Player player) {
            if (player == null || player.isLogoutStarted() || player.isTeleporting() || !Functions.CheckPlayerConditions(player)) {
                return null;
            }
            if (!this.aE(player)) {
                return null;
            }
            ItemInstance itemInstance = player.getInventory().getPaperdollItem(7);
            if (itemInstance == null) {
                player.sendMessage(new CustomMessage("services.VariationSellService.process.EquippedItemRequired", player, new Object[0]));
                return null;
            }
            if (itemInstance.isAugmented()) {
                player.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.STATIC);
                return null;
            }
            List<VariationGroupData> list = VariationGroupHolder.getInstance().getDataForItemId(itemInstance.getItemId());
            if (list == null || list.isEmpty()) {
                player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
                return null;
            }
            if (!this.consume(player)) {
                return null;
            }
            boolean bl = itemInstance.isEquipped();
            if (bl) {
                player.getInventory().unEquipItem(itemInstance);
            }
            itemInstance.setVariationStat1(this.bYt);
            itemInstance.setVariationStat2(this.bYu);
            if (bl) {
                player.getInventory().equipItem(itemInstance);
            }
            player.sendPacket((IStaticPacket)new InventoryUpdate().addModifiedItem(itemInstance));
            for (ShortCut shortCut : player.getAllShortCuts()) {
                if (shortCut.getId() != itemInstance.getObjectId() || shortCut.getType() != 1) continue;
                player.sendPacket((IStaticPacket)new ShortCutRegister(player, shortCut));
            }
            player.sendChanges();
            Log.service("VariationSellService", player, "bought an augment to " + itemInstance.getObjectId() + " [" + itemInstance.getName() + "]  Augment1 " + this.bYt + " Augment2 " + this.bYu);
            return itemInstance;
        }
    }
}

