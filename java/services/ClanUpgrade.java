/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.SiegeUtils;

public class ClanUpgrade
extends Functions {
    public void clan_upgrade_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_CLANLEVEL_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.isInPeaceZone()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_peace_zone.htm"));
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendPacket((IStaticPacket)Msg.YOU_ARE_NOT_A_CLAN_MEMBER);
            return;
        }
        if (clan.getLeaderId() != player.getObjectId()) {
            player.sendPacket((IStaticPacket)Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }
        if (clan.getLevel() < 1 || clan.getLevel() >= Config.SERVICES_CLAN_MAX_SELL_LEVEL) {
            player.sendMessage(new CustomMessage("services.ClanUpgrade.ToHighOrToLow", player, new Object[0]));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/clan_upgrade.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_CLANLEVEL_SELL_ITEM[clan.getLevel() - 1]));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_CLANLEVEL_SELL_PRICE[clan.getLevel() - 1]));
        npcHtmlMessage.replace("%clan_level_next%", String.valueOf(clan.getLevel() + 1));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void clan_upgrade() {
        Player player = this.getSelf();
        if (player == null || !ClanUpgrade.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_CLANLEVEL_SELL_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.isInPeaceZone()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_peace_zone.htm"));
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendMessage(new CustomMessage("services.ClanUpgrade.GetClan", player, new Object[0]));
            return;
        }
        if (clan.getLeaderId() != player.getObjectId()) {
            player.sendMessage(new CustomMessage("services.ClanUpgrade.ClanLeaderOnly", player, new Object[0]));
            return;
        }
        if (clan.getLevel() < 1 || clan.getLevel() >= Config.SERVICES_CLAN_MAX_SELL_LEVEL) {
            player.sendMessage(new CustomMessage("services.ClanUpgrade.ToHighOrToLow", player, new Object[0]));
            return;
        }
        int n = clan.getLevel() + 1;
        int n2 = Config.SERVICES_CLANLEVEL_SELL_ITEM[Math.min(Math.max(0, n - 2), Config.SERVICES_CLANLEVEL_SELL_ITEM.length - 1)];
        long l = Config.SERVICES_CLANLEVEL_SELL_PRICE[Math.min(Math.max(0, n - 2), Config.SERVICES_CLANLEVEL_SELL_PRICE.length - 1)];
        if (Functions.getItemCount(player, n2) < l) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        Functions.removeItem(player, n2, l);
        clan.setLevel(clan.getLevel() + 1);
        clan.updateClanInDB();
        clan.broadcastClanStatus(true, true, true);
        player.sendMessage(new CustomMessage("services.ClanUpgrade.Congratulation", player, new Object[0]));
        Log.service("ClanUpgrade", player, "received a clan level  for Clan " + player.getClan().getName() + " bought for " + n2 + " amount " + l);
        if (clan.getLevel() >= Config.MIN_CLAN_LEVEL_FOR_SIEGE_REGISTRATION) {
            SiegeUtils.addSiegeSkills(player);
            player.sendPacket((IStaticPacket)new SkillList(player));
            player.sendEtcStatusUpdate();
            player.updateStats();
        }
    }
}

