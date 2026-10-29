/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class InitAction
implements EventAction {
    private String _name;

    public InitAction(String string) {
        this._name = string;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        globalEvent.initAction(this._name);
    }
}

