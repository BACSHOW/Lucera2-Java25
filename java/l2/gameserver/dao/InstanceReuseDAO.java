package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.model.Player;
import l2.gameserver.templates.InstantZone;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceReuseDAO {
   private static final Logger sQ = LoggerFactory.getLogger(InstanceReuseDAO.class);
   private static final InstanceReuseDAO uS = new InstanceReuseDAO();

   public static InstanceReuseDAO getInstance() {
      return uS;
   }

   private InstanceReuseDAO() {
   }

   public Map<Integer, Long> load(Player var1) {
      return this.load(var1.getObjectId());
   }

   public Map<Integer, Long> load(int var1) {
      HashMap var2 = new HashMap();
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("SELECT * FROM `character_instances` WHERE `obj_id` = ?");
         var4.setInt(1, var1);
         var5 = var4.executeQuery();

         while (var5.next()) {
            int var6 = var5.getInt("id");
            long var7 = var5.getLong("reuse");
            var2.put(var6, var7);
         }
      } catch (SQLException var12) {
         sQ.error(var12.getMessage(), var12);
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }

      return var2;
   }

   public Long getHwidReuse(int var1, String var2) {
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("SELECT `reuse` FROM `instances_hwid_reuse` WHERE `id` = ? AND `hwid` = ?");
         var4.setInt(1, var1);
         var4.setString(2, var2);
         var5 = var4.executeQuery();
         if (var5.next()) {
            return var5.getLong("reuse");
         }
      } catch (SQLException var10) {
         sQ.error(var10.getMessage(), var10);
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }

      return -1L;
   }

   public void setHwidReuse(int var1, String var2, long var3) {
      mysql.set("REPLACE INTO `instances_hwid_reuse` (`id`, `hwid`, `reuse`) VALUES (?,?,?)", var1, var2, var3);
   }

   public void removeReuse(Player var1, int var2) {
      this.removeReuse(var1.getObjectId(), var2);
   }

   public void removeReuse(int var1, int var2) {
      mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=? AND `id`=? LIMIT 1", var1, var2);
   }

   public void removeAllReuse(Player var1) {
      this.removeAllReuse(var1.getObjectId());
   }

   public void removeAllReuse(int var1) {
      mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=?", var1);
   }

   public void setReuse(Player var1, int var2, long var3) {
      this.setReuse(var1.getObjectId(), var2, var3);
      InstantZone var5 = InstantZoneHolder.getInstance().getInstantZone(var2);
      String var6;
      if (var5 != null
         && var5.isHwidLimited()
         && var1.getNetConnection() != null
         && (var6 = var1.getNetConnection().getHwid()) != null
         && !StringUtils.isBlank(var6)) {
         this.setHwidReuse(var2, var6, var3);
      }
   }

   public void setReuse(int var1, int var2, long var3) {
      mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", var1, var2, var3);
   }
}
