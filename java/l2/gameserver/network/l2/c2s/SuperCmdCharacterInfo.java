/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

class SuperCmdCharacterInfo
extends L2GameClientPacket {
    private String adm;

    SuperCmdCharacterInfo() {
    }

    @Override
    protected void readImpl() {
        this.adm = this.readS();
    }

    @Override
    protected void runImpl() {
    }
}

