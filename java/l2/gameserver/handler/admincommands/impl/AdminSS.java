/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;

public class AdminSS
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().Menu) {
            return false;
        }
        switch (commands) {
            case admin_ssq_change: {
                if (stringArray.length > 2) {
                    int n = Integer.parseInt(stringArray[1]);
                    int n2 = Integer.parseInt(stringArray[2]);
                    SevenSigns.getInstance().changePeriod(n, n2 * 60);
                    break;
                }
                if (stringArray.length > 1) {
                    int n = Integer.parseInt(stringArray[1]);
                    SevenSigns.getInstance().changePeriod(n);
                    break;
                }
                SevenSigns.getInstance().changePeriod();
                break;
            }
            case admin_ssq_time: {
                if (stringArray.length <= 1) break;
                int n = Integer.parseInt(stringArray[1]);
                SevenSigns.getInstance().setTimeToNextPeriodChange(n);
                break;
            }
            case admin_ssq_cabal: {
                if (stringArray.length <= 3) break;
                int n = Integer.parseInt(stringArray[1]);
                int n3 = Integer.parseInt(stringArray[2]);
                int n4 = Integer.parseInt(stringArray[3]);
                SevenSigns.getInstance().setPlayerInfo(n, n3, n4);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_ssq_change,
        admin_ssq_time,
        admin_ssq_cabal;

    }
}

