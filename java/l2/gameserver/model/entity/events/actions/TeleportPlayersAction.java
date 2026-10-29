/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class TeleportPlayersAction
implements EventAction {
    private String _name;

    public TeleportPlayersAction(String string) {
        this._name = string;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        globalEvent.teleportPlayers(this._name);
    }
}

