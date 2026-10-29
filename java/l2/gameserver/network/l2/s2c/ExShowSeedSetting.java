/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Manor;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.manor.SeedProduction;

public class ExShowSeedSetting
extends L2GameServerPacket {
    private int Gh;
    private int _count;
    private long[] aim;

    public ExShowSeedSetting(int n) {
        this.Gh = n;
        Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this.Gh);
        List<Manor.SeedData> list = Manor.getInstance().getSeedsForCastle(this.Gh);
        this._count = list.size();
        this.aim = new long[this._count * 12];
        int n2 = 0;
        for (Manor.SeedData seedData : list) {
            this.aim[n2 * 12 + 0] = seedData.getId();
            this.aim[n2 * 12 + 1] = seedData.getLevel();
            this.aim[n2 * 12 + 2] = seedData.getReward(1);
            this.aim[n2 * 12 + 3] = seedData.getReward(2);
            this.aim[n2 * 12 + 4] = seedData.getSeedLimit();
            this.aim[n2 * 12 + 5] = Manor.getInstance().getSeedBuyPrice(seedData.getId());
            int n3 = Manor.getInstance().getSeedBasicPrice(seedData.getId());
            this.aim[n2 * 12 + 6] = n3 * 60 / 100;
            this.aim[n2 * 12 + 7] = n3 * 10;
            SeedProduction seedProduction = castle.getSeed(seedData.getId(), 0);
            if (seedProduction != null) {
                this.aim[n2 * 12 + 8] = seedProduction.getStartProduce();
                this.aim[n2 * 12 + 9] = seedProduction.getPrice();
            } else {
                this.aim[n2 * 12 + 8] = 0L;
                this.aim[n2 * 12 + 9] = 0L;
            }
            seedProduction = castle.getSeed(seedData.getId(), 1);
            if (seedProduction != null) {
                this.aim[n2 * 12 + 10] = seedProduction.getStartProduce();
                this.aim[n2 * 12 + 11] = seedProduction.getPrice();
            } else {
                this.aim[n2 * 12 + 10] = 0L;
                this.aim[n2 * 12 + 11] = 0L;
            }
            ++n2;
        }
    }

    @Override
    public void writeImpl() {
        this.writeEx(31);
        this.writeD(this.Gh);
        this.writeD(this._count);
        for (int i = 0; i < this._count; ++i) {
            this.writeD((int)this.aim[i * 12 + 0]);
            this.writeD((int)this.aim[i * 12 + 1]);
            this.writeC(1);
            this.writeD((int)this.aim[i * 12 + 2]);
            this.writeC(1);
            this.writeD((int)this.aim[i * 12 + 3]);
            this.writeD((int)this.aim[i * 12 + 4]);
            this.writeD((int)this.aim[i * 12 + 5]);
            this.writeD((int)this.aim[i * 12 + 6]);
            this.writeD((int)this.aim[i * 12 + 7]);
            this.writeD((int)this.aim[i * 12 + 8]);
            this.writeD((int)this.aim[i * 12 + 9]);
            this.writeD((int)this.aim[i * 12 + 10]);
            this.writeD((int)this.aim[i * 12 + 11]);
        }
    }
}

