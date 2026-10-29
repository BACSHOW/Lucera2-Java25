/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Summon;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class PetStatusUpdate
extends L2GameServerPacket {
    private int type;
    private int afs;
    private int level;
    private int akF;
    private int akE;
    private int maxHp;
    private int curHp;
    private int maxMp;
    private int curMp;
    private long akQ;
    private long akR;
    private long akS;
    private Location cH;
    private String title;

    public PetStatusUpdate(Summon summon) {
        this.type = summon.getSummonType();
        this.afs = summon.getObjectId();
        this.cH = summon.getLoc();
        this.title = summon.getTitle();
        this.curHp = (int)summon.getCurrentHp();
        this.maxHp = summon.getMaxHp();
        this.curMp = (int)summon.getCurrentMp();
        this.maxMp = summon.getMaxMp();
        this.akE = summon.getCurrentFed();
        this.akF = summon.getMaxFed();
        this.level = summon.getLevel();
        this.akQ = summon.getExp();
        this.akR = summon.getExpForThisLevel();
        this.akS = summon.getExpForNextLevel();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(181);
        this.writeD(this.type);
        this.writeD(this.afs);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeS(this.title);
        this.writeD(this.akE);
        this.writeD(this.akF);
        this.writeD(this.curHp);
        this.writeD(this.maxHp);
        this.writeD(this.curMp);
        this.writeD(this.maxMp);
        this.writeD(this.level);
        this.writeQ(this.akQ);
        this.writeQ(this.akR);
        this.writeQ(this.akS);
    }
}

