/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ChairSit
extends L2GameServerPacket {
    private int CS;
    private int aec;

    public ChairSit(Player player, StaticObjectInstance staticObjectInstance) {
        this.CS = player.getObjectId();
        this.aec = staticObjectInstance.getUId();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(225);
        this.writeD(this.CS);
        this.writeD(this.aec);
    }
}

