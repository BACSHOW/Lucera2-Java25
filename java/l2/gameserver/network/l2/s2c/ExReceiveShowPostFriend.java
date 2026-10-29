/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.napile.primitive.maps.IntObjectMap;

public class ExReceiveShowPostFriend
extends L2GameServerPacket {
    private IntObjectMap<String> ahJ;

    public ExReceiveShowPostFriend(Player player) {
        this.ahJ = player.getPostFriends();
    }

    @Override
    public void writeImpl() {
        this.writeEx(211);
        this.writeD(this.ahJ.size());
        for (String string : this.ahJ.values()) {
            this.writeS(string);
        }
    }
}

