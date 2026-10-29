/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.GameTimeController;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.games.FishingChampionShipManager;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExFishingEnd;
import l2.gameserver.network.l2.s2c.ExFishingHpRegen;
import l2.gameserver.network.l2.s2c.ExFishingStart;
import l2.gameserver.network.l2.s2c.ExFishingStartCombat;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.FishTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;

public class Fishing {
    private final Player Ff;
    public static final int FISHING_NONE = 0;
    public static final int FISHING_STARTED = 1;
    public static final int FISHING_WAITING = 2;
    public static final int FISHING_COMBAT = 3;
    private AtomicInteger qX;
    private int _time;
    private int Fg;
    private int Fh;
    private int Fi;
    private int Fj = -1;
    private int _deceptiveMode;
    private int Fk;
    private FishTemplate Fl;
    private int Fm;
    private Future<?> Fn;
    private Location Fo = new Location();

    public Fishing(Player player) {
        this.Ff = player;
        this.qX = new AtomicInteger(0);
    }

    public void setFish(FishTemplate fishTemplate) {
        this.Fl = fishTemplate;
    }

    public void setLureId(int n) {
        this.Fm = n;
    }

    public int getLureId() {
        return this.Fm;
    }

    public void setFishLoc(Location location) {
        this.Fo.x = location.x;
        this.Fo.y = location.y;
        this.Fo.z = location.z;
    }

    public Location getFishLoc() {
        return this.Fo;
    }

    public void startFishing() {
        if (!this.qX.compareAndSet(0, 1)) {
            return;
        }
        this.Ff.setFishing(true);
        this.Ff.broadcastCharInfo();
        this.Ff.broadcastPacket(new ExFishingStart(this.Ff, this.Fl.getType(), this.Ff.getFishLoc(), Fishing.isNightLure(this.Fm)));
        this.Ff.sendPacket((IStaticPacket)Msg.STARTS_FISHING);
        this.cd();
    }

    public void stopFishing() {
        if (this.qX.getAndSet(0) == 0) {
            return;
        }
        this.cc();
        this.Ff.setFishing(false);
        this.Ff.broadcastPacket(new ExFishingEnd(this.Ff, false));
        this.Ff.broadcastCharInfo();
        this.Ff.sendPacket((IStaticPacket)Msg.CANCELS_FISHING);
    }

    public void endFishing(boolean bl) {
        if (!this.qX.compareAndSet(3, 0)) {
            return;
        }
        this.cc();
        this.Ff.setFishing(false);
        this.Ff.broadcastPacket(new ExFishingEnd(this.Ff, bl));
        this.Ff.broadcastCharInfo();
        this.Ff.sendPacket((IStaticPacket)Msg.ENDS_FISHING);
    }

    private void cc() {
        if (this.Fn != null) {
            this.Fn.cancel(false);
            this.Fn = null;
        }
    }

