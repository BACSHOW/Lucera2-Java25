/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;
import org.apache.commons.lang3.StringUtils;

public class ConditionPlayerPrivateStore
extends Condition {
    private final int aqg;

    private static int z(String string) {
        if ("SELL".equalsIgnoreCase(string)) {
            return 1;
        }
        if ("BUY".equalsIgnoreCase(string)) {
            return 3;
        }
        if ("MANUFACTURE".equalsIgnoreCase(string)) {
            return 5;
        }
        if ("SELL_PACKAGE".equalsIgnoreCase(string)) {
            return 8;
        }
        return 0;
    }

    public ConditionPlayerPrivateStore(int n) {
        this.aqg = n;
    }

    public ConditionPlayerPrivateStore(String string) {
        this(StringUtils.isNumeric(string) ? Integer.parseInt(string) : ConditionPlayerPrivateStore.z(string));
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer()) {
            return false;
        }
        int n = ((Player)env.character).getPrivateStoreType();
        return n == this.aqg;
    }
}

