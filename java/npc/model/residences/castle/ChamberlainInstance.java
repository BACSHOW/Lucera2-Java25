package npc.model.residences.castle;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import l2.gameserver.dao.CastleDamageZoneDAO;
import l2.gameserver.dao.CastleDoorUpgradeDAO;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.CastleManorManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import l2.gameserver.model.entity.events.objects.DoorObject;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.pledge.Privilege;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.CastleSiegeInfo;
import l2.gameserver.network.l2.s2c.ExShowCropInfo;
import l2.gameserver.network.l2.s2c.ExShowCropSetting;
import l2.gameserver.network.l2.s2c.ExShowManorDefaultInfo;
import l2.gameserver.network.l2.s2c.ExShowSeedInfo;
import l2.gameserver.network.l2.s2c.ExShowSeedSetting;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.ReflectionUtils;
import npc.model.residences.ResidenceManager;

public class ChamberlainInstance extends ResidenceManager {
   private static final int ayp = 300;
   private static final int ayq = 1000;
   private static final int ayr = 10;
   private static final String ays = "SSQTicketBuyCount";
   private static final String ayt = "residence2/castle/chamberlain_saius049.htm";
   private static final String ayu = "residence2/castle/SSQ_NotEnoughTicket.htm";
   private static final String ayv = "residence2/castle/SSQ_NotDawnOrEvent.htm";
   private static final String ayw = "residence2/castle/chamberlain_saius021.htm";
   private static final String ayx = "residence2/castle/SSQ_SellDawnTicket.htm";
   private static final String ayy = "residence2/castle/chamberlain_saius063.htm";

