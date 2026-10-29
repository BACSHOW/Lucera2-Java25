/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.chat;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.handler.chat.IChatHandler;
import l2.gameserver.network.l2.components.ChatType;

public class ChatHandler
extends AbstractHolder {
    private static final ChatHandler zs = new ChatHandler();
    private IChatHandler[] zt = new IChatHandler[ChatType.VALUES.length];

    public static ChatHandler getInstance() {
        return zs;
    }

    private ChatHandler() {
    }

    public void register(IChatHandler iChatHandler) {
        this.zt[iChatHandler.getType().ordinal()] = iChatHandler;
    }

    public IChatHandler getHandler(ChatType chatType) {
        return this.zt[chatType.ordinal()];
    }

    @Override
    public int size() {
        return this.zt.length;
    }

    @Override
    public void clear() {
    }
}

