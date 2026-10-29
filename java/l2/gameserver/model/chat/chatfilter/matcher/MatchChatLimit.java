/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import java.util.Deque;
import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.model.chat.chatfilter.ChatMsg;
import l2.gameserver.network.l2.components.ChatType;

public class MatchChatLimit
implements ChatFilterMatcher {
    private final int Pe;
    private final int Pf;
    private final int Pg;

    public MatchChatLimit(int n, int n2, int n3) {
        this.Pe = n;
        this.Pf = n2;
        this.Pg = n3;
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        int n;
        int n2 = n = (int)(System.currentTimeMillis() / 1000L);
        int n3 = 0;
        Deque<ChatMsg> deque = player.getMessageBucket();
        Iterator<ChatMsg> iterator = deque.descendingIterator();
        while (iterator.hasNext()) {
            ChatMsg chatMsg = iterator.next();
            if (chatMsg.chatType != chatType) continue;
            n2 = chatMsg.time;
            if (this.Pg != ++n3) continue;
            break;
        }
        return this.Pg <= (n3 -= (n - n2) / this.Pf * this.Pe);
    }
}

