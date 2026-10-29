/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L2TopRuManager {
    private static final Logger _log = LoggerFactory.getLogger(L2TopRuManager.class);
    private static L2TopRuManager arj;
    private static String ark;
    private Pattern arl;
    private Pattern arm;
    private static final DateFormat zQ;
    private static Map<Integer, Long> arn;

    public static L2TopRuManager getInstance() {
        if (arj == null) {
            arj = new L2TopRuManager();
        }
        return arj;
    }

    private static void e() {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();){
            GameServer.getInstance().getDbmsStructureSynchronizer(connection).synchronize("l2topru_votes");
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private L2TopRuManager() {
        if (Config.L2TOPRU_DELAY < 1L) {
            return;
        }
        _log.info("L2TopRuManager: Initializing.");
        L2TopRuManager.e();
        this.arl = Pattern.compile("^([\\d-]+\\s[\\d:]+)\\s+(?:" + Config.L2TOPRU_PREFIX + "-)*([^\\s]+)$", 8);
        this.arm = Pattern.compile("^([\\d-]+\\s[\\d:]+)\\s+(?:" + Config.L2TOPRU_PREFIX + "-)*([^\\s]+)\\s+x(\\d{1,2})$", 8);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new L2TopRuTask(), Config.L2TOPRU_DELAY, Config.L2TOPRU_DELAY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ArrayList<L2TopRuVote> filterVotes(ArrayList<L2TopRuVote> arrayList) {
        ArrayList<L2TopRuVote> arrayList2 = new ArrayList<L2TopRuVote>();
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT `obj_Id`,`char_name` FROM `characters`");
            while (resultSet.next()) {
                hashMap.put(resultSet.getString("char_name"), resultSet.getInt("obj_Id"));
            }
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        int n = 0;
        for (L2TopRuVote l2TopRuVote : arrayList) {
            if (!hashMap.containsKey(l2TopRuVote.charname) || !this.c(n = ((Integer)hashMap.get(l2TopRuVote.charname)).intValue(), l2TopRuVote.datetime)) continue;
            l2TopRuVote.char_obj_id = n;
            arrayList2.add(l2TopRuVote);
        }
        return arrayList2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean c(int n, long l) {
        block17: {
            long l2 = 0L;
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            if (arn.containsKey(n)) {
                l2 = arn.get(n);
                if (l > l2) {
                    arn.put(n, l);
                    try {
                        connection = DatabaseFactory.getInstance().getConnection();
                        preparedStatement = connection.prepareStatement("REPLACE DELAYED INTO `l2topru_votes`(`obj_Id`,`last_vote`) VALUES (?,?)");
                        preparedStatement.setInt(1, n);
                        preparedStatement.setLong(2, l);
                        preparedStatement.executeUpdate();
                        DbUtils.closeQuietly(connection, preparedStatement);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    return true;
                }
            } else {
                try {
                    connection = DatabaseFactory.getInstance().getConnection();
                    preparedStatement = connection.prepareStatement("SELECT `obj_Id`,`last_vote` FROM `l2topru_votes` WHERE `obj_Id` = ?");
                    preparedStatement.setInt(1, n);
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        l2 = resultSet.getLong("last_vote");
                        DbUtils.closeQuietly(preparedStatement, resultSet);
                        if (l > l2) {
                            arn.put(n, l);
                            try {
                                preparedStatement = connection.prepareStatement("REPLACE DELAYED INTO `l2topru_votes`(`obj_Id`,`last_vote`) VALUES (?,?)");
                                preparedStatement.setInt(1, n);
                                preparedStatement.setLong(2, l);
                                preparedStatement.executeUpdate();
                                DbUtils.closeQuietly(connection, preparedStatement);
                            }
                            catch (Exception exception) {
                                exception.printStackTrace();
                            }
                            boolean bl = true;
                            return bl;
                        }
                        arn.put(n, l2);
                        break block17;
                    }
                    DbUtils.closeQuietly(preparedStatement, resultSet);
                    arn.put(n, l);
                    try {
                        preparedStatement = connection.prepareStatement("REPLACE DELAYED INTO `l2topru_votes`(`obj_Id`,`last_vote`) VALUES (?,?)");
                        preparedStatement.setInt(1, n);
                        preparedStatement.setLong(2, l);
                        preparedStatement.executeUpdate();
                        DbUtils.closeQuietly(connection, preparedStatement);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    boolean bl = true;
                    return bl;
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                finally {
                    DbUtils.closeQuietly(connection);
                }
            }
        }
        return false;
    }

    private void e(int n, int n2, int n3) {
        if (n < 1) {
            return;
        }
        Player player = World.getPlayer(n);
        if (player == null) {
            DelayedItemsManager.getInstance().addDelayed(n, n2, n3, 0, "L2Top Reward Item " + n2 + "(" + n3 + ") " + System.currentTimeMillis());
        } else {
            player.sendMessage(new CustomMessage("l2.gameserver.taskmanager.L2TopRuManager", player, new Object[0]).addItemName(player.getInventory().addItem(n2, n3)));
            ItemFunctions.addItem((Playable)player, n2, (long)n3, true);
        }
    }

    private void a(ArrayList<L2TopRuVote> arrayList) {
        for (L2TopRuVote l2TopRuVote : arrayList) {
            switch (l2TopRuVote.type) {
                case WEB: {
                    _log.info("L2TopRuManager: Rewarding " + l2TopRuVote.toString());
                    this.e(l2TopRuVote.char_obj_id, Config.L2TOPRU_WEB_REWARD_ITEMID, Config.L2TOPRU_WEB_REWARD_ITEMCOUNT);
                    break;
                }
                case SMS: {
                    _log.info("L2TopRuManager: Rewarding " + l2TopRuVote.toString());
                    if (!Config.L2TOPRU_SMS_REWARD_VOTE_MULTI) break;
                    this.e(l2TopRuVote.char_obj_id, Config.L2TOPRU_SMS_REWARD_ITEMID, Config.L2TOPRU_SMS_REWARD_VOTE_MULTI ? Config.L2TOPRU_SMS_REWARD_ITEMCOUNT * l2TopRuVote.count : Config.L2TOPRU_SMS_REWARD_ITEMCOUNT);
                }
            }
        }
    }

    private ArrayList<L2TopRuVote> dA() {
        ArrayList<L2TopRuVote> arrayList = new ArrayList<L2TopRuVote>();
        try {
            L2TopRuVote l2TopRuVote;
            String string;
            String string2;
            Matcher matcher = this.arl.matcher(this.A(Config.L2TOPRU_WEB_VOTE_URL));
            while (matcher.find()) {
                string2 = matcher.group(1);
                string = matcher.group(2);
                if (!Util.isMatchingRegexp(string, Config.CNAME_TEMPLATE)) continue;
                l2TopRuVote = new L2TopRuVote(string2, string);
                arrayList.add(l2TopRuVote);
            }
            matcher = this.arm.matcher(this.A(Config.L2TOPRU_SMS_VOTE_URL));
            while (matcher.find()) {
                string2 = matcher.group(1);
                string = matcher.group(2);
                String string3 = matcher.group(3);
                if (!Util.isMatchingRegexp(string, Config.CNAME_TEMPLATE)) continue;
                l2TopRuVote = new L2TopRuVote(string2, string, string3);
                arrayList.add(l2TopRuVote);
            }
            Collections.sort(arrayList, new L2TopRuVoteComparator());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return arrayList;
    }

    private void dB() {
        this.a(this.filterVotes(this.dA()));
    }

    private String A(String string) throws Exception {
        try {
            URL uRL = new URL(string);
            URLConnection uRLConnection = uRL.openConnection();
            uRLConnection.addRequestProperty("Host", uRL.getHost());
            uRLConnection.addRequestProperty("Accept", "*/*");
            uRLConnection.addRequestProperty("Connection", "close");
            uRLConnection.addRequestProperty("User-Agent", ark);
            uRLConnection.setConnectTimeout(30000);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream(), "cp1251"));
            StringBuilder stringBuilder = new StringBuilder();
            String string2 = null;
            while ((string2 = bufferedReader.readLine()) != null) {
                stringBuilder.append(string2).append("\n");
            }
            return stringBuilder.toString();
        }
        catch (Exception exception) {
            return "";
        }
    }

    static {
        ark = "Mozilla/5.0 (SunOS; 5.10; amd64; U) Java HotSpot(TM) 64-Bit Server VM/16.2-b04";
        zQ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        arn = new ConcurrentHashMap<Integer, Long>();
    }

    private final class L2TopRuVoteComparator<T>
    implements Comparator<L2TopRuVote> {
        private L2TopRuVoteComparator() {
        }

        @Override
        public int compare(L2TopRuVote l2TopRuVote, L2TopRuVote l2TopRuVote2) {
            if (l2TopRuVote.datetime == l2TopRuVote2.datetime) {
                return 0;
            }
            if (l2TopRuVote.datetime < l2TopRuVote2.datetime) {
                return Integer.MIN_VALUE;
            }
            if (l2TopRuVote.datetime > l2TopRuVote2.datetime) {
                return Integer.MAX_VALUE;
            }
            return -1;
        }
    }

    private class L2TopRuVote {
        public long datetime;
        public String charname;
        public int count;
        public int char_obj_id = -1;
        public L2TopRuVoteType type;

        public L2TopRuVote(String string, String string2, String string3) throws Exception {
            this.datetime = zQ.parse(string).getTime() / 1000L;
            this.count = Byte.parseByte(string3);
            this.charname = string2;
            this.type = L2TopRuVoteType.SMS;
        }

        public L2TopRuVote(String string, String string2) throws Exception {
            this.datetime = zQ.parse(string).getTime() / 1000L;
            this.charname = string2;
            this.count = 1;
            this.type = L2TopRuVoteType.WEB;
        }

        public String toString() {
            return this.charname + "-" + this.count + "[" + this.char_obj_id + "(" + this.datetime + "|" + this.type.name() + ")]";
        }
    }

    private static enum L2TopRuVoteType {
        WEB,
        SMS;

    }

    private class L2TopRuTask
    implements Runnable {
        private L2TopRuTask() {
        }

        @Override
        public void run() {
            L2TopRuManager.getInstance().dB();
        }
    }
}

