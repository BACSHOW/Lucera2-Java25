/*
 * Decompiled with CFR 0.152.
 */
package events.TvT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import l2.commons.geometry.Polygon;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Territory;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
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

public class TvT
extends Functions
implements OnDeathListener,
OnPlayerExitListener,
OnTeleportListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(TvT.class);
    private static final String EVENT_NAME = "TvT";
    private static ScheduledFuture<?> jl;
    private static List<Long> jm;
    private static List<Long> jn;
    private static List<Long> jo;
    private static List<Long> jp;
    private static boolean ht;
    private static int _status;
    private static int jq;
    private static int jr;
    private static int p;
    private static int s;
    private static int js;
    private static ScheduledFuture<?> jt;
    private static Zone _zone;
    private static ZoneListener ju;
    private static Territory jv;
    private static Territory jw;
    private static boolean _active;

    @Override
    public void onLoad() {
        if (TvT.isActive()) {
            _active = true;
            CharListenerList.addGlobal(this);
            _zone.addListener(ju);
            jl = ThreadPoolManager.getInstance().scheduleAtFixedRate(new StartTask(), 3600000L, 3600000L);
            _active = ServerVariables.getString(EVENT_NAME, "off").equalsIgnoreCase("on");
            _log.info("Loaded Event: TvT [state: active]");
        } else {
            _active = false;
            _log.debug("Loaded Event: TvT [state: deactivated]");
        }
    }

    @Override
    public void onReload() {
        _zone.removeListener(ju);
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
        return TvT.IsActive(EVENT_NAME);
    }

    public void activateEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (!TvT.isActive()) {
            if (jl == null) {
                jl = ThreadPoolManager.getInstance().scheduleAtFixedRate(new StartTask(), 3600000L, 3600000L);
            }
            ServerVariables.set(EVENT_NAME, "on");
            _log.info("Event 'TvT' activated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TvT.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'TvT' already active.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void deactivateEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (TvT.isActive()) {
            if (jl != null) {
                jl.cancel(false);
                jl = null;
            }
            ServerVariables.unset(EVENT_NAME);
            _log.info("Event 'TvT' deactivated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TvT.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'TvT' not active.");
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
            TvT.show(new CustomMessage("common.Error", player, new Object[0]), player);
            return;
        }
        try {
            n2 = Integer.valueOf(stringArray[0]);
            n = Integer.valueOf(stringArray[1]);
        }
        catch (Exception exception) {
            TvT.show(new CustomMessage("common.Error", player, new Object[0]), player);
            return;
        }
        jr = n2;
        js = n;
        if (jr == -1) {
            p = 1;
            s = 85;
        } else {
            p = TvT.getMinLevelForCategory(jr);
            s = TvT.getMaxLevelForCategory(jr);
        }
        if (jt != null) {
            TvT.show(new CustomMessage("common.TryLater", player, new Object[0]), player);
            return;
        }
        _status = 0;
        ht = true;
        jq = Config.EVENT_TvTTime;
        jm = new CopyOnWriteArrayList<Long>();
        jn = new CopyOnWriteArrayList<Long>();
        jo = new CopyOnWriteArrayList<Long>();
        jp = new CopyOnWriteArrayList<Long>();
        String[] stringArray2 = new String[]{String.valueOf(jq), String.valueOf(p), String.valueOf(s)};
        TvT.sayToAll("scripts.events.TvT.AnnouncePreStart", stringArray2);
        TvT.executeTask("events.TvT.TvT", "question", new Object[0], 10000L);
        TvT.executeTask("events.TvT.TvT", "announce", new Object[0], 60000L);
    }

    public static void sayToAll(String string, String[] stringArray) {
        Announcements.getInstance().announceByCustomMessage(string, stringArray, ChatType.CRITICAL_ANNOUNCE);
    }

    public static void question() {
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (player == null || player.isDead() || player.getLevel() < p || player.getLevel() > s || !player.getReflection().isDefault() || player.isOlyParticipant() || player.isInObserverMode()) continue;
            player.scriptRequest(new CustomMessage("scripts.events.TvT.AskPlayer", player, new Object[0]).toString(), "events.TvT.TvT:addPlayer", new Object[0]);
        }
    }

    public static void announce() {
        if (jm.isEmpty() || jn.isEmpty()) {
            TvT.sayToAll("scripts.events.TvT.AnnounceEventCancelled", null);
            ht = false;
            _status = 0;
            TvT.executeTask("events.TvT.TvT", "autoContinue", new Object[0], 10000L);
            return;
        }
        if (jq > 1) {
            String[] stringArray = new String[]{String.valueOf(--jq), String.valueOf(p), String.valueOf(s)};
            TvT.sayToAll("scripts.events.TvT.AnnouncePreStart", stringArray);
            TvT.executeTask("events.TvT.TvT", "announce", new Object[0], 60000L);
        } else {
            _status = 1;
            ht = false;
            TvT.sayToAll("scripts.events.TvT.AnnounceEventStarting", null);
            TvT.executeTask("events.TvT.TvT", "prepare", new Object[0], 5000L);
        }
    }

    public void addPlayer() {
        int n;
        Player player = this.getSelf();
        if (player == null || !TvT.checkPlayer(player, true)) {
            return;
        }
        int n2 = 0;
        int n3 = jm.size();
        n2 = n3 > (n = jn.size()) ? 2 : (n3 < n ? 1 : Rnd.get(1, 2));
        if (n2 == 1) {
            jm.add(player.getStoredId());
            jo.add(player.getStoredId());
            TvT.show(new CustomMessage("scripts.events.TvT.Registered", player, new Object[0]), player);
        } else if (n2 == 2) {
            jn.add(player.getStoredId());
            jp.add(player.getStoredId());
            TvT.show(new CustomMessage("scripts.events.TvT.Registered", player, new Object[0]), player);
        } else {
            _log.info("WTF??? Command id 0 in TvT...");
        }
    }

    public static boolean checkPlayer(Player player, boolean bl) {
        if (bl && (!ht || player.isDead())) {
            TvT.show(new CustomMessage("scripts.events.Late", player, new Object[0]), player);
            return false;
        }
        if (bl && (jm.contains(player.getStoredId()) || jn.contains(player.getStoredId()))) {
            TvT.show(new CustomMessage("scripts.events.TvT.Cancelled", player, new Object[0]), player);
            return false;
        }
        if (player.getLevel() < p || player.getLevel() > s) {
            TvT.show(new CustomMessage("scripts.events.TvT.CancelledLevel", player, new Object[0]), player);
            return false;
        }
        if (player.isMounted()) {
            TvT.show(new CustomMessage("scripts.events.TvT.Cancelled", player, new Object[0]), player);
            return false;
        }
        if (player.isInDuel()) {
            TvT.show(new CustomMessage("scripts.events.TvT.CancelledDuel", player, new Object[0]), player);
            return false;
        }
        if (player.getTeam() != TeamType.NONE) {
            TvT.show(new CustomMessage("scripts.events.TvT.CancelledOtherEvent", player, new Object[0]), player);
            return false;
        }
        if (player != null && !player.isDead() && player.getLevel() >= p && player.getLevel() <= s && player.getReflection().isDefault() && !player.isOlyParticipant() && !player.isInObserverMode()) {
            TvT.show(new CustomMessage("scripts.events.TvT.CancelledOlympiad", player, new Object[0]), player);
            return false;
        }
        if (player.isInParty() && player.getParty().isInDimensionalRift()) {
            TvT.show(new CustomMessage("scripts.events.TvT.CancelledOtherEvent", player, new Object[0]), player);
            return false;
        }
        if (player.isTeleporting()) {
            TvT.show(new CustomMessage("scripts.events.TvT.CancelledTeleport", player, new Object[0]), player);
            return false;
        }
        return true;
    }

    public static void prepare() {
        ReflectionUtils.getDoor(24190002).closeMe();
        ReflectionUtils.getDoor(24190003).closeMe();
        TvT.cleanPlayers();
        TvT.clearArena();
        TvT.executeTask("events.TvT.TvT", "ressurectPlayers", new Object[0], 1000L);
        TvT.executeTask("events.TvT.TvT", "healPlayers", new Object[0], 2000L);
        TvT.executeTask("events.TvT.TvT", "paralyzePlayers", new Object[0], 4000L);
        TvT.executeTask("events.TvT.TvT", "teleportPlayersToColiseum", new Object[0], 5000L);
        TvT.executeTask("events.TvT.TvT", "go", new Object[0], 60000L);
        TvT.sayToAll("scripts.events.TvT.AnnounceFinalCountdown", null);
    }

    public static void go() {
        _status = 2;
        TvT.upParalyzePlayers();
        TvT.checkLive();
        TvT.clearArena();
        TvT.sayToAll("scripts.events.TvT.AnnounceFight", null);
        jt = TvT.executeTask("events.TvT.TvT", "endBattle", new Object[0], 300000L);
    }

    public static void endBattle() {
        ReflectionUtils.getDoor(24190002).openMe();
        ReflectionUtils.getDoor(24190003).openMe();
        _status = 0;
        TvT.removeAura();
        if (jo.isEmpty()) {
            TvT.sayToAll("scripts.events.TvT.AnnounceFinishedBlueWins", null);
            TvT.giveItemsToWinner(false, true, 1.0);
        } else if (jp.isEmpty()) {
            TvT.sayToAll("scripts.events.TvT.AnnounceFinishedRedWins", null);
            TvT.giveItemsToWinner(true, false, 1.0);
        } else if (jo.size() < jp.size()) {
            TvT.sayToAll("scripts.events.TvT.AnnounceFinishedBlueWins", null);
            TvT.giveItemsToWinner(false, true, 1.0);
        } else if (jo.size() > jp.size()) {
            TvT.sayToAll("scripts.events.TvT.AnnounceFinishedRedWins", null);
            TvT.giveItemsToWinner(true, false, 1.0);
        } else if (jo.size() == jp.size()) {
            TvT.sayToAll("scripts.events.TvT.AnnounceFinishedDraw", null);
            TvT.giveItemsToWinner(true, true, 0.5);
        }
        TvT.sayToAll("scripts.events.TvT.AnnounceEnd", null);
        TvT.executeTask("events.TvT.TvT", "end", new Object[0], 30000L);
        ht = false;
        if (jt != null) {
            jt.cancel(false);
            jt = null;
        }
    }

    public static void end() {
        TvT.executeTask("events.TvT.TvT", "ressurectPlayers", new Object[0], 1000L);
        TvT.executeTask("events.TvT.TvT", "healPlayers", new Object[0], 2000L);
        TvT.executeTask("events.TvT.TvT", "teleportPlayers", new Object[0], 3000L);
        TvT.executeTask("events.TvT.TvT", "autoContinue", new Object[0], 10000L);
    }

    public void autoContinue() {
        jo.clear();
        jp.clear();
        jm.clear();
        jn.clear();
        if (js > 0) {
            if (js >= 6) {
                js = 0;
                return;
            }
            this.start(new String[]{"" + (js + 1), "" + (js + 1)});
        }
    }

    public static void giveItemsToWinner(boolean bl, boolean bl2, double d) {
        if (bl) {
            for (Player player : TvT.i(jm)) {
                TvT.addItem(player, Config.EVENT_TvTItemID, Math.round((double)(Config.EVENT_TvT_rate ? player.getLevel() : 1) * Config.EVENT_TvTItemCOUNT * d));
            }
        }
        if (bl2) {
            for (Player player : TvT.i(jn)) {
                TvT.addItem(player, Config.EVENT_TvTItemID, Math.round((double)(Config.EVENT_TvT_rate ? player.getLevel() : 1) * Config.EVENT_TvTItemCOUNT * d));
            }
        }
    }

    public static void teleportPlayersToColiseum() {
        for (Player player : TvT.i(jm)) {
            TvT.unRide(player);
            TvT.unSummonPet(player, true);
            player.teleToLocation(Territory.getRandomLoc(jv));
        }
        for (Player player : TvT.i(jn)) {
            TvT.unRide(player);
            TvT.unSummonPet(player, true);
            player.teleToLocation(Territory.getRandomLoc(jw));
        }
    }

    public static void teleportPlayers() {
        for (Player player : TvT.i(jm)) {
            player.teleToLocation(151480, 46712, -3400);
        }
        for (Player player : TvT.i(jn)) {
            player.teleToLocation(147640, 46712, -3400);
        }
    }

    public static void paralyzePlayers() {
        Skill skill = SkillTable.getInstance().getInfo(4515, 1);
        for (Player player : TvT.i(jm)) {
            player.getEffectList().stopEffect(1411);
            skill.getEffects(player, player, false, false);
            if (player.getPet() == null) continue;
            skill.getEffects(player, player.getPet(), false, false);
        }
        for (Player player : TvT.i(jn)) {
            player.getEffectList().stopEffect(1411);
            skill.getEffects(player, player, false, false);
            if (player.getPet() == null) continue;
            skill.getEffects(player, player.getPet(), false, false);
        }
    }

    public static void upParalyzePlayers() {
        for (Player player : TvT.i(jm)) {
            player.getEffectList().stopEffect(4515);
            if (player.getPet() != null) {
                player.getPet().getEffectList().stopEffect(4515);
            }
            player.leaveParty();
        }
        for (Player player : TvT.i(jn)) {
            player.getEffectList().stopEffect(4515);
            if (player.getPet() != null) {
                player.getPet().getEffectList().stopEffect(4515);
            }
            player.leaveParty();
        }
    }

    public static void ressurectPlayers() {
        for (Player player : TvT.i(jm)) {
            if (!player.isDead()) continue;
            player.restoreExp();
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp(), true);
            player.setCurrentMp(player.getMaxMp());
            player.broadcastPacket(new Revive(player));
        }
        for (Player player : TvT.i(jn)) {
            if (!player.isDead()) continue;
            player.restoreExp();
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp(), true);
            player.setCurrentMp(player.getMaxMp());
            player.broadcastPacket(new Revive(player));
        }
    }

    public static void healPlayers() {
        for (Player player : TvT.i(jm)) {
            player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
        for (Player player : TvT.i(jn)) {
            player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
    }

    public static void cleanPlayers() {
        for (Player player : TvT.i(jm)) {
            if (TvT.checkPlayer(player, false)) continue;
            TvT.removePlayer(player);
        }
        for (Player player : TvT.i(jn)) {
            if (TvT.checkPlayer(player, false)) continue;
            TvT.removePlayer(player);
        }
    }

    public static void checkLive() {
        Player player;
        CopyOnWriteArrayList<Long> copyOnWriteArrayList = new CopyOnWriteArrayList<Long>();
        CopyOnWriteArrayList<Long> copyOnWriteArrayList2 = new CopyOnWriteArrayList<Long>();
        for (Long serializable : jo) {
            player = GameObjectsStorage.getAsPlayer(serializable);
            if (player == null) continue;
            copyOnWriteArrayList.add(serializable);
        }
        for (Long l : jp) {
            player = GameObjectsStorage.getAsPlayer(l);
            if (player == null) continue;
            copyOnWriteArrayList2.add(l);
        }
        jo = copyOnWriteArrayList;
        jp = copyOnWriteArrayList2;
        for (Player player2 : TvT.i(jo)) {
            if (player2.isInZone(_zone) && !player2.isDead() && !player2.isLogoutStarted()) {
                player2.setTeam(TeamType.RED);
                continue;
            }
            TvT.b(player2);
        }
        for (Player player3 : TvT.i(jp)) {
            if (player3.isInZone(_zone) && !player3.isDead() && !player3.isLogoutStarted()) {
                player3.setTeam(TeamType.BLUE);
                continue;
            }
            TvT.b(player3);
        }
        if (jo.size() < 1 || jp.size() < 1) {
            TvT.endBattle();
        }
    }

    public static void removeAura() {
        for (Player player : TvT.i(jo)) {
            player.setTeam(TeamType.NONE);
        }
        for (Player player : TvT.i(jp)) {
            player.setTeam(TeamType.NONE);
        }
    }

    public static void clearArena() {
        for (Creature creature : _zone.getObjects()) {
            Player player;
            if (creature == null || (player = creature.getPlayer()) == null || jo.contains(player.getStoredId()) || jp.contains(player.getStoredId())) continue;
            player.teleToLocation(147451, 46728, -3410);
        }
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (_status > 1 && creature.isPlayer() && creature.getTeam() != TeamType.NONE && (jo.contains(creature.getStoredId()) || jp.contains(creature.getStoredId()))) {
            TvT.b((Player)creature);
            TvT.checkLive();
        }
    }

    @Override
    public void onTeleport(Player player, int n, int n2, int n3, Reflection reflection) {
        if (_zone.checkIfInZone(n, n2, n3, reflection)) {
            return;
        }
        if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && (jo.contains(player.getStoredId()) || jp.contains(player.getStoredId()))) {
            TvT.removePlayer(player);
            TvT.checkLive();
        }
    }

    @Override
    public void onPlayerExit(Player player) {
        if (player.getTeam() == TeamType.NONE) {
            return;
        }
        if (_status == 0 && ht && player.getTeam() != TeamType.NONE && (jo.contains(player.getStoredId()) || jp.contains(player.getStoredId()))) {
            TvT.removePlayer(player);
            return;
        }
        if (_status == 1 && (jo.contains(player.getStoredId()) || jp.contains(player.getStoredId()))) {
            TvT.removePlayer(player);
            try {
                String string = player.getVar("TvT_backCoords");
                if (string == null || string.equals("")) {
                    return;
                }
                String[] stringArray = string.split(" ");
                if (stringArray.length != 4) {
                    return;
                }
                player.teleToLocation(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[2]), Integer.parseInt(stringArray[3]));
                player.unsetVar("TvT_backCoords");
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            return;
        }
        if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && (jo.contains(player.getStoredId()) || jp.contains(player.getStoredId()))) {
            TvT.removePlayer(player);
            TvT.checkLive();
        }
    }

    private static void b(Player player) {
        if (player != null) {
            jo.remove(player.getStoredId());
            jp.remove(player.getStoredId());
            player.setTeam(TeamType.NONE);
            TvT.show(new CustomMessage("scripts.events.TvT.YouLose", player, new Object[0]), player);
        }
    }

    private static void removePlayer(Player player) {
        if (player != null) {
            jo.remove(player.getStoredId());
            jp.remove(player.getStoredId());
            jm.remove(player.getStoredId());
            jn.remove(player.getStoredId());
            player.setTeam(TeamType.NONE);
        }
    }

    private static List<Player> i(List<Long> list) {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        for (Long l : list) {
            Player player = GameObjectsStorage.getAsPlayer(l);
            if (player == null) continue;
            arrayList.add(player);
        }
        return arrayList;
    }

    static {
        jm = new CopyOnWriteArrayList<Long>();
        jn = new CopyOnWriteArrayList<Long>();
        jo = new CopyOnWriteArrayList<Long>();
        jp = new CopyOnWriteArrayList<Long>();
        ht = false;
        _status = 0;
        js = 0;
        _zone = ReflectionUtils.getZone("[colosseum_battle]");
        ju = new ZoneListener();
        jv = new Territory().add(new Polygon().add(149878, 47505).add(150262, 47513).add(150502, 47233).add(150507, 46300).add(150256, 46002).add(149903, 46005).setZmin(-3408).setZmax(-3308));
        jw = new Territory().add(new Polygon().add(149027, 46005).add(148686, 46003).add(148448, 46302).add(148449, 47231).add(148712, 47516).add(149014, 47527).setZmin(-3408).setZmax(-3308));
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
            if (_status > 0 && player != null && !jo.contains(player.getStoredId()) && !jp.contains(player.getStoredId())) {
                ThreadPoolManager.getInstance().schedule(new TeleportTask(creature, new Location(147451, 46728, -3410)), 3000L);
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
            if (creature == null) {
                return;
            }
            Player player = creature.getPlayer();
            if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && (jo.contains(player.getStoredId()) || jp.contains(player.getStoredId()))) {
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
                _log.info("TvT not started: another event is already running");
                return;
            }
            if (!Rnd.chance(Config.EVENT_TvTChanceToStart)) {
                _log.debug("TvT not started: chance");
                return;
            }
            for (Residence residence : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
                if (residence.getSiegeEvent() == null || !((SiegeEvent)residence.getSiegeEvent()).isInProgress()) continue;
                _log.debug("TvT not started: CastleSiege in progress");
                return;
            }
            TvT.this.start(new String[]{"1", "1"});
        }
    }
}

