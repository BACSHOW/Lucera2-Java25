/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.StartRotating;

public class StartRotatingC
extends L2GameClientPacket {
    private int aaL;
    private int Sx;

    @Override
    protected void readImpl() {
        this.aaL = this.readD();
        this.Sx = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        player.setHeading(this.aaL);
        player.broadcastPacket(new StartRotating(player, this.aaL, this.Sx, 0));
    }
}

