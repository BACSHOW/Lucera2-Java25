/*
 * Decompiled with CFR 0.152.
 */
package events.TvT2;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.lang.reference.HardReference;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExEventMatchMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NickNameChanged;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.TimeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.GlobalServices;

public class PvPEvent
extends Functions
implements ScriptFile {
    private static PvPEvent jx = null;
    private static final Logger _log = LoggerFactory.getLogger(PvPEvent.class);
    public static final String EVENT_NAME = "PvP";
    private static final Skill jy = SkillTable.getInstance().getInfo(1323, 1);
    private DieListner jz;
    private ZoneEnterLeaveListner jA;
    private ExitListner jB;
    private static final long jC = 20000L;
    public static final String VAR_EVENT_ACTIVE = "PvP_is_active";
    public static final String VAR_START_TIME = "PvP_start_time";
    public static final String VAR_ANNOUNCE_TIME = "PvP_announce_time";
    public static final String VAR_ANNOUNCE_REDUCT = "PvP_announce_reduct";
    public static final String VAR_INSTANCES_IDS = "PvP_instances_ids";
    public static final String VAR_EVENT_COUNTDOWN = "PvP_event_countdown";
    private boolean jD;
    private boolean jE;
    private String jF;
    private int jG;
    private int jH;
    private int[] jI;
    private PvPEventState jJ;
    private PvPEventRule jK = null;
    private Pair<String, int[]> jL = null;
    private ScheduledFuture<?> jM;
    private ScheduledFuture<?> jN;
    private Collection<Integer> jO;
    private RegisrationState jP;
    private Collection<Integer> jQ;

    public static final PvPEvent getInstance() {
        return jx;
    }

    public void LoadVars() {
        int n;
        this.jD = ServerVariables.getBool(VAR_EVENT_ACTIVE, false);
        this.jE = ServerVariables.getBool(VAR_EVENT_COUNTDOWN, true);
        this.jF = ServerVariables.getString(VAR_START_TIME, "");
        this.jG = ServerVariables.getInt(VAR_ANNOUNCE_TIME, 5);
        this.jH = ServerVariables.getInt(VAR_ANNOUNCE_REDUCT, 1);
        String[] stringArray = ServerVariables.getString(VAR_INSTANCES_IDS, "").split("\\s*;\\s*");
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        for (n = 0; n < stringArray.length; ++n) {
            String string = stringArray[n].trim();
            if (string.isEmpty()) continue;
            linkedList.add(Integer.parseInt(string));
        }
        this.jI = new int[linkedList.size()];
        for (n = 0; n < linkedList.size(); ++n) {
            this.jI[n] = (Integer)linkedList.get(n);
        }
    }

    private PvPEventState Z() {
        return this.jJ;
    }

    private synchronized void a(PvPEventState pvPEventState) {
        _log.info("PvPEventState changet to " + pvPEventState.name());
        this.jJ = pvPEventState;
    }

    public PvPEventRule getRule() {
        return this.jK;
    }

    public void setRule(PvPEventRule pvPEventRule) {
        this.jK = pvPEventRule;
    }

    public PvPEventRule getNextRule(PvPEventRule pvPEventRule) {
        if (pvPEventRule == null) {
            if (this.a(PvPEventRule.TVT)) {
                return PvPEventRule.TVT;
            }
            if (this.a(PvPEventRule.CTF)) {
                return PvPEventRule.CTF;
            }
            if (this.a(PvPEventRule.DM)) {
                return PvPEventRule.DM;
            }
            return null;
        }
        switch (pvPEventRule) {
            case TVT: {
                if (this.a(PvPEventRule.CTF)) {
                    return PvPEventRule.CTF;
                }
                if (this.a(PvPEventRule.DM)) {
                    return PvPEventRule.DM;
                }
                if (!this.a(PvPEventRule.TVT)) break;
                return PvPEventRule.TVT;
            }
            case CTF: {
                if (this.a(PvPEventRule.DM)) {
                    return PvPEventRule.DM;
                }
                if (this.a(PvPEventRule.TVT)) {
                    return PvPEventRule.TVT;
                }
                if (!this.a(PvPEventRule.CTF)) break;
                return PvPEventRule.CTF;
            }
            case DM: {
                if (this.a(PvPEventRule.TVT)) {
                    return PvPEventRule.TVT;
                }
                if (this.a(PvPEventRule.CTF)) {
                    return PvPEventRule.CTF;
                }
                if (!this.a(PvPEventRule.DM)) break;
                return PvPEventRule.DM;
            }
        }
        return null;
    }

    private boolean aa() {
        return ServerVariables.getBool("PvP_" + this.getRule().name() + "_use_capcha", true);
    }

    private boolean ab() {
        return ServerVariables.getBool("PvP_" + this.getRule().name() + "_use_hwid_restrict", false);
    }

    private boolean ac() {
        return ServerVariables.getBool("PvP_" + this.getRule().name() + "_use_ip_restrict", false);
    }

    private boolean ad() {
        return ServerVariables.getBool("PvP_" + this.getRule().name() + "_hide_identiti", false);
    }

    private int ae() {
        return ServerVariables.getInt("PvP_" + this.getRule().name() + "_max_parts", 100);
    }

    private int af() {
        return ServerVariables.getInt("PvP_" + this.getRule().name() + "_item_per_kill", 0);
    }

    private int ag() {
        return ServerVariables.getInt("PvP_" + this.getRule().name() + "_revive_delay", 1);
    }

    private boolean a(PvPEventRule pvPEventRule) {
        return ServerVariables.getBool("PvP_" + pvPEventRule.name() + "_enabled", false);
    }

    private boolean ah() {
        return ServerVariables.getBool("PvP_" + this.getRule().name() + "_buff_protection", false);
    }

    private int ai() {
        return ServerVariables.getInt("PvP_" + this.getRule().name() + "_req_parts", 50);
    }

    private int aj() {
        return ServerVariables.getInt("PvP_" + this.getRule().name() + "_min_lvl", 1);
    }

    private int ak() {
        return ServerVariables.getInt("PvP_" + this.getRule().name() + "_max_lvl", 86);
    }

    private int al() {
        return ServerVariables.getInt("PvP_" + this.getRule().name() + "_herorevhours", 0);
    }

    private int[] am() {
        String string = ServerVariables.getString("PvP_" + this.getRule().name() + "_prohibited_class_ids", "");
        if (string.isEmpty()) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        String string2 = this.getRule().name() + string;
        if (this.jL != null && string2.equals(this.jL.getLeft())) {
            return this.jL.getRight();
        }
        string = string.trim();
        StringBuilder stringBuilder = new StringBuilder();
        int[] nArray = ArrayUtils.EMPTY_INT_ARRAY;
        block4: for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': 
                case '9': {
                    stringBuilder.append(c);
                    continue block4;
                }
                case ',': 
                case ';': {
                    nArray = ArrayUtils.add(nArray, Integer.parseInt(stringBuilder.toString()));
                    stringBuilder.setLength(0);
                    continue block4;
                }
                default: {
                    _log.error("Can't parse prohibited class ids \"" + string + "\"");
                    return nArray;
                }
            }
        }
        if (stringBuilder.length() > 0) {
            nArray = ArrayUtils.add(nArray, Integer.parseInt(stringBuilder.toString()));
        }
        Arrays.sort(nArray);
        this.jL = Pair.of(string2, nArray);
        return nArray;
    }

    private List<Pair<ItemTemplate, Long>> an() {
        String string = ServerVariables.getString("PvP_" + this.getRule().name() + "_rev_team", "");
        return Functions.parseItemIdAmountList(string);
    }

    private List<Pair<ItemTemplate, Long>> ao() {
        String string = ServerVariables.getString("PvP_" + this.getRule().name() + "_rev_top", "");
        return Functions.parseItemIdAmountList(string);
    }

    private boolean ap() {
        return ServerVariables.getBool("PvP_" + this.getRule().name() + "_dispell", true);
    }

    private boolean aq() {
        return ServerVariables.getBool("PvP_" + this.getRule().name() + "_dispell_after", true);
    }

    private int ar() {
        switch (this.getRule()) {
            case DM: {
                return ServerVariables.getInt("PvP_" + this.getRule().name() + "_time", 5);
            }
            case TVT: 
            case CTF: {
                return ServerVariables.getInt("PvP_" + this.getRule().name() + "_time", 10);
            }
        }
        return 0;
    }

    private int as() {
        return ServerVariables.getInt("PvP_" + this.getRule().name() + "_min_kill_reward", 0);
    }

    private int at() {
        return this.jI[Rnd.get(this.jI.length)];
    }

    private synchronized void a(PvPEventState pvPEventState, long l) {
        this.jM = ThreadPoolManager.getInstance().schedule(new PvPStateTask(pvPEventState), l);
    }

    private synchronized void au() {
        if (this.jM != null) {
            this.jM.cancel(false);
            this.jM = null;
        }
    }

    private void av() {
        this.a(PvPEventState.STANDBY);
        long l = this.c(this.jF);
        if (l > 0L) {
            PvPEventRule pvPEventRule = this.getNextRule(this.getRule());
            if (pvPEventRule != null) {
                this.setRule(pvPEventRule);
                this.a(PvPEventState.REGISTRATION, l);
                _log.info("PvPEvent: Next scheduled at " + TimeUtils.toSimpleFormat(System.currentTimeMillis() + l));
            } else {
                _log.info("PvPEvent: No active next event");
            }
        } else {
            _log.warn("PvPEvent: Wrong event time: " + this.jF);
        }
    }

    public void goRegistration() {
        this.a(PvPEventState.REGISTRATION);
        PvPEvent.getInstance().a(new RegisrationTask(RegisrationState.ANNOUNCE, this.jG), 1000L);
    }

    private void aw() {
        this.a(PvPEventState.PREPARE_TO);
        this.getRule().getParticipantController().initReflection();
        this.getRule().getParticipantController().prepareParticipantsTo();
        this.a(PvPEventState.PORTING_TO, 2000L);
    }

    private void ax() {
        this.a(PvPEventState.PORTING_TO);
        this.getRule().getParticipantController().portParticipantsTo();
        PvPEvent.getInstance().a(new CompetitionRunTask(30), 1000L);
    }

    private void ay() {
        this.a(PvPEventState.COMPETITION);
        this.getRule().getParticipantController().initParticipant();
        this.a(PvPEventState.WINNER, (long)(this.ar() * 60 * 1000));
    }

    private void az() {
        this.a(PvPEventState.WINNER);
        this.getRule().getParticipantController().MakeWinner();
        this.a(PvPEventState.PREPARE_FROM, 1000L);
    }

    private void aA() {
        this.a(PvPEventState.PREPARE_FROM);
        this.getRule().getParticipantController().prepareParticipantsFrom();
        this.a(PvPEventState.PORTING_FROM, 10000L);
    }

    private void aB() {
        this.a(PvPEventState.PORTING_FROM);
        this.getRule().getParticipantController().portParticipantsBack();
        this.getRule().getParticipantController().doneParicipant();
        this.getRule().getParticipantController().doneReflection();
        this.jO.clear();
        this.jO = null;
        this.a(PvPEventState.STANDBY, 5000L);
    }

    private synchronized void a(Runnable runnable, long l) {
        this.jN = ThreadPoolManager.getInstance().schedule(runnable, l);
    }

    private synchronized void aC() {
        if (this.jN != null) {
            this.jN.cancel(false);
            this.jN = null;
        }
    }

    private Collection<Player> getPlayers() {
        ArrayList<Player> arrayList = new ArrayList<Player>(this.jO.size());
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (player == null) continue;
            for (Integer n : this.jO) {
                if (n.intValue() != player.getObjectId()) continue;
                arrayList.add(player);
            }
        }
        return arrayList;
    }

    private void aD() {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        for (Player player : GameObjectsStorage.getAllPlayers()) {
            if (!PvPEvent.c(player)) continue;
            arrayList.add(player);
        }
        for (Player player : arrayList) {
            player.scriptRequest(new CustomMessage("events.PvPEvent.AskToS1Participation", player, this.getRule().name()).toString(), "events.TvT2.PvPEvent:addDesire", new Object[0]);
        }
    }

    private void aE() {
        if (this.jP != RegisrationState.CAPCHA || this.jQ == null) {
            return;
        }
        ArrayList<Player> arrayList = new ArrayList<Player>();
        for (Integer serializable : this.jQ) {
            Player player = GameObjectsStorage.getPlayer(serializable);
            if (!PvPEvent.c(player)) continue;
            arrayList.add(player);
        }
        this.jQ.clear();
        this.jQ = null;
        PvPEvent.getInstance().jQ = new ConcurrentSkipListSet<Integer>();
        for (Player player : arrayList) {
            Scripts.getInstance().callScripts(player, "Util", "RequestCapcha", new Object[]{"events.TvT2.PvPEvent:addDesire", player.getStoredId(), Integer.valueOf(30)});
        }
    }

    private static boolean c(Player player) {
        if (player == null || player.getNetConnection() == null || !player.isConnected()) {
            return false;
        }
        if (player.isDead() || player.isBlocked() || player.isInZone(Zone.ZoneType.epic) || player.isInZone(Zone.ZoneType.SIEGE) || player.isInZone(Zone.ZoneType.SIEGE) || player.getReflectionId() != 0) {
            return false;
        }
        if (player.isFishing() || player.getTransformation() != 0 || player.isCursedWeaponEquipped()) {
            return false;
        }
        if (player.getLevel() < PvPEvent.getInstance().aj() || player.getLevel() > PvPEvent.getInstance().ak()) {
            return false;
        }
        if (player.isOlyParticipant() || ParticipantPool.getInstance().isRegistred(player)) {
            return false;
        }
        if (PvPEvent.getInstance().ab() && PvPEvent.getInstance().isHWIDRegistred(player.getNetConnection().getHwid(), player)) {
            return false;
        }
        if (PvPEvent.getInstance().ac() && PvPEvent.getInstance().isIPRegistered(player.getNetConnection().getIpAddr(), player)) {
            return false;
        }
        if (ArrayUtils.contains(PvPEvent.getInstance().am(), player.getActiveClassId())) {
            return false;
        }
        return player.getTeam() == TeamType.NONE && !player.isInDuel();
    }

    public boolean isHWIDRegistred(String string, Player player) {
        if (string == null || string.isEmpty() || this.jQ == null) {
            return false;
        }
        for (Integer n : this.jQ) {
            GameClient gameClient;
            Player player2 = GameObjectsStorage.getPlayer(n);
            if (player2 == null || player2 == player || (gameClient = player2.getNetConnection()) == null || !gameClient.isConnected() || gameClient.getHwid() == null || !string.equalsIgnoreCase(gameClient.getHwid())) continue;
            return true;
        }
        return false;
    }

    public boolean isIPRegistered(String string, Player player) {
        if (string == null || string.isEmpty() || this.jQ == null) {
            return false;
        }
        for (Integer n : this.jQ) {
            GameClient gameClient;
            Player player2 = GameObjectsStorage.getPlayer(n);
            if (player2 == null || player2 == player || (gameClient = player2.getNetConnection()) == null || !gameClient.isConnected() || gameClient.getIpAddr() == null || !string.equalsIgnoreCase(gameClient.getIpAddr())) continue;
            return true;
        }
        return false;
    }

    public void addDesire() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (PvPEvent.getInstance().jP != RegisrationState.REQUEST && PvPEvent.getInstance().jP != RegisrationState.CAPCHA) {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireInappropriateState", player, new Object[0]));
            return;
        }
        if (!PvPEvent.c(player) || PvPEvent.getInstance().jQ == null) {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireInsufficientConditions", player, new Object[0]));
            return;
        }
        if (PvPEvent.getInstance().jQ.contains(player.getObjectId())) {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireAlreadyAccepted", player, new Object[0]));
            return;
        }
        player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireAccepted", player, new Object[0]));
        PvPEvent.getInstance().jQ.add(player.getObjectId());
    }

    public void addDesireDuringAnnounce() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (PvPEvent.getInstance().jP != RegisrationState.ANNOUNCE) {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireInappropriateState", player, new Object[0]));
            return;
        }
        if (!PvPEvent.c(player) || PvPEvent.getInstance().jQ == null) {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireInsufficientConditions", player, new Object[0]));
            return;
        }
        if (PvPEvent.getInstance().jQ.contains(player.getObjectId())) {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireAlreadyAccepted", player, new Object[0]));
            return;
        }
        player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireAccepted", player, new Object[0]));
        PvPEvent.getInstance().jQ.add(player.getObjectId());
    }

    private void aF() {
        Object object;
        if (this.jP != RegisrationState.MORPH || this.jQ == null) {
            return;
        }
        LinkedList<Player> linkedList = new LinkedList<Player>();
        for (Integer n : this.jQ) {
            object = GameObjectsStorage.getPlayer(n);
            if (!PvPEvent.c((Player)object)) continue;
            linkedList.add((Player)object);
        }
        this.jQ.clear();
        this.jQ = null;
        ArrayList arrayList = new ArrayList();
        int n = this.ae();
        while (arrayList.size() < n && !linkedList.isEmpty()) {
            arrayList.add(linkedList.remove(Rnd.get(linkedList.size())));
        }
        if (arrayList.size() < this.ai()) {
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.EventS1LackParticipants", new String[]{this.getRule().name()});
            this.av();
            return;
        }
        this.jO = new ConcurrentSkipListSet<Integer>();
        for (Player player : linkedList) {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipantAskLater", player, new Object[0]));
        }
        Iterator<Player> acceptedPlayers = ((List<Player>)(List<?>)arrayList).iterator();
        while (acceptedPlayers.hasNext()) {
            Player player = acceptedPlayers.next();
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipantAccepted", player, new Object[0]));
            this.jO.add(player.getObjectId());
        }
        linkedList.clear();
        this.a(PvPEventState.PREPARE_TO, 10000L);
    }

    public void Activate() {
        PvPEvent.getInstance().a(PvPEventState.STANDBY, 1000L);
        _log.info("PvPEvent: [state: active]");
    }

    public void Deativate() {
        ServerVariables.set(VAR_EVENT_ACTIVE, false);
        PvPEvent.getInstance().LoadVars();
        PvPEvent.getInstance().au();
        _log.debug("PvPEvent: [state: inactive]");
    }

    @Override
    public void onLoad() {
        jx = this;
        this.LoadVars();
        if (this.jD) {
            this.Activate();
        } else {
            _log.debug("PvPEvent: [state: inactive]");
        }
        this.jz = new DieListner();
        this.jA = new ZoneEnterLeaveListner();
        this.jB = new ExitListner();
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private long c(String string) {
        Matcher matcher = Pattern.compile("(\\d{2})\\:(\\d{2});*").matcher(string);
        long l = System.currentTimeMillis();
        long l2 = Long.MAX_VALUE;
        while (matcher.find()) {
            long l3;
            String string2 = matcher.group(1);
            String string3 = matcher.group(2);
            int n = Integer.parseInt(string2);
            int n2 = Integer.parseInt(string3);
            Calendar calendar = Calendar.getInstance();
            calendar.set(11, n);
            calendar.set(12, n2);
            calendar.set(13, 0);
            calendar.set(14, 0);
            if (calendar.getTimeInMillis() < l) {
                calendar.add(5, 1);
            }
            if ((l3 = calendar.getTimeInMillis() - l) <= 0L || l3 >= l2) continue;
            l2 = l3;
        }
        return l2 < Long.MAX_VALUE ? l2 : -1L;
    }

    private void broadcast(L2GameServerPacket ... l2GameServerPacketArray) {
        Collection<Player> collection = this.getPlayers();
        for (Player player : collection) {
            player.sendPacket(l2GameServerPacketArray);
        }
    }

    public boolean isEventPartisipant() {
        Player player = this.getSelf();
        if (PvPEvent.getInstance().jO == null || player == null) {
            return false;
        }
        int n = player.getObjectId();
        for (Integer n2 : PvPEvent.getInstance().jO) {
            if (n2 != n) continue;
            return true;
        }
        return false;
    }

    private class TeleportAndReviveTask
    implements Runnable {
        private final HardReference<Player> e;
        private Location cH;
        private Reflection ky;

        public TeleportAndReviveTask(Player player, Location location, Reflection reflection) {
            this.e = player.getRef();
            this.cH = location;
            this.ky = reflection;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            Player player2 = player;
            synchronized (player2) {
                player.teleToLocation(this.cH, this.ky);
                if (!player.isConnected()) {
                    player.onTeleported();
                }
                if (player.isDead()) {
                    player.setCurrentHp(player.getMaxHp(), true, true);
                    player.setCurrentCp(player.getMaxCp());
                    player.setCurrentMp(player.getMaxMp());
                    player.broadcastPacket(new Revive(player));
                    HashSet<Skill> hashSet = new HashSet<Skill>();
                    for (Effect object : player.getEffectList().getAllEffects()) {
                        if (!object.getSkill().isOffensive()) continue;
                        hashSet.add(object.getSkill());
                    }
                    for (Skill skill : hashSet) {
                        for (Effect effect : player.getEffectList().getEffectsBySkill(skill)) {
                            effect.exit();
                        }
                    }
                    if (PvPEvent.getInstance().ah()) {
                        jy.getEffects(player, player, false, false, false);
                    }
                }
            }
        }
    }

    private class TeleportTask
    implements Runnable {
        private Player _player;
        private Location cH;
        private Reflection ky;

        public TeleportTask(Player player, Location location, Reflection reflection) {
            this._player = player;
            this.cH = location;
            this.ky = reflection;
        }

        @Override
        public void run() {
            this._player.teleToLocation(this.cH, this.ky);
        }
    }

    private class ExitListner
    implements OnPlayerExitListener {
        private ExitListner() {
        }

        @Override
        public void onPlayerExit(Player player) {
            try {
                if (PvPEvent.getInstance().Z() == PvPEventState.STANDBY) {
                    return;
                }
                PvPEvent.getInstance().getRule().getParticipantController().OnExit(player);
                PvPEvent.getInstance().jO.remove(player.getObjectId());
            }
            catch (Exception exception) {
                _log.warn("PVPEvent.onPlayerExit :", exception);
            }
        }
    }

    private class TeleportListner
    implements OnTeleportListener {
        private TeleportListner() {
        }

        @Override
        public void onTeleport(Player player, int n, int n2, int n3, Reflection reflection) {
            try {
                if (PvPEvent.getInstance().Z() != PvPEventState.COMPETITION) {
                    return;
                }
                PvPEvent.getInstance().getRule().getParticipantController().OnTeleport(player, n, n2, n3, reflection);
            }
            catch (Exception exception) {
                _log.warn("PVPEvent.onTeleport :", exception);
            }
        }
    }

    private class ZoneEnterLeaveListner
    implements OnZoneEnterLeaveListener {
        private ZoneEnterLeaveListner() {
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            try {
                if (PvPEvent.getInstance().Z() != PvPEventState.COMPETITION || !creature.isPlayer()) {
                    return;
                }
                PvPEvent.getInstance().getRule().getParticipantController().OnEnter(creature.getPlayer(), zone);
            }
            catch (Exception exception) {
                _log.warn("PVPEvent.onZoneEnter :", exception);
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
            try {
                if (PvPEvent.getInstance().Z() != PvPEventState.COMPETITION || !creature.isPlayer()) {
                    return;
                }
                PvPEvent.getInstance().getRule().getParticipantController().OnLeave(creature.getPlayer(), zone);
            }
            catch (Exception exception) {
                _log.warn("PVPEvent.onZoneLeave :", exception);
            }
        }
    }

    private class DieListner
    implements OnDeathListener {
        private DieListner() {
        }

        @Override
        public void onDeath(Creature creature, Creature creature2) {
            try {
                if (PvPEvent.getInstance().Z() != PvPEventState.COMPETITION) {
                    return;
                }
                Player player = creature.getPlayer();
                Player player2 = creature2.getPlayer();
                if (player != null) {
                    PvPEvent.getInstance().getRule().getParticipantController().OnPlayerDied(player, player2);
                }
            }
            catch (Exception exception) {
                _log.warn("PVPEvent.onDeath :", exception);
            }
        }
    }

    private class CompetitionRunTask
    implements Runnable {
        private int kn;

        public CompetitionRunTask(int n) {
            this.kn = n;
        }

        @Override
        public void run() {
            switch (this.kn) {
                case 30: {
                    PvPEvent.getInstance().a(new CompetitionRunTask(this.kn - 25), 25000L);
                    return;
                }
                case 0: {
                    PvPEvent.getInstance().a(PvPEventState.COMPETITION, 100L);
                    if (PvPEvent.this.jE) {
                        PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{ExEventMatchMessage.START});
                    }
                    return;
                }
                case 5: {
                    if (PvPEvent.this.jE) {
                        PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{ExEventMatchMessage.COUNT5});
                    }
                    for (Player player : PvPEvent.getInstance().getPlayers()) {
                        player.broadcastUserInfo(true);
                    }
                    break;
                }
                case 4: {
                    if (!PvPEvent.this.jE) break;
                    PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{ExEventMatchMessage.COUNT4});
                    break;
                }
                case 3: {
                    if (!PvPEvent.this.jE) break;
                    PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{ExEventMatchMessage.COUNT3});
                    break;
                }
                case 2: {
                    if (!PvPEvent.this.jE) break;
                    PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{ExEventMatchMessage.COUNT2});
                    break;
                }
                case 1: {
                    if (!PvPEvent.this.jE) break;
                    PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{ExEventMatchMessage.COUNT1});
                }
            }
            PvPEvent.getInstance().a(new CompetitionRunTask(this.kn - 1), 1000L);
        }
    }

    private class RegisrationTask
    implements Runnable {
        private final int kn;
        private final RegisrationState kx;

        public RegisrationTask(RegisrationState regisrationState, int n) {
            this.kn = n;
            this.kx = regisrationState;
        }

        @Override
        public void run() {
            if (PvPEvent.getInstance().jP != this.kx && this.kx == RegisrationState.ANNOUNCE) {
                if (PvPEvent.getInstance().jQ != null) {
                    PvPEvent.getInstance().jQ.clear();
                    PvPEvent.getInstance().jQ = null;
                }
                PvPEvent.getInstance().jQ = new ConcurrentSkipListSet();
            }
            PvPEvent.getInstance().jP = this.kx;
            switch (this.kx) {
                case ANNOUNCE: {
                    if (this.kn > 0) {
                        Announcements.getInstance().announceByCustomMessage("events.PvPEvent.EventS1StartAtS2Minutes", new String[]{PvPEvent.this.getRule().name(), String.valueOf(this.kn)});
                        PvPEvent.getInstance().a(new RegisrationTask(RegisrationState.ANNOUNCE, Math.max(0, this.kn - PvPEvent.getInstance().jH)), PvPEvent.getInstance().jH * 60 * 1000);
                        break;
                    }
                    PvPEvent.getInstance().a(new RegisrationTask(RegisrationState.REQUEST, 0), 1000L);
                    break;
                }
                case REQUEST: {
                    PvPEvent.getInstance().aD();
                    PvPEvent.getInstance().a(new RegisrationTask(PvPEvent.getInstance().aa() ? RegisrationState.CAPCHA : RegisrationState.MORPH, 0), 40000L);
                    break;
                }
                case CAPCHA: {
                    PvPEvent.getInstance().aE();
                    PvPEvent.getInstance().a(new RegisrationTask(RegisrationState.MORPH, 0), 40000L);
                    break;
                }
                case MORPH: {
                    PvPEvent.getInstance().aF();
                }
            }
        }
    }

    private static enum RegisrationState {
        ANNOUNCE,
        REQUEST,
        MORPH,
        CAPCHA;

    }

    private class PvPStateTask
    implements Runnable {
        private final PvPEventState kv;

        public PvPStateTask(PvPEventState pvPEventState) {
            this.kv = pvPEventState;
        }

        @Override
        public void run() {
            try {
                switch (this.kv) {
                    case STANDBY: {
                        PvPEvent.getInstance().av();
                        break;
                    }
                    case REGISTRATION: {
                        PvPEvent.getInstance().goRegistration();
                        break;
                    }
                    case PORTING_TO: {
                        PvPEvent.getInstance().ax();
                        break;
                    }
                    case PREPARE_TO: {
                        PvPEvent.getInstance().aw();
                        break;
                    }
                    case COMPETITION: {
                        PvPEvent.getInstance().ay();
                        break;
                    }
                    case WINNER: {
                        PvPEvent.getInstance().az();
                        break;
                    }
                    case PREPARE_FROM: {
                        PvPEvent.getInstance().aA();
                        break;
                    }
                    case PORTING_FROM: {
                        PvPEvent.getInstance().aB();
                    }
                }
            }
            catch (Exception exception) {
                _log.warn("PvPEvent: Exception on changing state to " + (Object)((Object)this.kv) + " state.", exception);
                exception.printStackTrace();
            }
        }
    }

    public static enum PvPEventRule {
        TVT(new TvTParticipantController()),
        CTF(new CTFParticipantController()),
        DM(new DMParticipantController());

        public static PvPEventRule[] VALUES;
        private final IParticipantController ks;

        private PvPEventRule(IParticipantController iParticipantController) {
            this.ks = iParticipantController;
        }

        public IParticipantController getParticipantController() {
            return this.ks;
        }

        static {
            VALUES = PvPEventRule.values();
        }
    }

    private static class DMParticipantController
    implements IParticipantController {
        private Map<Integer, AtomicInteger> ko;
        private static final String jV = "pvp_dm_title";
        private ScheduledFuture<?> kk;
        private Reflection _reflection = null;
        private int kd = 0;
        private String jW = "[pvp_%d_dm_default]";
        private String kp = "[pvp_%d_dm_spawn]";
        private String jZ = "backCoords";
        private Zone ka = null;
        private Zone kq = null;

        private DMParticipantController() {
        }

        @Override
        public void prepareParticipantsTo() {
            this.ko = new ConcurrentHashMap<Integer, AtomicInteger>();
            boolean bl = PvPEvent.getInstance().ap();
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                if (PvPEvent.c(player)) continue;
                PvPEvent.getInstance().jO.remove(player.getObjectId());
                this.OnExit(player);
            }
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                if (player.isAttackingNow()) {
                    player.abortAttack(true, false);
                }
                if (player.isCastingNow()) {
                    player.abortCast(true, false);
                }
                player.sendActionFailed();
                player.stopMove();
                player.sitDown(null);
                player.block();
                if (bl) {
                    player.getEffectList().stopAllEffects();
                    if (player.getPet() != null) {
                        player.getPet().getEffectList().stopAllEffects();
                    }
                }
                if (!Config.PVP_EVENTS_MAGE_BUFF.isEmpty() || !Config.PVP_EVENTS_WARRIOR_BUFF.isEmpty()) {
                    for (Pair<Integer, Integer> pair : player.isMageClass() ? Config.PVP_EVENTS_MAGE_BUFF : Config.PVP_EVENTS_WARRIOR_BUFF) {
                        Skill skill = SkillTable.getInstance().getInfo(pair.getLeft(), pair.getRight());
                        skill.getEffects(player, player, false, false);
                    }
                }
                boolean bl2 = false;
                if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                    player.getClan().disableSkills(player);
                }
                LinkedList<Effect> serializable2 = new LinkedList<Effect>();
                for (int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                    List<Effect> list;
                    int n = Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i];
                    Skill skill = player.getKnownSkill(n);
                    if (skill == null) continue;
                    if (skill.isToggle() && (list = player.getEffectList().getEffectsBySkill(skill)) != null && !list.isEmpty()) {
                        serializable2.addAll(list);
                    }
                    player.addUnActiveSkill(skill);
                    bl2 = true;
                }
                if (bl2) {
                    player.sendPacket((IStaticPacket)new SkillCoolTime(player));
                    player.updateStats();
                    player.updateEffectIcons();
                }
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
                player.setVar(jV, player.getTitle() != null ? player.getTitle() : "", -1L);
                this.ko.put(player.getObjectId(), new AtomicInteger(0));
                this.a(player, 0);
            }
        }

        private void a(Player player, int n) {
            player.setTransformationTitle(String.format("Kills: %d", n));
            player.setTitle(player.getTransformationTitle());
            player.broadcastPacket(new NickNameChanged(player));
        }

        @Override
        public void prepareParticipantsFrom() {
            boolean bl = PvPEvent.getInstance().aq();
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                String string;
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                if (player.isAttackingNow()) {
                    player.abortAttack(true, false);
                }
                if (player.isCastingNow()) {
                    player.abortCast(true, false);
                }
                player.sendActionFailed();
                player.stopMove();
                player.sitDown(null);
                player.block();
                if (bl) {
                    player.getEffectList().stopAllEffects();
                }
                if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                    player.getClan().enableSkills(player);
                }
                for (int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                    Skill skill;
                    int n = Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i];
                    if (!player.isUnActiveSkill(n) || (skill = player.getKnownSkill(n)) == null) continue;
                    player.removeUnActiveSkill(skill);
                }
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp(), true);
                player.setCurrentCp(player.getMaxCp());
                if (PvPEvent.getInstance().ad() && player.getTransformation() == 0) {
                    player.setTransformationName(null);
                    player.setTransformationTitle(null);
                }
                if ((string = player.getVar(jV)) != null) {
                    player.setTitle(string);
                    player.unsetVar(jV);
                }
                player.sendUserInfo(true);
            }
            this.ko.clear();
            this.ko = null;
        }

        @Override
        public void initParticipant() {
            boolean bl = PvPEvent.getInstance().ah();
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.addListener(PvPEvent.getInstance().jz);
                player.addListener(PvPEvent.getInstance().jB);
                player.setResurectProhibited(true);
                player.unblock();
                player.standUp();
                if (!bl) continue;
                jy.getEffects(player, player, false, false, false);
            }
            this.kk = ThreadPoolManager.getInstance().schedule(new RankBroadcastTask(this), 20000L);
        }

        @Override
        public void doneParicipant() {
            if (this.kk != null) {
                this.kk.cancel(true);
                this.kk = null;
            }
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.removeListener(PvPEvent.getInstance().jz);
                player.removeListener(PvPEvent.getInstance().jB);
                player.setResurectProhibited(false);
                player.unblock();
                if (player.isDead()) {
                    player.doRevive(100.0);
                }
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp(), true);
                player.setCurrentCp(player.getMaxCp());
                player.standUp();
            }
        }

        @Override
        public void portParticipantsTo() {
            int n = 0;
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.setVar(this.jZ, player.getLoc().toXYZString(), -1L);
                if (player.getParty() != null) {
                    player.getParty().removePartyMember(player, false);
                }
                if (PvPEvent.getInstance().ad()) {
                    player.setTransformationName(String.format("Player %d", ++n));
                }
                player.teleToLocation(this.aG(), this.getReflection());
            }
        }

        @Override
        public void portParticipantsBack() {
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                String string;
                if (PvPEvent.getInstance().ad() && player.getTransformation() == 0) {
                    player.setTransformationName(null);
                }
                if ((string = player.getVar(this.jZ)) != null) {
                    player.unsetVar(this.jZ);
                    player.teleToLocation(Location.parseLoc(string), ReflectionManager.DEFAULT);
                    continue;
                }
                player.teleToClosestTown();
            }
        }

        @Override
        public void initReflection() {
            this.kd = PvPEvent.getInstance().at();
            InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(this.kd);
            this.jW = String.format("[pvp_%d_dm_default]", this.kd);
            this.kp = String.format("[pvp_%d_dm_spawn]", this.kd);
            this._reflection = new Reflection();
            this._reflection.init(instantZone);
            this.ka = this._reflection.getZone(this.jW);
            this.ka.addListener(PvPEvent.getInstance().jA);
            this.kq = this._reflection.getZone(this.kp);
        }

        private Location aG() {
            return this.kq.getTerritory().getRandomLoc(this._reflection.getGeoIndex());
        }

        @Override
        public void doneReflection() {
            this.ka.removeListener(PvPEvent.getInstance().jA);
            this.ka = null;
            this.kq = null;
            this._reflection.collapse();
            this._reflection = null;
        }

        @Override
        public Reflection getReflection() {
            return this._reflection;
        }

        @Override
        public void OnPlayerDied(Player player, Player player2) {
            if (player != null && player2 != null && this.ko.containsKey(player.getObjectId()) && this.ko.containsKey(player2.getObjectId())) {
                int n = 0;
                AtomicInteger atomicInteger = this.ko.get(player.getObjectId());
                AtomicInteger atomicInteger2 = this.ko.get(player2.getObjectId());
                n = atomicInteger2.addAndGet(atomicInteger.getAndSet(0) + 1);
                this.a(player, 0);
                this.a(player2, n);
            }
            ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
            PvPEvent pvPEvent = PvPEvent.getInstance();
            pvPEvent.getClass();
            threadPoolManager.schedule(pvPEvent.new TeleportAndReviveTask(player, this.aG(), this.getReflection()), PvPEvent.getInstance().ag() * 1000);
        }

        @Override
        public void OnEnter(Player player, Zone zone) {
            if (player != null && !this.ko.containsKey(player.getObjectId())) {
                if (PvPEvent.getInstance().ad() && player.getTransformation() == 0) {
                    player.setTransformationName(null);
                }
                player.teleToClosestTown();
            }
        }

        @Override
        public void OnLeave(Player player, Zone zone) {
            if (player != null && !this.ka.checkIfInZone(player.getX(), player.getY(), player.getZ(), this.getReflection()) && this.ko.containsKey(player.getObjectId())) {
                double d = Math.PI * 2 - PositionUtils.convertHeadingToRadian(player.getHeading());
                double d2 = Math.cos(d);
                double d3 = Math.sin(d);
                Location location = zone.getTerritory().getRandomLoc(player.getGeoIndex());
                for (int i = 32; i < 512; i += 32) {
                    int n = (int)Math.floor((double)player.getX() - (double)i * d2);
                    int n2 = (int)Math.floor((double)player.getY() + (double)i * d3);
                    if (!zone.getTerritory().isInside(n, n2)) continue;
                    location.set(n, n2, player.getZ(), player.getHeading());
                    break;
                }
                ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
                PvPEvent pvPEvent = PvPEvent.getInstance();
                pvPEvent.getClass();
                threadPoolManager.schedule(pvPEvent.new TeleportTask(player, location.correctGeoZ(), this.getReflection()), 3000L);
            }
        }

        @Override
        public void OnExit(Player player) {
            String string = player.getVar(jV);
            if (string != null) {
                player.setTitle(player.getVar(jV));
                player.unsetVar(jV);
            }
            this.ko.remove(player.getObjectId());
        }

        @Override
        public void OnTeleport(Player player, int n, int n2, int n3, Reflection reflection) {
            Location location;
            if (player != null && !this.ka.checkIfInZone(n, n2, n3, this.getReflection()) && (location = this.aG()) != null) {
                ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
                PvPEvent pvPEvent = PvPEvent.getInstance();
                pvPEvent.getClass();
                threadPoolManager.schedule(pvPEvent.new TeleportTask(player, location, this.getReflection()), 3000L);
            }
        }

        private void aH() {
            int n = PvPEvent.getInstance().af();
            if (n <= 0) {
                return;
            }
            for (Map.Entry<Integer, AtomicInteger> entry : this.ko.entrySet()) {
                int n2 = entry.getValue().get();
                int n3 = entry.getKey();
                Player player = GameObjectsStorage.getPlayer(n3);
                if (n2 <= 0 || player == null) continue;
                Functions.addItem(player, n, n2);
            }
        }

        @Override
        public void MakeWinner() {
            int n = 0;
            int n2 = Integer.MIN_VALUE;
            for (Map.Entry<Integer, AtomicInteger> entry : this.ko.entrySet()) {
                int n3 = entry.getValue().get();
                if (n3 <= n2) continue;
                n = (Integer)entry.getKey();
                n2 = n3;
            }
            if (n != 0 && n2 > 0) {
                Player player = GameObjectsStorage.getPlayer(n);
                for (Pair pair : PvPEvent.getInstance().ao()) {
                    Functions.addItem(player, ((ItemTemplate)pair.getLeft()).getItemId(), (Long)pair.getRight());
                }
                if (PvPEvent.getInstance().al() > 0) {
                    GlobalServices.makeCustomHero(player, (long)(PvPEvent.getInstance().al() * 60) * 60L);
                }
                PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{new ExEventMatchMessage("'" + player.getName() + "' winns!"), new SystemMessage(1497).addName(player)});
                Announcements.getInstance().announceByCustomMessage("events.PvPEvent.PlayerS1WonTheDMGame", new String[]{player.getName()});
            } else {
                PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{new ExEventMatchMessage("Tie"), Msg.THE_GAME_ENDED_IN_A_TIE});
                Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TheDMGameEndedInATie", ArrayUtils.EMPTY_STRING_ARRAY);
            }
            this.aH();
        }

        private class RankBroadcastTask
        implements Runnable {
            private DMParticipantController kr;

            public RankBroadcastTask(DMParticipantController dMParticipantController2) {
                this.kr = dMParticipantController2;
            }

            @Override
            public void run() {
                if (PvPEvent.getInstance().Z() != PvPEventState.COMPETITION) {
                    return;
                }
                this.kr.kk = ThreadPoolManager.getInstance().schedule(this, 20000L);
            }
        }
    }

    private static class CTFParticipantController
    implements IParticipantController {
        private Map<Integer, AtomicInteger> jR;
        private Map<Integer, AtomicInteger> jS;
        private AtomicInteger jT;
        private AtomicInteger jU;
        private static final String jV = "pvp_ctf_title";
        private String jW = "[pvp_%d_ctf_default]";
        private String jX = "[pvp_%d_ctf_spawn_blue]";
        private String jY = "[pvp_%d_ctf_spawn_red]";
        private String jZ = "backCoords";
        private Reflection _reflection = null;
        private Zone ka = null;
        private Zone kb = null;
        private Zone kc = null;
        private int kd = 0;
        private static final int ke = 32027;
        private static final int kf = 32027;
        private static final int kg = 6718;
        private static final int kh = 6718;
        private WeakReference<CTFFlagInstance> ki;
        private WeakReference<CTFFlagInstance> kj;
        private ScheduledFuture<?> kk;

        private CTFParticipantController() {
        }

        @Override
        public void prepareParticipantsTo() {
            this.jR = new ConcurrentHashMap<Integer, AtomicInteger>();
            this.jS = new ConcurrentHashMap<Integer, AtomicInteger>();
            this.jT = new AtomicInteger(0);
            this.jU = new AtomicInteger(0);
            TeamType teamType = TeamType.BLUE;
            for (Object object : PvPEvent.getInstance().getPlayers()) {
                if (PvPEvent.c((Player)object)) continue;
                PvPEvent.getInstance().jO.remove(((GameObject)object).getObjectId());
                this.OnExit((Player)object);
            }
            boolean bl = PvPEvent.getInstance().ap();
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                if (player.isAttackingNow()) {
                    player.abortAttack(true, false);
                }
                if (player.isCastingNow()) {
                    player.abortCast(true, false);
                }
                player.sendActionFailed();
                player.stopMove();
                player.sitDown(null);
                player.block();
                if (bl) {
                    player.getEffectList().stopAllEffects();
                    if (player.getPet() != null) {
                        player.getPet().getEffectList().stopAllEffects();
                    }
                }
                if (!Config.PVP_EVENTS_MAGE_BUFF.isEmpty() || !Config.PVP_EVENTS_WARRIOR_BUFF.isEmpty()) {
                    for (Pair<Integer, Integer> pair : player.isMageClass() ? Config.PVP_EVENTS_MAGE_BUFF : Config.PVP_EVENTS_WARRIOR_BUFF) {
                        Skill skill = SkillTable.getInstance().getInfo(pair.getLeft(), pair.getRight());
                        skill.getEffects(player, player, false, false);
                    }
                }
                boolean bl2 = false;
                if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                    player.getClan().disableSkills(player);
                }
                LinkedList<Effect> serializable2 = new LinkedList<Effect>();
                for (int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                    List<Effect> list;
                    int n = Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i];
                    Skill skill = player.getKnownSkill(n);
                    if (skill == null) continue;
                    if (skill.isToggle() && (list = player.getEffectList().getEffectsBySkill(skill)) != null && !list.isEmpty()) {
                        serializable2.addAll(list);
                    }
                    player.addUnActiveSkill(skill);
                    bl2 = true;
                }
                if (bl2) {
                    player.sendPacket((IStaticPacket)new SkillCoolTime(player));
                    player.updateStats();
                    player.updateEffectIcons();
                }
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
                player.sendChanges();
                player.setVar(jV, player.getTitle() != null ? player.getTitle() : "", -1L);
                if (teamType == TeamType.BLUE) {
                    player.setTeam(TeamType.BLUE);
                    this.jS.put(player.getObjectId(), new AtomicInteger(0));
                    teamType = TeamType.RED;
                    continue;
                }
                player.setTeam(TeamType.RED);
                this.jR.put(player.getObjectId(), new AtomicInteger(0));
                teamType = TeamType.BLUE;
            }
        }

        @Override
        public void prepareParticipantsFrom() {
            ((CTFFlagInstance)this.ki.get()).removeFlag(null);
            ((CTFFlagInstance)this.kj.get()).removeFlag(null);
            boolean bl = PvPEvent.getInstance().aq();
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                if (player.isAttackingNow()) {
                    player.abortAttack(true, false);
                }
                if (player.isCastingNow()) {
                    player.abortCast(true, false);
                }
                player.sendActionFailed();
                player.stopMove();
                player.sitDown(null);
                player.block();
                if (bl) {
                    player.getEffectList().stopAllEffects();
                }
                if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                    player.getClan().enableSkills(player);
                }
                for (int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                    Skill skill;
                    int n = Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i];
                    if (!player.isUnActiveSkill(n) || (skill = player.getKnownSkill(n)) == null) continue;
                    player.removeUnActiveSkill(skill);
                }
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
                player.sendChanges();
                if (PvPEvent.getInstance().ad() && player.getTransformation() == 0) {
                    player.setTransformationName(null);
                    player.setTransformationTitle(null);
                }
                player.setTeam(TeamType.NONE);
                String string = player.getVar(jV);
                if (string != null) {
                    player.setTitle(string);
                    player.unsetVar(jV);
                }
                player.sendUserInfo(true);
            }
            this.jR.clear();
            this.jS.clear();
            this.jR = null;
            this.jS = null;
            this.jT = null;
            this.jU = null;
        }

        @Override
        public void initParticipant() {
            boolean bl = PvPEvent.getInstance().ah();
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.addListener(PvPEvent.getInstance().jz);
                player.addListener(PvPEvent.getInstance().jB);
                player.setResurectProhibited(true);
                player.unblock();
                player.standUp();
                if (!bl) continue;
                jy.getEffects(player, player, false, false, false);
            }
        }

        @Override
        public void doneParicipant() {
            if (this.kk != null) {
                this.kk.cancel(true);
                this.kk = null;
            }
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.removeListener(PvPEvent.getInstance().jz);
                player.removeListener(PvPEvent.getInstance().jB);
                player.setResurectProhibited(false);
                player.unblock();
                if (player.isDead()) {
                    player.doRevive(100.0);
                    player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                    player.setCurrentCp(player.getMaxCp());
                }
                player.standUp();
            }
        }

        @Override
        public void portParticipantsTo() {
            int n = 0;
            int n2 = 0;
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                TeamType teamType = player.getTeam();
                if (teamType != TeamType.BLUE && teamType != TeamType.RED) {
                    PvPEvent.getInstance().jO.remove(player.getObjectId());
                    this.OnExit(player);
                    continue;
                }
                player.setVar(this.jZ, player.getLoc().toXYZString(), -1L);
                if (player.getParty() != null) {
                    player.getParty().removePartyMember(player, false);
                }
                if (PvPEvent.getInstance().ad()) {
                    switch (teamType) {
                        case RED: {
                            player.setTransformationName(String.format("Red %d", ++n));
                            break;
                        }
                        case BLUE: {
                            player.setTransformationName(String.format("Blue %d", ++n2));
                        }
                    }
                }
                player.teleToLocation(this.a(teamType), this.getReflection());
            }
        }

        @Override
        public void portParticipantsBack() {
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                String string = player.getVar(this.jZ);
                if (string != null) {
                    player.unsetVar(this.jZ);
                    player.teleToLocation(Location.parseLoc(string), ReflectionManager.DEFAULT);
                    continue;
                }
                player.teleToClosestTown();
            }
        }

        @Override
        public void initReflection() {
            this.kd = PvPEvent.getInstance().at();
            InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(this.kd);
            this.jW = String.format("[pvp_%d_ctf_default]", this.kd);
            this.jX = String.format("[pvp_%d_ctf_spawn_blue]", this.kd);
            this.jY = String.format("[pvp_%d_ctf_spawn_red]", this.kd);
            this._reflection = new Reflection();
            this._reflection.init(instantZone);
            this.ka = this._reflection.getZone(this.jW);
            this.kb = this._reflection.getZone(this.jX);
            this.kc = this._reflection.getZone(this.jY);
            this.ka.addListener(PvPEvent.getInstance().jA);
            this.kb.addListener(PvPEvent.getInstance().jA);
            this.kc.addListener(PvPEvent.getInstance().jA);
            CTFFlagInstance cTFFlagInstance = new CTFFlagInstance(TeamType.RED, this);
            cTFFlagInstance.setSpawnedLoc(this.a(TeamType.RED));
            cTFFlagInstance.setReflection(this.getReflection());
            cTFFlagInstance.setCurrentHpMp(cTFFlagInstance.getMaxHp(), cTFFlagInstance.getMaxMp(), true);
            cTFFlagInstance.spawnMe(cTFFlagInstance.getSpawnedLoc());
            this.ki = new WeakReference<CTFFlagInstance>(cTFFlagInstance);
            CTFFlagInstance cTFFlagInstance2 = new CTFFlagInstance(TeamType.BLUE, this);
            cTFFlagInstance2.setSpawnedLoc(this.a(TeamType.BLUE));
            cTFFlagInstance2.setReflection(this.getReflection());
            cTFFlagInstance2.setCurrentHpMp(cTFFlagInstance2.getMaxHp(), cTFFlagInstance2.getMaxMp(), true);
            cTFFlagInstance2.spawnMe(cTFFlagInstance2.getSpawnedLoc());
            this.kj = new WeakReference<CTFFlagInstance>(cTFFlagInstance2);
        }

        @Override
        public void doneReflection() {
            if (this.kj.get() != null) {
                ((CTFFlagInstance)this.kj.get()).destroy();
                this.kj.clear();
            }
            if (this.ki.get() != null) {
                ((CTFFlagInstance)this.ki.get()).destroy();
                this.ki.clear();
            }
            this.ki = null;
            this.kj = null;
            this.ka.removeListener(PvPEvent.getInstance().jA);
            this.kb.removeListener(PvPEvent.getInstance().jA);
            this.kc.removeListener(PvPEvent.getInstance().jA);
            this.ka = null;
            this.kb = null;
            this.kc = null;
            this._reflection.collapse();
            this._reflection = null;
        }

        @Override
        public Reflection getReflection() {
            return this._reflection;
        }

        @Override
        public void OnPlayerDied(Player player, Player player2) {
            ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
            PvPEvent pvPEvent = PvPEvent.getInstance();
            pvPEvent.getClass();
            threadPoolManager.schedule(pvPEvent.new TeleportAndReviveTask(player, this.a(player.getTeam()), this.getReflection()), PvPEvent.getInstance().ag() * 1000);
        }

        @Override
        public void OnEnter(Player player, Zone zone) {
            if (player != null && !player.isDead()) {
                if (zone == this.ka && player.getTeam() != TeamType.BLUE && player.getTeam() != TeamType.RED) {
                    player.teleToClosestTown();
                    _log.warn("PvPEvent.CTF: '" + player.getName() + "' in zone.");
                } else if (zone == this.kb && player.getTeam() == TeamType.BLUE && player.getObjectId() == ((CTFFlagInstance)this.ki.get()).getOwnerOid()) {
                    ((CTFFlagInstance)this.ki.get()).removeFlag(null);
                    this.jU.incrementAndGet();
                } else if (zone == this.kc && player.getTeam() == TeamType.RED && player.getObjectId() == ((CTFFlagInstance)this.kj.get()).getOwnerOid()) {
                    ((CTFFlagInstance)this.kj.get()).removeFlag(null);
                    this.jT.incrementAndGet();
                }
            }
        }

        @Override
        public void OnLeave(Player player, Zone zone) {
            if (player != null && !this.ka.checkIfInZone(player.getX(), player.getY(), player.getZ(), this.getReflection()) && zone == this.ka) {
                if (player.getTeam() != TeamType.BLUE && player.getTeam() != TeamType.RED) {
                    if (PvPEvent.getInstance().ad() && player.getTransformation() == 0) {
                        player.setTransformationName(null);
                    }
                    player.teleToClosestTown();
                    return;
                }
                double d = Math.PI * 2 - PositionUtils.convertHeadingToRadian(player.getHeading());
                double d2 = Math.cos(d);
                double d3 = Math.sin(d);
                Location location = zone.getTerritory().getRandomLoc(player.getGeoIndex());
                for (int i = 32; i < 512; i += 32) {
                    int n = (int)Math.floor((double)player.getX() - (double)i * d2);
                    int n2 = (int)Math.floor((double)player.getY() + (double)i * d3);
                    if (!zone.getTerritory().isInside(n, n2)) continue;
                    location.set(n, n2, player.getZ(), player.getHeading());
                    break;
                }
                ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
                PvPEvent pvPEvent = PvPEvent.getInstance();
                pvPEvent.getClass();
                threadPoolManager.schedule(pvPEvent.new TeleportTask(player, location.correctGeoZ(), this.getReflection()), 3000L);
                if (player.getObjectId() == ((CTFFlagInstance)this.kj.get()).getOwnerOid()) {
                    ((CTFFlagInstance)this.kj.get()).removeFlag(player);
                } else if (player.getObjectId() == ((CTFFlagInstance)this.ki.get()).getOwnerOid()) {
                    ((CTFFlagInstance)this.ki.get()).removeFlag(player);
                }
            }
        }

        @Override
        public void OnExit(Player player) {
            String string;
            if (this.jS.containsKey(player.getObjectId())) {
                this.jS.remove(player.getObjectId());
            } else if (this.jR.containsKey(player.getObjectId())) {
                this.jR.remove(player.getObjectId());
            }
            if (player.getTransformation() == 0) {
                player.setTransformationName(null);
                player.setTransformationTitle(null);
            }
            if ((string = player.getVar(jV)) != null) {
                player.setTitle(player.getVar(jV));
                player.unsetVar(jV);
            }
        }

        @Override
        public void OnTeleport(Player player, int n, int n2, int n3, Reflection reflection) {
            Location location;
            if (player != null && !this.ka.checkIfInZone(n, n2, n3, reflection) && (location = this.a(player.getTeam())) != null) {
                ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
                PvPEvent pvPEvent = PvPEvent.getInstance();
                pvPEvent.getClass();
                threadPoolManager.schedule(pvPEvent.new TeleportTask(player, location, this.getReflection()), 3000L);
            }
        }

        private void a(Map<Integer, AtomicInteger> map, List<Pair<ItemTemplate, Long>> list) {
            for (Map.Entry<Integer, AtomicInteger> entry : map.entrySet()) {
                int n = entry.getKey();
                Player player = GameObjectsStorage.getPlayer(n);
                if (player == null) continue;
                for (Pair<ItemTemplate, Long> pair : list) {
                    Functions.addItem(player, pair.getLeft().getItemId(), pair.getRight());
                }
            }
        }

        @Override
        public void MakeWinner() {
            int n;
            int n2 = this.jU.get();
            if (n2 > (n = this.jT.get())) {
                this.a(this.jS, PvPEvent.getInstance().an());
                Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TeamBlueWonTheCTFGameCountIsS1S2", new String[]{String.valueOf(n2), String.valueOf(n)});
            } else if (n2 < n) {
                this.a(this.jR, PvPEvent.getInstance().an());
                Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TeamRedWonTheCTFGameCountIsS1S2", new String[]{String.valueOf(n), String.valueOf(n2)});
            } else if (n2 == n) {
                Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TheCTFGameEndedInATie", ArrayUtils.EMPTY_STRING_ARRAY);
            }
            PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{ExEventMatchMessage.FINISH});
        }

        private Location a(TeamType teamType) {
            if (teamType == TeamType.BLUE) {
                return this.kb.getTerritory().getRandomLoc(this._reflection.getGeoIndex());
            }
            if (teamType == TeamType.RED) {
                return this.kc.getTerritory().getRandomLoc(this._reflection.getGeoIndex());
            }
            return null;
        }

        private class CTFFlagInstance
        extends MonsterInstance
        implements FlagItemAttachment {
            private ItemInstance kl;
            private final TeamType _team;
            private CTFParticipantController km;

            public CTFFlagInstance(TeamType teamType, CTFParticipantController cTFParticipantController2) {
                super(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(teamType == TeamType.BLUE ? 32027 : (teamType == TeamType.RED ? 32027 : -1)));
                this._team = teamType;
                this.kl = ItemFunctions.createItem(teamType == TeamType.BLUE ? 6718 : (teamType == TeamType.RED ? 6718 : -1));
                this.kl.setAttachment(this);
                this.km = cTFParticipantController2;
            }

            public void destroy() {
                Player player = GameObjectsStorage.getPlayer(this.kl.getOwnerId());
                if (player != null) {
                    player.getInventory().destroyItem(this.kl);
                    player.sendDisarmMessage(this.kl);
                }
                this.kl.setAttachment(null);
                this.kl.deleteMe();
                this.kl.delete();
                this.kl = null;
                this.deleteMe();
            }

            @Override
            public boolean isAutoAttackable(Creature creature) {
                return this.isAttackable(creature);
            }

            @Override
            public boolean isAttackable(Creature creature) {
                return creature != null && creature.getTeam() != null && creature.getTeam() != TeamType.NONE && creature.getTeam() != this._team;
            }

            @Override
            protected void onDeath(Creature creature) {
                Player player;
                if (this.isAttackable(creature) && (player = creature.getPlayer()) != null && (this._team == TeamType.RED && creature.isInZone(this.km.kc) || this._team == TeamType.BLUE && creature.isInZone(this.km.kb))) {
                    player.getInventory().addItem(this.kl);
                    player.getInventory().equipItem(this.kl);
                    player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
                    this.decayMe();
                    if (this._team == TeamType.RED) {
                        ExShowScreenMessage exShowScreenMessage = new ExShowScreenMessage("'" + player.getName() + "' captured the Red flag!", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
                        for (Integer n : this.km.jR.keySet()) {
                            Player player2 = GameObjectsStorage.getPlayer(n);
                            if (player2 == null) continue;
                            player2.sendPacket((IStaticPacket)exShowScreenMessage);
                        }
                    } else if (this._team == TeamType.BLUE) {
                        ExShowScreenMessage exShowScreenMessage = new ExShowScreenMessage("'" + player.getName() + "' captured the Blue flag!", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
                        for (Integer n : this.km.jS.keySet()) {
                            Player player3 = GameObjectsStorage.getPlayer(n);
                            if (player3 == null) continue;
                            player3.sendPacket((IStaticPacket)exShowScreenMessage);
                        }
                    }
                    return;
                }
                this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp(), true);
            }

            public int getOwnerOid() {
                return this.kl.getOwnerId();
            }

            public void removeFlag(Player player) {
                if (player == null) {
                    player = GameObjectsStorage.getPlayer(this.kl.getOwnerId());
                }
                if (player != null) {
                    player.getInventory().removeItem(this.kl);
                    player.sendDisarmMessage(this.kl);
                }
                this.kl.setOwnerId(0);
                this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp(), true);
                this.spawnMe(this.km.a(this._team));
            }

            @Override
            public boolean canPickUp(Player player) {
                return false;
            }

            @Override
            public void pickUp(Player player) {
            }

            @Override
            public void setItem(ItemInstance itemInstance) {
                if (itemInstance != null) {
                    itemInstance.setCustomFlags(39);
                }
            }

            @Override
            public void onLogout(Player player) {
                player.getInventory().removeItem(this.kl);
                this.kl.setOwnerId(0);
                this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp(), true);
                this.spawnMe(this.km.a(this._team));
            }

            @Override
            public void onDeath(Player player, Creature creature) {
                player.getInventory().removeItem(this.kl);
                player.sendDisarmMessage(this.kl);
                this.kl.setOwnerId(0);
                this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp(), true);
                this.spawnMe(this.km.a(this._team));
            }

            @Override
            public void onEnterPeace(Player player) {
            }

            @Override
            public boolean canAttack(Player player) {
                player.sendMessage(new CustomMessage("THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS", player, new Object[0]));
                return false;
            }

            @Override
            public boolean canCast(Player player, Skill skill) {
                player.sendMessage(new CustomMessage("THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL", player, new Object[0]));
                return false;
            }

            @Override
            public boolean isEffectImmune() {
                return true;
            }

            @Override
            public boolean isDebuffImmune() {
                return true;
            }
        }
    }

    private static class TvTParticipantController
    implements IParticipantController {
        private static final RankComparator kz = new RankComparator();
        private Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> jR;
        private Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> jS;
        private static final String jV = "pvp_tvt_title";
        private int kd = 0;
        private String jW = "[pvp_%d_tvt_default]";
        private String jX = "[pvp_%d_tvt_spawn_blue]";
        private String jY = "[pvp_%d_tvt_spawn_red]";
        private String jZ = "backCoords";
        private Reflection _reflection = null;
        private Zone ka = null;
        private AtomicInteger jT;
        private AtomicInteger jU;
        private ScheduledFuture<?> kk;

        private TvTParticipantController() {
        }

        public int getKills(TeamType teamType) {
            int n = 0;
            if (teamType == TeamType.RED) {
                for (ImmutablePair<AtomicInteger, AtomicInteger> immutablePair : this.jR.values()) {
                    n += immutablePair.getLeft().get();
                }
            }
            if (teamType == TeamType.BLUE) {
                for (ImmutablePair<AtomicInteger, AtomicInteger> immutablePair : this.jS.values()) {
                    n += immutablePair.getLeft().get();
                }
            }
            return n;
        }

        @Override
        public void prepareParticipantsTo() {
            this.jR = new ConcurrentHashMap<Integer, ImmutablePair<AtomicInteger, AtomicInteger>>();
            this.jS = new ConcurrentHashMap<Integer, ImmutablePair<AtomicInteger, AtomicInteger>>();
            this.jT = new AtomicInteger(0);
            this.jU = new AtomicInteger(0);
            TeamType teamType = TeamType.BLUE;
            boolean bl = PvPEvent.getInstance().ap();
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                if (PvPEvent.c(player)) continue;
                PvPEvent.getInstance().jO.remove(player.getObjectId());
                this.OnExit(player);
            }
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                if (player.isAttackingNow()) {
                    player.abortAttack(true, false);
                }
                if (player.isCastingNow()) {
                    player.abortCast(true, false);
                }
                player.sendActionFailed();
                player.stopMove();
                player.sitDown(null);
                player.block();
                if (bl) {
                    player.getEffectList().stopAllEffects();
                    if (player.getPet() != null) {
                        player.getPet().getEffectList().stopAllEffects();
                    }
                }
                if (!Config.PVP_EVENTS_MAGE_BUFF.isEmpty() || !Config.PVP_EVENTS_WARRIOR_BUFF.isEmpty()) {
                    for (Pair<Integer, Integer> pair : player.isMageClass() ? Config.PVP_EVENTS_MAGE_BUFF : Config.PVP_EVENTS_WARRIOR_BUFF) {
                        Skill skill = SkillTable.getInstance().getInfo(pair.getLeft(), pair.getRight());
                        skill.getEffects(player, player, false, false);
                    }
                }
                boolean bl2 = false;
                if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                    player.getClan().disableSkills(player);
                }
                LinkedList<Effect> serializable2 = new LinkedList<Effect>();
                for (int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                    List<Effect> list;
                    int n = Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i];
                    Skill skill = player.getKnownSkill(n);
                    if (skill == null) continue;
                    if (skill.isToggle() && (list = player.getEffectList().getEffectsBySkill(skill)) != null && !list.isEmpty()) {
                        serializable2.addAll(list);
                    }
                    player.addUnActiveSkill(skill);
                    bl2 = true;
                }
                if (bl2) {
                    player.sendPacket((IStaticPacket)new SkillCoolTime(player));
                    player.updateStats();
                    player.updateEffectIcons();
                }
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
                player.sendChanges();
                player.setVar(jV, player.getTitle() != null ? player.getTitle() : "", -1L);
                if (teamType == TeamType.BLUE) {
                    player.setTeam(TeamType.BLUE);
                    this.jS.put(player.getObjectId(), new ImmutablePair<AtomicInteger, AtomicInteger>(new AtomicInteger(0), new AtomicInteger(0)));
                    teamType = TeamType.RED;
                } else {
                    player.setTeam(TeamType.RED);
                    this.jR.put(player.getObjectId(), new ImmutablePair<AtomicInteger, AtomicInteger>(new AtomicInteger(0), new AtomicInteger(0)));
                    teamType = TeamType.BLUE;
                }
                this.a(player, 0);
            }
        }

        @Override
        public void prepareParticipantsFrom() {
            boolean bl = PvPEvent.getInstance().aq();
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                if (player.isAttackingNow()) {
                    player.abortAttack(true, false);
                }
                if (player.isCastingNow()) {
                    player.abortCast(true, false);
                }
                player.sendActionFailed();
                player.stopMove();
                player.sitDown(null);
                player.block();
                if (bl) {
                    player.getEffectList().stopAllEffects();
                }
                if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                    player.getClan().enableSkills(player);
                }
                for (int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                    Skill skill;
                    int n = Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i];
                    if (!player.isUnActiveSkill(n) || (skill = player.getKnownSkill(n)) == null) continue;
                    player.removeUnActiveSkill(skill);
                }
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
                player.sendChanges();
                player.setTeam(TeamType.NONE);
                String string = player.getVar(jV);
                if (string != null) {
                    player.setTitle(string);
                    player.unsetVar(jV);
                }
                if (PvPEvent.getInstance().ad() && player.getTransformation() == 0) {
                    player.setTransformationName(null);
                    player.setTransformationTitle(null);
                }
                player.sendUserInfo(true);
            }
            this.jR.clear();
            this.jS.clear();
            this.jR = null;
            this.jS = null;
            this.jT = null;
            this.jU = null;
        }

        @Override
        public void initParticipant() {
            boolean bl = PvPEvent.getInstance().ah();
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.addListener(PvPEvent.getInstance().jz);
                player.addListener(PvPEvent.getInstance().jB);
                player.setResurectProhibited(true);
                player.unblock();
                player.standUp();
                if (!bl) continue;
                jy.getEffects(player, player, false, false, false);
            }
        }

        @Override
        public void doneParicipant() {
            if (this.kk != null) {
                this.kk.cancel(true);
                this.kk = null;
            }
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                player.removeListener(PvPEvent.getInstance().jz);
                player.removeListener(PvPEvent.getInstance().jB);
                player.setResurectProhibited(false);
                player.unblock();
                if (player.isDead()) {
                    player.doRevive(100.0);
                    player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                    player.setCurrentCp(player.getMaxCp());
                }
                player.standUp();
            }
        }

        private void a(Player player, int n) {
            player.setTransformationTitle(String.format("Kills: %d", n));
            player.setTitle(player.getTransformationTitle());
            player.broadcastPacket(new NickNameChanged(player));
        }

        @Override
        public void OnPlayerDied(Player player, Player player2) {
            ImmutablePair<AtomicInteger, AtomicInteger> immutablePair;
            if (player2 != null && player2.getTeam() != player.getTeam()) {
                if (player2.getTeam() == TeamType.RED && this.jR.containsKey(player2.getObjectId())) {
                    immutablePair = this.jR.get(player2.getObjectId());
                    AtomicInteger atomicInteger = immutablePair.getLeft();
                    this.a(player2, atomicInteger.incrementAndGet());
                    this.jT.incrementAndGet();
                } else if (player2.getTeam() == TeamType.BLUE && this.jS.containsKey(player2.getObjectId())) {
                    immutablePair = this.jS.get(player2.getObjectId());
                    AtomicInteger atomicInteger = immutablePair.getLeft();
                    this.a(player2, atomicInteger.incrementAndGet());
                    this.jU.incrementAndGet();
                } else if (player2.getTeam() != TeamType.NONE) {
                    _log.warn("PvPEvent.TVT: '" + player2.getName() + "' got color but not at list.");
                }
                player2.sendUserInfo(true);
            }
            if (player.getTeam() == TeamType.RED && this.jR.containsKey(player.getObjectId())) {
                immutablePair = this.jR.get(player.getObjectId());
                immutablePair.getRight().incrementAndGet();
            } else if (player.getTeam() == TeamType.BLUE && this.jS.containsKey(player.getObjectId())) {
                immutablePair = this.jS.get(player.getObjectId());
                immutablePair.getRight().incrementAndGet();
            }
            ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
            PvPEvent pvPEvent = PvPEvent.getInstance();
            pvPEvent.getClass();
            threadPoolManager.schedule(pvPEvent.new TeleportAndReviveTask(player, this.a(player.getTeam()), this.getReflection()), PvPEvent.getInstance().ag() * 1000);
        }

        @Override
        public void portParticipantsTo() {
            int n = 0;
            int n2 = 0;
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                TeamType teamType = player.getTeam();
                if (teamType != TeamType.BLUE && teamType != TeamType.RED) {
                    PvPEvent.getInstance().jO.remove(player.getObjectId());
                    this.OnExit(player);
                    continue;
                }
                player.setVar(this.jZ, player.getLoc().toXYZString(), -1L);
                if (player.getParty() != null) {
                    player.getParty().removePartyMember(player, false);
                }
                if (PvPEvent.getInstance().ad()) {
                    switch (teamType) {
                        case RED: {
                            player.setTransformationName(String.format("Red %d", ++n));
                            break;
                        }
                        case BLUE: {
                            player.setTransformationName(String.format("Blue %d", ++n2));
                        }
                    }
                }
                player.teleToLocation(this.a(teamType), this.getReflection());
            }
        }

        @Override
        public void portParticipantsBack() {
            for (Player player : PvPEvent.getInstance().getPlayers()) {
                String string;
                if (player.getTransformation() == 0) {
                    player.setTransformationName(null);
                }
                if ((string = player.getVar(this.jZ)) != null) {
                    player.unsetVar(this.jZ);
                    player.teleToLocation(Location.parseLoc(string), ReflectionManager.DEFAULT);
                    continue;
                }
                player.teleToClosestTown();
            }
        }

        @Override
        public void initReflection() {
            this.kd = PvPEvent.getInstance().at();
            InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(this.kd);
            this.jW = String.format("[pvp_%d_tvt_default]", this.kd);
            this.jX = String.format("[pvp_%d_tvt_spawn_blue]", this.kd);
            this.jY = String.format("[pvp_%d_tvt_spawn_red]", this.kd);
            this._reflection = new Reflection();
            this._reflection.init(instantZone);
            this.ka = this._reflection.getZone(this.jW);
            this.ka.addListener(PvPEvent.getInstance().jA);
        }

        @Override
        public void doneReflection() {
            this.ka.removeListener(PvPEvent.getInstance().jA);
            this._reflection.collapse();
            this._reflection = null;
        }

        @Override
        public Reflection getReflection() {
            return this._reflection;
        }

        private Location a(TeamType teamType) {
            if (teamType == TeamType.BLUE) {
                return this._reflection.getZone(this.jX).getTerritory().getRandomLoc(this._reflection.getGeoIndex());
            }
            if (teamType == TeamType.RED) {
                return this._reflection.getZone(this.jY).getTerritory().getRandomLoc(this._reflection.getGeoIndex());
            }
            return null;
        }

        @Override
        public void OnEnter(Player player, Zone zone) {
            if (player != null && !player.isGM() && player.getTeam() != TeamType.BLUE && player.getTeam() != TeamType.RED && zone == this.ka) {
                player.teleToClosestTown();
            }
        }

        @Override
        public void OnLeave(Player player, Zone zone) {
            if (player != null && !this.ka.checkIfInZone(player.getX(), player.getY(), player.getZ(), this.getReflection())) {
                if (player.getTeam() != TeamType.BLUE && player.getTeam() != TeamType.RED && zone == this.ka) {
                    player.teleToClosestTown();
                    return;
                }
                double d = Math.PI * 2 - PositionUtils.convertHeadingToRadian(player.getHeading());
                double d2 = Math.cos(d);
                double d3 = Math.sin(d);
                Location location = zone.getTerritory().getRandomLoc(player.getGeoIndex());
                for (int i = 32; i < 512; i += 32) {
                    int n = (int)Math.floor((double)player.getX() - (double)i * d2);
                    int n2 = (int)Math.floor((double)player.getY() + (double)i * d3);
                    if (!zone.getTerritory().isInside(n, n2)) continue;
                    location.set(n, n2, player.getZ(), player.getHeading());
                    break;
                }
                ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
                PvPEvent pvPEvent = PvPEvent.getInstance();
                pvPEvent.getClass();
                threadPoolManager.schedule(pvPEvent.new TeleportTask(player, location.correctGeoZ(), this.getReflection()), 3000L);
            }
        }

        @Override
        public void OnExit(Player player) {
            if (this.jS.containsKey(player.getObjectId())) {
                this.jS.remove(player.getObjectId());
            } else if (this.jR.containsKey(player.getObjectId())) {
                this.jR.remove(player.getObjectId());
            }
            String string = player.getVar(jV);
            if (string != null) {
                player.setTitle(player.getVar(jV));
                player.unsetVar(jV);
            }
        }

        @Override
        public void OnTeleport(Player player, int n, int n2, int n3, Reflection reflection) {
            Location location;
            if (player != null && !this.ka.checkIfInZone(n, n2, n3, this.getReflection()) && (location = this.a(player.getTeam())) != null) {
                ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
                PvPEvent pvPEvent = PvPEvent.getInstance();
                pvPEvent.getClass();
                threadPoolManager.schedule(pvPEvent.new TeleportTask(player, location, this.getReflection()), 3000L);
            }
        }

        private void a(Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> map) {
            int n = PvPEvent.getInstance().af();
            if (n <= 0) {
                return;
            }
            for (Map.Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> entry : map.entrySet()) {
                int n2 = entry.getKey();
                ImmutablePair<AtomicInteger, AtomicInteger> immutablePair = entry.getValue();
                int n3 = immutablePair.getLeft().get();
                Player player = GameObjectsStorage.getPlayer(n2);
                if (n3 <= 0 || player == null) continue;
                Functions.addItem(player, n, n3);
            }
        }

        private void a(Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> map, List<Pair<ItemTemplate, Long>> list, List<Pair<ItemTemplate, Long>> list2) {
            int n = -1;
            int n2 = Integer.MIN_VALUE;
            Object object = map.entrySet().iterator();
            while (((Iterator)object).hasNext()) {
                Map.Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> entry = (Map.Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>>)((Iterator)object).next();
                int n3 = entry.getKey();
                ImmutablePair<AtomicInteger, AtomicInteger> immutablePair = entry.getValue();
                int n4 = immutablePair.getLeft().get();
                Player player = GameObjectsStorage.getPlayer(n3);
                if (player == null) continue;
                boolean bl = false;
                if (n4 >= PvPEvent.getInstance().as()) {
                    for (Pair<ItemTemplate, Long> pair : list) {
                        bl = true;
                        Functions.addItem(player, pair.getLeft().getItemId(), pair.getRight());
                    }
                }
                if (n2 < n4) {
                    n2 = n4;
                    n = n3;
                }
                if (bl) continue;
                player.sendMessage(new CustomMessage("PVPEVENTS_YOUR_TEAM_WIN_BUT_NO_PRIZE", player, new Object[0]));
            }
            if (n > 0 && n2 > 0 && (object = GameObjectsStorage.getPlayer(n)) != null) {
                for (Pair pair : list2) {
                    Functions.addItem((Playable)object, ((ItemTemplate)pair.getLeft()).getItemId(), (Long)pair.getRight());
                }
                Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TheTvTGameTopPlayerIsS1", new String[]{((Creature)object).getName()});
                if (PvPEvent.getInstance().al() > 0) {
                    GlobalServices.makeCustomHero((Player)object, (long)(PvPEvent.getInstance().al() * 60) * 60L);
                }
            }
        }

        @Override
        public void MakeWinner() {
            int n;
            boolean bl = ServerVariables.getBool(PvPEvent.VAR_EVENT_COUNTDOWN, true);
            int n2 = this.jU.get();
            if (n2 > (n = this.jT.get())) {
                Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TeamBlueWonTheTvTGameCountIsS1S2", new String[]{String.valueOf(n2), String.valueOf(n)});
                this.a(this.jS, PvPEvent.getInstance().an(), PvPEvent.getInstance().ao());
            } else if (n2 < n) {
                Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TeamRedWonTheTvTGameCountIsS1S2", new String[]{String.valueOf(n), String.valueOf(n2)});
                this.a(this.jR, PvPEvent.getInstance().an(), PvPEvent.getInstance().ao());
            } else if (n2 == n) {
                Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TheTvTGameEndedInATie", ArrayUtils.EMPTY_STRING_ARRAY);
            }
            this.a(this.jR);
            this.a(this.jS);
            if (bl) {
                PvPEvent.getInstance().broadcast(new L2GameServerPacket[]{ExEventMatchMessage.FINISH});
            }
        }

        private class RankBroadcastTask
        implements Runnable {
            private TvTParticipantController kA;

            public RankBroadcastTask(TvTParticipantController tvTParticipantController2) {
                this.kA = tvTParticipantController2;
            }

            @Override
            public void run() {
                if (PvPEvent.getInstance().Z() != PvPEventState.COMPETITION) {
                    return;
                }
                this.kA.kk = ThreadPoolManager.getInstance().schedule(this, 20000L);
            }
        }

        private static class RankComparator
        implements Comparator<Map.Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>>> {
            private RankComparator() {
            }

            @Override
            public int compare(Map.Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> entry, Map.Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> entry2) {
                try {
                    int n;
                    if (entry == null && entry2 == null) {
                        return 1;
                    }
                    if (entry == null) {
                        return 1;
                    }
                    if (entry2 == null) {
                        return -1;
                    }
                    int n2 = entry.getKey();
                    int n3 = entry2.getKey();
                    int n4 = entry.getValue().getLeft().get();
                    return n4 != (n = entry2.getValue().getLeft().get()) ? n - n4 : n3 - n2;
                }
                catch (Exception exception) {
                    return 0;
                }
            }
        }
    }

    private static interface IParticipantController {
        public void prepareParticipantsTo();

        public void prepareParticipantsFrom();

        public void initParticipant();

        public void doneParicipant();

        public void portParticipantsTo();

        public void portParticipantsBack();

        public void initReflection();

        public void doneReflection();

        public Reflection getReflection();

        public void OnPlayerDied(Player var1, Player var2);

        public void OnEnter(Player var1, Zone var2);

        public void OnLeave(Player var1, Zone var2);

        public void OnExit(Player var1);

        public void OnTeleport(Player var1, int var2, int var3, int var4, Reflection var5);

        public void MakeWinner();
    }

    protected static enum PvPEventState {
        STANDBY,
        REGISTRATION,
        PORTING_TO,
        PREPARE_TO,
        COMPETITION,
        WINNER,
        PREPARE_FROM,
        PORTING_FROM;

    }
}
