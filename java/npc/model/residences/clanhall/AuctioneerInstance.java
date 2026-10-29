package npc.model.residences.clanhall;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import l2.commons.dao.JdbcEntityState;
import l2.gameserver.Config;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.AuctionSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.Privilege;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

public class AuctioneerInstance extends NpcInstance {
   private static final SimpleDateFormat lC = new SimpleDateFormat("dd/MM/yy");
   private static final NumberFormat ayB = NumberFormat.getIntegerInstance(Locale.KOREA);
   private static final long ayC = 604800000L;
   private static final int ayD = 7;
   private static final String ayE = "\t<tr>\n\t\t<td width=50>\n\t\t\t<font color=\"aaaaff\">&^%id%;</font>\n\t\t</td>\n\t\t<td width=100>\n\t\t\t<a action=\"bypass -h npc_%objectId%_info %id%\"><font color=\"ffffaa\">&%%id%;[%size%]</font></a>\n\t\t</td>\n\t\t<td width=50>%date%</td>\n\t\t<td width=70 align=right>\n\t\t\t<font color=\"aaffff\">%min_bid%</font>\n\t\t</td>\n\t</tr>";
   private static final int ayF = 10;
   private static final String ayG = "\t<tr>\n\t\t<td width=100><font color=\"aaaaff\">&%%id%;</font></td>\n\t\t<td width=100><font color=\"ffffaa\">%clan_name%</font></td>\n\t\t<td width=70>%date%</td>\n\t</tr>";
   private static final int[] ayH = new int[]{1, 3, 7};

