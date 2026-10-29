/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExGetBossRecord
extends L2GameServerPacket {
    private List<BossRecordInfo> agP;
    private int agQ;
    private int agR;

    public ExGetBossRecord(int n, int n2, List<BossRecordInfo> list) {
        this.agQ = n;
        this.agR = n2;
        this.agP = list;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(51);
        this.writeD(this.agQ);
        this.writeD(this.agR);
        this.writeD(this.agP.size());
        for (BossRecordInfo bossRecordInfo : this.agP) {
            this.writeD(bossRecordInfo._bossId);
            this.writeD(bossRecordInfo._points);
            this.writeD(bossRecordInfo._unk1);
        }
    }

    public static class BossRecordInfo {
        public int _bossId;
        public int _points;
        public int _unk1;

        public BossRecordInfo(int n, int n2, int n3) {
            this._bossId = n;
            this._points = n2;
            this._unk1 = n3;
        }
    }
}

