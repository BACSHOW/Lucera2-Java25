/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.L2LoginClient;
import l2.commons.net.nio.impl.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class L2LoginServerPacket
extends SendablePacket<L2LoginClient> {
    private static final Logger _log = LoggerFactory.getLogger(L2LoginServerPacket.class);

    @Override
    public final boolean write() {
        try {
            this.writeImpl();
            return true;
        }
        catch (Exception exception) {
            _log.error("Client: " + this.getClient() + " - Failed writing: " + this.getClass().getSimpleName() + "!", exception);
            return false;
        }
    }

    protected abstract void writeImpl();
}

