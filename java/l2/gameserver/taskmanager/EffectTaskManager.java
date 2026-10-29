/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import l2.commons.threading.RunnableImpl;
import l2.commons.threading.SteppingRunnableQueueManager;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;

public class EffectTaskManager
extends SteppingRunnableQueueManager {
    private static final long aqW = 250L;
    private static final EffectTaskManager[] arf = new EffectTaskManager[Config.EFFECT_TASK_MANAGER_COUNT];
    private static int aqY;

    public static final EffectTaskManager getInstance() {
        return arf[aqY++ & arf.length - 1];
    }

    private EffectTaskManager() {
        super(250L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(250L), 250L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                EffectTaskManager.this.purge();
            }
        }, 30000L, 30000L);
    }

    public CharSequence getStats(int n) {
        return arf[n].getStats();
    }

    static {
        for (int i = 0; i < arf.length; ++i) {
            EffectTaskManager.arf[i] = new EffectTaskManager();
        }
        aqY = 0;
    }
}

