/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.base.Experience;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.scripts.Functions;

public class Help
extends Functions
implements IVoicedCommandHandler {
    private String[] zF = new String[]{"exp", "whereis", "help"};

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.ALT_ALLOW_HELP_COMMAND) {
            return false;
        }
        if ((string = string.intern()).equalsIgnoreCase("help")) {
            return this.c(string, player, string2);
        }
        if (string.equalsIgnoreCase("whereis")) {
            return this.b(string, player, string2);
        }
        if (string.equalsIgnoreCase("exp")) {
            return this.a(string, player, string2);
        }
        return false;
    }

    private boolean a(String string, Player player, String string2) {
        if (player.getLevel() >= (player.isSubClassActive() ? Experience.getMaxSubLevel() : Experience.getMaxLevel())) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Help.MaxLevel", player, new Object[0]));
        } else {
            long l = Experience.LEVEL[player.getLevel() + 1] - player.getExp();
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Help.ExpLeft", player, new Object[0]).addNumber(l));
        }
        return true;
    }

    private boolean b(String string, Player player, String string2) {
        Player player2 = World.getPlayer(string2);
        if (player2 == null) {
            return false;
        }
        if (player2.getParty() == player.getParty() || player2.getClan() == player.getClan()) {
            RadarControl radarControl = new RadarControl(0, 1, player2.getLoc());
            player.sendPacket((IStaticPacket)radarControl);
            return true;
        }
        return false;
    }

    private boolean c(String string, Player player, String string2) {
        String string3 = HtmCache.getInstance().getNotNull("command/help.htm", player);
        Functions.show(string3, player, null, new Object[0]);
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }
}

