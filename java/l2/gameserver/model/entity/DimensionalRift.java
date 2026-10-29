/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.entity.DelusionChamber;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.Location;

public class DimensionalRift
extends Reflection {
    protected static final int MILLISECONDS_IN_MINUTE = 60000;
    protected int _roomType;
    protected List<Integer> _completedRooms = new ArrayList<Integer>();
    protected int jumps_current = 0;
    private Future<?> Ps;
    private Future<?> Pt;
    private Future<?> Pr;
    protected int _choosenRoom = -1;
    protected boolean _hasJumped = false;
    protected boolean isBossRoom = false;

    public DimensionalRift(Party party, int n, int n2) {
        Object object;
        this.onCreate();
        this.startCollapseTimer(0x6DDD00L);
        this.setName("Dimensional Rift");
        if (this instanceof DelusionChamber) {
            object = InstantZoneHolder.getInstance().getInstantZone(n + 120);
            this.setInstancedZone((InstantZone)object);
            this.setName(((InstantZone)object).getName());
        }
        this._roomType = n;
        this.setParty(party);
        if (!(this instanceof DelusionChamber)) {
            party.setDimensionalRift(this);
        }
        party.setReflection(this);
        this._choosenRoom = n2;
        this.checkBossRoom(this._choosenRoom);
        object = this.getRoomCoord(this._choosenRoom);
        this.setReturnLoc(party.getPartyLeader().getLoc());
        this.setTeleportLoc((Location)object);
        for (Player player : party.getPartyMembers()) {
            player.setVar("backCoords", this.getReturnLoc().toXYZString(), -1L);
            DimensionalRiftManager.teleToLocation(player, Location.findPointToStay((Location)object, 50, 100, this.getGeoIndex()), this);
            player.setReflection(this);
        }
        this.createSpawnTimer(this._choosenRoom);
        this.createTeleporterTimer();
    }

    public int getType() {
        return this._roomType;
    }

    public int getCurrentRoom() {
        return this._choosenRoom;
    }

    protected void createTeleporterTimer() {
        if (this.Ps != null) {
            this.Ps.cancel(false);
            this.Ps = null;
        }
        this.Ps = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                if (DimensionalRift.this.jumps_current < DimensionalRift.this.getMaxJumps() && DimensionalRift.this.getPlayersInside(true) > 0) {
                    ++DimensionalRift.this.jumps_current;
                    DimensionalRift.this.teleportToNextRoom();
                    DimensionalRift.this.createTeleporterTimer();
                } else {
                    DimensionalRift.this.createNewKillRiftTimer();
                }
            }
        }, this.calcTimeToNextJump());
    }

    public void createSpawnTimer(int n) {
        if (this.Pt != null) {
            this.Pt.cancel(false);
            this.Pt = null;
        }
        final DimensionalRiftManager.DimensionalRiftRoom dimensionalRiftRoom = DimensionalRiftManager.getInstance().getRoom(this._roomType, n);
        this.Pt = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                for (SimpleSpawner simpleSpawner : dimensionalRiftRoom.getSpawns()) {
                    SimpleSpawner simpleSpawner2 = simpleSpawner.clone();
                    simpleSpawner2.setReflection(DimensionalRift.this);
                    DimensionalRift.this.addSpawn(simpleSpawner2);
                    if (!DimensionalRift.this.isBossRoom) {
                        simpleSpawner2.startRespawn();
                    }
                    for (int i = 0; i < simpleSpawner2.getAmount(); ++i) {
                        simpleSpawner2.doSpawn(true);
                    }
                }
                DimensionalRift.this.addSpawnWithoutRespawn(DimensionalRift.this.getManagerId(), dimensionalRiftRoom.getTeleportCoords(), 0);
            }
        }, Config.RIFT_SPAWN_DELAY);
    }

    public synchronized void createNewKillRiftTimer() {
        if (this.Pr != null) {
            this.Pr.cancel(false);
            this.Pr = null;
        }
        this.Pr = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                if (DimensionalRift.this.isCollapseStarted()) {
                    return;
                }
                for (Player player : DimensionalRift.this.getParty().getPartyMembers()) {
                    if (player == null || player.getReflection() != DimensionalRift.this) continue;
                    DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
                }
                DimensionalRift.this.collapse();
            }
        }, 100L);
    }

    public void partyMemberInvited() {
        this.createNewKillRiftTimer();
    }

    public void partyMemberExited(Player player) {
        if (this.getParty().getMemberCount() < Config.RIFT_MIN_PARTY_SIZE || this.getParty().getMemberCount() == 1 || this.getPlayersInside(true) == 0) {
            this.createNewKillRiftTimer();
        }
    }

    public void manualTeleport(Player player, NpcInstance npcInstance) {
        if (!(player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift)) {
            return;
        }
        if (!player.getParty().isLeader(player)) {
            DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npcInstance);
            return;
        }
        if (!this.isBossRoom) {
            if (this._hasJumped) {
                DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/AlreadyTeleported.htm", npcInstance);
                return;
            }
        } else {
            this.manualExitRift(player, npcInstance);
            return;
        }
        this._hasJumped = true;
        this.teleportToNextRoom();
    }

    public void manualExitRift(Player player, NpcInstance npcInstance) {
        if (!player.isInParty() || !player.getParty().isInDimensionalRift()) {
            return;
        }
        if (!player.getParty().isLeader(player)) {
            DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npcInstance);
            return;
        }
        this.createNewKillRiftTimer();
    }

    protected void teleportToNextRoom() {
        this._completedRooms.add(this._choosenRoom);
        for (Spawner object : this.getSpawns()) {
            object.deleteAll();
        }
        int n = DimensionalRiftManager.getInstance().getRooms(this._roomType).size();
        if (this.getType() >= 5 && this.jumps_current == this.getMaxJumps()) {
            this._choosenRoom = 9;
        } else {
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (int i = 1; i <= n; ++i) {
                if (this._completedRooms.contains(i)) continue;
                arrayList.add(i);
            }
            this._choosenRoom = Config.RIFT_BOSS_ROOM_CHANCE > 0 && Rnd.chance(Config.RIFT_BOSS_ROOM_CHANCE) && arrayList.contains(9) ? 9 : (Integer)arrayList.get(Rnd.get(arrayList.size()));
        }
        this.checkBossRoom(this._choosenRoom);
        this.setTeleportLoc(this.getRoomCoord(this._choosenRoom));
        for (Player player : this.getParty().getPartyMembers()) {
            if (player.getReflection() != this) continue;
            DimensionalRiftManager.teleToLocation(player, Location.findPointToStay(this.getRoomCoord(this._choosenRoom), 50, 100, this.getGeoIndex()), this);
        }
        this.createSpawnTimer(this._choosenRoom);
    }

    @Override
    public void collapse() {
        if (this.isCollapseStarted()) {
            return;
        }
        Future<?> future = this.Ps;
        if (future != null) {
            this.Ps = null;
            future.cancel(false);
        }
        if ((future = this.Pt) != null) {
            this.Pt = null;
            future.cancel(false);
        }
        if ((future = this.Pr) != null) {
            this.Pr = null;
            future.cancel(false);
        }
        this._completedRooms = null;
        Party party = this.getParty();
        if (party != null) {
            party.setDimensionalRift(null);
        }
        super.collapse();
    }

    protected long calcTimeToNextJump() {
        if (this.isBossRoom) {
            return 3600000L;
        }
        return (long)Config.RIFT_AUTO_JUMPS_TIME * 60000L + (long)Rnd.get(Config.RIFT_AUTO_JUMPS_TIME_RAND);
    }

    public void memberDead(Player player) {
        if (this.getPlayersInside(true) == 0) {
            this.createNewKillRiftTimer();
        }
    }

    public void usedTeleport(Player player) {
        if (this.getPlayersInside(false) < Config.RIFT_MIN_PARTY_SIZE) {
            this.createNewKillRiftTimer();
        }
    }

    public void checkBossRoom(int n) {
        this.isBossRoom = DimensionalRiftManager.getInstance().getRoom(this._roomType, n).isBossRoom();
    }

    public Location getRoomCoord(int n) {
        return DimensionalRiftManager.getInstance().getRoom(this._roomType, n).getTeleportCoords();
    }

    public int getMaxJumps() {
        return Math.max(Math.min(Config.RIFT_MAX_JUMPS, 8), 1);
    }

    @Override
    public boolean canChampions() {
        return true;
    }

    @Override
    public String getName() {
        return "Dimensional Rift";
    }

    protected int getManagerId() {
        return 31865;
    }

    protected int getPlayersInside(boolean bl) {
        if (this._playerCount == 0) {
            return 0;
        }
        int n = 0;
        for (Player player : this.getPlayers()) {
            if (bl && player.isDead()) continue;
            ++n;
        }
        return n;
    }

    @Override
    public void removeObject(GameObject gameObject) {
        if (gameObject.isPlayer() && this._playerCount <= 1) {
            this.createNewKillRiftTimer();
        }
        super.removeObject(gameObject);
    }
}

