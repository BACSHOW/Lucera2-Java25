/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.SimpleItemHandler;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;

public class ClanReputation
extends SimpleItemHandler {
    @Override
    protected boolean useItemImpl(Player player, ItemInstance itemInstance, boolean bl) {
        int n = itemInstance.getItemId();
        if (player == null) {
            return false;
        }
        if (!Config.ITEM_CLAN_REPUTATION_ENABLE) {
            player.sendMessage(new CustomMessage("items.clanreputation.disabled", player, new Object[0]));
            return false;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendMessage(new CustomMessage("items.clanreputation.getClanFirst", player, new Object[0]));
            return false;
        }
        if (clan.getLevel() < Config.MIN_CLAN_LEVEL_FOR_REPUTATION) {
            player.sendMessage(new CustomMessage("items.clanreputation.GetClanLevelFirst", player, new Object[0]));
            return false;
        }
        for (int i = 0; i < Config.ITEM_CLAN_REPUTATION_ID.length; ++i) {
            if (n != Config.ITEM_CLAN_REPUTATION_ID[i] || Functions.removeItem(player, n, 1L) <= 0L) continue;
            clan.incReputation(Config.ITEM_CLAN_REPUTATION_AMOUNT[i], true, "ClanReputationItemAdd");
            player.sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(Config.ITEM_CLAN_REPUTATION_AMOUNT[i]));
            return true;
        }
        return false;
    }

    @Override
    public int[] getItemIds() {
        return Config.ITEM_CLAN_REPUTATION_ID;
    }
}

