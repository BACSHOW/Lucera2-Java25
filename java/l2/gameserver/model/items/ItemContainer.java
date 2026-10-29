/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.commons.math.SafeMath;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ItemContainer {
    private static final Logger _log = LoggerFactory.getLogger(ItemContainer.class);
    protected static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();
    protected final List<ItemInstance> _items = new ArrayList<ItemInstance>();
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected final Lock readLock = this.lock.readLock();
    protected final Lock writeLock = this.lock.writeLock();

    protected ItemContainer() {
    }

    public int getSize() {
        return this._items.size();
    }

    public ItemInstance[] getItems() {
        this.readLock();
        try {
            ItemInstance[] itemInstanceArray = this._items.toArray(new ItemInstance[this._items.size()]);
            return itemInstanceArray;
        }
        finally {
            this.readUnlock();
        }
    }

    public void clear() {
        this.writeLock();
        try {
            this._items.clear();
        }
        finally {
            this.writeUnlock();
        }
    }

    public final void writeLock() {
        this.writeLock.lock();
    }

    public final void writeUnlock() {
        this.writeLock.unlock();
    }

    public final void readLock() {
        this.readLock.lock();
    }

    public final void readUnlock() {
        this.readLock.unlock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance getItemByObjectId(int n) {
        this.readLock();
        try {
            for (int i = 0; i < this._items.size(); ++i) {
                ItemInstance itemInstance = this._items.get(i);
                if (itemInstance.getObjectId() != n) continue;
                ItemInstance itemInstance2 = itemInstance;
                return itemInstance2;
            }
        }
        finally {
            this.readUnlock();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance getItemByItemId(int n) {
        this.readLock();
        try {
            for (int i = 0; i < this._items.size(); ++i) {
                ItemInstance itemInstance = this._items.get(i);
                if (itemInstance.getItemId() != n) continue;
                ItemInstance itemInstance2 = itemInstance;
                return itemInstance2;
            }
        }
        finally {
            this.readUnlock();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<ItemInstance> getItemsByItemId(int n) {
        ArrayList<ItemInstance> arrayList = new ArrayList<ItemInstance>();
        this.readLock();
        try {
            for (int i = 0; i < this._items.size(); ++i) {
                ItemInstance itemInstance = this._items.get(i);
                if (itemInstance.getItemId() != n) continue;
                arrayList.add(itemInstance);
            }
        }
        finally {
            this.readUnlock();
        }
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getCountOf(int n) {
        long l = 0L;
        this.readLock();
        try {
            for (int i = 0; i < this._items.size(); ++i) {
                ItemInstance itemInstance = this._items.get(i);
                if (itemInstance.getItemId() != n) continue;
                l = SafeMath.addAndLimit(l, itemInstance.getCount());
            }
        }
        finally {
            this.readUnlock();
        }
        return l;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance addItem(int n, long l) {
        ItemInstance itemInstance;
        block8: {
            if (l < 1L) {
                return null;
            }
            this.writeLock();
            try {
                itemInstance = this.getItemByItemId(n);
                if (itemInstance != null && itemInstance.isStackable()) {
                    ItemInstance itemInstance2 = itemInstance;
                    synchronized (itemInstance2) {
                        itemInstance.setCount(SafeMath.addAndLimit(itemInstance.getCount(), l));
                        this.onModifyItem(itemInstance);
                        break block8;
                    }
                }
                itemInstance = ItemFunctions.createItem(n);
                itemInstance.setCount(l);
                this._items.add(itemInstance);
                this.onAddItem(itemInstance);
            }
            finally {
                this.writeUnlock();
            }
        }
        return itemInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance addItem(ItemInstance itemInstance) {
        if (itemInstance == null) {
            return null;
        }
        if (itemInstance.getCount() < 1L) {
            return null;
        }
        ItemInstance itemInstance2 = null;
        this.writeLock();
        try {
            int n;
            if (this.getItemByObjectId(itemInstance.getObjectId()) != null) {
                ItemInstance itemInstance3 = null;
                return itemInstance3;
            }
            if (itemInstance.isStackable() && (itemInstance2 = this.getItemByItemId(n = itemInstance.getItemId())) != null) {
                ItemInstance itemInstance4 = itemInstance2;
                synchronized (itemInstance4) {
                    itemInstance2.setCount(SafeMath.addAndLimit(itemInstance.getCount(), itemInstance2.getCount()));
                    this.onModifyItem(itemInstance2);
                    this.onDestroyItem(itemInstance);
                }
            }
            if (itemInstance2 == null) {
                this._items.add(itemInstance);
                itemInstance2 = itemInstance;
                this.onAddItem(itemInstance2);
            }
        }
        finally {
            this.writeUnlock();
        }
        return itemInstance2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance removeItemByObjectId(int n, long l) {
        ItemInstance itemInstance;
        if (l < 1L) {
            return null;
        }
        this.writeLock();
        try {
            ItemInstance itemInstance2 = this.getItemByObjectId(n);
            if (itemInstance2 == null) {
                ItemInstance itemInstance3 = null;
                return itemInstance3;
            }
            ItemInstance itemInstance4 = itemInstance2;
            synchronized (itemInstance4) {
                itemInstance = this.removeItem(itemInstance2, l);
            }
        }
        finally {
            this.writeUnlock();
        }
        return itemInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance removeItemByItemId(int n, long l) {
        ItemInstance itemInstance;
        if (l < 1L) {
            return null;
        }
        this.writeLock();
        try {
            ItemInstance itemInstance2 = this.getItemByItemId(n);
            if (itemInstance2 == null) {
                ItemInstance itemInstance3 = null;
                return itemInstance3;
            }
            ItemInstance itemInstance4 = itemInstance2;
            synchronized (itemInstance4) {
                itemInstance = this.removeItem(itemInstance2, l);
            }
        }
        finally {
            this.writeUnlock();
        }
        return itemInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance removeItem(ItemInstance itemInstance, long l) {
        if (itemInstance == null) {
            return null;
        }
        if (l < 1L) {
            return null;
        }
        if (itemInstance.getCount() < l) {
            return null;
        }
        this.writeLock();
        try {
            if (!this._items.contains(itemInstance)) {
                ItemInstance itemInstance2 = null;
                return itemInstance2;
            }
            if (itemInstance.getCount() > l) {
                itemInstance.setCount(itemInstance.getCount() - l);
                this.onModifyItem(itemInstance);
                ItemInstance itemInstance3 = new ItemInstance(IdFactory.getInstance().getNextId(), itemInstance.getItemId());
                itemInstance3.setCount(l);
                ItemInstance itemInstance4 = itemInstance3;
                return itemInstance4;
            }
            ItemInstance itemInstance5 = this.removeItem(itemInstance);
            return itemInstance5;
        }
        finally {
            this.writeUnlock();
        }
    }

    public ItemInstance removeItem(ItemInstance itemInstance) {
        if (itemInstance == null) {
            return null;
        }
        this.writeLock();
        try {
            if (!this._items.remove(itemInstance)) {
                ItemInstance itemInstance2 = null;
                return itemInstance2;
            }
            this.onRemoveItem(itemInstance);
            ItemInstance itemInstance3 = itemInstance;
            return itemInstance3;
        }
        finally {
            this.writeUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean destroyItemByObjectId(int n, long l) {
        this.writeLock();
        try {
            ItemInstance itemInstance = this.getItemByObjectId(n);
            if (itemInstance == null) {
                boolean bl = false;
                return bl;
            }
            ItemInstance itemInstance2 = itemInstance;
            synchronized (itemInstance2) {
                boolean bl = this.destroyItem(itemInstance, l);
                return bl;
            }
        }
        finally {
            this.writeUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean destroyItemByItemId(int n, long l) {
        this.writeLock();
        try {
            ItemInstance itemInstance = this.getItemByItemId(n);
            if (itemInstance == null) {
                boolean bl = false;
                return bl;
            }
            ItemInstance itemInstance2 = itemInstance;
            synchronized (itemInstance2) {
                boolean bl = this.destroyItem(itemInstance, l);
                return bl;
            }
        }
        finally {
            this.writeUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean destroyItem(ItemInstance itemInstance, long l) {
        if (itemInstance == null) {
            return false;
        }
        if (l < 1L) {
            return false;
        }
        if (itemInstance.getCount() < l) {
            return false;
        }
        this.writeLock();
        try {
            if (!this._items.contains(itemInstance)) {
                boolean bl = false;
                return bl;
            }
            if (itemInstance.getCount() > l) {
                itemInstance.setCount(itemInstance.getCount() - l);
                this.onModifyItem(itemInstance);
                boolean bl = true;
                return bl;
            }
            boolean bl = this.destroyItem(itemInstance);
            return bl;
        }
        finally {
            this.writeUnlock();
        }
    }

    public boolean destroyItem(ItemInstance itemInstance) {
        if (itemInstance == null) {
            return false;
        }
        this.writeLock();
        try {
            if (!this._items.remove(itemInstance)) {
                boolean bl = false;
                return bl;
            }
            this.onRemoveItem(itemInstance);
            this.onDestroyItem(itemInstance);
            boolean bl = true;
            return bl;
        }
        finally {
            this.writeUnlock();
        }
    }

    protected abstract void onAddItem(ItemInstance var1);

    protected abstract void onModifyItem(ItemInstance var1);

    protected abstract void onRemoveItem(ItemInstance var1);

    protected abstract void onDestroyItem(ItemInstance var1);
}

