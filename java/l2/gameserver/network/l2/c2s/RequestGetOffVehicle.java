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

public class RequestGetOffVehicle
extends L2GameClientPacket {
    private int CS;
    private Location CD = new Location();

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.CD.x = this.readD();
        this.CD.y = this.readD();
        this.CD.z = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Boat boat = BoatHolder.getInstance().getBoat(this.CS);
        if (boat == null || boat.isMoving()) {
            player.sendActionFailed();
            return;
        }
        boat.oustPlayer(player, this.CD, false);
    }
}

