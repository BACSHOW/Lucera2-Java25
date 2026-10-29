/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class SnoopQuit
extends L2GameClientPacket {
    private int adl;

    @Override
    protected void readImpl() {
        this.adl = this.readD();
    }

    @Override
    protected void runImpl() {
    }
}

