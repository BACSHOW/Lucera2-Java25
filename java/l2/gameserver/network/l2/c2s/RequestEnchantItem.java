/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.EnchantItemHolder;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.EnchantResult;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.item.support.EnchantScroll;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class RequestEnchantItem
extends L2GameClientPacket {
    private int CS;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected void runImpl() {
        GameClient gameClient = (GameClient)this.getClient();
        if (gameClient == null) {
            return;
        }
        Player player = gameClient.getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.setEnchantScroll(null);
            player.sendActionFailed();
            return;
        }
        if (player.isInTrade()) {
            player.setEnchantScroll(null);
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.setEnchantScroll(null);
            player.sendPacket((IStaticPacket)EnchantResult.CANCEL);
            player.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            player.sendActionFailed();
            return;
        }
        PcInventory pcInventory = player.getInventory();
        pcInventory.writeLock();
        try {
            ItemInstance itemInstance = pcInventory.getItemByObjectId(this.CS);
            ItemInstance itemInstance2 = player.getEnchantScroll();
            if (itemInstance == null || itemInstance2 == null) {
                player.sendActionFailed();
                return;
            }
            EnchantScroll enchantScroll = EnchantItemHolder.getInstance().getEnchantScroll(itemInstance2.getItemId());
            if (enchantScroll == null) {
                player.sendActionFailed();
                return;
            }
            if (!itemInstance.canBeEnchanted(false)) {
                player.sendPacket((IStaticPacket)EnchantResult.CANCEL);
                player.sendPacket((IStaticPacket)SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                player.sendActionFailed();
                return;
            }
            if (!enchantScroll.isUsableWith(itemInstance)) {
                player.sendPacket((IStaticPacket)EnchantResult.CANCEL);
                player.sendPacket((IStaticPacket)SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
                player.sendActionFailed();
                return;
            }
            double d = 1.0 + enchantScroll.getChanceMod();
            int n = itemInstance.getEnchantLevel() + enchantScroll.getIncrement();
            d *= player.getEnchantBonusMul();
            if (!pcInventory.destroyItem(itemInstance2, 1L)) {
                player.sendPacket((IStaticPacket)EnchantResult.CANCEL);
                player.sendPacket((IStaticPacket)SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                player.sendActionFailed();
                return;
            }
            double d2 = enchantScroll.getEnchantChance(itemInstance);
            if (enchantScroll.isInfallible() || Rnd.chance(d2 * d)) {
                this.c(player, itemInstance, n);
                return;
            }
            switch (enchantScroll.getOnFailAction()) {
                case CRYSTALIZE: {
                    this.a(player, itemInstance);
                    return;
                }
                case RESET: {
                    this.d(player, itemInstance, enchantScroll.getFailResultLevel());
                    return;
                }
                case NONE: {
                    this.b(player, itemInstance);
                    return;
                }
            }
            return;
        }
        finally {
            pcInventory.writeUnlock();
            player.setEnchantScroll(null);
            player.updateStats();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void c(Player player, ItemInstance itemInstance, int n) {
        PcInventory pcInventory = player.getInventory();
        if (n >= 65535) {
            player.sendPacket(EnchantResult.CANCEL, SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.sendActionFailed();
            return;
        }
        boolean bl = itemInstance.isEquipped();
        int n2 = itemInstance.getBodyPart();
        if (bl) {
            itemInstance.setEquipped(false);
            pcInventory.getListeners().onUnequip(n2, itemInstance);
        }
        int n3 = itemInstance.getEnchantLevel();
        try {
            itemInstance.setEnchantLevel(n);
            Log.LogItem(player, Log.ItemLog.EnchantSuccess, itemInstance);
        }
        finally {
            if (bl) {
                pcInventory.getListeners().onEquip(n2, itemInstance);
                itemInstance.setEquipped(true);
            }
            itemInstance.save();
        }
        player.sendPacket(new InventoryUpdate().addModifiedItem(itemInstance), n3 > 0 ? new SystemMessage(63).addNumber(n3).addItemName(itemInstance.getItemId()) : new SystemMessage(62).addItemName(itemInstance.getItemId()), EnchantResult.SUCESS);
        if (Config.SHOW_ENCHANT_EFFECT_RESULT) {
            RequestEnchantItem.c(player, itemInstance);
        }
    }

    private void a(Player player, ItemInstance itemInstance) {
        PcInventory pcInventory = player.getInventory();
        boolean bl = itemInstance.isEquipped();
        int n = itemInstance.getItemId();
        int n2 = itemInstance.getEnchantLevel();
        int n3 = itemInstance.getCrystalType().cry;
        int n4 = itemInstance.getTemplate().getCrystalCount();
        ItemInstance itemInstance2 = player.getEnchantScroll();
        if (bl) {
            player.sendDisarmMessage(itemInstance);
            pcInventory.unEquipItem(itemInstance);
        }
        Log.LogItem(player, Log.ItemLog.EnchantCrystallize, itemInstance, 1L, (long)itemInstance.getReferencePrice(), itemInstance2.getItemId());
        if (!pcInventory.destroyItem(itemInstance, 1L)) {
            player.sendActionFailed();
            return;
        }
        if (n3 > 0 && n4 > 0) {
            int n5 = (int)((double)n4 * 0.87);
            if (n2 > 3 && Config.CRYSTALLIZE_BONUS_AT_ENCHANT) {
                n5 = (int)((double)n5 + (double)n4 * 0.25 * (double)(n2 - 3));
            }
            if (n5 < 1) {
                n5 = 1;
            }
            player.sendPacket(new EnchantResult(1, n3, n5), new SystemMessage(65).addNumber(n2).addItemName(n));
            ItemFunctions.addItem((Playable)player, n3, (long)n5, true);
        } else {
            player.sendPacket(EnchantResult.FAILED_NO_CRYSTALS, new SystemMessage(64).addItemName(itemInstance.getItemId()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void d(Player player, ItemInstance itemInstance, int n) {
        PcInventory pcInventory = player.getInventory();
        boolean bl = itemInstance.isEquipped();
        int n2 = itemInstance.getBodyPart();
        int n3 = Math.min(itemInstance.getEnchantLevel(), n);
        ItemInstance itemInstance2 = player.getEnchantScroll();
        if (bl) {
            itemInstance.setEquipped(false);
            pcInventory.getListeners().onUnequip(n2, itemInstance);
        }
        try {
            itemInstance.setEnchantLevel(n3);
            Log.LogItem(player, Log.ItemLog.EnchantReset, itemInstance, 1L, (long)itemInstance.getReferencePrice(), itemInstance2.getItemId());
        }
        finally {
            if (bl) {
                pcInventory.getListeners().onEquip(n2, itemInstance);
                itemInstance.setEquipped(true);
            }
            itemInstance.save();
        }
        player.sendPacket(new InventoryUpdate().addModifiedItem(itemInstance), EnchantResult.BLESSED_FAILED, SystemMsg.THE_BLESSED_ENCHANT_FAILED);
    }

    private void b(Player player, ItemInstance itemInstance) {
        ItemInstance itemInstance2 = player.getEnchantScroll();
        Log.LogItem(player, Log.ItemLog.EnchantFail, itemInstance, 1L, (long)itemInstance.getReferencePrice(), itemInstance2.getItemId());
    }

    private static final void c(Player player, ItemInstance itemInstance) {
        if (itemInstance.getTemplate().getType2() == 0) {
            if (Config.SHOW_ENCHANT_EFFECT_RESULT_EVERY_NEXT_SUCCESS ? itemInstance.getEnchantLevel() == Config.WEAPON_FIRST_ENCHANT_EFFECT_LEVEL || itemInstance.getEnchantLevel() >= Config.WEAPON_SECOND_ENCHANT_EFFECT_LEVEL : itemInstance.getEnchantLevel() == Config.WEAPON_FIRST_ENCHANT_EFFECT_LEVEL || itemInstance.getEnchantLevel() == Config.WEAPON_SECOND_ENCHANT_EFFECT_LEVEL) {
                player.broadcastPacket(new MagicSkillUse(player, player, 2025, 1, 500, 1500L));
                player.broadCastCustomMessage("_C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3", player, player, itemInstance, itemInstance.getEnchantLevel());
            }
        } else if (Config.SHOW_ENCHANT_EFFECT_RESULT_EVERY_NEXT_SUCCESS ? itemInstance.getEnchantLevel() >= Config.ARMOR_ENCHANT_EFFECT_LEVEL : itemInstance.getEnchantLevel() == Config.ARMOR_ENCHANT_EFFECT_LEVEL) {
            player.broadcastPacket(new MagicSkillUse(player, player, 2025, 1, 500, 1500L));
            player.broadCastCustomMessage("_C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3", player, player, itemInstance, itemInstance.getEnchantLevel());
        }
    }
}

