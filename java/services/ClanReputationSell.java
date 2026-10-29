/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;

public class ClanReputationSell
extends Functions {
    public void clan_reputation_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_CLAN_REPUTATION_ENABLE) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.isInPeaceZone()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_peace_zone.htm"));
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendMessage(new CustomMessage("services.ClanReputationSell.GetClan", player, new Object[0]));
            return;
        }
        if (clan.getLevel() < Config.MIN_CLAN_LEVEL_FOR_REPUTATION) {
            player.sendMessage(new CustomMessage("services.ClanReputationSell.GetClanLevelFirst", player, new Object[0]));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/clan_reputation_sell.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_CLAN_REPUTATION_ITEM_ID));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_CLAN_REPUTATION_ITEM_COUNT));
        npcHtmlMessage.replace("%reputation_amount%", String.valueOf(Config.SERVICES_CLAN_REPUTATION_AMOUNT));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void clan_reputation_up() {
        Player player = this.getSelf();
        if (player == null || !ClanReputationSell.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_CLAN_REPUTATION_ENABLE) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.isInPeaceZone()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_peace_zone.htm"));
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendMessage(new CustomMessage("services.ClanReputationSell.GetClan", player, new Object[0]));
            return;
        }
        if (clan.getLevel() < Config.MIN_CLAN_LEVEL_FOR_REPUTATION) {
            player.sendMessage(new CustomMessage("services.ClanReputationSell.GetClanLevelFirst", player, new Object[0]));
            return;
        }
        if (ClanReputationSell.getItemCount(player, Config.SERVICES_CLAN_REPUTATION_ITEM_ID) < (long)Config.SERVICES_CLAN_REPUTATION_ITEM_COUNT) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        int n = player.getClan().getReputationScore();
        ClanReputationSell.removeItem(player, Config.SERVICES_CLAN_REPUTATION_ITEM_ID, Config.SERVICES_CLAN_REPUTATION_ITEM_COUNT);
        clan.incReputation(Config.SERVICES_CLAN_REPUTATION_AMOUNT, true, "ClanReputationServicesAdd");
        player.sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(Config.SERVICES_CLAN_REPUTATION_AMOUNT));
        Log.service("ClanReputationSell", player, "buy reputation count - " + Config.SERVICES_CLAN_REPUTATION_AMOUNT + " old reputation - " + n + " price " + Config.SERVICES_CLAN_REPUTATION_ITEM_ID + " " + Config.SERVICES_CLAN_REPUTATION_ITEM_COUNT);
    }
}

