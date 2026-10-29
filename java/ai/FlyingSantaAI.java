/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledFuture;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;

public class FlyingSantaAI
extends DefaultAI {
    private static final int bN = 100;
    private static final long bO = 300000L;
    private static final double bP = 50.0;
    private static final String[] bQ = new String[]{"\u0425\u043e-\u0425\u043e-\u0425\u043e", "\u0421 \u041f\u0440\u0430\u0437\u0434\u043d\u0438\u043a\u0430\u043c\u0438 \u0412\u0430\u0441!", "\u041b\u043e\u0432\u0438\u0442\u0435 \u043f\u043e\u0434\u0430\u0440\u043a\u0438 :D"};
    private static final double bR = 50.0;
    private static final int[] bS = new int[]{57};
    private static final int[] bT = new int[]{1};
    private static final Location[][] bU = new Location[][]{{new Location(82632, 149128, -3472), new Location(81051, 149288, -3472), new Location(81051, 148062, -3472), new Location(82904, 148056, -3472)}, {new Location(148408, 27928, -2272), new Location(146504, 27928, -2272)}, {new Location(146400, -54903, -2807), new Location(147694, -56555, -2807), new Location(148613, -55572, -2807)}};
    private int bV;
    private int bW;
    private ScheduledFuture<?> bX;

    public FlyingSantaAI(NpcInstance npcInstance) {
        super(npcInstance);
    }

    private static int a(Location location) {
        int n = -1;
        double d = Double.MAX_VALUE;
        for (int i = 0; i < bU.length; ++i) {
            for (int j = 0; j < bU[i].length; ++j) {
                double d2 = location.distance(bU[i][j]);
                if (!(d2 < d)) continue;
                d = d2;
                n = i;
            }
        }
        return n != -1 ? n : 0;
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null) {
            return;
        }
        npcInstance.setCollisionHeight(100.0);
        npcInstance.setWalking();
        this.bV = FlyingSantaAI.a(npcInstance.getSpawnedLoc());
        this.bW = 0;
        this.addTaskMove(bU[this.bV][this.bW], false);
        this.bX = ThreadPoolManager.getInstance().schedule(new UnspawnTask(npcInstance), 300000L);
        this.doTask();
    }

    private boolean g() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null) {
            return false;
        }
        ++this.bW;
        if (this.bW < bU[this.bV].length) {
            if (Rnd.chance(50.0)) {
                Functions.npcShout(npcInstance, bQ[Rnd.get(bQ.length)]);
            }
            if (Rnd.chance(50.0)) {
                int n = Rnd.get(bS.length);
                ItemInstance itemInstance = ItemFunctions.createItem(bS[n]);
                itemInstance.setCount(bT[n]);
                itemInstance.dropToTheGround(npcInstance, Location.coordsRandomize(npcInstance.getLoc(), 10, 50));
            }
            Location location = bU[this.bV][this.bW];
            this.addTaskMove(location, false);
            return this.doTask();
        }
        if (this.bX != null) {
            this.bX.cancel(true);
            this.bX = null;
        }
        npcInstance.decayMe();
        npcInstance.deleteMe();
        return false;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null || npcInstance.isDead()) {
            return true;
        }
        if (this._def_think) {
            this.doTask();
            return true;
        }
        return this.g();
    }

    @Override
    public boolean isGlobalAI() {
        return false;
    }

    @Override
    protected void onEvtArrived() {
        super.onEvtArrived();
        this.g();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }

    private class UnspawnTask
    implements Runnable {
        private WeakReference<NpcInstance> bY;

        public UnspawnTask(NpcInstance npcInstance) {
            this.bY = new WeakReference<NpcInstance>(npcInstance);
        }

        @Override
        public void run() {
            if (this.bY != null && this.bY.get() != null) {
                ((NpcInstance)this.bY.get()).decayMe();
                ((NpcInstance)this.bY.get()).deleteMe();
            }
        }
    }
}

