/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.boat;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.s2c.GetOffVehicle;
import l2.gameserver.network.l2.s2c.GetOnVehicle;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MoveToLocationInVehicle;
import l2.gameserver.network.l2.s2c.StopMove;
import l2.gameserver.network.l2.s2c.StopMoveToLocationInVehicle;
import l2.gameserver.network.l2.s2c.ValidateLocationInVehicle;
import l2.gameserver.network.l2.s2c.VehicleCheckLocation;
import l2.gameserver.network.l2.s2c.VehicleDeparture;
import l2.gameserver.network.l2.s2c.VehicleInfo;
import l2.gameserver.network.l2.s2c.VehicleStart;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.utils.Location;

public class Vehicle
extends Boat {
    public Vehicle(int n, CharTemplate charTemplate) {
        super(n, charTemplate);
    }

    @Override
    public L2GameServerPacket startPacket() {
        return new VehicleStart(this);
    }

    @Override
    public L2GameServerPacket validateLocationPacket(Player player) {
        return new ValidateLocationInVehicle(player);
    }

    @Override
    public L2GameServerPacket checkLocationPacket() {
        return new VehicleCheckLocation(this);
    }

    @Override
    public L2GameServerPacket infoPacket() {
        return new VehicleInfo(this);
    }

    @Override
    public L2GameServerPacket movePacket() {
        return new VehicleDeparture(this, this.getDestination());
    }

    @Override
    public L2GameServerPacket inMovePacket(Player player, Location location, Location location2) {
        return new MoveToLocationInVehicle(player, this, location, location2);
    }

    @Override
    public L2GameServerPacket stopMovePacket() {
        return new StopMove(this);
    }

    @Override
    public L2GameServerPacket inStopMovePacket(Player player) {
        return new StopMoveToLocationInVehicle(player);
    }

    @Override
    public L2GameServerPacket getOnPacket(Player player, Location location) {
        return new GetOnVehicle(player, this, location);
    }

    @Override
    public L2GameServerPacket getOffPacket(Player player, Location location) {
        return new GetOffVehicle(player, this, location);
    }

    @Override
    public void oustPlayers() {
    }

    @Override
    public boolean isVehicle() {
        return true;
    }
}

