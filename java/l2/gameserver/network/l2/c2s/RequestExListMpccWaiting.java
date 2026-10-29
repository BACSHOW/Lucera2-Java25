/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExListMpccWaiting;

public class RequestExListMpccWaiting
extends L2GameClientPacket {
    private int wr;
    private int abP;
    private boolean abQ;

    @Override
    protected void readImpl() throws Exception {
        this.wr = this.readD();
        this.abP = this.readD();
        this.abQ = this.readD() == 1;
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        player.sendPacket((IStaticPacket)new ExListMpccWaiting(player, this.wr, this.abP, this.abQ));
    }
}

