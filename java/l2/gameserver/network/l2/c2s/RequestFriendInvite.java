/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.FriendAddRequest;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import org.apache.commons.lang3.StringUtils;

public class RequestFriendInvite
extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        this._name = this.readS(16);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || StringUtils.isEmpty(this._name)) {
            return;
        }
        if (player.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (player.isProcessingRequest()) {
            player.sendPacket((IStaticPacket)SystemMsg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }
        Player player2 = World.getPlayer(this._name);
        if (player2 == null) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME);
            return;
        }
        if (player2 == player) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
            return;
        }
        if (player2.isBlockAll() || player2.isInBlockList(player) || player2.getMessageRefusal()) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
            return;
        }
        if (player.getFriendList().getList().containsKey(player2.getObjectId())) {
            player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1_IS_ALREADY_ON_YOUR_FRIEND_LIST).addName(player2));
            return;
        }
        if (player.getFriendList().getList().size() >= 128) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_CAN_ONLY_ENTER_UP_128_NAMES_IN_YOUR_FRIENDS_LIST);
            return;
        }
        if (player2.getFriendList().getList().size() >= 128) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_FRIENDS_LIST_OF_THE_PERSON_YOU_ARE_TRYING_TO_ADD_IS_FULL_SO_REGISTRATION_IS_NOT_POSSIBLE);
            return;
        }
        if (player2.isOlyParticipant()) {
            player.sendPacket((IStaticPacket)SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
            return;
        }
        new Request(Request.L2RequestType.FRIEND, player, player2).setTimeout(10000L);
        player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1_HAS_SENT_A_FRIEND_REQUEST).addName(player2));
        player2.sendPacket(new IStaticPacket[]{new SystemMessage2(SystemMsg.C1_HAS_SENT_A_FRIEND_REQUEST).addName(player), new FriendAddRequest(player.getName())});
    }
}

