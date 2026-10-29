package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ExMailArrived;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateMail extends Functions implements ICommunityBoardHandler, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(PrivateMail.class);
   private static final int bYz = 10;

   @Override
   public void onLoad() {
      if (Config.COMMUNITYBOARD_ENABLED) {
         _log.info("CommunityBoard: Private Mail service loaded.");
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
      return new String[]{"_maillist_", "_mailsearch_", "_mailread_", "_maildelete_"};
   }

   @Override
   public void onBypassCommand(Player var1, String var2) {
      StringTokenizer var3 = new StringTokenizer(var2, "_");
      String var4 = var3.nextToken();
      var1.setSessionVar("add_fav", null);
      if ("maillist".equals(var4)) {
         int var51 = Integer.parseInt(var3.nextToken());
         int var53 = Integer.parseInt(var3.nextToken());
         int var55 = Integer.parseInt(var3.nextToken());
         String var57 = var3.hasMoreTokens() ? var3.nextToken() : "";
         String var58 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_mail_list.htm", var1);
         int var98 = 0;
         int var99 = 0;
         Connection var100 = null;
         PreparedStatement var110 = null;
         ResultSet var14 = null;

         try {
            var100 = DatabaseFactory.getInstance().getConnection();
            var110 = var100.prepareStatement("SELECT count(*) as cnt FROM `bbs_mail` WHERE `box_type` = 0 and `to_object_id` = ?");
            var110.setInt(1, var1.getObjectId());
            var14 = var110.executeQuery();
            if (var14.next()) {
               var98 = var14.getInt("cnt");
            }

            var110.close();
            var110 = var100.prepareStatement("SELECT count(*) as cnt FROM `bbs_mail` WHERE `box_type` = 1 and `from_object_id` = ?");
            var110.setInt(1, var1.getObjectId());
            var14 = var110.executeQuery();
            if (var14.next()) {
               var99 = var14.getInt("cnt");
            }
         } catch (Exception var46) {
         } finally {
            DbUtils.closeQuietly(var100, var110, var14);
         }

         List var15 = null;
         switch (var51) {
            case 0:
               var58 = var58.replace("%inbox_link%", "[&$917;]");
               var58 = var58.replace("%sentbox_link%", "<a action=\"bypass _maillist_1_1_0_\">[&$918;]</a>");
               var58 = var58.replace("%archive_link%", "<a action=\"bypass _maillist_2_1_0_\">[&$919;]</a>");
               var58 = var58.replace("%temp_archive_link%", "<a action=\"bypass _maillist_3_1_0_\">[&$920;]</a>");
               var58 = var58.replace("%TREE%", "&$917;");
               var58 = var58.replace("%writer_header%", "&$911;");
               var15 = b(var1, var51, var57, var55 == 1);
               break;
            case 1:
               var58 = var58.replace("%inbox_link%", "<a action=\"bypass _maillist_0_1_0_\">[&$917;]</a>");
               var58 = var58.replace("%sentbox_link%", "[&$918;]");
               var58 = var58.replace("%archive_link%", "<a action=\"bypass _maillist_2_1_0_\">[&$919;]</a>");
               var58 = var58.replace("%temp_archive_link%", "<a action=\"bypass _maillist_3_1_0_\">[&$920;]</a>");
               var58 = var58.replace("%TREE%", "&$918;");
               var58 = var58.replace("%writer_header%", "&$909;");
               var15 = b(var1, var51, var57, var55 == 1);
               break;
            case 2:
               var58 = var58.replace("%inbox_link%", "<a action=\"bypass _maillist_0_1_0_\">[&$917;]</a>");
               var58 = var58.replace("%sentbox_link%", "<a action=\"bypass _maillist_1_1_0_\">[&$918;]</a>");
               var58 = var58.replace("%archive_link%", "[&$919;]");
               var58 = var58.replace("%temp_archive_link%", "<a action=\"bypass _maillist_3_1_0_\">[&$920;]</a>");
               var58 = var58.replace("%TREE%", "&$919;");
               var58 = var58.replace("%writer_header%", "&$911;");
               break;
            case 3:
               var58 = var58.replace("%inbox_link%", "<a action=\"bypass _maillist_0_1_0_\">[&$917;]</a>");
               var58 = var58.replace("%sentbox_link%", "<a action=\"bypass _maillist_1_1_0_\">[&$918;]</a>");
               var58 = var58.replace("%archive_link%", "<a action=\"bypass _maillist_2_1_0_\">[&$919;]</a>");
               var58 = var58.replace("%temp_archive_link%", "[&$920;]");
               var58 = var58.replace("%TREE%", "&$920;");
               var58 = var58.replace("%writer_header%", "&$909;");
         }

         if (var15 == null) {
            var58 = var58.replace("%ACTION_GO_LEFT%", "");
            var58 = var58.replace("%GO_LIST%", "");
            var58 = var58.replace("%NPAGE%", "1");
            var58 = var58.replace("%GO_LIST2%", "");
            var58 = var58.replace("%ACTION_GO_RIGHT%", "");
            var58 = var58.replace("%MAIL_LIST%", "");
         } else {
            int var16 = (var53 - 1) * 10;
            int var17 = Math.min(var53 * 10, var15.size());
            if (var53 == 1) {
               var58 = var58.replace("%ACTION_GO_LEFT%", "");
               var58 = var58.replace("%GO_LIST%", "");
               var58 = var58.replace("%NPAGE%", "1");
            } else {
               var58 = var58.replace("%ACTION_GO_LEFT%", "bypass _maillist_" + var51 + "_" + (var53 - 1) + "_" + var55 + "_" + var57);
               var58 = var58.replace("%NPAGE%", String.valueOf(var53));
               StringBuilder var18 = new StringBuilder("");

               for (int var19 = var53 > 10 ? var53 - 10 : 1; var19 < var53; var19++) {
                  var18.append("<td><a action=\"bypass _maillist_")
                     .append(var51)
                     .append("_")
                     .append(var19)
                     .append("_")
                     .append(var55)
                     .append("_")
                     .append(var57)
                     .append("\"> ")
                     .append(var19)
                     .append(" </a> </td>\n\n");
               }

               var58 = var58.replace("%GO_LIST%", var18.toString());
            }

            int var111 = Math.max(var15.size() / 10, 1);
            if (var15.size() > var111 * 10) {
               var111++;
            }

            if (var111 > var53) {
               var58 = var58.replace("%ACTION_GO_RIGHT%", "bypass _maillist_" + var51 + "_" + (var53 + 1) + "_" + var55 + "_" + var57);
               int var112 = Math.min(var53 + 10, var111);
               StringBuilder var20 = new StringBuilder("");

               for (int var21 = var53 + 1; var21 <= var112; var21++) {
                  var20.append("<td><a action=\"bypass _maillist_")
                     .append(var51)
                     .append("_")
                     .append(var21)
                     .append("_")
                     .append(var55)
                     .append("_")
                     .append(var57)
                     .append("\"> ")
                     .append(var21)
                     .append(" </a> </td>\n\n");
               }

               var58 = var58.replace("%GO_LIST2%", var20.toString());
            } else {
               var58 = var58.replace("%ACTION_GO_RIGHT%", "");
               var58 = var58.replace("%GO_LIST2%", "");
            }

            StringBuilder var113 = new StringBuilder("");
            String var114 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_mailtpl.htm", var1);

            for (int var115 = var16; var115 < var17; var115++) {
               PrivateMail.MailData var22 = (PrivateMail.MailData)var15.get(var115);
               String var23 = var114;
               var23 = var23.replace("%action%", "bypass _mailread_" + var22.messageId + "_" + var51 + "_" + var53 + "_" + var55 + "_" + var57);
               var23 = var23.replace("%writer%", var22.author);
               var23 = var23.replace("%title%", var22.title);
               var23 = var23.replace("%post_date%", var22.postDate);
               var113.append(var23);
            }

            var58 = var58.replace("%MAIL_LIST%", var113.toString());
         }

         var58 = var58.replace("%mailbox_type%", String.valueOf(var51));
         var58 = var58.replace("%incomming_mail_no%", String.valueOf(var98));
         var58 = var58.replace("%sent_mail_no%", String.valueOf(var99));
         var58 = var58.replace("%archived_mail_no%", "0");
         var58 = var58.replace("%temp_mail_no%", "0");
         ShowBoard.separateAndSend(var58, var1);
      } else if ("mailread".equals(var4)) {
         int var50 = Integer.parseInt(var3.nextToken());
         int var52 = Integer.parseInt(var3.nextToken());
         int var54 = Integer.parseInt(var3.nextToken());
         int var56 = Integer.parseInt(var3.nextToken());
         String var9 = var3.hasMoreTokens() ? var3.nextToken() : "";
         Connection var10 = null;
         PreparedStatement var11 = null;
         ResultSet var12 = null;

         label443: {
            try {
               var10 = DatabaseFactory.getInstance().getConnection();
               var11 = var10.prepareStatement("SELECT * FROM `bbs_mail` WHERE `message_id` = ? and `box_type` = ? and `to_object_id` = ?");
               var11.setInt(1, var50);
               var11.setInt(2, var52);
               var11.setInt(3, var1.getObjectId());
               var12 = var11.executeQuery();
               if (!var12.next()) {
                  break label443;
               }

               String var13 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_mail_read.htm", var1);
               switch (var52) {
                  case 0:
                     var13 = var13.replace("%TREE%", "<a action=\"bypass _maillist_0_1_0_\">&$917;</a>");
                     break;
                  case 1:
                     var13 = var13.replace("%TREE%", "<a action=\"bypass _maillist_1_1_0__\">&$918;</a>");
                     break;
                  case 2:
                     var13 = var13.replace("%TREE%", "<a action=\"bypass _maillist_2_1_0__\">&$919;</a>");
                     break;
                  case 3:
                     var13 = var13.replace("%TREE%", "<a action=\"bypass _maillist_3_1_0__\">&$920;</a>");
               }

               var13 = var13.replace("%writer%", var12.getString("from_name"));
               var13 = var13.replace("%post_date%", String.format("%1$te-%1$tm-%1$tY", new Date(var12.getInt("post_date") * 1000L)));
               var13 = var13.replace("%del_date%", String.format("%1$te-%1$tm-%1$tY", new Date((var12.getInt("post_date") + 7776000) * 1000L)));
               var13 = var13.replace("%char_name%", var12.getString("to_name"));
               var13 = var13.replace("%title%", var12.getString("title"));
               var13 = var13.replace("%CONTENT%", var12.getString("message").replace("\n", "<br1>"));
               var13 = var13.replace("%GOTO_LIST_LINK%", "bypass _maillist_" + var52 + "_" + var54 + "_" + var56 + "_" + var9);
               var13 = var13.replace("%message_id%", String.valueOf(var50));
               var13 = var13.replace("%mailbox_type%", String.valueOf(var52));
               var1.setSessionVar("add_fav", var2 + "&" + var12.getString("title"));
               var11.close();
               var11 = var10.prepareStatement("UPDATE `bbs_mail` SET `read` = `read` + 1 WHERE message_id = ?");
               var11.setInt(1, var50);
               var11.execute();
               ShowBoard.separateAndSend(var13, var1);
            } catch (Exception var48) {
               var48.printStackTrace();
               break label443;
            } finally {
               DbUtils.closeQuietly(var10, var11, var12);
            }

            return;
         }

         this.onBypassCommand(var1, "_maillist_" + var52 + "_" + var54 + "_" + var56 + "_" + var9);
      } else if ("maildelete".equals(var4)) {
         int var5 = Integer.parseInt(var3.nextToken());
         int var6 = Integer.parseInt(var3.nextToken());
         Connection var7 = null;
         PreparedStatement var8 = null;

         try {
            var7 = DatabaseFactory.getInstance().getConnection();
            var8 = var7.prepareStatement("DELETE FROM `bbs_mail` WHERE `box_type` = ? and `message_id` = ? and `to_object_id` = ?");
            var8.setInt(1, var5);
            var8.setInt(2, var6);
            var8.setInt(3, var1.getObjectId());
            var8.execute();
         } catch (Exception var44) {
         } finally {
            DbUtils.closeQuietly(var7, var8);
         }

         this.onBypassCommand(var1, "_maillist_" + var5 + "_1_0_");
      }
   }

   @Override
   public void onWriteCommand(Player var1, String var2, String var3, String var4, String var5, String var6, String var7) {
      StringTokenizer var8 = new StringTokenizer(var2, "_");
      String var9 = var8.nextToken();
      if ("mailsearch".equals(var9)) {
         this.onBypassCommand(var1, "_maillist_" + var8.nextToken() + "_1_" + ("Title".equals(var5) ? "1_" : "0_") + (var7 != null ? var7 : ""));
      }
   }

   public static void OnPlayerEnter(Player var0) {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT * FROM `bbs_mail` WHERE `box_type` = 0 and `read` = 0 and `to_object_id` = ?");
         var2.setInt(1, var0.getObjectId());
         var3 = var2.executeQuery();
         if (var3.next()) {
            var0.sendPacket(Msg.YOUVE_GOT_MAIL);
            var0.sendPacket(ExMailArrived.STATIC);
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   private static List<PrivateMail.MailData> b(Player var0, int var1, String var2, boolean var3) {
      ArrayList var4 = new ArrayList();
      Connection var5 = null;
      PreparedStatement var6 = null;
      ResultSet var7 = null;

      try {
         var5 = DatabaseFactory.getInstance().getConnection();
         var6 = var5.prepareStatement("DELETE FROM `bbs_mail` WHERE `to_object_id` = ? and `post_date` < ?");
         var6.setInt(1, var0.getObjectId());
         var6.setInt(2, (int)(System.currentTimeMillis() / 1000L) - 7776000);
         var6.execute();
         var6.close();
         String var8 = var1 == 0 ? "from_name" : "to_name";
         var6 = var5.prepareStatement("SELECT * FROM `bbs_mail` WHERE `box_type` = ? and `to_object_id` = ? ORDER BY post_date DESC");
         var6.setInt(1, var1);
         var6.setInt(2, var0.getObjectId());
         var7 = var6.executeQuery();

         while (var7.next()) {
            if (var2.isEmpty()) {
               var4.add(new PrivateMail.MailData(var7.getString(var8), var7.getString("title"), var7.getInt("post_date"), var7.getInt("message_id")));
            } else if (var3 && !var2.isEmpty() && var7.getString("title").toLowerCase().contains(var2.toLowerCase())) {
               var4.add(new PrivateMail.MailData(var7.getString(var8), var7.getString("title"), var7.getInt("post_date"), var7.getInt("message_id")));
            } else if (!var3 && !var2.isEmpty() && var7.getString(var8).toLowerCase().contains(var2.toLowerCase())) {
               var4.add(new PrivateMail.MailData(var7.getString(var8), var7.getString("title"), var7.getInt("post_date"), var7.getInt("message_id")));
            }
         }
      } catch (Exception var12) {
         var12.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var5, var6, var7);
      }

      return var4;
   }

   private static class MailData {
      public String author;
      public String title;
      public String postDate;
      public int messageId;

      public MailData(String var1, String var2, int var3, int var4) {
         this.author = var1;
         this.title = var2;
         this.postDate = String.format(String.format("%1$te-%1$tm-%1$tY", new Date(var3 * 1000L)));
         this.messageId = var4;
      }
   }
}
