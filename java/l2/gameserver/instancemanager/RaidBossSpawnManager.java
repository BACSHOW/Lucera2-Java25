/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.instances.ReflectionBossInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.GmListTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.SqlBatch;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaidBossSpawnManager {
    private static final Logger _log = LoggerFactory.getLogger(RaidBossSpawnManager.class);
    private static RaidBossSpawnManager AV;
    protected static Map<Integer, Spawner> _spawntable;
    protected static Map<Integer, StatsSet> _storedInfo;
    protected static Map<Integer, Map<Integer, Integer>> _points;
    public static final Integer KEY_RANK;
    public static final Integer KEY_TOTAL_POINTS;
    private Lock AW = new ReentrantLock();

    private RaidBossSpawnManager() {
        if (!Config.DONTLOADSPAWN) {
            this.reloadBosses();
        }
    }

    public void reloadBosses() {
        this.by();
        this.bz();
        this.calculateRanking();
    }

    public void cleanUp() {
        this.updateAllStatusDb();
        this.updatePointsDb();
        _storedInfo.clear();
        _spawntable.clear();
        _points.clear();
    }

    public static RaidBossSpawnManager getInstance() {
        if (AV == null) {
            AV = new RaidBossSpawnManager();
        }
        return AV;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void by() {
        _storedInfo = new ConcurrentHashMap<Integer, StatsSet>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            resultSet = connection.createStatement().executeQuery("SELECT * FROM `raidboss_status`");
            while (resultSet.next()) {
                int n = resultSet.getInt("id");
                StatsSet statsSet = new StatsSet();
                statsSet.set("current_hp", resultSet.getDouble("current_hp"));
                statsSet.set("current_mp", resultSet.getDouble("current_mp"));
                statsSet.set("respawn_delay", resultSet.getInt("respawn_delay"));
                _storedInfo.put(n, statsSet);
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("RaidBossSpawnManager: Couldnt load raidboss statuses");
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, statement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        DbUtils.closeQuietly(connection, statement, resultSet);
        _log.info("RaidBossSpawnManager: Loaded " + _storedInfo.size() + " Statuses");
    }

    public void updateAllStatusDb() {
        for (int n : _storedInfo.keySet()) {
            this.x(n);
        }
    }

    private static void a(final int n, long l) {
        long l2;
        if (Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY > 0 && l > 0L && ArrayUtils.contains(Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_IDS, n) && l - (long)Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY > (l2 = System.currentTimeMillis() / 1000L)) {
            ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                @Override
                public void runImpl() throws Exception {
                    NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
                    if (npcTemplate != null) {
                        Announcements.getInstance().announceByCustomMessage("l2.gameserver.instancemanager.RaidBossSpawnManager.AltAnnounceRaidbossSpawnSoon", new String[]{npcTemplate.getName()});
                    }
                }
            }, (l - (long)Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY - l2) * 1000L);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void x(int n) {
        NpcInstance npcInstance;
        Spawner spawner = _spawntable.get(n);
        if (spawner == null) {
            return;
        }
        StatsSet statsSet = _storedInfo.get(n);
        if (statsSet == null) {
            statsSet = new StatsSet();
            _storedInfo.put(n, statsSet);
        }
        if ((npcInstance = spawner.getFirstSpawned()) instanceof ReflectionBossInstance) {
            return;
        }
        int n2 = 0;
        if (npcInstance != null && !npcInstance.isDead()) {
            statsSet.set("current_hp", npcInstance.getCurrentHp());
            statsSet.set("current_mp", npcInstance.getCurrentMp());
            statsSet.set("respawn_delay", 0);
        } else {
            n2 = spawner.getRespawnTime();
            statsSet.set("current_hp", 0);
            statsSet.set("current_mp", 0);
            statsSet.set("respawn_delay", n2);
            RaidBossSpawnManager.a(n, n2);
        }
        Log.add("updateStatusDb id=" + n + " current_hp=" + statsSet.getDouble("current_hp") + " current_mp=" + statsSet.getDouble("current_mp") + " respawn_delay=" + statsSet.getInteger("respawn_delay", 0) + (npcInstance != null ? " respawnTime=" + npcInstance.getSpawn().getRespawnTime() : ""), "RaidBossSpawnManager");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("REPLACE INTO `raidboss_status` (`id`, `current_hp`, `current_mp`, `respawn_delay`) VALUES (?,?,?,?)");
            preparedStatement.setInt(1, n);
            preparedStatement.setInt(2, (int)statsSet.getDouble("current_hp"));
            preparedStatement.setInt(3, (int)statsSet.getDouble("current_mp"));
            preparedStatement.setInt(4, n2);
            preparedStatement.execute();
        }
        catch (SQLException sQLException) {
            try {
                _log.warn("RaidBossSpawnManager: Couldnt update raidboss_status table");
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public void addNewSpawn(int n, Spawner spawner) {
        if (_spawntable.containsKey(n)) {
            return;
        }
        _spawntable.put(n, spawner);
        StatsSet statsSet = _storedInfo.get(n);
        if (statsSet != null) {
            long l = statsSet.getLong("respawn_delay", 0L);
            spawner.setRespawnTime((int)l);
            Log.add("AddSpawn npc=" + n + " respawnDelay=" + spawner.getRespawnDelay() + " respawnDelayRandom=" + spawner.getRespawnDelayRandom() + " respawnCron=" + spawner.getRespawnCron() + " respawn_delay=" + l, "RaidBossSpawnManager");
            if (l > 0L) {
                RaidBossSpawnManager.a(n, l);
            }
        }
    }

    public void onBossSpawned(RaidBossInstance raidBossInstance) {
        int n = raidBossInstance.getNpcId();
        if (!_spawntable.containsKey(n)) {
            return;
        }
        StatsSet statsSet = _storedInfo.get(n);
        if (statsSet != null && statsSet.getDouble("current_hp") > 1.0) {
            raidBossInstance.setCurrentHp(statsSet.getDouble("current_hp"), false);
            raidBossInstance.setCurrentMp(statsSet.getDouble("current_mp"));
        }
        Log.add("onBossSpawned npc=" + n + " current_hp=" + raidBossInstance.getCurrentHp() + " current_mp=" + raidBossInstance.getCurrentMp(), "RaidBossSpawnManager");
        GmListTable.broadcastMessageToGMs("Spawning RaidBoss " + raidBossInstance.getName());
        if (ArrayUtils.contains(Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_IDS, raidBossInstance.getNpcId())) {
            Announcements.getInstance().announceByCustomMessage("l2.gameserver.instancemanager.RaidBossSpawnManager.AltAnnounceRaidbossSpawn", new String[]{raidBossInstance.getName()});
        }
    }

    public void onBossDespawned(RaidBossInstance raidBossInstance) {
        this.x(raidBossInstance.getNpcId());
    }

    public Status getRaidBossStatusId(int n) {
        Spawner spawner = _spawntable.get(n);
        if (spawner == null) {
            return Status.UNDEFINED;
        }
        NpcInstance npcInstance = spawner.getFirstSpawned();
        return npcInstance == null ? Status.DEAD : Status.ALIVE;
    }

    public boolean isDefined(int n) {
        return _spawntable.containsKey(n);
    }

    public Map<Integer, Spawner> getSpawnTable() {
        return _spawntable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void bz() {
        this.AW.lock();
        _points = new ConcurrentHashMap<Integer, Map<Integer, Integer>>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT owner_id, boss_id, points FROM `raidboss_points` ORDER BY owner_id ASC");
            int n = 0;
            HashMap<Integer, Integer> hashMap = null;
            while (resultSet.next()) {
                if (n != resultSet.getInt("owner_id")) {
                    n = resultSet.getInt("owner_id");
                    hashMap = new HashMap<Integer, Integer>();
                    _points.put(n, hashMap);
                }
                assert (hashMap != null);
                int n2 = resultSet.getInt("boss_id");
                NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n2);
                if (n2 == KEY_RANK || n2 == KEY_TOTAL_POINTS || npcTemplate == null || npcTemplate.rewardRp <= 0) continue;
                hashMap.put(n2, resultSet.getInt("points"));
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("RaidBossSpawnManager: Couldnt load raidboss points");
                _log.error("", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, statement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        DbUtils.closeQuietly(connection, statement, resultSet);
        this.AW.unlock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updatePointsDb() {
        Statement statement;
        Connection connection;
        block8: {
            this.AW.lock();
            if (!mysql.set("TRUNCATE `raidboss_points`")) {
                _log.warn("RaidBossSpawnManager: Couldnt empty raidboss_points table");
            }
            if (_points.isEmpty()) {
                this.AW.unlock();
                return;
            }
            connection = null;
            statement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                statement = connection.createStatement();
                SqlBatch sqlBatch = new SqlBatch("INSERT INTO `raidboss_points` (owner_id, boss_id, points) VALUES");
                for (Map.Entry<Integer, Map<Integer, Integer>> entry : _points.entrySet()) {
                    Map<Integer, Integer> map = entry.getValue();
                    if (map == null || map.isEmpty()) continue;
                    for (Map.Entry<Integer, Integer> entry2 : map.entrySet()) {
                        if (KEY_RANK.equals(entry2.getKey()) || KEY_TOTAL_POINTS.equals(entry2.getKey()) || entry2.getValue() == null || entry2.getValue() == 0) continue;
                        StringBuilder stringBuilder = new StringBuilder("(");
                        stringBuilder.append(entry.getKey()).append(",");
                        stringBuilder.append(entry2.getKey()).append(",");
                        stringBuilder.append(entry2.getValue()).append(")");
                        sqlBatch.write(stringBuilder.toString());
                    }
                }
                if (sqlBatch.isEmpty()) break block8;
                statement.executeUpdate(sqlBatch.close());
            }
            catch (SQLException sQLException) {
                try {
                    _log.warn("RaidBossSpawnManager: Couldnt update raidboss_points table");
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, statement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, statement);
            }
        }
        DbUtils.closeQuietly(connection, statement);
        this.AW.unlock();
    }

    public void deletePoints(int n) {
        if (n <= 0) {
            return;
        }
        this.AW.lock();
        try {
            _points.remove(n);
        }
        finally {
            this.AW.unlock();
        }
    }

    public void addPoints(int n, int n2, int n3) {
        if (n3 <= 0 || n <= 0 || n2 <= 0) {
            return;
        }
        this.AW.lock();
        Map<Integer, Integer> map = _points.get(n);
        if (map == null) {
            map = new HashMap<Integer, Integer>();
            _points.put(n, map);
        }
        if (map.isEmpty()) {
            map.put(n2, n3);
        } else {
            Integer n4 = map.get(n2);
            map.put(n2, n4 == null ? n3 : n4 + n3);
        }
        this.AW.unlock();
    }

    public TreeMap<Integer, Integer> calculateRanking() {
        TreeMap<Integer, Integer> treeMap = new TreeMap<Integer, Integer>();
        this.AW.lock();
        for (Map.Entry<Integer, Map<Integer, Integer>> object : _points.entrySet()) {
            Map<Integer, Integer> map = object.getValue();
            map.remove(KEY_RANK);
            map.remove(KEY_TOTAL_POINTS);
            int n = 0;
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                n += entry.getValue().intValue();
            }
            if (n == 0) continue;
            map.put(KEY_TOTAL_POINTS, n);
            treeMap.put(n, object.getKey());
        }
        int n = 1;
        for (Map.Entry entry : treeMap.descendingMap().entrySet()) {
            Map<Integer, Integer> map = _points.get(entry.getValue());
            map.put(KEY_RANK, n);
            ++n;
        }
        this.AW.unlock();
        return treeMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void distributeRewards() {
        this.AW.lock();
        try {
            TreeMap<Integer, Integer> treeMap = this.calculateRanking();
            Iterator iterator = treeMap.descendingMap().values().iterator();
            for (int i = 1; iterator.hasNext() && i <= 100; ++i) {
                int n = 0;
                int n2 = (Integer)iterator.next();
                if (i == 1) {
                    n = 2500;
                } else if (i == 2) {
                    n = 1800;
                } else if (i == 3) {
                    n = 1400;
                } else if (i == 4) {
                    n = 1200;
                } else if (i == 5) {
                    n = 900;
                } else if (i == 6) {
                    n = 700;
                } else if (i == 7) {
                    n = 600;
                } else if (i == 8) {
                    n = 400;
                } else if (i == 9) {
                    n = 300;
                } else if (i == 10) {
                    n = 200;
                } else if (i <= 50) {
                    n = 50;
                } else if (i <= 100) {
                    n = 25;
                }
                Player player = GameObjectsStorage.getPlayer(n2);
                Clan clan = null;
                clan = player != null ? player.getClan() : ClanTable.getInstance().getClan(mysql.simple_get_int("clanid", "characters", "obj_Id=" + n2));
                if (clan == null) continue;
                clan.incReputation(n, true, "RaidPoints");
            }
            _points.clear();
            this.updatePointsDb();
        }
        finally {
            this.AW.unlock();
        }
    }

    public Map<Integer, Map<Integer, Integer>> getPoints() {
        return _points;
    }

    public Map<Integer, Integer> getPointsForOwnerId(int n) {
        return _points.get(n);
    }

    static {
        _spawntable = new ConcurrentHashMap<Integer, Spawner>();
        KEY_RANK = Integer.valueOf(-1);
        KEY_TOTAL_POINTS = Integer.valueOf(0);
    }

    public static enum Status {
        ALIVE,
        DEAD,
        UNDEFINED;

    }
}
