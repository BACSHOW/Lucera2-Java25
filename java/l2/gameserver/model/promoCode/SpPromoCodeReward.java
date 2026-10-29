/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.promoCode;

import l2.gameserver.model.Player;
import l2.gameserver.model.promoCode.PromoCodeReward;
import org.dom4j.Element;

public class SpPromoCodeReward
extends PromoCodeReward {
    public int _value;

    public SpPromoCodeReward(Element element) {
        this._value = Integer.parseInt(element.attributeValue("val"));
    }

    @Override
    public void giveReward(Player player) {
        player.addExpAndSp(0L, this._value);
    }
}

