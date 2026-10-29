/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import java.util.Comparator;
import l2.commons.listener.ListenerList;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.listener.inventory.OnDisplayListener;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemContainer;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.listeners.StatsListener;
import l2.gameserver.templates.item.EtcItemTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Inventory
extends ItemContainer {
    private static final Logger _log = LoggerFactory.getLogger(Inventory.class);
    public static final int PAPERDOLL_UNDER = 0;
    public static final int PAPERDOLL_REAR = 1;
    public static final int PAPERDOLL_LEAR = 2;
    public static final int PAPERDOLL_NECK = 3;
    public static final int PAPERDOLL_RFINGER = 4;
    public static final int PAPERDOLL_LFINGER = 5;
    public static final int PAPERDOLL_HEAD = 6;
    public static final int PAPERDOLL_RHAND = 7;
    public static final int PAPERDOLL_LHAND = 8;
    public static final int PAPERDOLL_GLOVES = 9;
    public static final int PAPERDOLL_CHEST = 10;
    public static final int PAPERDOLL_LEGS = 11;
    public static final int PAPERDOLL_FEET = 12;
    public static final int PAPERDOLL_BACK = 13;
    public static final int PAPERDOLL_LRHAND = 14;
    public static final int PAPERDOLL_HAIR = 15;
    public static final int PAPERDOLL_DHAIR = 16;
    public static final int PAPERDOLL_MAX = 17;
    public static final int[] PAPERDOLL_ORDER = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 7, 15, 16};
    protected final int _ownerId;
    protected final ItemInstance[] _paperdoll = new ItemInstance[17];
    protected final InventoryListenerList _listeners = new InventoryListenerList();
    protected OnDisplayListener _onDisplayListener;
    protected int _totalWeight;
    protected long _wearedMask;

    protected Inventory(int n) {
        this._ownerId = n;
        this.addListener(StatsListener.getInstance());
    }

    public abstract Playable getActor();

    protected abstract ItemInstance.ItemLocation getBaseLocation();

    protected abstract ItemInstance.ItemLocation getEquipLocation();

    public int getOwnerId() {
        return this._ownerId;
    }

    protected void onRestoreItem(ItemInstance itemInstance) {
        this._totalWeight = (int)((long)this._totalWeight + (long)itemInstance.getTemplate().getWeight() * itemInstance.getCount());
    }

    @Override
    protected void onAddItem(ItemInstance itemInstance) {
        itemInstance.setOwnerId(this.getOwnerId());
        itemInstance.setLocation(this.getBaseLocation());
        itemInstance.setLocData(this.dg());
        this.sendAddItem(itemInstance);
        this.refreshWeight();
        itemInstance.save();
    }

    @Override
    protected void onModifyItem(ItemInstance itemInstance) {
        this.sendModifyItem(itemInstance);
        this.refreshWeight();
    }

    @Override
    protected void onRemoveItem(ItemInstance itemInstance) {
        if (itemInstance.isEquipped()) {
            this.unEquipItem(itemInstance);
        }
        this.sendRemoveItem(itemInstance);
        itemInstance.setLocData(-1);
        itemInstance.save();
        this.refreshWeight();
    }

    @Override
    protected void onDestroyItem(ItemInstance itemInstance) {
        itemInstance.setCount(0L);
        itemInstance.delete();
    }

    protected void onEquip(int n, ItemInstance itemInstance) {
        this._listeners.onEquip(n, itemInstance);
        itemInstance.setLocation(this.getEquipLocation());
        itemInstance.setLocData(n);
        itemInstance.setEquipped(true);
        this.sendModifyItem(itemInstance);
        this._wearedMask |= itemInstance.getTemplate().getItemMask();
        itemInstance.save();
    }

    protected void onUnequip(int n, ItemInstance itemInstance) {
        itemInstance.setLocation(this.getBaseLocation());
        itemInstance.setLocData(this.dg());
        itemInstance.setEquipped(false);
        itemInstance.setChargedSpiritshot(0);
        itemInstance.setChargedSoulshot(0);
        this.sendModifyItem(itemInstance);
        this._wearedMask &= itemInstance.getTemplate().getItemMask() ^ 0xFFFFFFFFFFFFFFFFL;
        this._listeners.onUnequip(n, itemInstance);
        itemInstance.save();
    }

    private int dg() {
        int n = 0;
        block0: for (n = 0; n < this._items.size(); ++n) {
            for (int i = 0; i < this._items.size(); ++i) {
                ItemInstance itemInstance = (ItemInstance)this._items.get(i);
                if (!itemInstance.isEquipped() && !itemInstance.getTemplate().isQuest() && itemInstance.getEquipSlot() == n) continue block0;
            }
        }
        return n;
    }

    public ItemInstance getPaperdollItem(int n) {
        return this._paperdoll[n];
    }

    public ItemInstance[] getPaperdollItems() {
        return this._paperdoll;
    }

    public int getPaperdollBodyPart(int n) {
        ItemInstance itemInstance = this.getPaperdollItem(n);
        if (itemInstance != null) {
            return itemInstance.getBodyPart();
        }
        if (n == 15 && (itemInstance = this._paperdoll[16]) != null) {
            return itemInstance.getBodyPart();
        }
        return 0;
    }

    public int getPaperdollItemId(int n) {
        Integer n2;
        ItemInstance itemInstance = this.getPaperdollItem(n);
        if (itemInstance != null) {
            Integer n3;
            if (this.getOnDisplayListener() != null && (n3 = this.getOnDisplayListener().onDisplay(n, itemInstance, this.getActor())) != null) {
                return n3;
            }
            return itemInstance.getVisibleItemId();
        }
        if (n == 15 && (itemInstance = this._paperdoll[16]) != null) {
            return itemInstance.getVisibleItemId();
        }
        if (this.getOnDisplayListener() != null && (n2 = this.getOnDisplayListener().onDisplay(n, null, this.getActor())) != null) {
            return n2;
        }
        return 0;
    }

    public int getPaperdollObjectId(int n) {
        ItemInstance itemInstance = this._paperdoll[n];
        if (itemInstance != null) {
            return itemInstance.getObjectId();
        }
        if (n == 15 && (itemInstance = this._paperdoll[16]) != null) {
            return itemInstance.getObjectId();
        }
        return 0;
    }

    public void addListener(OnEquipListener onEquipListener) {
        this._listeners.add(onEquipListener);
    }

    public void removeListener(OnEquipListener onEquipListener) {
        this._listeners.remove(onEquipListener);
    }

    public InventoryListenerList getListeners() {
        return this._listeners;
    }

    public OnDisplayListener getOnDisplayListener() {
        return this._onDisplayListener;
    }

    public void setOnDisplayListener(OnDisplayListener onDisplayListener) {
        this._onDisplayListener = onDisplayListener;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance setPaperdollItem(int n, ItemInstance itemInstance) {
        ItemInstance itemInstance2;
        this.writeLock();
        try {
            itemInstance2 = this._paperdoll[n];
            if (itemInstance2 != itemInstance) {
                if (itemInstance2 != null) {
                    this._paperdoll[n] = null;
                    this.onUnequip(n, itemInstance2);
                }
                if (itemInstance != null) {
                    this._paperdoll[n] = itemInstance;
                    this.onEquip(n, itemInstance);
                }
            }
        }
        finally {
            this.writeUnlock();
        }
        return itemInstance2;
    }

    public long getWearedMask() {
        return this._wearedMask;
    }

    public void unEquipItem(ItemInstance itemInstance) {
        if (itemInstance.isEquipped()) {
            this.a(itemInstance.getBodyPart(), itemInstance);
        }
    }

    public void unEquipItemInBodySlot(int n) {
        this.a(n, null);
    }

    private void a(int n, ItemInstance itemInstance) {
        int n2 = -1;
        switch (n) {
            case 8: {
                n2 = 3;
                break;
            }
            case 4: {
                n2 = 2;
                break;
            }
            case 2: {
                n2 = 1;
                break;
            }
            case 6: {
                if (itemInstance == null) {
                    return;
                }
                if (this.getPaperdollItem(2) == itemInstance) {
                    n2 = 2;
                }
                if (this.getPaperdollItem(1) != itemInstance) break;
                n2 = 1;
                break;
            }
            case 32: {
                n2 = 5;
                break;
            }
            case 16: {
                n2 = 4;
                break;
            }
            case 48: {
                if (itemInstance == null) {
                    return;
                }
                if (this.getPaperdollItem(5) == itemInstance) {
                    n2 = 5;
                }
                if (this.getPaperdollItem(4) != itemInstance) break;
                n2 = 4;
                break;
            }
            case 65536: {
                n2 = 15;
                break;
            }
            case 262144: {
                n2 = 16;
                break;
            }
            case 524288: {
                this.setPaperdollItem(16, null);
                n2 = 15;
                break;
            }
            case 64: {
                n2 = 6;
                break;
            }
            case 128: {
                n2 = 7;
                break;
            }
            case 256: {
                n2 = 8;
                break;
            }
            case 512: {
                n2 = 9;
                break;
            }
            case 2048: {
                n2 = 11;
                break;
            }
            case 1024: 
            case 32768: 
            case 131072: {
                n2 = 10;
                break;
            }
            case 8192: {
                n2 = 13;
                break;
            }
            case 4096: {
                n2 = 12;
                break;
            }
            case 1: {
                n2 = 0;
                break;
            }
            case 16384: {
                this.setPaperdollItem(8, null);
                n2 = 7;
                break;
            }
            default: {
                _log.warn("Requested invalid body slot: " + n + ", Item: " + itemInstance + ", ownerId: '" + this.getOwnerId() + "'");
                return;
            }
        }
        if (n2 >= 0) {
            this.setPaperdollItem(n2, null);
        }
    }

    public void equipItem(ItemInstance itemInstance) {
        int n = itemInstance.getBodyPart();
        double d = this.getActor().getCurrentHp();
        double d2 = this.getActor().getCurrentMp();
        double d3 = this.getActor().getCurrentCp();
        switch (n) {
            case 16384: {
                this.setPaperdollItem(8, null);
                this.setPaperdollItem(7, itemInstance);
                break;
            }
            case 256: {
                ItemInstance itemInstance2 = this.getPaperdollItem(7);
                ItemTemplate itemTemplate = itemInstance2 == null ? null : itemInstance2.getTemplate();
                ItemTemplate itemTemplate2 = itemInstance.getTemplate();
                if (itemTemplate2.getItemType() == EtcItemTemplate.EtcItemType.ARROW) {
                    if (itemTemplate == null) {
                        return;
                    }
                    if (itemTemplate.getItemType() != WeaponTemplate.WeaponType.BOW) {
                        return;
                    }
                    if (itemTemplate.getCrystalType() != itemTemplate2.getCrystalType()) {
                        return;
                    }
                } else if (itemTemplate2.getItemType() == EtcItemTemplate.EtcItemType.BAIT) {
                    if (itemTemplate == null) {
                        return;
                    }
                    if (itemTemplate.getItemType() != WeaponTemplate.WeaponType.ROD) {
                        return;
                    }
                    if (!this.getActor().isPlayer()) {
                        return;
                    }
                    Player player = (Player)this.getActor();
                    player.setVar("LastLure", String.valueOf(itemInstance.getObjectId()), -1L);
                } else if (itemTemplate != null && itemTemplate.getBodyPart() == 16384) {
                    this.setPaperdollItem(7, null);
                }
                this.setPaperdollItem(8, itemInstance);
                break;
            }
            case 128: {
                this.setPaperdollItem(7, itemInstance);
                break;
            }
            case 2: 
            case 4: 
            case 6: {
                if (this._paperdoll[1] == null) {
                    this.setPaperdollItem(1, itemInstance);
                    break;
                }
                if (this._paperdoll[2] == null) {
                    this.setPaperdollItem(2, itemInstance);
                    break;
                }
                this.setPaperdollItem(2, itemInstance);
                break;
            }
            case 16: 
            case 32: 
            case 48: {
                if (this._paperdoll[4] == null) {
                    this.setPaperdollItem(4, itemInstance);
                    break;
                }
                if (this._paperdoll[5] == null) {
                    this.setPaperdollItem(5, itemInstance);
                    break;
                }
                this.setPaperdollItem(5, itemInstance);
                break;
            }
            case 8: {
                this.setPaperdollItem(3, itemInstance);
                break;
            }
            case 32768: {
                this.setPaperdollItem(11, null);
                this.setPaperdollItem(10, itemInstance);
                break;
            }
            case 1024: {
                this.setPaperdollItem(10, itemInstance);
                break;
            }
            case 2048: {
                ItemInstance itemInstance3 = this.getPaperdollItem(10);
                if (itemInstance3 != null && itemInstance3.getBodyPart() == 32768 || this.getPaperdollBodyPart(10) == 131072) {
                    this.setPaperdollItem(10, null);
                }
                this.setPaperdollItem(11, itemInstance);
                break;
            }
            case 4096: {
                if (this.getPaperdollBodyPart(10) == 131072) {
                    this.setPaperdollItem(10, null);
                }
                this.setPaperdollItem(12, itemInstance);
                break;
            }
            case 512: {
                if (this.getPaperdollBodyPart(10) == 131072) {
                    this.setPaperdollItem(10, null);
                }
                this.setPaperdollItem(9, itemInstance);
                break;
            }
            case 64: {
                if (this.getPaperdollBodyPart(10) == 131072) {
                    this.setPaperdollItem(10, null);
                }
                this.setPaperdollItem(6, itemInstance);
                break;
            }
            case 65536: {
                ItemInstance itemInstance4 = this.getPaperdollItem(16);
                if (itemInstance4 != null && itemInstance4.getBodyPart() == 524288) {
                    this.setPaperdollItem(16, null);
                }
                this.setPaperdollItem(15, itemInstance);
                break;
            }
            case 262144: {
                ItemInstance itemInstance5 = this.getPaperdollItem(16);
                if (itemInstance5 != null && itemInstance5.getBodyPart() == 524288) {
                    this.setPaperdollItem(15, null);
                }
                this.setPaperdollItem(16, itemInstance);
                break;
            }
            case 524288: {
                this.setPaperdollItem(15, null);
                this.setPaperdollItem(16, itemInstance);
                break;
            }
            case 1: {
                this.setPaperdollItem(0, itemInstance);
                break;
            }
            case 8192: {
                this.setPaperdollItem(13, itemInstance);
                break;
            }
            case 131072: {
                this.setPaperdollItem(11, null);
                this.setPaperdollItem(6, null);
                this.setPaperdollItem(12, null);
                this.setPaperdollItem(9, null);
                this.setPaperdollItem(10, itemInstance);
                break;
            }
            default: {
                _log.warn("unknown body slot:" + n + " for item id: " + itemInstance.getItemId());
                return;
            }
        }
        this.getActor().setCurrentHp(d, false);
        this.getActor().setCurrentMp(d2);
        this.getActor().setCurrentCp(d3);
        if (this.getActor().isPlayer()) {
            ((Player)this.getActor()).autoShot();
        }
    }

    protected abstract void sendAddItem(ItemInstance var1);

    protected abstract void sendModifyItem(ItemInstance var1);

    protected abstract void sendRemoveItem(ItemInstance var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void refreshWeight() {
        int n = 0;
        this.readLock();
        try {
            for (int i = 0; i < this._items.size(); ++i) {
                ItemInstance itemInstance = (ItemInstance)this._items.get(i);
                n = (int)((long)n + (long)itemInstance.getTemplate().getWeight() * itemInstance.getCount());
            }
        }
        finally {
            this.readUnlock();
        }
        if (this._totalWeight == n) {
            return;
        }
        this._totalWeight = n;
        this.onRefreshWeight();
    }

    protected abstract void onRefreshWeight();

    public int getTotalWeight() {
        return this._totalWeight;
    }

    public boolean validateCapacity(ItemInstance itemInstance) {
        long l = 0L;
        if (!itemInstance.isStackable() || this.getItemByItemId(itemInstance.getItemId()) == null) {
            ++l;
        }
        return this.validateCapacity(l);
    }

    public boolean validateCapacity(int n, long l) {
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
        return this.validateCapacity(itemTemplate, l);
    }

    public boolean validateCapacity(ItemTemplate itemTemplate, long l) {
        long l2 = 0L;
        if (!itemTemplate.isStackable() || this.getItemByItemId(itemTemplate.getItemId()) == null) {
            l2 = l;
        }
        return this.validateCapacity(l2);
    }

    public boolean validateCapacity(long l) {
        if (l == 0L) {
            return true;
        }
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            return false;
        }
        if (this.getSize() + (int)l < 0) {
            return false;
        }
        return (long)this.getSize() + l <= (long)this.getActor().getInventoryLimit();
    }

    public boolean validateWeight(ItemInstance itemInstance) {
        long l = (long)itemInstance.getTemplate().getWeight() * itemInstance.getCount();
        return this.validateWeight(l);
    }

    public boolean validateWeight(int n, long l) {
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
        return this.validateWeight(itemTemplate, l);
    }

    public boolean validateWeight(ItemTemplate itemTemplate, long l) {
        long l2 = (long)itemTemplate.getWeight() * l;
        return this.validateWeight(l2);
    }

    public boolean validateWeight(long l) {
        if (l == 0L) {
            return true;
        }
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            return false;
        }
        if (this.getTotalWeight() + (int)l < 0) {
            return false;
        }
        return (long)this.getTotalWeight() + l <= (long)this.getActor().getMaxLoad();
    }

    public abstract void restore();

    public abstract void store();

    public static int getPaperdollIndex(int n) {
        switch (n) {
            case 1: {
                return 0;
            }
            case 2: {
                return 1;
            }
            case 4: {
                return 2;
            }
            case 8: {
                return 3;
            }
            case 16: {
                return 4;
            }
            case 32: {
                return 5;
            }
            case 64: {
                return 6;
            }
            case 128: {
                return 7;
            }
            case 256: {
                return 8;
            }
            case 16384: {
                return 14;
            }
            case 512: {
                return 9;
            }
            case 1024: 
            case 32768: 
            case 131072: {
                return 10;
            }
            case 2048: {
                return 11;
            }
            case 4096: {
                return 12;
            }
            case 8192: {
                return 13;
            }
            case 65536: 
            case 524288: {
                return 15;
            }
            case 262144: {
                return 16;
            }
        }
        return -1;
    }

    @Override
    public int getSize() {
        return super.getSize();
    }

    public static class ItemOrderComparator
    implements Comparator<ItemInstance> {
        private static final Comparator<ItemInstance> tS = new ItemOrderComparator();

        public static final Comparator<ItemInstance> getInstance() {
            return tS;
        }

        @Override
        public int compare(ItemInstance itemInstance, ItemInstance itemInstance2) {
            if (itemInstance == null || itemInstance2 == null) {
                return 0;
            }
            return itemInstance.getLocData() - itemInstance2.getLocData();
        }
    }

    public class InventoryListenerList
    extends ListenerList<Playable> {
        public void onEquip(int n, ItemInstance itemInstance) {
            this.forEachListener(OnEquipListener.class, onEquipListener -> onEquipListener.onEquip(n, itemInstance, Inventory.this.getActor()));
        }

        public void onUnequip(int n, ItemInstance itemInstance) {
            this.forEachListener(OnEquipListener.class, onEquipListener -> onEquipListener.onUnequip(n, itemInstance, Inventory.this.getActor()));
        }
    }
}

