/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCursedWeaponList
extends L2GameServerPacket {
    private int[] agy = CursedWeaponsManager.getInstance().getCursedWeaponsIds();

    @Override
    protected final void writeImpl() {
        this.writeEx(69);
        this.writeDD(this.agy, true);
    }
}

