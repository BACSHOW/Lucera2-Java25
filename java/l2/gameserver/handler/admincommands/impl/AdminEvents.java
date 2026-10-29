/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;

public class AdminEvents
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().IsEventGm) {
            return false;
        }
        switch (commands) {
            case admin_events: {
                if (stringArray.length == 1) {
                    player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/events/events.htm"));
                    break;
                }
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/events/" + stringArray[1].trim()));
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_events;

    }
}

