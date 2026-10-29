/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.SevenSignsFestival;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.PledgeShowInfoUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SevenSignsFestival {
    private static final Logger _log = LoggerFactory.getLogger(SevenSignsFestival.class);
    private static SevenSignsFestival Qc;
    private static final SevenSigns Qd;
    public static final int FESTIVAL_MANAGER_START = 120000;
    public static final int FESTIVAL_LENGTH = 1080000;
    public static final int FESTIVAL_CYCLE_LENGTH = 2280000;
    public static final int FESTIVAL_SIGNUP_TIME = 1200000;
    public static final int FESTIVAL_FIRST_SPAWN = 120000;
    public static final int FESTIVAL_FIRST_SWARM = 300000;
    public static final int FESTIVAL_SECOND_SPAWN = 540000;
    public static final int FESTIVAL_SECOND_SWARM = 720000;
    public static final int FESTIVAL_CHEST_SPAWN = 900000;
    public static final int FESTIVAL_COUNT = 5;
    public static final int FESTIVAL_LEVEL_MAX_31 = 0;
    public static final int FESTIVAL_LEVEL_MAX_42 = 1;
    public static final int FESTIVAL_LEVEL_MAX_53 = 2;
    public static final int FESTIVAL_LEVEL_MAX_64 = 3;
    public static final int FESTIVAL_LEVEL_MAX_NONE = 4;
    public static final int[] FESTIVAL_LEVEL_SCORES;
    public static final int FESTIVAL_BLOOD_OFFERING = 5901;
    public static final int FESTIVAL_OFFERING_VALUE = 1;
    private static boolean Qe;
    private static long[] Qf;
    private static Map<Integer, Long> Qg;
    private static Map<Integer, Long> Qh;
    private Map<Integer, Map<Integer, StatsSet>> Qi;

    public SevenSignsFestival() {
        Qf = new long[5];
        Qg = new ConcurrentHashMap<Integer, Long>();
        Qh = new ConcurrentHashMap<Integer, Long>();
        this.Qi = new ConcurrentHashMap<Integer, Map<Integer, StatsSet>>();
        this.cI();
    }

    public static SevenSignsFestival getInstance() {
        if (Qc == null) {
            Qc = new SevenSignsFestival();
        }
        return Qc;
    }

    public static String getFestivalName(int n) {
        switch (n) {
            case 0: {
                return "31";
            }
            case 1: {
                return "42";
            }
            case 2: {
                return "53";
            }
            case 3: {
                return "64";
            }
        }
        return "No Level Limit";
    }

    public static int getMaxLevelForFestival(int n) {
        switch (n) {
            case 0: {
                return 31;
            }
            case 1: {
                return 42;
            }
            case 2: {
                return 53;
            }
            case 3: {
                return 64;
            }
        }
        return Experience.getMaxLevel();
    }

    public static int getStoneCount(int n, int n2) {
        switch (n) {
            case 0: {
                if (n2 == 6360) {
                    return 900;
                }
                if (n2 == 6361) {
                    return 520;
                }
                return 270;
            }
            case 1: {
                if (n2 == 6360) {
                    return 1500;
                }
                if (n2 == 6361) {
                    return 900;
                }
                return 450;
            }
            case 2: {
                if (n2 == 6360) {
                    return 3000;
                }
                if (n2 == 6361) {
                    return 1500;
                }
                return 900;
            }
            case 3: {
                if (n2 == 6360) {
                    return 1500;
                }
                if (n2 == 6361) {
                    return 2700;
                }
                return 1350;
            }
            case 4: {
                if (n2 == 6360) {
                    return 6000;
                }
                if (n2 == 6361) {
                    return 3600;
                }
                return 1800;
            }
        }
        return 0;
    }

    public static String implodeString(List<?> list) {
        StringBuilder stringBuilder = new StringBuilder();
        int n = 0;
        while (n < list.size()) {
            Object obj = list.get(n);
            if (obj instanceof Player) {
                stringBuilder.append(((Player)obj).getName());
            } else {
                stringBuilder.append(obj);
            }
            if (++n >= list.size()) continue;
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cI() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            int n;
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `festivalId`, `cabal`, `cycle`, `date`, `score`, `members`, `names` FROM `seven_signs_festival`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Map<Integer, StatsSet> map;
                int n2 = Qd.getCurrentCycle();
                n = resultSet.getInt("festivalId");
                int n3 = SevenSigns.getCabalNumber(resultSet.getString("cabal"));
                StatsSet statsSet = new StatsSet();
                statsSet.set("festivalId", n);
                statsSet.set("cabal", n3);
                statsSet.set("cycle", n2);
                statsSet.set("date", resultSet.getString("date"));
                statsSet.set("score", resultSet.getInt("score"));
                statsSet.set("members", resultSet.getString("members"));
                statsSet.set("names", resultSet.getString("names"));
                if (n3 == 2) {
                    n += 5;
                }
                if ((map = this.Qi.get(n2)) == null) {
                    map = new TreeMap<Integer, StatsSet>();
                }
                map.put(n, statsSet);
                this.Qi.put(n2, map);
            }
            DbUtils.close(preparedStatement, resultSet);
            StringBuilder stringBuilder = new StringBuilder("SELECT festival_cycle, ");
            for (n = 0; n < 4; ++n) {
                stringBuilder.append("accumulated_bonus" + String.valueOf(n) + ", ");
            }
            stringBuilder.append("accumulated_bonus" + String.valueOf(4) + " ");
            stringBuilder.append("FROM seven_signs_status");
            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                for (n = 0; n < 5; ++n) {
                    SevenSignsFestival.Qf[n] = resultSet.getInt("accumulated_bonus" + String.valueOf(n));
                }
            }
        }
        catch (SQLException sQLException) {
            try {
                _log.error("SevenSignsFestival: Failed to load configuration: " + sQLException);
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
    public synchronized void saveFestivalData(boolean bl) {
        PreparedStatement preparedStatement;
        Connection connection;
        block7: {
            connection = null;
            preparedStatement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                boolean bl2 = false;
                preparedStatement = connection.prepareStatement("INSERT INTO `seven_signs_festival` (\n\t`festivalId`, \n\t`cabal`, \n\t`cycle`, \n\t`date`, \n\t`score`, \n\t`members`, \n\t`names`) \nVALUES (?,?,?,?,?,?,?)\nON DUPLICATE KEY UPDATE \n\t`date` = ?, \n\t`score` = ?, \n\t`members` = ?, \n\t`names` = ?");
                for (Map<Integer, StatsSet> map : this.Qi.values()) {
                    for (StatsSet statsSet : map.values()) {
                        int n = statsSet.getInteger("cycle");
                        int n2 = statsSet.getInteger("festivalId");
                        String string = SevenSigns.getCabalShortName(statsSet.getInteger("cabal"));
                        preparedStatement.setInt(1, n2);
                        preparedStatement.setString(2, string);
                        preparedStatement.setInt(3, n);
                        preparedStatement.setLong(4, Long.valueOf(statsSet.getString("date")));
                        preparedStatement.setInt(5, statsSet.getInteger("score"));
                        preparedStatement.setString(6, statsSet.getString("members"));
                        preparedStatement.setString(7, statsSet.getString("names", ""));
                        preparedStatement.setLong(8, Long.valueOf(statsSet.getString("date")));
                        preparedStatement.setInt(9, statsSet.getInteger("score"));
                        preparedStatement.setString(10, statsSet.getString("members"));
                        preparedStatement.setString(11, statsSet.getString("names", ""));
                        preparedStatement.addBatch();
                        bl2 = true;
                    }
                }
                if (!bl2) break block7;
                preparedStatement.executeBatch();
            }
            catch (Exception exception) {
                try {
                    _log.error("SevenSignsFestival: Failed to save configuration!", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement);
        if (bl) {
            Qd.saveSevenSignsData(0, true);
        }
    }

    public void rewardHighestRanked() {
        for (int i = 0; i < 5; ++i) {
            String[] stringArray;
            StatsSet statsSet = this.getOverallHighestScoreData(i);
            if (statsSet == null) continue;
            for (String string : stringArray = statsSet.getString("members").split(",")) {
                this.v(string);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void v(String string) {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection;
        block5: {
            Player player = GameObjectsStorage.getPlayer(Integer.parseInt(string));
            if (player != null) {
                if (player.getClan() == null) return;
                player.getClan().incReputation(100, true, "SevenSignsFestival");
                SystemMessage systemMessage = new SystemMessage(1775);
                systemMessage.addName(player);
                systemMessage.addNumber(100);
                player.getClan().broadcastToOnlineMembers(systemMessage);
                return;
            }
            connection = null;
            preparedStatement = null;
            resultSet = null;
            try {
                Clan clan;
                int n;
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("SELECT `char_name`, `clanid` FROM `characters` WHERE `obj_Id` = ?");
                preparedStatement.setString(1, string);
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next() || (n = resultSet.getInt("clanid")) <= 0 || (clan = ClanTable.getInstance().getClan(n)) == null) break block5;
                clan.incReputation(100, true, "SevenSignsFestival");
                clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                SystemMessage systemMessage = new SystemMessage(1775);
                systemMessage.addString(resultSet.getString("char_name"));
                systemMessage.addNumber(100);
                clan.broadcastToOnlineMembers(systemMessage);
            }
            catch (Exception exception) {
                try {
                    _log.warn("could not get clan name of " + string + ": " + exception);
                    _log.error("", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                return;
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return;
    }

    public void resetFestivalData(boolean bl) {
        for (int i = 0; i < 5; ++i) {
            SevenSignsFestival.Qf[i] = 0L;
        }
        Qg.clear();
        Qh.clear();
        TreeMap<Integer, StatsSet> treeMap = new TreeMap<Integer, StatsSet>();
        for (int i = 0; i < 10; ++i) {
            int n = i;
            if (i >= 5) {
                n -= 5;
            }
            StatsSet statsSet = new StatsSet();
            statsSet.set("festivalId", n);
            statsSet.set("cycle", Qd.getCurrentCycle());
            statsSet.set("date", "0");
            statsSet.set("score", 0);
            statsSet.set("members", "");
            if (i >= 5) {
                statsSet.set("cabal", 2);
            } else {
                statsSet.set("cabal", 1);
            }
            treeMap.put(i, statsSet);
        }
        this.Qi.put(Qd.getCurrentCycle(), treeMap);
        this.saveFestivalData(bl);
        _log.info("SevenSignsFestival: Reinitialized engine for next competition period.");
    }

    public boolean isFestivalInitialized() {
        return Qe;
    }

    public static void setFestivalInitialized(boolean bl) {
        Qe = bl;
    }

    public String getTimeToNextFestivalStr() {
        if (Qd.isSealValidationPeriod()) {
            return "<font color=\"FF0000\">This is the Seal Validation period. Festivals will resume next week.</font>";
        }
        return "<font color=\"FF0000\">The next festival is ready to start.</font>";
    }

    public long getHighestScore(int n, int n2) {
        return this.getHighestScoreData(n, n2).getLong("score");
    }

    public StatsSet getHighestScoreData(int n, int n2) {
        int n3 = n2;
        if (n == 2) {
            n3 += 5;
        }
        StatsSet statsSet = null;
        try {
            statsSet = this.Qi.get(Qd.getCurrentCycle()).get(n3);
        }
        catch (Exception exception) {
            _log.info("SSF: Error while getting scores");
            _log.info("oracle=" + n + " festivalId=" + n2 + " offsetId" + n3 + " _signsCycle" + Qd.getCurrentCycle());
            _log.info("_festivalData=" + this.Qi.toString());
            _log.error("", exception);
        }
        if (statsSet == null) {
            statsSet = new StatsSet();
            statsSet.set("score", 0);
            statsSet.set("members", "");
            _log.warn("SevenSignsFestival: Data missing for " + SevenSigns.getCabalName(n) + ", FestivalID = " + n2 + " (Current Cycle " + Qd.getCurrentCycle() + ")");
        }
        return statsSet;
    }

    public StatsSet getOverallHighestScoreData(int n) {
        StatsSet statsSet = null;
        int n2 = 0;
        for (Map<Integer, StatsSet> map : this.Qi.values()) {
            for (StatsSet statsSet2 : map.values()) {
                int n3 = statsSet2.getInteger("festivalId");
                int n4 = statsSet2.getInteger("score");
                if (n3 != n || n4 <= n2) continue;
                n2 = n4;
                statsSet = statsSet2;
            }
        }
        return statsSet;
    }

    public boolean setFinalScore(Party party, int n, int n2, long l) {
        List<Integer> list = party.getPartyMembersObjIds();
        List<Player> list2 = party.getPartyMembers();
        long l2 = this.getHighestScore(2, n2);
        long l3 = this.getHighestScore(1, n2);
        long l4 = 0L;
        long l5 = 0L;
        if (n == 2) {
            l4 = l2;
            l5 = l3;
            Qg.put(n2, l);
        } else {
            l4 = l3;
            l5 = l2;
            Qh.put(n2, l);
        }
        StatsSet statsSet = this.getHighestScoreData(n, n2);
        if (l > l4) {
            statsSet.set("date", String.valueOf(System.currentTimeMillis()));
            statsSet.set("score", l);
            statsSet.set("members", SevenSignsFestival.implodeString(list));
            statsSet.set("names", SevenSignsFestival.implodeString(list2));
            if (l > l5) {
                Qd.updateFestivalScore();
            }
            this.saveFestivalData(true);
            return true;
        }
        return false;
    }

    public long getAccumulatedBonus(int n) {
        return Qf[n];
    }

    public void addAccumulatedBonus(int n, int n2, long l) {
        int n3 = 0;
        switch (n2) {
            case 6360: {
                n3 = 3;
                break;
            }
            case 6361: {
                n3 = 5;
                break;
            }
            case 6362: {
                n3 = 10;
            }
        }
        int n4 = n;
        Qf[n4] = Qf[n4] + l * (long)n3;
    }

    public void distribAccumulatedBonus() {
        long l;
        int n;
        long[][] lArrayArray = new long[5][];
        long l2 = 0L;
        long l3 = 0L;
        for (n = 0; n < 5; ++n) {
            long l4 = this.getHighestScore(2, n);
            if (l4 > (l = this.getHighestScore(1, n))) {
                lArrayArray[n] = new long[]{2L, l4};
                continue;
            }
            if (l > l4) {
                lArrayArray[n] = new long[]{1L, l};
                continue;
            }
            lArrayArray[n] = new long[]{0L, l4};
            ++l2;
            l3 += Qf[n];
        }
        for (n = 0; n < 5; ++n) {
            if (lArrayArray[n][0] == 0L) continue;
            StatsSet statsSet = this.getHighestScoreData((int)lArrayArray[n][0], n);
            String string = statsSet.getString("members");
            l = l2 > 0L ? l3 / l2 : 0L;
            String[] stringArray = string.split(",");
            long l5 = (Qf[n] + l) / (long)stringArray.length;
            for (String string2 : stringArray) {
                SevenSigns.getInstance().addPlayerStoneContrib(Integer.parseInt(string2), 0L, 0L, l5 / 10L);
            }
        }
    }

    static {
        Qd = SevenSigns.getInstance();
        FESTIVAL_LEVEL_SCORES = new int[]{60, 70, 100, 120, 150};
    }
}

