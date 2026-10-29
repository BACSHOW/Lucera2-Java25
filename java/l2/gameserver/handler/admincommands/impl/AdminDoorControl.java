/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.network.l2.components.IStaticPacket;

public class AdminDoorControl
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().Door) {
            return false;
        }
        switch (commands) {
            case admin_open: {
                GameObject gameObject = stringArray.length > 1 ? World.getAroundObjectById(player, Integer.parseInt(stringArray[1])) : player.getTarget();
                if (gameObject != null && gameObject.isDoor()) {
                    ((DoorInstance)gameObject).openMe();
                    break;
                }
                player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                break;
            }
            case admin_close: {
                GameObject gameObject = stringArray.length > 1 ? World.getAroundObjectById(player, Integer.parseInt(stringArray[1])) : player.getTarget();
                if (gameObject != null && gameObject.isDoor()) {
                    ((DoorInstance)gameObject).closeMe();
                    break;
                }
                player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_open,
        admin_close;

    }
}

