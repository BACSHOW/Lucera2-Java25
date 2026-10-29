/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items.listeners;

import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.funcs.Func;

public final class StatsListener
implements OnEquipListener {
    private static final StatsListener XV = new StatsListener();

    public static StatsListener getInstance() {
        return XV;
    }

    @Override
    public void onUnequip(int n, ItemInstance itemInstance, Playable playable) {
        playable.removeStatsOwner(itemInstance);
        playable.updateStats();
    }

    @Override
    public void onEquip(int n, ItemInstance itemInstance, Playable playable) {
        Func[] funcArray = itemInstance.getStatFuncs();
        playable.addStatFuncs(funcArray);
        playable.updateStats();
    }
}

