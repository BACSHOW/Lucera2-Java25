/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ScriptItemHandler;
import l2.gameserver.data.xml.holder.EnchantItemHolder;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ChooseInventoryItem;

public class EnchantScrolls
extends ScriptItemHandler {
    private static int[] lS = null;

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        if (player.getEnchantScroll() != null) {
            return false;
        }
        player.setEnchantScroll(itemInstance);
        player.sendPacket((IStaticPacket)new ChooseInventoryItem(itemInstance.getItemId()));
        return true;
    }

    @Override
    public final int[] getItemIds() {
        if (lS == null) {
            lS = EnchantItemHolder.getInstance().getScrollIds();
        }
        return lS;
    }
}

