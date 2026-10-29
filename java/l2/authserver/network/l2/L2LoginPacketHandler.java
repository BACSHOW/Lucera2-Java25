/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2;

import java.nio.ByteBuffer;
import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.c2s.AuthGameGuard;
import l2.authserver.network.l2.c2s.L2LoginClientPacket;
import l2.authserver.network.l2.c2s.RequestAuthLogin;
import l2.authserver.network.l2.c2s.RequestServerList;
import l2.authserver.network.l2.c2s.RequestServerLogin;
import l2.commons.net.nio.impl.IPacketHandler;
import l2.commons.net.nio.impl.ReceivablePacket;

public final class L2LoginPacketHandler
implements IPacketHandler<L2LoginClient> {
    @Override
    public ReceivablePacket<L2LoginClient> handlePacket(ByteBuffer byteBuffer, L2LoginClient l2LoginClient) {
        int n = byteBuffer.get() & 0xFF;
        L2LoginClientPacket l2LoginClientPacket = null;
        L2LoginClient.LoginClientState loginClientState = l2LoginClient.getState();
        switch (loginClientState) {
            case CONNECTED: {
                if (n != 7) break;
                l2LoginClientPacket = new AuthGameGuard();
                break;
            }
            case AUTHED_GG: {
                if (n != 0) break;
                l2LoginClientPacket = new RequestAuthLogin();
                break;
            }
            case AUTHED: {
                if (n == 5) {
                    l2LoginClientPacket = new RequestServerList();
                    break;
                }
                if (n != 2) break;
                l2LoginClientPacket = new RequestServerLogin();
            }
        }
        return l2LoginClientPacket;
    }
}

