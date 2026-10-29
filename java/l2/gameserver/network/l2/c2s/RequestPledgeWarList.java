/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PledgeReceiveWarList;

public class RequestPledgeWarList
extends L2GameClientPacket {
    private int _type;
    private int _page;

    @Override
    protected void readImpl() {
        this._page = this.readD();
        this._type = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Clan clan = player.getClan();
        if (clan != null) {
            player.sendPacket((IStaticPacket)new PledgeReceiveWarList(clan, this._type, this._page));
        }
    }
}

