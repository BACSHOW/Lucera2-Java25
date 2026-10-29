/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class StaticObject
extends L2GameServerPacket {
    private final int aec;
    private final int CS;
    private final int _type;
    private final int amP;
    private final int VT;
    private final int amQ;
    private final int amR;
    private final int _maxHp;
    private final int amS;
    private final int amT;
    private final int amU;

    public StaticObject(StaticObjectInstance staticObjectInstance) {
        this.aec = staticObjectInstance.getUId();
        this.CS = staticObjectInstance.getObjectId();
        this._type = 0;
        this.amP = 1;
        this.VT = staticObjectInstance.getMeshIndex();
        this.amQ = 0;
        this.amR = 0;
        this._maxHp = 0;
        this.amS = 0;
        this.amT = 0;
        this.amU = 0;
    }

    public StaticObject(DoorInstance doorInstance, Player player) {
        this.aec = doorInstance.getDoorId();
        this.CS = doorInstance.getObjectId();
        this._type = 1;
        this.amP = doorInstance.getTemplate().isTargetable() ? 1 : 0;
        this.VT = 1;
        this.amQ = doorInstance.isOpen() ? 0 : 1;
        this.amR = doorInstance.isAutoAttackable(player) ? 1 : 0;
        this.amS = (int)doorInstance.getCurrentHp();
        this._maxHp = doorInstance.getMaxHp();
        this.amT = doorInstance.isHPVisible() ? 1 : 0;
        this.amU = doorInstance.getDamage();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(153);
        this.writeD(this.aec);
        this.writeD(this.CS);
    }
}

