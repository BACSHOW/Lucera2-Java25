/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item.support;

import l2.gameserver.model.items.ItemInstance;

public enum EnchantTargetType {
    ALL(true, true, true),
    WEAPON(true, false, false),
    ARMOR(false, true, true);

    private final boolean ava;
    private final boolean avb;
    private final boolean avc;

    private EnchantTargetType(boolean bl, boolean bl2, boolean bl3) {
        this.ava = bl;
        this.avb = bl2;
        this.avc = bl3;
    }

    public boolean isUsableOn(ItemInstance itemInstance) {
        if (this.ava && itemInstance.isWeapon()) {
            return true;
        }
        if (this.avb && itemInstance.isArmor()) {
            return true;
        }
        return this.avc && itemInstance.isAccessory();
    }
}

