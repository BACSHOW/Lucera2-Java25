/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2;

import java.nio.ByteBuffer;
import java.nio.Buffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import l2.commons.collections.LazyArrayList;
import l2.commons.dbutils.DbUtils;
import l2.commons.net.nio.impl.MMOClient;
import l2.commons.net.nio.impl.MMOConnection;
import l2.commons.net.nio.impl.SendablePacket;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.BypassManager;
import l2.gameserver.model.CharSelectInfoPackage;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.SessionKey;
import l2.gameserver.network.authcomm.gs2as.PlayerLogout;
import l2.gameserver.network.l2.CGModule;
import l2.gameserver.network.l2.GameCrypt;
import l2.gameserver.network.l2.SecondPasswordAuth;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.DeleteObject;
import l2.gameserver.network.l2.s2c.CharInfo;
import l2.gameserver.network.l2.s2c.NpcInfo;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.EtcStatusUpdate;
import l2.gameserver.network.l2.netty.disruptor.OutboundFloodQueue;
import l2.gameserver.network.l2.s2c.RequestNetPing;
import l2.gameserver.network.pfilter.PacketFilter;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import l2.gameserver.network.l2.fermata.FermataContext;
import org.apache.commons.lang3.mutable.MutableLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteOrder;

public final class GameClient
extends MMOClient<MMOConnection<GameClient>> {
    @FunctionalInterface
    public interface PacketBatchScope extends AutoCloseable {
        @Override
        void close();
    }

    private static final PacketBatchScope NOOP_BATCH_SCOPE = () -> {};
    private static final Logger _log = LoggerFactory.getLogger(GameClient.class);
    public static final String NO_IP = "?.?.?.?";
    private static final int NETTY_BUFFER_SIZE = 64 * 1024;
    private static final int MAX_NETTY_SERIALIZATION_DEPTH = 4;
    private static final ThreadLocal<ByteBuffer[]> NETTY_WRITE_BUFFERS = ThreadLocal.withInitial(() -> {
        ByteBuffer[] buffers = new ByteBuffer[MAX_NETTY_SERIALIZATION_DEPTH];
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = ByteBuffer.allocate(NETTY_BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        }
        return buffers;
    });
    private static final ThreadLocal<int[]> NETTY_SERIALIZATION_DEPTH = ThreadLocal.withInitial(() -> new int[1]);

    private NettyGameConnection _nettyConnection;
    private final PacketAuditTrail packetAuditTrail = new PacketAuditTrail();
    private volatile boolean _isCryptEnabled = false;
    public GameCrypt _crypt = null;
    public GameClientState _state;
    private String pm;
    private Player zT;
    private SessionKey ZG;
    private int ZH = 0;
    private int serverId;
    private List<Integer> ZI = new ArrayList<Integer>();
    private List<String> ZJ = null;
    private List<String> ZK = null;
    private Map<Class<? extends L2GameClientPacket>, MutableLong> ZL = new ConcurrentHashMap<Class<? extends L2GameClientPacket>, MutableLong>();
    private PacketFilter ZM;
    private final CGModule.CGContext ZN;
    private SecondPasswordAuth ZO;
    private boolean ZP = false;
    private int ZQ = 0;
    private int ZR = 0;
    public static int DEFAULT_PAWN_CLIPPING_RANGE = 2048;
    private int ZS = 0;
    private int ZT = 0;
    private int ZU = 0;
    private int ZV = 0;
    private ScheduledFuture<?> ZW;
    private final FermataContext _fermata = new FermataContext();

    public GameClient(MMOConnection<GameClient> mMOConnection) {
        super(mMOConnection);
        this._state = GameClientState.CONNECTED;
        this.ZN = CGModule.getInstance().initClient(this);
        this._crypt = CGModule.getInstance().initCrypt(this.ZN);
        this.ZN.setGameClient(this);
        this.ZM = PacketFilter.create(this);
    }

    public GameClient(NettyGameConnection nettyConnection) {
        super(null);
        this._nettyConnection = nettyConnection;
        this._state = GameClientState.CONNECTED;
        this.ZN = CGModule.getInstance().initClient(this);
        this._crypt = CGModule.getInstance().initCrypt(this.ZN);
        this.ZN.setGameClient(this);
        this.ZM = PacketFilter.create(this);
    }

    public NettyGameConnection getNettyConnection() {
        return this._nettyConnection;
    }

    public void setNettyConnection(NettyGameConnection conn) {
        this._nettyConnection = conn;
    }

    public void startPacketBatch() {
        if (this._nettyConnection != null) this._nettyConnection.startBatch();
    }

    public void endPacketBatch() {
        if (this._nettyConnection != null) this._nettyConnection.endBatch();
    }

    public PacketBatchScope openPacketBatch() {
        if (this._nettyConnection == null) return NOOP_BATCH_SCOPE;
        this.startPacketBatch();
        return this::endPacketBatch;
    }

    @Override
    public boolean isConnected() {
        if (this._nettyConnection != null) {
            return this._nettyConnection.isConnected();
        }
        return super.isConnected();
    }

    @Override
    public String getIpAddr() {
        if (this._nettyConnection != null) {
            return this._nettyConnection.getIpAddr();
        }
        return this.getConnection() != null ? ((MMOConnection)this.getConnection()).getIpAddr() : NO_IP;
    }

    @Override
    public void closeNow(boolean bl) {
        if (this._nettyConnection != null) {
            this.onDisconnection();
            this._nettyConnection.close();
            return;
        }
        super.closeNow(bl);
    }

    @Override
    protected void onDisconnection() {
        if (this.ZW != null) {
            this.ZW.cancel(true);
            this.ZW = null;
        }
        this.setState(GameClientState.DISCONNECTED);
        Player player = this.getActiveChar();
        this.setActiveChar(null);
        if (player != null) {
            player.setNetConnection(null);
            player.scheduleDelete();
        }
        CGModule.CGContext cGContext = this.getCGMContext();
        CGModule.getInstance().doneClient(cGContext);
        cGContext.setGameClient(null);
        if (this.getSessionKey() != null) {
            if (this.isAuthed()) {
                AuthServerCommunication.getInstance().removeAuthedClient(this.getLogin());
                AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(this.getLogin()));
            } else {
                AuthServerCommunication.getInstance().removeWaitingClient(this.getLogin());
            }
        }
    }

    @Override
    protected void onForcedDisconnection() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void markRestoredChar(int n) throws Exception {
        int n2 = this.getObjectIdForSlot(n);
        if (n2 < 0) {
            return;
        }
        if (this.zT != null && this.zT.getObjectId() == n2) {
            this.zT.setDeleteTimer(0);
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `characters` SET `deletetime`=0 WHERE `obj_id`=?");
            preparedStatement.setInt(1, n2);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("", exception);
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
    public void markToDeleteChar(int n) throws Exception {
        int n2 = this.getObjectIdForSlot(n);
        if (n2 < 0) {
            return;
        }
        if (this.zT != null && this.zT.getObjectId() == n2) {
            this.zT.setDeleteTimer((int)(System.currentTimeMillis() / 1000L));
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `characters` SET `deletetime`=? WHERE `obj_id`=?");
            preparedStatement.setLong(1, (int)(System.currentTimeMillis() / 1000L));
            preparedStatement.setInt(2, n2);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("data error on update deletime char:", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public void deleteCharacterInSlot(int n) throws Exception {
        if (this.zT != null) {
            return;
        }
        int n2 = this.getObjectIdForSlot(n);
        if (n2 == -1) {
            return;
        }
        this.deleteCharacterByCharacterObjId(n2);
    }

    public void deleteCharacterByCharacterObjId(int n) {
        CharacterDAO.getInstance().deleteCharacterDataByObjId(n);
    }

    public Player loadCharFromDisk(int n) {
        int n2 = this.getObjectIdForSlot(n);
        if (n2 == -1) {
            return null;
        }
        Player player = null;
        Player player2 = GameObjectsStorage.getPlayer(n2);
        if (player2 != null) {
            if (player2.isInOfflineMode() || player2.isLogoutStarted()) {
                player2.kick();
                return null;
            }
            player2.sendPacket((IStaticPacket)Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
            GameClient gameClient = player2.getNetConnection();
            if (gameClient != null) {
                gameClient.setActiveChar(null);
                gameClient.closeNow(false);
            }
            player2.setNetConnection(this);
            player = player2;
        }
        if (player == null) {
            player = Player.restore(n2);
        }
        if (player != null) {
            this.setActiveChar(player);
        } else {
            _log.warn("could not restore obj_id: " + n2 + " in slot:" + n);
        }
        return player;
    }

    public int getObjectIdForSlot(int n) {
        if (n < 0 || n >= this.ZI.size()) {
            _log.warn(this.getLogin() + " tried to modify Character in slot " + n + " but no characters exits at that slot.");
            return -1;
        }
        return this.ZI.get(n);
    }

    public int getSlotForObjectId(int n) {
        List<Integer> list = this.ZI;
        for (int i = 0; i < list.size(); ++i) {
            if (!Integer.valueOf(n).equals(list.get(i))) continue;
            return i;
        }
        return -1;
    }

    public SecondPasswordAuth getSecondPasswordAuth() {
        if (this.getLogin() == null || !Config.USE_SECOND_PASSWORD_AUTH) {
            return null;
        }
        if (this.ZO == null) {
            this.ZO = new SecondPasswordAuth(this.getLogin());
        }
        return this.ZO;
    }

    public boolean isSecondPasswordAuthed() {
        if (this.ZO == null) {
            return false;
        }
        return this.ZP;
    }

    public void setSecondPasswordAuthed(boolean bl) {
        this.ZP = bl;
    }

    public Player getActiveChar() {
        return this.zT;
    }

    public SessionKey getSessionKey() {
        return this.ZG;
    }

    public String getLogin() {
        return this.pm;
    }

    public void setLoginName(String string) {
        this.pm = string;
    }

    public void setActiveChar(Player player) {
        this.zT = player;
        if (player != null) {
            player.setNetConnection(this);
        }
    }

    public void setSessionId(SessionKey sessionKey) {
        this.ZG = sessionKey;
    }

    public void setCharSelection(CharSelectInfoPackage[] charSelectInfoPackageArray) {
        this.ZI.clear();
        for (CharSelectInfoPackage charSelectInfoPackage : charSelectInfoPackageArray) {
            int n = charSelectInfoPackage.getObjectId();
            this.ZI.add(n);
        }
    }

    public void setCharSelection(int n) {
        this.ZI.clear();
        this.ZI.add(n);
    }

    public int getRevision() {
        return this.ZH;
    }

    public void setRevision(int n) {
        this.ZH = n;
    }

    private volatile int _fermataCapabilities = l2.gameserver.network.l2.fermata.FermataCapabilities.MASK_NONE;

    public int getFermataCapabilities() {
        return this._fermataCapabilities;
    }

    public void setFermataCapabilities(int n) {
        this._fermataCapabilities = n;
    }

    public int getServerId() {
        return this.serverId;
    }

    public void setServerId(int n) {
        this.serverId = n;
    }

    public String getHwid() {
        return CGModule.getInstance().getHWIDText(this.getCGMContext());
    }

    private List<String> f(boolean bl) {
        if (bl) {
            if (this.ZK == null) {
                this.ZK = new LazyArrayList<String>();
            }
            return this.ZK;
        }
        if (this.ZJ == null) {
            this.ZJ = new LazyArrayList<String>();
        }
        return this.ZJ;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cleanBypasses(boolean bl) {
        List<String> list;
        List<String> list2 = list = this.f(bl);
        synchronized (list2) {
            list.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String encodeBypasses(String string, boolean bl) {
        List<String> list;
        List<String> list2 = list = this.f(bl);
        synchronized (list2) {
            return BypassManager.encode(string, list, bl);
        }
    }

    public BypassManager.DecodedBypass decodeBypass(String string) {
        BypassManager.BypassType bypassType = BypassManager.getBypassType(string, this.getActiveChar());
        boolean bl = bypassType == BypassManager.BypassType.ENCODED_BBS || bypassType == BypassManager.BypassType.SIMPLE_BBS;
        List<String> list = this.f(bl);
        if (bypassType == BypassManager.BypassType.ENCODED || bypassType == BypassManager.BypassType.ENCODED_BBS) {
            return BypassManager.decode(string, list, bl, this);
        }
        if (bypassType == BypassManager.BypassType.SIMPLE || bypassType == BypassManager.BypassType.SIMPLE_BBS) {
            return new BypassManager.DecodedBypass(string, bl, bypassType).trim();
        }
        _log.warn("Direct access to bypass: " + string + " / " + this.toString());
        return null;
    }

    public long getLastIncomePacketTimeStamp(Class<? extends L2GameClientPacket> clazz) {
        MutableLong mutableLong = this.ZL.get(clazz);
        if (mutableLong == null) {
            mutableLong = new MutableLong(0L);
            this.ZL.put(clazz, mutableLong);
        }
        return mutableLong.longValue();
    }

    public void setLastIncomePacketTimeStamp(Class<? extends L2GameClientPacket> clazz, long l) {
        MutableLong mutableLong = this.ZL.get(clazz);
        if (mutableLong == null) {
            mutableLong = new MutableLong(0L);
            this.ZL.put(clazz, mutableLong);
        }
        mutableLong.setValue(l);
    }

    public PacketFilter getPacketFilter() {
        return this.ZM;
    }

    @Override
    public boolean encrypt(ByteBuffer byteBuffer, int n) {
        if (this._nettyConnection != null && !this._isCryptEnabled) {
            return true;
        }
        this._crypt.encrypt(byteBuffer.array(), byteBuffer.position(), n);
        ((Buffer)byteBuffer).position(byteBuffer.position() + n);
        return true;
    }

    @Override
    public boolean decrypt(ByteBuffer byteBuffer, int n) {
        if (this._nettyConnection != null && !this._isCryptEnabled) {
            return true;
        }
        boolean bl = this._crypt.decrypt(byteBuffer.array(), byteBuffer.position(), n);
        return bl;
    }

    CGModule.CGContext getCGMContext() {
        return this.ZN;
    }

    public void sendPacket(L2GameServerPacket l2GameServerPacket) {
        if (!this.isConnected() || l2GameServerPacket == null) {
            return;
        }
        if (this._nettyConnection != null) {
            byte[] payload = this.serializeNettyPacket(l2GameServerPacket);
            if (payload != null) {
                this.sendSerializedNetty(l2GameServerPacket, payload);
            }
            return;
        }
        if (l2GameServerPacket instanceof NpcHtmlMessage) {
            ((NpcHtmlMessage) l2GameServerPacket).processHtml(this);
        }
        ((MMOConnection)((Object)this.getConnection())).sendPacket((SendablePacket<GameClient>)l2GameServerPacket);
    }

    private void sendSerializedNetty(L2GameServerPacket packet, byte[] payload) {
        if (packet instanceof DeleteObject) {
            this._nettyConnection.sendFlood(payload, OutboundFloodQueue.Kind.DELETE_OBJECT, ((DeleteObject) packet).getObjectId());
        } else if (packet instanceof CharInfo) {
            this._nettyConnection.sendFlood(payload, OutboundFloodQueue.Kind.CHAR_INFO, ((CharInfo) packet).getObjectId());
        } else if (packet instanceof NpcInfo) {
            this._nettyConnection.sendFlood(payload, OutboundFloodQueue.Kind.NPC_INFO, ((NpcInfo) packet).getObjectId());
        } else if (packet instanceof SkillList) {
            this._nettyConnection.sendFlood(payload, OutboundFloodQueue.Kind.SKILL_LIST, 0);
        } else if (packet instanceof EtcStatusUpdate) {
            this._nettyConnection.sendFlood(payload, OutboundFloodQueue.Kind.ETC_STATUS, 0);
        } else {
            this._nettyConnection.send(payload);
        }
    }

    private byte[] serializeNettyPacket(L2GameServerPacket packet) {
        if (packet instanceof NpcHtmlMessage) {
            ((NpcHtmlMessage) packet).processHtml(this);
        }

        int[] depthRef = NETTY_SERIALIZATION_DEPTH.get();
        int depth = depthRef[0]++;
        ByteBuffer buf = depth < MAX_NETTY_SERIALIZATION_DEPTH
            ? NETTY_WRITE_BUFFERS.get()[depth]
            : ByteBuffer.allocate(NETTY_BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        if (depth >= MAX_NETTY_SERIALIZATION_DEPTH) {
            _log.warn("[Netty Reentrancy] Serialization depth overflow (" + depth + ") for " + packet.getClass().getSimpleName() + " to " + this);
        }
        try {
            buf.clear();
            boolean ok = packet.writeTo(buf, this);
            buf.flip();
            int remaining = buf.remaining();
            int opcode = remaining > 0 ? buf.get(buf.position()) & 0xFF : -1;
            this.packetAuditTrail.record(packet.getClass().getSimpleName(), opcode, remaining, ok && remaining > 0);
            if (!ok || remaining <= 0) {
                _log.warn("[Netty S2C] Suppressed empty/failed packet: " + packet.getClass().getSimpleName() + " (ok=" + ok + ", rem=" + remaining + ") to " + this);
                return null;
            }

            byte[] payload = new byte[remaining];
            buf.get(payload);
            return payload;
        } finally {
            if (depthRef[0] > 0) {
                depthRef[0]--;
            } else {
                depthRef[0] = 0;
            }
        }
    }

    private void sendNettyBatch(List<L2GameServerPacket> packets) {
        if (packets == null || packets.isEmpty()) {
            return;
        }
        List<byte[]> payloads = new ArrayList<>(packets.size());
        for (L2GameServerPacket packet : packets) {
            if (packet != null) {
                byte[] payload = this.serializeNettyPacket(packet);
                if (payload != null) {
                    if (packet instanceof DeleteObject || packet instanceof CharInfo || packet instanceof NpcInfo
                        || packet instanceof SkillList || packet instanceof EtcStatusUpdate) {
                        this.sendSerializedNetty(packet, payload);
                    } else {
                        payloads.add(payload);
                    }
                }
            }
        }
        this._nettyConnection.sendBatch(payloads);
    }

    public void sendPacket(L2GameServerPacket ... l2GameServerPacketArray) {
        if (!this.isConnected() || l2GameServerPacketArray == null || l2GameServerPacketArray.length == 0) {
            return;
        }
        if (this._nettyConnection != null) {
            this.sendNettyBatch(java.util.Arrays.asList(l2GameServerPacketArray));
            return;
        }
        for (L2GameServerPacket l2GameServerPacket : l2GameServerPacketArray) {
            this.sendPacket(l2GameServerPacket);
        }
    }

    public void sendPackets(List<L2GameServerPacket> list) {
        if (!this.isConnected() || list == null || list.isEmpty()) {
            return;
        }
        if (this._nettyConnection != null) {
            this.sendNettyBatch(list);
            return;
        }
        for (L2GameServerPacket l2GameServerPacket : list) {
            this.sendPacket(l2GameServerPacket);
        }
    }

    public void close(L2GameServerPacket l2GameServerPacket) {
        if (!this.isConnected()) {
            return;
        }
        if (this._nettyConnection != null) {
            byte[] payload = null;
            if (l2GameServerPacket != null) {
                payload = this.serializeNettyPacket(l2GameServerPacket);
            }
            this.onDisconnection();
            this._nettyConnection.sendAndClose(payload == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(payload));
            return;
        }
        if (l2GameServerPacket instanceof NpcHtmlMessage) {
            NpcHtmlMessage npcHtmlMessage = (NpcHtmlMessage)l2GameServerPacket;
            npcHtmlMessage.processHtml(this);
        }
        ((MMOConnection)((Object)this.getConnection())).close(l2GameServerPacket);
    }

    public Object enableCrypt() {
        this._isCryptEnabled = true;
        return this._crypt.setKey(this._crypt.initKey(this.getCGMContext()));
    }

    public GameClientState getState() {
        return this._state;
    }

    public PacketAuditTrail getPacketAuditTrail() {
        return this.packetAuditTrail;
    }

    /**
     * Returns the per-client Fermata context. Always non-null; the context
     * is active only after the 0xFD handshake completes.
     */
    public FermataContext getFermata() {
        return this._fermata;
    }

    public boolean isStateIs(GameClientState gameClientState) {
        return this.getState() == gameClientState;
    }

    public void setState(GameClientState gameClientState) {
        this._state = gameClientState;
        switch (gameClientState) {
            case AUTHED: {
                if (this.getConnection() == null) break;
                this.onPing(0, 0, DEFAULT_PAWN_CLIPPING_RANGE);
                break;
            }
            case IN_GAME: {
                // Handshake-derived Fermata state is per-session; reset on
                // every fresh IN_GAME so a re-login cannot inherit stale
                // capability bits from a previous character.
                if (this._fermata != null) {
                    this._fermata.reset();
                }
                break;
            }
            case DISCONNECTED: {
                if (this._fermata != null) {
                    this._fermata.reset();
                }
                break;
            }
        }
    }

    public void onPacketReadFail() {
        if (this.ZQ++ >= Config.FAIL_PACKET_AMOUNT_LIMIT) {
            _log.warn("Too many client packet fails, connection closed : " + this);
            this.closeNow(true);
        }
    }

    public void onUnknownPacket() {
        if (this.ZR++ >= Config.UNKNOWN_PACKET_AMOUNT_LIMIT) {
            _log.warn("Too many client unknown packets, connection closed : " + this);
            this.closeNow(true);
        }
    }

    public String toString() {
        return (Object)((Object)this._state) + " IP: " + this.getIpAddr() + (this.pm == null ? "" : " Account: " + this.pm) + (this.zT == null ? "" : " Player : " + this.zT);
    }

    public void onPing(int n, int n2, int n3) {
        if (this.ZS == 0 || this.ZS == n) {
            long l = System.currentTimeMillis();
            long l2 = GameServer.getInstance().getServerStartTime();
            this.ZT = this.ZS > 0 ? (int)(l - l2 - (long)n) : 0;
            this.ZU = n2;
            this.ZV = n3;
            if (this.ZW == null) {
                this.ZW = ThreadPoolManager.getInstance().schedule(new PingTask(this), 30000L);
            }
        }
    }

    private final void doPing() {
        int n;
        long l = System.currentTimeMillis();
        long l2 = GameServer.getInstance().getServerStartTime();
        this.ZS = n = (int)(l - l2);
        this.sendPacket((L2GameServerPacket)new RequestNetPing(n));
        this.ZW = null;
    }

    public int getPing() {
        return this.ZT;
    }

    public int getFps() {
        return this.ZU;
    }

    public int getPawnClippingRange() {
        return this.ZV;
    }

    private static class PingTask
    extends RunnableImpl {
        private final GameClient ZY;

        private PingTask(GameClient gameClient) {
            this.ZY = gameClient;
        }

        @Override
        public void runImpl() throws Exception {
            if (this.ZY == null || !this.ZY.isConnected()) {
                return;
            }
            this.ZY.doPing();
        }
    }

    public static enum GameClientState {
        CONNECTED,
        AUTHED,
        IN_GAME,
        DISCONNECTED;

    }
}
