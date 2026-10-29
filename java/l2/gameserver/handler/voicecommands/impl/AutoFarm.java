package l2.gameserver.handler.voicecommands.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.PetSkillsTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.taskmanager.AutoFarmManager;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.TimeUtils;
import l2.gameserver.utils.Util;

public class AutoFarm implements IVoicedCommandHandler {
   private static final String[] zG = new String[]{
      "autofarm",
      "autosummonfarm",
      "farmstart",
      "farmstop",
      "buyfarm",
      "buyfarmTime",
      "tryFreeTime",
      "expendLimit",
      "changeSkillType",
      "refreshSkills",
      "removeSkill",
      "addSkill",
      "addNewSkill",
      "editFarmOption",
      "editSummonSkills",
      "removeSummonSkill",
      "addSummonSkill",
      "addNewSummonSkill",
      "editSummonFarmOption"
   };
   private static final String zH = "<font color=\"00FF00\">ON</font>";
   private static final String zI = "<font color=\"FF0000\">OFF</font>";

   @Override
   public boolean useVoicedCommand(String var1, Player var2, String var3) {
      if (var2 == null) {
         return false;
      }

      AutoFarmContext var4 = var2.getFarmSystem();
      int var5 = var2.getVarInt("farmType", Config.FARM_TYPE);
      if (var1.startsWith("farmstart")) {
         String[] var71 = var3.split(" ");
         String var86 = "attack";

         try {
            var86 = var71[0];
         } catch (Exception var19) {
         }

         if (var86 == null || var86.isEmpty()) {
            var86 = "attack";
         }

         if (var4.isActiveAutofarm()) {
            var4.startFarmTask();
         } else {
            var2.sendMessage(new CustomMessage("CANT_ACTIVATE_AUTO_FARM_YOU_HAVE_TO_PURCHASE_IT", var2));
         }

         this.a(var2, var4, null, var5, var86);
         return true;
      } else if (var1.startsWith("editSummonSkills")) {
         String[] var70 = var3.split(" ");
         String var85 = "attack";
         String var98 = "attack";

         try {
            var98 = var70[0];
         } catch (Exception var21) {
         }

         try {
            var85 = var70[1];
         } catch (Exception var20) {
         }

         if (var85 == null || var85.isEmpty()) {
            var85 = "attack";
         }

         if (var98 == null || var98.isEmpty()) {
            var98 = "attack";
         }

         if (!var4.isUseSummonSkills()) {
            var2.sendMessage(new CustomMessage("YOU_CANT_EDIT_SETTINGS_THE_OPTION_IS_DISABLED", var2));
            this.a(var2, var4, null, var5, var85);
            return false;
         } else if (var2.getPet() == null) {
            var2.sendMessage(new CustomMessage("YOU_CANT_USE_THIS_OPTION", var2));
            this.a(var2, var4, null, var5, var85);
            return false;
         } else {
            this.a(var2, var4, null, var5, var98, var85);
            return true;
         }
      } else if (var1.startsWith("changeSkillType")) {
         String[] var69 = var3.split(" ");
         String var84 = "attack";

         try {
            var84 = var69[0];
         } catch (Exception var22) {
         }

         if (var84 == null || var84.isEmpty()) {
            var84 = "attack";
         }

         this.a(var2, var4, null, var5, var84);
         return true;
      } else if (var1.startsWith("refreshSkills")) {
         String[] var68 = var3.split(" ");
         String var83 = "attack";
         String var97 = "1";

         try {
            var83 = var68[0];
         } catch (Exception var24) {
         }

         try {
            var97 = var68[1];
         } catch (Exception var23) {
         }

         if (var83 == null || var83.isEmpty()) {
            var83 = "attack";
         }

         if (var97 == null || var97.isEmpty()) {
            var97 = "1";
         }

         var4.setShortcutPageValue(Integer.parseInt(var97));
         var4.checkAllSlots();
         this.a(var2, var4, null, var5, var83);
         return true;
      } else if (var1.startsWith("removeSkill")) {
         String[] var67 = var3.split(" ");
         String var82 = null;
         String var96 = null;

         try {
            var82 = var67[0];
         } catch (Exception var26) {
         }

         try {
            var96 = var67[1];
         } catch (Exception var25) {
         }

         if (var82 == null || var82.isEmpty()) {
            var82 = "attack";
         }

         if (var82 != null && var96 != null) {
            List var106 = null;
            switch (var82) {
               case "attack":
                  var106 = var4.getAttackSpells();
                  break;
               case "chance":
                  var106 = var4.getChanceSpells();
                  break;
               case "self":
                  var106 = var4.getSelfSpells();
                  break;
               case "heal":
                  var106 = var4.getLowLifeSpells();
            }

            if (var106 != null && var106.contains(Integer.parseInt(var96))) {
               var106.remove(Integer.valueOf(Integer.parseInt(var96)));
               switch (var82) {
                  case "attack":
                     var4.saveSkills("farmAttackSkills");
                     break;
                  case "chance":
                     var4.saveSkills("farmChanceSkills");
                     break;
                  case "self":
                     var4.saveSkills("farmSelfSkills");
                     break;
                  case "heal":
                     var4.saveSkills("farmHealSkills");
               }
            }
         }

         this.a(var2, var4, null, var5, var82);
         return true;
      } else if (var1.startsWith("removeSummonSkill")) {
         String[] var66 = var3.split(" ");
         String var81 = null;
         String var95 = null;
         String var105 = null;

         try {
            var81 = var66[0];
         } catch (Exception var29) {
         }

         try {
            var95 = var66[1];
         } catch (Exception var28) {
         }

         try {
            var105 = var66[2];
         } catch (Exception var27) {
         }

         if (var81 != null && var95 != null && var105 != null) {
            List var130 = null;
            switch (var81) {
               case "attack":
                  var130 = var4.getSummonAttackSpells();
                  break;
               case "self":
                  var130 = var4.getSummonSelfSpells();
                  break;
               case "heal":
                  var130 = var4.getSummonHealSpells();
            }

            if (var130 != null && var130.contains(Integer.parseInt(var95))) {
               var130.remove(Integer.valueOf(Integer.parseInt(var95)));
               switch (var105) {
                  case "attack":
                     var4.saveSkills("farmAttackSummonSkills");
                     break;
                  case "self":
                     var4.saveSkills("farmSelfSummonSkills");
                     break;
                  case "heal":
                     var4.saveSkills("farmHealSummonSkills");
               }
            }
         }

         this.a(var2, var4, null, var5, var81, var105);
         return true;
      } else if (var1.startsWith("addSkill")) {
         String[] var65 = var3.split(" ");
         String var80 = null;
         String var94 = "1";

         try {
            var80 = var65[0];
         } catch (Exception var31) {
         }

         try {
            var94 = var65[1];
         } catch (Exception var30) {
         }

         if (var80 != null) {
            this.a(var2, var4, var80, Integer.parseInt(var94));
         }

         return true;
      } else if (var1.startsWith("addSummonSkill")) {
         String[] var64 = var3.split(" ");
         String var79 = null;
         String var93 = null;
         String var104 = "1";

         try {
            var79 = var64[0];
         } catch (Exception var34) {
         }

         try {
            var93 = var64[1];
         } catch (Exception var33) {
         }

         try {
            var104 = var64[2];
         } catch (Exception var32) {
         }

         if (var79 != null && var93 != null) {
            this.a(var2, var4, var5, var79, var93, Integer.parseInt(var104));
         }

         return true;
      } else if (var1.startsWith("addNewSkill")) {
         String[] var63 = var3.split(" ");
         String var78 = null;
         String var92 = null;

         try {
            var78 = var63[0];
         } catch (Exception var36) {
         }

         try {
            var92 = var63[1];
         } catch (Exception var35) {
         }

         if (var92 == null || var92.isEmpty()) {
            var92 = "attack";
         }

         if (var78 != null && var92 != null) {
            Skill var103 = var2.getKnownSkill(Integer.parseInt(var78));
            if (var103 != null) {
               switch (var92) {
                  case "attack":
                     if (var4.getAttackSpells().size() >= 8) {
                        this.a(var2, var4, null, var5, var92);
                        return false;
                     }

                     if (!var103.isSpoilSkill()
                        && !var103.isSweepSkill()
                        && var103.getId() != 1263
                        && (
                           var103.getSkillType() == Skill.SkillType.AGGRESSION
                              || var103.getSkillType() == Skill.SkillType.PDAM
                              || var103.getSkillType() == Skill.SkillType.MANADAM
                              || var103.getSkillType() == Skill.SkillType.MDAM
                              || var103.getSkillType() == Skill.SkillType.DRAIN
                              || var103.getSkillType() == Skill.SkillType.CPDAM
                              || var103.getSkillType() == Skill.SkillType.STUN
                        )) {
                        var4.getAttackSpells().add(var103.getId());
                        var4.saveSkills("farmAttackSkills");
                     }
                     break;
                  case "chance":
                     if (var4.getChanceSpells().size() >= 8) {
                        this.a(var2, var4, null, var5, var92);
                        return false;
                     }

                     if (var103.getSkillType() == Skill.SkillType.DOT
                        || var103.getSkillType() == Skill.SkillType.MDOT
                        || var103.getSkillType() == Skill.SkillType.POISON
                        || var103.getSkillType() == Skill.SkillType.BLEED
                        || var103.getSkillType() == Skill.SkillType.DEBUFF
                        || var103.getSkillType() == Skill.SkillType.SLEEP
                        || var103.getSkillType() == Skill.SkillType.ROOT
                        || var103.getSkillType() == Skill.SkillType.PARALYZE
                        || var103.getSkillType() == Skill.SkillType.MUTE
                        || var103.isSpoilSkill()
                        || var103.isSweepSkill()
                        || var103.getId() == 1263) {
                        var4.getChanceSpells().add(var103.getId());
                        var4.saveSkills("farmChanceSkills");
                     }
                     break;
                  case "self":
                     if (var4.getSelfSpells().size() >= 8) {
                        this.a(var2, var4, null, var5, var92);
                        return false;
                     }

                     if (!var103.isToggle() && !var103.isMusic() && var103.getSkillType() != Skill.SkillType.BUFF && !var103.isCubicSkill()) {
                        return false;
                     }

                     var4.getSelfSpells().add(var103.getId());
                     var4.saveSkills("farmSelfSkills");
                     break;
                  case "heal":
                     if (var4.getLowLifeSpells().size() >= 8) {
                        this.a(var2, var4, null, var5, var92);
                        return false;
                     }

                     if (var103.getSkillType() != Skill.SkillType.DRAIN
                        && var103.getSkillType() != Skill.SkillType.HEAL
                        && var103.getSkillType() != Skill.SkillType.HEAL_PERCENT
                        && var103.getSkillType() != Skill.SkillType.MANAHEAL
                        && var103.getSkillType() != Skill.SkillType.MANAHEAL_PERCENT) {
                        return false;
                     }

                     var4.getLowLifeSpells().add(var103.getId());
                     var4.saveSkills("farmHealSkills");
               }
            }
         }

         this.a(var2, var4, null, var5, var92);
         return true;
      } else if (var1.startsWith("addNewSummonSkill")) {
         String[] var62 = var3.split(" ");
         String var77 = null;
         String var91 = null;
         String var102 = null;

         try {
            var77 = var62[0];
         } catch (Exception var39) {
         }

         try {
            var91 = var62[1];
         } catch (Exception var38) {
         }

         try {
            var102 = var62[2];
         } catch (Exception var37) {
         }

         if (var77 != null && var91 != null && var102 != null) {
            if (var2.getPet() == null) {
               var2.sendMessage(new CustomMessage("YOU_CANT_USE_THIS_OPTION", var2));
               this.a(var2, var4, null, var5, var91, var102);
               return false;
            }

            Summon var128 = var2.getPet();
            if (var128.isPet() && var128.getLevel() - var2.getLevel() > 20) {
               var2.sendPacket(SystemMsg.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
               this.a(var2, var4, null, var5, var91, var102);
               return false;
            }

            int var135 = PetSkillsTable.getInstance().getAvailableLevel(var128, Integer.parseInt(var77));
            if (var135 > 0) {
               Skill var148 = SkillTable.getInstance().getInfo(Integer.parseInt(var77), var135);
               if (var148 != null) {
                  switch (var91) {
                     case "attack":
                        if (var4.getSummonAttackSpells().size() >= 8) {
                           this.a(var2, var4, null, var5, var91, var102);
                           return false;
                        }

                        if (var148.getSkillType() == Skill.SkillType.AGGRESSION
                           || var148.getSkillType() == Skill.SkillType.PDAM
                           || var148.getSkillType() == Skill.SkillType.MANADAM
                           || var148.getSkillType() == Skill.SkillType.MDAM
                           || var148.getSkillType() == Skill.SkillType.DRAIN
                           || var148.getSkillType() == Skill.SkillType.CPDAM
                           || var148.getSkillType() == Skill.SkillType.STUN) {
                           var4.getSummonAttackSpells().add(var148.getId());
                           var4.saveSkills("farmAttackSummonSkills");
                        }
                        break;
                     case "self":
                        if (var4.getSummonSelfSpells().size() >= 8) {
                           this.a(var2, var4, null, var5, var91, var102);
                           return false;
                        }

                        if (!var148.isToggle() && !var148.isMusic() && var148.getSkillType() != Skill.SkillType.BUFF && !var148.isCubicSkill()) {
                           return false;
                        }

                        var4.getSummonSelfSpells().add(var148.getId());
                        var4.saveSkills("farmSelfSummonSkills");
                        break;
                     case "heal":
                        if (var4.getSummonHealSpells().size() >= 8) {
                           this.a(var2, var4, null, var5, var91, var102);
                           return false;
                        }

                        if (var148.getSkillType() != Skill.SkillType.DRAIN
                           && var148.getSkillType() != Skill.SkillType.HEAL
                           && var148.getSkillType() != Skill.SkillType.HEAL_PERCENT
                           && var148.getSkillType() != Skill.SkillType.MANAHEAL
                           && var148.getSkillType() != Skill.SkillType.MANAHEAL_PERCENT) {
                           return false;
                        }

                        var4.getSummonHealSpells().add(var148.getId());
                        var4.saveSkills("farmHealSummonSkills");
                  }
               }
            }
         }

         this.a(var2, var4, null, var5, var91, var102);
         return true;
      } else if (var1.startsWith("editFarmOption")) {
         String[] var61 = var3.split(" ");
         String var76 = "attack";
         String var90 = null;

         try {
            var76 = var61[0];
         } catch (Exception var41) {
         }

         try {
            var90 = var61[1];
         } catch (Exception var40) {
         }

         if (var76 != null && var90 != null) {
            boolean var101 = false;
            switch (var5) {
               case 0:
                  if (var90.equalsIgnoreCase("farmLeaderAssist")) {
                     boolean var124 = !var4.isLeaderAssist();
                     var4.setLeaderAssist(var124, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmKeepLocation")) {
                     boolean var125 = !var4.isKeepLocation();
                     var4.setKeepLocation(var2.getLoc(), var125, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmDelaySkills")) {
                     boolean var126 = !var4.isExtraDelaySkill();
                     var4.setExDelaySkill(var126, false);
                     var101 = true;
                  }
                  break;
               case 1:
                  if (var90.equalsIgnoreCase("farmLeaderAssist")) {
                     boolean var120 = !var4.isLeaderAssist();
                     var4.setLeaderAssist(var120, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmKeepLocation")) {
                     boolean var121 = !var4.isKeepLocation();
                     var4.setKeepLocation(var2.getLoc(), var121, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmDelaySkills")) {
                     boolean var122 = !var4.isExtraDelaySkill();
                     var4.setExDelaySkill(var122, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmRunTargetCloseUp")) {
                     boolean var123 = !var4.isRunTargetCloseUp();
                     var4.setRunTargetCloseUp(var123, false);
                     var101 = true;
                  }
                  break;
               case 2:
                  if (var90.equalsIgnoreCase("farmLeaderAssist")) {
                     boolean var117 = !var4.isLeaderAssist();
                     var4.setLeaderAssist(var117, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmKeepLocation")) {
                     boolean var118 = !var4.isKeepLocation();
                     var4.setKeepLocation(var2.getLoc(), var118, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmRunTargetCloseUp")) {
                     boolean var119 = !var4.isRunTargetCloseUp();
                     var4.setRunTargetCloseUp(var119, false);
                     var101 = true;
                  }
                  break;
               case 3:
                  if (var90.equalsIgnoreCase("farmLeaderAssist")) {
                     boolean var114 = !var4.isLeaderAssist();
                     var4.setLeaderAssist(var114, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmAssistMonsterAttack")) {
                     boolean var115 = !var4.isAssistMonsterAttack();
                     var4.setAssistMonsterAttack(var115, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmTargetRestoreMp")) {
                     boolean var116 = !var4.isTargetRestoreMp();
                     var4.setTargetRestoreMp(var116, false);
                     var101 = true;
                  }
                  break;
               case 4:
                  if (var90.equalsIgnoreCase("farmLeaderAssist")) {
                     boolean var110 = !var4.isLeaderAssist();
                     var4.setLeaderAssist(var110, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmKeepLocation")) {
                     boolean var111 = !var4.isKeepLocation();
                     var4.setKeepLocation(var2.getLoc(), var111, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmUseSummonSkills")) {
                     if (var2.getPet() == null) {
                        var2.sendMessage(new CustomMessage("YOU_CANT_USE_THIS_OPTION", var2));
                        this.a(var2, var4, null, var5, var76);
                        return true;
                     }

                     boolean var112 = !var4.isUseSummonSkills();
                     var4.setUseSummonSkills(var112, false);
                     var101 = true;
                  } else if (var90.equalsIgnoreCase("farmDelaySkills")) {
                     boolean var113 = !var4.isExtraDelaySkill();
                     var4.setExDelaySkill(var113, false);
                     var101 = true;
                  }
            }

            if (var101) {
               this.a(var2, var4, null, var5, var76);
               return true;
            }

            switch (var76) {
               case "attack":
                  if (var90.equalsIgnoreCase("farmRndAttackSkills")) {
                     boolean var147 = !var4.isRndAttackSkills();
                     var4.setRndAttackSkills(var147, false);
                  }

                  this.a(var2, var4, null, var5, var76);
                  return true;
               case "chance":
                  if (var90.equalsIgnoreCase("farmRndChanceSkills")) {
                     boolean var146 = !var4.isRndChanceSkills();
                     var4.setRndChanceSkills(var146, false);
                  }

                  this.a(var2, var4, null, var5, var76);
                  return true;
               case "self":
                  if (var90.equalsIgnoreCase("farmRndSelfSkills")) {
                     boolean var145 = !var4.isRndSelfSkills();
                     var4.setRndSelfSkills(var145, false);
                  }

                  this.a(var2, var4, null, var5, var76);
                  return true;
               case "heal":
                  if (var90.equalsIgnoreCase("farmRndLifeSkills")) {
                     boolean var144 = !var4.isRndLifeSkills();
                     var4.setRndLifeSkills(var144, false);
                  }

                  this.a(var2, var4, null, var5, var76);
                  return true;
            }
         }

         this.a(var2, var4, null, var5, var76);
         return true;
      } else if (var1.startsWith("editSummonFarmOption")) {
         String[] var60 = var3.split(" ");
         String var75 = "attack";
         String var89 = "attack";
         String var100 = null;

         try {
            var75 = var60[0];
         } catch (Exception var44) {
         }

         try {
            var100 = var60[1];
         } catch (Exception var43) {
         }

         try {
            var89 = var60[2];
         } catch (Exception var42) {
         }

         label1310: {
            if (var75 == null || var100 == null) {
               this.a(var2, var4, null, var5, var75, var89);
               return true;
            }

            if (var100.equalsIgnoreCase("farmSummonDelaySkills")) {
               boolean var109 = !var4.isExtraSummonDelaySkill();
               var4.setExSummonDelaySkill(var109, false);
               this.a(var2, var4, null, var5, var75, var89);
               return true;
            }

            switch (var75) {
               case "attack":
                  if (var100.equalsIgnoreCase("farmRndSummonAttackSkills")) {
                     boolean var143 = !var4.isRndSummonAttackSkills();
                     var4.setRndSummonAttackSkills(var143, false);
                  }

                  this.a(var2, var4, null, var5, var75, var89);
                  return true;
               case "self":
                  if (var100.equalsIgnoreCase("farmRndSummonSelfSkills")) {
                     boolean var142 = !var4.isRndSummonSelfSkills();
                     var4.setRndSummonSelfSkills(var142, false);
                  }

                  this.a(var2, var4, null, var5, var75, var89);
                  return true;
               case "heal":
                  if (var100.equalsIgnoreCase("farmRndSummonLifeSkills")) {
                     boolean var141 = !var4.isRndSummonLifeSkills();
                     var4.setRndSummonLifeSkills(var141, false);
                  }

                  this.a(var2, var4, null, var5, var75, var89);
                  return true;
               default:
                  break label1310;
            }
         }
      } else {
         if (var1.equalsIgnoreCase("farmstop")) {
            var4.stopFarmTask(false);
            this.a(var2, var4, null, var5, "attack");
         } else if (var1.equalsIgnoreCase("expendLimit")) {
            if (var4.isAutofarming()) {
               this.a(var2, var4, null, var5, "attack");
               return false;
            }

            if (AutoFarmManager.getInstance().isNonCheckPlayer(var2.getObjectId())) {
               var2.sendMessage(new CustomMessage("YOU_HAVE_ALREADY_USED_THIS_SERVICE", var2));
               this.a(var2, var4, null, var5, "attack");
               return false;
            }

            if (Config.FARM_EXPEND_LIMIT_PRICE[0] != 0) {
               if (var2.getInventory().getItemByItemId(Config.FARM_EXPEND_LIMIT_PRICE[0]) == null
                  || var2.getInventory().getItemByItemId(Config.FARM_EXPEND_LIMIT_PRICE[0]).getCount() < Config.FARM_EXPEND_LIMIT_PRICE[1]) {
                  ItemTemplate var59 = ItemHolder.getInstance().getTemplate(Config.FARM_EXPEND_LIMIT_PRICE[0]);
                  if (var59 != null) {
                     var2.sendMessage(
                        new CustomMessage("TO_USE_THE_SERVICE_YOU_MUST_HAVE", var2).addNumber(Config.FARM_EXPEND_LIMIT_PRICE[1]).addString(var59.getName())
                     );
                  }

                  this.a(var2, var4, null, var5, "attack");
                  return false;
               }

               ItemFunctions.removeItem(var2, Config.FARM_EXPEND_LIMIT_PRICE[0], Config.FARM_EXPEND_LIMIT_PRICE[1], true);
               Log.service("AutoFarm", var2, " bought expend period " + Config.FARM_EXPEND_LIMIT_PRICE[0] + " amount " + Config.FARM_EXPEND_LIMIT_PRICE[1]);
            }

            AutoFarmManager.getInstance().addNonCheckPlayer(var2.getObjectId());
            var2.sendMessage(new CustomMessage("YOUR_CHARACTER_IS_EXCLUDED_FROM_LIMITS_LIST", var2));
            this.a(var2, var4, null, var5, "attack");
         } else {
            if (var1.equalsIgnoreCase("buyfarm")) {
               if (var4.isActiveAutofarm()) {
                  var2.sendMessage(new CustomMessage("YOU_ALREADY_HAVE_ACTIVE_AUTO_FARM_TIME", var2));
                  this.a(var2, var4, null, var5, "attack");
                  return false;
               }

               this.b(var2, var4);
               return true;
            }

            if (var1.startsWith("tryFreeTime")) {
               String[] var58 = var3.split(" ");
               String var74 = "attack";

               try {
                  var74 = var58[0];
               } catch (Exception var55) {
               }

               if (var74 == null || var74.isEmpty()) {
                  var74 = "attack";
               }

               long var88 = var2.getVarLong("farmFreeTime", 0L);
               if (!Config.ALLOW_FARM_FREE_TIME || var88 > 0L) {
                  var2.sendMessage(new CustomMessage("FUNCTION_IS_NOT_AVAILABLE", var2));
                  this.b(var2, var4);
                  return false;
               }

               long var107 = System.currentTimeMillis() + Config.FARM_FREE_TIME * 3600000L;
               if (Config.FARM_ONLINE_TYPE) {
                  var2.setVar("activeFarmOnlineTask", var107 - System.currentTimeMillis(), -1L);
                  var2.setVar("activeFarmOnlineTime", 0, -1L);
                  var4.refreshFarmOnlineTime();
               } else {
                  var2.setVar("activeFarmTask", var107, -1L);
                  var4.setAutoFarmEndTask(var107);
               }

               var2.setVar("farmFreeTime", var107, -1L);
               var4.checkFarmTask();
               this.a(var2, var4, null, var5, var74);
               var2.sendMessage(new CustomMessage("YOU_HAVE_SUCCESSFULLY_ACTIVATE_AUTO_FARM_FREE_TIME_SERVICE", var2));
            } else {
               if (var1.startsWith("buyfarmTime")) {
                  String[] var57 = var3.split(" ");
                  String var73 = null;
                  String var87 = "attack";

                  try {
                     var73 = var57[0];
                  } catch (Exception var46) {
                  }

                  try {
                     var87 = var57[1];
                  } catch (Exception var45) {
                  }

                  if (var73 != null) {
                     int var99 = Integer.parseInt(var73);
                     boolean var10 = false;
                     int var11 = 0;
                     long var12 = 0L;
                     long var14 = System.currentTimeMillis() + var99 * 3600000L;

                     for (int var17 : Config.AUTO_FARM_PRICES.keySet()) {
                        if (var17 == var99) {
                           var10 = true;
                           String[] var18 = Config.AUTO_FARM_PRICES.get(var17).split(":");
                           if (var18 != null && var18.length == 2) {
                              var11 = Integer.parseInt(var18[0]);
                              var12 = Long.parseLong(var18[1]);
                           }
                           break;
                        }
                     }

                     if (var10) {
                        if (var11 != 0) {
                           if (var2.getInventory().getItemByItemId(var11) == null || var2.getInventory().getItemByItemId(var11).getCount() < var12) {
                              ItemTemplate var152 = ItemHolder.getInstance().getTemplate(var11);
                              if (var152 != null) {
                                 var2.sendMessage(new CustomMessage("TO_USE_THE_SERVICE_YOU_MUST_HAVE", var2).addNumber(var12).addString(var152.getName()));
                              }

                              this.b(var2, var4);
                              return false;
                           }

                           ItemFunctions.removeItem(var2, var11, var12, true);
                           Log.service("AutoFarm", var2, "bought farm period " + var11 + " amount " + var12);
                        }

                        if (Config.FARM_ONLINE_TYPE) {
                           var2.setVar("activeFarmOnlineTask", var14 - System.currentTimeMillis(), -1L);
                           var2.setVar("activeFarmOnlineTime", 0, -1L);
                           var4.refreshFarmOnlineTime();
                        } else {
                           var2.setVar("activeFarmTask", var14, -1L);
                           var4.setAutoFarmEndTask(var14);
                        }

                        var4.checkFarmTask();
                        this.a(var2, var4, null, var5, var87);
                        var2.sendMessage(new CustomMessage("YOU_HAVE_SUCCESSFULLY_PURCHASED_THE_AUTO_FARM_SERVICE", var2));
                     }
                  }

                  return true;
               }

               if (var1.startsWith("autofarm")) {
                  String[] var56 = var3.split(" ");
                  String var72 = "attack";
                  if (var56.length >= 2 && var56[0].equalsIgnoreCase("edit_farm")) {
                     try {
                        var72 = var56[2];
                     } catch (Exception var51) {
                     }

                     this.a(var2, var4, var56[1], var5, var72);
                  } else if (var56.length >= 2 && var56[0].equals("edit_farmType")) {
                     String var8 = null;

                     try {
                        var8 = var56[1];
                     } catch (Exception var50) {
                     }

                     try {
                        var72 = var56[2];
                     } catch (Exception var49) {
                     }

                     if (var8 != null) {
                        int var9 = Integer.parseInt(var8);
                        if (var9 > 4) {
                           var9 = 4;
                        } else if (var9 < 0) {
                           var9 = 0;
                        }

                        var2.setVar("farmType", var9, -1L);
                        var4.setFarmTypeValue(var9);
                        this.a(var2, var4, null, var9, var72);
                        return true;
                     }

                     this.a(var2, var4, null, var5, var72);
                  } else if (var56.length >= 2) {
                     try {
                        var72 = var56[2];
                     } catch (Exception var48) {
                     }

                     if (var56[0].equals("set_attackSkills")) {
                        this.a(var2, var4, var56, 100, 1);
                     } else if (var56[0].equals("set_chanceSkills")) {
                        this.a(var2, var4, var56, 100, 1);
                     } else if (var56[0].equals("set_selfSkills")) {
                        this.a(var2, var4, var56, 100, 1);
                     } else if (var56[0].equals("set_healSkills")) {
                        this.a(var2, var4, var56, 100, 1);
                     } else if (var56[0].equals("set_attackSkillsPercent")) {
                        this.a(var2, var4, var56, 100, 1);
                     } else if (var56[0].equals("set_chanceSkillsPercent")) {
                        this.a(var2, var4, var56, 100, 1);
                     } else if (var56[0].equals("set_selfSkillsPercent")) {
                        this.a(var2, var4, var56, 100, 1);
                     } else if (var56[0].equals("set_healSkillsPercent")) {
                        this.a(var2, var4, var56, 100, 1);
                     } else if (var56[0].equals("set_distance")) {
                        this.a(var2, var4, var56, Config.SEARCH_DISTANCE, 1);
                     } else if (var56[0].equals("set_shortcutPage")) {
                        this.a(var2, var4, var56, Config.SHORTCUT_PAGE, 1);
                     }

                     this.a(var2, var4, null, var5, var72);
                  } else {
                     try {
                        var72 = var56[2];
                     } catch (Exception var47) {
                     }

                     this.a(var2, var4, null, var5, var72);
                  }

                  return true;
               }

               if (var1.startsWith("autosummonfarm")) {
                  String[] var6 = var3.split(" ");
                  String var7 = "attack";
                  if (var6.length >= 2 && var6[0].equalsIgnoreCase("edit_farm")) {
                     try {
                        var7 = var6[2];
                     } catch (Exception var54) {
                     }

                     this.a(var2, var4, var6[1], var5, var7, "attack");
                  } else if (var6.length >= 2) {
                     try {
                        var7 = var6[2];
                     } catch (Exception var53) {
                     }

                     if (var6[0].equals("set_attackSummonSkills")) {
                        this.a(var2, var4, var6, 100, 1);
                     } else if (var6[0].equals("set_selfSummonSkills")) {
                        this.a(var2, var4, var6, 100, 1);
                     } else if (var6[0].equals("set_healSummonSkills")) {
                        this.a(var2, var4, var6, 100, 1);
                     } else if (var6[0].equals("set_attackSummonSkillsPercent")) {
                        this.a(var2, var4, var6, 100, 1);
                     } else if (var6[0].equals("set_selfSummonSkillsPercent")) {
                        this.a(var2, var4, var6, 100, 1);
                     } else if (var6[0].equals("set_healSummonSkillsPercent")) {
                        this.a(var2, var4, var6, 100, 1);
                     }

                     this.a(var2, var4, null, var5, var7, "attack");
                  } else {
                     try {
                        var7 = var6[2];
                     } catch (Exception var52) {
                     }

                     this.a(var2, var4, null, var5, var7, "attack");
                  }

                  return true;
               }
            }
         }

         return true;
      }
      return true;
   }

   private void a(Player var1, AutoFarmContext var2, String[] var3, int var4, int var5) {
      String var6 = null;

      try {
         var6 = var3[1];
      } catch (Exception var9) {
      }

      if (var6 != null) {
         int var7 = 0;

         try {
            var7 = Integer.parseInt(var6);
            if (var7 > var4) {
               var7 = var4;
            }

            if (var7 < var5) {
               var7 = var5;
            }
         } catch (NumberFormatException var10) {
            if (var3[0].equals("set_attackSkills")) {
               var7 = var1.getVarInt("attackChanceSkills", Config.ATTACK_SKILL_CHANCE);
            } else if (var3[0].equals("set_chanceSkills")) {
               var7 = var1.getVarInt("chanceChanceSkills", Config.CHANCE_SKILL_CHANCE);
            } else if (var3[0].equals("set_selfSkills")) {
               var7 = var1.getVarInt("selfChanceSkills", Config.SELF_SKILL_CHANCE);
            } else if (var3[0].equals("set_healSkills")) {
               var7 = var1.getVarInt("healChanceSkills", Config.HEAL_SKILL_CHANCE);
            } else if (var3[0].equals("set_attackSkillsPercent")) {
               var7 = var1.getVarInt("attackSkillsPercent", Config.ATTACK_SKILL_PERCENT);
            } else if (var3[0].equals("set_chanceSkillsPercent")) {
               var7 = var1.getVarInt("chanceSkillsPercent", Config.CHANCE_SKILL_PERCENT);
            } else if (var3[0].equals("set_selfSkillsPercent")) {
               var7 = var1.getVarInt("selfSkillsPercent", Config.SELF_SKILL_PERCENT);
            } else if (var3[0].equals("set_healSkillsPercent")) {
               var7 = var1.getVarInt("healSkillsPercent", Config.HEAL_SKILL_PERCENT);
            } else if (var3[0].equals("set_distance")) {
               var7 = var1.getVarInt("farmDistance", Config.SEARCH_DISTANCE);
            } else if (var3[0].equals("set_shortcutPage")) {
               var7 = var1.getVarInt("shortcutPage", Config.SHORTCUT_PAGE);
            } else if (var3[0].equals("set_attackSummonSkills")) {
               var7 = var1.getVarInt("attackSummonChanceSkills", Config.SUMMON_ATTACK_SKILL_CHANCE);
            } else if (var3[0].equals("set_selfSummonSkills")) {
               var7 = var1.getVarInt("selfSummonChanceSkills", Config.SUMMON_SELF_SKILL_CHANCE);
            } else if (var3[0].equals("set_healSummonSkills")) {
               var7 = var1.getVarInt("healSummonChanceSkills", Config.SUMMON_HEAL_SKILL_CHANCE);
            } else if (var3[0].equals("set_attackSummonSkillsPercent")) {
               var7 = var1.getVarInt("attackSummonSkillsPercent", Config.SUMMON_ATTACK_SKILL_PERCENT);
            } else if (var3[0].equals("set_selfSummonSkillsPercent")) {
               var7 = var1.getVarInt("selfSummonSkillsPercent", Config.SUMMON_SELF_SKILL_PERCENT);
            } else if (var3[0].equals("set_healSummonSkillsPercent")) {
               var7 = var1.getVarInt("healSummonSkillsPercent", Config.SUMMON_HEAL_SKILL_PERCENT);
            }
         }

         if (var3[0].equals("set_attackSkills")) {
            var1.setVar("attackChanceSkills", var7, -1L);
            var2.setAttackSkillValue(false, var7);
         } else if (var3[0].equals("set_chanceSkills")) {
            var1.setVar("chanceChanceSkills", var7, -1L);
            var2.setChanceSkillValue(false, var7);
         } else if (var3[0].equals("set_selfSkills")) {
            var1.setVar("selfChanceSkills", var7, -1L);
            var2.setSelfSkillValue(false, var7);
         } else if (var3[0].equals("set_healSkills")) {
            var1.setVar("healChanceSkills", var7, -1L);
            var2.setLifeSkillValue(false, var7);
         } else if (var3[0].equals("set_attackSkillsPercent")) {
            var1.setVar("attackSkillsPercent", var7, -1L);
            var2.setAttackSkillValue(true, var7);
         } else if (var3[0].equals("set_chanceSkillsPercent")) {
            var1.setVar("chanceSkillsPercent", var7, -1L);
            var2.setChanceSkillValue(true, var7);
         } else if (var3[0].equals("set_selfSkillsPercent")) {
            var1.setVar("selfSkillsPercent", var7, -1L);
            var2.setSelfSkillValue(true, var7);
         } else if (var3[0].equals("set_healSkillsPercent")) {
            var1.setVar("healSkillsPercent", var7, -1L);
            var2.setLifeSkillValue(true, var7);
         } else if (var3[0].equals("set_distance")) {
            var1.setVar("farmDistance", var7, -1L);
            var2.setRadiusValue(var7);
         } else if (var3[0].equals("set_shortcutPage")) {
            var1.setVar("shortcutPage", var7, -1L);
            var2.setShortcutPageValue(var7);
         } else if (var3[0].equals("set_attackSummonSkills")) {
            var1.setVar("attackSummonChanceSkills", var7, -1L);
            var2.setSummonAttackSkillValue(false, var7);
         } else if (var3[0].equals("set_selfSummonSkills")) {
            var1.setVar("selfSummonChanceSkills", var7, -1L);
            var2.setSummonSelfSkillValue(false, var7);
         } else if (var3[0].equals("set_healSummonSkills")) {
            var1.setVar("healSummonChanceSkills", var7, -1L);
            var2.setSummonLifeSkillValue(false, var7);
         } else if (var3[0].equals("set_attackSummonSkillsPercent")) {
            var1.setVar("attackSummonSkillsPercent", var7, -1L);
            var2.setSummonAttackSkillValue(true, var7);
         } else if (var3[0].equals("set_selfSummonSkillsPercent")) {
            var1.setVar("selfSummonSkillsPercent", var7, -1L);
            var2.setSummonSelfSkillValue(true, var7);
         } else if (var3[0].equals("set_healSummonSkillsPercent")) {
            var1.setVar("healSummonSkillsPercent", var7, -1L);
            var2.setSummonLifeSkillValue(true, var7);
         }
      }
   }

   private void a(Player var1, AutoFarmContext var2, String var3, int var4, String var5) {
      String var6 = null;
      switch (var4) {
         case 0:
            var6 = HtmCache.getInstance().getNotNull("command/autofarm/index-fighter.htm", var1);
            boolean var7 = var2.isLeaderAssist();
            if (var7) {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%assist_bypass%", "bypass -h user_editFarmOption " + var5 + " farmLeaderAssist");
            boolean var8 = var2.isKeepLocation();
            if (var8) {
               var6 = var6.replace("%keepLoc_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%keepLoc_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%keepLoc_bypass%", "bypass -h user_editFarmOption " + var5 + " farmKeepLocation");
            boolean var9 = var2.isExtraDelaySkill();
            if (var9) {
               var6 = var6.replace("%delaySk_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%delaySk_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%delaySk_bypass%", "bypass -h user_editFarmOption " + var5 + " farmDelaySkills");
            break;
         case 1:
            var6 = HtmCache.getInstance().getNotNull("command/autofarm/index-archer.htm", var1);
            boolean var10 = var2.isLeaderAssist();
            if (var10) {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%assist_bypass%", "bypass -h user_editFarmOption " + var5 + " farmLeaderAssist");
            boolean var11 = var2.isKeepLocation();
            if (var11) {
               var6 = var6.replace("%keepLoc_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%keepLoc_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%keepLoc_bypass%", "bypass -h user_editFarmOption " + var5 + " farmKeepLocation");
            boolean var12 = var2.isExtraDelaySkill();
            if (var12) {
               var6 = var6.replace("%delaySk_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%delaySk_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%delaySk_bypass%", "bypass -h user_editFarmOption " + var5 + " farmDelaySkills");
            boolean var13 = var2.isRunTargetCloseUp();
            if (var13) {
               var6 = var6.replace("%runCloseUp_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%runCloseUp_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%runCloseUp_bypass%", "bypass -h user_editFarmOption " + var5 + " farmRunTargetCloseUp");
            break;
         case 2:
            var6 = HtmCache.getInstance().getNotNull("command/autofarm/index-mage.htm", var1);
            boolean var14 = var2.isLeaderAssist();
            if (var14) {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%assist_bypass%", "bypass -h user_editFarmOption " + var5 + " farmLeaderAssist");
            boolean var15 = var2.isKeepLocation();
            if (var15) {
               var6 = var6.replace("%keepLoc_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%keepLoc_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%keepLoc_bypass%", "bypass -h user_editFarmOption " + var5 + " farmKeepLocation");
            boolean var16 = var2.isRunTargetCloseUp();
            if (var16) {
               var6 = var6.replace("%runCloseUp_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%runCloseUp_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%runCloseUp_bypass%", "bypass -h user_editFarmOption " + var5 + " farmRunTargetCloseUp");
            break;
         case 3:
            var6 = HtmCache.getInstance().getNotNull("command/autofarm/index-heal.htm", var1);
            boolean var17 = var2.isLeaderAssist();
            if (var17) {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%assist_bypass%", "bypass -h user_editFarmOption " + var5 + " farmLeaderAssist");
            boolean var18 = var2.isAssistMonsterAttack();
            if (var18) {
               var6 = var6.replace("%assistMAttack_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%assistMAttack_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%assistMAttack_bypass%", "bypass -h user_editFarmOption " + var5 + " farmAssistMonsterAttack");
            boolean var19 = var2.isTargetRestoreMp();
            if (var19) {
               var6 = var6.replace("%tgRestoreMp_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%tgRestoreMp_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%tgRestoreMp_bypass%", "bypass -h user_editFarmOption " + var5 + " farmTargetRestoreMp");
            break;
         case 4:
            var6 = HtmCache.getInstance().getNotNull("command/autofarm/index-summon.htm", var1);
            boolean var20 = var2.isLeaderAssist();
            if (var20) {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%assist_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%assist_bypass%", "bypass -h user_editFarmOption " + var5 + " farmLeaderAssist");
            boolean var21 = var2.isKeepLocation();
            if (var21) {
               var6 = var6.replace("%keepLoc_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%keepLoc_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%keepLoc_bypass%", "bypass -h user_editFarmOption " + var5 + " farmKeepLocation");
            boolean var22 = var2.isUseSummonSkills();
            if (var22) {
               var6 = var6.replace("%useSummonSk_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%useSummonSk_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%useSummonSk_bypass%", "bypass -h user_editFarmOption " + var5 + " farmUseSummonSkills");
            boolean var23 = var2.isExtraDelaySkill();
            if (var23) {
               var6 = var6.replace("%delaySk_img%", "L2UI.CheckBox_checked");
            } else {
               var6 = var6.replace("%delaySk_img%", "L2UI.CheckBox");
            }

            var6 = var6.replace("%delaySk_bypass%", "bypass -h user_editFarmOption " + var5 + " farmDelaySkills");
      }

      String var77 = "";
      String var78 = "";
      String var79 = "";
      String var80 = "";
      String var81 = "";
      String var82 = "";
      String var83 = "";
      String var84 = "";
      List var85 = null;
      String var86 = null;
      switch (var5) {
         case "attack":
            var85 = var2.getAttackSpells();
            var86 = HtmCache.getInstance().getNotNull("command/autofarm/index-skill_attack.htm", var1);
            var77 = b(
               var1, var3 != null && var3.equals("editAttackSkills") ? var3 : null, "editAttackSkills", "attackChanceSkills", Config.ATTACK_SKILL_CHANCE, var5
            );
            var78 = b(
               var1,
               var3 != null && var3.equals("editAttackPercent") ? var3 : null,
               "editAttackPercent",
               "attackSkillsPercent",
               Config.ATTACK_SKILL_PERCENT,
               var5
            );
            boolean var106 = var2.isRndAttackSkills();
            if (var106) {
               var86 = var86.replace("%rndAttackSk_img%", "L2UI.CheckBox_checked");
            } else {
               var86 = var86.replace("%rndAttackSk_img%", "L2UI.CheckBox");
            }

            var86 = var86.replace("%rndAttackSk_bypass%", "bypass -h user_editFarmOption " + var5 + " farmRndAttackSkills");
            break;
         case "chance":
            var85 = var2.getChanceSpells();
            var86 = HtmCache.getInstance().getNotNull("command/autofarm/index-skill_chance.htm", var1);
            var79 = b(
               var1, var3 != null && var3.equals("editChanceSkills") ? var3 : null, "editChanceSkills", "chanceChanceSkills", Config.CHANCE_SKILL_CHANCE, var5
            );
            var80 = b(
               var1,
               var3 != null && var3.equals("editChancePercent") ? var3 : null,
               "editChancePercent",
               "chanceSkillsPercent",
               Config.CHANCE_SKILL_PERCENT,
               var5
            );
            boolean var109 = var2.isRndChanceSkills();
            if (var109) {
               var86 = var86.replace("%rndChanceSk_img%", "L2UI.CheckBox_checked");
            } else {
               var86 = var86.replace("%rndChanceSk_img%", "L2UI.CheckBox");
            }

            var86 = var86.replace("%rndChanceSk_bypass%", "bypass -h user_editFarmOption " + var5 + " farmRndChanceSkills");
            break;
         case "self":
            var85 = var2.getSelfSpells();
            var86 = HtmCache.getInstance().getNotNull("command/autofarm/index-skill_self.htm", var1);
            var81 = b(var1, var3 != null && var3.equals("editSelfSkills") ? var3 : null, "editSelfSkills", "selfChanceSkills", Config.SELF_SKILL_CHANCE, var5);
            var82 = b(
               var1, var3 != null && var3.equals("editSelfPercent") ? var3 : null, "editSelfPercent", "selfSkillsPercent", Config.SELF_SKILL_PERCENT, var5
            );
            boolean var114 = var2.isRndSelfSkills();
            if (var114) {
               var86 = var86.replace("%rndSelfSk_img%", "L2UI.CheckBox_checked");
            } else {
               var86 = var86.replace("%rndSelfSk_img%", "L2UI.CheckBox");
            }

            var86 = var86.replace("%rndSelfSk_bypass%", "bypass -h user_editFarmOption " + var5 + " farmRndSelfSkills");
            break;
         case "heal":
            var85 = var2.getLowLifeSpells();
            var86 = HtmCache.getInstance().getNotNull("command/autofarm/index-skill_heal.htm", var1);
            var83 = b(var1, var3 != null && var3.equals("editHealSkills") ? var3 : null, "editHealSkills", "healChanceSkills", Config.HEAL_SKILL_CHANCE, var5);
            var84 = b(
               var1,
               var3 != null && var3.equals("editLowHealPercent") ? var3 : null,
               "editLowHealPercent",
               "healSkillsPercent",
               Config.HEAL_SKILL_PERCENT,
               var5
            );
            boolean var118 = var2.isRndLifeSkills();
            if (var118) {
               var86 = var86.replace("%rndLifeSk_img%", "L2UI.CheckBox_checked");
            } else {
               var86 = var86.replace("%rndLifeSk_img%", "L2UI.CheckBox");
            }

            var86 = var86.replace("%rndLifeSk_bypass%", "bypass -h user_editFarmOption " + var5 + " farmRndLifeSkills");
      }

      String var96 = "";
      String var105 = "";
      if (var85 != null) {
         ArrayList var107 = new ArrayList();

         for (Object __raw_var115_1364 : (Iterable<?>)(Object)(var85)) {
            int var115 = ((Number)__raw_var115_1364).intValue();
            if (var115 > 0) {
               Skill var119 = var1.getKnownSkill(var115);
               if (var119 == null) {
                  var107.add(var115);
               }
            }
         }

         if (!var107.isEmpty()) {
            for (Object __raw_var116_1374 : (Iterable<?>)(Object)(var107)) {
               int var116 = ((Number)__raw_var116_1374).intValue();
               var85.remove(Integer.valueOf(var116));
            }

            var107.clear();
         }

         String var112 = HtmCache.getInstance().getNotNull("command/autofarm/skill-template.htm", var1);

         for (int var117 = 0; var117 < 8; var117++) {
            var96 = var112;
            if (var85.size() > 0 && var117 < var85.size()) {
               int var120 = (Integer)var85.get(var117);
               if (var120 > 0) {
                  Skill var121 = var1.getKnownSkill(var120);
                  if (var121 != null) {
                     var96 = var96.replace("%icon%", var121.getIcon());
                     var96 = var96.replace("%background%", "");
                     var96 = var96.replace("%bypass%", "bypass -h user_removeSkill " + var5 + " " + var121.getId() + "");
                  } else {
                     var96 = var96.replace("%icon%", "L2UI_CH3.systemicon3_down");
                     var96 = var96.replace("%bypass%", "bypass -h user_addSkill " + var5 + "");
                  }
               } else {
                  var96 = var96.replace("%icon%", "L2UI_CH3.systemicon3_down");
                  var96 = var96.replace("%bypass%", "bypass -h user_addSkill " + var5 + "");
               }
            } else {
               var96 = var96.replace("%icon%", "L2UI_CH3.systemicon3_down");
               var96 = var96.replace("%bypass%", "bypass -h user_addSkill " + var5 + "");
            }

            var105 = var105 + var96;
         }
      }

      String var108 = a(var1, var3 != null && var3.equals("editDistance") ? var3 : null, "editDistance", "farmDistance", Config.SEARCH_DISTANCE, var5);
      String var113 = b(var1, var3 != null && var3.equals("editShortcutPage") ? var3 : null, "editShortcutPage", "shortcutPage", Config.SHORTCUT_PAGE, var5);
      var6 = var6.replace("%activate%", this.a(var1, var2));
      var6 = var6.replace("%skillType%", var5);
      var6 = var6.replace("%skillList%", var105);
      var6 = var6.replace("%skillsParam%", var86);
      var6 = var6.replace("%activeHwids%", this.getActivateHwids(var1));
      var6 = var6.replace("%refreshSkills%", "bypass -h user_refreshSkills " + var5 + "");
      var6 = var6.replace("%status%", var2.isAutofarming() ? "<font color=\"00FF00\">ON</font>" : "<font color=\"FF0000\">OFF</font>");
      var6 = var6.replace(
         "%button%",
         var2.isAutofarming()
            ? "<button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_farmstop\" value=\"OFF\">"
            : "<button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_farmstart\" value=\"ON\">"
      );
      var6 = var6.replace("%chanceAttack%", var77.equals("") ? "" : var77);
      var6 = var6.replace("%chanceChance%", var79.equals("") ? "" : var79);
      var6 = var6.replace("%chanceSelf%", var81.equals("") ? "" : var81);
      var6 = var6.replace("%chanceLowHeal%", var83.equals("") ? "" : var83);
      var6 = var6.replace("%percentAttack%", var78.equals("") ? "" : var78);
      var6 = var6.replace("%percentChance%", var80.equals("") ? "" : var80);
      var6 = var6.replace("%percentSelf%", var82.equals("") ? "" : var82);
      var6 = var6.replace("%percentLowHeal%", var84.equals("") ? "" : var84);
      var6 = var6.replace("%distance%", var108.equals("") ? "" : var108);
      var6 = var6.replace("%shortcutPage%", var113.equals("") ? "" : var113);
      var6 = var6.replace("%farmType%", this.a(var1, var4, var5));
      Functions.show(var6, var1, null);
   }

   public String getActivateHwids(Player var1) {
      if (Config.FARM_ACTIVE_LIMITS < 0) {
         return "<font color=\"LEVEL\">-<font>";
      } else {
         int var2 = AutoFarmManager.getInstance().getActiveFarms(Config.ALLOW_CHECK_HWID_LIMIT ? var1.getNetConnection().getHwid() : var1.getIP());
         if (var2 > 0) {
            return "<font color=\"LEVEL\">" + var2 + "<font>";
         } else {
            return var2 <= 0 && AutoFarmManager.getInstance().isNonCheckPlayer(var1.getObjectId())
               ? "<font color=\"00FF00\">" + var2 + "<font>"
               : "<a action=\"bypass -h user_expendLimit\"><font color=\"FF0000\">" + var2 + "<font></a>";
         }
      }
   }

   private String a(Player var1, AutoFarmContext var2) {
      String var3 = StringHolder.getInstance().getNotNull(var1, "AUTO_FARM_NO_LIMIT_TIME");
      String var4 = StringHolder.getInstance().getNotNull(var1, "AUTO_FARM_BUY_TIME");
      if (!Config.AUTO_FARM_FREE && (!Config.PREMIUM_FARM_FREE || !var1.hasBonus())) {
         if (!var2.isActiveAutofarm()) {
            return var4;
         }

         if (Config.FARM_ONLINE_TYPE) {
            long var7 = 0L;
            if (!var2.isActiveFarmTask()) {
               var7 = (var1.getVarLong("activeFarmOnlineTask", 0L) - var2.getLastFarmOnlineTime()) / 1000L;
            } else {
               var7 = (var1.getVarLong("activeFarmOnlineTask", 0L) - (var2.getLastFarmOnlineTime() + (System.currentTimeMillis() - var2.getFarmOnlineTime())))
                  / 1000L;
            }

            return "<font color=\"E6D0AE\">" + TimeUtils.formatTime((int)var7, false) + "</font>";
         } else {
            long var5 = (var2.getAutoFarmEnd() - System.currentTimeMillis()) / 1000L;
            return "<font color=\"E6D0AE\">" + TimeUtils.formatTime((int)var5, false) + "</font>";
         }
      } else {
         return var3;
      }
   }

   private void b(Player var1, AutoFarmContext var2) {
      String var3 = HtmCache.getInstance().getNotNull("command/autofarm/buy.htm", var1);
      String var4 = "";
      ArrayList var5 = new ArrayList();

      for (int var7 : Config.AUTO_FARM_PRICES.keySet()) {
         var5.add(var7);
      }

      AutoFarm.SortTimeInfo var12 = new AutoFarm.SortTimeInfo();
      Collections.sort(var5, var12);
      int var13 = 0;

      for (Object __raw_var9_1494 : (Iterable<?>)(Object)(var5)) {
         int var9 = ((Number)__raw_var9_1494).intValue();
         if (var13 > 0) {
            var4 = var4 + ";";
         }

         var4 = var4 + "" + var9 + "";
         var13++;
      }

      var3 = var3.replace("%time%", var4);
      var3 = var3.replace("%freeUse%", this.c(var1, var2));
      var5.clear();
      Functions.show(var3, var1, null);
   }

   private String c(Player var1, AutoFarmContext var2) {
      if (!var2.isActiveAutofarm() && Config.ALLOW_FARM_FREE_TIME) {
         long var3 = var1.getVarLong("farmFreeTime", 0L);
         return var3 <= 0L
            ? "<button value=\"Try free for "
               + Config.FARM_FREE_TIME
               + " hour(s)\" action=\"bypass -h user_tryFreeTime\" width=120 height=25 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\""
            : "";
      } else {
         return "";
      }
   }

   private String a(Player var1, int var2, String var3) {
      String var4 = "<td aling=center width=20>";
      int var5 = 0;
      String var6 = "";
      switch (var2) {
         case 0:
            var4 = var4 + "<img src=\"L2UI_CH3.party_styleicon1_3\" width=16 height=16></td>";
            var6 = "Fighter";
            var5++;
            break;
         case 1:
            var4 = var4 + "<img src=\"L2UI_CH3.party_styleicon2_3\" width=16 height=16></td>";
            var6 = "Archer";
            var5 = 2;
            break;
         case 2:
            var4 = var4 + "<img src=\"L2UI_CH3.party_styleicon5_3\" width=16 height=16></td>";
            var6 = "Magic";
            var5 = 3;
            break;
         case 3:
            var4 = var4 + "<img src=\"L2UI_CH3.party_styleicon6_3\" width=16 height=16></td>";
            var6 = "Healer";
            var5 = 4;
            break;
         case 4:
            var4 = var4 + "<img src=\"L2UI_CH3.party_styleicon7_3\" width=16 height=16></td>";
            var6 = "Summon";
      }

      return var4
         + "<td width=90>"
         + var6
         + "</td><td width=60><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm edit_farmType "
         + var5
         + " "
         + var3
         + "\" value=\"Edit\"></td>";
   }

   private static String a(Player var0, String var1, String var2, String var3, int var4, String var5) {
      String var6 = "";
      if (var1 == null || var1.isEmpty()) {
         var6 = var6 + "<td aling=center width=50><font color=c1b33a>" + var0.getVarInt(var3, var4) + "</font></td>";
         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm edit_farm "
            + var2
            + " "
            + var5
            + "\" value=\"Edit\"></td>";
      } else if (var1.equals("editDistance")) {
         var6 = var6 + "<td width=50><edit var=\"" + var1 + "\" width=40 height=12></td>";
         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_distance $editDistance "
            + var5
            + "\" value=\"Save\"></td>";
      }

      return var6;
   }

   private static String b(Player var0, String var1, String var2, String var3, int var4, String var5) {
      String var6 = "";
      if (var1 != null && !var1.isEmpty()) {
         if (var1.equals("editAttackSkills")) {
            var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
            var6 = var6
               + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_attackSkills $editAttackSkills "
               + var5
               + "\" value=\"Save\"></td>";
         } else if (var1.equals("editChanceSkills")) {
            var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
            var6 = var6
               + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_chanceSkills $editChanceSkills "
               + var5
               + "\" value=\"Save\"></td>";
         } else if (var1.equals("editSelfSkills")) {
            var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
            var6 = var6
               + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_selfSkills $editSelfSkills "
               + var5
               + "\" value=\"Save\"></td>";
         } else if (var1.equals("editHealSkills")) {
            var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
            var6 = var6
               + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_healSkills $editHealSkills "
               + var5
               + "\" value=\"Save\"></td>";
         } else if (var1.equals("editAttackPercent")) {
            var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
            var6 = var6
               + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_attackSkillsPercent $editAttackPercent "
               + var5
               + "\" value=\"Save\"></td>";
         } else if (var1.equals("editChancePercent")) {
            var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
            var6 = var6
               + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_chanceSkillsPercent $editChancePercent "
               + var5
               + "\" value=\"Save\"></td>";
         } else if (var1.equals("editSelfPercent")) {
            var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
            var6 = var6
               + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_selfSkillsPercent $editSelfPercent "
               + var5
               + "\" value=\"Save\"></td>";
         } else if (var1.equals("editLowHealPercent")) {
            var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
            var6 = var6
               + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_healSkillsPercent $editLowHealPercent "
               + var5
               + "\" value=\"Save\"></td>";
         } else if (var1.equals("editShortcutPage")) {
            var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
            var6 = var6
               + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm set_shortcutPage $editShortcutPage "
               + var5
               + "\" value=\"Save\"></td>";
         }
      } else {
         if (var3.equals("shortcutPage")) {
            var6 = var6 + "<td aling=center width=45><font color=c1b33a>" + var0.getVarInt(var3, var4) + "</font></td>";
         } else {
            var6 = var6 + "<td aling=center width=45><font color=c1b33a>" + var0.getVarInt(var3, var4) + "%</font></td>";
         }

         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autofarm edit_farm "
            + var2
            + " "
            + var5
            + "\" value=\"Edit\"></td>";
      }

      return var6;
   }

   private static String c(Player var0, String var1, String var2, String var3, int var4, String var5) {
      String var6 = "";
      if (var1 == null || var1.isEmpty()) {
         var6 = var6 + "<td aling=center width=45><font color=c1b33a>" + var0.getVarInt(var3, var4) + "%</font></td>";
         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autosummonfarm edit_farm "
            + var2
            + " "
            + var5
            + "\" value=\"Edit\"></td>";
      } else if (var1.equals("editSummonAttackSkills")) {
         var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autosummonfarm set_attackSummonSkills $editSummonAttackSkills "
            + var5
            + "\" value=\"Save\"></td>";
      } else if (var1.equals("editSummonSelfSkills")) {
         var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autosummonfarm set_selfSummonSkills $editSummonSelfSkills "
            + var5
            + "\" value=\"Save\"></td>";
      } else if (var1.equals("editSummonHealSkills")) {
         var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autosummonfarm set_healSummonSkills $editSummonHealSkills "
            + var5
            + "\" value=\"Save\"></td>";
      } else if (var1.equals("editSummonAttackPercent")) {
         var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autosummonfarm set_attackSummonSkillsPercent $editSummonAttackPercent "
            + var5
            + "\" value=\"Save\"></td>";
      } else if (var1.equals("editSummonSelfPercent")) {
         var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autosummonfarm set_selfSummonSkillsPercent $editSummonSelfPercent "
            + var5
            + "\" value=\"Save\"></td>";
      } else if (var1.equals("editSummonLowHealPercent")) {
         var6 = var6 + "<td width=43><edit var=\"" + var1 + "\" width=34 height=12></td>";
         var6 = var6
            + "<td width=40><button width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h user_autosummonfarm set_healSummonSkillsPercent $editSummonLowHealPercent "
            + var5
            + "\" value=\"Save\"></td>";
      }

      return var6;
   }

   private void a(Player var1, AutoFarmContext var2, String var3, int var4) {
      List var5 = null;
      ArrayList var6 = new ArrayList();
      switch (var3) {
         case "attack":
            var5 = var2.getAttackSpells();

            for (Skill var39 : var1.getAllSkills()) {
               if (var39 != null
                  && !var39.isPassive()
                  && !var39.isSpoilSkill()
                  && !var39.isSweepSkill()
                  && var39.getId() != 1263
                  && (
                     var39.getSkillType() == Skill.SkillType.AGGRESSION
                        || var39.getSkillType() == Skill.SkillType.PDAM
                        || var39.getSkillType() == Skill.SkillType.MANADAM
                        || var39.getSkillType() == Skill.SkillType.MDAM
                        || var39.getSkillType() == Skill.SkillType.DRAIN
                        || var39.getSkillType() == Skill.SkillType.CPDAM
                        || var39.getSkillType() == Skill.SkillType.STUN
                  )) {
                  var6.add(var39);
               }
            }
            break;
         case "chance":
            var5 = var2.getChanceSpells();

            for (Skill var38 : var1.getAllSkills()) {
               if (var38 != null
                  && !var38.isPassive()
                  && (
                     var38.getSkillType() == Skill.SkillType.DOT
                        || var38.getSkillType() == Skill.SkillType.MDOT
                        || var38.getSkillType() == Skill.SkillType.POISON
                        || var38.getSkillType() == Skill.SkillType.BLEED
                        || var38.getSkillType() == Skill.SkillType.DEBUFF
                        || var38.getSkillType() == Skill.SkillType.SLEEP
                        || var38.getSkillType() == Skill.SkillType.ROOT
                        || var38.getSkillType() == Skill.SkillType.PARALYZE
                        || var38.getSkillType() == Skill.SkillType.MUTE
                        || var38.isSpoilSkill()
                        || var38.isSweepSkill()
                        || var38.getId() == 1263
                  )) {
                  var6.add(var38);
               }
            }
            break;
         case "self":
            var5 = var2.getSelfSpells();

            for (Skill var37 : var1.getAllSkills()) {
               if (var37 != null && !var37.isPassive() && (var37.isToggle() || var37.isMusic() || var37.getSkillType() == Skill.SkillType.BUFF)
                  || var37.isCubicSkill()) {
                  var6.add(var37);
               }
            }
            break;
         case "heal":
            var5 = var2.getLowLifeSpells();

            for (Skill var10 : var1.getAllSkills()) {
               if (var10 != null
                  && !var10.isPassive()
                  && (
                     var10.getSkillType() == Skill.SkillType.DRAIN
                        || var10.getSkillType() == Skill.SkillType.HEAL
                        || var10.getSkillType() == Skill.SkillType.HEAL_PERCENT
                        || var10.getSkillType() == Skill.SkillType.MANAHEAL
                        || var10.getSkillType() == Skill.SkillType.MANAHEAL_PERCENT
                  )) {
                  var6.add(var10);
               }
            }
      }

      if (var6.isEmpty()) {
         var1.sendMessage("You have no valid skills!");
         this.a(var1, var2, null, var1.getVarInt("farmType", Config.FARM_TYPE), var3);
      } else {
         if (var5.size() > 0) {
            ArrayList var18 = new ArrayList();

            for (Object __raw_var30_1795 : (Iterable<?>)(Object)(var6)) {
               Skill var30 = (Skill)__raw_var30_1795;
               if (var5.contains(var30.getId())) {
                  var18.add(var30);
               }
            }

            if (!var18.isEmpty()) {
               for (Object __raw_var31_1802 : (Iterable<?>)(Object)(var18)) {
                  Skill var31 = (Skill)__raw_var31_1802;
                  var6.remove(var31);
               }

               var18.clear();
            }
         }

         String var19 = HtmCache.getInstance().getNotNull("command/autofarm/player_skills.htm", var1);
         String var26 = HtmCache.getInstance().getNotNull("command/autofarm/player_skills_template.htm", var1);
         String var32 = "";
         String var40 = "";
         int var12 = 0;
         int var13 = var6.size();
         boolean var14 = var13 > 5;

         for (int var15 = (var4 - 1) * 5; var15 < var13; var15++) {
            Skill var16 = (Skill)var6.get(var15);
            if (var16 != null) {
               var32 = var26;
               var32 = var32.replace("%name%", var16.getName());
               var32 = var32.replace("%icon%", var16.getIcon());
               var32 = var32.replace("%bypass%", "bypass -h user_addNewSkill " + var16.getId() + " " + var3 + "");
               var40 = var40 + var32;
            }

            if (++var12 >= 5) {
               break;
            }
         }

         double var41 = var13 / 5.0;
         int var17 = (int)Math.ceil(var41);
         var19 = var19.replace("%list%", var40);
         var19 = var19.replace("%page%", String.valueOf(var4));
         var19 = var19.replace("%skillType%", var3);
         var19 = var19.replace("%navigation%", Util.getNavigationBlock(var17, var4, var13, 5, var14, "user_addSkill " + var3 + " %s"));
         Functions.show(var19, var1, null);
         var6.clear();
      }
   }

   private void a(Player var1, AutoFarmContext var2, String var3, int var4, String var5, String var6) {
      if (var1.getPet() == null) {
         var1.sendMessage("You can't use this option!");
         this.a(var1, var2, null, var4, var5, var6);
      } else {
         Summon var7 = var1.getPet();
         if (var7.isPet() && var7.getLevel() - var1.getLevel() > 20) {
            var1.sendPacket(SystemMsg.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
            this.a(var1, var2, null, var4, var5, var6);
         } else {
            String var8 = HtmCache.getInstance().getNotNull("command/autofarm/index-summonSkills.htm", var1);
            List var9 = null;
            String var10 = null;
            String var11 = "";
            String var12 = "";
            String var13 = "";
            String var14 = "";
            String var15 = "";
            String var16 = "";
            switch (var5) {
               case "attack":
                  var9 = var2.getSummonAttackSpells();
                  var10 = HtmCache.getInstance().getNotNull("command/autofarm/index-summon_skill_attack.htm", var1);
                  var11 = c(
                     var1,
                     var3 != null && var3.equals("editSummonAttackSkills") ? var3 : null,
                     "editSummonAttackSkills",
                     "attackSummonChanceSkills",
                     Config.SUMMON_ATTACK_SKILL_CHANCE,
                     var5
                  );
                  var12 = c(
                     var1,
                     var3 != null && var3.equals("editSummonAttackPercent") ? var3 : null,
                     "editSummonAttackPercent",
                     "attackSummonSkillsPercent",
                     Config.SUMMON_ATTACK_SKILL_PERCENT,
                     var5
                  );
                  boolean var19 = var2.isRndSummonAttackSkills();
                  if (var19) {
                     var10 = var10.replace("%rndAttackSk_img%", "L2UI.CheckBox_checked");
                  } else {
                     var10 = var10.replace("%rndAttackSk_img%", "L2UI.CheckBox");
                  }

                  var10 = var10.replace("%rndAttackSk_bypass%", "bypass -h user_editSummonFarmOption " + var5 + " farmRndSummonAttackSkills " + var6);
                  break;
               case "self":
                  var9 = var2.getSummonSelfSpells();
                  var10 = HtmCache.getInstance().getNotNull("command/autofarm/index-summon_skill_self.htm", var1);
                  var13 = c(
                     var1,
                     var3 != null && var3.equals("editSummonSelfSkills") ? var3 : null,
                     "editSummonSelfSkills",
                     "selfSummonChanceSkills",
                     Config.SUMMON_SELF_SKILL_CHANCE,
                     var5
                  );
                  var14 = c(
                     var1,
                     var3 != null && var3.equals("editSummonSelfPercent") ? var3 : null,
                     "editSummonSelfPercent",
                     "selfSummonSkillsPercent",
                     Config.SUMMON_SELF_SKILL_PERCENT,
                     var5
                  );
                  boolean var20 = var2.isRndSummonSelfSkills();
                  if (var20) {
                     var10 = var10.replace("%rndSelfSk_img%", "L2UI.CheckBox_checked");
                  } else {
                     var10 = var10.replace("%rndSelfSk_img%", "L2UI.CheckBox");
                  }

                  var10 = var10.replace("%rndSelfSk_bypass%", "bypass -h user_editSummonFarmOption " + var5 + " farmRndSummonSelfSkills " + var6);
                  break;
               case "heal":
                  var9 = var2.getSummonHealSpells();
                  var10 = HtmCache.getInstance().getNotNull("command/autofarm/index-summon_skill_heal.htm", var1);
                  var15 = c(
                     var1,
                     var3 != null && var3.equals("editSummonHealSkills") ? var3 : null,
                     "editSummonHealSkills",
                     "healSummonChanceSkills",
                     Config.SUMMON_HEAL_SKILL_CHANCE,
                     var5
                  );
                  var16 = c(
                     var1,
                     var3 != null && var3.equals("editSummonLowHealPercent") ? var3 : null,
                     "editSummonLowHealPercent",
                     "healSummonSkillsPercent",
                     Config.SUMMON_HEAL_SKILL_PERCENT,
                     var5
                  );
                  boolean var21 = var2.isRndSummonLifeSkills();
                  if (var21) {
                     var10 = var10.replace("%rndLifeSk_img%", "L2UI.CheckBox_checked");
                  } else {
                     var10 = var10.replace("%rndLifeSk_img%", "L2UI.CheckBox");
                  }

                  var10 = var10.replace("%rndLifeSk_bypass%", "bypass -h user_editSummonFarmOption " + var5 + " farmRndSummonLifeSkills " + var6);
            }

            String var41 = "";
            String var52 = "";
            if (var9 != null && !var9.isEmpty()) {
               ArrayList var53 = new ArrayList();

               for (Object __raw_var59_1954 : (Iterable<?>)(Object)(var9)) {
                  int var59 = ((Number)__raw_var59_1954).intValue();
                  if (var59 > 0) {
                     int var22 = PetSkillsTable.getInstance().getAvailableLevel(var7, var59);
                     if (var22 == 0) {
                        var53.add(var59);
                     }
                  }
               }

               if (!var53.isEmpty()) {
                  for (Object __raw_var60_1964 : (Iterable<?>)(Object)(var53)) {
                     int var60 = ((Number)__raw_var60_1964).intValue();
                     var9.remove(Integer.valueOf(var60));
                  }

                  var53.clear();
               }
            }

            String var54 = HtmCache.getInstance().getNotNull("command/autofarm/summon_skill-template.htm", var1);

            for (int var57 = 0; var57 < 8; var57++) {
               var41 = var54;
               if (var9.size() > 0 && var57 < var9.size()) {
                  int var61 = (Integer)var9.get(var57);
                  if (var61 > 0) {
                     int var62 = PetSkillsTable.getInstance().getAvailableLevel(var7, var61);
                     if (var62 > 0) {
                        var41 = var41.replace("%icon%", SkillTable.getInstance().getInfo(var61, var62).getIcon());
                        var41 = var41.replace("%background%", "");
                        var41 = var41.replace("%bypass%", "bypass -h user_removeSummonSkill " + var5 + " " + var61 + " " + var6);
                     } else {
                        var41 = var41.replace("%icon%", "icon.high_tab");
                        var41 = var41.replace("%background%", "background=\"l2ui_ch3.multisell_plusicon\"");
                        var41 = var41.replace("%bypass%", "bypass -h user_addSummonSkill " + var5 + " " + var6);
                     }
                  } else {
                     var41 = var41.replace("%icon%", "icon.high_tab");
                     var41 = var41.replace("%background%", "background=\"l2ui_ch3.multisell_plusicon\"");
                     var41 = var41.replace("%bypass%", "bypass -h user_addSummonSkill " + var5 + " " + var6);
                  }
               } else {
                  var41 = var41.replace("%icon%", "icon.high_tab");
                  var41 = var41.replace("%background%", "background=\"l2ui_ch3.multisell_plusicon\"");
                  var41 = var41.replace("%bypass%", "bypass -h user_addSummonSkill " + var5 + " " + var6);
               }

               var52 = var52 + var41;
            }

            boolean var58 = var2.isExtraSummonDelaySkill();
            if (var58) {
               var8 = var8.replace("%delaySk_img%", "L2UI.CheckBox_checked");
            } else {
               var8 = var8.replace("%delaySk_img%", "L2UI.CheckBox");
            }

            var8 = var8.replace("%delaySk_bypass%", "bypass -h user_editSummonFarmOption " + var5 + " farmSummonDelaySkills " + var6);
            var8 = var8.replace("%skillType%", var6);
            var8 = var8.replace("%skillsParam%", var10);
            var8 = var8.replace("%summonSkillType%", var5);
            var8 = var8.replace("%skillList%", var52);
            var8 = var8.replace("%chanceAttack%", var11.equals("") ? "" : var11);
            var8 = var8.replace("%chanceSelf%", var13.equals("") ? "" : var13);
            var8 = var8.replace("%chanceLowHeal%", var15.equals("") ? "" : var15);
            var8 = var8.replace("%percentAttack%", var12.equals("") ? "" : var12);
            var8 = var8.replace("%percentSelf%", var14.equals("") ? "" : var14);
            var8 = var8.replace("%percentLowHeal%", var16.equals("") ? "" : var16);
            Functions.show(var8, var1, null);
         }
      }
   }

   private void a(Player var1, AutoFarmContext var2, int var3, String var4, String var5, int var6) {
      if (var1.getPet() == null) {
         var1.sendMessage("You can't use this option!");
         this.a(var1, var2, null, var3, var4, var5);
      } else {
         Summon var7 = var1.getPet();
         if (var7.isPet() && var7.getLevel() - var1.getLevel() > 20) {
            var1.sendPacket(SystemMsg.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
            this.a(var1, var2, null, var3, var4, var5);
         } else {
            List var8 = null;
            ArrayList var9 = new ArrayList();
            switch (var4) {
               case "attack":
                  var8 = var2.getSummonAttackSpells();

                  for (int var41 : PetSkillsTable.getInstance().getAvailableSkills(var7)) {
                     int var44 = PetSkillsTable.getInstance().getAvailableLevel(var7, var41);
                     if (var44 > 0) {
                        Skill var46 = SkillTable.getInstance().getInfo(var41, var44);
                        if (var46 != null
                           && (
                              var46.getSkillType() == Skill.SkillType.AGGRESSION
                                 || var46.getSkillType() == Skill.SkillType.PDAM
                                 || var46.getSkillType() == Skill.SkillType.MANADAM
                                 || var46.getSkillType() == Skill.SkillType.MDAM
                                 || var46.getSkillType() == Skill.SkillType.DRAIN
                                 || var46.getSkillType() == Skill.SkillType.CPDAM
                                 || var46.getSkillType() == Skill.SkillType.STUN
                           )) {
                           var9.add(var46);
                        }
                     }
                  }
                  break;
               case "self":
                  var8 = var2.getSummonSelfSpells();

                  for (int var40 : PetSkillsTable.getInstance().getAvailableSkills(var7)) {
                     int var43 = PetSkillsTable.getInstance().getAvailableLevel(var7, var40);
                     if (var43 > 0) {
                        Skill var45 = SkillTable.getInstance().getInfo(var40, var43);
                        if (var45 != null && (var45.isToggle() || var45.isMusic() || var45.getSkillType() == Skill.SkillType.BUFF) || var45.isCubicSkill()) {
                           var9.add(var45);
                        }
                     }
                  }
                  break;
               case "heal":
                  var8 = var2.getSummonHealSpells();

                  for (int var13 : PetSkillsTable.getInstance().getAvailableSkills(var7)) {
                     int var14 = PetSkillsTable.getInstance().getAvailableLevel(var7, var13);
                     if (var14 > 0) {
                        Skill var15 = SkillTable.getInstance().getInfo(var13, var14);
                        if (var15 != null
                           && (
                              var15.getSkillType() == Skill.SkillType.DRAIN
                                 || var15.getSkillType() == Skill.SkillType.HEAL
                                 || var15.getSkillType() == Skill.SkillType.HEAL_PERCENT
                                 || var15.getSkillType() == Skill.SkillType.MANAHEAL
                                 || var15.getSkillType() == Skill.SkillType.MANAHEAL_PERCENT
                           )) {
                           var9.add(var15);
                        }
                     }
                  }
            }

            if (var8.size() > 0) {
               ArrayList var21 = new ArrayList();

               for (Object __raw_var33_2098 : (Iterable<?>)(Object)(var9)) {
                  Skill var33 = (Skill)__raw_var33_2098;
                  if (var8.contains(var33.getId())) {
                     var21.add(var33);
                  }
               }

               if (!var21.isEmpty()) {
                  for (Object __raw_var34_2105 : (Iterable<?>)(Object)(var21)) {
                     Skill var34 = (Skill)__raw_var34_2105;
                     var9.remove(var34);
                  }

                  var21.clear();
               }
            }

            if (var9.isEmpty()) {
               var1.sendMessage("Your summon has no valid skills!");
               this.a(var1, var2, null, var3, var4, var5);
            } else {
               String var22 = HtmCache.getInstance().getNotNull("command/autofarm/summon_skills.htm", var1);
               String var30 = HtmCache.getInstance().getNotNull("command/autofarm/summon_skills_template.htm", var1);
               String var35 = "";
               String var42 = "";
               int var47 = 0;
               int var16 = var9.size();
               boolean var17 = var16 > 5;

               for (int var18 = (var6 - 1) * 5; var18 < var16; var18++) {
                  Skill var19 = (Skill)var9.get(var18);
                  if (var19 != null) {
                     var35 = var30;
                     var35 = var35.replace("%name%", var19.getName());
                     var35 = var35.replace("%icon%", var19.getIcon());
                     var35 = var35.replace("%bypass%", "bypass -h user_addNewSummonSkill " + var19.getId() + " " + var4 + " " + var5 + "");
                     var42 = var42 + var35;
                  }

                  if (++var47 >= 5) {
                     break;
                  }
               }

               double var48 = var16 / 5.0;
               int var20 = (int)Math.ceil(var48);
               var22 = var22.replace("%list%", var42);
               var22 = var22.replace("%page%", String.valueOf(var6));
               var22 = var22.replace("%summonSkillType%", var4);
               var22 = var22.replace("%skillType%", var5);
               var22 = var22.replace("%navigation%", Util.getNavigationBlock(var20, var6, var16, 5, var17, "user_addSummonSkill " + var4 + " " + var5 + " %s"));
               Functions.show(var22, var1, null);
               var9.clear();
            }
         }
      }
   }

   @Override
   public String[] getVoicedCommandList() {
      return zG;
   }

   private static class SortTimeInfo implements Serializable, Comparator<Integer> {
      private static final long serialVersionUID = 7691414259610932752L;

      private SortTimeInfo() {
      }

      public int compare(Integer var1, Integer var2) {
         return Double.compare(var1.intValue(), var2.intValue());
      }
   }
}
