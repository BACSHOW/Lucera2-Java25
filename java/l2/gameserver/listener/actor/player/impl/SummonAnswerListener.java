/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.player.impl;

import l2.commons.lang.reference.HardReference;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.utils.Location;

public class SummonAnswerListener
implements OnAnswerListener {
    private HardReference<Player> e;
    private Location CD;
    private long CE;
    private final long CA;

    public SummonAnswerListener(Player player, Location location, long l, int n) {
        this.e = player.getRef();
        this.CD = location;
        this.CE = l;
        this.CA = n > 0 ? System.currentTimeMillis() + (long)n : Long.MAX_VALUE;
    }

    @Override
    public void sayYes() {
        Player player = this.e.get();
        if (player == null) {
            return;
        }
        if (System.currentTimeMillis() > this.CA) {
            return;
        }
        player.abortAttack(true, true);
        player.abortCast(true, true);
        player.stopMove();
        if (this.CE > 0L) {
            if (player.getInventory().destroyItemByItemId(8615, this.CE)) {
                player.sendPacket((IStaticPacket)SystemMessage2.removeItems(8615, this.CE));
                player.teleToLocation(this.CD);
            } else {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            }
        } else {
            player.teleToLocation(this.CD);
        }
    }

    @Override
    public void sayNo() {
    }
}

