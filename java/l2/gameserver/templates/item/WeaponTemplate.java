/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item;

import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemType;

public final class WeaponTemplate
extends ItemTemplate {
    private final int auG;
    private final int auH;
    private final int auI;
    private final int auJ;
    private final int KS;
    private final boolean auK;
    private final int auL;
    private int GJ;
    private final int auf;

    public WeaponTemplate(StatsSet statsSet) {
        super(statsSet);
        this.type = statsSet.getEnum("type", WeaponType.class);
        this.auG = statsSet.getInteger("soulshots", 0);
        this.auH = statsSet.getInteger("spiritshots", 0);
        this.auK = statsSet.getBool("is_magic_weapon", false);
        this.auI = statsSet.getInteger("rnd_dam", 0);
        this.auJ = statsSet.getInteger("atk_reuse", this.type == WeaponType.BOW ? 1500 : 0);
        this.KS = statsSet.getInteger("mp_consume", 0);
        switch (this.getItemType()) {
            case BOW: {
                this.auL = statsSet.getInteger("attack_range", 500);
                break;
            }
            case POLE: {
                this.auL = statsSet.getInteger("attack_range", 80);
                break;
            }
            default: {
                this.auL = statsSet.getInteger("attack_range", 40);
            }
        }
        if (this.getItemType() == WeaponType.NONE) {
            this._type1 = 1;
            this._type2 = 1;
        } else {
            this._type1 = 0;
            this._type2 = 0;
        }
        if (this.getItemType() == WeaponType.PET) {
            this._type1 = 0;
            this._type2 = this._bodyPart == -100 ? 6 : (this._bodyPart == -104 ? 10 : (this._bodyPart == -101 ? 7 : 8));
            this._bodyPart = 128;
        }
        switch (this.getCrystalType()) {
            case D: {
                this.auf = 90;
                break;
            }
            case C: {
                this.auf = 45;
                break;
            }
            case B: {
                this.auf = 67;
                break;
            }
            case A: {
                this.auf = 145;
                break;
            }
            case S: {
                this.auf = 250;
                break;
            }
            default: {
                this.auf = 0;
            }
        }
    }

    @Override
    public WeaponType getItemType() {
        return (WeaponType)this.type;
    }

    @Override
    public long getItemMask() {
        return this.getItemType().mask();
    }

    public int getSoulShotCount() {
        return this.auG;
    }

    public int getSpiritShotCount() {
        return this.auH;
    }

    public int getCritical() {
        return this.GJ;
    }

    public int getRandomDamage() {
        return this.auI;
    }

    public int getAttackReuseDelay() {
        return this.auJ;
    }

    public int getMpConsume() {
        return this.KS;
    }

    public int getAttackRange() {
        return this.auL;
    }

    @Override
    public void attachFunc(FuncTemplate funcTemplate) {
        if (funcTemplate._stat == Stats.CRITICAL_BASE && funcTemplate._order == 8) {
            this.GJ = (int)Math.round(funcTemplate._value / 10.0);
        }
        super.attachFunc(funcTemplate);
    }

    @Override
    public boolean isMageItem() {
        return this.auK;
    }

    public static enum WeaponType implements ItemType
    {
        NONE(1, "Shield", null),
        SWORD(2, "Sword", Stats.SWORD_WPN_VULNERABILITY),
        BLUNT(3, "Blunt", Stats.BLUNT_WPN_VULNERABILITY),
        DAGGER(4, "Dagger", Stats.DAGGER_WPN_VULNERABILITY),
        BOW(5, "Bow", Stats.BOW_WPN_VULNERABILITY),
        POLE(6, "Pole", Stats.POLE_WPN_VULNERABILITY),
        ETC(7, "Etc", null),
        FIST(8, "Fist", Stats.FIST_WPN_VULNERABILITY),
        DUAL(9, "Dual Sword", Stats.DUAL_WPN_VULNERABILITY),
        DUALFIST(10, "Dual Fist", Stats.FIST_WPN_VULNERABILITY),
        BIGSWORD(11, "Big Sword", Stats.SWORD_WPN_VULNERABILITY),
        PET(12, "Pet", Stats.FIST_WPN_VULNERABILITY),
        ROD(13, "Rod", null),
        BIGBLUNT(14, "Big Blunt", Stats.BLUNT_WPN_VULNERABILITY),
        CROSSBOW(15, "Crossbow", Stats.CROSSBOW_WPN_VULNERABILITY),
        RAPIER(16, "Rapier", Stats.DAGGER_WPN_VULNERABILITY),
        ANCIENTSWORD(17, "Ancient Sword", Stats.SWORD_WPN_VULNERABILITY),
        DUALDAGGER(18, "Dual Dagger", Stats.DAGGER_WPN_VULNERABILITY);

        public static final WeaponType[] VALUES;
        private final long aqx;
        private final String _name;
        private final Stats auM;

        private WeaponType(int n2, String string2, Stats stats) {
            this.aqx = 1L << n2;
            this._name = string2;
            this.auM = stats;
        }

        @Override
        public long mask() {
            return this.aqx;
        }

        public Stats getDefence() {
            return this.auM;
        }

        public String toString() {
            return this._name;
        }

        static {
            VALUES = WeaponType.values();
        }
    }
}

