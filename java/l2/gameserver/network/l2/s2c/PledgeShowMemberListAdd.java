/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeShowMemberListAdd
extends L2GameServerPacket {
    private PledgePacketMember alk;

    public PledgeShowMemberListAdd(UnitMember unitMember) {
        this.alk = new PledgePacketMember(unitMember);
    }

    @Override
    protected final void writeImpl() {
        this.writeC(85);
        this.writeS(this.alk._name);
        this.writeD(this.alk._level);
        this.writeD(this.alk.CW);
        this.writeD(this.alk.Dd);
        this.writeD(this.alk.CV);
        this.writeD(this.alk.all);
        this.writeD(this.alk.Ic);
    }

    private class PledgePacketMember {
        private String _name;
        private int _level;
        private int CW;
        private int Dd;
        private int CV;
        private int all;
        private int Ic;

        public PledgePacketMember(UnitMember unitMember) {
            this._name = unitMember.getName();
            this._level = unitMember.getLevel();
            this.CW = unitMember.getClassId();
            this.Dd = unitMember.getSex();
            this.CV = 0;
            this.all = unitMember.isOnline() ? unitMember.getObjectId() : 0;
            this.Ic = unitMember.getPledgeType();
        }
    }
}

