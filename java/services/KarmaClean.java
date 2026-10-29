/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;

public class KarmaClean
extends Functions {
    public void karmaclean_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_KARMA_CLEAN_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.isCursedWeaponEquipped()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_cursed_weapon.htm"));
            return;
        }
        if (player.getKarma() == 0) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_karma_clean.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/karma_clean.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_KARMA_CLEAN_SELL_ITEM));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_KARMA_CLEAN_SELL_PRICE));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void karmaclean() {
        Player player = this.getSelf();
        if (player == null || !KarmaClean.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_KARMA_CLEAN_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.isCursedWeaponEquipped()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_cursed_weapon.htm"));
            return;
        }
        if (player.getKarma() == 0) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_karma_clean.htm"));
            return;
        }
        if (KarmaClean.getItemCount(player, Config.SERVICES_KARMA_CLEAN_SELL_ITEM) < Config.SERVICES_KARMA_CLEAN_SELL_PRICE) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        KarmaClean.removeItem(player, Config.SERVICES_KARMA_CLEAN_SELL_ITEM, Config.SERVICES_KARMA_CLEAN_SELL_PRICE);
        player.setKarma(0, true);
        Log.service("KarmaClean", player, "clean karma for " + Config.SERVICES_KARMA_CLEAN_SELL_ITEM + " amount " + Config.SERVICES_KARMA_CLEAN_SELL_PRICE);
    }
}

