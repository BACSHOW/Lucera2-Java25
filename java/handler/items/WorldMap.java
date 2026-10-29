/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ScriptItemHandler;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ShowMiniMap;

public class WorldMap
extends ScriptItemHandler {
    private static final int[] iv = new int[]{1665, 1863};

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        player.sendPacket((IStaticPacket)new ShowMiniMap(player, itemInstance.getItemId()));
        return true;
    }

    @Override
    public final int[] getItemIds() {
        return iv;
    }
}

