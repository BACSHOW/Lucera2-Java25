/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.ArrayUtils;

public class ItemRemaining
extends Functions
implements IVoicedCommandHandler {
    private String[] zF = new String[]{"itemremaining", "itemsremaining", "rune", "rune_remaining", "runeremaining"};

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.ALT_ALLOW_ITEM_REMAINING_COMMAND) {
            return false;
        }
        if (ArrayUtils.contains(this.zF, string.toLowerCase())) {
            this.L(player);
            return true;
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }

    private void L(Player player) {
        ItemInstance[] itemInstanceArray;
        for (ItemInstance itemInstance : itemInstanceArray = player.getInventory().getItems()) {
            if (!itemInstance.isTemporalItem()) continue;
            int n = itemInstance.getPeriod();
            String string = Util.formatTime(n);
            player.sendMessage(new CustomMessage("voicedcommandhandlers.ItemRemaining", player, itemInstance, string));
        }
    }
}

