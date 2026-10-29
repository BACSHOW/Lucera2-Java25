package quests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;

public class _503_PursuitClanAmbition extends Quest implements ScriptFile {
   private final int bId = 3866;
   private final int bIe = 3842;
   private final int bIf = 3841;
   private final int bIg = 3840;
   private final int bIh = 3839;
   private final int bIi = 3843;
   private final int bIj = 3871;
   private final short bIk = 3867;
   private final short bIl = 3838;
   private final short bIm = 3846;
   private final short bIn = 3844;
   private final short bIo = 3845;
   private final int bIp = 3868;
   private final int bIq = 3847;
   private final int bIr = 3869;
   private final int bIs = 3870;
   private final int[] bIt = new int[]{3839, 3840, 3841, 3842};
   private final int bIu = 30760;
   private final int aJF = 30645;
   private final int bIv = 30758;
   private final int bIw = 30759;
   private final int bIx = 30761;
   private final int bIy = 30762;
   private final int bIz = 30763;
   private final int bIA = 30512;
   private final int bIB = 30764;
   private final int bIC = 30868;
   private final int bID = 30765;
   private final int bIE = 30766;
   private final int bIF = 20282;
   private final int bIG = 20243;
   private final int bIH = 20137;
   private final int bII = 20285;
   private final int bIJ = 27178;
   private final int bIK = 20668;
   private final int bIL = 27179;
   private final int bIM = 27181;
   private final int bIN = 20654;
   private final int bIO = 20656;

   @Override
   public void onLoad() {
   }

   @Override
   public void onReload() {
   }

   @Override
   public void onShutdown() {
   }

   public _503_PursuitClanAmbition() {
      super(2);
      this.addStartNpc(30760);
      this.addTalkId(30645);
      this.addTalkId(30758);
      this.addTalkId(30759);
      this.addTalkId(30761);
      this.addTalkId(30762);
      this.addTalkId(30763);
      this.addTalkId(30512);
      this.addTalkId(30764);
      this.addTalkId(30868);
      this.addTalkId(30765);
      this.addTalkId(30766);
      this.addKillId(20282, 20243, 20137, 20285, 27178, 20654, 20656, 20668, 27179, 27181);

      for (int var1 = 3839; var1 <= 3848; var1++) {
         this.addQuestItem(var1);
      }

      for (int var2 = 3866; var2 <= 3869; var2++) {
         this.addQuestItem(var2);
      }
   }

   public void suscribe_members(QuestState var1) {
      int var2 = var1.getPlayer().getClan().getClanId();
      Connection var3 = null;
      PreparedStatement var4 = null;
      PreparedStatement var5 = null;
      ResultSet var6 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("SELECT obj_Id FROM characters WHERE clanid=? AND online=0");
         var5 = var3.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)");
         var4.setInt(1, var2);
         var6 = var4.executeQuery();

