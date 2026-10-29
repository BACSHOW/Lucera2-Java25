/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchLogicalNot
implements ChatFilterMatcher {
    private final ChatFilterMatcher Pi;

    public MatchLogicalNot(ChatFilterMatcher chatFilterMatcher) {
        this.Pi = chatFilterMatcher;
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        return !this.Pi.isMatch(player, chatType, string, player2);
    }
}

