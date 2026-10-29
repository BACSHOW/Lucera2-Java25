/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class DoorInfo
extends L2GameServerPacket {
    private int _id;
    private int afs;
    private final boolean aft;
    private int unk1;
    private int afu;
    private int afv;
    private int _maxHp;
    private int afw;
    private int _damage;

    public DoorInfo(DoorInstance doorInstance, Player player) {
        this._id = doorInstance.getDoorId();
        this.afs = doorInstance.getObjectId();
        this.unk1 = 1;
        this.afu = doorInstance.isOpen() ? 0 : 1;
        this.afv = (int)doorInstance.getCurrentHp();
        this._maxHp = doorInstance.getMaxHp();
        this.afw = doorInstance.isHPVisible() ? 1 : 0;
        this._damage = doorInstance.getDamage();
        this.aft = doorInstance.isAutoAttackable(player);
    }

    @Override
    protected final void writeImpl() {
        this.writeC(76);
        this.writeD(this.afs);
        this.writeD(this._id);
        this.writeD(this.aft);
        this.writeD(this.unk1);
        this.writeD(this.afu);
        this.writeD(this.afv);
        this.writeD(this._maxHp);
        this.writeD(this.afw);
        this.writeD(this._damage);
    }
}

