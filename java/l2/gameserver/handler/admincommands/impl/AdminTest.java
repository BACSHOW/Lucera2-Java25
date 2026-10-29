/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.commons.util.Rnd;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;

public class AdminTest
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        switch (commands) {
            case admin_collapse_this: {
                if (player.getReflection() != null) {
                    player.getReflection().startCollapseTimer(1000L);
                    break;
                }
                player.sendMessage("No reflection");
                break;
            }
            case admin_collapse_this2: {
                if (player.getReflection() != null) {
                    player.getReflection().collapse();
                    break;
                }
                player.sendMessage("No reflection");
                break;
            }
            case admin_alt_move_000: {
                Player player2;
                Player player3 = player2 = player.getTarget() != null ? player.getTarget().getPlayer() : null;
                if (player2 == null) {
                    player2 = player;
                }
                player2.moveToLocation(0, 0, 0, 0, true);
                break;
            }
            case admin_alt_move_rnd: {
                Player player4;
                Player player5 = player4 = player.getTarget() != null ? player.getTarget().getPlayer() : null;
                if (player4 == null) {
                    player4 = player;
                }
                player4.moveToLocation(Rnd.get(World.MAP_MIN_X, World.MAP_MAX_X), Rnd.get(World.MAP_MIN_Y, World.MAP_MAX_Y), Rnd.get(World.MAP_MIN_Z, World.MAP_MAX_Z), 0, true);
                break;
            }
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_collapse_this,
        admin_collapse_this2,
        admin_alt_move_000,
        admin_alt_move_rnd;

    }
}

