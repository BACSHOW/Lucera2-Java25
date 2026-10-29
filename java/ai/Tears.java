/*
 * Decompiled with CFR 0.152.
 */
package ai;

import gnu.trove.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;

public class Tears
extends DefaultAI {
    final Skill Invincible;
    final Skill Freezing;
    private static final int cS = 2369;
    private static final int cT = 25535;
    ScheduledFuture<?> spawnTask;
    ScheduledFuture<?> despawnTask;
    List<NpcInstance> spawns = new ArrayList<NpcInstance>();
    private boolean cU = false;
    private int cV = 0;
    private long cW = 0L;

    public Tears(NpcInstance npcInstance) {
        super(npcInstance);
        TIntObjectHashMap<Skill> tIntObjectHashMap = this.getActor().getTemplate().getSkills();
        this.Invincible = tIntObjectHashMap.get(5420);
        this.Freezing = tIntObjectHashMap.get(5238);
    }

    @Override
    protected void onEvtSeeSpell(Skill skill, Creature creature) {
        Player player;
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead() || skill == null || creature == null) {
            return;
        }
        if (System.currentTimeMillis() - this.cW > 5000L) {
            this.cV = 0;
        }
        if (skill.getId() == 2369) {
            ++this.cV;
            this.cW = System.currentTimeMillis();
        }
        if ((player = creature.getPlayer()) == null) {
            return;
        }
        int n = 1;
        Party party = player.getParty();
        if (party != null) {
            n = party.getMemberCount();
        }
        if (this.cV >= n) {
            this.cV = 0;
            npcInstance.getEffectList().stopEffect(this.Invincible);
        }
    }

    @Override
    protected boolean createNewTask() {
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
        int n = Rnd.get(100);
        if (d2 < 15.0 && !this.cU) {
            this.cU = true;
            this.addTaskBuff(npcInstance, this.Invincible);
            Functions.npcSay(npcInstance, "\u0413\u043e\u0442\u043e\u0432\u044c\u0442\u0435\u0441\u044c \u043a \u0441\u043c\u0435\u0440\u0442\u0438!!!");
            return true;
        }
        if (n < 5 && this.spawnTask == null && this.despawnTask == null) {
            npcInstance.broadcastPacketToOthers(new MagicSkillUse(npcInstance, npcInstance, 5441, 1, 3000, 0L));
            this.spawnTask = ThreadPoolManager.getInstance().schedule(new SpawnMobsTask(), 3000L);
            return true;
        }
        if (!npcInstance.isAMuted() && n < 75) {
            return this.chooseTaskAndTargets(null, creature, d);
        }
        return this.chooseTaskAndTargets(this.Freezing, creature, d);
    }

    private void l() {
        Creature creature;
        Location location;
        NpcInstance npcInstance = this.getActor();
        for (int i = 0; i < 9; ++i) {
            try {
                location = Location.findPointToStay(144298, 154420, -11854, 300, 320, npcInstance.getGeoIndex());
                SimpleSpawner simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(25535));
                simpleSpawner.setLoc(location);
                simpleSpawner.setReflection(npcInstance.getReflection());
                NpcInstance npcInstance2 = simpleSpawner.doSpawn(true);
                this.spawns.add(npcInstance2);
                creature = npcInstance.getAggroList().getRandomHated();
                if (creature == null) continue;
                npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(1, 100));
                continue;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        location = Location.findPointToStay(144298, 154420, -11854, 300, 320, npcInstance.getReflectionId());
        npcInstance.teleToLocation(location);
        creature = npcInstance.getAggroList().getRandomHated();
        if (creature != null) {
            npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(1, 100));
        }
        if (this.despawnTask != null) {
            this.despawnTask.cancel(false);
        }
        this.despawnTask = ThreadPoolManager.getInstance().schedule(new DeSpawnTask(), 30000L);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    private class SpawnMobsTask
    extends RunnableImpl {
        private SpawnMobsTask() {
        }

        @Override
        public void runImpl() {
            Tears.this.l();
            Tears.this.spawnTask = null;
        }
    }

    private class DeSpawnTask
    extends RunnableImpl {
        private DeSpawnTask() {
        }

        @Override
        public void runImpl() {
            for (NpcInstance npcInstance : Tears.this.spawns) {
                if (npcInstance == null) continue;
                npcInstance.deleteMe();
            }
            Tears.this.spawns.clear();
            Tears.this.despawnTask = null;
        }
    }
}

