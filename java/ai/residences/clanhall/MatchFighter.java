/*
 * Decompiled with CFR 0.152.
 */
package ai.residences.clanhall;

import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.Fighter;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2.gameserver.utils.Location;

public abstract class MatchFighter
extends Fighter {
    public MatchFighter(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        CTBBossInstance cTBBossInstance = this.getActor();
        if (cTBBossInstance.isActionsDisabled()) {
            return true;
        }
        if (this._def_think) {
            if (this.doTask()) {
                this.clearTasks();
            }
            return true;
        }
        long l = System.currentTimeMillis();
        if (l - this._checkAggroTimestamp > (long)Config.AGGRO_CHECK_INTERVAL) {
            this._checkAggroTimestamp = l;
            List<Creature> list = World.getAroundCharacters(cTBBossInstance);
            CollectionUtils.eqSort(list, this._nearestTargetComparator);
            for (Creature creature : list) {
                if (!this.checkAggression(creature)) continue;
                cTBBossInstance.getAggroList().addDamageHate(creature, 0, 2);
                if (creature.isSummon()) {
                    cTBBossInstance.getAggroList().addDamageHate(creature.getPlayer(), 0, 1);
                }
                this.startRunningTask(this.AI_TASK_ATTACK_DELAY);
                this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
                return true;
            }
        }
        return this.randomWalk();
    }

    @Override
    protected boolean checkAggression(Creature creature) {
        CTBBossInstance cTBBossInstance = this.getActor();
        if (this.getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) {
            return false;
        }
        if (creature.isAlikeDead() || creature.isInvul()) {
            return false;
        }
        if (!cTBBossInstance.isAttackable(creature)) {
            return false;
        }
        if (!GeoEngine.canSeeTarget(cTBBossInstance, creature, false)) {
            return false;
        }
        cTBBossInstance.getAggroList().addDamageHate(creature, 0, 2);
        if (creature.isSummon() || creature.isPet()) {
            cTBBossInstance.getAggroList().addDamageHate(creature.getPlayer(), 0, 1);
        }
        this.startRunningTask(this.AI_TASK_ATTACK_DELAY);
        this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
        return true;
    }

    @Override
    protected boolean canAttackCharacter(Creature creature) {
        CTBBossInstance cTBBossInstance = this.getActor();
        return ((NpcInstance)cTBBossInstance).isAttackable(creature);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        CTBBossInstance cTBBossInstance = this.getActor();
        int n = (int)((double)cTBBossInstance.getX() + 800.0 * Math.cos(cTBBossInstance.headingToRadians(cTBBossInstance.getHeading() - 32768)));
        int n2 = (int)((double)cTBBossInstance.getY() + 800.0 * Math.sin(cTBBossInstance.headingToRadians(cTBBossInstance.getHeading() - 32768)));
        cTBBossInstance.setSpawnedLoc(new Location(n, n2, cTBBossInstance.getZ()));
        this.addTaskMove(cTBBossInstance.getSpawnedLoc(), true);
        this.doTask();
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public CTBBossInstance getActor() {
        return (CTBBossInstance)super.getActor();
    }
}

