/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.chat.ChatFilters;
import l2.gameserver.model.chat.chatfilter.ChatFilter;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2FriendSay;
import l2.gameserver.utils.Log;

public class RequestSendL2FriendSay
extends L2GameClientPacket {
    private String acK;
    private String acL;

    @Override
    protected void readImpl() {
        this.acK = this.readS(2048);
        this.acL = this.readS(16);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Player player2 = World.getPlayer(this.acL);
        if (player2 == null) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            return;
        }
        if (player2.isBlockAll()) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
            return;
        }
        if (!player.getPlayerAccess().CanAnnounce) {
            block5: for (ChatFilter chatFilter : ChatFilters.getinstance().getFilters()) {
                if (!chatFilter.isMatch(player, ChatType.L2FRIEND, this.acK, player2)) continue;
                switch (chatFilter.getAction()) {
                    case 1: {
                        player.updateNoChannel((long)Integer.parseInt(chatFilter.getValue()) * 1000L);
                        break block5;
                    }
                    case 2: {
                        player.sendMessage(new CustomMessage(chatFilter.getValue(), player, new Object[0]));
                        return;
                    }
                    case 3: {
                        this.acK = chatFilter.getValue();
                        break block5;
                    }
                    default: {
                        continue block5;
                    }
                }
            }
        }
        if (player.getNoChannel() > 0L && ArrayUtils.contains(Config.BAN_CHANNEL_LIST, ChatType.L2FRIEND)) {
            if (player.getNoChannelRemained() > 0L) {
                long l = player.getNoChannelRemained() / 60000L + 1L;
                player.sendMessage(new CustomMessage("common.ChatBanned", player, new Object[0]).addNumber(l));
                return;
            }
            player.updateNoChannel(0L);
        }
        if (!player.getFriendList().getList().containsKey(player2.getObjectId())) {
            return;
        }
        if (player.canTalkWith(player2)) {
            L2FriendSay l2FriendSay = new L2FriendSay(player.getName(), this.acL, this.acK);
            player2.sendPacket((IStaticPacket)l2FriendSay);
            Log.LogChat("FRIENDTELL", player.getName(), this.acL, this.acK, 0);
        }
    }
}

