/*
 * Decompiled with CFR 0.152.
 */
package bosses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpicBossState {
    private static final Logger _log = LoggerFactory.getLogger(EpicBossState.class);
    private int _bossId;
    private long fk;
    private State fl;

    public int getBossId() {
        return this._bossId;
    }

    public void setBossId(int n) {
        this._bossId = n;
    }

    public State getState() {
        return this.fl;
    }

    public void setState(State state) {
        this.fl = state;
    }

    public long getRespawnDate() {
        return this.fk;
    }

    public void setRespawnDate(long l) {
        this.fk = l + System.currentTimeMillis();
    }

    public EpicBossState(int n) {
        this(n, true);
    }

    public EpicBossState(int n, boolean bl) {
        this._bossId = n;
        if (bl) {
            this.load();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load() {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection;
        block4: {
            connection = null;
            preparedStatement = null;
            resultSet = null;
            try {
                int n;
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("SELECT * FROM epic_boss_spawn WHERE bossId = ? LIMIT 1");
                preparedStatement.setInt(1, this._bossId);
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block4;
                this.fk = resultSet.getLong("respawnDate") * 1000L;
                this.fl = this.fk - System.currentTimeMillis() <= 0L ? State.NOTSPAWN : ((n = resultSet.getInt("state")) == State.NOTSPAWN.ordinal() ? State.NOTSPAWN : (n == State.INTERVAL.ordinal() ? State.INTERVAL : (n == State.ALIVE.ordinal() ? State.ALIVE : (n == State.DEAD.ordinal() ? State.DEAD : State.NOTSPAWN))));
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
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("REPLACE INTO epic_boss_spawn (bossId,respawnDate,state) VALUES(?,?,?)");
            preparedStatement.setInt(1, this._bossId);
            preparedStatement.setInt(2, (int)(this.fk / 1000L));
            preparedStatement.setInt(3, this.fl.ordinal());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("", exception);
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
    public void update() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE epic_boss_spawn SET respawnDate=" + this.fk / 1000L + ", state=" + this.fl.ordinal() + " WHERE bossId=" + this._bossId);
            Date date = new Date(this.fk);
            _log.info("update EpicBossState: ID:" + this._bossId + ", RespawnDate:" + date + ", State:" + this.fl.toString());
        }
        catch (Exception exception) {
            try {
                _log.error("Exception on update EpicBossState: ID " + this._bossId + ", RespawnDate:" + this.fk / 1000L + ", State:" + this.fl.toString(), exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, statement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, statement);
        }
        DbUtils.closeQuietly(connection, statement);
    }

    public void setNextRespawnDate(long l) {
        this.fk = l;
    }

    public long getInterval() {
        long l = this.fk - System.currentTimeMillis();
        return l > 0L ? l : 0L;
    }

    public static enum State {
        NOTSPAWN,
        ALIVE,
        DEAD,
        INTERVAL;

    }
}

