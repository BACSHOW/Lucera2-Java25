/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.CastleSiegeDefenderList;

public class RequestCastleSiegeDefenderList
extends L2GameClientPacket {
    private int abs;

    @Override
    protected void readImpl() {
        this.abs = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this.abs);
        if (castle == null || castle.getOwner() == null) {
            return;
        }
        player.sendPacket((IStaticPacket)new CastleSiegeDefenderList(castle));
    }
}

