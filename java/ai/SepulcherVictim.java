/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.concurrent.ScheduledFuture;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherParser;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnDefine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.SepulcherVictimInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;

public class SepulcherVictim
extends CharacterAI {
    private static final int cN = 31455;
    private final SepulcherVictimInstance cO;
    private ScheduledFuture cP;
    private ScheduledFuture cQ;
    private volatile boolean cR = false;

    public SepulcherVictim(Creature creature) {
        super(creature);
        this.cO = (SepulcherVictimInstance)creature;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        this.cO.setRunning();
        this.cP = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> {
            Functions.npcSayCustomMessage((NpcInstance)this.cO, "ROYAL_RUSH_10484_HELP_ME", new Object[0]);
            this.cO.moveToLocation(Location.findPointToStay(this.cO.getSpawnedLoc(), 500, this.cO.getGeoIndex()), 50, false);
        }, 500L, 5000L);
        this.cQ = ThreadPoolManager.getInstance().schedule(() -> {
            this.cR = true;
            this.a(this.cP);
            Functions.npcSayCustomMessage((NpcInstance)this.cO, "ROYAL_RUSH_503_THX_FOR_SAVING_ME", new Object[0]);
            this.cO.getSepulcherSpawnDefine().getHandler().spawnCustomSingle(31455, this.cO.getLoc());
        }, 300000L);
    }

    @Override
    public void onEvtDeSpawn() {
        super.onEvtDeSpawn();
        this.a(this.cP);
        this.a(this.cQ);
    }

    private void a(ScheduledFuture scheduledFuture) {
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(false);
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        super.onEvtDead(creature);
        if (!this.cR) {
            SepulcherSpawnDefine sepulcherSpawnDefine = this.cO.getSepulcherSpawnDefine();
            int n = SepulcherParser.getInstance().getVictimBossData().get(sepulcherSpawnDefine.getHandler().getName());
            sepulcherSpawnDefine.getHandler().spawnCustomSingle(n, this.cO.getLoc());
        }
    }
}

