/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.player;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFarmTask
implements Runnable {
    private static final Logger _log = LoggerFactory.getLogger(BaseFarmTask.class);
    protected static final int RUN_AWAY_STATIC_DISTANCE = 500;
    protected static final int RUN_AWAY_RANDOM_DISTANCE = 100;
    private final AutoFarmContext Nw;
    private NpcInstance Ny = null;
    private HardReference<Player> Nz = HardReferences.emptyRef();
    private HardReference<Summon> NA = HardReferences.emptyRef();
    private long NB = 0L;
    private Pair<ScheduledFuture<?>, Location> NC;

    public BaseFarmTask(AutoFarmContext autoFarmContext) {
        this.Nw = autoFarmContext;
    }

    protected AutoFarmContext getAutoFarmContext() {
        return this.Nw;
    }

    public Player getCommittedOwner() {
        return this.Nz.get();
    }

    public void setCommittedOwner(Player player) {
        this.Nz = player != null ? player.getRef() : HardReferences.emptyRef();
    }

    public long getExtraDelay() {
        return this.NB;
    }

    public void setExtraDelay(long l) {
        this.NB = l;
    }

    public Pair<ScheduledFuture<?>, Location> getMoveToPair() {
        return this.NC;
    }

    public void setMoveToPair(Pair<ScheduledFuture<?>, Location> pair) {
        this.NC = pair;
    }

    protected boolean canAutoAssist() {
        return true;
    }

    private boolean cD() {
        Pair<ScheduledFuture<?>, Location> pair;
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null) {
            return false;
        }
        if (this.getAutoFarmContext().isKeepLocation() && this.getAutoFarmContext().getKeepLocation() != null && (pair = this.moveToAndThan(player, this.getAutoFarmContext().getKeepLocation(), this)) != null) {
            if (this.getMoveToPair() != null && this.getMoveToPair().getLeft() != null) {
                this.getMoveToPair().getLeft().cancel(false);
            }
            this.setMoveToPair(pair);
            if (this.getCommittedSummon() != null) {
                this.getCommittedSummon().moveToLocation(pair.getRight(), 0, true);
            }
            return true;
        }
        return false;
    }

    protected boolean selectRandomTarget() {
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null || player.isCastingNow()) {
            return false;
        }
        NpcInstance npcInstance2 = this.getCommittedTarget();
        if (npcInstance2 != null && (this.canAutoAssist() || this.getAutoFarmContext().isAssistMonsterAttack() || !this.getAutoFarmContext().isLeaderAssist())) {
            if (this.spoilCheck()) {
                return false;
            }
            if (!npcInstance2.isDead() && GeoEngine.canSeeTarget(player, npcInstance2, false)) {
                if (player.getTarget() != npcInstance2) {
                    player.setTarget(npcInstance2);
                    player.sendPacket((IStaticPacket)new MyTargetSelected(npcInstance2.getObjectId(), player.getLevel() - npcInstance2.getLevel()));
                    player.sendPacket((IStaticPacket)npcInstance2.makeStatusUpdate(9, 10));
                }
                return true;
            }
            this.setCommittedTarget(null);
            player.setTarget(null);
            if (this.cD()) {
                return false;
            }
        }
        if (this.getAutoFarmContext().isLeaderAssist()) {
            if (player.getParty() == null) {
                this.setCommittedOwner(null);
                this.getAutoFarmContext().setLeaderAssist(false, false);
            } else {
                this.setCommittedOwner(player.getParty().getPartyLeader());
            }
        }
        if (this.getCommittedSummon() == null) {
            this.setCommittedSummon(player.getPet() != null ? player.getPet() : null);
        }
        if (this.getCommittedOwner() != null && !this.getCommittedOwner().isDead() && this.getAutoFarmContext().isAssistMonsterAttack()) {
            Pair<ScheduledFuture<?>, Location> pair;
            npcInstance2 = this.getAutoFarmContext().getLeaderTarget(this.getCommittedOwner());
            this.setCommittedTarget(npcInstance2);
            if (npcInstance2 != null) {
                return true;
            }
            if (npcInstance2 != null && player.getDistance(npcInstance2) < (double)Config.RUN_CLOSE_UP_DISTANCE && (pair = this.runAwayFromTargetAndThan(npcInstance2, player, 500, 100, this)) != null) {
                if (this.getMoveToPair() != null && this.getMoveToPair().getLeft() != null) {
                    this.getMoveToPair().getLeft().cancel(false);
                }
                this.setMoveToPair(pair);
                if (this.getCommittedSummon() != null) {
                    this.getCommittedSummon().moveToLocation(pair.getRight(), 0, true);
                }
                return false;
            }
        } else {
            if (this.getAutoFarmContext().isLeaderAssist()) {
                return true;
            }
            List<NpcInstance> list = this.getAutoFarmContext().getAroundNpc(player, npcInstance -> GeoEngine.canSeeTarget(player, npcInstance, false) && !npcInstance.isDead());
            if (list.isEmpty() && this.cD()) {
                return false;
            }
            Optional<NpcInstance> optional = list.stream().min(Comparator.comparingDouble(player::getDistance));
            if (optional.isPresent()) {
                player.setTarget(this.setCommittedTarget(optional.get()));
                player.sendPacket((IStaticPacket)new MyTargetSelected(optional.get().getObjectId(), player.getLevel() - optional.get().getLevel()));
                player.sendPacket((IStaticPacket)optional.get().makeStatusUpdate(9, 10));
                return true;
            }
        }
        return false;
    }

    protected boolean spoilCheck() {
        NpcInstance npcInstance = this.getCommittedTarget();
        return npcInstance != null && npcInstance.isDead() && npcInstance instanceof MonsterInstance && ((MonsterInstance)npcInstance).isSpoiled() && this.cF();
    }

    private boolean cE() {
        NpcInstance npcInstance = this.getCommittedTarget();
        return npcInstance != null && npcInstance.isDead() && npcInstance instanceof MonsterInstance && ((MonsterInstance)npcInstance).isSweepActive();
    }

    protected void tryAttack(boolean bl) {
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null) {
            return;
        }
        if (bl && this.getCommittedTarget() != null) {
            this.physicalAttack();
        }
        this.tryUseSpell(bl);
        if (bl && this.getCommittedTarget() != null && this.getAutoFarmContext().isUseSummonSkills()) {
            this.tryUseSummonSpell();
        }
        if (bl && this.getCommittedTarget() != null) {
            this.physicalAttack();
        }
    }

    protected void physicalAttack() {
        Player player = this.getAutoFarmContext().getPlayer();
        if (player != null && this.getCommittedTarget() != null && this.getCommittedTarget().isAutoAttackable(player) && !this.getCommittedTarget().isAlikeDead()) {
            if (player.getTarget() != this.getCommittedTarget()) {
                player.setTarget(this.getCommittedTarget());
                player.sendPacket((IStaticPacket)new MyTargetSelected(this.getCommittedTarget().getObjectId(), player.getLevel() - this.getCommittedTarget().getLevel()));
                player.sendPacket((IStaticPacket)this.getCommittedTarget().makeStatusUpdate(9, 10));
            }
            if (GeoEngine.canSeeTarget(player, this.getCommittedTarget(), false)) {
                player.getAI().Attack(this.getCommittedTarget(), false, false);
            } else if (!this.getCommittedTarget().isInRangeZ(player, 200L) && player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this.getCommittedTarget(), null);
            }
        }
    }

    protected boolean doTryUseLowLifeSkillSpell() {
        Skill skill = this.getAutoFarmContext().nextHealSkill(this.getCommittedTarget(), null);
        if (skill != null) {
            this.useMagicSkill(skill, !skill.isOffensive());
            return true;
        }
        return false;
    }

    protected boolean doTryUseSelfSkillSpell() {
        Skill skill = this.getAutoFarmContext().nextSelfSkill(null);
        if (skill != null) {
            this.useMagicSkill(skill, true);
            return true;
        }
        return false;
    }

    protected boolean doTryUseChanceSkillSpell() {
        Skill skill = this.getAutoFarmContext().nextChanceSkill(this.getCommittedTarget(), this.getExtraDelay());
        if (skill != null) {
            this.useMagicSkill(skill, false);
            return true;
        }
        return false;
    }

    protected boolean doTryUseAttackSkillSpell() {
        Skill skill = this.getAutoFarmContext().nextAttackSkill(this.getCommittedTarget(), this.getExtraDelay());
        if (skill != null) {
            this.useMagicSkill(skill, false);
            return true;
        }
        return false;
    }

    protected void tryUseSpell(boolean bl) {
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null || player.isCastingNow()) {
            return;
        }
        if (bl) {
            this.doTryUseChanceSkillSpell();
        }
        if (this.doTryUseLowLifeSkillSpell()) {
            return;
        }
        if (this.doTryUseSelfSkillSpell()) {
            return;
        }
        if (bl) {
            this.doTryUseAttackSkillSpell();
        }
    }

    protected void tryUseSummonSpell() {
    }

    protected final Pair<ScheduledFuture<?>, Location> moveToAndThan(Creature creature, Location location, Runnable runnable) {
        if (location != null && !creature.isOutOfControl()) {
            if (creature.isMoving()) {
                creature.stopMove();
            }
            double d = creature.getDistance(location.getX(), location.getY(), location.getZ());
            long l = (long)(d / (double)(creature.isRunning() ? creature.getRunSpeed() : creature.getWalkSpeed()) * 1000.0);
            if (creature.moveToLocation(location, 0, true)) {
                return Pair.of(ThreadPoolManager.getInstance().schedule(runnable, Math.max(1500L, 333L + l + Config.RUN_CLOSE_UP_DELAY)), location);
            }
        }
        return null;
    }

    protected final Pair<ScheduledFuture<?>, Location> runAwayFromTargetAndThan(GameObject gameObject, Creature creature, int n, int n2, Runnable runnable) {
        double d = Math.toRadians(PositionUtils.calculateAngleFrom(gameObject, creature));
        int n3 = creature.getX();
        int n4 = creature.getY();
        int n5 = n3 + (int)((double)n * Math.cos(d));
        int n6 = n4 + (int)((double)n * Math.sin(d));
        Location location = Location.findPointToStay(new Location(n5, n6, creature.getZ()), n2, creature.getGeoIndex());
        for (int i = 0; i < 10 && !GeoEngine.canSeeCoord(gameObject, location.getX(), location.getY(), (int)((double)location.getZ() + creature.getColHeight() + 64.0), false); ++i) {
            location = Location.findPointToStay(new Location(n5, n6, creature.getZ()), n2, creature.getGeoIndex());
        }
        if (creature.isMoving() && creature.getFinalDestination() != null && creature.getFinalDestination().distance(n5, n6) <= (double)(n2 * 2)) {
            return null;
        }
        Pair<ScheduledFuture<?>, Location> pair = this.moveToAndThan(creature, location, runnable);
        if (pair != null) {
            return pair;
        }
        return null;
    }

    protected boolean preDoUseMagicSkill(Skill skill, boolean bl) {
        return true;
    }

    protected final void useMagicSkill(Skill skill, boolean bl) {
        Player player = this.getAutoFarmContext().getPlayer();
        if (skill == null || player == null || player.isOutOfControl() || skill.isToggle() && player.isMounted()) {
            return;
        }
        if (this.preDoUseMagicSkill(skill, bl)) {
            if (this.getAutoFarmContext().isExtraDelaySkill()) {
                this.setExtraDelay(System.currentTimeMillis() + Config.SKILLS_EXTRA_DELAY);
            }
            this.b(skill, bl);
        }
    }

    private void b(Skill skill, boolean bl) {
        Player player = this.getAutoFarmContext().getPlayer();
        if (skill == null || player == null || player.isOutOfControl()) {
            return;
        }
        if (bl) {
            GameObject gameObject = player.getTarget();
            player.setTarget(player);
            player.setGroundSkillLoc(null);
            player.getAI().Cast(skill, player, false, false);
            player.setTarget(gameObject);
            return;
        }
        if (player.getTarget() == null) {
            return;
        }
        Creature creature = skill.getAimingTarget(player, player.getTarget());
        player.setGroundSkillLoc(null);
        player.getAI().Cast(skill, creature, false, false);
    }

    protected NpcInstance getCommittedTarget() {
        return this.Ny;
    }

    protected NpcInstance setCommittedTarget(NpcInstance npcInstance) {
        this.Ny = npcInstance;
        return this.Ny;
    }

    public Summon getCommittedSummon() {
        return this.NA.get();
    }

    public void setCommittedSummon(Summon summon) {
        this.NA = summon != null ? (HardReference<Summon>)(HardReference<?>)summon.getRef() : HardReferences.<Summon>emptyRef();
    }

    private boolean cF() {
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null) {
            return false;
        }
        Skill skill = player.getKnownSkill(42);
        Skill skill2 = player.getKnownSkill(444);
        if (skill == null && skill2 == null) {
            return false;
        }
        if (this.cE()) {
            this.useMagicSkill(skill2 != null ? skill2 : skill, false);
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        try {
            Player player = this.getAutoFarmContext().getPlayer();
            if (player == null) {
                return;
            }
            for (String string : Config.AUTO_FARM_LIMIT_ZONE_NAMES) {
                if (!player.isInZone(string)) continue;
                this.getAutoFarmContext().stopFarmTask(false);
                player.sendMessage(new CustomMessage("AUTO_HUNTING_PROHIBITED", player, new Object[0]));
                return;
            }
            this.runImpl();
        }
        catch (Throwable throwable) {
            _log.error("Exception: RunnableImpl.run(): " + throwable, throwable);
        }
    }

    public abstract void runImpl() throws Exception;
}

