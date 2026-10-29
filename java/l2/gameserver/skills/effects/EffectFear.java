/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.utils.PositionUtils;

public final class EffectFear
extends Effect {
    public static final double FEAR_RANGE = 2600.0;

    public EffectFear(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        if (this._effected.isFearImmune()) {
            this.getEffector().sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }
        Player player = this._effected.getPlayer();
        if (player != null) {
            SiegeEvent siegeEvent = player.getEvent(SiegeEvent.class);
            if (this._effected.isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance)this._effected)) {
                this.getEffector().sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }
        }
        if (this._effected.isInZonePeace()) {
            this.getEffector().sendPacket((IStaticPacket)Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!this._effected.startFear()) {
            this._effected.abortAttack(true, true);
            this._effected.abortCast(true, true);
            this._effected.stopMove();
        }
        double d = Math.toRadians(PositionUtils.calculateAngleFrom(this._effector, this._effected));
        int n = this._effected.getX();
        int n2 = this._effected.getY();
        int n3 = n + (int)(2600.0 * Math.cos(d));
        int n4 = n2 + (int)(2600.0 * Math.sin(d));
        this._effected.setRunning();
        this._effected.moveToLocation(GeoEngine.moveCheck(n, n2, this._effected.getZ(), n3, n4, this._effected.getGeoIndex()), 0, false);
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopFear();
        this._effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

