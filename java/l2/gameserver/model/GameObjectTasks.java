/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.ai.debug.NpcMovementDebugTracer;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.StringUtils;

public class GameObjectTasks {

    public static class NotifyAITask
    extends RunnableImpl {
        private final CtrlEvent FC;
        private final Object FD;
        private final Object FE;
        private final HardReference<? extends Creature> Fz;

        public NotifyAITask(Creature creature, CtrlEvent ctrlEvent, Object object, Object object2) {
            this.Fz = creature.getRef();
            this.FC = ctrlEvent;
            this.FD = object;
            this.FE = object2;
        }

        public NotifyAITask(Creature creature, CtrlEvent ctrlEvent, Object object) {
            this(creature, ctrlEvent, object, null);
        }

        public NotifyAITask(Creature creature, CtrlEvent ctrlEvent) {
            this(creature, ctrlEvent, null, null);
        }

        @Override
        public void runImpl() {
            Creature creature = this.Fz.get();
            if (creature == null || !creature.hasAI()) {
                return;
            }
            creature.getAI().notifyEvent(this.FC, this.FD, this.FE);
        }
    }

    public static class MagicLaunchedTask
    extends RunnableImpl {
        public boolean _forceUse;
        private final HardReference<? extends Creature> Fz;

        public MagicLaunchedTask(Creature creature, boolean bl) {
            this.Fz = creature.getRef();
            this._forceUse = bl;
        }

        @Override
        public void runImpl() {
            Creature creature = this.Fz.get();
            if (creature == null) {
                return;
            }
            Skill skill = creature.getCastingSkill();
            Creature creature2 = creature.getCastingTarget();
            if (skill == null || creature2 == null) {
                creature.clearCastVars();
                return;
            }
            if (!skill.checkCondition(creature, creature2, this._forceUse, false, false)) {
                creature.abortCast(true, false);
                return;
            }
            List<Creature> list = skill.getTargets(creature, creature2, this._forceUse);
            creature.broadcastPacket(new MagicSkillLaunched(creature, skill, list));
        }
    }

    public static class MagicUseTask
    extends RunnableImpl {
        public boolean _forceUse;
        private final HardReference<? extends Creature> Fz;

        public MagicUseTask(Creature creature, boolean bl) {
            this.Fz = creature.getRef();
            this._forceUse = bl;
        }

        @Override
        public void runImpl() {
            Creature creature = this.Fz.get();
            if (creature == null) {
                return;
            }
            Skill skill = creature.getCastingSkill();
            Creature creature2 = creature.getCastingTarget();
            if (skill == null || creature2 == null) {
                creature.clearCastVars();
                return;
            }
            creature.onMagicUseTimer(creature2, skill, this._forceUse);
        }
    }

    public static class ActReadyTask
    extends RunnableImpl {
        private final HardReference<? extends Creature> Fz;

        public ActReadyTask(Creature creature) {
            this.Fz = creature.getRef();
        }

        @Override
        public void runImpl() throws Exception {
            Creature creature = this.Fz.get();
            if (creature == null) {
                return;
            }
            creature.getAI().notifyEvent(CtrlEvent.EVT_READY_TO_ACT);
        }
    }

    public static class HitTask
    extends RunnableImpl {
        boolean _crit;
        boolean _miss;
        boolean _shld;
        boolean _soulshot;
        boolean _unchargeSS;
        boolean _notify;
        int _damage;
        long _actDelay;
        private final HardReference<? extends Creature> Fz;
        private final HardReference<? extends Creature> FA;

        public HitTask(Creature creature, Creature creature2, int n, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6) {
            this.Fz = creature.getRef();
            this.FA = creature2.getRef();
            this._damage = n;
            this._crit = bl;
            this._shld = bl4;
            this._miss = bl2;
            this._soulshot = bl3;
            this._unchargeSS = bl5;
            this._notify = bl6;
            this._actDelay = 0L;
        }

        public HitTask(Creature creature, Creature creature2, int n, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, long l) {
            this.Fz = creature.getRef();
            this.FA = creature2.getRef();
            this._damage = n;
            this._crit = bl;
            this._shld = bl4;
            this._miss = bl2;
            this._soulshot = bl3;
            this._unchargeSS = bl5;
            this._notify = bl6;
            this._actDelay = l;
        }

        @Override
        public void runImpl() {
            Creature creature;
            Creature creature2 = this.Fz.get();
            if (creature2 == null || (creature = this.FA.get()) == null) {
                return;
            }
            if (creature2.isAttackAborted()) {
                return;
            }
            creature2.onHitTimer(creature, this._damage, this._crit, this._miss, this._soulshot, this._shld, this._unchargeSS);
            if (creature2.isNpc()) {
                NpcMovementDebugTracer.getInstance().traceCombat((NpcInstance) creature2, "HIT_TASK_APPLIED", creature,
                    "dmg=" + this._damage + ", crit=" + this._crit + ", miss=" + this._miss + ", actDelay=" + this._actDelay + "ms");
            }
            if (this._notify) {
                // L2OFF / BrProject: Para monstros e NPCs, liberar prontidao imediatamente apos a aplicacao do hit
                // evitando que o monstro fique 1 a 2 segundos paralisado como estatua antes de continuar a perseguicao.
                if (creature2.isNpc() || this._actDelay <= 0L) {
                    creature2.getAI().notifyEvent(CtrlEvent.EVT_READY_TO_ACT);
                } else {
                    ThreadPoolManager.getInstance().schedule(new ActReadyTask(creature2), this._actDelay);
                }
            }
        }
    }

