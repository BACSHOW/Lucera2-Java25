/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;
import l2.gameserver.network.l2.GameClient;

public class PlayerAuthRequest
extends SendablePacket {
    private String account;
    private int playOkID1;
    private int playOkID2;
    private int loginOkID1;
    private int loginOkID2;

    public PlayerAuthRequest(GameClient gameClient) {
        this.account = gameClient.getLogin();
        this.playOkID1 = gameClient.getSessionKey().playOkID1;
        this.playOkID2 = gameClient.getSessionKey().playOkID2;
        this.loginOkID1 = gameClient.getSessionKey().loginOkID1;
        this.loginOkID2 = gameClient.getSessionKey().loginOkID2;
    }

    @Override
    protected void writeImpl() {
        this.writeC(2);
        this.writeS(this.account);
        this.writeD(this.playOkID1);
        this.writeD(this.playOkID2);
        this.writeD(this.loginOkID1);
        this.writeD(this.loginOkID2);
    }
}

