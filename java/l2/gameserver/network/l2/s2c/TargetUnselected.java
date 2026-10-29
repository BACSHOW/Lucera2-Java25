/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.GameObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class TargetUnselected
extends L2GameServerPacket {
    private int _targetId;
    private Location cH;

    public TargetUnselected(GameObject gameObject) {
        this._targetId = gameObject.getObjectId();
        this.cH = gameObject.getLoc();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(42);
        this.writeD(this._targetId);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
    }
}

