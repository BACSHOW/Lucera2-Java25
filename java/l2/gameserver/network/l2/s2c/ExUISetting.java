/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExUISetting
extends L2GameServerPacket {
    private final byte[] data;

    public ExUISetting(Player player) {
        this.data = player.getKeyBindings();
    }

    @Override
    protected void writeImpl() {
        this.writeEx(112);
        this.writeD(this.data.length);
        this.writeB(this.data);
    }
}

