package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.s2c.ExMailArrived;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.utils.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClanCommunity extends Functions implements ICommunityBoardHandler, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(ClanCommunity.class);
   private static final int bYw = 10;
   private final ClanCommunity.Listener bYx = new ClanCommunity.Listener();

   @Override
   public void onLoad() {
      CharListenerList.addGlobal(this.bYx);
      if (Config.COMMUNITYBOARD_ENABLED) {
         _log.info("CommunityBoard: Clan Community service loaded.");
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
      return new String[]{
         "_bbsclan",
         "_clbbsclan_",
         "_clbbslist_",
         "_clsearch",
         "_clbbsadmi",
         "_mailwritepledgeform",
         "_announcepledgewriteform",
         "_announcepledgeswitchshowflag",
         "_announcepledgewrite",
         "_clwriteintro",
         "_clwritemail"
      };
   }

   @Override
   public void onBypassCommand(Player var1, String var2) {
      StringTokenizer var3 = new StringTokenizer(var2, "_");
      String var4 = var3.nextToken();
      var1.setSessionVar("add_fav", null);
      if ("bbsclan".equals(var4)) {
         Clan var5 = var1.getClan();
         if (var5 != null && var5.getLevel() > 1) {
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         this.onBypassCommand(var1, "_clbbslist_1_0_");
      } else if ("clbbslist".equals(var4)) {
         int var64 = Integer.parseInt(var3.nextToken());
         int var6 = Integer.parseInt(var3.nextToken());
         String var7 = var3.hasMoreTokens() ? var3.nextToken() : "";
         HashMap var8 = l2.gameserver.utils.Util.parseTemplate(HtmCache.getInstance().getNotNull("scripts/services/community/bbs_clanlist.htm", var1));
         String var9 = (String)var8.get(0);
         Clan var10 = var1.getClan();
         if (var10 != null) {
            String var11 = (String)var8.get(1);
            var11 = var11.replace("%PLEDGE_ID%", String.valueOf(var10.getClanId()));
            var11 = var11.replace("%MY_PLEDGE_NAME%", var10.getLevel() > 1 ? var10.getName() : "");
            var9 = var9.replace("<?my_clan_link?>", var11);
         } else {
            var9 = var9.replace("<?my_clan_link?>", "");
         }

         List var114 = d(var7, var6 == 1);
         int var12 = (var64 - 1) * 10;
         int var13 = Math.min(var64 * 10, var114.size());
         if (var64 == 1) {
            var9 = var9.replace("%ACTION_GO_LEFT%", "");
            var9 = var9.replace("%GO_LIST%", "");
            var9 = var9.replace("%NPAGE%", "1");
         } else {
            var9 = var9.replace("%ACTION_GO_LEFT%", "bypass _clbbslist_" + (var64 - 1) + "_" + var6 + "_" + var7);
            var9 = var9.replace("%NPAGE%", String.valueOf(var64));
            StringBuilder var14 = new StringBuilder("");

            for (int var15 = var64 > 10 ? var64 - 10 : 1; var15 < var64; var15++) {
               var14.append("<td><a action=\"bypass _clbbslist_")
                  .append(var15)
                  .append("_")
                  .append(var6)
                  .append("_")
                  .append(var7)
                  .append("\"> ")
                  .append(var15)
                  .append(" </a> </td>\n\n");
            }

            var9 = var9.replace("%GO_LIST%", var14.toString());
         }

         int var133 = Math.max(var114.size() / 10, 1);
         if (var114.size() > var133 * 10) {
            var133++;
         }

         if (var133 <= var64) {
            var9 = var9.replace("%ACTION_GO_RIGHT%", "");
            var9 = var9.replace("%GO_LIST2%", "");
         } else {
            var9 = var9.replace("%ACTION_GO_RIGHT%", "bypass _clbbslist_" + (var64 + 1) + "_" + var6 + "_" + var7);
            int var134 = Math.min(var64 + 10, var133);
            StringBuilder var16 = new StringBuilder("");

            for (int var17 = var64 + 1; var17 <= var134; var17++) {
               var16.append("<td><a action=\"bypass _clbbslist_")
                  .append(var17)
                  .append("_")
                  .append(var6)
                  .append("_")
                  .append(var7)
                  .append("\"> ")
                  .append(var17)
                  .append(" </a> </td>\n\n");
            }

            var9 = var9.replace("%GO_LIST2%", var16.toString());
         }

         StringBuilder var135 = new StringBuilder("");
         String var136 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_clantpl.htm", var1);

         for (int var137 = var12; var137 < var13; var137++) {
            Clan var18 = (Clan)var114.get(var137);
            String var19 = var136;
            var19 = var19.replace("%action_clanhome%", "bypass _clbbsclan_" + var18.getClanId());
            var19 = var19.replace("%clan_name%", var18.getName());
            var19 = var19.replace("%clan_owner%", var18.getLeaderName());
            var19 = var19.replace("%skill_level%", String.valueOf(var18.getLevel()));
            var19 = var19.replace("%member_count%", String.valueOf(var18.getAllSize()));
            var135.append(var19);
         }

         var9 = var9.replace("%CLAN_LIST%", var135.toString());
         ShowBoard.separateAndSend(var9, var1);
      } else if ("clbbsclan".equals(var4)) {
         int var65 = Integer.parseInt(var3.nextToken());
         if (var65 == 0) {
            var1.sendPacket(new SystemMessage(238));
            this.onBypassCommand(var1, "_clbbslist_1_0");
            return;
         }

         Clan var70 = ClanTable.getInstance().getClan(var65);
         if (var70 == null) {
            this.onBypassCommand(var1, "_clbbslist_1_0");
            return;
         }

         if (var70.getLevel() < 2) {
            var1.sendPacket(new SystemMessage(1050));
            this.onBypassCommand(var1, "_clbbslist_1_0");
            return;
         }

         Connection var83 = null;
         PreparedStatement var92 = null;
         ResultSet var106 = null;
         String var109 = "";

         try {
            var83 = DatabaseFactory.getInstance().getConnection();
            var92 = var83.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type = 2");
            var92.setInt(1, var65);
            var106 = var92.executeQuery();
            if (var106.next()) {
               var109 = var106.getString("notice");
            }
         } catch (Exception var62) {
         } finally {
            DbUtils.closeQuietly(var83, var92, var106);
         }

         HashMap var115 = l2.gameserver.utils.Util.parseTemplate(HtmCache.getInstance().getNotNull("scripts/services/community/bbs_clan.htm", var1));
         String var118 = (String)var115.get(0);
         var118 = var118.replace("%PLEDGE_ID%", String.valueOf(var65));
         var118 = var118.replace("%ACTION_ANN%", "");
         var118 = var118.replace("%ACTION_FREE%", "");
         if (var1.getClanId() == var65 && var1.isClanLeader()) {
            var118 = var118.replace("<?menu?>", (CharSequence)var115.get(1));
         } else {
            var118 = var118.replace("<?menu?>", "");
         }

         var118 = var118.replace("%CLAN_INTRO%", var109.replace("\n", "<br1>"));
         var118 = var118.replace("%CLAN_NAME%", var70.getName());
         var118 = var118.replace("%SKILL_LEVEL%", String.valueOf(var70.getLevel()));
         var118 = var118.replace("%CLAN_MEMBERS%", String.valueOf(var70.getAllSize()));
         var118 = var118.replace("%OWNER_NAME%", var70.getLeaderName());
         var118 = var118.replace("%ALLIANCE_NAME%", var70.getAlliance() != null ? var70.getAlliance().getAllyName() : "");
         var118 = var118.replace("%ANN_LIST%", "");
         var118 = var118.replace("%THREAD_LIST%", "");
         ShowBoard.separateAndSend(var118, var1);
      } else if ("clbbsadmi".equals(var4)) {
         Clan var66 = var1.getClan();
         if (var66 == null || var66.getLevel() < 2 || !var1.isClanLeader()) {
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         String var71 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_clanadmin.htm", var1);
         var71 = var71.replace("%PLEDGE_ID%", String.valueOf(var66.getClanId()));
         var71 = var71.replace("%ACTION_ANN%", "");
         var71 = var71.replace("%ACTION_FREE%", "");
         var71 = var71.replace("%CLAN_NAME%", var66.getName());
         var71 = var71.replace("%per_list%", "");
         Connection var84 = null;
         PreparedStatement var93 = null;
         ResultSet var107 = null;
         String var110 = "";

         try {
            var84 = DatabaseFactory.getInstance().getConnection();
            var93 = var84.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type = 2");
            var93.setInt(1, var66.getClanId());
            var107 = var93.executeQuery();
            if (var107.next()) {
               var110 = var107.getString("notice");
            }
         } catch (Exception var60) {
         } finally {
            DbUtils.closeQuietly(var84, var93, var107);
         }

         ArrayList var116 = new ArrayList();
         var116.add("0");
         var116.add("0");
         var116.add("0");
         var116.add("0");
         var116.add("0");
         var116.add("0");
         var116.add("");
         var116.add("0");
         var116.add("");
         var116.add("0");
         var116.add("");
         var116.add("");
         var116.add(var110);
         var116.add("");
         var116.add("");
         var116.add("0");
         var116.add("0");
         var116.add("");
         var1.sendPacket(new ShowBoard(var71, "1001", var1));
         var1.sendPacket(new ShowBoard(var116));
      } else if ("mailwritepledgeform".equals(var4)) {
         Clan var67 = var1.getClan();
         if (var67 == null || var67.getLevel() < 2 || !var1.isClanLeader()) {
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         String var77 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_pledge_mail_write.htm", var1);
         var77 = var77.replace("%PLEDGE_ID%", String.valueOf(var67.getClanId()));
         var77 = var77.replace("%pledge_id%", String.valueOf(var67.getClanId()));
         var77 = var77.replace("%pledge_name%", var67.getName());
         ShowBoard.separateAndSend(var77, var1);
      } else if ("announcepledgewriteform".equals(var4)) {
         Clan var68 = var1.getClan();
         if (var68 == null || var68.getLevel() < 2 || !var1.isClanLeader()) {
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         HashMap var81 = l2.gameserver.utils.Util.parseTemplate(HtmCache.getInstance().getNotNull("scripts/services/community/bbs_clanannounce.htm", var1));
         String var85 = (String)var81.get(0);
         var85 = var85.replace("%PLEDGE_ID%", String.valueOf(var68.getClanId()));
         var85 = var85.replace("%ACTION_ANN%", "");
         var85 = var85.replace("%ACTION_FREE%", "");
         Connection var94 = null;
         PreparedStatement var108 = null;
         ResultSet var111 = null;
         String var117 = "";
         int var131 = 0;

         try {
            var94 = DatabaseFactory.getInstance().getConnection();
            var108 = var94.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type != 2");
            var108.setInt(1, var68.getClanId());
            var111 = var108.executeQuery();
            if (var111.next()) {
               var117 = var111.getString("notice");
               var131 = var111.getInt("type");
            }
         } catch (Exception var58) {
         } finally {
            DbUtils.closeQuietly(var94, var108, var111);
         }

         if (var131 == 0) {
            var85 = var85.replace("<?usage?>", (CharSequence)var81.get(1));
         } else {
            var85 = var85.replace("<?usage?>", (CharSequence)var81.get(2));
         }

         var85 = var85.replace("%flag%", String.valueOf(var131));
         ArrayList var132 = new ArrayList();
         var132.add("0");
         var132.add("0");
         var132.add("0");
         var132.add("0");
         var132.add("0");
         var132.add("0");
         var132.add("");
         var132.add("0");
         var132.add("");
         var132.add("0");
         var132.add("");
         var132.add("");
         var132.add(var117);
         var132.add("");
         var132.add("");
         var132.add("0");
         var132.add("0");
         var132.add("");
         var1.sendPacket(new ShowBoard(var85, "1001", var1));
         var1.sendPacket(new ShowBoard(var132));
      } else if ("announcepledgeswitchshowflag".equals(var4)) {
         Clan var69 = var1.getClan();
         if (var69 == null || var69.getLevel() < 2 || !var1.isClanLeader()) {
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         int var82 = Integer.parseInt(var3.nextToken());
         Connection var91 = null;
         PreparedStatement var95 = null;

         try {
            var91 = DatabaseFactory.getInstance().getConnection();
            var95 = var91.prepareStatement("UPDATE `bbs_clannotice` SET type = ? WHERE `clan_id` = ? and type = ?");
            var95.setInt(1, var82);
            var95.setInt(2, var69.getClanId());
            var95.setInt(3, var82 == 1 ? 0 : 1);
            var95.execute();
         } catch (Exception var56) {
         } finally {
            DbUtils.closeQuietly(var91, var95);
         }

         var69.setNotice(var82 == 0 ? "" : null);
         this.onBypassCommand(var1, "_announcepledgewriteform");
      }
   }

   @Override
   public void onWriteCommand(Player var1, String var2, String var3, String var4, String var5, String var6, String var7) {
      StringTokenizer var8 = new StringTokenizer(var2, "_");
      String var9 = var8.nextToken();
      if ("clsearch".equals(var9)) {
         if (var5 == null) {
            var5 = "";
         }

         this.onBypassCommand(var1, "_clbbslist_1_" + ("Ruler".equals(var6) ? "1" : "0") + "_" + var5);
      } else if ("clwriteintro".equals(var9)) {
         Clan var58 = var1.getClan();
         if (var58 == null || var58.getLevel() < 2 || !var1.isClanLeader() || var5 == null || var5.isEmpty()) {
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         var5 = HtmlUtils.sanitizeHtml(var5, 3000);
         Connection var60 = null;
         PreparedStatement var62 = null;

         try {
            var60 = DatabaseFactory.getInstance().getConnection();
            var62 = var60.prepareStatement("REPLACE INTO `bbs_clannotice`(clan_id, type, notice) VALUES(?, ?, ?)");
            var62.setInt(1, var58.getClanId());
            var62.setInt(2, 2);
            var62.setString(3, var5);
            var62.execute();
         } catch (Exception var35) {
         } finally {
            DbUtils.closeQuietly(var60, var62);
         }

         this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
      } else if ("clwritemail".equals(var9)) {
         Clan var57 = var1.getClan();
         if (var57 == null || var57.getLevel() < 2 || !var1.isClanLeader()) {
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         if (var5 == null || var6 == null) {
            var1.sendPacket(Msg.THE_MESSAGE_WAS_NOT_SENT);
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         var5 = var5.replace("<", "");
         var5 = var5.replace(">", "");
         var5 = var5.replace("&", "");
         var5 = var5.replace("$", "");
         var5 = var5.replace("\"", "&quot;");
         var7 = var7.replace("<", "");
         var7 = var7.replace(">", "");
         var7 = var7.replace("&", "");
         var7 = var7.replace("$", "");
         var7 = var7.replace("\"", "&quot;");
         if (var5.isEmpty() || var6.isEmpty()) {
            var1.sendPacket(Msg.THE_MESSAGE_WAS_NOT_SENT);
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         if (var5.length() > 128) {
            var5 = var5.substring(0, 128);
         }

         if (var6.length() > 3000) {
            var7 = var7.substring(0, 3000);
         }

         Connection var59 = null;
         PreparedStatement var61 = null;

         label502: {
            try {
               var59 = DatabaseFactory.getInstance().getConnection();
               var61 = var59.prepareStatement(
                  "INSERT INTO `bbs_mail`(to_name, to_object_id, from_name, from_object_id, title, message, post_date, box_type) VALUES(?, ?, ?, ?, ?, ?, ?, 0)"
               );

               for (UnitMember var14 : var57) {
                  var61.setString(1, var57.getName());
                  var61.setInt(2, var14.getObjectId());
                  var61.setString(3, var1.getName());
                  var61.setInt(4, var1.getObjectId());
                  var61.setString(5, var5);
                  var61.setString(6, var7);
                  var61.setInt(7, (int)(System.currentTimeMillis() / 1000L));
                  var61.execute();
               }

               var61.close();
               var61 = var59.prepareStatement(
                  "INSERT INTO `bbs_mail`(to_name, to_object_id, from_name, from_object_id, title, message, post_date, box_type) VALUES(?, ?, ?, ?, ?, ?, ?, 1)"
               );
               var61.setString(1, var57.getName());
               var61.setInt(2, var1.getObjectId());
               var61.setString(3, var1.getName());
               var61.setInt(4, var1.getObjectId());
               var61.setString(5, var5);
               var61.setString(6, var7);
               var61.setInt(7, (int)(System.currentTimeMillis() / 1000L));
               var61.execute();
               break label502;
            } catch (Exception var37) {
               var1.sendPacket(Msg.THE_MESSAGE_WAS_NOT_SENT);
               this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            } finally {
               DbUtils.closeQuietly(var59, var61);
            }

            return;
         }

         var1.sendPacket(Msg.YOUVE_SENT_MAIL);

         for (Player var65 : var57.getOnlineMembers(0)) {
            var65.sendPacket(Msg.YOUVE_GOT_MAIL);
            var65.sendPacket(ExMailArrived.STATIC);
         }

         this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
      } else if ("announcepledgewrite".equals(var9)) {
         Clan var10 = var1.getClan();
         if (var10 == null || var10.getLevel() < 2 || !var1.isClanLeader()) {
            this.onBypassCommand(var1, "_clbbsclan_" + var1.getClanId());
            return;
         }

         if (var5 == null || var5.isEmpty()) {
            this.onBypassCommand(var1, "_announcepledgewriteform");
            return;
         }

         var5 = var5.replace("<", "");
         var5 = var5.replace(">", "");
         var5 = var5.replace("&", "");
         var5 = var5.replace("$", "");
         var5 = var5.replace("\"", "&quot;");
         if (var5.isEmpty()) {
            this.onBypassCommand(var1, "_announcepledgewriteform");
            return;
         }

         if (var5.length() > 3000) {
            var5 = var5.substring(0, 3000);
         }

         int var11 = Integer.parseInt(var8.nextToken());
         Connection var12 = null;
         PreparedStatement var13 = null;

         label512: {
            try {
               var12 = DatabaseFactory.getInstance().getConnection();
               var13 = var12.prepareStatement("REPLACE INTO `bbs_clannotice`(clan_id, type, notice) VALUES(?, ?, ?)");
               var13.setInt(1, var10.getClanId());
               var13.setInt(2, var11);
               var13.setString(3, var5);
               var13.execute();
               break label512;
            } catch (Exception var39) {
               var39.printStackTrace();
               this.onBypassCommand(var1, "_announcepledgewriteform");
            } finally {
               DbUtils.closeQuietly(var12, var13);
            }

            return;
         }

         if (var11 == 1) {
            var10.setNotice(var5.replace("\n", "<br1>"));
         } else {
            var10.setNotice("");
         }

         var1.sendPacket(Msg.NOTICE_HAS_BEEN_SAVED);
         this.onBypassCommand(var1, "_announcepledgewriteform");
      }
   }

   private static List<Clan> d(String var0, boolean var1) {
      ArrayList var2 = new ArrayList();
      Clan[] var3 = ClanTable.getInstance().getClans();
      Arrays.sort(var3, new ClanCommunity.ClansComparator<>());

      for (Clan var7 : var3) {
         if (var7.getLevel() > 1) {
            var2.add(var7);
         }
      }

      if (!var0.isEmpty()) {
         ArrayList var8 = new ArrayList();

         for (Object __raw_var10_584 : (Iterable<?>)(Object)(var2)) {
            Clan var10 = (Clan)__raw_var10_584;
            if (var1 && var10.getLeaderName().toLowerCase().contains(var0.toLowerCase())) {
               var8.add(var10);
            } else if (!var1 && var10.getName().toLowerCase().contains(var0.toLowerCase())) {
               var8.add(var10);
            }
         }

         var2 = var8;
      }

      return var2;
   }

   private static class ClansComparator<T> implements Comparator<T> {
      private ClansComparator() {
      }

      @Override
      public int compare(Object var1, Object var2) {
         if (var1 instanceof Clan && var2 instanceof Clan) {
            Clan var3 = (Clan)var1;
            Clan var4 = (Clan)var2;
            return var3.getName().compareTo(var4.getName());
         } else {
            return 0;
         }
      }
   }

   private class Listener implements OnPlayerEnterListener {
      private Listener() {
      }

      @Override
      public void onPlayerEnter(Player var1) {
         Clan var2 = var1.getClan();
         if (var2 != null && var2.getLevel() >= 2) {
            if (var2.getNotice() == null) {
               Connection var3 = null;
               PreparedStatement var4 = null;
               ResultSet var5 = null;
               String var6 = "";
               int var7 = 0;

               try {
                  var3 = DatabaseFactory.getInstance().getConnection();
                  var4 = var3.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type != 2");
                  var4.setInt(1, var2.getClanId());
                  var5 = var4.executeQuery();
                  if (var5.next()) {
                     var6 = var5.getString("notice");
                     var7 = var5.getInt("type");
                  }
               } catch (Exception var12) {
               } finally {
                  DbUtils.closeQuietly(var3, var4, var5);
               }

               var2.setNotice(var7 == 1 ? var6.replace("\n", "<br1>\n") : "");
            }

            if (!var2.getNotice().isEmpty()) {
               String var14 = HtmCache.getInstance().getNotNull("scripts/services/community/clan_popup.htm", var1);
               var14 = var14.replace("%pledge_name%", var2.getName());
               var14 = var14.replace("%content%", var2.getNotice());
               var1.sendPacket(new NpcHtmlMessage(0).setHtml(var14));
            }
         }
      }
   }
}
