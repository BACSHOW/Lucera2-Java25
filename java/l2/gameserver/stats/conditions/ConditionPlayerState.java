/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerState
extends Condition {
    private final CheckPlayerState aql;
    private final boolean apR;

    public ConditionPlayerState(CheckPlayerState checkPlayerState, boolean bl) {
        this.aql = checkPlayerState;
        this.apR = bl;
    }

    @Override
    protected boolean testImpl(Env env) {
        switch (this.aql) {
            case RESTING: {
                if (env.character.isPlayer()) {
                    return ((Player)env.character).isSitting() == this.apR;
                }
                return !this.apR;
            }
            case MOVING: {
                return env.character.isMoving() == this.apR;
            }
            case RUNNING: {
                return (env.character.isMoving() && env.character.isRunning()) == this.apR;
            }
            case STANDING: {
                if (env.character.isPlayer()) {
                    return ((Player)env.character).isSitting() != this.apR && env.character.isMoving() != this.apR;
                }
                return env.character.isMoving() != this.apR;
            }
            case FLYING: {
                if (env.character.isPlayer()) {
                    return env.character.isFlying() == this.apR;
                }
                return !this.apR;
            }
            case FLYING_TRANSFORM: {
                if (env.character.isPlayer()) {
                    return ((Player)env.character).isInFlyingTransform() == this.apR;
                }
                return !this.apR;
            }
        }
        return !this.apR;
    }

    public static enum CheckPlayerState {
        RESTING,
        MOVING,
        RUNNING,
        STANDING,
        FLYING,
        FLYING_TRANSFORM;

    }
}

