/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.promoCode;

import l2.gameserver.model.Player;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.promoCode.PromoCodeReward;
import org.dom4j.Element;

public class AddLevelPromoCodeReward
extends PromoCodeReward {
    public int _level;

    public AddLevelPromoCodeReward(Element element) {
        this._level = Integer.parseInt(element.attributeValue("val"));
    }

    @Override
    public void giveReward(Player player) {
        long l = Experience.LEVEL[Math.min(player.getLevel() + this._level, Experience.LEVEL.length - 1)] - player.getExp();
        player.addExpAndSp(l, 0L);
    }
}

