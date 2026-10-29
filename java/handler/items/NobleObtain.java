/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.SimpleItemHandler;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.scripts.Functions;

public class NobleObtain
extends SimpleItemHandler {
    @Override
    protected boolean useItemImpl(Player player, ItemInstance itemInstance, boolean bl) {
        int n = itemInstance.getItemId();
        if (player == null || !Functions.CheckPlayerConditions(player)) {
            return false;
        }
        if (!Config.SERVICES_ITEM_NOBLESS_SELL_ENABLED) {
            player.sendMessage(new CustomMessage("services.NoblesItemSell.Disabled", player, new Object[0]));
            return false;
        }
        if (Config.SERVICES_ITEM_NOBLESS_SELL_WITHOUT_SUBCLASS && player.getClassId().getLevel() < 4) {
            player.sendMessage(new CustomMessage("services.NoblesItemSell.Need3Job", player, new Object[0]));
            return false;
        }
        if (!Config.SERVICES_ITEM_NOBLESS_SELL_WITHOUT_SUBCLASS && player.getSubLevel() < Config.NOBLESS_ITEM_LEVEL_FOR_SELL) {
            player.sendMessage(new CustomMessage("services.NoblesItemSell.NeedSubAndLevel", player, new Object[0]).addNumber(Config.NOBLESS_LEVEL_FOR_SELL));
            return false;
        }
        if (player.isNoble()) {
            player.sendMessage(new CustomMessage("services.NoblesItemSell.AlreadyHave", player, new Object[0]));
            return false;
        }
        if (player.getLevel() < Config.NOBLESS_ITEM_LEVEL_FOR_SELL) {
            player.sendMessage(new CustomMessage("services.NoblesItemSell.NeedLevel", player, new Object[0]).addNumber(Config.NOBLESS_ITEM_LEVEL_FOR_SELL));
            return false;
        }
        for (int i = 0; i < Config.SERVICES_ITEM_NOBLESS_SELL_ITEM_ID.length; ++i) {
            if (n != Config.SERVICES_ITEM_NOBLESS_SELL_ITEM_ID[i] || Functions.removeItem(player, n, 1L) <= 0L) continue;
            player.getInventory().addItem(7694, 1L);
            NoblesController.getInstance().addNoble(player);
            player.setNoble(true);
            player.updatePledgeClass();
            player.updateNobleSkills();
            player.sendPacket((IStaticPacket)new SkillList(player));
            player.broadcastUserInfo(true);
            return true;
        }
        return false;
    }

    @Override
    public int[] getItemIds() {
        return Config.SERVICES_ITEM_NOBLESS_SELL_ITEM_ID;
    }
}

