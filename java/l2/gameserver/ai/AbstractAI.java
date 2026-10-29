/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.ai;

import java.util.ArrayList;
import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAI
extends RunnableImpl {
    protected static final Logger _log = LoggerFactory.getLogger(AbstractAI.class);
    protected final Creature _actor;
    private HardReference<? extends Creature> tL = HardReferences.emptyRef();
    private CtrlIntention tM = CtrlIntention.AI_INTENTION_IDLE;
    protected List<Player> _targetList = new ArrayList<Player>();

    protected AbstractAI(Creature creature) {
        this._actor = creature;
    }

    @Override
    public void runImpl() throws Exception {
    }

    public void changeIntention(CtrlIntention ctrlIntention, Object object, Object object2) {
        this.tM = ctrlIntention;
        if (ctrlIntention != CtrlIntention.AI_INTENTION_CAST && ctrlIntention != CtrlIntention.AI_INTENTION_ATTACK) {
            this.setAttackTarget(null);
        }
    }

    public final void setIntention(CtrlIntention ctrlIntention) {
        this.setIntention(ctrlIntention, null, null);
    }

    public final void setIntention(CtrlIntention ctrlIntention, Object object) {
        this.setIntention(ctrlIntention, object, null);
    }

    public void setIntention(CtrlIntention ctrlIntention, Object object, Object object2) {
        Creature creature;
        if (ctrlIntention != CtrlIntention.AI_INTENTION_CAST && ctrlIntention != CtrlIntention.AI_INTENTION_ATTACK) {
            this.setAttackTarget(null);
        }
        if (!(creature = this.getActor()).isVisible()) {
            if (this.tM == CtrlIntention.AI_INTENTION_IDLE) {
                return;
            }
            ctrlIntention = CtrlIntention.AI_INTENTION_IDLE;
        }
        creature.getListeners().onAiIntention(ctrlIntention, object, object2);
        switch (ctrlIntention) {
            case AI_INTENTION_IDLE: {
                this.onIntentionIdle();
                break;
            }
            case AI_INTENTION_ACTIVE: {
                this.onIntentionActive();
                break;
            }
            case AI_INTENTION_REST: {
                this.onIntentionRest();
                break;
            }
            case AI_INTENTION_ATTACK: {
                this.onIntentionAttack((Creature)object);
                break;
            }
            case AI_INTENTION_CAST: {
                this.onIntentionCast((Skill)object, (Creature)object2);
                break;
            }
            case AI_INTENTION_PICK_UP: {
                this.onIntentionPickUp((GameObject)object);
                break;
            }
            case AI_INTENTION_INTERACT: {
                this.onIntentionInteract((GameObject)object);
                break;
            }
            case AI_INTENTION_FOLLOW: {
                this.onIntentionFollow((Creature)object);
            }
        }
    }

    public final void notifyEvent(CtrlEvent ctrlEvent) {
        this.notifyEvent(ctrlEvent, new Object[0]);
    }

    public final void notifyEvent(CtrlEvent ctrlEvent, Object object) {
        this.notifyEvent(ctrlEvent, new Object[]{object});
    }

    public final void notifyEvent(CtrlEvent ctrlEvent, Object object, Object object2) {
        this.notifyEvent(ctrlEvent, new Object[]{object, object2});
    }

    public final void notifyEvent(CtrlEvent ctrlEvent, Object object, Object object2, Object object3) {
        this.notifyEvent(ctrlEvent, new Object[]{object, object2, object3});
    }

    public void notifyEvent(CtrlEvent ctrlEvent, Object[] objectArray) {
        Creature creature = this.getActor();
        if (creature == null || !creature.isVisible()) {
            return;
        }
        creature.getListeners().onAiEvent(ctrlEvent, objectArray);
        switch (ctrlEvent) {
            case EVT_THINK: {
                this.onEvtThink();
                break;
            }
            case EVT_ATTACKED: {
                this.onEvtAttacked((Creature)objectArray[0], ((Number)objectArray[1]).intValue());
                break;
            }
            case EVT_CLAN_ATTACKED: {
                this.onEvtClanAttacked((Creature)objectArray[0], (Creature)objectArray[1], ((Number)objectArray[2]).intValue());
                break;
            }
            case EVT_AGGRESSION: {
                this.onEvtAggression((Creature)objectArray[0], ((Number)objectArray[1]).intValue());
                break;
            }
            case EVT_READY_TO_ACT: {
                this.onEvtReadyToAct();
                break;
            }
            case EVT_ARRIVED: {
                this.onEvtArrived();
                break;
            }
            case EVT_ARRIVED_TARGET: {
                this.onEvtArrivedTarget();
                break;
            }
            case EVT_ARRIVED_BLOCKED: {
                this.onEvtArrivedBlocked((Location)objectArray[0]);
                break;
            }
            case EVT_FORGET_OBJECT: {
                this.onEvtForgetObject((GameObject)objectArray[0]);
                break;
            }
            case EVT_DEAD: {
                this.onEvtDead((Creature)objectArray[0]);
                break;
            }
            case EVT_FAKE_DEATH: {
                this.onEvtFakeDeath();
                break;
            }
            case EVT_FINISH_CASTING: {
                this.onEvtFinishCasting((Skill)objectArray[0], (Creature)objectArray[1]);
                break;
            }
            case EVT_SEE_SPELL: {
                this.onEvtSeeSpell((Skill)objectArray[0], (Creature)objectArray[1]);
                break;
            }
            case EVT_SPAWN: {
                this.onEvtSpawn();
                break;
            }
            case EVT_DESPAWN: {
                this.onEvtDeSpawn();
                break;
            }
            case EVT_TIMER: {
                this.onEvtTimer(((Number)objectArray[0]).intValue(), objectArray[1], objectArray[2]);
            }
        }
    }

    protected void clientActionFailed() {
        Creature creature = this.getActor();
        if (creature != null && creature.isPlayer()) {
            creature.sendActionFailed();
        }
    }

    public void clientStopMoving(boolean bl) {
        Creature creature = this.getActor();
        creature.stopMove(bl);
    }

    public void clientStopMoving() {
        Creature creature = this.getActor();
        creature.stopMove();
    }

    public Creature getActor() {
        return this._actor;
    }

    public CtrlIntention getIntention() {
        return this.tM;
    }

    public void setAttackTarget(Creature creature) {
        this.tL = creature == null ? HardReferences.emptyRef() : creature.getRef();
    }

    public Creature getAttackTarget() {
        return this.tL.get();
    }

    public boolean isGlobalAI() {
        return false;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " for " + this.getActor();
    }

    public void addToTargetList(Player player) {
        if (this._targetList != null && !this._targetList.contains(player)) {
            this._targetList.add(player);
        }
    }

    public List<Player> getTargetList() {
        return this._targetList;
    }

    protected abstract void onIntentionIdle();

    protected abstract void onIntentionActive();

    protected abstract void onIntentionRest();

    protected abstract void onIntentionAttack(Creature var1);

    protected abstract void onIntentionCast(Skill var1, Creature var2);

    protected abstract void onIntentionPickUp(GameObject var1);

    protected abstract void onIntentionInteract(GameObject var1);

    protected abstract void onEvtThink();

    protected abstract void onEvtAttacked(Creature var1, int var2);

    protected abstract void onEvtClanAttacked(Creature var1, Creature var2, int var3);

    protected abstract void onEvtAggression(Creature var1, int var2);

    protected abstract void onEvtReadyToAct();

    protected abstract void onEvtArrived();

    protected abstract void onEvtArrivedTarget();

    protected abstract void onEvtArrivedBlocked(Location var1);

    protected abstract void onEvtForgetObject(GameObject var1);

    protected abstract void onEvtDead(Creature var1);

    protected abstract void onEvtFakeDeath();

    protected abstract void onEvtFinishCasting(Skill var1, Creature var2);

    protected abstract void onEvtSeeSpell(Skill var1, Creature var2);

    protected abstract void onEvtSpawn();

    public abstract void onEvtDeSpawn();

    protected abstract void onIntentionFollow(Creature var1);

    protected abstract void onEvtTimer(int var1, Object var2, Object var3);
}

