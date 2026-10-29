/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.ItemInfoCache;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.ExRpItemLink;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RequestExRqItemLink
extends L2GameClientPacket {
    private int CS;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
    }

    @Override
    protected void runImpl() {
        ItemInfo itemInfo = ItemInfoCache.getInstance().get(this.CS);
        if (itemInfo == null) {
            this.sendPacket(ActionFail.STATIC);
        } else {
            this.sendPacket((L2GameServerPacket)new ExRpItemLink(itemInfo));
        }
    }
}

