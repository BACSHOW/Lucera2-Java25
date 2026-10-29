/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchMinJobLevel
implements ChatFilterMatcher {
    private final int Pk;

    public MatchMinJobLevel(int n) {
        this.Pk = n;
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        return player.getClassId().level() < this.Pk;
    }
}

