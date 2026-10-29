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

public class PromoCodeUserDAO {
    public static final PromoCodeUserDAO INSTANCE = new PromoCodeUserDAO();
    private static final Logger _log = LoggerFactory.getLogger(PromoCodeUserDAO.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean isContains(String string, String string2) {
        boolean bl;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT code FROM promocode_users WHERE account_name=? AND code=?");
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
}

