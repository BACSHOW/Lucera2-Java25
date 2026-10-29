/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.player;

import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.actor.player.BaseFarmTask;
import l2.gameserver.utils.Location;

public class AutoHealFarmTask
extends BaseFarmTask
implements Runnable {
    public AutoHealFarmTask(AutoFarmContext autoFarmContext) {
        super(autoFarmContext);
    }

    @Override
    public void runImpl() throws Exception {
        this.tryAttack(this.selectRandomTarget());
    }

    @Override
    protected boolean doTryUseLowLifeSkillSpell() {
        Skill skill = this.getAutoFarmContext().nextHealSkill(this.getCommittedTarget(), this.getCommittedOwner());
        if (skill != null) {
            this.useMagicSkill(skill, skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF);
            return true;
        }
        return false;
    }

    @Override
    protected boolean doTryUseSelfSkillSpell() {
        Skill skill = this.getAutoFarmContext().nextSelfSkill(this.getCommittedOwner());
        if (skill != null) {
            this.useMagicSkill(skill, skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF);
            return true;
        }
        return false;
    }

    @Override
    protected void physicalAttack() {
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null) {
            return;
        }
        if (this.getCommittedOwner() != null && player.getDistance(this.getCommittedOwner()) > 200.0) {
            player.setTarget(null);
            Location location = Location.findPointToStay(this.getCommittedOwner().getLoc(), 100, player.getGeoIndex());
            if (location != null) {
                player.moveToLocation(location, 0, true);
            }
            return;
        }
        if (player.isCastingNow()) {
            return;
        }
        if (this.getAutoFarmContext().isAssistMonsterAttack() || !this.getAutoFarmContext().isLeaderAssist()) {
            super.physicalAttack();
        }
    }

    @Override
    protected boolean canAutoAssist() {
        return false;
    }
}

