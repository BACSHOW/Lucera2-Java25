/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.ChatType;

public interface ChatFilterMatcher {
    public boolean isMatch(Player var1, ChatType var2, String var3, Player var4);
}

