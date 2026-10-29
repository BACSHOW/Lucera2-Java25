/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.model.Player;
import l2.gameserver.network.authcomm.SendablePacket;

public class IGPwdCng
extends SendablePacket {
    private int oI;
    private String oY;
    private String oZ;
    private String pa;

    public IGPwdCng(Player player, String string, String string2) {
        this.oI = player.getObjectId();
        this.oY = player.getAccountName();
        this.oZ = string;
        this.pa = string2;
    }

    @Override
    protected void writeImpl() {
        this.writeC(160);
        this.writeD(this.oI);
        this.writeS(this.oY);
        this.writeS(this.oZ);
        this.writeS(this.pa);
    }
}

