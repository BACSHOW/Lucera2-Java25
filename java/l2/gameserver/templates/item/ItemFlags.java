/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item;

public enum ItemFlags {
    DESTROYABLE(true),
    DROPABLE(true),
    FREIGHTABLE(false),
    AUGMENTABLE(true),
    ENCHANTABLE(true),
    ATTRIBUTABLE(true),
    SELLABLE(true),
    TRADEABLE(true),
    STOREABLE(true);

    public static final ItemFlags[] VALUES;
    private final int YK;
    private final boolean auk;

    private ItemFlags(boolean bl) {
        this.auk = bl;
        this.YK = 1 << this.ordinal();
    }

    public int mask() {
        return this.YK;
    }

    public boolean getDefaultValue() {
        return this.auk;
    }

    static {
        VALUES = ItemFlags.values();
    }
}

