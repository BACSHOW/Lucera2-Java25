/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SpawnEmitter
extends L2GameServerPacket {
    private int amG;
    private int amH;

    public SpawnEmitter(NpcInstance npcInstance, Player player) {
        this.amH = player.getObjectId();
        this.amG = npcInstance.getObjectId();
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(93);
        this.writeD(this.amG);
        this.writeD(this.amH);
        this.writeD(0);
    }
}

