/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PromoCodeDAO {
    public static final PromoCodeDAO INSTANCE = new PromoCodeDAO();
    private static final Logger _log = LoggerFactory.getLogger(PromoCodeDAO.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean isActivated(String string, String string2) {
        boolean bl;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT code FROM promocodes WHERE account_name=? AND code=?");
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, string2);
            resultSet = preparedStatement.executeQuery();
            bl = resultSet.next();
        }
        catch (Exception exception) {
            try {
                _log.error(exception.getMessage(), exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            return false;
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(String string, String string2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO promocodes (account_name,code) VALUES(?,?)");
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, string2);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error(exception.getMessage(), exception);
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean isHwidActivated(String string, String string2) {
        boolean bl;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `code` FROM promocodes_hwid_limit WHERE `hwid`=? AND `code`=?");
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, string2);
            resultSet = preparedStatement.executeQuery();
            bl = resultSet.next();
        }
        catch (Exception exception) {
            try {
                _log.error(exception.getMessage(), exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            return false;
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insertHwidLimit(String string, String string2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO promocodes_hwid_limit (hwid,code) VALUES(?,?)");
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, string2);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error(exception.getMessage(), exception);
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean isIpActivated(String string, String string2) {
        boolean bl;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `code` FROM promocodes_ip_limit WHERE `ip`=? AND `code`=?");
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, string2);
            resultSet = preparedStatement.executeQuery();
            bl = resultSet.next();
        }
        catch (Exception exception) {
            try {
                _log.error(exception.getMessage(), exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            return false;
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insertIpLimit(String string, String string2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO promocodes_ip_limit (ip, code) VALUES(?,?)");
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, string2);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error(exception.getMessage(), exception);
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

