/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.util.Map;
import l2.commons.text.PrintfFormat;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;

public class AdminQuests
implements IAdminCommandHandler {
    private static final PrintfFormat yR = new PrintfFormat("<center><font color=\"LEVEL\">%s [id=%d]</font><br><edit var=\"new_val\" width=100 height=12></center><br>");
    private static final PrintfFormat yS = new PrintfFormat("<tr><td>%s</td><td>%s</td><td width=30>%s</td></tr>");
    private static final PrintfFormat yT = new PrintfFormat("<button value=\"Set\" action=\"bypass -h admin_quest %d %s %s %s %s\" width=30 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\">");
    private static final PrintfFormat yU = new PrintfFormat("<br><br><br><center><button value=\"Clear Quest\" action=\"bypass -h admin_quest %d CLEAR %s\" width=100 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"> <button value=\"Quests List\" action=\"bypass -h admin_quests %s\" width=100 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></center>");
    private static final PrintfFormat yV = new PrintfFormat("<tr><td><a action=\"bypass -h admin_quest %d %s\">%s</a></td><td>%s</td></tr>");
    private static final PrintfFormat yW = new PrintfFormat("<tr><td><edit var=\"new_quest\" width=100 height=12></td><td><button value=\"Add\" action=\"bypass -h admin_quest $new_quest STATE 2 %s\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanEditCharAll) {
            return false;
        }
        switch (commands) {
            case admin_quests: {
                return AdminQuests.c(this.a(stringArray, 1, player), player);
            }
            case admin_quest: {
                if (stringArray.length < 2) {
                    player.sendMessage("USAGE: //quest id|name [SHOW|STATE|VAR|CLEAR] ...");
                    return true;
                }
                Quest quest = QuestManager.getQuest2(stringArray[1]);
                if (quest == null) {
                    player.sendMessage("Quest " + stringArray[1] + " undefined");
                    return true;
                }
                if (stringArray.length < 3 || stringArray[2].equalsIgnoreCase("SHOW")) {
                    return this.b(quest, stringArray, player);
                }
                if (stringArray[2].equalsIgnoreCase("STATE")) {
                    return this.d(quest, stringArray, player);
                }
                if (stringArray[2].equalsIgnoreCase("VAR")) {
                    return this.c(quest, stringArray, player);
                }
                if (stringArray[2].equalsIgnoreCase("CLEAR")) {
                    return this.a(quest, stringArray, player);
                }
                return this.b(quest, stringArray, player);
            }
        }
        return true;
    }

    private boolean a(Quest quest, String[] stringArray, Player player) {
        Player player2 = this.a(stringArray, 3, player);
        QuestState questState = player2.getQuestState(quest.getName());
        if (questState == null) {
            player.sendMessage("Player " + player2.getName() + " havn't Quest [" + quest.getName() + "]");
            return false;
        }
        questState.exitCurrentQuest(true);
        return AdminQuests.c(player2, player);
    }

    private boolean b(Quest quest, String[] stringArray, Player player) {
        Player player2 = this.a(stringArray, 3, player);
        QuestState questState = player2.getQuestState(quest.getName());
        if (questState == null) {
            player.sendMessage("Player " + player2.getName() + " havn't Quest [" + quest.getName() + "]");
            return false;
        }
        return AdminQuests.a(questState, player);
    }

    private static boolean a(QuestState questState, Player player) {
        Map<String, String> map = questState.getVars();
        int n = questState.getQuest().getQuestIntId();
        String string = questState.getPlayer().getName();
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body>");
        stringBuilder.append(yR.sprintf(questState.getQuest().getClass().getSimpleName(), n));
        stringBuilder.append("<table width=260>");
        stringBuilder.append(yS.sprintf("PLAYER: ", string, ""));
        stringBuilder.append(yS.sprintf("STATE: ", questState.getStateName(), yT.sprintf(n, "STATE", "$new_val", string, "")));
        for (String string2 : map.keySet()) {
            if (string2.equalsIgnoreCase("<state>")) continue;
            stringBuilder.append(yS.sprintf(string2 + ": ", map.get(string2), yT.sprintf(n, "VAR", string2, "$new_val", string)));
        }
        stringBuilder.append(yS.sprintf("<edit var=\"new_name\" width=50 height=12>", "~new var~", yT.sprintf(n, "VAR", "$new_name", "$new_val", string)));
        stringBuilder.append("</table>");
        stringBuilder.append(yU.sprintf(n, string, string));
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        map.clear();
        return true;
    }

    private static boolean c(Player player, Player player2) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body><table width=260>");
        for (QuestState questState : player.getAllQuestsStates()) {
            if (questState == null || questState.getQuest().getQuestIntId() == 255) continue;
            stringBuilder.append(yV.sprintf(questState.getQuest().getQuestIntId(), player.getName(), questState.getQuest().getName(), questState.getStateName()));
        }
        stringBuilder.append(yW.sprintf(new Object[]{player.getName()}));
        stringBuilder.append("</table></body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player2.sendPacket((IStaticPacket)npcHtmlMessage);
        return true;
    }

    private boolean c(Quest quest, String[] stringArray, Player player) {
        if (stringArray.length < 5) {
            player.sendMessage("USAGE: //quest id|name VAR varname newvalue [target]");
            return false;
        }
        Player player2 = this.a(stringArray, 5, player);
        QuestState questState = player2.getQuestState(quest.getName());
        if (questState == null) {
            player.sendMessage("Player " + player2.getName() + " havn't Quest [" + quest.getName() + "], init quest by command:");
            player.sendMessage("//quest id|name STATE 1|2|3 [target]");
            return false;
        }
        if (stringArray[4].equalsIgnoreCase("~") || stringArray[4].equalsIgnoreCase("#")) {
            questState.unset(stringArray[3]);
        } else {
            questState.set(stringArray[3], stringArray[4]);
        }
        return AdminQuests.a(questState, player);
    }

    private boolean d(Quest quest, String[] stringArray, Player player) {
        if (stringArray.length < 4) {
            player.sendMessage("USAGE: //quest id|name STATE 1|2|3 [target]");
            return false;
        }
        int n = 0;
        try {
            n = Integer.parseInt(stringArray[3]);
        }
        catch (Exception exception) {
            player.sendMessage("Wrong State ID: " + stringArray[3]);
            return false;
        }
        Player player2 = this.a(stringArray, 4, player);
        QuestState questState = player2.getQuestState(quest.getName());
        if (questState == null) {
            player.sendMessage("Init Quest [" + quest.getName() + "] for " + player2.getName());
            questState = quest.newQuestState(player2, n);
            questState.set("cond", "1");
        } else {
            questState.setState(n);
        }
        return AdminQuests.a(questState, player);
    }

    private Player a(String[] stringArray, int n, Player player) {
        if (n >= 0 && stringArray.length > n) {
            Player player2 = World.getPlayer(stringArray[n]);
            if (player2 == null) {
                player.sendMessage("Can't find player: " + stringArray[n]);
            }
            return player2;
        }
        GameObject gameObject = player.getTarget();
        if (gameObject != null && gameObject.isPlayer()) {
            return (Player)gameObject;
        }
        return player;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_quests,
        admin_quest;

    }
}

