/*
 * Decompiled with CFR 0.152.
 */
package ai;

import bosses.ValakasManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.tables.SkillTable;

public class Valakas
extends DefaultAI {
    final Skill s_lava_skin = this.a(4680, 1);
    final Skill s_fear = this.a(4689, 1);
    final Skill s_regen1 = this.a(4691, 1);
    final Skill s_regen2 = this.a(4691, 2);
    final Skill s_regen3 = this.a(4691, 3);
    final Skill s_regen4 = this.a(4691, 4);
    final Skill s_tremple_left = this.a(4681, 1);
    final Skill s_tremple_right = this.a(4682, 1);
    final Skill s_tail_stomp_a = this.a(4685, 1);
    final Skill s_tail_lash = this.a(4688, 1);
    final Skill s_meteor = this.a(4690, 1);
    final Skill s_breath_low = this.a(4683, 1);
    final Skill s_breath_high = this.a(4684, 1);
    private long dg = Long.MAX_VALUE;
    private final long dh = 120000L;
    private double di;
    private int aV = 0;
    private List<NpcInstance> aX = new ArrayList<NpcInstance>();

    public Valakas(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        ValakasManager.setLastAttackTime();
        for (Playable playable : ValakasManager.getZone().getInsidePlayables()) {
            this.notifyEvent(CtrlEvent.EVT_AGGRESSION, playable, 1);
        }
        if (n > 100 && creature.getDistance(npcInstance) > 400.0) {
            this.di += (double)n / 1000.0;
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean createNewTask() {
        Serializable serializable;
        this.clearTasks();
        Creature creature = this.prepareTarget();
        if (creature == null) {
            return false;
        }
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead()) {
            return false;
        }
        double d = npcInstance.getDistance(creature);
        double d2 = npcInstance.getCurrentHpPercents();
        if (this.aV == 0) {
            npcInstance.altOnMagicUseTimer(npcInstance, this.s_regen1);
            this.aV = 1;
        } else if (d2 < 80.0 && this.aV == 1) {
            npcInstance.altOnMagicUseTimer(npcInstance, this.s_regen2);
            this.dg = System.currentTimeMillis();
            this.aV = 2;
        } else if (d2 < 50.0 && this.aV == 2) {
            npcInstance.altOnMagicUseTimer(npcInstance, this.s_regen3);
            this.aV = 3;
        } else if (d2 < 30.0 && this.aV == 3) {
            npcInstance.altOnMagicUseTimer(npcInstance, this.s_regen4);
            this.aV = 4;
        }
        if (this.di > 2000.0) {
            if (Rnd.chance(60)) {
                serializable = npcInstance.getAggroList().getRandomHated();
                if (serializable != null && Math.abs(npcInstance.getZ() - ((GameObject)serializable).getZ()) < 400) {
                    this.setAttackTarget((Creature)serializable);
                    if (this._madnessTask == null && !npcInstance.isConfused()) {
                        npcInstance.startConfused();
                        this._madnessTask = ThreadPoolManager.getInstance().schedule(this.new MadnessTask(), 20000L);
                    }
                }
                ValakasManager.broadcastCustomScreenMessage("ValakasRangedAttack");
                this.di = 0.0;
            }
        } else if (this.dg < System.currentTimeMillis()) {
            ValakasManager.broadcastCustomScreenMessage("ValakasAttaksKeepIt");
            this.dg = System.currentTimeMillis() + 120000L + (long)Rnd.get(60) * 1000L;
            return this.chooseTaskAndTargets(this.s_fear, creature, d);
        }
        if (Rnd.chance(50)) {
            return this.chooseTaskAndTargets(Rnd.chance(50) ? this.s_tremple_left : this.s_tremple_right, creature, d);
        }
        serializable = new HashMap();
        switch (this.aV) {
            case 1: {
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_breath_low);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_tail_stomp_a);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_meteor);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_fear);
                break;
            }
            case 2: 
            case 3: {
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_breath_low);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_tail_stomp_a);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_breath_high);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_tail_lash);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_meteor);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_fear);
                break;
            }
            case 4: {
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_breath_low);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_tail_stomp_a);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_breath_high);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_tail_lash);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_meteor);
                this.addDesiredSkill((Map<Skill, Integer>)((Object)serializable), creature, d, this.s_fear);
            }
        }
        Skill skill = this.selectTopSkill((Map<Skill, Integer>)((Object)serializable));
        if (skill != null && !skill.isOffensive()) {
            creature = npcInstance;
        }
        return this.chooseTaskAndTargets(skill, creature, d);
    }

    @Override
    protected void thinkAttack() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isInZone(Zone.ZoneType.poison) && npcInstance.getEffectList() != null && npcInstance.getEffectList().getEffectsBySkill(this.s_lava_skin) == null) {
            npcInstance.altOnMagicUseTimer(npcInstance, this.s_lava_skin);
        }
        super.thinkAttack();
    }

    private Skill a(int n, int n2) {
        return SkillTable.getInstance().getInfo(n, n2);
    }

    private int f() {
        int n = 0;
        for (NpcInstance npcInstance : this.aX) {
            if (npcInstance == null || npcInstance.isDead()) continue;
            ++n;
        }
        return n;
    }

    @Override
    protected void onEvtDead(Creature creature) {
        if (this.aX != null && !this.aX.isEmpty()) {
            for (NpcInstance npcInstance : this.aX) {
                npcInstance.deleteMe();
            }
        }
        super.onEvtDead(creature);
    }
}

