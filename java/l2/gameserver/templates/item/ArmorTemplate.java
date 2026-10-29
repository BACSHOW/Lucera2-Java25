/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item;

import l2.gameserver.Config;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemType;
import l2.gameserver.templates.item.WeaponTemplate;

public final class ArmorTemplate
extends ItemTemplate {
    public static final double EMPTY_RING = 5.0;
    public static final double EMPTY_EARRING = 9.0;
    public static final double EMPTY_NECKLACE = 13.0;
    public static final double EMPTY_HELMET = 12.0;
    public static final double EMPTY_BODY_FIGHTER = 31.0;
    public static final double EMPTY_LEGS_FIGHTER = 18.0;
    public static final double EMPTY_BODY_MYSTIC = 15.0;
    public static final double EMPTY_LEGS_MYSTIC = 8.0;
    public static final double EMPTY_GLOVES = 8.0;
    public static final double EMPTY_BOOTS = 7.0;
    private final int auf;

    public ArmorTemplate(StatsSet statsSet) {
        super(statsSet);
        this.type = statsSet.getEnum("type", ArmorType.class);
        if (this._bodyPart == 8 || (this._bodyPart & 4) != 0 || (this._bodyPart & 0x20) != 0) {
            this._type1 = 0;
            this._type2 = 2;
        } else if (this._bodyPart == 65536 || this._bodyPart == 262144 || this._bodyPart == 524288) {
            this._type1 = 2;
            this._type2 = Config.ALT_HAIR_TO_ACC_SLOT ? 2 : 5;
        } else {
            this._type1 = 1;
            this._type2 = 1;
        }
        if (this.getItemType() == ArmorType.PET) {
            this._type1 = 1;
            switch (this._bodyPart) {
                case -100: {
                    this._type2 = 6;
                    this._bodyPart = 1024;
                    break;
                }
                case -104: {
                    this._type2 = 10;
                    this._bodyPart = 1024;
                    break;
                }
                case -101: {
                    this._type2 = 7;
                    this._bodyPart = 1024;
                    break;
                }
                case -105: {
                    this._type2 = 11;
                    this._bodyPart = 8;
                    break;
                }
                case -103: {
                    this._type2 = 12;
                    this._bodyPart = 1024;
                    break;
                }
                default: {
                    this._type2 = 8;
                    this._bodyPart = 1024;
                }
            }
        }
        switch (this.getCrystalType()) {
            case D: {
                this.auf = 11;
                break;
            }
            case C: {
                this.auf = 6;
                break;
            }
            case B: {
                this.auf = 11;
                break;
            }
            case A: {
                this.auf = 20;
                break;
            }
            case S: {
                this.auf = 25;
                break;
            }
            default: {
                this.auf = 0;
            }
        }
    }

    @Override
    public ArmorType getItemType() {
        return (ArmorType)this.type;
    }

    @Override
    public long getItemMask() {
        return this.getItemType().mask();
    }

    public static enum ArmorType implements ItemType
    {
        NONE(1, "None"),
        LIGHT(2, "Light"),
        HEAVY(3, "Heavy"),
        MAGIC(4, "Magic"),
        PET(5, "Pet"),
        SIGIL(6, "Sigil");

        public static final ArmorType[] VALUES;
        private final long aqx;
        private final String _name;

        private ArmorType(int n2, String string2) {
            this.aqx = 1L << n2 + WeaponTemplate.WeaponType.VALUES.length;
            this._name = string2;
        }

        @Override
        public long mask() {
            return this.aqx;
        }

        public String toString() {
            return this._name;
        }

        static {
            VALUES = ArmorType.values();
        }
    }
}

