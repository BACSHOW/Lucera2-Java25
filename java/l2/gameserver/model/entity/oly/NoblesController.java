/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoblesController {
    private static final Logger _log = LoggerFactory.getLogger(NoblesController.class);
    private static NoblesController RO;
    private static final String RP = "SELECT `char_id`,`class_id`,`char_name`,`points_current`,`points_past`,`points_pre_past`,`class_free_cnt`,`class_based_cnt`,`team_cnt`,`comp_win`,`comp_loose`,`comp_done`, `comp_done_past` FROM `oly_nobles`";
    private static final String RQ = "REPLACE INTO `oly_nobles`(`char_id`,`class_id`,`char_name`,`points_current`,`points_past`,`points_pre_past`,`class_free_cnt`,`class_based_cnt`,`team_cnt`,`comp_win`,`comp_loose`,`comp_done`, `comp_done_past`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String RR = "DELETE FROM `oly_nobles` WHERE `char_id` = ?";
    private List<NobleRecord> RS = new CopyOnWriteArrayList<NobleRecord>();
    private static final NobleRecordCmp RT;
    private static final NobleRecordCmpCurr RU;

    public static final NoblesController getInstance() {
        if (RO == null) {
            RO = new NoblesController();
        }
        return RO;
    }

    private NoblesController() {
        this.LoadNobleses();
        this.ComputeRanks();
    }

    public boolean isNobles(int n) {
        return null != this.getNobleRecord(n);
    }

    public boolean isNobles(Player player) {
        return this.getNobleRecord(player.getObjectId()) != null;
    }

    public NobleRecord getNobleRecord(int n) {
        for (NobleRecord nobleRecord : this.RS) {
            if (nobleRecord == null || nobleRecord.char_id != n) continue;
            return nobleRecord;
        }
        return null;
    }

    public void renameNoble(int n, String string) {
        for (NobleRecord nobleRecord : this.RS) {
            if (nobleRecord == null || nobleRecord.char_id != n) continue;
            nobleRecord.char_name = string;
            this.SaveNobleRecord(nobleRecord);
            return;
        }
    }

    protected Collection<NobleRecord> getNoblesRecords() {
        return this.RS;
    }

    public int getPointsOf(int n) {
        for (NobleRecord nobleRecord : this.RS) {
            if (nobleRecord == null || nobleRecord.char_id != n) continue;
            return nobleRecord.points_current;
        }
        return -1;
    }

    public void setPointsOf(int n, int n2) {
        for (NobleRecord nobleRecord : this.RS) {
            if (nobleRecord == null || nobleRecord.char_id != n) continue;
            nobleRecord.points_current = n2;
            this.SaveNobleRecord(nobleRecord);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void removeNoble(Player player) {
        NobleRecord nobleRecord = null;
        for (NobleRecord object2 : this.RS) {
            if (object2.char_id != player.getObjectId()) continue;
            nobleRecord = object2;
        }
        if (nobleRecord == null) {
            return;
        }
        Connection object3 = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            object3 = DatabaseFactory.getInstance().getConnection();
            PreparedStatement preparedStatement = object3.prepareStatement(RR);
            preparedStatement.setInt(1, nobleRecord.char_id);
            preparedStatement.executeUpdate();
            this.RS.remove(nobleRecord);
            DbUtils.closeQuietly((Connection)object3, preparedStatement, resultSet);
        }
        catch (SQLException sQLException) {
            _log.warn("NoblesController: Can't remove nobleses ", sQLException);
        }
        finally {
            DbUtils.closeQuietly((Connection)object3, statement, resultSet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void addNoble(Player player) {
        List<NobleRecord> list = this.RS;
        synchronized (list) {
            NobleRecord object = null;
            for (NobleRecord classIdArray : this.RS) {
                if (classIdArray.char_id != player.getObjectId()) continue;
                object = classIdArray;
            }
            this.RS.remove(object);
            if (object == null) {
                int n = player.getBaseClassId();
                if (n < 88) {
                    for (ClassId classId : ClassId.values()) {
                        if (classId.level() != 3 || classId.getParent(0).getId() != n) continue;
                        n = classId.getId();
                        break;
                    }
                }
                object = new NobleRecord(player.getObjectId(), n, player.getName(), Config.OLY_SEASON_START_POINTS, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            }
            if (!player.getName().equals(object.char_name)) {
                object.char_name = player.getName();
            }
            if (player.getBaseClassId() != object.class_id) {
                _log.warn("OlympiadController: " + player.getName() + " got base class " + player.getBaseClassId() + " but " + object.class_id + " class nobless");
                object.class_id = object.class_id;
            }
            this.RS.add(object);
            this.a(object);
        }
    }

    private void a(NobleRecord nobleRecord) {
        this.SaveNobleRecord(nobleRecord);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void LoadNobleses() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(RP);
            while (resultSet.next()) {
                NobleRecord nobleRecord = new NobleRecord(resultSet.getInt("char_id"), resultSet.getInt("class_id"), resultSet.getString("char_name"), resultSet.getInt("points_current"), resultSet.getInt("points_past"), resultSet.getInt("points_pre_past"), resultSet.getInt("class_free_cnt"), resultSet.getInt("class_based_cnt"), resultSet.getInt("team_cnt"), resultSet.getInt("comp_win"), resultSet.getInt("comp_loose"), resultSet.getInt("comp_done"), resultSet.getInt("comp_done_past"));
                this.RS.add(nobleRecord);
            }
        }
        catch (SQLException sQLException) {
            try {
                _log.warn("NoblesController: Can't load nobleses ", sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, statement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        DbUtils.closeQuietly(connection, statement, resultSet);
        _log.info("NoblesController: loaded " + this.RS.size() + " nobleses.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void SaveNobleses() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(RQ);
            for (NobleRecord nobleRecord : this.RS) {
                preparedStatement.setInt(1, nobleRecord.char_id);
                preparedStatement.setInt(2, nobleRecord.class_id);
                preparedStatement.setString(3, nobleRecord.char_name);
                preparedStatement.setInt(4, nobleRecord.points_current);
                preparedStatement.setInt(5, nobleRecord.points_past);
                preparedStatement.setInt(6, nobleRecord.points_pre_past);
                preparedStatement.setInt(7, nobleRecord.class_free_cnt);
                preparedStatement.setInt(8, nobleRecord.class_based_cnt);
                preparedStatement.setInt(9, nobleRecord.team_cnt);
                preparedStatement.setInt(10, nobleRecord.comp_win);
                preparedStatement.setInt(11, nobleRecord.comp_loose);
                preparedStatement.setInt(12, nobleRecord.comp_done);
                preparedStatement.setInt(13, nobleRecord.comp_done_past);
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("Oly: can't save nobleses : ", exception);
                exception.printStackTrace();
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
    public void SaveNobleRecord(NobleRecord nobleRecord) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(RQ);
            preparedStatement.setInt(1, nobleRecord.char_id);
            preparedStatement.setInt(2, nobleRecord.class_id);
            preparedStatement.setString(3, nobleRecord.char_name);
            preparedStatement.setInt(4, nobleRecord.points_current);
            preparedStatement.setInt(5, nobleRecord.points_past);
            preparedStatement.setInt(6, nobleRecord.points_pre_past);
            preparedStatement.setInt(7, nobleRecord.class_free_cnt);
            preparedStatement.setInt(8, nobleRecord.class_based_cnt);
            preparedStatement.setInt(9, nobleRecord.team_cnt);
            preparedStatement.setInt(10, nobleRecord.comp_win);
            preparedStatement.setInt(11, nobleRecord.comp_loose);
            preparedStatement.setInt(12, nobleRecord.comp_done);
            preparedStatement.setInt(13, nobleRecord.comp_done_past);
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            try {
                _log.warn("Oly: can't save noble " + nobleRecord.char_name, exception);
                exception.printStackTrace();
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public void TransactNewSeason() {
        _log.info("NoblesController: Cleanuping last period.");
        for (NobleRecord nobleRecord : this.RS) {
            Log.add(String.format("NoblesController: %s(%d) new season clean. points_current=%d|points_past=%d|points_pre_past=%d|comp_done=%d", nobleRecord.char_name, nobleRecord.char_id, nobleRecord.points_current, nobleRecord.points_past, nobleRecord.points_pre_past, nobleRecord.comp_done), "olympiad");
            if (nobleRecord.comp_done >= Config.OLY_MIN_NOBLE_COMPS) {
                nobleRecord.points_past = nobleRecord.points_current;
                nobleRecord.points_pre_past = nobleRecord.points_current;
                nobleRecord.comp_done_past = nobleRecord.comp_done;
            } else {
                nobleRecord.points_past = 0;
                nobleRecord.points_pre_past = 0;
                nobleRecord.comp_done_past = 0;
            }
            nobleRecord.comp_done = 0;
            nobleRecord.comp_win = 0;
            nobleRecord.comp_loose = 0;
            nobleRecord.class_based_cnt = 0;
            nobleRecord.class_free_cnt = 0;
            nobleRecord.team_cnt = 0;
            nobleRecord.points_current = Config.OLY_DEFAULT_POINTS;
        }
        this.SaveNobleses();
    }

    public void AddWeaklyBonus() {
        for (NobleRecord nobleRecord : this.RS) {
            nobleRecord.points_current += Config.OLY_WBONUS_POINTS;
            nobleRecord.class_based_cnt = 0;
            nobleRecord.class_free_cnt = 0;
            nobleRecord.team_cnt = 0;
        }
        this.SaveNobleses();
    }

    public String[] getClassLeaders(int n) {
        ArrayList<NobleRecord> arrayList = new ArrayList<NobleRecord>();
        ArrayList<String> arrayList2 = new ArrayList<String>();
        for (NobleRecord nobleRecord : this.RS) {
            if (nobleRecord.class_id != n || nobleRecord.points_pre_past <= 0 || nobleRecord.comp_done_past < Config.OLY_MIN_HERO_COMPS) continue;
            arrayList.add(nobleRecord);
        }
        NobleRecord[] nobleRecordArray = arrayList.toArray(new NobleRecord[arrayList.size()]);
        Arrays.sort(nobleRecordArray, RT);
        for (int i = 0; i < nobleRecordArray.length && i < 15; ++i) {
            if (nobleRecordArray[i] == null) continue;
            arrayList2.add(nobleRecordArray[i].char_name);
        }
        return arrayList2.toArray(new String[arrayList2.size()]);
    }

    public String[] getClassLeadersCurr(int n) {
        ArrayList<NobleRecord> arrayList = new ArrayList<NobleRecord>();
        ArrayList<String> arrayList2 = new ArrayList<String>();
        for (NobleRecord nobleRecord : this.RS) {
            if (nobleRecord.class_id != n || nobleRecord.points_current <= 0 || nobleRecord.comp_done < Config.OLY_MIN_HERO_COMPS) continue;
            arrayList.add(nobleRecord);
        }
        NobleRecord[] nobleRecordArray = arrayList.toArray(new NobleRecord[arrayList.size()]);
        Arrays.sort(nobleRecordArray, RU);
        for (int i = 0; i < nobleRecordArray.length && i < 15; ++i) {
            if (nobleRecordArray[i] == null) continue;
            arrayList2.add(nobleRecordArray[i].char_name);
        }
        return arrayList2.toArray(new String[arrayList2.size()]);
    }

    public int getPlayerClassRank(int n, int n2) {
        ArrayList<NobleRecord> arrayList = new ArrayList<NobleRecord>();
        for (NobleRecord nobleRecord : this.RS) {
            if (nobleRecord.class_id != n) continue;
            arrayList.add(nobleRecord);
        }
        Collections.sort(arrayList, RT);
        for (int i = 0; i < arrayList.size(); ++i) {
            if (arrayList.get(i) == null || ((NobleRecord)arrayList.get((int)i)).char_id != n2) continue;
            return i;
        }
        return -1;
    }

    public synchronized void ComputeRanks() {
        int n;
        _log.info("NoblesController: Computing ranks.");
        NobleRecord[] nobleRecordArray = this.RS.toArray(new NobleRecord[this.RS.size()]);
        Arrays.sort(nobleRecordArray, RT);
        int n2 = (int)Math.round((double)nobleRecordArray.length * 0.01);
        int n3 = (int)Math.round((double)nobleRecordArray.length * 0.1);
        int n4 = (int)Math.round((double)nobleRecordArray.length * 0.25);
        int n5 = (int)Math.round((double)nobleRecordArray.length * 0.5);
        if (n2 == 0) {
            n2 = 1;
            ++n3;
            ++n4;
            ++n5;
        }
        for (n = 0; n <= n2 && n < nobleRecordArray.length; ++n) {
            nobleRecordArray[n].rank = 0;
        }
        while (n <= n3 && n < nobleRecordArray.length) {
            nobleRecordArray[n].rank = 1;
            ++n;
        }
        while (n <= n4 && n < nobleRecordArray.length) {
            nobleRecordArray[n].rank = 2;
            ++n;
        }
        while (n <= n5 && n < nobleRecordArray.length) {
            nobleRecordArray[n].rank = 3;
            ++n;
        }
        while (n < nobleRecordArray.length) {
            nobleRecordArray[n].rank = 4;
            ++n;
        }
    }

    public synchronized int getNoblessePasses(Player player) {
        int n = player.getObjectId();
        NobleRecord nobleRecord = NoblesController.getInstance().getNobleRecord(n);
        if (nobleRecord != null) {
            if (nobleRecord.points_past == 0) {
                return 0;
            }
            int n2 = 0;
            if (nobleRecord.rank >= 0 && nobleRecord.rank < Config.OLY_POINTS_SETTLEMENT.length) {
                n2 = Config.OLY_POINTS_SETTLEMENT[nobleRecord.rank];
            }
            if (HeroController.getInstance().isCurrentHero(n) || HeroController.getInstance().isInactiveHero(n)) {
                n2 += Config.OLY_HERO_POINT_BONUS;
            }
            nobleRecord.points_past = 0;
            NoblesController.getInstance().SaveNobleRecord(nobleRecord);
            return n2 * Config.OLY_ITEMS_SETTLEMENT_PER_POINT;
        }
        return 0;
    }

    static {
        RT = new NobleRecordCmp();
        RU = new NobleRecordCmpCurr();
    }

    private static class NobleRecordCmpCurr
    implements Comparator<NobleRecord> {
        private NobleRecordCmpCurr() {
        }

        @Override
        public int compare(NobleRecord nobleRecord, NobleRecord nobleRecord2) {
            if (nobleRecord == null && nobleRecord2 == null) {
                return 0;
            }
            if (nobleRecord == null && nobleRecord2 != null) {
                return 1;
            }
            if (nobleRecord2 == null && nobleRecord != null) {
                return -1;
            }
            int n = nobleRecord2.points_current - nobleRecord.points_current;
            if (n != 0) {
                return n;
            }
            return nobleRecord2.comp_done - nobleRecord.comp_done;
        }
    }

    private static class NobleRecordCmp
    implements Comparator<NobleRecord> {
        private NobleRecordCmp() {
        }

        @Override
        public int compare(NobleRecord nobleRecord, NobleRecord nobleRecord2) {
            if (nobleRecord == null && nobleRecord2 == null) {
                return 0;
            }
            if (nobleRecord == null && nobleRecord2 != null) {
                return 1;
            }
            if (nobleRecord2 == null && nobleRecord != null) {
                return -1;
            }
            int n = nobleRecord2.points_pre_past - nobleRecord.points_pre_past;
            if (n != 0) {
                return n;
            }
            return nobleRecord2.comp_done_past - nobleRecord.comp_done_past;
        }
    }

    public class NobleRecord {
        public int char_id;
        public int class_id;
        public String char_name;
        public int points_current;
        public int points_past;
        public int points_pre_past;
        public int class_free_cnt;
        public int class_based_cnt;
        public int team_cnt;
        public int comp_win;
        public int comp_loose;
        public int comp_done;
        public int comp_done_past;
        public int rank;

        private NobleRecord(int n, int n2, String string, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10, int n11, int n12) {
            this.char_id = n;
            this.class_id = n2;
            this.char_name = string;
            this.points_current = n3;
            this.points_past = n4;
            this.points_pre_past = n5;
            this.class_free_cnt = n6;
            this.class_based_cnt = n7;
            this.team_cnt = n8;
            this.comp_win = n9;
            this.comp_loose = n10;
            this.comp_done = n11;
            this.comp_done_past = n12;
            this.rank = 0;
        }
    }
}

