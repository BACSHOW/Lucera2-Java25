/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import gnu.trove.TObjectIntHashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PromoCodeLimitDAO {
    public static final PromoCodeLimitDAO INSTANCE = new PromoCodeLimitDAO();
    private static final Logger _log = LoggerFactory.getLogger(PromoCodeLimitDAO.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TObjectIntHashMap<String> select() {
        TObjectIntHashMap<String> tObjectIntHashMap = new TObjectIntHashMap<String>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM promocode_limits");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tObjectIntHashMap.put(resultSet.getString("code"), resultSet.getInt("count"));
            }
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
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return tObjectIntHashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(String string, int n) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("REPLACE INTO promocode_limits (code, count) VALUES(?,?)");
            preparedStatement.setString(1, string);
            preparedStatement.setInt(2, n);
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

