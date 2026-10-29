/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.threading.SteppingRunnableQueueManager;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;

public class DecayTaskManager
extends SteppingRunnableQueueManager {
    private static final DecayTaskManager arc = new DecayTaskManager();

    public static final DecayTaskManager getInstance() {
        return arc;
    }

    private DecayTaskManager() {
        super(500L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 500L, 500L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                DecayTaskManager.this.purge();
            }
        }, 60000L, 60000L);
    }

    public Future<?> addDecayTask(final Creature creature, long l) {
        return this.schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                creature.doDecay();
            }
        }, l);
    }
}

