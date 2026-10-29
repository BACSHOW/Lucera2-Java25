/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExFishingHpRegen
extends L2GameServerPacket {
    private int _time;
    private int agG;
    private int agH;
    private int agI;
    private int agJ;
    private int agK;
    private int agL;
    private int char_obj_id;

    public ExFishingHpRegen(Creature creature, int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        this.char_obj_id = creature.getObjectId();
        this._time = n;
        this.agG = n2;
        this.agH = n3;
        this.agJ = n4;
        this.agI = n5;
        this.agK = n6;
        this.agL = n7;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(22);
        this.writeD(this.char_obj_id);
        this.writeD(this._time);
        this.writeD(this.agG);
        this.writeC(this.agH);
        this.writeC(this.agJ);
        this.writeC(this.agI);
        this.writeD(this.agK);
        this.writeC(this.agL);
    }
}

