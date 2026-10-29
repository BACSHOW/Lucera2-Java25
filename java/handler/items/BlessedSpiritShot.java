/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ShotsItemHandler;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.item.WeaponTemplate;

public class BlessedSpiritShot
extends ShotsItemHandler {
    private static final int[] iv = new int[]{3947, 3948, 3949, 3950, 3951, 3952};
    private static final int[] lR = new int[]{2061, 2160, 2161, 2162, 2163, 2164};

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        ItemInstance itemInstance2 = player.getActiveWeaponInstance();
        WeaponTemplate weaponTemplate = player.getActiveWeaponItem();
        int n = itemInstance.getItemId();
        boolean bl2 = false;
        if (player.getAutoSoulShot().contains(n)) {
            bl2 = true;
        }
        if (itemInstance2 == null) {
            if (!bl2) {
                player.sendPacket((IStaticPacket)Msg.CANNOT_USE_SPIRITSHOTS);
            }
            return false;
        }
        if (itemInstance2.getChargedSpiritshot() == 2) {
            return false;
        }
        int n2 = itemInstance.getItemId();
        int n3 = weaponTemplate.getCrystalType().gradeOrd();
        int n4 = weaponTemplate.getSpiritShotCount();
        if (n4 == 0) {
            if (bl2) {
                player.removeAutoSoulShot(n);
                player.sendPacket(new ExAutoSoulShot(n, false), new SystemMessage(1434).addItemName(n2));
                return false;
            }
            player.sendPacket((IStaticPacket)Msg.CANNOT_USE_SPIRITSHOTS);
            return false;
        }
        if (n3 == 0 && n2 != 3947 || n3 == 1 && n2 != 3948 || n3 == 2 && n2 != 3949 || n3 == 3 && n2 != 3950 || n3 == 4 && n2 != 3951 || n3 == 5 && n2 != 3952) {
            if (bl2) {
                return false;
            }
            player.sendPacket((IStaticPacket)Msg.SPIRITSHOT_DOES_NOT_MATCH_WEAPON_GRADE);
            return false;
        }
        if (Config.ALT_CONSUME_SPIRITSHOTS && (!player.hasBonus() || Config.ALT_PA_CONSUME_SPIRITSHOTS) && !player.getInventory().destroyItem(itemInstance, n4)) {
            if (bl2) {
                player.removeAutoSoulShot(n);
                player.sendPacket(new ExAutoSoulShot(n, false), new SystemMessage(1434).addItemName(n2));
                return false;
            }
            player.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_SPIRITSHOTS);
            return false;
        }
        itemInstance2.setChargedSpiritshot(2);
        player.sendPacket((IStaticPacket)Msg.POWER_OF_MANA_ENABLED);
        this.broadcastShotSkill(player, lR[n3], 1);
        return true;
    }

    @Override
    public final int[] getItemIds() {
        return iv;
    }
}

