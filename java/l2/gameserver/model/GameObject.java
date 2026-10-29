/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.WorldRegion;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.EventOwner;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.network.l2.s2c.DeleteObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.scripts.Events;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameObject
extends EventOwner {
    private static final Logger _log = LoggerFactory.getLogger(GameObject.class);
    public static final GameObject[] EMPTY_L2OBJECT_ARRAY = new GameObject[0];
    protected static final int CREATED = 0;
    protected static final int VISIBLE = 1;
    protected static final int DELETED = -1;
    protected int objectId;
    private int _x;
    private int _y;
    private int Di;
    protected Reflection _reflection = ReflectionManager.DEFAULT;
    private WorldRegion Fu;
    private final AtomicInteger qX = new AtomicInteger(0);

    protected GameObject() {
    }

    public GameObject(int n) {
        this.objectId = n;
    }

    public HardReference<? extends GameObject> getRef() {
        return HardReferences.emptyRef();
    }

    private void cf() {
        HardReference<? extends GameObject> hardReference = this.getRef();
        if (hardReference != null) {
            hardReference.clear();
        }
    }

    public Reflection getReflection() {
        return this._reflection;
    }

    public int getReflectionId() {
        return this._reflection.getId();
    }

    public int getGeoIndex() {
        return this._reflection.getGeoIndex();
    }

    public void setReflection(Reflection reflection) {
        Reflection reflection2;
        if (this._reflection == reflection) {
            return;
        }
        boolean bl = false;
        if (this.isVisible()) {
            this.decayMe();
            bl = true;
        }
        if (!(reflection2 = this.getReflection()).isDefault()) {
            reflection2.removeObject(this);
        }
        this._reflection = reflection;
        if (!reflection.isDefault()) {
            reflection.addObject(this);
        }
        if (bl) {
            this.spawnMe();
        }
    }

    public void setReflection(int n) {
        Reflection reflection = ReflectionManager.getInstance().get(n);
        if (reflection == null) {
            Log.debug("Trying to set unavailable reflection: " + n + " for object: " + this + "!", new Throwable().fillInStackTrace());
            return;
        }
        this.setReflection(reflection);
    }

    public final int hashCode() {
        return this.objectId;
    }

    public final int getObjectId() {
        return this.objectId;
    }

    public int getX() {
        return this._x;
    }

    public int getY() {
        return this._y;
    }

    public int getZ() {
        return this.Di;
    }

    public Location getLoc() {
        return new Location(this._x, this._y, this.Di, this.getHeading());
    }

    public int getGeoZ(Location location) {
        return GeoEngine.getHeight(location, this.getGeoIndex());
    }

    public void setLoc(Location location) {
        this.setXYZ(location.x, location.y, location.z);
    }

    public void setXYZ(int n, int n2, int n3) {
        this._x = World.validCoordX(n);
        this._y = World.validCoordY(n2);
        this.Di = World.validCoordZ(n3);
        World.addVisibleObject(this, null);
    }

    public final boolean isVisible() {
        return this.qX.get() == 1;
    }

    public InvisibleType getInvisibleType() {
        return InvisibleType.NONE;
    }

    public final boolean isInvisible() {
        return this.getInvisibleType() != InvisibleType.NONE;
    }

    public void spawnMe(Location location) {
        this.spawnMe0(location, null);
    }

    protected void spawnMe0(Location location, Creature creature) {
        this._x = location.x;
        this._y = location.y;
        this.Di = this.getGeoZ(location);
        this.spawn0(creature);
    }

    public final void spawnMe() {
        this.spawn0(null);
    }

    protected void spawn0(Creature creature) {
        if (!this.qX.compareAndSet(0, 1)) {
            return;
        }
        World.addVisibleObject(this, creature);
        this.onSpawn();
    }

    public void toggleVisible() {
        if (this.isVisible()) {
            this.decayMe();
        } else {
            this.spawnMe();
        }
    }

    protected void onSpawn() {
    }

    public final void decayMe() {
        if (!this.qX.compareAndSet(1, 0)) {
            return;
        }
        World.removeVisibleObject(this);
        this.onDespawn();
    }

    protected void onDespawn() {
    }

    public final void deleteMe() {
        this.decayMe();
        if (!this.qX.compareAndSet(0, -1)) {
            return;
        }
        this.onDelete();
    }

    public final boolean isDeleted() {
        return this.qX.get() == -1;
    }

    protected void onDelete() {
        Reflection reflection = this.getReflection();
        if (!reflection.isDefault()) {
            reflection.removeObject(this);
        }
        this.cf();
    }

    public void onAction(Player player, boolean bl) {
        if (Events.onAction(player, this, bl)) {
            return;
        }
        player.sendActionFailed();
    }

    public int getActingRange() {
        return -1;
    }

    public void onForcedAttack(Player player, boolean bl) {
        player.sendActionFailed();
    }

    public boolean isAttackable(Creature creature) {
        return false;
    }

    public String getL2ClassShortName() {
        return this.getClass().getSimpleName();
    }

    public final long getXYDeltaSq(int n, int n2) {
        long l = n - this.getX();
        long l2 = n2 - this.getY();
        return l * l + l2 * l2;
    }

    public final long getXYDeltaSq(Location location) {
        return this.getXYDeltaSq(location.x, location.y);
    }

    public final long getZDeltaSq(int n) {
        long l = n - this.getZ();
        return l * l;
    }

    public final long getZDeltaSq(Location location) {
        return this.getZDeltaSq(location.z);
    }

    public final long getXYZDeltaSq(int n, int n2, int n3) {
        return this.getXYDeltaSq(n, n2) + this.getZDeltaSq(n3);
    }

    public final long getXYZDeltaSq(Location location) {
        return this.getXYDeltaSq(location.x, location.y) + this.getZDeltaSq(location.z);
    }

    public final double getDistance(int n, int n2) {
        return Math.sqrt(this.getXYDeltaSq(n, n2));
    }

    public final double getDistance(int n, int n2, int n3) {
        return Math.sqrt(this.getXYZDeltaSq(n, n2, n3));
    }

    public final double getDistance(Location location) {
        return this.getDistance(location.x, location.y, location.z);
    }

    public final boolean isInRange(GameObject gameObject, long l) {
        if (gameObject == null) {
            return false;
        }
        if (gameObject.getReflection() != this.getReflection()) {
            return false;
        }
        long l2 = Math.abs(gameObject.getX() - this.getX());
        if (l2 > l) {
            return false;
        }
        long l3 = Math.abs(gameObject.getY() - this.getY());
        if (l3 > l) {
            return false;
        }
        long l4 = Math.abs(gameObject.getZ() - this.getZ());
        return l4 <= 1500L && l2 * l2 + l3 * l3 <= l * l;
    }

    public final boolean isInActingRange(GameObject gameObject) {
        return this.isInRange(gameObject, (long)this.getActingRange());
    }

    public final boolean isInRangeZ(GameObject gameObject, long l) {
        if (gameObject == null) {
            return false;
        }
        if (gameObject.getReflection() != this.getReflection()) {
            return false;
        }
        long l2 = Math.abs(gameObject.getX() - this.getX());
        if (l2 > l) {
            return false;
        }
        long l3 = Math.abs(gameObject.getY() - this.getY());
        if (l3 > l) {
            return false;
        }
        long l4 = Math.abs(gameObject.getZ() - this.getZ());
        return l4 <= l && l2 * l2 + l3 * l3 + l4 * l4 <= l * l;
    }

    public final boolean isInRange(Location location, long l) {
        return this.isInRangeSq(location, l * l);
    }

    public final boolean isInRangeSq(Location location, long l) {
        return this.getXYDeltaSq(location) <= l;
    }

    public final boolean isInRangeZ(Location location, long l) {
        return this.isInRangeZSq(location, l * l);
    }

    public final boolean isInRangeZSq(Location location, long l) {
        return this.getXYZDeltaSq(location) <= l;
    }

    public final double getDistance(GameObject gameObject) {
        if (gameObject == null) {
            return 0.0;
        }
        return Math.sqrt(this.getXYDeltaSq(gameObject.getX(), gameObject.getY()));
    }

    public final double getDistance3D(GameObject gameObject) {
        if (gameObject == null) {
            return 0.0;
        }
        return Math.sqrt(this.getXYZDeltaSq(gameObject.getX(), gameObject.getY(), gameObject.getZ()));
    }

    public final double getRealDistance(GameObject gameObject) {
        return this.getRealDistance3D(gameObject, true);
    }

    public final double getRealDistance3D(GameObject gameObject) {
        return this.getRealDistance3D(gameObject, false);
    }

    public final double getRealDistance3D(GameObject gameObject, boolean bl) {
        double d;
        double d2 = d = bl ? this.getDistance(gameObject) : this.getDistance3D(gameObject);
        if (this.isCreature()) {
            d -= ((Creature)this).getTemplate().collisionRadius;
        }
        if (gameObject.isCreature()) {
            d -= ((Creature)gameObject).getTemplate().collisionRadius;
        }
        return d > 0.0 ? d : 0.0;
    }

    /**
     * Dhousefe-style squared-distance radius check with early-axis exit.
     * Zero sqrt allocations, exact Euclidean bounding, accounts for creature collision.
     */
    public boolean isInStrictRadius(GameObject obj, int radius) {
        if (obj == null) {
            return false;
        }
        if (this.getReflectionId() != obj.getReflectionId()) {
            return false;
        }
        double colA = this.isCreature() ? ((Creature) this).getTemplate().collisionRadius : 0.0;
        double colB = obj.isCreature() ? ((Creature) obj).getTemplate().collisionRadius : 0.0;
        return isInStrictRadius(this.getX(), this.getY(), this.getZ(), obj.getX(), obj.getY(), obj.getZ(), colA, colB, radius);
    }

    /**
     * Dhousefe-style squared-distance radius check to a location with early-axis exit.
     */
    public boolean isInStrictRadius(Location loc, int radius) {
        if (loc == null) {
            return false;
        }
        double colA = this.isCreature() ? ((Creature) this).getTemplate().collisionRadius : 0.0;
        return isInStrictRadius(this.getX(), this.getY(), this.getZ(), loc.getX(), loc.getY(), loc.getZ(), colA, 0.0, radius);
    }

    /**
     * Pure static calculation for isInStrictRadius: avoids instance requirements for headless tests and SIMD loops.
     */
    public static boolean isInStrictRadius(int ox, int oy, int oz, int tx, int ty, int tz, double collisionA, double collisionB, int radius) {
        double totalRadius = radius + collisionA + collisionB;
        long dx = (long) tx - ox;
        long dy = (long) ty - oy;
        long dz = (long) tz - oz;

        if (Math.abs(dx) > totalRadius || Math.abs(dy) > totalRadius || Math.abs(dz) > totalRadius) {
            return false;
        }

        return (dx * dx + dy * dy + dz * dz) < (totalRadius * totalRadius);
    }

    public final long getSqDistance(int n, int n2) {
        return this.getXYDeltaSq(n, n2);
    }

    public final long getSqDistance(GameObject gameObject) {
        if (gameObject == null) {
            return 0L;
        }
        return this.getXYDeltaSq(gameObject.getLoc());
    }

    public Player getPlayer() {
        return null;
    }

    public int getHeading() {
        return 0;
    }

    public int getMoveSpeed() {
        return 0;
    }

    public WorldRegion getCurrentRegion() {
        return this.Fu;
    }

    public void setCurrentRegion(WorldRegion worldRegion) {
        this.Fu = worldRegion;
    }

    public boolean isInObserverMode() {
        return false;
    }

    public boolean isOlyParticipant() {
        return false;
    }

    public boolean isInBoat() {
        return false;
    }

    public boolean isFlying() {
        return false;
    }

    public double getColRadius() {
        _log.warn("getColRadius called directly from L2Object");
        Thread.dumpStack();
        return 0.0;
    }

    public double getColHeight() {
        _log.warn("getColHeight called directly from L2Object");
        Thread.dumpStack();
        return 0.0;
    }

    public boolean isCreature() {
        return false;
    }

    public boolean isPlayable() {
        return false;
    }

    public boolean isPlayer() {
        return false;
    }

    public boolean isPet() {
        return false;
    }

    public boolean isSummon() {
        return false;
    }

    public boolean isNpc() {
        return false;
    }

    public boolean isMonster() {
        return false;
    }

    public boolean isItem() {
        return false;
    }

    public boolean isRaid() {
        return false;
    }

    public boolean isBoss() {
        return false;
    }

    public boolean isTrap() {
        return false;
    }

    public boolean isDoor() {
        return false;
    }

    public boolean isArtefact() {
        return false;
    }

    public boolean isSiegeGuard() {
        return false;
    }

    public boolean isBoat() {
        return false;
    }

    public boolean isVehicle() {
        return false;
    }

    public boolean isMinion() {
        return false;
    }

    public String getName() {
        return this.getClass().getSimpleName() + ":" + this.objectId;
    }

    public String dump() {
        return this.dump(true);
    }

    public String dump(boolean bl) {
        return Util.dumpObject(this, bl, true, true);
    }

    public List<L2GameServerPacket> addPacketList(Player player, Creature creature) {
        return Collections.emptyList();
    }

    public List<L2GameServerPacket> deletePacketList() {
        return Collections.singletonList(new DeleteObject(this));
    }

    @Override
    public void addEvent(GlobalEvent globalEvent) {
        globalEvent.onAddEvent(this);
        super.addEvent(globalEvent);
    }

    @Override
    public void removeEvent(GlobalEvent globalEvent) {
        globalEvent.onRemoveEvent(this);
        super.removeEvent(globalEvent);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        return ((GameObject)object).getObjectId() == this.getObjectId();
    }
}

