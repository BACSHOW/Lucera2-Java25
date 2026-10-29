/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.StringTokenizer;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.CastleManorManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.manor.CropProcure;
import l2.gameserver.templates.manor.SeedProduction;

public class AdminManor
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().Menu) {
            return false;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        if ((string = stringTokenizer.nextToken()).equals("admin_manor")) {
            this.showMainPage(player);
        } else if (string.equals("admin_manor_reset")) {
            int n = 0;
            try {
                n = Integer.parseInt(stringTokenizer.nextToken());
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (n > 0) {
                Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, n);
                castle.setCropProcure(new ArrayList<CropProcure>(), 0);
                castle.setCropProcure(new ArrayList<CropProcure>(), 1);
                castle.setSeedProduction(new ArrayList<SeedProduction>(), 0);
                castle.setSeedProduction(new ArrayList<SeedProduction>(), 1);
                castle.saveCropData();
                castle.saveSeedData();
                player.sendMessage("Manor data for " + castle.getName() + " was nulled");
            } else {
                for (Castle castle : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
                    castle.setCropProcure(new ArrayList<CropProcure>(), 0);
                    castle.setCropProcure(new ArrayList<CropProcure>(), 1);
                    castle.setSeedProduction(new ArrayList<SeedProduction>(), 0);
                    castle.setSeedProduction(new ArrayList<SeedProduction>(), 1);
                    castle.saveCropData();
                    castle.saveSeedData();
                }
                player.sendMessage("Manor data was nulled");
            }
            this.showMainPage(player);
        } else if (string.equals("admin_manor_save")) {
            CastleManorManager.getInstance().save();
            player.sendMessage("Manor System: all data saved");
            this.showMainPage(player);
        } else if (string.equals("admin_manor_disable")) {
            boolean bl = CastleManorManager.getInstance().isDisabled();
            CastleManorManager.getInstance().setDisabled(!bl);
            if (bl) {
                player.sendMessage("Manor System: enabled");
            } else {
                player.sendMessage("Manor System: disabled");
            }
            this.showMainPage(player);
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void showMainPage(Player player) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body>");
        stringBuilder.append("<center><font color=\"LEVEL\"> [Manor System] </font></center><br>");
        stringBuilder.append("<table width=\"100%\">");
        stringBuilder.append("<tr><td>Disabled: " + (CastleManorManager.getInstance().isDisabled() ? "yes" : "no") + "</td>");
        stringBuilder.append("<td>Under Maintenance: " + (CastleManorManager.getInstance().isUnderMaintenance() ? "yes" : "no") + "</td></tr>");
        stringBuilder.append("<tr><td>Approved: " + (ServerVariables.getBool("ManorApproved") ? "yes" : "no") + "</td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("<center><table>");
        stringBuilder.append("<tr><td><button value=\"" + (CastleManorManager.getInstance().isDisabled() ? "Enable" : "Disable") + "\" action=\"bypass -h admin_manor_disable\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td><button value=\"Reset\" action=\"bypass -h admin_manor_reset\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
        stringBuilder.append("<tr><td><button value=\"Refresh\" action=\"bypass -h admin_manor\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td><button value=\"Back\" action=\"bypass -h admin_admin\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
        stringBuilder.append("</table></center>");
        stringBuilder.append("<br><center>Castle Information:<table width=\"100%\">");
        stringBuilder.append("<tr><td></td><td>Current Period</td><td>Next Period</td></tr>");
        for (Castle castle : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
            stringBuilder.append("<tr><td>" + castle.getName() + "</td><td>" + castle.getManorCost(0) + "a</td><td>" + castle.getManorCost(1) + "a</td></tr>");
        }
        stringBuilder.append("</table><br>");
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private static enum Commands {
        admin_manor,
        admin_manor_reset,
        admin_manor_save,
        admin_manor_disable;

    }
}

