/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.tables.ClanTable;

public class AdminClanHall
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanEditNPC) {
            return false;
        }
        ClanHall clanHall = null;
        if (stringArray.length > 1) {
            clanHall = ResidenceHolder.getInstance().getResidence(ClanHall.class, Integer.parseInt(stringArray[1]));
        }
        if (clanHall == null) {
            this.showClanHallSelectPage(player);
            return true;
        }
        switch (commands) {
            case admin_clanhall: {
                this.showClanHallSelectPage(player);
                break;
            }
            case admin_clanhallset: {
                GameObject gameObject = player.getTarget();
                Player player2 = player;
                if (gameObject != null && gameObject.isPlayer()) {
                    player2 = (Player)gameObject;
                }
                if (player2.getClan() == null) {
                    player.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                    break;
                }
                clanHall.changeOwner(player2.getClan());
                break;
            }
            case admin_clanhalldel: {
                clanHall.changeOwner(null);
                break;
            }
            case admin_clanhallteleportself: {
                Zone zone = clanHall.getZone();
                if (zone == null) break;
                player.teleToLocation(zone.getSpawn());
            }
        }
        this.showClanHallPage(player, clanHall);
        return true;
    }

    public void showClanHallSelectPage(Player player) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body>");
        stringBuilder.append("<table width=268><tr>");
        stringBuilder.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td width=180><center><font color=\"LEVEL\">Clan Halls:</font></center></td>");
        stringBuilder.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("</tr></table><br>");
        stringBuilder.append("<table width=268>");
        stringBuilder.append("<tr><td width=130>ClanHall Name</td><td width=58>Town</td><td width=80>Owner</td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void showClanHallPage(Player player, ClanHall clanHall) {
        Clan clan;
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body>");
        stringBuilder.append("<table width=260><tr>");
        stringBuilder.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td width=180><center>ClanHall Name</center></td>");
        stringBuilder.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_clanhall\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("</tr></table>");
        stringBuilder.append("<center>");
        stringBuilder.append("<br><br><br>ClanHall: " + clanHall.getName() + "<br>");
        stringBuilder.append("Location: &^" + clanHall.getId() + ";<br>");
        stringBuilder.append("ClanHall Owner: ");
        Clan clan2 = clan = clanHall.getOwnerId() == 0 ? null : ClanTable.getInstance().getClan(clanHall.getOwnerId());
        if (clan == null) {
            stringBuilder.append("none");
        } else {
            stringBuilder.append(clan.getName());
        }
        stringBuilder.append("<br><br><br>");
        stringBuilder.append("<table>");
        stringBuilder.append("<tr><td><button value=\"Open Doors\" action=\"bypass -h admin_clanhallopendoors " + clanHall.getId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td><button value=\"Close Doors\" action=\"bypass -h admin_clanhallclosedoors " + clanHall.getId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("<br>");
        stringBuilder.append("<table>");
        stringBuilder.append("<tr><td><button value=\"Give ClanHall\" action=\"bypass -h admin_clanhallset " + clanHall.getId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td><button value=\"Take ClanHall\" action=\"bypass -h admin_clanhalldel " + clanHall.getId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("<br>");
        stringBuilder.append("<table><tr>");
        stringBuilder.append("<td><button value=\"Teleport self\" action=\"bypass -h admin_clanhallteleportself " + clanHall.getId() + " \" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("</center>");
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_clanhall,
        admin_clanhallset,
        admin_clanhalldel,
        admin_clanhallteleportself;

    }
}

