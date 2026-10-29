/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.usercommands;

import l2.gameserver.model.Player;

public interface IUserCommandHandler {
    public boolean useUserCommand(int var1, Player var2);

    public int[] getUserCommandList();
}

