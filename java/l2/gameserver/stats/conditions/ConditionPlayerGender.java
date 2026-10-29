/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerGender
extends Condition {
    private final Gender aqa;

    public ConditionPlayerGender(Gender gender) {
        this.aqa = gender;
    }

    public ConditionPlayerGender(String string) {
        this(Gender.valueOf(string.toUpperCase()));
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer()) {
            return false;
        }
        return ((Player)env.character).getSex() == this.aqa.getPlayerGenderId();
    }

    public static enum Gender {
        MALE(0),
        FEMALE(1);

        private final int aqb;

        private Gender(int n2) {
            this.aqb = n2;
        }

        public int getPlayerGenderId() {
            return this.aqb;
        }
    }
}

