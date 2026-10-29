/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.StringUtils;

public class NickColor
extends Functions {
    public void list() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_CHANGE_NICK_COLOR_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/change_nick_color.htm");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Config.SERVICES_CHANGE_NICK_COLOR_LIST.length; ++i) {
            String string = Config.SERVICES_CHANGE_NICK_COLOR_LIST[i];
            String string2 = StringHolder.getInstance().getNotNull(player, "services.NameColor.entryHtml");
            string2 = string2.replaceAll("%color%", string);
            string2 = string2.replaceAll("%fontColor%", string.substring(4, 6) + string.substring(2, 4) + string.substring(0, 2));
            string2 = string2.replaceAll("%itemId%", String.valueOf(Config.SERVICES_CHANGE_NICK_COLOR_ITEM[i]));
            string2 = string2.replaceAll("%itemCount%", String.valueOf(Config.SERVICES_CHANGE_NICK_COLOR_PRICE[i]));
            string2 = string2.replaceAll("%playerName%", player.getName());
            stringBuilder.append(string2);
        }
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void change(String[] stringArray) {
        Player player = this.getSelf();
        if (player == null || !NickColor.CheckPlayerConditions(player)) {
            return;
        }
        if (stringArray == null || stringArray.length < 1) {
            return;
        }
        if (!Config.SERVICES_CHANGE_NICK_COLOR_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (stringArray[0].equalsIgnoreCase("FFFFFF")) {
            player.setNameColor(Integer.decode("0xFFFFFF"));
            player.broadcastUserInfo(true);
            return;
        }
        String string = StringUtils.trimToEmpty(stringArray[0]);
        if (string.isEmpty()) {
            return;
        }
        for (int i = 0; i < Config.SERVICES_CHANGE_NICK_COLOR_LIST.length; ++i) {
            if (!StringUtils.equalsIgnoreCase(Config.SERVICES_CHANGE_NICK_COLOR_LIST[i], string)) continue;
            int n = Config.SERVICES_CHANGE_NICK_COLOR_ITEM[i];
            long l = Config.SERVICES_CHANGE_NICK_COLOR_PRICE[i];
            if (ItemFunctions.getItemCount(player, n) < l) {
                if (n == 57) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                } else {
                    player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                }
                return;
            }
            if (ItemFunctions.removeItem(player, n, l, true) < l) continue;
            player.setNameColor(Integer.decode("0x" + stringArray[0]));
            player.broadcastUserInfo(true);
            Log.service("NickColor", player, "change nick color on " + Integer.decode("0x" + stringArray[0]) + " for " + n + " amount " + l);
        }
    }
}

