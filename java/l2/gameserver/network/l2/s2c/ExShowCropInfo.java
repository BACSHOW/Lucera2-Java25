/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.gameserver.model.Manor;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.manor.CropProcure;

public class ExShowCropInfo
extends L2GameServerPacket {
    private List<CropProcure> aic;
    private int Gh;

    public ExShowCropInfo(int n, List<CropProcure> list) {
        this.Gh = n;
        this.aic = list;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(29);
        this.writeC(0);
        this.writeD(this.Gh);
        this.writeD(0);
        this.writeD(this.aic.size());
        for (CropProcure cropProcure : this.aic) {
            this.writeD(cropProcure.getId());
            this.writeD((int)cropProcure.getAmount());
            this.writeD((int)cropProcure.getStartAmount());
            this.writeD((int)cropProcure.getPrice());
            this.writeC(cropProcure.getReward());
            this.writeD(Manor.getInstance().getSeedLevelByCrop(cropProcure.getId()));
            this.writeC(1);
            this.writeD(Manor.getInstance().getRewardItem(cropProcure.getId(), 1));
            this.writeC(1);
            this.writeD(Manor.getInstance().getRewardItem(cropProcure.getId(), 2));
        }
    }
}

