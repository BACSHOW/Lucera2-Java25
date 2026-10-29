package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.utils.SqlBatch;
import org.napile.primitive.maps.IntObjectMap.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterGroupReuseDAO {
   private static final Logger _log = LoggerFactory.getLogger(CharacterGroupReuseDAO.class);
   private static CharacterGroupReuseDAO uF = new CharacterGroupReuseDAO();
   public static final String DELETE_SQL_QUERY = "DELETE FROM `character_group_reuse` WHERE `object_id`=?";
   public static final String SELECT_SQL_QUERY = "SELECT * FROM `character_group_reuse` WHERE `object_id`=?";
   public static final String INSERT_SQL_QUERY = "REPLACE INTO `character_group_reuse` (`object_id`,`reuse_group`,`item_id`,`end_time`,`reuse`) VALUES";

   public static CharacterGroupReuseDAO getInstance() {
      return uF;
   }

   public void select(Player var1) {
      long var2 = System.currentTimeMillis();
      Connection var4 = null;
      PreparedStatement var5 = null;
      ResultSet var6 = null;

      try {
         var4 = DatabaseFactory.getInstance().getConnection();
         var5 = var4.prepareStatement("SELECT * FROM `character_group_reuse` WHERE `object_id`=?");
         var5.setInt(1, var1.getObjectId());
         var6 = var5.executeQuery();

         while (var6.next()) {
            int var7 = var6.getInt("reuse_group");
            int var8 = var6.getInt("item_id");
            long var9 = var6.getLong("end_time");
            long var11 = var6.getLong("reuse");
            if (var9 - var2 > 500L) {
               TimeStamp var13 = new TimeStamp(var8, var9, var11);
               var1.addSharedGroupReuse(var7, var13);
            }
         }

         DbUtils.close(var5);
         var5 = var4.prepareStatement("DELETE FROM `character_group_reuse` WHERE `object_id`=?");
         var5.setInt(1, var1.getObjectId());
         var5.execute();
      } catch (Exception var17) {
         _log.error("CharacterGroupReuseDAO.select(L2Player):", var17);
      } finally {
         DbUtils.closeQuietly(var4, var5, var6);
      }
   }

   public void insert(Player var1) {
      Connection var2 = null;
      PreparedStatement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("DELETE FROM `character_group_reuse` WHERE `object_id`=?");
         var3.setInt(1, var1.getObjectId());
         var3.execute();
         Collection<Entry<TimeStamp>> var4 = var1.getSharedGroupReuses();
         if (!var4.isEmpty()) {
            SqlBatch var5 = new SqlBatch("REPLACE INTO `character_group_reuse` (`object_id`,`reuse_group`,`item_id`,`end_time`,`reuse`) VALUES");
            synchronized (var4) {
               for (Entry<TimeStamp> var8 : var4) {
                  int var9 = var8.getKey();
                  TimeStamp var10 = var8.getValue();
                  if (var10.hasNotPassed()) {
                     StringBuilder var11 = new StringBuilder("(");
                     var11.append(var1.getObjectId()).append(",");
                     var11.append(var9).append(",");
                     var11.append(var10.getId()).append(",");
                     var11.append(var10.getEndTime()).append(",");
                     var11.append(var10.getReuseBasic()).append(")");
                     var5.write(var11.toString());
                  }
               }
            }

            if (!var5.isEmpty()) {
               var3.executeUpdate(var5.close());
            }

            return;
         }
      } catch (Exception var18) {
         _log.error("CharacterGroupReuseDAO.insert(L2Player):", var18);
         return;
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }
   }
}
