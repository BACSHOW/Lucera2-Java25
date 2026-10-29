/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class HennaInfo
extends L2GameServerPacket {
    private final Henna[] ajp = new Henna[3];
    private final int afh;
    private final int afi;
    private final int afk;
    private final int afg;
    private final int afl;
    private final int afj;
    private int _count = 0;
    private int ajq;

    public HennaInfo(Player player) {
        for (int i = 0; i < 3; ++i) {
            l2.gameserver.templates.Henna henna = player.getHenna(i + 1);
            if (henna == null) continue;
            this.ajp[this._count++] = new Henna(henna.getSymbolId(), henna.isForThisClass(player));
        }
        this.afh = player.getHennaStatSTR();
        this.afi = player.getHennaStatCON();
        this.afk = player.getHennaStatDEX();
        this.afg = player.getHennaStatINT();
        this.afl = player.getHennaStatWIT();
        this.afj = player.getHennaStatMEN();
        this.ajq = player.getLevel() < 40 ? 2 : 3;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(228);
        this.writeC(this.afg);
        this.writeC(this.afh);
        this.writeC(this.afi);
        this.writeC(this.afj);
        this.writeC(this.afk);
        this.writeC(this.afl);
        this.writeD(this.ajq);
        this.writeD(this._count);
        for (int i = 0; i < this._count; ++i) {
            this.writeD(this.ajp[i]._symbolId);
            this.writeD(this.ajp[i].ajr ? this.ajp[i]._symbolId : 0);
        }
    }

    private static class Henna {
        private int _symbolId;
        private boolean ajr;

        public Henna(int n, boolean bl) {
            this._symbolId = n;
            this.ajr = bl;
        }
    }
}

