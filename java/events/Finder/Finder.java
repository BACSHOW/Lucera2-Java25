/*
 * Decompiled with CFR 0.152.
 */
package events.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.data.xml.holder.ZoneHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Territory;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.CharMoveToLocation;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.ZoneTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Finder
extends Functions
implements OnDeathListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(Finder.class);
    private static final String hg = "Finder";
    private static boolean _active = false;
    private static final long hh = 600000L;
    private static final long hi = 60000L;
    private static final int hj = 40017;
    private static final int hk = 40016;
    private static final int hl = 40019;
    private static final String hm = "[event_finder_list]";
    private static NpcInstance hn;
    private static NpcInstance ho;
    private static Location hp;
    private static ScheduledFuture<?> hq;
    private static ScheduledFuture<?> hr;
    private static Map<String, ScheduledFuture<?>> hs;

    private static boolean isActive() {
        return Finder.IsActive(hg);
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (Finder.SetActive(hg, true)) {
            this.activate();
        } else {
            player.sendMessage("Event 'Finder' already active.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (Finder.SetActive(hg, false)) {
            this.N();
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.Finder.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'Finder' already diactivated.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    private void activate() {
        SpawnManager.getInstance().spawn(hm);
        if (hs != null) {
            for (ScheduledFuture<?> scheduledFuture : hs.values()) {
                scheduledFuture.cancel(true);
            }
            hs = null;
        }
        hs = Finder.ScheduleTimeStarts(new EventTask(), Config.EVENT_FinderHostageStartTime);
        _log.info("Event 'Finder' started on " + Arrays.toString(hs.keySet().toArray(new String[hs.keySet().size()])));
    }

    private void N() {
        if (hs != null) {
            for (ScheduledFuture<?> scheduledFuture : hs.values()) {
                scheduledFuture.cancel(true);
            }
            hs = null;
        }
        SpawnManager.getInstance().despawn(hm);
    }

    private static Location O() {
        ArrayList<ZoneTemplate> arrayList = new ArrayList<ZoneTemplate>();
        for (ZoneTemplate zoneTemplate : ZoneHolder.getInstance().getZones().values()) {
            if (zoneTemplate == null || !zoneTemplate.isDefault()) continue;
            arrayList.add(zoneTemplate);
        }
        Territory territory = ((ZoneTemplate)arrayList.get(Rnd.get(arrayList.size()))).getTerritory();
        return territory.getRandomLoc(0);
    }

    public void spawnHostageAndRaider() {
        if (!_active) {
            return;
        }
        Location location = Finder.O();
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(40017);
        if (npcTemplate == null) {
            _log.info("WARNING! events.Finder.Finder.spawnHostageAndRaider template is null for npc: 40017");
            Thread.dumpStack();
            return;
        }
        SimpleSpawner simpleSpawner = new SimpleSpawner(npcTemplate);
        simpleSpawner.setLoc(location);
        simpleSpawner.setAmount(1);
        simpleSpawner.setRespawnDelay(0);
        simpleSpawner.stopRespawn();
        hn = simpleSpawner.doSpawn(true);
        if (hn == null) {
            return;
        }
        npcTemplate = NpcHolder.getInstance().getTemplate(40016);
        if (npcTemplate == null) {
            _log.info("WARNING! events.Finder.Finder.spawnHostageAndRaider template is null for npc: 40016");
            Thread.dumpStack();
            return;
        }
        Location location2 = Location.findPointToStay(hn.getX(), hn.getY(), hn.getZ(), 100, 120, hn.getReflection().getGeoIndex());
        SimpleSpawner simpleSpawner2 = new SimpleSpawner(npcTemplate);
        simpleSpawner2.setLoc(location2);
        simpleSpawner2.setAmount(1);
        simpleSpawner2.setRespawnDelay(0);
        simpleSpawner2.stopRespawn();
        ho = simpleSpawner2.doSpawn(true);
        ho.addListener(this);
        if (hq != null) {
            hq.cancel(true);
            hq = null;
        }
        hq = ThreadPoolManager.getInstance().schedule(new HostageKilledTask(), 600000L);
        if (hr != null) {
            hr.cancel(true);
            hr = null;
        }
        hr = ThreadPoolManager.getInstance().schedule(new ShoutTask(), 60000L);
        hp = hn.getLoc();
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.Finder.AnnounceHostageTaken", null, ChatType.ANNOUNCEMENT);
        }
    }

    private static void P() {
        if (hn != null && hn.getSpawn() != null) {
            hn.deleteMe();
        }
        if (ho != null && ho.getSpawn() != null) {
            ho.deleteMe();
        }
        hn = null;
        ho = null;
        hp = null;
        if (hr != null) {
            hr.cancel(true);
            hr = null;
        }
    }

    public void GetPoint() {
        Player player = this.getSelf();
        if (!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300.0) {
            return;
        }
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (hp != null) {
            player.sendPacket((IStaticPacket)new SystemMessage(2010).addZoneName(hp).addString(new CustomMessage("scripts.events.Finder.HostageAtS1", player, new Object[0]).toString()));
            player.sendPacket(new RadarControl(2, 2, hp), new RadarControl(0, 1, hp));
        } else {
            player.sendMessage(new CustomMessage("scripts.events.Finder.NoHostage", player, new Object[0]));
        }
    }

    @Override
    public void onLoad() {
        if (Finder.isActive()) {
            _active = true;
            this.activate();
        } else {
            _log.debug("Loaded Event: 'Finder' [state: deactivated]");
        }
    }

    @Override
    public void onReload() {
        if (_active) {
            this.N();
            this.activate();
        }
    }

    @Override
    public void onShutdown() {
        this.N();
    }

    public static void spawnRewarder(Player player) {
        for (NpcInstance npcInstance : player.getAroundNpc(1500, 300)) {
            if (npcInstance.getNpcId() != 40019) continue;
            return;
        }
        Object object = Location.findPointToStay(player, 300, 400);
        for (int i = 0; i < 20 && !GeoEngine.canSeeCoord(player, ((Location)object).x, ((Location)object).y, ((Location)object).z, false); ++i) {
            object = Location.findPointToStay(player, 300, 400);
        }
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(40019);
        if (npcTemplate == null) {
            _log.info("WARNING! events.SavingSnowman.spawnRewarder template is null for npc: 40019");
            Thread.dumpStack();
            return;
        }
        NpcInstance npcInstance = new NpcInstance(IdFactory.getInstance().getNextId(), npcTemplate);
        npcInstance.setLoc((Location)object);
        npcInstance.setHeading((int)(Math.atan2(((Location)object).y - player.getY(), ((Location)object).x - player.getX()) * 10430.378350470453) + 32768);
        npcInstance.spawnMe();
        Functions.npcSayCustomMessage(npcInstance, "scripts.events.Finder.RewarderPhrase1", new Object[0]);
        Location location = Location.findFrontPosition(player, player, 40, 50);
        npcInstance.setSpawnedLoc(location);
        npcInstance.broadcastPacket(new CharMoveToLocation(npcInstance.getObjectId(), npcInstance.getLoc(), location));
        Finder.executeTask("events.Finder.Finder", "reward", new Object[]{npcInstance, player}, 5000L);
    }

    public static void reward(NpcInstance npcInstance, Player player) {
        if (!_active || npcInstance == null || player == null) {
            return;
        }
        Functions.npcSayCustomMessage(npcInstance, "scripts.events.Finder.RewarderPhrase2", player.getName());
        Functions.addItem(player, Config.EVENT_FINDER_REWARD_ID, Config.EVENT_FINDER_ITEM_COUNT);
        Finder.executeTask("events.Finder.Finder", "removeRewarder", new Object[]{npcInstance}, 5000L);
    }

    public static void removeRewarder(NpcInstance npcInstance) {
        if (!_active || npcInstance == null) {
            return;
        }
        Functions.npcSayCustomMessage(npcInstance, "scripts.events.Finder.RewarderPhrase3", new Object[0]);
        Location location = npcInstance.getSpawnedLoc();
        double d = PositionUtils.convertHeadingToRadian(npcInstance.getHeading());
        int n = location.x - (int)(Math.sin(d) * 300.0);
        int n2 = location.y + (int)(Math.cos(d) * 300.0);
        int n3 = location.z;
        npcInstance.broadcastPacket(new CharMoveToLocation(npcInstance.getObjectId(), location, new Location(n, n2, n3)));
        Finder.executeTask("events.Finder.Finder", "unspawnRewarder", new Object[]{npcInstance}, 2000L);
    }

    public static void unspawnRewarder(NpcInstance npcInstance) {
        if (!_active || npcInstance == null) {
            return;
        }
        npcInstance.decayMe();
        npcInstance.deleteMe();
        Finder.P();
    }

    public static void OnDie(Creature creature, Creature creature2) {
        if (!_active) {
            return;
        }
        if (creature2.isPlayer()) {
            _log.info("killed 0 " + creature.getName());
        }
        if (creature.isNpc() && creature == ho && creature2.isPlayer()) {
            _log.info("killed 1 " + creature.getName());
            Player player = creature2.getPlayer();
            if (hq != null) {
                hq.cancel(true);
                hq = null;
            }
            if (hr != null) {
                hr.cancel(true);
                hr = null;
            }
            Finder.spawnRewarder(player);
            _log.info("killed 2 " + creature.getName());
            for (Player creature3 : GameObjectsStorage.getAllPlayersForIterate()) {
                Announcements.getInstance().announceToPlayerByCustomMessage(creature3, "scripts.events.Finder.AnnounceHostageSavedByS1", new String[]{player.getName()}, ChatType.ANNOUNCEMENT);
            }
            for (NpcInstance npcInstance : player.getAroundNpc(1500, 300)) {
                if (npcInstance.getNpcId() != 40017) continue;
                _log.info("killed 3 " + creature.getName());
                Functions.npcSayCustomMessage(npcInstance, "scripts.events.Finder.HostageThx", new Object[0]);
                return;
            }
        }
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        creature.removeListener(this);
        if (!_active || creature2 == null) {
            return;
        }
        if (creature2.isPlayer()) {
            _log.info("killed 0 " + creature.getName());
        }
        if (creature.isNpc() && creature == ho && creature2.isPlayer()) {
            _log.info("killed 1 " + creature.getName());
            Player player = creature2.getPlayer();
            if (hq != null) {
                hq.cancel(true);
                hq = null;
            }
            if (hr != null) {
                hr.cancel(true);
                hr = null;
            }
            Finder.spawnRewarder(player);
            _log.info("killed 2 " + creature.getName());
            for (Player creature3 : GameObjectsStorage.getAllPlayersForIterate()) {
                Announcements.getInstance().announceToPlayerByCustomMessage(creature3, "scripts.events.Finder.AnnounceHostageSavedByS1", new String[]{player.getName()}, ChatType.ANNOUNCEMENT);
            }
            for (NpcInstance npcInstance : player.getAroundNpc(1500, 300)) {
                if (npcInstance.getNpcId() != 40017) continue;
                _log.info("killed 3 " + creature.getName());
                Functions.npcSayCustomMessage(npcInstance, "scripts.events.Finder.HostageThx", new Object[0]);
                return;
            }
        }
    }

    public class ShoutTask
    implements Runnable {
        @Override
        public void run() {
            if (!_active || hn == null || hp == null) {
                return;
            }
            Functions.npcShoutCustomMessage(hn, "scripts.events.Finder.HostageShout", new Object[0]);
        }
    }

    private class HostageKilledTask
    implements Runnable {
        private HostageKilledTask() {
        }

        @Override
        public void run() {
            if (!_active || hn == null || ho == null) {
                return;
            }
            Finder.P();
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.Finder.AnnounceHostageKilled", null, ChatType.ANNOUNCEMENT);
            }
        }
    }

    private class EventTask
    implements Runnable {
        private EventTask() {
        }

        @Override
        public void run() {
            Finder.this.spawnHostageAndRaider();
        }
    }
}

