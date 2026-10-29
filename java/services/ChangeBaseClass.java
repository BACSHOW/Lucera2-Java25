/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.util.ArrayList;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.Player;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.base.PlayerClass;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class ChangeBaseClass
extends Functions {
    public void changebase_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_CHANGE_BASE_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.getSubClasses().size() == 1) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/changebase_err01.htm"));
            return;
        }
        if (!player.getActiveClass().isBase()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/changebase_err02.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/changebase.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_CHANGE_BASE_ITEM));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_CHANGE_BASE_PRICE));
        ArrayList<SubClass> arrayList = new ArrayList<SubClass>();
        if (player.getActiveClass().isBase()) {
            arrayList.addAll(player.getSubClasses().values());
            arrayList.remove(player.getSubClasses().get(player.getBaseClassId()));
            for (SubClass object : player.getSubClasses().values()) {
                for (SubClass subClass : player.getSubClasses().values()) {
                    if (object == subClass || Config.SERVICES_CHANGE_BASE_LIST_UNCOMPATABLE || PlayerClass.areClassesComportable(PlayerClass.values()[object.getClassId()], PlayerClass.values()[subClass.getClassId()]) || subClass.getLevel() < 75) continue;
                    arrayList.remove(subClass);
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (!arrayList.isEmpty()) {
            String string = HtmCache.getInstance().getNotNull("scripts/services/changebase_list.htm", player);
            for (SubClass subClass : arrayList) {
                stringBuilder.append(string.replace("%class_id%", String.valueOf(subClass.getClassId())).replace("%class_name%", HtmlUtils.htmlClassName(subClass.getClassId(), player)));
            }
        }
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void changebase(String[] stringArray) {
        Player player = this.getSelf();
        if (player == null || !ChangeBaseClass.CheckPlayerConditions(player)) {
            return;
        }
        if (stringArray == null || stringArray.length < 1) {
            return;
        }
        if (!Config.SERVICES_CHANGE_BASE_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (player.getSubClasses().size() == 1) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/changebase_err01.htm"));
            return;
        }
        if (!player.getActiveClass().isBase()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/changebase_err02.htm"));
            return;
        }
        if (!player.isInPeaceZone() || !player.getReflection().isDefault()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/changebase_err03.htm"));
            return;
        }
        if (player.isHero()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/changebase_err04.htm"));
            return;
        }
        if (ItemFunctions.getItemCount(player, Config.SERVICES_CHANGE_BASE_ITEM) < (long)Config.SERVICES_CHANGE_BASE_PRICE) {
            if (Config.SERVICES_CHANGE_BASE_ITEM == 57) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            }
            return;
        }
        ItemFunctions.removeItem(player, Config.SERVICES_CHANGE_BASE_ITEM, Config.SERVICES_CHANGE_BASE_PRICE, true);
        int n = Integer.parseInt(stringArray[0]);
        SubClass subClass = player.getSubClasses().get(n);
        player.getActiveClass().setBase(false);
        player.getActiveClass().setExp(player.getExp());
        player.checkSkills();
        subClass.setBase(true);
        player.setBaseClass(n);
        player.setHairColor(0);
        player.setHairStyle(0);
        player.setFace(0);
        if (player.isNoble()) {
            player.setNoble(false);
            NoblesController.getInstance().removeNoble(player);
            HeroController.getInstance().removeHero(player);
            NoblesController.getInstance().addNoble(player);
            player.setNoble(true);
        }
        player.logout();
        Log.service("ChangeBaseClass", player, " base changed to " + n + " for " + Config.SERVICES_CHANGE_BASE_ITEM + " " + Config.SERVICES_CHANGE_BASE_PRICE);
    }
}

