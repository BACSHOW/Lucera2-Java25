/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PrivateStoreListSell
extends L2GameServerPacket {
    private int acz;
    private long adQ;
    private final boolean adj;
    private List<TradeItem> HM;

    public PrivateStoreListSell(Player player, Player player2) {
        this.acz = player2.getObjectId();
        this.adQ = player.getAdena();
        this.adj = player2.getPrivateStoreType() == 8;
        this.HM = player2.getSellList();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(155);
        this.writeD(this.acz);
        this.writeD(this.adj ? 1 : 0);
        this.writeD((int)this.adQ);
        this.writeD(this.HM.size());
        for (TradeItem tradeItem : this.HM) {
            this.writeD(tradeItem.getItem().getType2ForPackets());
            this.writeD(tradeItem.getObjectId());
            this.writeD(tradeItem.getItemId());
            this.writeD((int)tradeItem.getCount());
            this.writeH(0);
            this.writeH(tradeItem.getEnchantLevel());
            this.writeH(0);
            this.writeD(tradeItem.getItem().getBodyPart());
            this.writeD((int)tradeItem.getOwnersPrice());
            this.writeD((int)tradeItem.getStorePrice());
        }
    }
}

