/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class DropItem
extends L2GameServerPacket {
    private Location cH;
    private int afo;
    private int afB;
    private int afC;
    private int afD;
    private long CE;

    public DropItem(ItemInstance itemInstance, int n) {
        this.afo = n;
        this.afB = itemInstance.getObjectId();
        this.afC = itemInstance.getItemId();
        this.cH = itemInstance.getLoc();
        this.afD = itemInstance.isStackable() ? 1 : 0;
        this.CE = itemInstance.getCount();
    }

    public DropItem(int n, int n2, int n3, Location location, boolean bl, int n4) {
        this.afo = n;
        this.afB = n2;
        this.afC = n3;
        this.cH = location.clone();
        this.afD = bl ? 1 : 0;
        this.CE = n4;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(12);
        this.writeD(this.afo);
        this.writeD(this.afB);
        this.writeD(this.afC);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z + Config.CLIENT_Z_SHIFT);
        this.writeD(this.afD);
        this.writeD((int)this.CE);
        this.writeD(1);
    }
}

