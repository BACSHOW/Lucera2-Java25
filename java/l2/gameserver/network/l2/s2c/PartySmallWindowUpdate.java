/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PartySmallWindowUpdate
extends L2GameServerPacket {
    private int afs;
    private int class_id;
    private int level;
    private int curCp;
    private int maxCp;
    private int curHp;
    private int maxHp;
    private int curMp;
    private int maxMp;
    private String akw;

    public PartySmallWindowUpdate(Player player) {
        this.afs = player.getObjectId();
        this.akw = player.getName();
        this.curCp = (int)player.getCurrentCp();
        this.maxCp = player.getMaxCp();
        this.curHp = (int)player.getCurrentHp();
        this.maxHp = player.getMaxHp();
        this.curMp = (int)player.getCurrentMp();
        this.maxMp = player.getMaxMp();
        this.level = player.getLevel();
        this.class_id = player.getClassId().getId();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(82);
        this.writeD(this.afs);
        this.writeS(this.akw);
        this.writeD(this.curCp);
        this.writeD(this.maxCp);
        this.writeD(this.curHp);
        this.writeD(this.maxHp);
        this.writeD(this.curMp);
        this.writeD(this.maxMp);
        this.writeD(this.level);
        this.writeD(this.class_id);
    }
}

