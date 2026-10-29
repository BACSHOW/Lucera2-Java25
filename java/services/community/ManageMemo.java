package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageMemo implements ICommunityBoardHandler, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(ManageMemo.class);
   private static final int bYy = 12;

   private static String d(Player var0, int var1, int var2) {
      StringBuilder var3 = new StringBuilder("");
      Connection var4 = null;
      PreparedStatement var5 = null;
      ResultSet var6 = null;

      try {
         if (var2 > 0) {
            int var7 = (var1 - 1) * 12;
            int var8 = var1 * 12;
            var4 = DatabaseFactory.getInstance().getConnection();
            var5 = var4.prepareStatement(
               "SELECT memo_id,title,post_date FROM `bbs_memo` WHERE `account_name` = ? ORDER BY post_date DESC LIMIT " + var7 + "," + var8
            );
            var5.setString(1, var0.getAccountName());
            var6 = var5.executeQuery();
            String var9 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_memo_post.htm", var0);

            while (var6.next()) {
               String var10 = var9;
               var10 = var10.replace("%memo_id%", String.valueOf(var6.getInt("memo_id")));
               var10 = var10.replace("%memo_title%", L(var6.getString("title")));
               var10 = var10.replace("%page%", String.valueOf(var1));
               var10 = var10.replace("%memo_date%", String.format("%1$te-%1$tm-%1$tY", new Date(var6.getInt("post_date") * 1000L)));
               var3.append(var10);
            }
         }
      } catch (Exception var14) {
         var14.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var4, var5, var6);
      }

      return var3.toString();
   }

   private static int aI(Player var0) {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;
      int var4 = 0;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT count(*) as cnt FROM bbs_memo WHERE `account_name` = ?");
         var2.setString(1, var0.getAccountName());
         var3 = var2.executeQuery();
         if (var3.next()) {
            var4 = var3.getInt("cnt");
         }
      } catch (Exception var9) {
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }

      return var4;
   }

   private static String L(String var0) {
      return StringUtils.replaceEach(
         StringUtils.defaultString(var0), new String[]{"<", ">", "&", "$", "\""}, new String[]{"&$740;", "&$740;", "&$740;", "&$740;", "&$740;"}
      );
   }

   @Override
   public void onLoad() {
      if (Config.COMMUNITYBOARD_ENABLED) {
         _log.info("CommunityBoard: Manage Memo service loaded.");
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
      return new String[]{"_bbsmemo", "_mmread_", "_mmlist_", "_mmcrea", "_mmwrite", "_mmmodi_", "_mmdele"};
   }

   @Override
   public void onBypassCommand(Player var1, String var2) {
      StringTokenizer var3 = new StringTokenizer(var2, "_");
      String var4 = var3.nextToken();
      var1.setSessionVar("add_fav", null);
      String var5 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_memo_list.htm", var1);
      if ("bbsmemo".equals(var4) || "_mmlist_1".equals(var2)) {
         int var59 = aI(var1);
         var5 = var5.replace("%memo_list%", d(var1, 1, var59));
         var5 = var5.replace("%prev_page%", "");
         var5 = var5.replace("%page%", "1");
         String var64 = "<td>1</td>\n\n";
         if (var59 > 12) {
            int var68 = var59 / 12;
            if (var59 % 12 != 0) {
               var68++;
            }

            var5 = var5.replace("%next_page%", "bypass _mmlist_2");

            for (int var71 = 2; var71 <= var68; var71++) {
               var64 = var64 + "<td><a action=\"bypass _mmlist_" + var71 + "\"> " + var71 + " </a></td>\n\n";
            }
         } else {
            var5 = var5.replace("%next_page%", "");
         }

         var5 = var5.replace("%pages%", var64);
      } else if ("mmlist".equals(var4)) {
         int var6 = Integer.parseInt(var3.nextToken());
         int var7 = aI(var1);
         var5 = var5.replace("%memo_list%", d(var1, var6, var7));
         var5 = var5.replace("%prev_page%", "bypass _mmlist_" + (var6 - 1));
         var5 = var5.replace("%page%", String.valueOf(var6));
         String var8 = "";
         int var9 = var7 / 12;
         if (var7 % 12 != 0) {
            var9++;
         }

         if (var7 > var6 * 12) {
            var5 = var5.replace("%next_page%", "bypass _mmlist_" + (var6 + 1));
         } else {
            var5 = var5.replace("%next_page%", "");
         }

         for (int var10 = 1; var10 <= var9; var10++) {
            if (var10 == var6) {
               var8 = var8 + "<td>" + var10 + "</td>\n\n";
            } else {
               var8 = var8 + "<td height=15><a action=\"bypass _mmlist_" + var10 + "\"> " + var10 + " </a></td>\n\n";
            }
         }

         var5 = var5.replace("%pages%", var8);
      } else {
         if ("mmcrea".equals(var4)) {
            if (aI(var1) >= 100) {
               var1.sendPacket(new SystemMessage(1206));
               this.onBypassCommand(var1, "_mmlist_1");
               return;
            }

            String var58 = var3.nextToken();
            var5 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_memo_edit.htm", var1);
            var5 = var5.replace("%page%", var58);
            var5 = var5.replace("%memo_id%", "0");
            if (var1.isLangRus()) {
               var5 = var5.replace("%TREE%", "&nbsp;>&nbsp;Создание записки");
            } else {
               var5 = var5.replace("%TREE%", "&nbsp;>&nbsp;Create a Note");
            }

            var1.sendPacket(new ShowBoard(var5, "1001", var1));
            ArrayList var63 = new ArrayList();
            var63.add("0");
            var63.add("0");
            var63.add("0");
            var63.add("0");
            var63.add("0");
            var63.add("0");
            var63.add("");
            var63.add("0");
            var63.add("");
            var63.add("0");
            var63.add("");
            var63.add("");
            var63.add("");
            var63.add("1970-01-01 00:00:00 ");
            var63.add("1970-01-01 00:00:00 ");
            var63.add("0");
            var63.add("0");
            var63.add("");
            var1.sendPacket(new ShowBoard(var63));
            return;
         }

         if ("mmread".equals(var4)) {
            int var57 = Integer.parseInt(var3.nextToken());
            String var62 = var3.nextToken();
            Connection var67 = null;
            PreparedStatement var70 = null;
            ResultSet var73 = null;

            label368: {
               try {
                  var67 = DatabaseFactory.getInstance().getConnection();
                  var70 = var67.prepareStatement("SELECT * FROM `bbs_memo` WHERE `account_name` = ? and memo_id = ?");
                  var70.setString(1, var1.getAccountName());
                  var70.setInt(2, var57);
                  var73 = var70.executeQuery();
                  if (!var73.next()) {
                     break label368;
                  }

                  String var74 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_memo_read.htm", var1);
                  var74 = var74.replace("%title%", L(var73.getString("title")));
                  var74 = var74.replace("%char_name%", var73.getString("char_name"));
                  var74 = var74.replace("%post_date%", String.format("%1$tY-%1$tm-%1$te %1$tH:%1tM:%1$tS", new Date(var73.getInt("post_date") * 1000L)));
                  var74 = var74.replace("%memo%", L(var73.getString("memo")).replace("\n", "<br1>"));
                  var74 = var74.replace("%page%", var62);
                  var74 = var74.replace("%memo_id%", String.valueOf(var57));
                  ShowBoard.separateAndSend(var74, var1);
               } catch (Exception var35) {
                  break label368;
               } finally {
                  DbUtils.closeQuietly(var67, var70, var73);
               }

               return;
            }

            this.onBypassCommand(var1, "_bbsmemo");
            return;
         }

         if ("mmdele".equals(var4)) {
            int var56 = Integer.parseInt(var3.nextToken());
            Connection var61 = null;
            PreparedStatement var66 = null;

            try {
               var61 = DatabaseFactory.getInstance().getConnection();
               var66 = var61.prepareStatement("DELETE FROM `bbs_memo` WHERE `account_name` = ? and memo_id = ?");
               var66.setString(1, var1.getAccountName());
               var66.setInt(2, var56);
               var66.execute();
            } catch (Exception var33) {
            } finally {
               DbUtils.closeQuietly(var61, var66);
            }

            this.onBypassCommand(var1, "_mmlist_1");
            return;
         }

         if ("mmmodi".equals(var4)) {
            int var55 = Integer.parseInt(var3.nextToken());
            String var60 = var3.nextToken();
            Connection var65 = null;
            PreparedStatement var69 = null;
            ResultSet var72 = null;

            label377: {
               try {
                  var65 = DatabaseFactory.getInstance().getConnection();
                  var69 = var65.prepareStatement("SELECT * FROM `bbs_memo` WHERE `account_name` = ? and memo_id = ?");
                  var69.setString(1, var1.getAccountName());
                  var69.setInt(2, var55);
                  var72 = var69.executeQuery();
                  if (!var72.next()) {
                     break label377;
                  }

                  var5 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_memo_edit.htm", var1);
                  var5 = var5.replace("%page%", var60);
                  var5 = var5.replace("%memo_id%", String.valueOf(var55));
                  if (var1.isLangRus()) {
                     var5 = var5.replace(
                        "%TREE%",
                        "&nbsp;>&nbsp;<a action=\"bypass _mmread_"
                           + var55
                           + "_"
                           + var60
                           + "\">Записка: "
                           + L(var72.getString("title"))
                           + "</a>&nbsp;>&nbsp;Редактирование"
                     );
                  } else {
                     var5 = var5.replace(
                        "%TREE%",
                        "&nbsp;>&nbsp;<a action=\"bypass _mmread_"
                           + var55
                           + "_"
                           + var60
                           + "\">Note: "
                           + L(var72.getString("title"))
                           + "</a>&nbsp;>&nbsp;Editing"
                     );
                  }

                  var1.sendPacket(new ShowBoard(var5, "1001", var1));
                  ArrayList var11 = new ArrayList();
                  var11.add("0");
                  var11.add("0");
                  var11.add(String.valueOf(var55));
                  var11.add("0");
                  var11.add("0");
                  var11.add("0");
                  var11.add(var1.getName());
                  var11.add("0");
                  var11.add(var1.getAccountName());
                  var11.add("0");
                  var11.add(L(var72.getString("title")));
                  var11.add(L(var72.getString("title")));
                  var11.add(L(var72.getString("memo")));
                  var11.add(String.format("%1$tY-%1$tm-%1$te %1$tH:%1tM:%1$tS", new Date(var72.getInt("post_date") * 1000L)));
                  var11.add(String.format("%1$tY-%1$tm-%1$te %1$tH:%1tM:%1$tS", new Date(var72.getInt("post_date") * 1000L)));
                  var11.add("0");
                  var11.add("0");
                  var11.add("");
                  var1.sendPacket(new ShowBoard(var11));
               } catch (Exception var37) {
                  break label377;
               } finally {
                  DbUtils.closeQuietly(var65, var69, var72);
               }

               return;
            }

            this.onBypassCommand(var1, "_mmlist_" + var60);
            return;
         }
      }

      ShowBoard.separateAndSend(var5, var1);
   }

   @Override
   public void onWriteCommand(Player var1, String var2, String var3, String var4, String var5, String var6, String var7) {
      StringTokenizer var8 = new StringTokenizer(var2, "_");
      String var9 = var8.nextToken();
      if ("mmwrite".equals(var9)) {
         if (aI(var1) >= 100) {
            var1.sendPacket(new SystemMessage(1206));
            this.onBypassCommand(var1, "_mmlist_1");
            return;
         }

         if (var5 != null && !var5.isEmpty() && var6 != null && !var6.isEmpty()) {
            String var10 = L(var5);
            if (var10.length() > 128) {
               var10 = var10.substring(0, 128);
            }

            String var11 = L(var6);
            if (var11.length() > 1000) {
               var11 = var11.substring(0, 1000);
            }

            int var12 = 0;
            if (var4 != null && !var4.isEmpty()) {
               var12 = Integer.parseInt(var4);
            }

            if (var10.length() > 0 && var11.length() > 0) {
               Connection var13 = null;
               PreparedStatement var14 = null;

               try {
                  var13 = DatabaseFactory.getInstance().getConnection();
                  if (var12 > 0) {
                     var14 = var13.prepareStatement("UPDATE bbs_memo SET title = ?, memo = ? WHERE memo_id = ? AND account_name = ?");
                     var14.setString(1, var10);
                     var14.setString(2, var11);
                     var14.setInt(3, var12);
                     var14.setString(4, var1.getAccountName());
                     var14.execute();
                  } else {
                     var14 = var13.prepareStatement("INSERT INTO bbs_memo(account_name, char_name, ip, title, memo, post_date) VALUES(?, ?, ?, ?, ?, ?)");
                     var14.setString(1, var1.getAccountName());
                     var14.setString(2, var1.getName());
                     var14.setString(3, var1.getNetConnection().getIpAddr());
                     var14.setString(4, var10);
                     var14.setString(5, var11);
                     var14.setInt(6, (int)(System.currentTimeMillis() / 1000L));
                     var14.execute();
                  }
               } catch (Exception var19) {
               } finally {
                  DbUtils.closeQuietly(var13, var14);
               }
            }
         }
      }

      this.onBypassCommand(var1, "_bbsmemo");
   }
}
