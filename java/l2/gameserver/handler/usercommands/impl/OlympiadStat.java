/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.usercommands.impl;

import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;

public class OlympiadStat
implements IUserCommandHandler {
    private static final int[] zy = new int[]{109};

    @Override
    public boolean useUserCommand(int n, Player player) {
        if (n != zy[0]) {
            return false;
        }
        if (!player.isNoble()) {
            player.sendPacket((IStaticPacket)SystemMsg.THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE);
            return true;
        }
        NoblesController.NobleRecord nobleRecord = NoblesController.getInstance().getNobleRecord(player.getObjectId());
        CustomMessage customMessage = new CustomMessage("Olympiad.stat", player, new Object[0]);
        customMessage = customMessage.addNumber(Math.max(0, nobleRecord.comp_done));
        customMessage = customMessage.addNumber(Math.max(0, nobleRecord.comp_win));
        customMessage = customMessage.addNumber(Math.max(0, nobleRecord.comp_loose));
        customMessage = customMessage.addNumber(Math.max(0, nobleRecord.points_current));
        player.sendMessage(customMessage);
        return true;
    }

    @Override
    public int[] getUserCommandList() {
        return zy;
    }
}

