/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.CastleSiegeAttackerList;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RequestCastleSiegeAttackerList
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
        Object r = ResidenceHolder.getInstance().getResidence(this.abs);
        if (r != null) {
            this.sendPacket((L2GameServerPacket)new CastleSiegeAttackerList((Residence)r));
        }
    }
}

