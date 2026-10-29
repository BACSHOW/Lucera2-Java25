/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SetupGauge
extends L2GameServerPacket {
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int CYAN = 2;
    private int CT;
    private int aml;
    private int _time;

    public SetupGauge(Creature creature, int n, int n2) {
        this.CT = creature.getObjectId();
        this.aml = n;
        this._time = n2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(109);
        this.writeD(this.aml);
        this.writeD(this._time);
        this.writeD(this._time);
    }
}

