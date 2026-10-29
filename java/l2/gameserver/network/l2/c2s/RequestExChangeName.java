/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestExChangeName
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        int n = this.readD();
        String string = this.readS();
        int n2 = this.readD();
    }

    @Override
    protected void runImpl() {
    }
}

