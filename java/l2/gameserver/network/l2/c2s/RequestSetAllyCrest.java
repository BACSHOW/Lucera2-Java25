/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.CrestCache;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestSetAllyCrest
extends L2GameClientPacket {
    private int acO;
    private byte[] _data;

    @Override
    protected void readImpl() {
        this.acO = this.readD();
        if (this.acO == 192 && this.acO == this._buf.remaining()) {
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
        Alliance alliance = player.getAlliance();
        if (alliance != null && player.isAllyLeader()) {
            int n = 0;
            if (this._data != null && CrestCache.isValidCrestData(this._data)) {
                n = CrestCache.getInstance().saveAllyCrest(alliance.getAllyId(), this._data);
            } else if (alliance.hasAllyCrest()) {
                CrestCache.getInstance().removeAllyCrest(alliance.getAllyId());
            }
            alliance.setAllyCrestId(n);
            alliance.broadcastAllyStatus();
        }
    }
}

