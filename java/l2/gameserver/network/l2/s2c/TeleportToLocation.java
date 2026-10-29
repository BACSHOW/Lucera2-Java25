/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.GameObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class TeleportToLocation
extends L2GameServerPacket {
    private int _targetId;
    private Location cH;

    public TeleportToLocation(GameObject gameObject, Location location) {
        this._targetId = gameObject.getObjectId();
        this.cH = location;
    }

    public TeleportToLocation(GameObject gameObject, int n, int n2, int n3) {
        this._targetId = gameObject.getObjectId();
        this.cH = new Location(n, n2, n3, gameObject.getHeading());
    }

    @Override
    protected final void writeImpl() {
        this.writeC(40);
        this.writeD(this._targetId);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z + Config.CLIENT_Z_SHIFT);
        this.writeD(0); // Interlude standard: isFastTeleport (0 = black screen, 1 = fast teleport)
    }
}