   public ChamberlainInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
   }

   @Override
   protected void setDialogs() {
      this._mainDialog = "castle/chamberlain/chamberlain.htm";
      this._failDialog = "castle/chamberlain/chamberlain-notlord.htm";
      this._siegeDialog = this._mainDialog;
   }

   @Override
   public void onBypassFeedback(Player var1, String var2) {
      if (canBypassCheck(var1, this)) {
         int var3 = this.getCond(var1);
         if (var3 == 2) {
            StringTokenizer var4 = new StringTokenizer(var2, " ");
            String var5 = var4.nextToken();
            String var6 = "";
            if (var4.countTokens() >= 1) {
               var6 = var4.nextToken();
            }

            Castle var7 = this.getCastle();
            if (var5.equalsIgnoreCase("viewSiegeInfo")) {
               if (!this.isHaveRigths(var1, 131072)) {
                  var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                  return;
               }

               var1.sendPacket(new CastleSiegeInfo(var7, var1));
            } else if (var5.equalsIgnoreCase("ssq_selldawnticket")) {
               NpcHtmlMessage var42 = new NpcHtmlMessage(var1, this);
               if (!var1.isClanLeader()) {
                  this.showChatWindow(var1, "residence2/castle/chamberlain_saius063.htm");
                  return;
               }

               if (!this.ak(var1)) {
                  return;
               }

               if (SevenSigns.getInstance().getCurrentPeriod() != 1 || SevenSigns.getInstance().getPlayerCabal(var1) != 2) {
                  this.showChatWindow(var1, "residence2/castle/SSQ_NotDawnOrEvent.htm");
                  return;
               }

               if (SevenSigns.getInstance().getCurrentPeriod() == 1 && SevenSigns.getInstance().getPlayerCabal(var1) == 2) {
                  if (var1.getVarInt("SSQTicketBuyCount", 0) < 30) {
                     var42.setFile("residence2/castle/SSQ_SellDawnTicket.htm");
                     var42.replace("<?DawnTicketLeft?>", String.valueOf(300 - var1.getVarInt("SSQTicketBuyCount", 0) * 10));
                     var42.replace("<?DawnTicketBundle?>", String.valueOf(10));
                     var42.replace("<?DawnTicketPrice?>", String.valueOf(10000));
                     var1.sendPacket(var42);
                  } else {
                     this.showChatWindow(var1, "residence2/castle/SSQ_NotEnoughTicket.htm");
                  }
               }
            } else if (var5.equalsIgnoreCase("ask260")) {
               if (!var1.isClanLeader()) {
                  this.showChatWindow(var1, "residence2/castle/chamberlain_saius063.htm");
                  return;
               }

               if (!this.ak(var1)) {
                  return;
               }

               if (SevenSigns.getInstance().getCurrentPeriod() != 1 || SevenSigns.getInstance().getPlayerCabal(var1) != 2) {
                  this.showChatWindow(var1, "residence2/castle/SSQ_NotDawnOrEvent.htm");
                  return;
               }

               if (SevenSigns.getInstance().getCurrentPeriod() == 1
                  && SevenSigns.getInstance().getPlayerCabal(var1) == 2
                  && var1.getVarInt("SSQTicketBuyCount", 0) < 30) {
                  if (var1.getAdena() >= 10000L) {
                     ItemFunctions.removeItem(var1, 57, 10000L, true);
                     ItemFunctions.addItem(var1, 6388, 10L, true);
                     var1.setVar("SSQTicketBuyCount", var1.getVarInt("SSQTicketBuyCount", 0) + 1, -1L);
                  } else {
                     this.showChatWindow(var1, "residence2/castle/chamberlain_saius049.htm");
                  }
               }
            } else if (var5.equalsIgnoreCase("ManageTreasure")) {
               if (!var1.isClanLeader()) {
                  var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                  return;
               }

               NpcHtmlMessage var41 = new NpcHtmlMessage(var1, this);
               var41.setFile("castle/chamberlain/chamberlain-castlevault.htm");
               var41.replace("%Treasure%", String.valueOf(var7.getTreasury()));
               var41.replace("%CollectedShops%", String.valueOf(var7.getCollectedShops()));
               var41.replace("%CollectedSeed%", String.valueOf(var7.getCollectedSeed()));
               var1.sendPacket(var41);
            } else if (var5.equalsIgnoreCase("TakeTreasure")) {
               if (!var1.isClanLeader()) {
                  var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                  return;
               }

               if (!var6.equals("")) {
                  long var39 = Long.parseLong(var6);
                  if (var7.getTreasury() < var39) {
                     NpcHtmlMessage var54 = new NpcHtmlMessage(var1, this);
                     var54.setFile("castle/chamberlain/chamberlain-havenottreasure.htm");
                     var54.replace("%Treasure%", String.valueOf(var7.getTreasury()));
                     var54.replace("%Requested%", String.valueOf(var39));
                     var1.sendPacket(var54);
                     return;
                  }

                  if (var39 > 0L) {
                     var7.addToTreasuryNoTax(-var39, false, false);
                     Log.add(var7.getName() + "|" + -var39 + "|CastleChamberlain", "treasury");
                     var1.addAdena(var39);
                  }
               }

               NpcHtmlMessage var40 = new NpcHtmlMessage(var1, this);
               var40.setFile("castle/chamberlain/chamberlain-castlevault.htm");
               var40.replace("%Treasure%", String.valueOf(var7.getTreasury()));
               var40.replace("%CollectedShops%", String.valueOf(var7.getCollectedShops()));
               var40.replace("%CollectedSeed%", String.valueOf(var7.getCollectedSeed()));
               var1.sendPacket(var40);
            } else if (var5.equalsIgnoreCase("PutTreasure")) {
               if (!var6.equals("")) {
                  long var37 = Long.parseLong(var6);
                  if (var37 > var1.getAdena()) {
                     var1.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                     return;
                  }

                  if (var37 > 0L) {
                     var7.addToTreasuryNoTax(var37, false, false);
                     Log.add(var7.getName() + "|" + var37 + "|CastleChamberlain", "treasury");
                     var1.reduceAdena(var37, true);
                  }
               }

               NpcHtmlMessage var38 = new NpcHtmlMessage(var1, this);
               var38.setFile("castle/chamberlain/chamberlain-castlevault.htm");
               var38.replace("%Treasure%", String.valueOf(var7.getTreasury()));
               var38.replace("%CollectedShops%", String.valueOf(var7.getCollectedShops()));
               var38.replace("%CollectedSeed%", String.valueOf(var7.getCollectedSeed()));
               var1.sendPacket(var38);
            } else if (var5.equalsIgnoreCase("manor")) {
               if (!this.isHaveRigths(var1, 65536)) {
                  var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                  return;
               }

               String var35 = "";
               if (CastleManorManager.getInstance().isDisabled()) {
                  var35 = "npcdefault.htm";
               } else {
                  int var49 = Integer.parseInt(var6);
                  switch (var49) {
                     case 0:
                        var35 = "castle/chamberlain/manor/manor.htm";
                        break;
                     case 4:
                        var35 = "castle/chamberlain/manor/manor_help00" + var4.nextToken() + ".htm";
                        break;
                     default:
                        var35 = "castle/chamberlain/chamberlain-no.htm";
                  }
               }

               if (var35.length() > 0) {
                  NpcHtmlMessage var50 = new NpcHtmlMessage(var1, this);
                  var50.setFile(var35);
                  var1.sendPacket(var50);
               }
            } else if (var5.startsWith("manor_menu_select")) {
               if (!this.isHaveRigths(var1, 65536)) {
                  var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                  return;
               }

               if (CastleManorManager.getInstance().isUnderMaintenance()) {
                  var1.sendPacket(SystemMsg.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
                  var1.sendActionFailed();
                  return;
               }

               String var34 = var5.substring(var5.indexOf("?") + 1);
               StringTokenizer var48 = new StringTokenizer(var34, "&");
               int var53 = Integer.parseInt(var48.nextToken().split("=")[1]);
               int var58 = Integer.parseInt(var48.nextToken().split("=")[1]);
               int var59 = Integer.parseInt(var48.nextToken().split("=")[1]);
               int var61;
               if (var58 == -1) {
                  var61 = var7.getId();
               } else {
                  var61 = var58;
               }

               switch (var53) {
                  case 3:
                     if (var59 == 1 && !ResidenceHolder.getInstance().getResidence(Castle.class, var61).isNextPeriodApproved()) {
                        var1.sendPacket(new ExShowSeedInfo(var61, Collections.emptyList()));
                     } else {
                        var1.sendPacket(new ExShowSeedInfo(var61, ResidenceHolder.getInstance().getResidence(Castle.class, var61).getSeedProduction(var59)));
                     }
                     break;
                  case 4:
                     if (var59 == 1 && !ResidenceHolder.getInstance().getResidence(Castle.class, var61).isNextPeriodApproved()) {
                        var1.sendPacket(new ExShowCropInfo(var61, Collections.emptyList()));
                     } else {
                        var1.sendPacket(new ExShowCropInfo(var61, ResidenceHolder.getInstance().getResidence(Castle.class, var61).getCropProcure(var59)));
                     }
                     break;
                  case 5:
                     var1.sendPacket(new ExShowManorDefaultInfo());
                  case 6:
                  default:
                     break;
                  case 7:
                     if (var7.isNextPeriodApproved()) {
                        var1.sendPacket(SystemMsg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM);
                     } else {
                        var1.sendPacket(new ExShowSeedSetting(var7.getId()));
                     }
                     break;
                  case 8:
                     if (var7.isNextPeriodApproved()) {
                        var1.sendPacket(SystemMsg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM);
                     } else {
                        var1.sendPacket(new ExShowCropSetting(var7.getId()));
                     }
               }
            } else if (var5.equalsIgnoreCase("operate_door")) {
               if (!this.isHaveRigths(var1, 32768)) {
                  var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                  return;
               }

               if (var7.<SiegeEvent>getSiegeEvent().isInProgress()) {
                  this.showChatWindow(var1, "residence2/castle/chamberlain_saius021.htm");
                  return;
               }

               if (!var6.equals("")) {
                  boolean var32 = Integer.parseInt(var6) == 1;

                  while (var4.hasMoreTokens()) {
                     DoorInstance var47 = ReflectionUtils.getDoor(Integer.parseInt(var4.nextToken()));
                     if (var32) {
                        var47.openMe(var1, true);
                     } else {
                        var47.closeMe(var1, true);
                     }
                  }
               }

               NpcHtmlMessage var33 = new NpcHtmlMessage(var1, this);
               var33.setFile("castle/chamberlain/" + this.getTemplate().npcId + "-d.htm");
               var1.sendPacket(var33);
            } else if (var5.equalsIgnoreCase("tax_set")) {
               if (!this.isHaveRigths(var1, 1048576)) {
                  var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                  return;
               }

               if (!var6.equals("")) {
                  byte var30 = 15;
                  if (SevenSigns.getInstance().getSealOwner(3) == 1) {
                     var30 = 5;
                  } else if (SevenSigns.getInstance().getSealOwner(3) == 2) {
                     var30 = 25;
                  }

                  int var46 = Integer.parseInt(var6);
                  if (var46 < 0 || var46 > var30) {
                     NpcHtmlMessage var52 = new NpcHtmlMessage(var1, this);
                     var52.setFile("castle/chamberlain/chamberlain-hightax.htm");
                     var52.replace("%CurrentTax%", String.valueOf(var7.getTaxPercent()));
                     var1.sendPacket(var52);
                     return;
                  }

                  var7.setTaxPercent(var1, var46);
               }

               NpcHtmlMessage var31 = new NpcHtmlMessage(var1, this);
               var31.setFile("castle/chamberlain/chamberlain-settax.htm");
               var31.replace("%CurrentTax%", String.valueOf(var7.getTaxPercent()));
               var1.sendPacket(var31);
            } else if (var5.equalsIgnoreCase("upgrade_castle")) {
               if (!this.ak(var1)) {
                  return;
               }

               this.showChatWindow(var1, "castle/chamberlain/chamberlain-upgrades.htm");
            } else if (var5.equalsIgnoreCase("reinforce")) {
               if (!this.ak(var1)) {
                  return;
               }

               NpcHtmlMessage var29 = new NpcHtmlMessage(var1, this);
               var29.setFile("castle/chamberlain/doorStrengthen-" + var7.getName() + ".htm");
               var1.sendPacket(var29);
            } else if (var5.equalsIgnoreCase("trap_select")) {
               if (!this.ak(var1)) {
                  return;
               }

               NpcHtmlMessage var28 = new NpcHtmlMessage(var1, this);
               var28.setFile("castle/chamberlain/trap_select-" + var7.getName() + ".htm");
               var1.sendPacket(var28);
            } else if (var5.equalsIgnoreCase("buy_trap")) {
               if (!this.ak(var1)) {
                  return;
               }

               if (var7.<SiegeEvent>getSiegeEvent().getObjects("bought_zones").contains(var6)) {
                  NpcHtmlMessage var27 = new NpcHtmlMessage(var1, this);
                  var27.setFile("castle/chamberlain/trapAlready.htm");
                  var1.sendPacket(var27);
                  return;
               }

               List var26 = var7.<SiegeEvent>getSiegeEvent().getObjects(var6);
               long var44 = 0L;

               for (Object __raw_var12_375 : (Iterable<?>)(Object)(var26)) {
                  CastleDamageZoneObject var12 = (CastleDamageZoneObject)__raw_var12_375;
                  var44 += var12.getPrice();
               }

               var44 = n(var44);
               if (var1.getClan().getAdenaCount() < var44) {
                  var1.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                  return;
               }

               var1.getClan().getWarehouse().destroyItemByItemId(57, var44);
               var7.<SiegeEvent>getSiegeEvent().addObject("bought_zones", var6);
               CastleDamageZoneDAO.getInstance().insert(var7, var6);
               NpcHtmlMessage var57 = new NpcHtmlMessage(var1, this);
               var57.setFile("castle/chamberlain/trapSuccess.htm");
               var1.sendPacket(var57);
            } else if (var5.equalsIgnoreCase("door_manage")) {
               if (!this.isHaveRigths(var1, 32768)) {
                  var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                  return;
               }

               if (var7.<SiegeEvent>getSiegeEvent().isInProgress()) {
                  this.showChatWindow(var1, "residence2/castle/chamberlain_saius021.htm");
                  return;
               }

               NpcHtmlMessage var25 = new NpcHtmlMessage(var1, this);
               var25.setFile("castle/chamberlain/doorManage.htm");
               var25.replace("%id%", var6);
               var25.replace("%type%", var4.nextToken());
               var1.sendPacket(var25);
            } else if (var5.equalsIgnoreCase("upgrade_door_confirm")) {
               if (!this.isHaveRigths(var1, 131072)) {
                  var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                  return;
               }

               int var24 = Integer.parseInt(var6);
               int var43 = Integer.parseInt(var4.nextToken());
               int var51 = Integer.parseInt(var4.nextToken());
               long var55 = this.k(var43, var51);
               NpcHtmlMessage var60 = new NpcHtmlMessage(var1, this);
               var60.setFile("castle/chamberlain/doorConfirm.htm");
               var60.replace("%id%", String.valueOf(var24));
               var60.replace("%level%", String.valueOf(var51));
               var60.replace("%type%", String.valueOf(var43));
               var60.replace("%price%", String.valueOf(var55));
               var1.sendPacket(var60);
            } else if (!var5.equalsIgnoreCase("upgrade_door")) {
               if (var5.equalsIgnoreCase("report")) {
                  if (!this.isHaveRigths(var1, 262144)) {
                     var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                     return;
                  }

                  NpcHtmlMessage var19 = new NpcHtmlMessage(var1, this);
                  var19.setFile("castle/chamberlain/chamberlain-report.htm");
                  var19.replace("%FeudName%", var7.getName());
                  var19.replace("%CharClan%", var1.getClan().getName());
                  var19.replace("%CharName%", var1.getName());
                  if (SevenSigns.getInstance().getCurrentPeriod() == 1) {
                     var19.replace("%SSPeriod%", new CustomMessage("ChamberlainInstance.NpcString.COMPETITION", var1).toString());
                  } else if (SevenSigns.getInstance().getCurrentPeriod() == 3) {
                     var19.replace("%SSPeriod%", new CustomMessage("ChamberlainInstance.NpcString.SEAL_VALIDATION", var1).toString());
                  } else {
                     var19.replace("%SSPeriod%", new CustomMessage("ChamberlainInstance.NpcString.PREPARATION", var1).toString());
                  }

                  switch (SevenSigns.getInstance().getSealOwner(1)) {
                     case 0:
                        var19.replace("%Avarice%", new CustomMessage("SevenSigns.NpcString.NO_OWNER", var1).toString());
                        break;
                     case 1:
                        var19.replace("%Avarice%", new CustomMessage("SevenSigns.NpcString.DUSK", var1).toString());
                        break;
                     case 2:
                        var19.replace("%Avarice%", new CustomMessage("SevenSigns.NpcString.DAWN", var1).toString());
                  }

                  switch (SevenSigns.getInstance().getSealOwner(2)) {
                     case 0:
                        var19.replace("%Revelation%", new CustomMessage("SevenSigns.NpcString.NO_OWNER", var1).toString());
                        break;
                     case 1:
                        var19.replace("%Revelation%", new CustomMessage("SevenSigns.NpcString.DUSK", var1).toString());
                        break;
                     case 2:
                        var19.replace("%Revelation%", new CustomMessage("SevenSigns.NpcString.DAWN", var1).toString());
                  }

                  switch (SevenSigns.getInstance().getSealOwner(3)) {
                     case 0:
                        var19.replace("%Strife%", new CustomMessage("SevenSigns.NpcString.NO_OWNER", var1).toString());
                        break;
                     case 1:
                        var19.replace("%Strife%", new CustomMessage("SevenSigns.NpcString.DUSK", var1).toString());
                        break;
                     case 2:
                        var19.replace("%Strife%", new CustomMessage("SevenSigns.NpcString.DAWN", var1).toString());
                  }

                  var1.sendPacket(var19);
               } else if (var5.equalsIgnoreCase("Crown")) {
                  if (!var1.isClanLeader()) {
                     var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                     return;
                  }

                  if (var1.getInventory().getItemByItemId(6841) == null) {
                     var1.getInventory().addItem(ItemFunctions.createItem(6841));
                     NpcHtmlMessage var20 = new NpcHtmlMessage(var1, this);
                     var20.setFile("castle/chamberlain/chamberlain-givecrown.htm");
                     var20.replace("%CharName%", var1.getName());
                     var20.replace("%FeudName%", var7.getName());
                     var1.sendPacket(var20);
                  } else {
                     NpcHtmlMessage var21 = new NpcHtmlMessage(var1, this);
                     var21.setFile("castle/chamberlain/alreadyhavecrown.htm");
                     var1.sendPacket(var21);
                  }
               } else if (var5.equalsIgnoreCase("manageFunctions")) {
                  if (!var1.hasPrivilege(Privilege.CS_FS_SET_FUNCTIONS)) {
                     this.showChatWindow(var1, "residence2/castle/chamberlain_saius063.htm");
                  } else {
                     this.showChatWindow(var1, "residence2/castle/chamberlain_saius065.htm");
                  }
               } else if (var5.equalsIgnoreCase("manageSiegeFunctions")) {
                  if (!var1.hasPrivilege(Privilege.CS_FS_SET_FUNCTIONS)) {
                     this.showChatWindow(var1, "residence2/castle/chamberlain_saius063.htm");
                  } else if (SevenSigns.getInstance().getCurrentPeriod() != 3) {
                     this.showChatWindow(var1, "residence2/castle/chamberlain_saius068.htm");
                  } else {
                     this.showChatWindow(var1, "residence2/castle/chamberlain_saius052.htm");
                  }
               } else if (var5.equalsIgnoreCase("items")) {
                  NpcHtmlMessage var22 = new NpcHtmlMessage(var1, this);
                  var22.setFile("residence2/castle/chamberlain_saius064.htm");
                  var22.replace("%npcId%", String.valueOf(this.getNpcId()));
                  var1.sendPacket(var22);
               } else if (var5.equalsIgnoreCase("default")) {
                  NpcHtmlMessage var23 = new NpcHtmlMessage(var1, this);
                  var23.setFile("castle/chamberlain/chamberlain.htm");
                  var1.sendPacket(var23);
               } else {
                  super.onBypassFeedback(var1, var2);
               }
            } else {
               if (!this.ak(var1)) {
                  return;
               }

               int var8 = Integer.parseInt(var6);
               int var9 = Integer.parseInt(var4.nextToken());
               int var10 = Integer.parseInt(var4.nextToken());
               long var11 = this.k(var9, var10);
               List var13 = var7.<SiegeEvent>getSiegeEvent().getObjects("doors");
               DoorObject var14 = null;
               Iterator var15 = var13.iterator();

               while (true) {
                  if (var15.hasNext()) {
                     DoorObject var16 = (DoorObject)var15.next();
                     if (var16.getUId() != var8) {
                        continue;
                     }

                     var14 = var16;
                  }

                  DoorInstance var62 = var14.getDoor();
                  int var63 = (var62.getMaxHp() - var62.getUpgradeHp()) * var10 - var62.getMaxHp();
                  if (var11 == 0L || var63 < 0) {
                     var1.sendMessage(new CustomMessage("common.Error", var1));
                     return;
                  }

                  if (var62.getUpgradeHp() >= var63) {
                     int var17 = var62.getUpgradeHp() / (var62.getMaxHp() - var62.getUpgradeHp()) + 1;
                     NpcHtmlMessage var18 = new NpcHtmlMessage(var1, this);
                     var18.setFile("castle/chamberlain/doorAlready.htm");
                     var18.replace("%level%", String.valueOf(var17));
                     var1.sendPacket(var18);
                     return;
                  }

                  if (var1.getClan().getAdenaCount() < var11) {
                     var1.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                     return;
                  }

                  var1.getClan().getWarehouse().destroyItemByItemId(57, var11);
                  var1.sendMessage(new CustomMessage("npc.model.ChamberlainInstance.BuildReinforced", var1));
                  var14.setUpgradeValue(var7.getSiegeEvent(), var63);
                  CastleDoorUpgradeDAO.getInstance().insert(var62.getDoorId(), var63);
                  break;
               }
            }
         }
      }
   }

   @Override
   protected int getCond(Player var1) {
      if (var1.isGM()) {
         return 2;
      }

      Castle var2 = this.getCastle();
      if (var2 != null && var2.getId() > 0 && var1.getClan() != null) {
         if (var2.<SiegeEvent>getSiegeEvent().isInProgress()) {
            return 1;
         }

         if (var2.getOwnerId() == var1.getClanId()) {
            if (var1.isClanLeader()) {
               return 2;
            }

            if (this.isHaveRigths(var1, 32768)
               || this.isHaveRigths(var1, 65536)
               || this.isHaveRigths(var1, 131072)
               || this.isHaveRigths(var1, 262144)
               || this.isHaveRigths(var1, 524288)
               || this.isHaveRigths(var1, 1048576)
               || this.isHaveRigths(var1, 2097152)
               || this.isHaveRigths(var1, 4194304)) {
               return 2;
            }
         }
      }

      return 0;
   }

   private long k(int var1, int var2) {
      int var3 = 0;
      switch (var1) {
         case 1:
            switch (var2) {
               case 2:
                  var3 = 3000000;
                  return n(var3);
               case 3:
                  var3 = 4000000;
                  return n(var3);
               case 4:
               default:
                  return n(var3);
               case 5:
                  var3 = 5000000;
                  return n(var3);
            }
         case 2:
            switch (var2) {
               case 2:
                  var3 = 750000;
                  return n(var3);
               case 3:
                  var3 = 900000;
                  return n(var3);
               case 4:
               default:
                  return n(var3);
               case 5:
                  var3 = 1000000;
                  return n(var3);
            }
         case 3:
            switch (var2) {
               case 2:
                  var3 = 1600000;
                  break;
               case 3:
                  var3 = 1800000;
               case 4:
               default:
                  break;
               case 5:
                  var3 = 2000000;
            }
      }

      return n(var3);
   }

   private static long n(long var0) {
      byte var2 = 80;
      byte var3 = 100;
      short var4 = 300;
      switch (SevenSigns.getInstance().getSealOwner(3)) {
         case 1:
            var0 = var0 * var4 / 100L;
            break;
         case 2:
            var0 = var0 * var2 / 100L;
            break;
         default:
            var0 = var0 * var3 / 100L;
      }

      return var0;
   }

   @Override
   protected Residence getResidence() {
      return this.getCastle();
   }

   @Override
   public L2GameServerPacket decoPacket() {
      return null;
   }

   @Override
   protected int getPrivUseFunctions() {
      return 262144;
   }

   @Override
   protected int getPrivSetFunctions() {
      return 4194304;
   }

   @Override
   protected int getPrivDismiss() {
      return 524288;
   }

   @Override
   protected int getPrivDoors() {
      return 32768;
   }

   private boolean ak(Player var1) {
      Castle var2 = this.getCastle();
      if (!var1.hasPrivilege(Privilege.CS_FS_SIEGE_WAR)) {
         var1.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
         return false;
      } else if (var2.<SiegeEvent>getSiegeEvent().isInProgress()) {
         this.showChatWindow(var1, "residence2/castle/chamberlain_saius021.htm");
         return false;
      } else {
         return true;
      }
   }

   @Override
   protected boolean canInteractWithKarmaPlayer() {
      return true;
   }
}
