/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items.listeners;

import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.WeaponTemplate;

public final class BowListener
implements OnEquipListener {
    private static final BowListener XR = new BowListener();

    public static BowListener getInstance() {
        return XR;
    }

    @Override
    public void onUnequip(int n, ItemInstance itemInstance, Playable playable) {
        if (!itemInstance.isEquipable() || n != 7) {
            return;
        }
        Player player = (Player)playable;
        if (itemInstance.getItemType() == WeaponTemplate.WeaponType.BOW || itemInstance.getItemType() == WeaponTemplate.WeaponType.ROD) {
            player.getInventory().setPaperdollItem(8, null);
        }
    }

    @Override
    public void onEquip(int n, ItemInstance itemInstance, Playable playable) {
        ItemInstance itemInstance2;
        if (!itemInstance.isEquipable() || n != 7) {
            return;
        }
        Player player = (Player)playable;
        if (itemInstance.getItemType() == WeaponTemplate.WeaponType.BOW && (itemInstance2 = player.getInventory().findArrowForBow(itemInstance.getTemplate())) != null) {
            player.getInventory().setPaperdollItem(8, itemInstance2);
        }
        if (itemInstance.getItemType() == WeaponTemplate.WeaponType.ROD && (itemInstance2 = player.getInventory().findEquippedLure()) != null) {
            player.getInventory().setPaperdollItem(8, itemInstance2);
        }
    }
}

