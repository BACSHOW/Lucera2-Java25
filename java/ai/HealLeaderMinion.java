/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.ai.Priest;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.utils.Location;

public class HealLeaderMinion
extends Priest {
    public HealLeaderMinion(NpcInstance npcInstance) {
        super(npcInstance);
        this.MAX_PURSUE_RANGE = 10000;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead()) {
            return true;
        }
        if (this._def_think) {
            if (this.doTask()) {
                this.clearTasks();
            }
            return true;
        }
        Creature creature = this.h();
        if (creature == null) {
            return false;
        }
        if (npcInstance.getDistance(creature) - creature.getColRadius() - npcInstance.getColRadius() > 200.0) {
            this.b(Location.findFrontPosition(creature, npcInstance, 100, 150));
            return false;
        }
        if (!creature.isCurrentHpFull() && this.doTask()) {
            return this.createNewTask();
        }
        return false;
    }

    private void b(Location location) {
        NpcInstance npcInstance = this.getActor();
        npcInstance.setRunning();
        if (npcInstance.moveToLocation(location, 0, true)) {
            return;
        }
        this.clientStopMoving();
        this._pathfindFails = 0;
        npcInstance.broadcastPacketToOthers(new MagicSkillUse(npcInstance, npcInstance, 2036, 1, 500, 600000L));
        ThreadPoolManager.getInstance().schedule(this.new Teleport(location), 500L);
    }

    @Override
    protected boolean createNewTask() {
        this.clearTasks();
        NpcInstance npcInstance = this.getActor();
        Creature creature = this.h();
        if (npcInstance.isDead() || creature == null) {
            return false;
        }
        if (!creature.isCurrentHpFull()) {
            Skill skill = this._healSkills[Rnd.get(this._healSkills.length)];
            if ((double)skill.getAOECastRange() < npcInstance.getDistance(creature)) {
                this.b(Location.findFrontPosition(creature, npcInstance, skill.getAOECastRange() - 30, skill.getAOECastRange() - 10));
            }
            this.addTaskBuff(creature, skill);
            return true;
        }
        return false;
    }

    private Creature h() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isMinion()) {
            return ((MinionInstance)npcInstance).getLeader();
        }
        return null;
    }

    @Override
    protected void onIntentionAttack(Creature creature) {
    }
}

