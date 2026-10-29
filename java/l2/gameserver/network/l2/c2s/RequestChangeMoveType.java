/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestChangeMoveType
extends L2GameClientPacket {
    private int abv;

    @Override
    protected void readImpl() throws Exception {
        this.abv = this.readD();
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
        if (player.isOutOfControl() || player.isInStoreMode() || player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.getTransformation() != 0 || player.isCursedWeaponEquipped()) {
            player.sendActionFailed();
            return;
        }
        switch (this.abv) {
            case 0: {
                if (!player.isRunning()) break;
                player.setWalking();
                break;
            }
            case 1: {
                player.setRunning();
            }
        }
    }
}

