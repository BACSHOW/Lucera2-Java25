/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.parser.ItemParser;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.taskmanager.actionrunner.tasks.SchedulingPatternTask;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MMOTopVote
implements ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(MMOTopVote.class);
    private static final Pattern bXm = Pattern.compile("^(\\d+)\\s+(\\d{2}\\.\\d{2}\\.\\d{4}\\s+\\d{2}:\\d{2}:\\d{2})\\s+([0-9a-fA-F\\.\\:]+)\\s+([\\w\\W]*?)\\s+(\\d+)$", 8);
    private static final DateFormat bXn = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final DateFormat bXo = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
    private static Map<MMOTopVoteType, List<Pair<ItemTemplate, Long>>> bXp = Collections.emptyMap();

    private static Pair<List<MMOTopVoteInstance>, Date> dW() throws Exception {
        String string;
        Date date = new Date(System.currentTimeMillis());
        LinkedList<MMOTopVoteInstance> linkedList = new LinkedList<MMOTopVoteInstance>();
        URL uRL = new URL(Config.MMO_TOP_VOTES_LINK);
        URLConnection uRLConnection = uRL.openConnection();
        uRLConnection.addRequestProperty("Host", uRL.getHost());
        uRLConnection.addRequestProperty("Accept", "*/*");
        uRLConnection.addRequestProperty("Connection", "close");
        uRLConnection.addRequestProperty("User-Agent", "MMOTop");
        uRLConnection.setConnectTimeout(10000);
        Object object = uRLConnection.getInputStream();
        Object object2 = null;
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream((InputStream)object);
             InputStreamReader inputStreamReader = new InputStreamReader((InputStream)bufferedInputStream, StandardCharsets.UTF_8);){
            string = IOUtils.toString(inputStreamReader);
        }
        catch (Throwable throwable) {
            object2 = throwable;
            throw throwable;
        }
        finally {
            if (object != null) {
                if (object2 != null) {
                    try {
                        ((InputStream)object).close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)object2).addSuppressed(throwable);
                    }
                } else {
                    ((InputStream)object).close();
                }
            }
        }
        try {
            object = uRLConnection.getHeaderField("Date");
            if (object != null && !((String)(object = ((String)object).trim())).isEmpty()) {
                date = bXo.parse((String)object);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        object = bXm.matcher(string);
        while (((Matcher)object).find()) {
            object2 = new MMOTopVoteInstance(Math.abs(Long.parseLong(((Matcher)object).group(1))), bXn.parse(((Matcher)object).group(2)), ((Matcher)object).group(3), ((Matcher)object).group(4), MMOTopVoteType.getTypeByTypeId(Integer.parseInt(((Matcher)object).group(5))));
            linkedList.add((MMOTopVoteInstance)object2);
        }
        return Pair.of((List<MMOTopVoteInstance>)linkedList, date);
    }

    private static List<MMOTopVoteInstance> a(long l, long l2, boolean bl) throws Exception {
        LinkedList<MMOTopVoteInstance> linkedList = new LinkedList<MMOTopVoteInstance>();
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT `voteId`, `timestamp`, `voterIp`, `voterName`, `voteType` FROM `mmotop_votes` WHERE  `timestamp` >= ? AND `timestamp` < ? AND `isRewarded` = ? ORDER BY `timestamp` DESC");){
            preparedStatement.setLong(1, l);
            preparedStatement.setLong(2, l2);
            preparedStatement.setInt(3, bl ? 1 : 0);
            try (ResultSet resultSet = preparedStatement.executeQuery();){
                while (resultSet.next()) {
                    linkedList.add(new MMOTopVoteInstance(resultSet.getLong("voteId"), resultSet.getLong("timestamp"), resultSet.getString("voterIp"), resultSet.getString("voterName"), MMOTopVoteType.getTypeByTypeId(resultSet.getInt("voteType"))));
                }
            }
        }
        return linkedList;
    }

    private static void a(List<MMOTopVoteInstance> list, boolean bl) throws Exception {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO `mmotop_votes` (`voteId`, `timestamp`, `voterIp`, `voterName`, `voteType`, `isRewarded`) VALUES (?, ?, ?, ?, ?, ?)");){
            for (MMOTopVoteInstance mMOTopVoteInstance : list) {
                preparedStatement.setLong(1, mMOTopVoteInstance.bXq);
                preparedStatement.setLong(2, mMOTopVoteInstance.timestamp);
                preparedStatement.setString(3, mMOTopVoteInstance.bXr);
                preparedStatement.setString(4, mMOTopVoteInstance.bXs);
                preparedStatement.setInt(5, mMOTopVoteInstance.bXt.getTypeId());
                preparedStatement.setInt(6, bl ? 1 : 0);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    private static boolean a(Player player, MMOTopVoteInstance mMOTopVoteInstance) {
        List<Pair<ItemTemplate, Long>> list = bXp.get((Object)mMOTopVoteInstance.bXt);
        if (list == null) {
            return false;
        }
        for (Pair<ItemTemplate, Long> pair : list) {
            ItemFunctions.addItem((Playable)player, pair.getKey().getItemId(), (long)pair.getValue(), true);
        }
        player.sendMessage(player.isLangRus() ? "\u0412\u0430\u043c \u0431\u044b\u043b\u043e \u043d\u0430\u0447\u0438\u0441\u043b\u0435\u043d\u043e \u0432\u043e\u0437\u043d\u0430\u0433\u0440\u0430\u0436\u0434\u0435\u043d\u0438\u0435 \u0437\u0430 \u0433\u043e\u043b\u043e\u0441\u043e\u0432\u0430\u043d\u0438\u0435 \u0432 \u0440\u0435\u0439\u0442\u0438\u043d\u0433\u0435 MMOTop.ru" : "You were rewarded for a vote at MMOTop.ru");
        return true;
    }

    private static List<MMOTopVoteInstance> m(List<MMOTopVoteInstance> list) {
        Object object;
        Object object2;
        HashMap<String, MMOTopVoteInstance> hashMap = new HashMap<String, MMOTopVoteInstance>();
        LinkedList<MMOTopVoteInstance> linkedList = new LinkedList<MMOTopVoteInstance>();
        for (MMOTopVoteInstance object32 : list) {
            hashMap.put(object32.bXs.toLowerCase(), object32);
        }
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            object2 = player.getName().toLowerCase();
            object = (MMOTopVoteInstance)hashMap.get(object2);
            if (object == null) continue;
            if (MMOTopVote.a(player, (MMOTopVoteInstance)object)) {
                linkedList.add((MMOTopVoteInstance)object);
            }
            hashMap.remove(object2);
        }
        ArrayList arrayList = new ArrayList(hashMap.values());
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            List<Pair<ItemTemplate, Long>> list2;
            object2 = (MMOTopVoteInstance)iterator.next();
            object = ((MMOTopVoteInstance)object2).bXs;
            if (!Util.isMatchingRegexp((String)object, Config.CNAME_TEMPLATE) || ((String)object).length() > Config.CNAME_MAXLEN) continue;
            linkedList.add((MMOTopVoteInstance)object2);
            int n = CharacterDAO.getInstance().getObjectIdByName((String)object);
            if (n <= 0 || (list2 = bXp.get((Object)((MMOTopVoteInstance)object2).bXt)) == null) continue;
            for (Pair<ItemTemplate, Long> pair : list2) {
                DelayedItemsManager.getInstance().addDelayed(n, pair.getLeft().getItemId(), Math.toIntExact(pair.getRight()), 0, "MMOTop Reward Item " + pair.getLeft().toString() + "(" + pair.getRight() + ") " + System.currentTimeMillis());
            }
        }
        return linkedList;
    }

    private static void dX() throws Exception {
        Pair<List<MMOTopVoteInstance>, Date> pair = MMOTopVote.dW();
        List<MMOTopVoteInstance> list = pair.getLeft();
        Date date = pair.getRight();
        long l = (date.getTime() - TimeUnit.DAYS.toMillis(Config.MMO_TOP_VOTE_WINDOW_DAYS)) / 1000L;
        long l2 = (date.getTime() - TimeUnit.HOURS.toMillis(Config.MMO_TOP_VOTE_REWARD_HOURS)) / 1000L;
        List<MMOTopVoteInstance> list2 = MMOTopVote.a(l, l2, false);
        List<MMOTopVoteInstance> list3 = MMOTopVote.a(l, l2, true);
        LinkedList<MMOTopVoteInstance> linkedList = new LinkedList<MMOTopVoteInstance>();
        LinkedList<MMOTopVoteInstance> linkedList2 = new LinkedList<MMOTopVoteInstance>();
        block0: for (MMOTopVoteInstance mMOTopVoteInstance : list) {
            if (mMOTopVoteInstance.timestamp < l || mMOTopVoteInstance.timestamp >= l2) continue;
            for (MMOTopVoteInstance mMOTopVoteInstance2 : list3) {
                if (mMOTopVoteInstance.bXq == mMOTopVoteInstance2.bXq && mMOTopVoteInstance2.timestamp == mMOTopVoteInstance.timestamp) continue block0;
            }
            boolean bl = true;
            for (MMOTopVoteInstance mMOTopVoteInstance3 : list2) {
                if (mMOTopVoteInstance.bXq != mMOTopVoteInstance3.bXq || mMOTopVoteInstance3.timestamp != mMOTopVoteInstance.timestamp) continue;
                bl = false;
                linkedList.add(mMOTopVoteInstance);
                break;
            }
            if (!bl) continue;
            linkedList2.add(mMOTopVoteInstance);
        }
        MMOTopVote.a(linkedList2, false);
        List<MMOTopVoteInstance> list4 = MMOTopVote.m(linkedList);
        MMOTopVote.a(list4, true);
        sQ.info("MMOTopVote: Rewarded {} voter(s).", (Object)list4.size());
    }

    public static void main(String ... stringArray) throws Exception {
        SkillTable.getInstance().load();
        ItemParser.getInstance().load();
        Config.MMO_TOP_VOTES_LINK = "https://mmotop.ru/votes/10ea26db579be962293082dd80195c8c1b5380ae.txt?23fd08b37db58fb1e604ede3446fa0fd";
        Map<MMOTopVoteType, List<Pair<ItemTemplate, Long>>> map = MMOTopVote.J("NORMAL:57/100;SMS10:6673/1;SMS50:6673/5;SMS100:6673/10;SMS500:6673/50");
        Pair<List<MMOTopVoteInstance>, Date> pair = MMOTopVote.dW();
        System.currentTimeMillis();
    }

    private static Map<MMOTopVoteType, List<Pair<ItemTemplate, Long>>> J(String string) {
        LinkedHashMap<MMOTopVoteType, List<Pair<ItemTemplate, Long>>> linkedHashMap = new LinkedHashMap<MMOTopVoteType, List<Pair<ItemTemplate, Long>>>();
        StringTokenizer stringTokenizer = new StringTokenizer(string.trim(), ";");
        try {
            while (stringTokenizer.hasMoreTokens()) {
                String string2;
                String string3 = stringTokenizer.nextToken();
                int n = string3.indexOf(58);
                if (n <= 0) {
                    throw new IllegalArgumentException("Can't parse reward \"" + string3 + "\"");
                }
                String string4 = string3.substring(0, n).trim();
                MMOTopVoteType mMOTopVoteType = MMOTopVoteType.Unknown;
                mMOTopVoteType = StringUtils.isNumeric(string4) ? MMOTopVoteType.getTypeByTypeId(Integer.parseInt(string4)) : MMOTopVoteType.valueOf(string4.toUpperCase());
                if (mMOTopVoteType == null || mMOTopVoteType == MMOTopVoteType.Unknown) {
                    throw new IllegalArgumentException("Unknown vote type \"" + string4 + "\"");
                }
                LinkedList<Pair<ItemTemplate, Long>> linkedList = (LinkedList<Pair<ItemTemplate, Long>>)linkedHashMap.get((Object)mMOTopVoteType);
                if (linkedList == null) {
                    linkedList = new LinkedList<Pair<ItemTemplate, Long>>();
                    linkedHashMap.put(mMOTopVoteType, linkedList);
                }
                if ((string2 = string3.substring(n + 1).trim()).isEmpty()) continue;
                n = StringUtils.indexOfAny((CharSequence)string2, ",/\\");
                if (n <= 0) {
                    throw new IllegalArgumentException("Can't parse reward \"" + string3 + "\"");
                }
                int n2 = Integer.parseInt(string2.substring(0, n).trim());
                long l = Long.parseLong(string2.substring(n + 1).trim());
                ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n2);
                linkedList.add(Pair.of(itemTemplate, l));
            }
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("Can't parse rewards \"" + string + "\"", exception);
        }
        return linkedHashMap;
    }

    private static void e() {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();){
            GameServer.getInstance().getDbmsStructureSynchronizer(connection).synchronize("mmotop_votes");
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onLoad() {
        if (Config.MMO_TOP_CRON_TASK != null && !Config.MMO_TOP_CRON_TASK.isEmpty() && Config.MMO_TOP_VOTES_LINK != null && !Config.MMO_TOP_VOTES_LINK.isEmpty()) {
            MMOTopVote.e();
            bXp = MMOTopVote.J(Config.MMO_TOP_REWARD);
            new SchedulingPatternTask(Config.MMO_TOP_CRON_TASK){

                @Override
                protected void runTask() {
                    try {
                        MMOTopVote.dX();
                    }
                    catch (Exception exception) {
                        sQ.error(exception.getMessage(), exception);
                    }
                }
            };
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static class MMOTopVoteInstance {
        private final long bXq;
        private final long timestamp;
        private final String bXr;
        private final String bXs;
        private final MMOTopVoteType bXt;

        private MMOTopVoteInstance(long l, Date date, String string, String string2, MMOTopVoteType mMOTopVoteType) {
            this.bXq = l;
            this.timestamp = date.getTime() / 1000L;
            this.bXr = string;
            this.bXs = string2;
            this.bXt = mMOTopVoteType;
        }

        private MMOTopVoteInstance(long l, long l2, String string, String string2, MMOTopVoteType mMOTopVoteType) {
            this.bXq = l;
            this.timestamp = l2;
            this.bXr = string;
            this.bXs = string2;
            this.bXt = mMOTopVoteType;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            MMOTopVoteInstance mMOTopVoteInstance = (MMOTopVoteInstance)object;
            return this.bXq == mMOTopVoteInstance.bXq;
        }

        public int hashCode() {
            return Objects.hash(this.bXq);
        }

        public String toString() {
            return "MMOTopVoteInstance{voteId=" + this.bXq + ", timestamp='" + this.timestamp + '\'' + ", voterIp='" + this.bXr + '\'' + ", userName='" + this.bXs + '\'' + ", voteType=" + (Object)((Object)this.bXt) + '}';
        }
    }

    static enum MMOTopVoteType {
        Unknown(-1),
        NORMAL(1),
        SMS10(2),
        SMS50(3),
        SMS100(4),
        SMS500(5);

        private static final Map<Integer, MMOTopVoteType> bXu;
        private final int bXv;

        private MMOTopVoteType(int n2) {
            this.bXv = n2;
        }

        public static MMOTopVoteType getTypeByTypeId(int n) {
            return bXu.getOrDefault(n, Unknown);
        }

        public int getTypeId() {
            return this.bXv;
        }

        static {
            bXu = new HashMap<Integer, MMOTopVoteType>();
            for (MMOTopVoteType mMOTopVoteType : MMOTopVoteType.values()) {
                bXu.put(mMOTopVoteType.getTypeId(), mMOTopVoteType);
            }
        }
    }
}

