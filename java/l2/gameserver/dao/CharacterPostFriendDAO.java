/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterPostFriendDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterPostFriendDAO.class);
    private static final CharacterPostFriendDAO uH = new CharacterPostFriendDAO();
    private static final String SELECT_SQL_QUERY = "SELECT `pf`.`post_friend`, `c`.`char_name` FROM `character_post_friends` `pf` LEFT JOIN `characters` `c` ON `pf`.`post_friend` = `c`.`obj_Id` WHERE `pf`.`object_id` = ?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO `character_post_friends`(`object_id`, `post_friend`) VALUES (?,?)";
    private static final String DELETE_SQL_QUERY = "DELETE FROM `character_post_friends` WHERE `object_id`=? AND `post_friend`=?";

    public static CharacterPostFriendDAO getInstance() {
        return uH;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IntObjectMap<String> select(Player player) {
        CHashIntObjectMap<String> cHashIntObjectMap = new CHashIntObjectMap<String>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SELECT_SQL_QUERY);
            preparedStatement.setInt(1, player.getObjectId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String string = resultSet.getString(2);
                if (string == null) continue;
                cHashIntObjectMap.put(resultSet.getInt(1), resultSet.getString(2));
            }
        }
        catch (Exception exception) {
            try {
                _log.error("CharacterPostFriendDAO.load(L2Player): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return cHashIntObjectMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(Player player, int n) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(INSERT_SQL_QUERY);
            preparedStatement.setInt(1, player.getObjectId());
            preparedStatement.setInt(2, n);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("CharacterPostFriendDAO.insert(L2Player, int): " + exception, exception);
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
    public void delete(Player player, int n) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(DELETE_SQL_QUERY);
            preparedStatement.setInt(1, player.getObjectId());
            preparedStatement.setInt(2, n);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("CharacterPostFriendDAO.delete(L2Player, int): " + exception, exception);
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

