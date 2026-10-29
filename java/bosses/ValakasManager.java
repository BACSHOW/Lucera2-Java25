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
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.data.StringHolder;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
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

public class ValakasManager
extends Functions
implements OnDeathListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(ValakasManager.class);
    private static final int[][] gF = new int[][]{{214880, -116144, -1644, 0}, {213696, -116592, -1644, 0}, {212112, -116688, -1644, 0}, {211184, -115472, -1664, 0}, {210336, -114592, -1644, 0}, {211360, -113904, -1644, 0}, {213152, -112352, -1644, 0}, {214032, -113232, -1644, 0}, {214752, -114592, -1644, 0}, {209824, -115568, -1421, 0}, {210528, -112192, -1403, 0}, {213120, -111136, -1408, 0}, {215184, -111504, -1392, 0}, {215456, -117328, -1392, 0}, {213200, -118160, -1424, 0}};
    private static List<NpcInstance> gG = new ArrayList<NpcInstance>();
    private static BossInstance gH;
    private static ScheduledFuture<?> gI;
    private static ScheduledFuture<?> eA;
    private static ScheduledFuture<?> eB;
    private static ScheduledFuture<?> eQ;
    private static ScheduledFuture<?> eC;
    private static ScheduledFuture<?> gJ;
    private static ScheduledFuture<?> eD;
    private static ScheduledFuture<?> eE;
    private static ScheduledFuture<?> eF;
    public static final int VALAKAS_NPC_ID = 29028;
    private static final int er = 31759;
    private static EpicBossState _state;
    private static Zone _zone;
    private static long eG;
    private static long eH;
    private static AtomicBoolean eI;
    private static final Location et;
    private static volatile boolean eJ;

    public static EpicBossState getEpicBossState() {
        return _state;
    }

    private static void t() {
        for (Player player : ValakasManager.v()) {
            player.teleToClosestTown();
        }
    }

    private static synchronized void u() {
        if (eF == null && ValakasManager.x()) {
            eF = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), Config.VALAKAS_CLEAR_ZONE_IF_ALL_DIE);
        }
    }

    private static List<Player> v() {
        return ValakasManager.getZone().getInsidePlayers();
    }

    private static long w() {
        SchedulingPattern schedulingPattern = null;
        if (!Config.FWA_FIXTIMEPATTERNOFVALAKAS.isEmpty()) {
            long l = System.currentTimeMillis();
            try {
                schedulingPattern = new SchedulingPattern(Config.FWA_FIXTIMEPATTERNOFVALAKAS);
                long l2 = schedulingPattern.next(l) - l;
                return Math.max(60000L, l2);
            }
            catch (SchedulingPattern.InvalidPatternException invalidPatternException) {
                throw new RuntimeException("Invalid respawn data \"" + Config.FWA_FIXTIMEPATTERNOFVALAKAS + "\" in " + ValakasManager.class.getSimpleName(), invalidPatternException);
            }
        }
        return (long)(Config.ALT_RAID_RESPAWN_MULTIPLIER * (double)(Config.FWV_FIXINTERVALOFVALAKAS + Rnd.get(0L, Config.FWV_RANDOMINTERVALOFVALAKAS)));
    }

    public static Zone getZone() {
        return _zone;
    }

    private static boolean x() {
        for (Player player : ValakasManager.v()) {
            if (player.isDead()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (creature.isPlayer() && _state != null && _state.getState() == EpicBossState.State.ALIVE && _zone != null && _zone.checkIfInZone(creature.getX(), creature.getY())) {
            ValakasManager.u();
        } else if (creature.isNpc() && creature.getNpcId() == 29028) {
            ThreadPoolManager.getInstance().schedule(new SpawnDespawn(12), 1L);
        }
    }

    private static void L() {
        if (!eI.compareAndSet(false, true)) {
            return;
        }
        _state.setRespawnDate(ValakasManager.w());
        _state.setState(EpicBossState.State.INTERVAL);
        _state.update();
        ValakasManager.z();
        eJ = false;
        for (int[] nArray : gF) {
            gG.add(Functions.spawn(new Location(nArray[0], nArray[1], nArray[2], nArray[3]), 31759));
        }
        Log.add("Valakas died", "bosses");
    }

    private static void setIntervalEndTask() {
        ValakasManager.setUnspawn();
        if (_state.getState().equals((Object)EpicBossState.State.ALIVE)) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
            return;
        }
        if (!_state.getState().equals((Object)EpicBossState.State.INTERVAL)) {
            _state.setRespawnDate(ValakasManager.w());
            _state.setState(EpicBossState.State.INTERVAL);
            _state.update();
        }
        ValakasManager.z();
    }

    private static void z() {
        if (eA != null) {
            eA.cancel(false);
            eA = null;
        }
        eA = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
    }

    private static void setUnspawn() {
        ValakasManager.t();
        eJ = false;
        if (gH != null) {
            gH.deleteMe();
        }
        for (NpcInstance npcInstance : gG) {
            if (npcInstance == null) continue;
            Spawner spawner = npcInstance.getSpawn();
            if (spawner != null) {
                spawner.stopRespawn();
            }
            npcInstance.deleteMe();
        }
        gG.clear();
        if (gI != null) {
            gI.cancel(false);
            gI = null;
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
        if (eD != null) {
            eD.cancel(false);
            eD = null;
        }
        if (eE != null) {
            eE.cancel(false);
            eE = null;
        }
        if (gJ != null) {
            gJ.cancel(false);
            gJ = null;
        }
        if (eF != null) {
            eF.cancel(false);
            eF = null;
        }
    }

    private static void A() {
        if (Config.VALAKAS_ANNOUNCE_PREPARING_SPAWN) {
            Announcements.getInstance().announceByCustomMessage("ValakasGoingSleep", null);
        }
        ValakasManager.setUnspawn();
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
        eE = ThreadPoolManager.getInstance().schedule(new MaxTimeCheck(), Config.FWA_LIMITMAXUNTILSLEEPVALAKAS);
    }

    public static synchronized void setValakasSpawnTask() {
        if (gI == null) {
            gI = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(1), Config.FWV_APPTIMEOFVALAKAS);
            if (Config.VALAKAS_ANNOUNCE_PREPARING_SPAWN) {
                Announcements.getInstance().announceByCustomMessage("ValakasPreparingToWakeUp", new String[]{Long.toString(Config.FWV_APPTIMEOFVALAKAS / 60000L)});
            }
        }
        if (Config.FWA_LIMITMAXUNTILSLEEPVALAKAS > 0L) {
            ValakasManager.setLastEnterTime();
        }
    }

    public static void broadcastCustomScreenMessage(String string) {
        for (Player player : ValakasManager.v()) {
            player.sendPacket((IStaticPacket)new ExShowScreenMessage(StringHolder.getInstance().getNotNull(player, string), 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
        }
    }

    private void init() {
        CharListenerList.addGlobal(this);
        _state = new EpicBossState(29028);
        _zone = ReflectionUtils.getZone("[valakas_epic]");
        _log.debug("ValakasManager: State of Valakas is " + (Object)((Object)_state.getState()) + ".");
        if (!_state.getState().equals((Object)EpicBossState.State.NOTSPAWN)) {
            ValakasManager.setIntervalEndTask();
        }
        _log.debug("ValakasManager: Next spawn date of Valakas is " + TimeUtils.toSimpleFormat(_state.getRespawnDate()) + ".");
    }

    public static boolean isEnableEnterToLair() {
        return _state.getState() == EpicBossState.State.NOTSPAWN;
    }

    public static void enterTheLair(Player player) {
        if (player == null) {
            return;
        }
        if (eJ) {
            player.sendMessage(new CustomMessage("ValakasStillReborn", player, new Object[0]));
            return;
        }
        switch (_state.getState()) {
            case ALIVE: {
                player.sendMessage(new CustomMessage("ValakasAlreadyReborn", player, new Object[0]));
                return;
            }
            case DEAD: 
            case INTERVAL: {
                player.sendMessage(new CustomMessage("ValakasStillReborn", player, new Object[0]));
                return;
            }
        }
        if (player.isDead() || player.isFlying() || player.isCursedWeaponEquipped() || !player.isInRange(player, 500L)) {
            player.sendMessage(new CustomMessage("ValakasPlayerNotRequirements", player, new Object[0]));
            return;
        }
        player.teleToLocation(et);
        ValakasManager.setValakasSpawnTask();
    }

    @Override
    public void onLoad() {
        this.init();
    }

    @Override
    public void onReload() {
        ValakasManager.A();
    }

    @Override
    public void onShutdown() {
    }

    static /* synthetic */ List access$800() {
        return ValakasManager.v();
    }

    static {
        gI = null;
        eA = null;
        eB = null;
        eQ = null;
        eC = null;
        gJ = null;
        eD = null;
        eF = null;
        eG = 0L;
        eH = 0L;
        eI = new AtomicBoolean(false);
        et = new Location(203940, -111840, 66);
        eJ = false;
    }

    private static class SpawnDespawn
    extends RunnableImpl {
        private int eK = 2550;
        private int eL;
        private List<Player> eM = ValakasManager.access$800();

        SpawnDespawn(int n) {
            this.eL = n;
        }

        @Override
        public void runImpl() throws Exception {
            switch (this.eL) {
                case 1: {
                    if (Config.VALAKAS_ANNOUNCE_PREPARING_SPAWN) {
                        Announcements.getInstance().announceByCustomMessage("ValakasWakeUp", null);
                    }
                    gH = (BossInstance)Functions.spawn(new Location(212852, -114842, -1632, 833), 29028);
                    eI.set(false);
                    gH.block();
                    gH.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS03_A", 1, gH.getObjectId(), gH.getLoc()));
                    _state.setRespawnDate((long)(Config.ALT_RAID_RESPAWN_MULTIPLIER * (double)(Config.FWV_FIXINTERVALOFVALAKAS + Rnd.get(0L, Config.FWV_RANDOMINTERVALOFVALAKAS))));
                    _state.setState(EpicBossState.State.ALIVE);
                    _state.update();
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(2), 16L);
                    eJ = true;
                    break;
                }
                case 2: {
                    gH.broadcastPacket(new SocialAction(gH.getObjectId(), 1));
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1800, 180, -1, 1500, 15000, 0, 0, 1, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(3), 1500L);
                    break;
                }
                case 3: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1300, 180, -5, 3000, 15000, 0, -5, 1, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(4), 3300L);
                    break;
                }
                case 4: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 500, 180, -8, 600, 15000, 0, 60, 1, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(5), 2900L);
                    break;
                }
                case 5: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 800, 180, -8, 2700, 15000, 0, 30, 1, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(6), 2700L);
                    break;
                }
                case 6: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 200, 250, 70, 0, 15000, 30, 80, 1, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(7), 1L);
                    break;
                }
                case 7: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1100, 250, 70, 2500, 15000, 30, 80, 1, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(8), 3200L);
                    break;
                }
                case 8: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 700, 150, 30, 0, 15000, -10, 60, 1, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(9), 1400L);
                    break;
                }
                case 9: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1200, 150, 20, 2900, 15000, -10, 30, 1, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(10), 6700L);
                    break;
                }
                case 10: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 750, 170, -10, 3400, 15000, 10, -15, 1, 0);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(11), 5700L);
                    break;
                }
                case 11: {
                    for (Player player : this.eM) {
                        player.leaveMovieMode();
                    }
                    gH.unblock();
                    ValakasManager.broadcastCustomScreenMessage("ValakasFool");
                    if (gH.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
                        gH.moveToLocation(new Location(Rnd.get(211080, 214909), Rnd.get(-115841, -112822), -1662, 0), 0, false);
                    }
                    eD = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000L);
                    break;
                }
                case 12: {
                    gH.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "B03_D", 1, gH.getObjectId(), gH.getLoc()));
                    ValakasManager.broadcastCustomScreenMessage("VALAKAS_THE_EVIL_FIRE_DRAGON_VALAKAS_DEFEATED");
                    ValakasManager.L();
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 2000, 130, -1, 0, 15000, 0, 0, 1, 1);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(13), 500L);
                    break;
                }
                case 13: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1100, 210, -5, 3000, 15000, -13, 0, 1, 1);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(14), 3500L);
                    break;
                }
                case 14: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1300, 200, -8, 3000, 15000, 0, 15, 1, 1);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(15), 4500L);
                    break;
                }
                case 15: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1000, 190, 0, 500, 15000, 0, 10, 1, 1);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(16), 500L);
                    break;
                }
                case 16: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1700, 120, 0, 2500, 15000, 12, 40, 1, 1);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(17), 4600L);
                    break;
                }
                case 17: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1700, 20, 0, 700, 15000, 10, 10, 1, 1);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(18), 750L);
                    break;
                }
                case 18: {
                    for (Player player : this.eM) {
                        if (player.getDistance(gH) <= (double)this.eK) {
                            player.enterMovieMode();
                            player.specialCamera(gH, 1700, 10, 0, 1000, 15000, 20, 70, 1, 1);
                            continue;
                        }
                        player.leaveMovieMode();
                    }
                    eB = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(19), 2500L);
                    break;
                }
                case 19: {
                    for (Player player : this.eM) {
                        player.leaveMovieMode();
                    }
                    break;
                }
            }
        }
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
            Class<ValakasManager> clazz = ValakasManager.class;
            synchronized (ValakasManager.class) {
                if (!ValakasManager.x() || _state.getState() == EpicBossState.State.INTERVAL) {
                    eF = null;
                    // ** MonitorExit[var1_1] (shouldn't be in output)
                    return;
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
                ValakasManager.A();
                return;
            }
        }
    }

    private static class IntervalEnd
    extends RunnableImpl {
        private IntervalEnd() {
        }

        @Override
        public void runImpl() throws Exception {
            ValakasManager.setUnspawn();
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
            if (eH + Config.FWA_LIMITMAXUNTILSLEEPVALAKAS <= System.currentTimeMillis()) {
                ValakasManager.A();
            }
        }
    }

    private static class CheckLastAttack
    extends RunnableImpl {
        private CheckLastAttack() {
        }

        @Override
        public void runImpl() throws Exception {
            if (_state.getState() == EpicBossState.State.ALIVE) {
                if (eG + Config.FWV_LIMITUNTILSLEEPVALAKAS < System.currentTimeMillis()) {
                    ValakasManager.A();
                } else {
                    eD = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 60000L);
                }
            }
        }
    }
}

