/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.ai.NextAction;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;

public class RequestMagicSkillUse
extends L2GameClientPacket {
    private static final long DEBOUNCE_MS = 250L;
    private static final long QUEUE_WINDOW_MS = 300L;
    private static final long ACTION_FAILED_COOLDOWN_MS = 500L;

    private Integer ace;
    private boolean aaX;
    private boolean aaY;

    @Override
    protected void readImpl() {
        this.ace = this.readD();
        this.aaX = this.readD() != 0;
        this.aaY = this.readC() != 0;
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        player.setActive();

        Skill skill = SkillTable.getInstance().getInfo(this.ace, player.getSkillLevel(this.ace));
        if (skill == null) {
            if (!player.getSkillFloodTracker().shouldSuppressActionFailed(this.ace, ACTION_FAILED_COOLDOWN_MS)) {
                player.getSkillFloodTracker().markActionFailed(this.ace);
                player.sendActionFailed();
            }
            return;
        }
        if (!skill.isActive() && !skill.isToggle()) {
            return;
        }

        FlagItemAttachment flagItemAttachment = player.getActiveWeaponFlagAttachment();
        if (flagItemAttachment != null && !flagItemAttachment.canCast(player, skill)) {
            if (!player.getSkillFloodTracker().shouldSuppressActionFailed(skill.getId(), ACTION_FAILED_COOLDOWN_MS)) {
                player.getSkillFloodTracker().markActionFailed(skill.getId());
                player.sendActionFailed();
            }
            return;
        }
        if ((player.getTransformation() != 0 || player.isCursedWeaponEquipped()) && !player.getAllSkills().contains(skill)) {
            return;
        }
        if (skill.isToggle() && player.getEffectList().getEffectsBySkill(skill) != null) {
            player.getEffectList().stopEffect(skill.getId());
            player.sendPacket((IStaticPacket)new SystemMessage(335).addSkillName(skill.getId(), skill.getLevel()));
            player.sendActionFailed();
            return;
        }

        if (player.isOutOfControl()) {
            if (!player.getSkillFloodTracker().shouldSuppressActionFailed(skill.getId(), ACTION_FAILED_COOLDOWN_MS)) {
                player.getSkillFloodTracker().markActionFailed(skill.getId());
                player.sendActionFailed();
            }
            return;
        }

        GameObject target = player.getTarget();
        int targetObjId = target != null ? target.getObjectId() : 0;
        Creature creature = skill.getAimingTarget(player, target);

        if (player.isCastingNow()) {
            // R1: Debounce de retransmissão idêntica durante cast ativo (drop silencioso sem ActionFailed)
            if (!player.getSkillFloodTracker().tryMarkCastPacketSeen(skill.getId(), targetObjId, DEBOUNCE_MS)) {
                return;
            }

            // R2: Skill Queuing nos últimos 300ms do cast atual
            long remainingMs = player.getAnimationEndTime() - System.currentTimeMillis();
            if (remainingMs > 0L && remainingMs <= QUEUE_WINDOW_MS) {
                player.getAI().setNextAction(NextAction.CAST, skill, creature, this.aaX, this.aaY);
                return;
            }

            // R3: Histerese de ActionFailed para flood sustentado
            if (player.getSkillFloodTracker().shouldSuppressActionFailed(skill.getId(), ACTION_FAILED_COOLDOWN_MS)) {
                return;
            }
            player.getSkillFloodTracker().markActionFailed(skill.getId());
            player.sendActionFailed();
            return;
        }

        // Primeiro pacote válido aceito
        player.getSkillFloodTracker().tryMarkCastPacketSeen(skill.getId(), targetObjId, DEBOUNCE_MS);

        player.setGroundSkillLoc(null);
        l2.gameserver.utils.MovementTelemetryLogger.logC2S(player, "RequestMagicSkillUse", String.format("Skill=%s [#%d lvl %d] Target=%s dist=%.1f range=%d isCtrl=%b isShift=%b", skill.getName(), skill.getId(), skill.getLevel(), (creature != null ? creature.getName() : "null"), (creature != null ? player.getDistance(creature) : -1.0), skill.getCastRange(), this.aaX, this.aaY));
        player.getAI().Cast(skill, creature, this.aaX, this.aaY);
    }
}

