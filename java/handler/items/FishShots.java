/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ShotsItemHandler;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.item.WeaponTemplate;

public class FishShots
extends ShotsItemHandler {
    private static int[] iv = new int[]{6535, 6536, 6537, 6538, 6539, 6540};
    private static int[] lR = new int[]{2181, 2182, 2183, 2184, 2185, 2186};

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        int n = itemInstance.getItemId();
        boolean bl2 = false;
        if (player.getAutoSoulShot().contains(n)) {
            bl2 = true;
        }
        ItemInstance itemInstance2 = player.getActiveWeaponInstance();
        WeaponTemplate weaponTemplate = player.getActiveWeaponItem();
        if (itemInstance2 == null || weaponTemplate.getItemType() != WeaponTemplate.WeaponType.ROD) {
            if (!bl2) {
                player.sendPacket((IStaticPacket)Msg.CANNOT_USE_SOULSHOTS);
            }
            return false;
        }
        if (itemInstance2.getChargedFishshot()) {
            return false;
        }
        if (itemInstance.getCount() < 1L) {
            if (bl2) {
                player.removeAutoSoulShot(n);
                player.sendPacket(new ExAutoSoulShot(n, false), new SystemMessage(1434).addString(itemInstance.getName()));
                return false;
            }
            player.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_SPIRITSHOTS);
            return false;
        }
        int n2 = weaponTemplate.getCrystalType().gradeOrd();
        if (n2 == 0 && n != 6535 || n2 == 1 && n != 6536 || n2 == 2 && n != 6537 || n2 == 3 && n != 6538 || n2 == 4 && n != 6539 || n2 == 5 && n != 6540) {
            if (bl2) {
                return false;
            }
            player.sendPacket((IStaticPacket)Msg.THIS_FISHING_SHOT_IS_NOT_FIT_FOR_THE_FISHING_POLE_CRYSTAL);
            return false;
        }
        if (player.getInventory().destroyItem(itemInstance, 1L)) {
            itemInstance2.setChargedFishshot(true);
            player.sendPacket((IStaticPacket)Msg.POWER_OF_MANA_ENABLED);
            this.broadcastShotSkill(player, lR[n2], 1);
        }
        return true;
    }

    @Override
    public int[] getItemIds() {
        return iv;
    }
}

