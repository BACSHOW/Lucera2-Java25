/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.skills.TimeStamp;

public class ExUseSharedGroupItem
extends L2GameServerPacket {
    private int _itemId;
    private int air;
    private int ais;
    private int ait;

    public ExUseSharedGroupItem(int n, TimeStamp timeStamp) {
        this.air = n;
        this._itemId = timeStamp.getId();
        this.ais = (int)(timeStamp.getReuseCurrent() / 1000L);
        this.ait = (int)(timeStamp.getReuseBasic() / 1000L);
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(73);
        this.writeD(this._itemId);
        this.writeD(this.air);
        this.writeD(this.ais);
        this.writeD(this.ait);
    }
}

