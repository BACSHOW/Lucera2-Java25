/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class StartStopAction
implements EventAction {
    public static final String EVENT = "event";
    private final String _name;
    private final boolean QD;

    public StartStopAction(String string, boolean bl) {
        this._name = string;
        this.QD = bl;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        globalEvent.action(this._name, this.QD);
    }
}

