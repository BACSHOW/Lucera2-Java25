/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.c2s;

import l2.authserver.network.l2.L2LoginClient;
import l2.commons.net.nio.impl.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class L2LoginClientPacket
extends ReceivablePacket<L2LoginClient> {
    private static Logger _log = LoggerFactory.getLogger(L2LoginClientPacket.class);

    @Override
    protected final boolean read() {
        try {
            this.readImpl();
            return true;
        }
        catch (Exception exception) {
            _log.error("", exception);
            return false;
        }
    }

    @Override
    public void run() {
        try {
            this.runImpl();
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
    }

    protected abstract void readImpl();

    protected abstract void runImpl() throws Exception;
}

