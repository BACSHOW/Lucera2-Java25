/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class OpenCloseAction
implements EventAction {
    private final boolean Qw;
    private final String _name;

    public OpenCloseAction(boolean bl, String string) {
        this.Qw = bl;
        this._name = string;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        globalEvent.doorAction(this._name, this.Qw);
    }
}

