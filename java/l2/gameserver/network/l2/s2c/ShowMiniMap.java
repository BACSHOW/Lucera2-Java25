/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ShowMiniMap
extends L2GameServerPacket {
    private int amA;
    private int Xj;

    public ShowMiniMap(Player player, int n) {
        this.amA = n;
        this.Xj = SevenSigns.getInstance().getCurrentPeriod();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(157);
        this.writeD(this.amA);
        this.writeC(this.Xj);
    }
}

