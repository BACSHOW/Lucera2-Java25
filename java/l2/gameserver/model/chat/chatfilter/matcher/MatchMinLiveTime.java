/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchMinLiveTime
implements ChatFilterMatcher {
    private final long Hc;

    public MatchMinLiveTime(int n) {
        this.Hc = (long)n * 1000L;
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        return System.currentTimeMillis() - player.getCreateTime() < this.Hc;
    }
}

