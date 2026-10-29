/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.instancemanager.BypassManager;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBypassToServerInLobby
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestBypassToServerInLobby.class);
    private String _bypass;

    @Override
    protected void readImpl() {
        this._bypass = this.readS();
    }

    @Override
    protected void runImpl() {
        GameClient gameClient = (GameClient)this.getClient();
        if (!gameClient.isAuthed() || gameClient.getActiveChar() != null) {
            return;
        }
        BypassManager.DecodedBypass decodedBypass = null;
        try {
            if (this._bypass.isEmpty() || (decodedBypass = gameClient.decodeBypass(this._bypass)) == null) {
                return;
            }
            if (Config.USE_SECOND_PASSWORD_AUTH && decodedBypass.bypass.startsWith("spa_")) {
                gameClient.getSecondPasswordAuth().getUI().handle(gameClient, decodedBypass.bypass.substring(4));
            }
        }
        catch (Exception exception) {
            _log.error("Bad RequestBypassToServer from " + ((GameClient)this.getClient()).toString() + ": " + decodedBypass != null ? decodedBypass.bypass : "direct[" + this._bypass + "]", exception);
        }
    }
}

