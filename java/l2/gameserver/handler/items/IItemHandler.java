/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.items;

import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.ArrayUtils;

public interface IItemHandler {
    public static final IItemHandler NULL = new IItemHandler(){

        @Override
        public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
            return false;
        }

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
        public int[] getItemIds() {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
    };

    public boolean useItem(Playable var1, ItemInstance var2, boolean var3);

    public void dropItem(Player var1, ItemInstance var2, long var3, Location var5);

    public boolean pickupItem(Playable var1, ItemInstance var2);

    public int[] getItemIds();
}

