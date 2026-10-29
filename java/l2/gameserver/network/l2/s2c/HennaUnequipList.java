/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.Henna;

public class HennaUnequipList
extends L2GameServerPacket {
    private int ajn;
    private long adQ;
    private List<Henna> ajt = new ArrayList<Henna>(3);

    public HennaUnequipList(Player player) {
        this.adQ = player.getAdena();
        this.ajn = player.getHennaEmptySlots();
        for (int i = 1; i <= 3; ++i) {
            if (player.getHenna(i) == null) continue;
            this.ajt.add(player.getHenna(i));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(229);
        this.writeD((int)this.adQ);
        this.writeD(this.ajn);
        this.writeD(this.ajt.size());
        for (Henna henna : this.ajt) {
            this.writeD(henna.getSymbolId());
            this.writeD(henna.getDyeId());
            this.writeD((int)henna.getDrawCount());
            this.writeD((int)henna.getPrice());
            this.writeD(1);
        }
    }
}

