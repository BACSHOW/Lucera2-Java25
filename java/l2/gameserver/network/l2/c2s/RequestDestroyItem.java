/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class RequestDestroyItem
extends L2GameClientPacket {
    private int CS;
    private long CE;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.CE = this.readD();
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
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        long l = this.CE;
        ItemInstance itemInstance = player.getInventory().getItemByObjectId(this.CS);
        if (itemInstance == null) {
            player.sendActionFailed();
            return;
        }
        if (l < 1L) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT);
            return;
        }
        if (!player.isGM() && itemInstance.isHeroWeapon()) {
            player.sendPacket((IStaticPacket)Msg.HERO_WEAPONS_CANNOT_BE_DESTROYED);
            return;
        }
        if (player.getPet() != null && player.getPet().getControlItemObjId() == itemInstance.getObjectId()) {
            player.sendPacket((IStaticPacket)Msg.THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_DELETED);
            return;
        }
        if (!player.isGM() && !itemInstance.canBeDestroyed(player)) {
            player.sendPacket((IStaticPacket)Msg.THIS_ITEM_CANNOT_BE_DISCARDED);
            return;
        }
        if (this.CE > itemInstance.getCount()) {
            l = itemInstance.getCount();
        }
        boolean bl = itemInstance.canBeCrystallized(player);
        int n = itemInstance.getTemplate().getCrystalType().cry;
        int n2 = itemInstance.getTemplate().getCrystalCount();
        if (bl) {
            if (Config.DWARF_AUTOMATICALLY_CRYSTALLIZE_ON_ITEM_DELETE) {
                int n3 = player.getSkillLevel(248);
                if (n3 < 1 || n - 1458 + 1 > n3) {
                    bl = false;
                }
            } else {
                bl = false;
            }
        }
        Log.LogItem(player, Log.ItemLog.Delete, itemInstance, l);
        if (!player.getInventory().destroyItemByObjectId(this.CS, l)) {
            player.sendActionFailed();
            return;
        }
        if (PetDataTable.isPetControlItem(itemInstance)) {
            PetDataTable.deletePet(itemInstance, player);
        }
        if (bl) {
            player.sendPacket((IStaticPacket)Msg.THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED);
            ItemFunctions.addItem((Playable)player, n, (long)n2, true);
        } else {
            player.sendPacket((IStaticPacket)SystemMessage2.removeItems(itemInstance.getItemId(), l));
        }
        player.sendChanges();
    }
}

