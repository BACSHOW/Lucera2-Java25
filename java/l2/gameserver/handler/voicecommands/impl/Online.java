/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;

public class Online
extends Functions
implements IVoicedCommandHandler {
    private final String[] zF = new String[]{"online"};

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (Config.SERVICES_ONLINE_COMMAND_ENABLE || player.isGM()) {
            player.sendMessage(new CustomMessage("scripts.commands.user.online.service", player, new Object[0]).addNumber(Math.round((double)GameObjectsStorage.getAllPlayersCount() * Config.SERVICE_COMMAND_MULTIPLIER)));
            return false;
        }
        return true;
    }
}

