/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SocialAction;

public class RequestSocialAction
extends L2GameClientPacket {
    private int aat;

    @Override
    protected void readImpl() throws Exception {
        this.aat = this.readD();
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isOutOfControl() || player.getTransformation() != 0 || player.isCursedWeaponEquipped() || player.isActionsDisabled() || player.isSitting() || player.getPrivateStoreType() != 0 || player.isProcessingRequest()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
            return;
        }
        if (this.aat > 1 && this.aat < 14) {
            player.broadcastPacket(new SocialAction(player.getObjectId(), this.aat));
        }
    }
}

