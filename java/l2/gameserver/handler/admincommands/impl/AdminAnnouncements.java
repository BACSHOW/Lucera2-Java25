/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.util.List;
import l2.gameserver.Announcements;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;

public class AdminAnnouncements
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanAnnounce) {
            return false;
        }
        switch (commands) {
            case admin_list_announcements: {
                this.listAnnouncements(player);
                break;
            }
            case admin_announce_menu: {
                Announcements.getInstance().announceToAll(string.substring(20));
                this.listAnnouncements(player);
                break;
            }
            case admin_announce_announcements: {
                for (Player player2 : GameObjectsStorage.getAllPlayersForIterate()) {
                    Announcements.getInstance().showAnnouncements(player2);
                }
                this.listAnnouncements(player);
                break;
            }
            case admin_add_announcement: {
                if (stringArray.length < 3) {
                    return false;
                }
                try {
                    int n = Integer.parseInt(stringArray[1]);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 2; i < stringArray.length; ++i) {
                        stringBuilder.append(" ").append(stringArray[i]);
                    }
                    Announcements.getInstance().addAnnouncement(n, stringBuilder.toString(), true);
                    this.listAnnouncements(player);
                }
                catch (Exception exception) {}
                break;
            }
            case admin_del_announcement: {
                if (stringArray.length != 2) {
                    return false;
                }
                int n = Integer.parseInt(stringArray[1]);
                Announcements.getInstance().delAnnouncement(n);
                this.listAnnouncements(player);
                break;
            }
            case admin_announce: {
                Announcements.getInstance().announceToAll(string.substring(15));
                break;
            }
            case admin_a: {
                Announcements.getInstance().announceToAll(string.substring(8));
                break;
            }
            case admin_crit_announce: 
            case admin_c: {
                if (stringArray.length < 2) {
                    return false;
                }
                Announcements.getInstance().announceToAll(player.getName() + ": " + string.replaceFirst("admin_crit_announce ", "").replaceFirst("admin_c ", ""), ChatType.CRITICAL_ANNOUNCE);
                break;
            }
            case admin_toscreen: 
            case admin_s: {
                if (stringArray.length < 2) {
                    return false;
                }
                String string2 = player.getName() + ": " + string.replaceFirst("admin_toscreen ", "").replaceFirst("admin_s ", "");
                int n = 3000 + string2.length() * 100;
                ExShowScreenMessage exShowScreenMessage = new ExShowScreenMessage(string2, n, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, string2.length() < 64);
                for (Player player3 : GameObjectsStorage.getAllPlayersForIterate()) {
                    player3.sendPacket((IStaticPacket)exShowScreenMessage);
                }
                break;
            }
            case admin_reload_announcements: {
                Announcements.getInstance().loadAnnouncements();
                this.listAnnouncements(player);
                player.sendMessage("Announcements reloaded.");
            }
        }
        return true;
    }

    public void listAnnouncements(Player player) {
        List<Announcements.Announce> list = Announcements.getInstance().getAnnouncements();
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body>");
        stringBuilder.append("<table width=260><tr>");
        stringBuilder.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td width=180><center>Announcement Menu</center></td>");
        stringBuilder.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("</tr></table>");
        stringBuilder.append("<br><br>");
        stringBuilder.append("<center>Add or announce a new announcement:</center>");
        stringBuilder.append("<center><multiedit var=\"new_announcement\" width=240 height=30></center><br>");
        stringBuilder.append("<center>Time(in seconds, 0 - only for start)<edit var=\"time\" width=40 height=20></center><br>");
        stringBuilder.append("<center><table><tr><td>");
        stringBuilder.append("<button value=\"Add\" action=\"bypass -h admin_add_announcement $time $new_announcement\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Announce\" action=\"bypass -h admin_announce_menu $new_announcement\" width=64 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Reload\" action=\"bypass -h admin_reload_announcements\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Broadcast\" action=\"bypass -h admin_announce_announcements\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\">");
        stringBuilder.append("</td></tr></table></center>");
        stringBuilder.append("<br>");
        for (int i = 0; i < list.size(); ++i) {
            Announcements.Announce announce = list.get(i);
            stringBuilder.append("<table width=260><tr><td width=180>" + announce.getAnnounce() + "</td><td width=40>" + announce.getTime() + "</td><<td width=40>");
            stringBuilder.append("<button value=\"Delete\" action=\"bypass -h admin_del_announcement " + i + "\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table>");
        }
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_list_announcements,
        admin_announce_announcements,
        admin_add_announcement,
        admin_del_announcement,
        admin_announce,
        admin_a,
        admin_announce_menu,
        admin_crit_announce,
        admin_c,
        admin_toscreen,
        admin_s,
        admin_reload_announcements;

    }
}

