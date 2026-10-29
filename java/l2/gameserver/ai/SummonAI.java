/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.ai;

import java.util.concurrent.ScheduledFuture;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.PlayableAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;

public class SummonAI
extends PlayableAI {
    private HardReference<Playable> uh = HardReferences.emptyRef();

    public SummonAI(Summon summon) {
        super(summon);
    }

    @Override
    protected void thinkActive() {
        Summon summon = this.getActor();
        this.clearNextAction();
        if (summon.isDepressed()) {
            this.setAttackTarget(summon.getPlayer());
            this.changeIntention(CtrlIntention.AI_INTENTION_ATTACK, summon.getPlayer(), null);
            this.thinkAttack(true);
        } else if (summon.isFollowMode()) {
            this.changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, summon.getPlayer(), null);
            this.thinkFollow();
        }
        super.thinkActive();
    }

    @Override
    protected void thinkAttack(boolean bl) {
        Summon summon = this.getActor();
        if (summon.isDepressed()) {
            this.setAttackTarget(summon.getPlayer());
        }
        super.thinkAttack(bl);
    }

    private void bj() {
        Summon summon = this.getActor();
        if (!summon.isDead() && !summon.isDepressed()) {
            Player player = summon.getPlayer();
            Playable playable = this.uh.get();
            if (player != null && playable != null && !player.isDead() && !player.isOutOfControl()) {
                if (playable.isInCombat() && summon.getDistance(playable) < (double)summon.getActingRange()) {
                    int n = SummonAI.getIndentRange(summon.getActingRange());
                    Location location = player.getLoc();
                    Location location2 = playable.getLoc();
                    double d = PositionUtils.convertHeadingToRadian((32768 + PositionUtils.getHeadingTo(location, location2)) % 65535);
                    Location location3 = new Location((int)(0.5 + (double)location.getX() + (double)n * Math.sin(d)), (int)(0.5 + (double)location.getY() + (double)n * Math.cos(d)), location.getZ()).correctGeoZ();
                    summon.moveToLocation(location3, 0, true);
                    return;
                }
                this.uh = HardReferences.emptyRef();
            } else {
                this.uh = HardReferences.emptyRef();
            }
        }
    }

    @Override
    protected void onEvtArrived() {
        if (!this.setNextIntention()) {
            if (this.getIntention() == CtrlIntention.AI_INTENTION_INTERACT || this.getIntention() == CtrlIntention.AI_INTENTION_PICK_UP || this.getIntention() == CtrlIntention.AI_INTENTION_FOLLOW) {
                this.onEvtThink();
            } else {
                this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
            }
        }
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        Summon summon = this.getActor();
        if (creature != null && summon.getPlayer().isDead() && !summon.isDepressed()) {
            this.Attack(creature, false, false);
        }
        if (creature != null && creature.isPlayable()) {
            this.uh = (HardReference<Playable>)(HardReference<?>)creature.getRef();
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    public Summon getActor() {
        return (Summon)super.getActor();
    }

    @Override
    protected ScheduledFuture<?> scheduleThinkFollowTask() {
        return ThreadPoolManager.getInstance().schedule(new ThinkFollowForSummon(this.getActor()), Config.MOVE_TASK_QUANTUM_NPC);
    }

    protected static class ThinkFollowForSummon
    extends RunnableImpl {
        private final HardReference<? extends Summon> ue;

        public ThinkFollowForSummon(Summon summon) {
            this.ue = summon.getRef();
        }

        @Override
        public void runImpl() throws Exception {
            Summon summon = this.ue.get();
            if (summon == null) {
                return;
            }
            SummonAI summonAI = summon.getAI();
            if (summonAI.getIntention() != CtrlIntention.AI_INTENTION_FOLLOW) {
                return;
            }
            Creature creature = (Creature)summonAI._intention_arg0;
            if (creature == null || creature.isAlikeDead()) {
                summonAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                return;
            }
            int n = summon.getPlayer() != null && summon.getPlayer().getNetConnection() != null ? summon.getPlayer().getNetConnection().getPawnClippingRange() : GameClient.DEFAULT_PAWN_CLIPPING_RANGE;
            int n2 = (int)(summon.getColRadius() + creature.getColRadius());
            boolean bl = PlayableAI.isThinkImplyZ(summon, creature);
            int n3 = (int)(!bl ? summon.getDistance(creature) : summon.getDistance3D(creature)) - n2;
            int n4 = Math.min(n, creature.getActingRange());
            int n5 = summon.getActingRange();
            if (n3 > n || n3 > 2 << World.SHIFT_BY) {
                summonAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                summonAI.clientStopMoving();
                return;
            }
            Player player = summon.getPlayer();
            if (player == null || player.isLogoutStarted() || (summon.isPet() || summon.isSummon()) && player.getPet() != summon) {
                summonAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                summonAI.clientStopMoving();
                return;
            }
            if (n3 > n5) {
                if (!summon.isFollowing() || summon.getFollowTarget() != creature) {
                    summon.moveToRelative(creature, CharacterAI.getIndentRange(n4), n5);
                }
            } else {
                summonAI.bj();
            }
            summonAI._followTask = summonAI.scheduleThinkFollowTask();
        }
    }
}

