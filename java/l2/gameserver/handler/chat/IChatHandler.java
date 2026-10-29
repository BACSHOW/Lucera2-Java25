/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.chat;

import l2.gameserver.network.l2.components.ChatType;

public interface IChatHandler {
    public void say();

    public ChatType getType();
}

