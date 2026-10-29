/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.promoCode;

import l2.gameserver.model.Player;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.promoCode.PromoCodeReward;
import org.dom4j.Element;

public class SetLevelPromoCodeReward
extends PromoCodeReward {
    public int _level;

    public SetLevelPromoCodeReward(Element element) {
        this._level = Integer.parseInt(element.attributeValue("val"));
    }

    @Override
    public void giveReward(Player player) {
        if (player.getLevel() == this._level) {
            return;
        }
        long l = Experience.LEVEL[this._level] - player.getExp();
        player.addExpAndSp(l, 0L);
    }
}