         while (var6.next()) {
            int var7 = var6.getInt("obj_Id");

            try {
               var5.setInt(1, var7);
               var5.setString(2, this.getName());
               var5.setString(3, "<state>");
               var5.setString(4, "Started");
               var5.executeUpdate();
            } catch (Exception var13) {
               var13.printStackTrace();
            }
         }
      } catch (Exception var14) {
         var14.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var5);
         DbUtils.closeQuietly(var3, var4, var6);
      }
   }

   public void offlineMemberExit(QuestState var1) {
      int var2 = var1.getPlayer().getClan().getClanId();
      Connection var3 = null;
      PreparedStatement var4 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("DELETE FROM character_quests WHERE name=? AND char_id IN (SELECT obj_id FROM characters WHERE clanId=? AND online=0)");
         var4.setString(1, this.getName());
         var4.setInt(2, var2);
         var4.executeUpdate();
      } catch (Exception var9) {
         var9.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var3, var4);
      }
   }

   public Player getLeader(QuestState var1) {
      Player var2 = var1.getPlayer();
      if (var2 == null) {
         return null;
      }

      Clan var3 = var2.getClan();
      return var3 == null ? null : var3.getLeader().getPlayer();
   }

   public int getLeaderVar(QuestState var1, String var2) {
      boolean var3 = "cond".equalsIgnoreCase(var2);

      try {
         Player var4 = this.getLeader(var1);
         if (var4 != null) {
            if (var3) {
               return var4.getQuestState(this.getName()).getCond();
            }

            return var4.getQuestState(this.getName()).getInt(var2);
         }
      } catch (Exception var16) {
         return -1;
      }

      Clan var19 = var1.getPlayer().getClan();
      if (var19 == null) {
         return -1;
      }

      int var5 = var19.getLeaderId();
      Connection var6 = null;
      PreparedStatement var7 = null;
      ResultSet var8 = null;

      try {
         var6 = DatabaseFactory.getInstance().getConnection();
         var7 = var6.prepareStatement("SELECT value FROM character_quests WHERE char_id=? AND var=? AND name=?");
         var7.setInt(1, var5);
         var7.setString(2, var2);
         var7.setString(3, this.getName());
         int var9 = -1;
         var8 = var7.executeQuery();
         if (var8.next()) {
            var9 = var8.getInt("value");
            if (var3 && (var9 & -2147483648) != 0) {
               var9 &= Integer.MAX_VALUE;

               for (int var10 = 1; var10 < 32; var10++) {
                  var9 >>= 1;
                  if (var9 == 0) {
                     return var10;
                  }
               }
            }
         }

         return var9;
      } catch (Exception var17) {
         var17.printStackTrace();
         return -1;
      } finally {
         DbUtils.closeQuietly(var6, var7, var8);
      }
   }

   public void setLeaderVar(QuestState var1, String var2, String var3) {
      Clan var4 = var1.getPlayer().getClan();
      if (var4 != null) {
         Player var5 = var4.getLeader().getPlayer();
         if (var5 != null) {
            if ("cond".equalsIgnoreCase(var2)) {
               var5.getQuestState(this.getName()).setCond(Integer.parseInt(var3));
            } else {
               var5.getQuestState(this.getName()).set(var2, var3);
            }
         } else {
            int var6 = var1.getPlayer().getClan().getLeaderId();
            Connection var7 = null;
            PreparedStatement var8 = null;

            try {
               var7 = DatabaseFactory.getInstance().getConnection();
               var8 = var7.prepareStatement("UPDATE character_quests SET value=? WHERE char_id=? AND var=? AND name=?");
               var8.setString(1, var3);
               var8.setInt(2, var6);
               var8.setString(3, var2);
               var8.setString(4, this.getName());
               var8.executeUpdate();
            } catch (Exception var13) {
               var13.printStackTrace();
            } finally {
               DbUtils.closeQuietly(var7, var8);
            }
         }
      }
   }

   public boolean checkEggs(QuestState var1) {
      int var2 = 0;

      for (int var6 : this.bIt) {
         if (var1.getQuestItemsCount(var6) > 9L) {
            var2++;
         }
      }

      return var2 > 3;
   }

   public void giveItem(int var1, long var2, QuestState var4) {
      Player var5 = var4.getPlayer();
      if (var5 != null) {
         Player var6 = this.getLeader(var4);
         if (var6 != null) {
            if (!(var5.getDistance(var6) > Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
               QuestState var7 = var6.getQuestState(this.getClass());
               if (var7 != null) {
                  long var8 = var7.getQuestItemsCount(var1);
                  if (var8 < var2) {
                     var7.giveItems(var1, 1L);
                     if (var8 == var2 - 1L) {
                        var7.playSound("ItemSound.quest_middle");
                     } else {
                        var7.playSound("ItemSound.quest_itemget");
                     }
                  }
               }
            }
         }
      }
   }

   public String exit503(boolean var1, QuestState var2) {
      if (var1) {
         var2.giveItems(3870, 1L);
         var2.addExpAndSp(0L, 250000L);
         var2.unset("cond");
         var2.unset("Fritz");
         var2.unset("Lutz");
         var2.unset("Kurtz");
         var2.unset("ImpGraveKeeper");
         var2.exitCurrentQuest(false);
      } else {
         var2.exitCurrentQuest(true);
      }

      var2.takeItems(3869, -1L);

      try {
         for (Player var5 : var2.getPlayer().getClan().getOnlineMembers(0)) {
            if (var5 != null) {
               QuestState var6 = var5.getQuestState(this.getName());
               if (var6 != null) {
                  var6.exitCurrentQuest(true);
               }
            }
         }

         this.offlineMemberExit(var2);
         return "Congratulations, you have finished the Pursuit of Clan Ambition";
      } catch (Exception var7) {
         return "You dont have any members in your Clan, so you can't finish the Pursuit of Aspiration";
      }
   }

   @Override
   public String onEvent(String var1, QuestState var2, NpcInstance var3) {
      String var4 = var1;
      if (var1.equalsIgnoreCase("30760-08.htm")) {
         var2.giveItems(3866, 1L);
         var2.setCond(1);
         var2.set("Fritz", "1");
         var2.set("Lutz", "1");
         var2.set("Kurtz", "1");
         var2.set("ImpGraveKeeper", "1");
         var2.setState(2);
      } else if (var1.equalsIgnoreCase("30760-12.htm")) {
         var2.giveItems(3867, 1L);
         var2.setCond(4);
      } else if (var1.equalsIgnoreCase("30760-16.htm")) {
         var2.giveItems(3868, 1L);
         var2.setCond(7);
      } else if (var1.equalsIgnoreCase("30760-20.htm")) {
         this.exit503(true, var2);
      } else if (var1.equalsIgnoreCase("30760-22.htm")) {
         var2.setCond(13);
      } else if (var1.equalsIgnoreCase("30760-23.htm")) {
         this.exit503(true, var2);
      } else if (var1.equalsIgnoreCase("30645-03.htm")) {
         var2.takeItems(3866, -1L);
         var2.setCond(2);
         this.suscribe_members(var2);

         for (Player var7 : var2.getPlayer().getClan().getOnlineMembers(var2.getPlayer().getObjectId())) {
            this.newQuestState(var7, 2);
         }
      } else if (var1.equalsIgnoreCase("30763-03.htm")) {
         if (var2.getInt("Kurtz") == 1) {
            var4 = "30763-02.htm";
            var2.giveItems(3839, 6L);
            var2.giveItems(3843, 1L);
            var2.set("Kurtz", "2");
         }
      } else if (var1.equalsIgnoreCase("30762-03.htm")) {
         int var8 = var2.getInt("Lutz");
         if (var8 == 1) {
            var4 = "30762-02.htm";
            var2.giveItems(3839, 4L);
            var2.giveItems(3840, 3L);
            var2.set("Lutz", "2");
         }

         var2.addSpawn(27178, var3.getLoc().x, var3.getLoc().y, var3.getLoc().z, Location.getRandomHeading(), 300, 120000);
         var2.addSpawn(27178, var3.getLoc().x, var3.getLoc().y, var3.getLoc().z, Location.getRandomHeading(), 300, 120000);
      } else if (var1.equalsIgnoreCase("30761-03.htm")) {
         int var9 = var2.getInt("Fritz");
         if (var9 == 1) {
            var4 = "30761-02.htm";
            var2.giveItems(3840, 3L);
            var2.set("Fritz", "2");
         }

         var2.addSpawn(27178, var3.getLoc().x, var3.getLoc().y, var3.getLoc().z, Location.getRandomHeading(), 300, 120000);
         var2.addSpawn(27178, var3.getLoc().x, var3.getLoc().y, var3.getLoc().z, Location.getRandomHeading(), 300, 120000);
      } else if (var1.equalsIgnoreCase("30512-03.htm")) {
         var2.takeItems(3843, -1L);
         var2.giveItems(3871, 1L);
         var2.set("Kurtz", "3");
      } else if (var1.equalsIgnoreCase("30764-03.htm")) {
         var2.takeItems(3867, -1L);
         var2.setCond(5);
         var2.set("Kurtz", "3");
      } else if (var1.equalsIgnoreCase("30764-05.htm")) {
         var2.takeItems(3867, -1L);
         var2.setCond(5);
      } else if (var1.equalsIgnoreCase("30764-06.htm")) {
         var2.takeItems(3871, -1L);
         var2.set("Kurtz", "4");
         var2.giveItems(3838, 1L);
      } else if (var1.equalsIgnoreCase("30868-04.htm")) {
         var2.takeItems(3868, -1L);
         var2.setCond(8);
      } else if (var1.equalsIgnoreCase("30868-06a.htm")) {
         var2.setCond(10);
      } else if (var1.equalsIgnoreCase("30868-10.htm")) {
         var2.setCond(12);
      } else if (var1.equalsIgnoreCase("30766-04.htm")) {
         var2.setCond(9);
         NpcInstance var10 = var2.findTemplate(30766);
         if (var10 != null) {
            Functions.npcSay(var10, "Blood and Honour");
         }

         var10 = var2.findTemplate(30759);
         if (var10 != null) {
            Functions.npcSay(var10, "Ambition and Power");
         }

         var10 = var2.findTemplate(30758);
         if (var10 != null) {
            Functions.npcSay(var10, "War and Death");
         }
      } else if (var1.equalsIgnoreCase("30766-08.htm")) {
         var2.takeItems(3869, -1L);
         this.exit503(false, var2);
      }

      return var4;
   }

   @Override
   public String onTalk(NpcInstance var1, QuestState var2) {
      int var3 = var1.getNpcId();
      int var4 = var2.getState();
      String var5 = "noquest";
      boolean var6 = var2.getPlayer().isClanLeader();
      if (var4 == 1 && var3 == 30760) {
         if (var2.getPlayer().getClan() != null) {
            if (var6) {
               int var17 = var2.getPlayer().getClan().getLevel();
               if (var2.getQuestItemsCount(3870) > 0L) {
                  var5 = "30760-03.htm";
                  var2.exitCurrentQuest(true);
               } else if (var17 > 3) {
                  var5 = "30760-04.htm";
               } else {
                  var5 = "30760-02.htm";
                  var2.exitCurrentQuest(true);
               }
            } else {
               var5 = "30760-04t.htm";
               var2.exitCurrentQuest(true);
            }
         } else {
            var5 = "30760-01.htm";
            var2.exitCurrentQuest(true);
         }

         return var5;
      } else {
         if (var2.getPlayer().getClan() != null && var2.getPlayer().getClan().getLevel() == 5) {
            return "completed";
         }

         if (var6) {
            if (var2.getCond() == 0) {
               var2.setCond(1);
            }

            if (var2.get("Kurtz") == null) {
               var2.set("Kurtz", "1");
            }

            if (var2.get("Lutz") == null) {
               var2.set("Lutz", "1");
            }

            if (var2.get("Fritz") == null) {
               var2.set("Fritz", "1");
            }

            int var16 = var2.getCond();
            int var8 = var2.getInt("Kurtz");
            int var9 = var2.getInt("Lutz");
            int var10 = var2.getInt("Fritz");
            if (var3 == 30760) {
               if (var16 == 1) {
                  var5 = "30760-09.htm";
               } else if (var16 == 2) {
                  var5 = "30760-10.htm";
               } else if (var16 == 3) {
                  var5 = "30760-11.htm";
               } else if (var16 == 4) {
                  var5 = "30760-13.htm";
               } else if (var16 == 5) {
                  var5 = "30760-14.htm";
               } else if (var16 == 6) {
                  var5 = "30760-15.htm";
               } else if (var16 == 7) {
                  var5 = "30760-17.htm";
               } else if (var16 == 12) {
                  var5 = "30760-19.htm";
               } else if (var16 == 13) {
                  var5 = "30760-24.htm";
               } else {
                  var5 = "30760-18.htm";
               }
            } else if (var3 == 30645) {
               if (var16 == 1) {
                  var5 = "30645-02.htm";
               } else if (var16 == 2) {
                  if (this.checkEggs(var2)) {
                     var5 = "30645-05.htm";
                     var2.setCond(3);
                     var2.playSound("ItemSound.quest_middle");

                     for (int var14 : this.bIt) {
                        var2.takeItems(var14, -1L);
                     }
                  } else {
                     var5 = "30645-04.htm";
                  }
               } else if (var16 == 3) {
                  var5 = "30645-07.htm";
               } else {
                  var5 = "30645-08.htm";
               }
            } else if (var3 == 30762 && var16 == 2) {
               var5 = "30762-01.htm";
            } else if (var3 == 30763 && var16 == 2) {
               var5 = "30763-01.htm";
            } else if (var3 == 30761 && var16 == 2) {
               var5 = "30761-01.htm";
            } else if (var3 == 30512) {
               if (var8 == 1) {
                  var5 = "30512-01.htm";
               } else if (var8 == 2) {
                  var5 = "30512-02.htm";
               } else {
                  var5 = "30512-04.htm";
               }
            } else if (var3 == 30764) {
               if (var16 == 4) {
                  if (var8 > 2) {
                     var5 = "30764-04.htm";
                  } else {
                     var5 = "30764-02.htm";
                  }
               } else if (var16 == 5) {
                  if (var2.getQuestItemsCount(3846) > 9L && var2.getQuestItemsCount(3844) > 9L) {
                     var5 = "30764-08.htm";
                     var2.takeItems(3846, -1L);
                     var2.takeItems(3844, -1L);
                     var2.takeItems(3843, -1L);
                     var2.setCond(6);
                  } else {
                     var5 = "30764-07.htm";
                  }
               } else if (var16 == 6) {
                  var5 = "30764-09.htm";
               }
            } else if (var3 == 30868) {
               if (var16 == 7) {
                  var5 = "30868-02.htm";
               } else if (var16 == 8) {
                  var5 = "30868-05.htm";
               } else if (var16 == 9) {
                  var5 = "30868-06.htm";
               } else if (var16 == 10) {
                  var5 = "30868-08.htm";
               } else if (var16 == 11) {
                  var5 = "30868-09.htm";
               } else if (var16 == 12) {
                  var5 = "30868-11.htm";
               }
            } else if (var3 == 30766) {
               if (var16 == 8) {
                  var5 = "30766-02.htm";
               } else if (var16 == 9) {
                  var5 = "30766-05.htm";
               } else if (var16 == 10) {
                  var5 = "30766-06.htm";
               } else if (var16 == 11 || var16 == 12 || var16 == 13) {
                  var5 = "30766-07.htm";
               }
            } else if (var3 == 30765) {
               if (var2.getCond() == 10) {
                  if (var2.getQuestItemsCount(3847) < 6L) {
                     var5 = "30765-03a.htm";
                  } else if (var2.getInt("ImpGraveKeeper") == 3) {
                     var5 = "30765-02.htm";
                     var2.setCond(11);
                     var2.takeItems(3847, 6L);
                     var2.giveItems(3869, 1L);
                  } else {
                     var5 = "<html><head><body>(You and your Clan didn't kill the Imperial Gravekeeper by your own, do it try again.)</body></html>";
                  }
               } else {
                  var5 = "<html><head><body>(You already have the Scepter of Judgement.)</body></html>";
               }
            } else if (var3 == 30759) {
               var5 = "30759-01.htm";
            } else if (var3 == 30758) {
               var5 = "30758-01.htm";
            }

            return var5;
         } else {
            int var7 = this.getLeaderVar(var2, "cond");
            if (var3 != 30645 || var7 != 1 && var7 != 2 && var7 != 3) {
               if (var3 == 30868) {
                  if (var7 == 9 || var7 == 10) {
                     var5 = "30868-07.htm";
                  } else if (var7 == 7) {
                     var5 = "30868-01.htm";
                  }
               } else if (var3 == 30764 && var7 == 4) {
                  var5 = "30764-01.htm";
               } else if (var3 == 30766 && var7 == 8) {
                  var5 = "30766-01.htm";
               } else if (var3 == 30512 && var7 > 2 && var7 < 6) {
                  var5 = "30512-01a.htm";
               } else if (var3 == 30765 && var7 == 10) {
                  var5 = "30765-01.htm";
               } else if (var3 == 30760) {
                  if (var7 == 3) {
                     var5 = "30760-11t.htm";
                  } else if (var7 == 4) {
                     var5 = "30760-15t.htm";
                  } else if (var7 == 12) {
                     var5 = "30760-19t.htm";
                  } else if (var7 == 13) {
                     var5 = "30766-24t.htm";
                  }
               }
            } else {
               var5 = "30645-01.htm";
            }

            return var5;
         }
      }
   }

   @Override
   public String onKill(NpcInstance var1, QuestState var2) {
      int var3 = var1.getNpcId();
      int var4 = this.getLeaderVar(var2, "cond");
      switch (var4) {
         case 2:
            switch (var3) {
               case 20137:
                  if (Rnd.chance(20)) {
                     this.giveItem(3841, 10L, var2);
                  }

                  return null;
               case 20243:
                  if (Rnd.chance(15)) {
                     this.giveItem(3842, 10L, var2);
                  }

                  return null;
               case 20282:
                  if (Rnd.chance(20)) {
                     this.giveItem(3842, 10L, var2);
                  }

                  return null;
               case 20285:
                  if (Rnd.chance(25)) {
                     this.giveItem(3841, 10L, var2);
                  }

                  return null;
               case 27178:
                  this.giveItem(3840, 10L, var2);
                  return null;
               default:
                  return null;
            }
         case 5:
            byte var6 = 0;
            switch (var3) {
               case 20654:
                  var6 = 25;
                  break;
               case 20656:
                  var6 = 35;
            }

            if (var6 > 0 && Rnd.chance(var6)) {
               switch (Rnd.get(3)) {
                  case 0:
                     if (this.getLeaderVar(var2, "Kurtz") < 4) {
                        return null;
                     }

                     this.giveItem(3845, 40L, var2);
                     return null;
                  case 1:
                     this.giveItem(3846, 10L, var2);
                     return null;
                  case 2:
                     this.giveItem(3844, 10L, var2);
               }
            }
            break;
         case 10:
            switch (var3) {
               case 20668:
                  if (Rnd.chance(15)) {
                     var2.addSpawn(27179, 120000);
                  }
                  break;
               case 27179:
                  if (Rnd.chance(80)) {
                     this.giveItem(3847, 6L, var2);
                  }
                  break;
               case 27181:
                  NpcInstance var5 = var2.addSpawn(30765, 120000);
                  Functions.npcSay(var5, "Curse of the gods on the one that defiles the property of the empire!");
                  this.setLeaderVar(var2, "ImpGraveKeeper", "3");
            }
      }

      return null;
   }
}
