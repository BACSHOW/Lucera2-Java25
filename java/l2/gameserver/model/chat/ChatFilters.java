/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.chat.chatfilter.ChatFilter;
import org.apache.commons.lang3.ArrayUtils;

public class ChatFilters
extends AbstractHolder {
    private static final ChatFilters OZ = new ChatFilters();
    private ChatFilter[] Pa = new ChatFilter[0];

    public static final ChatFilters getinstance() {
        return OZ;
    }

    private ChatFilters() {
    }

    public ChatFilter[] getFilters() {
        return this.Pa;
    }

    public void add(ChatFilter chatFilter) {
        this.Pa = ArrayUtils.add(this.Pa, chatFilter);
    }

    @Override
    public void log() {
        this.info(String.format("loaded %d filter(s).", this.size()));
    }

    @Override
    public int size() {
        return this.Pa.length;
    }

    @Override
    public void clear() {
        this.Pa = new ChatFilter[0];
    }
}

