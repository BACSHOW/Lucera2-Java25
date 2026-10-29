/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm;

import java.nio.ByteBuffer;
import l2.gameserver.network.authcomm.ReceivablePacket;
import l2.gameserver.network.authcomm.as2gs.AuthResponse;
import l2.gameserver.network.authcomm.as2gs.KickPlayer;
import l2.gameserver.network.authcomm.as2gs.LoginServerFail;
import l2.gameserver.network.authcomm.as2gs.NotifyPwdCngResult;
import l2.gameserver.network.authcomm.as2gs.PingRequest;
import l2.gameserver.network.authcomm.as2gs.PlayerAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketHandler {
    private static final Logger _log = LoggerFactory.getLogger(PacketHandler.class);

    public static ReceivablePacket handlePacket(ByteBuffer byteBuffer) {
        ReceivablePacket receivablePacket = null;
        int n = byteBuffer.get() & 0xFF;
        switch (n) {
            case 0: {
                receivablePacket = new AuthResponse();
                break;
            }
            case 1: {
                receivablePacket = new LoginServerFail();
                break;
            }
            case 2: {
                receivablePacket = new PlayerAuthResponse();
                break;
            }
            case 3: {
                receivablePacket = new KickPlayer();
                break;
            }
            case 161: {
                receivablePacket = new NotifyPwdCngResult();
                break;
            }
            case 255: {
                receivablePacket = new PingRequest();
                break;
            }
            default: {
                _log.error("Received unknown packet: " + Integer.toHexString(n));
            }
        }
        return receivablePacket;
    }
}

