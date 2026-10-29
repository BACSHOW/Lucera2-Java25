/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.Residence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiegeClanDAO {
    public static final String SELECT_SQL_QUERY = "SELECT `clan_id`, `param`, `date` FROM `siege_clans` WHERE `residence_id`=? AND `type`=? ORDER BY `date`";
    public static final String INSERT_SQL_QUERY = "INSERT INTO `siege_clans`(`residence_id`, `clan_id`, `param`, `type`, `date`) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_SQL_QUERY = "UPDATE `siege_clans` SET `type`=?, `param`=? WHERE `residence_id`=? AND `clan_id`=?";
    public static final String DELETE_SQL_QUERY = "DELETE FROM `siege_clans` WHERE `residence_id`=? AND `clan_id`=? AND `type`=?";
    public static final String DELETE_SQL_QUERY2 = "DELETE FROM `siege_clans` WHERE `residence_id`=?";
    private static final Logger _log = LoggerFactory.getLogger(SiegeClanDAO.class);
    private static final SiegeClanDAO vq = new SiegeClanDAO();

    public static SiegeClanDAO getInstance() {
        return vq;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<SiegeClanObject> load(Residence residence, String string) {
        List<SiegeClanObject> list = Collections.emptyList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SELECT_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.setString(2, string);
            resultSet = preparedStatement.executeQuery();
            list = new ArrayList<SiegeClanObject>();
            while (resultSet.next()) {
                int n = resultSet.getInt("clan_id");
                long l = resultSet.getLong("param");
                long l2 = resultSet.getLong("date");
                Object s = ((SiegeEvent)residence.getSiegeEvent()).newSiegeClan(string, n, l, l2);
                if (s != null) {
                    list.add((SiegeClanObject)s);
                    continue;
                }
                _log.info("SiegeClanDAO#load(Residence, String): null clan: " + n + "; residence: " + residence.getId());
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("SiegeClanDAO#load(Residence, String): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(Residence residence, SiegeClanObject siegeClanObject) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(INSERT_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.setInt(2, siegeClanObject.getObjectId());
            preparedStatement.setLong(3, siegeClanObject.getParam());
            preparedStatement.setString(4, siegeClanObject.getType());
            preparedStatement.setLong(5, siegeClanObject.getDate());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("SiegeClanDAO#insert(Residence, SiegeClan): " + exception, exception);
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
    public void delete(Residence residence, SiegeClanObject siegeClanObject) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(DELETE_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.setInt(2, siegeClanObject.getObjectId());
            preparedStatement.setString(3, siegeClanObject.getType());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("SiegeClanDAO#delete(Residence, SiegeClan): " + exception, exception);
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
                _log.warn("SiegeClanDAO#delete(Residence): " + exception, exception);
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
    public void update(Residence residence, SiegeClanObject siegeClanObject) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_SQL_QUERY);
            preparedStatement.setString(1, siegeClanObject.getType());
            preparedStatement.setLong(2, siegeClanObject.getParam());
            preparedStatement.setInt(3, residence.getId());
            preparedStatement.setInt(4, siegeClanObject.getObjectId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("SiegeClanDAO#update(Residence, SiegeClan): " + exception, exception);
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

