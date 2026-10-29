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

public class RequestGetOnVehicle
extends L2GameClientPacket {
    private int CS;
    private Location cH = new Location();

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.cH.x = this.readD();
        this.cH.y = this.readD();
        this.cH.z = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Boat boat = BoatHolder.getInstance().getBoat(this.CS);
        if (boat == null) {
            return;
        }
        player._stablePoint = boat.getCurrentWay().getReturnLoc();
        boat.addPlayer(player, this.cH);
    }
}

