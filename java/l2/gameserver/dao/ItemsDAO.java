/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ItemStateFlags;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemsDAO {
    private static final Logger _log = LoggerFactory.getLogger(ItemsDAO.class);
    private static final ItemsDAO uT = new ItemsDAO();
    private static final String uU = "{CALL `lip_GetItem`(?)}";
    private static final String uV = "{CALL `lip_LoadItemsByOwner`(?)}";
    private static final String uW = "{CALL `lip_LoadItemsByOwnerAndLoc`(?, ?)}";
    private static final String uX = "{CALL `lip_DeleteItem`(?)}";
    private static final String uY = "{CALL `lip_StoreItem`(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

    public static final ItemsDAO getInstance() {
        return uT;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void store(ItemInstance itemInstance) {
        if (!itemInstance.getItemStateFlag().get(ItemStateFlags.STATE_CHANGED)) {
            return;
        }
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            callableStatement = connection.prepareCall(uY);
            this.a(itemInstance, callableStatement);
            callableStatement.execute();
            itemInstance.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, false);
        }
        catch (SQLException sQLException) {
            try {
                _log.error("Exception while store item", sQLException);
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
    public void store(Collection<ItemInstance> collection) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            callableStatement = connection.prepareCall(uY);
            for (ItemInstance itemInstance : collection) {
                if (!itemInstance.getItemStateFlag().get(ItemStateFlags.STATE_CHANGED)) continue;
                this.a(itemInstance, callableStatement);
                callableStatement.execute();
                itemInstance.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, false);
            }
        }
        catch (SQLException sQLException) {
            try {
                _log.error("Exception while store items", sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, callableStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, callableStatement);
        }
        DbUtils.closeQuietly(connection, callableStatement);
    }

    private void a(ItemInstance itemInstance, CallableStatement callableStatement) throws SQLException {
        callableStatement.setInt(1, itemInstance.getObjectId());
        callableStatement.setInt(2, itemInstance.getOwnerId());
        callableStatement.setInt(3, itemInstance.getItemId());
        callableStatement.setLong(4, itemInstance.getCount());
        callableStatement.setInt(5, itemInstance.getLocData());
        callableStatement.setString(6, itemInstance.getLocName());
        callableStatement.setInt(7, itemInstance.getEnchantLevel());
        callableStatement.setInt(8, itemInstance.getDuration());
        callableStatement.setInt(9, itemInstance.getPeriodBegin());
        if (itemInstance.isWeapon()) {
            callableStatement.setByte(10, itemInstance.getAttackElement().getId());
            callableStatement.setInt(11, itemInstance.getAttackElementValue());
            callableStatement.setInt(12, 0);
            callableStatement.setInt(13, 0);
            callableStatement.setInt(14, 0);
            callableStatement.setInt(15, 0);
            callableStatement.setInt(16, 0);
            callableStatement.setInt(17, 0);
        } else {
            callableStatement.setByte(10, Element.NONE.getId());
            callableStatement.setInt(11, 0);
            callableStatement.setInt(12, itemInstance.getAttributes().getFire());
            callableStatement.setInt(13, itemInstance.getAttributes().getWater());
            callableStatement.setInt(14, itemInstance.getAttributes().getWind());
            callableStatement.setInt(15, itemInstance.getAttributes().getEarth());
            callableStatement.setInt(16, itemInstance.getAttributes().getHoly());
            callableStatement.setInt(17, itemInstance.getAttributes().getUnholy());
        }
        callableStatement.setInt(18, itemInstance.getVariationStat1());
        callableStatement.setInt(19, itemInstance.getVariationStat2());
        callableStatement.setInt(20, itemInstance.getBlessed());
        callableStatement.setInt(21, itemInstance.getDamaged());
        callableStatement.setInt(22, 0);
        callableStatement.setInt(23, itemInstance.getCustomFlags());
        if (itemInstance.getVisibleItemId() != itemInstance.getItemId()) {
            callableStatement.setInt(24, itemInstance.getVisibleItemId());
        } else {
            callableStatement.setInt(24, 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void delete(Collection<ItemInstance> collection) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            callableStatement = connection.prepareCall(uX);
            for (ItemInstance itemInstance : collection) {
                callableStatement.setInt(1, itemInstance.getObjectId());
                callableStatement.execute();
                itemInstance.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, false);
            }
        }
        catch (SQLException sQLException) {
            try {
                _log.error("Exception while deleting items", sQLException);
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
    public void delete(int n) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            callableStatement = connection.prepareCall(uX);
            callableStatement.setInt(1, n);
            callableStatement.execute();
        }
        catch (SQLException sQLException) {
            try {
                _log.error("Exception while deleting item", sQLException);
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
    public void delete(ItemInstance itemInstance) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            callableStatement = connection.prepareCall(uX);
            callableStatement.setInt(1, itemInstance.getObjectId());
            callableStatement.execute();
            itemInstance.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, false);
        }
        catch (SQLException sQLException) {
            try {
                _log.error("Exception while deleting item", sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, callableStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, callableStatement);
        }
        DbUtils.closeQuietly(connection, callableStatement);
    }

    private ItemInstance a(ResultSet resultSet) throws SQLException {
        int n = resultSet.getInt("item_id");
        ItemInstance itemInstance = new ItemInstance(n);
        int n2 = resultSet.getInt("owner_id");
        itemInstance.setOwnerId(n2);
        int n3 = resultSet.getInt("item_type");
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n3);
        if (itemTemplate == null) {
            _log.error("Not defined itemTypeId " + n3 + " for [" + n2 + "]");
            return null;
        }
        itemInstance.setItemId(n3);
        itemInstance.setCount(resultSet.getLong("amount"));
        itemInstance.setLocName(resultSet.getString("location"));
        itemInstance.setLocData(resultSet.getInt("slot"));
        itemInstance.setEnchantLevel(resultSet.getInt("enchant"));
        itemInstance.setDuration(resultSet.getInt("duration"));
        itemInstance.setPeriodBegin(resultSet.getInt("period"));
        byte by = resultSet.getByte("attack_attr_type");
        int n4 = resultSet.getInt("attack_attr_val");
        int n5 = resultSet.getInt("defence_attr_fire");
        int n6 = resultSet.getInt("defence_attr_water");
        int n7 = resultSet.getInt("defence_attr_wind");
        int n8 = resultSet.getInt("defence_attr_earth");
        int n9 = resultSet.getInt("defence_attr_holy");
        int n10 = resultSet.getInt("defence_attr_unholy");
        if (by != Element.NONE.getId()) {
            itemInstance.setAttributeElement(Element.VALUES[by], n4);
        } else {
            itemInstance.setAttributeElement(Element.FIRE, n5);
            itemInstance.setAttributeElement(Element.WATER, n6);
            itemInstance.setAttributeElement(Element.WIND, n7);
            itemInstance.setAttributeElement(Element.EARTH, n8);
            itemInstance.setAttributeElement(Element.HOLY, n9);
            itemInstance.setAttributeElement(Element.UNHOLY, n10);
        }
        itemInstance.setVariationStat1(resultSet.getInt("variation_stat1"));
        itemInstance.setVariationStat2(resultSet.getInt("variation_stat2"));
        itemInstance.setBlessed(resultSet.getInt("blessed"));
        itemInstance.setDamaged(resultSet.getInt("damaged"));
        itemInstance.setCustomFlags(resultSet.getInt("custom_flags"));
        itemInstance.setVisibleItemId(resultSet.getInt("item_vis_type"));
        itemInstance.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, false);
        return itemInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Collection<ItemInstance> loadItemsByOwnerIdAndLoc(int n, ItemInstance.ItemLocation itemLocation) {
        LinkedList<ItemInstance> linkedList = new LinkedList<ItemInstance>();
        Connection connection = null;
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            callableStatement = connection.prepareCall(uW);
            callableStatement.setInt(1, n);
            callableStatement.setString(2, itemLocation.name());
            resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                ItemInstance itemInstance = this.a(resultSet);
                if (itemInstance == null) continue;
                linkedList.add(itemInstance);
            }
        }
        catch (SQLException sQLException) {
            try {
                _log.error("Exception while load items", sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, callableStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, callableStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, callableStatement, resultSet);
        return linkedList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Collection<Integer> loadItemObjectIdsByOwner(int n) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            callableStatement = connection.prepareCall(uV);
            callableStatement.setInt(1, n);
            resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                linkedList.add(resultSet.getInt("item_id"));
            }
        }
        catch (SQLException sQLException) {
            try {
                _log.error("Exception while load items", sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, callableStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, callableStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, callableStatement, resultSet);
        return linkedList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance load(int n) {
        ResultSet resultSet;
        CallableStatement callableStatement;
        Connection connection;
        ItemInstance itemInstance;
        block4: {
            itemInstance = null;
            connection = null;
            callableStatement = null;
            resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                callableStatement = connection.prepareCall(uU);
                callableStatement.setInt(1, n);
                resultSet = callableStatement.executeQuery();
                if (!resultSet.next()) break block4;
                itemInstance = this.a(resultSet);
            }
            catch (SQLException sQLException) {
                try {
                    _log.error("Exception while load items", sQLException);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, callableStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, callableStatement, resultSet);
            }
        }
        DbUtils.closeQuietly(connection, callableStatement, resultSet);
        return itemInstance;
    }
}

