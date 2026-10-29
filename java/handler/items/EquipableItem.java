/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import gnu.trove.TIntHashSet;
import handler.items.ScriptItemHandler;
import l2.gameserver.Config;
import l2.gameserver.ai.NextAction;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SendTradeDone;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.ItemFunctions;

public class EquipableItem
extends ScriptItemHandler {
    private int[] iv;

    public EquipableItem() {
        TIntHashSet tIntHashSet = new TIntHashSet();
        for (ItemTemplate itemTemplate : ItemHolder.getInstance().getAllTemplates()) {
            if (itemTemplate == null || !itemTemplate.isEquipable()) continue;
            tIntHashSet.add(itemTemplate.getItemId());
        }
        this.iv = tIntHashSet.toArray();
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        SystemMessage systemMessage;
        if (!playable.isPlayer()) {
            return false;
        }
        Player player = playable.getPlayer();
        if (player.isAttackingNow() && Config.AUTO_ATTACK_ON_WEAPON_CHANGE || player.isCastingNow()) {
            player.sendPacket((IStaticPacket)new SystemMessage(104));
            if (player.isProcessingRequest()) {
                Request request = player.getRequest();
                if (player.isInTrade()) {
                    Player player2 = request.getOtherPlayer(player);
                    player.sendPacket((IStaticPacket)SendTradeDone.FAIL);
                    player2.sendPacket((IStaticPacket)SendTradeDone.FAIL);
                }
                request.cancel();
            }
            player.getAI().setNextAction(NextAction.EQUIP, itemInstance, null, bl, false);
            return false;
        }
        if (player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAlikeDead() || player.isFakeDeath()) {
            player.sendPacket((IStaticPacket)new SystemMessage(113).addItemName(itemInstance.getItemId()));
            return false;
        }
        int n = itemInstance.getBodyPart();
        if ((n == 16384 || n == 256 || n == 128) && (player.isMounted() || player.isCursedWeaponEquipped() || player.getActiveWeaponFlagAttachment() != null || player.isWeaponEquipBlocked() && itemInstance.getItemType() != WeaponTemplate.WeaponType.NONE)) {
            player.sendPacket((IStaticPacket)new SystemMessage(113).addItemName(itemInstance.getItemId()));
            return false;
        }
        if (itemInstance.isCursed()) {
            player.sendPacket((IStaticPacket)new SystemMessage(113).addItemName(itemInstance.getItemId()));
            return false;
        }
        if (itemInstance.isEquipped()) {
            ItemInstance itemInstance2 = player.getActiveWeaponInstance();
            if (itemInstance == itemInstance2) {
                player.abortAttack(true, true);
                player.abortCast(true, true);
            }
            player.sendDisarmMessage(itemInstance);
            player.getInventory().unEquipItem(itemInstance);
            return false;
        }
        L2GameServerPacket l2GameServerPacket = ItemFunctions.checkIfCanEquip(player, itemInstance);
        if (l2GameServerPacket != null) {
            player.sendPacket((IStaticPacket)l2GameServerPacket);
            return false;
        }
        player.getInventory().equipItem(itemInstance);
        if (!itemInstance.isEquipped()) {
            player.sendActionFailed();
            return false;
        }
        if (itemInstance.getEnchantLevel() > 0) {
            systemMessage = new SystemMessage(368);
            systemMessage.addNumber(itemInstance.getEnchantLevel());
            systemMessage.addItemName(itemInstance.getItemId());
        } else {
            systemMessage = new SystemMessage(49).addItemName(itemInstance.getItemId());
        }
        player.sendPacket((IStaticPacket)systemMessage);
        return true;
    }

    @Override
    public int[] getItemIds() {
        return this.iv;
    }
}

