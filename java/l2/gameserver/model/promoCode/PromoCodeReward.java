/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.promoCode;

import l2.gameserver.model.Player;

public abstract class PromoCodeReward {
    public boolean validate() {
        return true;
    }

    public abstract void giveReward(Player var1);
}

