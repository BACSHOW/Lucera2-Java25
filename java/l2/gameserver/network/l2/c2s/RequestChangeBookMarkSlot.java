/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestChangeBookMarkSlot
extends L2GameClientPacket {
    private int abt;
    private int abu;

    @Override
    protected void readImpl() {
        this.abt = this.readD();
        this.abu = this.readD();
    }

    @Override
    protected void runImpl() {
    }
}

