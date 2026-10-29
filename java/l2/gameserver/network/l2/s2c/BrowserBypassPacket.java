/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class BrowserBypassPacket
extends L2GameServerPacket {
    private String URL;

    public BrowserBypassPacket(String string) {
        this.URL = string;
    }

    @Override
    protected void writeImpl() {
        this.writeC(255);
        this.writeC(3);
        this.writeS(this.URL);
    }
}

