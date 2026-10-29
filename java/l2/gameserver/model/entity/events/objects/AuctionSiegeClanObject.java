/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.pledge.Clan;

public class AuctionSiegeClanObject
extends SiegeClanObject {
    private long QS;

    public AuctionSiegeClanObject(String string, Clan clan, long l) {
        this(string, clan, l, System.currentTimeMillis());
    }

    public AuctionSiegeClanObject(String string, Clan clan, long l, long l2) {
        super(string, clan, l, l2);
        this.QS = l;
    }

    @Override
    public long getParam() {
        return this.QS;
    }

    public void setParam(long l) {
        this.QS = l;
    }
}

