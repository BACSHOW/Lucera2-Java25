/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class AbnormalStatusUpdate
extends L2GameServerPacket {
    public static final int INFINITIVE_EFFECT = -1;
    private List<Effect> Fe = new ArrayList<Effect>();

    public void addEffect(int n, int n2, int n3) {
        this.Fe.add(new Effect(n, n2, n3));
    }

    @Override
    protected final void writeImpl() {
        this.writeC(127);
        this.writeH(this.Fe.size());
        for (Effect effect : this.Fe) {
            this.writeD(effect.skillId);
            this.writeH(effect.dat);
            this.writeD(effect.duration);
        }
    }

    class Effect {
        int skillId;
        int dat;
        int duration;

        public Effect(int n, int n2, int n3) {
            this.skillId = n;
            this.dat = n2;
            this.duration = n3;
        }
    }
}

