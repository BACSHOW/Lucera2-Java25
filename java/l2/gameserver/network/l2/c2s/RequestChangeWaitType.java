/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;

public class RequestChangeWaitType
extends L2GameClientPacket {
    private int abw;

    @Override
    protected void readImpl() throws Exception {
        this.abw = this.readD();
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFrozen()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }
        if (player.isOutOfControl() || player.isInStoreMode() || player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.getTransformation() != 0 || player.isCursedWeaponEquipped()) {
            player.sendActionFailed();
            return;
        }
        GameObject gameObject = player.getTarget();
        switch (this.abw) {
            case 0: {
                if (player.isSitting()) break;
                if (gameObject != null && gameObject instanceof StaticObjectInstance && ((StaticObjectInstance)gameObject).getType() == 1 && player.getDistance3D(gameObject) <= (double)gameObject.getActingRange()) {
                    player.sitDown((StaticObjectInstance)gameObject);
                    break;
                }
                player.sitDown(null);
                break;
            }
            case 1: {
                if (!player.isSitting()) break;
                player.standUp();
            }
        }
    }
}

