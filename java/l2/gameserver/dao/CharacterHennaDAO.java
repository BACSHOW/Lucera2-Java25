/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.data.xml.holder.HennaHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.templates.Henna;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterHennaDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterHennaDAO.class);
    private static CharacterHennaDAO uG = new CharacterHennaDAO();

    public static CharacterHennaDAO getInstance() {
        return uG;
    }

    private CharacterHennaDAO() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void select(Player player) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `slot`, `symbol_id` FROM `character_hennas` WHERE `char_obj_id`=? AND `class_index`=?");
            preparedStatement.setInt(1, player.getObjectId());
            preparedStatement.setInt(2, player.getActiveClassId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Henna henna;
                int n;
                int n2 = resultSet.getInt("slot");
                if (n2 < 1 || n2 > 3 || (n = resultSet.getInt("symbol_id")) == 0 || (henna = HennaHolder.getInstance().getHenna(n)) == null) continue;
                player.getHennas()[n2 - 1] = henna;
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("could not restore henna: " + exception);
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
    public void insert(Player player, int n, int n2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO `character_hennas` (`char_obj_id`, `symbol_id`, `slot`, `class_index`) VALUES (?,?,?,?)");
            preparedStatement.setInt(1, player.getObjectId());
            preparedStatement.setInt(2, n2);
            preparedStatement.setInt(3, n);
            preparedStatement.setInt(4, player.getActiveClassId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("could not save char henna: " + exception);
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
            preparedStatement = connection.prepareStatement("DELETE FROM `character_hennas` WHERE `char_obj_id`=? and `slot`=? and `class_index`=?");
            preparedStatement.setInt(1, player.getObjectId());
            preparedStatement.setInt(2, n);
            preparedStatement.setInt(3, player.getActiveClassId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("could not remove char henna: " + exception, exception);
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

