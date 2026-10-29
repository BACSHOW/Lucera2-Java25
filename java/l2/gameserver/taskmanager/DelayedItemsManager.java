/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayedItemsManager
extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(DelayedItemsManager.class);
    private static DelayedItemsManager ard;
    private static final Object aaK;
    private int are = 0;

    public static DelayedItemsManager getInstance() {
        if (ard == null) {
            ard = new DelayedItemsManager();
        }
        return ard;
    }

    public DelayedItemsManager() {
        Connection connection = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            this.are = this.a(connection);
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
        finally {
            DbUtils.closeQuietly(connection);
        }
        ThreadPoolManager.getInstance().schedule(this, 10000L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int a(Connection connection) {
        int n;
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        block4: {
            preparedStatement = null;
            resultSet = null;
            n = this.are;
            try {
                preparedStatement = connection.prepareStatement("SELECT MAX(`payment_id`) AS `last` FROM `items_delayed`");
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block4;
                n = resultSet.getInt("last");
            }
            catch (Exception exception) {
                try {
                    _log.error("", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(preparedStatement, resultSet);
            }
        }
        DbUtils.closeQuietly(preparedStatement, resultSet);
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void runImpl() throws Exception {
        block9: {
            Player player = null;
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                int n = this.a(connection);
                if (n == this.are) break block9;
                Object object = aaK;
                synchronized (object) {
                    preparedStatement = connection.prepareStatement("SELECT DISTINCT `owner_id` FROM `items_delayed` WHERE `payment_status`=0 AND `payment_id` > ?");
                    preparedStatement.setInt(1, this.are);
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        player = GameObjectsStorage.getPlayer(resultSet.getInt("owner_id"));
                        if (player == null) continue;
                        this.loadDelayed(player, true);
                    }
                    this.are = n;
                }
            }
            catch (Exception exception) {
                _log.error("", exception);
            }
            finally {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
        }
        ThreadPoolManager.getInstance().schedule(this, 10000L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int loadDelayed(Player player, boolean bl) {
        if (player == null) {
            return 0;
        }
        int n = player.getObjectId();
        PcInventory pcInventory = player.getInventory();
        if (pcInventory == null) {
            return 0;
        }
        int n2 = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSet = null;
        Object object = aaK;
        synchronized (object) {
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("SELECT * FROM `items_delayed` WHERE `owner_id`=? AND `payment_status`=0");
                preparedStatement.setInt(1, n);
                resultSet = preparedStatement.executeQuery();
                preparedStatement2 = connection.prepareStatement("UPDATE `items_delayed` SET `payment_status`=1 WHERE `payment_id`=?");
                while (resultSet.next()) {
                    int n3 = resultSet.getInt("item_id");
                    long l = resultSet.getLong("count");
                    int n4 = resultSet.getInt("enchant_level");
                    int n5 = resultSet.getInt("variationId1");
                    int n6 = resultSet.getInt("variationId2");
                    int n7 = resultSet.getInt("payment_id");
                    int n8 = resultSet.getInt("flags");
                    boolean bl2 = ItemHolder.getInstance().getTemplate(n3).isStackable();
                    boolean bl3 = false;
                    int n9 = 0;
                    while ((long)n9 < (bl2 ? 1L : l)) {
                        ItemInstance itemInstance;
                        ItemInstance itemInstance2 = ItemFunctions.createItem(n3);
                        if (itemInstance2.isStackable()) {
                            itemInstance2.setCount(l);
                        } else {
                            itemInstance2.setEnchantLevel(n4);
                            itemInstance2.setVariationStat1(n5);
                            itemInstance2.setVariationStat2(n6);
                        }
                        itemInstance2.setLocation(ItemInstance.ItemLocation.INVENTORY);
                        itemInstance2.setCustomFlags(n8);
                        if (l > 0L && (itemInstance = pcInventory.addItem(itemInstance2)) == null) {
                            _log.warn("Unable to delayed create item " + n3 + " request " + n7);
                        } else {
                            bl3 = true;
                            ++n2;
                            if (bl && l > 0L) {
                                player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(n3, bl2 ? l : 1L, n4));
                            }
                            player.sendMessage(new CustomMessage("l2.gameserver.taskmanager.DelayedItemsManager.ItemSendMessage", player, new Object[0]));
                        }
                        ++n9;
                    }
                    if (!bl3) continue;
                    Log.add("<add owner_id=" + n + " item_id=" + n3 + " count=" + l + " enchant_level=" + n4 + " variation_1=" + n5 + " variation_2=" + n6 + " payment_id=" + n7 + "/>", "delayed_add");
                    preparedStatement2.setInt(1, n7);
                    preparedStatement2.execute();
                }
            }
            catch (Exception exception) {
                try {
                    _log.error("Could not load delayed items for player " + player + "!", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(preparedStatement2);
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(preparedStatement2);
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
            DbUtils.closeQuietly(preparedStatement2);
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        return n2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDelayed(int n, int n2, int n3, int n4, String string) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO \t`items_delayed`\t(\t`payment_id`, \t\t`owner_id`, \t\t`item_id`, \t\t`count`, \t\t`enchant_level`, \t\t`variationId1`, \t\t`variationId2`, \t\t`flags`, \t\t`payment_status`, \t\t`description`\t) SELECT \tMAX(`payment_id`) + 1, \t?, ?, ?, ?, 0, 0 ,0, 0, ? \tFROM `items_delayed`");
            preparedStatement.setInt(1, n);
            preparedStatement.setInt(2, n2);
            preparedStatement.setInt(3, n3);
            preparedStatement.setInt(4, n4);
            preparedStatement.setString(5, string);
            preparedStatement.executeUpdate();
        }
        catch (SQLException sQLException) {
            try {
                _log.error("Could not add delayed items " + n2 + " " + n3 + "(+" + n4 + ") for objId " + n + " desc \"" + string + "\" !", sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    static {
        aaK = new Object();
    }
}

