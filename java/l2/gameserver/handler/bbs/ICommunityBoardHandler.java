/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.bbs;

import l2.gameserver.model.Player;

public interface ICommunityBoardHandler {
    public String[] getBypassCommands();

    public void onBypassCommand(Player var1, String var2);

    public void onWriteCommand(Player var1, String var2, String var3, String var4, String var5, String var6, String var7);
}

