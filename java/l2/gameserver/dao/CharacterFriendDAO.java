/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.Friend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterFriendDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterFriendDAO.class);
    private static final CharacterFriendDAO uE = new CharacterFriendDAO();

    public static CharacterFriendDAO getInstance() {
        return uE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<Integer, Friend> select(Player player) {
        HashMap<Integer, Friend> hashMap = new HashMap<Integer, Friend>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT f.friend_id, c.char_name, s.class_id, s.level FROM character_friends f LEFT JOIN characters c ON f.friend_id = c.obj_Id LEFT JOIN character_subclasses s ON ( f.friend_id = s.char_obj_id AND s.active =1 ) WHERE f.char_id = ?");
            preparedStatement.setInt(1, player.getObjectId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int n = resultSet.getInt("f.friend_id");
                String string = resultSet.getString("c.char_name");
                int n2 = resultSet.getInt("s.class_id");
                int n3 = resultSet.getInt("s.level");
                hashMap.put(n, new Friend(n, string, n3, n2));
            }
        }
        catch (Exception exception) {
            try {
                _log.error("CharacterFriendDAO.load(L2Player): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return hashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(Player player, Player player2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO `character_friends` (`char_id`,`friend_id`) VALUES(?,?)");
            preparedStatement.setInt(1, player.getObjectId());
            preparedStatement.setInt(2, player2.getObjectId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn(player.getFriendList() + " could not add friend objectid: " + player2.getObjectId(), exception);
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
            preparedStatement = connection.prepareStatement("DELETE FROM `character_friends` WHERE (`char_id`=? AND `friend_id`=?) OR (`char_id`=? AND `friend_id`=?)");
            preparedStatement.setInt(1, player.getObjectId());
            preparedStatement.setInt(2, n);
            preparedStatement.setInt(3, n);
            preparedStatement.setInt(4, player.getObjectId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("FriendList: could not delete friend objectId: " + n + " ownerId: " + player.getObjectId(), exception);
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

