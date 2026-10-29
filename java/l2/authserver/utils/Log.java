/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import l2.authserver.Config;
import l2.authserver.accounts.Account;
import l2.authserver.database.L2DatabaseFactory;
import l2.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private static final Logger _log = LoggerFactory.getLogger(Log.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void LogAccount(Account account) {
        if (!Config.LOGIN_LOG) {
            return;
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = L2DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO `account_log` (`time`, `login`, `ip`) VALUES(?,?,?)");
            preparedStatement.setInt(1, account.getLastAccess());
            preparedStatement.setString(2, account.getLogin());
            preparedStatement.setString(3, account.getLastIP());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("", exception);
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

