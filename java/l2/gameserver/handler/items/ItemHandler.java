/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.items;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.templates.item.ItemTemplate;

public class ItemHandler
extends AbstractHolder {
    private static final ItemHandler zu = new ItemHandler();

    public static ItemHandler getInstance() {
        return zu;
    }

    private ItemHandler() {
    }

    public void registerItemHandler(IItemHandler iItemHandler) {
        int[] nArray;
        for (int n : nArray = iItemHandler.getItemIds()) {
            ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
            if (itemTemplate == null) {
                this.warn("Item not found: " + n + " handler: " + iItemHandler.getClass().getSimpleName());
                continue;
            }
            if (itemTemplate.getHandler() != IItemHandler.NULL) {
                this.warn("Duplicate handler for item: " + n + "(" + itemTemplate.getHandler().getClass().getSimpleName() + "," + iItemHandler.getClass().getSimpleName() + ")");
                continue;
            }
            itemTemplate.setHandler(iItemHandler);
        }
    }

    public void unregisterItemHandler(IItemHandler iItemHandler) {
        int[] nArray;
        for (int n : nArray = iItemHandler.getItemIds()) {
            ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
            if (itemTemplate == null) {
                this.warn("Item not found: " + n + " handler: " + iItemHandler.getClass().getSimpleName());
                continue;
            }
            if (itemTemplate.getHandler() != iItemHandler) {
                this.warn("Attempt to unregister item handler");
                continue;
            }
            itemTemplate.setHandler(iItemHandler);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
    }
}

