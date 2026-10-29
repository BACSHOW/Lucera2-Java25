package quests;

import java.util.HashMap;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _230_TestOfSummoner extends Quest implements ScriptFile {
   static int MARK_OF_SUMMONER_ID = 3336;
   static int LETOLIZARDMAN_AMULET_ID = 3337;
   static int SAC_OF_REDSPORES_ID = 3338;
   static int KARULBUGBEAR_TOTEM_ID = 3339;
   static int SHARDS_OF_MANASHEN_ID = 3340;
   static int BREKAORC_TOTEM_ID = 3341;
   static int CRIMSON_BLOODSTONE_ID = 3342;
   static int TALONS_OF_TYRANT_ID = 3343;
   static int WINGS_OF_DRONEANT_ID = 3344;
   static int TUSK_OF_WINDSUS_ID = 3345;
   static int FANGS_OF_WYRM_ID = 3346;
   static int LARS_LIST1_ID = 3347;
   static int LARS_LIST2_ID = 3348;
   static int LARS_LIST3_ID = 3349;
   static int LARS_LIST4_ID = 3350;
   static int LARS_LIST5_ID = 3351;
   static int GALATEAS_LETTER_ID = 3352;
   static int BEGINNERS_ARCANA_ID = 3353;
   static int ALMORS_ARCANA_ID = 3354;
   static int CAMONIELL_ARCANA_ID = 3355;
   static int BELTHUS_ARCANA_ID = 3356;
   static int BASILLIA_ARCANA_ID = 3357;
   static int CELESTIEL_ARCANA_ID = 3358;
   static int BRYNTHEA_ARCANA_ID = 3359;
   static int CRYSTAL_OF_PROGRESS1_ID = 3360;
   static int CRYSTAL_OF_INPROGRESS1_ID = 3361;
   static int CRYSTAL_OF_FOUL1_ID = 3362;
   static int CRYSTAL_OF_DEFEAT1_ID = 3363;
   static int CRYSTAL_OF_VICTORY1_ID = 3364;
   static int CRYSTAL_OF_PROGRESS2_ID = 3365;
   static int CRYSTAL_OF_INPROGRESS2_ID = 3366;
   static int CRYSTAL_OF_FOUL2_ID = 3367;
   static int CRYSTAL_OF_DEFEAT2_ID = 3368;
   static int CRYSTAL_OF_VICTORY2_ID = 3369;
   static int CRYSTAL_OF_PROGRESS3_ID = 3370;
   static int CRYSTAL_OF_INPROGRESS3_ID = 3371;
   static int CRYSTAL_OF_FOUL3_ID = 3372;
   static int CRYSTAL_OF_DEFEAT3_ID = 3373;
   static int CRYSTAL_OF_VICTORY3_ID = 3374;
   static int CRYSTAL_OF_PROGRESS4_ID = 3375;
   static int CRYSTAL_OF_INPROGRESS4_ID = 3376;
   static int CRYSTAL_OF_FOUL4_ID = 3377;
   static int CRYSTAL_OF_DEFEAT4_ID = 3378;
   static int CRYSTAL_OF_VICTORY4_ID = 3379;
   static int CRYSTAL_OF_PROGRESS5_ID = 3380;
   static int CRYSTAL_OF_INPROGRESS5_ID = 3381;
   static int CRYSTAL_OF_FOUL5_ID = 3382;
   static int CRYSTAL_OF_DEFEAT5_ID = 3383;
   static int CRYSTAL_OF_VICTORY5_ID = 3384;
   static int CRYSTAL_OF_PROGRESS6_ID = 3385;
   static int CRYSTAL_OF_INPROGRESS6_ID = 3386;
   static int CRYSTAL_OF_FOUL6_ID = 3387;
   static int CRYSTAL_OF_DEFEAT6_ID = 3388;
   static int CRYSTAL_OF_VICTORY6_ID = 3389;
   static int[] npc = new int[]{30063, 30634, 30635, 30636, 30637, 30638, 30639, 30640};
   static int Lara = npc[0];
   static int Galatea = npc[1];
   static int Almors = npc[2];
   static int Camoniell = npc[3];
   static int Belthus = npc[4];
   static int Basilla = npc[5];
   static int Celestiel = npc[6];
   static int Brynthea = npc[7];
   static int[][] SUMMONERS = new int[][]{
      {30635, ALMORS_ARCANA_ID, CRYSTAL_OF_VICTORY1_ID},
      {30636, CAMONIELL_ARCANA_ID, CRYSTAL_OF_VICTORY2_ID},
      {30637, BELTHUS_ARCANA_ID, CRYSTAL_OF_VICTORY3_ID},
      {30638, BASILLIA_ARCANA_ID, CRYSTAL_OF_VICTORY4_ID},
      {30639, CELESTIEL_ARCANA_ID, CRYSTAL_OF_VICTORY5_ID},
      {30640, BRYNTHEA_ARCANA_ID, CRYSTAL_OF_VICTORY6_ID}
   };
   static Map<Integer, String> NAMES = new HashMap<>();
   static Map<Integer, Integer[]> DROPLIST_LARA = new HashMap<>();
   static String[] STATS = new String[]{"cond", "step", "Lara_Part", "Arcanas", "Belthus", "Brynthea", "Celestiel", "Camoniell", "Basilla", "Almors"};
   static int[][] LISTS = new int[][]{
      new int[0],
      {LARS_LIST1_ID, SAC_OF_REDSPORES_ID, LETOLIZARDMAN_AMULET_ID},
      {LARS_LIST2_ID, KARULBUGBEAR_TOTEM_ID, SHARDS_OF_MANASHEN_ID},
      {LARS_LIST3_ID, CRIMSON_BLOODSTONE_ID, BREKAORC_TOTEM_ID},
      {LARS_LIST4_ID, TUSK_OF_WINDSUS_ID, TALONS_OF_TYRANT_ID},
      {LARS_LIST5_ID, WINGS_OF_DRONEANT_ID, FANGS_OF_WYRM_ID}
   };
   static Map<Integer, Integer[]> DROPLIST_SUMMON = new HashMap<>();
   static Map<Integer, String> DROPLIST_SUMMON_VARS = new HashMap<>();

   @Override
   public void onLoad() {
   }

   @Override
   public void onReload() {
   }

   @Override
   public void onShutdown() {
   }

   public _230_TestOfSummoner() {
      super(0);
      this.addStartNpc(Galatea);

      for (int var4 : npc) {
         this.addTalkId(var4);
      }

      for (int var8 : DROPLIST_LARA.keySet()) {
         this.addKillId(var8);
      }

      for (int var9 : DROPLIST_SUMMON.keySet()) {
         this.addKillId(var9);
         this.addAttackId(var9);
      }

      for (int var7 = 3337; var7 <= 3389; var7++) {
         this.addQuestItem(var7);
      }
   }

   @Override
   public String onEvent(String var1, QuestState var2, NpcInstance var3) {
      String var4 = var1;
      if (var1.equalsIgnoreCase("30634-08.htm")) {
         for (String var8 : STATS) {
            if (!var8.equalsIgnoreCase("Arcanas") && !var8.equalsIgnoreCase("Lara_Part")) {
               if (var8.equalsIgnoreCase("cond")) {
                  var2.setCond(1);
               } else {
                  var2.set(var8, "1");
               }
            }
         }

         var2.setState(2);
         var2.playSound("ItemSound.quest_accept");
      } else if (var1.equalsIgnoreCase("30634-07.htm")) {
         var2.giveItems(GALATEAS_LETTER_ID, 1L, false);
      } else if (var1.equalsIgnoreCase("30063-02.htm")) {
         int var9 = Rnd.get(5) + 1;
         var2.giveItems(LISTS[var9][0], 1L, false);
         var2.takeItems(GALATEAS_LETTER_ID, 1L);
         var2.set("Lara_Part", this.str(var9));
         var2.set("step", "2");
         var2.setCond(2);
      } else if (var1.equalsIgnoreCase("30063-04.htm")) {
         int var10 = Rnd.get(5) + 1;
         var2.giveItems(LISTS[var10][0], 1L, false);
         var2.set("Lara_Part", this.str(var10));
      } else if (var1.equalsIgnoreCase("30635-02.htm")) {
         if (var2.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0L) {
            var4 = "30635-03.htm";
            var2.set("Almors", "2");
         }
      } else if (var1.equalsIgnoreCase("30635-04.htm")) {
         var2.giveItems(CRYSTAL_OF_PROGRESS1_ID, 1L, false);
         var2.takeItems(CRYSTAL_OF_FOUL1_ID, -1L);
         var2.takeItems(CRYSTAL_OF_DEFEAT1_ID, -1L);
         var2.takeItems(BEGINNERS_ARCANA_ID, 1L);
      } else if (var1.equalsIgnoreCase("30636-02.htm")) {
         if (var2.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0L) {
            var4 = "30636-03.htm";
            var2.set("Camoniell", "2");
         }
      } else if (var1.equalsIgnoreCase("30636-04.htm")) {
         var2.giveItems(CRYSTAL_OF_PROGRESS2_ID, 1L, false);
         var2.takeItems(CRYSTAL_OF_FOUL2_ID, -1L);
         var2.takeItems(CRYSTAL_OF_DEFEAT2_ID, -1L);
         var2.takeItems(BEGINNERS_ARCANA_ID, 1L);
      } else if (var1.equalsIgnoreCase("30637-02.htm")) {
         if (var2.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0L) {
            var4 = "30637-03.htm";
            var2.set("Belthus", "2");
         }
      } else if (var1.equalsIgnoreCase("30637-04.htm")) {
         var2.giveItems(CRYSTAL_OF_PROGRESS3_ID, 1L, false);
         var2.takeItems(CRYSTAL_OF_FOUL3_ID, -1L);
         var2.takeItems(CRYSTAL_OF_DEFEAT3_ID, -1L);
         var2.takeItems(BEGINNERS_ARCANA_ID, 1L);
      } else if (var1.equalsIgnoreCase("30638-02.htm")) {
         if (var2.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0L) {
            var4 = "30638-03.htm";
            var2.set("Basilla", "2");
         }
      } else if (var1.equalsIgnoreCase("30638-04.htm")) {
         var2.giveItems(CRYSTAL_OF_PROGRESS4_ID, 1L, false);
         var2.takeItems(CRYSTAL_OF_FOUL4_ID, -1L);
         var2.takeItems(CRYSTAL_OF_DEFEAT4_ID, -1L);
         var2.takeItems(BEGINNERS_ARCANA_ID, 1L);
      } else if (var1.equalsIgnoreCase("30639-02.htm")) {
         if (var2.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0L) {
            var4 = "30639-03.htm";
            var2.set("Celestiel", "2");
         }
      } else if (var1.equalsIgnoreCase("30639-04.htm")) {
         var2.giveItems(CRYSTAL_OF_PROGRESS5_ID, 1L, false);
         var2.takeItems(CRYSTAL_OF_FOUL5_ID, -1L);
         var2.takeItems(CRYSTAL_OF_DEFEAT5_ID, -1L);
         var2.takeItems(BEGINNERS_ARCANA_ID, 1L);
      } else if (var1.equalsIgnoreCase("30640-02.htm")) {
         if (var2.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0L) {
            var4 = "30640-03.htm";
            var2.set("Brynthea", "2");
         }
      } else if (var1.equalsIgnoreCase("30640-04.htm")) {
         var2.giveItems(CRYSTAL_OF_PROGRESS6_ID, 1L, false);
         var2.takeItems(CRYSTAL_OF_FOUL6_ID, -1L);
         var2.takeItems(CRYSTAL_OF_DEFEAT6_ID, -1L);
         var2.takeItems(BEGINNERS_ARCANA_ID, 1L);
      }

      return var4;
   }

   @Override
   public String onTalk(NpcInstance var1, QuestState var2) {
      if (var2.getQuestItemsCount(MARK_OF_SUMMONER_ID) > 0L) {
         var2.exitCurrentQuest(true);
         return "completed";
      }

      String var3 = "noquest";
      int var4 = var1.getNpcId();
      int var5 = var2.getState();
      if (var5 == 1 && var4 == 30634) {
         for (String var19 : STATS) {
            if (var19.equalsIgnoreCase("cond")) {
               var2.setCond(0);
            } else {
               var2.set(var19, "0");
            }
         }

         if (var2.getPlayer().getClassId() != ClassId.wizard
            && var2.getPlayer().getClassId() != ClassId.elven_wizard
            && var2.getPlayer().getClassId() != ClassId.dark_wizard) {
            var3 = "30634-01.htm";
            var2.exitCurrentQuest(true);
         } else if (var2.getPlayer().getLevel() > 38) {
            var3 = "30634-03.htm";
         } else {
            var3 = "30634-02.htm";
            var2.exitCurrentQuest(true);
         }
      } else if (var5 == 2) {
         int var6 = var2.getInt("Lara_Part");
         int var7 = var2.getInt("Arcanas");
         int var8 = var2.getInt("step");
         if (var4 == 30634) {
            if (var8 == 1) {
               var3 = "30634-09.htm";
            } else if (var8 == 2) {
               if (var7 == 6) {
                  var3 = "30634-12.htm";
                  var2.playSound("ItemSound.quest_finish");
                  var2.takeItems(LARS_LIST1_ID, -1L);
                  var2.takeItems(LARS_LIST2_ID, -1L);
                  var2.takeItems(LARS_LIST3_ID, -1L);
                  var2.takeItems(LARS_LIST4_ID, -1L);
                  var2.takeItems(LARS_LIST5_ID, -1L);
                  var2.takeItems(ALMORS_ARCANA_ID, -1L);
                  var2.takeItems(BASILLIA_ARCANA_ID, -1L);
                  var2.takeItems(CAMONIELL_ARCANA_ID, -1L);
                  var2.takeItems(CELESTIEL_ARCANA_ID, -1L);
                  var2.takeItems(BELTHUS_ARCANA_ID, -1L);
                  var2.takeItems(BRYNTHEA_ARCANA_ID, -1L);
                  var2.giveItems(MARK_OF_SUMMONER_ID, 1L);
                  if (!var2.getPlayer().getVarB("prof2.3")) {
                     var2.giveItems(7562, 8L);
                     var2.addExpAndSp(148409L, 30000L);
                     var2.getPlayer().setVar("prof2.3", "1", -1L);
                  }

                  var2.playSound("ItemSound.quest_finish");
                  var2.exitCurrentQuest(true);
               }
            } else {
               var3 = "30634-10.htm";
            }
         } else if (var4 == Lara) {
            if (var8 == 1) {
               var3 = "30063-01.htm";
            } else if (var6 == 0) {
               var3 = "30063-03.htm";
            } else {
               long var9 = var2.getQuestItemsCount(LISTS[var6][1]);
               long var11 = var2.getQuestItemsCount(LISTS[var6][2]);
               if (var9 < 30L || var11 < 30L) {
                  var3 = "30063-05.htm";
               } else if (var9 > 29L && var11 > 29L) {
                  var3 = "30063-06.htm";
                  var2.giveItems(BEGINNERS_ARCANA_ID, 2L, false);
                  var2.takeItems(LISTS[var6][0], 1L);
                  var2.takeItems(LISTS[var6][1], -1L);
                  var2.takeItems(LISTS[var6][2], -1L);
                  var2.setCond(3);
                  var2.set("Lara_Part", "0");
               }
            }
         } else {
            for (int[] var12 : SUMMONERS) {
               if (var12[0] == var4) {
                  Integer[] var13 = DROPLIST_SUMMON.get(var4 - 30635 + 27102);
                  int var14 = var2.getInt(NAMES.get(var12[0]));
                  if (var8 > 1) {
                     if (var2.getQuestItemsCount(var13[0]) > 0L) {
                        var3 = this.str(var4) + "-08.htm";
                     } else if (var2.getQuestItemsCount(var13[1]) > 0L) {
                        var2.addNotifyOfDeath(var2.getPlayer(), true);
                        var3 = this.str(var4) + "-09.htm";
                     } else if (var2.getQuestItemsCount(var13[3]) > 0L) {
                        var3 = this.str(var4) + "-05.htm";
                     } else if (var2.getQuestItemsCount(var13[2]) > 0L) {
                        var3 = this.str(var4) + "-06.htm";
                     } else if (var2.getQuestItemsCount(var13[4]) > 0L) {
                        var3 = this.str(var4) + "-07.htm";
                        var2.takeItems(SUMMONERS[var4 - 30635][2], -1L);
                        var2.giveItems(SUMMONERS[var4 - 30635][1], 1L, false);
                        if (var2.getQuestItemsCount(3354)
                              + var2.getQuestItemsCount(3355)
                              + var2.getQuestItemsCount(3356)
                              + var2.getQuestItemsCount(3357)
                              + var2.getQuestItemsCount(3358)
                              + var2.getQuestItemsCount(3359)
                           >= 6L) {
                           var2.setCond(4);
                        }

                        var2.set(NAMES.get(var12[0]), "7");
                        var2.set("Arcanas", this.str(var7 + 1));
                     } else if (var14 == 7) {
                        var3 = this.str(var4) + "-10.htm";
                     } else {
                        var3 = this.str(var4) + "-01.htm";
                     }
                  }
               }
            }
         }
      }

      return var3;
   }

   @Override
   public String onDeath(Creature var1, Creature var2, QuestState var3) {
      if (var1 != null && var2 != null) {
         int var4 = var1.getNpcId();
         if ((var2 == var3.getPlayer() || var2 == var3.getPlayer().getPet()) && var4 >= 27102 && var4 <= 27107) {
            String[] var5 = new String[]{"Almors", "Camoniell", "Belthus", "Basilla", "Celestiel", "Brynthea"};
            String var6 = var5[var4 - 27102];
            Integer[] var7 = DROPLIST_SUMMON.get(var4);
            int var8 = var7[3];
            if (var3.getInt(var6) == 3) {
               var3.set(var6, "4");
               var3.giveItems(var8, 1L, false);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @Override
   public String onAttack(NpcInstance var1, QuestState var2) {
      int var3 = var1.getNpcId();
      if (var3 >= 27102 && var3 <= 27107) {
         String[] var4 = new String[]{"Almors", "Camoniell", "Belthus", "Basilla", "Celestiel", "Brynthea"};
         String var5 = var4[var3 - 27102];
         Integer[] var6 = DROPLIST_SUMMON.get(var3);
         int var7 = var6[0];
         int var8 = var6[1];
         if (var2.getInt(var5) == 2) {
            var2.set(var5, "3");
            var2.giveItems(var8, 1L, false);
            var2.takeItems(var7, 1L);
            var2.playSound("ItemSound.quest_itemget");
         }

         if (var2.getQuestItemsCount(var6[2]) != 0L) {
            return null;
         }

         Summon var9 = var2.getPlayer().getPet();
         if (var9 == null || var9.isPet()) {
            var2.giveItems(var6[2], 1L, false);
         }
      }

      return null;
   }

   @Override
   public String onKill(NpcInstance var1, QuestState var2) {
      int var3 = var1.getNpcId();
      if (DROPLIST_LARA.containsKey(var3)) {
         Integer[] var4 = DROPLIST_LARA.get(var3);
         String var5 = "Lara_Part";
         int var6 = var4[0];
         int var7 = var4[1];
         int var8 = var4[2];
         long var9 = var2.getQuestItemsCount(var8);
         if (var2.getInt(var5) == var6 && var9 < 30L && Rnd.chance(var7)) {
            var2.giveItems(var8, 1L, true);
            if (var9 == 29L) {
               var2.playSound("ItemSound.quest_middle");
            } else {
               var2.playSound("ItemSound.quest_itemget");
            }
         }
      } else if (DROPLIST_SUMMON.containsKey(var3)) {
         String[] var16 = new String[]{"Almors", "Camoniell", "Belthus", "Basilla", "Celestiel", "Brynthea"};
         String var17 = var16[var3 - 27102];
         Integer[] var18 = DROPLIST_SUMMON.get(var3);
         int var19 = var18[1];
         int var20 = var18[2];
         int var21 = var18[4];
         if (var2.getInt(var17) == 3) {
            boolean var10 = var2.getQuestItemsCount(var20) == 0L;
            boolean var11 = true;

            for (Integer var15 : DROPLIST_SUMMON.get(var3)) {
               if (!var11) {
                  var2.takeItems(var15, -1L);
               }

               var11 = false;
            }

            var2.takeItems(var19, -1L);
            if (var10) {
               var2.set(var17, "6");
               var2.giveItems(var21, 1L, false);
               var2.playSound("ItemSound.quest_middle");
            } else {
               var2.set(var17, "5");
            }
         }
      }

      return null;
   }

   static {
      NAMES.put(30635, "Almors");
      NAMES.put(30636, "Camoniell");
      NAMES.put(30637, "Belthus");
      NAMES.put(30638, "Basilla");
      NAMES.put(30639, "Celestiel");
      NAMES.put(30640, "Brynthea");
      DROPLIST_LARA.put(20555, new Integer[]{1, 80, SAC_OF_REDSPORES_ID});
      DROPLIST_LARA.put(20557, new Integer[]{1, 25, LETOLIZARDMAN_AMULET_ID});
      DROPLIST_LARA.put(20558, new Integer[]{1, 25, LETOLIZARDMAN_AMULET_ID});
      DROPLIST_LARA.put(20559, new Integer[]{1, 25, LETOLIZARDMAN_AMULET_ID});
      DROPLIST_LARA.put(20580, new Integer[]{1, 50, LETOLIZARDMAN_AMULET_ID});
      DROPLIST_LARA.put(20581, new Integer[]{1, 75, LETOLIZARDMAN_AMULET_ID});
      DROPLIST_LARA.put(20582, new Integer[]{1, 75, LETOLIZARDMAN_AMULET_ID});
      DROPLIST_LARA.put(20600, new Integer[]{2, 80, KARULBUGBEAR_TOTEM_ID});
      DROPLIST_LARA.put(20563, new Integer[]{2, 80, SHARDS_OF_MANASHEN_ID});
      DROPLIST_LARA.put(20552, new Integer[]{3, 60, CRIMSON_BLOODSTONE_ID});
      DROPLIST_LARA.put(20267, new Integer[]{3, 25, BREKAORC_TOTEM_ID});
      DROPLIST_LARA.put(20268, new Integer[]{3, 25, BREKAORC_TOTEM_ID});
      DROPLIST_LARA.put(20271, new Integer[]{3, 25, BREKAORC_TOTEM_ID});
      DROPLIST_LARA.put(20269, new Integer[]{3, 50, BREKAORC_TOTEM_ID});
      DROPLIST_LARA.put(20270, new Integer[]{3, 50, BREKAORC_TOTEM_ID});
      DROPLIST_LARA.put(20553, new Integer[]{4, 70, TUSK_OF_WINDSUS_ID});
      DROPLIST_LARA.put(20192, new Integer[]{4, 50, TALONS_OF_TYRANT_ID});
      DROPLIST_LARA.put(20193, new Integer[]{4, 50, TALONS_OF_TYRANT_ID});
      DROPLIST_LARA.put(20089, new Integer[]{5, 30, WINGS_OF_DRONEANT_ID});
      DROPLIST_LARA.put(20090, new Integer[]{5, 60, WINGS_OF_DRONEANT_ID});
      DROPLIST_LARA.put(20176, new Integer[]{5, 50, FANGS_OF_WYRM_ID});
      DROPLIST_SUMMON.put(
         27102, new Integer[]{CRYSTAL_OF_PROGRESS1_ID, CRYSTAL_OF_INPROGRESS1_ID, CRYSTAL_OF_FOUL1_ID, CRYSTAL_OF_DEFEAT1_ID, CRYSTAL_OF_VICTORY1_ID}
      );
      DROPLIST_SUMMON.put(
         27103, new Integer[]{CRYSTAL_OF_PROGRESS2_ID, CRYSTAL_OF_INPROGRESS2_ID, CRYSTAL_OF_FOUL2_ID, CRYSTAL_OF_DEFEAT2_ID, CRYSTAL_OF_VICTORY2_ID}
      );
      DROPLIST_SUMMON.put(
         27104, new Integer[]{CRYSTAL_OF_PROGRESS3_ID, CRYSTAL_OF_INPROGRESS3_ID, CRYSTAL_OF_FOUL3_ID, CRYSTAL_OF_DEFEAT3_ID, CRYSTAL_OF_VICTORY3_ID}
      );
      DROPLIST_SUMMON.put(
         27105, new Integer[]{CRYSTAL_OF_PROGRESS4_ID, CRYSTAL_OF_INPROGRESS4_ID, CRYSTAL_OF_FOUL4_ID, CRYSTAL_OF_DEFEAT4_ID, CRYSTAL_OF_VICTORY4_ID}
      );
      DROPLIST_SUMMON.put(
         27106, new Integer[]{CRYSTAL_OF_PROGRESS5_ID, CRYSTAL_OF_INPROGRESS5_ID, CRYSTAL_OF_FOUL5_ID, CRYSTAL_OF_DEFEAT5_ID, CRYSTAL_OF_VICTORY5_ID}
      );
      DROPLIST_SUMMON.put(
         27107, new Integer[]{CRYSTAL_OF_PROGRESS6_ID, CRYSTAL_OF_INPROGRESS6_ID, CRYSTAL_OF_FOUL6_ID, CRYSTAL_OF_DEFEAT6_ID, CRYSTAL_OF_VICTORY6_ID}
      );
      NAMES.put(27102, "Almors");
      NAMES.put(27103, "Camoniell");
      NAMES.put(27104, "Belthus");
      NAMES.put(27105, "Basilla");
      NAMES.put(27106, "Celestiel");
      NAMES.put(27107, "Brynthea");
   }
}
