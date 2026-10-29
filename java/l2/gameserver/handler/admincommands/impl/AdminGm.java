/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;

public class AdminGm
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (Boolean.TRUE.booleanValue()) {
            return false;
        }
        if (!player.getPlayerAccess().CanEditChar) {
            return false;
        }
        switch (commands) {
            case admin_gm: {
                this.t(player);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void t(Player player) {
        if (player.isGM()) {
            player.getPlayerAccess().IsGM = false;
            player.sendMessage("You no longer have GM status.");
        } else {
            player.getPlayerAccess().IsGM = true;
            player.sendMessage("You have GM status now.");
        }
    }

    private static enum Commands {
        admin_gm;

    }
}

