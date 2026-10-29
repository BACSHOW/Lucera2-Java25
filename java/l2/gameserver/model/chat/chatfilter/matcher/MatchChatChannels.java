/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchChatChannels
implements ChatFilterMatcher {
    private final ChatType[] Pd;

    public MatchChatChannels(ChatType[] chatTypeArray) {
        this.Pd = chatTypeArray;
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        for (ChatType chatType2 : this.Pd) {
            if (chatType2 != chatType) continue;
            return true;
        }
        return false;
    }
}

