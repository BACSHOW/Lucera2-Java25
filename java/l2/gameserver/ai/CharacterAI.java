/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.ai;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.AbstractAI;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.NextAction;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.Die;
import l2.gameserver.utils.Location;

public class CharacterAI
extends AbstractAI {
    public CharacterAI(Creature creature) {
        super(creature);
    }

    @Override
    protected void onIntentionIdle() {
        this.clientStopMoving();
        this.changeIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
    }

    @Override
    protected void onIntentionActive() {
        this.clientStopMoving();
        this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
        this.onEvtThink();
    }

    @Override
    protected void onIntentionAttack(Creature creature) {
        this.setAttackTarget(creature);
        this.clientStopMoving();
        this.changeIntention(CtrlIntention.AI_INTENTION_ATTACK, creature, null);
        this.onEvtThink();
    }

    @Override
    protected void onIntentionCast(Skill skill, Creature creature) {
        this.setAttackTarget(creature);
        this.changeIntention(CtrlIntention.AI_INTENTION_CAST, skill, creature);
        this.onEvtThink();
    }

    @Override
    protected void onIntentionFollow(Creature creature) {
        this.changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, creature, null);
        this.onEvtThink();
    }

    protected static int getIndentRange(int n) {
        return n < 300 ? n / 3 * 2 : n - 100;
    }

    @Override
    protected void onIntentionInteract(GameObject gameObject) {
    }

    @Override
    protected void onIntentionPickUp(GameObject gameObject) {
    }

    @Override
    protected void onIntentionRest() {
    }

    @Override
    protected void onEvtArrivedBlocked(Location location) {
        Creature creature = this.getActor();
        if (creature.isPlayer()) {
            Location location2 = ((Player)creature).getLastServerPosition();
            if (location2 != null) {
                creature.setLoc(location2, true);
            }
            if (creature.isMoving()) {
                creature.stopMove();
            }
        }
        this.onEvtThink();
    }

    @Override
    protected void onEvtForgetObject(GameObject gameObject) {
        if (gameObject == null) {
            return;
        }
        Creature creature = this.getActor();
        if (creature.isAttackingNow() && this.getAttackTarget() == gameObject) {
            creature.abortAttack(true, true);
        }
        if (creature.isCastingNow() && this.getAttackTarget() == gameObject) {
            creature.abortCast(true, true);
        }
        if (this.getAttackTarget() == gameObject) {
            this.setAttackTarget(null);
        }
        if (creature.getTargetId() == gameObject.getObjectId()) {
            creature.setTarget(null);
        }
        if (creature.getFollowTarget() == gameObject) {
            creature.stopMove();
        }
        if (creature.getPet() != null) {
            creature.getPet().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, gameObject);
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        Creature creature2 = this.getActor();
        creature2.abortAttack(true, true);
        creature2.abortCast(true, true);
        creature2.stopMove();
        creature2.broadcastPacket(new Die(creature2));
        this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    protected void onEvtFakeDeath() {
        this.clientStopMoving();
        this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }

    @Override
    protected void onEvtClanAttacked(Creature creature, Creature creature2, int n) {
    }

    public void Attack(GameObject gameObject, boolean bl, boolean bl2) {
        this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, gameObject);
    }

    public void Cast(Skill skill, Creature creature) {
        this.Cast(skill, creature, false, false);
    }

    public void Cast(Skill skill, Creature creature, boolean bl, boolean bl2) {
        this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
    }

    @Override
    protected void onEvtThink() {
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }

    @Override
    protected void onEvtFinishCasting(Skill skill, Creature creature) {
    }

    @Override
    protected void onEvtReadyToAct() {
    }

    @Override
    protected void onEvtArrived() {
    }

    @Override
    protected void onEvtArrivedTarget() {
    }

    @Override
    protected void onEvtSeeSpell(Skill skill, Creature creature) {
    }

    @Override
    protected void onEvtSpawn() {
    }

    @Override
    public void onEvtDeSpawn() {
    }

    public void stopAITask() {
    }

    public void startAITask() {
    }

    public void setNextAction(NextAction nextAction, Object object, Object object2, boolean bl, boolean bl2) {
    }

    public void clearNextAction() {
    }

    public boolean isActive() {
        return true;
    }

    @Override
    protected void onEvtTimer(int n, Object object, Object object2) {
    }

    public void addTimer(int n, long l) {
        this.addTimer(n, null, null, l);
    }

    public void addTimer(int n, Object object, long l) {
        this.addTimer(n, object, null, l);
    }

    public void addTimer(int n, Object object, Object object2, long l) {
        ThreadPoolManager.getInstance().schedule(new Timer(n, object, object2), l);
    }

    protected class Timer
    extends RunnableImpl {
        private int tN;
        private Object tO;
        private Object tP;

        public Timer(int n, Object object, Object object2) {
            this.tN = n;
            this.tO = object;
            this.tP = object2;
        }

        @Override
        public void runImpl() {
            CharacterAI.this.notifyEvent(CtrlEvent.EVT_TIMER, this.tN, this.tO, this.tP);
        }
    }
}

