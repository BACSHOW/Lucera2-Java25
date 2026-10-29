/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerVariables {
    private static final Logger _log = LoggerFactory.getLogger(ServerVariables.class);
    private static StatsSet Ba = null;

    private static StatsSet bA() {
        if (Ba == null) {
            Ba = new StatsSet();
            ServerVariables.bB();
        }
        return Ba;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void bB() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM `server_variables`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Ba.set(resultSet.getString("name"), resultSet.getString("value"));
            }
        }
        catch (Exception exception) {
            try {
                _log.error("", exception);
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
    private static void j(String string) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            String string2 = ServerVariables.bA().getString(string, "");
            if (string2.isEmpty()) {
                preparedStatement = connection.prepareStatement("DELETE FROM `server_variables` WHERE `name` = ?");
                preparedStatement.setString(1, string);
                preparedStatement.execute();
            } else {
                preparedStatement = connection.prepareStatement("REPLACE INTO `server_variables` (`name`, `value`) VALUES (?,?)");
                preparedStatement.setString(1, string);
                preparedStatement.setString(2, string2);
                preparedStatement.execute();
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
        finally {
            DbUtils.closeQuietly(connection, preparedStatement);
        }
    }

    public static boolean getBool(String string) {
        return ServerVariables.bA().getBool(string);
    }

    public static boolean getBool(String string, boolean bl) {
        return ServerVariables.bA().getBool(string, bl);
    }

    public static int getInt(String string) {
        return ServerVariables.bA().getInteger(string);
    }

    public static int getInt(String string, int n) {
        return ServerVariables.bA().getInteger(string, n);
    }

    public static long getLong(String string) {
        return ServerVariables.bA().getLong(string);
    }

    public static long getLong(String string, long l) {
        return ServerVariables.bA().getLong(string, l);
    }

    public static double getFloat(String string) {
        return ServerVariables.bA().getDouble(string);
    }

    public static double getFloat(String string, double d) {
        return ServerVariables.bA().getDouble(string, d);
    }

    public static String getString(String string) {
        return ServerVariables.bA().getString(string);
    }

    public static String getString(String string, String string2) {
        return ServerVariables.bA().getString(string, string2);
    }

    public static void set(String string, boolean bl) {
        ServerVariables.bA().set(string, bl);
        ServerVariables.j(string);
    }

    public static void set(String string, int n) {
        ServerVariables.bA().set(string, n);
        ServerVariables.j(string);
    }

    public static void set(String string, long l) {
        ServerVariables.bA().set(string, l);
        ServerVariables.j(string);
    }

    public static void set(String string, double d) {
        ServerVariables.bA().set(string, d);
        ServerVariables.j(string);
    }

    public static void set(String string, String string2) {
        ServerVariables.bA().set(string, string2);
        ServerVariables.j(string);
    }

    public static void unset(String string) {
        ServerVariables.bA().unset(string);
        ServerVariables.j(string);
    }
}

