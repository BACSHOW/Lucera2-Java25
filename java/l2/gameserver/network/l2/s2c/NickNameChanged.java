/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class NickNameChanged
extends L2GameServerPacket {
    private final int objectId;
    private final String title;

    public NickNameChanged(Creature creature) {
        this.objectId = creature.getObjectId();
        this.title = creature.getTitle();
    }

    @Override
    protected void writeImpl() {
        this.writeC(204);
        this.writeD(this.objectId);
        this.writeS(this.title);
    }
}

