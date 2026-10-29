/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.chat.chatfilter;

import l2.gameserver.network.l2.components.ChatType;

public class ChatMsg {
    public final ChatType chatType;
    public final int recipient;
    public final int msgHashcode;
    public final int time;

    public ChatMsg(ChatType chatType, int n, int n2, int n3) {
        this.chatType = chatType;
        this.recipient = n;
        this.msgHashcode = n2;
        this.time = n3;
    }
}

