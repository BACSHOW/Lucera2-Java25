/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm;

import java.nio.ByteBuffer;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SendablePacket
extends l2.commons.net.nio.SendablePacket<AuthServerCommunication> {
    private static final Logger _log = LoggerFactory.getLogger(SendablePacket.class);

    @Override
    public AuthServerCommunication getClient() {
        return AuthServerCommunication.getInstance();
    }

    @Override
    protected ByteBuffer getByteBuffer() {
        return this.getClient().getWriteBuffer();
    }

    @Override
    public boolean write() {
        try {
            this.writeImpl();
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
        return true;
    }

    protected abstract void writeImpl();
}

