/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.components;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public interface IStaticPacket {
    public L2GameServerPacket packet(Player var1);
}

