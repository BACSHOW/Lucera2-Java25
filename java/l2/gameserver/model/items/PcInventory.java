/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import java.util.Collection;
import l2.commons.collections.CollectionUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;
import l2.gameserver.model.items.listeners.AccessoryListener;
import l2.gameserver.model.items.listeners.ArmorSetListener;
import l2.gameserver.model.items.listeners.BowListener;
import l2.gameserver.model.items.listeners.ItemAugmentationListener;
import l2.gameserver.model.items.listeners.ItemEnchantOptionsListener;
import l2.gameserver.model.items.listeners.ItemSkillsListener;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.templates.item.EtcItemTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import org.apache.commons.lang3.ArrayUtils;

public class PcInventory
extends Inventory {
    private final Player LF;
    private LockType XG = LockType.NONE;
    private int[] XH = ArrayUtils.EMPTY_INT_ARRAY;
    public boolean isRefresh = false;
    private static final int[][] XI = new int[][]{{17}, {1341, 22067}, {1342, 22068}, {1343, 22069}, {1344, 22070}, {1345, 22071}};

    public PcInventory(Player player) {
        super(player.getObjectId());
        this.LF = player;
        this.addListener(ItemSkillsListener.getInstance());
        this.addListener(ItemAugmentationListener.getInstance());
        this.addListener(ItemEnchantOptionsListener.getInstance());
        this.addListener(ArmorSetListener.getInstance());
        this.addListener(BowListener.getInstance());
        this.addListener(AccessoryListener.getInstance());
    }

    @Override
    public Player getActor() {
        return this.LF;
    }

    @Override
    protected ItemInstance.ItemLocation getBaseLocation() {
        return ItemInstance.ItemLocation.INVENTORY;
    }

    @Override
    protected ItemInstance.ItemLocation getEquipLocation() {
        return ItemInstance.ItemLocation.PAPERDOLL;
    }

    public long getAdena() {
        ItemInstance itemInstance = this.getItemByItemId(57);
        if (itemInstance == null) {
            return 0L;
        }
        return itemInstance.getCount();
    }

    public ItemInstance addAdena(long l) {
        return this.addItem(57, l);
    }

    public boolean reduceAdena(long l) {
        return this.destroyItemByItemId(57, l);
    }

    public int getPaperdollAugmentationId(int n) {
        ItemInstance itemInstance = this._paperdoll[n];
        if (itemInstance != null && itemInstance.isAugmented()) {
            return itemInstance.getVariationStat1() & 0xFFFF | itemInstance.getVariationStat2() << 16;
        }
        return 0;
    }

    @Override
    protected void onRefreshWeight() {
        this.getActor().refreshOverloaded();
    }

    public void validateItems() {
        for (ItemInstance itemInstance : this._paperdoll) {
            if (itemInstance == null || ItemFunctions.checkIfCanEquip(this.getActor(), itemInstance) == null && itemInstance.getTemplate().testCondition(this.getActor(), itemInstance, true)) continue;
            this.unEquipItem(itemInstance);
            this.getActor().sendDisarmMessage(itemInstance);
        }
    }

    public void validateItemsSkills() {
        for (ItemInstance itemInstance : this._paperdoll) {
            boolean bl;
            boolean bl2;
            if (itemInstance == null || itemInstance.getTemplate().getType2() != 0) continue;
            boolean bl3 = bl2 = this.getActor().getGradePenalty() > 0;
            if (itemInstance.getTemplate().getAttachedSkills().length > 0) {
                boolean bl4 = bl = this.getActor().getSkillLevel(itemInstance.getTemplate().getAttachedSkills()[0].getId()) > 0;
                if (bl2 && bl) {
                    ItemSkillsListener.getInstance().onUnequip(itemInstance.getEquipSlot(), itemInstance, this.getActor());
                    continue;
                }
                if (bl2 || bl) continue;
                ItemSkillsListener.getInstance().onEquip(itemInstance.getEquipSlot(), itemInstance, this.getActor());
                continue;
            }
            if (itemInstance.getTemplate().getEnchant4Skill() != null) {
                boolean bl5 = bl = this.getActor().getSkillLevel(itemInstance.getTemplate().getEnchant4Skill().getId()) > 0;
                if (bl2 && bl) {
                    ItemSkillsListener.getInstance().onUnequip(itemInstance.getEquipSlot(), itemInstance, this.getActor());
                    continue;
                }
                if (bl2 || bl) continue;
                ItemSkillsListener.getInstance().onEquip(itemInstance.getEquipSlot(), itemInstance, this.getActor());
                continue;
            }
            if (itemInstance.getTemplate().getTriggerList().isEmpty()) continue;
            if (bl2) {
                ItemSkillsListener.getInstance().onUnequip(itemInstance.getEquipSlot(), itemInstance, this.getActor());
                continue;
            }
            ItemSkillsListener.getInstance().onEquip(itemInstance.getEquipSlot(), itemInstance, this.getActor());
        }
    }

    public void refreshEquip() {
        this.isRefresh = true;
        for (ItemInstance itemInstance : this.getItems()) {
            if (itemInstance.isEquipped()) {
                int n = itemInstance.getEquipSlot();
                this._listeners.onUnequip(n, itemInstance);
                this._listeners.onEquip(n, itemInstance);
                continue;
            }
            if (itemInstance.getItemType() != EtcItemTemplate.EtcItemType.RUNE) continue;
            this._listeners.onUnequip(-1, itemInstance);
            this._listeners.onEquip(-1, itemInstance);
        }
        this.isRefresh = false;
    }

    public void sort(int[][] nArray) {
        boolean bl = false;
        for (int[] nArray2 : nArray) {
            ItemInstance itemInstance = this.getItemByObjectId(nArray2[0]);
            if (itemInstance == null || itemInstance.getLocation() != ItemInstance.ItemLocation.INVENTORY || itemInstance.getLocData() == nArray2[1]) continue;
            itemInstance.setLocData(nArray2[1]);
            bl = true;
        }
        if (bl) {
            CollectionUtils.eqSort(this._items, Inventory.ItemOrderComparator.getInstance());
        }
    }

    public ItemInstance findArrowForBow(ItemTemplate itemTemplate) {
        int[] nArray = XI[itemTemplate.getCrystalType().gradeOrd()];
        ItemInstance itemInstance = null;
        for (int n : nArray) {
            itemInstance = this.getItemByItemId(n);
            if (itemInstance == null) continue;
            return itemInstance;
        }
        return null;
    }

    public ItemInstance findEquippedLure() {
        ItemInstance itemInstance = null;
        int n = 0;
        Player player = this.getActor();
        String string = player.getVar("LastLure");
        if (string != null && !string.isEmpty()) {
            n = Integer.valueOf(string);
        }
        for (ItemInstance itemInstance2 : this.getItems()) {
            if (itemInstance2.getItemType() != EtcItemTemplate.EtcItemType.BAIT) continue;
            if (itemInstance2.getLocation() == ItemInstance.ItemLocation.PAPERDOLL && itemInstance2.getEquipSlot() == 8) {
                return itemInstance2;
            }
            if (n <= 0 || itemInstance != null || itemInstance2.getObjectId() != n) continue;
            itemInstance = itemInstance2;
        }
        return itemInstance;
    }

    public void lockItems(LockType lockType, int[] nArray) {
        this.XG = lockType;
        this.XH = nArray;
        this.getActor().sendItemList(false);
    }

    public void unlock() {
        if (this.XG == LockType.NONE) {
            return;
        }
        this.XG = LockType.NONE;
        this.XH = ArrayUtils.EMPTY_INT_ARRAY;
        this.getActor().sendItemList(false);
    }

    public boolean isLockedItem(ItemInstance itemInstance) {
        switch (this.XG) {
            case INCLUDE: {
                return ArrayUtils.contains(this.XH, itemInstance.getItemId());
            }
            case EXCLUDE: {
                return !ArrayUtils.contains(this.XH, itemInstance.getItemId());
            }
        }
        return false;
    }

    public boolean isLockedItem(int n) {
        switch (this.XG) {
            case INCLUDE: {
                return ArrayUtils.contains(this.XH, n);
            }
            case EXCLUDE: {
                return !ArrayUtils.contains(this.XH, n);
            }
        }
        return false;
    }

    public LockType getLockType() {
        return this.XG;
    }

    public int[] getLockItems() {
        return this.XH;
    }

    @Override
    protected void onRestoreItem(ItemInstance itemInstance) {
        super.onRestoreItem(itemInstance);
        if (itemInstance.getItemType() == EtcItemTemplate.EtcItemType.RUNE) {
            this._listeners.onEquip(-1, itemInstance);
        }
        if (itemInstance.isTemporalItem()) {
            itemInstance.startTimer(new LifeTimeTask(itemInstance));
        }
        if (itemInstance.isCursed() && this.getActor().getNetConnection() != null && this.getActor().getNetConnection().isStateIs(GameClient.GameClientState.IN_GAME)) {
            CursedWeaponsManager.getInstance().checkPlayer(this.getActor(), itemInstance);
        }
    }

    @Override
    protected void onAddItem(ItemInstance itemInstance) {
        super.onAddItem(itemInstance);
        if (itemInstance.getItemType() == EtcItemTemplate.EtcItemType.RUNE) {
            this._listeners.onEquip(-1, itemInstance);
        }
        if (itemInstance.isTemporalItem()) {
            itemInstance.startTimer(new LifeTimeTask(itemInstance));
        }
        if (itemInstance.isCursed()) {
            CursedWeaponsManager.getInstance().checkPlayer(this.getActor(), itemInstance);
        }
    }

    @Override
    protected void onRemoveItem(ItemInstance itemInstance) {
        super.onRemoveItem(itemInstance);
        this.getActor().removeItemFromShortCut(itemInstance.getObjectId());
        if (itemInstance.getItemType() == EtcItemTemplate.EtcItemType.RUNE) {
            this._listeners.onUnequip(-1, itemInstance);
        }
        if (itemInstance.isTemporalItem()) {
            itemInstance.stopTimer();
        }
    }

    @Override
    protected void onEquip(int n, ItemInstance itemInstance) {
        super.onEquip(n, itemInstance);
        if (itemInstance.isShadowItem()) {
            itemInstance.startTimer(new ShadowLifeTimeTask(itemInstance));
        }
    }

    @Override
    protected void onUnequip(int n, ItemInstance itemInstance) {
        super.onUnequip(n, itemInstance);
        if (itemInstance.isShadowItem()) {
            itemInstance.stopTimer();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void restore() {
        int n = this.getOwnerId();
        this.writeLock();
        try {
            Collection<ItemInstance> collection = _itemsDAO.loadItemsByOwnerIdAndLoc(n, this.getBaseLocation());
            for (ItemInstance itemInstance : collection) {
                this._items.add(itemInstance);
                this.onRestoreItem(itemInstance);
            }
            CollectionUtils.eqSort(this._items, Inventory.ItemOrderComparator.getInstance());
            collection = _itemsDAO.loadItemsByOwnerIdAndLoc(n, this.getEquipLocation());
            for (ItemInstance itemInstance : collection) {
                this._items.add(itemInstance);
                this.onRestoreItem(itemInstance);
                if (itemInstance.getEquipSlot() >= 17) {
                    itemInstance.setLocation(this.getBaseLocation());
                    itemInstance.setLocData(0);
                    itemInstance.setEquipped(false);
                    continue;
                }
                this.setPaperdollItem(itemInstance.getEquipSlot(), itemInstance);
            }
        }
        finally {
            this.writeUnlock();
        }
        DelayedItemsManager.getInstance().loadDelayed(this.getActor(), false);
        this.refreshWeight();
    }

    @Override
    public void store() {
        this.writeLock();
        try {
            _itemsDAO.store(this._items);
        }
        finally {
            this.writeUnlock();
        }
    }

    @Override
    protected void sendAddItem(ItemInstance itemInstance) {
        Player player = this.getActor();
        player.sendPacket((IStaticPacket)new InventoryUpdate().addNewItem(itemInstance));
    }

    @Override
    protected void sendModifyItem(ItemInstance itemInstance) {
        Player player = this.getActor();
        player.sendPacket((IStaticPacket)new InventoryUpdate().addModifiedItem(itemInstance));
    }

    @Override
    protected void sendRemoveItem(ItemInstance itemInstance) {
        this.getActor().sendPacket((IStaticPacket)new InventoryUpdate().addRemovedItem(itemInstance));
    }

    public void startTimers() {
    }

    public void stopAllTimers() {
        for (ItemInstance itemInstance : this.getItems()) {
            if (!itemInstance.isShadowItem() && !itemInstance.isTemporalItem()) continue;
            itemInstance.stopTimer();
        }
    }

    protected class LifeTimeTask
    extends RunnableImpl {
        private ItemInstance item;

        LifeTimeTask(ItemInstance itemInstance) {
            this.item = itemInstance;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void runImpl() throws Exception {
            int n;
            Player player = PcInventory.this.getActor();
            ItemInstance itemInstance = this.item;
            synchronized (itemInstance) {
                n = this.item.getPeriod();
                if (n <= 0) {
                    PcInventory.this.destroyItem(this.item);
                }
            }
            if (n <= 0) {
                player.sendPacket((IStaticPacket)new SystemMessage(1726).addItemName(this.item.getItemId()));
            }
        }
    }

    protected class ShadowLifeTimeTask
    extends RunnableImpl {
        private ItemInstance item;

        ShadowLifeTimeTask(ItemInstance itemInstance) {
            this.item = itemInstance;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void runImpl() throws Exception {
            int n;
            Player player = PcInventory.this.getActor();
            if (!this.item.isEquipped()) {
                return;
            }
            Object object = this.item;
            synchronized (object) {
                n = Math.max(0, this.item.getDuration() - 1);
                this.item.setDuration(n);
                if (n == 0) {
                    PcInventory.this.destroyItem(this.item);
                }
            }
            object = null;
            if (n == 10) {
                object = new SystemMessage(1979);
            } else if (n == 5) {
                object = new SystemMessage(1980);
            } else if (n == 1) {
                object = new SystemMessage(1981);
            } else if (n <= 0) {
                object = new SystemMessage(1982);
            } else {
                player.sendPacket((IStaticPacket)new InventoryUpdate().addModifiedItem(this.item));
            }
            if (object != null) {
                ((SystemMessage)object).addItemName(this.item.getItemId());
                player.sendPacket((IStaticPacket)object);
            }
        }
    }
}

