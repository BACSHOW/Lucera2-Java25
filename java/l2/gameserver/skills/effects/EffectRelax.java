/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectRelax
extends Effect {
    private boolean aoM;

    public EffectRelax(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        Player player = this._effected.getPlayer();
        if (player == null) {
            return false;
        }
        if (player.isMounted()) {
            player.sendPacket((IStaticPacket)new SystemMessage(113).addSkillName(this._skill.getId(), this._skill.getLevel()));
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = this._effected.getPlayer();
        if (player.isMoving()) {
            player.stopMove();
        }
        this.aoM = player.isSitting();
        player.sitDown(null);
    }

    @Override
    public void onExit() {
        super.onExit();
        Skill skill = this.getSkill();
        if (skill != null) {
            for (Effect effect : this._effected.getEffectList().getEffectsBySkill(skill)) {
                if (effect == this) continue;
                effect.exit();
            }
        }
        if (!this.aoM) {
            this._effected.getPlayer().standUp();
        }
    }

    @Override
    public boolean onActionTime() {
        Player player = this._effected.getPlayer();
        if (player.isAlikeDead() || player == null) {
            return false;
        }
        if (!player.isSitting()) {
            return false;
        }
        if (player.isCurrentHpFull() && this.getSkill().isToggle()) {
            this.getEffected().sendPacket((IStaticPacket)Msg.HP_WAS_FULLY_RECOVERED_AND_SKILL_WAS_REMOVED);
            return false;
        }
        double d = this.calc();
        if (d > this._effected.getCurrentMp() && this.getSkill().isToggle()) {
            player.sendPacket(Msg.NOT_ENOUGH_MP, new SystemMessage(749).addSkillName(this.getSkill().getId(), this.getSkill().getDisplayLevel()));
            return false;
        }
        this._effected.reduceCurrentMp(d, null);
        return true;
    }
}

