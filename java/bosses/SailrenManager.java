/*
 * Decompiled with CFR 0.152.
 */
package bosses;

import bosses.EpicBossState;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.SchedulingPattern;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.ReflectionUtils;
import l2.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SailrenManager
extends Functions
implements OnDeathListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(SailrenManager.class);
    private static NpcInstance gr;
    private static NpcInstance gs;
    private static NpcInstance gt;
    private static NpcInstance gu;
    private static NpcInstance eU;
    private static ScheduledFuture<?> eO;
    private static ScheduledFuture<?> ez;
    private static ScheduledFuture<?> eA;
    private static ScheduledFuture<?> eB;
    private static ScheduledFuture<?> gv;
    private static ScheduledFuture<?> eF;
    private static final int gw = 29065;
    private static final int gx = 22198;
    private static final int gy = 22199;
    private static final int gz = 22217;
    private static final int gA = 32107;
    private static EpicBossState _state;
    private static Zone _zone;
    private static Location gB;
    private static final boolean gC;
    private static AtomicBoolean gD;
    private static AtomicBoolean eI;

    private static void t() {
        for (Player player : SailrenManager.v()) {
            player.teleToClosestTown();
        }
    }

    private static synchronized void u() {
        if (eF == null && SailrenManager.x()) {
            eF = ThreadPoolManager.getInstance().schedule(new AnnihilateTask(), Config.SAILREN_CLEAR_ZONE_IF_ALL_DIE);
        }
    }

    private static List<Player> v() {
        return SailrenManager.getZone().getInsidePlayers();
    }

    private static long w() {
        SchedulingPattern schedulingPattern = null;
        if (!Config.FWA_FIXTIMEPATTERNOFSAILREN.isEmpty()) {
            long l = System.currentTimeMillis();
            try {
                schedulingPattern = new SchedulingPattern(Config.FWA_FIXTIMEPATTERNOFSAILREN);
                long l2 = schedulingPattern.next(l) - l;
                return Math.max(60000L, l2);
            }
            catch (SchedulingPattern.InvalidPatternException invalidPatternException) {
                throw new RuntimeException("Invalid respawn data \"" + Config.FWA_FIXTIMEPATTERNOFSAILREN + "\" in " + SailrenManager.class.getSimpleName(), invalidPatternException);
            }
        }
        return (long)(Config.ALT_RAID_RESPAWN_MULTIPLIER * (double)(Config.FWS_FIXINTERVALOFSAILRENSPAWN + Rnd.get(0L, Config.FWS_RANDOMINTERVALOFSAILRENSPAWN)));
    }

    public static Zone getZone() {
        return _zone;
    }

    private void init() {
        CharListenerList.addGlobal(this);
        _state = new EpicBossState(29065);
        _zone = ReflectionUtils.getZone("[sailren_epic]");
        _log.debug("SailrenManager: State of Sailren is " + (Object)((Object)_state.getState()) + ".");
        if (!_state.getState().equals((Object)EpicBossState.State.NOTSPAWN)) {
            SailrenManager.setIntervalEndTask();
        }
        _log.debug("SailrenManager: Next spawn date of Sailren is " + TimeUtils.toSimpleFormat(_state.getRespawnDate()) + ".");
    }

    private static boolean x() {
        for (Player player : SailrenManager.v()) {
            if (player.isDead()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (creature.isPlayer() && _state != null && _state.getState() == EpicBossState.State.ALIVE && _zone != null && _zone.checkIfInZone(creature.getX(), creature.getY())) {
            SailrenManager.u();
        } else if (creature == gr) {
            if (ez != null) {
                ez.cancel(false);
            }
            ez = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(22199), Config.FWS_INTERVALOFNEXTMONSTER);
        } else if (creature == gs) {
            if (ez != null) {
                ez.cancel(false);
            }
            ez = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(22217), Config.FWS_INTERVALOFNEXTMONSTER);
        } else if (creature == gt) {
            if (ez != null) {
                ez.cancel(false);
            }
            ez = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(29065), Config.FWS_INTERVALOFNEXTMONSTER);
        } else if (creature == gu) {
            SailrenManager.d(creature2);
        }
    }

    private static void d(Creature creature) {
        if (!eI.compareAndSet(false, true)) {
            return;
        }
        _state.setRespawnDate(SailrenManager.w());
        _state.setState(EpicBossState.State.INTERVAL);
        _state.update();
        SailrenManager.z();
        Log.add("Sailren died", "bosses");
        eO = ThreadPoolManager.getInstance().schedule(new CubeSpawn(), 10000L);
    }

    private static void setIntervalEndTask() {
        SailrenManager.setUnspawn();
        if (_state.getState().equals((Object)EpicBossState.State.ALIVE)) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
            return;
        }
        if (!_state.getState().equals((Object)EpicBossState.State.INTERVAL)) {
            _state.setRespawnDate(SailrenManager.w());
            _state.setState(EpicBossState.State.INTERVAL);
            _state.update();
        }
        SailrenManager.z();
    }

    private static void z() {
        if (eA != null) {
            eA.cancel(false);
            eA = null;
        }
        eA = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
    }

    private static void setUnspawn() {
        SailrenManager.t();
        if (gr != null) {
            if (gr.getSpawn() != null) {
                gr.getSpawn().stopRespawn();
            }
            gr.deleteMe();
            gr = null;
        }
        if (gs != null) {
            if (gs.getSpawn() != null) {
                gs.getSpawn().stopRespawn();
            }
            gs.deleteMe();
            gs = null;
        }
        if (gt != null) {
            if (gt.getSpawn() != null) {
                gt.getSpawn().stopRespawn();
            }
            gt.deleteMe();
            gt = null;
        }
        if (gu != null) {
            if (gu.getSpawn() != null) {
                gu.getSpawn().stopRespawn();
            }
            gu.deleteMe();
            gu = null;
        }
        if (eU != null) {
            if (eU.getSpawn() != null) {
                eU.getSpawn().stopRespawn();
            }
            eU.deleteMe();
            eU = null;
        }
        if (eO != null) {
            eO.cancel(false);
            eO = null;
        }
        if (ez != null) {
            ez.cancel(false);
            ez = null;
        }
        if (eA != null) {
            eA.cancel(false);
            eA = null;
        }
        if (eB != null) {
            eB.cancel(false);
            eB = null;
        }
        if (gv != null) {
            gv.cancel(false);
            gv = null;
        }
        if (eF != null) {
            eF.cancel(false);
            eF = null;
        }
    }

    private static void A() {
        SailrenManager.setUnspawn();
        gD.set(false);
        if (_state.getState().equals((Object)EpicBossState.State.ALIVE)) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
        }
    }

    public static synchronized void setSailrenSpawnTask() {
        if (ez == null) {
            ez = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(22198), Config.FWS_INTERVALOFNEXTMONSTER);
        }
    }

    public static boolean isEnableEnterToLair() {
        return _state.getState() == EpicBossState.State.NOTSPAWN;
    }

    public static CanIntoSailrenLairResult canIntoSailrenLair(Player player) {
        if (!gC && player.getParty() == null) {
            return CanIntoSailrenLairResult.Fail_NoParty;
        }
        if (gD.get()) {
            return CanIntoSailrenLairResult.Fail_InUse;
        }
        if (_state.getState().equals((Object)EpicBossState.State.ALIVE) || _state.getState().equals((Object)EpicBossState.State.DEAD)) {
            return CanIntoSailrenLairResult.Fail_Awake;
        }
        if (_state.getState().equals((Object)EpicBossState.State.INTERVAL)) {
            return CanIntoSailrenLairResult.Fail_Reborn;
        }
        return CanIntoSailrenLairResult.Ok;
    }

    public static void entryToSailrenLair(Player player) {
        if (gD.compareAndSet(false, true)) {
            if (player.getParty() == null) {
                player.teleToLocation(Location.findPointToStay(gB, 80, player.getGeoIndex()));
            } else {
                ArrayList<Player> arrayList = new ArrayList<Player>();
                for (Player player2 : player.getParty().getPartyMembers()) {
                    if (player2 == null || player2.isDead() || !player2.isInRange(player, 1000L)) continue;
                    arrayList.add(player2);
                }
                for (Player player2 : arrayList) {
                    player2.teleToLocation(Location.findPointToStay(gB, 80, player2.getGeoIndex()));
                }
            }
        }
    }

    @Override
    public void onLoad() {
        this.init();
    }

    @Override
    public void onReload() {
        SailrenManager.A();
    }

    @Override
    public void onShutdown() {
    }

    static {
        eO = null;
        ez = null;
        eA = null;
        eB = null;
        gv = null;
        eF = null;
        gB = new Location(27734, -6938, -1982);
        gC = Boolean.TRUE;
        gD = new AtomicBoolean(false);
        eI = new AtomicBoolean(false);
    }

    public static enum CanIntoSailrenLairResult {
        Ok,
        Fail_Awake,
        Fail_InUse,
        Fail_Reborn,
        Fail_NoParty;

    }

    private static class SailrenSpawn
    extends RunnableImpl {
        private int _npcId;
        private final Location _pos = new Location(27628, -6109, -1982, 44732);

        SailrenSpawn(int n) {
            this._npcId = n;
        }

        @Override
        public void runImpl() throws Exception {
            if (eB != null) {
                eB.cancel(false);
                eB = null;
            }
            switch (this._npcId) {
                case 22198: {
                    eI.set(false);
                    gr = Functions.spawn(new Location(27852, -5536, -1983, 44732), 22198);
                    ((DefaultAI)gr.getAI()).addTaskMove(this._pos, false);
                    if (eB != null) {
                        eB.cancel(false);
                        eB = null;
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new Social(gr, 2), 6000L);
                    if (gv != null) {
                        gv.cancel(false);
                        gv = null;
                    }
                    gv = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), Config.FWS_ACTIVITYTIMEOFMOBS);
                    break;
                }
                case 22199: {
                    gs = Functions.spawn(new Location(27852, -5536, -1983, 44732), 22199);
                    ((DefaultAI)gs.getAI()).addTaskMove(this._pos, false);
                    if (eB != null) {
                        eB.cancel(false);
                        eB = null;
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new Social(gs, 2), 6000L);
                    if (gv != null) {
                        gv.cancel(false);
                        gv = null;
                    }
                    gv = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), Config.FWS_ACTIVITYTIMEOFMOBS);
                    break;
                }
                case 22217: {
                    gt = Functions.spawn(new Location(27852, -5536, -1983, 44732), 22217);
                    ((DefaultAI)gt.getAI()).addTaskMove(this._pos, false);
                    if (eB != null) {
                        eB.cancel(false);
                        eB = null;
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new Social(gt, 2), 6000L);
                    if (gv != null) {
                        gv.cancel(false);
                        gv = null;
                    }
                    gv = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), Config.FWS_ACTIVITYTIMEOFMOBS);
                    break;
                }
                case 29065: {
                    gu = Functions.spawn(new Location(27810, -5655, -1983, 44732), 29065);
                    _state.setRespawnDate(SailrenManager.w() + Config.FWS_ACTIVITYTIMEOFMOBS);
                    _state.setState(EpicBossState.State.ALIVE);
                    _state.update();
                    gu.setRunning();
                    ((DefaultAI)gu.getAI()).addTaskMove(this._pos, false);
                    if (eB != null) {
                        eB.cancel(false);
                        eB = null;
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new Social(gu, 2), 6000L);
                    if (gv != null) {
                        gv.cancel(false);
                        gv = null;
                    }
                    gv = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), Config.FWS_ACTIVITYTIMEOFMOBS);
                }
            }
        }
    }

    private static class AnnihilateTask
    extends RunnableImpl {
        private AnnihilateTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void runImpl() {
            Class<SailrenManager> clazz = SailrenManager.class;
            synchronized (SailrenManager.class) {
                if (!SailrenManager.x() || _state.getState() == EpicBossState.State.INTERVAL) {
                    eF = null;
                    // ** MonitorExit[var1_1] (shouldn't be in output)
                    return;
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
                SailrenManager.A();
                return;
            }
        }
    }

    private static class Social
    extends RunnableImpl {
        private int fj;
        private NpcInstance _npc;

        public Social(NpcInstance npcInstance, int n) {
            this._npc = npcInstance;
            this.fj = n;
        }

        @Override
        public void runImpl() throws Exception {
            this._npc.broadcastPacket(new SocialAction(this._npc.getObjectId(), this.fj));
        }
    }

    private static class IntervalEnd
    extends RunnableImpl {
        private IntervalEnd() {
        }

        @Override
        public void runImpl() throws Exception {
            SailrenManager.setUnspawn();
            gD.set(false);
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
        }
    }

    private static class CubeSpawn
    extends RunnableImpl {
        private CubeSpawn() {
        }

        @Override
        public void runImpl() throws Exception {
            eU = Functions.spawn(new Location(27734, -6838, -1982, 0), 32107);
        }
    }

    private static class ActivityTimeEnd
    extends RunnableImpl {
        private ActivityTimeEnd() {
        }

        @Override
        public void runImpl() throws Exception {
            SailrenManager.A();
        }
    }
}

