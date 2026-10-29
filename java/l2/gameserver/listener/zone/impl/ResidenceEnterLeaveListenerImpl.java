/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.zone.impl;

import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncMul;

public class ResidenceEnterLeaveListenerImpl
implements OnZoneEnterLeaveListener {
    public static final OnZoneEnterLeaveListener STATIC = new ResidenceEnterLeaveListenerImpl();

    @Override
    public void onZoneEnter(Zone zone, Creature creature) {
        double d;
        if (!creature.isPlayer()) {
            return;
        }
        Player player = (Player)creature;
        Residence residence = (Residence)zone.getParams().get("residence");
        if (residence.getOwner() == null || residence.getOwner() != player.getClan()) {
            return;
        }
        if (residence.isFunctionActive(3)) {
            d = 1.0 + (double)residence.getFunction(3).getLevel() / 100.0;
            player.addStatFunc(new FuncMul(Stats.REGENERATE_HP_RATE, 48, residence, d));
        }
        if (residence.isFunctionActive(4)) {
            d = 1.0 + (double)residence.getFunction(4).getLevel() / 100.0;
            player.addStatFunc(new FuncMul(Stats.REGENERATE_MP_RATE, 48, residence, d));
        }
    }

    @Override
    public void onZoneLeave(Zone zone, Creature creature) {
        if (!creature.isPlayer()) {
            return;
        }
        Residence residence = (Residence)zone.getParams().get("residence");
        creature.removeStatsOwner(residence);
    }
}

