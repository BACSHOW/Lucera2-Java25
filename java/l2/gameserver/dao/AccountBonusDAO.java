/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.actor.instances.player.Bonus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountBonusDAO {
    private static final Logger LOG = LoggerFactory.getLogger(AccountBonusDAO.class);
    private static final AccountBonusDAO us = new AccountBonusDAO();
    private static final String ut = "SELECT    `expireTime` AS `expireTime`,    `rateXp` AS `rateXp`,    `rateSp` AS `rateSp`,    `rateRaidXp` AS `rateRaidXp`,    `rateRaidSp` AS `rateRaidSp`,    `questRewardRate` AS `questRewardRate`,    `questRewardAdenaRate` AS `questRewardAdenaRate`,    `questDropRate` AS `questDropRate`,    `dropAdena` AS `dropAdena`,    `dropItems` AS `dropItems`,    `dropRaidItems` AS `dropRaidItems`,    `dropSpoil` AS `dropSpoil`,    `enchantItemBonus` AS `enchantItemBonus`,    `hwidsLimit` AS `hwidsLimit`FROM     `accounts_bonuses`WHERE     `account` = ?";
    private static final String uu = "REPLACE LOW_PRIORITY INTO `accounts_bonuses` (    `account`,    `expireTime`,    `rateXp`,    `rateSp`,    `rateRaidXp`,    `rateRaidSp`,    `questRewardRate`,    `questRewardAdenaRate`,    `questDropRate`,    `dropAdena`,    `dropItems`,    `dropRaidItems`,    `dropSpoil`,    `enchantItemBonus`,    `hwidsLimit`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String uv = "DELETE FROM `accounts_bonuses` WHERE `account` = ?";

    public static AccountBonusDAO getInstance() {
        return us;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load(String string, Bonus bonus) {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection;
        block4: {
            connection = null;
            preparedStatement = null;
            resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement(ut);
                preparedStatement.setString(1, string);
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block4;
                bonus.setBonusExpire(resultSet.getLong("expireTime"));
                bonus.setRateXp(resultSet.getFloat("rateXp"));
                bonus.setRateSp(resultSet.getFloat("rateSp"));
                bonus.setRateRaidXp(resultSet.getFloat("rateRaidXp"));
                bonus.setRateRaidSp(resultSet.getFloat("rateRaidSp"));
                bonus.setQuestRewardRate(resultSet.getFloat("questRewardRate"));
                bonus.setQuestRewardAdenaRate(resultSet.getFloat("questRewardAdenaRate"));
                bonus.setQuestDropRate(resultSet.getFloat("questDropRate"));
                bonus.setDropAdena(resultSet.getFloat("dropAdena"));
                bonus.setDropItems(resultSet.getFloat("dropItems"));
                bonus.setDropRaidItems(resultSet.getFloat("dropRaidItems"));
                bonus.setDropSpoil(resultSet.getFloat("dropSpoil"));
                bonus.setEnchantItem(resultSet.getFloat("enchantItemBonus"));
                bonus.setHwidsLimit(resultSet.getInt("hwidsLimit"));
            }
            catch (SQLException sQLException) {
                try {
                    LOG.error("Can't load account bonus for account \"" + string + "\"", sQLException);
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

    public Bonus load(String string) {
        Bonus bonus = new Bonus();
        this.load(string, bonus);
        return bonus;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void store(String string, Bonus bonus) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(uu);
            preparedStatement.setString(1, string);
            preparedStatement.setLong(2, bonus.getBonusExpire());
            preparedStatement.setFloat(3, bonus.getRateXp());
            preparedStatement.setFloat(4, bonus.getRateSp());
            preparedStatement.setFloat(5, bonus.getRateRaidXp());
            preparedStatement.setFloat(6, bonus.getRateRaidSp());
            preparedStatement.setFloat(7, bonus.getQuestRewardRate());
            preparedStatement.setFloat(8, bonus.getQuestRewardAdenaRate());
            preparedStatement.setFloat(9, bonus.getQuestDropRate());
            preparedStatement.setFloat(10, bonus.getDropAdena());
            preparedStatement.setFloat(11, bonus.getDropItems());
            preparedStatement.setFloat(12, bonus.getDropRaidItems());
            preparedStatement.setFloat(13, bonus.getDropSpoil());
            preparedStatement.setFloat(14, bonus.getEnchantItemMul());
            preparedStatement.setInt(15, bonus.getHwidsLimit());
            preparedStatement.executeUpdate();
        }
        catch (SQLException sQLException) {
            try {
                LOG.error("Can't store account bonus for account \"" + string + "\"", sQLException);
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
    public void delete(String string) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(uv);
            preparedStatement.setString(1, string);
            preparedStatement.executeUpdate();
        }
        catch (SQLException sQLException) {
            try {
                LOG.error("Can't store account bonus for account \"" + string + "\"", sQLException);
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

