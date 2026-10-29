/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events;

import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.taskmanager.actionrunner.ActionWrapper;

public class EventWrapper
extends ActionWrapper {
    private final GlobalEvent Qp;
    private final int _time;

    public EventWrapper(String string, GlobalEvent globalEvent, int n) {
        super(string);
        this.Qp = globalEvent;
        this._time = n;
    }

    @Override
    public void runImpl0() throws Exception {
        this.Qp.timeActions(this._time);
    }
}

