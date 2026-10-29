/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;

public class Ping
implements IVoicedCommandHandler {
    private final String[] zF = new String[]{"ping"};

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.ALT_ALLOW_PING_COMMAND) {
            return false;
        }
        if (string.equals("ping")) {
            player.sendMessage("Ping: " + player.getNetConnection().getPing() + " ms");
            return true;
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }
}

