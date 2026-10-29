package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.StringTokenizer;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageFavorites implements ICommunityBoardHandler, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(ManageFavorites.class);

   @Override
   public void onLoad() {
      if (Config.COMMUNITYBOARD_ENABLED) {
         _log.info("CommunityBoard: Manage Favorites service loaded.");
         CommunityBoardManager.getInstance().registerHandler(this);
      }
   }

   @Override
   public void onReload() {
      if (Config.COMMUNITYBOARD_ENABLED) {
         CommunityBoardManager.getInstance().removeHandler(this);
      }
   }

   @Override
   public void onShutdown() {
   }

   @Override
   public String[] getBypassCommands() {
      return new String[]{"_bbsgetfav", "_bbsaddfav_List", "_bbsdelfav_"};
   }

   @Override
   public void onBypassCommand(Player var1, String var2) {
      StringTokenizer var3 = new StringTokenizer(var2, "_");
      String var4 = var3.nextToken();
      if ("bbsgetfav".equals(var4)) {
         Connection var5 = null;
         PreparedStatement var6 = null;
         ResultSet var7 = null;
         StringBuilder var8 = new StringBuilder("");

         try {
            var5 = DatabaseFactory.getInstance().getConnection();
            var6 = var5.prepareStatement("SELECT * FROM `bbs_favorites` WHERE `object_id` = ? ORDER BY `add_date` DESC");
            var6.setInt(1, var1.getObjectId());
            var7 = var6.executeQuery();
            String var9 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_favoritetpl.htm", var1);

            while (var7.next()) {
               String var10 = var9.replace("%fav_title%", var7.getString("fav_title"));
               var10 = var10.replace("%fav_bypass%", var7.getString("fav_bypass"));
               var10 = var10.replace("%add_date%", String.format("%1$te.%1$tm.%1$tY %1$tH:%1tM", new Date(var7.getInt("add_date") * 1000L)));
               var10 = var10.replace("%fav_id%", String.valueOf(var7.getInt("fav_id")));
               var8.append(var10);
            }
         } catch (Exception var36) {
         } finally {
            DbUtils.closeQuietly(var5, var6, var7);
         }

         String var45 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_getfavorite.htm", var1);
         var45 = var45.replace("%FAV_LIST%", var8.toString());
         ShowBoard.separateAndSend(var45, var1);
      } else if ("bbsaddfav".equals(var4)) {
         String var38 = var1.getSessionVar("add_fav");
         var1.setSessionVar("add_fav", null);
         if (var38 != null) {
            String[] var40 = var38.split("&");
            if (var40.length > 1) {
               Connection var42 = null;
               PreparedStatement var44 = null;

               try {
                  var42 = DatabaseFactory.getInstance().getConnection();
                  var44 = var42.prepareStatement("REPLACE INTO `bbs_favorites`(`object_id`, `fav_bypass`, `fav_title`, `add_date`) VALUES(?, ?, ?, ?)");
                  var44.setInt(1, var1.getObjectId());
                  var44.setString(2, var40[0]);
                  var44.setString(3, var40[1]);
                  var44.setInt(4, (int)(System.currentTimeMillis() / 1000L));
                  var44.execute();
               } catch (Exception var34) {
                  var34.printStackTrace();
               } finally {
                  DbUtils.closeQuietly(var42, var44);
               }
            }
         }

         this.onBypassCommand(var1, "_bbsgetfav");
      } else if ("bbsdelfav".equals(var4)) {
         int var39 = Integer.parseInt(var3.nextToken());
         Connection var41 = null;
         PreparedStatement var43 = null;

         try {
            var41 = DatabaseFactory.getInstance().getConnection();
            var43 = var41.prepareStatement("DELETE FROM `bbs_favorites` WHERE `fav_id` = ? and `object_id` = ?");
            var43.setInt(1, var39);
            var43.setInt(2, var1.getObjectId());
            var43.execute();
         } catch (Exception var32) {
         } finally {
            DbUtils.closeQuietly(var41, var43);
         }

         this.onBypassCommand(var1, "_bbsgetfav");
      }
   }

   @Override
   public void onWriteCommand(Player var1, String var2, String var3, String var4, String var5, String var6, String var7) {
   }
}
