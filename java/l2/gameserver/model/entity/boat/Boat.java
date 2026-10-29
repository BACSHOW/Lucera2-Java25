/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.boat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import l2.gameserver.ai.BoatAI;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.events.impl.BoatWayEvent;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;

public abstract class Boat
extends Creature {
    private int Qj;
    private int Qk;
    protected int _fromHome;
    protected int _runState;
    private final BoatWayEvent[] Ql = new BoatWayEvent[2];
    protected final Set<Player> _players = new CopyOnWriteArraySet<Player>();

    public Boat(int n, CharTemplate charTemplate) {
        super(n, charTemplate);
    }

    @Override
    public void onSpawn() {
        this._fromHome = 1;
        this.getCurrentWay().reCalcNextTime(false);
    }

    @Override
    public void setXYZ(int n, int n2, int n3, boolean bl) {
        super.setXYZ(n, n2, n3, bl);
        this.updatePeopleInTheBoat(n, n2, n3);
    }

    public void onEvtArrived() {
        this.getCurrentWay().moveNext();
    }

    protected void updatePeopleInTheBoat(int n, int n2, int n3) {
        for (Player player : this._players) {
            if (player == null) continue;
            player.setXYZ(n, n2, n3, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPlayer(Player player, Location location) {
        Set<Player> set = this._players;
        synchronized (set) {
            this._players.add(player);
            player.setBoat(this);
            player.setLoc(this.getLoc(), true);
            player.setInBoatPosition(location);
            player.stopMove(true, false, true);
            player.broadcastPacket(this.getOnPacket(player, location), this.inStopMovePacket(player));
        }
    }

    public void moveInBoat(Player player, Location location, Location location2) {
        if (player.getPet() != null) {
            player.sendPacket(SystemMsg.YOU_SHOULD_RELEASE_YOUR_PET_OR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN, ActionFail.STATIC);
            return;
        }
        if (player.getTransformation() != 0) {
            player.sendPacket(SystemMsg.YOU_CANNOT_BOARD_A_SHIP_WHILE_YOU_ARE_POLYMORPHED, ActionFail.STATIC);
            return;
        }
        if (player.isMovementDisabled() || player.isSitting()) {
            player.sendActionFailed();
            return;
        }
        if (!player.isInBoat()) {
            player.setBoat(this);
        }
        location2.h = PositionUtils.getHeadingTo(location, location2);
        player.setInBoatPosition(location2);
        player.broadcastPacket(this.inMovePacket(player, location, location2));
    }

    public void trajetEnded(boolean bl) {
        this._runState = 0;
        this._fromHome = this._fromHome == 1 ? 0 : 1;
        L2GameServerPacket l2GameServerPacket = this.checkLocationPacket();
        if (l2GameServerPacket != null) {
            this.broadcastPacket(this.infoPacket(), l2GameServerPacket);
        }
        if (bl) {
            this.oustPlayers();
            this.getCurrentWay().reCalcNextTime(false);
        }
    }

    public void teleportShip(int n, int n2, int n3) {
        if (this.isMoving()) {
            this.stopMove(false);
        }
        for (Player player : this._players) {
            player.teleToLocation(n, n2, n3);
        }
        this.setHeading(this.calcHeading(n, n2));
        this.setXYZ(n, n2, n3, true);
        this.getCurrentWay().moveNext();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void oustPlayer(Player player, Location location, boolean bl) {
        Set<Player> set = this._players;
        synchronized (set) {
            player._stablePoint = null;
            player.setBoat(null);
            player.setInBoatPosition(null);
            player.broadcastPacket(this.getOffPacket(player, location));
            if (this.getLoc().distance3D(location) < (double)(2 * this.getActingRange())) {
                player.setLoc(location, true);
            }
            if (bl) {
                player.teleToLocation(location);
            }
            this._players.remove(player);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removePlayer(Player player) {
        Set<Player> set = this._players;
        synchronized (set) {
            this._players.remove(player);
        }
    }

    public void broadcastPacketToPassengers(IStaticPacket iStaticPacket) {
        for (Player player : this._players) {
            player.sendPacket(iStaticPacket);
        }
    }

    @Override
    public int getActingRange() {
        return 150;
    }

    public abstract L2GameServerPacket infoPacket();

    @Override
    public abstract L2GameServerPacket movePacket();

    public abstract L2GameServerPacket inMovePacket(Player var1, Location var2, Location var3);

    @Override
    public abstract L2GameServerPacket stopMovePacket();

    public abstract L2GameServerPacket inStopMovePacket(Player var1);

    public abstract L2GameServerPacket startPacket();

    public abstract L2GameServerPacket validateLocationPacket(Player var1);

    public abstract L2GameServerPacket checkLocationPacket();

    public abstract L2GameServerPacket getOnPacket(Player var1, Location var2);

    public abstract L2GameServerPacket getOffPacket(Player var1, Location var2);

    public abstract void oustPlayers();

    @Override
    public CharacterAI getAI() {
        if (this._ai == null) {
            this._ai = new BoatAI(this);
        }
        return this._ai;
    }

    @Override
    public void broadcastCharInfo() {
        this.broadcastPacket(this.infoPacket());
    }

    @Override
    public void broadcastPacket(L2GameServerPacket ... l2GameServerPacketArray) {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        arrayList.addAll(this._players);
        arrayList.addAll(World.getAroundPlayers(this));
        for (Player player : arrayList) {
            if (player == null) continue;
            player.sendPacket(l2GameServerPacketArray);
        }
    }

    @Override
    public void validateLocation(int n) {
    }

    @Override
    public void sendChanges() {
    }

    @Override
    public int getMoveSpeed() {
        return this.Qj;
    }

    @Override
    public int getRunSpeed() {
        return this.Qj;
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        return false;
    }

    public int getRunState() {
        return this._runState;
    }

    public void setRunState(int n) {
        this._runState = n;
    }

    public void setMoveSpeed(int n) {
        this.Qj = n;
    }

    public void setRotationSpeed(int n) {
        this.Qk = n;
    }

    public int getRotationSpeed() {
        return this.Qk;
    }

    public BoatWayEvent getCurrentWay() {
        return this.Ql[this._fromHome];
    }

    public void setWay(int n, BoatWayEvent boatWayEvent) {
        this.Ql[n] = boatWayEvent;
    }

    public Set<Player> getPlayers() {
        return this._players;
    }

    public boolean isDocked() {
        return this._runState == 0;
    }

    public Location getReturnLoc() {
        return this.getCurrentWay().getReturnLoc();
    }

    @Override
    public boolean isBoat() {
        return true;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player player, Creature creature) {
        if (!this.isMoving()) {
            return Collections.singletonList(this.infoPacket());
        }
        ArrayList<L2GameServerPacket> arrayList = new ArrayList<L2GameServerPacket>(2);
        arrayList.add(this.infoPacket());
        arrayList.add(this.movePacket());
        return arrayList;
    }
}

