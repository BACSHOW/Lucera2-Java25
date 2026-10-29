/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.CameraMode;
import l2.gameserver.network.l2.s2c.SpecialCamera;

public class AdminCamera
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().Menu) {
            return false;
        }
        switch (commands) {
            case admin_freelook: {
                if (string.length() <= 15) {
                    player.sendMessage("Usage: //freelook 1 or //freelook 0");
                    return false;
                }
                string = string.substring(15);
                int n = Integer.parseInt(string);
                if (n == 1) {
                    player.setInvisibleType(InvisibleType.NORMAL);
                    player.setIsInvul(true);
                    player.setNoChannel(-1L);
                    player.setFlying(true);
                } else {
                    player.setInvisibleType(InvisibleType.NONE);
                    player.setIsInvul(false);
                    player.setNoChannel(0L);
                    player.setFlying(false);
                }
                player.sendPacket((IStaticPacket)new CameraMode(n));
                break;
            }
            case admin_cinematic: {
                int n = Integer.parseInt(stringArray[1]);
                int n2 = Integer.parseInt(stringArray[2]);
                int n3 = Integer.parseInt(stringArray[3]);
                int n4 = Integer.parseInt(stringArray[4]);
                int n5 = Integer.parseInt(stringArray[5]);
                int n6 = Integer.parseInt(stringArray[6]);
                player.sendPacket((IStaticPacket)new SpecialCamera(n, n2, n3, n4, n5, n6));
                break;
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_freelook,
        admin_cinematic;

    }
}

