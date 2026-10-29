/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity;

import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.InstanceReuseDAO;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.door.impl.MasterOnOpenCloseListenerImpl;
import l2.gameserver.listener.reflection.OnReflectionCollapseListener;
import l2.gameserver.listener.zone.impl.NoLandingZoneListener;
import l2.gameserver.listener.zone.impl.ResidenceEnterLeaveListenerImpl;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.HardSpawner;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.templates.DoorTemplate;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.templates.ZoneTemplate;
import l2.gameserver.templates.spawn.SpawnTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reflection {
    private static final Logger _log = LoggerFactory.getLogger(Reflection.class);
    private static final AtomicInteger AE = new AtomicInteger();
    private final int _id;
    private String _name = "";
    private InstantZone PC;
    private int PD;
    private Location PE;
    private Location PF;
    private Location PG;
    protected List<Spawner> _spawns = new ArrayList<Spawner>();
    protected List<GameObject> _objects = new ArrayList<GameObject>();
    protected IntObjectMap<DoorInstance> _doors = Containers.emptyIntObjectMap();
    protected Map<String, Zone> _zones = Collections.emptyMap();
    protected Map<String, List<Spawner>> _spawners = Collections.emptyMap();
    protected TIntHashSet _visitors = new TIntHashSet();
    protected final Lock lock = new ReentrantLock();
    protected int _playerCount;
    protected Party _party;
    protected CommandChannel _commandChannel;
    private int PH;
    private boolean PI;
    private Future<?> PJ;
    private Future<?> PK;
    private Future<?> PL;
    private final ReflectionListenerList PM = new ReflectionListenerList();

    public Reflection() {
        this(AE.incrementAndGet());
    }

    private Reflection(int n) {
        this._id = n;
    }

    public int getId() {
        return this._id;
    }

    public int getInstancedZoneId() {
        return this.PC == null ? 0 : this.PC.getId();
    }

    public void setParty(Party party) {
        this._party = party;
    }

    public Party getParty() {
        return this._party;
    }

    public void setCommandChannel(CommandChannel commandChannel) {
        this._commandChannel = commandChannel;
    }

    public void setCollapseIfEmptyTime(int n) {
        this.PH = n;
    }

    public String getName() {
        return this._name;
    }

    protected void setName(String string) {
        this._name = string;
    }

    public InstantZone getInstancedZone() {
        return this.PC;
    }

    protected void setInstancedZone(InstantZone instantZone) {
        this.PC = instantZone;
    }

    public int getGeoIndex() {
        return this.PD;
    }

    protected void setGeoIndex(int n) {
        this.PD = n;
    }

    public void setCoreLoc(Location location) {
        this.PE = location;
    }

    public Location getCoreLoc() {
        return this.PE;
    }

    public void setReturnLoc(Location location) {
        this.PF = location;
    }

    public Location getReturnLoc() {
        return this.PF;
    }

    public void setTeleportLoc(Location location) {
        this.PG = location;
    }

    public Location getTeleportLoc() {
        return this.PG;
    }

    public List<Spawner> getSpawns() {
        return this._spawns;
    }

    public Collection<DoorInstance> getDoors() {
        return this._doors.values();
    }

    public DoorInstance getDoor(int n) {
        return this._doors.get(n);
    }

    public Zone getZone(String string) {
        return this._zones.get(string);
    }

    public void startCollapseTimer(long l) {
        if (this.isDefault() || this.isStatic()) {
            new Exception("Basic reflection " + this._id + " could not be collapsed!").printStackTrace();
            return;
        }
        this.lock.lock();
        try {
            if (this.PJ != null) {
                this.PJ.cancel(false);
                this.PJ = null;
            }
            if (this.PK != null) {
                this.PK.cancel(false);
                this.PK = null;
            }
            this.PJ = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                @Override
                public void runImpl() throws Exception {
                    Reflection.this.collapse();
                }
            }, l);
            if (l >= 60000L) {
                this.PK = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        Reflection.this.minuteBeforeCollapse();
                    }
                }, l - 60000L);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void stopCollapseTimer() {
        this.lock.lock();
        try {
            if (this.PJ != null) {
                this.PJ.cancel(false);
                this.PJ = null;
            }
            if (this.PK != null) {
                this.PK.cancel(false);
                this.PK = null;
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void minuteBeforeCollapse() {
        if (this.PI) {
            return;
        }
        this.lock.lock();
        try {
            for (GameObject gameObject : this._objects) {
                if (!gameObject.isPlayer()) continue;
                Player player = gameObject.getPlayer();
                player.sendMessage(new CustomMessage("THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTES_YOU_WILL_BE_FORCED_OUT_OF_THE_DANGEON_THEN_TIME_EXPIRES", player, new Object[0]).addNumber(1L));
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void collapse() {
        if (this._id <= 0) {
            new Exception("Basic reflection " + this._id + " could not be collapsed!").printStackTrace();
            return;
        }
        this.lock.lock();
        try {
            if (this.PI) {
                return;
            }
            this.PI = true;
        }
        finally {
            this.lock.unlock();
        }
        this.PM.onCollapse();
        try {
            this.stopCollapseTimer();
            if (this.PL != null) {
                this.PL.cancel(false);
                this.PL = null;
            }
            for (Spawner arrayList2 : this._spawns) {
                arrayList2.deleteAll();
            }
            for (String string : this._spawners.keySet()) {
                this.despawnByGroup(string);
            }
            for (DoorInstance doorInstance : this._doors.values()) {
                doorInstance.deleteMe();
            }
            this._doors.clear();
            for (Zone zone : this._zones.values()) {
                zone.setActive(false);
            }
            this._zones.clear();
            ArrayList arrayList3 = new ArrayList();
            ArrayList<GameObject> arrayList = new ArrayList<GameObject>();
            this.lock.lock();
            try {
                for (GameObject gameObject : this._objects) {
                    if (gameObject.isPlayer()) {
                        arrayList3.add((Player)gameObject);
                        continue;
                    }
                    if (gameObject.isPlayable()) continue;
                    arrayList.add(gameObject);
                }
            }
            finally {
                this.lock.unlock();
            }
            Iterator<GameObject> iterator = arrayList3.iterator();
            while (iterator.hasNext()) {
                GameObject gameObject;
                gameObject = (Player)iterator.next();
                if (((Player)gameObject).getParty() != null) {
                    if (this.equals(((Player)gameObject).getParty().getReflection())) {
                        ((Player)gameObject).getParty().setReflection(null);
                    }
                    if (((Player)gameObject).getParty().getCommandChannel() != null && this.equals(((Player)gameObject).getParty().getCommandChannel().getReflection())) {
                        ((Player)gameObject).getParty().getCommandChannel().setReflection(null);
                    }
                }
                if (!this.equals(gameObject.getReflection())) continue;
                if (this.getReturnLoc() != null) {
                    ((Creature)gameObject).teleToLocation(this.getReturnLoc(), ReflectionManager.DEFAULT);
                    continue;
                }
                ((Player)gameObject).setReflection(ReflectionManager.DEFAULT);
            }
            if (this._commandChannel != null) {
                this._commandChannel.setReflection(null);
                this._commandChannel = null;
            }
            if (this._party != null) {
                this._party.setReflection(null);
                this._party = null;
            }
            for (GameObject gameObject : arrayList) {
                gameObject.deleteMe();
            }
            this._spawns.clear();
            this._objects.clear();
            this._visitors.clear();
            this._doors.clear();
            this._playerCount = 0;
            this.onCollapse();
        }
        finally {
            ReflectionManager.getInstance().remove(this);
            GeoEngine.FreeGeoIndex(this.getGeoIndex());
        }
    }

    protected void onCollapse() {
    }

    public void addObject(GameObject gameObject) {
        if (this.PI) {
            return;
        }
        this.lock.lock();
        try {
            this._objects.add(gameObject);
            if (gameObject.isPlayer()) {
                ++this._playerCount;
                this._visitors.add(gameObject.getObjectId());
                this.onPlayerEnter(gameObject.getPlayer());
            }
        }
        finally {
            this.lock.unlock();
        }
        if (this.PH > 0 && this.PL != null) {
            this.PL.cancel(false);
            this.PL = null;
        }
    }

    public void removeObject(GameObject gameObject) {
        if (this.PI) {
            return;
        }
        this.lock.lock();
        try {
            if (!this._objects.remove(gameObject)) {
                return;
            }
            if (gameObject.isPlayer()) {
                --this._playerCount;
                this.onPlayerExit(gameObject.getPlayer());
            }
        }
        finally {
            this.lock.unlock();
        }
        if (this._playerCount <= 0 && !this.isDefault() && !this.isStatic() && this.PL == null) {
            if (this.PH <= 0) {
                this.collapse();
            } else {
                this.PL = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        Reflection.this.collapse();
                    }
                }, (long)(this.PH * 60) * 1000L);
            }
        }
    }

    public void onPlayerEnter(Player player) {
        player.getInventory().validateItems();
    }

    public void onPlayerExit(Player player) {
        player.getInventory().validateItems();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Player> getPlayers() {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        this.lock.lock();
        try {
            for (GameObject gameObject : this._objects) {
                if (!gameObject.isPlayer()) continue;
                arrayList.add((Player)gameObject);
            }
        }
        finally {
            this.lock.unlock();
        }
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<NpcInstance> getNpcs() {
        ArrayList<NpcInstance> arrayList = new ArrayList<NpcInstance>();
        this.lock.lock();
        try {
            for (GameObject gameObject : this._objects) {
                if (!gameObject.isNpc()) continue;
                arrayList.add((NpcInstance)gameObject);
            }
        }
        finally {
            this.lock.unlock();
        }
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<NpcInstance> getAllByNpcId(int n, boolean bl) {
        ArrayList<NpcInstance> arrayList = new ArrayList<NpcInstance>();
        this.lock.lock();
        try {
            for (GameObject gameObject : this._objects) {
                NpcInstance npcInstance;
                if (!gameObject.isNpc() || n != (npcInstance = (NpcInstance)gameObject).getNpcId() || bl && npcInstance.isDead()) continue;
                arrayList.add(npcInstance);
            }
        }
        finally {
            this.lock.unlock();
        }
        return arrayList;
    }

    public boolean canChampions() {
        return this._id <= 0;
    }

    public boolean isAutolootForced() {
        return false;
    }

    public boolean isCollapseStarted() {
        return this.PI;
    }

    public void addSpawn(SimpleSpawner simpleSpawner) {
        if (simpleSpawner != null) {
            this._spawns.add(simpleSpawner);
        }
    }

    public void fillSpawns(List<InstantZone.SpawnInfo> list) {
        if (list == null) {
            return;
        }
        block5: for (InstantZone.SpawnInfo spawnInfo : list) {
            switch (spawnInfo.getSpawnType()) {
                case 0: {
                    SimpleSpawner simpleSpawner;
                    for (Location location : spawnInfo.getCoords()) {
                        simpleSpawner = new SimpleSpawner(spawnInfo.getNpcId());
                        simpleSpawner.setReflection(this);
                        simpleSpawner.setRespawnDelay(spawnInfo.getRespawnDelay(), spawnInfo.getRespawnRnd());
                        simpleSpawner.setAmount(spawnInfo.getCount());
                        simpleSpawner.setLoc(location);
                        simpleSpawner.doSpawn(true);
                        if (spawnInfo.getRespawnDelay() == 0) {
                            simpleSpawner.stopRespawn();
                        } else {
                            simpleSpawner.startRespawn();
                        }
                        this.addSpawn(simpleSpawner);
                    }
                    continue block5;
                }
                case 1: {
                    SimpleSpawner simpleSpawner = new SimpleSpawner(spawnInfo.getNpcId());
                    simpleSpawner.setReflection(this);
                    simpleSpawner.setRespawnDelay(spawnInfo.getRespawnDelay(), spawnInfo.getRespawnRnd());
                    simpleSpawner.setAmount(1);
                    simpleSpawner.setLoc(spawnInfo.getCoords().get(Rnd.get(spawnInfo.getCoords().size())));
                    simpleSpawner.doSpawn(true);
                    if (spawnInfo.getRespawnDelay() == 0) {
                        simpleSpawner.stopRespawn();
                    } else {
                        simpleSpawner.startRespawn();
                    }
                    this.addSpawn(simpleSpawner);
                    break;
                }
                case 2: {
                    SimpleSpawner simpleSpawner = new SimpleSpawner(spawnInfo.getNpcId());
                    simpleSpawner.setReflection(this);
                    simpleSpawner.setRespawnDelay(spawnInfo.getRespawnDelay(), spawnInfo.getRespawnRnd());
                    simpleSpawner.setAmount(spawnInfo.getCount());
                    simpleSpawner.setTerritory(spawnInfo.getLoc());
                    for (int i = 0; i < spawnInfo.getCount(); ++i) {
                        simpleSpawner.doSpawn(true);
                    }
                    if (spawnInfo.getRespawnDelay() == 0) {
                        simpleSpawner.stopRespawn();
                    } else {
                        simpleSpawner.startRespawn();
                    }
                    this.addSpawn(simpleSpawner);
                }
            }
        }
    }

    public void init(IntObjectMap<DoorTemplate> intObjectMap, Map<String, ZoneTemplate> map) {
        Object object;
        if (!intObjectMap.isEmpty()) {
            this._doors = new HashIntObjectMap<DoorInstance>(intObjectMap.size());
        }
        for (DoorTemplate object2 : intObjectMap.values()) {
            object = new DoorInstance(IdFactory.getInstance().getNextId(), object2);
            ((GameObject)object).setReflection(this);
            ((Creature)object).setIsInvul(true);
            ((Creature)object).spawnMe(object2.getLoc());
            if (object2.isOpened()) {
                ((DoorInstance)object).openMe();
            }
            this._doors.put(object2.getNpcId(), (DoorInstance)object);
        }
        this.cG();
        if (!map.isEmpty()) {
            this._zones = new HashMap<String, Zone>(map.size());
        }
        for (ZoneTemplate zoneTemplate : map.values()) {
            if (this.isDefault() && !zoneTemplate.isDefault()) continue;
            object = new Zone(zoneTemplate);
            ((Zone)object).setReflection(this);
            switch (((Zone)object).getType()) {
                case no_landing: 
                case SIEGE: {
                    ((Zone)object).addListener(NoLandingZoneListener.STATIC);
                    break;
                }
                case RESIDENCE: {
                    ((Zone)object).addListener(ResidenceEnterLeaveListenerImpl.STATIC);
                }
            }
            if (zoneTemplate.isEnabled()) {
                ((Zone)object).setActive(true);
            }
            this._zones.put(zoneTemplate.getName(), (Zone)object);
        }
    }

    private void a(IntObjectMap<InstantZone.DoorInfo> intObjectMap, Map<String, InstantZone.ZoneInfo> map) {
        Object object;
        if (!intObjectMap.isEmpty()) {
            this._doors = new HashIntObjectMap<DoorInstance>(intObjectMap.size());
        }
        for (InstantZone.DoorInfo object2 : intObjectMap.values()) {
            object = new DoorInstance(IdFactory.getInstance().getNextId(), object2.getTemplate());
            ((GameObject)object).setReflection(this);
            ((Creature)object).setIsInvul(object2.isInvul());
            ((Creature)object).spawnMe(object2.getTemplate().getLoc());
            if (object2.isOpened()) {
                ((DoorInstance)object).openMe();
            }
            this._doors.put(object2.getTemplate().getNpcId(), (DoorInstance)object);
        }
        this.cG();
        if (!map.isEmpty()) {
            this._zones = new HashMap<String, Zone>(map.size());
        }
        for (InstantZone.ZoneInfo zoneInfo : map.values()) {
            object = new Zone(zoneInfo.getTemplate());
            ((Zone)object).setReflection(this);
            switch (((Zone)object).getType()) {
                case no_landing: 
                case SIEGE: {
                    ((Zone)object).addListener(NoLandingZoneListener.STATIC);
                    break;
                }
                case RESIDENCE: {
                    ((Zone)object).addListener(ResidenceEnterLeaveListenerImpl.STATIC);
                }
            }
            if (zoneInfo.isActive()) {
                ((Zone)object).setActive(true);
            }
            this._zones.put(zoneInfo.getTemplate().getName(), (Zone)object);
        }
    }

    private void cG() {
        for (DoorInstance doorInstance : this._doors.values()) {
            if (doorInstance.getTemplate().getMasterDoor() <= 0) continue;
            DoorInstance doorInstance2 = this.getDoor(doorInstance.getTemplate().getMasterDoor());
            doorInstance2.addListener(new MasterOnOpenCloseListenerImpl(doorInstance));
        }
    }

    public void openDoor(int n) {
        DoorInstance doorInstance = this._doors.get(n);
        if (doorInstance != null) {
            doorInstance.openMe();
        }
    }

    public void closeDoor(int n) {
        DoorInstance doorInstance = this._doors.get(n);
        if (doorInstance != null) {
            doorInstance.closeMe();
        }
    }

    public void clearReflection(int n, boolean bl) {
        if (this.isDefault() || this.isStatic()) {
            return;
        }
        for (NpcInstance creature : this.getNpcs()) {
            creature.deleteMe();
        }
        this.startCollapseTimer((long)(n * 60) * 1000L);
        if (bl) {
            for (Player player : this.getPlayers()) {
                if (player == null) continue;
                for (Player player2 : player) {
                    player2.sendMessage(new CustomMessage("THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES", player2, new Object[0]).addNumber(n));
                }
            }
        }
    }

    public NpcInstance addSpawnWithoutRespawn(int n, Location location, int n2) {
        Location location2 = n2 > 0 ? Location.findPointToStay(location, 0, n2, this.getGeoIndex()).setH(location.h) : location;
        return NpcUtils.spawnSingle(n, location2, this);
    }

    public NpcInstance addSpawnWithRespawn(int n, Location location, int n2, int n3) {
        SimpleSpawner simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(n));
        simpleSpawner.setLoc(n2 > 0 ? Location.findPointToStay(location, 0, n2, this.getGeoIndex()) : location);
        simpleSpawner.setReflection(this);
        simpleSpawner.setAmount(1);
        simpleSpawner.setRespawnDelay(n3);
        simpleSpawner.doSpawn(true);
        simpleSpawner.startRespawn();
        return simpleSpawner.getLastSpawn();
    }

    public boolean isDefault() {
        return this.getId() <= 0;
    }

    public boolean isStatic() {
        return false;
    }

    public int[] getVisitors() {
        return this._visitors.toArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setReenterTime(long l) {
        int[] nArray = null;
        this.lock.lock();
        try {
            nArray = this._visitors.toArray();
        }
        finally {
            this.lock.unlock();
        }
        if (nArray != null) {
            for (int n : nArray) {
                try {
                    Player player = World.getPlayer(n);
                    if (player != null) {
                        player.setInstanceReuse(this.getInstancedZoneId(), l);
                        continue;
                    }
                    InstanceReuseDAO.getInstance().setReuse(n, this.getInstancedZoneId(), l);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    protected void onCreate() {
        ReflectionManager.getInstance().add(this);
    }

    public static Reflection createReflection(int n) {
        if (n > 0) {
            throw new IllegalArgumentException("id should be <= 0");
        }
        return new Reflection(n);
    }

    public void init(InstantZone instantZone) {
        this.setName(instantZone.getName());
        this.setInstancedZone(instantZone);
        if (instantZone.getMapX() >= 0) {
            int n = GeoEngine.NextGeoIndex(instantZone.getMapX(), instantZone.getMapY(), this.getId());
            this.setGeoIndex(n);
        }
        this.setTeleportLoc(instantZone.getTeleportCoord());
        if (instantZone.getReturnCoords() != null) {
            this.setReturnLoc(instantZone.getReturnCoords());
        }
        this.fillSpawns(instantZone.getSpawnsInfo());
        if (instantZone.getSpawns().size() > 0) {
            this._spawners = new HashMap<String, List<Spawner>>(instantZone.getSpawns().size());
            for (Map.Entry<String, InstantZone.SpawnInfo2> entry : instantZone.getSpawns().entrySet()) {
                ArrayList<Spawner> arrayList = new ArrayList<Spawner>(entry.getValue().getTemplates().size());
                this._spawners.put(entry.getKey(), arrayList);
                for (SpawnTemplate spawnTemplate : entry.getValue().getTemplates()) {
                    HardSpawner hardSpawner = new HardSpawner(spawnTemplate);
                    arrayList.add(hardSpawner);
                    hardSpawner.setAmount(spawnTemplate.getCount());
                    hardSpawner.setRespawnDelay(spawnTemplate.getRespawn(), spawnTemplate.getRespawnRandom());
                    hardSpawner.setReflection(this);
                    hardSpawner.setRespawnTime(0);
                }
                if (!entry.getValue().isSpawned()) continue;
                this.spawnByGroup(entry.getKey());
            }
        }
        this.a(instantZone.getDoors(), instantZone.getZones());
        if (!this.isStatic()) {
            this.setCollapseIfEmptyTime(instantZone.getCollapseIfEmpty());
            this.startCollapseTimer((long)(instantZone.getTimelimit() * 60) * 1000L);
        }
        this.onCreate();
    }

    public void spawnByGroup(String string) {
        List<Spawner> list = this._spawners.get(string);
        if (list == null) {
            throw new IllegalArgumentException();
        }
        for (Spawner spawner : list) {
            spawner.init();
        }
    }

    public void despawnByGroup(String string) {
        List<Spawner> list = this._spawners.get(string);
        if (list == null) {
            throw new IllegalArgumentException();
        }
        for (Spawner spawner : list) {
            spawner.deleteAll();
        }
    }

    public Collection<Zone> getZones() {
        return this._zones.values();
    }

    public <T extends Listener<Reflection>> boolean addListener(T t) {
        return this.PM.add(t);
    }

    public <T extends Listener<Reflection>> boolean removeListener(T t) {
        return this.PM.remove(t);
    }

    public class ReflectionListenerList
    extends ListenerList<Reflection> {
        public void onCollapse() {
            this.forEachListener(OnReflectionCollapseListener.class, onReflectionCollapseListener -> onReflectionCollapseListener.onReflectionCollapse(Reflection.this));
        }
    }
}

