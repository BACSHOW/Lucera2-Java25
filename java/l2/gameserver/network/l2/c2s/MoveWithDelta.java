/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class MoveWithDelta
extends L2GameClientPacket {
    private int aaQ;
    private int aaR;
    private int aaS;

    @Override
    protected void readImpl() {
        this.aaQ = this.readD();
        this.aaR = this.readD();
        this.aaS = this.readD();
    }

    @Override
    protected void runImpl() {
    }
}

