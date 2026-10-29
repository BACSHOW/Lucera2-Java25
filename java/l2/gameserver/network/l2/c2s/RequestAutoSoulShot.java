/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestAutoSoulShot
extends L2GameClientPacket {
    private static final Logger LOG = LoggerFactory.getLogger(RequestAutoSoulShot.class);
    private int _itemId;
    private boolean abc;

    @Override
    protected void readImpl() {
        this._itemId = this.readD();
        this.abc = this.readD() == 1;
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getPrivateStoreType() != 0 || player.isDead()) {
            return;
        }
        ItemInstance itemInstance = player.getInventory().getItemByItemId(this._itemId);
        if (itemInstance == null) {
            return;
        }
        if (!itemInstance.getTemplate().isShotItem()) {
            return;
        }
        if (!itemInstance.getTemplate().testCondition(player, itemInstance, false)) {
            String string = "Player " + player.getName() + " trying illegal item use, ban this player!";
            Log.add(string, "illegal-actions");
            LOG.warn(string);
            return;
        }
        if (this.abc) {
            player.addAutoSoulShot(this._itemId);
            player.sendPacket((IStaticPacket)new ExAutoSoulShot(this._itemId, true));
            player.sendPacket((IStaticPacket)new SystemMessage(1433).addString(itemInstance.getName()));
            IItemHandler iItemHandler = itemInstance.getTemplate().getHandler();
            iItemHandler.useItem(player, itemInstance, false);
            return;
        }
        player.removeAutoSoulShot(this._itemId);
        player.sendPacket((IStaticPacket)new ExAutoSoulShot(this._itemId, false));
        player.sendPacket((IStaticPacket)new SystemMessage(1434).addString(itemInstance.getName()));
    }
}

