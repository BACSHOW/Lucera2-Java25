/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class Kama63Minion
extends Fighter {
    private static final int cp = 18571;
    private static final int cq = 25000;
    private long bf = 0L;
    private NpcInstance cr;
    private boolean cs = false;
    ScheduledFuture<?> _dieTask = null;

    public Kama63Minion(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtSpawn() {
        this.cr = this.b(18571);
        super.onEvtSpawn();
    }

    @Override
    protected boolean thinkActive() {
        if (this.cr == null) {
            this.cr = this.b(18571);
        } else if (!this.cs) {
            this.cs = true;
            Functions.npcSayCustomMessage(this.cr, "Kama63Boss", new Object[0]);
            NpcInstance npcInstance = this.getActor();
            npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this.cr.getAggroList().getRandomHated(), Rnd.get(1, 100));
            this._dieTask = ThreadPoolManager.getInstance().schedule(new DieScheduleTimerTask(npcInstance, this.cr), 25000L);
        }
        return super.thinkActive();
    }

    private NpcInstance b(int n) {
        if (System.currentTimeMillis() < this.bf) {
            return null;
        }
        this.bf = System.currentTimeMillis() + 15000L;
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null) {
            return null;
        }
        for (NpcInstance npcInstance2 : World.getAroundNpc(npcInstance)) {
            if (npcInstance2.getNpcId() != n) continue;
            return npcInstance2;
        }
        return null;
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.cs = false;
        if (this._dieTask != null) {
            this._dieTask.cancel(false);
            this._dieTask = null;
        }
        super.onEvtDead(creature);
    }

    public class DieScheduleTimerTask
    extends RunnableImpl {
        NpcInstance _minion = null;
        NpcInstance _master = null;

        public DieScheduleTimerTask(NpcInstance npcInstance, NpcInstance npcInstance2) {
            this._minion = npcInstance;
            this._master = npcInstance2;
        }

        @Override
        public void runImpl() {
            if (this._master != null && this._minion != null && !this._master.isDead() && !this._minion.isDead()) {
                this._master.setCurrentHp(this._master.getCurrentHp() + this._minion.getCurrentHp() * 5.0, false);
            }
            Functions.npcSayCustomMessage(this._minion, "Kama63Minion", new Object[0]);
            this._minion.doDie(this._minion);
        }
    }
}

