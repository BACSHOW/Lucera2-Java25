/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.BoatHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.utils.Location;

public class RequestMoveToLocationInVehicle
extends L2GameClientPacket {
    private Location _pos = new Location();
    private Location acg = new Location();
    private int ach;

    @Override
    protected void readImpl() {
        this.ach = this.readD();
        this._pos.x = this.readD();
        this._pos.y = this.readD();
        this._pos.z = this.readD();
        this.acg.x = this.readD();
        this.acg.y = this.readD();
        this.acg.z = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Boat boat = BoatHolder.getInstance().getBoat(this.ach);
        if (boat == null) {
            player.sendActionFailed();
            return;
        }
        boat.moveInBoat(player, this.acg, this._pos);
    }
}

