/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class ChatFilter {
    public static final int ACTION_NONE = 0;
    public static final int ACTION_BAN_CHAT = 1;
    public static final int ACTION_WARN_MSG = 2;
    public static final int ACTION_REPLACE_MSG = 3;
    public static final int ACTION_REDIRECT_MSG = 4;
    private final ChatFilterMatcher Pb;
    private final int fj;
    private final String Pc;

    public ChatFilter(ChatFilterMatcher chatFilterMatcher, int n, String string) {
        this.Pb = chatFilterMatcher;
        this.fj = n;
        this.Pc = string;
    }

    public int getAction() {
        return this.fj;
    }

    public String getValue() {
        return this.Pc;
    }

    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        return this.Pb.isMatch(player, chatType, string, player2);
    }
}

