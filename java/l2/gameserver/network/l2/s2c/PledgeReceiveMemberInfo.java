/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeReceiveMemberInfo
extends L2GameServerPacket {
    private UnitMember ala;

    public PledgeReceiveMemberInfo(UnitMember unitMember) {
        this.ala = unitMember;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(61);
        this.writeD(this.ala.getPledgeType());
        this.writeS(this.ala.getName());
        this.writeS(this.ala.getTitle());
        this.writeD(this.ala.getPowerGrade());
        this.writeS(this.ala.getSubUnit().getName());
        this.writeS(this.ala.getRelatedName());
    }
}

