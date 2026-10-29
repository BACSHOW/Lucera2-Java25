/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.GameObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class Revive
extends L2GameServerPacket {
    private int CS;

    public Revive(GameObject gameObject) {
        this.CS = gameObject.getObjectId();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(7);
        this.writeD(this.CS);
    }
}

