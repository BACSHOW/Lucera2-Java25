/*
 * Decompiled with CFR 0.152.
 */
package ai.isle_of_prayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.Fighter;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.EFunction;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;

public class FafurionKindred
extends Fighter {
    private static final int dW = 22270;
    private static final int dX = 22271;
    private static final int dY = 2368;
    private static final int cS = 9691;
    private static final int dZ = 9700;
    ScheduledFuture<?> poisonTask;
    ScheduledFuture<?> despawnTask;
    List<SimpleSpawner> spawns = new ArrayList<SimpleSpawner>();
    private static final FuncTemplate ea = new FuncTemplate(null, EFunction.Mul, Stats.HEAL_EFFECTIVNESS, 144, 0.0);

    public FafurionKindred(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.addStatFunc(ea.getFunc(this));
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        this.spawns.clear();
        ThreadPoolManager.getInstance().schedule(new SpawnTask(22270), 500L);
        ThreadPoolManager.getInstance().schedule(new SpawnTask(22271), 500L);
        ThreadPoolManager.getInstance().schedule(new SpawnTask(22270), 500L);
        ThreadPoolManager.getInstance().schedule(new SpawnTask(22271), 500L);
        this.poisonTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PoisonTask(), 3000L, 3000L);
        this.despawnTask = ThreadPoolManager.getInstance().schedule(new DeSpawnTask(), 300000L);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.cleanUp();
        super.onEvtDead(creature);
    }

    @Override
    protected void onEvtSeeSpell(Skill skill, Creature creature) {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead() || skill == null) {
            return;
        }
        if (skill.getId() == 2368) {
            npcInstance.setCurrentHp(npcInstance.getCurrentHp() + 3000.0, false);
        }
        npcInstance.getAggroList().remove(creature, true);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    private void cleanUp() {
        if (this.poisonTask != null) {
            this.poisonTask.cancel(false);
            this.poisonTask = null;
        }
        if (this.despawnTask != null) {
            this.despawnTask.cancel(false);
            this.despawnTask = null;
        }
        for (SimpleSpawner simpleSpawner : this.spawns) {
            simpleSpawner.deleteAll();
        }
        this.spawns.clear();
    }

    private void a(NpcInstance npcInstance, int n, int n2) {
        ItemInstance itemInstance = ItemFunctions.createItem(n);
        itemInstance.setCount(n2);
        itemInstance.dropToTheGround(npcInstance, Location.findPointToStay(npcInstance, 100));
    }

    private class DeSpawnTask
    extends RunnableImpl {
        private DeSpawnTask() {
        }

        @Override
        public void runImpl() {
            NpcInstance npcInstance = FafurionKindred.this.getActor();
            FafurionKindred.this.a(npcInstance, 9691, Rnd.get(1, 2));
            if (Rnd.chance(36)) {
                FafurionKindred.this.a(npcInstance, 9700, Rnd.get(1, 3));
            }
            FafurionKindred.this.cleanUp();
            npcInstance.deleteMe();
        }
    }

    private class PoisonTask
    extends RunnableImpl {
        private PoisonTask() {
        }

        @Override
        public void runImpl() {
            NpcInstance npcInstance = FafurionKindred.this.getActor();
            npcInstance.reduceCurrentHp(500.0, npcInstance, null, true, false, true, false, false, false, false);
        }
    }

    private class SpawnTask
    extends RunnableImpl {
        private final int _id;

        public SpawnTask(int n) {
            this._id = n;
        }

        @Override
        public void runImpl() {
            NpcInstance npcInstance = FafurionKindred.this.getActor();
            SimpleSpawner simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(this._id));
            simpleSpawner.setLoc(Location.findPointToStay(npcInstance, 100, 120));
            simpleSpawner.setRespawnDelay(30L, 40L);
            simpleSpawner.doSpawn(true);
            FafurionKindred.this.spawns.add(simpleSpawner);
        }
    }
}