    public static class CastEndTimeTask
    extends RunnableImpl {
        private final HardReference<? extends Creature> Fz;

        public CastEndTimeTask(Creature creature) {
            this.Fz = creature.getRef();
        }

        @Override
        public void runImpl() {
            Creature creature = this.Fz.get();
            if (creature == null) {
                return;
            }
            creature.onCastEndTime();
        }
    }

    public static class AltMagicUseTask
    extends RunnableImpl {
        public final Skill _skill;
        private final HardReference<? extends Creature> Fz;
        private final HardReference<? extends Creature> FA;

        public AltMagicUseTask(Creature creature, Creature creature2, Skill skill) {
            this.Fz = creature.getRef();
            this.FA = creature2.getRef();
            this._skill = skill;
        }

        @Override
        public void runImpl() {
            Creature creature;
            Creature creature2 = this.Fz.get();
            if (creature2 == null || (creature = this.FA.get()) == null) {
                return;
            }
            creature2.altOnMagicUseTimer(creature, this._skill);
        }
    }

    public static class EndCustomHeroTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        public EndCustomHeroTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            if (player.getVar("CustomHeroEndTime") == null || HeroController.getInstance().isCurrentHero(player)) {
                return;
            }
            player.setHero(false);
            HeroController.removeSkills(player, true);
            HeroController.checkHeroWeaponary(player);
            player.broadcastUserInfo(true);
        }
    }

    public static class EndStandUpTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        public EndStandUpTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            player.sittingTaskLaunched = false;
            player.setSitting(false);
            if (!player.getAI().setNextIntention()) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }
        }
    }

    public static class EndSitDownTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        public EndSitDownTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            player.sittingTaskLaunched = false;
            player.getAI().clearNextAction();
        }
    }

    public static class UnJailTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        public UnJailTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            String string = player.getVar("jailed");
            if (StringUtils.isBlank(string)) {
                player.teleToClosestTown();
                return;
            }
            Location location = Location.parseLoc(string.substring(string.indexOf(59) + 1));
            Reflection reflection = null;
            if (location.getH() != 0) {
                reflection = ReflectionManager.getInstance().get(location.getH());
            }
            player.standUp();
            if (reflection != null) {
                if (reflection.isCollapseStarted()) {
                    player.teleToClosestTown();
                } else {
                    player.teleToLocation(location.getX(), location.getY(), location.getZ(), reflection);
                }
            } else {
                player.teleToLocation(location.getX(), location.getY(), location.getZ(), ReflectionManager.DEFAULT);
            }
            player.unblock();
            player.unsetVar("jailed");
        }
    }

    public static class KickTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        public KickTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            player.setOfflineMode(false);
            player.kick();
        }
    }

    public static class WaterTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        public WaterTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            if (player.isDead() || !player.isInWater()) {
                player.stopWaterTask();
                return;
            }
            double d = player.getMaxHp() < 100 ? 1.0 : (double)(player.getMaxHp() / 100);
            player.reduceCurrentHp(d, player, null, false, false, true, false, false, false, false);
            player.sendPacket((IStaticPacket)new SystemMessage(297).addNumber((long)d));
        }
    }

    public static class HourlyTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        public HourlyTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            int n = player.getHoursInGame();
            player.sendPacket((IStaticPacket)new SystemMessage(764).addNumber(n));
        }
    }

    public static class PvPFlagTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        public PvPFlagTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            long l = Math.abs(System.currentTimeMillis() - player.getlastPvpAttack());
            if (l > (long)(Config.PVP_TIME + Config.PVP_BLINKING_UNFLAG_TIME)) {
                player.stopPvPFlag();
            } else if (l > (long)Config.PVP_TIME) {
                player.updatePvPFlag(2);
            } else {
                player.updatePvPFlag(1);
            }
        }
    }

    public static class SoulConsumeTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        public SoulConsumeTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            player.setConsumedSouls(player.getConsumedSouls() + 1, null);
        }
    }

    public static class DeleteTask
    extends RunnableImpl {
        private final HardReference<? extends Creature> FB;

        public DeleteTask(Creature creature) {
            this.FB = creature.getRef();
        }

        @Override
        public void runImpl() {
            Creature creature = this.FB.get();
            if (creature != null) {
                creature.deleteMe();
            }
        }
    }
}

