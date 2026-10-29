/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerRiding
extends Condition {
    private final CheckPlayerRiding aqi;

    public ConditionPlayerRiding(CheckPlayerRiding checkPlayerRiding) {
        this.aqi = checkPlayerRiding;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer()) {
            return false;
        }
        if (this.aqi == CheckPlayerRiding.STRIDER && ((Player)env.character).isRiding()) {
            return true;
        }
        if (this.aqi == CheckPlayerRiding.WYVERN && ((Player)env.character).isFlying()) {
            return true;
        }
        return this.aqi == CheckPlayerRiding.NONE && !((Player)env.character).isRiding() && !((Player)env.character).isFlying();
    }

    public static enum CheckPlayerRiding {
        NONE,
        STRIDER,
        WYVERN;

    }
}

