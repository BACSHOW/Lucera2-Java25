/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm;

import java.nio.ByteBuffer;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReceivablePacket
extends l2.commons.net.nio.ReceivablePacket<GameServer> {
    private static final Logger _log = LoggerFactory.getLogger(ReceivablePacket.class);
    protected GameServer _gs;
    protected ByteBuffer _buf;

    protected void setByteBuffer(ByteBuffer byteBuffer) {
        this._buf = byteBuffer;
    }

    @Override
    protected ByteBuffer getByteBuffer() {
        return this._buf;
    }

    protected void setClient(GameServer gameServer) {
        this._gs = gameServer;
    }

    @Override
    public GameServer getClient() {
        return this._gs;
    }

    public GameServer getGameServer() {
        return this.getClient();
    }

    @Override
    public final boolean read() {
        try {
            this.readImpl();
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
        return true;
    }

    @Override
    public final void run() {
        try {
            this.runImpl();
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
    }

    protected abstract void readImpl();

    protected abstract void runImpl();

    public void sendPacket(SendablePacket sendablePacket) {
        this.getGameServer().sendPacket(sendablePacket);
    }
}

