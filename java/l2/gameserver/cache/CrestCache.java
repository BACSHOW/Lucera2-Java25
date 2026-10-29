/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.cache;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrestCache {
    public static final int ALLY_CREST_SIZE = 192;
    public static final int CREST_SIZE = 256;
    public static final int LARGE_CREST_SIZE = 2176;
    private static final Logger _log = LoggerFactory.getLogger(CrestCache.class);
    private static final CrestCache ui = new CrestCache();
    private final TIntIntHashMap uj = new TIntIntHashMap();
    private final TIntIntHashMap uk = new TIntIntHashMap();
    private final TIntIntHashMap ul = new TIntIntHashMap();
    private final TIntObjectHashMap<byte[]> um = new TIntObjectHashMap();
    private final TIntObjectHashMap<byte[]> un = new TIntObjectHashMap();
    private final TIntObjectHashMap<byte[]> uo = new TIntObjectHashMap();
    private final ReentrantReadWriteLock up = new ReentrantReadWriteLock();
    private final Lock readLock = this.up.readLock();
    private final Lock writeLock = this.up.writeLock();

    public static final CrestCache getInstance() {
        return ui;
    }

    private CrestCache() {
        this.load();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load() {
        int n = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            int n2;
            byte[] byArray;
            int n3;
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `clan_id`, `crest` FROM `clan_data` WHERE `crest` IS NOT NULL");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ++n;
                n3 = resultSet.getInt("clan_id");
                byArray = resultSet.getBytes("crest");
                n2 = CrestCache.b(n3, byArray);
                this.uj.put(n3, n2);
                this.um.put(n2, byArray);
            }
            DbUtils.close(preparedStatement, resultSet);
            preparedStatement = connection.prepareStatement("SELECT `clan_id`, `largecrest` FROM `clan_data` WHERE `largecrest` IS NOT NULL");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ++n;
                n3 = resultSet.getInt("clan_id");
                byArray = resultSet.getBytes("largecrest");
                n2 = CrestCache.b(n3, byArray);
                this.uk.put(n3, n2);
                this.un.put(n2, byArray);
            }
            DbUtils.close(preparedStatement, resultSet);
            preparedStatement = connection.prepareStatement("SELECT `ally_id`, `crest` FROM `ally_data` WHERE `crest` IS NOT NULL");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ++n;
                n3 = resultSet.getInt("ally_id");
                byArray = resultSet.getBytes("crest");
                n2 = CrestCache.b(n3, byArray);
                this.ul.put(n3, n2);
                this.uo.put(n2, byArray);
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
        _log.info("CrestCache: Loaded " + n + " crests");
    }

    private static int b(int n, byte[] byArray) {
        return Math.abs(new HashCodeBuilder(15, 87).append(n).append(byArray).toHashCode());
    }

    public byte[] getPledgeCrest(int n) {
        byte[] byArray = null;
        this.readLock.lock();
        try {
            byArray = this.um.get(n);
        }
        finally {
            this.readLock.unlock();
        }
        return byArray;
    }

    public byte[] getPledgeCrestLarge(int n) {
        byte[] byArray = null;
        this.readLock.lock();
        try {
            byArray = this.un.get(n);
        }
        finally {
            this.readLock.unlock();
        }
        return byArray;
    }

    public byte[] getAllyCrest(int n) {
        byte[] byArray = null;
        this.readLock.lock();
        try {
            byArray = this.uo.get(n);
        }
        finally {
            this.readLock.unlock();
        }
        return byArray;
    }

    public int getPledgeCrestId(int n) {
        int n2 = 0;
        this.readLock.lock();
        try {
            n2 = this.uj.get(n);
        }
        finally {
            this.readLock.unlock();
        }
        return n2;
    }

    public int getPledgeCrestLargeId(int n) {
        int n2 = 0;
        this.readLock.lock();
        try {
            n2 = this.uk.get(n);
        }
        finally {
            this.readLock.unlock();
        }
        return n2;
    }

    public int getAllyCrestId(int n) {
        int n2 = 0;
        this.readLock.lock();
        try {
            n2 = this.ul.get(n);
        }
        finally {
            this.readLock.unlock();
        }
        return n2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removePledgeCrest(int n) {
        this.writeLock.lock();
        try {
            this.um.remove(this.uj.remove(n));
        }
        finally {
            this.writeLock.unlock();
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `clan_data` SET `crest`=? WHERE `clan_id`=?");
            preparedStatement.setNull(1, -3);
            preparedStatement.setInt(2, n);
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
    public void removePledgeCrestLarge(int n) {
        this.writeLock.lock();
        try {
            this.un.remove(this.uk.remove(n));
        }
        finally {
            this.writeLock.unlock();
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `clan_data` SET `largecrest`=? WHERE `clan_id`=?");
            preparedStatement.setNull(1, -3);
            preparedStatement.setInt(2, n);
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
    public void removeAllyCrest(int n) {
        this.writeLock.lock();
        try {
            this.uo.remove(this.ul.remove(n));
        }
        finally {
            this.writeLock.unlock();
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `ally_data` SET `crest`=? WHERE `ally_id`=?");
            preparedStatement.setNull(1, -3);
            preparedStatement.setInt(2, n);
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
    public int savePledgeCrest(int n, byte[] byArray) {
        int n2 = CrestCache.b(n, byArray);
        this.writeLock.lock();
        try {
            this.uj.put(n, n2);
            this.um.put(n2, byArray);
        }
        finally {
            this.writeLock.unlock();
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `clan_data` SET `crest`=? WHERE `clan_id`=?");
            preparedStatement.setBytes(1, byArray);
            preparedStatement.setInt(2, n);
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
        return n2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int savePledgeCrestLarge(int n, byte[] byArray) {
        int n2 = CrestCache.b(n, byArray);
        this.writeLock.lock();
        try {
            this.uk.put(n, n2);
            this.un.put(n2, byArray);
        }
        finally {
            this.writeLock.unlock();
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `clan_data` SET `largecrest`=? WHERE `clan_id`=?");
            preparedStatement.setBytes(1, byArray);
            preparedStatement.setInt(2, n);
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
        return n2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int saveAllyCrest(int n, byte[] byArray) {
        int n2 = CrestCache.b(n, byArray);
        this.writeLock.lock();
        try {
            this.ul.put(n, n2);
            this.uo.put(n2, byArray);
        }
        finally {
            this.writeLock.unlock();
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `ally_data` SET `crest`=? WHERE `ally_id`=?");
            preparedStatement.setBytes(1, byArray);
            preparedStatement.setInt(2, n);
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
        return n2;
    }

    public static boolean isValidCrestData(byte[] byArray) {
        switch (byArray.length) {
            case 192: 
            case 256: 
            case 2176: {
                break;
            }
            default: {
                return false;
            }
        }
        if (byArray[0] != 68 || byArray[1] != 68 || byArray[2] != 83 || byArray[3] != 32 || byArray[84] != 68 || byArray[85] != 88 || byArray[86] != 84 || byArray[87] != 49) {
            return false;
        }
        switch (byArray.length) {
            case 192: {
                if (byArray[12] == 16 && byArray[16] == 8) break;
                return false;
            }
            case 256: {
                if (byArray[12] == 16 && byArray[16] == 16) break;
                return false;
            }
            case 2176: {
                if (byArray[12] == 64 && byArray[16] == 64) break;
                return false;
            }
        }
        return true;
    }
}

