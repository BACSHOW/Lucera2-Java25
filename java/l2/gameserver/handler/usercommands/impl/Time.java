/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.usercommands.impl;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import l2.gameserver.Config;
import l2.gameserver.GameTimeController;
import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class Time
implements IUserCommandHandler {
    private static final int[] zy = new int[]{77};
    private static final NumberFormat aU = NumberFormat.getInstance(Locale.ENGLISH);
    private static final SimpleDateFormat zD = new SimpleDateFormat("H:mm");

    @Override
    public boolean useUserCommand(int n, Player player) {
        if (zy[0] != n) {
            return false;
        }
        int n2 = GameTimeController.getInstance().getGameHour();
        int n3 = GameTimeController.getInstance().getGameMin();
        SystemMessage systemMessage = GameTimeController.getInstance().isNowNight() ? new SystemMessage(928) : new SystemMessage(927);
        systemMessage.addString(aU.format(n2)).addString(aU.format(n3));
        player.sendPacket((IStaticPacket)systemMessage);
        if (Config.ALT_SHOW_SERVER_TIME) {
            player.sendMessage(new CustomMessage("usercommandhandlers.Time.ServerTime", player, zD.format(new Date(System.currentTimeMillis()))));
        }
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return zy;
    }

    static {
        aU.setMinimumIntegerDigits(2);
    }
}

