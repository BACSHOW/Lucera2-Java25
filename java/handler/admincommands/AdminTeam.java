package handler.admincommands;

import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.components.SystemMsg;

public class AdminTeam extends ScriptAdminCommand {
   @Override
   public boolean useAdminCommand(Enum var1, String[] var2, String var3, Player var4) {
      TeamType var5 = TeamType.NONE;
      if (var2.length >= 2) {
         for (TeamType var9 : TeamType.values()) {
            if (var2[1].equalsIgnoreCase(var9.name())) {
               var5 = var9;
            }
         }
      }

      GameObject var10 = var4.getTarget();
      if (var10 != null && var10.isCreature()) {
         ((Creature)var10).setTeam(var5);
         return true;
      } else {
         var4.sendPacket(SystemMsg.INVALID_TARGET);
         return false;
      }
   }

   @Override
   public Enum[] getAdminCommandEnum() {
      return AdminTeam.Commands.values();
   }

   enum Commands {
      admin_setteam;
   }
}
