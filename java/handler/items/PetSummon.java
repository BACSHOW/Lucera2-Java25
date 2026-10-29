/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ScriptItemHandler;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.tables.SkillTable;

public class PetSummon
extends ScriptItemHandler {
    private static final int[] iv = PetDataTable.getPetControlItems();
    private static final int _skillId = 2046;

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        player.setPetControlItem(itemInstance);
        player.getAI().Cast(SkillTable.getInstance().getInfo(2046, 1), player, false, true);
        return true;
    }

    @Override
    public final int[] getItemIds() {
        return iv;
    }
}

