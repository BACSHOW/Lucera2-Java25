/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import l2.commons.threading.RunnableImpl;
import l2.commons.threading.SteppingRunnableQueueManager;
import l2.gameserver.ThreadPoolManager;

public class RegenTaskManager
extends SteppingRunnableQueueManager {
    private static final RegenTaskManager arq = new RegenTaskManager();

    public static final RegenTaskManager getInstance() {
        return arq;
    }

    private RegenTaskManager() {
        super(1000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                RegenTaskManager.this.purge();
            }
        }, 10000L, 10000L);
    }
}

