/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class ActiveDeactiveAction
implements EventAction {
    private final boolean _active;
    private final String _name;

    public ActiveDeactiveAction(boolean bl, String string) {
        this._active = bl;
        this._name = string;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        globalEvent.zoneAction(this._name, this._active);
    }
}

