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

public class PKClean
extends Functions {
    public void pkclean_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_PK_CLEAN_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.getPkKills() == 0) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_no_pk.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/pk_clean.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_PK_CLEAN_SELL_ITEM));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_PK_CLEAN_SELL_PRICE));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void pkclean() {
        Player player = this.getSelf();
        if (player == null || !PKClean.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_PK_CLEAN_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.getPkKills() == 0) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_no_pk.htm"));
            return;
        }
        if (PKClean.getItemCount(player, Config.SERVICES_PK_CLEAN_SELL_ITEM) < Config.SERVICES_PK_CLEAN_SELL_PRICE) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        PKClean.removeItem(player, Config.SERVICES_PK_CLEAN_SELL_ITEM, Config.SERVICES_PK_CLEAN_SELL_PRICE);
        player.setPkKills(0);
        player.sendUserInfo(true);
        Log.service("PKClean", player, "reduced PK kills to " + player.getPkKills() + " bought for" + Config.SERVICES_PK_CLEAN_SELL_ITEM + " " + Config.SERVICES_PK_CLEAN_SELL_PRICE);
    }
}

