/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExSendManorList
extends L2GameServerPacket {
    @Override
    protected void writeImpl() {
        this.writeEx(27);
        List<Castle> list = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        this.writeD(list.size());
        for (Residence residence : list) {
            this.writeD(residence.getId());
            this.writeS(residence.getName().toLowerCase());
        }
    }
}