   public AuctioneerInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
   }

   @Override
   public void onBypassFeedback(Player var1, String var2) {
      if (canBypassCheck(var1, this)) {
         StringTokenizer var3 = new StringTokenizer(var2.replace("\r\n", "<br1>"));
         String var4 = var3.nextToken();
         if (var4.equalsIgnoreCase("map")) {
            this.showChatWindow(var1, this.dO());
         } else if (var4.equalsIgnoreCase("list_all")) {
            int var5 = Integer.parseInt(var3.nextToken());
            ArrayList var6 = new ArrayList();

            for (ClanHall var8 : ResidenceHolder.getInstance().getResidenceList(ClanHall.class)) {
               if (var8.getSiegeEvent().getClass() == ClanHallAuctionEvent.class
                  && var8.<SiegeEvent>getSiegeEvent().isInProgress()
                  && Config.CH_DISPLAY_IDS.contains(var8.getId())) {
                  var6.add(var8.getSiegeEvent());
               }
            }

            if (var6.isEmpty()) {
               var1.sendPacket(SystemMsg.THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION);
               this.showChatWindow(var1, 0);
               return;
            }

            int var47 = 7 * var5;
            int var56 = var47 + 7;
            if (var47 > var6.size()) {
               var47 = 0;
               var56 = var47 + 7;
            }

            if (var56 > var6.size()) {
               var56 = var6.size();
            }

            NpcHtmlMessage var9 = new NpcHtmlMessage(var1, this);
            var9.setFile("residence2/clanhall/auction_list_clanhalls.htm");
            StringBuilder var10 = new StringBuilder();

            for (int var11 = var47; var11 < var56; var11++) {
               ClanHallAuctionEvent var12 = (ClanHallAuctionEvent)var6.get(var11);
               List var13 = var12.getObjects("attackers");
               Calendar var14 = var12.getEndSiegeDate();
               String var15 = "\t<tr>\n\t\t<td width=50>\n\t\t\t<font color=\"aaaaff\">&^%id%;</font>\n\t\t</td>\n\t\t<td width=100>\n\t\t\t<a action=\"bypass -h npc_%objectId%_info %id%\"><font color=\"ffffaa\">&%%id%;[%size%]</font></a>\n\t\t</td>\n\t\t<td width=50>%date%</td>\n\t\t<td width=70 align=right>\n\t\t\t<font color=\"aaffff\">%min_bid%</font>\n\t\t</td>\n\t</tr>"
                  .replace("%id%", String.valueOf(var12.getId()))
                  .replace("%min_bid%", String.valueOf(var12.getResidence().getAuctionMinBid()))
                  .replace("%size%", String.valueOf(var13.size()))
                  .replace("%date%", lC.format(var14.getTimeInMillis()));
               var10.append(var15);
            }

            var9.replace("%list%", var10.toString());
            if (var6.size() > var56) {
               var9.replace("%next_button%", "<td>" + StringHolder.getInstance().getNotNull(var1, "html_util_button_next") + "</td>");
               var9.replace("%next_bypass%", "-h npc_%objectId%_list_all " + (var5 + 1));
            } else {
               var9.replace("%next_button%", "");
            }

            if (var5 != 0) {
               var9.replace("%prev_button%", "<td>" + StringHolder.getInstance().getNotNull(var1, "html_util_button_perv") + "</td>");
               var9.replace("%prev_bypass%", "-h npc_%objectId%_list_all " + (var5 - 1));
            } else {
               var9.replace("%prev_button%", "");
            }

            var1.sendPacket(var9);
         } else if (var4.equalsIgnoreCase("info")) {
            String var19 = null;
            ClanHall var33 = null;
            SiegeClanObject var48 = null;
            if (var3.hasMoreTokens()) {
               int var58 = Integer.parseInt(var3.nextToken());
               var33 = ResidenceHolder.getInstance().getResidence(var58);
               var19 = "residence2/clanhall/auction_clanhall_info_main.htm";
            } else {
               var33 = var1.getClan() == null
                  ? null
                  : (var1.getClan().getHasHideout() > 0 ? ResidenceHolder.getInstance().getResidence(var1.getClan().getHasHideout()) : null);
               if (var33 == null || var33.getSiegeEvent().getClass() != ClanHallAuctionEvent.class) {
                  for (ClanHall var67 : ResidenceHolder.getInstance().getResidenceList(ClanHall.class)) {
                     if (var67.getSiegeEvent().getClass() == ClanHallAuctionEvent.class
                        && (var48 = var67.<SiegeEvent>getSiegeEvent().getSiegeClan("attackers", var1.getClan())) != null) {
                        var33 = var67;
                        break;
                     }
                  }

                  if (var48 == null) {
                     var1.sendPacket(SystemMsg.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
                     this.showChatWindow(var1, 0);
                     return;
                  }

                  var19 = "residence2/clanhall/auction_clanhall_info_bidded.htm";
               } else if (var33.<SiegeEvent>getSiegeEvent().isInProgress()) {
                  var19 = "residence2/clanhall/auction_clanhall_info_owner_sell.htm";
               } else {
                  var19 = "residence2/clanhall/auction_clanhall_info_owner.htm";
               }
            }

            ClanHallAuctionEvent var59 = var33.getSiegeEvent();
            List var68 = var59.getObjects("attackers");
            NpcHtmlMessage var79 = new NpcHtmlMessage(var1, this);
            var79.setFile(var19);
            var79.replace("%id%", String.valueOf(var33.getId()));
            var79.replace("%bigger_size%", String.valueOf(var68.size()));
            var79.replace("%grade%", String.valueOf(var33.getGrade()));
            var79.replace("%rental_fee%", String.valueOf(var33.getRentalFee()));
            var79.replace("%cha_currency%", HtmlUtils.htmlItemName(Config.CH_BID_CURRENCY_ITEM_ID));
            Clan var87 = var33.getOwner();
            var79.replace("%owner%", var87 == null ? "" : var87.getName());
            var79.replace("%owner_leader%", var87 == null ? "" : var87.getLeaderName());
            var79.replace("%description%", var33.getAuctionDescription());
            var79.replace("%min_bid%", String.valueOf(var33.getAuctionMinBid()));
            Calendar var93 = var59.getEndSiegeDate();
            var79.replace("%date%", lC.format(var93.getTimeInMillis()));
            var79.replace("%hour%", String.valueOf(var93.get(11)));
            int var98 = (int)((var93.getTimeInMillis() - System.currentTimeMillis()) / 60000L);
            var79.replace("%remaining_hour%", String.valueOf(var98 / 60));
            var79.replace("%remaining_minutes%", String.valueOf(var98 % 60));
            if (var48 != null) {
               var79.replace("%my_bid%", String.valueOf(var48.getParam()));
            }

            var1.sendPacket(var79);
         } else if (var4.equalsIgnoreCase("bidder_list")) {
            int var21 = Integer.parseInt(var3.nextToken());
            int var35 = Integer.parseInt(var3.nextToken());
            ClanHall var49 = ResidenceHolder.getInstance().getResidence(var21);
            ClanHallAuctionEvent var60 = var49.getSiegeEvent();
            List var69 = var60.getObjects("attackers");
            if (!var60.isInProgress()) {
               return;
            }

            int var80 = 10 * var35;
            int var88 = var80 + 10;
            if (var80 > var69.size()) {
               var80 = 0;
               var88 = var80 + 10;
            }

            if (var88 > var69.size()) {
               var88 = var69.size();
            }

            NpcHtmlMessage var94 = new NpcHtmlMessage(var1, this);
            var94.setFile("residence2/clanhall/auction_bidder_list.htm");
            var94.replace("%id%", String.valueOf(var21));
            StringBuilder var99 = new StringBuilder();

            for (int var101 = var80; var101 < var88; var101++) {
               AuctionSiegeClanObject var103 = (AuctionSiegeClanObject)var69.get(var101);
               String var16 = "\t<tr>\n\t\t<td width=100><font color=\"aaaaff\">&%%id%;</font></td>\n\t\t<td width=100><font color=\"ffffaa\">%clan_name%</font></td>\n\t\t<td width=70>%date%</td>\n\t</tr>"
                  .replace("%id%", String.valueOf(var21))
                  .replace("%clan_name%", var103.getClan().getName())
                  .replace("%date%", lC.format(var103.getDate()));
               var99.append(var16);
            }

            var94.replace("%list%", var99.toString());
            if (var69.size() > var88) {
               var94.replace("%next_button%", "<td>" + StringHolder.getInstance().getNotNull(var1, "html_util_button_next") + "</td>");
               var94.replace("%next_bypass%", "-h npc_%objectId%_bidder_list " + var21 + " " + (var35 + 1));
            } else {
               var94.replace("%next_button%", "");
            }

            if (var35 != 0) {
               var94.replace("%prev_button%", "<td>" + StringHolder.getInstance().getNotNull(var1, "html_util_button_prev") + "</td>");
               var94.replace("%prev_bypass%", "-h npc_%objectId%_bidder_list " + var21 + " " + (var35 - 1));
            } else {
               var94.replace("%prev_button%", "");
            }

            var1.sendPacket(var94);
         } else if (var4.equalsIgnoreCase("bid_start")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            int var22 = Integer.parseInt(var3.nextToken());
            ClanHall var36 = ResidenceHolder.getInstance().getResidence(var22);
            ClanHallAuctionEvent var50 = var36.getSiegeEvent();
            if (!var50.isInProgress()) {
               return;
            }

            long var61 = var36.getAuctionMinBid();
            AuctionSiegeClanObject var81 = var50.getSiegeClan("attackers", var1.getClan());
            if (var81 != null) {
               var61 = var81.getParam();
            }

            NpcHtmlMessage var89 = new NpcHtmlMessage(var1, this);
            var89.setFile("residence2/clanhall/auction_bid_start.htm");
            var89.replace("%id%", String.valueOf(var22));
            var89.replace("%min_bid%", String.valueOf(var61));
            var89.replace("%clan_currency_amount%", String.valueOf(var1.getClan().getWarehouse().getCountOf(Config.CH_BID_CURRENCY_ITEM_ID)));
            var89.replace("%cha_currency%", HtmlUtils.htmlItemName(Config.CH_BID_CURRENCY_ITEM_ID));
            var1.sendPacket(var89);
         } else if (var4.equalsIgnoreCase("bid_next")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            int var23 = Integer.parseInt(var3.nextToken());
            long var37 = 0L;
            if (var3.hasMoreTokens()) {
               try {
                  var37 = ayB.parse(var3.nextToken()).longValue();
               } catch (ParseException var18) {
               }
            }

            ClanHall var62 = ResidenceHolder.getInstance().getResidence(var23);
            ClanHallAuctionEvent var70 = var62.getSiegeEvent();
            if (!var70.isInProgress()) {
               return;
            }

            if (!this.a(var1, var70, var37)) {
               return;
            }

            long var82 = var62.getAuctionMinBid();
            AuctionSiegeClanObject var95 = var70.getSiegeClan("attackers", var1.getClan());
            if (var95 != null) {
               var82 = var95.getParam();
            }

            NpcHtmlMessage var100 = new NpcHtmlMessage(var1, this);
            var100.setFile("residence2/clanhall/auction_bid_confirm.htm");
            var100.replace("%id%", String.valueOf(var23));
            var100.replace("%bid%", String.valueOf(var37));
            var100.replace("%cha_currency%", HtmlUtils.htmlItemName(Config.CH_BID_CURRENCY_ITEM_ID));
            var100.replace("%min_bid%", String.valueOf(var82));
            Calendar var102 = var70.getEndSiegeDate();
            var100.replace("%date%", lC.format(var102.getTimeInMillis()));
            var100.replace("%hour%", String.valueOf(var102.get(11)));
            var1.sendPacket(var100);
         } else if (var4.equalsIgnoreCase("bid_confirm")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            int var24 = Integer.parseInt(var3.nextToken());
            long var38 = Long.parseLong(var3.nextToken());
            ClanHall var63 = ResidenceHolder.getInstance().getResidence(var24);
            ClanHallAuctionEvent var71 = var63.getSiegeEvent();
            if (!var71.isInProgress()) {
               return;
            }

            for (ClanHall var90 : ResidenceHolder.getInstance().getResidenceList(ClanHall.class)) {
               if (var63 != var90
                  && var90.getSiegeEvent().getClass() == ClanHallAuctionEvent.class
                  && var90.<SiegeEvent>getSiegeEvent().isInProgress()
                  && var90.<SiegeEvent>getSiegeEvent().getSiegeClan("attackers", var1.getClan()) != null) {
                  var1.sendPacket(SystemMsg.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME);
                  this.onBypassFeedback(var1, "bid_start " + var24);
                  return;
               }
            }

            if (!this.a(var1, var71, var38)) {
               return;
            }

            long var84 = var38;
            AuctionSiegeClanObject var96 = var71.getSiegeClan("attackers", var1.getClan());
            if (var96 != null) {
               var84 -= var96.getParam();
               if (var38 <= var96.getParam()) {
                  var1.sendPacket(SystemMsg.THE_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_PREVIOUS_BID);
                  this.onBypassFeedback(var1, "bid_start " + var71.getId());
                  return;
               }
            }

            var1.getClan().getWarehouse().destroyItemByItemId(Config.CH_BID_CURRENCY_ITEM_ID, var84);
            if (var96 != null) {
               var96.setParam(var38);
               SiegeClanDAO.getInstance().update(var63, var96);
            } else {
               var96 = new AuctionSiegeClanObject("attackers", var1.getClan(), var38);
               var71.addObject("attackers", var96);
               SiegeClanDAO.getInstance().insert(var63, var96);
            }

            var1.sendPacket(SystemMsg.YOUR_BID_HAS_BEEN_SUCCESSFULLY_PLACED);
            this.onBypassFeedback(var1, "info");
         } else if (var4.equalsIgnoreCase("cancel_bid")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            int var25 = Integer.parseInt(var3.nextToken());
            ClanHall var39 = ResidenceHolder.getInstance().getResidence(var25);
            ClanHallAuctionEvent var51 = var39.getSiegeEvent();
            if (!var51.isInProgress()) {
               return;
            }

            AuctionSiegeClanObject var64 = var51.getSiegeClan("attackers", var1.getClan());
            if (var64 == null) {
               return;
            }

            long var72 = var64.getParam() - (long)(var64.getParam() * 0.1);
            NpcHtmlMessage var91 = new NpcHtmlMessage(var1, this);
            var91.setFile("residence2/clanhall/auction_bid_cancel.htm");
            var91.replace("%id%", String.valueOf(var25));
            var91.replace("%cha_currency%", HtmlUtils.htmlItemName(Config.CH_BID_CURRENCY_ITEM_ID));
            var91.replace("%bid%", String.valueOf(var64.getParam()));
            var91.replace("%return%", String.valueOf(var72));
            var1.sendPacket(var91);
         } else if (var4.equalsIgnoreCase("cancel_bid_confirm")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            int var26 = Integer.parseInt(var3.nextToken());
            ClanHall var40 = ResidenceHolder.getInstance().getResidence(var26);
            ClanHallAuctionEvent var52 = var40.getSiegeEvent();
            if (!var52.isInProgress()) {
               return;
            }

            AuctionSiegeClanObject var65 = var52.getSiegeClan("attackers", var1.getClan());
            if (var65 == null) {
               return;
            }

            long var73 = var65.getParam() - (long)(var65.getParam() * 0.1);
            var1.getClan().getWarehouse().addItem(Config.CH_BID_CURRENCY_ITEM_ID, var73);
            var52.removeObject("attackers", var65);
            SiegeClanDAO.getInstance().delete(var40, var65);
            var1.sendPacket(SystemMsg.YOU_HAVE_CANCELED_YOUR_BID);
            this.showChatWindow(var1, 0);
         } else if (var4.equalsIgnoreCase("register_start")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            ClanHall var27 = ResidenceHolder.getInstance().getResidence(var1.getClan().getHasHideout());
            if (var27.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || var27.<SiegeEvent>getSiegeEvent().isInProgress()) {
               return;
            }

            if (var27.getLastSiegeDate().getTimeInMillis() + 604800000L > System.currentTimeMillis()) {
               var1.sendPacket(SystemMsg.IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION);
               this.onBypassFeedback(var1, "info");
               return;
            }

            NpcHtmlMessage var41 = new NpcHtmlMessage(var1, this);
            var41.setFile("residence2/clanhall/auction_clanhall_register_start.htm");
            var41.replace("%id%", String.valueOf(var1.getClan().getHasHideout()));
            var41.replace("%currency_amount%", String.valueOf(var1.getClan().getWarehouse().getCountOf(Config.CH_BID_CURRENCY_ITEM_ID)));
            var41.replace("%cha_currency%", HtmlUtils.htmlItemName(Config.CH_BID_CURRENCY_ITEM_ID));
            var41.replace("%deposit%", String.valueOf(var27.getDeposit()));
            var1.sendPacket(var41);
         } else if (var4.equalsIgnoreCase("register_next")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            ClanHall var28 = ResidenceHolder.getInstance().getResidence(var1.getClan().getHasHideout());
            if (var28.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || var28.<SiegeEvent>getSiegeEvent().isInProgress()) {
               this.showChatWindow(var1, 0);
               return;
            }

            if (var1.getClan().getWarehouse().getCountOf(Config.CH_BID_CURRENCY_ITEM_ID) < var28.getDeposit()) {
               var1.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
               this.onBypassFeedback(var1, "register_start");
               return;
            }

            NpcHtmlMessage var42 = new NpcHtmlMessage(var1, this);
            var42.setFile("residence2/clanhall/auction_clanhall_register_next.htm");
            var42.replace("%min_bid%", String.valueOf(var28.getBaseMinBid()));
            var42.replace("%last_bid%", String.valueOf(var28.getBaseMinBid()));
            var42.replace("%cha_currency%", HtmlUtils.htmlItemName(Config.CH_BID_CURRENCY_ITEM_ID));
            var1.sendPacket(var42);
         } else if (var4.equalsIgnoreCase("register_next2")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            ClanHall var29 = ResidenceHolder.getInstance().getResidence(var1.getClan().getHasHideout());
            if (var29.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || var29.<SiegeEvent>getSiegeEvent().isInProgress()) {
               this.showChatWindow(var1, 0);
               return;
            }

            int var43 = Integer.parseInt(var3.nextToken());
            if (ArrayUtils.indexOf(ayH, var43) == -1) {
               this.showChatWindow(var1, 0);
               return;
            }

            long var53 = -1L;
            String var74 = "";
            if (var3.hasMoreTokens()) {
               try {
                  var53 = Long.parseLong(var3.nextToken());
               } catch (Exception var17) {
               }
            }

            if (var3.hasMoreTokens()) {
               var74 = var3.nextToken();

               while (var3.hasMoreTokens()) {
                  var74 = var74 + " " + var3.nextToken();
               }
            }

            var74 = HtmlUtils.sanitizeHtml(var74);
            if (var53 <= -1L) {
               this.onBypassFeedback(var1, "register_next");
               return;
            }

            Calendar var85 = Calendar.getInstance();
            var85.add(11, var43);
            NpcHtmlMessage var92 = new NpcHtmlMessage(var1, this);
            var92.setFile("residence2/clanhall/auction_clanhall_register_confirm.htm");
            var92.replace("%description%", var74);
            var92.replace("%day%", String.valueOf(var43));
            var92.replace("%bid%", String.valueOf(var53));
            var92.replace("%cha_currency%", HtmlUtils.htmlItemName(Config.CH_BID_CURRENCY_ITEM_ID));
            var92.replace("%base_bid%", String.valueOf(var29.getBaseMinBid()));
            var92.replace("%hour%", String.valueOf(var85.get(11)));
            var92.replace("%date%", lC.format(var85.getTimeInMillis()));
            var1.sendPacket(var92);
         } else if (var4.equalsIgnoreCase("register_confirm")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            ClanHall var30 = ResidenceHolder.getInstance().getResidence(var1.getClan().getHasHideout());
            if (var30.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || var30.<SiegeEvent>getSiegeEvent().isInProgress()) {
               this.showChatWindow(var1, 0);
               return;
            }

            if (var30.getLastSiegeDate().getTimeInMillis() + 604800000L > System.currentTimeMillis()) {
               var1.sendPacket(SystemMsg.IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION);
               this.onBypassFeedback(var1, "info");
               return;
            }

            int var44 = Integer.parseInt(var3.nextToken());
            if (ArrayUtils.indexOf(ayH, var44) == -1) {
               this.showChatWindow(var1, 0);
               return;
            }

            long var54 = Long.parseLong(var3.nextToken());
            String var76 = "";
            if (var3.hasMoreTokens()) {
               var76 = var3.nextToken();

               while (var3.hasMoreTokens()) {
                  var76 = var76 + " " + var3.nextToken();
               }
            }

            var76 = HtmlUtils.sanitizeHtml(var76);
            if (var54 <= -1L) {
               this.onBypassFeedback(var1, "register_next");
               return;
            }

            var30.setAuctionMinBid(var54);
            var30.setAuctionDescription(var76);
            var30.setAuctionLength(var44);
            var30.getSiegeDate().setTimeInMillis(System.currentTimeMillis());
            var30.setJdbcState(JdbcEntityState.UPDATED);
            var30.update();
            var30.<SiegeEvent>getSiegeEvent().reCalcNextTime(false);
            this.onBypassFeedback(var1, "info");
            var1.sendPacket(SystemMsg.YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION);
         } else if (var4.equals("cancel_start")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            ClanHall var31 = ResidenceHolder.getInstance().getResidence(var1.getClan().getHasHideout());
            if (var31.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || !var31.<SiegeEvent>getSiegeEvent().isInProgress()) {
               this.showChatWindow(var1, 0);
               return;
            }

            NpcHtmlMessage var45 = new NpcHtmlMessage(var1, this);
            var45.setFile("residence2/clanhall/auction_clanhall_cancel_confirm.htm");
            var45.replace("%deposit%", String.valueOf(var31.getDeposit()));
            var45.replace("%cha_currency%", HtmlUtils.htmlItemName(Config.CH_BID_CURRENCY_ITEM_ID));
            var1.sendPacket(var45);
         } else if (var4.equals("cancel_confirm")) {
            if (!this.al(var1)) {
               this.showChatWindow(var1, 0);
               return;
            }

            ClanHall var32 = ResidenceHolder.getInstance().getResidence(var1.getClan().getHasHideout());
            if (var32.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || !var32.<SiegeEvent>getSiegeEvent().isInProgress()) {
               this.showChatWindow(var1, 0);
               return;
            }

            var32.<SiegeEvent>getSiegeEvent().setInProgress(false);
            var32.getSiegeDate().setTimeInMillis(0L);
            var32.getLastSiegeDate().setTimeInMillis(System.currentTimeMillis());
            var32.setAuctionDescription("");
            var32.setAuctionLength(0);
            var32.setAuctionMinBid(0L);
            var32.setJdbcState(JdbcEntityState.UPDATED);
            var32.update();
            ClanHallAuctionEvent var46 = var32.getSiegeEvent();
            List var55 = var46.removeObjects("attackers");
            SiegeClanDAO.getInstance().delete(var32);

            for (Object __raw_var78_585 : (Iterable<?>)(Object)(var55)) {
               AuctionSiegeClanObject var78 = (AuctionSiegeClanObject)__raw_var78_585;
               long var86 = var78.getParam() - (long)(var78.getParam() * 0.1);
               var78.getClan().getWarehouse().addItem(Config.CH_BID_CURRENCY_ITEM_ID, var86);
            }

            var32.<SiegeEvent>getSiegeEvent().reCalcNextTime(false);
            this.onBypassFeedback(var1, "info");
         } else {
            super.onBypassFeedback(var1, var2);
         }
      }
   }

   @Override
   public void showChatWindow(Player var1, int var2, Object... var3) {
      this.showChatWindow(var1, "residence2/clanhall/auction_dealer001.htm");
   }

   private boolean al(Player var1) {
      if (var1.getClan() == null || var1.getClan().getLevel() < Config.CLAN_MIN_LEVEL_FOR_BID) {
         var1.sendPacket(SystemMsg.ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_HIGHER_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION);
         return false;
      } else if (var1.getClan().isPlacedForDisband()) {
         var1.sendPacket(SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
         return false;
      } else if (!var1.hasPrivilege(Privilege.CH_AUCTION)) {
         var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
         return false;
      } else {
         return true;
      }
   }

   private boolean a(Player var1, ClanHallAuctionEvent var2, long var3) {
      long var5 = var3;
      AuctionSiegeClanObject var7 = var2.getSiegeClan("attackers", var1.getClan());
      if (var7 != null) {
         var5 -= var7.getParam();
      }

      if (var5 > var1.getClan().getWarehouse().getCountOf(Config.CH_BID_CURRENCY_ITEM_ID)) {
         var1.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
         this.onBypassFeedback(var1, "bid_start " + var2.getId());
         return false;
      } else {
         long var8 = var7 == null ? var2.getResidence().getAuctionMinBid() : var7.getParam();
         if (var3 < var8) {
            var1.sendPacket(SystemMsg.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
            this.onBypassFeedback(var1, "bid_start " + var2.getId());
            return false;
         } else {
            return true;
         }
      }
   }

   private String dO() {
      int var1 = MapUtils.regionX(this);
      int var2 = MapUtils.regionY(this);
      return String.format("residence2/clanhall/map_agit_%d_%d.htm", var1, var2);
   }
}
