/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.data.xml.holder.HennaHolder;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.Henna;

public class HennaEquipList
extends L2GameServerPacket {
    private int ajn;
    private long adQ;
    private List<Henna> ajo = new ArrayList<Henna>();

    public HennaEquipList(Player player) {
        this.adQ = player.getAdena();
        this.ajn = player.getHennaEmptySlots();
        List<Henna> list = HennaHolder.getInstance().generateList(player);
        for (Henna henna : list) {
            if (player.getInventory().getItemByItemId(henna.getDyeId()) == null) continue;
            this.ajo.add(henna);
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(226);
        this.writeD((int)this.adQ);
        this.writeD(this.ajn);
        if (this.ajo.size() != 0) {
            this.writeD(this.ajo.size());
            for (Henna henna : this.ajo) {
                this.writeD(henna.getSymbolId());
                this.writeD(henna.getDyeId());
                this.writeD((int)henna.getDrawCount());
                this.writeD((int)henna.getPrice());
                this.writeD(1);
            }
        } else {
            this.writeD(1);
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
        }
    }
}

