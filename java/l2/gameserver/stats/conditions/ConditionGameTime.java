/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.GameTimeController;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionGameTime
extends Condition {
    private final CheckGameTime apQ;
    private final boolean apR;

    public ConditionGameTime(CheckGameTime checkGameTime, boolean bl) {
        this.apQ = checkGameTime;
        this.apR = bl;
    }

    @Override
    protected boolean testImpl(Env env) {
        switch (this.apQ) {
            case NIGHT: {
                return GameTimeController.getInstance().isNowNight() == this.apR;
            }
        }
        return !this.apR;
    }

    public static enum CheckGameTime {
        NIGHT;

    }
}

