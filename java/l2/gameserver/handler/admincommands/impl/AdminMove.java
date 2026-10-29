/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;

public class AdminMove
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanReload) {
            return false;
        }
        switch (commands) {
            case admin_move_debug: {
                if (stringArray.length > 1) {
                    int n = Integer.parseInt(stringArray[1]);
                    if (n > 0) {
                        player.setVar("debugMove", Integer.parseInt(stringArray[1]), -1L);
                        player.sendMessage("Move debug mode " + n);
                        break;
                    }
                    player.unsetVar("debugMove");
                    player.sendMessage("Move debug disabled");
                    break;
                }
                player.setVar("debugMove", player.getVarInt("debugMove", 0) > 0 ? 0 : 1, -1L);
                player.sendMessage("Move debug mode " + player.getVarInt("debugMove", 0));
            }
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_move_debug;

    }
}

