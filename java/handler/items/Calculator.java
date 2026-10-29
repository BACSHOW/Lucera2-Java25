/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ScriptItemHandler;
import l2.gameserver.model.Playable;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ShowCalc;

public class Calculator
extends ScriptItemHandler {
    private static final int lT = 4393;

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (!playable.isPlayer()) {
            return false;
        }
        playable.sendPacket((IStaticPacket)new ShowCalc(itemInstance.getItemId()));
        return true;
    }

    @Override
    public int[] getItemIds() {
        return new int[]{4393};
    }
}

