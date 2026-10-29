/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.Henna;

public class GMHennaInfo
extends L2GameServerPacket {
    private int _count;
    private int afh;
    private int afi;
    private int afk;
    private int afg;
    private int afl;
    private int afj;
    private final Henna[] aiD = new Henna[3];

    public GMHennaInfo(Player player) {
        this.afh = player.getHennaStatSTR();
        this.afi = player.getHennaStatCON();
        this.afk = player.getHennaStatDEX();
        this.afg = player.getHennaStatINT();
        this.afl = player.getHennaStatWIT();
        this.afj = player.getHennaStatMEN();
        int n = 0;
        for (int i = 0; i < 3; ++i) {
            Henna henna = player.getHenna(i + 1);
            if (henna == null) continue;
            this.aiD[n++] = henna;
        }
        this._count = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(234);
        this.writeC(this.afg);
        this.writeC(this.afh);
        this.writeC(this.afi);
        this.writeC(this.afj);
        this.writeC(this.afk);
        this.writeC(this.afl);
        this.writeD(3);
        this.writeD(this._count);
        for (int i = 0; i < this._count; ++i) {
            this.writeD(this.aiD[i].getSymbolId());
            this.writeD(this.aiD[i].getSymbolId());
        }
    }
}

