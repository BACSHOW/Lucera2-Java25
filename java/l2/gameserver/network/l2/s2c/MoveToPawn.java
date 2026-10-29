/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class MoveToPawn
extends L2GameServerPacket {
    private int aed;
    private int _targetId;
    private int ajO;
    private int _x;
    private int _y;
    private int Di;

    public MoveToPawn(Creature creature, GameObject gameObject, int n) {
        this.aed = creature.getObjectId();
        this._targetId = gameObject.getObjectId();
        this.ajO = n;
        this._x = creature.getX();
        this._y = creature.getY();
        this.Di = creature.getZ();
    }

    @Override
    protected final void writeImpl() {
        if (this.getClient() != null && this.getClient().getActiveChar() != null && this.getClient().getActiveChar().getObjectId() == this.aed) {
            l2.gameserver.utils.MovementTelemetryLogger.logS2C(this.getClient().getActiveChar(), "MoveToPawn", String.format("TargetId=%d dist=%d Loc=(%d,%d,%d)", this._targetId, this.ajO, this._x, this._y, this.Di));
        }
        this.writeC(96);
        this.writeD(this.aed);
        this.writeD(this._targetId);
        this.writeD(this.ajO);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
    }
}

