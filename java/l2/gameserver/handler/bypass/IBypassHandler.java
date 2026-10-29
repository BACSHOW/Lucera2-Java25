/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.bypass;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;

public interface IBypassHandler {
    public String[] getBypasses();

    public void onBypassFeedback(NpcInstance var1, Player var2, String var3);
}

