/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm;

import java.nio.ByteBuffer;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;
import l2.authserver.network.gamecomm.gs2as.AuthRequest;
import l2.authserver.network.gamecomm.gs2as.ChangeAccessLevel;
import l2.authserver.network.gamecomm.gs2as.ChangeAccessLevelMulti;
import l2.authserver.network.gamecomm.gs2as.IGPwdCng;
import l2.authserver.network.gamecomm.gs2as.OnlineStatus;
import l2.authserver.network.gamecomm.gs2as.PingResponse;
import l2.authserver.network.gamecomm.gs2as.PlayerAuthRequest;
import l2.authserver.network.gamecomm.gs2as.PlayerInGame;
import l2.authserver.network.gamecomm.gs2as.PlayerLogout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketHandler {
    private static Logger _log = LoggerFactory.getLogger(PacketHandler.class);

    public static ReceivablePacket handlePacket(GameServer gameServer, ByteBuffer byteBuffer) {
        ReceivablePacket receivablePacket = null;
        int n = byteBuffer.get() & 0xFF;
        if (!gameServer.isAuthed()) {
            switch (n) {
                case 0: {
                    receivablePacket = new AuthRequest();
                    break;
                }
                default: {
                    _log.error("Received unknown packet: " + Integer.toHexString(n));
                    break;
                }
            }
        } else {
            switch (n) {
                case 1: {
                    receivablePacket = new OnlineStatus();
                    break;
                }
                case 2: {
                    receivablePacket = new PlayerAuthRequest();
                    break;
                }
                case 3: {
                    receivablePacket = new PlayerInGame();
                    break;
                }
                case 4: {
                    receivablePacket = new PlayerLogout();
                    break;
                }
                case 17: {
                    receivablePacket = new ChangeAccessLevel();
                    break;
                }
                case 18: {
                    receivablePacket = new ChangeAccessLevelMulti();
                    break;
                }
                case 160: {
                    receivablePacket = new IGPwdCng();
                    break;
                }
                case 255: {
                    receivablePacket = new PingResponse();
                    break;
                }
                default: {
                    _log.error("Received unknown packet: " + Integer.toHexString(n));
                }
            }
        }
        return receivablePacket;
    }
}

