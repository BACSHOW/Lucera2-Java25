/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExConfirmAddingPostFriend
extends L2GameServerPacket {
    public static int NAME_IS_NOT_EXISTS = 0;
    public static int SUCCESS = 1;
    public static int PREVIOS_NAME_IS_BEEN_REGISTERED = -1;
    public static int NAME_IS_NOT_EXISTS2 = -2;
    public static int LIST_IS_FULL = -3;
    public static int ALREADY_ADDED = -4;
    public static int NAME_IS_NOT_REGISTERED = -4;
    private String _name;
    private int oJ;

    public ExConfirmAddingPostFriend(String string, int n) {
        this._name = string;
        this.oJ = n;
    }

    @Override
    public void writeImpl() {
        this.writeEx(210);
        this.writeS(this._name);
        this.writeD(this.oJ);
    }
}

