/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.cache;

import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class ItemInfoCache {
    private static final ItemInfoCache uq = new ItemInfoCache();
    private Cache ur = CacheManager.getInstance().getCache(this.getClass().getName());

    public static final ItemInfoCache getInstance() {
        return uq;
    }

    private ItemInfoCache() {
    }

    public void put(ItemInstance itemInstance) {
        this.ur.put(new Element(itemInstance.getObjectId(), new ItemInfo(itemInstance)));
    }

    public ItemInfo get(int n) {
        Element element = this.ur.get(Integer.valueOf(n));
        ItemInfo itemInfo = null;
        if (element != null) {
            itemInfo = (ItemInfo)element.getObjectValue();
        }
        Player player = null;
        if (itemInfo != null) {
            player = World.getPlayer(itemInfo.getOwnerId());
            ItemInstance itemInstance = null;
            if (player != null) {
                itemInstance = player.getInventory().getItemByObjectId(n);
            }
            if (itemInstance != null && itemInstance.getItemId() == itemInfo.getItemId()) {
                itemInfo = new ItemInfo(itemInstance);
                this.ur.put(new Element(itemInstance.getObjectId(), itemInfo));
            }
        }
        return itemInfo;
    }
}

