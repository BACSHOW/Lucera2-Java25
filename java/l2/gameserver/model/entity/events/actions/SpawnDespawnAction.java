/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class SpawnDespawnAction
implements EventAction {
    private final boolean QC;
    private final String _name;

    public SpawnDespawnAction(String string, boolean bl) {
        this.QC = bl;
        this._name = string;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        globalEvent.spawnAction(this._name, this.QC);
    }
}

