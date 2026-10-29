/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Util;

public class Rename
extends Functions {
    public void rename_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_CHANGE_NICK_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/rename_char.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_CHANGE_NICK_ITEM));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_CHANGE_NICK_PRICE));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void rename(String[] stringArray) {
        Player player = this.getSelf();
        if (player == null || !Rename.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_CHANGE_NICK_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (stringArray == null || stringArray.length < 1) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/rename_char_err01.htm"));
            return;
        }
        if (player.isHero() || player.isClanLeader()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/rename_char_err03.htm"));
            return;
        }
        if (player.getEvent(SiegeEvent.class) != null) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/rename_char_err03.htm"));
            return;
        }
        String string = stringArray[0];
        if (Util.isMatchingRegexp(string, Config.CNAME_FORBIDDEN_PATTERN) || CharacterDAO.getInstance().getObjectIdByName(string) > 0) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/rename_char_err02.htm"));
            return;
        }
        if (!Util.isMatchingRegexp(string, Config.CUSTOM_CNAME_TEMPLATE)) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/rename_char_err01.htm"));
            return;
        }
        if (ItemFunctions.getItemCount(player, Config.SERVICES_CHANGE_NICK_ITEM) < (long)Config.SERVICES_CHANGE_NICK_PRICE) {
            if (Config.SERVICES_CHANGE_NICK_ITEM == 57) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            }
            return;
        }
        ItemFunctions.removeItem(player, Config.SERVICES_CHANGE_NICK_ITEM, Config.SERVICES_CHANGE_NICK_PRICE, true);
        String string2 = player.getName();
        player.reName(string, true);
        player.broadcastCharInfo();
        Log.service("Rename", player, "Change name - " + string2 + " on new name " + string + " price " + Config.SERVICES_CHANGE_CLAN_NAME_ITEM + " " + Config.SERVICES_CHANGE_CLAN_NAME_PRICE);
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/rename_char_msg01.htm").replace("%old_name%", string2).replace("%new_name%", string));
    }
}

