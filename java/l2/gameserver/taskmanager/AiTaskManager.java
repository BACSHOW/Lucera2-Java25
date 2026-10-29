/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import l2.commons.threading.RunnableImpl;
import l2.commons.threading.SteppingRunnableQueueManager;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;

public class AiTaskManager
extends SteppingRunnableQueueManager {
    private static final long aqW = 250L;
    private static final AiTaskManager[] aqX = new AiTaskManager[Config.AI_TASK_MANAGER_COUNT];
    private static int aqY;

    public static final AiTaskManager getInstance() {
        return aqX[aqY++ & aqX.length - 1];
    }

    private AiTaskManager() {
        super(250L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(250L), 250L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                AiTaskManager.this.purge();
            }
        }, 60000L, 60000L);
    }

    public CharSequence getStats(int n) {
        return aqX[n].getStats();
    }

    static {
        for (int i = 0; i < aqX.length; ++i) {
            AiTaskManager.aqX[i] = new AiTaskManager();
        }
        aqY = 0;
    }
}

