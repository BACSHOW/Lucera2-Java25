/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class TutorialShowQuestionMark
extends L2GameServerPacket {
    private int _number;

    public TutorialShowQuestionMark(int n) {
        this._number = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(161);
        this.writeD(this._number);
    }
}

