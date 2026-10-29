/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.util.HashMap;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.templates.item.ItemTemplate;

public final class ItemHolder
extends AbstractHolder {
    private static final ItemHolder wi = new ItemHolder();
    private final TIntObjectHashMap<ItemTemplate> wj = new TIntObjectHashMap();
    private ItemTemplate[] wk;

    public static ItemHolder getInstance() {
        return wi;
    }

    private ItemHolder() {
    }

    public void addItem(ItemTemplate itemTemplate) {
        this.wj.put(itemTemplate.getItemId(), itemTemplate);
    }

    private void bk() {
        int n = 0;
        for (int n2 : this.wj.keys()) {
            if (n2 <= n) continue;
            n = n2;
        }
        this.wk = new ItemTemplate[n + 1];
        Object object = this.wj.iterator();
        while (((TIntObjectIterator)object).hasNext()) {
            ((TIntObjectIterator)object).advance();
            this.wk[((TIntObjectIterator)object).key()] = (ItemTemplate)((TIntObjectIterator)object).value();
        }
    }

    public ItemTemplate getTemplate(int n) {
        ItemTemplate itemTemplate = ArrayUtils.valid(this.wk, n);
        if (itemTemplate == null) {
            this.warn("Not defined item id : " + n + ", or out of range!", new Exception());
            return null;
        }
        return this.wk[n];
    }

    public ItemTemplate[] getAllTemplates() {
        return this.wk;
    }

    private void bl() {
        HashMap<ItemTemplate.Grade, Long> hashMap = new HashMap<ItemTemplate.Grade, Long>();
        for (ItemTemplate.Grade grade : ItemTemplate.Grade.values()) {
            if (grade.cry <= 0) continue;
            ItemTemplate itemTemplate = this.getTemplate(grade.cry);
            hashMap.put(grade, Long.valueOf(itemTemplate.getReferencePrice()));
        }
        TIntObjectIterator<ItemTemplate> tIntObjectIterator = this.wj.iterator();
        while (tIntObjectIterator.hasNext()) {
            tIntObjectIterator.advance();
            ItemTemplate itemTemplate = (ItemTemplate)tIntObjectIterator.value();
            if (itemTemplate == null) continue;
            int n = itemTemplate.getCrystalCount();
            long l = itemTemplate.getReferencePrice();
            ItemTemplate.Grade grade = itemTemplate.getCrystalType();
            Long l2 = (Long)hashMap.get((Object)grade);
            if (l2 == null || grade.cry == itemTemplate.getItemId() || n == 0 || l == 0L) continue;
            long l3 = (long)n * l2;
            if (l2 <= l) continue;
            this.warn("Reference price (" + l + ") of item \"" + itemTemplate.getItemId() + "\" lower than crystal price (" + l3 + ")");
        }
    }

    private void bm() {
        this.bl();
    }

    @Override
    protected void process() {
        this.bk();
        this.bm();
    }

    @Override
    public int size() {
        return this.wj.size();
    }

    @Override
    public void clear() {
        this.wj.clear();
    }
}

