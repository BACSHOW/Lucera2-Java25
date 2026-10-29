/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.usercommands.impl;

import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.mapregion.RestartArea;

public class LocCommand
implements IUserCommandHandler {
    private static final int[] zy = new int[]{0};

    @Override
    public boolean useUserCommand(int n, Player player) {
        int n2;
        if (zy[0] != n) {
            return false;
        }
        RestartArea restartArea = MapRegionManager.getInstance().getRegionData(RestartArea.class, player);
        int n3 = n2 = restartArea != null ? restartArea.getRestartPoint().get((Object)player.getRace()).getMsgId() : 0;
        if (n2 > 0) {
            player.sendPacket((IStaticPacket)new SystemMessage(n2).addNumber(player.getX()).addNumber(player.getY()).addNumber(player.getZ()));
        } else {
            player.sendPacket((IStaticPacket)new SystemMessage(1983).addString("Current location : " + player.getX() + ", " + player.getY() + ", " + player.getZ()));
        }
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return zy;
    }
}

