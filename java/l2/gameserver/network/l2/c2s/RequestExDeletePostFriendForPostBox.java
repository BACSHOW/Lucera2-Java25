/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.dao.CharacterPostFriendDAO;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.maps.IntObjectMap;

public class RequestExDeletePostFriendForPostBox
extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() throws Exception {
        this._name = this.readS();
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (StringUtils.isEmpty(this._name)) {
            return;
        }
        int n = 0;
        IntObjectMap<String> intObjectMap = player.getPostFriends();
        for (IntObjectMap.Entry<String> entry : intObjectMap.entrySet()) {
            if (!entry.getValue().equalsIgnoreCase(this._name)) continue;
            n = entry.getKey();
        }
        if (n == 0) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
            return;
        }
        player.getPostFriends().remove(n);
        CharacterPostFriendDAO.getInstance().delete(player, n);
        player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST).addString(this._name));
    }
}

