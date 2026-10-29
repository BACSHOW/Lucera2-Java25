/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class GiveItemAction
implements EventAction {
    private int _itemId;
    private long CE;

    public GiveItemAction(int n, long l) {
        this._itemId = n;
        this.CE = l;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        for (Player player : globalEvent.itemObtainPlayers()) {
            globalEvent.giveItem(player, this._itemId, this.CE);
        }
    }
}

