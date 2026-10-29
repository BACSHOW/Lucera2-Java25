/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.StringUtils;

public class HeroSell
extends Functions {
    private static boolean makeCustomHero(Player player, long l) {
        if (player.isHero() || l <= 0L) {
            return false;
        }
        player.setCustomHero(true, l, Config.ALT_ALLOW_CUSTOM_HERO_SKILLS);
        player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
        player.broadcastUserInfo(true);
        return true;
    }

    public void hero_sell_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_HERO_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.isInPeaceZone()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_peace_zone.htm"));
            return;
        }
        assert (Config.SERVICES_HERO_SELLER_ITEM_IDS.length == Config.SERVICES_HERO_SELLER_ITEM_COUNTS.length && Config.SERVICES_HERO_SELLER_ITEM_IDS.length == Config.SERVICE_HERO_STATUS_DURATIONS.length);
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/hero_sell.htm");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Config.SERVICE_HERO_STATUS_DURATIONS.length; ++i) {
            stringBuilder.append(new CustomMessage("services.HeroSell.requestLine", player, String.valueOf(i), String.valueOf(Config.SERVICES_HERO_SELLER_ITEM_IDS[i]), String.valueOf(Config.SERVICES_HERO_SELLER_ITEM_COUNTS[i]), String.valueOf(Config.SERVICE_HERO_STATUS_DURATIONS[i])).toString());
        }
        npcHtmlMessage.replace("%hero_options_list%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void hero_sell(String[] stringArray) {
        Player player = this.getSelf();
        if (player == null || !HeroSell.CheckPlayerConditions(player) || stringArray == null || stringArray.length == 0 || !StringUtils.isNumeric(stringArray[0])) {
            return;
        }
        int n = Integer.parseInt(stringArray[0]);
        if (n < 0 || n >= Config.SERVICES_HERO_SELLER_ITEM_IDS.length) {
            return;
        }
        if (!Config.SERVICES_HERO_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.isHero()) {
            this.show("You are already Hero.", player);
            return;
        }
        if (!player.isNoble()) {
            this.show("You are not a Nobles, only Nobles can become a Hero.", player);
            return;
        }
        if (player.isSubClassActive()) {
            this.show("You can get Hero status only on main class.", player);
            return;
        }
        if (Functions.removeItem(player, Config.SERVICES_HERO_SELLER_ITEM_IDS[n], Config.SERVICES_HERO_SELLER_ITEM_COUNTS[n]) < Config.SERVICES_HERO_SELLER_ITEM_COUNTS[n]) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        HeroSell.makeCustomHero(player, Config.SERVICE_HERO_STATUS_DURATIONS[n] * 24L * 60L * 60L);
        Log.service("HeroSell", player, "bought a custom hero for a period " + Config.SERVICE_HERO_STATUS_DURATIONS[n] * 24L * 60L * 60L + " for " + Config.SERVICES_HERO_SELLER_ITEM_IDS[n] + " amount " + Config.SERVICES_HERO_SELLER_ITEM_COUNTS[n]);
    }
}

