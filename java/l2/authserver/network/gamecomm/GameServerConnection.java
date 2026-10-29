/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm;

import java.nio.ByteBuffer;
import java.nio.Buffer;
import java.nio.ByteOrder;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.authserver.Config;
import l2.authserver.ThreadPoolManager;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.SendablePacket;
import l2.authserver.network.gamecomm.as2gs.PingRequest;
import l2.commons.threading.RunnableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerConnection {
    private static final Logger _log = LoggerFactory.getLogger(GameServerConnection.class);
    final ByteBuffer readBuffer = ByteBuffer.allocate(65536).order(ByteOrder.LITTLE_ENDIAN);
    final Queue<SendablePacket> sendQueue = new ArrayDeque<SendablePacket>();
    final Lock sendLock = new ReentrantLock();
    final AtomicBoolean isPengingWrite = new AtomicBoolean();
    private final Selector oz;
    private final SelectionKey oB;
    private GameServer oC;
    private Future<?> oD;
    private int oE;

    public GameServerConnection(SelectionKey selectionKey) {
        this.oB = selectionKey;
        this.oz = selectionKey.selector();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendPacket(SendablePacket sendablePacket) {
        boolean bl;
        this.sendLock.lock();
        try {
            this.sendQueue.add(sendablePacket);
            bl = this.enableWriteInterest();
        }
        catch (CancelledKeyException cancelledKeyException) {
            return;
        }
        finally {
            this.sendLock.unlock();
        }
        if (bl) {
            this.oz.wakeup();
        }
    }

    protected boolean disableWriteInterest() throws CancelledKeyException {
        if (this.isPengingWrite.compareAndSet(true, false)) {
            this.oB.interestOps(this.oB.interestOps() & 0xFFFFFFFB);
            return true;
        }
        return false;
    }

    protected boolean enableWriteInterest() throws CancelledKeyException {
        if (!this.isPengingWrite.getAndSet(true)) {
            this.oB.interestOps(this.oB.interestOps() | 4);
            return true;
        }
        return false;
    }

    public void closeNow() {
        this.oB.interestOps(8);
        this.oz.wakeup();
    }

    public void onDisconnection() {
        try {
            this.stopPingTask();
            ((Buffer)this.readBuffer).clear();
            this.sendLock.lock();
            try {
                this.sendQueue.clear();
            }
            finally {
                this.sendLock.unlock();
            }
            this.isPengingWrite.set(false);
            if (this.oC != null && this.oC.isAuthed()) {
                _log.info("Connection with gameserver " + this.oC.getId() + " [" + this.oC.getName() + "] lost.");
                _log.info("Setting gameserver down. All proxies will be down as well.");
                this.oC.setDown();
            }
            this.oC = null;
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
    }

    ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

    GameServer getGameServer() {
        return this.oC;
    }

    void setGameServer(GameServer gameServer) {
        this.oC = gameServer;
    }

    public String getIpAddress() {
        return ((SocketChannel)this.oB.channel()).socket().getInetAddress().getHostAddress();
    }

    public void onPingResponse() {
        this.oE = 0;
    }

    public void startPingTask() {
        if (Config.GAME_SERVER_PING_DELAY == 0L) {
            return;
        }
        this.oD = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PingTask(), Config.GAME_SERVER_PING_DELAY, Config.GAME_SERVER_PING_DELAY);
    }

    public void stopPingTask() {
        if (this.oD != null) {
            this.oD.cancel(false);
            this.oD = null;
        }
    }

    private class PingTask
    extends RunnableImpl {
        private PingTask() {
        }

        @Override
        public void runImpl() {
            if (Config.GAME_SERVER_PING_RETRY > 0 && GameServerConnection.this.oE > Config.GAME_SERVER_PING_RETRY) {
                _log.warn("Gameserver " + GameServerConnection.this.oC.getId() + " [" + GameServerConnection.this.oC.getName() + "] : ping timeout!");
                GameServerConnection.this.closeNow();
                return;
            }
            GameServerConnection.this.oE++;
            GameServerConnection.this.sendPacket(new PingRequest());
        }
    }
}
