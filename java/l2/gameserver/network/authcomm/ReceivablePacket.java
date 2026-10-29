/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm;

import java.nio.ByteBuffer;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReceivablePacket
extends l2.commons.net.nio.ReceivablePacket<AuthServerCommunication> {
    private static final Logger _log = LoggerFactory.getLogger(ReceivablePacket.class);

    @Override
    public AuthServerCommunication getClient() {
        return AuthServerCommunication.getInstance();
    }

    @Override
    protected ByteBuffer getByteBuffer() {
        return this.getClient().getReadBuffer();
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

    protected void sendPacket(SendablePacket sendablePacket) {
        this.getClient().sendPacket(sendablePacket);
    }
}

