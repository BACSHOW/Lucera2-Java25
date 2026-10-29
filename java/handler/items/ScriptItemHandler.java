/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.handler.items.ItemHandler;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;

public abstract class ScriptItemHandler
implements IItemHandler,
ScriptFile {
    @Override
    public void dropItem(Player player, ItemInstance itemInstance, long l, Location location) {
        if (itemInstance.isEquipped()) {
            player.getInventory().unEquipItem(itemInstance);
            player.sendUserInfo(true);
        }
        if ((itemInstance = player.getInventory().removeItemByObjectId(itemInstance.getObjectId(), l)) == null) {
            player.sendActionFailed();
            return;
        }
        Log.LogItem(player, Log.ItemLog.Drop, itemInstance);
        itemInstance.dropToTheGround((Playable)player, location);
        player.disableDrop(1000);
        player.sendChanges();
    }

    @Override
    public boolean pickupItem(Playable playable, ItemInstance itemInstance) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

