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
import npc.model.QueenAntInstance;

public class QueenAntNurse
extends Priest {
    public QueenAntNurse(NpcInstance npcInstance) {
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
        Creature creature = this.i();
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

    @Override
    protected boolean createNewTask() {
        this.clearTasks();
        NpcInstance npcInstance = this.getActor();
        Creature creature = this.i();
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

    @Override
    public boolean isGlobalAI() {
        return true;
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

    private Creature i() {
        NpcInstance npcInstance = this.getActor();
        QueenAntInstance queenAntInstance = (QueenAntInstance)((MinionInstance)npcInstance).getLeader();
        if (queenAntInstance == null) {
            return null;
        }
        NpcInstance npcInstance2 = queenAntInstance.getLarva();
        if (npcInstance2 != null && npcInstance2.getCurrentHpPercents() < 5.0) {
            return npcInstance2;
        }
        return queenAntInstance;
    }

    @Override
    protected void onIntentionAttack(Creature creature) {
    }

    @Override
    protected void onEvtClanAttacked(Creature creature, Creature creature2, int n) {
        if (this.doTask()) {
            this.createNewTask();
        }
    }
}

