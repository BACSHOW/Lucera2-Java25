/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopPvPPKService
extends Functions
implements ScriptFile {
    private static Logger LOG = LoggerFactory.getLogger(TopPvPPKService.class);
    private static String bYm = "scripts/services/";

    private static void a(TopRecordHolder topRecordHolder, Player player, NpcInstance npcInstance, String string) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, npcInstance).setFile(bYm + string);
        StringBuilder stringBuilder = new StringBuilder();
        Collection<TopRecord> collection = topRecordHolder.getTopRecords();
        for (TopRecord topRecord : collection) {
            stringBuilder.append(topRecord.formatHtml(player));
        }
        npcHtmlMessage.replace("%content%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void topPvP() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_PVP_PK_STATISTIC) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        TopPvPPKService.a(TopPvPRecordHolder.getInstance(), player, this.getNpc(), "top_pvp.htm");
    }

    public void topPK() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_PVP_PK_STATISTIC) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        TopPvPPKService.a(TopPKRecordHolder.getInstance(), player, this.getNpc(), "top_pk.htm");
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static class TopPKRecordHolder
    extends TopRecordHolder {
        private static final TopPKRecordHolder bYn = new TopPKRecordHolder();

        private TopPKRecordHolder() {
            super(Config.PVP_PK_STAT_RECORD_LIMIT, Config.PVP_PK_STAT_CACHE_UPDATE_INTERVAL);
        }

        public static TopPKRecordHolder getInstance() {
            return bYn;
        }

        @Override
        protected Collection<TopRecord> fetchTopOnlineRecords() {
            LinkedList<TopRecord> linkedList = new LinkedList<TopRecord>();
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                linkedList.add(new TopRecord(player.getObjectId(), player.getName(), player.getPkKills()));
            }
            return linkedList;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected Collection<TopRecord> fetchTopDbRecords() {
            LinkedList<TopRecord> linkedList = new LinkedList<TopRecord>();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("SELECT  `characters`.`obj_Id` AS `playerObjectId`,  `characters`.`char_name` AS `playerName`,  `characters`.`pkkills` AS `pkKills` FROM  `characters` ORDER BY  `characters`.`pkkills` DESC LIMIT ?");
                preparedStatement.setInt(1, this.getLimit());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    linkedList.add(new TopRecord(resultSet.getInt("playerObjectId"), resultSet.getString("playerName"), resultSet.getInt("pkKills")));
                }
            }
            catch (SQLException sQLException) {
                try {
                    LOG.error("Can't fetch top PK records.", sQLException);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            return linkedList;
        }
    }

    private static class TopPvPRecordHolder
    extends TopRecordHolder {
        private static final TopPvPRecordHolder bYo = new TopPvPRecordHolder();

        private TopPvPRecordHolder() {
            super(Config.PVP_PK_STAT_RECORD_LIMIT, Config.PVP_PK_STAT_CACHE_UPDATE_INTERVAL);
        }

        public static TopPvPRecordHolder getInstance() {
            return bYo;
        }

        @Override
        protected Collection<TopRecord> fetchTopOnlineRecords() {
            LinkedList<TopRecord> linkedList = new LinkedList<TopRecord>();
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                linkedList.add(new TopRecord(player.getObjectId(), player.getName(), player.getPvpKills()));
            }
            return linkedList;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected Collection<TopRecord> fetchTopDbRecords() {
            LinkedList<TopRecord> linkedList = new LinkedList<TopRecord>();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("SELECT  `characters`.`obj_Id` AS `playerObjectId`,  `characters`.`char_name` AS `playerName`,  `characters`.`pvpkills` AS `pvpKills` FROM  `characters` ORDER BY  `characters`.`pvpkills` DESC LIMIT ?");
                preparedStatement.setInt(1, this.getLimit());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    linkedList.add(new TopRecord(resultSet.getInt("playerObjectId"), resultSet.getString("playerName"), resultSet.getInt("pvpKills")));
                }
            }
            catch (SQLException sQLException) {
                try {
                    LOG.error("Can't fetch top PvP records.", sQLException);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            return linkedList;
        }
    }

    private static abstract class TopRecordHolder {
        private final AtomicReference<Pair<Long, Collection<TopRecord>>> bYp;
        private final int KH;
        private final long bYq;

        protected TopRecordHolder(int n, long l) {
            this.bYq = l;
            this.bYp = new AtomicReference(Pair.of(0L, Collections.emptyList()));
            this.KH = n;
        }

        protected int getLimit() {
            return this.KH;
        }

        protected abstract Collection<TopRecord> fetchTopOnlineRecords();

        protected abstract Collection<TopRecord> fetchTopDbRecords();

        protected Collection<TopRecord> fetchTopRecords() {
            Collection<TopRecord> collection = this.fetchTopOnlineRecords();
            Collection<TopRecord> collection2 = this.fetchTopDbRecords();
            ArrayList<TopRecord> arrayList = new ArrayList<TopRecord>(collection.size() + collection2.size());
            for (TopRecord topRecord : collection) {
                arrayList.add(topRecord);
            }
            for (TopRecord topRecord : collection2) {
                if (arrayList.contains(topRecord)) continue;
                arrayList.add(topRecord);
            }
            Collections.sort(arrayList);
            return new ArrayList<TopRecord>(arrayList.subList(0, Math.min(this.KH, arrayList.size())));
        }

        public Collection<TopRecord> getTopRecords() {
            Pair<Long, Collection<TopRecord>> pair;
            while ((pair = this.bYp.get()).getLeft() + this.bYq < System.currentTimeMillis()) {
                Collection<TopRecord> collection = this.fetchTopRecords();
                Pair<Long, Collection<TopRecord>> pair2 = Pair.of(System.currentTimeMillis(), collection);
                if (!this.bYp.compareAndSet(pair, pair2)) continue;
                return Collections.unmodifiableCollection(pair2.getRight());
            }
            return Collections.unmodifiableCollection(pair.getRight());
        }
    }

    private static class TopRecord
    implements Comparable<TopRecord> {
        private final int EQ;
        private final int _value;
        protected String _playerName;

        private TopRecord(int n, String string, int n2) {
            this.EQ = n;
            this._playerName = string;
            this._value = n2;
        }

        protected int getPlayerObjectId() {
            return this.EQ;
        }

        protected Player getPlayer() {
            return GameObjectsStorage.getPlayer(this.EQ);
        }

        public String getPlayerName() {
            return this._playerName;
        }

        public boolean isOnline() {
            Player player = this.getPlayer();
            return player != null && player.isOnline();
        }

        public int getTopValue() {
            return this._value;
        }

        public String formatHtml(Player player) {
            String string = StringHolder.getInstance().getNotNull(player, this.isOnline() ? "services.TopPvPPKService.TopRecord.RecordHtmlPlayerOnline" : "services.TopPvPPKService.TopRecord.RecordHtmlPlayerOffline");
            string = string.replace("%name%", this.getPlayerName());
            string = string.replace("%val%", String.valueOf(this.getTopValue()));
            return string;
        }

        @Override
        public int compareTo(TopRecord topRecord) {
            if (this.getPlayerObjectId() == topRecord.getPlayerObjectId()) {
                return 0;
            }
            return topRecord.getTopValue() - this.getTopValue();
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || !(object instanceof TopRecord)) {
                return false;
            }
            return this.getPlayerObjectId() == ((TopRecord)object).getPlayerObjectId();
        }
    }
}

