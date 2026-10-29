/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager.actionrunner.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.taskmanager.actionrunner.tasks.AutomaticTask;
import l2.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteExpiredVarsTask
extends AutomaticTask {
    private static final Logger _log = LoggerFactory.getLogger(DeleteExpiredVarsTask.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doTask() throws Exception {
        long l = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        HashMap<Integer, Object> hashMap = new HashMap<Integer, Object>();
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `obj_id`, `name` FROM `character_variables` WHERE `expire_time` > 0 AND `expire_time` < ?");
            preparedStatement.setLong(1, System.currentTimeMillis());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String string = resultSet.getString("name");
                String object = Strings.stripSlashes(resultSet.getString("obj_id"));
                hashMap.put(Integer.parseInt(object), string);
            }
        }
        catch (Exception exception) {
            try {
                _log.error("", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        if (!hashMap.isEmpty()) {
            for (Map.Entry entry : hashMap.entrySet()) {
                Player player = GameObjectsStorage.getPlayer((Integer)entry.getKey());
                if (player != null && player.isOnline()) {
                    player.unsetVar((String)entry.getValue());
                    continue;
                }
                mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public long reCalcTime(boolean bl) {
        return System.currentTimeMillis() + 600000L;
    }
}

