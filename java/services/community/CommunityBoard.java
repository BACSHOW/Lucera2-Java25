package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.handler.items.RefineryHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.PackageToList;
import l2.gameserver.network.l2.s2c.SellRefundList;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.utils.WarehouseFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.community.custom.ACbConfigManager;

public class CommunityBoard implements ICommunityBoardHandler, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(CommunityBoard.class);

   @Override
   public void onLoad() {
      if (Config.COMMUNITYBOARD_ENABLED) {
         _log.info("CommunityBoard: service loaded.");
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
         "_bbshome",
         "_bbslink",
         "_bbsmultisell",
         "_bbspage",
         "_bbsscripts",
         "_bbsselllist",
         "_bbssaugmentation",
         "_bbssaugmentcancel",
         "_bbsscargodeposit",
         "_bbsscargowithdraw",
         "_bbs_lang_ru",
         "_bbs_lang_en"
      };
   }

   @Override
   public void onBypassCommand(Player var1, String var2) {
      StringTokenizer var3 = new StringTokenizer(var2, "_");
      String var4 = var3.nextToken();
      String var5 = "";
      if ("bbshome".equals(var4)) {
         StringTokenizer var6 = new StringTokenizer(Config.BBS_DEFAULT, "_");
         String var7 = var6.nextToken();
         if (!var7.equals(var4)) {
            this.onBypassCommand(var1, Config.BBS_DEFAULT);
            return;
         }

         var5 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_top.htm", var1);
         int var8 = 0;
         Connection var9 = null;
         PreparedStatement var10 = null;
         ResultSet var11 = null;

         try {
            var9 = DatabaseFactory.getInstance().getConnection();
            var10 = var9.prepareStatement("SELECT count(*) as cnt FROM `bbs_favorites` WHERE `object_id` = ?");
            var10.setInt(1, var1.getObjectId());
            var11 = var10.executeQuery();
            if (var11.next()) {
               var8 = var11.getInt("cnt");
            }
         } catch (Exception var16) {
         } finally {
            DbUtils.closeQuietly(var9, var10, var11);
         }

         var5 = var5.replace("<?fav_count?>", String.valueOf(var8));
         var5 = var5.replace("<?clan_count?>", String.valueOf(ClanTable.getInstance().getClans().length));
         var5 = var5.replace("<?market_count?>", String.valueOf(CommunityBoardManager.getInstance().getIntProperty("col_count")));
      } else if ("bbslink".equals(var4)) {
         var5 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_homepage.htm", var1);
      } else if (var2.startsWith("_bbspage")) {
         String[] var22 = var2.split(":");
         String var25 = var22[1];
         var5 = HtmCache.getInstance().getNotNull("scripts/services/community/pages/" + var25 + ".htm", var1);
      } else {
         if (var2.startsWith("_bbsmultisell")) {
            StringTokenizer var24 = new StringTokenizer(var2, ";");
            String[] var27 = var24.nextToken().split(":");
            String var29 = var24.hasMoreTokens() ? var24.nextToken() : null;
            if (var29 != null) {
               ICommunityBoardHandler var32 = CommunityBoardManager.getInstance().getCommunityHandler(var29, var1);
               if (var32 != null) {
                  var32.onBypassCommand(var1, var29);
               }
            }

            int var33 = Integer.parseInt(var27[1]);
            MultiSellHolder.getInstance().SeparateAndSend(var33, var1, 0.0);
            return;
         }

         if (var2.equalsIgnoreCase("_bbsselllist")) {
            var1.sendPacket(new SellRefundList(var1, false));
            return;
         }

         if (var2.equalsIgnoreCase("_bbs_lang_ru")) {
            var1.setVar("lang@", "ru", -1L);
            var1.sendMessage(new CustomMessage("usercommandhandlers.LangRu", var1));
            var5 = HtmCache.getInstance().getNotNull("scripts/services/community/pages/index.htm", var1);
         } else if (var2.equalsIgnoreCase("_bbs_lang_en")) {
            var1.setVar("lang@", "en", -1L);
            var1.sendMessage(new CustomMessage("usercommandhandlers.LangEn", var1));
            var5 = HtmCache.getInstance().getNotNull("scripts/services/community/pages/index.htm", var1);
         } else {
            if (var2.equalsIgnoreCase("_bbssaugmentation") && ACbConfigManager.ALT_CB_ALLOW_AUGMENT_BBS) {
               var1.setRefineryHandler(RefineryHandler.getInstance());
               RefineryHandler.getInstance().onInitRefinery(var1);
               return;
            }

            if (var2.equalsIgnoreCase("_bbssaugmentcancel") && ACbConfigManager.ALT_CB_ALLOW_AUGMENT_CANCEL_BBS) {
               var1.setRefineryHandler(RefineryHandler.getInstance());
               RefineryHandler.getInstance().onInitRefineryCancel(var1);
               return;
            }

            if (var2.equalsIgnoreCase("_bbsscargodeposit")) {
               var1.sendPacket(new PackageToList(var1));
               return;
            }

            if (var2.equalsIgnoreCase("_bbsscargowithdraw")) {
               WarehouseFunctions.showFreightWindow(var1);
               return;
            }

            if (var2.startsWith("_bbsscripts")) {
               StringTokenizer var23 = new StringTokenizer(var2, ";");
               String var26 = var23.nextToken().substring(12);
               String var28 = var23.hasMoreTokens() ? var23.nextToken() : null;
               if (var28 != null) {
                  ICommunityBoardHandler var30 = CommunityBoardManager.getInstance().getCommunityHandler(var28, var1);
                  if (var30 != null) {
                     var30.onBypassCommand(var1, var28);
                  }
               }

               String[] var31 = var26.split("\\s+");
               String[] var34 = var26.substring(var31[0].length()).trim().split("\\s+");
               String[] var35 = var31[0].split(":");
               if (var35.length != 2) {
                  return;
               }

               Scripts.getInstance().callScripts(var1, var35[0], var35[1], var31.length == 1 ? new Object[0] : new Object[]{var34});
               return;
            }
         }
      }

      var5 = var5.replace("%online%", String.valueOf(Math.round(GameObjectsStorage.getAllPlayersCount() * ACbConfigManager.ALT_CB_ONLINE_MULTIPLIER)));
      ShowBoard.separateAndSend(var5, var1);
   }

   @Override
   public void onWriteCommand(Player var1, String var2, String var3, String var4, String var5, String var6, String var7) {
   }
}
