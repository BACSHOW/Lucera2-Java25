/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.SimpleItemHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SSQStatus;
import l2.gameserver.network.l2.s2c.ShowXMasSeal;

public class Books
extends SimpleItemHandler {
    private static final int[] lS = new int[]{5555, 5707};

    @Override
    public int[] getItemIds() {
        return lS;
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance itemInstance, boolean bl) {
        int n = itemInstance.getItemId();
        switch (n) {
            case 5555: {
                player.sendPacket((IStaticPacket)new ShowXMasSeal(5555));
                break;
            }
            case 5707: {
                player.sendPacket((IStaticPacket)new SSQStatus(player, 1));
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }
}

