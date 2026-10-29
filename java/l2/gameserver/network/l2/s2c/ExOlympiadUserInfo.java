/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExOlympiadUserInfo
extends L2GameServerPacket {
    private int Sx;
    private int class_id;
    private int curHp;
    private int maxHp;
    private int curCp;
    private int maxCp;
    private int afs = 0;
    private String _name;

    public ExOlympiadUserInfo(Player player, int n) {
        this.Sx = n;
        this.afs = player.getObjectId();
        this.class_id = player.getClassId().getId();
        this._name = player.getName();
        this.curHp = (int)player.getCurrentHp();
        this.maxHp = player.getMaxHp();
        this.curCp = (int)player.getCurrentCp();
        this.maxCp = player.getMaxCp();
    }

    public ExOlympiadUserInfo(Player player) {
        this.Sx = player.getOlyParticipant().getSide();
        this.afs = player.getObjectId();
        this.class_id = player.getClassId().getId();
        this._name = player.getName();
        this.curHp = (int)player.getCurrentHp();
        this.maxHp = player.getMaxHp();
        this.curCp = (int)player.getCurrentCp();
        this.maxCp = player.getMaxCp();
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(41);
        this.writeC(this.Sx);
        this.writeD(this.afs);
        this.writeS(this._name);
        this.writeD(this.class_id);
        this.writeD(this.curHp);
        this.writeD(this.maxHp);
        this.writeD(this.curCp);
        this.writeD(this.maxCp);
    }
}

