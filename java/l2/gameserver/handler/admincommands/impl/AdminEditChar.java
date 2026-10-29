package l2.gameserver.handler.admincommands.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.AccountBonusDAO;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.mysql;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.World;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.PlayerClass;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExPCCafePointInfo;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class AdminEditChar implements IAdminCommandHandler {
   @Override
   public boolean useAdminCommand(Enum var1, String[] var2, String var3, Player var4) {
      AdminEditChar.Commands var5 = (AdminEditChar.Commands)var1;
      if (var4.getPlayerAccess().CanRename) {
         if (var3.startsWith("admin_settitle")) {
            try {
               String var61 = var3.substring(15);
               GameObject var95 = var4.getTarget();
               Player var106 = null;
               if (var95 == null) {
                  return false;
               }

               if (var95.isPlayer()) {
                  var106 = (Player)var95;
                  var106.setTitle(var61);
                  var106.sendMessage("Your title has been changed by a GM");
                  var106.sendChanges();
               } else if (var95.isNpc()) {
                  ((NpcInstance)var95).setTitle(var61);
                  var95.decayMe();
                  var95.spawnMe();
               }

               return true;
            } catch (StringIndexOutOfBoundsException var13) {
               var4.sendMessage("You need to specify the new title.");
               return false;
            }
         }

         if (var3.startsWith("admin_setclass")) {
            try {
               String var60 = var3.substring(15);
               int var94 = Integer.parseInt(var60.trim());
               GameObject var105 = var4.getTarget();
               if (var105 == null || !var105.isPlayer()) {
                  var105 = var4;
               }

               if (var94 > 118) {
                  var4.sendMessage("There are no classes over 118 id.");
                  return false;
               }

               if (!Config.EVERYBODY_HAS_ADMIN_RIGHTS && !var4.getPlayerAccess().CanChangeClass) {
                  var4.sendMessage("You have no rights to change class.");
                  return false;
               }

               Player var118 = var105.getPlayer();
               var118.setClassId(var94, false, false);
               var118.sendMessage("Your class has been changed by a GM");
               var118.broadcastCharInfo();
               return true;
            } catch (StringIndexOutOfBoundsException var30) {
               var4.sendMessage("You need to specify the new class id.");
               return false;
            }
         }

         if (var3.startsWith("admin_setname")) {
            try {
               String var59 = var3.substring(14);
               GameObject var93 = var4.getTarget();
               if (var93 != null && var93.isPlayer()) {
                  Player var104 = (Player)var93;
                  if (mysql.simple_get_int("count(*)", "characters", "`char_name` like '" + var59 + "'") > 0) {
                     var4.sendMessage("Name already exist.");
                     return false;
                  }

                  Log.add("Character " + var104.getName() + " renamed to " + var59 + " by GM " + var4.getName(), "renames");
                  var104.reName(var59);
                  var104.sendMessage("Your name has been changed by a GM");
                  return true;
               }

               return false;
            } catch (StringIndexOutOfBoundsException var14) {
               var4.sendMessage("You need to specify the new name.");
               return false;
            }
         }
      }

      if (!var4.getPlayerAccess().CanEditChar && !var4.getPlayerAccess().CanViewChar) {
         return false;
      }

      if (var3.equals("admin_current_player")) {
         showCharacterList(var4, null);
      } else if (var3.startsWith("admin_character_list")) {
         try {
            String var58 = var3.substring(21);
            Player var92 = GameObjectsStorage.getPlayer(var58);
            if (var92 == null && var58.length() > 6 && StringUtils.isNumeric(var58)) {
               var92 = GameObjectsStorage.getPlayer(Integer.parseInt(var58));
            }

            showCharacterList(var4, var92);
         } catch (StringIndexOutOfBoundsException var29) {
         }
      } else if (var3.startsWith("admin_show_characters_by_ip")) {
         try {
            String var57 = var3.substring(28).trim();
            String[] var91 = var57.split("\\s+");
            this.a(var4, var91[0], var91.length > 1 ? Integer.parseInt(var91[1]) : 0);
         } catch (StringIndexOutOfBoundsException var28) {
         }
      } else if (var3.startsWith("admin_show_characters_by_hwid")) {
         try {
            String var56 = var3.substring(30).trim();
            String[] var90 = var56.split("\\s+");
            this.b(var4, var90[0], var90.length > 1 ? Integer.parseInt(var90[1]) : 0);
         } catch (StringIndexOutOfBoundsException var27) {
         }
      } else if (var3.startsWith("admin_show_characters")) {
         try {
            String var55 = var3.substring(22);
            int var89 = Integer.parseInt(var55);
            this.e(var4, var89);
         } catch (StringIndexOutOfBoundsException var26) {
         }
      } else if (var3.startsWith("admin_find_character")) {
         try {
            String var54 = var3.substring(21);
            this.d(var4, var54);
         } catch (StringIndexOutOfBoundsException var25) {
            var4.sendMessage("You didnt enter a character name to find.");
            this.e(var4, 0);
         }
      } else {
         if (!var4.getPlayerAccess().CanEditChar) {
            return false;
         }

         if (var3.equals("admin_edit_character")) {
            this.p(var4);
         } else if (var3.equals("admin_character_actions")) {
            this.q(var4);
         } else if (var3.equals("admin_nokarma")) {
            this.f(var4, 0);
         } else if (var3.startsWith("admin_setkarma")) {
            try {
               String var53 = var3.substring(15);
               int var88 = Integer.parseInt(var53);
               this.f(var4, var88);
            } catch (StringIndexOutOfBoundsException var24) {
               var4.sendMessage("Please specify new karma value.");
            }
         } else if (var3.startsWith("admin_save_modifications")) {
            try {
               String var52 = var3.substring(24);
               this.c(var4, var52);
            } catch (StringIndexOutOfBoundsException var23) {
               var4.sendMessage("Error while modifying character.");
               this.e(var4, 0);
            }
         } else if (var3.equals("admin_rec")) {
            GameObject var51 = var4.getTarget();
            Player var86 = null;
            if (var51 == null || !var51.isPlayer()) {
               return false;
            }

            var86 = (Player)var51;
            var86.setGivableRec(var86.getGivableRec() + 1);
            var86.sendMessage("You have been recommended by a GM");
            var86.broadcastCharInfo();
         } else if (var3.startsWith("admin_rec")) {
            try {
               String var50 = var3.substring(10);
               int var85 = Integer.parseInt(var50);
               GameObject var103 = var4.getTarget();
               Player var116 = null;
               if (var103 == null || !var103.isPlayer()) {
                  return false;
               }

               var116 = (Player)var103;
               var116.setGivableRec(var116.getGivableRec() + var85);
               var116.sendMessage("You have been recommended by a GM");
               var116.broadcastCharInfo();
            } catch (NumberFormatException var31) {
               var4.sendMessage("Command format is //rec <number>");
            }
         } else if (var3.startsWith("admin_sethero")) {
            GameObject var49 = var4.getTarget();
            Player var84;
            if (var2.length > 1 && var2[1] != null) {
               var84 = GameObjectsStorage.getPlayer(var2[1]);
               if (var84 == null) {
                  var4.sendMessage("Character " + var2[1] + " not found in game.");
                  return false;
               }
            } else {
               if (var49 == null || !var49.isPlayer()) {
                  var4.sendMessage("You must specify the name or target character.");
                  return false;
               }

               var84 = (Player)var49;
            }

            if (var84.isHero()) {
               var84.setHero(false);
               var84.updatePledgeClass();
               var84.removeSkill(SkillTable.getInstance().getInfo(395, 1));
               var84.removeSkill(SkillTable.getInstance().getInfo(396, 1));
               var84.removeSkill(SkillTable.getInstance().getInfo(1374, 1));
               var84.removeSkill(SkillTable.getInstance().getInfo(1375, 1));
               var84.removeSkill(SkillTable.getInstance().getInfo(1376, 1));
            } else {
               var84.setHero(true);
               var84.updatePledgeClass();
               var84.addSkill(SkillTable.getInstance().getInfo(395, 1));
               var84.addSkill(SkillTable.getInstance().getInfo(396, 1));
               var84.addSkill(SkillTable.getInstance().getInfo(1374, 1));
               var84.addSkill(SkillTable.getInstance().getInfo(1375, 1));
               var84.addSkill(SkillTable.getInstance().getInfo(1376, 1));
            }

            var84.sendPacket(new SkillList(var84));
            var84.sendMessage("Admin has changed your hero status.");
            var84.broadcastUserInfo(true);
         } else if (var3.startsWith("admin_setnoble")) {
            GameObject var48 = var4.getTarget();
            Player var83;
            if (var2.length > 1 && var2[1] != null) {
               var83 = GameObjectsStorage.getPlayer(var2[1]);
               if (var83 == null) {
                  var4.sendMessage("Character " + var2[1] + " not found in game.");
                  return false;
               }
            } else {
               if (var48 == null || !var48.isPlayer()) {
                  var4.sendMessage("You must specify the name or target character.");
                  return false;
               }

               var83 = (Player)var48;
            }

            if (var83.isNoble()) {
               var83.setNoble(false);
               NoblesController.getInstance().addNoble(var83);
               var83.sendMessage("Admin changed your noble status, now you are not nobless.");
            } else {
               var83.setNoble(true);
               NoblesController.getInstance().addNoble(var83);
               var83.sendMessage("Admin changed your noble status, now you are Nobless.");
            }

            var83.updatePledgeClass();
            var83.updateNobleSkills();
            var83.sendPacket(new SkillList(var83));
            var83.broadcastUserInfo(true);
         } else if (var3.startsWith("admin_setsex")) {
            GameObject var47 = var4.getTarget();
            Player var81 = null;
            if (var47 == null || !var47.isPlayer()) {
               return false;
            }

            var81 = (Player)var47;
            var81.changeSex();
            var81.sendMessage("Your gender has been changed by a GM");
            var81.broadcastUserInfo(true);
         } else if (var3.startsWith("admin_setcolor")) {
            try {
               String var46 = var3.substring(15);
               GameObject var80 = var4.getTarget();
               Player var101 = null;
               if (var80 == null || !var80.isPlayer()) {
                  return false;
               }

               var101 = (Player)var80;
               var101.setNameColor(Integer.decode("0x" + var46));
               var101.sendMessage("Your name color has been changed by a GM");
               var101.broadcastUserInfo(true);
            } catch (StringIndexOutOfBoundsException var32) {
               var4.sendMessage("You need to specify the new color.");
            }
         } else if (var3.startsWith("admin_add_exp_sp_to_character")) {
            this.r(var4);
         } else if (var3.startsWith("admin_add_exp_sp")) {
            try {
               String var45 = var3.substring(16).trim();
               String[] var79 = var45.split(" ");
               long var100 = NumberUtils.toLong(var79[0], 0L);
               int var127 = var79.length > 1 ? NumberUtils.toInt(var79[1], 0) : 0;
               this.a(var4, var100, var127);
            } catch (Exception var22) {
               var4.sendMessage("Usage: //add_exp_sp <exp> <sp>");
            }
         } else if (var3.startsWith("admin_trans")) {
            StringTokenizer var44 = new StringTokenizer(var3);
            if (var44.countTokens() > 1) {
               var44.nextToken();
               int var77 = 0;

               try {
                  var77 = Integer.parseInt(var44.nextToken());
               } catch (Exception var21) {
                  var4.sendMessage("Specify a valid integer value.");
                  return false;
               }

               if (var77 != 0 && var4.getTransformation() != 0) {
                  var4.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                  return false;
               }

               var4.setTransformation(var77);
               var4.sendMessage("Transforming...");
            } else {
               var4.sendMessage("Usage: //trans <ID>");
            }
         } else if (var3.startsWith("admin_setsubclass")) {
            GameObject var43 = var4.getTarget();
            if (var43 == null || !var43.isPlayer()) {
               var4.sendPacket(Msg.SELECT_TARGET);
               return false;
            }

            Player var76 = (Player)var43;
            StringTokenizer var99 = new StringTokenizer(var3);
            if (var99.countTokens() > 1) {
               var99.nextToken();
               short var115 = Short.parseShort(var99.nextToken());
               if (!var76.addSubClass(var115, true)) {
                  var4.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", var4));
                  return false;
               }

               var76.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS);
            } else {
               this.a(var4, var76);
            }
         } else if (var3.startsWith("admin_setbday")) {
            String var42 = "Usage: //setbday YYYY-MM-DD";
            String var75 = var3.substring(14);
            if (var75.length() != 10 || !Util.isMatchingRegexp(var75, "[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
               var4.sendMessage(var42);
               return false;
            }

            SimpleDateFormat var98 = new SimpleDateFormat("yyyy-MM-dd");

            try {
               var98.parse(var75);
            } catch (ParseException var20) {
               var4.sendMessage(var42);
            }

            if (var4.getTarget() == null || !var4.getTarget().isPlayer()) {
               var4.sendMessage("Please select a character.");
               return false;
            }

            if (!mysql.set("update characters set createtime = UNIX_TIMESTAMP('" + var75 + "') where obj_Id = " + var4.getTarget().getObjectId())) {
               var4.sendMessage(var42);
               return false;
            }

            var4.sendMessage("New Birthday for " + var4.getTarget().getName() + ": " + var75);
            var4.getTarget().getPlayer().sendMessage("Admin changed your birthday to: " + var75);
         } else {
            if (var3.startsWith("admin_give_all_by_ip")) {
               if (var2.length >= 3) {
                  long var73 = 0L;

                  ItemTemplate var41;
                  try {
                     var41 = ItemHolder.getInstance().getTemplate(Integer.parseInt(var2[1]));
                     var73 = Long.parseLong(var2[2]);
                  } catch (NumberFormatException var15) {
                     var4.sendMessage("only numbers");
                     return false;
                  }

                  HashMap var114 = new HashMap();

                  for (Player var132 : GameObjectsStorage.getAllPlayersForIterate()) {
                     if (var132 != null && !var132.isInOfflineMode() && !var132.isLogoutStarted()) {
                        var114.putIfAbsent(var132.getIP(), var132.getRef());
                     }
                  }

                  for (Object __raw_var133_439 : (Iterable<?>)(Object)(var114.values())) {
                     HardReference var133 = (HardReference)__raw_var133_439;
                     Player var135 = (Player)var133.get();
                     if (var135 != null && var135.getInventory().validateWeight(var41, var73) && var135.getInventory().validateCapacity(var41, var73)) {
                        Functions.addItem(var135, var41.getItemId(), var73);
                        var135.sendMessage(
                           var135.isLangRus()
                              ? "Вы были вознаграждены! " + var41.getName() + " " + var73
                              : "You have been rewarded! " + var41.getName() + " " + var73
                        );
                     }
                  }

                  return true;
               }

               var4.sendMessage("use: //give_all_by_ip itemId count");
               return false;
            }

            if (var3.startsWith("admin_give_all_by_hwid")) {
               if (var2.length >= 3) {
                  long var71 = 0L;

                  ItemTemplate var40;
                  try {
                     var40 = ItemHolder.getInstance().getTemplate(Integer.parseInt(var2[1]));
                     var71 = Long.parseLong(var2[2]);
                  } catch (NumberFormatException var16) {
                     var4.sendMessage("only numbers");
                     return false;
                  }

                  HashMap var113 = new HashMap();

                  for (Player var130 : GameObjectsStorage.getAllPlayersForIterate()) {
                     if (var130 != null && var130.getNetConnection().getHwid() != null && !var130.isInOfflineMode() && !var130.isLogoutStarted()) {
                        var113.putIfAbsent(var130.getNetConnection().getHwid(), var130.getRef());
                     }
                  }

                  for (Object __raw_var131_479 : (Iterable<?>)(Object)(var113.values())) {
                     HardReference var131 = (HardReference)__raw_var131_479;
                     Player var134 = (Player)var131.get();
                     if (var134 != null && var134.getInventory().validateWeight(var40, var71) && var134.getInventory().validateCapacity(var40, var71)) {
                        Functions.addItem(var134, var40.getItemId(), var71);
                        var134.sendMessage(
                           var134.isLangRus()
                              ? "Вы были вознаграждены! " + var40.getName() + " " + var71
                              : "You have been rewarded! " + var40.getName() + " " + var71
                        );
                     }
                  }

                  return true;
               }

               var4.sendMessage("use: //give_all_by_hwid itemId count");
               return false;
            }

            if (var3.startsWith("admin_give_all_radius")) {
               if (var2.length >= 3) {
                  long var69 = 0L;
                  int var111 = 0;

                  ItemTemplate var39;
                  try {
                     var39 = ItemHolder.getInstance().getTemplate(Integer.parseInt(var2[1]));
                     var69 = Long.parseLong(var2[2]);
                     var111 = Integer.parseInt(var2[3]);
                  } catch (Exception var17) {
                     var4.sendMessage("use: //give_all_radius [itemId] [count] [radius] > only numbers");
                     return false;
                  }

                  for (Player var129 : World.getAroundPlayers(var4, var111, 200)) {
                     if (var129 != null
                        && !var129.isInOfflineMode()
                        && !var129.isLogoutStarted()
                        && var129.getInventory().validateWeight(var39, var69)
                        && var129.getInventory().validateCapacity(var39, var69)
                        && var129 != var4) {
                        Functions.addItem(var129, var39.getItemId(), var69);
                        var129.sendMessage(
                           var129.isLangRus()
                              ? "Вы были вознаграждены! " + var39.getName() + " " + var69
                              : "You have been rewarded! " + var39.getName() + " " + var69
                        );
                        var4.sendMessage("You make reward " + var69 + " " + var39.getName() + " for all players at radius " + var111);
                     }
                  }

                  return true;
               }

               var4.sendMessage("use: //give_all_radius [itemId] [count] [radius]");
               return false;
            }

            if (var3.startsWith("admin_give_all")) {
               if (var2.length >= 3) {
                  long var67 = 0L;

                  ItemTemplate var38;
                  try {
                     var38 = ItemHolder.getInstance().getTemplate(Integer.parseInt(var2[1]));
                     var67 = Long.parseLong(var2[2]);
                  } catch (NumberFormatException var18) {
                     var4.sendMessage("only numbers");
                     return false;
                  }

                  for (Player var121 : GameObjectsStorage.getAllPlayersForIterate()) {
                     if (var121 != null
                        && !var121.isInOfflineMode()
                        && !var121.isLogoutStarted()
                        && var121.getInventory().validateWeight(var38, var67)
                        && var121.getInventory().validateCapacity(var38, var67)) {
                        Functions.addItem(var121, var38.getItemId(), var67);
                        var121.sendMessage(
                           var121.isLangRus()
                              ? "Вы были вознаграждены! " + var38.getName() + " " + var67
                              : "You have been rewarded! " + var38.getName() + " " + var67
                        );
                     }
                  }

                  return true;
               }

               var4.sendMessage("use: //give_all itemId count");
               return false;
            }

            if (var3.startsWith("admin_give_item")) {
               if (var2.length < 3) {
                  var4.sendMessage("Usage: //give_item id count <target>");
                  return false;
               }

               int var37 = Integer.parseInt(var2[1]);
               int var66 = Integer.parseInt(var2[2]);
               if (var37 < 1 || var66 < 1 || var4.getTarget() == null || !var4.getTarget().isPlayer()) {
                  var4.sendMessage("Usage: //give_item id count <target>");
                  return false;
               }

               ItemFunctions.addItem(var4.getTarget().getPlayer(), var37, var66, true);
            } else if (!var3.startsWith("admin_set_pa")) {
               if (var3.startsWith("admin_remove_item")) {
                  Player var33 = null;
                  boolean var62 = false;
                  if (var2.length >= 3) {
                     int var96 = Integer.parseInt(var2[1]);
                     int var108 = Integer.parseInt(var2[2]);
                     if (var2.length > 3) {
                        var33 = World.getPlayer(var2[3]);
                     }

                     if (var33 == null && var4.getTarget() != null) {
                        var33 = var4.getTarget().getPlayer();
                     }

                     if (var33 != null && var96 > 0 && var108 > 0) {
                        long var119 = ItemFunctions.getItemCount(var33, var96);
                        if (var119 < var108) {
                           var62 = true;
                           var4.sendMessage("Failed: '" + var33.getName() + "' have only " + var119 + " items.");
                        } else {
                           var62 = true;
                           var4.sendMessage("Removed " + ItemFunctions.removeItem(var33, var96, var108, true) + " from '" + var33.getName() + "'");
                        }
                     }
                  }

                  if (!var62) {
                     var4.sendMessage("Usage: //remove_item id count <target>");
                     return false;
                  }
               } else if (var3.startsWith("admin_destroy_items")) {
                  Player var34 = null;
                  boolean var63 = false;
                  if (var2.length >= 1) {
                     if (var2.length > 1) {
                        var34 = World.getPlayer(var2[1]);
                     }

                     if (var34 == null && var4.getTarget() != null) {
                        var34 = var4.getTarget().getPlayer();
                     }

                     if (var34 != null) {
                        for (ItemInstance var128 : var4.getInventory().getItems()) {
                           var4.getInventory().destroyItem(var128);
                           var4.sendPacket(SystemMessage2.removeItems(var128.getItemId(), var128.getCount()));
                        }
                     } else {
                        var63 = true;
                        var4.sendMessage("Usage: //destro_items <target>");
                     }
                  }

                  if (!var63) {
                     var4.sendMessage("WARNING! ALL ITEMS REMOVED FROM " + var4.getName());
                     return false;
                  }
               } else if (var3.startsWith("admin_add_bang")) {
                  if (!Config.ALT_PCBANG_POINTS_ENABLED) {
                     var4.sendMessage("Error! Pc Bang Points service disabled!");
                     return true;
                  }

                  if (var2.length < 1) {
                     var4.sendMessage("Usage: //add_bang count <target>");
                     return false;
                  }

                  int var35 = Integer.parseInt(var2[1]);
                  if (var35 < 1 || var4.getTarget() == null || !var4.getTarget().isPlayer()) {
                     var4.sendMessage("Usage: //add_bang count <target>");
                     return false;
                  }

                  Player var64 = var4.getTarget().getPlayer();
                  var64.addPcBangPoints(var35, false);
                  var4.sendMessage("You have added " + var35 + " Pc Bang Points to " + var64.getName());
               } else if (var3.startsWith("admin_set_bang")) {
                  if (!Config.ALT_PCBANG_POINTS_ENABLED) {
                     var4.sendMessage("Error! Pc Bang Points service disabled!");
                     return true;
                  }

                  if (var2.length < 1) {
                     var4.sendMessage("Usage: //set_bang count <target>");
                     return false;
                  }

                  int var36 = Integer.parseInt(var2[1]);
                  if (var36 < 1 || var4.getTarget() == null || !var4.getTarget().isPlayer()) {
                     var4.sendMessage("Usage: //set_bang count <target>");
                     return false;
                  }

                  Player var65 = var4.getTarget().getPlayer();
                  var65.setPcBangPoints(var36);
                  var65.sendMessage("Your Pc Bang Points count is now " + var36);
                  var65.sendPacket(new ExPCCafePointInfo(var65, var36, 1, 2, 12));
                  var4.sendMessage("You have set " + var65.getName() + "'s Pc Bang Points to " + var36);
               }
            } else {
               if (!Config.SERVICES_RATE_ENABLED) {
                  var4.sendMessage("Service Premium Account is Disabled");
                  return false;
               }

               if (var2.length < 2) {
                  var4.sendMessage("USAGE: //set_pa <pa_id> <target>");
                  return false;
               }

               Player var6 = null;

               int var7;
               try {
                  var7 = Integer.parseInt(var2[1]);
               } catch (Exception var19) {
                  var4.sendMessage("PA id unspecified.");
                  return false;
               }

               if (var2.length > 2) {
                  var6 = World.getPlayer(var2[2]);
                  if (var6 == null) {
                     var4.sendMessage("Target \"" + var2[2] + "\" not found.");
                     return false;
                  }
               }

               if (var6 == null && var4.getTarget() != null) {
                  var6 = var4.getTarget().getPlayer();
               }

               if (var6 == null) {
                  var4.sendMessage("Target is unspecified.");
                  return false;
               }

               Config.RateBonusInfo var8 = null;

               for (Config.RateBonusInfo var12 : Config.SERVICES_RATE_BONUS_INFO) {
                  if (var12.id == var7) {
                     var8 = var12;
                  }
               }

               if (var8 == null) {
                  var4.sendMessage("Undefined bonus!");
                  return false;
               }

               if (var7 < 1 || var4.getTarget() == null || !var4.getTarget().isPlayer()) {
                  var4.sendMessage("Please select a character.");
                  return false;
               }

               AccountBonusDAO.getInstance().store(var6.getAccountName(), var8.makeBonus());
               var6.stopBonusTask();
               var6.startBonusTask();
               if (var6.getParty() != null) {
                  var6.getParty().recalculatePartyData();
               }

               var6.broadcastUserInfo(true);
               Log.add(
                  "Admin Command PA Bonus added " + var6.getName() + "|" + var6.getObjectId() + "|rate bonus|" + var8.id + "|" + var8.bonusTimeSeconds + "|",
                  "services"
               );
               var4.sendMessage("SYS: Premium Account added for " + var6.getName() + " id bonus is " + var8.id);
            }
         }
      }

      return true;
   }

   @Override
   public Enum[] getAdminCommandEnum() {
      return AdminEditChar.Commands.values();
   }

   private void a(Player var1, String var2, int var3) {
      List var4 = GameObjectsStorage.getAllPlayers();
      LinkedList var5 = new LinkedList();

      for (Object __raw_var7_772 : (Iterable<?>)(Object)(var4)) {
         Player var7 = (Player)__raw_var7_772;
         if (var7 != null && !var7.isInOfflineMode() && var7.isConnected() && var7.getNetConnection() != null) {
            String var8 = var7.getNetConnection().getIpAddr();
            if (var8 != null && var2.trim().equals(var8)) {
               var5.add(var7);
            }
         }
      }

      byte var14 = 20;
      int var15 = var5.size() / var14;
      if (var5.size() > var14 * var15) {
         var15++;
      }

      if (var3 > var15) {
         var3 = var15;
      }

      int var16 = var14 * var3;
      int var9 = var5.size();
      if (var9 - var16 > var14) {
         var9 = var16 + var14;
      }

      NpcHtmlMessage var10 = new NpcHtmlMessage(5);
      StringBuilder var11 = new StringBuilder("<html><body>");
      var11.append("<table width=260><tr>");
      var11.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var11.append("<td width=180><center>Character Selection Menu</center></td>");
      var11.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var11.append("</tr></table>");
      var11.append("<br><br>");
      var11.append("<center>Characters with IP \"").append(var2).append("\"</center>");

      for (int var12 = 0; var12 < var15; var12++) {
         int var13 = var12 + 1;
         var11.append("<center><a action=\"bypass -h admin_show_characters_by_ip " + var2 + " " + var12 + "\">Page " + var13 + "</a></center>");
      }

      var11.append("<br>");
      var11.append("<table width=270>");
      var11.append("<tr><td width=80>Name:</td><td width=110>Class:</td><td width=40>Level:</td></tr>");

      for (int var17 = var16; var17 < var9; var17++) {
         Player var18 = (Player)var5.get(var17);
         var11.append(
            "<tr><td width=80><a action=\"bypass -h admin_character_list "
               + var18.getObjectId()
               + "\">"
               + var18.getName()
               + "</a></td><td width=110>"
               + var18.getTemplate().className
               + "</td><td width=40>"
               + var18.getLevel()
               + "</td></tr>"
         );
      }

      var11.append("</table>");
      var11.append("</body></html>");
      var10.setHtml(var11.toString());
      var1.sendPacket(var10);
   }

   private void b(Player var1, String var2, int var3) {
      List var4 = GameObjectsStorage.getAllPlayers();
      LinkedList var5 = new LinkedList();

      for (Object __raw_var7_841 : (Iterable<?>)(Object)(var4)) {
         Player var7 = (Player)__raw_var7_841;
         if (var7 != null && var7.getNetConnection() != null && !var7.isInOfflineMode() && var7.isConnected()) {
            String var8 = var7.getNetConnection().getHwid();
            if (var8 != null && var2.trim().equals(var8)) {
               var5.add(var7);
            }
         }
      }

      byte var14 = 20;
      int var15 = var5.size() / var14;
      if (var5.size() > var14 * var15) {
         var15++;
      }

      if (var3 > var15) {
         var3 = var15;
      }

      int var16 = var14 * var3;
      int var9 = var5.size();
      if (var9 - var16 > var14) {
         var9 = var16 + var14;
      }

      NpcHtmlMessage var10 = new NpcHtmlMessage(5);
      StringBuilder var11 = new StringBuilder("<html><body>");
      var11.append("<table width=260><tr>");
      var11.append(
         "<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>"
      );
      var11.append("<td width=180><center>Character Selection Menu</center></td>");
      var11.append(
         "<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>"
      );
      var11.append("</tr></table>");
      var11.append("<br><br>");
      var11.append("<center>Characters with HIWD \"").append(var2).append("\"</center>");

      for (int var12 = 0; var12 < var15; var12++) {
         int var13 = var12 + 1;
         var11.append("<center><a action=\"bypass -h admin_show_characters_by_hwid " + var2 + " " + var12 + "\">Page " + var13 + "</a></center>");
      }

      var11.append("<br>");
      var11.append("<table width=270>");
      var11.append("<tr><td width=80>Name:</td><td width=110>Class:</td><td width=40>Level:</td></tr>");

      for (int var17 = var16; var17 < var9; var17++) {
         Player var18 = (Player)var5.get(var17);
         var11.append(
            "<tr><td width=80><a action=\"bypass -h admin_character_list "
               + var18.getObjectId()
               + "\">"
               + var18.getName()
               + "</a></td><td width=110>"
               + var18.getTemplate().className
               + "</td><td width=40>"
               + var18.getLevel()
               + "</td></tr>"
         );
      }

      var11.append("</table>");
      var11.append("</body></html>");
      var10.setHtml(var11.toString());
      var1.sendPacket(var10);
   }

   private void e(Player var1, int var2) {
      List var3 = GameObjectsStorage.getAllPlayers();
      byte var4 = 20;
      int var5 = var3.size() / var4;
      if (var3.size() > var4 * var5) {
         var5++;
      }

      var2 = Math.min(var2, var5);
      int var6 = var4 * var2;
      int var7 = var3.size();
      if (var7 - var6 > var4) {
         var7 = var6 + var4;
      }

      NpcHtmlMessage var8 = new NpcHtmlMessage(5);
      StringBuilder var9 = new StringBuilder("<html><body>");
      var9.append("<table width=260><tr>");
      var9.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var9.append("<td width=180><center>Character Selection Menu</center></td>");
      var9.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var9.append("</tr></table>");
      var9.append("<br><br>");
      var9.append("<table width=270>");
      var9.append("<tr><td width=270>You can find a character by writing his name and</td></tr>");
      var9.append("<tr><td width=270>clicking Find bellow.<br></td></tr>");
      var9.append("<tr><td width=270>Note: Names should be written case sensitive.</td></tr>");
      var9.append("</table><br>");
      var9.append("<center><table><tr><td>");
      var9.append(
         "<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\">"
      );
      var9.append("</td></tr></table></center><br><br>");

      for (int var10 = 0; var10 < var5; var10++) {
         if (var10 == 0 || var10 > var2 - 10 && var10 < var2 + 10 || var10 + 1 == var5) {
            int var11 = var10 + 1;
            var9.append("<center><a action=\"bypass -h admin_show_characters " + var10 + "\">Page " + var11 + "</a></center>");
         }
      }

      var9.append("<br>");
      var9.append("<table width=270>");
      var9.append("<tr><td width=80>Name:</td><td width=110>Class:</td><td width=40>Level:</td></tr>");

      for (int var13 = var6; var13 < var7; var13++) {
         Player var14 = (Player)var3.get(var13);
         var9.append(
            "<tr><td width=80><a action=\"bypass -h admin_character_list "
               + var14.getObjectId()
               + "\">"
               + var14.getName()
               + "</a></td><td width=110>"
               + var14.getTemplate().className
               + "</td><td width=40>"
               + var14.getLevel()
               + "</td></tr>"
         );
      }

      var9.append("</table>");
      var9.append("</body></html>");
      var8.setHtml(var9.toString());
      var1.sendPacket(var8);
   }

   public static void showCharacterList(Player var0, Player var1) {
      if (var1 == null) {
         GameObject var2 = var0.getTarget();
         if (var2 == null || !var2.isPlayer()) {
            return;
         }

         var1 = (Player)var2;
      } else {
         var0.setTarget(var1);
         var0.sendPacket(new MyTargetSelected(var1.getObjectId(), 0));
      }

      AutoFarmContext var8 = var1.getFarmSystem();
      String var3 = "No Clan";
      if (var1.getClan() != null) {
         var3 = var1.getClan().getName() + "/" + var1.getClan().getLevel();
      }

      NumberFormat var4 = NumberFormat.getNumberInstance(Locale.ENGLISH);
      var4.setMaximumFractionDigits(4);
      var4.setMinimumFractionDigits(1);
      NpcHtmlMessage var5 = new NpcHtmlMessage(5);
      StringBuilder var6 = new StringBuilder("<html><body>");
      var6.append("<table width=260><tr>");
      var6.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var6.append("<td width=180><center>Character Selection Menu</center></td>");
      var6.append(
         "<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>"
      );
      var6.append("</tr></table><br1>");
      var6.append("<table width=270>");
      if (var0.getPlayerAccess().CanSeeIp) {
         var6.append(
            "<tr><td width=100>Account/IP:</td><td>"
               + var1.getAccountName()
               + "/<a action=\"bypass -h admin_show_characters_by_ip "
               + var1.getIP()
               + "\">"
               + var1.getIP()
               + "</a></td></tr>"
         );
      } else {
         var6.append("<tr><td width=100>Account:</td><td>" + var1.getAccountName() + "</td></tr>");
      }

      if (var1.getNetConnection() != null) {
         if (var1.getNetConnection().getHwid() != null && !var1.getNetConnection().getHwid().isEmpty() && var0.getPlayerAccess().CanSeeHwid) {
            String var7 = var1.getNetConnection().getHwid();
            var6.append("<tr><td width=100>HWID:</td><td><a action=\"bypass -h admin_show_characters_by_hwid " + var7 + "\">" + var7 + "</a></td></tr>");
         }

         int var9 = var1.getNetConnection().getFps();
         if (var9 > 0) {
            var6.append("<tr><td width=100>FPS:</td><td>" + var9 + "</td></tr>");
         }
      }

      var6.append("<tr><td width=100>Name/Level:</td><td>" + var1.getName() + "/" + var1.getLevel() + "</td></tr>");
      var6.append("<tr><td width=100>Class/Id:</td><td>" + var1.getTemplate().className + "/" + var1.getClassId().getId() + "</td></tr>");
      var6.append("<tr><td width=100>Clan/Level:</td><td>" + var3 + "</td></tr>");
      var6.append("<tr><td width=100>Exp/Sp:</td><td>" + var1.getExp() + "/" + var1.getSp() + "</td></tr>");
      var6.append("<tr><td width=100>Cur/Max Hp:</td><td>" + (int)var1.getCurrentHp() + "/" + var1.getMaxHp() + "</td></tr>");
      var6.append("<tr><td width=100>Cur/Max Mp:</td><td>" + (int)var1.getCurrentMp() + "/" + var1.getMaxMp() + "</td></tr>");
      var6.append("<tr><td width=100>Cur/Max Load:</td><td>" + var1.getCurrentLoad() + "/" + var1.getMaxLoad() + "</td></tr>");
      var6.append("<tr><td width=100>Patk/Matk:</td><td>" + var1.getPAtk(null) + "/" + var1.getMAtk(null, null) + "</td></tr>");
      var6.append("<tr><td width=100>Pdef/Mdef:</td><td>" + var1.getPDef(null) + "/" + var1.getMDef(null, null) + "</td></tr>");
      var6.append("<tr><td width=100>PAtkSpd/MAtkSpd:</td><td>" + var1.getPAtkSpd() + "/" + var1.getMAtkSpd() + "</td></tr>");
      var6.append("<tr><td width=100>Acc/Evas:</td><td>" + var1.getAccuracy() + "/" + var1.getEvasionRate(null) + "</td></tr>");
      var6.append(
         "<tr><td width=100>Crit/MCrit:</td><td>" + var1.getCriticalHit(null, null) + "/" + var4.format(var1.getMagicCriticalRate(null, null)) + "%</td></tr>"
      );
      var6.append("<tr><td width=100>Walk/Run:</td><td>" + var1.getWalkSpeed() + "/" + var1.getRunSpeed() + "</td></tr>");
      var6.append("<tr><td width=100>PvP/PK:</td><td>" + var1.getPvpKills() + "/" + var1.getPkKills() + "</td></tr>");
      var6.append("<tr><td width=100>Coordinates:</td><td>" + var1.getX() + "," + var1.getY() + "," + var1.getZ() + "</td></tr>");
      var6.append("<tr><td width=100>AI:</td><td>" + var1.getAI().getIntention() + "/" + var1.getAI().getNextAction() + "</td></tr>");
      var6.append("<tr><td width=100>Direction:</td><td>" + PositionUtils.getDirectionTo(var1, var0) + "</td></tr>");
      if (Config.ALLOW_AUTO_FARM) {
         var6.append("<tr><td width=100>AutoFarm State:</td><td>" + (var8 != null && var8.isAutofarming() ? "Active" : "Not Active") + "</td></tr>");
      }

      var6.append("</table><br1>");
      var6.append("<table<tr>");
      var6.append("<td><button value=\"Skills\" action=\"bypass -h admin_show_skills\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var6.append("<td><button value=\"Effects\" action=\"bypass -h admin_show_effects\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var6.append("<td><button value=\"Actions\" action=\"bypass -h admin_character_actions\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var6.append("</tr><tr>");
      var6.append("<td><button value=\"Stats\" action=\"bypass -h admin_edit_character\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var6.append(
         "<td><button value=\"Exp & Sp\" action=\"bypass -h admin_add_exp_sp_to_character\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>"
      );
      var6.append("<td></td>");
      var6.append("</tr></table></body></html>");
      var5.setHtml(var6.toString());
      var0.sendPacket(var5);
   }

   private void f(Player var1, int var2) {
      GameObject var3 = var1.getTarget();
      if (var3 == null) {
         var1.sendPacket(Msg.INVALID_TARGET);
      } else if (var3.isPlayer()) {
         Player var4 = (Player)var3;
         if (var2 >= 0) {
            int var5 = var4.getKarma();
            var4.setKarma(var2, true);
            var4.sendMessage("Admin has changed your karma from " + var5 + " to " + var2 + ".");
            var1.sendMessage("Successfully Changed karma for " + var4.getName() + " from (" + var5 + ") to (" + var2 + ").");
         } else {
            var1.sendMessage("You must enter a value for karma greater than or equal to 0.");
         }
      }
   }

   private void c(Player var1, String var2) {
      GameObject var3 = var1.getTarget();
      if (var3 != null && var3.isPlayer()) {
         Player var4 = (Player)var3;
         String[] var5 = var2.split("&");
         Integer[] var6 = new Integer[var5.length];

         for (int var7 = 0; var7 < var5.length; var7++) {
            var5[var7] = var5[var7].trim();
            var6[var7] = var5[var7].isEmpty() ? null : Integer.valueOf(var5[var7]);
         }

         if (var6[0] != null) {
            var4.setCurrentHp(var6[0].intValue(), false);
         }

         if (var6[1] != null) {
            var4.setCurrentMp(var6[1].intValue());
         }

         if (var6[2] != null) {
            var4.setKarma(var6[2], true);
         }

         if (var6[3] != null) {
            var4.setPvpFlag(var6[3]);
         }

         if (var6[4] != null) {
            var4.setPvpKills(var6[4]);
         }

         if (var6[5] != null) {
            var4.setClassId(var6[5], true, false);
         }

         this.p(var1);
         var4.broadcastCharInfo();
         var4.decayMe();
         var4.spawnMe(var1.getLoc());
      } else {
         var1.sendPacket(Msg.SELECT_TARGET);
      }
   }

   private void p(Player var1) {
      GameObject var2 = var1.getTarget();
      if (var2 != null && var2.isPlayer()) {
         Player var3 = (Player)var2;
         NpcHtmlMessage var4 = new NpcHtmlMessage(5);
         StringBuilder var5 = new StringBuilder("<html><body>");
         var5.append("<table width=260><tr>");
         var5.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
         var5.append("<td width=180><center>Character Selection Menu</center></td>");
         var5.append(
            "<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>"
         );
         var5.append("</tr></table>");
         var5.append("<br><br>");
         var5.append("<center>Editing character: " + var3.getName() + "</center><br>");
         var5.append("<table width=250>");
         var5.append("<tr><td width=40></td><td width=70>Curent:</td><td width=70>Max:</td><td width=70></td></tr>");
         var5.append(
            "<tr><td width=40>HP:</td><td width=70>"
               + var3.getCurrentHp()
               + "</td><td width=70>"
               + var3.getMaxHp()
               + "</td><td width=70>Karma: "
               + var3.getKarma()
               + "</td></tr>"
         );
         var5.append(
            "<tr><td width=40>MP:</td><td width=70>"
               + var3.getCurrentMp()
               + "</td><td width=70>"
               + var3.getMaxMp()
               + "</td><td width=70>Pvp Kills: "
               + var3.getPvpKills()
               + "</td></tr>"
         );
         var5.append(
            "<tr><td width=40>Load:</td><td width=70>"
               + var3.getCurrentLoad()
               + "</td><td width=70>"
               + var3.getMaxLoad()
               + "</td><td width=70>Pvp Flag: "
               + var3.getPvpFlag()
               + "</td></tr>"
         );
         var5.append("</table>");
         var5.append("<table width=270><tr><td>Class<?> Template Id: " + var3.getClassId() + "/" + var3.getClassId().getId() + "</td></tr></table><br>");
         var5.append("<table width=270>");
         var5.append("<tr><td>Note: Fill all values before saving the modifications.</td></tr>");
         var5.append("</table><br>");
         var5.append("<table width=270>");
         var5.append("<tr><td width=50>Hp:</td><td><edit var=\"hp\" width=50></td><td width=50>Mp:</td><td><edit var=\"mp\" width=50></td></tr>");
         var5.append(
            "<tr><td width=50>Pvp Flag:</td><td><edit var=\"pvpflag\" width=50></td><td width=50>Karma:</td><td><edit var=\"karma\" width=50></td></tr>"
         );
         var5.append(
            "<tr><td width=50>Class<?> Id:</td><td><edit var=\"classid\" width=50></td><td width=50>Pvp Kills:</td><td><edit var=\"pvpkills\" width=50></td></tr>"
         );
         var5.append("</table><br>");
         var5.append(
            "<center><button value=\"Save Changes\" action=\"bypass -h admin_save_modifications $hp & $mp & $karma & $pvpflag & $pvpkills & $classid &\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></center><br>"
         );
         var5.append("</body></html>");
         var4.setHtml(var5.toString());
         var1.sendPacket(var4);
      } else {
         var1.sendPacket(Msg.SELECT_TARGET);
      }
   }

   private void q(Player var1) {
      GameObject var2 = var1.getTarget();
      if (var2 != null && var2.isPlayer()) {
         Player var3 = (Player)var2;
         NpcHtmlMessage var4 = new NpcHtmlMessage(5);
         StringBuilder var5 = new StringBuilder("<html><body>");
         var5.append("<table width=260><tr>");
         var5.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
         var5.append("<td width=180><center>Character Selection Menu</center></td>");
         var5.append(
            "<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>"
         );
         var5.append("</tr></table><br><br>");
         var5.append("<center>Admin Actions for: " + var3.getName() + "</center><br>");
         var5.append("<center><table width=200><tr>");
         var5.append("<td width=100>Argument(*):</td><td width=100><edit var=\"arg\" width=100></td>");
         var5.append("</tr></table><br></center>");
         var5.append("<table width=270>");
         var5.append(
            "<tr><td width=90><button value=\"Teleport\" action=\"bypass -h admin_teleportto "
               + var3.getName()
               + "\" width=85 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>"
         );
         var5.append(
            "<td width=90><button value=\"Recall\" action=\"bypass -h admin_recall "
               + var3.getName()
               + "\" width=85 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>"
         );
         var5.append(
            "<td width=90><button value=\"Quests\" action=\"bypass -h admin_quests "
               + var3.getName()
               + "\" width=85 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>"
         );
         var5.append("</body></html>");
         var4.setHtml(var5.toString());
         var1.sendPacket(var4);
      }
   }

   private void d(Player var1, String var2) {
      NpcHtmlMessage var3 = new NpcHtmlMessage(5);
      int var4 = 0;
      StringBuilder var5 = new StringBuilder("<html><body>");
      var5.append("<table width=260><tr>");
      var5.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var5.append("<td width=180><center>Character Selection Menu</center></td>");
      var5.append(
         "<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>"
      );
      var5.append("</tr></table>");
      var5.append("<br><br>");
      String var6 = var2.toLowerCase();

      for (Player var8 : GameObjectsStorage.getAllPlayersForIterate()) {
         if (var8.getName() != null && var8.getName().toLowerCase().startsWith(var6)) {
            var4++;
            var5.append("<table width=270>");
            var5.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td></tr>");
            var5.append(
               "<tr><td width=80><a action=\"bypass -h admin_character_list "
                  + var8.getObjectId()
                  + "\">"
                  + var8.getName()
                  + "</a></td><td width=110>"
                  + var8.getTemplate().className
                  + "</td><td width=40>"
                  + var8.getLevel()
                  + "</td></tr>"
            );
            var5.append("</table>");
         }
      }

      if (var4 == 0) {
         var5.append("<table width=270>");
         var5.append("<tr><td width=270>Your search did not find any characters.</td></tr>");
         var5.append("<tr><td width=270>Please try again.<br></td></tr>");
         var5.append("</table><br>");
         var5.append("<center><table><tr><td>");
         var5.append(
            "<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\">"
         );
         var5.append("</td></tr></table></center>");
      } else {
         var5.append("<center><br>Found " + var4 + " character");
         if (var4 == 1) {
            var5.append(".");
         } else if (var4 > 1) {
            var5.append("s.");
         }
      }

      var5.append("</center></body></html>");
      var3.setHtml(var5.toString());
      var1.sendPacket(var3);
   }

   private void r(Player var1) {
      GameObject var2 = var1.getTarget();
      if (var2 == null || !var2.isPlayer() || var1 != var2 && !var1.getPlayerAccess().CanEditCharAll) {
         var1.sendPacket(Msg.INVALID_TARGET);
      } else {
         Player var3 = (Player)var2;
         NpcHtmlMessage var4 = new NpcHtmlMessage(5);
         StringBuilder var5 = new StringBuilder("<html><body>");
         var5.append("<table width=260><tr>");
         var5.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
         var5.append("<td width=180><center>Character Selection Menu</center></td>");
         var5.append(
            "<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>"
         );
         var5.append("</tr></table>");
         var5.append("<br><br>");
         var5.append("<table width=270><tr><td>Name: " + var3.getName() + "</td></tr>");
         var5.append("<tr><td>Lv: " + var3.getLevel() + " " + var3.getTemplate().className + "</td></tr>");
         var5.append("<tr><td>Exp: " + var3.getExp() + "</td></tr>");
         var5.append("<tr><td>Sp: " + var3.getSp() + "</td></tr></table>");
         var5.append("<br><table width=270><tr><td>Note: Dont forget that modifying players skills can</td></tr>");
         var5.append("<tr><td>ruin the game...</td></tr></table><br>");
         var5.append("<table width=270><tr><td>Note: Fill all values before saving the modifications.,</td></tr>");
         var5.append("<tr><td>Note: Use 0 if no changes are needed.</td></tr></table><br>");
         var5.append("<center><table><tr>");
         var5.append("<td>Exp: <edit var=\"exp_to_add\" width=50></td>");
         var5.append("<td>Sp:  <edit var=\"sp_to_add\" width=50></td>");
         var5.append(
            "<td>&nbsp;<button value=\"Save Changes\" action=\"bypass -h admin_add_exp_sp $exp_to_add $sp_to_add\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>"
         );
         var5.append("</tr></table></center>");
         var5.append("</body></html>");
         var4.setHtml(var5.toString());
         var1.sendPacket(var4);
      }
   }

   private void a(Player var1, long var2, int var4) {
      if (!var1.getPlayerAccess().CanEditCharAll) {
         var1.sendMessage("You have not enough privileges, for use this function.");
      } else {
         GameObject var5 = var1.getTarget();
         if (var5 == null) {
            var1.sendPacket(Msg.SELECT_TARGET);
         } else if (!var5.isPlayable()) {
            var1.sendPacket(Msg.INVALID_TARGET);
         } else {
            Playable var6 = (Playable)var5;
            var6.addExpAndSp(var2, var4);
            var1.sendMessage("Added " + var2 + " experience and " + var4 + " SP to " + var6.getName() + ".");
         }
      }
   }

   private void a(Player var1, Player var2) {
      StringBuilder var3 = new StringBuilder("<html><body>");
      NpcHtmlMessage var4 = new NpcHtmlMessage(5);
      Set var5 = this.s(var2);
      if (var5 != null && !var5.isEmpty()) {
         var3.append("Add Subclass:<br>Which subclass do you wish to add?<br>");

         for (Object __raw_var7_1362 : (Iterable<?>)(Object)(var5)) {
            PlayerClass var7 = (PlayerClass)__raw_var7_1362;
            var3.append("<a action=\"bypass -h admin_setsubclass " + var7.ordinal() + "\">" + this.a(var7) + "</a><br>");
         }

         var3.append("</body></html>");
         var4.setHtml(var3.toString());
         var1.sendPacket(var4);
      } else {
         var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", var1));
      }
   }

   private Set<PlayerClass> s(Player var1) {
      int var2 = var1.getBaseClassId();
      PlayerClass var3 = PlayerClass.values()[var2];
      Set var4 = var3.getAvailableSubclasses();
      if (var4 == null) {
         return null;
      }

      var4.remove(var3);

      for (Object __raw_var6_1384 : (Iterable<?>)(Object)(var4)) {
         PlayerClass var6 = (PlayerClass)__raw_var6_1384;
         for (SubClass var8 : var1.getSubClasses().values()) {
            if (var6.ordinal() == var8.getClassId()) {
               var4.remove(var6);
            } else {
               ClassId var9 = ClassId.VALUES[var6.ordinal()].getParent(var1.getSex());
               if (var9 != null && var9.getId() == var8.getClassId()) {
                  var4.remove(var6);
               } else {
                  ClassId var10 = ClassId.VALUES[var8.getClassId()].getParent(var1.getSex());
                  if (var10 != null && var10.getId() == var6.ordinal()) {
                     var4.remove(var6);
                  }
               }
            }
         }
      }

      return var4;
   }

   private String a(PlayerClass var1) {
      String var2 = var1.toString();
      char[] var3 = var2.toCharArray();

      for (int var4 = 1; var4 < var3.length; var4++) {
         if (Character.isUpperCase(var3[var4])) {
            var2 = var2.substring(0, var4) + " " + var2.substring(var4);
         }
      }

      return var2;
   }

   private enum Commands {
      admin_edit_character,
      admin_character_actions,
      admin_current_player,
      admin_nokarma,
      admin_setkarma,
      admin_character_list,
      admin_show_characters,
      admin_show_characters_by_ip,
      admin_show_characters_by_hwid,
      admin_find_character,
      admin_save_modifications,
      admin_rec,
      admin_settitle,
      admin_setclass,
      admin_setname,
      admin_setsex,
      admin_setcolor,
      admin_add_exp_sp_to_character,
      admin_add_exp_sp,
      admin_sethero,
      admin_setnoble,
      admin_trans,
      admin_setsubclass,
      admin_setfame,
      admin_setbday,
      admin_give_item,
      admin_give_all,
      admin_give_all_by_ip,
      admin_give_all_by_hwid,
      admin_give_all_radius,
      admin_remove_item,
      admin_destroy_items,
      admin_add_bang,
      admin_set_bang,
      admin_set_pa;
   }
}
