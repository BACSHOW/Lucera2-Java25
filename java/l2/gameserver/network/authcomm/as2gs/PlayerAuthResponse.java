/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.as2gs;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.ReceivablePacket;
import l2.gameserver.network.authcomm.SessionKey;
import l2.gameserver.network.authcomm.gs2as.PlayerInGame;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.LoginFail;
import l2.gameserver.network.l2.s2c.ServerClose;

public class PlayerAuthResponse
extends ReceivablePacket {
    private String account;
    private boolean oK;
    private int pe;
    private int pf;
    private int pg;
    private int ph;
    private int oN = Config.REQUEST_ID;

    @Override
    public void readImpl() {
        this.account = this.readS();
        boolean bl = this.oK = this.readC() == 1;
        if (this.oK) {
            this.pe = this.readD();
            this.pf = this.readD();
            this.pg = this.readD();
            this.ph = this.readD();
            if (this.getAvaliableBytes() > 0) {
                this.oN = this.readD();
            }
        }
    }

    @Override
    protected void runImpl() {
        SessionKey sessionKey = new SessionKey(this.pg, this.ph, this.pe, this.pf);
        GameClient gameClient = AuthServerCommunication.getInstance().removeWaitingClient(this.account);
        if (gameClient == null) {
            return;
        }
        if (this.oK && gameClient.getSessionKey().equals(sessionKey)) {
            Object object;
            gameClient.setAuthed(true);
            gameClient.setState(GameClient.GameClientState.AUTHED);
            gameClient.setServerId(this.oN);
            GameClient gameClient2 = AuthServerCommunication.getInstance().addAuthedClient(gameClient);
            if (!Config.ALLOW_MULILOGIN && gameClient2 != null) {
                gameClient2.setAuthed(false);
                object = gameClient2.getActiveChar();
                if (object != null) {
                    ((Player)object).sendPacket((IStaticPacket)Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
                    ((Player)object).logout();
                } else {
                    gameClient2.close(ServerClose.STATIC);
                }
            }
            this.sendPacket(new PlayerInGame(gameClient.getLogin()));
            object = new CharacterSelectionInfo(gameClient.getLogin(), gameClient.getSessionKey().playOkID1);
            gameClient.sendPacket((L2GameServerPacket)object);
            gameClient.setCharSelection(((CharacterSelectionInfo)object).getCharInfo());
        } else {
            gameClient.close(new LoginFail(LoginFail.ACCESS_FAILED_TRY_LATER));
        }
    }
}

