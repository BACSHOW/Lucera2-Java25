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

public class RequestSetPledgeCrest
extends L2GameClientPacket {
    private int acO;
    private byte[] _data;

    @Override
    protected void readImpl() {
        this.acO = this.readD();
        if (this.acO == 256 && this.acO == this._buf.remaining()) {
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
        if ((player.getClanPrivileges() & 0x80) == 128) {
            if (clan.isPlacedForDisband()) {
                player.sendPacket((IStaticPacket)Msg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
                return;
            }
            if (clan.getLevel() < 3) {
                player.sendPacket((IStaticPacket)Msg.CLAN_CREST_REGISTRATION_IS_ONLY_POSSIBLE_WHEN_CLANS_SKILL_LEVELS_ARE_ABOVE_3);
                return;
            }
            int n = 0;
            if (this._data != null && CrestCache.isValidCrestData(this._data)) {
                n = CrestCache.getInstance().savePledgeCrest(clan.getClanId(), this._data);
            } else if (clan.hasCrest()) {
                CrestCache.getInstance().removePledgeCrest(clan.getClanId());
            }
            clan.setCrestId(n);
            clan.broadcastClanStatus(false, true, false);
        }
    }
}

