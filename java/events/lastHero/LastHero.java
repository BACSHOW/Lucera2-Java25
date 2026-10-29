/*
 * Decompiled with CFR 0.152.
 */
package events.lastHero;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastHero
extends Functions
implements OnDeathListener,
OnPlayerExitListener,
OnTeleportListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(LastHero.class);
    private static ScheduledFuture<?> jl;
    private static List<Long> lz;
    private static List<Long> lA;
    private static boolean ht;
    private static int _status;
    private static int jq;
    private static int jr;
    private static int p;
    private static int s;
    private static int js;
    private static ScheduledFuture<?> jt;
    private static Zone _zone;
    private static ZoneListener lB;
    private static final Location gB;
    private static boolean _active;

    @Override
    public void onLoad() {
        if (LastHero.isActive()) {
            _active = true;
            CharListenerList.addGlobal(this);
            _zone.addListener(lB);
            jl = ThreadPoolManager.getInstance().scheduleAtFixedRate(new StartTask(), 3600000L, 3600000L);
            _active = ServerVariables.getString("LastHero", "off").equalsIgnoreCase("on");
            _log.info("Loaded Event: LastHero [state: active]");
        } else {
            _active = false;
            _log.debug("Loaded Event: LastHero [state: deactivated]");
        }
    }

    @Override
    public void onReload() {
        _zone.removeListener(lB);
        if (jl != null) {
            jl.cancel(false);
            jl = null;
        }
    }

    @Override
    public void onShutdown() {
        this.onReload();
    }

    private static boolean isActive() {
        return _active;
    }

    public void activateEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (!LastHero.isActive()) {
            if (jl == null) {
                jl = ThreadPoolManager.getInstance().scheduleAtFixedRate(new StartTask(), 3600000L, 3600000L);
            }
            ServerVariables.set("LastHero", "on");
            _log.info("Event 'Last Hero' activated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.LastHero.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'Last Hero' already active.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void deactivateEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (LastHero.isActive()) {
            if (jl != null) {
                jl.cancel(false);
                jl = null;
            }
            ServerVariables.unset("LastHero");
            _log.info("Event 'Last Hero' deactivated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.LastHero.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'LastHero' not active.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    public static boolean isRunned() {
        return ht || _status > 0;
    }

    public static int getMinLevelForCategory(int n) {
        switch (n) {
            case 1: {
                return 20;
            }
            case 2: {
                return 30;
            }
            case 3: {
                return 40;
            }
            case 4: {
                return 52;
            }
            case 5: {
                return 62;
            }
            case 6: {
                return 76;
            }
        }
        return 0;
    }

    public static int getMaxLevelForCategory(int n) {
        switch (n) {
            case 1: {
                return 29;
            }
            case 2: {
                return 39;
            }
            case 3: {
                return 51;
            }
            case 4: {
                return 61;
            }
            case 5: {
                return 75;
            }
            case 6: {
                return 85;
            }
        }
        return 0;
    }

    public static int getCategory(int n) {
        if (n >= 20 && n <= 29) {
            return 1;
        }
        if (n >= 30 && n <= 39) {
            return 2;
        }
        if (n >= 40 && n <= 51) {
            return 3;
        }
        if (n >= 52 && n <= 61) {
            return 4;
        }
        if (n >= 62 && n <= 75) {
            return 5;
        }
        if (n >= 76) {
            return 6;
        }
        return 0;
    }

    public void start(String[] stringArray) {
        Integer n;
        Integer n2;
        Player player = this.getSelf();
        if (stringArray.length != 2) {
            LastHero.show(new CustomMessage("common.Error", player, new Object[0]), player);
            return;
        }
        try {
            n2 = Integer.valueOf(stringArray[0]);
            n = Integer.valueOf(stringArray[1]);
        }
        catch (Exception exception) {
            LastHero.show(new CustomMessage("common.Error", player, new Object[0]), player);
            return;
        }
        jr = n2;
        js = n;
        if (jr == -1) {
            p = 1;
            s = 85;
        } else {
            p = LastHero.getMinLevelForCategory(jr);
            s = LastHero.getMaxLevelForCategory(jr);
        }
        if (jt != null) {
            LastHero.show(new CustomMessage("common.TryLater", player, new Object[0]), player);
            return;
        }
        _status = 0;
        ht = true;
        jq = Config.EVENT_LastHeroTime;
        lz = new CopyOnWriteArrayList<Long>();
        lA = new CopyOnWriteArrayList<Long>();
        String[] stringArray2 = new String[]{String.valueOf(jq), String.valueOf(p), String.valueOf(s)};
        LastHero.sayToAll("scripts.events.LastHero.AnnouncePreStart", stringArray2);
        LastHero.executeTask("events.lastHero.LastHero", "question", new Object[0], 10000L);
        LastHero.executeTask("events.lastHero.LastHero", "announce", new Object[0], 60000L);
    }

    public static void sayToAll(String string, String[] stringArray) {
        Announcements.getInstance().announceByCustomMessage(string, stringArray, ChatType.CRITICAL_ANNOUNCE);
    }

    public static void question() {
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (player == null || player.isDead() || player.getLevel() < p || player.getLevel() > s || !player.getReflection().isDefault() || player.isOlyParticipant() || player.isInObserverMode()) continue;
            player.scriptRequest(new CustomMessage("scripts.events.LastHero.AskPlayer", player, new Object[0]).toString(), "events.lastHero.LastHero:addPlayer", new Object[0]);
        }
    }

    public static void announce() {
        if (lz.size() < 2) {
            LastHero.sayToAll("scripts.events.LastHero.AnnounceEventCancelled", null);
            ht = false;
            _status = 0;
            LastHero.executeTask("events.lastHero.LastHero", "autoContinue", new Object[0], 10000L);
            return;
        }
        if (jq > 1) {
            String[] stringArray = new String[]{String.valueOf(--jq), String.valueOf(p), String.valueOf(s)};
            LastHero.sayToAll("scripts.events.LastHero.AnnouncePreStart", stringArray);
            LastHero.executeTask("events.lastHero.LastHero", "announce", new Object[0], 60000L);
        } else {
            _status = 1;
            ht = false;
            LastHero.sayToAll("scripts.events.LastHero.AnnounceEventStarting", null);
            LastHero.executeTask("events.lastHero.LastHero", "prepare", new Object[0], 5000L);
        }
    }

    public void addPlayer() {
        Player player = this.getSelf();
        if (player == null || !LastHero.checkPlayer(player, true)) {
            return;
        }
        lz.add(player.getStoredId());
        lA.add(player.getStoredId());
        LastHero.show(new CustomMessage("scripts.events.LastHero.Registered", player, new Object[0]), player);
    }

    public static boolean checkPlayer(Player player, boolean bl) {
        if (bl && (!ht || player.isDead())) {
            LastHero.show(new CustomMessage("scripts.events.Late", player, new Object[0]), player);
            return false;
        }
        if (bl && lz.contains(player.getStoredId())) {
            LastHero.show(new CustomMessage("scripts.events.LastHero.Cancelled", player, new Object[0]), player);
            return false;
        }
        if (player.getLevel() < p || player.getLevel() > s) {
            LastHero.show(new CustomMessage("scripts.events.LastHero.CancelledLevel", player, new Object[0]), player);
            return false;
        }
        if (player.isMounted()) {
            LastHero.show(new CustomMessage("scripts.events.LastHero.Cancelled", player, new Object[0]), player);
            return false;
        }
        if (player.isInDuel()) {
            LastHero.show(new CustomMessage("scripts.events.LastHero.CancelledDuel", player, new Object[0]), player);
            return false;
        }
        if (player.getTeam() != TeamType.NONE) {
            LastHero.show(new CustomMessage("scripts.events.LastHero.CancelledOtherEvent", player, new Object[0]), player);
            return false;
        }
        if (player.isOlyParticipant() || bl && ParticipantPool.getInstance().isRegistred(player)) {
            LastHero.show(new CustomMessage("scripts.events.LastHero.CancelledOlympiad", player, new Object[0]), player);
            return false;
        }
        if (player.isInParty() && player.getParty().isInDimensionalRift()) {
            LastHero.show(new CustomMessage("scripts.events.LastHero.CancelledOtherEvent", player, new Object[0]), player);
            return false;
        }
        if (player.isTeleporting()) {
            LastHero.show(new CustomMessage("scripts.events.LastHero.CancelledTeleport", player, new Object[0]), player);
            return false;
        }
        return true;
    }

    public static void prepare() {
        ReflectionUtils.getDoor(24190002).closeMe();
        ReflectionUtils.getDoor(24190003).closeMe();
        LastHero.cleanPlayers();
        LastHero.clearArena();
        LastHero.executeTask("events.lastHero.LastHero", "ressurectPlayers", new Object[0], 1000L);
        LastHero.executeTask("events.lastHero.LastHero", "healPlayers", new Object[0], 2000L);
        LastHero.executeTask("events.lastHero.LastHero", "saveBackCoords", new Object[0], 3000L);
        LastHero.executeTask("events.lastHero.LastHero", "paralyzePlayers", new Object[0], 4000L);
        LastHero.executeTask("events.lastHero.LastHero", "teleportPlayersToColiseum", new Object[0], 5000L);
        LastHero.executeTask("events.lastHero.LastHero", "go", new Object[0], 60000L);
        LastHero.sayToAll("scripts.events.LastHero.AnnounceFinalCountdown", null);
    }

    public static void go() {
        _status = 2;
        LastHero.upParalyzePlayers();
        LastHero.checkLive();
        LastHero.clearArena();
        LastHero.sayToAll("scripts.events.LastHero.AnnounceFight", null);
        jt = LastHero.executeTask("events.lastHero.LastHero", "endBattle", new Object[0], 300000L);
    }

    public static void endBattle() {
        Iterator<Player> iterator;
        ReflectionUtils.getDoor(24190002).openMe();
        ReflectionUtils.getDoor(24190003).openMe();
        _status = 0;
        LastHero.removeAura();
        if (lA.size() == 1 && (iterator = LastHero.i(lA).iterator()).hasNext()) {
            Player player = iterator.next();
            String[] stringArray = new String[]{player.getName()};
            LastHero.sayToAll("scripts.events.LastHero.AnnounceWiner", stringArray);
            LastHero.addItem(player, Config.EVENT_LastHeroItemID, Math.round(Config.EVENT_LastHeroRateFinal ? (double)player.getLevel() * Config.EVENT_LastHeroItemCOUNTFinal : 1.0 * Config.EVENT_LastHeroItemCOUNTFinal));
        }
        LastHero.sayToAll("scripts.events.LastHero.AnnounceEnd", null);
        LastHero.executeTask("events.lastHero.LastHero", "end", new Object[0], 30000L);
        ht = false;
        if (jt != null) {
            jt.cancel(false);
            jt = null;
        }
    }

    public static void end() {
        LastHero.executeTask("events.lastHero.LastHero", "ressurectPlayers", new Object[0], 1000L);
        LastHero.executeTask("events.lastHero.LastHero", "healPlayers", new Object[0], 2000L);
        LastHero.executeTask("events.lastHero.LastHero", "teleportPlayersToSavedCoords", new Object[0], 3000L);
        LastHero.executeTask("events.lastHero.LastHero", "autoContinue", new Object[0], 10000L);
    }

    public void autoContinue() {
        if (js > 0) {
            if (js >= 6) {
                js = 0;
                return;
            }
            this.start(new String[]{"" + (js + 1), "" + (js + 1)});
        }
    }

    public static void saveBackCoords() {
        for (Player player : LastHero.i(lz)) {
            player.setVar("LastHero_backCoords", player.getX() + " " + player.getY() + " " + player.getZ() + " " + player.getReflectionId(), -1L);
        }
    }

    public static void teleportPlayersToColiseum() {
        for (Player player : LastHero.i(lz)) {
            LastHero.unRide(player);
            LastHero.unSummonPet(player, true);
            player.teleToLocation(Location.findPointToStay(gB, 150, 500, ReflectionManager.DEFAULT.getGeoIndex()), ReflectionManager.DEFAULT);
        }
    }

    public static void teleportPlayersToSavedCoords() {
        for (Player player : LastHero.i(lz)) {
            try {
                String[] stringArray;
                String string = player.getVar("LastHero_backCoords");
                if (string == null || string.equals("") || (stringArray = string.split(" ")).length != 4) continue;
                player.teleToLocation(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[2]), Integer.parseInt(stringArray[3]));
                player.unsetVar("LastHero_backCoords");
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void paralyzePlayers() {
        Skill skill = SkillTable.getInstance().getInfo(4515, 1);
        for (Player player : LastHero.i(lz)) {
            player.getEffectList().stopEffect(1411);
            skill.getEffects(player, player, false, false);
            if (player.getPet() == null) continue;
            skill.getEffects(player, player.getPet(), false, false);
        }
    }

    public static void upParalyzePlayers() {
        for (Player player : LastHero.i(lz)) {
            player.getEffectList().stopEffect(4515);
            if (player.getPet() != null) {
                player.getPet().getEffectList().stopEffect(4515);
            }
            player.leaveParty();
        }
    }

    public static void ressurectPlayers() {
        for (Player player : LastHero.i(lz)) {
            if (!player.isDead()) continue;
            player.restoreExp();
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp(), true);
            player.setCurrentMp(player.getMaxMp());
            player.broadcastPacket(new Revive(player));
        }
    }

    public static void healPlayers() {
        for (Player player : LastHero.i(lz)) {
            player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
    }

    public static void cleanPlayers() {
        for (Player player : LastHero.i(lz)) {
            if (LastHero.checkPlayer(player, false)) continue;
            LastHero.removePlayer(player);
        }
    }

    public static void checkLive() {
        CopyOnWriteArrayList<Long> copyOnWriteArrayList = new CopyOnWriteArrayList<Long>();
        for (Long serializable : lA) {
            Player player = GameObjectsStorage.getAsPlayer(serializable);
            if (player == null) continue;
            copyOnWriteArrayList.add(serializable);
        }
        lA = copyOnWriteArrayList;
        for (Player player : LastHero.i(lA)) {
            if (player.isInZone(_zone) && !player.isDead() && !player.isLogoutStarted()) {
                player.setTeam(TeamType.RED);
                continue;
            }
            LastHero.b(player);
        }
        if (lA.size() <= 1) {
            LastHero.endBattle();
        }
    }

    public static void removeAura() {
        for (Player player : LastHero.i(lA)) {
            player.setTeam(TeamType.NONE);
        }
    }

    public static void clearArena() {
        for (Creature creature : _zone.getObjects()) {
            Player player;
            if (creature == null || (player = creature.getPlayer()) == null || lA.contains(player.getStoredId())) continue;
            player.teleToLocation(147451, 46728, -3410);
        }
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (_status > 1 && creature.isPlayer() && creature.getTeam() != TeamType.NONE && lA.contains(creature.getStoredId())) {
            Player player = (Player)creature;
            LastHero.b(player);
            LastHero.checkLive();
            if (creature2 != null && creature2.isPlayer() && creature2.getPlayer().expertiseIndex - player.expertiseIndex > 2 && !creature2.getPlayer().getIP().equals(player.getIP())) {
                LastHero.addItem((Player)creature2, Config.EVENT_LastHeroItemID, Math.round(Config.EVENT_LastHeroRate ? (double)player.getLevel() * Config.EVENT_LastHeroItemCOUNT : 1.0 * Config.EVENT_LastHeroItemCOUNT));
            }
        }
    }

    @Override
    public void onTeleport(Player player, int n, int n2, int n3, Reflection reflection) {
        if (_zone.checkIfInZone(n, n2, n3, reflection)) {
            return;
        }
        if (_status > 1 && player.getTeam() != TeamType.NONE && lA.contains(player.getStoredId())) {
            LastHero.removePlayer(player);
            LastHero.checkLive();
        }
    }

    @Override
    public void onPlayerExit(Player player) {
        if (player.getTeam() == TeamType.NONE) {
            return;
        }
        if (_status == 0 && ht && lA.contains(player.getStoredId())) {
            LastHero.removePlayer(player);
            return;
        }
        if (_status == 1 && lA.contains(player.getStoredId())) {
            LastHero.removePlayer(player);
            try {
                String string = player.getVar("LastHero_backCoords");
                if (string == null || string.equals("")) {
                    return;
                }
                String[] stringArray = string.split(" ");
                if (stringArray.length != 4) {
                    return;
                }
                player.teleToLocation(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[2]), Integer.parseInt(stringArray[3]));
                player.unsetVar("LastHero_backCoords");
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            return;
        }
        if (_status > 1 && player.getTeam() != TeamType.NONE && lA.contains(player.getStoredId())) {
            LastHero.removePlayer(player);
            LastHero.checkLive();
        }
    }

    private static void b(Player player) {
        if (player != null) {
            lA.remove(player.getStoredId());
            player.setTeam(TeamType.NONE);
            LastHero.show(new CustomMessage("scripts.events.LastHero.YouLose", player, new Object[0]), player);
        }
    }

    private static void removePlayer(Player player) {
        if (player != null) {
            lA.remove(player.getStoredId());
            lz.remove(player.getStoredId());
            player.setTeam(TeamType.NONE);
        }
    }

    private static List<Player> i(List<Long> list) {
        ArrayList<Player> arrayList = new ArrayList<Player>(list.size());
        for (Long l : list) {
            Player player = GameObjectsStorage.getAsPlayer(l);
            if (player == null) continue;
            arrayList.add(player);
        }
        return arrayList;
    }

    static {
        lz = new CopyOnWriteArrayList<Long>();
        lA = new CopyOnWriteArrayList<Long>();
        ht = false;
        _status = 0;
        js = 0;
        _zone = ReflectionUtils.getZone("[colosseum_battle]");
        lB = new ZoneListener();
        gB = new Location(149505, 46719, -3417);
        _active = false;
    }

    private static class TeleportTask
    extends RunnableImpl {
        Location loc;
        Creature target;

        public TeleportTask(Creature creature, Location location) {
            this.target = creature;
            this.loc = location;
            creature.block();
        }

        @Override
        public void runImpl() throws Exception {
            this.target.unblock();
            this.target.teleToLocation(this.loc);
        }
    }

    private static class ZoneListener
    implements OnZoneEnterLeaveListener {
        private ZoneListener() {
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            if (creature == null) {
                return;
            }
            Player player = creature.getPlayer();
            if (_status > 0 && player != null && !lA.contains(player.getStoredId())) {
                ThreadPoolManager.getInstance().schedule(new TeleportTask(creature, new Location(147451, 46728, -3410)), 3000L);
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
            if (creature == null) {
                return;
            }
            Player player = creature.getPlayer();
            if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && lA.contains(player.getStoredId())) {
                double d = PositionUtils.convertHeadingToDegree(creature.getHeading());
                double d2 = Math.toRadians(d - 90.0);
                int n = (int)((double)creature.getX() + 50.0 * Math.sin(d2));
                int n2 = (int)((double)creature.getY() - 50.0 * Math.cos(d2));
                int n3 = creature.getZ();
                ThreadPoolManager.getInstance().schedule(new TeleportTask(creature, new Location(n, n2, n3)), 3000L);
            }
        }
    }

    public class StartTask
    extends RunnableImpl {
        @Override
        public void runImpl() {
            if (!_active) {
                return;
            }
            if (Functions.isPvPEventStarted()) {
                _log.info("Last Hero not started: another event is already running");
                return;
            }
            if (!Rnd.chance(Config.EVENT_LastHeroChanceToStart)) {
                _log.debug("LastHero not started: chance");
                return;
            }
            for (Residence residence : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
                if (residence.getSiegeEvent() == null || !((SiegeEvent)residence.getSiegeEvent()).isInProgress()) continue;
                _log.debug("LastHero not started: CastleSiege in progress");
                return;
            }
            LastHero.this.start(new String[]{"1", "1"});
        }
    }
}

