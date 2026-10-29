/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.CrestCache;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;

public class RequestSetPledgeCrestLarge
extends L2GameClientPacket {
    private int acO;
    private byte[] _data;

    @Override
    protected void readImpl() {
        this.acO = this.readD();
        if (this.acO == 2176 && this.acO == this._buf.remaining()) {
            this._data = new byte[this.acO];
            this.readB(this._data);
        }
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            return;
        }
        if ((player.getClanPrivileges() & 0x80) == 128) {
            if (clan.isPlacedForDisband()) {
                player.sendPacket((IStaticPacket)Msg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
                return;
            }
            if (clan.getCastle() == 0 && clan.getHasHideout() == 0) {
                player.sendPacket((IStaticPacket)Msg.THE_CLANS_EMBLEM_WAS_SUCCESSFULLY_REGISTERED__ONLY_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_A_CASTLE_CAN_GET_THEIR_EMBLEM_DISPLAYED_ON_CLAN_RELATED_ITEMS);
                return;
            }
            int n = 0;
            if (this._data != null && CrestCache.isValidCrestData(this._data)) {
                n = CrestCache.getInstance().savePledgeCrestLarge(clan.getClanId(), this._data);
                player.sendPacket((IStaticPacket)Msg.THE_CLANS_EMBLEM_WAS_SUCCESSFULLY_REGISTERED__ONLY_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_A_CASTLE_CAN_GET_THEIR_EMBLEM_DISPLAYED_ON_CLAN_RELATED_ITEMS);
            } else if (clan.hasCrestLarge()) {
                CrestCache.getInstance().removePledgeCrestLarge(clan.getClanId());
            }
            clan.setCrestLargeId(n);
            clan.broadcastClanStatus(false, true, false);
        }
    }
}

