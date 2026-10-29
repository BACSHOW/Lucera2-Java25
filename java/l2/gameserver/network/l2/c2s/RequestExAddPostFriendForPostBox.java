/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.dao.CharacterPostFriendDAO;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExConfirmAddingPostFriend;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import org.napile.primitive.maps.IntObjectMap;

public class RequestExAddPostFriendForPostBox
extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() throws Exception {
        this._name = this.readS(Config.CNAME_MAXLEN);
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        int n = CharacterDAO.getInstance().getObjectIdByName(this._name);
        if (n == 0) {
            player.sendPacket((IStaticPacket)new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.NAME_IS_NOT_EXISTS));
            return;
        }
        if (this._name.equalsIgnoreCase(player.getName())) {
            player.sendPacket((IStaticPacket)new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.NAME_IS_NOT_REGISTERED));
            return;
        }
        IntObjectMap<String> intObjectMap = player.getPostFriends();
        if (intObjectMap.size() >= 100) {
            player.sendPacket((IStaticPacket)new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.LIST_IS_FULL));
            return;
        }
        if (intObjectMap.containsKey(n)) {
            player.sendPacket((IStaticPacket)new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.ALREADY_ADDED));
            return;
        }
        CharacterPostFriendDAO.getInstance().insert(player, n);
        intObjectMap.put(n, CharacterDAO.getInstance().getNameByObjectId(n));
        player.sendPacket(new IStaticPacket[]{new SystemMessage2(SystemMsg.S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST).addString(this._name), new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.SUCCESS)});
    }
}

