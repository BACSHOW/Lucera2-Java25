/*
 * Decompiled with CFR 0.152.
 */
package ai;

import bosses.BaiumManager;
import gnu.trove.TIntObjectHashMap;
import java.util.HashMap;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;

public class Baium
extends DefaultAI {
    private boolean aZ = true;
    private final Skill ba;
    private final Skill bb;
    private final Skill bc;
    private final Skill bd;
    private final Skill be;

    public Baium(NpcInstance npcInstance) {
        super(npcInstance);
        TIntObjectHashMap<Skill> tIntObjectHashMap = this.getActor().getTemplate().getSkills();
        this.ba = tIntObjectHashMap.get(4127);
        this.bb = tIntObjectHashMap.get(4128);
        this.bc = tIntObjectHashMap.get(4129);
        this.bd = tIntObjectHashMap.get(4130);
        this.be = tIntObjectHashMap.get(4131);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        BaiumManager.setLastAttackTime();
        for (Playable playable : BaiumManager.getZone().getInsidePlayables()) {
            this.notifyEvent(CtrlEvent.EVT_AGGRESSION, playable, 1);
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected boolean createNewTask() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null) {
            return true;
        }
        if (!BaiumManager.getZone().checkIfInZone(npcInstance)) {
            this.teleportHome();
            return false;
        }
        this.clearTasks();
        Creature creature = this.prepareTarget();
        if (creature == null) {
            return false;
        }
        if (!BaiumManager.getZone().checkIfInZone(creature)) {
            npcInstance.getAggroList().remove(creature, false);
            return false;
        }
        int n = 20;
        int n2 = 20;
        int n3 = npcInstance.getCurrentHpPercents() > 50.0 ? 0 : 20;
        int n4 = npcInstance.getCurrentHpPercents() > 25.0 ? 0 : 20;
        Skill skill = null;
        if (npcInstance.isMovementDisabled()) {
            skill = this.bd;
        } else if (!Rnd.chance(100 - n4 - n3 - n - n2)) {
            HashMap<Skill, Integer> hashMap = new HashMap<Skill, Integer>();
            double d = npcInstance.getDistance(creature);
            this.addDesiredSkill(hashMap, creature, d, this.bb);
            this.addDesiredSkill(hashMap, creature, d, this.bc);
            if (n3 > 0) {
                this.addDesiredSkill(hashMap, creature, d, this.be);
            }
            if (n4 > 0) {
                this.addDesiredSkill(hashMap, creature, d, this.bd);
            }
            skill = this.selectTopSkill(hashMap);
        }
        if (skill == null) {
            skill = this.ba;
        } else if (skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF) {
            creature = npcInstance;
        }
        this.addTaskCast(creature, skill);
        skill = null;
        return true;
    }

    @Override
    protected boolean maybeMoveToHome() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance != null && !BaiumManager.getZone().checkIfInZone(npcInstance)) {
            this.teleportHome();
        }
        return false;
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.aZ = true;
        super.onEvtDead(creature);
    }
}

