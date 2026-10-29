/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class RefreshAction
implements EventAction {
    private final String _name;

    public RefreshAction(String string) {
        this._name = string;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        globalEvent.refreshAction(this._name);
    }
}

