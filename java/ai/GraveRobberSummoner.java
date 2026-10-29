/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Mystic;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.templates.npc.MinionData;

public class GraveRobberSummoner
extends Mystic {
    private static final int[] ce = new int[]{22683, 22684, 22685, 22686};
    private int cf = 1;

    public GraveRobberSummoner(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.addStatFunc(new FuncMulMinionCount(Stats.MAGIC_DEFENCE, 48, npcInstance));
        npcInstance.addStatFunc(new FuncMulMinionCount(Stats.POWER_DEFENCE, 48, npcInstance));
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        NpcInstance npcInstance = this.getActor();
        npcInstance.getMinionList().addMinion(new MinionData(ce[Rnd.get(ce.length)], Rnd.get(2)));
        this.cf = Math.max(npcInstance.getMinionList().getAliveMinions().size(), 1);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        MonsterInstance monsterInstance = (MonsterInstance)this.getActor();
        if (monsterInstance.isDead()) {
            return;
        }
        this.cf = Math.max(monsterInstance.getMinionList().getAliveMinions().size(), 1);
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        npcInstance.getMinionList().deleteMinions();
        super.onEvtDead(creature);
    }

    private class FuncMulMinionCount
    extends Func {
        public FuncMulMinionCount(Stats stats, int n, Object object) {
            super(stats, n, object);
        }

        @Override
        public void calc(Env env) {
            env.value *= (double)GraveRobberSummoner.this.cf;
        }
    }
}

