/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SSQInfo
extends L2GameServerPacket {
    private int _state = 0;

    public SSQInfo() {
        int n = SevenSigns.getInstance().getCabalHighestScore();
        if (SevenSigns.getInstance().isSealValidationPeriod()) {
            if (n == 2) {
                this._state = 2;
            } else if (n == 1) {
                this._state = 1;
            }
        }
    }

    public SSQInfo(int n) {
        this._state = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(248);
        switch (this._state) {
            case 1: {
                this.writeH(257);
                break;
            }
            case 2: {
                this.writeH(258);
                break;
            }
            default: {
                this.writeH(256);
            }
        }
    }
}

