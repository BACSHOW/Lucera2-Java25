/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands;

import l2.gameserver.model.Player;

public interface IAdminCommandHandler {
    public boolean useAdminCommand(Enum var1, String[] var2, String var3, Player var4);

    public Enum[] getAdminCommandEnum();
}

