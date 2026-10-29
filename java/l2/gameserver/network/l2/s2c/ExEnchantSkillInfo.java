/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.LinkedList;
import java.util.List;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ExEnchantSkillInfo
extends L2GameServerPacket {
    private final int _skillId;
    private final int _skillLevel;
    private final int _sp;
    private final long _exp;
    private final int cJ;
    private final List<Pair<Integer, Long>> agA;

    public ExEnchantSkillInfo(int n, int n2, int n3, long l, int n4) {
        this._skillId = n;
        this._skillLevel = n2;
        this._sp = n3;
        this._exp = l;
        this.cJ = n4;
        this.agA = new LinkedList<Pair<Integer, Long>>();
    }

    public void addNeededItem(int n, long l) {
        this.agA.add(ImmutablePair.of(n, l));
    }

    @Override
    protected void writeImpl() {
        this.writeEx(24);
        this.writeD(this._skillId);
        this.writeD(this._skillLevel);
        this.writeD(this._sp);
        this.writeQ(this._exp);
        this.writeD(this.cJ);
        if (this.agA.isEmpty()) {
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
        } else {
            this.writeD(this.agA.size());
            for (Pair<Integer, Long> pair : this.agA) {
                this.writeD(4);
                this.writeD(pair.getKey());
                this.writeD(pair.getValue().intValue());
                this.writeD(0);
            }
        }
    }
}

