/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.RecipeShopItemInfo;

public class RequestRecipeShopMakeInfo
extends L2GameClientPacket {
    private int _manufacturerId;
    private int XE;

    @Override
    protected void readImpl() {
        this._manufacturerId = this.readD();
        this.XE = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        Player player2 = (Player)player.getVisibleObject(this._manufacturerId);
        if (player2 == null || player2.getPrivateStoreType() != 5 || !player2.isInActingRange(player)) {
            player.sendActionFailed();
            return;
        }
        long l = -1L;
        for (ManufactureItem manufactureItem : player2.getCreateList()) {
            if (manufactureItem.getRecipeId() != this.XE) continue;
            l = manufactureItem.getCost();
            break;
        }
        if (l == -1L) {
            player.sendActionFailed();
            return;
        }
        player.sendPacket((IStaticPacket)new RecipeShopItemInfo(player, player2, this.XE, l, -1));
    }
}

