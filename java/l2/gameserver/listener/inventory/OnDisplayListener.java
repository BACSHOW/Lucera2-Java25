/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.inventory;

import l2.gameserver.model.Playable;
import l2.gameserver.model.items.ItemInstance;

public interface OnDisplayListener {
    public Integer onDisplay(int var1, ItemInstance var2, Playable var3);
}

