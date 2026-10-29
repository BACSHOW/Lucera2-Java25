/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.SimpleItemHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.scripts.Functions;

public class Special
extends SimpleItemHandler {
    private static final int[] lS = new int[]{8060};

    @Override
    public int[] getItemIds() {
        return lS;
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance itemInstance, boolean bl) {
        int n = itemInstance.getItemId();
        switch (n) {
            case 8060: {
                return this.b(player, bl);
            }
        }
        return false;
    }

    private boolean b(Player player, boolean bl) {
        if (Functions.removeItem(player, 8058, 1L) == 1L) {
            Functions.addItem(player, 8059, 1L);
            return true;
        }
        return false;
    }
}

