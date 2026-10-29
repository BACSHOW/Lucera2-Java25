/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExDuelUpdateUserInfo
extends L2GameServerPacket {
    private String _name;
    private int afs;
    private int class_id;
    private int level;
    private int curHp;
    private int maxHp;
    private int curMp;
    private int maxMp;
    private int curCp;
    private int maxCp;

    public ExDuelUpdateUserInfo(Player player) {
        this._name = player.getName();
        this.afs = player.getObjectId();
        this.class_id = player.getClassId().getId();
        this.level = player.getLevel();
        this.curHp = (int)player.getCurrentHp();
        this.maxHp = player.getMaxHp();
        this.curMp = (int)player.getCurrentMp();
        this.maxMp = player.getMaxMp();
        this.curCp = (int)player.getCurrentCp();
        this.maxCp = player.getMaxCp();
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(79);
        this.writeS(this._name);
        this.writeD(this.afs);
        this.writeD(this.class_id);
        this.writeD(this.level);
        this.writeD(this.curHp);
        this.writeD(this.maxHp);
        this.writeD(this.curMp);
        this.writeD(this.maxMp);
        this.writeD(this.curCp);
        this.writeD(this.maxCp);
    }
}

