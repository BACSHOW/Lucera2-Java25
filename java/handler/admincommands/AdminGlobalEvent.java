/*
 * Decompiled with CFR 0.152.
 */
package handler.admincommands;

import handler.admincommands.ScriptAdminCommand;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;

public class AdminGlobalEvent
extends ScriptAdminCommand {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        switch (commands) {
            case admin_list_events: {
                GameObject gameObject = player.getTarget();
                if (gameObject == null) {
                    player.sendPacket((IStaticPacket)SystemMsg.INVALID_TARGET);
                    break;
                }
                for (GlobalEvent globalEvent : gameObject.getEvents()) {
                    player.sendMessage("- " + globalEvent.toString());
                }
                break;
            }
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    static enum Commands {
        admin_list_events;

    }
}

