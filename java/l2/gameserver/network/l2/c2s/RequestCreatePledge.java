/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestCreatePledge
extends L2GameClientPacket {
    private String abF;

    @Override
    protected void readImpl() {
        this.abF = this.readS(64);
    }

    @Override
    protected void runImpl() {
    }
}

