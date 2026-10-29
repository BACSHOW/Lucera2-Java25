/*
 * Decompiled with CFR 0.152.
 */
package handler.admincommands;

import handler.admincommands.ScriptAdminCommand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Pair;

public class AdminTeleportBookmark
extends ScriptAdminCommand {
    private static int lM = 12;
    private static String lN = "gmbk_";
    private static Comparator<Pair<String, Location>> lO = new Comparator<Pair<String, Location>>(){

        @Override
        public int compare(Pair<String, Location> pair, Pair<String, Location> pair2) {
            return pair.getLeft().compareTo(pair2.getLeft());
        }
    };

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanUseGMCommand) {
            return false;
        }
        switch (commands) {
            case admin_bkpage: {
                int n = stringArray.length > 1 ? Integer.parseInt(stringArray[1]) : 1;
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
                npcHtmlMessage.setHtml(this.a(this.k(player), n));
                player.sendPacket((IStaticPacket)npcHtmlMessage);
                return true;
            }
            case admin_bkgo: {
                if (stringArray.length > 1) {
                    String string2 = stringArray[1].replace("<", "&lt;").replace(">", "&gt;");
                    String string3 = player.getVar(lN + string2);
                    if (string3 != null) {
                        player.teleToLocation(Location.parseLoc(string3));
                    }
                }
                return true;
            }
            case admin_bk: {
                Object object;
                if (stringArray.length > 1) {
                    object = stringArray[1].replace("<", "&lt;").replace(">", "&gt;");
                    player.setVar(lN + (String)object, player.getLoc().toXYZString(), -1L);
                }
                object = new NpcHtmlMessage(5);
                ((NpcHtmlMessage)object).setHtml(this.a(this.k(player), 1));
                player.sendPacket((IStaticPacket)object);
                return true;
            }
            case admin_bkdel: {
                Object object;
                if (stringArray.length > 1) {
                    object = stringArray[1].replace("<", "&lt;").replace(">", "&gt;");
                    player.unsetVar(lN + (String)object);
                }
                object = new NpcHtmlMessage(5);
                ((NpcHtmlMessage)object).setHtml(this.a(this.k(player), 1));
                player.sendPacket((IStaticPacket)object);
                return true;
            }
        }
        return false;
    }

    private List<Pair<String, Location>> k(Player player) {
        ArrayList<Pair<String, Location>> arrayList = new ArrayList<Pair<String, Location>>();
        MultiValueSet<String> multiValueSet = player.getVars();
        for (Map.Entry entry : multiValueSet.entrySet()) {
            if (entry == null || entry.getKey() == null || !((String)entry.getKey()).startsWith(lN)) continue;
            arrayList.add(Pair.of(((String)entry.getKey()).substring(lN.length()), Location.parseLoc(entry.getValue().toString())));
        }
        Collections.sort(arrayList, lO);
        return arrayList;
    }

    private String a(List<Pair<String, Location>> list, int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><body>");
        stringBuilder.append("<center><br>Bookmark list<br>");
        stringBuilder.append("<table width=270 border=0>");
        int n2 = Math.max(0, (n - 1) * lM);
        int n3 = Math.min(list.size(), n * lM);
        for (int i = n2; i < n3; ++i) {
            Pair<String, Location> pair = list.get(i);
            stringBuilder.append("<tr>");
            stringBuilder.append("<td> ");
            stringBuilder.append("<a action=\"bypass admin_bkgo ").append(pair.getKey()).append("\"> [");
            stringBuilder.append(pair.getKey()).append(", ").append(pair.getValue().toXYZString());
            stringBuilder.append("] </a> </td><td> ");
            stringBuilder.append("<a action=\"bypass admin_bkdel ").append(pair.getKey()).append("\">del");
            stringBuilder.append(" </td>");
            stringBuilder.append(" </tr>");
        }
        stringBuilder.append("<tr><td align=center>");
        stringBuilder.append("<table border=0 cellspacing=2 cellpadding=2><tr>");
        n = Math.max(1, n);
        if (n > 1) {
            stringBuilder.append("<td align=right> ").append("<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">".replace("%prev_bypass%", "admin_bkpage " + (n - 1))).append(" </td>");
        }
        stringBuilder.append("<td> ").append(n).append(" </td>");
        if (n <= list.size() / lM) {
            stringBuilder.append("<td> ").append("<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">".replace("%next_bypass%", "admin_bkpage " + (n + 1))).append(" </td>");
        }
        stringBuilder.append("</tr></table>");
        stringBuilder.append("</td><td></td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("</center>");
        stringBuilder.append("</body></html>");
        return stringBuilder.toString();
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    static enum Commands {
        admin_bk,
        admin_bkgo,
        admin_bkdel,
        admin_bkpage;

    }
}

