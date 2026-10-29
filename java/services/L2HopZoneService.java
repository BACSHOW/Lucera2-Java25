/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import l2.commons.dbutils.DbUtils;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L2HopZoneService
implements IVoicedCommandHandler,
ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(L2HopZoneService.class);
    private static final String bXc = "L2HopZoneVoteTimestamp";
    private static final String[] bXd = new String[]{"hopzone", Config.L2HOPZONE_ADD_COMMAND};
    private static final String bXe = "https://api.hopzone.net/lineage2/vote?token=%api_key%&ip_address=%player_key%";
    private Executor bXf = Executors.newSingleThreadExecutor(new ThreadFactory(){

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "L2HopZoneService-exec");
        }
    });
    private static L2HopZoneService bXg = new L2HopZoneService();

    public static L2HopZoneService getInstance() {
        return bXg;
    }

    private static String az(Player player) {
        if (player == null) {
            return null;
        }
        GameClient gameClient = player.getNetConnection();
        if (gameClient == null || !gameClient.isConnected() || gameClient.getState() != GameClient.GameClientState.IN_GAME) {
            return null;
        }
        return gameClient.getIpAddr();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static long I(String string) {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection;
        long l;
        block4: {
            l = 0L;
            connection = null;
            preparedStatement = null;
            resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("SELECT `last_check` AS `lastCheck` FROM `l2hopzone_votes` WHERE `player_key` = ?");
                preparedStatement.setString(1, string);
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block4;
                l = resultSet.getLong(1);
            }
            catch (SQLException sQLException) {
                try {
                    LOG.error("L2HopZoneService: Cant get last stored vote check date", sQLException);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return l;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void a(String string, long l) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("REPLACE INTO `l2hopzone_votes`(`player_key`, `last_check`) VALUES (?, ?)");
            preparedStatement.setString(1, string);
            preparedStatement.setLong(2, l);
            preparedStatement.executeUpdate();
        }
        catch (SQLException sQLException) {
            try {
                LOG.error("L2HopZoneService: Cant set last stored vote check date", sQLException);
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static boolean e(String string, String string2) throws Exception {
        try {
            URL uRL = new URL(bXe.replace("%api_key%", string).replace("%player_key%", string2));
            URLConnection uRLConnection = uRL.openConnection();
            uRLConnection.addRequestProperty("Host", uRL.getHost());
            uRLConnection.addRequestProperty("Accept", "*/*");
            uRLConnection.addRequestProperty("Connection", "close");
            uRLConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            uRLConnection.setConnectTimeout(5000);
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            try {
                inputStream = uRLConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                char[] cArray = new char[64];
                inputStreamReader = new InputStreamReader(inputStream);
                int n = -1;
                while ((n = inputStreamReader.read(cArray)) > 0 && stringBuilder.length() < 1024) {
                    stringBuilder.append(cArray, 0, n);
                }
                String string3 = stringBuilder.toString().toLowerCase();
                if ((string3.indexOf("\"status_code\":200") > 0 || string3.indexOf("\"status_code\":\"200\"") > 0) && string3.indexOf("\"voted\":true") > 0) {
                    boolean bl2 = true;
                    return bl2;
                }
                boolean bl = false;
                return bl;
            }
            finally {
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    }
                    catch (IOException iOException) {}
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
        catch (Exception exception) {
            LOG.warn("L2HopZoneService: Check request failed", exception);
            return false;
        }
    }

    private void aA(Player player) {
        long l;
        long l2;
        final String string = L2HopZoneService.az(player);
        if (player == null) {
            return;
        }
        long l3 = System.currentTimeMillis();
        final long l4 = TimeUnit.MILLISECONDS.toSeconds(l3);
        if (Config.L2HOPZONE_REUSE_TIME > 0L) {
            l2 = player.getVarLong(bXc, 0L);
            l = l2 + Config.L2HOPZONE_REUSE_TIME - l3;
            if (l > 0L) {
                long l5 = TimeUnit.MILLISECONDS.toMinutes(l);
                player.sendPacket((IStaticPacket)new SystemMessage(1814).addString("L2HopZone").addNumber(l5));
                return;
            }
            player.setVar(bXc, l3, -1L);
        }
        if ((l = (l2 = L2HopZoneService.I(string)) + Config.L2HOPZONE_VOTE_TIME_TO_LIVE - l4) > 0L) {
            int n = (int)(l / 3600L);
            int n2 = (int)(l % 3600L);
            if (n > 0) {
                player.sendPacket((IStaticPacket)new SystemMessage(1813).addString("L2HopZone").addNumber(n));
            } else {
                player.sendPacket((IStaticPacket)new SystemMessage(1814).addString("L2HopZone").addNumber(n2));
            }
            return;
        }
        final HardReference<Player> hardReference = player.getRef();
        this.bXf.execute(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                Player player = (Player)hardReference.get();
                if (player == null) {
                    return;
                }
                try {
                    if (L2HopZoneService.e(Config.L2HOPZONE_API_KEY, string) && Config.L2HOPZONE_REWARD_ITEM_ID.length > 0 && Config.L2HOPZONE_REWARD_ITEM_COUNT.length > 0) {
                        for (int i = 0; i < Config.L2HOPZONE_REWARD_ITEM_ID.length; ++i) {
                            if (Config.L2HOPZONE_REWARD_ITEM_ID[i] == 0 || Config.L2HOPZONE_REWARD_ITEM_COUNT[i] <= 0 || !Rnd.chance(Config.L2HOPZONE_REWARD_CHANCE[i])) continue;
                            ItemFunctions.addItem((Playable)player, Config.L2HOPZONE_REWARD_ITEM_ID[i], (long)Config.L2HOPZONE_REWARD_ITEM_COUNT[i], true);
                            player.sendMessage(new CustomMessage("services.L2HopZone.Thank", player, new Object[0]));
                            Log.service("L2HopZoneService", player, "received an award for voting " + Config.L2HOPZONE_REWARD_ITEM_ID[i] + " amount " + Config.L2HOPZONE_REWARD_ITEM_COUNT[i]);
                        }
                        L2HopZoneService.a(string, l4);
                    } else {
                        player.sendMessage(new CustomMessage("services.L2HopZone.NoVotes", player, new Object[0]));
                    }
                }
                catch (Throwable throwable) {
                    LOG.warn("L2HopZoneService: Cant process reward.");
                    player.sendPacket((IStaticPacket)SystemMsg.SYSTEM_ERROR);
                }
            }
        });
    }

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        for (String string3 : bXd) {
            if (!string3.equalsIgnoreCase(string)) continue;
            this.aA(player);
            return true;
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return bXd;
    }

    private static void e() {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();){
            GameServer.getInstance().getDbmsStructureSynchronizer(connection).synchronize("l2hopzone_votes");
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onLoad() {
        if (Config.L2HOPZONE_ENABLED) {
            L2HopZoneService.e();
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(L2HopZoneService.getInstance());
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

