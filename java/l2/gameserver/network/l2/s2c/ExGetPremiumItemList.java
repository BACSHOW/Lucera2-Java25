/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Map;
import l2.gameserver.model.Player;
import l2.gameserver.model.PremiumItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExGetPremiumItemList
extends L2GameServerPacket {
    private int CS;
    private Map<Integer, PremiumItem> agS;

    public ExGetPremiumItemList(Player player) {
        this.CS = player.getObjectId();
        this.agS = player.getPremiumItemList();
    }

    @Override
    protected void writeImpl() {
        this.writeEx(134);
        if (!this.agS.isEmpty()) {
            this.writeD(this.agS.size());
            for (Map.Entry<Integer, PremiumItem> entry : this.agS.entrySet()) {
                this.writeD(entry.getKey());
                this.writeD(this.CS);
                this.writeD(entry.getValue().getItemId());
                this.writeQ(entry.getValue().getCount());
                this.writeD(0);
                this.writeS(entry.getValue().getSender());
            }
        }
    }
}

