/*
 * Decompiled with CFR 0.152.
 */
package bosses;

import bosses.EpicBossState;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.SchedulingPattern;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.BossInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.Earthquake;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.ReflectionUtils;
import l2.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaiumManager
extends Functions
implements OnDeathListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(BaiumManager.class);
    private static ScheduledFuture<?> eN = null;
    private static ScheduledFuture<?> eO = null;
    private static ScheduledFuture<?> eA = null;
    private static ScheduledFuture<?> eP = null;
    private static ScheduledFuture<?> eQ = null;
    private static ScheduledFuture<?> eC = null;
    private static ScheduledFuture<?> eD = null;
    private static ScheduledFuture<?> eE;
    private static ScheduledFuture<?> eB;
    private static ScheduledFuture<?> eR;
    private static ScheduledFuture<?> eF;
    private static EpicBossState _state;
    private static long eG;
    private static long eH;
    private static NpcInstance eS;
    private static SimpleSpawner eT;
    private static NpcInstance eU;
    private static SimpleSpawner eV;
    private static List<NpcInstance> eW;
    private static Map<Integer, SimpleSpawner> eX;
    private static List<NpcInstance> eY;
    private static List<SimpleSpawner> eZ;
    private static Zone _zone;
    public static final int BAIUM_NPC_ID = 29020;
    private static final int fa = 29021;
    private static final int fb = 29025;
    private static AtomicBoolean eI;
    private static final Location[] fc;
    private static final Location fd;
    private static final Location fe;
    private static final int ff = 31842;

    public static EpicBossState getEpicBossState() {
        return _state;
    }

    private static void t() {
        for (Player player : BaiumManager.v()) {
            player.teleToClosestTown();
        }
    }

    private static synchronized void u() {
        if (eF == null && BaiumManager.x()) {
            eF = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), Config.BAIUM_CLEAR_ZONE_IF_ALL_DIE);
        }
    }

    private static void B() {
        for (NpcInstance npcInstance : eY) {
            if (npcInstance == null || npcInstance.getSpawn() == null) continue;
            npcInstance.getSpawn().stopRespawn();
            npcInstance.deleteMe();
        }
        eY.clear();
    }

    private static List<Player> v() {
        return BaiumManager.getZone().getInsidePlayers();
    }

    public static Zone getZone() {
        return _zone;
    }

    private void init() {
        Object object;
        _state = new EpicBossState(29020);
        _zone = ReflectionUtils.getZone("[baium_epic]");
        CharListenerList.addGlobal(this);
        try {
            eT = new SimpleSpawner(NpcHolder.getInstance().getTemplate(29025));
            eT.setAmount(1);
            eT.setLoc(fe);
            eT.stopRespawn();
            object = new SimpleSpawner(NpcHolder.getInstance().getTemplate(29020));
            ((Spawner)object).setAmount(1);
            eX.put(29020, (SimpleSpawner)object);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            object = NpcHolder.getInstance().getTemplate(31842);
            eV = new SimpleSpawner((NpcTemplate)object);
            eV.setAmount(1);
            eV.setLoc(fd);
            eV.setRespawnDelay(60);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            int n;
            object = NpcHolder.getInstance().getTemplate(29021);
            eZ.clear();
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (int i = 0; i < 5; ++i) {
                n = -1;
                while (n == -1 || arrayList.contains(n)) {
                    n = Rnd.get(10);
                }
                arrayList.add(n);
            }
            Iterator iterator = arrayList.iterator();
            while (iterator.hasNext()) {
                n = (Integer)iterator.next();
                SimpleSpawner simpleSpawner = new SimpleSpawner((NpcTemplate)object);
                simpleSpawner.setAmount(1);
                simpleSpawner.setLoc(fc[n]);
                simpleSpawner.setRespawnDelay(300000);
                eZ.add(simpleSpawner);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        _log.debug("BaiumManager: State of Baium is " + (Object)((Object)_state.getState()) + ".");
        if (_state.getState().equals((Object)EpicBossState.State.NOTSPAWN)) {
            eT.doSpawn(true);
        } else if (_state.getState().equals((Object)EpicBossState.State.ALIVE)) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
            eT.doSpawn(true);
        } else if (_state.getState().equals((Object)EpicBossState.State.INTERVAL) || _state.getState().equals((Object)EpicBossState.State.DEAD)) {
            BaiumManager.setIntervalEndTask();
        }
        _log.debug("BaiumManager: Next spawn date: " + TimeUtils.toSimpleFormat(_state.getRespawnDate()));
    }

    private static boolean x() {
        for (Player player : BaiumManager.v()) {
            if (player.isDead()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (creature.isPlayer() && _state != null && _state.getState() == EpicBossState.State.ALIVE && _zone != null && _zone.checkIfInZone(creature)) {
            BaiumManager.u();
        } else if (creature.isNpc() && creature.getNpcId() == 29020) {
            BaiumManager.onBaiumDie(creature);
        }
    }

    public static void onBaiumDie(Creature creature) {
        if (!eI.compareAndSet(false, true)) {
            return;
        }
        creature.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, creature.getLoc()));
        _state.setRespawnDate(BaiumManager.w());
        _state.setState(EpicBossState.State.INTERVAL);
        _state.update();
        BaiumManager.z();
        Log.add("Baium died", "bosses");
        BaiumManager.B();
        eO = ThreadPoolManager.getInstance().schedule(new CubeSpawn(), 10000L);
    }

    private static long w() {
        SchedulingPattern schedulingPattern = null;
        if (!Config.FWA_FIXTIMEPATTERNOFBAIUM.isEmpty()) {
            long l = System.currentTimeMillis();
            try {
                schedulingPattern = new SchedulingPattern(Config.FWA_FIXTIMEPATTERNOFBAIUM);
                long l2 = schedulingPattern.next(l) - l;
                return Math.max(60000L, l2);
            }
            catch (SchedulingPattern.InvalidPatternException invalidPatternException) {
                throw new RuntimeException("Invalid respawn data \"" + Config.FWA_FIXTIMEPATTERNOFBAIUM + "\" in " + BaiumManager.class.getSimpleName(), invalidPatternException);
            }
        }
        return (long)(Config.ALT_RAID_RESPAWN_MULTIPLIER * (double)(Config.FWB_FIXINTERVALOFBAIUM + Rnd.get(0L, Config.FWB_RANDOMINTERVALOFBAIUM)));
    }

    private static void setIntervalEndTask() {
        BaiumManager.setUnspawn();
        if (!_state.getState().equals((Object)EpicBossState.State.INTERVAL)) {
            _state.setRespawnDate(BaiumManager.w());
            _state.setState(EpicBossState.State.INTERVAL);
            _state.update();
        }
        BaiumManager.z();
    }

    private static void z() {
        if (eA != null) {
            eA.cancel(false);
            eA = null;
        }
        eA = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
    }

    public static void setLastAttackTime() {
        eG = System.currentTimeMillis();
    }

    public static void setUnspawn() {
        BaiumManager.t();
        BaiumManager.B();
        for (NpcInstance npcInstance : eW) {
            npcInstance.getSpawn().stopRespawn();
            npcInstance.deleteMe();
        }
        eW.clear();
        if (eU != null) {
            eU.getSpawn().stopRespawn();
            eU.deleteMe();
            eU = null;
        }
        if (eO != null) {
            eO.cancel(false);
            eO = null;
        }
        if (eA != null) {
            eA.cancel(false);
            eA = null;
        }
        if (eB != null) {
            eB.cancel(false);
            eB = null;
        }
        if (eQ != null) {
            eQ.cancel(false);
            eQ = null;
        }
        if (eC != null) {
            eC.cancel(false);
            eC = null;
        }
        if (eR != null) {
            eR.cancel(false);
            eR = null;
        }
        if (eP != null) {
            eP.cancel(false);
            eP = null;
        }
        if (eN != null) {
            eN.cancel(false);
            eN = null;
        }
        if (eD != null) {
            eD.cancel(false);
            eD = null;
        }
        if (eE != null) {
            eE.cancel(false);
            eE = null;
        }
        if (eF != null) {
            eF.cancel(false);
            eF = null;
        }
    }

    private static void C() {
        BaiumManager.setUnspawn();
        Log.add("Baium going to sleep, spawning statue", "bosses");
        _state.setState(EpicBossState.State.NOTSPAWN);
        _state.update();
        eT.doSpawn(true);
    }

    public static void spawnBaium(NpcInstance npcInstance, Player player) {
        eI.set(false);
        eS = npcInstance;
        SimpleSpawner simpleSpawner = eX.get(29020);
        simpleSpawner.setLoc(eS.getLoc());
        eS.getSpawn().stopRespawn();
        eS.deleteMe();
        BossInstance bossInstance = (BossInstance)simpleSpawner.doSpawn(true);
        eW.add(bossInstance);
        _state.setRespawnDate(BaiumManager.w());
        _state.setState(EpicBossState.State.ALIVE);
        _state.update();
        Log.add("Spawned Baium, awake by: " + player, "bosses");
        BaiumManager.setLastAttackTime();
        bossInstance.startImmobilized();
        bossInstance.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_A", 1, 0, bossInstance.getLoc()));
        bossInstance.broadcastPacket(new SocialAction(bossInstance.getObjectId(), 2));
        eB = ThreadPoolManager.getInstance().schedule(new Social(bossInstance, 3), 15000L);
        ThreadPoolManager.getInstance().schedule(new EarthquakeTask(bossInstance), 25000L);
        eR = ThreadPoolManager.getInstance().schedule(new Social(bossInstance, 1), 25000L);
        eP = ThreadPoolManager.getInstance().schedule(new KillPc(player, bossInstance), 26000L);
        eN = ThreadPoolManager.getInstance().schedule(new CallArchAngel(), 35000L);
        eQ = ThreadPoolManager.getInstance().schedule(new SetMobilised(bossInstance), 35500L);
        Location location = new Location(Rnd.get(112826, 116241), Rnd.get(15575, 16375), 10078, 0);
        eC = ThreadPoolManager.getInstance().schedule(new MoveAtRandom(bossInstance, location), 36000L);
        eD = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000L);
        if (Config.FWA_LIMITMAXUNTILSLEEPBAIUM > 0L) {
            BaiumManager.setLastEnterTime();
        }
    }

    public static void setLastEnterTime() {
        eH = System.currentTimeMillis();
        eE = ThreadPoolManager.getInstance().schedule(new MaxTimeCheck(), Config.FWA_LIMITMAXUNTILSLEEPBAIUM);
    }

    @Override
    public void onLoad() {
        if (Config.ALLOW_BAIUM_RAID) {
            this.init();
        }
    }

    @Override
    public void onReload() {
        BaiumManager.C();
    }

    @Override
    public void onShutdown() {
    }

    static {
        eB = null;
        eR = null;
        eF = null;
        eG = 0L;
        eH = 0L;
        eT = null;
        eU = null;
        eV = null;
        eW = new ArrayList<NpcInstance>();
        eX = new ConcurrentHashMap<Integer, SimpleSpawner>();
        eY = new ArrayList<NpcInstance>();
        eZ = new ArrayList<SimpleSpawner>();
        eI = new AtomicBoolean(false);
        fc = new Location[]{new Location(113004, 16209, 10136, 60242), new Location(114053, 16642, 10136, 4411), new Location(114563, 17184, 10136, 49241), new Location(116356, 16402, 10136, 31109), new Location(115015, 16393, 10136, 32760), new Location(115481, 15335, 10136, 16241), new Location(114680, 15407, 10136, 32485), new Location(114886, 14437, 10136, 16868), new Location(115391, 17593, 10136, 55346), new Location(115245, 17558, 10136, 35536)};
        fd = new Location(115203, 16620, 10078, 0);
        fe = new Location(115996, 17417, 10106, 41740);
    }

    public static class EarthquakeTask
    extends RunnableImpl {
        private final BossInstance fg;

        public EarthquakeTask(BossInstance bossInstance) {
            this.fg = bossInstance;
        }

        @Override
        public void runImpl() throws Exception {
            Earthquake earthquake = new Earthquake(this.fg.getLoc(), 40, 5);
            this.fg.broadcastPacket(earthquake);
        }
    }

    public static class onAnnihilated
    extends RunnableImpl {
        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void runImpl() {
            Class<BaiumManager> clazz = BaiumManager.class;
            synchronized (BaiumManager.class) {
                if (!BaiumManager.x() || _state.getState() == EpicBossState.State.INTERVAL) {
                    eF = null;
                    // ** MonitorExit[var1_1] (shouldn't be in output)
                    return;
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
                BaiumManager.C();
                return;
            }
        }
    }

    public static class Social
    extends RunnableImpl {
        private int fj;
        private NpcInstance _npc;

        public Social(NpcInstance npcInstance, int n) {
            this._npc = npcInstance;
            this.fj = n;
        }

        @Override
        public void runImpl() throws Exception {
            SocialAction socialAction = new SocialAction(this._npc.getObjectId(), this.fj);
            this._npc.broadcastPacket(socialAction);
        }
    }

    public static class SetMobilised
    extends RunnableImpl {
        private BossInstance fh;

        public SetMobilised(BossInstance bossInstance) {
            this.fh = bossInstance;
        }

        @Override
        public void runImpl() throws Exception {
            this.fh.stopImmobilized();
        }
    }

    public static class MoveAtRandom
    extends RunnableImpl {
        private NpcInstance _npc;
        private Location _pos;

        public MoveAtRandom(NpcInstance npcInstance, Location location) {
            this._npc = npcInstance;
            this._pos = location;
        }

        @Override
        public void runImpl() throws Exception {
            if (this._npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
                this._npc.moveToLocation(this._pos, 0, false);
            }
        }
    }

    public static class KillPc
    extends RunnableImpl {
        private BossInstance fh;
        private Player fi;

        public KillPc(Player player, BossInstance bossInstance) {
            this.fi = player;
            this.fh = bossInstance;
        }

        @Override
        public void runImpl() throws Exception {
            Skill skill = SkillTable.getInstance().getInfo(4136, 1);
            if (this.fi != null && skill != null) {
                this.fh.setTarget(this.fi);
                this.fh.doCast(skill, this.fi, false);
            }
        }
    }

    public static class IntervalEnd
    extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            BaiumManager.setUnspawn();
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
            eT.doSpawn(true);
        }
    }

    public static class CubeSpawn
    extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            eU = eV.doSpawn(true);
        }
    }

    private static class MaxTimeCheck
    extends RunnableImpl {
        private MaxTimeCheck() {
        }

        @Override
        public void runImpl() throws Exception {
            if (_state.getState() != EpicBossState.State.ALIVE) {
                return;
            }
            if (eH + Config.FWA_LIMITMAXUNTILSLEEPBAIUM <= System.currentTimeMillis()) {
                BaiumManager.C();
            }
        }
    }

    public static class CheckLastAttack
    extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            if (_state.getState().equals((Object)EpicBossState.State.ALIVE)) {
                if (eG + Config.FWB_LIMITUNTILSLEEPBAIUM < System.currentTimeMillis()) {
                    BaiumManager.C();
                } else {
                    eD = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 60000L);
                }
            }
        }
    }

    public static class CallArchAngel
    extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            for (SimpleSpawner simpleSpawner : eZ) {
                eY.add(simpleSpawner.doSpawn(true));
            }
        }
    }
}

