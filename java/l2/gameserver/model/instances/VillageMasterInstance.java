package l2.gameserver.model.instances;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.ClassType;
import l2.gameserver.model.base.PlayerClass;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PledgeReceiveSubPledgeCreated;
import l2.gameserver.network.l2.s2c.PledgeShowInfoUpdate;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListUpdate;
import l2.gameserver.network.l2.s2c.PledgeStatusChanged;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.SiegeUtils;
import l2.gameserver.utils.Util;

public final class VillageMasterInstance extends NpcInstance {
   public VillageMasterInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
   }

   @Override
   public void onBypassFeedback(Player var1, String var2) {
      if (canBypassCheck(var1, this)) {
         if (var2.equals("create_clan_check")) {
            if (var1.getLevel() < Config.CHARACTER_MIN_LEVEL_FOR_CLAN_CREATE) {
               this.showChatWindow(var1, "villagemaster/pl002.htm");
            } else if (var1.isClanLeader()) {
               this.showChatWindow(var1, "villagemaster/pl003.htm");
            } else if (var1.getClan() != null) {
               this.showChatWindow(var1, "villagemaster/pl004.htm");
            } else {
               this.showChatWindow(var1, "villagemaster/pl005.htm");
            }
         } else if (var2.equals("disband_clan_check")) {
            if (b(this, var1)) {
               this.showChatWindow(var1, "villagemaster/pl007.htm");
            }
         } else if (var2.equals("restore_clan_check")) {
            if (b(this, var1)) {
               this.showChatWindow(var1, "villagemaster/pl010.htm");
            }
         } else if (var2.startsWith("create_clan") && var2.length() > 12) {
            String var25 = var2.substring(12);
            this.a(this, var1, var25);
         } else if (var2.startsWith("create_academy") && var2.length() > 15) {
            String var24 = var2.substring(15, var2.length());
            this.a(var1, var24, -1, 5, "");
         } else if (var2.startsWith("create_royal") && var2.length() > 15) {
            String[] var23 = var2.substring(13, var2.length()).split(" ", 2);
            if (var23.length == 2) {
               this.a(var1, var23[1], 100, 6, var23[0]);
            }
         } else if (var2.startsWith("create_knight") && var2.length() > 16) {
            String[] var22 = var2.substring(14, var2.length()).split(" ", 2);
            if (var22.length == 2) {
               this.a(var1, var22[1], 1001, 7, var22[0]);
            }
         } else if (var2.startsWith("assign_subpl_leader") && var2.length() > 22) {
            String[] var21 = var2.substring(20, var2.length()).split(" ", 2);
            if (var21.length == 2) {
               this.a(var1, var21[1], var21[0]);
            }
         } else if (var2.startsWith("assign_new_clan_leader") && var2.length() > 23) {
            String var20 = var2.substring(23);
            this.n(var1, var20);
         } else if (var2.startsWith("cancel_new_clan_leader")) {
            this.V(var1);
         } else if (var2.startsWith("create_ally") && var2.length() > 12) {
            String var19 = var2.substring(12);
            this.o(var1, var19);
         } else if (var2.startsWith("dissolve_ally")) {
            this.dissolveAlly(var1);
         } else if (var2.startsWith("dissolve_clan")) {
            c(this, var1);
         } else if (var2.startsWith("restore_clan")) {
            a(this, var1);
         } else if (var2.startsWith("increase_clan_level")) {
            this.W(var1);
         } else if (var2.startsWith("learn_clan_skills")) {
            showClanSkillList(var1);
         } else if (var2.startsWith("ShowCouponExchange")) {
            if (Functions.getItemCount(var1, 8869) <= 0L && Functions.getItemCount(var1, 8870) <= 0L) {
               var2 = "Link villagemaster/reflect_weapon_master_noticket.htm";
            } else {
               var2 = "Multisell 800";
            }

            super.onBypassFeedback(var1, var2);
         } else if (var2.startsWith("Subclass")) {
            if (var1.getPet() != null) {
               var1.sendPacket(SystemMsg.A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED);
               return;
            }

            if (var1.isActionsDisabled() || var1.getTransformation() != 0 || var1.isCursedWeaponEquipped()) {
               var1.sendPacket(SystemMsg.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
               return;
            }

            if (var1.getWeightPenalty() >= 3) {
               var1.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
               return;
            }

            if (var1.getInventoryLimit() * 0.8 < var1.getInventory().getSize()) {
               var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.InventoryLimit", var1));
               return;
            }

            StringBuilder var3 = new StringBuilder("<html><body>");
            NpcHtmlMessage var4 = new NpcHtmlMessage(var1, this);
            Map var5 = var1.getSubClasses();
            if (var1.getLevel() < 40) {
               var3.append("You must be level 40 or more to operate with your sub-classes.");
               var3.append("</body></html>");
               var4.setHtml(var3.toString());
               var1.sendPacket(var4);
               return;
            }

            int var7 = 0;
            int var8 = 0;
            int var9 = 0;

            try {
               for (String var13 : var2.substring(9, var2.length()).split(" ")) {
                  if (var9 == 0) {
                     var9 = Integer.parseInt(var13);
                  } else if (var7 > 0) {
                     var8 = Integer.parseInt(var13);
                  } else {
                     var7 = Integer.parseInt(var13);
                  }
               }
            } catch (Exception var17) {
               var17.printStackTrace();
            }

            switch (var9) {
               case 1:
                  Set var26 = this.e(var1, true);
                  if (var26 == null || var26.isEmpty()) {
                     var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", var1));
                     return;
                  }

                  var3.append("Add Subclass:<br>Which subclass do you wish to add?<br>");
                  if (Config.ALT_ALLOW_SUBCLASS_FOR_CUSTOM_ITEM && !var1.getVarB("SubclassCustomItem")) {
                     var3.append(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubClassPriceForCustomItem", var1));
                     var3.append("<br>");
                  }

                  for (Object __raw_var32_178 : (Iterable<?>)(Object)(var26)) {
                     PlayerClass var32 = (PlayerClass)__raw_var32_178;
                     var3.append("<a action=\"bypass -h npc_")
                        .append(this.getObjectId())
                        .append("_Subclass 4 ")
                        .append(var32.ordinal())
                        .append("\">")
                        .append(HtmlUtils.htmlClassName(var32.ordinal(), var1))
                        .append("</a><br>");
                  }
                  break;
               case 2:
                  var3.append("Change Subclass:<br>");
                  int var27 = var1.getBaseClassId();
                  if (var5.size() < 2) {
                     var3.append("You can't change subclasses when you don't have a subclass to begin with.<br><a action=\"bypass -h npc_")
                        .append(this.getObjectId())
                        .append("_Subclass 1\">Add subclass.</a>");
                  } else {
                     var3.append("Which class would you like to switch to?<br>");
                     if (var27 == var1.getActiveClassId()) {
                        var3.append(HtmlUtils.htmlClassName(var27, var1)).append(" <font color=\"LEVEL\">(Base Class)</font><br><br>");
                     } else {
                        var3.append("<a action=\"bypass -h npc_")
                           .append(this.getObjectId())
                           .append("_Subclass 5 ")
                           .append(var27)
                           .append("\">")
                           .append(HtmlUtils.htmlClassName(var27, var1))
                           .append("</a> <font color=\"LEVEL\">(Base Class)</font><br><br>");
                     }

                     for (Object __raw_var38_209 : (Iterable<?>)(Object)(var5.values())) {
                        SubClass var38 = (SubClass)__raw_var38_209;
                        if (!var38.isBase()) {
                           int var44 = var38.getClassId();
                           if (var44 == var1.getActiveClassId()) {
                              var3.append(HtmlUtils.htmlClassName(var44, var1)).append("<br>");
                           } else {
                              var3.append("<a action=\"bypass -h npc_")
                                 .append(this.getObjectId())
                                 .append("_Subclass 5 ")
                                 .append(var44)
                                 .append("\">")
                                 .append(HtmlUtils.htmlClassName(var44, var1))
                                 .append("</a><br>");
                           }
                        }
                     }
                  }
                  break;
               case 3:
                  var3.append("Change Subclass:<br>Which of the following sub-classes would you like to change?<br>");

                  for (Object __raw_var37_230 : (Iterable<?>)(Object)(var5.values())) {
                     SubClass var37 = (SubClass)__raw_var37_230;
                     var3.append("<br>");
                     if (!var37.isBase()) {
                        var3.append("<a action=\"bypass -h npc_")
                           .append(this.getObjectId())
                           .append("_Subclass 6 ")
                           .append(var37.getClassId())
                           .append("\">")
                           .append(HtmlUtils.htmlClassName(var37.getClassId(), var1))
                           .append("</a><br>");
                     }
                  }

                  var3.append("<br>If you change a sub-class, you'll start at level 40 after the 2nd class transfer.");
                  break;
               case 4:
                  boolean var29 = true;
                  if (var1.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS) {
                     var1.sendMessage(
                        new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", var1)
                           .addNumber(Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)
                     );
                     var29 = false;
                  }

                  if (!var5.isEmpty()) {
                     for (Object __raw_var41_256 : (Iterable<?>)(Object)(var5.values())) {
                        SubClass var41 = (SubClass)__raw_var41_256;
                        if (var41.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS) {
                           var1.sendMessage(
                              new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", var1)
                                 .addNumber(Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)
                           );
                           var29 = false;
                           break;
                        }
                     }
                  }

                  if (var1.isInDuel()) {
                     var29 = false;
                  }

                  if (Config.OLY_ENABLED && (ParticipantPool.getInstance().isRegistred(var1) || var1.isOlyParticipant())) {
                     var1.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
                     return;
                  }

                  if (!Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS && !var5.isEmpty() && var5.size() < 2) {
                     if (!Config.ALT_GAME_SUBCLASS_NOT_CHECK_QUEST_234 && !var1.isQuestCompleted("_234_FatesWhisper")) {
                        var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.QuestFatesWhisper", var1));
                        var29 = false;
                     } else {
                        var29 = var1.isQuestCompleted("_235_MimirsElixir");
                        if (!var29) {
                           var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.QuestMimirsElixir", var1));
                        }
                     }
                  }

                  if (Config.ALT_ALLOW_SUBCLASS_FOR_CUSTOM_ITEM && var29 && !var1.getVarB("SubclassCustomItem")) {
                     boolean var36 = true;

                     for (int var42 = 0; var42 < Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID.length; var42++) {
                        int var45 = Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID[var42];
                        long var47 = Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_COUNT[var42];
                        if (ItemFunctions.getItemCount(var1, var45) < var47) {
                           var36 = false;
                           break;
                        }
                     }

                     if (!var36) {
                        var1.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                        return;
                     }

                     for (int var43 = 0; var43 < Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID.length; var43++) {
                        int var46 = Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID[var43];
                        long var48 = Config.ALT_SUBCLASS_FOR_CUSTOM_ITEM_COUNT[var43];
                        if (ItemFunctions.removeItem(var1, var46, var48, true) < var48) {
                           var36 = false;
                           break;
                        }
                     }

                     if (!var36) {
                        return;
                     }

                     var1.setVar("SubclassCustomItem", 1, -1L);
                  }

                  if (var29) {
                     if (!var1.addSubClass(var7, true)) {
                        var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", var1));
                        return;
                     }

                     var3.append("Add Subclass:<br>The subclass of <font color=\"LEVEL\">")
                        .append(HtmlUtils.htmlClassName(var7, var1))
                        .append("</font> has been added.");
                     var1.sendPacket(SystemMsg.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
                  } else {
                     var4.setFile("villagemaster/SubClass_Fail.htm");
                  }
                  break;
               case 5:
                  if (Config.OLY_ENABLED && (ParticipantPool.getInstance().isRegistred(var1) || var1.isOlyParticipant())) {
                     var1.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
                     return;
                  }

                  if (var1.isInDuel()) {
                     var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", var1));
                     return;
                  }

                  if (!Config.ALT_PARTY_CLASS_LIMIT.isEmpty() && Config.ALT_PARTY_CLASS_LIMIT.containsKey(var7)) {
                     Party var34 = var1.getParty();
                     int var40 = 0;
                     if (var34 != null) {
                        for (Player var15 : var34.getPartyMembers()) {
                           if (var15.getActiveClass().getClassId() == var7) {
                              var40++;
                           }
                        }

                        if (var40 >= Config.ALT_PARTY_CLASS_LIMIT.get(var7)) {
                           var34.removePartyMember(var1, true);
                           var1.sendMessage(new CustomMessage("PARTY_PARTICIPATION_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET", var1));
                        }
                     }
                  }

                  var1.setActiveSubClass(var7, true);
                  var3.append("Change Subclass:<br>Your active subclass is now a <font color=\"LEVEL\">")
                     .append(HtmlUtils.htmlClassName(var1.getActiveClassId(), var1))
                     .append("</font>.");
                  var1.sendPacket(SystemMsg.YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS);
                  break;
               case 6:
                  var3.append(
                     "Please choose a subclass to change to. If the one you are looking for is not here, please seek out the appropriate master for that class.<br><font color=\"LEVEL\">Warning!</font> All classes and skills for this class will be removed.<br><br>"
                  );
                  Set var6 = this.e(var1, false);
                  if (var6.isEmpty()) {
                     var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", var1));
                     return;
                  }

                  for (Object __raw_var39_380 : (Iterable<?>)(Object)(var6)) {
                     PlayerClass var39 = (PlayerClass)__raw_var39_380;
                     var3.append("<a action=\"bypass -h npc_")
                        .append(this.getObjectId())
                        .append("_Subclass 7 ")
                        .append(var7)
                        .append(" ")
                        .append(var39.ordinal())
                        .append("\">")
                        .append(HtmlUtils.htmlClassName(var39.ordinal(), var1))
                        .append("</a><br>");
                  }
                  break;
               case 7:
                  if (Config.OLY_ENABLED && (ParticipantPool.getInstance().isRegistred(var1) || var1.isOlyParticipant())) {
                     var1.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
                     return;
                  }

                  if (!var1.modifySubClass(var7, var8)) {
                     var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", var1));
                     return;
                  }

                  var3.append("Change Subclass:<br>Your subclass has been changed to <font color=\"LEVEL\">")
                     .append(HtmlUtils.htmlClassName(var8, var1))
                     .append("</font>.");
                  var1.sendPacket(SystemMsg.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
            }

            var3.append("</body></html>");
            if (var3.length() > 26) {
               var4.setHtml(var3.toString());
            }

            var1.sendPacket(var4);
         } else {
            super.onBypassFeedback(var1, var2);
         }
      }
   }

   @Override
   public String getHtmlPath(int var1, int var2, Player var3) {
      String var4;
      if (var2 == 0) {
         var4 = "" + var1;
      } else {
         var4 = var1 + "-" + var2;
      }

      return "villagemaster/" + var4 + ".htm";
   }

   private void a(NpcInstance var1, Player var2, String var3) {
      if (var2.getLevel() < Config.CHARACTER_MIN_LEVEL_FOR_CLAN_CREATE) {
         var2.sendPacket(Msg.YOU_ARE_NOT_QUALIFIED_TO_CREATE_A_CLAN);
      } else if (var2.getClanId() != 0) {
         var2.sendPacket(Msg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
      } else if (!var2.canCreateClan()) {
         var2.sendPacket(Msg.YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN);
      } else if (var3.length() > 16) {
         var2.sendPacket(Msg.CLAN_NAMES_LENGTH_IS_INCORRECT);
      } else if (!Util.isMatchingRegexp(var3, Config.CLAN_NAME_TEMPLATE)) {
         var2.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
      } else {
         Clan var4 = ClanTable.getInstance().createClan(var2, var3);
         if (var4 == null) {
            var2.sendPacket(Msg.THIS_NAME_ALREADY_EXISTS);
         } else {
            var2.sendPacket(var4.listAll());
            var2.sendPacket(new PledgeShowInfoUpdate(var4), Msg.CLAN_HAS_BEEN_CREATED);
            var2.updatePledgeClass();
            var2.broadcastCharInfo();
            var1.showChatWindow(var2, "villagemaster/pl006.htm");
         }
      }
   }

   private void V(Player var1) {
      if (!var1.isClanLeader()) {
         this.showChatWindow(var1, "villagemaster/pl_err_master.htm");
      } else if (var1.getEvent(SiegeEvent.class) != null) {
         var1.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow", var1));
      } else {
         Clan var2 = var1.getClan();
         SubUnit var3 = var2.getSubUnit(0);
         UnitMember var4 = var3.getLeader();
         if (var4.getObjectId() == var1.getObjectId() && var3.getNextLeaderObjectId() != 0 && var3.getNextLeaderObjectId() != var1.getObjectId()) {
            setLeader(var1, var2, var3, var4);
            this.showChatWindow(var1, "villagemaster/pl_cancel_success.htm");
         } else {
            this.showChatWindow(var1, "villagemaster/pl_not_transfer.htm");
         }
      }
   }

   private void n(Player var1, String var2) {
      if (!var1.isClanLeader()) {
         this.showChatWindow(var1, "villagemaster/pl_err_master.htm");
      } else if (var1.getClan().isPlacedForDisband()) {
         var1.sendPacket(SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
      } else if (var1.getEvent(SiegeEvent.class) != null) {
         var1.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow", var1));
      } else {
         Clan var3 = var1.getClan();
         SubUnit var4 = var3.getSubUnit(0);
         UnitMember var5 = var4.getUnitMember(var2);
         if (var5 == null) {
            this.showChatWindow(var1, "villagemaster/clan-20.htm");
         } else if (var5.getLeaderOf() != -128) {
            var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.CannotAssignUnitLeader", var1));
         } else if (var4.getNextLeaderObjectId() != 0 && var4.getNextLeaderObjectId() != var1.getObjectId()) {
            this.showChatWindow(var1, "villagemaster/pl_transfer_already.htm");
         } else {
            setLeader(var1, var3, var4, var5);
            this.showChatWindow(var1, "villagemaster/pl_transfer_success.htm");
         }
      }
   }

   public static void setLeader(Player var0, Clan var1, SubUnit var2, UnitMember var3) {
      var0.sendMessage(
         new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.ClanLeaderWillBeChangedFromS1ToS2", var0)
            .addString(var1.getLeaderName())
            .addString(var3.getName())
      );
      if (Config.CLAN_LEADER_CHANGE_METHOD) {
         if (var1.getLevel() >= Config.MIN_CLAN_LEVEL_FOR_SIEGE_REGISTRATION) {
            if (var1.getLeader() != null) {
               Player var4 = var1.getLeader().getPlayer();
               if (var4 != null) {
                  SiegeUtils.removeSiegeSkills(var4);
                  var4.sendPacket(new SkillList(var4));
               }
            }

            Player var7 = var3.getPlayer();
            if (var7 != null) {
               SiegeUtils.addSiegeSkills(var7);
               var7.sendPacket(new SkillList(var7));
            }
         }

         synchronized (var1) {
            var2.setLeader(var3, true);
         }

         var1.broadcastClanStatus(true, true, false);
      } else {
         var2.updateDbLeader(var3);
         var1.broadcastClanStatus(true, true, false);
      }
   }

   private void a(Player var1, String var2, int var3, int var4, String var5) {
      UnitMember var6 = null;
      Clan var7 = var1.getClan();
      if (var7 != null && var1.isClanLeader()) {
         if (!Util.isMatchingRegexp(var2, Config.CLAN_NAME_TEMPLATE)) {
            var1.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
         } else {
            for (SubUnit var10 : var7.getAllSubUnits()) {
               if (var10.getName().equals(var2)) {
                  var1.sendPacket(Msg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
                  return;
               }
            }

            if (ClanTable.getInstance().getClanByName(var2) != null) {
               var1.sendPacket(Msg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
            } else if (var7.getLevel() < var4) {
               var1.sendPacket(Msg.THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET);
            } else {
               SubUnit var12 = var7.getSubUnit(0);
               if (var3 != -1) {
                  var6 = var12.getUnitMember(var5);
                  if (var6 == null) {
                     var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", var1));
                     return;
                  }

                  if (var6.getLeaderOf() != -128) {
                     var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader", var1));
                     return;
                  }
               }

               var3 = var7.createSubPledge(var1, var3, var6, var2);
               if (var3 != -128) {
                  var7.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(var7.getSubUnit(var3)));
                  SystemMessage var13;
                  if (var3 == -1) {
                     var13 = new SystemMessage(1741);
                     var13.addString(var1.getClan().getName());
                  } else if (var3 >= 1001) {
                     var13 = new SystemMessage(1794);
                     var13.addString(var1.getClan().getName());
                  } else if (var3 >= 100) {
                     var13 = new SystemMessage(1795);
                     var13.addString(var1.getClan().getName());
                  } else {
                     var13 = Msg.CLAN_HAS_BEEN_CREATED;
                  }

                  var1.sendPacket(var13);
                  if (var6 != null) {
                     var7.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(var6));
                     if (var6.isOnline()) {
                        var6.getPlayer().updatePledgeClass();
                        var6.getPlayer().broadcastCharInfo();
                     }
                  }
               }
            }
         }
      } else {
         var1.sendPacket(Msg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
      }
   }

   private void a(Player var1, String var2, String var3) {
      Clan var4 = var1.getClan();
      if (var4 == null) {
         var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.ClanDoesntExist", var1));
      } else if (!var1.isClanLeader()) {
         var1.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
      } else {
         SubUnit var5 = null;

         for (SubUnit var7 : var4.getAllSubUnits()) {
            if (var7.getType() != 0 && var7.getType() != -1 && var7.getName().equalsIgnoreCase(var2)) {
               var5 = var7;
            }
         }

         if (var5 == null) {
            var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubUnitNotFound", var1));
         } else {
            SubUnit var8 = var4.getSubUnit(0);
            UnitMember var9 = var8.getUnitMember(var3);
            if (var9 == null) {
               var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", var1));
            } else if (var9.getObjectId() == var8.getNextLeaderObjectId()) {
               var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", var1));
            } else if (var9.getLeaderOf() != -128) {
               var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader", var1));
            } else {
               var5.setLeader(var9, true);
               var4.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(var5));
               var4.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(var9));
               if (var9.isOnline()) {
                  var9.getPlayer().updatePledgeClass();
                  var9.getPlayer().broadcastCharInfo();
               }

               var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NewSubUnitLeaderHasBeenAssigned", var1));
            }
         }
      }
   }

   private static boolean b(NpcInstance var0, Player var1) {
      if (var1.getClan() == null) {
         var0.showChatWindow(var1, "villagemaster/pl_no_pledgeman.htm");
         return false;
      } else if (!var1.isClanLeader()) {
         var0.showChatWindow(var1, "villagemaster/pl_err_master.htm");
         return false;
      } else {
         return true;
      }
   }

   private static void c(NpcInstance var0, Player var1) {
      if (var1 != null && var1.getClan() != null) {
         Clan var2 = var1.getClan();
         if (!var1.isClanLeader()) {
            var1.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
         } else if (var2.isPlacedForDisband()) {
            var1.sendPacket(Msg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
         } else if (!var2.canDisband()) {
            var1.sendPacket(Msg.YOU_CANNOT_APPLY_FOR_DISSOLUTION_AGAIN_WITHIN_SEVEN_DAYS_AFTER_A_PREVIOUS_APPLICATION_FOR_DISSOLUTION);
         } else if (var2.getAllyId() != 0) {
            var1.sendPacket(Msg.YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE);
         } else if (var2.isAtWar() > 0) {
            var1.sendPacket(Msg.YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR);
         } else if (var2.getCastle() == 0 && var2.getHasHideout() == 0) {
            for (Residence var4 : ResidenceHolder.getInstance().getResidences()) {
               if (var4.<SiegeEvent>getSiegeEvent().getSiegeClan("attackers", var2) != null
                  || var4.<SiegeEvent>getSiegeEvent().getSiegeClan("defenders", var2) != null
                  || var4.<SiegeEvent>getSiegeEvent().getSiegeClan("defenders_waiting", var2) != null) {
                  var1.sendPacket(SystemMsg.UNABLE_TO_DISSOLVE_YOUR_CLAN_HAS_REQUESTED_TO_PARTICIPATE_IN_A_CASTLE_SIEGE);
                  return;
               }
            }

            var2.placeForDisband();
            var2.broadcastClanStatus(true, true, false);
            var0.showChatWindow(var1, "villagemaster/pl009.htm");
         } else {
            var1.sendPacket(Msg.UNABLE_TO_DISPERSE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS);
         }
      }
   }

   private static void a(VillageMasterInstance var0, Player var1) {
      if (b(var0, var1)) {
         Clan var2 = var1.getClan();
         if (!var2.isPlacedForDisband()) {
            var1.sendPacket(SystemMsg.THERE_ARE_NO_REQUESTS_TO_DISPERSE);
         } else {
            var2.unPlaceDisband();
            var2.broadcastClanStatus(true, true, false);
            var0.showChatWindow(var1, "villagemaster/pl012.htm");
         }
      }
   }

   private void W(Player var1) {
      Clan var2 = var1.getClan();
      if (var2 != null) {
         if (!var1.isClanLeader()) {
            var1.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
         } else if (var1.getClan().isPlacedForDisband()) {
            var1.sendPacket(SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
         } else {
            boolean var3 = false;
            switch (var2.getLevel()) {
               case 0:
                  if (var1.getSp() >= Config.CLAN_FIRST_LEVEL_SP && var1.getAdena() >= Config.CLAN_FIRST_LEVEL_ADENA) {
                     var1.setSp(var1.getSp() - Config.CLAN_FIRST_LEVEL_SP);
                     var1.reduceAdena(Config.CLAN_FIRST_LEVEL_ADENA, true);
                     var3 = true;
                  }
                  break;
               case 1:
                  if (var1.getSp() >= Config.CLAN_SECOND_LEVEL_SP && var1.getAdena() >= Config.CLAN_SECOND_LEVEL_ADENA) {
                     var1.setSp(var1.getSp() - Config.CLAN_SECOND_LEVEL_SP);
                     var1.reduceAdena(Config.CLAN_SECOND_LEVEL_ADENA, true);
                     var3 = true;
                  }
                  break;
               case 2:
                  if (var1.getSp() >= Config.CLAN_THIRD_LEVEL_SP && var1.getInventory().destroyItemByItemId(1419, 1L)) {
                     var1.setSp(var1.getSp() - Config.CLAN_THIRD_LEVEL_SP);
                     var3 = true;
                  }
                  break;
               case 3:
                  if (var1.getSp() >= Config.CLAN_FOUR_LEVEL_SP && var1.getInventory().destroyItemByItemId(3874, 1L)) {
                     var1.setSp(var1.getSp() - Config.CLAN_FOUR_LEVEL_SP);
                     var3 = true;
                  }
                  break;
               case 4:
                  if (var1.getSp() >= Config.CLAN_FIVE_LEVEL_SP && var1.getInventory().destroyItemByItemId(3870, 1L)) {
                     var1.setSp(var1.getSp() - Config.CLAN_FIVE_LEVEL_SP);
                     var3 = true;
                  }
                  break;
               case 5:
                  if (var2.getReputationScore() >= Config.CLAN_SIX_LEVEL_CLAN_REPUTATION && var2.getAllSize() >= Config.CLAN_SIX_LEVEL_CLAN_MEMBER_COUNT) {
                     var2.incReputation(-Config.CLAN_SIX_LEVEL_CLAN_REPUTATION, false, "LvlUpClan");
                     var3 = true;
                  }
                  break;
               case 6:
                  if (var2.getReputationScore() >= Config.CLAN_SEVEN_LEVEL_CLAN_REPUTATION && var2.getAllSize() >= Config.CLAN_SEVEN_LEVEL_CLAN_MEMBER_COUNT) {
                     var2.incReputation(-Config.CLAN_SEVEN_LEVEL_CLAN_REPUTATION, false, "LvlUpClan");
                     var3 = true;
                  }
                  break;
               case 7:
                  if (var2.getReputationScore() >= Config.CLAN_EIGHT_LEVEL_CLAN_REPUTATION && var2.getAllSize() >= Config.CLAN_EIGHT_LEVEL_CLAN_MEMBER_COUNT) {
                     var2.incReputation(-Config.CLAN_EIGHT_LEVEL_CLAN_REPUTATION, false, "LvlUpClan");
                     var3 = true;
                  }
            }

            if (var3) {
               var2.setLevel(var2.getLevel() + 1);
               var2.updateClanInDB();
               var1.broadcastCharInfo();
               this.doCast(SkillTable.getInstance().getInfo(5103, 1), var1, true);
               if (var2.getLevel() >= Config.MIN_CLAN_LEVEL_FOR_SIEGE_REGISTRATION) {
                  SiegeUtils.addSiegeSkills(var1);
                  var1.sendPacket(new SkillList(var1));
                  var1.sendEtcStatusUpdate();
                  var1.updateStats();
               }

               if (var2.getLevel() == 5) {
                  var1.sendPacket(Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
               }

               PledgeShowInfoUpdate var4 = new PledgeShowInfoUpdate(var2);
               PledgeStatusChanged var5 = new PledgeStatusChanged(var2);

               for (UnitMember var7 : var2) {
                  if (var7.isOnline()) {
                     var7.getPlayer().updatePledgeClass();
                     var7.getPlayer().sendPacket(Msg.CLANS_SKILL_LEVEL_HAS_INCREASED, var4, var5);
                     var7.getPlayer().broadcastCharInfo();
                  }
               }
            } else {
               var1.sendPacket(Msg.CLAN_HAS_FAILED_TO_INCREASE_SKILL_LEVEL);
            }
         }
      }
   }

   private void o(Player var1, String var2) {
      if (!var1.isClanLeader()) {
         var1.sendPacket(Msg.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
      } else if (var1.getClan().getAllyId() != 0) {
         var1.sendPacket(Msg.YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE);
      } else if (var1.getClan().isPlacedForDisband()) {
         var1.sendPacket(SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
      } else if (var2.length() > 16) {
         var1.sendPacket(Msg.INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME);
      } else if (!Util.isMatchingRegexp(var2, Config.ALLY_NAME_TEMPLATE)) {
         var1.sendPacket(Msg.INCORRECT_ALLIANCE_NAME);
      } else if (var1.getClan().getLevel() < 5) {
         var1.sendPacket(Msg.TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
      } else if (ClanTable.getInstance().getAllyByName(var2) != null) {
         var1.sendPacket(Msg.THIS_ALLIANCE_NAME_ALREADY_EXISTS);
      } else if (!var1.getClan().canCreateAlly()) {
         var1.sendPacket(Msg.YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_AFTER_DISSOLUTION);
      } else {
         Alliance var3 = ClanTable.getInstance().createAlliance(var1, var2);
         if (var3 != null) {
            var1.broadcastCharInfo();
            var1.sendMessage(new CustomMessage("L2VillageMasterInstance.AllianceCreated", var1).addString(var2));
         }
      }
   }

   private void dissolveAlly(Player var1) {
      if (var1 != null && var1.getAlliance() != null) {
         if (!var1.isAllyLeader()) {
            var1.sendPacket(Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
         } else if (var1.getAlliance().getMembersCount() > 1) {
            var1.sendPacket(Msg.YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE);
         } else {
            ClanTable.getInstance().dissolveAlly(var1);
         }
      }
   }

   private Set<PlayerClass> e(Player var1, boolean var2) {
      int var3 = var1.getBaseClassId();
      Race var4 = this.de();
      ClassType var5 = this.df();
      PlayerClass var6 = PlayerClass.values()[var3];
      Set var7 = var6.getAvailableSubclasses();
      if (var7 == null) {
         return Collections.emptySet();
      }

      var7.remove(var6);

      for (Object __raw_var9_842 : (Iterable<?>)(Object)(var7)) {
         PlayerClass var9 = (PlayerClass)__raw_var9_842;
         for (SubClass var11 : var1.getSubClasses().values()) {
            if (var9.ordinal() == var11.getClassId()) {
               var7.remove(var9);
            } else {
               ClassId var12 = ClassId.VALUES[var9.ordinal()].getParent(var1.getSex());
               if (var12 != null && var12.getId() == var11.getClassId()) {
                  var7.remove(var9);
               } else {
                  ClassId var13 = ClassId.VALUES[var11.getClassId()].getParent(var1.getSex());
                  if (var13 != null && var13.getId() == var9.ordinal()) {
                     var7.remove(var9);
                  }
               }
            }
         }

         if (!Config.ALTSUBCLASS_LIST_ALL) {
            if (!var9.isOfRace(Race.human) && !var9.isOfRace(Race.elf)) {
               if (!var9.isOfRace(var4)) {
                  var7.remove(var9);
               }
            } else if (!var9.isOfType(var5) || var4 != Race.human) {
               var7.remove(var9);
            }
         }
      }

      return var7;
   }

   private Race de() {
      switch (this.getTemplate().getRace()) {
         case 14:
            return Race.human;
         case 15:
            return Race.elf;
         case 16:
            return Race.darkelf;
         case 17:
            return Race.orc;
         case 18:
            return Race.dwarf;
         default:
            return null;
      }
   }

   private ClassType df() {
      switch (this.getNpcId()) {
         case 30017:
         case 30019:
         case 30033:
         case 30034:
         case 30035:
         case 30068:
         case 30069:
         case 30110:
         case 30111:
         case 30112:
         case 30114:
         case 30115:
         case 30144:
         case 30145:
         case 30154:
         case 30158:
         case 30171:
         case 30174:
         case 30175:
         case 30176:
         case 30189:
         case 30190:
         case 30194:
         case 30293:
         case 30330:
         case 30344:
         case 30375:
         case 30377:
         case 30461:
         case 30464:
         case 30473:
         case 30476:
         case 30609:
         case 30610:
         case 30612:
         case 30634:
         case 30635:
         case 30637:
         case 30638:
         case 30639:
         case 30640:
         case 30666:
         case 30680:
         case 30694:
         case 30695:
         case 30696:
         case 30701:
         case 30715:
         case 30717:
         case 30720:
         case 30721:
         case 30854:
         case 30855:
         case 30861:
         case 30864:
         case 30907:
         case 30908:
         case 30912:
         case 30915:
         case 30988:
         case 31001:
         case 31046:
         case 31047:
         case 31048:
         case 31049:
         case 31050:
         case 31051:
         case 31052:
         case 31053:
         case 31281:
         case 31282:
         case 31283:
         case 31285:
         case 31326:
         case 31330:
         case 31331:
         case 31332:
         case 31333:
         case 31337:
         case 31339:
         case 31359:
         case 31415:
         case 31425:
         case 31426:
         case 31427:
         case 31430:
         case 31431:
         case 31605:
         case 31608:
         case 31614:
         case 31620:
         case 31643:
         case 31740:
         case 31755:
         case 31953:
         case 31969:
         case 31970:
         case 31971:
         case 31972:
         case 31976:
         case 31977:
         case 31996:
         case 32056:
         case 32074:
         case 32082:
         case 32083:
         case 32084:
         case 32085:
         case 32086:
         case 32087:
         case 32088:
         case 32089:
         case 32098:
            return ClassType.Mystic;
         case 30022:
         case 30030:
         case 30031:
         case 30032:
         case 30036:
         case 30037:
         case 30067:
         case 30070:
         case 30116:
         case 30117:
         case 30118:
         case 30120:
         case 30129:
         case 30130:
         case 30131:
         case 30132:
         case 30133:
         case 30141:
         case 30188:
         case 30191:
         case 30289:
         case 30305:
         case 30358:
         case 30359:
         case 30404:
         case 30419:
         case 30421:
         case 30422:
         case 30424:
         case 30502:
         case 30507:
         case 30510:
         case 30515:
         case 30537:
         case 30538:
         case 30571:
         case 30572:
         case 30575:
         case 30598:
         case 30614:
         case 30657:
         case 30665:
         case 30682:
         case 30706:
         case 30857:
         case 30858:
         case 30859:
         case 30905:
         case 30906:
         case 30927:
         case 30981:
         case 31279:
         case 31290:
         case 31291:
         case 31328:
         case 31335:
         case 31336:
         case 31348:
         case 31349:
         case 31350:
         case 31424:
         case 31428:
         case 31429:
         case 31452:
         case 31454:
         case 31524:
         case 31581:
         case 31591:
         case 31602:
         case 31613:
         case 31644:
         case 31856:
         case 31968:
         case 31973:
         case 31979:
         case 31980:
         case 32008:
         case 32010:
         case 32019:
         case 32095:
            return ClassType.Priest;
         default:
            return ClassType.Fighter;
      }
   }
}
