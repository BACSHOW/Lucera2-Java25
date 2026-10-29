/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class TutorialShowHtml
extends L2GameServerPacket {
    private String _html;

    public TutorialShowHtml(String string) {
        this._html = string;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(160);
        this.writeS(this._html);
    }
}

