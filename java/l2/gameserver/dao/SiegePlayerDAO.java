/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.entity.residence.Residence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiegePlayerDAO {
    private static final Logger _log = LoggerFactory.getLogger(SiegePlayerDAO.class);
    private static final SiegePlayerDAO vr = new SiegePlayerDAO();
    public static final String INSERT_SQL_QUERY = "INSERT INTO `siege_players`(`residence_id`, `object_id`, `clan_id`) VALUES (?,?,?)";
    public static final String DELETE_SQL_QUERY = "DELETE FROM `siege_players` WHERE `residence_id`=? AND `object_id`=? AND `clan_id`=?";
    public static final String DELETE_SQL_QUERY2 = "DELETE FROM `siege_players` WHERE `residence_id`=?";
    public static final String SELECT_SQL_QUERY = "SELECT `object_id` FROM `siege_players` WHERE `residence_id`=? AND `clan_id`=?";

    public static SiegePlayerDAO getInstance() {
        return vr;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> select(Residence residence, int n) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SELECT_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.setInt(2, n);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                arrayList.add(resultSet.getInt("object_id"));
            }
        }
        catch (Exception exception) {
            try {
                _log.error("SiegePlayerDAO.select(Residence, int): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(Residence residence, int n, int n2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(INSERT_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.setInt(2, n2);
            preparedStatement.setInt(3, n);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("SiegePlayerDAO.insert(Residence, int, int): " + exception, exception);
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
    public void delete(Residence residence, int n, int n2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(DELETE_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.setInt(2, n2);
            preparedStatement.setInt(3, n);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("SiegePlayerDAO.delete(Residence, int, int): " + exception, exception);
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
            preparedStatement = connection.prepareStatement(DELETE_SQL_QUERY2);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("SiegePlayerDAO.delete(Residence): " + exception, exception);
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

