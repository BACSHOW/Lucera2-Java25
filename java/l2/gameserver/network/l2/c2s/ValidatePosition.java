/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.data.BoatHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.utils.Location;

public class ValidatePosition
extends L2GameClientPacket {
    private final Location cH = new Location();
    private int ach;
    private Location JW;
    private Location JX;

    @Override
    protected void readImpl() {
        this.cH.x = this.readD();
        this.cH.y = this.readD();
        this.cH.z = this.readD();
        this.cH.h = this.readD();
        this.ach = this.readD();
    }

    @Override
    protected void runImpl() {
        boolean bl;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isTeleporting() || player.isInObserverMode() || player.isOlyObserver()) {
            return;
        }
        this.JW = player.getLastClientPosition();
        this.JX = player.getLastServerPosition();
        if (this.JW == null) {
            this.JW = player.getLoc();
        }
        if (this.JX == null) {
            this.JX = player.getLoc();
        }
        if (player.getX() == 0 && player.getY() == 0 && player.getZ() == 0) {
            this.ad(player);
            return;
        }
        if (player.isInFlyingTransform()) {
            if (this.cH.x > -166168) {
                player.setTransformation(0);
                return;
            }
            if (this.cH.z <= 0 || this.cH.z >= 6000) {
                player.teleToLocation(player.getLoc().setZ(Math.min(5950, Math.max(50, this.cH.z))));
                return;
            }
        }
        double d = player.getDistance(this.cH.x, this.cH.y);
        double d2 = player.getDistance(this.cH.x, this.cH.y, this.cH.z);
        int n = Math.abs(this.cH.z - player.getZ());
        int n2 = this.JX.z - player.getZ();
        l2.gameserver.utils.MovementTelemetryLogger.logC2S(player, "ValidatePosition", String.format("ClientLoc=(%d,%d,%d, h=%d) | delta2D=%.1f delta3D=%.1f deltaZ=%d", this.cH.x, this.cH.y, this.cH.z, this.cH.h, d, d2, n));
        if (this.ach > 0) {
            Boat boat = BoatHolder.getInstance().getBoat(this.ach);
            if (boat != null && player.getBoat() == boat) {
                player.setHeading(this.cH.h);
                boat.validateLocationPacket(player);
            }
            player.setLastClientPosition(this.cH.setH(player.getHeading()));
            player.setLastServerPosition(player.getLoc());
            return;
        }
        if (player.isFalling()) {
            d = 0.0;
            n = 0;
            n2 = 0;
        }
        int n3 = player.maxZDiff() + 192;
        boolean bl2 = bl = !player.isInWater() && !player.isFlying();
        if (bl && n2 >= 512) {
            player.falling(n2);
        }
        if (bl && n >= n3) {
            player.validateLocation(0);
        } else if (this.cH.z < -30000 || this.cH.z > 30000) {
            if (player.getIncorrectValidateCount() >= 3) {
                player.teleToClosestTown();
            } else {
                this.ad(player);
                player.setIncorrectValidateCount(player.getIncorrectValidateCount() + 1);
            }
        } else if (d2 > 600.0) {
            player.validateLocation(1);
        } else if (d > 1024.0) {
            if (player.getIncorrectValidateCount() >= Config.MOVE_TO_TOWN_ON_FAIL_TRY_COUNT && Config.MOVE_TO_TOWN_ON_FAIL_VALIDATE) {
                player.teleToClosestTown();
            } else if (player.getIncorrectValidateCount() > 3) {
                player.teleToLocation(Location.findPointToStay(this.JX, player.getIncorrectValidateCount() * 32, player.getGeoIndex()));
                player.setIncorrectValidateCount(player.getIncorrectValidateCount() + 1);
            } else {
                player.teleToLocation(player.getLoc());
                player.setIncorrectValidateCount(player.getIncorrectValidateCount() + 1);
            }
        } else {
            player.setIncorrectValidateCount(0);
        }
        player.setLastClientPosition(this.cH.setH(player.getHeading()));
        player.setLastServerPosition(player.getLoc());
    }

    private void ad(Player player) {
        if (player.isGM()) {
            player.sendMessage("Server loc: " + player.getLoc());
            player.sendMessage("Correcting position...");
        }
        if (this.JX.x != 0 && this.JX.y != 0 && this.JX.z != 0) {
            if (GeoEngine.getNSWE(this.JX.x, this.JX.y, this.JX.z, player.getGeoIndex()) == 15) {
                player.teleToLocation(this.JX);
            } else {
                player.teleToClosestTown();
            }
        } else if (this.JW.x != 0 && this.JW.y != 0 && this.JW.z != 0) {
            if (GeoEngine.getNSWE(this.JW.x, this.JW.y, this.JW.z, player.getGeoIndex()) == 15) {
                player.teleToLocation(this.JW);
            } else {
                player.teleToClosestTown();
            }
        } else {
            player.teleToClosestTown();
        }
    }
}

