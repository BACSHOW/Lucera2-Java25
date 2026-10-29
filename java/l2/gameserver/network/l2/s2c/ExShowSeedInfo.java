/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.gameserver.model.Manor;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.manor.SeedProduction;

public class ExShowSeedInfo
extends L2GameServerPacket {
    private List<SeedProduction> ail;
    private int Gh;

    public ExShowSeedInfo(int n, List<SeedProduction> list) {
        this.Gh = n;
        this.ail = list;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(28);
        this.writeC(0);
        this.writeD(this.Gh);
        this.writeD(0);
        this.writeD(this.ail.size());
        for (SeedProduction seedProduction : this.ail) {
            this.writeD(seedProduction.getId());
            this.writeD((int)seedProduction.getCanProduce());
            this.writeD((int)seedProduction.getStartProduce());
            this.writeD((int)seedProduction.getPrice());
            this.writeD(Manor.getInstance().getSeedLevel(seedProduction.getId()));
            this.writeC(1);
            this.writeD(Manor.getInstance().getRewardItemBySeed(seedProduction.getId(), 1));
            this.writeC(1);
            this.writeD(Manor.getInstance().getRewardItemBySeed(seedProduction.getId(), 2));
        }
    }
}

