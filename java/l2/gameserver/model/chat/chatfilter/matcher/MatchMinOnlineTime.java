/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchMinOnlineTime
implements ChatFilterMatcher {
    private final long Hd;

    public MatchMinOnlineTime(int n) {
        this.Hd = (long)n * 1000L;
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        return player.getOnlineTime() < this.Hd;
    }
}

