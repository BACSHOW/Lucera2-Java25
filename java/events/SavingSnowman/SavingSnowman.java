/*
 * Decompiled with CFR 0.152.
 */
package events.SavingSnowman;

import ai.FlyingSantaAI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.util.RandomUtils;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.data.xml.holder.ZoneHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Summon;
import l2.gameserver.model.Territory;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.ChatType;
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
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SavingSnowman
extends Functions
implements OnDeathListener,
OnPlayerEnterListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(SavingSnowman.class);
    private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
    private static ScheduledFuture<?> hR;
    private static ScheduledFuture<?> hS;
    private static ScheduledFuture<?> hT;
    private static ScheduledFuture<?> hU;
    private static ScheduledFuture<?> hV;
    public static SnowmanState _snowmanState;
    private static NpcInstance hW;
    private static NpcInstance hX;
    private static final int hY = 1800000;
    private static final int hZ = 0x6DDD00;
    private static final int ia = 60000;
    private static final int ib = 600000;
    private static final int ic = 600000;
    private static final int ie = 6600000;
    private static final int cfr_renamed_1 = 13184;
    private static final int ig = 13006;
    private static final int ih = 13186;
    private static final int ii = 13160;
    private static final int ij = 13183;
    private static int[] ik;
    private static long[] il;
    private static double im;
    private static boolean _active;
    private static final Location[] in;
    private static final List<Pair<Pair<Integer, Long>, Double>> io;
    private static final double iq;
    private static final ReentrantLock ir;

    @Override
    public void onLoad() {
        if (SavingSnowman.isActive()) {
            _active = true;
            this.spawnEventManagers();
            _log.info("Loaded Event: SavingSnowman [state: activated]");
            hS = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveTask(), 1800000L, 0x6DDD00L);
            hT = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SayTask(), 600000L, 600000L);
            hV = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SantaTask(), 6600000L, 6600000L);
            _snowmanState = SnowmanState.SAVED;
            PlayerListenerList.addGlobal(this);
            CharListenerList.addGlobal(this);
        } else {
            _log.debug("Loaded Event: SavingSnowman [state: deactivated]");
        }
    }

    private static boolean isActive() {
        return SavingSnowman.IsActive("SavingSnowman");
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (SavingSnowman.SetActive("SavingSnowman", true)) {
            this.spawnEventManagers();
            _log.info("Event 'SavingSnowman' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.SavingSnowman.AnnounceEventStarted", null);
            if (hS == null) {
                hS = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveTask(), 1800000L, 0x6DDD00L);
            }
            if (hT == null) {
                hT = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SayTask(), 600000L, 600000L);
            }
            if (hV == null) {
                hV = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SantaTask(), 6600000L, 6600000L);
            }
            _snowmanState = SnowmanState.SAVED;
            PlayerListenerList.addGlobal(this);
            CharListenerList.addGlobal(this);
        } else {
            player.sendMessage("Event 'SavingSnowman' already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (SavingSnowman.SetActive("SavingSnowman", false)) {
            this.unSpawnEventManagers();
            if (hW != null) {
                hW.deleteMe();
            }
            if (hX != null) {
                hX.deleteMe();
            }
            _log.info("Event 'SavingSnowman' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.SavingSnowman.AnnounceEventStoped", null);
            if (hS != null) {
                hS.cancel(true);
                hS = null;
            }
            if (hT != null) {
                hT.cancel(true);
                hT = null;
            }
            if (hU != null) {
                hU.cancel(true);
                hU = null;
            }
            if (hV != null) {
                hV.cancel(true);
                hV = null;
            }
            _snowmanState = SnowmanState.SAVED;
            PlayerListenerList.removeGlobal(this);
            CharListenerList.removeGlobal(this);
        } else {
            player.sendMessage("Event 'SavingSnowman' not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    private void spawnEventManagers() {
        int[][] nArrayArray = new int[][]{{83640, 147976, -3400, 24575}, {146405, 28360, -2269, 49648}, {19319, 144919, -3103, 31135}, {-82805, 149890, -3129, 16384}, {-12347, 122549, -3104, 16384}, {110642, 220165, -3655, 61898}, {116619, 75463, -2721, 20881}, {85513, 16014, -3668, 23681}, {81999, 53793, -1496, 61621}, {148159, -55484, -2734, 44315}, {44185, -48502, -797, 27479}, {86899, -143229, -1293, 8192}};
        int[][] nArrayArray2 = new int[][]{{83656, 148056, -3400, 24576}, {83204, 147912, -3368, 32768}, {83204, 148395, -3368, 32768}, {83204, 148844, -3368, 32768}, {83202, 149325, -3368, 32768}, {146445, 28360, -2269, 0}, {19319, 144959, -3103, 0}, {-82845, 149890, -3129, 0}, {-12387, 122549, -3104, 0}, {110602, 220165, -3655, 0}, {116659, 75463, -2721, 0}, {85553, 16014, -3668, 0}, {81999, 53743, -1496, 0}, {148199, -55484, -2734, 0}, {44185, -48542, -797, 0}, {86859, -143229, -1293, 0}};
        SavingSnowman.SpawnNPCs(13184, nArrayArray, _spawns);
        SavingSnowman.SpawnNPCs(13006, nArrayArray2, _spawns);
    }

    private void unSpawnEventManagers() {
        SavingSnowman.deSpawnNPCs(_spawns);
    }

    @Override
    public void onReload() {
        this.unSpawnEventManagers();
        if (hS != null) {
            hS.cancel(true);
        }
        if (hT != null) {
            hT.cancel(true);
        }
        if (hV != null) {
            hV.cancel(true);
        }
        _snowmanState = SnowmanState.SAVED;
    }

    @Override
    public void onShutdown() {
        PlayerListenerList.removeGlobal(this);
        CharListenerList.removeGlobal(this);
        this.unSpawnEventManagers();
    }

    public static void spawnRewarder(Player player) {
        for (NpcInstance npcInstance : player.getAroundNpc(1500, 300)) {
            if (npcInstance.getNpcId() != 13186) continue;
            return;
        }
        Object object = Location.findPointToStay(player, 300, 400);
        for (int i = 0; i < 20 && !GeoEngine.canSeeCoord(player, ((Location)object).x, ((Location)object).y, ((Location)object).z, false); ++i) {
            object = Location.findPointToStay(player, 300, 400);
        }
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(13186);
        if (npcTemplate == null) {
            _log.info("WARNING! events.SavingSnowman.spawnRewarder template is null for npc: 13186");
            Thread.dumpStack();
            return;
        }
        NpcInstance npcInstance = new NpcInstance(IdFactory.getInstance().getNextId(), npcTemplate);
        npcInstance.setLoc((Location)object);
        npcInstance.setHeading((int)(Math.atan2(((Location)object).y - player.getY(), ((Location)object).x - player.getX()) * 10430.378350470453) + 32768);
        npcInstance.spawnMe();
        Functions.npcSayCustomMessage(npcInstance, "scripts.events.SavingSnowman.RewarderPhrase1", new Object[0]);
        Location location = Location.findPointToStay(player, 40, 50);
        npcInstance.setSpawnedLoc(location);
        npcInstance.broadcastPacket(new CharMoveToLocation(npcInstance.getObjectId(), npcInstance.getLoc(), location));
        SavingSnowman.executeTask("events.SavingSnowman.SavingSnowman", "reward", new Object[]{npcInstance, player}, 5000L);
    }

    public static void reward(NpcInstance npcInstance, Player player) {
        if (!_active || npcInstance == null || player == null) {
            return;
        }
        Functions.npcSayCustomMessage(npcInstance, "scripts.events.SavingSnowman.RewarderPhrase2", player.getName());
        Functions.addItem(player, 14616, 1L);
        SavingSnowman.executeTask("events.SavingSnowman.SavingSnowman", "removeRewarder", new Object[]{npcInstance}, 5000L);
    }

    public static void removeRewarder(NpcInstance npcInstance) {
        if (!_active || npcInstance == null) {
            return;
        }
        Functions.npcSayCustomMessage(npcInstance, "scripts.events.SavingSnowman.RewarderPhrase3", new Object[0]);
        Location location = npcInstance.getSpawnedLoc();
        double d = PositionUtils.convertHeadingToRadian(npcInstance.getHeading());
        int n = location.x - (int)(Math.sin(d) * 300.0);
        int n2 = location.y + (int)(Math.cos(d) * 300.0);
        int n3 = location.z;
        npcInstance.broadcastPacket(new CharMoveToLocation(npcInstance.getObjectId(), location, new Location(n, n2, n3)));
        SavingSnowman.executeTask("events.SavingSnowman.SavingSnowman", "unspawnRewarder", new Object[]{npcInstance}, 2000L);
    }

    public static void unspawnRewarder(NpcInstance npcInstance) {
        if (!_active || npcInstance == null) {
            return;
        }
        npcInstance.decayMe();
        npcInstance.deleteMe();
    }

    public void buff() {
        Player player = this.getSelf();
        if (!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300.0) {
            return;
        }
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (_snowmanState != SnowmanState.SAVED) {
            this.show("default/13184-3.htm", player);
            return;
        }
        Summon summon = player.getPet();
        if (summon != null) {
            // empty if block
        }
    }

    public void locateSnowman() {
        Player player = this.getSelf();
        if (!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300.0) {
            return;
        }
        if (hW != null) {
            player.sendPacket(new RadarControl(2, 2, hW.getLoc()), new RadarControl(0, 1, hW.getLoc()));
            player.sendPacket((IStaticPacket)new SystemMessage(2010).addZoneName(hW.getLoc()).addString("\u0421\u043d\u0435\u0433\u043e\u0432\u0438\u043a\u0430 \u0437\u0430\u0445\u0432\u0430\u0442\u0438\u043b\u0438 \u0432 "));
        } else {
            player.sendPacket((IStaticPacket)Msg.YOUR_TARGET_CANNOT_BE_FOUND);
        }
    }

    public void lotery() {
        Player player = this.getSelf();
        if (!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300.0) {
            return;
        }
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (SavingSnowman.getItemCount(player, Config.EVENT_SAVING_SNOWMAN_LOTERY_CURENCY) < Config.EVENT_SAVING_SNOWMAN_LOTERY_PRICE) {
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            return;
        }
        SavingSnowman.removeItem(player, Config.EVENT_SAVING_SNOWMAN_LOTERY_CURENCY, Config.EVENT_SAVING_SNOWMAN_LOTERY_PRICE);
        if (iq != 100.0) {
            _log.info("WARN: Sum != 100: " + iq);
        }
        Pair pair = (Pair)RandomUtils.pickRandomSortedGroup(io, iq);
        int n = (Integer)pair.getKey();
        long l = (Long)pair.getValue();
        SavingSnowman.addItem(player, n, l);
    }

    public String DialogAppend_13184(Integer n) {
        if (n != 0) {
            return "";
        }
        return " (" + Util.formatAdena(Config.EVENT_SAVING_SNOWMAN_LOTERY_PRICE) + " Adena)";
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

    public void captureSnowman() {
        Location location = SavingSnowman.O();
        for (Player eventOwner2 : GameObjectsStorage.getAllPlayersForIterate()) {
            Announcements.getInstance().announceToPlayerByCustomMessage(eventOwner2, "scripts.events.SavingSnowman.AnnounceSnowmanCaptured", null, ChatType.ANNOUNCEMENT);
            eventOwner2.sendPacket((IStaticPacket)new SystemMessage(2010).addZoneName(location).addString("\u0418\u0449\u0438\u0442\u0435 \u0421\u043d\u0435\u0433\u043e\u0432\u0438\u043a\u0430 \u0432 "));
            eventOwner2.sendPacket(new RadarControl(2, 2, location), new RadarControl(0, 1, location));
        }
        Object object = NpcHolder.getInstance().getTemplate(13160);
        if (object == null) {
            _log.info("WARNING! events.SavingSnowman.captureSnowman template is null for npc: 13160");
            Thread.dumpStack();
            return;
        }
        SimpleSpawner simpleSpawner = new SimpleSpawner((NpcTemplate)object);
        simpleSpawner.setLoc(location);
        simpleSpawner.setAmount(1);
        simpleSpawner.setRespawnDelay(0);
        hW = simpleSpawner.doSpawn(true);
        if (hW == null) {
            return;
        }
        object = NpcHolder.getInstance().getTemplate(13183);
        if (object == null) {
            _log.info("WARNING! events.SavingSnowman.captureSnowman template is null for npc: 13183");
            Thread.dumpStack();
            return;
        }
        Location location2 = Location.findPointToStay(hW.getX(), hW.getY(), hW.getZ(), 100, 120, hW.getReflection().getGeoIndex());
        SimpleSpawner simpleSpawner2 = new SimpleSpawner((NpcTemplate)object);
        simpleSpawner2.setLoc(location2);
        simpleSpawner2.setAmount(1);
        simpleSpawner2.setRespawnDelay(0);
        hX = simpleSpawner2.doSpawn(true);
        if (hX == null) {
            return;
        }
        _snowmanState = SnowmanState.CAPTURED;
        if (hR != null) {
            hR.cancel(true);
            hR = null;
        }
        hR = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ShoutTask(), 1L, 60000L);
        if (hU != null) {
            hU.cancel(true);
            hU = null;
        }
        hU = SavingSnowman.executeTask("events.SavingSnowman.SavingSnowman", "eatSnowman", new Object[0], 600000L);
    }

    public static void eatSnowman() {
        if (hW == null || hX == null) {
            return;
        }
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceSnowmanKilled", null, ChatType.ANNOUNCEMENT);
        }
        _snowmanState = SnowmanState.KILLED;
        if (hR != null) {
            hR.cancel(true);
            hR = null;
        }
        hW.deleteMe();
        hX.deleteMe();
    }

    public static void freeSnowman(Creature creature) {
        ir.lock();
        try {
            if (_snowmanState != SnowmanState.CAPTURED || hW == null || creature == null || !creature.isPlayable()) {
                return;
            }
            _snowmanState = SnowmanState.SAVED;
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceSnowmanSaved", null, ChatType.ANNOUNCEMENT);
            }
            if (hR != null) {
                hR.cancel(true);
                hR = null;
            }
            if (hU != null) {
                hU.cancel(true);
                hU = null;
            }
            Player player = creature.getPlayer();
            Functions.npcSayCustomMessage(hW, "scripts.events.SavingSnowman.SnowmanSayTnx", player.getName());
            SavingSnowman.addItem(player, 14616, 5L);
            if (creature.isPlayer()) {
                SavingSnowman.spawnRewarder(creature.getPlayer());
            }
            hW.decayMe();
            hW.deleteMe();
        }
        finally {
            ir.unlock();
        }
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (_active && creature2 != null && _active && SavingSnowman.SimpleCheckDrop(creature, creature2) && Rnd.chance(im)) {
            int n = Rnd.get(ik.length);
            ((NpcInstance)creature).dropItem(creature2.getPlayer(), ik[n], il[n]);
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceEventStarted", null);
        }
    }

    static {
        ik = new int[]{17130, 17131, 17132, 17133, 17134, 17135, 17136, 17137};
        il = new long[]{1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L};
        im = 10.0;
        _active = false;
        in = new Location[]{new Location(82632, 148712, -3472), new Location(147448, 28552, -2272), new Location(145873, -54756, -2807)};
        io = (List)Arrays.asList(new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(14612, 1L), 20.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(9627, 1L), 10.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(5561, 1L), 8.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(5560, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(22206, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(22207, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21587, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21588, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21583, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21126, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21124, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(20020, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(13490, 1L), 5.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21618, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21924, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21925, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21926, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21944, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21945, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21946, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21938, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21956, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21963, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21964, 1L), 1.0), new ImmutablePair<ImmutablePair<Integer, Long>, Double>(new ImmutablePair<Integer, Long>(21965, 1L), 1.0));
        Collections.sort(io, RandomUtils.DOUBLE_GROUP_COMPARATOR);
        double d = 0.0;
        for (Pair<Pair<Integer, Long>, Double> pair : io) {
            d += pair.getRight().doubleValue();
        }
        iq = d;
        ir = new ReentrantLock();
    }

    public class SaveTask
    implements Runnable {
        @Override
        public void run() {
            if (!_active || _snowmanState == SnowmanState.CAPTURED) {
                return;
            }
            SavingSnowman.this.captureSnowman();
        }
    }

    public class ShoutTask
    implements Runnable {
        @Override
        public void run() {
            if (!_active || hW == null || _snowmanState != SnowmanState.CAPTURED) {
                return;
            }
            Functions.npcShoutCustomMessage(hW, "scripts.events.SavingSnowman.SnowmanShout", new Object[0]);
        }
    }

    public class SantaTask
    implements Runnable {
        @Override
        public void run() {
            if (!_active) {
                return;
            }
            for (Location location : in) {
                NpcInstance npcInstance = NpcHolder.getInstance().getTemplate(13186).getNewInstance();
                npcInstance.setAI(new FlyingSantaAI(npcInstance));
                npcInstance.spawnMe(location);
                npcInstance.setSpawnedLoc(location);
            }
        }
    }

    public class SayTask
    implements Runnable {
        @Override
        public void run() {
            if (!_active) {
                return;
            }
            for (SimpleSpawner simpleSpawner : _spawns) {
                if (simpleSpawner.getCurrentNpcId() != 13184) continue;
                Functions.npcSayCustomMessage(simpleSpawner.getLastSpawn(), "scripts.events.SavingSnowman.SantaSay", new Object[0]);
            }
        }
    }

    public static enum SnowmanState {
        CAPTURED,
        KILLED,
        SAVED;

    }
}

