package l2.gameserver.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AutoBan {
   private static final Logger _log = LoggerFactory.getLogger(AutoBan.class);

   public static boolean isBanned(int var0) {
      boolean var1 = false;
      Connection var2 = null;
      PreparedStatement var3 = null;
      ResultSet var4 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("SELECT MAX(`endban`) AS `endban` FROM `bans` WHERE `obj_Id`=? AND `endban` IS NOT NULL");
         var3.setInt(1, var0);
         var4 = var3.executeQuery();
         if (var4.next()) {
            Long var5 = var4.getLong("endban") * 1000L;
            var1 = var5 > System.currentTimeMillis();
         }
      } catch (Exception var9) {
         _log.warn("Could not restore ban data: " + var9);
      } finally {
         DbUtils.closeQuietly(var2, var3, var4);
      }

      return var1;
   }

   public static void Banned(Player var0, int var1, String var2, String var3) {
      int var4 = 0;
      if (var1 == -1) {
         var4 = Integer.MAX_VALUE;
      } else {
         if (var1 <= 0) {
            _log.warn("Negative ban period: " + var1);
            return;
         }

         Calendar var5 = Calendar.getInstance();
         var5.add(5, var1);
         var4 = (int)(var5.getTimeInMillis() / 1000L);
      }

      String var16 = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
      String var6 = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date(var4 * 1000L));
      if (var4 * 1000L <= Calendar.getInstance().getTimeInMillis()) {
         _log.warn("Negative ban period | From " + var16 + " to " + var6);
      } else {
         Connection var7 = null;
         PreparedStatement var8 = null;

         try {
            var7 = DatabaseFactory.getInstance().getConnection();
            var8 = var7.prepareStatement("INSERT INTO `bans` (`account_name`, `obj_id`, `baned`, `unban`, `reason`, `GM`, `endban`) VALUES(?,?,?,?,?,?,?)");
            var8.setString(1, var0.getAccountName());
            var8.setInt(2, var0.getObjectId());
            var8.setString(3, var16);
            var8.setString(4, var6);
            var8.setString(5, var2);
            var8.setString(6, var3);
            var8.setLong(7, var4);
            var8.execute();
         } catch (Exception var13) {
            _log.warn("could not store bans data:" + var13);
         } finally {
            DbUtils.closeQuietly(var7, var8);
         }
      }
   }

   public static boolean Banned(String var0, int var1, int var2, String var3, String var4) {
      int var6 = CharacterDAO.getInstance().getObjectIdByName(var0);
      boolean var5 = var6 > 0;
      if (!var5) {
         return false;
      }

      Connection var7 = null;
      PreparedStatement var8 = null;

      try {
         var7 = DatabaseFactory.getInstance().getConnection();
         var8 = var7.prepareStatement("UPDATE `characters` SET `accesslevel`=? WHERE `obj_Id`=?");
         var8.setInt(1, var1);
         var8.setInt(2, var6);
         var8.executeUpdate();
         DbUtils.close(var8);
         if (var1 < 0) {
            int var9 = 0;
            if (var2 == -1) {
               var9 = Integer.MAX_VALUE;
            } else {
               if (var2 <= 0) {
                  _log.warn("Negative ban period: " + var2);
                  return false;
               }

               Calendar var10 = Calendar.getInstance();
               var10.add(5, var2);
               var9 = (int)(var10.getTimeInMillis() / 1000L);
            }

            String var19 = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
            String var11 = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date(var9 * 1000L));
            if (var9 * 1000L <= Calendar.getInstance().getTimeInMillis()) {
               _log.warn("Negative ban period | From " + var19 + " to " + var11);
               return false;
            }

            var8 = var7.prepareStatement("INSERT INTO `bans` (`obj_id`, `baned`, `unban`, `reason`, `GM`, `endban`) VALUES(?,?,?,?,?,?)");
            var8.setInt(1, var6);
            var8.setString(2, var19);
            var8.setString(3, var11);
            var8.setString(4, var3);
            var8.setString(5, var4);
            var8.setLong(6, var9);
            var8.execute();
         } else {
            var8 = var7.prepareStatement("DELETE FROM `bans` WHERE `obj_id`=?");
            var8.setInt(1, var6);
            var8.execute();
         }
      } catch (Exception var16) {
         var16.printStackTrace();
         _log.warn("could not store bans data:" + var16);
         var5 = false;
      } finally {
         DbUtils.closeQuietly(var7, var8);
      }

      return var5;
   }

   public static void Karma(Player var0, int var1, String var2, String var3) {
      Connection var4 = null;
      PreparedStatement var5 = null;

      try {
         String var6 = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
         var2 = "Add karma(" + var1 + ") " + var2;
         var4 = DatabaseFactory.getInstance().getConnection();
         var5 = var4.prepareStatement("INSERT INTO `bans` (`account_name`, `obj_id`, `baned`, `reason`, `GM`) VALUES(?,?,?,?,?)");
         var5.setString(1, var0.getAccountName());
         var5.setInt(2, var0.getObjectId());
         var5.setString(3, var6);
         var5.setString(4, var2);
         var5.setString(5, var3);
         var5.execute();
      } catch (Exception var10) {
         _log.warn("could not store bans data:" + var10);
      } finally {
         DbUtils.closeQuietly(var4, var5);
      }
   }

   public static void Banned(Player var0, int var1, String var2) {
      Banned(var0, var1, var2, "AutoBan");
   }

   public static boolean ChatBan(String var0, int var1, String var2, String var3) {
      boolean var4 = true;
      long var5 = var1 * 60000;
      int var7 = CharacterDAO.getInstance().getObjectIdByName(var0);
      if (var7 == 0) {
         return false;
      }

      Player var8 = World.getPlayer(var0);
      Connection var9 = null;
      PreparedStatement var10 = null;
      if (var8 != null) {
         var8.sendMessage(new CustomMessage("l2p.Util.AutoBan.ChatBan", var8).addString(var3).addNumber(var1));
         var8.updateNoChannel(var5);
      } else {
         try {
            var9 = DatabaseFactory.getInstance().getConnection();
            var10 = var9.prepareStatement("UPDATE `characters` SET `nochannel` = ? WHERE `obj_Id`=?");
            var10.setLong(1, var5 > 0L ? var5 / 1000L : var5);
            var10.setInt(2, var7);
            var10.executeUpdate();
         } catch (Exception var15) {
            var4 = false;
            _log.warn("Could not activate nochannel:" + var15);
         } finally {
            DbUtils.closeQuietly(var9, var10);
         }
      }

      return var4;
   }

   public static boolean noSpam(String var0, int var1) {
      boolean var2 = true;
      int var3 = CharacterDAO.getInstance().getObjectIdByName(var0);
      if (var3 == 0) {
         return false;
      }

      Player var4 = World.getPlayer(var0);
      Connection var5 = null;
      PreparedStatement var6 = null;
      if (var4 != null) {
         var4.setAntiSpam(TimeUnit.MINUTES.toMillis(var1), true);
         var4.sendMessage("You have add no spam chat the player " + var4 + " for an " + var1 + " period.");
      } else {
         try {
            var5 = DatabaseFactory.getInstance().getConnection();
            String var7 = "REPLACE INTO `character_variables` VALUE (?, 'user-var', 'antispam', ?, -1);";
            var6 = var5.prepareStatement(var7);
            var6.setInt(1, var3);
            var6.setLong(2, TimeUnit.MINUTES.toMillis(var1));
            var6.executeUpdate();
         } catch (Exception var11) {
            var2 = false;
            _log.warn("Could not activate nospam:" + var11);
         } finally {
            DbUtils.closeQuietly(var5, var6);
         }
      }

      return var2;
   }

   public static boolean ChatUnBan(String var0, String var1) {
      boolean var2 = true;
      Player var3 = World.getPlayer(var0);
      int var4 = CharacterDAO.getInstance().getObjectIdByName(var0);
      if (var4 == 0) {
         return false;
      }

      Connection var5 = null;
      PreparedStatement var6 = null;
      if (var3 != null) {
         var3.sendMessage(new CustomMessage("l2p.Util.AutoBan.ChatUnBan", var3).addString(var1));
         var3.updateNoChannel(0L);
      } else {
         try {
            var5 = DatabaseFactory.getInstance().getConnection();
            var6 = var5.prepareStatement("UPDATE `characters` SET `nochannel` = ? WHERE `obj_Id`=?");
            var6.setLong(1, 0L);
            var6.setInt(2, var4);
            var6.executeUpdate();
         } catch (Exception var11) {
            var2 = false;
            _log.warn("Could not activate nochannel:" + var11);
         } finally {
            DbUtils.closeQuietly(var5, var6);
         }
      }

      return var2;
   }
}
