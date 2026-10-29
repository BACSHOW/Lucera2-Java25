/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class MonRaceInfo
extends L2GameServerPacket {
    private int ajJ;
    private int ajK;
    private NpcInstance[] ajL;
    private int[][] ajM;

    public MonRaceInfo(int n, int n2, NpcInstance[] npcInstanceArray, int[][] nArray) {
        this.ajJ = n;
        this.ajK = n2;
        this.ajL = npcInstanceArray;
        this.ajM = nArray;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(221);
        this.writeD(this.ajJ);
        this.writeD(this.ajK);
        this.writeD(8);
        for (int i = 0; i < 8; ++i) {
            this.writeD(this.ajL[i].getObjectId());
            this.writeD(this.ajL[i].getTemplate().npcId + 1000000);
            this.writeD(14107);
            this.writeD(181875 + 58 * (7 - i));
            this.writeD(-3566);
            this.writeD(12080);
            this.writeD(181875 + 58 * (7 - i));
            this.writeD(-3566);
            this.writeF(this.ajL[i].getColHeight());
            this.writeF(this.ajL[i].getColRadius());
            this.writeD(120);
            for (int j = 0; j < 20; ++j) {
                this.writeC(this.ajJ == 0 ? this.ajM[i][j] : 0);
            }
            this.writeD(0);
        }
    }
}

