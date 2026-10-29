/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;

public class Debug
implements IVoicedCommandHandler {
    private final String[] zF = new String[]{"debug"};

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.ALT_DEBUG_ENABLED) {
            return false;
        }
        if (player.isDebug()) {
            player.setDebug(false);
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Disabled", player, new Object[0]));
        } else {
            player.setDebug(true);
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Enabled", player, new Object[0]));
        }
        return true;
    }
}

