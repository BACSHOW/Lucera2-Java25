/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item;

import l2.commons.lang.ArrayUtils;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.Config;
import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.EtcItemTemplate;
import l2.gameserver.templates.item.ItemFlags;
import l2.gameserver.templates.item.ItemType;
import l2.gameserver.templates.item.WeaponTemplate;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public abstract class ItemTemplate
extends StatTemplate {
    public static final int ITEM_ID_PC_BANG_POINTS = -100;
    public static final int ITEM_ID_CLAN_REPUTATION_SCORE = -200;
    public static final int ITEM_ID_FAME = -300;
    public static final int ITEM_ID_ADENA = 57;
    public static final int[] ITEM_ID_CASTLE_CIRCLET = new int[]{0, 6838, 6835, 6839, 6837, 6840, 6834, 6836, 8182, 8183};
    public static final int ITEM_ID_FORMAL_WEAR = 6408;
    public static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
    public static final int TYPE1_SHIELD_ARMOR = 1;
    public static final int TYPE1_OTHER = 2;
    public static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;
    public static final int TYPE2_WEAPON = 0;
    public static final int TYPE2_SHIELD_ARMOR = 1;
    public static final int TYPE2_ACCESSORY = 2;
    public static final int TYPE2_QUEST = 3;
    public static final int TYPE2_MONEY = 4;
    public static final int TYPE2_OTHER = 5;
    public static final int TYPE2_PET_WOLF = 6;
    public static final int TYPE2_PET_HATCHLING = 7;
    public static final int TYPE2_PET_STRIDER = 8;
    public static final int TYPE2_NODROP = 9;
    public static final int TYPE2_PET_GWOLF = 10;
    public static final int TYPE2_PENDANT = 11;
    public static final int TYPE2_PET_BABY = 12;
    public static final int SLOT_NONE = 0;
    public static final int SLOT_UNDERWEAR = 1;
    public static final int SLOT_R_EAR = 2;
    public static final int SLOT_L_EAR = 4;
    public static final int SLOT_NECK = 8;
    public static final int SLOT_R_FINGER = 16;
    public static final int SLOT_L_FINGER = 32;
    public static final int SLOT_HEAD = 64;
    public static final int SLOT_R_HAND = 128;
    public static final int SLOT_L_HAND = 256;
    public static final int SLOT_GLOVES = 512;
    public static final int SLOT_CHEST = 1024;
    public static final int SLOT_LEGS = 2048;
    public static final int SLOT_FEET = 4096;
    public static final int SLOT_BACK = 8192;
    public static final int SLOT_LR_HAND = 16384;
    public static final int SLOT_FULL_ARMOR = 32768;
    public static final int SLOT_HAIR = 65536;
    public static final int SLOT_FORMAL_WEAR = 131072;
    public static final int SLOT_DHAIR = 262144;
    public static final int SLOT_HAIRALL = 524288;
    public static final int SLOT_R_BRACELET = 0x100000;
    public static final int SLOT_L_BRACELET = 0x200000;
    public static final int SLOT_DECO = 0x400000;
    public static final int SLOT_BELT = 0x10000000;
    public static final int SLOT_WOLF = -100;
    public static final int SLOT_HATCHLING = -101;
    public static final int SLOT_STRIDER = -102;
    public static final int SLOT_BABYPET = -103;
    public static final int SLOT_GWOLF = -104;
    public static final int SLOT_PENDANT = -105;
    public static final int SLOTS_ARMOR = 180032;
    public static final int SLOTS_JEWELRY = 62;
    public static final int CRYSTAL_NONE = 0;
    public static final int CRYSTAL_D = 1458;
    public static final int CRYSTAL_C = 1459;
    public static final int CRYSTAL_B = 1460;
    public static final int CRYSTAL_A = 1461;
    public static final int CRYSTAL_S = 1462;
    public static final int ATTRIBUTE_NONE = -2;
    public static final int ATTRIBUTE_FIRE = 0;
    public static final int ATTRIBUTE_WATER = 1;
    public static final int ATTRIBUTE_WIND = 2;
    public static final int ATTRIBUTE_EARTH = 3;
    public static final int ATTRIBUTE_HOLY = 4;
    public static final int ATTRIBUTE_DARK = 5;
    protected final int _itemId;
    private final ItemClass aum;
    protected final String _name;
    protected final String _addname;
    protected final String _icon;
    protected final String _icon32;
    protected int _type1;
    protected int _type2;
    private final int KF;
    protected final Grade _crystalType;
    private final int aun;
    protected int _bodyPart;
    private final int auo;
    private final int aup;
    private final boolean auq;
    private final boolean aur;
    private final boolean aus;
    private int _flags;
    private final ReuseType aut;
    private final int ajH;
    private final int amo;
    private final int auu;
    private final int auv;
    protected Skill[] _skills;
    private Skill auw = null;
    public ItemType type;
    private int[] aux = new int[6];
    private IntObjectMap<int[]> auy = Containers.emptyIntObjectMap();
    private Condition[] _conditions = Condition.EMPTY_ARRAY;
    private IItemHandler auz = IItemHandler.NULL;
    private boolean auA;
    private boolean auB = false;

    protected ItemTemplate(StatsSet statsSet) {
        this._itemId = statsSet.getInteger("item_id");
        this.aum = statsSet.getEnum("class", ItemClass.class, ItemClass.OTHER);
        this._name = statsSet.getString("name");
        this._addname = statsSet.getString("add_name", "");
        this._icon = statsSet.getString("icon", "");
        this._icon32 = "<img src=" + this._icon + " width=32 height=32>";
        this.KF = statsSet.getInteger("weight", 0);
        this.aus = statsSet.getBool("crystallizable", false);
        this.aur = statsSet.getBool("stackable", false);
        this._crystalType = statsSet.getEnum("crystal_type", Grade.class, Grade.NONE);
        this.aun = statsSet.getInteger("durability", -1);
        this.auq = statsSet.getBool("temporal", false);
        this._bodyPart = statsSet.getInteger("bodypart", 0);
        this.auo = statsSet.getInteger("price", 0);
        this.aup = statsSet.getInteger("crystal_count", 0);
        this.aut = statsSet.getEnum("reuse_type", ReuseType.class, ReuseType.NORMAL);
        this.ajH = statsSet.getInteger("reuse_delay", 0);
        this.amo = statsSet.getInteger("delay_share_group", -this._itemId);
        this.auu = statsSet.getInteger("equip_reuse_delay", -1);
        this.auv = statsSet.getInteger("enchanted", 0);
        for (ItemFlags itemFlags : ItemFlags.VALUES) {
            boolean bl = statsSet.getBool(itemFlags.name().toLowerCase(), itemFlags.getDefaultValue());
            if (!bl) continue;
            this.a(itemFlags);
        }
        this._funcTemplates = FuncTemplate.EMPTY_ARRAY;
        this._skills = Skill.EMPTY_ARRAY;
    }

    public ItemType getItemType() {
        return this.type;
    }

    public String getIcon() {
        return this._icon;
    }

    public String getIcon32() {
        return this._icon32;
    }

    public final int getDurability() {
        return this.aun;
    }

    public final boolean isTemporal() {
        return this.auq;
    }

    public final int getItemId() {
        return this._itemId;
    }

    public abstract long getItemMask();

    public final int getType2() {
        return this._type2;
    }

    public final int getBaseAttributeValue(Element element) {
        if (element == Element.NONE) {
            return 0;
        }
        return this.aux[element.getId()];
    }

    public void setBaseAtributeElements(int[] nArray) {
        this.aux = nArray;
    }

    public int getBaseEnchantLevel() {
        return this.auv;
    }

    public final int getType2ForPackets() {
        int n = this._type2;
        switch (this._type2) {
            case 6: 
            case 7: 
            case 8: 
            case 10: 
            case 12: {
                if (this._bodyPart == 1024) {
                    n = 1;
                    break;
                }
                n = 0;
                break;
            }
            case 11: {
                n = 2;
            }
        }
        return n;
    }

    public final int getWeight() {
        return this.KF;
    }

    public final boolean isCrystallizable() {
        return this.aus && !this.isStackable() && this.getCrystalType() != Grade.NONE && this.getCrystalCount() > 0;
    }

    public final Grade getCrystalType() {
        return this._crystalType;
    }

    public final Grade getItemGrade() {
        return this.getCrystalType();
    }

    public final int getCrystalCount() {
        return this.aup;
    }

    public final int getCrystalCount(int n, boolean bl) {
        int n2 = this.aup;
        if (bl) {
            n2 /= 2;
        }
        if (n > 0 && Config.CRYSTALLIZE_BONUS_AT_ENCHANT) {
            int n3 = this.isWeapon() ? this.getItemGrade().cryMod : this.getItemGrade().cryMod / 10;
            n2 += n * n3;
            int n4 = n - 3;
            if (n4 > 0) {
                int n5 = this.isWeapon() ? this.getItemGrade().cryMod : this.getItemGrade().cryMod / 5;
                n2 += n4 * n5;
            }
        }
        return n2;
    }

    public final String getName() {
        return this._name;
    }

    public final String getAdditionalName() {
        return this._addname;
    }

    public final int getBodyPart() {
        return this._bodyPart;
    }

    public final int getType1() {
        return this._type1;
    }

    public final boolean isStackable() {
        return this.aur;
    }

    public final int getReferencePrice() {
        return this.auo;
    }

    public boolean isForHatchling() {
        return this._type2 == 7;
    }

    public boolean isForStrider() {
        return this._type2 == 8;
    }

    public boolean isForWolf() {
        return this._type2 == 6;
    }

    public boolean isForPetBaby() {
        return this._type2 == 12;
    }

    public boolean isForGWolf() {
        return this._type2 == 10;
    }

    public boolean isPendant() {
        return this._type2 == 11;
    }

    public boolean isForPet() {
        return this._type2 == 11 || this._type2 == 7 || this._type2 == 6 || this._type2 == 8 || this._type2 == 10 || this._type2 == 12;
    }

    public void attachSkill(Skill skill) {
        this._skills = ArrayUtils.add(this._skills, skill);
    }

    public Skill[] getAttachedSkills() {
        return this._skills;
    }

    public Skill getFirstSkill() {
        if (this._skills.length > 0) {
            return this._skills[0];
        }
        return null;
    }

    public Skill getEnchant4Skill() {
        return this.auw;
    }

    public String toString() {
        return this._itemId + " " + this._name;
    }

    public boolean isShadowItem() {
        return this.aun > 0 && !this.isTemporal();
    }

    public boolean isSealedItem() {
        return this._name.startsWith("Sealed");
    }

    public boolean isAltSeed() {
        return this._name.contains("Alternative");
    }

    public ItemClass getItemClass() {
        return this.aum;
    }

    public boolean isSealStone() {
        switch (this._itemId) {
            case 6360: 
            case 6361: 
            case 6362: {
                return true;
            }
        }
        return false;
    }

    public boolean isAdena() {
        return this._itemId == 57;
    }

    public boolean isEquipment() {
        return this._type1 != 4;
    }

    public boolean isKeyMatherial() {
        return this.aum == ItemClass.PIECES;
    }

    public boolean isRecipe() {
        return this.aum == ItemClass.RECIPIES;
    }

    public boolean isArrow() {
        return this.type == EtcItemTemplate.EtcItemType.ARROW;
    }

    public boolean isBelt() {
        return this._bodyPart == 0x10000000;
    }

    public boolean isBracelet() {
        return this._bodyPart == 0x100000 || this._bodyPart == 0x200000;
    }

    public boolean isUnderwear() {
        return this._bodyPart == 1;
    }

    public boolean isCloak() {
        return this._bodyPart == 8192;
    }

    public boolean isTalisman() {
        return this._bodyPart == 0x400000;
    }

    public boolean isHerb() {
        return this.type == EtcItemTemplate.EtcItemType.HERB;
    }

    public boolean isHeroWeapon() {
        return this._itemId >= 6611 && this._itemId <= 6621 || this._itemId >= 9388 && this._itemId <= 9390;
    }

    public boolean isCursed() {
        return CursedWeaponsManager.getInstance().isCursed(this._itemId);
    }

    public boolean isMercenaryTicket() {
        return this.type == EtcItemTemplate.EtcItemType.MERCENARY_TICKET;
    }

    public boolean isRod() {
        return this.getItemType() == WeaponTemplate.WeaponType.ROD;
    }

    public boolean isWeapon() {
        return this.getType2() == 0;
    }

    public boolean isArmor() {
        return this.getType2() == 1;
    }

    public boolean isAccessory() {
        return this.getType2() == 2;
    }

    public boolean isQuest() {
        return this.getType2() == 3;
    }

    public boolean isMageItem() {
        return false;
    }

    public boolean canBeEnchanted(@Deprecated boolean bl) {
        if (bl && this.getCrystalType() == Grade.NONE) {
            return false;
        }
        if (this.isCursed()) {
            return false;
        }
        if (this.isQuest()) {
            return false;
        }
        return this.isEnchantable();
    }

    public boolean isEquipable() {
        return this.getItemType() == EtcItemTemplate.EtcItemType.BAIT || this.getItemType() == EtcItemTemplate.EtcItemType.ARROW || this.getBodyPart() != 0 && !(this instanceof EtcItemTemplate);
    }

    public void setEnchant4Skill(Skill skill) {
        this.auw = skill;
    }

    public boolean testCondition(Playable playable, ItemInstance itemInstance) {
        return this.testCondition(playable, itemInstance, true);
    }

    public boolean testCondition(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (this.getConditions().length == 0) {
            return true;
        }
        Env env = new Env();
        env.character = playable;
        env.item = itemInstance;
        for (Condition condition : this.getConditions()) {
            if (condition.test(env)) continue;
            if (bl && condition.getSystemMsg() != null) {
                if (condition.getSystemMsg().size() > 0) {
                    playable.sendPacket((IStaticPacket)new SystemMessage2(condition.getSystemMsg()).addItemName(this.getItemId()));
                } else {
                    playable.sendPacket((IStaticPacket)condition.getSystemMsg());
                }
            }
            return false;
        }
        return true;
    }

    public void addCondition(Condition condition) {
        this._conditions = ArrayUtils.add(this._conditions, condition);
    }

    public Condition[] getConditions() {
        return this._conditions;
    }

    public boolean isEnchantable() {
        return this.hasFlag(ItemFlags.ENCHANTABLE);
    }

    public boolean isTradeable() {
        return this.hasFlag(ItemFlags.TRADEABLE);
    }

    public boolean isDestroyable() {
        return this.hasFlag(ItemFlags.DESTROYABLE);
    }

    public boolean isDropable() {
        return this.hasFlag(ItemFlags.DROPABLE);
    }

    public final boolean isSellable() {
        return this.hasFlag(ItemFlags.SELLABLE);
    }

    public final boolean isAugmentable() {
        return this.hasFlag(ItemFlags.AUGMENTABLE);
    }

    public final boolean isAttributable() {
        return this.hasFlag(ItemFlags.ATTRIBUTABLE);
    }

    public final boolean isStoreable() {
        return this.hasFlag(ItemFlags.STOREABLE);
    }

    public final boolean isFreightable() {
        return this.hasFlag(ItemFlags.FREIGHTABLE);
    }

    public boolean hasFlag(ItemFlags itemFlags) {
        return (this._flags & itemFlags.mask()) == itemFlags.mask();
    }

    private void a(ItemFlags itemFlags) {
        this._flags |= itemFlags.mask();
    }

    public IItemHandler getHandler() {
        return this.auz;
    }

    public void setHandler(IItemHandler iItemHandler) {
        this.auz = iItemHandler;
    }

    public boolean isShotItem() {
        return this.auA;
    }

    public void setIsShotItem(boolean bl) {
        this.auA = bl;
    }

    public int getReuseDelay() {
        return this.ajH;
    }

    public int getReuseGroup() {
        return this.amo;
    }

    public int getDisplayReuseGroup() {
        return this.amo < 0 ? -1 : this.amo;
    }

    public int getEquipReuseDelay() {
        return this.auu;
    }

    public void addEnchantOptions(int n, int[] nArray) {
        if (this.auy.isEmpty()) {
            this.auy = new HashIntObjectMap<int[]>();
        }
        this.auy.put(n, nArray);
    }

    public IntObjectMap<int[]> getEnchantOptions() {
        return this.auy;
    }

    public ReuseType getReuseType() {
        return this.aut;
    }

    public void setStatDisabled(boolean bl) {
        this.auB = bl;
    }

    @Override
    public FuncTemplate[] getAttachedFuncs() {
        if (this.auB) {
            return FuncTemplate.EMPTY_ARRAY;
        }
        return super.getAttachedFuncs();
    }

    @Override
    public Func[] getStatFuncs(Object object) {
        if (this.auB) {
            return Func.EMPTY_FUNC_ARRAY;
        }
        return super.getStatFuncs(object);
    }

    public static enum Grade {
        NONE(0, 0, 0),
        D(1458, 90, 1),
        C(1459, 45, 2),
        B(1460, 67, 3),
        A(1461, 145, 4),
        S(1462, 250, 5);

        public final int cry;
        public final int cryMod;
        public final int externalOrdinal;

        private Grade(int n2, int n3, int n4) {
            this.cry = n2;
            this.cryMod = n3;
            this.externalOrdinal = n4;
        }

        public int gradeOrd() {
            return this.externalOrdinal;
        }
    }

    public static enum ItemClass {
        ALL,
        WEAPON,
        ARMOR,
        JEWELRY,
        ACCESSORY,
        CONSUMABLE,
        MATHERIALS,
        PIECES,
        RECIPIES,
        SPELLBOOKS,
        MISC,
        OTHER;

    }

    public static enum ReuseType {
        NORMAL(new String[]{"THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME", "THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME", "THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME"}){

            @Override
            public long next(ItemInstance itemInstance) {
                return System.currentTimeMillis() + (long)itemInstance.getTemplate().getReuseDelay();
            }
        }
        ,
        EVERY_DAY_AT_6_30(new String[]{"THERE_ARE_S2_SECONDS_REMAINING_FOR_S1S_REUSE_TIME", "THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_FOR_S1S_REUSE_TIME", "THERE_ARE_S2_HOURS_S3_MINUTES_S4_SECONDS_REMAINING_FOR_S1S_REUSE_TIME"}){
            private final SchedulingPattern Q = new SchedulingPattern("30 6 * * *");

            @Override
            public long next(ItemInstance itemInstance) {
                return this.Q.next(System.currentTimeMillis());
            }
        };

        private String[] auE;

        private ReuseType(String ... stringArray) {
            this.auE = stringArray;
        }

        public abstract long next(ItemInstance var1);

        public String[] getMessageAddresses() {
            return this.auE;
        }
    }
}

