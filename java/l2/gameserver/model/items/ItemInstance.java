/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.collections.LazyArrayList;
import l2.commons.util.concurrent.atomic.AtomicEnumBitFlag;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemAttributes;
import l2.gameserver.model.items.ItemStateFlags;
import l2.gameserver.model.items.attachment.ItemAttachment;
import l2.gameserver.model.items.listeners.ItemEnchantOptionsListener;
import l2.gameserver.network.l2.s2c.DropItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SpawnItem;
import l2.gameserver.scripts.Events;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.taskmanager.ItemsAutoDestroy;
import l2.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemType;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

public final class ItemInstance
extends GameObject {
    public static final int[] EMPTY_ENCHANT_OPTIONS = new int[3];
    private static final long serialVersionUID = 3162753878915133228L;
    public static final long MAX_AMMOUNT = Integer.MAX_VALUE;
    private static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();
    public static final int CHARGED_NONE = 0;
    public static final int CHARGED_SOULSHOT = 1;
    public static final int CHARGED_SPIRITSHOT = 1;
    public static final int CHARGED_BLESSED_SPIRITSHOT = 2;
    public static final int FLAG_NO_DROP = 1;
    public static final int FLAG_NO_TRADE = 2;
    public static final int FLAG_NO_TRANSFER = 4;
    public static final int FLAG_NO_CRYSTALLIZE = 8;
    public static final int FLAG_NO_ENCHANT = 16;
    public static final int FLAG_NO_DESTROY = 32;
    private ItemAttributes Xb = new ItemAttributes();
    private int[] Xc = EMPTY_ENCHANT_OPTIONS;
    private int Xd;
    private int Xe;
    private long Xf;
    private ItemLocation Xg;
    private int _slot;
    private int Xh;
    private int Xi = -1;
    private int Xj = -9999;
    private int Xk;
    private int Xl;
    private int Xm;
    private int Xn;
    private int Xo;
    private int Xp;
    private AtomicEnumBitFlag<ItemStateFlags> Xq = new AtomicEnumBitFlag();
    private ItemTemplate Xr;
    private boolean WI;
    private long Xs;
    private IntSet Xt = Containers.EMPTY_INT_SET;
    private long Xu;
    private int Xv = 0;
    private int Xw = 0;
    private boolean Xx = false;
    private ItemAttachment Xy;
    private ScheduledFuture<?> Xz;

    public ItemInstance(int n) {
        super(n);
    }

    public ItemInstance(int n, int n2) {
        super(n);
        this.setItemId(n2);
        this.setDuration(this.getTemplate().getDurability());
        this.setPeriodBegin(this.getTemplate().isTemporal() ? (int)(System.currentTimeMillis() / 1000L) + this.getTemplate().getDurability() * 60 : -9999);
        this.setLocData(-1);
        this.setEnchantLevel(this.getTemplate().getBaseEnchantLevel());
    }

    public int getOwnerId() {
        return this.Xd;
    }

    public void setOwnerId(int n) {
        if (this.Xd == n) {
            return;
        }
        this.Xd = n;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public int getItemId() {
        return this.Xe;
    }

    public void setVisibleItemId(int n) {
        this.Xp = n;
    }

    public int getVisibleItemId() {
        if (this.Xp > 0) {
            return this.Xp;
        }
        return this.getItemId();
    }

    public void setItemId(int n) {
        this.Xe = n;
        this.Xr = ItemHolder.getInstance().getTemplate(n);
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public long getCount() {
        return this.Xf;
    }

    public AtomicEnumBitFlag<ItemStateFlags> getItemStateFlag() {
        return this.Xq;
    }

    public void setCount(long l) {
        if (l < 0L) {
            l = 0L;
        }
        if (this.isStackable() && l > Integer.MAX_VALUE) {
            this.Xf = Integer.MAX_VALUE;
            this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
            return;
        }
        if (!this.isStackable() && l > 1L) {
            this.Xf = 1L;
            this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
            return;
        }
        if (this.Xf == l) {
            return;
        }
        this.Xf = l;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public int getEnchantLevel() {
        return this.Xh;
    }

    public void setEnchantLevel(int n) {
        int n2 = this.Xh;
        this.Xh = Math.max(this.getTemplate().getBaseEnchantLevel(), n);
        if (n2 != this.Xh && this.getTemplate().getEnchantOptions().size() > 0) {
            int[] nArray;
            Player player = GameObjectsStorage.getPlayer(this.getOwnerId());
            if (this.isEquipped() && player != null) {
                ItemEnchantOptionsListener.getInstance().onUnequip(this.getEquipSlot(), this, player);
            }
            int[] nArray2 = this.Xc = (nArray = this.getTemplate().getEnchantOptions().get(this.Xh)) == null ? EMPTY_ENCHANT_OPTIONS : nArray;
            if (this.isEquipped() && player != null) {
                ItemEnchantOptionsListener.getInstance().onEquip(this.getEquipSlot(), this, player);
            }
        }
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public void setLocName(String string) {
        this.setLocation(ItemLocation.valueOf(string));
    }

    public String getLocName() {
        return this.Xg.name();
    }

    public void setLocation(ItemLocation itemLocation) {
        if (this.Xg == itemLocation) {
            return;
        }
        this.Xg = itemLocation;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public ItemLocation getLocation() {
        return this.Xg;
    }

    public void setLocData(int n) {
        if (this._slot == n) {
            return;
        }
        this._slot = n;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public int getLocData() {
        return this._slot;
    }

    public int getBlessed() {
        return this.Xm;
    }

    public void setBlessed(int n) {
        this.Xm = n;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public int getDamaged() {
        return this.Xn;
    }

    public void setDamaged(int n) {
        this.Xn = n;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public int getCustomFlags() {
        return this.Xo;
    }

    public void setCustomFlags(int n) {
        if (this.Xo == n) {
            return;
        }
        this.Xo = n;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public ItemAttributes getAttributes() {
        return this.Xb;
    }

    public void setAttributes(ItemAttributes itemAttributes) {
        this.Xb = itemAttributes;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public int getDuration() {
        if (!this.isShadowItem()) {
            return -1;
        }
        return this.Xi;
    }

    public void setDuration(int n) {
        this.Xi = n;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public int getPeriod() {
        if (!this.isTemporalItem()) {
            return -9999;
        }
        return this.Xj - (int)(System.currentTimeMillis() / 1000L);
    }

    public int getPeriodBegin() {
        if (!this.isTemporalItem()) {
            return -9999;
        }
        return this.Xj;
    }

    public void setPeriodBegin(int n) {
        this.Xj = n;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public void startTimer(Runnable runnable) {
        this.Xz = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(runnable, 0L, 60000L);
    }

    public void stopTimer() {
        if (this.Xz != null) {
            this.Xz.cancel(false);
            this.Xz = null;
        }
    }

    public boolean isEquipable() {
        return this.Xr.isEquipable();
    }

    public boolean isEquipped() {
        return this.WI;
    }

    public void setEquipped(boolean bl) {
        this.WI = bl;
    }

    public int getBodyPart() {
        return this.Xr.getBodyPart();
    }

    public int getEquipSlot() {
        return this.getLocData();
    }

    public ItemTemplate getTemplate() {
        return this.Xr;
    }

    public void setDropTime(long l) {
        this.Xs = l;
    }

    public long getLastDropTime() {
        return this.Xs;
    }

    public long getDropTimeOwner() {
        return this.Xu;
    }

    public ItemType getItemType() {
        return this.Xr.getItemType();
    }

    public boolean isArmor() {
        return this.Xr.isArmor();
    }

    public boolean isAccessory() {
        return this.Xr.isAccessory();
    }

    public boolean isWeapon() {
        return this.Xr.isWeapon();
    }

    public int getReferencePrice() {
        return this.Xr.getReferencePrice();
    }

    public boolean isStackable() {
        return this.Xr.isStackable();
    }

    @Override
    public void onAction(Player player, boolean bl) {
        if (Events.onAction(player, this, bl)) {
            return;
        }
        if (player.isCursedWeaponEquipped() && CursedWeaponsManager.getInstance().isCursed(this.getItemId())) {
            return;
        }
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, this, null);
    }

    @Override
    public int getActingRange() {
        return 16;
    }

    public boolean isAugmented() {
        return this.getVariationStat1() != 0 || this.getVariationStat2() != 0;
    }

    public int getVariationStat1() {
        return this.Xk;
    }

    public int getVariationStat2() {
        return this.Xl;
    }

    public void setVariationStat1(int n) {
        this.Xk = n;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public void setVariationStat2(int n) {
        this.Xl = n;
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public int getChargedSoulshot() {
        return this.Xv;
    }

    public int getChargedSpiritshot() {
        return this.Xw;
    }

    public boolean getChargedFishshot() {
        return this.Xx;
    }

    public void setChargedSoulshot(int n) {
        this.Xv = n;
    }

    public void setChargedSpiritshot(int n) {
        this.Xw = n;
    }

    public void setChargedFishshot(boolean bl) {
        this.Xx = bl;
    }

    public Func[] getStatFuncs() {
        Func[] funcArray = Func.EMPTY_FUNC_ARRAY;
        LazyArrayList<Func> lazyArrayList = LazyArrayList.newInstance();
        if (this.Xr.getAttachedFuncs().length > 0) {
            for (FuncTemplate funcTemplate : this.Xr.getAttachedFuncs()) {
                Func func = funcTemplate.getFunc(this);
                if (func == null) continue;
                lazyArrayList.add(func);
            }
        }
        for (Element element : Element.VALUES) {
            if (this.isWeapon()) {
                lazyArrayList.add(new FuncAttack(element, 64, this));
            }
            if (!this.isArmor()) continue;
            lazyArrayList.add(new FuncDefence(element, 64, this));
        }
        if (!lazyArrayList.isEmpty()) {
            funcArray = lazyArrayList.toArray(new Func[lazyArrayList.size()]);
        }
        LazyArrayList.recycle(lazyArrayList);
        return funcArray;
    }

    public boolean isHeroWeapon() {
        return this.Xr.isHeroWeapon();
    }

    public boolean canBeDestroyed(Player player) {
        if ((this.getCustomFlags() & 0x20) == 32) {
            return false;
        }
        if (this.isHeroWeapon()) {
            return false;
        }
        if (PetDataTable.isPetControlItem(this) && player.isMounted()) {
            return false;
        }
        if (player.getPetControlItem() == this) {
            return false;
        }
        if (player.getEnchantScroll() == this) {
            return false;
        }
        if (this.isCursed()) {
            return false;
        }
        return this.Xr.isDestroyable();
    }

    public boolean canBeDropped(Player player, boolean bl) {
        if (player.getPlayerAccess().CanDropAnyItems) {
            return true;
        }
        if ((this.getCustomFlags() & 1) == 1) {
            return false;
        }
        if (this.isShadowItem()) {
            return false;
        }
        if (this.isTemporalItem()) {
            return false;
        }
        if (!(!this.isAugmented() || bl && Config.DROP_ITEMS_AUGMENTED || Config.ALT_ALLOW_DROP_AUGMENTED)) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        return this.Xr.isDropable();
    }

    public boolean canBeTraded(Player player) {
        if (this.isEquipped()) {
            return false;
        }
        if (player.getPlayerAccess().CanTradeAnyItem) {
            return true;
        }
        if ((this.getCustomFlags() & 2) == 2) {
            return false;
        }
        if (this.isShadowItem()) {
            return false;
        }
        if (this.isTemporalItem()) {
            return false;
        }
        if (this.isAugmented() && !Config.ALT_ALLOW_TRADE_AUGMENTED) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        return this.Xr.isTradeable();
    }

    public boolean canBeSold(Player player) {
        if ((this.getCustomFlags() & 0x20) == 32) {
            return false;
        }
        if (this.getItemId() == 57) {
            return false;
        }
        if (this.Xr.getReferencePrice() == 0) {
            return false;
        }
        if (this.isShadowItem()) {
            return false;
        }
        if (this.isTemporalItem()) {
            return false;
        }
        if (this.isAugmented() && !Config.ALT_ALLOW_TRADE_AUGMENTED) {
            return false;
        }
        if (this.isEquipped()) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        return this.Xr.isSellable();
    }

    public boolean canBeStored(Player player, boolean bl) {
        if ((this.getCustomFlags() & 4) == 4) {
            return false;
        }
        if (!this.getTemplate().isStoreable()) {
            return false;
        }
        if (!bl && (this.isShadowItem() || this.isTemporalItem())) {
            return false;
        }
        if (!bl && this.isAugmented() && !Config.ALT_ALLOW_TRADE_AUGMENTED) {
            return false;
        }
        if (this.isEquipped()) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        return bl || this.Xr.isTradeable();
    }

    public boolean canBeCrystallized(Player player) {
        if ((this.getCustomFlags() & 8) == 8) {
            return false;
        }
        if (this.isShadowItem()) {
            return false;
        }
        if (this.isTemporalItem()) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        return this.Xr.isCrystallizable();
    }

    public boolean canBeEnchanted(boolean bl) {
        if ((this.getCustomFlags() & 0x10) == 16) {
            return false;
        }
        return this.Xr.canBeEnchanted(bl);
    }

    public boolean canBeExchanged(Player player) {
        if ((this.getCustomFlags() & 0x20) == 32) {
            return false;
        }
        if (this.isShadowItem()) {
            return false;
        }
        if (this.isTemporalItem()) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        return this.Xr.isDestroyable();
    }

    public boolean isShadowItem() {
        return this.Xr.isShadowItem();
    }

    public boolean isTemporalItem() {
        return this.Xr.isTemporal();
    }

    public boolean isAltSeed() {
        return this.Xr.isAltSeed();
    }

    public boolean isCursed() {
        return this.Xr.isCursed();
    }

    public void dropToTheGround(Player player, NpcInstance npcInstance) {
        Creature creature = npcInstance;
        if (creature == null) {
            creature = player;
        }
        Location location = Location.findAroundPosition(creature, 128);
        if (player != null) {
            this.Xt = new HashIntSet(1, 2.0f);
            for (Player player2 : player.getPlayerGroup()) {
                this.Xt.add(player2.getObjectId());
            }
            this.Xu = System.currentTimeMillis();
            this.Xu = npcInstance != null ? (Config.IGNORE_ITEM_PICKUP_DELAY_MONSTER_IDS.contains(npcInstance.getNpcId()) ? (this.Xu += Config.IGNORE_ITEM_PICKUP_DELAY) : (npcInstance.isRaid() ? (this.Xu += Config.NONOWNER_ITEM_PICKUP_DELAY_RAID) : (this.Xu += Config.NONOWNER_ITEM_PICKUP_DELAY))) : (this.Xu += Config.NONOWNER_ITEM_PICKUP_DELAY);
        }
        this.dropMe(creature, location);
        if (this.isHerb()) {
            ItemsAutoDestroy.getInstance().addHerb(this);
        } else if (Config.AUTODESTROY_ITEM_AFTER > 0 && !this.isCursed()) {
            ItemsAutoDestroy.getInstance().addItem(this);
        }
    }

    public void dropToTheGround(Creature creature, Location location) {
        if (GeoEngine.canMoveToCoord(creature.getX(), creature.getY(), creature.getZ(), location.x, location.y, location.z, creature.getGeoIndex())) {
            this.dropMe(creature, location);
        } else {
            this.dropMe(creature, creature.getLoc());
        }
        if (this.isHerb()) {
            ItemsAutoDestroy.getInstance().addHerb(this);
        } else if (Config.AUTODESTROY_ITEM_AFTER > 0 && !this.isCursed()) {
            ItemsAutoDestroy.getInstance().addItem(this);
        }
    }

    public void dropToTheGround(Playable playable, Location location) {
        this.setLocation(ItemLocation.VOID);
        this.save();
        if (GeoEngine.canMoveToCoord(playable.getX(), playable.getY(), playable.getZ(), location.x, location.y, location.z, playable.getGeoIndex())) {
            this.dropMe(playable, location);
        } else {
            this.dropMe(playable, playable.getLoc());
        }
        if (this.isHerb()) {
            ItemsAutoDestroy.getInstance().addHerb(this);
        } else if (Config.AUTODESTROY_ITEM_AFTER > 0 && !this.isCursed()) {
            ItemsAutoDestroy.getInstance().addItem(this);
        }
    }

    public void dropMe(Creature creature, Location location) {
        if (creature != null) {
            this.setReflection(creature.getReflection());
        }
        this.spawnMe0(location, creature);
    }

    public final void pickupMe() {
        this.decayMe();
        this.setReflection(ReflectionManager.DEFAULT);
    }

    public ItemTemplate.ItemClass getItemClass() {
        return this.Xr.getItemClass();
    }

    private int getDefence(Element element) {
        return this.isArmor() ? this.getAttributeElementValue(element, true) : 0;
    }

    public int getDefenceFire() {
        return this.getDefence(Element.FIRE);
    }

    public int getDefenceWater() {
        return this.getDefence(Element.WATER);
    }

    public int getDefenceWind() {
        return this.getDefence(Element.WIND);
    }

    public int getDefenceEarth() {
        return this.getDefence(Element.EARTH);
    }

    public int getDefenceHoly() {
        return this.getDefence(Element.HOLY);
    }

    public int getDefenceUnholy() {
        return this.getDefence(Element.UNHOLY);
    }

    public int getAttributeElementValue(Element element, boolean bl) {
        return this.Xb.getValue(element) + (bl ? this.Xr.getBaseAttributeValue(element) : 0);
    }

    public Element getAttributeElement() {
        return this.Xb.getElement();
    }

    public int getAttributeElementValue() {
        return this.Xb.getValue();
    }

    public Element getAttackElement() {
        Element element;
        Element element2 = element = this.isWeapon() ? this.getAttributeElement() : Element.NONE;
        if (element == Element.NONE) {
            for (Element element3 : Element.VALUES) {
                if (this.Xr.getBaseAttributeValue(element3) <= 0) continue;
                return element3;
            }
        }
        return element;
    }

    public int getAttackElementValue() {
        return this.isWeapon() ? this.getAttributeElementValue(this.getAttackElement(), true) : 0;
    }

    public void setAttributeElement(Element element, int n) {
        this.Xb.setValue(element, n);
        this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }

    public boolean isHerb() {
        return this.getTemplate().isHerb();
    }

    public ItemTemplate.Grade getCrystalType() {
        return this.Xr.getCrystalType();
    }

    @Override
    public String getName() {
        return this.getTemplate().getName();
    }

    public void save() {
        _itemsDAO.store(this);
    }

    public void delete() {
        _itemsDAO.delete(this);
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player player, Creature creature) {
        L2GameServerPacket l2GameServerPacket = null;
        l2GameServerPacket = creature != null ? new DropItem(this, creature.getObjectId()) : new SpawnItem(this);
        return Collections.singletonList(l2GameServerPacket);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getTemplate().getItemId());
        stringBuilder.append(" ");
        if (this.getEnchantLevel() > 0) {
            stringBuilder.append("+");
            stringBuilder.append(this.getEnchantLevel());
            stringBuilder.append(" ");
        }
        stringBuilder.append(this.getTemplate().getName());
        if (!this.getTemplate().getAdditionalName().isEmpty()) {
            stringBuilder.append(" ");
            stringBuilder.append("\\").append(this.getTemplate().getAdditionalName()).append("\\");
        }
        stringBuilder.append(" ");
        stringBuilder.append("(");
        stringBuilder.append(this.getCount());
        stringBuilder.append(")");
        stringBuilder.append("[");
        stringBuilder.append(this.getObjectId());
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public boolean isItem() {
        return true;
    }

    public ItemAttachment getAttachment() {
        return this.Xy;
    }

    public void setAttachment(ItemAttachment itemAttachment) {
        ItemAttachment itemAttachment2 = this.Xy;
        this.Xy = itemAttachment;
        if (this.Xy != null) {
            this.Xy.setItem(this);
        }
        if (itemAttachment2 != null) {
            itemAttachment2.setItem(null);
        }
    }

    public int[] getEnchantOptions() {
        return this.Xc;
    }

    public IntSet getDropPlayers() {
        return this.Xt;
    }

    public class FuncDefence
    extends Func {
        private final Element XA;

        public FuncDefence(Element element, int n, Object object) {
            super(element.getDefence(), n, object);
            this.XA = element;
        }

        @Override
        public void calc(Env env) {
            env.value += (double)ItemInstance.this.getAttributeElementValue(this.XA, true);
        }
    }

    public class FuncAttack
    extends Func {
        private final Element XA;

        public FuncAttack(Element element, int n, Object object) {
            super(element.getAttack(), n, object);
            this.XA = element;
        }

        @Override
        public void calc(Env env) {
            env.value += (double)ItemInstance.this.getAttributeElementValue(this.XA, true);
        }
    }

    public static enum ItemLocation {
        VOID,
        INVENTORY,
        PAPERDOLL,
        PET_INVENTORY,
        PET_PAPERDOLL,
        WAREHOUSE,
        CLANWH,
        FREIGHT,
        LEASE,
        MAIL;

    }
}

