/*
 * Decompiled with CFR 0.152.
 */
package bosses;

import bosses.EpicBossState;
import handler.items.SimpleItemHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.SchedulingPattern;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.handler.items.ItemHandler;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.CharListener;
import l2.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.Earthquake;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.MagicSkillCanceled;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import l2.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrintezzaManager
implements IAdminCommandHandler,
ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(FrintezzaManager.class);
    private static final FrintezzaManager fn = new FrintezzaManager();
    public static final int FRINTEZZA_NPC_ID = 29045;
    EpicBossState _state;
    private static final Location[] fo = new Location[]{new Location(173247, -76979, -5104, 1000), new Location(174985, -75161, -5104, 1000), new Location(173917, -76109, -5104, 1000), new Location(174037, -76321, -5104, 1000), new Location(173249, -75154, -5104, 1000)};
    private static final Location fp = new Location(174259, -86550, -5088, 16440);
    static final int BREAKING_ARROW_ITEM_ID = 8192;
    static final int DEWDROP_OF_DESSTRUCTION_ID = 8556;
    static final int FRINTEZZA_SEEKER_NPC_ID = 29059;
    static final long BREAKING_ARROW_CANCEL_TIME = 60000L;
    private static final String fq = "[LastImperialTomb]";
    private static final String fr = "[Frintezza]";
    private static final String fs = "[last_imperial_tomb_a_alarm_devices]";
    private static final String ft = "[last_imperial_tomb_a_hall_keeper]";
    private static final String fu = "[last_imperial_tomb_b_undeadband_players]";
    private static final String fv = "[last_imperial_tomb_b_undeadband_masters]";
    private static final String fw = "[last_imperial_tomb_b_undeadband_member]";
    private Skill fx;
    private Zone fy;
    private Zone fz;
    private Zone fA;
    private Zone fB;
    private Zone fC;
    private Zone fD;
    private Zone fE;
    private Zone fF;
    private Zone fG;
    private Zone fH;
    private Zone fI;
    private Zone fJ;
    private List<Spawner> fK = new ArrayList<Spawner>();
    private List<Spawner> fL = new ArrayList<Spawner>();
    private List<Spawner> fM = new ArrayList<Spawner>();
    private List<Spawner> fN = new ArrayList<Spawner>();
    private List<Spawner> fO = new ArrayList<Spawner>();
    private List<DoorInstance> fP = new ArrayList<DoorInstance>();
    private List<DoorInstance> fQ = new ArrayList<DoorInstance>();
    private List<DoorInstance> fR = new ArrayList<DoorInstance>();
    private List<DoorInstance> fS = new ArrayList<DoorInstance>();
    private DeathListener fT;
    private FrintessaEnterListener fU;
    private CurrentHpListener fV = new CurrentHpListener();
    private AtomicInteger fW;
    private Future<?> fX;
    private static int fY = 30000;
    private static NpcLocation fZ = new NpcLocation(174240, -89805, -5022, 16048, 29045);
    private static NpcLocation ga = new NpcLocation(174234, -88015, -5116, 48028, 29046);
    private static NpcLocation[] gb = new NpcLocation[]{new NpcLocation(175880, -88696, -5104, 35048, 29048), new NpcLocation(175816, -87160, -5104, 28205, 29049), new NpcLocation(172648, -87176, -5104, 64817, 29048), new NpcLocation(172600, -88664, -5104, 57730, 29049)};
    private static NpcLocation[] gc = new NpcLocation[]{new NpcLocation(175880, -88696, -5104, 35048, 29050), new NpcLocation(175816, -87160, -5104, 28205, 29051), new NpcLocation(172648, -87176, -5104, 64817, 29051), new NpcLocation(172600, -88664, -5104, 57730, 29050)};
    private NpcInstance gd;
    private NpcInstance ge;
    private NpcInstance gf;
    private NpcInstance gg;
    private NpcInstance[] gh = new NpcInstance[4];
    private NpcInstance[] gi = new NpcInstance[4];
    private Future<?> gj;
    private Future<?> gk;
    private int gl = 0;
    private static final long gm = 300000L;
    private ScheduledFuture<?> gn;

    public static final FrintezzaManager getInstance() {
        return fn;
    }

    public EpicBossState getEpicBossState() {
        return this._state;
    }

    private void init() {
        int n;
        this._state = new EpicBossState(29045);
        this.fW = new AtomicInteger(0);
        this.fy = ReflectionUtils.getZone(fq);
        this.fz = ReflectionUtils.getZone(fr);
        LOG.debug("FrintezzaManager: State of Frintezza is " + (Object)((Object)this._state.getState()) + ".");
        if (this._state.getState().equals((Object)EpicBossState.State.ALIVE)) {
            this._state.setState(EpicBossState.State.NOTSPAWN);
            this._state.update();
        } else if (this._state.getState().equals((Object)EpicBossState.State.INTERVAL) || this._state.getState().equals((Object)EpicBossState.State.DEAD)) {
            this.setIntervalEndTask();
        }
        Date date = new Date(this._state.getRespawnDate());
        LOG.debug("FrintezzaManager: Next spawn date of Frintezza is " + date.toString() + ".");
        this.fA = ReflectionUtils.getZone("[25_15_frintessa_01_01]");
        this.fB = ReflectionUtils.getZone("[25_15_frintessa_02_01]");
        this.fC = ReflectionUtils.getZone("[25_15_frintessa_01_02]");
        this.fD = ReflectionUtils.getZone("[25_15_frintessa_02_02]");
        this.fE = ReflectionUtils.getZone("[25_15_frintessa_01_03]");
        this.fF = ReflectionUtils.getZone("[25_15_frintessa_02_03]");
        this.fG = ReflectionUtils.getZone("[25_15_frintessa_01_04]");
        this.fH = ReflectionUtils.getZone("[25_15_frintessa_02_04]");
        this.fI = ReflectionUtils.getZone("[25_15_frintessa_01_05]");
        this.fJ = ReflectionUtils.getZone("[25_15_frintessa_02_05]");
        for (n = 25150042; n <= 25150043; ++n) {
            this.fP.add(ReflectionUtils.getDoor(n));
        }
        for (n = 25150045; n <= 25150046; ++n) {
            this.fQ.add(ReflectionUtils.getDoor(n));
        }
        for (n = 25150051; n <= 25150058; ++n) {
            this.fR.add(ReflectionUtils.getDoor(n));
        }
        for (n = 25150061; n <= 25150070; ++n) {
            this.fS.add(ReflectionUtils.getDoor(n));
        }
        this.fK.addAll(SpawnManager.getInstance().getSpawners(fs));
        this.fL.addAll(SpawnManager.getInstance().getSpawners(ft));
        this.fM.addAll(SpawnManager.getInstance().getSpawners(fu));
        this.fN.addAll(SpawnManager.getInstance().getSpawners(fv));
        this.fO.addAll(SpawnManager.getInstance().getSpawners(fw));
        this.fT = new DeathListener();
        this.fV = new CurrentHpListener();
        this.fU = new FrintessaEnterListener();
        this.fx = SkillTable.getInstance().getInfo(2234, 1);
        this.fz.addListener(this.fU);
        ItemHandler.getInstance().registerItemHandler(new FrintezzaItemHandler());
    }

    private static long w() {
        SchedulingPattern schedulingPattern = null;
        if (!Config.FWA_FIXTIMEPATTERNOFFRINTEZZA.isEmpty()) {
            long l = System.currentTimeMillis();
            try {
                schedulingPattern = new SchedulingPattern(Config.FWA_FIXTIMEPATTERNOFFRINTEZZA);
                long l2 = schedulingPattern.next(l) - l;
                return Math.max(60000L, l2);
            }
            catch (SchedulingPattern.InvalidPatternException invalidPatternException) {
                throw new RuntimeException("Invalid respawn data \"" + Config.FWA_FIXTIMEPATTERNOFFRINTEZZA + "\" in " + FrintezzaManager.class.getSimpleName(), invalidPatternException);
            }
        }
        return (long)(Config.ALT_RAID_RESPAWN_MULTIPLIER * (double)(Config.FWF_FIXINTERVALOFFRINTEZZA + Rnd.get(0L, Config.FWF_RANDOMINTERVALOFFRINTEZZA)));
    }

    public void setIntervalEndTask() {
        if (!EpicBossState.State.INTERVAL.equals((Object)this._state.getState())) {
            this._state.setRespawnDate(FrintezzaManager.w());
            this._state.setState(EpicBossState.State.INTERVAL);
            this._state.update();
        }
        this.gj = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), this._state.getInterval());
    }

    private void done() {
    }

    public boolean isInUse() {
        return this.fW.get() != 0;
    }

    public boolean canEnter() {
        if (this.isInUse()) {
            return false;
        }
        return this._state.getState() == EpicBossState.State.NOTSPAWN;
    }

    public boolean tryEnter(List<Party> list) {
        try {
            if (!this.startUp()) {
                return false;
            }
            for (int i = 0; i < list.size(); ++i) {
                Party party = list.get(i);
                Location location = fo[i % fo.length];
                for (Player player : party.getPartyMembers()) {
                    this.preparePlayableToEnter(player);
                    Location location2 = Location.findPointToStay(location, 256, player.getGeoIndex());
                    player.teleToLocation(location2);
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }

    public boolean startUp() {
        try {
            if (this.fW.compareAndSet(0, 1)) {
                this.t();
                SpawnManager.getInstance().despawn("[frintezza_teleporter]");
                this.D();
                this.b(this.fR);
                this.b(this.fP);
                this.b(this.fK, this.fT);
                this.e(this.fK);
                this.e(this.fL);
                this.b(this.fM, this.fT);
                this.e(this.fM);
                this.b(this.fN, this.fT);
                this.e(this.fN);
                this.e(this.fO);
                this.c(this.fK);
                this.d(this.fK);
                this.a(this.fK, this.fT);
                this.c(this.fL);
                this.fX = ThreadPoolManager.getInstance().schedule(new TombTimeoutTask(), 5000L);
                return true;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    private void D() {
        int n;
        if (this.gd != null) {
            this.gd.deleteMe();
            this.gd = null;
        }
        if (this.ge != null) {
            this.ge.deleteMe();
            this.ge = null;
        }
        if (this.gf != null) {
            this.gf.deleteMe();
            this.gf = null;
        }
        if (this.gg != null) {
            this.gg.deleteMe();
            this.gg = null;
        }
        for (n = 0; n < this.gh.length; ++n) {
            if (this.gh[n] == null) continue;
            this.gh[n].deleteMe();
            this.gh[n] = null;
        }
        for (n = 0; n < this.gi.length; ++n) {
            if (this.gi[n] == null) continue;
            this.gi[n].deleteMe();
            this.gi[n] = null;
        }
        for (Creature creature : this.fy.getObjects()) {
            if (creature == null || !(creature instanceof NpcInstance)) continue;
            creature.deleteMe();
        }
        if (this.gn != null) {
            this.gn.cancel(true);
            this.gn = null;
        }
        if (this.gk != null) {
            this.gk.cancel(true);
            this.gk = null;
        }
        this.I();
    }

    public boolean cleanUp() {
        if (this.fW.get() != 0) {
            this.t();
            this.b(this.fK, this.fT);
            this.e(this.fK);
            this.e(this.fL);
            this.b(this.fM, this.fT);
            this.e(this.fM);
            this.b(this.fN, this.fT);
            this.e(this.fN);
            this.e(this.fO);
            this.b(this.fR);
            this.b(this.fP);
            this.b(this.fS);
            this.b(this.fQ);
            this.D();
            if (this.gj != null) {
                this.gj.cancel(true);
                this.gj = null;
            }
            if (this._state.getState() == EpicBossState.State.ALIVE || this.fW.get() > 0 && this.fW.get() < 5) {
                this._state.setState(EpicBossState.State.NOTSPAWN);
                this._state.update();
            } else {
                this.setIntervalEndTask();
            }
            this.fW.set(0);
            return true;
        }
        return false;
    }

    private void a(List<DoorInstance> list) {
        for (DoorInstance doorInstance : list) {
            if (doorInstance == null) continue;
            doorInstance.openMe();
        }
    }

    private void b(List<DoorInstance> list) {
        for (DoorInstance doorInstance : list) {
            if (doorInstance == null) continue;
            doorInstance.closeMe();
        }
    }

    private void c(List<Spawner> list) {
        for (Spawner spawner : list) {
            spawner.init();
        }
    }

    private void d(List<Spawner> list) {
        for (Spawner spawner : list) {
            spawner.stopRespawn();
        }
    }

    private void e(List<Spawner> list) {
        for (Spawner spawner : list) {
            spawner.deleteAll();
        }
    }

    private List<NpcInstance> f(List<Spawner> list) {
        ArrayList<NpcInstance> arrayList = new ArrayList<NpcInstance>();
        for (Spawner spawner : list) {
            arrayList.addAll(spawner.getAllSpawned());
        }
        return arrayList;
    }

    private boolean a(NpcInstance npcInstance, List<Spawner> list) {
        Spawner spawner = npcInstance.getSpawn();
        for (Spawner spawner2 : list) {
            if (spawner != spawner2) continue;
            return true;
        }
        return false;
    }

    private List<NpcInstance> g(List<Spawner> list) {
        ArrayList<NpcInstance> arrayList = new ArrayList<NpcInstance>();
        for (Spawner spawner : list) {
            List<NpcInstance> list2 = spawner.getAllSpawned();
            for (NpcInstance npcInstance : list2) {
                if (npcInstance.isDead()) continue;
                arrayList.add(npcInstance);
            }
        }
        return arrayList;
    }

    private boolean h(List<Spawner> list) {
        for (Spawner spawner : list) {
            List<NpcInstance> list2 = spawner.getAllSpawned();
            for (NpcInstance npcInstance : list2) {
                if (npcInstance.isDead()) continue;
                return false;
            }
        }
        return true;
    }

    private void a(List<Spawner> list, CharListener charListener) {
        List<NpcInstance> list2 = this.f(list);
        for (NpcInstance npcInstance : list2) {
            npcInstance.addListener(charListener);
        }
    }

    private void b(List<Spawner> list, CharListener charListener) {
        List<NpcInstance> list2 = this.f(list);
        for (NpcInstance npcInstance : list2) {
            npcInstance.removeListener(charListener);
        }
    }

    private void c(NpcInstance npcInstance) {
        if (this.h(this.fK)) {
            if (this.fW.compareAndSet(1, 2)) {
                this.b(this.fK, this.fT);
                this.e(this.fL);
                this.a(this.fR);
                this.a(this.fP);
                Functions.npcShoutCustomMessage(npcInstance, "LastImperialTomb.DeactivateTheAlarm", new Object[0]);
                this.b(this.fK, this.fT);
                this.b(this.fS);
                this.b(this.fQ);
                this.c(this.fM);
                this.d(this.fM);
                this.a(this.fM, this.fT);
                this.c(this.fN);
                this.d(this.fN);
                this.a(this.fN, this.fT);
                this.c(this.fO);
            }
        } else {
            Functions.npcShoutCustomMessage(npcInstance, "LastImperialTomb.Intruders", new Object[0]);
        }
    }

    private void E() {
        NpcInstance npcInstance;
        List<NpcInstance> list = this.g(this.fN);
        if (!list.isEmpty() && (npcInstance = list.get(Rnd.get(list.size()))) != null) {
            Functions.npcShoutCustomMessage(npcInstance, String.format("LastImperialTomb.UndeadBandMasterShout%d", 1 + Rnd.get(3)), new Object[0]);
        }
    }

    private void F() {
        if (this.h(this.fN) && this.fW.compareAndSet(2, 3)) {
            this.d(this.fO);
            this.e(this.fO);
            this.b(this.fN, this.fT);
            this.e(this.fN);
            this.a(this.fQ);
        }
    }

    private void d(NpcInstance npcInstance) {
        if (this.h(this.fM)) {
            this.b(this.fR);
            this.b(this.fP);
            this.a(this.fS);
            this.E();
            this.b(this.fM, this.fT);
            this.e(this.fM);
            this.E();
        }
        this.F();
    }

    private void a(NpcInstance npcInstance, Creature creature) {
        if (creature != null && creature.getPlayer() != null && this.g(this.fN).size() % 2 == 0) {
            npcInstance.dropItem(creature.getPlayer(), 8192, 1L);
        }
        this.F();
    }

    private void G() {
        if (this.fW.compareAndSet(3, 4)) {
            this.gk = ThreadPoolManager.getInstance().schedule(new Spawn(1), 300000L);
        }
    }

    private void H() {
        if (this.gn != null) {
            this.gn.cancel(false);
        }
        if (this.ge != null) {
            this.ge.broadcastPacket(new MagicSkillCanceled(this.ge));
            this.gn = ThreadPoolManager.getInstance().schedule(new Music(1), 60000L + (long)Rnd.get(10000));
        }
    }

    private void I() {
        this.fA.setActive(false);
        this.fB.setActive(false);
        this.fC.setActive(false);
        this.fD.setActive(false);
        this.fE.setActive(false);
        this.fF.setActive(false);
        this.fG.setActive(false);
        this.fH.setActive(false);
        this.fI.setActive(false);
        this.fJ.setActive(false);
    }

    private NpcInstance a(NpcLocation npcLocation) {
        return NpcUtils.spawnSingle(npcLocation.npcId, npcLocation);
    }

    private void a(NpcInstance npcInstance, int n, int n2, int n3, int n4, int n5, int n6) {
        if (npcInstance == null) {
            return;
        }
        for (Player player : this.fz.getInsidePlayers()) {
            if (player.getDistance(npcInstance) <= 2550.0) {
                player.enterMovieMode();
                player.specialCamera(npcInstance, n, n2, n3, n4, n5);
                continue;
            }
            player.leaveMovieMode();
        }
        if (n6 > 0 && n6 < 5) {
            npcInstance.broadcastPacket(new SocialAction(npcInstance.getObjectId(), n6));
        }
    }

    private void a(boolean bl) {
        this.a(this.ge, bl);
        this.a(this.gf, bl);
        this.a(this.gg, bl);
        for (int i = 0; i < 4; ++i) {
            this.a(this.gh[i], bl);
            this.a(this.gi[i], bl);
        }
    }

    private void a(NpcInstance npcInstance, boolean bl) {
        if (npcInstance == null || npcInstance.isDead()) {
            return;
        }
        if (bl) {
            npcInstance.abortAttack(true, false);
            npcInstance.abortCast(true, true);
            npcInstance.setTarget(null);
            if (npcInstance.isMoving()) {
                npcInstance.stopMove();
            }
            npcInstance.block();
        } else {
            npcInstance.unblock();
        }
        npcInstance.setIsInvul(bl);
    }

    public void preparePlayableToEnter(Playable playable) {
        long l;
        long l2 = ItemFunctions.getItemCount(playable, 8192);
        if (l2 > 0L) {
            ItemFunctions.removeItem(playable, 8192, l2, true);
        }
        if ((l = ItemFunctions.getItemCount(playable, 8556)) > 0L) {
            ItemFunctions.removeItem(playable, 8556, l, true);
        }
        if (playable.getPet() != null) {
            this.preparePlayableToEnter(playable.getPet());
        }
    }

    private void t() {
        for (Player player : this.fy.getInsidePlayers()) {
            player.teleToClosestTown();
        }
    }

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        switch (commands) {
            case admin_fri_startup: {
                FrintezzaManager.getInstance().startUp();
                break;
            }
            case admin_fri_cleanup: {
                FrintezzaManager.getInstance().cleanUp();
                break;
            }
            case admin_fri_go: {
                player.teleToLocation(fo[Rnd.get(fo.length)]);
                break;
            }
            case admin_fri_devdump: {
                System.out.println("progress: " + this.fW.get());
                System.out.println("room A devices: " + Arrays.deepToString(this.fK.toArray(new Spawner[this.fK.size()])));
                System.out.println("room A keepers: " + Arrays.deepToString(this.fL.toArray(new Spawner[this.fL.size()])));
                System.out.println("room B Players: " + Arrays.deepToString(this.fM.toArray(new Spawner[this.fM.size()])));
                System.out.println("room B masters: " + Arrays.deepToString(this.fN.toArray(new Spawner[this.fN.size()])));
                System.out.println("state: " + (Object)((Object)this._state.getState()));
                break;
            }
            case admin_fri_musdump: {
                System.out.println("areadataHeal1: " + this.fA.toString() + " | " + this.fA.isActive() + " " + this.fA.getInsidePlayers().size());
                System.out.println("areadataHeal2: " + this.fB.toString() + " | " + this.fB.isActive() + " " + this.fB.getInsidePlayers().size());
                System.out.println("areadataPower1: " + this.fC.toString() + " | " + this.fC.isActive() + " " + this.fC.getInsidePlayers().size());
                System.out.println("areadataPower2: " + this.fD.toString() + " | " + this.fD.isActive() + " " + this.fD.getInsidePlayers().size());
                System.out.println("areadataPsycho1: " + this.fE.toString() + " | " + this.fE.isActive() + " " + this.fE.getInsidePlayers().size());
                System.out.println("areadataPsycho2: " + this.fF.toString() + " | " + this.fF.isActive() + " " + this.fF.getInsidePlayers().size());
                System.out.println("areadataRampage1: " + this.fG.toString() + " | " + this.fG.isActive() + " " + this.fG.getInsidePlayers().size());
                System.out.println("areadataRampage2: " + this.fH.toString() + " | " + this.fH.isActive() + " " + this.fH.getInsidePlayers().size());
                System.out.println("areadataPlague1: " + this.fI.toString() + " | " + this.fI.isActive() + " " + this.fI.getInsidePlayers().size());
                System.out.println("areadataPlague2: " + this.fJ.toString() + " | " + this.fJ.isActive() + " " + this.fJ.getInsidePlayers().size());
            }
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public void onLoad() {
        AdminCommandHandler.getInstance().registerAdminCommandHandler(FrintezzaManager.getInstance());
        FrintezzaManager.getInstance().init();
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static enum Commands {
        admin_fri_startup,
        admin_fri_cleanup,
        admin_fri_go,
        admin_fri_devdump,
        admin_fri_musdump;

    }

    private class FrintezzaItemHandler
    extends SimpleItemHandler {
        private FrintezzaItemHandler() {
        }

        @Override
        protected boolean useItemImpl(Player player, ItemInstance itemInstance, boolean bl) {
            int n = itemInstance.getItemId();
            if (!SimpleItemHandler.useItem(player, itemInstance, 1L)) {
                return false;
            }
            switch (n) {
                case 8192: {
                    NpcInstance npcInstance;
                    if (player.getTarget() == null || !player.getTarget().isNpc() || player.getTarget() != FrintezzaManager.getInstance().ge || (npcInstance = (NpcInstance)player.getTarget()).getNpcId() != 29045) break;
                    player.callSkill(FrintezzaManager.this.fx, Collections.singletonList(FrintezzaManager.getInstance().ge), false);
                    player.broadcastPacket(new MagicSkillUse(player, FrintezzaManager.getInstance().ge, 2234, 1, 1000, 0L));
                    FrintezzaManager.getInstance().H();
                    break;
                }
                case 8556: {
                    NpcInstance npcInstance;
                    if (player.getTarget() == null || !player.getTarget().isNpc() || (npcInstance = (NpcInstance)player.getTarget()).getNpcId() != 29048 && npcInstance.getNpcId() != 29049) break;
                    npcInstance.doDie(player);
                }
            }
            return false;
        }

        @Override
        public int[] getItemIds() {
            return new int[]{8556, 8192};
        }
    }

    private class FrintessaEnterListener
    implements OnZoneEnterLeaveListener {
        private FrintessaEnterListener() {
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            FrintezzaManager.this.G();
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
            if (creature.isNpc() && (creature == FrintezzaManager.this.gf || creature == FrintezzaManager.this.gg)) {
                creature.teleToLocation(new Location(174240, -88020, -5112));
                ((NpcInstance)creature).getAggroList().clear(true);
                creature.setCurrentHpMp(creature.getMaxHp(), creature.getMaxMp());
                creature.broadcastCharInfo();
            }
        }
    }

    public class CurrentHpListener
    implements OnCurrentHpDamageListener {
        @Override
        public void onCurrentHpDamage(Creature creature, double d, Creature creature2, Skill skill) {
            if (creature.isDead() || creature != FrintezzaManager.this.gf) {
                return;
            }
            double d2 = creature.getCurrentHp() - d;
            double d3 = creature.getMaxHp();
            switch (FrintezzaManager.this.gl) {
                case 1: {
                    if (!(d2 < 0.75 * d3)) break;
                    FrintezzaManager.this.gl = 2;
                    ThreadPoolManager.getInstance().schedule(new SecondMorph(1), 1100L);
                    break;
                }
                case 2: {
                    if (!(d2 < 0.1 * d3)) break;
                    FrintezzaManager.this.gl = 3;
                    ThreadPoolManager.getInstance().schedule(new ThirdMorph(1), 2000L);
                }
            }
        }
    }

    private class TombTimeoutTask
    extends RunnableImpl {
        private int _timeLeft = Config.FRINTEZZA_TOMB_TIMEOUT;

        @Override
        public void runImpl() throws Exception {
            if (this._timeLeft > 0) {
                List<Player> list = FrintezzaManager.this.fy.getInsidePlayers();
                for (Player player : list) {
                    player.sendPacket((IStaticPacket)new ExShowScreenMessage(new CustomMessage("LastImperialTomb.Remaining", player, this._timeLeft).toString(), 10000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, false));
                }
                if (this._timeLeft > 5) {
                    this._timeLeft -= 5;
                    ThreadPoolManager.getInstance().schedule(this, 300000L);
                } else {
                    --this._timeLeft;
                    ThreadPoolManager.getInstance().schedule(this, 60000L);
                }
            } else {
                FrintezzaManager.this.cleanUp();
            }
        }
    }

    private class DeathListener
    implements OnDeathListener {
        private DeathListener() {
        }

        @Override
        public void onDeath(Creature creature, Creature creature2) {
            if (creature.isNpc()) {
                NpcInstance npcInstance = (NpcInstance)creature;
                if (npcInstance == FrintezzaManager.this.gf) {
                    npcInstance.decayMe();
                } else if (npcInstance == FrintezzaManager.this.gg) {
                    ThreadPoolManager.getInstance().schedule(new Die(1), 10L);
                } else if (FrintezzaManager.this.a(npcInstance, FrintezzaManager.this.fK)) {
                    FrintezzaManager.this.c(npcInstance);
                } else if (FrintezzaManager.this.a(npcInstance, FrintezzaManager.this.fM)) {
                    FrintezzaManager.this.d((NpcInstance)creature);
                } else if (FrintezzaManager.this.a(npcInstance, FrintezzaManager.this.fN)) {
                    FrintezzaManager.this.a(npcInstance, creature2);
                }
            }
        }
    }

    private class Die
    extends RunnableImpl {
        private int eL = 0;

        public Die(int n) {
            this.eL = n;
        }

        @Override
        public void runImpl() throws Exception {
            try {
                switch (this.eL) {
                    case 1: {
                        FrintezzaManager.this.a(true);
                        int n = Math.abs((FrintezzaManager.this.gg.getHeading() < 32768 ? 180 : 540) - (int)((double)FrintezzaManager.this.gg.getHeading() / 182.044444444));
                        FrintezzaManager.this.a(FrintezzaManager.this.gg, 300, n - 180, 5, 0, 7000, 0);
                        FrintezzaManager.this.a(FrintezzaManager.this.gg, 200, n, 85, 4000, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Die(2), 7500L);
                        break;
                    }
                    case 2: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 100, 120, 5, 0, 7000, 0);
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 100, 90, 5, 5000, 15000, 0);
                        ThreadPoolManager.getInstance().schedule(new Die(3), 6000L);
                        break;
                    }
                    case 3: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 900, 90, 25, 7000, 10000, 0);
                        FrintezzaManager.this.ge.doDie(FrintezzaManager.this.ge);
                        FrintezzaManager.this.ge = null;
                        ThreadPoolManager.getInstance().schedule(new Die(4), 7000L);
                        break;
                    }
                    case 4: {
                        FrintezzaManager.this.I();
                        for (Player player : FrintezzaManager.this.fz.getInsidePlayers()) {
                            player.leaveMovieMode();
                        }
                        SpawnManager.getInstance().spawn("[frintezza_teleporter]");
                        FrintezzaManager.this._state.setState(EpicBossState.State.DEAD);
                        FrintezzaManager.this._state.update();
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private class ThirdMorph
    extends RunnableImpl {
        private int eL = 0;
        private int gq = 0;

        public ThirdMorph(int n) {
            this.eL = n;
        }

        @Override
        public void runImpl() throws Exception {
            try {
                switch (this.eL) {
                    case 1: {
                        this.gq = Math.abs((FrintezzaManager.this.gf.getHeading() < 32768 ? 180 : 540) - (int)((double)FrintezzaManager.this.gf.getHeading() / 182.044444444));
                        for (Player player : FrintezzaManager.this.fz.getInsidePlayers()) {
                            player.enterMovieMode();
                        }
                        FrintezzaManager.this.a(true);
                        FrintezzaManager.this.ge.broadcastPacket(new MagicSkillCanceled(FrintezzaManager.this.ge));
                        FrintezzaManager.this.ge.broadcastPacket(new SocialAction(FrintezzaManager.this.ge.getObjectId(), 4));
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(2), 100L);
                        break;
                    }
                    case 2: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 250, 120, 15, 0, 1000, 0);
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 250, 120, 15, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(3), 6500L);
                        break;
                    }
                    case 3: {
                        FrintezzaManager.this.ge.broadcastPacket(new MagicSkillUse(FrintezzaManager.this.ge, FrintezzaManager.this.ge, 5006, 1, 34000, 0L));
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 500, 70, 15, 3000, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(4), 3000L);
                        break;
                    }
                    case 4: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 2500, 90, 12, 6000, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(5), 3000L);
                        break;
                    }
                    case 5: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gf, 250, this.gq, 12, 0, 1000, 0);
                        FrintezzaManager.this.a(FrintezzaManager.this.gf, 250, this.gq, 12, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(6), 500L);
                        break;
                    }
                    case 6: {
                        FrintezzaManager.this.gf.doDie(FrintezzaManager.this.gf);
                        FrintezzaManager.this.a(FrintezzaManager.this.gf, 450, this.gq, 14, 8000, 8000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(7), 6250L);
                        break;
                    }
                    case 7: {
                        NpcLocation npcLocation = new NpcLocation();
                        npcLocation.set(FrintezzaManager.this.gf.getLoc());
                        npcLocation.npcId = 29047;
                        FrintezzaManager.this.gf.deleteMe();
                        FrintezzaManager.this.gf = null;
                        FrintezzaManager.this.gg = FrintezzaManager.this.a(npcLocation);
                        FrintezzaManager.this.gg.addListener(FrintezzaManager.this.fT);
                        FrintezzaManager.this.a(FrintezzaManager.this.gg, true);
                        FrintezzaManager.this.a(FrintezzaManager.this.gg, 450, this.gq, 12, 500, 14000, 2);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(9), 5000L);
                        break;
                    }
                    case 9: {
                        FrintezzaManager.this.a(false);
                        for (Player player : FrintezzaManager.this.fz.getInsidePlayers()) {
                            player.leaveMovieMode();
                        }
                        Skill skill = SkillTable.getInstance().getInfo(5017, 1);
                        skill.getEffects(FrintezzaManager.this.gg, FrintezzaManager.this.gg, false, false);
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private class SecondMorph
    extends RunnableImpl {
        private int eL = 0;

        public SecondMorph(int n) {
            this.eL = n;
        }

        @Override
        public void runImpl() throws Exception {
            try {
                switch (this.eL) {
                    case 1: {
                        int n = Math.abs((FrintezzaManager.this.gf.getHeading() < 32768 ? 180 : 540) - (int)((double)FrintezzaManager.this.gf.getHeading() / 182.044444444));
                        for (Player player : FrintezzaManager.this.fz.getInsidePlayers()) {
                            player.enterMovieMode();
                        }
                        FrintezzaManager.this.a(true);
                        FrintezzaManager.this.a(FrintezzaManager.this.gf, 500, n, 5, 500, 15000, 0);
                        ThreadPoolManager.getInstance().schedule(new SecondMorph(2), 2000L);
                        break;
                    }
                    case 2: {
                        FrintezzaManager.this.gf.broadcastPacket(new SocialAction(FrintezzaManager.this.gf.getObjectId(), 1));
                        FrintezzaManager.this.gf.setCurrentHp(FrintezzaManager.this.gf.getMaxHp() * 3 / 4, false);
                        FrintezzaManager.this.gf.setRHandId(7903);
                        FrintezzaManager.this.gf.broadcastCharInfo();
                        ThreadPoolManager.getInstance().schedule(new SecondMorph(3), 5500L);
                        break;
                    }
                    case 3: {
                        FrintezzaManager.this.gf.broadcastPacket(new SocialAction(FrintezzaManager.this.gf.getObjectId(), 4));
                        FrintezzaManager.this.a(false);
                        Skill skill = SkillTable.getInstance().getInfo(5017, 1);
                        skill.getEffects(FrintezzaManager.this.gf, FrintezzaManager.this.gf, false, false);
                        for (Player player : FrintezzaManager.this.fz.getInsidePlayers()) {
                            player.leaveMovieMode();
                        }
                        break;
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private class Music
    extends RunnableImpl {
        private int gp;

        private Music(int n) {
            this.gp = n;
        }

        @Override
        public void runImpl() throws Exception {
            if (FrintezzaManager.this.ge == null) {
                return;
            }
            FrintezzaManager.this.I();
            if (!FrintezzaManager.this.ge.isBlocked()) {
                switch (this.gp) {
                    case 1: {
                        String string = "Requiem of Hatred";
                        FrintezzaManager.this.fA.setActive(true);
                        FrintezzaManager.this.fB.setActive(true);
                        FrintezzaManager.this.ge.broadcastPacket(new ExShowScreenMessage(string, 3000, 0, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                        FrintezzaManager.this.ge.broadcastPacket(new MagicSkillUse(FrintezzaManager.this.ge, FrintezzaManager.this.ge, 5007, 1, fY, 0L));
                        break;
                    }
                    case 2: {
                        String string = "Frenetic Toccata";
                        FrintezzaManager.this.fG.setActive(true);
                        FrintezzaManager.this.fH.setActive(true);
                        FrintezzaManager.this.ge.broadcastPacket(new ExShowScreenMessage(string, 3000, 0, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                        FrintezzaManager.this.ge.broadcastPacket(new MagicSkillUse(FrintezzaManager.this.ge, FrintezzaManager.this.ge, 5007, 2, fY, 0L));
                        break;
                    }
                    case 3: {
                        String string = "Fugue of Jubilation";
                        FrintezzaManager.this.fC.setActive(true);
                        FrintezzaManager.this.fD.setActive(true);
                        FrintezzaManager.this.ge.broadcastPacket(new ExShowScreenMessage(string, 3000, 0, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                        FrintezzaManager.this.ge.broadcastPacket(new MagicSkillUse(FrintezzaManager.this.ge, FrintezzaManager.this.ge, 5007, 3, fY, 0L));
                        break;
                    }
                    case 4: {
                        String string = "Mournful Chorale Prelude";
                        FrintezzaManager.this.fI.setActive(true);
                        FrintezzaManager.this.fJ.setActive(true);
                        FrintezzaManager.this.ge.broadcastPacket(new ExShowScreenMessage(string, 3000, 0, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                        FrintezzaManager.this.ge.broadcastPacket(new MagicSkillUse(FrintezzaManager.this.ge, FrintezzaManager.this.ge, 5007, 4, fY, 0L));
                        break;
                    }
                    case 5: {
                        String string = "Hypnotic Mazurka";
                        FrintezzaManager.this.fE.setActive(true);
                        FrintezzaManager.this.fF.setActive(true);
                        FrintezzaManager.this.ge.broadcastPacket(new ExShowScreenMessage(string, 3000, 0, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                        FrintezzaManager.this.ge.broadcastPacket(new MagicSkillUse(FrintezzaManager.this.ge, FrintezzaManager.this.ge, 5007, 5, fY, 0L));
                        break;
                    }
                    default: {
                        String string = "Rondo of Solitude";
                        if (FrintezzaManager.this.ge != null) {
                            FrintezzaManager.this.ge.broadcastPacket(new MagicSkillUse(FrintezzaManager.this.ge, FrintezzaManager.this.ge, 5006, 1, fY, 0L));
                        }
                        FrintezzaManager.this.I();
                    }
                }
            }
            FrintezzaManager.this.gn = ThreadPoolManager.getInstance().schedule(new Music(this.J()), fY + Rnd.get(10000));
        }

        private List<Creature> f(int n) {
            ArrayList<Creature> arrayList = new ArrayList<Creature>();
            if (n < 4) {
                if (FrintezzaManager.this.gf != null && !FrintezzaManager.this.gf.isDead()) {
                    arrayList.add(FrintezzaManager.this.gf);
                }
                if (FrintezzaManager.this.gg != null && !FrintezzaManager.this.gg.isDead()) {
                    arrayList.add(FrintezzaManager.this.gg);
                }
                for (int i = 0; i < 4; ++i) {
                    if (FrintezzaManager.this.gh[i] != null && !FrintezzaManager.this.gh[i].isDead() && FrintezzaManager.this.gh[i] != null) {
                        arrayList.add(FrintezzaManager.this.gh[i]);
                    }
                    if (FrintezzaManager.this.gi[i] == null || FrintezzaManager.this.gi[i].isDead()) continue;
                    arrayList.add(FrintezzaManager.this.gi[i]);
                }
            } else {
                for (Player player : FrintezzaManager.this.fz.getInsidePlayers()) {
                    if (player.isDead()) continue;
                    arrayList.add(player);
                }
            }
            return arrayList;
        }

        private int J() {
            if (this.K()) {
                return 1;
            }
            return 1 + Rnd.get(5);
        }

        private boolean K() {
            if (!Rnd.chance(40)) {
                return false;
            }
            if (FrintezzaManager.this.gf != null && !FrintezzaManager.this.gf.isAlikeDead() && FrintezzaManager.this.gf.getCurrentHp() < (double)(FrintezzaManager.this.gf.getMaxHp() * 2 / 3)) {
                return true;
            }
            if (FrintezzaManager.this.gg != null && !FrintezzaManager.this.gg.isAlikeDead() && FrintezzaManager.this.gg.getCurrentHp() < (double)(FrintezzaManager.this.gg.getMaxHp() * 2 / 3)) {
                return true;
            }
            for (int i = 0; i < 4; ++i) {
                if (FrintezzaManager.this.gh[i] != null && !FrintezzaManager.this.gh[i].isDead() && FrintezzaManager.this.gh[i].getCurrentHp() < (double)(FrintezzaManager.this.gh[i].getMaxHp() / 3)) {
                    return true;
                }
                if (FrintezzaManager.this.gi[i] == null || FrintezzaManager.this.gi[i].isDead() || !(FrintezzaManager.this.gi[i].getCurrentHp() < (double)(FrintezzaManager.this.gi[i].getMaxHp() / 3))) continue;
                return true;
            }
            return false;
        }
    }

    private class Spawn
    extends RunnableImpl {
        private int eL = 0;

        public Spawn(int n) {
            this.eL = n;
        }

        @Override
        public void runImpl() throws Exception {
            try {
                switch (this.eL) {
                    case 1: {
                        FrintezzaManager.this.gd = FrintezzaManager.this.a(new NpcLocation(174232, -89816, -5016, 16048, 29059));
                        ThreadPoolManager.getInstance().schedule(new Spawn(2), 1000L);
                        break;
                    }
                    case 2: {
                        FrintezzaManager.this._state.setRespawnDate(FrintezzaManager.w());
                        FrintezzaManager.this._state.setState(EpicBossState.State.ALIVE);
                        FrintezzaManager.this._state.update();
                        FrintezzaManager.this.fW.compareAndSet(4, 5);
                        FrintezzaManager.this.b(FrintezzaManager.this.fQ);
                        FrintezzaManager.this.ge = FrintezzaManager.this.a(fZ);
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 500, 90, 0, 6500, 8000, 0);
                        for (int i = 0; i < 4; ++i) {
                            ((FrintezzaManager)FrintezzaManager.this).gh[i] = FrintezzaManager.this.a(gb[i]);
                            FrintezzaManager.this.gh[i].startImmobilized();
                            ((FrintezzaManager)FrintezzaManager.this).gi[i] = FrintezzaManager.this.a(gc[i]);
                        }
                        FrintezzaManager.this.a(true);
                        ThreadPoolManager.getInstance().schedule(new Spawn(3), 6500L);
                        break;
                    }
                    case 3: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gd, 1800, 90, 8, 6500, 7000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(4), 900L);
                        break;
                    }
                    case 4: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gd, 140, 90, 10, 2500, 4500, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(5), 4000L);
                        break;
                    }
                    case 5: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 40, 75, -10, 0, 1000, 0);
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 40, 75, -10, 0, 12000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(6), 1350L);
                        break;
                    }
                    case 6: {
                        FrintezzaManager.this.ge.broadcastPacket(new SocialAction(FrintezzaManager.this.ge.getObjectId(), 2));
                        FrintezzaManager.this.gn = ThreadPoolManager.getInstance().schedule(new Music(0), 5000L);
                        ThreadPoolManager.getInstance().schedule(new Spawn(7), 7000L);
                        break;
                    }
                    case 7: {
                        FrintezzaManager.this.gd.deleteMe();
                        FrintezzaManager.this.gd = null;
                        ThreadPoolManager.getInstance().schedule(new Spawn(8), 1000L);
                        break;
                    }
                    case 8: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gi[0], 140, 0, 3, 22000, 3000, 1);
                        ThreadPoolManager.getInstance().schedule(new Spawn(9), 2800L);
                        break;
                    }
                    case 9: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gi[1], 140, 0, 3, 22000, 3000, 1);
                        ThreadPoolManager.getInstance().schedule(new Spawn(10), 2800L);
                        break;
                    }
                    case 10: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gi[2], 140, 180, 3, 22000, 3000, 1);
                        ThreadPoolManager.getInstance().schedule(new Spawn(11), 2800L);
                        break;
                    }
                    case 11: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gi[3], 140, 180, 3, 22000, 3000, 1);
                        ThreadPoolManager.getInstance().schedule(new Spawn(12), 3000L);
                        break;
                    }
                    case 12: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 240, 90, 0, 0, 1000, 0);
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 240, 90, 25, 5500, 10000, 3);
                        ThreadPoolManager.getInstance().schedule(new Spawn(13), 3000L);
                        break;
                    }
                    case 13: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 100, 195, 35, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(14), 700L);
                        break;
                    }
                    case 14: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 100, 195, 35, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(15), 1300L);
                        break;
                    }
                    case 15: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 120, 180, 45, 1500, 10000, 0);
                        FrintezzaManager.this.ge.broadcastPacket(new MagicSkillUse(FrintezzaManager.this.ge, FrintezzaManager.this.ge, 5006, 1, 34000, 0L));
                        ThreadPoolManager.getInstance().schedule(new Spawn(16), 1500L);
                        break;
                    }
                    case 16: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 520, 135, 45, 8000, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(17), 7500L);
                        break;
                    }
                    case 17: {
                        FrintezzaManager.this.a(FrintezzaManager.this.ge, 1500, 110, 25, 10000, 13000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(18), 9500L);
                        break;
                    }
                    case 18: {
                        FrintezzaManager.this.gf = FrintezzaManager.this.a(ga);
                        FrintezzaManager.this.a(FrintezzaManager.this.gf, true);
                        FrintezzaManager.this.gf.addListener(FrintezzaManager.this.fV);
                        FrintezzaManager.this.gf.broadcastPacket(new MagicSkillUse(FrintezzaManager.this.gf, FrintezzaManager.this.gf, 5016, 1, 3000, 0L));
                        FrintezzaManager.this.fz.broadcastPacket(new Earthquake(FrintezzaManager.this.gf.getLoc(), 50, 6), false);
                        FrintezzaManager.this.a(FrintezzaManager.this.gf, 1000, 160, 20, 6000, 6000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(19), 5500L);
                        break;
                    }
                    case 19: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gf, 800, 160, 5, 1000, 10000, 2);
                        ThreadPoolManager.getInstance().schedule(new Spawn(20), 2100L);
                        break;
                    }
                    case 20: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gf, 300, 60, 8, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(21), 2000L);
                        break;
                    }
                    case 21: {
                        FrintezzaManager.this.a(FrintezzaManager.this.gf, 1000, 90, 10, 3000, 5000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(22), 3000L);
                        break;
                    }
                    case 22: {
                        ThreadPoolManager.getInstance().schedule(new Spawn(23), 2000L);
                        for (Player player : FrintezzaManager.this.fz.getInsidePlayers()) {
                            player.leaveMovieMode();
                        }
                        break;
                    }
                    case 23: {
                        FrintezzaManager.this.a(false);
                        FrintezzaManager.this.gl = 1;
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private class IntervalEnd
    implements Runnable {
        private IntervalEnd() {
        }

        @Override
        public void run() {
            FrintezzaManager.this._state.setState(EpicBossState.State.NOTSPAWN);
            FrintezzaManager.this._state.update();
        }
    }

    public static class NpcLocation
    extends Location {
        public int npcId;

        public NpcLocation() {
        }

        public NpcLocation(int n, int n2, int n3, int n4, int n5) {
            super(n, n2, n3, n4);
            this.npcId = n5;
        }
    }
}

