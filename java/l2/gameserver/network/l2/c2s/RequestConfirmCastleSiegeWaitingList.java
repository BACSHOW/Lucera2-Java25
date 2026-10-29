/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.io.Serializable;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.CastleSiegeDefenderList;

public class RequestConfirmCastleSiegeWaitingList
extends L2GameClientPacket {
    private boolean abx;
    private int abs;
    private int CU;

    @Override
    protected void readImpl() {
        this.abs = this.readD();
        this.CU = this.readD();
        this.abx = this.readD() == 1;
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getClan() == null) {
            return;
        }
        Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this.abs);
        if (castle == null || player.getClan().getCastle() != castle.getId()) {
            player.sendActionFailed();
            return;
        }
        CastleSiegeEvent castleSiegeEvent = (CastleSiegeEvent)castle.getSiegeEvent();
        Object s = castleSiegeEvent.getSiegeClan("defenders_waiting", this.CU);
        if (s == null) {
            s = castleSiegeEvent.getSiegeClan("defenders", this.CU);
        }
        if (s == null) {
            return;
        }
        if ((player.getClanPrivileges() & 0x20000) != 131072) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST);
            return;
        }
        if (castleSiegeEvent.isRegistrationOver()) {
            player.sendPacket((IStaticPacket)SystemMsg.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPTED_OR_REJECTED);
            return;
        }
        int n = castleSiegeEvent.getObjects("defenders").size();
        if (n >= 20) {
            player.sendPacket((IStaticPacket)SystemMsg.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE);
            return;
        }
        castleSiegeEvent.removeObject(((SiegeClanObject)s).getType(), (Serializable)s);
        if (this.abx) {
            ((SiegeClanObject)s).setType("defenders");
        } else {
            ((SiegeClanObject)s).setType("defenders_refused");
        }
        castleSiegeEvent.addObject(((SiegeClanObject)s).getType(), (Serializable)s);
        SiegeClanDAO.getInstance().update(castle, (SiegeClanObject)s);
        player.sendPacket((IStaticPacket)new CastleSiegeDefenderList(castle));
    }
}

