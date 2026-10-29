/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class StopMove
extends L2GameServerPacket {
    private final int CS;
    private final int _x;
    private final int _y;
    private final int Di;
    private final int amW;

    public StopMove(Creature creature) {
        this.CS = creature.getObjectId();
        this._x = creature.getX();
        this._y = creature.getY();
        this.Di = creature.getZ();
        this.amW = creature.getHeading();
    }

    public StopMove(int n, Location location) {
        this.CS = n;
        this._x = location.x;
        this._y = location.y;
        this.Di = location.z;
        this.amW = location.h;
    }

    @Override
    protected final void writeImpl() {
        if (this.getClient() != null && this.getClient().getActiveChar() != null && this.getClient().getActiveChar().getObjectId() == this.CS) {
            l2.gameserver.utils.MovementTelemetryLogger.logS2C(this.getClient().getActiveChar(), "StopMove", String.format("Loc=(%d,%d,%d, h=%d)", this._x, this._y, this.Di, this.amW));
        }
        this.writeC(71);
        this.writeD(this.CS);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
        this.writeD(this.amW);
    }
}

