package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;

public class AdminRide implements IAdminCommandHandler {
   @Override
   public boolean useAdminCommand(Enum var1, String[] var2, String var3, Player var4) {
      AdminRide.Commands var5 = (AdminRide.Commands)var1;
      Player var6 = var4.getTarget() != null ? var4.getTarget().getPlayer() : (var2.length > 1 ? GameObjectsStorage.getPlayer(var2[1]) : null);
      if (!var4.getPlayerAccess().Rider) {
         return false;
      }

      switch (var5) {
         case admin_ride:
            if (var6.isMounted() || var6.getPet() != null) {
               var4.sendMessage("Already Have a Pet or Mounted.");
               return false;
            }

            if (var2.length != 2) {
               var4.sendMessage("Incorrect id.");
               return false;
            }

            var6.setMount(Integer.parseInt(var2[1]), 0, 85);
            break;
         case admin_ride_wyvern:
         case admin_wr:
            if (var6.isMounted() || var6.getPet() != null) {
               var4.sendMessage("Already Have a Pet or Mounted.");
               return false;
            }

            var6.setMount(12621, 0, 85);
            break;
         case admin_ride_strider:
         case admin_sr:
            if (var6.isMounted() || var6.getPet() != null) {
               var4.sendMessage("Already Have a Pet or Mounted.");
               return false;
            }

            var6.setMount(12526, 0, 85);
            break;
         case admin_unride:
         case admin_ur:
            var6.setMount(0, 0, 0);
      }

      return true;
   }

   @Override
   public Enum[] getAdminCommandEnum() {
      return AdminRide.Commands.values();
   }

   private enum Commands {
      admin_ride,
      admin_ride_wyvern,
      admin_ride_strider,
      admin_unride,
      admin_wr,
      admin_sr,
      admin_ur;
   }
}
