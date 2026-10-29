/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Manor;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.manor.CropProcure;

public class ExShowCropSetting
extends L2GameServerPacket {
    private int Gh;
    private int _count;
    private long[] aid;

    public ExShowCropSetting(int n) {
        this.Gh = n;
        Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this.Gh);
        List<Manor.SeedData> list = Manor.getInstance().getCropsForCastle(this.Gh);
        this._count = list.size();
        this.aid = new long[this._count * 14];
        int n2 = 0;
        for (Manor.SeedData seedData : list) {
            this.aid[n2 * 14 + 0] = seedData.getCrop();
            this.aid[n2 * 14 + 1] = seedData.getLevel();
            this.aid[n2 * 14 + 2] = seedData.getReward(1);
            this.aid[n2 * 14 + 3] = seedData.getReward(2);
            this.aid[n2 * 14 + 4] = seedData.getCropLimit();
            this.aid[n2 * 14 + 5] = 0L;
            int n3 = Manor.getInstance().getCropBasicPrice(seedData.getCrop());
            this.aid[n2 * 14 + 6] = n3 * 60 / 100;
            this.aid[n2 * 14 + 7] = n3 * 10;
            CropProcure cropProcure = castle.getCrop(seedData.getCrop(), 0);
            if (cropProcure != null) {
                this.aid[n2 * 14 + 8] = cropProcure.getStartAmount();
                this.aid[n2 * 14 + 9] = cropProcure.getPrice();
                this.aid[n2 * 14 + 10] = cropProcure.getReward();
            } else {
                this.aid[n2 * 14 + 8] = 0L;
                this.aid[n2 * 14 + 9] = 0L;
                this.aid[n2 * 14 + 10] = 0L;
            }
            cropProcure = castle.getCrop(seedData.getCrop(), 1);
            if (cropProcure != null) {
                this.aid[n2 * 14 + 11] = cropProcure.getStartAmount();
                this.aid[n2 * 14 + 12] = cropProcure.getPrice();
                this.aid[n2 * 14 + 13] = cropProcure.getReward();
            } else {
                this.aid[n2 * 14 + 11] = 0L;
                this.aid[n2 * 14 + 12] = 0L;
                this.aid[n2 * 14 + 13] = 0L;
            }
            ++n2;
        }
    }

    @Override
    public void writeImpl() {
        this.writeEx(32);
        this.writeD(this.Gh);
        this.writeD(this._count);
        for (int i = 0; i < this._count; ++i) {
            this.writeD((int)this.aid[i * 14 + 0]);
            this.writeD((int)this.aid[i * 14 + 1]);
            this.writeC(1);
            this.writeD((int)this.aid[i * 14 + 2]);
            this.writeC(1);
            this.writeD((int)this.aid[i * 14 + 3]);
            this.writeD((int)this.aid[i * 14 + 4]);
            this.writeD((int)this.aid[i * 14 + 5]);
            this.writeD((int)this.aid[i * 14 + 6]);
            this.writeD((int)this.aid[i * 14 + 7]);
            this.writeD((int)this.aid[i * 14 + 8]);
            this.writeD((int)this.aid[i * 14 + 9]);
            this.writeC((int)this.aid[i * 14 + 10]);
            this.writeD((int)this.aid[i * 14 + 11]);
            this.writeD((int)this.aid[i * 14 + 12]);
            this.writeC((int)this.aid[i * 14 + 13]);
        }
    }
}

