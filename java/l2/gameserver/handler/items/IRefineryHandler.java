/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.items;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;

public interface IRefineryHandler {
    public void onInitRefinery(Player var1);

    public void onPutTargetItem(Player var1, ItemInstance var2);

    public void onPutMineralItem(Player var1, ItemInstance var2, ItemInstance var3);

    public void onPutGemstoneItem(Player var1, ItemInstance var2, ItemInstance var3, ItemInstance var4, long var5);

    public void onRequestRefine(Player var1, ItemInstance var2, ItemInstance var3, ItemInstance var4, long var5);

    public void onInitRefineryCancel(Player var1);

    public void onPutTargetCancelItem(Player var1, ItemInstance var2);

    public void onRequestCancelRefine(Player var1, ItemInstance var2);
}

