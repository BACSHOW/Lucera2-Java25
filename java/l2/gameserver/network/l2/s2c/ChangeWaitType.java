/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ChangeWaitType
extends L2GameServerPacket {
    private int CS;
    private int abv;
    private int _x;
    private int _y;
    private int Di;
    public static final int WT_SITTING = 0;
    public static final int WT_STANDING = 1;
    public static final int WT_START_FAKEDEATH = 2;
    public static final int WT_STOP_FAKEDEATH = 3;

    public ChangeWaitType(Creature creature, int n) {
        this.CS = creature.getObjectId();
        this.abv = n;
        this._x = creature.getX();
        this._y = creature.getY();
        this.Di = creature.getZ();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(47);
        this.writeD(this.CS);
        this.writeD(this.abv);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
    }
}

