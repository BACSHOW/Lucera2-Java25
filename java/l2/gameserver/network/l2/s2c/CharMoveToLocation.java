/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;

public class CharMoveToLocation
extends L2GameServerPacket {
    private int CS;
    private int afc;
    private Location afd;
    private Location _destination;

    public CharMoveToLocation(Creature creature) {
        this(creature, creature.getLoc(), creature.getDestination());
    }

    public CharMoveToLocation(Creature creature, Location location, Location location2) {
        this.CS = creature.getObjectId();
        if (creature.isPlayer() && creature.getPlayer().getLastClientPosition() != null && creature.isMoving()) {
            Location clientPos = creature.getPlayer().getLastClientPosition();
            if (clientPos.distance(location) < 128.0) {
                location = clientPos;
            }
        }
        this.afd = location;
        this._destination = location2;
        if (!creature.isFlying()) {
            this.afc = Config.CLIENT_Z_SHIFT;
        }
        if (creature.isInWater()) {
            this.afc += Config.CLIENT_Z_SHIFT;
        }
        if (this._destination == null) {
            Log.debug("CharMoveToLocation: desc is null, but moving. L2Character: " + creature.getObjectId() + ":" + creature.getName() + "; Loc: " + this.afd);
            this._destination = this.afd;
        }
    }

    public CharMoveToLocation(int n, Location location, Location location2) {
        this.CS = n;
        this.afd = location;
        this._destination = location2;
    }

    @Override
    protected final void writeImpl() {
        if (this.getClient() != null && this.getClient().getActiveChar() != null && this.getClient().getActiveChar().getObjectId() == this.CS) {
            l2.gameserver.utils.MovementTelemetryLogger.logS2C(this.getClient().getActiveChar(), "CharMoveToLocation", String.format("From=(%d,%d,%d) To=(%d,%d,%d)", this.afd.x, this.afd.y, this.afd.z, this._destination.x, this._destination.y, this._destination.z));
        }
        this.writeC(1);
        this.writeD(this.CS);
        this.writeD(this._destination.x);
        this.writeD(this._destination.y);
        this.writeD(this._destination.z + this.afc);
        this.writeD(this.afd.x);
        this.writeD(this.afd.y);
        this.writeD(this.afd.z + this.afc);
    }
}

