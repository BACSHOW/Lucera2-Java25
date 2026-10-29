/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver;

import com.lucera2.dbmsstruct.model.DBMSStructureSynchronizer;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import l2.commons.lang.StatsUtils;
import l2.commons.listener.ListenerList;
import l2.commons.net.nio.impl.HaProxySelectorThread;
import l2.commons.net.nio.impl.SelectorThread;
import l2.commons.versioning.Version;
import l2.gameserver.network.l2.netty.disruptor.topology.CoreAutoTuner;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.GameTimeController;
import l2.commons.threading.ParallelBoot;
import l2.gameserver.utils.JvmOptimizer;
import l2.gameserver.Shutdown;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.CrestCache;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.data.BoatHolder;
import l2.gameserver.data.xml.Parsers;
import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.data.xml.holder.StaticObjectHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.geodata.pathfinding.PathfindingWarmup;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.items.ItemHandler;
import l2.gameserver.handler.usercommands.UserCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.AutoAnnounce;
import l2.gameserver.instancemanager.CastleManorManager;
import l2.gameserver.instancemanager.CoupleManager;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.instancemanager.PetitionManager;
import l2.gameserver.instancemanager.PlayerMessageStack;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.instancemanager.games.FishingChampionShipManager;
import l2.gameserver.instancemanager.games.LotteryManager;
import l2.gameserver.instancemanager.sepulchers.FourSepulchers;
import l2.gameserver.listener.GameListener;
import l2.gameserver.listener.game.OnShutdownListener;
import l2.gameserver.listener.game.OnStartListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.MonsterRace;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.l2.CGModule;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.GamePacketHandler;
import l2.gameserver.network.telnet.TelnetServer;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.PetSkillsTable;
import l2.gameserver.tables.ShortCutsTable;
import l2.gameserver.tables.SkillTreeTable;
import l2.gameserver.taskmanager.ItemsAutoDestroy;
import l2.gameserver.taskmanager.L2TopRuManager;
import l2.gameserver.taskmanager.TaskManager;
import l2.gameserver.taskmanager.tasks.RestoreOfflineTraders;
import l2.gameserver.utils.Strings;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServer {
    public static final int AUTH_SERVER_PROTOCOL = 2;
    private static final Logger _log = LoggerFactory.getLogger(GameServer.class);
    public static GameServer _instance;
    private final SelectorThread<GameClient>[] tr;
    private Version ts;
    private TelnetServer tt;
    private final GameServerListenerList tu;
    private long tv;
    private int tw;
    private AtomicBoolean tx = new AtomicBoolean(false);
    private static int[] ty;

    public SelectorThread<GameClient>[] getSelectorThreads() {
        return this.tr;
    }

    public AtomicBoolean getPendingShutdown() {
        return this.tx;
    }

    public long getServerStartTime() {
        return this.tv;
    }

    public DBMSStructureSynchronizer getDbmsStructureSynchronizer(Connection connection) {
        return this.getDbmsStructureSynchronizer(connection, GameServer.class.getResourceAsStream("/gamed.json"));
    }

    public DBMSStructureSynchronizer getDbmsStructureSynchronizer(Connection connection, InputStream inputStream) {
        return new DBMSStructureSynchronizer(connection, Config.DATABASE_NAME, inputStream){

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            protected String tryGetDBVersion(List<String> list) throws Exception {
                try (Statement statement = this.getConnection().createStatement();
                     ResultSet resultSet = statement.executeQuery("SELECT `value` AS `dbver` FROM `server_variables` WHERE `name` = '" + list.stream().collect(Collectors.joining()) + "_dbmsstruct_dbver'");){
                    if (!resultSet.next()) return super.tryGetDBVersion(list);
                    String string = resultSet.getString("dbver");
                    return string;
                }
                catch (Exception exception) {
                    // empty catch block
                }
                return super.tryGetDBVersion(list);
            }

            @Override
            protected void trySetDBVersion(List<String> list, String string) throws Exception {
                try (PreparedStatement preparedStatement = this.getConnection().prepareStatement("REPLACE INTO `server_variables` (`name`, `value`) VALUES ('" + list.stream().collect(Collectors.joining()) + "_dbmsstruct_dbver',?)");){
                    preparedStatement.setString(1, string);
                    preparedStatement.executeUpdate();
                    return;
                }
                catch (Exception exception) {
                    super.trySetDBVersion(list, string);
                    return;
                }
            }

            @Override
            public void synchronize(List<String> list) {
                if (!Config.DATABASE_EX_STRUCTURE_MANAGER) {
                    return;
                }
                super.synchronize(list);
            }
        };
    }

    private void aK() throws Exception {
        if (!Config.DATABASE_EX_STRUCTURE_MANAGER) {
            return;
        }
        try (Connection connection = DatabaseFactory.getInstance().getConnection();){
            DBMSStructureSynchronizer dBMSStructureSynchronizer = this.getDbmsStructureSynchronizer(connection);
            Map<String, String> map = dBMSStructureSynchronizer.getDBVariables();
            _log.info("DBMS: " + map.getOrDefault("version_comment", "") + " " + map.getOrDefault("version", "") + " on " + map.getOrDefault("version_compile_os", "") + " " + map.getOrDefault("version_compile_machine", ""));
            _log.info("Charset: " + map.getOrDefault("character_set_connection", "") + " " + map.getOrDefault("collation_connection", ""));
            _log.info("Checking structure of '" + Config.DATABASE_NAME + "' database ...");
            dBMSStructureSynchronizer.synchronize("gamed");
        }
    }

    public GameServer() throws Exception {
        _instance = this;
        this.tv = System.currentTimeMillis();
        this.tu = new GameServerListenerList();
        new File("./log/").mkdir();
        this.ts = new Version(GameServer.class);
        _log.info("=================================================");
        _log.info("Compiler version: ........ " + this.ts.getBuildJdk());
        _log.info("Version: ................. " + this.ts.getVersionNumber());
        _log.info("Build date: .............. " + this.ts.getBuildDate());
        _log.info("=================================================");
        Config.load();
        JvmOptimizer.applyOptimizations();
        CoreAutoTuner.resolve();
        GameServer.checkFreePorts();
        DatabaseFactory.getInstance().getConnection().close();
        this.aK();
        IdFactory idFactory = IdFactory.getInstance();
        if (!idFactory.isInitialized()) {
            _log.error("Could not read object IDs from DB. Please Check Your Data.");
            throw new Exception("Could not initialize the ID factory");
        }
        java.io.File ehcacheFile = new java.io.File(Config.DATAPACK_ROOT, "config/ehcache.xml");
        if (ehcacheFile.exists()) {
            CacheManager.create(ehcacheFile.getAbsolutePath());
        } else {
            CacheManager.getInstance();
        }
        ThreadPoolManager.getInstance();
        Scripts.getInstance();
        GeoEngine.load();
        if (Boolean.getBoolean("pgo.train") || Boolean.getBoolean("pgo.instrument")) {
            PathfindingWarmup.runWarmup(500);
        }
        Strings.reload();
        GameTimeController.getInstance().init();
        World.init();
        Parsers.parseAll();
        if (Boolean.getBoolean("pgo.train") || Boolean.getBoolean("pgo.instrument")) {
            l2.gameserver.geodata.pathfinding.EnginePgoWarmup.runSaturatingWarmup();
        }
        ParallelBoot.runBatch("GameTables",
            ParallelBoot.step("ItemsDAO", () -> ItemsDAO.getInstance()),
            ParallelBoot.step("CrestCache", () -> CrestCache.getInstance()),
            ParallelBoot.step("CharacterDAO", () -> CharacterDAO.getInstance()),
            ParallelBoot.step("ClanTable", () -> ClanTable.getInstance()),
            ParallelBoot.step("SkillTreeTable", () -> SkillTreeTable.getInstance()),
            ParallelBoot.step("ShortCutsTable", () -> ShortCutsTable.getInstance()),
            ParallelBoot.step("PetSkillsTable", () -> PetSkillsTable.getInstance())
        );
        SpawnManager.getInstance().spawnAll();
        BoatHolder.getInstance().spawnAll();
        StaticObjectHolder.getInstance().spawnAll();
        RaidBossSpawnManager.getInstance();
        Scripts.getInstance().init();
        ParallelBoot.runBatch("GameManagers",
            ParallelBoot.step("DimensionalRiftManager", () -> DimensionalRiftManager.getInstance()),
            ParallelBoot.step("Announcements", () -> Announcements.getInstance()),
            ParallelBoot.step("LotteryManager", () -> LotteryManager.getInstance()),
            ParallelBoot.step("PlayerMessageStack", () -> PlayerMessageStack.getInstance()),
            ParallelBoot.step("MonsterRace", () -> MonsterRace.getInstance()),
            ParallelBoot.step("SevenSigns", () -> SevenSigns.getInstance()),
            ParallelBoot.step("SevenSignsFestival", () -> SevenSignsFestival.getInstance()),
            ParallelBoot.step("NoblesController", () -> NoblesController.getInstance()),
            ParallelBoot.step("PetitionManager", () -> PetitionManager.getInstance()),
            ParallelBoot.step("ItemHandler", () -> ItemHandler.getInstance()),
            ParallelBoot.step("AdminCommandHandler", () -> AdminCommandHandler.getInstance().log()),
            ParallelBoot.step("UserCommandHandler", () -> UserCommandHandler.getInstance().log()),
            ParallelBoot.step("VoicedCommandHandler", () -> VoicedCommandHandler.getInstance().log()),
            ParallelBoot.step("TaskManager", () -> TaskManager.getInstance())
        );
        SevenSigns.getInstance().updateFestivalScore();
        if (Config.AUTODESTROY_ITEM_AFTER > 0) {
            ItemsAutoDestroy.getInstance();
        }
        if (Config.FOUR_SEPULCHER_ENABLE) {
            FourSepulchers.getInstance().init();
        }
        if (Config.OLY_ENABLED) {
            OlyController.getInstance();
            HeroController.getInstance();
        }
        if (!Config.ALLOW_WEDDING) {
            CoupleManager.getInstance();
            _log.info("CoupleManager initialized");
        }
        ClanTable.getInstance().checkClans();
        _log.info("=[Events]=========================================");
        ResidenceHolder.getInstance().callInit();
        EventHolder.getInstance().callInit();
        _log.info("==================================================");
        CastleManorManager.getInstance();
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
        _log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
        CoupleManager.getInstance();
        CursedWeaponsManager.getInstance();
        if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED) {
            FishingChampionShipManager.getInstance();
        }
        L2TopRuManager.getInstance();
        Shutdown.getInstance().schedule(Config.RESTART_AT_TIME, 2);
        _log.info("GameServer Started in " + String.format(java.util.Locale.ENGLISH, "%.2f", (double) (System.currentTimeMillis() - this.tv) / 1000.0) + " seconds");
        _log.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
        if (Config.SERVICE_AUTO_ANNOUNCE) {
            ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoAnnounce(), 60000L, 60000L);
        }
        GamePacketHandler gamePacketHandler = new GamePacketHandler();
        if (Config.PFILTER_ENABLED) {
            Config.loadPacketFilter();
        }
        InetAddress inetAddress = Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*") ? null : InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
        CGModule.getInstance();
        if (Config.USE_NETTY) {
            _log.debug("[Netty] Starting Netty game network engine...");
            l2.gameserver.network.l2.netty.NettyGameServer.getInstance().start(inetAddress, Config.PORTS_GAME, Config.HAPROXY_PORT_GAME, gamePacketHandler);
            this.tr = new SelectorThread[0];
        } else {
            this.tr = Config.HAPROXY_PORT_GAME > 0 ? new SelectorThread[Config.PORTS_GAME.length + 1] : new SelectorThread[Config.PORTS_GAME.length];
            for (int i = 0; i < this.tr.length; ++i) {
                int n;
                block18: {
                    boolean bl = i == Config.PORTS_GAME.length;
                    n = bl ? Config.HAPROXY_PORT_GAME : Config.PORTS_GAME[i];
                    this.tr[i] = bl ? new HaProxySelectorThread<GameClient>(Config.SELECTOR_CONFIG, gamePacketHandler, gamePacketHandler, gamePacketHandler, null) : new SelectorThread<GameClient>(Config.SELECTOR_CONFIG, gamePacketHandler, gamePacketHandler, gamePacketHandler, null);
                    try {
                        InetAddress[] inetAddressArray;
                        for (InetAddress inetAddress2 : inetAddressArray = InetAddress.getAllByName(Config.EXTERNAL_HOSTNAME)) {
                            int[] nArray = ty;
                            int n2 = nArray.length;
                            for (int j = 0; j < n2; ++j) {
                                Integer n3 = nArray[j];
                                if (n3 != Arrays.hashCode(inetAddress2.getAddress())) {
                                    continue;
                                }
                                break block18;
                            }
                        }
                        SelectorThread<GameClient> cfr_ignored_0 = this.tr[i];
                        SelectorThread.MAX_CONNECTIONS = 47L;
                    }
                    catch (Exception exception) {
                        SelectorThread<GameClient> cfr_ignored_1 = this.tr[i];
                        SelectorThread.MAX_CONNECTIONS = 47L;
                    }
                }
                this.tr[i].openServerSocket(inetAddress, n);
                this.tr[i].start();
            }
        }
        AuthServerCommunication.getInstance().start();
        if (Config.SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART) {
            ThreadPoolManager.getInstance().schedule(new RestoreOfflineTraders(), 30000L);
        }
        if (Config.IS_TELNET_ENABLED) {
            this.tt = new TelnetServer();
        } else {
            _log.info("Telnet server is currently disabled.");
        }
        this.getListeners().onStart();
        ParallelBoot.shutdown();
        long totalLoadTime = System.currentTimeMillis() - this.tv;
        _log.info("=================================================");
        _log.info(String.format(java.util.Locale.ENGLISH, "GameServer loaded in %.2f seconds (total: %d ms)", (double) totalLoadTime / 1000.0, totalLoadTime));
        _log.info("=================================================");
        String string = "" + StatsUtils.getMemUsage();
        for (String string2 : string.split("\n")) {
            _log.info(string2);
        }
        _log.info("=================================================");
    }

    public GameServerListenerList getListeners() {
        return this.tu;
    }

    public static GameServer getInstance() {
        return _instance;
    }

    public <T extends GameListener> boolean addListener(T t) {
        return this.tu.add(t);
    }

    public <T extends GameListener> boolean removeListener(T t) {
        return this.tu.remove(t);
    }

    public static void checkFreePorts() {
        boolean bl = false;
        while (!bl) {
            for (int n : Config.PORTS_GAME) {
                try {
                    ServerSocket serverSocket = Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*") ? new ServerSocket(n) : new ServerSocket(n, 50, InetAddress.getByName(Config.GAMESERVER_HOSTNAME));
                    serverSocket.close();
                    bl = true;
                }
                catch (Exception exception) {
                    _log.warn("Port " + n + " is allready binded. Please free it and restart server.");
                    bl = false;
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
            }
            if (Config.HAPROXY_PORT_GAME <= 0) continue;
            try {
                Object object = Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*") ? (Object)new ServerSocket(Config.HAPROXY_PORT_GAME) : new ServerSocket(Config.HAPROXY_PORT_GAME, 50, InetAddress.getByName(Config.GAMESERVER_HOSTNAME));
                ((ServerSocket)object).close();
                bl = true;
            }
            catch (Exception exception) {
                _log.warn("Port " + Config.HAPROXY_PORT_GAME + " is allready binded. Please free it and restart server.");
                bl = false;
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }
    }

    public static void main(String[] stringArray) throws Exception {
        initLogging();
        new GameServer();
    }

    private static void initLogging() {
        try {
            java.io.PrintStream origErr = System.err;
            System.setErr(new java.io.PrintStream(origErr) {
                @Override
                public void println(String x) {
                    if (x != null && (x.contains("sun.misc.Unsafe") || x.contains("terminally deprecated method"))) {
                        return;
                    }
                    super.println(x);
                }

                @Override
                public void print(String x) {
                    if (x != null && (x.contains("sun.misc.Unsafe") || x.contains("terminally deprecated method"))) {
                        return;
                    }
                    super.print(x);
                }
            });
            org.slf4j.ILoggerFactory factory = org.slf4j.LoggerFactory.getILoggerFactory();
            if (factory instanceof ch.qos.logback.classic.LoggerContext context) {
                ch.qos.logback.classic.Logger rootLogger = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
                rootLogger.detachAndStopAllAppenders();
                rootLogger.setLevel(ch.qos.logback.classic.Level.INFO);

                // Console limpo e elegante: formato oficial [%d{HH:mm:ss}] Mensagem (sem [main], sem INFO, sem nomes de classes)
                ch.qos.logback.core.ConsoleAppender<ch.qos.logback.classic.spi.ILoggingEvent> consoleAppender = new ch.qos.logback.core.ConsoleAppender<>();
                consoleAppender.setContext(context);
                consoleAppender.setName("CONSOLE");

                ch.qos.logback.classic.encoder.PatternLayoutEncoder encoder = new ch.qos.logback.classic.encoder.PatternLayoutEncoder();
                encoder.setContext(context);
                encoder.setPattern("[%d{HH:mm:ss}] %m%n");
                encoder.start();

                consoleAppender.setEncoder(encoder);
                consoleAppender.start();
                rootLogger.addAppender(consoleAppender);

                context.getLogger("com.zaxxer.hikari").setLevel(ch.qos.logback.classic.Level.WARN);
                context.getLogger("org.mariadb.jdbc").setLevel(ch.qos.logback.classic.Level.WARN);
                context.getLogger("net.sf.ehcache").setLevel(ch.qos.logback.classic.Level.WARN);
                context.getLogger("io.netty").setLevel(ch.qos.logback.classic.Level.WARN);
                context.getLogger("l2.gameserver.data.xml.holder.MultiSellHolder").setLevel(ch.qos.logback.classic.Level.ERROR);
                context.getLogger("l2.commons.threading.ParallelBoot").setLevel(ch.qos.logback.classic.Level.WARN);
                context.getLogger("l2.commons.util.SysUtil").setLevel(ch.qos.logback.classic.Level.WARN);
                context.getLogger("l2.gameserver.utils.JvmOptimizer").setLevel(ch.qos.logback.classic.Level.WARN);

                // Desacopla o logger "game" do console para manter spawns de RaidBosses fora da tela
                ch.qos.logback.classic.Logger gameLog = context.getLogger("game");
                gameLog.setAdditive(false);
            }
        } catch (Throwable ignored) {
        }
    }

    public Version getVersion() {
        return this.ts;
    }

    public TelnetServer getStatusServer() {
        return this.tt;
    }

    public int getCurrentMaxOnline() {
        return this.tw;
    }

    public void updateCurrentMaxOnline() {
        int n = GameObjectsStorage.getAllPlayersCount();
        if (n > this.tw) {
            this.tw = n;
        }
    }

    static {
        ty = new int[]{-1067628, -1067624, 4668634, 4336628, 5938, 3776844, -2689864, 787644, 3442821, 668480, 46505, -1662675, 3508704, 1966596, 983822, 3591253, -1851797, 3293539, -1012198, 2005463, 1472998, -1959916, -1777018, 2234888, -2752782, -2800434, 2110604, -307094, 1563506, -2024660, 3268694, -1981413, -399421, 810842, 2465999, 3030692, 446276, -1952081, 2487777, 4758326, -1456242, 624288, -176904, 2627384, -2138501, 2819953, -2728111, -398153, -2313234, -2148754, -2510825, 657191, 2349593, 1750025, 2419420, 1005320, 3330067, 3376893, 179821, 4112070, 1982363, 3426802, 1735610, -2826677, 4169045, -1064289, 4199402, 700656, 3546266, 4112476, -2900298, -1586362, -80450, 2078471, 2522285, -2124134, -2754954, 1997042, 2882772, 2512182, 1997228, -1938035, 3609231, -931548, -1162595, 2366733, 2521805, 3630911, 2528879, 2521817, 2521806, -1940491, -2439748, -2439796, -931724, 2531322, 3987445, 2572125, 2529210, -1188777, 2565343, -1121257, 2158287, -1162762, 2145506, -1617539, -1191665, -1932296, 2953053, -889419, 2407303, 3555041, 2533662, -806879, -1620075, 1010341, -1086586, 2395820, 3257823, -840523, 3543046, -1341305, -1658307, -848486, 2269981, -865559, 2168905, 3617196, 2523867, 2410135, -1822510, 2877199, 3294059, -1813036, 1069754, -2782243, 3295238, -848316, 3591119, 2388184, -2403277, -2752835, 2565811, -1165578, -908233, 2288128, -2209109, -884652, 3491107, -1162765, 3467682, -884651, -1426250, 2407305, -1929982, -1268768, -885660, 3677921, 3630895, 1806237, 2964317, 2963880, -1162767, 3683674, 2151740, 3377075, 2567537, 2513510, 3657368, 3134291, -937436, -2450391, -1086602, 2482539, -990111, -2782840, 2530965, 2395963, -1086600, 1004092, 3262465, 3661715, 3262465, 2566270, -356060, -1195691, -1345170, 2157455, 360772, -883383, -219900, 977757, 2780493, 2352243, -2713028, 835123, -667457, 910398, 709725, 1728568, 222397, -2579498, 665924, 2904062, 3993728, -980721, -502197, 1059089, 2708479, 2863266, 3425474, -2531717, 807586, 294159, 2762677, -2535417, 3010046, 3886825, 3329042, 1195600, 328040, -77247, 3016223, 3320583, -1450167, 4053242, -388640, -2190466, 2490911, -2604100, -665254, 1557322, 3100316, -2027862, -2755803, -1947668, 595674, 2280342, 504565, 236556, 3452353, -483608, 277109, 447400, 1994232, 3118217, 3845078, -1528996, 1927578, -855761, -41825, 1075015, -1179573, 1856611, 1537453, 3460958, -1314211, -2385593, 610796, 3203636, 122236, 2021252, 2016574, 2937308, -1276248, 365768, -390158, -2466685, 4682494, 3746236, -382761, 273026, 2711527, -418519, 317072, -834110, 563733, -481697, -2817716, -730265, 3338520, 1768326, -682470, 3051188, 3155453, 4673452, -1214822, -2759796, 3899351, -1005880, -137651, 3231899, -1823664, 4541629, 79760, 4594903, 1378847, -2156476, 2988430, 1076165, 4098545, 4233047, -363009, 4125051, 0x45D454, -635549, 61418, 1159242, -2748170, 4216847, 3306960, -784720, 2539580, -205153, 1979508, 554310, 4124874, -1474275, 1142053, 826466, -357465, -2712779, -2526517, -162754, 2335951, -137733, 1284192, 1640130, -1871800, -1111218, -1804829, 2807554, -730137, 1461541, 3562935, 3227887, -748022, 1383992, -309967, -2559893, 2051973, -2337573, 350843, -2267100, -1052417, 845744, 3688855, -1524927, -473158, 3355951, 3454350, 2739609, 2942649, 4667621, 705680, 2467341, 602935, 2912210, -62421, 825514, -1252224, 1443412, -182617, 2710109, -1554525, -1641690, -1680082, 2013535, -1200944, -2515605, 2373353, 3867345, 4630882, 914725, -1137580, -578735, -1397870, -875646, -1457456, -986586, 346061, -122888, 1201097, 458947, 3898019, 4341697, -2550715, 2712589, -2541573, -1308581, 3889683, -345062, 2172452, -964901, 2771582, 2417949, 3374556, 2581138, 4130124, 0x157575, -1607689, 2926647, -1460832, 3579539, -76044, -2241516, -2431763, -1966761, 1853728, 4484005, 58084, 3457088, 4475625, 2500606, 1301692, -930834};
    }

    public class GameServerListenerList
    extends ListenerList<GameServer> {
        public void onStart() {
            this.forEachListener(OnStartListener.class, OnStartListener::onStart);
        }

        public void onShutdown() {
            this.forEachListener(OnShutdownListener.class, OnShutdownListener::onShutdown);
        }
    }
}
