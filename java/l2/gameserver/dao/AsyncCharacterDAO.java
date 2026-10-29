package l2.gameserver.dao;

import l2.gameserver.database.queue.DatabaseWriterQueue;
import l2.gameserver.database.queue.DbWriteTask;
import l2.gameserver.model.Player;
import l2.gameserver.model.state.PlayerDirtyFlags;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO de Alta Performance para persistência cirúrgica assíncrona de jogadores.
 * Enfileira apenas instruções correspondentes aos campos sujos (dirty checking).
 */
public final class AsyncCharacterDAO {
    private static final AsyncCharacterDAO INSTANCE = new AsyncCharacterDAO();

    public static AsyncCharacterDAO getInstance() {
        return INSTANCE;
    }

    private AsyncCharacterDAO() {}

    /**
     * Enfileira uma atualização assíncrona cirúrgica de coordenadas no micro-batch.
     */
    public void updateCoordinatesAsync(int playerObjId, int x, int y, int z, int heading) {
        DatabaseWriterQueue.getInstance().enqueue(new DbWriteTask() {
            @Override
            public int getPartitionKey() {
                return playerObjId;
            }

            @Override
            public void execute(Connection conn) throws SQLException {
                try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE `characters` SET `x`=?, `y`=?, `z`=?, `heading`=? WHERE `obj_Id`=? LIMIT 1")) {
                    ps.setInt(1, x);
                    ps.setInt(2, y);
                    ps.setInt(3, z);
                    ps.setInt(4, heading);
                    ps.setInt(5, playerObjId);
                    ps.executeUpdate();
                }
            }
        });
    }

    /**
     * Enfileira atualização de PvP/PK/Karma.
     */
    public void updatePvPPkAsync(int playerObjId, int pvpKills, int pkKills, int karma) {
        DatabaseWriterQueue.getInstance().enqueue(new DbWriteTask() {
            @Override
            public int getPartitionKey() {
                return playerObjId;
            }

            @Override
            public void execute(Connection conn) throws SQLException {
                try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE `characters` SET `pvpkills`=?, `pkkills`=?, `karma`=? WHERE `obj_Id`=? LIMIT 1")) {
                    ps.setInt(1, pvpKills);
                    ps.setInt(2, pkKills);
                    ps.setInt(3, karma);
                    ps.setInt(4, playerObjId);
                    ps.executeUpdate();
                }
            }
        });
    }

    /**
     * Enfileira atualização de tempo online e último acesso.
     */
    public void updateOnlineTimeAsync(int playerObjId, int onlineTime, int lastAccess) {
        DatabaseWriterQueue.getInstance().enqueue(new DbWriteTask() {
            @Override
            public int getPartitionKey() {
                return playerObjId;
            }

            @Override
            public void execute(Connection conn) throws SQLException {
                try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE `characters` SET `onlinetime`=?, `lastAccess`=? WHERE `obj_Id`=? LIMIT 1")) {
                    ps.setInt(1, onlineTime);
                    ps.setInt(2, lastAccess);
                    ps.setInt(3, playerObjId);
                    ps.executeUpdate();
                }
            }
        });
    }

    /**
     * Enfileira atualização de HP, MP e CP atual na tabela character_subclasses.
     */
    public void updateHpMpCpAsync(int playerObjId, int classId, double curHp, double curMp, double curCp) {
        DatabaseWriterQueue.getInstance().enqueue(new DbWriteTask() {
            @Override
            public int getPartitionKey() {
                return playerObjId;
            }

            @Override
            public void execute(Connection conn) throws SQLException {
                try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE `character_subclasses` SET `curHp`=?, `curMp`=?, `curCp`=? WHERE `char_obj_id`=? AND `class_id`=? LIMIT 1")) {
                    ps.setDouble(1, curHp);
                    ps.setDouble(2, curMp);
                    ps.setDouble(3, curCp);
                    ps.setInt(4, playerObjId);
                    ps.setInt(5, classId);
                    ps.executeUpdate();
                }
            }
        });
    }

    /**
     * Enfileira atualização de EXP, SP e Level na tabela character_subclasses.
     */
    public void updateExpSpLevelAsync(int playerObjId, int classId, long exp, long sp, int level) {
        DatabaseWriterQueue.getInstance().enqueue(new DbWriteTask() {
            @Override
            public int getPartitionKey() {
                return playerObjId;
            }

            @Override
            public void execute(Connection conn) throws SQLException {
                try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE `character_subclasses` SET `exp`=?, `sp`=?, `level`=? WHERE `char_obj_id`=? AND `class_id`=? LIMIT 1")) {
                    ps.setLong(1, exp);
                    ps.setLong(2, sp);
                    ps.setInt(3, level);
                    ps.setInt(4, playerObjId);
                    ps.setInt(5, classId);
                    ps.executeUpdate();
                }
            }
        });
    }
}
