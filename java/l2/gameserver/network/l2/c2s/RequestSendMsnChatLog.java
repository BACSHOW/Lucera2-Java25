/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestSendMsnChatLog
extends L2GameClientPacket {
    private int abL;
    private String acM;
    private String acN;

    @Override
    protected void runImpl() {
    }

    @Override
    protected void readImpl() {
        this.acM = this.readS();
        this.acN = this.readS();
        this.abL = this.readD();
    }
}

