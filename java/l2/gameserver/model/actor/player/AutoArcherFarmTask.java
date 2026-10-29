/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.player;

import java.util.concurrent.ScheduledFuture;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.actor.player.BaseFarmTask;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Pair;

public class AutoArcherFarmTask
extends BaseFarmTask
implements Runnable {
    public AutoArcherFarmTask(AutoFarmContext autoFarmContext) {
        super(autoFarmContext);
    }

    @Override
    public void runImpl() throws Exception {
        this.tryAttack(this.selectRandomTarget());
    }

    @Override
    protected void physicalAttack() {
        Pair<ScheduledFuture<?>, Location> pair;
        NpcInstance npcInstance;
        AutoFarmContext autoFarmContext = this.getAutoFarmContext();
        if (autoFarmContext == null) {
            return;
        }
        Player player = autoFarmContext.getPlayer();
        if (player == null) {
            return;
        }
        if (autoFarmContext.isRunTargetCloseUp() && (npcInstance = this.getCommittedTarget()) != null && player.getDistance(npcInstance) < (double)Config.RUN_CLOSE_UP_DISTANCE && (pair = this.runAwayFromTargetAndThan(npcInstance, player, 500, 100, this)) != null) {
            if (this.getMoveToPair() != null && this.getMoveToPair().getLeft() != null) {
                this.getMoveToPair().getLeft().cancel(false);
            }
            this.setMoveToPair(pair);
            return;
        }
        super.physicalAttack();
    }
}

