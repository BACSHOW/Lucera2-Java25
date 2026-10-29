/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.Set;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExMpccPartymasterList
extends L2GameServerPacket {
    private Set<String> _members = Collections.emptySet();

    public ExMpccPartymasterList(Set<String> set) {
        this._members = set;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(162);
        this.writeD(this._members.size());
        for (String string : this._members) {
            this.writeS(string);
        }
    }
}

