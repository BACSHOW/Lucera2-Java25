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
import l2.gameserver.dao.CastleDoorUpgradeDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.entity.residence.Residence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CastleDamageZoneDAO {
    private static final CastleDamageZoneDAO ux = new CastleDamageZoneDAO();
    private static final Logger _log = LoggerFactory.getLogger(CastleDoorUpgradeDAO.class);
    public static final String SELECT_SQL_QUERY = "SELECT `zone` FROM `castle_damage_zones` WHERE `residence_id`=?";
    public static final String INSERT_SQL_QUERY = "INSERT INTO `castle_damage_zones` (`residence_id`, `zone`) VALUES (?,?)";
    public static final String DELETE_SQL_QUERY = "DELETE FROM `castle_damage_zones` WHERE `residence_id`=?";

    public static CastleDamageZoneDAO getInstance() {
        return ux;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> load(Residence residence) {
        List<String> list = Collections.emptyList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SELECT_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            resultSet = preparedStatement.executeQuery();
            list = new ArrayList();
            while (resultSet.next()) {
                list.add(resultSet.getString("zone"));
            }
        }
        catch (Exception exception) {
            try {
                _log.error("CastleDamageZoneDAO:load(Residence): " + exception, exception);
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
    public void insert(Residence residence, String string) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(INSERT_SQL_QUERY);
            preparedStatement.setInt(1, residence.getId());
            preparedStatement.setString(2, string);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("CastleDamageZoneDAO:insert(Residence, String): " + exception, exception);
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
                _log.error("CastleDamageZoneDAO:delete(Residence): " + exception, exception);
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

