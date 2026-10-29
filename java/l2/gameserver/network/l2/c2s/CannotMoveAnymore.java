/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.StopMove;
import l2.gameserver.utils.Location;

public class CannotMoveAnymore
extends L2GameClientPacket {
    private Location cH = new Location();

    @Override
    protected void readImpl() {
        this.cH.x = this.readD();
        this.cH.y = this.readD();
        this.cH.z = this.readD();
        this.cH.h = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isOlyObserver()) {
            player.sendPacket((IStaticPacket)new StopMove(player.getObjectId(), this.cH));
            return;
        }
        if (!player.isOutOfControl()) {
            player.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, this.cH, null);
            player.stopMove();
        }
    }
}

