package l2.gameserver.handler.voicecommands.impl;

import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.database.mysql;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;

public class CWHPrivileges implements IVoicedCommandHandler {
   private String[] zF = new String[]{"clan"};

   @Override
   public String[] getVoicedCommandList() {
      return this.zF;
   }

   @Override
   public boolean useVoicedCommand(String var1, Player var2, String var3) {
      if (var2.getClan() == null) {
         return false;
      }

      if (!var1.equals("clan") || !Config.ALT_ALLOW_CLAN_COMMAND_ALLOW_WH) {
         return false;
      }

      if (Config.ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER && !var2.isClanLeader()) {
         return false;
      }

      if ((var2.getClanPrivileges() & 16) != 16) {
         return false;
      }

      if (var3 != null) {
         String[] var4 = var3.split(" ");
         if (var4.length > 0) {
            if (var4[0].equalsIgnoreCase("allowwh") && var4.length > 1) {
               UnitMember var14 = var2.getClan().getAnyMember(var4[1]);
               if (var14 != null && var14.getPlayer() != null) {
                  if (var14.getPlayer().getVarB("canWhWithdraw")) {
                     var14.getPlayer().unsetVar("canWhWithdraw");
                     var2.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PrivilegeRemoved", var2));
                  } else {
                     var14.getPlayer().setVar("canWhWithdraw", "1", -1L);
                     var2.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PrivilegeGiven", var2));
                  }
               } else if (var14 != null) {
                  int var15 = mysql.simple_get_int("value", "character_variables", "obj_id=" + var14.getObjectId() + " AND name LIKE 'canWhWithdraw'");
                  if (var15 > 0) {
                     mysql.set("DELETE FROM `character_variables` WHERE obj_id=" + var14.getObjectId() + " AND name LIKE 'canWhWithdraw' LIMIT 1");
                     var2.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PrivilegeRemoved", var2));
                  } else {
                     mysql.set(
                        "INSERT INTO character_variables  (obj_id, type, name, value, expire_time) VALUES ("
                           + var14.getObjectId()
                           + ",'user-var','canWhWithdraw','1',-1)"
                     );
                     var2.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PrivilegeGiven", var2));
                  }
               } else {
                  var2.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PlayerNotFound", var2));
               }
            } else if (var4[0].equalsIgnoreCase("list")) {
               StringBuilder var5 = new StringBuilder("SELECT `obj_id` FROM `character_variables` WHERE `obj_id` IN (");
               List var6 = var2.getClan().getAllMembers();

               for (int var7 = 0; var7 < var6.size(); var7++) {
                  var5.append(((UnitMember)var6.get(var7)).getObjectId());
                  if (var7 < var6.size() - 1) {
                     var5.append(",");
                  }
               }

               var5.append(") AND `name`='canWhWithdraw'");
               List var16 = mysql.get_array(var5.toString());
               var5 = new StringBuilder("<html><body>Clan member Warehouse privilege<br><br><table>");

               for (Object var9 : var16) {
                  for (Object __raw_var11_84 : (Iterable<?>)(Object)(var6)) {
                     UnitMember var11 = (UnitMember)__raw_var11_84;
                     if (var11.getObjectId() == Integer.parseInt(var9.toString())) {
                        var5.append("<tr><td width=10></td><td width=60>")
                           .append(var11.getName())
                           .append("</td><td width=20><button width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h user_clan allowwh ")
                           .append(var11.getName())
                           .append("\" value=\"Remove\">")
                           .append("<br></td></tr>");
                     }
                  }
               }

               var5.append(
                  "<tr><td width=10></td><td width=20><button width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h user_clan\" value=\"Back\"></td></tr></table></body></html>"
               );
               Functions.show(var5.toString(), var2, null);
               return true;
            }
         }
      }

      String var12 = HtmCache.getInstance().getNotNull("scripts/services/clan.htm", var2);
      Functions.show(var12, var2, null);
      return true;
   }
}
