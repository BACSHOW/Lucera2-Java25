/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.item.support.EnchantItem;
import l2.gameserver.templates.item.support.EnchantScroll;
import org.napile.primitive.sets.impl.HashIntSet;

public class EnchantItemHolder
extends AbstractHolder {
    private static EnchantItemHolder vR = new EnchantItemHolder();
    private Map<Integer, EnchantItem> vS = new HashMap<Integer, EnchantItem>();

    public static EnchantItemHolder getInstance() {
        return vR;
    }

    private EnchantItemHolder() {
    }

    @Override
    public void log() {
        this.info("load " + this.vS.size() + " enchant item(s).");
    }

    public EnchantItem getEnchantItem(int n) {
        return this.vS.get(n);
    }

    public EnchantScroll getEnchantScroll(int n) {
        EnchantItem enchantItem = this.getEnchantItem(n);
        if (enchantItem != null && enchantItem instanceof EnchantScroll) {
            return (EnchantScroll)enchantItem;
        }
        return null;
    }

    public void addEnchantItem(EnchantItem enchantItem) {
        this.vS.put(enchantItem.getItemId(), enchantItem);
    }

    public int[] getScrollIds() {
        HashIntSet hashIntSet = new HashIntSet();
        for (EnchantItem enchantItem : this.vS.values()) {
            if (!(enchantItem instanceof EnchantScroll)) continue;
            hashIntSet.add(enchantItem.getItemId());
        }
        return hashIntSet.toArray(new int[hashIntSet.size()]);
    }

    @Override
    public int size() {
        return this.vS.size();
    }

    @Override
    public void clear() {
        this.vS.clear();
    }
}

