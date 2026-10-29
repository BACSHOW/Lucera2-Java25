/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResidenceFunction {
    private static final Logger _log = LoggerFactory.getLogger(ResidenceFunction.class);
    public static final int TELEPORT = 1;
    public static final int ITEM_CREATE = 2;
    public static final int RESTORE_HP = 3;
    public static final int RESTORE_MP = 4;
    public static final int RESTORE_EXP = 5;
    public static final int SUPPORT = 6;
    public static final int CURTAIN = 7;
    public static final int PLATFORM = 8;
    private int _id;
    private int _type;
    private int _level;
    private Calendar Ts;
    private boolean Tt;
    private boolean _active;
    private Map<Integer, Integer> Tu = new ConcurrentSkipListMap<Integer, Integer>();
    private Map<Integer, TeleportLocation[]> Tv = new ConcurrentSkipListMap<Integer, TeleportLocation[]>();
    private Map<Integer, int[]> Tw = new ConcurrentSkipListMap<Integer, int[]>();
    private Map<Integer, Object[][]> Tx = new ConcurrentSkipListMap<Integer, Object[][]>();
    public static final String A = "";
    public static final String W = "W";
    public static final String M = "M";
    private static final Object[][][] Ty = new Object[][][]{new Object[0][], {{SkillTable.getInstance().getInfo(4342, 1), ""}, {SkillTable.getInstance().getInfo(4343, 1), ""}, {SkillTable.getInstance().getInfo(4344, 1), ""}, {SkillTable.getInstance().getInfo(4346, 1), ""}, {SkillTable.getInstance().getInfo(4345, 1), "W"}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}, {SkillTable.getInstance().getInfo(4347, 2), ""}, {SkillTable.getInstance().getInfo(4349, 1), ""}, {SkillTable.getInstance().getInfo(4350, 1), "W"}, {SkillTable.getInstance().getInfo(4348, 2), ""}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}, {SkillTable.getInstance().getInfo(4347, 2), ""}, {SkillTable.getInstance().getInfo(4349, 1), ""}, {SkillTable.getInstance().getInfo(4350, 1), "W"}, {SkillTable.getInstance().getInfo(4348, 2), ""}, {SkillTable.getInstance().getInfo(4351, 2), "M"}, {SkillTable.getInstance().getInfo(4352, 1), ""}, {SkillTable.getInstance().getInfo(4353, 2), "W"}, {SkillTable.getInstance().getInfo(4358, 1), "W"}, {SkillTable.getInstance().getInfo(4354, 1), "W"}}, new Object[0][], {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}, {SkillTable.getInstance().getInfo(4347, 6), ""}, {SkillTable.getInstance().getInfo(4349, 2), ""}, {SkillTable.getInstance().getInfo(4350, 4), "W"}, {SkillTable.getInstance().getInfo(4348, 6), ""}, {SkillTable.getInstance().getInfo(4351, 6), "M"}, {SkillTable.getInstance().getInfo(4352, 2), ""}, {SkillTable.getInstance().getInfo(4353, 6), "W"}, {SkillTable.getInstance().getInfo(4358, 3), "W"}, {SkillTable.getInstance().getInfo(4354, 4), "W"}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}, {SkillTable.getInstance().getInfo(4347, 6), ""}, {SkillTable.getInstance().getInfo(4349, 2), ""}, {SkillTable.getInstance().getInfo(4350, 4), "W"}, {SkillTable.getInstance().getInfo(4348, 6), ""}, {SkillTable.getInstance().getInfo(4351, 6), "M"}, {SkillTable.getInstance().getInfo(4352, 2), ""}, {SkillTable.getInstance().getInfo(4353, 6), "W"}, {SkillTable.getInstance().getInfo(4358, 3), "W"}, {SkillTable.getInstance().getInfo(4354, 4), "W"}, {SkillTable.getInstance().getInfo(4355, 1), "M"}, {SkillTable.getInstance().getInfo(4356, 1), "M"}, {SkillTable.getInstance().getInfo(4357, 1), "W"}, {SkillTable.getInstance().getInfo(4359, 1), "W"}, {SkillTable.getInstance().getInfo(4360, 1), "W"}}};

    public ResidenceFunction(int n, int n2) {
        this._id = n;
        this._type = n2;
        this.Ts = Calendar.getInstance();
    }

    public int getResidenceId() {
        return this._id;
    }

    public int getType() {
        return this._type;
    }

    public int getLevel() {
        return this._level;
    }

    public void setLvl(int n) {
        this._level = n;
    }

    public long getEndTimeInMillis() {
        return this.Ts.getTimeInMillis();
    }

    public void setEndTimeInMillis(long l) {
        this.Ts.setTimeInMillis(l);
    }

    public void setInDebt(boolean bl) {
        this.Tt = bl;
    }

    public boolean isInDebt() {
        return this.Tt;
    }

    public void setActive(boolean bl) {
        this._active = bl;
    }

    public boolean isActive() {
        return this._active;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateRentTime(boolean bl) {
        this.setEndTimeInMillis(System.currentTimeMillis() + 86400000L);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `residence_functions` SET `endTime`=?, `inDebt`=? WHERE `type`=? AND `id`=?");
            preparedStatement.setInt(1, (int)(this.getEndTimeInMillis() / 1000L));
            preparedStatement.setInt(2, bl ? 1 : 0);
            preparedStatement.setInt(3, this.getType());
            preparedStatement.setInt(4, this.getResidenceId());
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            try {
                _log.error(A, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public TeleportLocation[] getTeleports() {
        return this.getTeleports(this._level);
    }

    public TeleportLocation[] getTeleports(int n) {
        return this.Tv.get(n);
    }

    public void addTeleports(int n, TeleportLocation[] teleportLocationArray) {
        this.Tv.put(n, teleportLocationArray);
    }

    public int getLease() {
        if (this._level == 0) {
            return 0;
        }
        return this.getLease(this._level);
    }

    public int getLease(int n) {
        return this.Tu.get(n);
    }

    public void addLease(int n, int n2) {
        this.Tu.put(n, n2);
    }

    public int[] getBuylist() {
        return this.getBuylist(this._level);
    }

    public int[] getBuylist(int n) {
        return this.Tw.get(n);
    }

    public void addBuylist(int n, int[] nArray) {
        this.Tw.put(n, nArray);
    }

    public Object[][] getBuffs() {
        return this.getBuffs(this._level);
    }

    public Object[][] getBuffs(int n) {
        return this.Tx.get(n);
    }

    public void addBuffs(int n) {
        this.Tx.put(n, Ty[n]);
    }

    public Set<Integer> getLevels() {
        return this.Tu.keySet();
    }
}

