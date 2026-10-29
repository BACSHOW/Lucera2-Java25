/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.commons.collections.LazyArrayList;
import l2.commons.collections.MultiValueSet;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Territory;
import l2.gameserver.model.World;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.EventTrigger;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncAdd;
import l2.gameserver.taskmanager.EffectTaskManager;
import l2.gameserver.templates.ZoneTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zone {
    private static final Logger _log = LoggerFactory.getLogger(Zone.class);
    public static final Zone[] EMPTY_L2ZONE_ARRAY = new Zone[0];
    public static final String BLOCKED_ACTION_PRIVATE_STORE = "open_private_store";
    public static final String BLOCKED_ACTION_PRIVATE_WORKSHOP = "open_private_workshop";
    public static final String BLOCKED_ACTION_DROP_MERCHANT_GUARD = "drop_merchant_guard";
    public static final String BLOCKED_ACTION_SAVE_BOOKMARK = "save_bookmark";
    public static final String BLOCKED_ACTION_USE_BOOKMARK = "use_bookmark";
    public static final String BLOCKED_ACTION_MINIMAP = "open_minimap";
    public static final String BLOCKED_ACTION_DROP_ITEM = "drop_item";
    private ZoneType Me;
    private boolean _active;
    private final MultiValueSet<String> Mf;
    private final ZoneTemplate Mg;
    private Reflection _reflection;
    private final ZoneListenerList Mh = new ZoneListenerList();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = this.lock.readLock();
    private final Lock writeLock = this.lock.writeLock();
    private final List<Creature> _objects = new LazyArrayList<Creature>(32);
    private final Map<Creature, ZoneTimer> Mi = new ConcurrentHashMap<Creature, ZoneTimer>();
    public static final int ZONE_STATS_ORDER = 64;

    public Zone(ZoneTemplate zoneTemplate) {
        this(zoneTemplate.getType(), zoneTemplate);
    }

    public Zone(ZoneType zoneType, ZoneTemplate zoneTemplate) {
        this.Me = zoneType;
        this.Mg = zoneTemplate;
        this.Mf = zoneTemplate.getParams();
    }

    public ZoneTemplate getTemplate() {
        return this.Mg;
    }

    public final String getName() {
        return this.getTemplate().getName();
    }

    public ZoneType getType() {
        return this.Me;
    }

    public boolean isType(ZoneType zoneType) {
        return this.getType() == zoneType;
    }

    public boolean isAnyType(ZoneType ... zoneTypeArray) {
        for (ZoneType zoneType : zoneTypeArray) {
            if (!this.isType(zoneType)) continue;
            return true;
        }
        return false;
    }

    public void setType(ZoneType zoneType) {
        this.Me = zoneType;
    }

    public Territory getTerritory() {
        return this.getTemplate().getTerritory();
    }

    public final int getEnteringMessageId() {
        return this.getTemplate().getEnteringMessageId();
    }

    public final int getLeavingMessageId() {
        return this.getTemplate().getLeavingMessageId();
    }

    public Skill getZoneSkill() {
        return this.getTemplate().getZoneSkill();
    }

    public ZoneTarget getZoneTarget() {
        return this.getTemplate().getZoneTarget();
    }

    public Race getAffectRace() {
        return this.getTemplate().getAffectRace();
    }

    public int getDamageMessageId() {
        return this.getTemplate().getDamageMessageId();
    }

    public int getDamageOnHP() {
        return this.getTemplate().getDamageOnHP();
    }

    public int getDamageOnMP() {
        return this.getTemplate().getDamageOnMP();
    }

    public double getMoveBonus() {
        return this.getTemplate().getMoveBonus();
    }

    public double getRegenBonusHP() {
        return this.getTemplate().getRegenBonusHP();
    }

    public double getRegenBonusMP() {
        return this.getTemplate().getRegenBonusMP();
    }

    public long getRestartTime() {
        return this.getTemplate().getRestartTime();
    }

    public List<Location> getRestartPoints() {
        return this.getTemplate().getRestartPoints();
    }

    public List<Location> getPKRestartPoints() {
        return this.getTemplate().getPKRestartPoints();
    }

    public Location getSpawn() {
        if (this.getRestartPoints() == null) {
            return null;
        }
        Location location = this.getRestartPoints().get(Rnd.get(this.getRestartPoints().size()));
        return location.clone();
    }

    public Location getPKSpawn() {
        if (this.getPKRestartPoints() == null) {
            return this.getSpawn();
        }
        Location location = this.getPKRestartPoints().get(Rnd.get(this.getPKRestartPoints().size()));
        return location.clone();
    }

    public boolean checkIfInZone(int n, int n2) {
        return this.checkIfInZone(n, n2, false);
    }

    public boolean checkIfInZone(int n, int n2, boolean bl) {
        return (!bl || this.isActive()) && this.getTerritory().isInside(n, n2);
    }

    public boolean checkIfInZone(int n, int n2, int n3) {
        return this.isActive() && this.getTerritory().isInside(n, n2, n3);
    }

    public boolean checkIfInZone(int n, int n2, int n3, Reflection reflection) {
        return this.isActive() && this._reflection == reflection && this.getTerritory().isInside(n, n2, n3);
    }

    public boolean checkIfInZone(Location location, Reflection reflection) {
        return this.isActive() && this._reflection == reflection && this.getTerritory().isInside(location.getX(), location.getY(), location.getZ());
    }

    public boolean checkIfInZone(Creature creature) {
        this.readLock.lock();
        try {
            boolean bl = this._objects.contains(creature);
            return bl;
        }
        finally {
            this.readLock.unlock();
        }
    }

    public final double findDistanceToZone(GameObject gameObject, boolean bl) {
        return this.findDistanceToZone(gameObject.getX(), gameObject.getY(), gameObject.getZ(), bl);
    }

    public final double findDistanceToZone(int n, int n2, int n3, boolean bl) {
        return PositionUtils.calculateDistance(n, n2, n3, (this.getTerritory().getXmax() + this.getTerritory().getXmin()) / 2, (this.getTerritory().getYmax() + this.getTerritory().getYmin()) / 2, (this.getTerritory().getZmax() + this.getTerritory().getZmin()) / 2, bl);
    }

    public void doEnter(Creature creature) {
        boolean bl = false;
        this.writeLock.lock();
        try {
            if (!this._objects.contains(creature)) {
                bl = this._objects.add(creature);
            }
        }
        finally {
            this.writeLock.unlock();
        }
        if (bl) {
            this.onZoneEnter(creature);
        }
    }

    protected void onZoneEnter(Creature creature) {
        this.a(creature, true);
        this.h(creature);
        if (creature.isPlayer()) {
            if (this.getEnteringMessageId() != 0) {
                creature.sendPacket((IStaticPacket)new SystemMessage(this.getEnteringMessageId()));
            }
            if (this.getTemplate().getEventId() != 0) {
                creature.sendPacket((IStaticPacket)new EventTrigger(this.getTemplate().getEventId(), true));
            }
            if (this.getTemplate().getBlockedActions() != null) {
                ((Player)creature).blockActions(this.getTemplate().getBlockedActions());
            }
        }
        this.Mh.onEnter(creature);
    }

    public void doLeave(Creature creature) {
        boolean bl = false;
        this.writeLock.lock();
        try {
            bl = this._objects.remove(creature);
        }
        finally {
            this.writeLock.unlock();
        }
        if (bl) {
            this.onZoneLeave(creature);
        }
    }

    protected void onZoneLeave(Creature creature) {
        this.a(creature, false);
        this.i(creature);
        if (creature.isPlayer()) {
            if (this.getLeavingMessageId() != 0 && creature.isPlayer()) {
                creature.sendPacket((IStaticPacket)new SystemMessage(this.getLeavingMessageId()));
            }
            if (this.getTemplate().getEventId() != 0 && creature.isPlayer()) {
                creature.sendPacket((IStaticPacket)new EventTrigger(this.getTemplate().getEventId(), false));
            }
            if (this.getTemplate().getBlockedActions() != null) {
                ((Player)creature).unblockActions(this.getTemplate().getBlockedActions());
            }
        }
        this.Mh.onLeave(creature);
    }

    private void h(Creature creature) {
        if (!this.checkTarget(creature)) {
            return;
        }
        if (this.getMoveBonus() != 0.0 && creature.isPlayable()) {
            creature.addStatFunc(new FuncAdd(Stats.RUN_SPEED, 64, this, this.getMoveBonus()));
            creature.sendChanges();
        }
        if (this.getRegenBonusHP() != 0.0) {
            creature.addStatFunc(new FuncAdd(Stats.REGENERATE_HP_RATE, 64, this, this.getRegenBonusHP()));
        }
        if (this.getRegenBonusMP() != 0.0) {
            creature.addStatFunc(new FuncAdd(Stats.REGENERATE_MP_RATE, 64, this, this.getRegenBonusMP()));
        }
    }

    private void i(Creature creature) {
        if (this.getRegenBonusHP() == 0.0 && this.getRegenBonusMP() == 0.0 && this.getMoveBonus() == 0.0) {
            return;
        }
        creature.removeStatsOwner(this);
        creature.sendChanges();
    }

    private void a(Creature creature, boolean bl) {
        if (this.checkTarget(creature)) {
            if (bl) {
                if (this.getZoneSkill() != null) {
                    SkillTimer skillTimer = new SkillTimer(creature);
                    this.Mi.put(creature, skillTimer);
                    skillTimer.start();
                } else if (this.getDamageOnHP() > 0 || this.getDamageOnMP() > 0) {
                    DamageTimer damageTimer = new DamageTimer(creature);
                    this.Mi.put(creature, damageTimer);
                    damageTimer.start();
                }
            } else {
                ZoneTimer zoneTimer = this.Mi.remove(creature);
                if (zoneTimer != null) {
                    zoneTimer.stop();
                }
                if (this.getZoneSkill() != null) {
                    creature.getEffectList().stopEffect(this.getZoneSkill());
                }
            }
        }
    }

    private boolean checkTarget(Creature creature) {
        switch (this.getZoneTarget()) {
            case pc: {
                if (creature.isPlayable()) break;
                return false;
            }
            case only_pc: {
                if (creature.isPlayer()) break;
                return false;
            }
            case npc: {
                if (creature.isNpc()) break;
                return false;
            }
        }
        if (this.getAffectRace() != null) {
            Player player = creature.getPlayer();
            if (player == null) {
                return false;
            }
            if (player.getRace() != this.getAffectRace()) {
                return false;
            }
        }
        return true;
    }

    public Creature[] getObjects() {
        this.readLock.lock();
        try {
            Creature[] creatureArray = this._objects.toArray(new Creature[this._objects.size()]);
            return creatureArray;
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Player> getInsidePlayers() {
        LazyArrayList<Player> lazyArrayList = new LazyArrayList<Player>();
        this.readLock.lock();
        try {
            for (int i = 0; i < this._objects.size(); ++i) {
                Creature creature = this._objects.get(i);
                if (creature == null || !creature.isPlayer()) continue;
                lazyArrayList.add((Player)creature);
            }
        }
        finally {
            this.readLock.unlock();
        }
        return lazyArrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Playable> getInsidePlayables() {
        LazyArrayList<Playable> lazyArrayList = new LazyArrayList<Playable>();
        this.readLock.lock();
        try {
            for (int i = 0; i < this._objects.size(); ++i) {
                Creature creature = this._objects.get(i);
                if (creature == null || !creature.isPlayable()) continue;
                lazyArrayList.add((Playable)creature);
            }
        }
        finally {
            this.readLock.unlock();
        }
        return lazyArrayList;
    }

    public void setActive(boolean bl) {
        this.writeLock.lock();
        try {
            if (this._active == bl) {
                return;
            }
            this._active = bl;
        }
        finally {
            this.writeLock.unlock();
        }
        if (this.isActive()) {
            World.addZone(this);
        } else {
            World.removeZone(this);
        }
    }

    public boolean isActive() {
        return this._active;
    }

    public void setReflection(Reflection reflection) {
        this._reflection = reflection;
    }

    public Reflection getReflection() {
        return this._reflection;
    }

    public void setParam(String string, String string2) {
        this.Mf.put(string, string2);
    }

    public void setParam(String string, Object object) {
        this.Mf.put(string, object);
    }

    public MultiValueSet<String> getParams() {
        return this.Mf;
    }

    public <T extends Listener<Zone>> boolean addListener(T t) {
        return this.Mh.add(t);
    }

    public <T extends Listener<Zone>> boolean removeListener(T t) {
        return this.Mh.remove(t);
    }

    public final String toString() {
        return "[Zone " + (Object)((Object)this.getType()) + " name: " + this.getName() + "]";
    }

    public void broadcastPacket(L2GameServerPacket l2GameServerPacket, boolean bl) {
        List<Player> list = this.getInsidePlayers();
        if (list != null && !list.isEmpty()) {
            for (Player player : list) {
                if (bl) {
                    if (player.isDead()) continue;
                    player.broadcastPacket(l2GameServerPacket);
                    continue;
                }
                player.broadcastPacket(l2GameServerPacket);
            }
        }
    }

    public class ZoneListenerList
    extends ListenerList<Zone> {
        public void onEnter(Creature creature) {
            this.forEachListener(OnZoneEnterLeaveListener.class, onZoneEnterLeaveListener -> onZoneEnterLeaveListener.onZoneEnter(Zone.this, creature));
        }

        public void onLeave(Creature creature) {
            this.forEachListener(OnZoneEnterLeaveListener.class, onZoneEnterLeaveListener -> onZoneEnterLeaveListener.onZoneLeave(Zone.this, creature));
        }
    }

    private class DamageTimer
    extends ZoneTimer {
        public DamageTimer(Creature creature) {
            super(creature);
        }

        @Override
        public void runImpl() throws Exception {
            if (!Zone.this.isActive()) {
                return;
            }
            if (!Zone.this.checkTarget(this.cha)) {
                return;
            }
            int n = Zone.this.getDamageOnHP();
            int n2 = Zone.this.getDamageOnMP();
            int n3 = Zone.this.getDamageMessageId();
            if (n == 0 && n2 == 0) {
                return;
            }
            if (n > 0) {
                this.cha.reduceCurrentHp(n, this.cha, null, false, false, true, false, false, false, true);
                if (n3 > 0) {
                    this.cha.sendPacket((IStaticPacket)new SystemMessage(n3).addNumber(n));
                }
            }
            if (n2 > 0) {
                this.cha.reduceCurrentMp(n2, null);
                if (n3 > 0) {
                    this.cha.sendPacket((IStaticPacket)new SystemMessage(n3).addNumber(n2));
                }
            }
            this.next();
        }
    }

    private class SkillTimer
    extends ZoneTimer {
        public SkillTimer(Creature creature) {
            super(creature);
        }

        @Override
        public void runImpl() throws Exception {
            if (!Zone.this.isActive()) {
                return;
            }
            if (!Zone.this.checkTarget(this.cha)) {
                return;
            }
            Skill skill = Zone.this.getZoneSkill();
            if (skill == null) {
                return;
            }
            if (Rnd.chance(Zone.this.getTemplate().getSkillProb()) && !this.cha.isDead()) {
                skill.getEffects(this.cha, this.cha, false, false);
            }
            this.next();
        }
    }

    private abstract class ZoneTimer
    extends RunnableImpl {
        protected Creature cha;
        protected Future<?> future;
        protected boolean active;

        public ZoneTimer(Creature creature) {
            this.cha = creature;
        }

        public void start() {
            this.active = true;
            this.future = EffectTaskManager.getInstance().schedule(this, (long)Zone.this.getTemplate().getInitialDelay() * 1000L);
        }

        public void stop() {
            this.active = false;
            if (this.future != null) {
                this.future.cancel(false);
                this.future = null;
            }
        }

        public void next() {
            if (!this.active) {
                return;
            }
            if (Zone.this.getTemplate().getUnitTick() == 0 && Zone.this.getTemplate().getRandomTick() == 0) {
                return;
            }
            this.future = EffectTaskManager.getInstance().schedule(this, (long)(Zone.this.getTemplate().getUnitTick() + Rnd.get(0, Zone.this.getTemplate().getRandomTick())) * 1000L);
        }

        @Override
        public abstract void runImpl() throws Exception;
    }

    public static enum ZoneTarget {
        pc,
        npc,
        only_pc;

    }

    public static enum ZoneType {
        SIEGE,
        RESIDENCE,
        HEADQUARTER,
        FISHING,
        water,
        battle_zone,
        damage,
        instant_skill,
        mother_tree,
        peace_zone,
        poison,
        ssq_zone,
        swamp,
        no_escape,
        no_landing,
        no_restart,
        no_summon,
        dummy,
        offshore,
        epic,
        fun;

    }
}