    private void cd() {
        if (!this.qX.compareAndSet(1, 2)) {
            return;
        }
        long l = 10000L;
        switch (this.Fl.getGroup()) {
            case 0: {
                l = Math.round((double)this.Fl.getGutsCheckTime() * 1.33);
                break;
            }
            case 1: {
                l = this.Fl.getGutsCheckTime();
                break;
            }
            case 2: {
                l = Math.round((double)this.Fl.getGutsCheckTime() * 0.66);
            }
        }
        this.Fn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new LookingForFishTask(), 10000L, l);
    }

    public boolean isInCombat() {
        return this.qX.get() == 3;
    }

    private void ce() {
        if (!this.qX.compareAndSet(2, 3)) {
            return;
        }
        this.Fg = 0;
        this.Fh = 0;
        this.Fi = 0;
        this._time = this.Fl.getCombatTime() / 1000;
        this.Fk = this.Fl.getHP();
        this.Fj = Rnd.chance(20) ? 1 : 0;
        switch (Fishing.getLureGrade(this.Fm)) {
            case 0: 
            case 1: {
                this._deceptiveMode = 0;
                break;
            }
            case 2: {
                this._deceptiveMode = Rnd.chance(10) ? 1 : 0;
            }
        }
        ExFishingStartCombat exFishingStartCombat = new ExFishingStartCombat(this.Ff, this._time, this.Fl.getHP(), this.Fj, this.Fl.getGroup(), this._deceptiveMode);
        this.Ff.broadcastPacket(exFishingStartCombat);
        this.Ff.sendPacket((IStaticPacket)Msg.SUCCEEDED_IN_GETTING_A_BITE);
        this.Fn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FishCombatTask(), 1000L, 1000L);
    }

    private void e(int n, int n2) {
        this.Fk -= n;
        if (this.Fk < 0) {
            this.Fk = 0;
        }
        this.Ff.broadcastPacket(new ExFishingHpRegen(this.Ff, this._time, this.Fk, this.Fj, this.Fh, this.Fi, n2, this._deceptiveMode));
        this.Fh = 0;
        this.Fi = 0;
        if (this.Fk > this.Fl.getHP() * 2) {
            this.Fk = this.Fl.getHP() * 2;
            this.b(false);
        } else if (this.Fk == 0) {
            this.b(true);
        }
    }

    private void b(boolean bl) {
        this.cc();
        if (bl) {
            if (!this.Ff.isInPeaceZone() && Rnd.chance(5)) {
                bl = false;
                this.Ff.sendPacket((IStaticPacket)Msg.YOU_HAVE_CAUGHT_A_MONSTER);
                Fishing.spawnPenaltyMonster(this.Ff);
            } else {
                this.Ff.sendPacket((IStaticPacket)Msg.SUCCEEDED_IN_FISHING);
                ItemFunctions.addItem((Playable)this.Ff, this.Fl.getId(), 1L, true);
                FishingChampionShipManager.getInstance().newFish(this.Ff, this.Fm);
            }
        }
        this.endFishing(bl);
    }

    public void useFishingSkill(int n, int n2, Skill.SkillType skillType) {
        if (!this.isInCombat()) {
            return;
        }
        int n3 = skillType == Skill.SkillType.REELING && !GameTimeController.getInstance().isNowNight() ? 1 : (skillType == Skill.SkillType.PUMPING && GameTimeController.getInstance().isNowNight() ? 1 : 0);
        this.Fi = n3 + 1;
        if (Rnd.chance(10)) {
            this.Ff.sendPacket((IStaticPacket)Msg.FISH_HAS_RESISTED);
            this.Fh = 0;
            this.e(0, n2);
            return;
        }
        if (this.Fj == n3) {
            if (this._deceptiveMode == 0) {
                Fishing.a(this.Ff, n, n2, skillType, 1);
                this.Fh = 1;
                this.e(n, n2);
            } else {
                Fishing.a(this.Ff, n, n2, skillType, 2);
                this.Fh = 2;
                this.e(-n, n2);
            }
        } else if (this._deceptiveMode == 0) {
            Fishing.a(this.Ff, n, n2, skillType, 2);
            this.Fh = 2;
            this.e(-n, n2);
        } else {
            Fishing.a(this.Ff, n, n2, skillType, 3);
            this.Fh = 1;
            this.e(n, n2);
        }
    }

    private static void a(Player player, int n, int n2, Skill.SkillType skillType, int n3) {
        switch (n3) {
            case 1: {
                if (skillType == Skill.SkillType.PUMPING) {
                    player.sendPacket((IStaticPacket)new SystemMessage(1465).addNumber(n));
                    if (n2 != 50) break;
                    player.sendPacket((IStaticPacket)new SystemMessage(1672).addNumber(n2));
                    break;
                }
                player.sendPacket((IStaticPacket)new SystemMessage(1467).addNumber(n));
                if (n2 != 50) break;
                player.sendPacket((IStaticPacket)new SystemMessage(1671).addNumber(n2));
                break;
            }
            case 2: {
                if (skillType == Skill.SkillType.PUMPING) {
                    player.sendPacket((IStaticPacket)new SystemMessage(1466).addNumber(n));
                    break;
                }
                player.sendPacket((IStaticPacket)new SystemMessage(1468).addNumber(n));
                break;
            }
            case 3: {
                if (skillType == Skill.SkillType.PUMPING) {
                    player.sendPacket((IStaticPacket)new SystemMessage(1465).addNumber(n));
                    if (n2 != 50) break;
                    player.sendPacket((IStaticPacket)new SystemMessage(1672).addNumber(n2));
                    break;
                }
                player.sendPacket((IStaticPacket)new SystemMessage(1467).addNumber(n));
                if (n2 != 50) break;
                player.sendPacket((IStaticPacket)new SystemMessage(1671).addNumber(n2));
                break;
            }
        }
    }

    public static void spawnPenaltyMonster(Player player) {
        int n = 18319 + Math.min(player.getLevel() / 11, 7);
        MonsterInstance monsterInstance = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(n));
        monsterInstance.setSpawnedLoc(Location.findPointToStay(player, 100, 120));
        monsterInstance.setReflection(player.getReflection());
        monsterInstance.setHeading(player.getHeading() - 32768);
        monsterInstance.spawnMe(monsterInstance.getSpawnedLoc());
        monsterInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, Rnd.get(1, 100));
    }

    public static int getRandomFishType(int n, int n2, int n3) {
        int n4;
        int n5 = Rnd.get(100);
        switch (n) {
            case 7807: {
                if (n5 <= 54) {
                    n4 = 5;
                    break;
                }
                if (n5 <= 77) {
                    n4 = 4;
                    break;
                }
                n4 = 6;
                break;
            }
            case 7808: {
                if (n5 <= 54) {
                    n4 = 4;
                    break;
                }
                if (n5 <= 77) {
                    n4 = 6;
                    break;
                }
                n4 = 5;
                break;
            }
            case 7809: {
                if (n5 <= 54) {
                    n4 = 6;
                    break;
                }
                if (n5 <= 77) {
                    n4 = 5;
                    break;
                }
                n4 = 4;
                break;
            }
            case 8486: {
                if (n5 <= 33) {
                    n4 = 4;
                    break;
                }
                if (n5 <= 66) {
                    n4 = 5;
                    break;
                }
                n4 = 6;
                break;
            }
            case 7610: 
            case 7611: 
            case 7612: 
            case 7613: 
            case 8496: 
            case 8497: 
            case 8498: 
            case 8499: 
            case 8500: 
            case 8501: 
            case 8502: 
            case 8503: 
            case 8504: {
                n4 = 3;
                break;
            }
            case 8548: {
                if (n5 <= 32) {
                    n4 = 1;
                    break;
                }
                if (n5 <= 64) {
                    n4 = 2;
                    break;
                }
                if (n5 <= 96) {
                    n4 = 0;
                    break;
                }
                if (n3 == 4 && n2 > 19) {
                    n4 = 10;
                    break;
                }
                n4 = 0;
                break;
            }
            case 6519: 
            case 6520: 
            case 6521: 
            case 8505: 
            case 8507: {
                if (n5 <= 54) {
                    n4 = 1;
                    break;
                }
                if (n5 <= 74) {
                    n4 = 0;
                    break;
                }
                if (n5 <= 94) {
                    n4 = 2;
                    break;
                }
                n4 = 3;
                break;
            }
            case 6522: 
            case 6523: 
            case 6524: 
            case 8508: 
            case 8510: {
                if (n5 <= 54) {
                    n4 = 0;
                    break;
                }
                if (n5 <= 74) {
                    n4 = 1;
                    break;
                }
                if (n5 <= 94) {
                    n4 = 2;
                    break;
                }
                n4 = 3;
                break;
            }
            case 6525: 
            case 6526: 
            case 6527: 
            case 8511: 
            case 8513: {
                if (n5 <= 55) {
                    n4 = 2;
                    break;
                }
                if (n5 <= 74) {
                    n4 = 1;
                    break;
                }
                if (n5 <= 94) {
                    n4 = 0;
                    break;
                }
                n4 = 3;
                break;
            }
            case 8484: {
                if (n5 <= 33) {
                    n4 = 0;
                    break;
                }
                if (n5 <= 66) {
                    n4 = 1;
                    break;
                }
                n4 = 2;
                break;
            }
            case 8506: {
                if (n5 <= 54) {
                    n4 = 8;
                    break;
                }
                if (n5 <= 77) {
                    n4 = 7;
                    break;
                }
                n4 = 9;
                break;
            }
            case 8509: {
                if (n5 <= 54) {
                    n4 = 7;
                    break;
                }
                if (n5 <= 77) {
                    n4 = 9;
                    break;
                }
                n4 = 8;
                break;
            }
            case 8512: {
                if (n5 <= 54) {
                    n4 = 9;
                    break;
                }
                if (n5 <= 77) {
                    n4 = 8;
                    break;
                }
                n4 = 7;
                break;
            }
            case 8485: {
                if (n5 <= 33) {
                    n4 = 7;
                    break;
                }
                if (n5 <= 66) {
                    n4 = 8;
                    break;
                }
                n4 = 9;
                break;
            }
            default: {
                n4 = 1;
            }
        }
        return n4;
    }

    public static int getRandomFishLvl(Player player) {
        int n;
        int n2 = 0;
        Effect effect = player.getEffectList().getEffectByStackType("fishPot");
        n2 = effect != null ? (int)effect.getSkill().getPower() : player.getSkillLevel(1315);
        if (n2 <= 0) {
            return 1;
        }
        int n3 = Rnd.get(100);
        if (n3 < 50) {
            n = n2;
        } else if (n3 <= 85) {
            n = n2 - 1;
            if (n <= 0) {
                n = 1;
            }
        } else {
            n = n2 + 1;
        }
        n = Math.min(27, Math.max(1, n));
        return n;
    }

    public static int getFishGroup(int n) {
        switch (n) {
            case 7807: 
            case 7808: 
            case 7809: 
            case 8486: {
                return 0;
            }
            case 8485: 
            case 8506: 
            case 8509: 
            case 8512: {
                return 2;
            }
        }
        return 1;
    }

    public static int getLureGrade(int n) {
        switch (n) {
            case 6519: 
            case 6522: 
            case 6525: 
            case 8505: 
            case 8508: 
            case 8511: {
                return 0;
            }
            case 6520: 
            case 6523: 
            case 6526: 
            case 7610: 
            case 7611: 
            case 7612: 
            case 7613: 
            case 7807: 
            case 7808: 
            case 7809: 
            case 8484: 
            case 8485: 
            case 8486: 
            case 8496: 
            case 8497: 
            case 8498: 
            case 8499: 
            case 8500: 
            case 8501: 
            case 8502: 
            case 8503: 
            case 8504: 
            case 8506: 
            case 8509: 
            case 8512: 
            case 8548: {
                return 1;
            }
            case 6521: 
            case 6524: 
            case 6527: 
            case 8507: 
            case 8510: 
            case 8513: {
                return 2;
            }
        }
        return -1;
    }

    public static boolean isNightLure(int n) {
        switch (n) {
            case 8505: 
            case 8508: 
            case 8511: {
                return true;
            }
            case 8496: 
            case 8497: 
            case 8498: 
            case 8499: 
            case 8500: 
            case 8501: 
            case 8502: 
            case 8503: 
            case 8504: {
                return true;
            }
            case 8506: 
            case 8509: 
            case 8512: {
                return true;
            }
            case 8510: 
            case 8513: {
                return true;
            }
            case 8485: {
                return true;
            }
        }
        return false;
    }

    private class FishCombatTask
    extends RunnableImpl {
        private FishCombatTask() {
        }

        @Override
        public void runImpl() throws Exception {
            if (Fishing.this.Fk >= Fishing.this.Fl.getHP() * 2) {
                Fishing.this.Ff.sendPacket((IStaticPacket)Msg.THE_FISH_GOT_AWAY);
                Fishing.this.b(false);
            } else if (Fishing.this._time <= 0) {
                Fishing.this.Ff.sendPacket((IStaticPacket)Msg.TIME_IS_UP_SO_THAT_FISH_GOT_AWAY);
                Fishing.this.b(false);
            } else {
                Fishing.this._time--;
                if (Fishing.this.Fj == 1 && Fishing.this._deceptiveMode == 0 || Fishing.this.Fj == 0 && Fishing.this._deceptiveMode == 1) {
                    Fishing.this.Fk = Fishing.this.Fk + Fishing.this.Fl.getHpRegen();
                }
                if (Fishing.this.Fg == 0) {
                    Fishing.this.Fg = 1;
                    if (Rnd.chance(30)) {
                        Fishing.this.Fj = Fishing.this.Fj == 0 ? 1 : 0;
                    }
                    if (Fishing.this.Fl.getGroup() == 2 && Rnd.chance(10)) {
                        Fishing.this._deceptiveMode = Fishing.this._deceptiveMode == 0 ? 1 : 0;
                    }
                } else {
                    Fishing.this.Fg--;
                }
                ExFishingHpRegen exFishingHpRegen = new ExFishingHpRegen(Fishing.this.Ff, Fishing.this._time, Fishing.this.Fk, Fishing.this.Fj, 0, Fishing.this.Fi, 0, Fishing.this._deceptiveMode);
                if (Fishing.this.Fi != 0) {
                    Fishing.this.Ff.broadcastPacket(exFishingHpRegen);
                } else {
                    Fishing.this.Ff.sendPacket((IStaticPacket)exFishingHpRegen);
                }
            }
        }
    }

    protected class LookingForFishTask
    extends RunnableImpl {
        private long Fp;

        protected LookingForFishTask() {
            this.Fp = System.currentTimeMillis() + (long)Fishing.this.Fl.getWaitTime() + 10000L;
        }

        @Override
        public void runImpl() throws Exception {
            if (System.currentTimeMillis() >= this.Fp) {
                Fishing.this.Ff.sendPacket((IStaticPacket)Msg.BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
                Fishing.this.cc();
                Fishing.this.endFishing(false);
                return;
            }
            if (!GameTimeController.getInstance().isNowNight() && Fishing.isNightLure(Fishing.this.Fm)) {
                Fishing.this.Ff.sendPacket((IStaticPacket)Msg.BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
                Fishing.this.cc();
                Fishing.this.endFishing(false);
                return;
            }
            int n = Rnd.get(1000);
            if (Fishing.this.Fl.getFishGuts() > n) {
                Fishing.this.cc();
                Fishing.this.ce();
            }
        }
    }
}

