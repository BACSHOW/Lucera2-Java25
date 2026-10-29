/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.manor.CropProcure;

public class ExShowProcureCropDetail
extends L2GameServerPacket {
    private int _cropId;
    private Map<Integer, CropProcure> aie;

    public ExShowProcureCropDetail(int n) {
        this._cropId = n;
        this.aie = new TreeMap<Integer, CropProcure>();
        List<Castle> list = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (Castle castle : list) {
            CropProcure cropProcure = castle.getCrop(this._cropId, 0);
            if (cropProcure == null || cropProcure.getAmount() <= 0L) continue;
            this.aie.put(castle.getId(), cropProcure);
        }
    }

    @Override
    public void writeImpl() {
        this.writeEx(34);
        this.writeD(this._cropId);
        this.writeD(this.aie.size());
        for (int n : this.aie.keySet()) {
            CropProcure cropProcure = this.aie.get(n);
            this.writeD(n);
            this.writeD((int)cropProcure.getAmount());
            this.writeD((int)cropProcure.getPrice());
            this.writeC(cropProcure.getReward());
        }
    }
}

