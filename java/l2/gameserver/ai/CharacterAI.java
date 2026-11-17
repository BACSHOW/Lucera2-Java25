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
            Player player = (Player)creature;
            // Para o movimento ANTES de aceitar o ponto. Com o movimento ainda ativo, o stopMove
            // recalculava a posicao pelo percurso e desfazia a aceitacao (medido: 17566 -> 17627).
            // No barco o StopMove e diferente (inStopMovePacket): la fica o caminho original.
            boolean wasMoving = creature.isMoving() && !player.isInBoat();
            if (wasMoving) {
                creature.stopMove(false, false);
            }
            // O cliente informa onde ficou bloqueado (ex.: no corpo de um monstro). Se o ponto for
            // valido, o servidor passa a usa-lo; senao mantem o comportamento original.
            boolean accepted = player.tryAcceptClientPosition(location, true);
            l2.gameserver.metrics.ServerMetrics.count(l2.gameserver.metrics.ServerMetrics.Counter.ARRIVED_BLOCKED);
            if (l2.gameserver.utils.MovementTelemetryLogger.ENABLED) {
                l2.gameserver.utils.MovementTelemetryLogger.logEvent(player, "ARRIVED_BLOCKED", String.format("ClientLoc=(%d,%d,%d) accepted=%b", location.x, location.y, location.z, accepted));
            }
            Location location2 = player.getLastServerPosition();
            // Quando o proprio servidor para o movimento (geodata bloqueou), o ponto informado e a posicao
            // atual do servidor, que ja e valida. Voltar para a ultima posicao do ValidatePosition (ate ~1 s
            // atras) puxava o personagem cerca de 160 unidades para tras sem motivo.
            boolean serverOwnStop = player.getDistance(location.x, location.y) < 1.0;
            if (!accepted && !serverOwnStop && location2 != null) {
                creature.setLoc(location2, true);
            }
            if (wasMoving) {
                // O StopMove que antes saia no stopMove(), agora com a posicao ja decidida
                // (quando aceita, o tryAcceptClientPosition ja enviou o validateLocation).
                if (!accepted) {
                    creature.validateLocation(2);
                }
                creature.broadcastPacket(new l2.gameserver.network.l2.s2c.StopMove(creature));
            } else if (creature.isMoving()) {
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

