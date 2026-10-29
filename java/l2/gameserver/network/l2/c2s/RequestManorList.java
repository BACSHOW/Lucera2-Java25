/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.ExSendManorList;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RequestManorList
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        this.sendPacket((L2GameServerPacket)new ExSendManorList());
    }
}

