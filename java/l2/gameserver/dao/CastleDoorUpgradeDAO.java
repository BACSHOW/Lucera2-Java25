package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CastleDoorUpgradeDAO {
   private static final CastleDoorUpgradeDAO uy = new CastleDoorUpgradeDAO();
   private static final Logger _log = LoggerFactory.getLogger(CastleDoorUpgradeDAO.class);
   public static final String SELECT_SQL_QUERY = "SELECT `hp` FROM `castle_door_upgrade` WHERE `door_id`=?";
   public static final String REPLACE_SQL_QUERY = "REPLACE INTO `castle_door_upgrade` (`door_id`, `hp`) VALUES (?,?)";
   public static final String DELETE_SQL_QUERY = "DELETE FROM `castle_door_upgrade` WHERE `door_id`=?";

   public static CastleDoorUpgradeDAO getInstance() {
      return uy;
   }

   public int load(int var1) {
      Connection var2 = null;
      PreparedStatement var3 = null;
      ResultSet var4 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("SELECT `hp` FROM `castle_door_upgrade` WHERE `door_id`=?");
         var3.setInt(1, var1);
         var4 = var3.executeQuery();
         if (var4.next()) {
            return var4.getInt("hp");
         }
      } catch (Exception var9) {
         _log.error("CastleDoorUpgradeDAO:load(int): " + var9, var9);
      } finally {
         DbUtils.closeQuietly(var2, var3, var4);
      }

      return 0;
   }

   public void insert(int var1, int var2) {
      Connection var3 = null;
      PreparedStatement var4 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("REPLACE INTO `castle_door_upgrade` (`door_id`, `hp`) VALUES (?,?)");
         var4.setInt(1, var1);
         var4.setInt(2, var2);
         var4.execute();
      } catch (Exception var9) {
         _log.error("CastleDoorUpgradeDAO:insert(int, int): " + var9, var9);
      } finally {
         DbUtils.closeQuietly(var3, var4);
      }
   }

   public void delete(int var1) {
      Connection var2 = null;
      PreparedStatement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("DELETE FROM `castle_door_upgrade` WHERE `door_id`=?");
         var3.setInt(1, var1);
         var3.execute();
      } catch (Exception var8) {
         _log.error("CastleDoorUpgradeDAO:delete(int): " + var8, var8);
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }
   }
}
