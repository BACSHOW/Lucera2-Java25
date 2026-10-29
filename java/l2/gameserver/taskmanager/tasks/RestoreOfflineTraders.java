/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.skills.AbnormalEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestoreOfflineTraders
extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(RestoreOfflineTraders.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void runImpl() throws Exception {
        int n = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            int n2;
            connection = DatabaseFactory.getInstance().getConnection();
            if (Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0L) {
                n2 = (int)(System.currentTimeMillis() / 1000L - Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK);
                preparedStatement = connection.prepareStatement("DELETE FROM `character_variables` WHERE `name` = 'offline' AND `value` < ?");
                preparedStatement.setLong(1, n2);
                preparedStatement.executeUpdate();
                DbUtils.close(preparedStatement);
            }
            preparedStatement = connection.prepareStatement("DELETE FROM `character_variables` WHERE `name` = 'offline' AND `obj_id` IN (SELECT `obj_id` FROM `characters` WHERE `accessLevel` < 0)");
            preparedStatement.executeUpdate();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("SELECT `obj_id`,`value` FROM `character_variables` WHERE `name` = 'offline'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                n2 = resultSet.getInt("obj_id");
                int n3 = resultSet.getInt("value");
                Player player = Player.restore(n2);
                if (player == null) continue;
                if (player.isDead()) {
                    player.kick();
                    continue;
                }
                if (Config.SERVICES_OFFLINE_TRADE_NAME_COLOR_CHANGE) {
                    player.setNameColor(Config.SERVICES_OFFLINE_TRADE_NAME_COLOR);
                }
                if (Config.SERVICES_OFFLINE_TRADE_ABNORMAL != AbnormalEffect.NULL) {
                    player.startAbnormalEffect(Config.SERVICES_OFFLINE_TRADE_ABNORMAL);
                }
                player.setOfflineMode(true);
                player.setIsOnline(true);
                player.spawnMe();
                if (player.getClan() != null && player.getClan().getAnyMember(player.getObjectId()) != null) {
                    player.getClan().getAnyMember(player.getObjectId()).setPlayerInstance(player, false);
                }
                if (Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0L) {
                    player.startKickTask((Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK + (long)n3 - System.currentTimeMillis() / 1000L) * 1000L);
                }
                if (Config.SERVICES_TRADE_ONLY_FAR) {
                    for (Player player2 : World.getAroundPlayers(player, Config.SERVICES_TRADE_RADIUS, 200)) {
                        if (!player2.isInStoreMode()) continue;
                        if (player2.isInOfflineMode()) {
                            player2.setOfflineMode(false);
                            player2.kick();
                            _log.warn("Offline trader: " + player2 + " kicked.");
                            continue;
                        }
                        player2.setPrivateStoreType(0);
                    }
                }
                ++n;
            }
        }
        catch (Exception exception) {
            try {
                _log.error("Error while restoring offline traders!", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        _log.info("Restored " + n + " offline traders");
    }
}

