/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dao.JdbcEntityState;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.entity.residence.Castle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CastleDAO {
    private static final Logger _log = LoggerFactory.getLogger(CastleDAO.class);
    private static final CastleDAO uw = new CastleDAO();
    public static final String SELECT_SQL_QUERY = "SELECT `tax_percent`, `treasury`, `reward_count`, `siege_date`, `last_siege_date`, `own_date` FROM `castle` WHERE `id`=? LIMIT 1";
    public static final String UPDATE_SQL_QUERY = "UPDATE `castle` SET `tax_percent`=?, `treasury`=?, `reward_count`=?, `siege_date`=?, `last_siege_date`=?, `own_date`=? WHERE `id`=?";

    public static CastleDAO getInstance() {
        return uw;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void select(Castle castle) {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection;
        block4: {
            connection = null;
            preparedStatement = null;
            resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement(SELECT_SQL_QUERY);
                preparedStatement.setInt(1, castle.getId());
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block4;
                castle.setTaxPercent(resultSet.getInt("tax_percent"));
                castle.setTreasury(resultSet.getLong("treasury"));
                castle.setRewardCount(resultSet.getInt("reward_count"));
                castle.getSiegeDate().setTimeInMillis(resultSet.getLong("siege_date"));
                castle.getLastSiegeDate().setTimeInMillis(resultSet.getLong("last_siege_date"));
                castle.getOwnDate().setTimeInMillis(resultSet.getLong("own_date"));
            }
            catch (Exception exception) {
                try {
                    _log.error("CastleDAO.select(Castle):" + exception, exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }

    public void update(Castle castle) {
        if (!castle.getJdbcState().isUpdatable()) {
            return;
        }
        this.a(castle);
        castle.setJdbcState(JdbcEntityState.STORED);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void a(Castle castle) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_SQL_QUERY);
            preparedStatement.setInt(1, castle.getTaxPercent0());
            preparedStatement.setLong(2, castle.getTreasury());
            preparedStatement.setInt(3, castle.getRewardCount());
            preparedStatement.setLong(4, castle.getSiegeDate().getTimeInMillis());
            preparedStatement.setLong(5, castle.getLastSiegeDate().getTimeInMillis());
            preparedStatement.setLong(6, castle.getOwnDate().getTimeInMillis());
            preparedStatement.setInt(7, castle.getId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("CastleDAO#update0(Castle): " + exception, exception);
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

