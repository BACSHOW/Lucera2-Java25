/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.apache.commons.lang3.StringUtils;

public class PrivateStoreMsgSell
extends L2GameServerPacket {
    private final int aeo;
    private final String _name;

    public PrivateStoreMsgSell(Player player) {
        this.aeo = player.getObjectId();
        this._name = StringUtils.defaultString(player.getSellStoreName());
    }

    @Override
    protected final void writeImpl() {
        this.writeC(156);
        this.writeD(this.aeo);
        this.writeS(this._name);
    }
}

