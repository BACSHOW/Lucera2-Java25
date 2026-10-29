/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeShowMemberListUpdate
extends L2GameServerPacket {
    private String _name;
    private int acS;
    private int CW;
    private int Dd;
    private int alw;
    private int CS;
    private int Ic;
    private int alx;

    public PledgeShowMemberListUpdate(Player player) {
        UnitMember unitMember;
        this._name = player.getName();
        this.acS = player.getLevel();
        this.CW = player.getClassId().getId();
        this.Dd = player.getSex();
        this.CS = player.getObjectId();
        this.alw = player.isOnline() ? 1 : 0;
        this.Ic = player.getPledgeType();
        SubUnit subUnit = player.getSubUnit();
        UnitMember unitMember2 = unitMember = subUnit == null ? null : subUnit.getUnitMember(this.CS);
        if (unitMember != null) {
            this.alx = unitMember.hasSponsor() ? 1 : 0;
        }
    }

    public PledgeShowMemberListUpdate(UnitMember unitMember) {
        this._name = unitMember.getName();
        this.acS = unitMember.getLevel();
        this.CW = unitMember.getClassId();
        this.Dd = unitMember.getSex();
        this.CS = unitMember.getObjectId();
        this.alw = unitMember.isOnline() ? 1 : 0;
        this.Ic = unitMember.getPledgeType();
        this.alx = unitMember.hasSponsor() ? 1 : 0;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(84);
        this.writeS(this._name);
        this.writeD(this.acS);
        this.writeD(this.CW);
        this.writeD(this.Dd);
        this.writeD(this.CS);
        this.writeD(this.alw);
        this.writeD(this.Ic);
        this.writeD(this.alx);
    }
}

