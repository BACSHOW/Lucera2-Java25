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
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.StringHolder;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.BossInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.ReflectionUtils;
import l2.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntharasManager
extends Functions
implements OnDeathListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(AntharasManager.class);
    public static final int ANTHARAS_STRONG = 29068;
    private static final int er = 31859;
    private static final int es = 3865;
    private static final Location et = new Location(179892, 114915, -7704);
    private static final Location eu = new Location(177615, 114941, -7709, 0);
    private static final Location ev = new Location(181911, 114835, -7678, 32542);
    private static BossInstance ew;
    private static NpcInstance ex;
    private static List<NpcInstance> ey;
    private static ScheduledFuture<?> ez;
    private static ScheduledFuture<?> eA;
    private static ScheduledFuture<?> eB;
    private static ScheduledFuture<?> eC;
    private static ScheduledFuture<?> eD;
    private static ScheduledFuture<?> eE;
    private static ScheduledFuture<?> eF;
    private static EpicBossState _state;
    private static Zone _zone;
    private static long eG;
    private static long eH;
    private static AtomicBoolean eI;
    private static volatile boolean eJ;

    public static EpicBossState getEpicBossState() {
        return _state;
    }

    private static void t() {
        for (Player player : AntharasManager.v()) {
            player.teleToClosestTown();
        }
    }

    private static synchronized void u() {
        if (eF == null && AntharasManager.x()) {
            eF = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), Config.ANTHARAS_CLEAR_ZONE_IF_ALL_DIE);
        }
    }

    private static List<Player> v() {
        return AntharasManager.getZone().getInsidePlayers();
    }

    private static long w() {
        SchedulingPattern schedulingPattern = null;
        if (!Config.FWA_FIXTIMEPATTERNOFANTHARAS.isEmpty()) {
            long l = System.currentTimeMillis();
            try {
                schedulingPattern = new SchedulingPattern(Config.FWA_FIXTIMEPATTERNOFANTHARAS);
                long l2 = schedulingPattern.next(l) - l;
                return Math.max(60000L, l2);
            }
            catch (SchedulingPattern.InvalidPatternException invalidPatternException) {
                throw new RuntimeException("Invalid respawn data \"" + Config.FWA_FIXTIMEPATTERNOFANTHARAS + "\" in " + AntharasManager.class.getSimpleName(), invalidPatternException);
            }
        }
        return (long)(Config.ALT_RAID_RESPAWN_MULTIPLIER * (double)(Config.FWA_FIXTIMEINTERVALOFANTHARAS + Rnd.get(0L, Config.FWA_RANDOMINTERVALOFANTHARAS)));
    }

    public static Zone getZone() {
        return _zone;
    }

    private static boolean x() {
        for (Player player : AntharasManager.v()) {
            if (player.isDead()) continue;
            return false;
        }
        return true;
    }

    private static void y() {
        if (!eI.compareAndSet(false, true)) {
            return;
        }
        _state.setRespawnDate(AntharasManager.w());
        _state.setState(EpicBossState.State.INTERVAL);
        _state.update();
        AntharasManager.z();
        eJ = false;
        ex = Functions.spawn(eu, 31859);
        Log.add("Antharas died", "bosses");
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (creature.isPlayer() && _state != null && _state.getState() == EpicBossState.State.ALIVE && _zone != null && _zone.checkIfInZone(creature.getX(), creature.getY())) {
            AntharasManager.u();
        } else if (creature.isNpc() && creature.getNpcId() == 29068) {
            ThreadPoolManager.getInstance().schedule(new AntharasSpawn(8), 10L);
        }
    }

    private static void setIntervalEndTask() {
        AntharasManager.setUnspawn();
        if (_state.getState().equals((Object)EpicBossState.State.ALIVE)) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
            return;
        }
        if (!_state.getState().equals((Object)EpicBossState.State.INTERVAL)) {
            _state.setRespawnDate(AntharasManager.w());
            _state.setState(EpicBossState.State.INTERVAL);
            _state.update();
        }
        AntharasManager.z();
    }

    private static void z() {
        if (eA != null) {
            eA.cancel(false);
            eA = null;
        }
        eA = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
    }

    private static void setUnspawn() {
        AntharasManager.t();
        if (ew != null) {
            ew.deleteMe();
        }
        for (NpcInstance npcInstance : ey) {
            npcInstance.deleteMe();
        }
        if (ex != null) {
            ex.deleteMe();
        }
        eJ = false;
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
        if (eC != null) {
            eC.cancel(false);
            eC = null;
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

    private void init() {
        _state = new EpicBossState(29068);
        _zone = ReflectionUtils.getZone("[antharas_epic]");
        CharListenerList.addGlobal(this);
        _log.debug("AntharasManager: State of Antharas is " + (Object)((Object)_state.getState()) + ".");
        if (!_state.getState().equals((Object)EpicBossState.State.NOTSPAWN)) {
            AntharasManager.setIntervalEndTask();
        }
        _log.debug("AntharasManager: Next spawn date of Antharas is " + TimeUtils.toSimpleFormat(_state.getRespawnDate()) + ".");
    }

    private static void A() {
        if (Config.ANTHARAS_ANNOUNCE_PREPARING_SPAWN) {
            Announcements.getInstance().announceByCustomMessage("AntharasGoingSleep", null);
        }
        AntharasManager.setUnspawn();
        if (_state.getState().equals((Object)EpicBossState.State.ALIVE)) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
        }
    }

    public static void setLastAttackTime() {
        eG = System.currentTimeMillis();
    }

    public static void setLastEnterTime() {
        eH = System.currentTimeMillis();
        eE = ThreadPoolManager.getInstance().schedule(new MaxTimeCheck(), Config.FWA_LIMITMAXUNTILSLEEPANTHARAS);
    }

    public static synchronized void setAntharasSpawnTask() {
        if (ez == null) {
            if (Config.ANTHARAS_ANNOUNCE_PREPARING_SPAWN) {
                Announcements.getInstance().announceByCustomMessage("AntharasPreparingToWakeUp", new String[]{Long.toString(Config.FWV_APPTIMEOFVALAKAS / 60000L)});
            }
            ez = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(1), Config.FWA_APPTIMEOFANTHARAS);
        }
        if (Config.FWA_LIMITMAXUNTILSLEEPANTHARAS > 0L) {
            AntharasManager.setLastEnterTime();
        }
    }

    public static void broadcastCustomScreenMessage(String string) {
        for (Player player : AntharasManager.v()) {
            player.sendPacket((IStaticPacket)new ExShowScreenMessage(StringHolder.getInstance().getNotNull(player, string), 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
        }
    }

    public static void addSpawnedMinion(NpcInstance npcInstance) {
        ey.add(npcInstance);
    }

    public static void enterTheLair(Player player) {
        if (player == null) {
            return;
        }
        if (eJ) {
            player.sendMessage(new CustomMessage("AntharasStillReborn", player, new Object[0]));
            return;
        }
        switch (_state.getState()) {
            case ALIVE: {
                player.sendMessage(new CustomMessage("AntharasAlreadyReborn", player, new Object[0]));
                return;
            }
            case DEAD: 
            case INTERVAL: {
                player.sendMessage(new CustomMessage("AntharasStillReborn", player, new Object[0]));
                return;
            }
        }
        if (player.isDead() || player.isFlying() || player.isCursedWeaponEquipped() || player.getInventory().getCountOf(3865) < 1L || !player.isInRange(player, 500L)) {
            player.sendMessage(new CustomMessage("AntharasPlayerNotRequirements", player, new Object[0]));
            return;
        }
        player.getInventory().destroyItemByItemId(3865, 1L);
        player.teleToLocation(et);
        AntharasManager.setAntharasSpawnTask();
    }

    @Override
    public void onLoad() {
        this.init();
    }

    @Override
    public void onReload() {
        AntharasManager.A();
    }

    @Override
    public void onShutdown() {
    }

    static /* synthetic */ List access$000() {
        return AntharasManager.v();
    }

    static {
        ey = new ArrayList<NpcInstance>();
        eG = 0L;
        eH = 0L;
        eI = new AtomicBoolean(false);
        eJ = false;
    }

    private static class onAnnihilated
    extends RunnableImpl {
        private onAnnihilated() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void runImpl() {
            Class<AntharasManager> clazz = AntharasManager.class;
            synchronized (AntharasManager.class) {
                if (!AntharasManager.x() || _state.getState() == EpicBossState.State.INTERVAL) {
                    eF = null;
                    // ** MonitorExit[var1_1] (shouldn't be in output)
                    return;
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
                AntharasManager.A();
                return;
            }
        }
    }

    private static class IntervalEnd
    extends RunnableImpl {
        private IntervalEnd() {
        }

        @Override
        public void runImpl() {
            AntharasManager.setUnspawn();
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
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
            if (eH + Config.FWA_LIMITMAXUNTILSLEEPANTHARAS <= System.currentTimeMillis()) {
                AntharasManager.A();
            }
        }
    }

    private static class CheckLastAttack
    extends RunnableImpl {
        private CheckLastAttack() {
        }

        @Override
        public void runImpl() {
            if (_state.getState() == EpicBossState.State.ALIVE) {
                if (eG + Config.FWA_LIMITUNTILSLEEPANTHARAS < System.currentTimeMillis()) {
                    AntharasManager.A();
                } else {
                    eD = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 60000L);
                }
            }
        }
    }

    private static class AntharasSpawn
    extends RunnableImpl {
        private int eK = 2550;
        private int eL = 0;
        private List<Player> eM = AntharasManager.access$000();

        AntharasSpawn(int n) {
            this.eL = n;
        }

        @Override
        public void runImpl() {
            switch (this.eL) {
                case 1: {
                    if (Config.ANTHARAS_ANNOUNCE_PREPARING_SPAWN) {
                        Announcements.getInstance().announceByCustomMessage("AntharasWakeUp", null);
                    }
                    eI.set(false);
                    ew = (BossInstance)Functions.spawn(ev, 29068);
                    ew.setAggroRange(0);
                    _state.setRespawnDate(AntharasManager.w());
                    _state.setState(EpicBossState.State.ALIVE);
                    _state.update();
                    eB = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(2), 2000L);
                    eJ = true;
                    break;
                }
                case 2: {
                    for (Player player : this.eM) {
                        if (player.getDistance(ew) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(ew, 700, 13, -19, 0, 20000, 0, 0, 0, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(3), 3000L);
                    break;
                }
                case 3: {
                    ew.broadcastPacket(new SocialAction(ew.getObjectId(), 1));
                    for (Player player : this.eM) {
                        if (player.getDistance(ew) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(ew, 700, 13, 0, 6000, 20000, 0, 0, 0, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(4), 10000L);
                    break;
                }
                case 4: {
                    ew.broadcastPacket(new SocialAction(ew.getObjectId(), 2));
                    for (Player player : this.eM) {
                        if (player.getDistance(ew) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(ew, 3700, 0, -3, 0, 10000, 0, 0, 0, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(5), 200L);
                    break;
                }
                case 5: {
                    for (Player player : this.eM) {
                        if (player.getDistance(ew) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(ew, 1100, 0, -3, 22000, 30000, 0, 0, 0, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(6), 10800L);
                    break;
                }
                case 6: {
                    for (Player player : this.eM) {
                        if (player.getDistance(ew) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(ew, 1100, 0, -3, 300, 7000, 0, 0, 0, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(7), 7000L);
                    break;
                }
                case 7: {
                    for (Player player : this.eM) {
                        player.leaveMovieMode();
                    }
                    AntharasManager.broadcastCustomScreenMessage("AntharasYoucannothope");
                    ew.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_A", 1, ew.getObjectId(), ew.getLoc()));
                    ew.setAggroRange(ew.getTemplate().aggroRange);
                    ew.setRunning();
                    ew.moveToLocation(new Location(179011, 114871, -7704), 0, false);
                    eD = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000L);
                    break;
                }
                case 8: {
                    for (Player player : this.eM) {
                        if (player.getDistance(ew) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(ew, 1200, 20, -10, 0, 13000, 0, 0, 0, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(9), 13000L);
                    break;
                }
                case 9: {
                    for (Player player : this.eM) {
                        player.leaveMovieMode();
                    }
                    AntharasManager.broadcastCustomScreenMessage("ANTHARAS_THE_EVIL_LAND_DRAGON_ANTHARAS_DEFEATED");
                    AntharasManager.y();
                }
            }
        }
    }
}

