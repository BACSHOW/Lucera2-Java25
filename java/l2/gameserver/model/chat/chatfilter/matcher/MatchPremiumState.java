/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchPremiumState
implements ChatFilterMatcher {
    private final boolean Pl;

    public MatchPremiumState(boolean bl) {
        this.Pl = bl;
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        return this.Pl || !player.hasBonus();
    }
}

