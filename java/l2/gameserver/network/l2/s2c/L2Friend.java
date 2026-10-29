/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class L2Friend
extends L2GameServerPacket {
    private boolean ajx;
    private boolean pb;
    private String _name;
    private int ajy;

    public L2Friend(Player player, boolean bl) {
        this.ajx = bl;
        this._name = player.getName();
        this.ajy = player.getObjectId();
        this.pb = true;
    }

    public L2Friend(String string, boolean bl, boolean bl2, int n) {
        this._name = string;
        this.ajx = bl;
        this.ajy = n;
        this.pb = bl2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(251);
        this.writeD(this.ajx ? 1 : 3);
        this.writeD(0);
        this.writeS(this._name);
        this.writeD(this.pb ? 1 : 0);
        this.writeD(this.ajy);
    }
}

