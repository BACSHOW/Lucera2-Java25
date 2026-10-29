/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item;

import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ArmorTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemType;
import l2.gameserver.templates.item.WeaponTemplate;

public final class EtcItemTemplate
extends ItemTemplate {
    public EtcItemTemplate(StatsSet statsSet) {
        super(statsSet);
        this.type = statsSet.getEnum("type", EtcItemType.class);
        this._type1 = 4;
        switch (this.getItemType()) {
            case QUEST: {
                this._type2 = 3;
                break;
            }
            case MONEY: {
                this._type2 = 4;
                break;
            }
            default: {
                this._type2 = 5;
            }
        }
    }

    @Override
    public EtcItemType getItemType() {
        return (EtcItemType)this.type;
    }

    @Override
    public long getItemMask() {
        return this.getItemType().mask();
    }

    @Override
    public final boolean isShadowItem() {
        return false;
    }

    @Override
    public boolean canBeEnchanted(boolean bl) {
        return false;
    }

    public static enum EtcItemType implements ItemType
    {
        ARROW(1, "Arrow"),
        MATERIAL(2, "Material"),
        PET_COLLAR(3, "PetCollar"),
        POTION(4, "Potion"),
        RECIPE(5, "Recipe"),
        SCROLL(6, "Scroll"),
        QUEST(7, "Quest"),
        MONEY(8, "Money"),
        OTHER(9, "Other"),
        SPELLBOOK(10, "Spellbook"),
        SEED(11, "Seed"),
        BAIT(12, "Bait"),
        SHOT(13, "Shot"),
        BOLT(14, "Bolt"),
        RUNE(15, "Rune"),
        HERB(16, "Herb"),
        MERCENARY_TICKET(17, "Mercenary Ticket");

        private final long aqx;
        private final String _name;

        private EtcItemType(int n2, String string2) {
            this.aqx = 1L << n2 + WeaponTemplate.WeaponType.VALUES.length + ArmorTemplate.ArmorType.VALUES.length;
            this._name = string2;
        }

        @Override
        public long mask() {
            return this.aqx;
        }

        public String toString() {
            return this._name;
        }
    }
}

