/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.utils.Location;

public class CannotMoveAnymoreInVehicle
extends L2GameClientPacket {
    private Location cH = new Location();
    private int aaH;

    @Override
    protected void readImpl() {
        this.aaH = this.readD();
        this.cH.x = this.readD();
        this.cH.y = this.readD();
        this.cH.z = this.readD();
        this.cH.h = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Boat boat = player.getBoat();
        if (boat != null && boat.getObjectId() == this.aaH) {
            player.setInBoatPosition(this.cH);
            player.setHeading(this.cH.h);
            player.broadcastPacket(boat.inStopMovePacket(player));
        }
    }
}

