/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.math.SafeMath;
import l2.commons.util.RandomUtils;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.handler.items.ItemHandler;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapsuleItemHolder
extends AbstractHolder {
    private static final Logger LOG = LoggerFactory.getLogger(CapsuleItemHolder.class);
    private static final CapsuleItemHolder vA = new CapsuleItemHolder();
    private final Map<Integer, List<CapsuledItem>> vB = new HashMap<Integer, List<CapsuledItem>>();
    private final Map<Integer, Pair<Integer, Long>> vC = new HashMap<Integer, Pair<Integer, Long>>();
    private CapsuleItemsHandler vD;

    public static CapsuleItemHolder getInstance() {
        return vA;
    }

    public Pair<Integer, Long> getCapsuleRequiredItems(int n) {
        return this.vC.get(n);
    }

    public List<CapsuledItem> getCapsuledItems(int n) {
        return this.vB.get(n);
    }

    public void add(int n, List<CapsuledItem> list) {
        this.add(n, null, list);
    }

    public void add(int n, Pair<Integer, Long> pair, List<CapsuledItem> list) {
        if (this.vB.containsKey(n)) {
            LOG.warn("Capsule item " + n + " already defined.");
        }
        this.vB.put(n, Collections.unmodifiableList(list));
        if (pair != null) {
            this.vC.put(n, pair);
        }
    }

    @Override
    public int size() {
        return this.vB.size();
    }

    @Override
    public void clear() {
        ItemHandler.getInstance().unregisterItemHandler(this.vD);
        this.vB.clear();
    }

    @Override
    protected void process() {
        int[] nArray = new int[this.vB.size()];
        Iterator<Integer> iterator = this.vB.keySet().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            nArray[n] = iterator.next();
            ++n;
        }
        this.vD = new CapsuleItemsHandler(nArray);
        ItemHandler.getInstance().registerItemHandler(this.vD);
    }

    public static class CapsuledItem {
        private final int _itemId;
        private final long vF;
        private final long vG;
        private final double hc;
        private final int vH;
        private final int vI;

        public CapsuledItem(int n, long l, long l2, double d, int n2, int n3) {
            this._itemId = n;
            this.vF = l;
            this.vG = l2;
            this.hc = d;
            this.vH = n2;
            this.vI = n3;
        }

        public int getItemId() {
            return this._itemId;
        }

        public double getChance() {
            return this.hc;
        }

        public long getMax() {
            return this.vG;
        }

        public long getMin() {
            return this.vF;
        }

        public int getMinEnchant() {
            return this.vH;
        }

        public int getMaxEnchant() {
            return this.vI;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            CapsuledItem capsuledItem = (CapsuledItem)object;
            return new EqualsBuilder().append(this._itemId, capsuledItem._itemId).append(this.vF, capsuledItem.vF).append(this.vG, capsuledItem.vG).append(this.hc, capsuledItem.hc).append(this.vH, capsuledItem.vH).append(this.vI, capsuledItem.vI).isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(this._itemId).append(this.vF).append(this.vG).append(this.hc).append(this.vH).append(this.vI).toHashCode();
        }
    }

    public static class CapsuleItemsHandler
    implements IItemHandler {
        private final int[] vE;

        public CapsuleItemsHandler(int[] nArray) {
            this.vE = nArray;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
            int n = itemInstance.getItemId();
            Player player = playable.getPlayer();
            List<CapsuledItem> list = CapsuleItemHolder.getInstance().getCapsuledItems(n);
            if (list == null) {
                return false;
            }
            Pair<Integer, Long> pair = CapsuleItemHolder.getInstance().getCapsuleRequiredItems(n);
            if (pair != null && pair.getKey() > 0 && pair.getValue() > 0L && ItemFunctions.getItemCount(player, pair.getKey()) < pair.getValue()) {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                return false;
            }
            player.sendPacket((IStaticPacket)new SystemMessage(47).addItemName(itemInstance.getItemId()));
            PcInventory pcInventory = player.getInventory();
            pcInventory.writeLock();
            try {
                long l;
                ArrayList arrayList = new ArrayList();
                LinkedHashMap<Object, Pair<Object, Long>> linkedHashMap = new LinkedHashMap<Object, Pair<Object, Long>>();
                for (CapsuledItem capsuledItem : list) {
                    if (capsuledItem.getChance() == 100.0) {
                        l = capsuledItem.getMax() > capsuledItem.getMin() ? Rnd.get(capsuledItem.getMin(), capsuledItem.getMax()) : capsuledItem.getMin();
                        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(capsuledItem.getItemId());
                        linkedHashMap.put(capsuledItem, Pair.of(itemTemplate, l));
                        continue;
                    }
                    arrayList.add(Pair.of(capsuledItem, capsuledItem.getChance()));
                }
                if (!arrayList.isEmpty()) {
                    Collections.sort(arrayList, RandomUtils.DOUBLE_GROUP_COMPARATOR);
                    CapsuledItem capsuledItem = (CapsuledItem)RandomUtils.pickRandomSortedGroup(arrayList, 100.0);
                    if (capsuledItem != null) {
                        long l2 = capsuledItem.getMax() > capsuledItem.getMin() ? Rnd.get(capsuledItem.getMin(), capsuledItem.getMax()) : capsuledItem.getMin();
                        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(capsuledItem.getItemId());
                        linkedHashMap.put(capsuledItem, Pair.of(itemTemplate, l2));
                    }
                }
                if (linkedHashMap.isEmpty()) {
                    player.getInventory().destroyItem(itemInstance, 1L);
                    if (pair != null && pair.getKey() > 0 && pair.getValue() > 0L) {
                        ItemFunctions.removeItem(player, pair.getKey(), pair.getValue(), true);
                    }
                } else {
                    Object object;
                    long l3 = 0L;
                    l = 0L;
                    for (Map.Entry entry : linkedHashMap.entrySet()) {
                        object = (ItemTemplate)((Pair)entry.getValue()).getLeft();
                        long l4 = (Long)((Pair)entry.getValue()).getRight();
                        l = SafeMath.addAndCheck(l, SafeMath.mulAndCheck(l4, (long)((ItemTemplate)object).getWeight()));
                        if (((ItemTemplate)object).isStackable()) {
                            ++l3;
                            continue;
                        }
                        l3 = SafeMath.addAndCheck(l3, l4);
                    }
                    if (!pcInventory.validateCapacity(l3)) {
                        player.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
                        boolean bl2 = false;
                        return bl2;
                    }
                    if (!pcInventory.validateWeight(l)) {
                        player.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                        boolean bl3 = false;
                        return bl3;
                    }
                    if (!player.getInventory().destroyItem(itemInstance, 1L)) {
                        player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                        boolean bl4 = false;
                        return bl4;
                    }
                    if (pair != null && pair.getKey() > 0 && pair.getValue() > 0L && ItemFunctions.removeItem(player, pair.getKey(), pair.getValue(), true) < pair.getValue()) {
                        playable.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                        boolean bl5 = false;
                        return bl5;
                    }
                    for (Map.Entry entry : linkedHashMap.entrySet()) {
                        object = (CapsuledItem)entry.getKey();
                        ItemTemplate itemTemplate = (ItemTemplate)((Pair)entry.getValue()).getLeft();
                        long l5 = (Long)((Pair)entry.getValue()).getRight();
                        ArrayList<Pair<ItemInstance, Long>> arrayList2 = new ArrayList<Pair<ItemInstance, Long>>();
                        if (itemTemplate.isStackable()) {
                            arrayList2.add(Pair.of(player.getInventory().addItem(itemTemplate.getItemId(), l5), l5));
                        } else {
                            for (long i = 0L; i < l5; ++i) {
                                arrayList2.add(Pair.of(player.getInventory().addItem(itemTemplate.getItemId(), 1L), 1L));
                            }
                        }
                        for (Pair pair2 : arrayList2) {
                            ItemInstance itemInstance2 = (ItemInstance)pair2.getLeft();
                            long l6 = (Long)pair2.getRight();
                            if (!itemInstance2.canBeEnchanted(true)) {
                                player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(itemInstance2.getItemId(), l6, 0));
                                continue;
                            }
                            if (((CapsuledItem)object).getMaxEnchant() > ((CapsuledItem)object).getMinEnchant()) {
                                itemInstance2.setEnchantLevel(Rnd.get(((CapsuledItem)object).getMinEnchant(), ((CapsuledItem)object).getMaxEnchant()));
                            } else {
                                itemInstance2.setEnchantLevel(((CapsuledItem)object).getMinEnchant());
                            }
                            player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(itemInstance2.getItemId(), l6, itemInstance2.getEnchantLevel()));
                            player.sendPacket((IStaticPacket)new InventoryUpdate().addModifiedItem(itemInstance2));
                        }
                    }
                }
            }
            catch (ArithmeticException arithmeticException) {
                player.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            }
            finally {
                pcInventory.writeUnlock();
            }
            return true;
        }

        @Override
        public void dropItem(Player player, ItemInstance itemInstance, long l, Location location) {
            IItemHandler.NULL.dropItem(player, itemInstance, l, location);
        }

        @Override
        public boolean pickupItem(Playable playable, ItemInstance itemInstance) {
            return IItemHandler.NULL.pickupItem(playable, itemInstance);
        }

        @Override
        public int[] getItemIds() {
            return this.vE;
        }
    }
}

