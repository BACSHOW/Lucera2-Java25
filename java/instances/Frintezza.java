/*
 * Decompiled with CFR 0.152.
 */
package instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.Earthquake;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.MagicSkillCanceled;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;

public class Frintezza
extends Reflection {
    private static final int lU = 18328;
    private static final int lV = 18339;
    private static final int lW = 29046;
    private static final int lX = 29047;
    private static final int lY = 29061;
    private static final int lZ = 7903;
    private static final int ma = 8556;
    private static final int[] mb = new int[]{25150051, 25150052, 25150053, 25150054, 25150055, 25150056, 25150057, 25150058};
    private static final int[] mc = new int[]{25150042, 25150043};
    private static final int[] md = new int[]{25150061, 25150062, 25150063, 25150064, 25150065, 25150066, 25150067, 25150068, 25150069, 25150070};
    private static final int[] me = new int[]{25150045, 25150046};
    private static final int[] mf = new int[]{18329, 18330, 18331, 18333};
    private static final int[] mg = new int[]{18334, 18335, 18336, 18337, 18338};
    private static int fY = 30000;
    private static NpcLocation mh = new NpcLocation(174240, -89805, -5022, 16048, 29045);
    private static NpcLocation mi = new NpcLocation(174234, -88015, -5116, 48028, 29046);
    private static NpcLocation[] mj = new NpcLocation[]{new NpcLocation(175880, -88696, -5104, 35048, 29048), new NpcLocation(175816, -87160, -5104, 28205, 29049), new NpcLocation(172648, -87176, -5104, 64817, 29048), new NpcLocation(172600, -88664, -5104, 57730, 29049)};
    private static NpcLocation[] mk = new NpcLocation[]{new NpcLocation(175880, -88696, -5104, 35048, 29050), new NpcLocation(175816, -87160, -5104, 28205, 29051), new NpcLocation(172648, -87176, -5104, 64817, 29051), new NpcLocation(172600, -88664, -5104, 57730, 29050)};
    private NpcInstance gd;
    private NpcInstance ge;
    private NpcInstance gf;
    private NpcInstance gg;
    private NpcInstance[] gh = new NpcInstance[4];
    private NpcInstance[] gi = new NpcInstance[4];
    private int gl = 0;
    private static final long gm = 300000L;
    private DeathListener ml = new DeathListener();
    private CurrentHpListener mm = new CurrentHpListener();
    private ZoneListener mn = new ZoneListener();
    private ScheduledFuture<?> gn;

    @Override
    protected void onCreate() {
        super.onCreate();
        this.getZone("[Frintezza]").addListener(this.mn);
        for (NpcInstance npcInstance : this.getNpcs()) {
            npcInstance.addListener(this.ml);
        }
        this.a(true, mf);
    }

    private NpcInstance a(NpcLocation npcLocation) {
        return this.addSpawnWithoutRespawn(npcLocation.npcId, npcLocation, 0);
    }

    private void a(NpcInstance npcInstance, int n, int n2, int n3, int n4, int n5, int n6) {
        if (npcInstance == null) {
            return;
        }
        for (Player player : this.getPlayers()) {
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

    private void cleanUp() {
        this.startCollapseTimer(900000L);
        for (Player creature : this.getPlayers()) {
            creature.sendPacket((IStaticPacket)new SystemMessage(2106).addNumber(15));
        }
        for (NpcInstance npcInstance : this.getNpcs()) {
            npcInstance.deleteMe();
        }
    }

    private void a(boolean bl, int[] nArray) {
        for (NpcInstance npcInstance : this.getNpcs()) {
            if (!ArrayUtils.contains(nArray, npcInstance.getNpcId())) continue;
            if (bl) {
                npcInstance.block();
                npcInstance.setIsInvul(true);
                continue;
            }
            npcInstance.unblock();
            npcInstance.setIsInvul(false);
        }
    }

    @Override
    protected void onCollapse() {
        super.onCollapse();
        if (this.gn != null) {
            this.gn.cancel(true);
        }
    }

    public class ZoneListener
    implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
            if (creature.isNpc() && (creature.getNpcId() == 29046 || creature.getNpcId() == 29047)) {
                creature.teleToLocation(new Location(174240, -88020, -5112));
                ((NpcInstance)creature).getAggroList().clear(true);
                creature.setCurrentHpMp(creature.getMaxHp(), creature.getMaxMp());
                creature.broadcastCharInfo();
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
                if (creature.getNpcId() == 18328) {
                    for (int i = 0; i < mb.length; ++i) {
                        Frintezza.this.openDoor(mb[i]);
                    }
                    Frintezza.this.a(false, mf);
                    for (NpcInstance npcInstance : Frintezza.this.getNpcs()) {
                        if (!ArrayUtils.contains(mf, npcInstance.getNpcId())) continue;
                        npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, Frintezza.this.getPlayers().get(Rnd.get(Frintezza.this.getPlayers().size())), 200);
                    }
                } else if (ArrayUtils.contains(mf, creature.getNpcId())) {
                    for (NpcInstance npcInstance : Frintezza.this.getNpcs()) {
                        if (!ArrayUtils.contains(mf, npcInstance.getNpcId()) || npcInstance.isDead()) continue;
                        return;
                    }
                    for (int i = 0; i < mc.length; ++i) {
                        Frintezza.this.openDoor(mc[i]);
                    }
                    Frintezza.this.a(true, mg);
                } else if (creature.getNpcId() == 18339) {
                    for (NpcInstance npcInstance : Frintezza.this.getNpcs()) {
                        if (npcInstance.getNpcId() != 18339 || npcInstance.isDead()) continue;
                        return;
                    }
                    for (int i = 0; i < md.length; ++i) {
                        Frintezza.this.openDoor(md[i]);
                    }
                    Frintezza.this.a(false, mg);
                } else if (ArrayUtils.contains(mg, creature.getNpcId())) {
                    if (Rnd.chance(10)) {
                        ((NpcInstance)creature).dropItem(creature2.getPlayer(), 8556, 1L);
                    }
                    for (NpcInstance npcInstance : Frintezza.this.getNpcs()) {
                        if (!ArrayUtils.contains(mg, npcInstance.getNpcId()) && !ArrayUtils.contains(mf, npcInstance.getNpcId()) || npcInstance.isDead()) continue;
                        return;
                    }
                    for (int i = 0; i < me.length; ++i) {
                        Frintezza.this.openDoor(me[i]);
                    }
                    ThreadPoolManager.getInstance().schedule(new FrintezzaStart(), 300000L);
                } else {
                    if (creature.getNpcId() == 29046) {
                        creature.decayMe();
                        return;
                    }
                    if (creature.getNpcId() == 29047) {
                        ThreadPoolManager.getInstance().schedule(new Die(1), 10L);
                        Frintezza.this.setReenterTime(System.currentTimeMillis());
                    }
                }
            }
        }
    }

    public class CurrentHpListener
    implements OnCurrentHpDamageListener {
        @Override
        public void onCurrentHpDamage(Creature creature, double d, Creature creature2, Skill skill) {
            if (creature.isDead() || creature != Frintezza.this.gf) {
                return;
            }
            double d2 = creature.getCurrentHp() - d;
            double d3 = creature.getMaxHp();
            switch (Frintezza.this.gl) {
                case 1: {
                    if (!(d2 < 0.75 * d3)) break;
                    Frintezza.this.gl = 2;
                    ThreadPoolManager.getInstance().schedule(new SecondMorph(1), 1100L);
                    break;
                }
                case 2: {
                    if (!(d2 < 0.1 * d3)) break;
                    Frintezza.this.gl = 3;
                    ThreadPoolManager.getInstance().schedule(new ThirdMorph(1), 2000L);
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
                        Frintezza.this.a(true);
                        int n = Math.abs((Frintezza.this.gg.getHeading() < 32768 ? 180 : 540) - (int)((double)Frintezza.this.gg.getHeading() / 182.044444444));
                        Frintezza.this.a(Frintezza.this.gg, 300, n - 180, 5, 0, 7000, 0);
                        Frintezza.this.a(Frintezza.this.gg, 200, n, 85, 4000, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Die(2), 7500L);
                        break;
                    }
                    case 2: {
                        Frintezza.this.a(Frintezza.this.ge, 100, 120, 5, 0, 7000, 0);
                        Frintezza.this.a(Frintezza.this.ge, 100, 90, 5, 5000, 15000, 0);
                        ThreadPoolManager.getInstance().schedule(new Die(3), 6000L);
                        break;
                    }
                    case 3: {
                        Frintezza.this.a(Frintezza.this.ge, 900, 90, 25, 7000, 10000, 0);
                        Frintezza.this.ge.doDie(Frintezza.this.ge);
                        Frintezza.this.ge = null;
                        ThreadPoolManager.getInstance().schedule(new Die(4), 7000L);
                        break;
                    }
                    case 4: {
                        for (Player player : Frintezza.this.getPlayers()) {
                            player.leaveMovieMode();
                        }
                        Frintezza.this.cleanUp();
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
                        this.gq = Math.abs((Frintezza.this.gf.getHeading() < 32768 ? 180 : 540) - (int)((double)Frintezza.this.gf.getHeading() / 182.044444444));
                        for (Player player : Frintezza.this.getPlayers()) {
                            player.enterMovieMode();
                        }
                        Frintezza.this.a(true);
                        Frintezza.this.ge.broadcastPacket(new MagicSkillCanceled(Frintezza.this.ge));
                        Frintezza.this.ge.broadcastPacket(new SocialAction(Frintezza.this.ge.getObjectId(), 4));
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(2), 100L);
                        break;
                    }
                    case 2: {
                        Frintezza.this.a(Frintezza.this.ge, 250, 120, 15, 0, 1000, 0);
                        Frintezza.this.a(Frintezza.this.ge, 250, 120, 15, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(3), 6500L);
                        break;
                    }
                    case 3: {
                        Frintezza.this.ge.broadcastPacket(new MagicSkillUse(Frintezza.this.ge, Frintezza.this.ge, 5006, 1, 34000, 0L));
                        Frintezza.this.a(Frintezza.this.ge, 500, 70, 15, 3000, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(4), 3000L);
                        break;
                    }
                    case 4: {
                        Frintezza.this.a(Frintezza.this.ge, 2500, 90, 12, 6000, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(5), 3000L);
                        break;
                    }
                    case 5: {
                        Frintezza.this.a(Frintezza.this.gf, 250, this.gq, 12, 0, 1000, 0);
                        Frintezza.this.a(Frintezza.this.gf, 250, this.gq, 12, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(6), 500L);
                        break;
                    }
                    case 6: {
                        Frintezza.this.gf.doDie(Frintezza.this.gf);
                        Frintezza.this.a(Frintezza.this.gf, 450, this.gq, 14, 8000, 8000, 0);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(7), 6250L);
                        break;
                    }
                    case 7: {
                        NpcLocation npcLocation = new NpcLocation();
                        npcLocation.set(Frintezza.this.gf.getLoc());
                        npcLocation.npcId = 29047;
                        Frintezza.this.gf.deleteMe();
                        Frintezza.this.gf = null;
                        Frintezza.this.gg = Frintezza.this.a(npcLocation);
                        Frintezza.this.gg.addListener(Frintezza.this.ml);
                        Frintezza.this.a(Frintezza.this.gg, true);
                        Frintezza.this.a(Frintezza.this.gg, 450, this.gq, 12, 500, 14000, 2);
                        ThreadPoolManager.getInstance().schedule(new ThirdMorph(9), 5000L);
                        break;
                    }
                    case 9: {
                        Frintezza.this.a(false);
                        for (Player player : Frintezza.this.getPlayers()) {
                            player.leaveMovieMode();
                        }
                        Skill skill = SkillTable.getInstance().getInfo(5017, 1);
                        skill.getEffects(Frintezza.this.gg, Frintezza.this.gg, false, false);
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
                        int n = Math.abs((Frintezza.this.gf.getHeading() < 32768 ? 180 : 540) - (int)((double)Frintezza.this.gf.getHeading() / 182.044444444));
                        for (Player player : Frintezza.this.getPlayers()) {
                            player.enterMovieMode();
                        }
                        Frintezza.this.a(true);
                        Frintezza.this.a(Frintezza.this.gf, 500, n, 5, 500, 15000, 0);
                        ThreadPoolManager.getInstance().schedule(new SecondMorph(2), 2000L);
                        break;
                    }
                    case 2: {
                        Frintezza.this.gf.broadcastPacket(new SocialAction(Frintezza.this.gf.getObjectId(), 1));
                        Frintezza.this.gf.setCurrentHp(Frintezza.this.gf.getMaxHp() * 3 / 4, false);
                        Frintezza.this.gf.setRHandId(7903);
                        Frintezza.this.gf.broadcastCharInfo();
                        ThreadPoolManager.getInstance().schedule(new SecondMorph(3), 5500L);
                        break;
                    }
                    case 3: {
                        Frintezza.this.gf.broadcastPacket(new SocialAction(Frintezza.this.gf.getObjectId(), 4));
                        Frintezza.this.a(false);
                        Skill skill = SkillTable.getInstance().getInfo(5017, 1);
                        skill.getEffects(Frintezza.this.gf, Frintezza.this.gf, false, false);
                        for (Player player : Frintezza.this.getPlayers()) {
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

    private class SongEffectLaunched
    extends RunnableImpl {
        private final List<Creature> mo;
        private final int mp;
        private final int mq;

        public SongEffectLaunched(List<Creature> list, int n, int n2) {
            this.mo = list;
            this.mp = n;
            this.mq = n2;
        }

        @Override
        public void runImpl() throws Exception {
            if (Frintezza.this.ge == null) {
                return;
            }
            if (this.mq > fY) {
                return;
            }
            SongEffectLaunched songEffectLaunched = new SongEffectLaunched(this.mo, this.mp, this.mq + fY / 10);
            ThreadPoolManager.getInstance().schedule(songEffectLaunched, fY / 10);
            Frintezza.this.ge.callSkill(SkillTable.getInstance().getInfo(5008, this.mp), this.mo, false);
        }
    }

    private class Music
    extends RunnableImpl {
        private Music() {
        }

        @Override
        public void runImpl() throws Exception {
            String string;
            if (Frintezza.this.ge == null) {
                return;
            }
            int n = Math.max(1, Math.min(4, this.J()));
            switch (n) {
                case 1: {
                    string = "Requiem of Hatred";
                    break;
                }
                case 2: {
                    string = "Frenetic Toccata";
                    break;
                }
                case 3: {
                    string = "Fugue of Jubilation";
                    break;
                }
                case 4: {
                    string = "Mournful Chorale Prelude";
                    break;
                }
                default: {
                    return;
                }
            }
            if (!Frintezza.this.ge.isBlocked()) {
                Frintezza.this.ge.broadcastPacket(new ExShowScreenMessage(string, 3000, 0, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                Frintezza.this.ge.broadcastPacket(new MagicSkillUse(Frintezza.this.ge, Frintezza.this.ge, 5007, n, fY, 0L));
                ThreadPoolManager.getInstance().schedule(new SongEffectLaunched(this.f(n), n, 10000), 10000L);
            }
            Frintezza.this.gn = ThreadPoolManager.getInstance().schedule(new Music(), fY + Rnd.get(10000));
        }

        private List<Creature> f(int n) {
            ArrayList<Creature> arrayList = new ArrayList<Creature>();
            if (n < 4) {
                if (Frintezza.this.gf != null && !Frintezza.this.gf.isDead()) {
                    arrayList.add(Frintezza.this.gf);
                }
                if (Frintezza.this.gg != null && !Frintezza.this.gg.isDead()) {
                    arrayList.add(Frintezza.this.gg);
                }
                for (int i = 0; i < 4; ++i) {
                    if (Frintezza.this.gh[i] != null && !Frintezza.this.gh[i].isDead()) {
                        arrayList.add(Frintezza.this.gh[i]);
                    }
                    if (Frintezza.this.gi[i] == null || Frintezza.this.gi[i].isDead()) continue;
                    arrayList.add(Frintezza.this.gi[i]);
                }
            } else {
                for (Player player : Frintezza.this.getPlayers()) {
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
            return Rnd.get(2, 4);
        }

        private boolean K() {
            if (!Rnd.chance(40)) {
                return false;
            }
            if (Frintezza.this.gf != null && !Frintezza.this.gf.isAlikeDead() && Frintezza.this.gf.getCurrentHp() < (double)(Frintezza.this.gf.getMaxHp() * 2 / 3)) {
                return true;
            }
            if (Frintezza.this.gg != null && !Frintezza.this.gg.isAlikeDead() && Frintezza.this.gg.getCurrentHp() < (double)(Frintezza.this.gg.getMaxHp() * 2 / 3)) {
                return true;
            }
            for (int i = 0; i < 4; ++i) {
                if (Frintezza.this.gh[i] != null && !Frintezza.this.gh[i].isDead() && Frintezza.this.gh[i].getCurrentHp() < (double)(Frintezza.this.gh[i].getMaxHp() / 3)) {
                    return true;
                }
                if (Frintezza.this.gi[i] == null || Frintezza.this.gi[i].isDead() || !(Frintezza.this.gi[i].getCurrentHp() < (double)(Frintezza.this.gi[i].getMaxHp() / 3))) continue;
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
                        Frintezza.this.gd = Frintezza.this.a(new NpcLocation(174232, -89816, -5016, 16048, 29059));
                        ThreadPoolManager.getInstance().schedule(new Spawn(2), 1000L);
                        break;
                    }
                    case 2: {
                        Frintezza.this.closeDoor(me[1]);
                        Frintezza.this.ge = Frintezza.this.a(mh);
                        Frintezza.this.a(Frintezza.this.ge, 500, 90, 0, 6500, 8000, 0);
                        for (int i = 0; i < 4; ++i) {
                            ((Frintezza)Frintezza.this).gh[i] = Frintezza.this.a(mj[i]);
                            Frintezza.this.gh[i].startImmobilized();
                            ((Frintezza)Frintezza.this).gi[i] = Frintezza.this.a(mk[i]);
                        }
                        Frintezza.this.a(true);
                        ThreadPoolManager.getInstance().schedule(new Spawn(3), 6500L);
                        break;
                    }
                    case 3: {
                        Frintezza.this.a(Frintezza.this.gd, 1800, 90, 8, 6500, 7000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(4), 900L);
                        break;
                    }
                    case 4: {
                        Frintezza.this.a(Frintezza.this.gd, 140, 90, 10, 2500, 4500, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(5), 4000L);
                        break;
                    }
                    case 5: {
                        Frintezza.this.a(Frintezza.this.ge, 40, 75, -10, 0, 1000, 0);
                        Frintezza.this.a(Frintezza.this.ge, 40, 75, -10, 0, 12000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(6), 1350L);
                        break;
                    }
                    case 6: {
                        Frintezza.this.ge.broadcastPacket(new SocialAction(Frintezza.this.ge.getObjectId(), 2));
                        ThreadPoolManager.getInstance().schedule(new Spawn(7), 7000L);
                        break;
                    }
                    case 7: {
                        Frintezza.this.gd.deleteMe();
                        Frintezza.this.gd = null;
                        ThreadPoolManager.getInstance().schedule(new Spawn(8), 1000L);
                        break;
                    }
                    case 8: {
                        Frintezza.this.a(Frintezza.this.gi[0], 140, 0, 3, 22000, 3000, 1);
                        ThreadPoolManager.getInstance().schedule(new Spawn(9), 2800L);
                        break;
                    }
                    case 9: {
                        Frintezza.this.a(Frintezza.this.gi[1], 140, 0, 3, 22000, 3000, 1);
                        ThreadPoolManager.getInstance().schedule(new Spawn(10), 2800L);
                        break;
                    }
                    case 10: {
                        Frintezza.this.a(Frintezza.this.gi[2], 140, 180, 3, 22000, 3000, 1);
                        ThreadPoolManager.getInstance().schedule(new Spawn(11), 2800L);
                        break;
                    }
                    case 11: {
                        Frintezza.this.a(Frintezza.this.gi[3], 140, 180, 3, 22000, 3000, 1);
                        ThreadPoolManager.getInstance().schedule(new Spawn(12), 3000L);
                        break;
                    }
                    case 12: {
                        Frintezza.this.a(Frintezza.this.ge, 240, 90, 0, 0, 1000, 0);
                        Frintezza.this.a(Frintezza.this.ge, 240, 90, 25, 5500, 10000, 3);
                        ThreadPoolManager.getInstance().schedule(new Spawn(13), 3000L);
                        break;
                    }
                    case 13: {
                        Frintezza.this.a(Frintezza.this.ge, 100, 195, 35, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(14), 700L);
                        break;
                    }
                    case 14: {
                        Frintezza.this.a(Frintezza.this.ge, 100, 195, 35, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(15), 1300L);
                        break;
                    }
                    case 15: {
                        Frintezza.this.a(Frintezza.this.ge, 120, 180, 45, 1500, 10000, 0);
                        Frintezza.this.ge.broadcastPacket(new MagicSkillUse(Frintezza.this.ge, Frintezza.this.ge, 5006, 1, 34000, 0L));
                        ThreadPoolManager.getInstance().schedule(new Spawn(16), 1500L);
                        break;
                    }
                    case 16: {
                        Frintezza.this.a(Frintezza.this.ge, 520, 135, 45, 8000, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(17), 7500L);
                        break;
                    }
                    case 17: {
                        Frintezza.this.a(Frintezza.this.ge, 1500, 110, 25, 10000, 13000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(18), 9500L);
                        break;
                    }
                    case 18: {
                        Frintezza.this.gf = Frintezza.this.a(mi);
                        Frintezza.this.a(Frintezza.this.gf, true);
                        Frintezza.this.gf.addListener(Frintezza.this.mm);
                        Frintezza.this.gf.broadcastPacket(new MagicSkillUse(Frintezza.this.gf, Frintezza.this.gf, 5016, 1, 3000, 0L));
                        Earthquake earthquake = new Earthquake(Frintezza.this.gf.getLoc(), 50, 6);
                        for (Player player : Frintezza.this.getPlayers()) {
                            player.broadcastPacket(earthquake);
                        }
                        Frintezza.this.a(Frintezza.this.gf, 1000, 160, 20, 6000, 6000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(19), 5500L);
                        break;
                    }
                    case 19: {
                        Frintezza.this.a(Frintezza.this.gf, 800, 160, 5, 1000, 10000, 2);
                        ThreadPoolManager.getInstance().schedule(new Spawn(20), 2100L);
                        break;
                    }
                    case 20: {
                        Frintezza.this.a(Frintezza.this.gf, 300, 60, 8, 0, 10000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(21), 2000L);
                        break;
                    }
                    case 21: {
                        Frintezza.this.a(Frintezza.this.gf, 1000, 90, 10, 3000, 5000, 0);
                        ThreadPoolManager.getInstance().schedule(new Spawn(22), 3000L);
                        break;
                    }
                    case 22: {
                        for (Player player : Frintezza.this.getPlayers()) {
                            player.leaveMovieMode();
                        }
                        ThreadPoolManager.getInstance().schedule(new Spawn(23), 2000L);
                        break;
                    }
                    case 23: {
                        Frintezza.this.a(false);
                        Frintezza.this.a(new NpcLocation(174056, -76024, -5104, 0, 29061));
                        Frintezza.this.gl = 1;
                        Frintezza.this.gn = ThreadPoolManager.getInstance().schedule(new Music(), 5000L);
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private class FrintezzaStart
    extends RunnableImpl {
        private FrintezzaStart() {
        }

        @Override
        public void runImpl() throws Exception {
            ThreadPoolManager.getInstance().schedule(new Spawn(1), 1000L);
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

