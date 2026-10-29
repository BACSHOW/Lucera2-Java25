/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import java.util.ArrayList;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectDiscord
extends Effect {
    public EffectDiscord(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        int n = this._effected.getLevel() - this._skill.getMagicLevel();
        int n2 = this._effected.getLevel() - this._effector.getLevel();
        if (n > 10 || n > 5 && Rnd.chance(30) || Rnd.chance(Math.abs(n2) * 2)) {
            return false;
        }
        boolean bl = this._skill.isAoE();
        if (!this._effected.isMonster()) {
            if (!bl) {
                this.getEffector().sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            }
            return false;
        }
        if (this._effected.isFearImmune() || this._effected.isRaid()) {
            if (!bl) {
                this.getEffector().sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            }
            return false;
        }
        Player player = this._effected.getPlayer();
        if (player != null) {
            SiegeEvent siegeEvent = player.getEvent(SiegeEvent.class);
            if (this._effected.isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance)this._effected)) {
                if (!bl) {
                    this.getEffector().sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                }
                return false;
            }
        }
        if (this._effected.isInZonePeace()) {
            if (!bl) {
                this.getEffector().sendPacket((IStaticPacket)Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
            }
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startConfused();
        this.onActionTime();
    }

    @Override
    public void onExit() {
        super.onExit();
        if (!this._effected.stopConfused()) {
            this._effected.abortAttack(true, true);
            this._effected.abortCast(true, true);
            this._effected.stopMove();
            this._effected.getAI().setAttackTarget(null);
            this._effected.setWalking();
            this._effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    @Override
    public boolean onActionTime() {
        ArrayList<Creature> arrayList = new ArrayList<Creature>();
        for (Creature creature : this._effected.getAroundCharacters(900, 200)) {
            if (!creature.isNpc() || creature == this.getEffected()) continue;
            arrayList.add(creature);
        }
        if (arrayList.isEmpty()) {
            return true;
        }
        Creature creature = (Creature)arrayList.get(Rnd.get(arrayList.size()));
        this._effected.setRunning();
        this._effected.getAI().Attack(creature, true, false);
        return false;
    }
}

