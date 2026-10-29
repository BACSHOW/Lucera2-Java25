/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PartySmallWindowDelete
extends L2GameServerPacket {
    private final int aeo;
    private final String _name;

    public PartySmallWindowDelete(Player player) {
        this.aeo = player.getObjectId();
        this._name = player.getName();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(81);
        this.writeD(this.aeo);
        this.writeS(this._name);
    }
}

