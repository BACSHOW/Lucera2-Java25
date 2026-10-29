/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CastleHiredGuardDAO {
    private static final Logger _log = LoggerFactory.getLogger(CastleHiredGuardDAO.class);
    private static final CastleHiredGuardDAO uz = new CastleHiredGuardDAO();
    public static final String SELECT_SQL_QUERY = "SELECT * FROM `castle_hired_guards` WHERE `residence_id`=?";
    public static final String INSERT_SQL_QUERY = "INSERT INTO `castle_hired_guards`(`residence_id`, `item_id`, `x`, `y`, `z`) VALUES (?, ?, ?, ?, ?)";
    public static final String DELETE_SQL_QUERY = "DELETE FROM `castle_hired_guards` WHERE `residence_id`=?";
    public static final String DELETE_SQL_QUERY2 = "DELETE FROM `castle_hired_guards` WHERE `residence_id`=? AND `item_id`=? AND `x`=? AND `y`=? AND `z`=?";

    public static CastleHiredGuardDAO getInstance() {
        return uz;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load(Castle castle) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SELECT_SQL_QUERY);
            preparedStatement.setInt(1, castle.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int n = resultSet.getInt("item_id");
                Location location = new Location(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                ItemInstance itemInstance = ItemFunctions.createItem(n);
                itemInstance.spawnMe(location);
                castle.getSpawnMerchantTickets().add(itemInstance);
            }
        }
        catch (Exception exception) {
            try {
                _log.error("CastleHiredGuardDAO:load(Castle): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(Residence residence, int n, Location location) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(INSERT_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.setInt(2, n);
            preparedStatement.setInt(3, location.x);
            preparedStatement.setInt(4, location.y);
            preparedStatement.setInt(5, location.z);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("CastleHiredGuardDAO:insert(Residence, int, Location): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void delete(Residence residence, ItemInstance itemInstance) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(DELETE_SQL_QUERY2);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.setInt(2, itemInstance.getItemId());
            preparedStatement.setInt(3, itemInstance.getLoc().x);
            preparedStatement.setInt(4, itemInstance.getLoc().y);
            preparedStatement.setInt(5, itemInstance.getLoc().z);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("CastleHiredGuardDAO:delete(Residence): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void delete(Residence residence) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(DELETE_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("CastleHiredGuardDAO:delete(Residence): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }
}

