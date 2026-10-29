/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.idfactory;

import gnu.trove.TIntArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.idfactory.BitSetIDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IdFactory {
    private static final Logger _log = LoggerFactory.getLogger(IdFactory.class);
    public static final String[][] EXTRACT_OBJ_ID_TABLES = new String[][]{{"characters", "obj_id"}, {"items", "item_id"}, {"clan_data", "clan_id"}, {"ally_data", "ally_id"}, {"pets", "objId"}, {"couples", "id"}};
    public static final int FIRST_OID = 0x10000000;
    public static final int LAST_OID = Integer.MAX_VALUE;
    public static final int FREE_OBJECT_ID_SIZE = 0x6FFFFFFF;
    protected static final IdFactory _instance = new BitSetIDFactory();
    protected boolean initialized;
    protected long releasedCount = 0L;

    public static final IdFactory getInstance() {
        return _instance;
    }

    protected IdFactory() {
        this.bw();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void bw() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE characters SET online = 0");
            _log.info("IdFactory: Clear characters online status.");
        }
        catch (SQLException sQLException) {
            try {
                _log.error("", sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, statement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, statement);
        }
        DbUtils.closeQuietly(connection, statement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected int[] extractUsedObjectIDTable() throws SQLException {
        TIntArrayList tIntArrayList = new TIntArrayList();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            statement = connection.createStatement();
            for (String[] stringArray : EXTRACT_OBJ_ID_TABLES) {
                resultSet = statement.executeQuery("SELECT " + stringArray[1] + " FROM " + stringArray[0]);
                int n = tIntArrayList.size();
                while (resultSet.next()) {
                    tIntArrayList.add(resultSet.getInt(1));
                }
                DbUtils.close(resultSet);
                n = tIntArrayList.size() - n;
                if (n <= 0) continue;
                _log.info("IdFactory: Extracted " + n + " used id's from " + stringArray[0]);
            }
        }
        finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        int[] nArray = tIntArrayList.toNativeArray();
        Arrays.sort(nArray);
        _log.info("IdFactory: Extracted total " + nArray.length + " used id's.");
        return nArray;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public abstract int getNextId();

    public void releaseId(int n) {
        ++this.releasedCount;
    }

    public long getReleasedCount() {
        return this.releasedCount;
    }

    public abstract int size();
}

