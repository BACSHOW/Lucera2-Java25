/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item;

public enum Bodypart {
    NONE(0),
    CHEST(1024),
    BELT(0x10000000),
    RIGHT_BRACELET(0x100000),
    LEFT_BRACELET(0x200000),
    FULL_ARMOR(32768),
    HEAD(64),
    HAIR(65536),
    FACE(262144),
    HAIR_ALL(524288),
    UNDERWEAR(1),
    BACK(8192),
    NECKLACE(8),
    LEGS(2048),
    FEET(4096),
    GLOVES(512),
    RIGHT_HAND(128),
    LEFT_HAND(256),
    LEFT_RIGHT_HAND(16384),
    RIGHT_EAR(2),
    LEFT_EAR(4),
    RIGHT_FINGER(16),
    FORMAL_WEAR(131072),
    TALISMAN(0x400000),
    LEFT_FINGER(32),
    WOLF(-100, CHEST),
    GREAT_WOLF(-104, CHEST),
    HATCHLING(-101, CHEST),
    STRIDER(-102, CHEST),
    BABY_PET(-103, CHEST),
    PENDANT(-105, NECKLACE);

    private int YK;
    private Bodypart auh;

    private Bodypart(int n2) {
        this(n2, null);
    }

    private Bodypart(int n2, Bodypart bodypart) {
        this.YK = n2;
        this.auh = bodypart;
    }

    public int mask() {
        return this.YK;
    }

    public Bodypart getReal() {
        return this.auh;
    }
}

