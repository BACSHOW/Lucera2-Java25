/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import java.util.Collections;
import java.util.List;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class IfElseAction
implements EventAction {
    private String _name;
    private boolean Qq;
    private List<EventAction> Qr = Collections.emptyList();
    private List<EventAction> Qs = Collections.emptyList();

    public IfElseAction(String string, boolean bl) {
        this._name = string;
        this.Qq = bl;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        List<EventAction> list = (this.Qq ? !globalEvent.ifVar(this._name) : globalEvent.ifVar(this._name)) ? this.Qr : this.Qs;
        for (EventAction eventAction : list) {
            eventAction.call(globalEvent);
        }
    }

    public void setIfList(List<EventAction> list) {
        this.Qr = list;
    }

    public void setElseList(List<EventAction> list) {
        this.Qs = list;
    }
}

