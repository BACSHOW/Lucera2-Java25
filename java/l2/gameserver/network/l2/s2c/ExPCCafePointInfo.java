/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPCCafePointInfo
extends L2GameServerPacket {
    private int ahc;
    private int ahd;
    private int ahe;
    private int Ji;
    private int ahf;

    public ExPCCafePointInfo(Player player, int n, int n2, int n3, int n4) {
        this.Ji = player.getPcBangPoints();
        this.ahc = n;
        this.ahd = n2;
        this.ahe = n3;
        this.ahf = n4;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(49);
        this.writeD(this.Ji);
        this.writeD(this.ahc);
        this.writeC(this.ahd);
        this.writeD(this.ahf);
        this.writeC(this.ahe);
    }
}

