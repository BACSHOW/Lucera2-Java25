/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class VehicleStart
extends L2GameServerPacket {
    private int CS;
    private int _state;

    public VehicleStart(Boat boat) {
        this.CS = boat.getObjectId();
        this._state = boat.getRunState();
    }

    @Override
    protected void writeImpl() {
        this.writeC(186);
        this.writeD(this.CS);
        this.writeD(this._state);
    }
}

