/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;

public class AdminIP
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanSeeIp) {
            return false;
        }
        switch (commands) {
            case admin_charip: {
                if (stringArray.length != 2) {
                    player.sendMessage("Command syntax: //charip <char_name>");
                    player.sendMessage(" Gets character's IP.");
                    break;
                }
                Player player2 = World.getPlayer(stringArray[1]);
                if (player2 == null) {
                    player.sendMessage("Character " + stringArray[1] + " not found.");
                    break;
                }
                String string2 = player2.getIP();
                if (string2.equalsIgnoreCase("<not connected>")) {
                    player.sendMessage("Character " + stringArray[1] + " not found.");
                    break;
                }
                player.sendMessage("Character's IP: " + string2);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_charip;

    }
}

