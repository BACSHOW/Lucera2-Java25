/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ShotsItemHandler;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.item.WeaponTemplate;

public class SoulShots
extends ShotsItemHandler {
    private static final int[] iv = new int[]{5789, 1835, 1463, 1464, 1465, 1466, 1467};
    private static final int[] lR = new int[]{2039, 2150, 2151, 2152, 2153, 2154};

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        int n;
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        WeaponTemplate weaponTemplate = player.getActiveWeaponItem();
        ItemInstance itemInstance2 = player.getActiveWeaponInstance();
        int n2 = itemInstance.getItemId();
        boolean bl2 = false;
        if (player.getAutoSoulShot().contains(n2)) {
            bl2 = true;
        }
        if (itemInstance2 == null) {
            if (!bl2) {
                player.sendPacket((IStaticPacket)Msg.CANNOT_USE_SOULSHOTS);
            }
            return false;
        }
        if (itemInstance2.getChargedSoulshot() != 0) {
            return false;
        }
        int n3 = weaponTemplate.getCrystalType().gradeOrd();
        int n4 = weaponTemplate.getSoulShotCount();
        if (n4 == 0) {
            if (bl2) {
                player.removeAutoSoulShot(n2);
                player.sendPacket(new ExAutoSoulShot(n2, false), new SystemMessage(1434).addItemName(n2));
                return false;
            }
            player.sendPacket((IStaticPacket)Msg.CANNOT_USE_SOULSHOTS);
            return false;
        }
        if (n3 == 0 && n2 != 5789 && n2 != 1835 || n3 == 1 && n2 != 1463 || n3 == 2 && n2 != 1464 || n3 == 3 && n2 != 1465 || n3 == 4 && n2 != 1466 || n3 == 5 && n2 != 1467) {
            if (bl2) {
                return false;
            }
            player.sendPacket((IStaticPacket)Msg.SOULSHOT_DOES_NOT_MATCH_WEAPON_GRADE);
            return false;
        }
        if (weaponTemplate.getItemType() == WeaponTemplate.WeaponType.BOW && (n = (int)player.calcStat(Stats.SS_USE_BOW, n4, null, null)) < n4 && Rnd.chance(player.calcStat(Stats.SS_USE_BOW_CHANCE, n4, null, null))) {
            n4 = n;
        }
        if (Config.ALT_CONSUME_SOULSHOTS && (!player.hasBonus() || Config.ALT_PA_CONSUME_SOULSHOTS) && !player.getInventory().destroyItem(itemInstance, n4)) {
            player.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_SOULSHOTS);
            return false;
        }
        itemInstance2.setChargedSoulshot(1);
        player.sendPacket((IStaticPacket)Msg.POWER_OF_THE_SPIRITS_ENABLED);
        this.broadcastShotSkill(player, lR[n3], 1);
        return true;
    }

    @Override
    public final int[] getItemIds() {
        return iv;
    }
}

