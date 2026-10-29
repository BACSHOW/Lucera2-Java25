/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.listener;

import java.util.function.Consumer;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.listener.actor.OnAttackHitListener;
import l2.gameserver.listener.actor.OnAttackListener;
import l2.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2.gameserver.listener.actor.OnCurrentHpHealListener;
import l2.gameserver.listener.actor.OnCurrentMpReduceListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.OnKillListener;
import l2.gameserver.listener.actor.OnMagicHitListener;
import l2.gameserver.listener.actor.OnMagicUseListener;
import l2.gameserver.listener.actor.OnMoveListener;
import l2.gameserver.listener.actor.ai.OnAiEventListener;
import l2.gameserver.listener.actor.ai.OnAiIntentionListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.utils.Location;

public class CharListenerList
extends ListenerList<Creature> {
    static final ListenerList<Creature> global = new ListenerList();
    protected final Creature actor;

    protected <ListenerT extends Listener<Creature>> void forEachListenerWithGlobal(Class<ListenerT> clazz, Consumer<ListenerT> consumer) {
        global.forEachListener(clazz, consumer);
        super.forEachListener(clazz, consumer);
    }

    public CharListenerList(Creature creature) {
        this.actor = creature;
    }

    public Creature getActor() {
        return this.actor;
    }

    public static final boolean addGlobal(Listener<Creature> listener) {
        return global.add(listener);
    }

    public static final boolean removeGlobal(Listener<Creature> listener) {
        return global.remove(listener);
    }

    public void onAiIntention(CtrlIntention ctrlIntention, Object object, Object object2) {
        this.forEachListener(OnAiIntentionListener.class, onAiIntentionListener -> onAiIntentionListener.onAiIntention(this.getActor(), ctrlIntention, object, object2));
    }

    public void onAiEvent(CtrlEvent ctrlEvent, Object[] objectArray) {
        this.forEachListener(OnAiEventListener.class, onAiEventListener -> onAiEventListener.onAiEvent(this.getActor(), ctrlEvent, objectArray));
    }

    public void onAttack(Creature creature) {
        this.forEachListenerWithGlobal(OnAttackListener.class, onAttackListener -> onAttackListener.onAttack(this.getActor(), creature));
    }

    public void onAttackHit(Creature creature) {
        this.forEachListenerWithGlobal(OnAttackHitListener.class, onAttackHitListener -> onAttackHitListener.onAttackHit(this.getActor(), creature));
    }

    public void onMagicUse(Skill skill, Creature creature, boolean bl) {
        this.forEachListenerWithGlobal(OnMagicUseListener.class, onMagicUseListener -> onMagicUseListener.onMagicUse(this.getActor(), skill, creature, bl));
    }

    public void onMagicHit(Skill skill, Creature creature) {
        this.forEachListenerWithGlobal(OnMagicHitListener.class, onMagicHitListener -> onMagicHitListener.onMagicHit(this.getActor(), skill, creature));
    }

    public void onDeath(Creature creature) {
        this.forEachListenerWithGlobal(OnDeathListener.class, onDeathListener -> onDeathListener.onDeath(this.getActor(), creature));
    }

    public void onKill(Creature creature) {
        this.forEachListenerWithGlobal(OnKillListener.class, onKillListener -> {
            if (!onKillListener.ignorePetOrSummon()) {
                onKillListener.onKill(this.getActor(), creature);
            }
        });
    }

    public void onKillIgnorePetOrSummon(Creature creature) {
        this.forEachListenerWithGlobal(OnKillListener.class, onKillListener -> {
            if (onKillListener.ignorePetOrSummon()) {
                onKillListener.onKill(this.getActor(), creature);
            }
        });
    }

    public void onCurrentHpDamage(double d, Creature creature, Skill skill) {
        this.forEachListenerWithGlobal(OnCurrentHpDamageListener.class, onCurrentHpDamageListener -> onCurrentHpDamageListener.onCurrentHpDamage(this.getActor(), d, creature, skill));
    }

    public void onCurrentHpHeal(double d, Creature creature, Skill skill) {
        this.forEachListenerWithGlobal(OnCurrentHpHealListener.class, onCurrentHpHealListener -> onCurrentHpHealListener.onCurrentHpHeal(this.getActor(), d, creature, skill));
    }

    public void onCurrentMpReduce(double d, Creature creature) {
        this.forEachListenerWithGlobal(OnCurrentMpReduceListener.class, onCurrentMpReduceListener -> onCurrentMpReduceListener.onCurrentMpReduce(this.getActor(), d, creature));
    }

    public void onMove(Location location) {
        this.forEachListenerWithGlobal(OnMoveListener.class, onMoveListener -> onMoveListener.onMove(this.getActor(), location));
    }
}

