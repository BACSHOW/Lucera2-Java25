/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class AcquireSkillInfo
extends L2GameServerPacket {
    private SkillLearn adx;
    private AcquireType abb;
    private List<Require> ady = Collections.emptyList();

    public AcquireSkillInfo(AcquireType acquireType, SkillLearn skillLearn) {
        this(acquireType, skillLearn, skillLearn.getItemId(), (int)skillLearn.getItemCount());
    }

    public AcquireSkillInfo(AcquireType acquireType, SkillLearn skillLearn, int n, int n2) {
        this.abb = acquireType;
        this.adx = skillLearn;
        if (n != 0) {
            this.ady = new ArrayList<Require>(1);
            this.ady.add(new Require(99, n, n2, 50));
        }
    }

    @Override
    public void writeImpl() {
        this.writeC(139);
        this.writeD(this.adx.getId());
        this.writeD(this.adx.getLevel());
        this.writeD(this.adx.getCost());
        this.writeD(this.abb.ordinal());
        this.writeD(this.ady.size());
        for (Require require : this.ady) {
            this.writeD(require.type);
            this.writeD(require.itemId);
            this.writeD((int)require.count);
            this.writeD(require.unk);
        }
    }

    private static class Require {
        public int itemId;
        public long count;
        public int type;
        public int unk;

        public Require(int n, int n2, long l, int n3) {
            this.itemId = n2;
            this.type = n;
            this.count = l;
            this.unk = n3;
        }
    }
}

