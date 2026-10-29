/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.threading.SteppingRunnableQueueManager;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Player;

public class AutoSaveManager
extends SteppingRunnableQueueManager {
    private static final AutoSaveManager arb = new AutoSaveManager();

    public static final AutoSaveManager getInstance() {
        return arb;
    }

    private AutoSaveManager() {
        super(10000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                AutoSaveManager.this.purge();
            }
        }, 60000L, 60000L);
    }

    public Future<?> addAutoSaveTask(final Player player) {
        long l = (long)Rnd.get(180, 360) * 1000L;
        return this.scheduleAtFixedRate(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                if (!player.isOnline()) {
                    return;
                }
                player.store(true);
                if (Config.AUTOSAVE_ITEMS) {
                    player.getInventory().store();
                }
            }
        }, l, l);
    }
}

