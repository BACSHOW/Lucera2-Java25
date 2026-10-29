/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestSEKCustom
extends L2GameClientPacket {
    private int acI;
    private int acJ;

    @Override
    protected void readImpl() {
        this.acI = this.readD();
        this.acJ = this.readD();
    }

    @Override
    protected void runImpl() {
    }
}

