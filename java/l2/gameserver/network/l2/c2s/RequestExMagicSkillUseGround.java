/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.ai.NextAction;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;

public class RequestExMagicSkillUseGround
extends L2GameClientPacket {
    private static final long DEBOUNCE_MS = 250L;
    private static final long QUEUE_WINDOW_MS = 300L;
    private static final long ACTION_FAILED_COOLDOWN_MS = 500L;

    private Location cH = new Location();
    private int _skillId;
    private boolean aaX;
    private boolean aaY;

    @Override
    protected void readImpl() {
        this.cH.x = this.readD();
        this.cH.y = this.readD();
        this.cH.z = this.readD();
        this._skillId = this.readD();
        this.aaX = this.readD() != 0;
        this.aaY = this.readC() != 0;
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }

        Skill skill = SkillTable.getInstance().getInfo(this._skillId, player.getSkillLevel(this._skillId));
        if (skill == null) {
            if (!player.getSkillFloodTracker().shouldSuppressActionFailed(this._skillId, ACTION_FAILED_COOLDOWN_MS)) {
                player.getSkillFloodTracker().markActionFailed(this._skillId);
                player.sendActionFailed();
            }
            return;
        }

        if (player.isOutOfControl()) {
            if (!player.getSkillFloodTracker().shouldSuppressActionFailed(skill.getId(), ACTION_FAILED_COOLDOWN_MS)) {
                player.getSkillFloodTracker().markActionFailed(skill.getId());
                player.sendActionFailed();
            }
            return;
        }
        if (skill.getAddedSkills().length == 0) {
            return;
        }
        if ((player.getTransformation() != 0 || player.isCursedWeaponEquipped()) && !player.getAllSkills().contains(skill)) {
            return;
        }
        if (!player.isInRange(this.cH, (long)skill.getCastRange())) {
            player.sendPacket((IStaticPacket)Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
            player.sendActionFailed();
            return;
        }

        Creature creature = skill.getAimingTarget(player, player.getTarget());

        if (player.isCastingNow()) {
            // R1: Debounce de retransmissao identica durante cast ativo
            if (!player.getSkillFloodTracker().tryMarkCastPacketSeen(skill.getId(), 0, DEBOUNCE_MS)) {
                return;
            }

            // R2: Skill Queuing nos ultimos 300ms do cast atual
            long remainingMs = player.getAnimationEndTime() - System.currentTimeMillis();
            if (remainingMs > 0L && remainingMs <= QUEUE_WINDOW_MS) {
                player.setGroundSkillLoc(this.cH);
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

        // Primeiro pacote valido aceito
        player.getSkillFloodTracker().tryMarkCastPacketSeen(skill.getId(), 0, DEBOUNCE_MS);

        if (skill.checkCondition(player, creature, this.aaX, this.aaY, true)) {
            player.setGroundSkillLoc(this.cH);
            player.getAI().Cast(skill, creature, this.aaX, this.aaY);
        } else {
            if (!player.getSkillFloodTracker().shouldSuppressActionFailed(skill.getId(), ACTION_FAILED_COOLDOWN_MS)) {
                player.getSkillFloodTracker().markActionFailed(skill.getId());
                player.sendActionFailed();
            }
        }
    }
}

