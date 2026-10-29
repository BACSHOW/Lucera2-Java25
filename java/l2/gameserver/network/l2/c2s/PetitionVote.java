/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class PetitionVote
extends L2GameClientPacket {
    private int _type;
    private int _unk1;
    private String aaV;

    @Override
    protected void runImpl() {
    }

    @Override
    protected void readImpl() {
        this._type = this.readD();
        this._unk1 = this.readD();
        this.aaV = this.readS(4096);
    }
}

