/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class DoorStatusUpdate
extends L2GameServerPacket {
    private final int afx;
    private final boolean afy;
    private final int _dmg;
    private final boolean aft;
    private final int afz;
    private final int afA;
    private final int _maxHp;

    public DoorStatusUpdate(DoorInstance doorInstance, Player player) {
        this.afx = doorInstance.getObjectId();
        this.afz = doorInstance.getDoorId();
        this.afy = !doorInstance.isOpen();
        this.aft = doorInstance.isAutoAttackable(player);
        this.afA = (int)doorInstance.getCurrentHp();
        this._maxHp = doorInstance.getMaxHp();
        this._dmg = doorInstance.getDamage();
    }

    @Override
    protected void writeImpl() {
        this.writeC(77);
        this.writeD(this.afx);
        this.writeD(this.afy);
        this.writeD(this._dmg);
        this.writeD(this.aft);
        this.writeD(this.afz);
        this.writeD(this._maxHp);
        this.writeD(this.afA);
    }
}

