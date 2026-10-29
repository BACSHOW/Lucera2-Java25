/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class AttackDeadTarget
extends L2GameServerPacket {
    @Override
    protected void writeImpl() {
        this.writeC(10);
    }
}

