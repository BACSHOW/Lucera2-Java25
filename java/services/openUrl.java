/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.BrowserBypassPacket;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

public class openUrl
extends Functions
implements IVoicedCommandHandler,
ScriptFile {
    private final String[] bYK = new String[]{"url"};

    @Override
    public void onLoad() {
        if (Config.CUSTOM_PATCH_OPTIONS_ENABLE) {
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String[] getVoicedCommandList() {
        return this.bYK;
    }

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (string.equals("url") && string2.startsWith("http") || string2.startsWith("skype:")) {
            player.sendPacket((IStaticPacket)new BrowserBypassPacket(string2));
            return true;
        }
        return false;
    }
}

