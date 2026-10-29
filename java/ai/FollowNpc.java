/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;

public class FollowNpc
extends DefaultAI {
    private boolean bZ = false;
    private ScheduledFuture<?> _followTask;

    public FollowNpc(NpcInstance npcInstance) {
        super(npcInstance);
        this.AI_TASK_DELAY_CURRENT = 1000L;
        this.AI_TASK_ACTIVE_DELAY = 1000L;
    }

    @Override
    protected boolean randomWalk() {
        return this.getActor() instanceof MonsterInstance;
    }

    @Override
    protected void onEvtThink() {
        NpcInstance npcInstance = this.getActor();
        if (this.bZ || npcInstance.isActionsDisabled() || npcInstance.isAfraid() || npcInstance.isDead() || npcInstance.isMovementDisabled()) {
            return;
        }
        this.bZ = true;
        try {
            if (!(Config.BLOCK_ACTIVE_TASKS || this.getIntention() != CtrlIntention.AI_INTENTION_ACTIVE && this.getIntention() != CtrlIntention.AI_INTENTION_IDLE)) {
                this.thinkActive();
            } else if (this.getIntention() == CtrlIntention.AI_INTENTION_FOLLOW) {
                this.thinkFollow();
            }
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
        finally {
            this.bZ = false;
        }
    }

    protected void thinkFollow() {
        NpcInstance npcInstance = this.getActor();
        Creature creature = npcInstance.getFollowTarget();
        if (creature == null) {
            creature = npcInstance.getPlayer();
        }
        if (creature == null || creature.isAlikeDead() || npcInstance.getDistance(creature) > 4000.0 || npcInstance.isMovementDisabled()) {
            this.clientActionFailed();
            return;
        }
        if (npcInstance.isFollowing() && npcInstance.getFollowTarget() == creature) {
            this.clientActionFailed();
            return;
        }
        if (npcInstance.isInRange(creature, 100L)) {
            this.clientActionFailed();
        }
        if (this._followTask != null) {
            this._followTask.cancel(false);
            this._followTask = null;
        }
        this._followTask = ThreadPoolManager.getInstance().schedule(new ThinkFollow(), 500L);
    }

    protected class ThinkFollow
    extends RunnableImpl {
        protected ThinkFollow() {
        }

        public NpcInstance getActor() {
            return FollowNpc.this.getActor();
        }

        @Override
        public void runImpl() {
            NpcInstance npcInstance = this.getActor();
            if (npcInstance == null) {
                return;
            }
            Creature creature = npcInstance.getFollowTarget();
            if (creature == null) {
                creature = npcInstance.getPlayer();
            }
            if (creature == null || creature.isAlikeDead() || npcInstance.getDistance(creature) > 4000.0) {
                FollowNpc.this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                return;
            }
            if (!(npcInstance.isInRange(creature, 150L) || npcInstance.isFollowing() && npcInstance.getFollowTarget() == creature)) {
                npcInstance.moveToRelative(creature, 100, 150, false);
            }
            FollowNpc.this._followTask = ThreadPoolManager.getInstance().schedule(this, 500L);
        }
    }
}

