/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.player;

import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.actor.player.BaseFarmTask;

public class AutoPhysicalFarmTask
extends BaseFarmTask
implements Runnable {
    public AutoPhysicalFarmTask(AutoFarmContext autoFarmContext) {
        super(autoFarmContext);
    }

    @Override
    public void runImpl() throws Exception {
        this.tryAttack(this.selectRandomTarget());
    }
}

