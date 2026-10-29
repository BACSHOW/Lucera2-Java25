/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;

public class Action
extends L2GameClientPacket {
    private int CS;
    private int aat;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.readD();
        this.readD();
        this.readD();
        this.aat = this.readC();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.sendActionFailed();
            return;
        }
        GameObject gameObject = player.getVisibleObject(this.CS);
        if (gameObject == null) {
            player.sendActionFailed();
            return;
        }
        player.setActive();
        if (player.getAggressionTarget() != null && player.getAggressionTarget() != gameObject) {
            player.sendActionFailed();
            return;
        }
        if (player.isFrozen()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }
        l2.gameserver.utils.MovementTelemetryLogger.logC2S(player, "Action", String.format("Target=%s [#%d] isShift=%b curTarget=%s dist=%.1f", gameObject.getName(), gameObject.getObjectId(), this.aat == 1, (player.getTarget() != null ? player.getTarget().getName() : "null"), player.getDistance(gameObject)));
        gameObject.onAction(player, this.aat == 1);
    }
}

