/*
 * Decompiled with CFR 0.152.
 */
package services.community.custom;

import java.util.ArrayList;
import java.util.StringTokenizer;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExPCCafePointInfo;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Util;
import services.community.custom.ACbConfigManager;
import services.community.custom.CommunityTools;

public class CommunityQuests
implements ICommunityBoardHandler,
ScriptFile {
    private static final int[] bYJ = new int[]{20, 40, 76};

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            CommunityBoardManager.getInstance().registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            CommunityBoardManager.getInstance().removeHandler(this);
        }
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String[] getBypassCommands() {
        return new String[]{"_cbbsquestsmain", "_cbbsquestsocupation"};
    }

    @Override
    public void onBypassCommand(Player player, String string) {
        if (!CommunityTools.checkConditions(player)) {
            String string2 = HtmCache.getInstance().getNotNull("scripts/services/community/pages/locked.htm", player);
            string2 = string2.replace("%name%", player.getName());
            ShowBoard.separateAndSend(string2, player);
            return;
        }
        String string3 = HtmCache.getInstance().getNotNull("scripts/services/community/pages/quests.htm", player);
        String string4 = "";
        if (string.startsWith("_cbbsquestsmain")) {
            string4 = this.aJ(player);
        } else {
            StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
            stringTokenizer.nextToken();
            String[] stringArray = new String[]{};
            while (stringTokenizer.hasMoreTokens()) {
                stringArray = ArrayUtils.add(stringArray, stringTokenizer.nextToken());
            }
            if (string.startsWith("_cbbsquestsocupation")) {
                string4 = this.a(stringArray, player);
            }
        }
        string3 = string3.replace("%content%", string4);
        ShowBoard.separateAndSend(string3, player);
    }

    @Override
    public void onWriteCommand(Player player, String string, String string2, String string3, String string4, String string5, String string6) {
    }

    private String aJ(Player player) {
        String string = "";
        string = string + CommunityQuests.aK(player);
        return string;
    }

    public static String htmlButton(String string, int n, int n2, String string2, Object ... objectArray) {
        String string3 = "bypass " + string2;
        for (Object object : objectArray) {
            string3 = string3 + " " + object.toString();
        }
        return HtmlUtils.htmlButton(string, string3, n, n2);
    }

    public static String htmlButton(String string, int n, String string2, Object ... objectArray) {
        return CommunityQuests.htmlButton(string, n, 22, string2, objectArray);
    }

    private static boolean c(Player player, int n, long l) {
        if (Functions.getItemCount(player, n) < l) {
            if (n == 57) {
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            }
            return false;
        }
        return true;
    }

    private static ArrayList<ClassId> c(ClassId classId) {
        ArrayList<ClassId> arrayList = new ArrayList<ClassId>();
        for (ClassId classId2 : ClassId.values()) {
            if (classId2.getLevel() != classId.getLevel() + 1 || !classId2.childOf(classId)) continue;
            arrayList.add(classId2);
        }
        return arrayList;
    }

    private String a(String[] stringArray, Player player) {
        ClassId classId = player.getClassId();
        if (classId.getLevel() == 4) {
            return this.aJ(player);
        }
        int n = bYJ[classId.getLevel() - 1];
        if (player.getLevel() < n) {
            return this.aJ(player);
        }
        int n2 = Integer.parseInt(stringArray[0]);
        ClassId classId2 = null;
        ArrayList<ClassId> arrayList = CommunityQuests.c(classId);
        for (ClassId classId3 : arrayList) {
            if (classId3.getId() != n2) continue;
            classId2 = classId3;
            break;
        }
        if (classId2 == null) {
            return this.aJ(player);
        }
        int n3 = 0;
        int n4 = 0;
        switch (classId.getLevel()) {
            case 1: {
                n3 = ACbConfigManager.FIRST_CLASS_ID;
                n4 = ACbConfigManager.FIRST_CLASS_PRICE;
                break;
            }
            case 2: {
                n3 = ACbConfigManager.SECOND_CLASS_ID;
                n4 = ACbConfigManager.SECOND_CLASS_PRICE;
                break;
            }
            case 3: {
                n3 = ACbConfigManager.THRID_CLASS_ID;
                n4 = ACbConfigManager.THRID_CLASS_PRICE;
            }
        }
        if (n3 == 0 || n4 == 0) {
            return this.aJ(player);
        }
        if (n3 == -300) {
            if (!CommunityQuests.u(player, n4)) {
                return this.aJ(player);
            }
        } else {
            if (!CommunityQuests.c(player, n3, n4)) {
                return this.aJ(player);
            }
            Functions.removeItem(player, n3, n4);
        }
        Log.add("QUEST\t\u0421\u043c\u0435\u043d\u0430 \u043f\u0440\u043e\u0444\u0435\u0441\u0438\u0438 " + classId.getId() + " -> " + classId2.getId() + " \u0437\u0430 " + n3 + ":" + n4, "service_quests", player);
        if (ACbConfigManager.COMMUNITY_CLASS_MASTERS_REWARD_ITEM.length >= classId.getLevel() && ACbConfigManager.COMMUNITY_CLASS_MASTERS_REWARD_ITEM[classId.getLevel() - 1] > 0 && ACbConfigManager.COMMUNITY_MASTERS_REWARD_AMOUNT.length >= classId.getLevel() && (long)ACbConfigManager.COMMUNITY_MASTERS_REWARD_AMOUNT[classId.getLevel() - 1] > 0L) {
            ItemFunctions.addItem((Playable)player, ACbConfigManager.COMMUNITY_CLASS_MASTERS_REWARD_ITEM[classId.getLevel() - 1], (long)ACbConfigManager.COMMUNITY_MASTERS_REWARD_AMOUNT[classId.getLevel() - 1], true);
        }
        player.sendPacket((IStaticPacket)new SystemMessage(1308));
        player.setClassId(n2, false, false);
        player.broadcastUserInfo(true);
        return this.aJ(player);
    }

    private static String aK(Player player) {
        ClassId classId = player.getClassId();
        String string = CommunityQuests.t(player, classId.getId());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<center>");
        stringBuilder.append(CommunityQuests.localize(player, 1, new Object[0])).append(": <font color=LEVEL>").append(string).append("</font>");
        stringBuilder.append("</center>");
        if (classId.getLevel() == 4) {
            return stringBuilder.toString() + "<br>";
        }
        int n = bYJ[classId.getLevel() - 1];
        if (player.getLevel() < n) {
            return stringBuilder.toString() + "<br1>" + CommunityQuests.localize(player, 1, new Object[0]) + ": " + n + "<br>";
        }
        ArrayList<ClassId> arrayList = CommunityQuests.c(classId);
        if (arrayList.size() == 0) {
            return stringBuilder.toString() + "<br>";
        }
        stringBuilder.append("<center><table>");
        stringBuilder.append("<tr><td>");
        switch (classId.getLevel()) {
            case 1: {
                stringBuilder.append(CommunityQuests.localize(player, 3, ACbConfigManager.FIRST_CLASS_ID, ACbConfigManager.FIRST_CLASS_PRICE));
                break;
            }
            case 2: {
                stringBuilder.append(CommunityQuests.localize(player, 3, ACbConfigManager.SECOND_CLASS_ID, ACbConfigManager.SECOND_CLASS_PRICE));
                break;
            }
            case 3: {
                stringBuilder.append(CommunityQuests.localize(player, 3, ACbConfigManager.THRID_CLASS_ID, ACbConfigManager.THRID_CLASS_PRICE));
            }
        }
        stringBuilder.append("</td></tr>");
        for (ClassId classId2 : arrayList) {
            String string2 = CommunityQuests.t(player, classId2.getId());
            stringBuilder.append("<tr><td>");
            stringBuilder.append("<button value=\"");
            stringBuilder.append(string2);
            stringBuilder.append("\" action=\"bypass _cbbsquestsocupation ");
            stringBuilder.append(classId2.getId());
            if (classId.getLevel() == 3) {
                stringBuilder.append(" 0");
            }
            stringBuilder.append("\" back=\"L2UI_CH3.bigbutton3_down\" fore=\"L2UI_CH3.bigbutton3\" width=134 height=22>");
            stringBuilder.append("</td></tr>");
        }
        stringBuilder.append("</table></center><br><br>");
        return stringBuilder.toString();
    }

    public static String localize(Player player, int n, Object ... objectArray) {
        boolean bl = player.isLangRus();
        switch (n) {
            case 1: {
                return bl ? "\u0412\u0430\u0448\u0430 \u0442\u0435\u043a\u0443\u0449\u0430\u044f \u043f\u0440\u043e\u0444\u0435\u0441\u0441\u0438\u044f" : "Your current occupation";
            }
            case 2: {
                return bl ? "\u0414\u043b\u044f \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u044f \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0435\u0439 \u043f\u0440\u043e\u0444\u0435\u0441\u0441\u0438\u0438 \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u0434\u043e\u0441\u0442\u0438\u0447\u044c \u0443\u0440\u043e\u0432\u043d\u044f" : "To get your's next occupation you should reach level";
            }
            case 3: {
                int n2 = ((Number)objectArray[0]).intValue();
                String string = ItemHolder.getInstance().getTemplate(n2).getName();
                long l = ((Number)objectArray[1]).longValue();
                return bl ? "\u0426\u0435\u043d\u0430 \u0437\u0430 \u043f\u0440\u043e\u0444\u0435\u0441\u0441\u0438\u044e: " + Util.formatAdena(l) + " " + string : "Class occupation price: " + Util.formatAdena(l) + " " + string;
            }
        }
        return "Unknown localize String - " + n;
    }

    private static String t(Player player, int n) {
        String string = HtmlUtils.htmlClassName(n, player);
        return string != null ? string : "Unknown";
    }

    private static boolean u(Player player, int n) {
        if (player == null || player.getPcBangPoints() < n) {
            return false;
        }
        player.setPcBangPoints(player.getPcBangPoints() - n);
        player.sendPacket((IStaticPacket)new ExPCCafePointInfo(player, 0, 1, 2, 12));
        return true;
    }
}

