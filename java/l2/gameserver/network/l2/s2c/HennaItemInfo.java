/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.Henna;

public class HennaItemInfo
extends L2GameServerPacket {
    private int afh;
    private int afi;
    private int afk;
    private int afg;
    private int afl;
    private int afj;
    private long adQ;
    private Henna ajs;

    public HennaItemInfo(Henna henna, Player player) {
        this.ajs = henna;
        this.adQ = player.getAdena();
        this.afh = player.getSTR();
        this.afk = player.getDEX();
        this.afi = player.getCON();
        this.afg = player.getINT();
        this.afl = player.getWIT();
        this.afj = player.getMEN();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(227);
        this.writeD(this.ajs.getSymbolId());
        this.writeD(this.ajs.getDyeId());
        this.writeD((int)this.ajs.getDrawCount());
        this.writeD((int)this.ajs.getPrice());
        this.writeD(1);
        this.writeD((int)this.adQ);
        this.writeD(this.afg);
        this.writeC(this.afg + this.ajs.getStatINT());
        this.writeD(this.afh);
        this.writeC(this.afh + this.ajs.getStatSTR());
        this.writeD(this.afi);
        this.writeC(this.afi + this.ajs.getStatCON());
        this.writeD(this.afj);
        this.writeC(this.afj + this.ajs.getStatMEN());
        this.writeD(this.afk);
        this.writeC(this.afk + this.ajs.getStatDEX());
        this.writeD(this.afl);
        this.writeC(this.afl + this.ajs.getStatWIT());
    }
}

