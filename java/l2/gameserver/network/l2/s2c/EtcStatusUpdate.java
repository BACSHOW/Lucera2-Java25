/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class EtcStatusUpdate
extends L2GameServerPacket {
    private int afI;
    private int afJ;
    private int afK;
    private int afL;
    private int afM;
    private int afN;
    private int afO;
    private int afP;

    public EtcStatusUpdate(Player player) {
        this.afI = player.getIncreasedForce();
        this.afJ = player.getWeightPenalty();
        this.afK = player.getMessageRefusal() || player.getNoChannel() != 0L || player.isBlockAll() ? 1 : 0;
        this.afL = player.isInDangerArea() ? 1 : 0;
        this.afM = player.getGradePenalty();
        this.afN = player.isCharmOfCourage() ? 1 : 0;
        this.afO = player.getDeathPenalty() == null ? 0 : player.getDeathPenalty().getLevel();
        this.afP = player.getConsumedSouls();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(243);
        this.writeD(this.afI);
        this.writeD(this.afJ);
        this.writeD(this.afK);
        this.writeD(this.afL);
        this.writeD(this.afM);
        this.writeD(this.afN);
        this.writeD(this.afO);
    }
}

