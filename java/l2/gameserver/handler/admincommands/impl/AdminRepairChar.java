package l2.gameserver.handler.admincommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;

public class AdminRepairChar implements IAdminCommandHandler {
   @Override
   public boolean useAdminCommand(Enum var1, String[] var2, String var3, Player var4) {
      AdminRepairChar.Commands var5 = (AdminRepairChar.Commands)var1;
      if (var4.getPlayerAccess() != null && var4.getPlayerAccess().CanEditChar) {
         if (var2.length != 2) {
            return false;
         }

         Connection var6 = null;
         PreparedStatement var7 = null;
         ResultSet var8 = null;

         try {
            var6 = DatabaseFactory.getInstance().getConnection();
            var7 = var6.prepareStatement("UPDATE `characters` SET `x`=-84318, `y`=244579, `z`=-3730 WHERE `char_name`=?");
            var7.setString(1, var2[1]);
            var7.execute();
            DbUtils.close(var7);
            var7 = var6.prepareStatement("SELECT `obj_id` FROM `characters` where `char_name`=?");
            var7.setString(1, var2[1]);
            var8 = var7.executeQuery();
            int var9 = 0;
            if (var8.next()) {
               var9 = var8.getInt(1);
            }

            DbUtils.close(var7, var8);
            if (var9 == 0) {
               return false;
            }

            var7 = var6.prepareStatement("DELETE FROM `character_shortcuts` WHERE `object_id`=?");
            var7.setInt(1, var9);
            var7.execute();
            DbUtils.close(var7);
            var7 = var6.prepareStatement("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`='reflection' LIMIT 1");
            var7.setInt(1, var9);
            var7.execute();
            DbUtils.close(var7);
         } catch (Exception var14) {
         } finally {
            DbUtils.closeQuietly(var6, var7, var8);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public Enum[] getAdminCommandEnum() {
      return AdminRepairChar.Commands.values();
   }

   private enum Commands {
      admin_restore,
      admin_repair;
   }
}
