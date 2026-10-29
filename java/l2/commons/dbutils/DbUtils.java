/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.dbutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtils {
    public static void close(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static void close(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
    }

    public static void close(Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }

    public static void close(Statement statement, ResultSet resultSet) throws SQLException {
        DbUtils.close(statement);
        DbUtils.close(resultSet);
    }

    public static void closeQuietly(Connection connection) {
        try {
            DbUtils.close(connection);
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void closeQuietly(Connection connection, Statement statement) {
        try {
            DbUtils.closeQuietly(statement);
        }
        finally {
            DbUtils.closeQuietly(connection);
        }
    }

    public static void closeQuietly(Statement statement, ResultSet resultSet) {
        try {
            DbUtils.closeQuietly(statement);
        }
        finally {
            DbUtils.closeQuietly(resultSet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void closeQuietly(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            DbUtils.closeQuietly(resultSet);
        }
        finally {
            try {
                DbUtils.closeQuietly(statement);
            }
            finally {
                DbUtils.closeQuietly(connection);
            }
        }
    }

    public static void closeQuietly(ResultSet resultSet) {
        try {
            DbUtils.close(resultSet);
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void closeQuietly(Statement statement) {
        try {
            DbUtils.close(statement);
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }
}

