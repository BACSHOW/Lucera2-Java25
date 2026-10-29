package handler.admincommands;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;
import l2.commons.dao.JdbcEntityState;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.utils.HtmlUtils;

public class AdminResidence extends ScriptAdminCommand {
   @Override
   public boolean useAdminCommand(Enum var1, String[] var2, String var3, Player var4) {
      AdminResidence.Commands var5 = (AdminResidence.Commands)var1;
      if (!var4.getPlayerAccess().CanEditNPC) {
         return false;
      }

      switch (var5) {
         case admin_residence_list:
            NpcHtmlMessage var26 = new NpcHtmlMessage(5);
            var26.setFile("admin/residence/residence_list.htm");
            StringBuilder var10 = new StringBuilder(200);

            for (Residence var29 : ResidenceHolder.getInstance().getResidences()) {
               if (var29 != null) {
                  var10.append("<tr><td>");
                  var10.append("<a action=\"bypass -h admin_residence ")
                     .append(var29.getId())
                     .append("\">")
                     .append(HtmlUtils.htmlResidenceName(var29.getId()))
                     .append("</a>");
                  var10.append("</td><td>");
                  Clan var31 = var29.getOwner();
                  if (var31 == null) {
                     var10.append("NPC");
                  } else {
                     var10.append(var31.getName());
                  }

                  var10.append("</td></tr>");
               }
            }

            var26.replace("%residence_list%", var10.toString());
            var4.sendPacket(var26);
            break;
         case admin_residence:
            if (var2.length != 2) {
               return false;
            }

            Residence var20 = ResidenceHolder.getInstance().getResidence(Integer.parseInt(var2[1]));
            if (var20 == null) {
               return false;
            }

            SiegeEvent var24 = var20.getSiegeEvent();
            NpcHtmlMessage var9 = new NpcHtmlMessage(5);
            var9.setFile("admin/residence/siege_info.htm");
            var9.replace("%residence%", HtmlUtils.htmlResidenceName(var20.getId()));
            var9.replace("%id%", String.valueOf(var20.getId()));
            var9.replace("%owner%", var20.getOwner() == null ? "NPC" : var20.getOwner().getName());
            var9.replace("%cycle%", String.valueOf(var20.getCycle()));
            var9.replace("%paid_cycle%", String.valueOf(var20.getPaidCycle()));
            var9.replace("%reward_count%", String.valueOf(var20.getRewardCount()));
            var9.replace("%left_time%", String.valueOf(var20.getCycleDelay()));
            StringBuilder var11 = new StringBuilder(100);

            for (Entry var30 : var24.getObjects().entrySet()) {
               for (Object __raw_var33_82 : (Iterable<?>)(Object)((List)var30.getValue())) {
                  Serializable var33 = (Serializable)__raw_var33_82;
                  if (var33 instanceof SiegeClanObject) {
                     SiegeClanObject var34 = (SiegeClanObject)var33;
                     var11.append("<tr>")
                        .append("<td>")
                        .append(var34.getClan().getName())
                        .append("</td>")
                        .append("<td>")
                        .append(var34.getClan().getLeaderName())
                        .append("</td>")
                        .append("<td>")
                        .append(var34.getType())
                        .append("</td>")
                        .append("</tr>");
                  }
               }
            }

            var9.replace("%clans%", var11.toString());
            var9.replace("%hour%", String.valueOf(var20.getSiegeDate().get(11)));
            var9.replace("%minute%", String.valueOf(var20.getSiegeDate().get(12)));
            var9.replace("%day%", String.valueOf(var20.getSiegeDate().get(5)));
            var9.replace("%month%", String.valueOf(var20.getSiegeDate().get(2) + 1));
            var9.replace("%year%", String.valueOf(var20.getSiegeDate().get(1)));
            var4.sendPacket(var9);
            break;
         case admin_set_owner:
            if (var2.length != 3) {
               return false;
            }

            Residence var19 = ResidenceHolder.getInstance().getResidence(Integer.parseInt(var2[1]));
            if (var19 == null) {
               return false;
            }

            Clan var12 = null;
            String var13 = var2[2];
            if (!var13.equalsIgnoreCase("npc")) {
               var12 = ClanTable.getInstance().getClanByName(var13);
               if (var12 == null) {
                  var4.sendPacket(SystemMsg.INCORRECT_NAME);
                  AdminCommandHandler.getInstance().useAdminCommandHandler(var4, "admin_residence " + var19.getId());
                  return false;
               }
            }

            SiegeEvent var23 = var19.getSiegeEvent();
            var23.clearActions();
            var19.getLastSiegeDate().setTimeInMillis(var12 == null ? 0L : System.currentTimeMillis());
            var19.getOwnDate().setTimeInMillis(var12 == null ? 0L : System.currentTimeMillis());
            var19.changeOwner(var12);
            var23.reCalcNextTime(false);
            break;
         case admin_set_siege_time:
            Residence var18 = ResidenceHolder.getInstance().getResidence(Integer.parseInt(var2[1]));
            if (var18 == null) {
               return false;
            }

            Calendar var25 = (Calendar)var18.getSiegeDate().clone();

            for (int var14 = 2; var14 < var2.length; var14++) {
               int var16 = Integer.parseInt(var2[var14]);
               byte var15;
               switch (var14) {
                  case 2:
                     var15 = 11;
                     break;
                  case 3:
                     var15 = 12;
                     break;
                  case 4:
                     var15 = 5;
                     break;
                  case 5:
                     var15 = 2;
                     var16--;
                     break;
                  case 6:
                     var15 = 1;
                     break;
                  default:
                     continue;
               }

               var25.set(var15, var16);
            }

            SiegeEvent var22 = var18.getSiegeEvent();
            var22.clearActions();
            var18.getSiegeDate().setTimeInMillis(var25.getTimeInMillis());
            var22.registerActions();
            var18.setJdbcState(JdbcEntityState.UPDATED);
            var18.update();
            AdminCommandHandler.getInstance().useAdminCommandHandler(var4, "admin_residence " + var18.getId());
            break;
         case admin_quick_siege_start:
            Residence var17 = ResidenceHolder.getInstance().getResidence(Integer.parseInt(var2[1]));
            if (var17 == null) {
               return false;
            }

            Calendar var8 = Calendar.getInstance();
            if (var2.length >= 3) {
               var8.set(13, -Integer.parseInt(var2[2]));
            }

            SiegeEvent var21 = var17.getSiegeEvent();
            var21.clearActions();
            var17.getSiegeDate().setTimeInMillis(var8.getTimeInMillis());
            var21.registerActions();
            var17.setJdbcState(JdbcEntityState.UPDATED);
            var17.update();
            AdminCommandHandler.getInstance().useAdminCommandHandler(var4, "admin_residence " + var17.getId());
            break;
         case admin_quick_siege_stop:
            Residence var6 = ResidenceHolder.getInstance().getResidence(Integer.parseInt(var2[1]));
            if (var6 == null) {
               return false;
            }

            final SiegeEvent var7 = var6.getSiegeEvent();
            var7.clearActions();
            ThreadPoolManager.getInstance().execute(new RunnableImpl() {
               @Override
               public void runImpl() throws Exception {
                  var7.stopEvent();
               }
            });
            AdminCommandHandler.getInstance().useAdminCommandHandler(var4, "admin_residence " + var6.getId());
      }

      return true;
   }

   @Override
   public Enum[] getAdminCommandEnum() {
      return AdminResidence.Commands.values();
   }

   private enum Commands {
      admin_residence_list,
      admin_residence,
      admin_set_owner,
      admin_set_siege_time,
      admin_quick_siege_start,
      admin_quick_siege_stop;
   }
}
