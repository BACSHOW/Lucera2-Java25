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
import l2.gameserver.model.entity.residence.ClanHall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClanHallDAO {
    private static final Logger _log = LoggerFactory.getLogger(ClanHallDAO.class);
    private static final ClanHallDAO uP = new ClanHallDAO();
    public static final String SELECT_SQL_QUERY = "SELECT `siege_date`, `own_date`, `last_siege_date`, `auction_desc`, `auction_length`, `auction_min_bid`, `cycle`, `paid_cycle` FROM `clanhall` WHERE `id` = ?";
    public static final String UPDATE_SQL_QUERY = "UPDATE `clanhall` SET `siege_date`=?, `last_siege_date`=?, `own_date`=?, `auction_desc`=?, `auction_length`=?, `auction_min_bid`=?, `cycle`=?, `paid_cycle`=? WHERE `id`=?";

    public static ClanHallDAO getInstance() {
        return uP;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void select(ClanHall clanHall) {
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
                preparedStatement.setInt(1, clanHall.getId());
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block4;
                clanHall.getSiegeDate().setTimeInMillis(resultSet.getLong("siege_date"));
                clanHall.getLastSiegeDate().setTimeInMillis(resultSet.getLong("last_siege_date"));
                clanHall.getOwnDate().setTimeInMillis(resultSet.getLong("own_date"));
                clanHall.setAuctionLength(resultSet.getInt("auction_length"));
                clanHall.setAuctionMinBid(resultSet.getLong("auction_min_bid"));
                clanHall.setAuctionDescription(resultSet.getString("auction_desc"));
                clanHall.setCycle(resultSet.getInt("cycle"));
                clanHall.setPaidCycle(resultSet.getInt("paid_cycle"));
            }
            catch (Exception exception) {
                try {
                    _log.error("ClanHallDAO.select(ClanHall):" + exception, exception);
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

    public void update(ClanHall clanHall) {
        if (!clanHall.getJdbcState().isUpdatable()) {
            return;
        }
        clanHall.setJdbcState(JdbcEntityState.STORED);
        this.a(clanHall);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void a(ClanHall clanHall) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_SQL_QUERY);
            preparedStatement.setLong(1, clanHall.getSiegeDate().getTimeInMillis());
            preparedStatement.setLong(2, clanHall.getLastSiegeDate().getTimeInMillis());
            preparedStatement.setLong(3, clanHall.getOwnDate().getTimeInMillis());
            preparedStatement.setString(4, clanHall.getAuctionDescription());
            preparedStatement.setInt(5, clanHall.getAuctionLength());
            preparedStatement.setLong(6, clanHall.getAuctionMinBid());
            preparedStatement.setInt(7, clanHall.getCycle());
            preparedStatement.setInt(8, clanHall.getPaidCycle());
            preparedStatement.setInt(9, clanHall.getId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("ClanHallDAO#update0(ClanHall): " + exception, exception);
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

