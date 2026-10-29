/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestExCleftEnter
extends L2GameClientPacket {
    private int unk;

    @Override
    protected void readImpl() {
        this.unk = this.readD();
    }

    @Override
    protected void runImpl() {
    }
}

