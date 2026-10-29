/*
 * Decompiled with CFR 0.152.
 */
package handler.petition;

import l2.gameserver.handler.petition.IPetitionHandler;
import l2.gameserver.model.Player;

public class SimplePetitionHandler
implements IPetitionHandler {
    @Override
    public void handle(Player player, int n, String string) {
        player.sendMessage(string);
    }
}

