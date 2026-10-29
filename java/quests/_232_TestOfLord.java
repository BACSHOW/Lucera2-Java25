package quests;

import java.util.HashMap;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Drop;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _232_TestOfLord extends Quest implements ScriptFile {
   private static int aXI = 30510;
   private static int aPz = 30515;
   private static int aXJ = 30558;
   private static int aXK = 30564;
   private static int aPH = 30565;
   private static int aXL = 30566;
   private static int aXM = 30567;
   private static int aXN = 30568;
   private static int aXO = 30641;
   private static int aPy = 30642;
   private static int aXP = 30643;
   private static int aXQ = 30649;
   private static int aXR = 20233;
   private static int aLy = 20269;
   private static int aXS = 20270;
   private static int aXT = 20564;
   private static int aXU = 20583;
   private static int aXV = 20584;
   private static int aXW = 20585;
   private static int aXX = 20586;
   private static int aXY = 20587;
   private static int aXZ = 20588;
   private static int aYa = 20778;
   private static int aYb = 20779;
   private static int aYc = 3390;
   private static int aYd = 1341;
   private static int aLD = 7562;
   private static int aYe = 3403;
   private static int aYf = 3398;
   private static int aYg = 3414;
   private static int aYh = 3415;
   private static int aYi = 3407;
   private static int aYj = 3408;
   private static int aYk = 3410;
   private static int aYl = 3391;
   private static int aYm = 3392;
   private static int aYn = 3393;
   private static int aYo = 3394;
   private static int aYp = 3395;
   private static int aYq = 3396;
   private static int aYr = 3397;
   private static int aYs = 3399;
   private static int aYt = 3400;
   private static int aYu = 3401;
   private static int aYv = 3402;
   private static int aYw = 3404;
   private static int aYx = 3405;
   private static int aYy = 3406;
   private static int aYz = 3409;
   private static int aYA = 3411;
   private static int aYB = 3412;
   private static int aYC = 3413;
   private static int aYD = 3416;
   private static Map<Integer, Drop> aUW = new HashMap<>();

   public _232_TestOfLord() {
      super(0);
      this.addStartNpc(aPH);
      this.addTalkId(aXI);
      this.addTalkId(aPz);
      this.addTalkId(aXJ);
      this.addTalkId(aXK);
      this.addTalkId(aXL);
      this.addTalkId(aXM);
      this.addTalkId(aXN);
      this.addTalkId(aXO);
      this.addTalkId(aPy);
      this.addTalkId(aXP);
      this.addTalkId(aXQ);
      aUW.put(aXU, new Drop(1, 10, 50).addItem(aYe));
      aUW.put(aXV, new Drop(1, 10, 55).addItem(aYe));
      aUW.put(aXW, new Drop(1, 10, 60).addItem(aYe));
      aUW.put(aXX, new Drop(1, 10, 65).addItem(aYe));
      aUW.put(aXY, new Drop(1, 10, 70).addItem(aYe));
      aUW.put(aXZ, new Drop(1, 10, 75).addItem(aYe));
      aUW.put(aLy, new Drop(1, 20, 40).addItem(aYf));
      aUW.put(aXS, new Drop(1, 20, 50).addItem(aYf));
      aUW.put(aYa, new Drop(4, 1, 100).addItem(aYg));
      aUW.put(aYb, new Drop(4, 1, 100).addItem(aYh));
      aUW.put(aXR, new Drop(1, 10, 100).addItem(aYi).addItem(aYj));
      aUW.put(aXT, new Drop(1, 20, 90).addItem(aYk));

      for (int var2 : aUW.keySet()) {
         this.addKillId(var2);
      }

      for (Drop var8 : aUW.values()) {
         for (int var6 : var8.itemList) {
            if (!this.isQuestItem(var6)) {
               this.addQuestItem(var6);
            }
         }
      }

      this.addQuestItem(aYl);
      this.addQuestItem(aYm);
      this.addQuestItem(aYn);
      this.addQuestItem(aYo);
      this.addQuestItem(aYp);
      this.addQuestItem(aYq);
      this.addQuestItem(aYr);
      this.addQuestItem(aYs);
      this.addQuestItem(aYt);
      this.addQuestItem(aYu);
      this.addQuestItem(aYv);
      this.addQuestItem(aYw);
      this.addQuestItem(aYx);
      this.addQuestItem(aYy);
      this.addQuestItem(aYz);
      this.addQuestItem(aYA);
      this.addQuestItem(aYB);
      this.addQuestItem(aYC);
      this.addQuestItem(aYD);
   }

   private static void e(QuestState var0) {
      var0.addSpawn(aXP, 21036, -107690, -3038);
   }

   @Override
   public String onEvent(String var1, QuestState var2, NpcInstance var3) {
      int var4 = var2.getState();
      if (var4 == 1) {
         if (var1.equalsIgnoreCase("30565-05.htm")) {
            var2.giveItems(aYl, 1L);
            var2.setState(2);
            var2.setCond(1);
            var2.playSound("ItemSound.quest_accept");
         }
      } else if (var4 == 2) {
         if (var1.equalsIgnoreCase("30565-12.htm") && var2.getQuestItemsCount(aYD) > 0L) {
            var2.takeItems(aYD, -1L);
            var2.giveItems(aYc, 1L);
            if (!var2.getPlayer().getVarB("prof2.3")) {
               var2.giveItems(aLD, 8L);
               var2.addExpAndSp(92955L, 16250L);
               var2.getPlayer().setVar("prof2.3", "1", -1L);
            }

            var2.playSound("ItemSound.quest_finish");
            var2.unset("cond");
            var2.exitCurrentQuest(true);
         } else if (var1.equalsIgnoreCase("30565-08.htm")) {
            var2.takeItems(aYw, -1L);
            var2.takeItems(aYy, -1L);
            var2.takeItems(aYA, -1L);
            var2.takeItems(aYz, -1L);
            var2.takeItems(aYl, -1L);
            var2.takeItems(aYt, -1L);
            var2.giveItems(aYB, 1L);
            var2.playSound("ItemSound.quest_middle");
            var2.setCond(3);
         } else if (var1.equalsIgnoreCase("30566-02.htm")) {
            var2.giveItems(aYm, 1L);
         } else if (var1.equalsIgnoreCase("30567-02.htm")) {
            var2.giveItems(aYn, 1L);
         } else if (var1.equalsIgnoreCase("30558-02.htm") && var2.getQuestItemsCount(57) >= 1000L) {
            var2.takeItems(57, 1000L);
            var2.giveItems(aYx, 1L);
            var2.playSound("ItemSound.quest_middle");
         } else if (var1.equalsIgnoreCase("30568-02.htm")) {
            var2.giveItems(aYo, 1L);
         } else if (var1.equalsIgnoreCase("30641-02.htm")) {
            var2.giveItems(aYp, 1L);
         } else if (var1.equalsIgnoreCase("30642-02.htm")) {
            var2.giveItems(aYq, 1L);
         } else if (var1.equalsIgnoreCase("30649-04.htm") && var2.getQuestItemsCount(aYB) > 0L) {
            var2.takeItems(aYB, -1L);
            var2.giveItems(aYC, 1L);
            var2.playSound("ItemSound.quest_middle");
            var2.setCond(4);
         } else if (var1.equalsIgnoreCase("30649-07.htm")) {
            var2.setCond(6);
            e(var2);
         }
      }

      return var1;
   }

   @Override
   public String onTalk(NpcInstance var1, QuestState var2) {
      if (var2.getQuestItemsCount(aYc) > 0L) {
         var2.exitCurrentQuest(true);
         return "completed";
      }

      int var3 = var2.getState();
      int var4 = var1.getNpcId();
      int var5 = var2.getCond();
      if (var3 == 1) {
         if (var4 != aPH) {
            return "noquest";
         } else if (var2.getPlayer().getRace() != Race.orc) {
            var2.exitCurrentQuest(true);
            return "30565-01.htm";
         } else if (var2.getPlayer().getClassId().getId() != 50) {
            var2.exitCurrentQuest(true);
            return "30565-02.htm";
         } else if (var2.getPlayer().getLevel() < 39) {
            var2.exitCurrentQuest(true);
            return "30565-03.htm";
         } else {
            var2.setCond(0);
            return "30565-04.htm";
         }
      } else if (var3 == 2 && var5 >= 1) {
         long var6 = var2.getQuestItemsCount(aYl);
         long var8 = var2.getQuestItemsCount(aYt);
         long var10 = var2.getQuestItemsCount(aYw);
         long var12 = var2.getQuestItemsCount(aYy);
         long var14 = var2.getQuestItemsCount(aYA);
         long var16 = var2.getQuestItemsCount(aYz);
         long var18 = var2.getQuestItemsCount(aYB);
         long var20 = var2.getQuestItemsCount(aYC);
         long var22 = var2.getQuestItemsCount(aYD);
         long var24 = var2.getQuestItemsCount(aYm);
         long var26 = var2.getQuestItemsCount(aYs);
         long var28 = var2.getQuestItemsCount(aYr);
         long var30 = var2.getQuestItemsCount(aYf);
         long var32 = var2.getQuestItemsCount(aYn);
         long var34 = var2.getQuestItemsCount(aYx);
         long var36 = var2.getQuestItemsCount(aYo);
         long var38 = var2.getQuestItemsCount(aYv);
         long var40 = var2.getQuestItemsCount(aYe);
         long var42 = var2.getQuestItemsCount(aYu);
         long var44 = var2.getQuestItemsCount(aYp);
         if (var4 == aPH) {
            if (var6 > 0L) {
               return this.f(var2) ? "30565-07.htm" : "30565-06.htm";
            }

            if (var18 > 0L) {
               return "30565-09.htm";
            }

            if (var20 > 0L) {
               return "30565-10.htm";
            }

            if (var22 > 0L) {
               return "30565-11.htm";
            }
         }

         if (var4 == aXL && var6 > 0L) {
            if (var8 > 0L) {
               return "30566-05.htm";
            }

            if (var24 == 0L) {
               return "30566-01.htm";
            }

            if (var26 == 0L) {
               return "30566-03.htm";
            }

            var2.takeItems(aYm, -1L);
            var2.takeItems(aYs, -1L);
            var2.giveItems(aYt, 1L);
            if (this.f(var2)) {
               var2.playSound("ItemSound.quest_jackpot");
               var2.setCond(2);
            } else {
               var2.playSound("ItemSound.quest_middle");
            }

            return "30566-04.htm";
         } else {
            if (var4 == aPz && var6 > 0L) {
               if (var24 > 0L && var8 == 0L) {
                  if (var26 == 0L) {
                     if (var28 == 0L) {
                        var2.giveItems(aYr, 1L);
                        return "30515-01.htm";
                     }

                     if (var30 < 20L) {
                        return "30515-02.htm";
                     }

                     var2.takeItems(aYr, -1L);
                     var2.takeItems(aYf, -1L);
                     var2.giveItems(aYs, 1L);
                     var2.playSound("ItemSound.quest_middle");
                     return "30515-03.htm";
                  }

                  if (var28 == 0L) {
                     return "30515-04.htm";
                  }
               } else if (var24 == 0L && var8 > 0L && var26 == 0L && var28 == 0L) {
                  return "30515-05.htm";
               }
            }

            if (var4 == aXM) {
               if (var12 == 0L) {
                  if (var32 == 0L) {
                     return "30567-01.htm";
                  }

                  if (var34 != 0L && var2.getQuestItemsCount(aYd) >= 1000L) {
                     var2.takeItems(aYn, -1L);
                     var2.takeItems(aYx, -1L);
                     var2.takeItems(aYd, 1000L);
                     var2.giveItems(aYy, 1L);
                     if (this.f(var2)) {
                        var2.playSound("ItemSound.quest_jackpot");
                        var2.setCond(2);
                     } else {
                        var2.playSound("ItemSound.quest_middle");
                     }

                     return "30567-04.htm";
                  }

                  return "30567-03.htm";
               }

               if (var32 == 0L) {
                  return "30567-05.htm";
               }
            }

            if (var4 == aXJ) {
               if (var32 > 0L && var12 == 0L) {
                  if (var34 > 0L) {
                     return "30558-04.htm";
                  }

                  return var2.getQuestItemsCount(57) < 1000L ? "30558-03.htm" : "30558-01.htm";
               }

               if (var32 == 0L && var12 > 0L) {
                  return "30558-05.htm";
               }
            }

            if (var4 == aXN) {
               if (var10 == 0L) {
                  if (var36 == 0L) {
                     return "30568-01.htm";
                  }

                  if (var38 != 0L && var40 >= 10L) {
                     var2.takeItems(aYo, -1L);
                     var2.takeItems(aYv, -1L);
                     var2.takeItems(aYe, -1L);
                     var2.giveItems(aYw, 1L);
                     if (this.f(var2)) {
                        var2.playSound("ItemSound.quest_jackpot");
                        var2.setCond(2);
                     } else {
                        var2.playSound("ItemSound.quest_middle");
                     }

                     return "30568-04.htm";
                  }

                  return "30568-03.htm";
               }

               if (var36 == 0L) {
                  return "30568-05.htm";
               }
            }

            if (var4 == aXK) {
               if (var36 > 0L && var10 == 0L) {
                  if (var38 == 0L) {
                     if (var42 > 0L) {
                        return "30564-02.htm";
                     }

                     var2.giveItems(aYu, 1L);
                     var2.playSound("ItemSound.quest_middle");
                     return "30564-01.htm";
                  }

                  if (var42 == 0L) {
                     return "30564-03.htm";
                  }
               } else if (var36 == 0L && var10 > 0L && var38 == 0L && var42 == 0L) {
                  return "30564-04.htm";
               }
            }

            if (var4 == aXI) {
               if (var10 == 0L) {
                  if (var38 == 0L && var36 > 0L && var42 > 0L) {
                     var2.takeItems(aYu, -1L);
                     var2.giveItems(aYv, 1L);
                     var2.playSound("ItemSound.quest_middle");
                     return "30510-01.htm";
                  }

                  if (var38 > 0L && var36 > 0L && var42 == 0L) {
                     return "30510-02.htm";
                  }
               } else if (var38 == 0L && var36 == 0L && var42 == 0L) {
                  return "30510-03.htm";
               }
            }

            if (var4 == aXO) {
               if (var16 == 0L) {
                  if (var44 == 0L) {
                     return "30641-01.htm";
                  }

                  if (var2.getQuestItemsCount(aYi) >= 10L && var2.getQuestItemsCount(aYj) >= 10L) {
                     var2.takeItems(aYi, -1L);
                     var2.takeItems(aYj, -1L);
                     var2.takeItems(aYp, -1L);
                     var2.giveItems(aYz, 1L);
                     if (this.f(var2)) {
                        var2.playSound("ItemSound.quest_jackpot");
                        var2.setCond(2);
                     } else {
                        var2.playSound("ItemSound.quest_middle");
                     }

                     return "30641-04.htm";
                  }

                  return "30641-03.htm";
               }

               if (var44 == 0L) {
                  return "30641-05.htm";
               }
            }

            if (var4 == aPy) {
               long var46 = var2.getQuestItemsCount(aYq);
               if (var14 == 0L) {
                  if (var46 == 0L) {
                     return "30642-01.htm";
                  }

                  if (var2.getQuestItemsCount(aYk) < 20L) {
                     return "30642-03.htm";
                  }

                  var2.takeItems(aYk, -1L);
                  var2.takeItems(aYq, -1L);
                  var2.giveItems(aYA, 1L);
                  if (this.f(var2)) {
                     var2.playSound("ItemSound.quest_jackpot");
                     var2.setCond(2);
                  } else {
                     var2.playSound("ItemSound.quest_middle");
                  }

                  return "30642-04.htm";
               }

               if (var46 == 0L) {
                  return "30642-05.htm";
               }
            }

            if (var4 == aXQ) {
               if (var18 > 0L) {
                  return "30649-01.htm";
               }

               if (var20 > 0L) {
                  if (var5 != 5 && (var2.getQuestItemsCount(aYh) <= 0L || var2.getQuestItemsCount(aYg) <= 0L)) {
                     return "30649-05.htm";
                  }

                  var2.takeItems(aYC, -1L);
                  var2.takeItems(aYg, -1L);
                  var2.takeItems(aYh, -1L);
                  var2.giveItems(aYD, 1L);
                  var2.playSound("ItemSound.quest_middle");
                  return "30649-06.htm";
               }

               if (var5 == 6 || var5 == 7) {
                  return "30649-08.htm";
               }
            }

            if (var4 == aXP && var2.getQuestItemsCount(aYD) > 0L) {
               var2.setCond(7);
               return "30643-01.htm";
            } else {
               return "noquest";
            }
         }
      } else {
         return "noquest";
      }
   }

   private boolean f(QuestState var1) {
      long var2 = var1.getQuestItemsCount(aYt);
      long var4 = var1.getQuestItemsCount(aYw);
      long var6 = var1.getQuestItemsCount(aYy);
      long var8 = var1.getQuestItemsCount(aYA);
      long var10 = var1.getQuestItemsCount(aYz);
      return var2 > 0L && var4 > 0L && var6 > 0L && var8 > 0L && var10 > 0L;
   }

   @Override
   public String onKill(NpcInstance var1, QuestState var2) {
      if (var2.getState() != 2) {
         return null;
      }

      int var3 = var1.getNpcId();
      Drop var4 = aUW.get(var3);
      if (var4 == null) {
         return null;
      }

      int var5 = var2.getCond();

      for (int var9 : var4.itemList) {
         long var10 = var2.getQuestItemsCount(aYl);
         if ((var9 != aYe || var10 > 0L && var2.getQuestItemsCount(aYo) > 0L && var2.getQuestItemsCount(aYw) == 0L)
            && (var9 != aYf || var10 > 0L && var2.getQuestItemsCount(aYm) > 0L && var2.getQuestItemsCount(aYr) > 0L)
            && (var3 != aXR || var10 > 0L && var2.getQuestItemsCount(aYp) > 0L)
            && (var3 != aXT || var10 > 0L && var2.getQuestItemsCount(aYq) > 0L)) {
            long var12 = var2.getQuestItemsCount(var9);
            if (var5 == var4.condition && var12 < var4.maxcount && Rnd.chance(var4.chance)) {
               var2.giveItems(var9, 1L);
               if (var12 + 1L == var4.maxcount) {
                  if (var5 == 4 && var2.getQuestItemsCount(aYg) > 0L && var2.getQuestItemsCount(aYh) > 0L) {
                     var2.setCond(5);
                  }

                  var2.playSound("ItemSound.quest_middle");
               } else {
                  var2.playSound("ItemSound.quest_itemget");
               }
            }
         }
      }

      return null;
   }

   @Override
   public void onLoad() {
   }

   @Override
   public void onReload() {
   }

   @Override
   public void onShutdown() {
   }
}
