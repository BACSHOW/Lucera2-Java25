/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestPCCafeCouponUse
extends L2GameClientPacket {
    private String acj;

    @Override
    protected void readImpl() {
        this.acj = this.readS();
    }

    @Override
    protected void runImpl() {
    }
}

