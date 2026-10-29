/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeReceiveSubPledgeCreated
extends L2GameServerPacket {
    private int type;
    private String _name;
    private String leader_name;

    public PledgeReceiveSubPledgeCreated(SubUnit subUnit) {
        this.type = subUnit.getType();
        this._name = subUnit.getName();
        this.leader_name = subUnit.getLeaderName();
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(63);
        this.writeD(1);
        this.writeD(this.type);
        this.writeS(this._name);
        this.writeS(this.leader_name);
    }
}

