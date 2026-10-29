/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.player;

import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.actor.player.BaseFarmTask;
import l2.gameserver.utils.Location;

public class AutoSummonFarmTask
extends BaseFarmTask
implements Runnable {
    private long Nx = 0L;

    public AutoSummonFarmTask(AutoFarmContext autoFarmContext) {
        super(autoFarmContext);
    }

    @Override
    public void runImpl() throws Exception {
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null) {
            return;
        }
        if (player.getPet() == null) {
            this.setCommittedSummon(null);
        }
        this.tryAttack(this.selectRandomTarget());
    }

    @Override
    protected boolean doTryUseAttackSkillSpell() {
        Skill skill;
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null) {
            return false;
        }
        Summon summon = this.getCommittedSummon();
        boolean bl = super.doTryUseAttackSkillSpell();
        if ((this.Nx == 0L || this.Nx > System.currentTimeMillis()) && summon != null && !summon.isDead() && this.getCommittedTarget() != null && this.getAutoFarmContext().isUseSummonSkills() && (skill = this.getAutoFarmContext().nextSummonAttackSkill(this.getCommittedTarget(), this.getCommittedSummon(), this.Nx)) != null) {
            this.a(skill, false);
        }
        return bl;
    }

    @Override
    protected boolean doTryUseLowLifeSkillSpell() {
        Skill skill;
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null) {
            return false;
        }
        Summon summon = this.getCommittedSummon();
        Skill skill2 = this.getAutoFarmContext().nextHealSkill(this.getCommittedTarget(), this.getCommittedSummon());
        boolean bl = false;
        if (skill2 != null) {
            this.useMagicSkill(skill2, !skill2.isOffensive());
            bl = true;
        }
        if ((this.Nx == 0L || this.Nx > System.currentTimeMillis()) && summon != null && !summon.isDead() && this.getCommittedTarget() != null && this.getAutoFarmContext().isUseSummonSkills() && (skill = this.getAutoFarmContext().nextSummonHealSkill(this.getCommittedTarget(), this.getCommittedSummon(), player)) != null) {
            this.a(skill, skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF);
        }
        return bl;
    }

    @Override
    protected boolean doTryUseSelfSkillSpell() {
        Skill skill;
        Player player = this.getAutoFarmContext().getPlayer();
        if (player == null) {
            return false;
        }
        Summon summon = this.getCommittedSummon();
        Skill skill2 = this.getAutoFarmContext().nextSelfSkill(this.getCommittedSummon());
        boolean bl = false;
        if (skill2 != null) {
            this.useMagicSkill(skill2, true);
            bl = true;
        }
        if ((this.Nx == 0L || this.Nx > System.currentTimeMillis()) && summon != null && !summon.isDead() && this.getCommittedTarget() != null && this.getAutoFarmContext().isUseSummonSkills() && (skill = this.getAutoFarmContext().nextSummonSelfSkill(this.getCommittedSummon(), player)) != null) {
            this.a(skill, skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF);
        }
        return bl;
    }

    @Override
    protected void physicalAttack() {
        Player player = this.getAutoFarmContext().getPlayer();
        Summon summon = this.getCommittedSummon();
        if (player == null) {
            return;
        }
        if (this.getCommittedTarget() != null && this.getCommittedTarget().isAutoAttackable(player) && !this.getCommittedTarget().isAlikeDead()) {
            player.setTarget(this.getCommittedTarget());
            if (GeoEngine.canSeeTarget(player, this.getCommittedTarget(), false)) {
                player.getAI().Attack(this.getCommittedTarget(), false, false);
                if (summon != null && player.getPet() != null && !summon.isDead()) {
                    summon.getAI().Attack(this.getCommittedTarget(), false, false);
                }
            } else {
                Location location = this.getCommittedTarget().getLoc();
                if (summon != null && player.getPet() != null && !summon.isDead()) {
                    summon.moveToLocation(location, 0, true);
                }
                player.moveToLocation(location, 0, true);
            }
        }
    }

    private void a(Skill skill, boolean bl) {
        Player player = this.getAutoFarmContext().getPlayer();
        Summon summon = this.getCommittedSummon();
        if (player == null || skill == null || summon == null) {
            return;
        }
        if (summon.isOutOfControl()) {
            return;
        }
        if (this.getAutoFarmContext().isExtraSummonDelaySkill()) {
            this.Nx = System.currentTimeMillis() + Config.SKILLS_EXTRA_DELAY;
        }
        this.trySummonUseMagic(skill, bl);
    }

    public void trySummonUseMagic(Skill skill, boolean bl) {
        Summon summon = this.getCommittedSummon();
        if (summon == null || skill == null) {
            return;
        }
        if (bl) {
            GameObject gameObject = summon.getTarget();
            summon.setTarget(summon);
            summon.getAI().Cast(skill, summon, false, false);
            summon.setTarget(gameObject);
            return;
        }
        if (summon.getTarget() == null) {
            return;
        }
        Creature creature = skill.getAimingTarget(summon, summon.getTarget());
        summon.getAI().Cast(skill, creature, false, false);
    }
}

