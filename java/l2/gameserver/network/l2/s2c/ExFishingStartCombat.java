/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExFishingStartCombat
extends L2GameServerPacket {
    int _time;
    int _hp;
    int _lureType;
    int _deceptiveMode;
    int _mode;
    private int char_obj_id;

    public ExFishingStartCombat(Creature creature, int n, int n2, int n3, int n4, int n5) {
        this.char_obj_id = creature.getObjectId();
        this._time = n;
        this._hp = n2;
        this._mode = n3;
        this._lureType = n4;
        this._deceptiveMode = n5;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(21);
        this.writeD(this.char_obj_id);
        this.writeD(this._time);
        this.writeD(this._hp);
        this.writeC(this._mode);
        this.writeC(this._lureType);
        this.writeC(this._deceptiveMode);
    }
}

