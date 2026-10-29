/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.Henna;

public class RequestHennaUnequip
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
        if (player.isOlyParticipant()) {
            return;
        }
        for (int i = 1; i <= 3; ++i) {
            Henna henna = player.getHenna(i);
            if (henna == null || henna.getSymbolId() != this._symbolId) continue;
            long l = henna.getPrice() / 5L;
            if (player.getAdena() < l) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                break;
            }
            player.reduceAdena(l);
            player.removeHenna(i);
            player.sendPacket((IStaticPacket)SystemMsg.THE_SYMBOL_HAS_BEEN_DELETED);
            break;
        }
    }
}

