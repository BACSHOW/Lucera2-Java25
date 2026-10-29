/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.player;

import l2.gameserver.Config;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;

public class AutoFarmEndTask
implements Runnable {
    private final AutoFarmContext Nw;

    public AutoFarmEndTask(AutoFarmContext autoFarmContext) {
        this.Nw = autoFarmContext;
    }

    @Override
    public void run() {
        if (this.Nw != null) {
            this.Nw.setAutoFarmEndTask(0L);
            this.Nw.stopFarmTask(false);
            if (Config.FARM_ONLINE_TYPE) {
                this.Nw.checkFarmTask();
            }
        }
    }
}

