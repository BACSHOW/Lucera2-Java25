/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.promoCode;

import l2.gameserver.model.Player;
import l2.gameserver.model.promoCode.PromoCodeReward;
import org.dom4j.Element;

public class ExpPromoCodeReward
extends PromoCodeReward {
    public long _value;

    public ExpPromoCodeReward(Element element) {
        this._value = Long.parseLong(element.attributeValue("val"));
    }

    @Override
    public void giveReward(Player player) {
        player.addExpAndSp(this._value, 0L);
    }
}

