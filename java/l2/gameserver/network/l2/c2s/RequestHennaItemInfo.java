/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.HennaHolder;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.HennaItemInfo;
import l2.gameserver.templates.Henna;

public class RequestHennaItemInfo
extends L2GameClientPacket {
    private int _symbolId;

    @Override
    protected void readImpl() {
        this._symbolId = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Henna henna = HennaHolder.getInstance().getHenna(this._symbolId);
        if (henna != null) {
            player.sendPacket((IStaticPacket)new HennaItemInfo(henna, player));
        }
    }
}

