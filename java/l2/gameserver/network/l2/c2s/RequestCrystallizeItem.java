/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class RequestCrystallizeItem
extends L2GameClientPacket {
    private int CS;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.sendPacket((IStaticPacket)Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (player.isInTrade()) {
            player.sendActionFailed();
            return;
        }
        ItemInstance itemInstance = player.getInventory().getItemByObjectId(this.CS);
        if (itemInstance == null) {
            player.sendActionFailed();
            return;
        }
        if (itemInstance.isHeroWeapon()) {
            player.sendPacket((IStaticPacket)Msg.HERO_WEAPONS_CANNOT_BE_DESTROYED);
            return;
        }
        if (!itemInstance.canBeCrystallized(player)) {
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.sendPacket((IStaticPacket)Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        if (player.isInTrade()) {
            player.sendActionFailed();
            return;
        }
        int n = player.getSkillLevel(248);
        if (itemInstance.getTemplate().getItemGrade().externalOrdinal > n) {
            player.sendPacket((IStaticPacket)Msg.CANNOT_CRYSTALLIZE_CRYSTALLIZATION_SKILL_LEVEL_TOO_LOW);
            player.sendActionFailed();
            return;
        }
        int n2 = itemInstance.getTemplate().getCrystalCount(itemInstance.getEnchantLevel(), false);
        int n3 = itemInstance.getTemplate().getCrystalType().cry;
        Log.LogItem(player, Log.ItemLog.Crystalize, itemInstance);
        if (!player.getInventory().destroyItemByObjectId(this.CS, 1L)) {
            player.sendActionFailed();
            return;
        }
        player.sendPacket((IStaticPacket)Msg.THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED);
        ItemFunctions.addItem((Playable)player, n3, (long)n2, true);
        player.sendChanges();
    }
}

