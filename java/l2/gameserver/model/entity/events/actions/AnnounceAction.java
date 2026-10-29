/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class AnnounceAction
implements EventAction {
    private int _id;

    public AnnounceAction(int n) {
        this._id = n;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        globalEvent.announce(this._id);
    }
}

