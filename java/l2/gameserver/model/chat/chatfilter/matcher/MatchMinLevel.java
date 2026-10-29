/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchMinLevel
implements ChatFilterMatcher {
    private final int _level;

    public MatchMinLevel(int n) {
        this._level = n;
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        return player.getLevel() < this._level;
    }
}

