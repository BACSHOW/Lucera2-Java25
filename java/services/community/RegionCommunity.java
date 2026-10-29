package services.community;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemTemplate.Grade;
import l2.gameserver.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionCommunity implements ICommunityBoardHandler, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(RegionCommunity.class);
   private static final RegionCommunity.TownEntry[] bYA = new RegionCommunity.TownEntry[]{
      new RegionCommunity.TownEntry("Gatekeeper.TheTownofGludio", 19, 21),
      new RegionCommunity.TownEntry("Gatekeeper.TheTownofDion", 20, 22),
      new RegionCommunity.TownEntry("Gatekeeper.TheTownofGiran", 22, 22),
      new RegionCommunity.TownEntry("Gatekeeper.TheTownofOren", 22, 19),
      new RegionCommunity.TownEntry("Gatekeeper.TheTownofAden", 24, 18),
      new RegionCommunity.TownEntry("Gatekeeper.Heine", 23, 24),
      new RegionCommunity.TownEntry("Gatekeeper.TheTownofGoddard", 24, 16),
      new RegionCommunity.TownEntry("Gatekeeper.RuneTownship", 21, 16),
      new RegionCommunity.TownEntry("Gatekeeper.TheTownofSchuttgart", 22, 13)
   };
   private static final String[] bYB = new String[]{"&$596;", "&$597;", "&$665;"};
   private static final String[] bYC = new String[]{"&$1622;", "&$1623;", "&$1624;", "&$1625;", "&$1626;", "&$1627;"};
   private static final String[] bYD = new String[]{"&$1291;", "&$1292;", "&$1293;", "&$1294;", "&$1295;", "S80 Grade", "S84 Grade"};
   private static final int bYE = 12;

   @Override
   public void onLoad() {
      if (Config.COMMUNITYBOARD_ENABLED) {
         _log.info("CommunityBoard: Region service loaded.");
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
      return new String[]{"_bbsloc", "_bbsregion_", "_bbsreglist_", "_bbsregsearch", "_bbsregview_", "_bbsregtarget_"};
   }

   @Override
   public void onBypassCommand(Player var1, String var2) {
      StringTokenizer var3 = new StringTokenizer(var2, "_");
      String var4 = var3.nextToken();
      var1.setSessionVar("add_fav", null);
      if ("bbsloc".equals(var4)) {
         String var27 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_regiontpl.htm", var1);
         StringBuilder var31 = new StringBuilder("");

         for (int var34 = 0; var34 < bYA.length; var34++) {
            RegionCommunity.TownEntry var38 = bYA[var34];
            String var45 = var27.replace("%region_bypass%", "_bbsregion_" + String.valueOf(var34));
            var45 = var45.replace("%region_name%", StringHolder.getInstance().getNotNull(var1, var38.getTownNameAddr()));
            var45 = var45.replace("%region_desc%", "&$498;: &$1157;, &$1434;, &$645;.");
            var45 = var45.replace("%region_type%", "l2ui.bbs_folder");
            int var59 = 0;
            int var62 = var38.getX();
            int var65 = var38.getY();
            byte var72 = 0;

            for (Player var90 : GameObjectsStorage.getAllPlayersForIterate()) {
               int var104 = MapUtils.regionX(var90);
               int var109 = MapUtils.regionY(var90);
               if (var104 >= var62 - var72
                  && var104 <= var62 + var72
                  && var109 >= var65 - var72
                  && var109 <= var65 + var72
                  && var90.getPrivateStoreType() > 0
                  && var90.getPrivateStoreType() != 7) {
                  var59++;
               }
            }

            var45 = var45.replace("%sellers_count%", String.valueOf(var59));
            var31.append(var45);
         }

         HashMap var35 = l2.gameserver.utils.Util.parseTemplate(HtmCache.getInstance().getNotNull("scripts/services/community/bbs_region_list.htm", var1));
         String var39 = (String)var35.get(0);
         var39 = var39.replace("%REGION_LIST%", var31.toString());
         var39 = var39.replace("<?tree_menu?>", (CharSequence)var35.get(1));
         ShowBoard.separateAndSend(var39, var1);
      } else if ("bbsregion".equals(var4)) {
         String var26 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_regiontpl.htm", var1);
         int var30 = Integer.parseInt(var3.nextToken());
         StringBuilder var33 = new StringBuilder("");
         RegionCommunity.TownEntry var37 = bYA[var30];
         var1.setSessionVar("add_fav", var2 + "&Region " + var30);

         for (int var43 = 0; var43 < bYB.length; var43++) {
            String var51 = var26.replace("%region_bypass%", "_bbsreglist_" + var30 + "_" + var43 + "_1_0_");
            var51 = var51.replace("%region_name%", bYB[var43]);
            var51 = var51.replace("%region_desc%", bYB[var43] + ".");
            var51 = var51.replace("%region_type%", "l2ui.bbs_board");
            int var61 = 0;
            int var64 = var37.getX();
            int var71 = var37.getY();
            byte var85 = 0;

            for (Player var103 : GameObjectsStorage.getAllPlayersForIterate()) {
               int var108 = MapUtils.regionX(var103);
               int var113 = MapUtils.regionY(var103);
               if (var108 >= var64 - var85 && var108 <= var64 + var85 && var113 >= var71 - var85 && var113 <= var71 + var85) {
                  if (var43 != 0 || var103.getPrivateStoreType() != 1 && var103.getPrivateStoreType() != 8) {
                     if (var43 == 1 && var103.getPrivateStoreType() == 3) {
                        var61++;
                     } else if (var43 == 2 && var103.getPrivateStoreType() == 5) {
                        var61++;
                     }
                  } else {
                     var61++;
                  }
               }
            }

            var51 = var51.replace("%sellers_count%", String.valueOf(var61));
            var33.append(var51);
         }

         HashMap var44 = l2.gameserver.utils.Util.parseTemplate(HtmCache.getInstance().getNotNull("scripts/services/community/bbs_region_list.htm", var1));
         String var56 = (String)var44.get(0);
         var56 = var56.replace("%REGION_LIST%", var33.toString());
         var56 = var56.replace(
            "<?tree_menu?>", ((String)var44.get(2)).replace("%TREE%", "&nbsp;>&nbsp;" + StringHolder.getInstance().getNotNull(var1, var37.getTownNameAddr()))
         );
         ShowBoard.separateAndSend(var56, var1);
      } else if ("bbsreglist".equals(var4)) {
         int var25 = Integer.parseInt(var3.nextToken());
         int var29 = Integer.parseInt(var3.nextToken());
         int var32 = Integer.parseInt(var3.nextToken());
         int var36 = Integer.parseInt(var3.nextToken());
         String var42 = var3.hasMoreTokens() ? var3.nextToken().toLowerCase() : "";
         RegionCommunity.TownEntry var50 = bYA[var25];
         var1.setSessionVar("add_fav", var2 + "&Region " + var25 + " " + bYB[var29]);
         List var60 = c(var25, var29, var42, var36 == 1);
         int var63 = (var32 - 1) * 12;
         int var70 = Math.min(var32 * 12, var60.size());
         String var73 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_region_sellers.htm", var1);
         if (var32 == 1) {
            var73 = var73.replace("%ACTION_GO_LEFT%", "");
            var73 = var73.replace("%GO_LIST%", "");
            var73 = var73.replace("%NPAGE%", "1");
         } else {
            var73 = var73.replace("%ACTION_GO_LEFT%", "bypass _bbsreglist_" + var25 + "_" + var29 + "_" + (var32 - 1) + "_" + var36 + "_" + var42);
            var73 = var73.replace("%NPAGE%", String.valueOf(var32));
            StringBuilder var87 = new StringBuilder("");

            for (int var100 = var32 > 10 ? var32 - 10 : 1; var100 < var32; var100++) {
               var87.append("<td><a action=\"bypass _bbsreglist_")
                  .append(var25)
                  .append("_")
                  .append(var29)
                  .append("_")
                  .append(var100)
                  .append("_")
                  .append(var36)
                  .append("_")
                  .append(var42)
                  .append("\"> ")
                  .append(var100)
                  .append(" </a> </td>\n\n");
            }

            var73 = var73.replace("%GO_LIST%", var87.toString());
         }

         int var88 = Math.max(var60.size() / 12, 1);
         if (var60.size() > var88 * 12) {
            var88++;
         }

         if (var88 <= var32) {
            var73 = var73.replace("%ACTION_GO_RIGHT%", "");
            var73 = var73.replace("%GO_LIST2%", "");
         } else {
            var73 = var73.replace("%ACTION_GO_RIGHT%", "bypass _bbsreglist_" + var25 + "_" + var29 + "_" + (var32 + 1) + "_" + var36 + "_" + var42);
            int var101 = Math.min(var32 + 10, var88);
            StringBuilder var106 = new StringBuilder("");

            for (int var111 = var32 + 1; var111 <= var101; var111++) {
               var106.append("<td><a action=\"bypass _bbsreglist_")
                  .append(var25)
                  .append("_")
                  .append(var29)
                  .append("_")
                  .append(var111)
                  .append("_")
                  .append(var36)
                  .append("_")
                  .append(var42)
                  .append("\"> ")
                  .append(var111)
                  .append(" </a> </td>\n\n");
            }

            var73 = var73.replace("%GO_LIST2%", var106.toString());
         }

         StringBuilder var102 = new StringBuilder("");
         String var107 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_region_stpl.htm", var1);

         for (int var112 = var63; var112 < var70; var112++) {
            Player var115 = (Player)var60.get(var112);
            List var117 = var115.getTradeList();
            List var127 = var115.getCreateList();
            if (var117 != null || var127 != null) {
               String var130 = var107;
               var130 = var130.replace(
                  "%view_bypass%", "bypass _bbsregview_" + var25 + "_" + var29 + "_" + var32 + "_" + var115.getObjectId() + "_" + var36 + "_" + var42
               );
               var130 = var130.replace("%seller_name%", var115.getName());
               String var23 = "-";
               if (var29 == 0) {
                  var23 = var117 != null && var115.getSellStoreName() != null && !var115.getSellStoreName().isEmpty() ? var115.getSellStoreName() : "-";
               } else if (var29 == 1) {
                  var23 = var117 != null && var115.getBuyStoreName() != null && !var115.getBuyStoreName().isEmpty() ? var115.getBuyStoreName() : "-";
               } else if (var29 == 2 && var115.getPrivateStoreType() == 5) {
                  var23 = var127 != null && var115.getManufactureName() != null && !var115.getManufactureName().isEmpty() ? var115.getManufactureName() : "-";
               }

               var23 = var23.replace("<", "");
               var23 = var23.replace(">", "");
               var23 = var23.replace("&", "");
               var23 = var23.replace("$", "");
               if (var23.isEmpty()) {
                  var23 = "-";
               }

               var130 = var130.replace("%seller_title%", var23);
               var102.append(var130);
            }
         }

         var73 = var73.replace("%SELLER_LIST%", var102.toString());
         var73 = var73.replace("%search_bypass%", "_bbsregsearch_" + var25 + "_" + var29);
         var73 = var73.replace(
            "%TREE%",
            "&nbsp;>&nbsp;<a action=\"bypass _bbsregion_"
               + var25
               + "\">"
               + StringHolder.getInstance().getNotNull(var1, var50.getTownNameAddr())
               + "</a>&nbsp;>&nbsp;"
               + bYB[var29]
         );
         ShowBoard.separateAndSend(var73, var1);
      } else if ("bbsregview".equals(var4)) {
         int var24 = Integer.parseInt(var3.nextToken());
         int var28 = Integer.parseInt(var3.nextToken());
         int var7 = Integer.parseInt(var3.nextToken());
         int var8 = Integer.parseInt(var3.nextToken());
         int var9 = Integer.parseInt(var3.nextToken());
         String var10 = var3.hasMoreTokens() ? var3.nextToken().toLowerCase() : "";
         RegionCommunity.TownEntry var11 = bYA[var24];
         Player var12 = World.getPlayer(var8);
         if (var12 == null || var12.getPrivateStoreType() == 0) {
            this.onBypassCommand(var1, "_bbsreglist_" + var24 + "_" + var28 + "_" + var7 + "_" + var9 + "_" + var10);
            return;
         }

         String var13 = "-";
         String var14 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_region_storetpl.htm", var1);
         StringBuilder var15 = new StringBuilder("");
         if (var28 < 2) {
            List var16 = var28 == 0 ? var12.getSellList() : var12.getBuyList();
            List var17 = var12.getTradeList();
            if (var16 == null || var16.isEmpty() || var17 == null) {
               this.onBypassCommand(var1, "_bbsreglist_" + var24 + "_" + var28 + "_" + var7 + "_" + var9 + "_" + var10);
               return;
            }

            if (var28 == 0 && var12.getSellStoreName() != null && !var12.getSellStoreName().isEmpty()) {
               var13 = var12.getSellStoreName();
            } else if (var28 == 1 && var12.getBuyStoreName() != null && !var12.getBuyStoreName().isEmpty()) {
               var13 = var12.getBuyStoreName();
            }

            for (Object __raw_var19_309 : (Iterable<?>)(Object)(var16)) {
               TradeItem var19 = (TradeItem)__raw_var19_309;
               ItemTemplate var20 = ItemHolder.getInstance().getTemplate(var19.getItemId());
               if (var20 != null) {
                  String var21 = var14.replace(
                     "%item_name%", var20.getName() + (var20.isEquipment() && var19.getEnchantLevel() > 0 ? " +" + var19.getEnchantLevel() : "")
                  );
                  var21 = var21.replace("%item_img%", var20.getIcon());
                  var21 = var21.replace("%item_count%", String.valueOf(var19.getCount()));
                  var21 = var21.replace("%item_price%", String.format("%,3d", var19.getOwnersPrice()).replace(" ", ","));
                  String var22 = "";
                  if (var20.getCrystalType() != Grade.NONE) {
                     var22 = bYD[var20.getCrystalType().ordinal() - 1];
                     var22 = var22
                        + (var20.getCrystalCount() > 0 ? (var1.isLangRus() ? " Кристаллов: " : " Crystals: ") + var20.getCrystalCount() + ";&nbsp;" : ";&nbsp;");
                  }

                  if (var20.isEquipment()) {
                     if (var19.getAttackElement() >= 0 && var19.getAttackElementValue() > 0) {
                        var22 = var22 + "&$1620;: " + bYC[var19.getAttackElement()] + " +" + var19.getAttackElementValue();
                     } else if (var19.getDefenceFire() > 0
                        || var19.getDefenceWater() > 0
                        || var19.getDefenceWind() > 0
                        || var19.getDefenceEarth() > 0
                        || var19.getDefenceHoly() > 0
                        || var19.getDefenceUnholy() > 0) {
                        var22 = var22 + "&$1651;:";
                        if (var19.getDefenceFire() > 0) {
                           var22 = var22 + " &$1622; +" + var19.getDefenceFire() + ";&nbsp;";
                        }

                        if (var19.getDefenceWater() > 0) {
                           var22 = var22 + " &$1623; +" + var19.getDefenceWater() + ";&nbsp;";
                        }

                        if (var19.getDefenceWind() > 0) {
                           var22 = var22 + " &$1624; +" + var19.getDefenceWind() + ";&nbsp;";
                        }

                        if (var19.getDefenceEarth() > 0) {
                           var22 = var22 + " &$1625; +" + var19.getDefenceEarth() + ";&nbsp;";
                        }

                        if (var19.getDefenceHoly() > 0) {
                           var22 = var22 + " &$1626; +" + var19.getDefenceHoly() + ";&nbsp;";
                        }

                        if (var19.getDefenceUnholy() > 0) {
                           var22 = var22 + " &$1627; +" + var19.getDefenceUnholy() + ";&nbsp;";
                        }
                     }
                  }

                  if (var20.isStackable()) {
                     var22 = var22 + (var1.isLangRus() ? "Стыкуемый;&nbsp;" : "Stackable;&nbsp;");
                  }

                  if (var20.isSealedItem()) {
                     var22 = var22 + (var1.isLangRus() ? "Запечатанный;&nbsp;" : "Sealed;&nbsp;");
                  }

                  if (var20.isShadowItem()) {
                     var22 = var22 + (var1.isLangRus() ? "Теневой предмет;&nbsp;" : "Shadow item;&nbsp;");
                  }

                  if (var20.isTemporal()) {
                     var22 = var22 + (var1.isLangRus() ? "Временный;&nbsp;" : "Temporal;&nbsp;");
                  }

                  var21 = var21.replace("%item_desc%", var22);
                  var15.append(var21);
               }
            }
         } else {
            List var91 = var12.getCreateList();
            if (var91 == null) {
               this.onBypassCommand(var1, "_bbsreglist_" + var24 + "_" + var28 + "_" + var7 + "_" + var9 + "_" + var10);
               return;
            }

            if ((var13 = var12.getManufactureName()) == null) {
               var13 = "-";
            }

            for (Object __raw_var110_392 : (Iterable<?>)(Object)(var91)) {
               ManufactureItem var110 = (ManufactureItem)__raw_var110_392;
               Recipe var114 = RecipeHolder.getInstance().getRecipeById(var110.getRecipeId() - 1);
               if (var114 != null && !var114.getProducts().isEmpty()) {
                  ItemTemplate var116 = (ItemTemplate)var114.getProducts().get(0).getKey();
                  if (var116 != null) {
                     String var122 = var14.replace("%item_name%", var116.getName());
                     var122 = var122.replace("%item_img%", var116.getIcon());
                     var122 = var122.replace("%item_count%", "N/A");
                     var122 = var122.replace("%item_price%", String.format("%,3d", var110.getCost()).replace(" ", ","));
                     String var129 = "";
                     if (var116.getCrystalType() != Grade.NONE) {
                        var129 = bYD[var116.getCrystalType().ordinal() - 1]
                           + (
                              var116.getCrystalCount() > 0
                                 ? (var1.isLangRus() ? " Кристаллов: " : " Crystals: ") + var116.getCrystalCount() + ";&nbsp;"
                                 : ";&nbsp;"
                           );
                     }

                     if (var116.isStackable()) {
                        var129 = var1.isLangRus() ? "Стыкуемый;&nbsp;" : "Stackable;&nbsp;";
                     }

                     if (var116.isSealedItem()) {
                        var129 = var129 + (var1.isLangRus() ? "Запечатанный;&nbsp;" : "Sealed;&nbsp;");
                     }

                     var122 = var122.replace("%item_desc%", var129);
                     var15.append(var122);
                  }
               }
            }
         }

         String var92 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_region_view.htm", var1);
         var92 = var92.replace("%sell_type%", bYB[var28]);
         var13 = var13.replace("<", "");
         var13 = var13.replace(">", "");
         var13 = var13.replace("&", "");
         var13 = var13.replace("$", "");
         if (var13.isEmpty()) {
            var13 = "-";
         }

         var92 = var92.replace("%title%", var13);
         var92 = var92.replace("%char_name%", var12.getName());
         var92 = var92.replace("%object_id%", String.valueOf(var12.getObjectId()));
         var92 = var92.replace("%STORE_LIST%", var15.toString());
         var92 = var92.replace("%list_bypass%", "_bbsreglist_" + var24 + "_" + var28 + "_" + var7 + "_" + var9 + "_" + var10);
         var92 = var92.replace(
            "%TREE%",
            "&nbsp;>&nbsp;<a action=\"bypass _bbsregion_"
               + var24
               + "\">"
               + StringHolder.getInstance().getNotNull(var1, var11.getTownNameAddr())
               + "</a>&nbsp;>&nbsp;<a action=\"bypass _bbsreglist_"
               + var24
               + "_"
               + var28
               + "_"
               + var7
               + "_"
               + var9
               + "_\">"
               + bYB[var28]
               + "</a>&nbsp;>&nbsp;"
               + var12.getName()
         );
         ShowBoard.separateAndSend(var92, var1);
      } else if ("bbsregtarget".equals(var4)) {
         int var5 = Integer.parseInt(var3.nextToken());
         Player var6 = World.getPlayer(var5);
         if (var6 != null) {
            var1.sendPacket(new RadarControl(0, 2, var6.getLoc()));
            if (var1.knowsObject(var6)) {
               var1.setObjectTarget(var6);
               var6.broadcastRelationChanged();
            }
         } else {
            var1.sendActionFailed();
         }
      }
   }

   @Override
   public void onWriteCommand(Player var1, String var2, String var3, String var4, String var5, String var6, String var7) {
      StringTokenizer var8 = new StringTokenizer(var2, "_");
      String var9 = var8.nextToken();
      if ("bbsregsearch".equals(var9)) {
         int var10 = Integer.parseInt(var8.nextToken());
         int var11 = Integer.parseInt(var8.nextToken());
         String var12 = "Item".equals(var6) ? "1" : "0";
         if (var5 == null) {
            var5 = "";
         }

         var5 = var5.replace("<", "");
         var5 = var5.replace(">", "");
         var5 = var5.replace("&", "");
         var5 = var5.replace("$", "");
         if (var5.length() > 30) {
            var5 = var5.substring(0, 30);
         }

         this.onBypassCommand(var1, "_bbsreglist_" + var10 + "_" + var11 + "_1_" + var12 + "_" + var5);
      }
   }

   private static List<Player> c(int var0, int var1, String var2, boolean var3) {
      ArrayList var4 = new ArrayList();
      RegionCommunity.TownEntry var5 = bYA[var0];
      int var6 = var5.getX();
      int var7 = var5.getY();
      byte var8 = 0;

      for (Player var10 : GameObjectsStorage.getAllPlayersForIterate()) {
         int var11 = MapUtils.regionX(var10);
         int var12 = MapUtils.regionY(var10);
         if (var11 >= var6 - var8 && var11 <= var6 + var8 && var12 >= var7 - var8 && var12 <= var7 + var8) {
            List var13 = var10.getTradeList();
            List var14 = var10.getCreateList();
            if (var10.getPrivateStoreType() > 0) {
               if (var1 != 0 || var13 == null || var10.getPrivateStoreType() != 1 && var10.getPrivateStoreType() != 8) {
                  if (var1 == 1 && var13 != null && var10.getPrivateStoreType() == 3) {
                     var4.add(var10);
                  } else if (var1 == 2 && var14 != null && var10.getPrivateStoreType() == 5) {
                     var4.add(var10);
                  }
               } else {
                  var4.add(var10);
               }
            }
         }
      }

      if (!var2.isEmpty() && !var4.isEmpty()) {
         ArrayList var18 = new ArrayList();

         for (Object __raw_var21_530 : (Iterable<?>)(Object)(var4)) {
            Player var21 = (Player)__raw_var21_530;
            List var22 = var21.getTradeList();
            List var23 = var21.getCreateList();
            if (var3) {
               if ((var1 == 0 || var1 == 1) && var22 != null) {
                  List var25 = var1 == 0 ? var21.getSellList() : var21.getBuyList();
                  if (var25 != null) {
                     for (Object __raw_var27_537 : (Iterable<?>)(Object)(var25)) {
                        TradeItem var27 = (TradeItem)__raw_var27_537;
                        ItemTemplate var28 = ItemHolder.getInstance().getTemplate(var27.getItemId());
                        if (var28 != null && var28.getName() != null && var28.getName().toLowerCase().contains(var2)) {
                           var18.add(var21);
                           break;
                        }
                     }
                  }
               } else if (var1 == 2 && var23 != null) {
                  for (Object __raw_var15_546 : (Iterable<?>)(Object)(var23)) {
                     ManufactureItem var15 = (ManufactureItem)__raw_var15_546;
                     Recipe var16 = RecipeHolder.getInstance().getRecipeById(var15.getRecipeId() - 1);
                     if (var16 != null && !var16.getProducts().isEmpty()) {
                        ItemTemplate var17 = (ItemTemplate)var16.getProducts().get(0).getKey();
                        if (var17 != null && var17.getName() != null && var17.getName().toLowerCase().contains(var2)) {
                           var18.add(var21);
                           break;
                        }
                     }
                  }
               }
            } else if (var1 == 0 && var22 != null && var21.getSellStoreName() != null && var21.getSellStoreName().toLowerCase().contains(var2)) {
               var18.add(var21);
            } else if (var1 == 1 && var22 != null && var21.getBuyStoreName() != null && var21.getBuyStoreName().toLowerCase().contains(var2)) {
               var18.add(var21);
            } else if (var1 == 2
               && var23 != null
               && var21.getCreateList() != null
               && var21.getManufactureName() != null
               && var21.getManufactureName().toLowerCase().contains(var2)) {
               var18.add(var21);
            }
         }

         var4 = var18;
      }

      if (!var4.isEmpty()) {
         Player[] var19 = new Player[var4.size()];
         var4.toArray(var19);
         Arrays.sort(var19, new RegionCommunity.PlayersComparator<>());
         var4.clear();
         var4.addAll(Arrays.asList(var19));
      }

      return var4;
   }

   private static class PlayersComparator<T> implements Comparator<T> {
      private PlayersComparator() {
      }

      @Override
      public int compare(Object var1, Object var2) {
         if (var1 instanceof Player && var2 instanceof Player) {
            Player var3 = (Player)var1;
            Player var4 = (Player)var2;
            return var3.getName().compareTo(var4.getName());
         } else {
            return 0;
         }
      }
   }

   private static class TownEntry {
      private final String bYF;
      private final int bYG;
      private final int bYH;

      private TownEntry(String var1, int var2, int var3) {
         this.bYF = var1;
         this.bYG = var2;
         this.bYH = var3;
      }

      public String getTownNameAddr() {
         return this.bYF;
      }

      public int getX() {
         return this.bYG;
      }

      public int getY() {
         return this.bYH;
      }
   }
}
