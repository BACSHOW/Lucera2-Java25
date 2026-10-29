/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.HennaHolder;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.Henna;

public class RequestHennaEquip
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
        if (henna == null || !henna.isForThisClass(player) || player.isOlyParticipant()) {
            player.sendPacket((IStaticPacket)Msg.THE_SYMBOL_CANNOT_BE_DRAWN);
            return;
        }
        if (player.getHennaEmptySlots() == 0) {
            player.sendPacket((IStaticPacket)SystemMsg.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
            return;
        }
        long l = player.getAdena();
        long l2 = player.getInventory().getCountOf(henna.getDyeId());
        if (l2 >= henna.getDrawCount() && l >= henna.getPrice()) {
            if (player.consumeItem(henna.getDyeId(), henna.getDrawCount()) && player.reduceAdena(henna.getPrice())) {
                player.sendPacket((IStaticPacket)SystemMsg.THE_SYMBOL_HAS_BEEN_ADDED);
                player.addHenna(henna);
            }
        } else {
            player.sendPacket((IStaticPacket)SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
        }
    }
}

