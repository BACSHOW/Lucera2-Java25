/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.CastleSiegeInfo;

public class RequestSetCastleSiegeTime
extends L2GameClientPacket {
    private int _id;
    private int _time;

    @Override
    protected void readImpl() {
        this._id = this.readD();
        this._time = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this._id);
        if (castle == null) {
            return;
        }
        if (player.getClan().getCastle() != castle.getId()) {
            return;
        }
        if ((player.getClanPrivileges() & 0x20000) != 131072) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME);
            return;
        }
        CastleSiegeEvent castleSiegeEvent = (CastleSiegeEvent)castle.getSiegeEvent();
        castleSiegeEvent.setNextSiegeTime(this._time);
        player.sendPacket((IStaticPacket)new CastleSiegeInfo(castle, player));
    }
}

