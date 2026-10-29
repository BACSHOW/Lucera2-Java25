/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.ai;

import l2.gameserver.Config;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.NextAction;
import l2.gameserver.ai.PlayableAI;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;

public class PlayerAI
extends PlayableAI {
    public PlayerAI(Player player) {
        super(player);
    }

    @Override
    protected void onIntentionRest() {
        this.changeIntention(CtrlIntention.AI_INTENTION_REST, null, null);
        this.setAttackTarget(null);
        this.clientStopMoving();
    }

    @Override
    protected void onIntentionActive() {
        this.clearNextAction();
        this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
    }

    @Override
    public void onIntentionInteract(GameObject gameObject) {
        Player player = this.getActor();
        if (player.getSittingTask()) {
            this.setNextAction(NextAction.INTERACT, gameObject, null, false, false);
            return;
        }
        if (player.isSitting()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
            this.clientActionFailed();
            return;
        }
        super.onIntentionInteract(gameObject);
    }

    @Override
    public void onIntentionPickUp(GameObject gameObject) {
        Player player = this.getActor();
        if (player.getSittingTask()) {
            this.setNextAction(NextAction.PICKUP, gameObject, null, false, false);
            return;
        }
        if (player.isSitting()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
            this.clientActionFailed();
            return;
        }
        super.onIntentionPickUp(gameObject);
    }

    @Override
    protected void thinkAttack(boolean bl) {
        Player player = this.getActor();
        if (player.isInFlyingTransform()) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            return;
        }
        FlagItemAttachment flagItemAttachment = player.getActiveWeaponFlagAttachment();
        if (flagItemAttachment != null && !flagItemAttachment.canAttack(player)) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            player.sendActionFailed();
            return;
        }
        if (player.isFrozen()) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }
        super.thinkAttack(bl);
    }

    @Override
    protected void thinkCast(boolean bl) {
        Player player = this.getActor();
        FlagItemAttachment flagItemAttachment = player.getActiveWeaponFlagAttachment();
        if (flagItemAttachment != null && !flagItemAttachment.canCast(player, this._skill)) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            player.sendActionFailed();
            return;
        }
        if (player.isFrozen()) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }
        super.thinkCast(bl);
    }

    @Override
    public void Attack(GameObject gameObject, boolean bl, boolean bl2) {
        Player player = this.getActor();
        if (player.isInFlyingTransform()) {
            player.sendActionFailed();
            return;
        }
        if (System.currentTimeMillis() - player.getLastAttackPacket() < (long)Config.ATTACK_PACKET_DELAY) {
            player.sendActionFailed();
            return;
        }
        player.setLastAttackPacket();
        if (player.getSittingTask()) {
            this.setNextAction(NextAction.ATTACK, gameObject, null, bl, false);
            return;
        }
        if (player.isSitting()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
            this.clientActionFailed();
            return;
        }
        super.Attack(gameObject, bl, bl2);
    }

    @Override
    public void Cast(Skill skill, Creature creature, boolean bl, boolean bl2) {
        Player player = this.getActor();
        if (!(skill.altUse() || skill.isToggle() || skill.getSkillType() == Skill.SkillType.CRAFT && Config.ALLOW_TALK_WHILE_SITTING)) {
            if (player.getSittingTask()) {
                this.setNextAction(NextAction.CAST, skill, creature, bl, bl2);
                this.clientActionFailed();
                return;
            }
            if (skill.getSkillType() == Skill.SkillType.SUMMON && player.getPrivateStoreType() != 0) {
                player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS);
                this.clientActionFailed();
                return;
            }
            if (player.isSitting()) {
                if (skill.getSkillType() == Skill.SkillType.TRANSFORMATION) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
                } else {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
                }
                this.clientActionFailed();
                return;
            }
        }
        super.Cast(skill, creature, bl, bl2);
    }

    @Override
    public Player getActor() {
        return (Player)super.getActor();
    }
}

