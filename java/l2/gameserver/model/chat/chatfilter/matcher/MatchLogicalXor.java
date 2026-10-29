/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchLogicalXor
implements ChatFilterMatcher {
    private final ChatFilterMatcher[] Ph;

    public MatchLogicalXor(ChatFilterMatcher[] chatFilterMatcherArray) {
        this.Ph = chatFilterMatcherArray;
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        boolean bl = false;
        for (ChatFilterMatcher chatFilterMatcher : this.Ph) {
            if (!chatFilterMatcher.isMatch(player, chatType, string, player2)) continue;
            if (bl) {
                return false;
            }
            bl = true;
        }
        return bl;
    }
}

