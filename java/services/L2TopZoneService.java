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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.dbutils.DbUtils;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L2TopZoneService
implements IVoicedCommandHandler,
ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(L2TopZoneService.class);
    private static final String[] bXd = new String[]{"l2topzone", "topzone"};
    private static final String bXe = "https://api.l2topzone.com/v1/vote?token=%api_key%&ip=%player_key%";
    private static final Pattern bXj = Pattern.compile("\"?ok\"?\\s*:\\s*\"?([^\",\\}]+)\"?", 8);
    private static final Pattern bXk = Pattern.compile("\"?isVoted\"?\\s*:\\s*\"?([^\",\\}]+)\"?", 8);
    private Executor bXf = Executors.newSingleThreadExecutor(new ThreadFactory(){

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "L2TopZoneService-exec");
        }
    });
    private static L2TopZoneService bXl = new L2TopZoneService();

    public static L2TopZoneService getInstance() {
        return bXl;
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
                preparedStatement = connection.prepareStatement("SELECT `last_check` AS `lastCheck` FROM `l2topzone_votes` WHERE `player_key` = ?");
                preparedStatement.setString(1, string);
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block4;
                l = resultSet.getLong(1);
            }
            catch (SQLException sQLException) {
                try {
                    LOG.error("L2TopZoneService: Cant get last stored vote check date", sQLException);
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
            preparedStatement = connection.prepareStatement("REPLACE INTO `l2topzone_votes`(`player_key`, `last_check`) VALUES (?, ?)");
            preparedStatement.setString(1, string);
            preparedStatement.setLong(2, l);
            preparedStatement.executeUpdate();
        }
        catch (SQLException sQLException) {
            try {
                LOG.error("L2TopZoneService: Cant set last stored vote check date", sQLException);
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
            uRLConnection.addRequestProperty("User-Agent", "L2TopZone");
            uRLConnection.setConnectTimeout(1000);
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
                String string3 = stringBuilder.toString();
                Matcher matcher = bXj.matcher(string3);
                if (!matcher.find()) {
                    boolean bl = false;
                    return bl;
                }
                String string4 = matcher.group(1);
                if (!StringUtils.equalsIgnoreCase("true", string4)) {
                    boolean bl = false;
                    return bl;
                }
                Matcher matcher2 = bXk.matcher(string3);
                if (!matcher2.find()) {
                    boolean bl = false;
                    return bl;
                }
                String string5 = matcher2.group(1);
                if (StringUtils.equalsIgnoreCase("true", string5)) {
                    boolean bl = true;
                    return bl;
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
            LOG.warn("L2TopZoneService: Check request failed", exception);
            return false;
        }
    }

    private void aA(final Player player) {
        final String string = L2TopZoneService.az(player);
        if (player == null) {
            return;
        }
        long l = System.currentTimeMillis() / 1000L;
        long l2 = L2TopZoneService.I(string);
        long l3 = l2 + Config.L2TOPZONE_VOTE_TIME_TO_LIVE - l;
        if (l3 > 0L) {
            int n = (int)(l3 / 3600L);
            int n2 = (int)(l3 % 3600L);
            if (n > 0) {
                player.sendPacket((IStaticPacket)new SystemMessage(1813).addString("L2TopZone").addNumber(n));
            } else {
                player.sendPacket((IStaticPacket)new SystemMessage(1814).addString("L2TopZone").addNumber(n2));
            }
            return;
        }
        L2TopZoneService.a(string, l);
        this.bXf.execute(new Runnable(){

            @Override
            public void run() {
                try {
                    if (L2TopZoneService.e(Config.L2TOPZONE_API_KEY, string) && Config.L2TOPZONE_REWARD_ITEM_ID.length > 0 && Config.L2TOPZONE_REWARD_ITEM_COUNT.length > 0) {
                        for (int i = 0; i < Config.L2TOPZONE_REWARD_ITEM_ID.length; ++i) {
                            if (Config.L2TOPZONE_REWARD_ITEM_ID[i] == 0 || Config.L2TOPZONE_REWARD_ITEM_COUNT[i] <= 0 || !Rnd.chance(Config.L2TOPZONE_REWARD_CHANCE[i])) continue;
                            ItemFunctions.addItem((Playable)player, Config.L2TOPZONE_REWARD_ITEM_ID[i], (long)Config.L2TOPZONE_REWARD_ITEM_COUNT[i], true);
                            player.sendMessage(new CustomMessage("services.L2TopZone.Thank", player, new Object[0]));
                            Log.service("L2TopZoneService", player, "received an award for voting " + Config.L2TOPZONE_REWARD_ITEM_ID[i] + " amount " + Config.L2TOPZONE_REWARD_ITEM_COUNT[i]);
                        }
                    } else {
                        player.sendMessage(new CustomMessage("services.L2TopZone.NoVotes", player, new Object[0]));
                    }
                }
                catch (Throwable throwable) {
                    LOG.warn("L2TopZoneService: Cant process reward.");
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
            GameServer.getInstance().getDbmsStructureSynchronizer(connection).synchronize("l2topzone_votes");
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onLoad() {
        if (Config.L2TOPZONE_ENABLED) {
            L2TopZoneService.e();
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(L2TopZoneService.getInstance());
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

