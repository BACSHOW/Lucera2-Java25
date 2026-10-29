/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.ai.Fighter;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Territory;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.Location;

public class SaintNinja
extends Fighter {
    private boolean bE = false;
    private long cu;
    private final NpcTemplate cI;
    private final int cJ;

    public SaintNinja(NpcInstance npcInstance) {
        super(npcInstance);
        this.cI = NpcHolder.getInstance().getTemplate(npcInstance.getParameter("CreateOnePrivateOnAttack", 0));
        this.cJ = npcInstance.getParameter("CreateOnePrivateOnAttackChance", 0);
    }

    @Override
    protected void onEvtSpawn() {
        this.bE = false;
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (Rnd.get(100) < this.cJ && !this.bE) {
            this.bE = true;
            SimpleSpawner simpleSpawner = new SimpleSpawner(this.cI);
            simpleSpawner.setLoc(Location.findPointToStay(npcInstance, 100, 100));
            NpcInstance npcInstance2 = simpleSpawner.doSpawn(true);
            if (creature.isPet() || creature.isSummon()) {
                npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(2, 100));
            }
            npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature.getPlayer(), Rnd.get(1, 100));
            simpleSpawner.stopRespawn();
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected boolean maybeMoveToHome() {
        int n;
        NpcInstance npcInstance = this.getActor();
        if (System.currentTimeMillis() - this.cu < 10000L) {
            return false;
        }
        boolean bl = npcInstance.hasRandomWalk();
        Location location = npcInstance.getSpawnedLoc();
        if (location == null) {
            return false;
        }
        if (bl && (!Config.RND_WALK || Rnd.chance(Config.RND_WALK_RATE))) {
            return false;
        }
        if (!bl && npcInstance.isInRangeZ(location, (long)Config.MAX_DRIFT_RANGE)) {
            return false;
        }
        int n2 = location.x + Rnd.get(-Config.MAX_DRIFT_RANGE, Config.MAX_DRIFT_RANGE);
        int n3 = GeoEngine.getHeight(n2, n = location.y + Rnd.get(-Config.MAX_DRIFT_RANGE, Config.MAX_DRIFT_RANGE), location.z, npcInstance.getGeoIndex());
        if (location.z - n3 > 64) {
            return false;
        }
        SpawnRange spawnRange = npcInstance.getSpawnRange();
        boolean bl2 = true;
        if (spawnRange != null && spawnRange instanceof Territory) {
            bl2 = ((Territory)spawnRange).isInside(n2, n);
        }
        if (bl2) {
            npcInstance.broadcastPacketToOthers(new MagicSkillUse(npcInstance, npcInstance, 4671, 1, 500, 0L));
            ThreadPoolManager.getInstance().schedule(this.new Teleport(new Location(n2, n, n3)), 500L);
            this.cu = System.currentTimeMillis();
        }
        return bl2;
    }
}

