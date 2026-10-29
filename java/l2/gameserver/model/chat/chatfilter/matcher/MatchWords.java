/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter.matcher;

import java.util.regex.Pattern;
import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchWords
implements ChatFilterMatcher {
    public final Pattern[] _patterns;

    public MatchWords(String[] stringArray) {
        this._patterns = new Pattern[stringArray.length];
        for (int i = 0; i < stringArray.length; ++i) {
            this._patterns[i] = Pattern.compile(stringArray[i], 66);
        }
    }

    @Override
    public boolean isMatch(Player player, ChatType chatType, String string, Player player2) {
        for (Pattern pattern : this._patterns) {
            if (!pattern.matcher(string).find()) continue;
            return true;
        }
        return false;
    }
}

