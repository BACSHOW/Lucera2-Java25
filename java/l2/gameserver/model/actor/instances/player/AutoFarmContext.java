package l2.gameserver.model.actor.instances.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import l2.commons.lang.reference.HardReference;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.player.AutoArcherFarmTask;
import l2.gameserver.model.actor.player.AutoFarmEndTask;
import l2.gameserver.model.actor.player.AutoHealFarmTask;
import l2.gameserver.model.actor.player.AutoMagicFarmTask;
import l2.gameserver.model.actor.player.AutoPhysicalFarmTask;
import l2.gameserver.model.actor.player.AutoSummonFarmTask;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.tables.PetSkillsTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.taskmanager.AutoFarmManager;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;

public class AutoFarmContext {
   private final List<Integer> Mn = Arrays.asList(0, 1, 2, 3);
   private final List<Integer> Mo = Arrays.asList(4, 5);
   private final List<Integer> Mp = Arrays.asList(6, 7, 8, 9);
   private final List<Integer> Mq = Arrays.asList(10, 11);
   private final List<Integer> Mr = new ArrayList<>();
   private final List<Integer> Ms = new ArrayList<>();
   private final List<Integer> Mt = new ArrayList<>();
   private final List<Integer> Mu = new ArrayList<>();
   private final List<Integer> Mv = new ArrayList<>();
   private final List<Integer> Mw = new ArrayList<>();
   private final List<Integer> Mx = new ArrayList<>();
   private final HardReference<Player> e;
   private int My;
   private int Mz;
   private int MA;
   private int MB;
   private boolean MC = false;
   private int MD;
   private boolean ME = false;
   private int MF;
   private boolean MG = false;
   private int MH;
   private boolean MI = false;
   private int MJ;
   private boolean MK = false;
   private int ML;
   private boolean MM = false;
   private int MN;
   private boolean MO = false;
   private int MP;
   private int MQ;
   private int MR;
   private int MS;
   private int MT;
   private int MU;
   private int MV;
   private boolean MW = false;
   private boolean MX = false;
   private boolean MY = false;
   private boolean MZ = false;
   private boolean Na = false;
   private boolean Nb = false;
   private boolean Nc = false;
   private boolean Nd = false;
   private long Ne;
   private long Nf = 0L;
   private long Ng = 0L;
   private boolean Nh = false;
   private Location Ni = null;
   private ScheduledFuture<?> Nj;
   private ScheduledFuture<?> Nk;

   public AutoFarmContext(Player var1) {
      this.e = var1.getRef();
   }

   private static boolean c(Skill var0) {
      return var0.getSkillType() == Skill.SkillType.MANAHEAL || var0.getSkillType() == Skill.SkillType.MANAHEAL_PERCENT;
   }

   private static boolean d(Skill var0) {
      return var0.getSkillType() == Skill.SkillType.HEAL || var0.getSkillType() == Skill.SkillType.HEAL_PERCENT;
   }

   public Player getPlayer() {
      return this.e.get();
   }

   public void setFarmTypeValue(int var1) {
      if (var1 < 0) {
         var1 = 0;
      } else if (var1 > 4) {
         var1 = 4;
      }

      this.My = var1;
      if (this.isAutofarming()) {
         this.stopFarmTask(true);
      }
   }

   private int cC() {
      return this.My;
   }

   public void setRadiusValue(int var1) {
      this.MA = var1;
   }

   public void setShortcutPageValue(int var1) {
      if (var1 < 1) {
         var1 = 1;
      } else if (var1 > 10) {
         var1 = 10;
      }

      int var2 = var1 - 1;
      this.Mz = var2;
   }

   public int getAttackPercent() {
      return this.MS;
   }

   public int getAttackChance() {
      return this.MB;
   }

   public int getChancePercent() {
      return this.MT;
   }

   public int getChanceChance() {
      return this.MD;
   }

   public int getSelfPercent() {
      return this.MU;
   }

   public int getSelfChance() {
      return this.MF;
   }

   public int getLifePercent() {
      return this.MV;
   }

   public int getLifeChance() {
      return this.MH;
   }

   public void setAttackSkillValue(boolean var1, int var2) {
      if (var1) {
         this.MS = var2;
      } else {
         this.MB = var2;
      }
   }

   public void setChanceSkillValue(boolean var1, int var2) {
      if (var1) {
         this.MT = var2;
      } else {
         this.MD = var2;
      }
   }

   public void setSelfSkillValue(boolean var1, int var2) {
      if (var1) {
         this.MU = var2;
      } else {
         this.MF = var2;
      }
   }

   public void setLifeSkillValue(boolean var1, int var2) {
      if (var1) {
         this.MV = var2;
      } else {
         this.MH = var2;
      }
   }

   public void restoreVariables(Player var1) {
      this.setAttackSkillValue(false, var1.getVarInt("attackChanceSkills", Config.ATTACK_SKILL_CHANCE));
      this.setAttackSkillValue(true, var1.getVarInt("attackSkillsPercent", Config.ATTACK_SKILL_PERCENT));
      this.setChanceSkillValue(false, var1.getVarInt("chanceChanceSkills", Config.CHANCE_SKILL_CHANCE));
      this.setChanceSkillValue(true, var1.getVarInt("chanceSkillsPercent", Config.CHANCE_SKILL_PERCENT));
      this.setSelfSkillValue(false, var1.getVarInt("selfChanceSkills", Config.SELF_SKILL_CHANCE));
      this.setSelfSkillValue(true, var1.getVarInt("selfSkillsPercent", Config.SELF_SKILL_PERCENT));
      this.setLifeSkillValue(false, var1.getVarInt("healChanceSkills", Config.HEAL_SKILL_CHANCE));
      this.setLifeSkillValue(true, var1.getVarInt("healSkillsPercent", Config.HEAL_SKILL_PERCENT));
      this.setSummonAttackSkillValue(false, var1.getVarInt("attackSummonChanceSkills", Config.SUMMON_ATTACK_SKILL_CHANCE));
      this.setSummonAttackSkillValue(true, var1.getVarInt("attackSummonSkillsPercent", Config.SUMMON_ATTACK_SKILL_PERCENT));
      this.setSummonSelfSkillValue(false, var1.getVarInt("selfSummonChanceSkills", Config.SUMMON_SELF_SKILL_CHANCE));
      this.setSummonSelfSkillValue(true, var1.getVarInt("selfSummonSkillsPercent", Config.SUMMON_SELF_SKILL_PERCENT));
      this.setSummonLifeSkillValue(false, var1.getVarInt("healSummonChanceSkills", Config.SUMMON_HEAL_SKILL_CHANCE));
      this.setSummonLifeSkillValue(true, var1.getVarInt("healSummonSkillsPercent", Config.SUMMON_HEAL_SKILL_PERCENT));
      this.setShortcutPageValue(var1.getVarInt("shortcutPage", Config.SHORTCUT_PAGE));
      this.setRadiusValue(var1.getVarInt("farmDistance", Config.SEARCH_DISTANCE));
      this.setFarmTypeValue(var1.getVarInt("farmType", Config.FARM_TYPE));
      this.setRndAttackSkills(var1.getVarB("farmRndAttackSkills", false), true);
      this.setRndChanceSkills(var1.getVarB("farmRndChanceSkills", false), true);
      this.setRndSelfSkills(var1.getVarB("farmRndSelfSkills", false), true);
      this.setRndLifeSkills(var1.getVarB("farmRndLifeSkills", false), true);
      this.setRndSummonAttackSkills(var1.getVarB("farmRndSummonAttackSkills", false), true);
      this.setRndSummonSelfSkills(var1.getVarB("farmRndSummonSelfSkills", false), true);
      this.setRndSummonLifeSkills(var1.getVarB("farmRndSummonLifeSkills", false), true);
      this.setLeaderAssist(var1.getVarB("farmLeaderAssist", false), true);
      this.setKeepLocation(var1.getLoc(), var1.getVarB("farmKeepLocation", false), true);
      this.setExDelaySkill(var1.getVarB("farmExDelaySkill", false), true);
      this.setExSummonDelaySkill(var1.getVarB("farmExSummonDelaySkill", false), true);
      this.setRunTargetCloseUp(var1.getVarB("farmRunTargetCloseUp", false), true);
      this.setUseSummonSkills(var1.getVarB("farmUseSummonSkills", false), true);
      this.setAssistMonsterAttack(var1.getVarB("farmAssistMonsterAttack", false), true);
      this.setTargetRestoreMp(var1.getVarB("farmTargetRestoreMp", false), true);
      this.N(var1);
   }

   private void N(Player var1) {
      String var2 = var1.getVar("farmAttackSkills");
      if (var2 != null && !var2.isEmpty()) {
         this.getAttackSpells().clear();
         String[] var3 = var2.split(";");

         for (String var7 : var3) {
            if (var7 != null) {
               Skill var8 = var1.getKnownSkill(Integer.parseInt(var7));
               if (var8 != null) {
                  this.getAttackSpells().add(var8.getId());
               }
            }
         }
      }

      String var14 = var1.getVar("farmChanceSkills");
      if (var14 != null && !var14.isEmpty()) {
         this.getChanceSpells().clear();
         String[] var15 = var14.split(";");

         for (String var29 : var15) {
            if (var29 != null) {
               Skill var9 = var1.getKnownSkill(Integer.parseInt(var29));
               if (var9 != null) {
                  this.getChanceSpells().add(var9.getId());
               }
            }
         }
      }

      String var16 = var1.getVar("farmSelfSkills");
      if (var16 != null && !var16.isEmpty()) {
         this.getSelfSpells().clear();
         String[] var18 = var16.split(";");

         for (String var35 : var18) {
            if (var35 != null) {
               Skill var10 = var1.getKnownSkill(Integer.parseInt(var35));
               if (var10 != null) {
                  this.getSelfSpells().add(var10.getId());
               }
            }
         }
      }

      String var19 = var1.getVar("farmHealSkills");
      if (var19 != null && !var19.isEmpty()) {
         this.getLowLifeSpells().clear();
         String[] var22 = var19.split(";");

         for (String var40 : var22) {
            if (var40 != null) {
               Skill var11 = var1.getKnownSkill(Integer.parseInt(var40));
               if (var11 != null) {
                  this.getLowLifeSpells().add(var11.getId());
               }
            }
         }
      }

      String var23 = var1.getVar("farmAttackSummonSkills");
      if (var23 != null && !var23.isEmpty()) {
         this.getSummonAttackSpells().clear();
         String[] var27 = var23.split(";");

         for (String var44 : var27) {
            if (var44 != null) {
               this.getSummonAttackSpells().add(Integer.parseInt(var44));
            }
         }
      }

      String var28 = var1.getVar("farmSelfSummonSkills");
      if (var28 != null && !var28.isEmpty()) {
         this.getSummonSelfSpells().clear();
         String[] var33 = var28.split(";");

         for (String var12 : var33) {
            if (var12 != null) {
               this.getSummonSelfSpells().add(Integer.parseInt(var12));
            }
         }
      }

      String var34 = var1.getVar("farmHealSummonSkills");
      if (var34 != null && !var34.isEmpty()) {
         this.getSummonHealSpells().clear();
         String[] var39 = var34.split(";");

         for (String var13 : var39) {
            if (var13 != null) {
               this.getSummonHealSpells().add(Integer.parseInt(var13));
            }
         }
      }
   }

   public void saveSkills(String var1) {
      Player var2 = this.getPlayer();
      if (var2 != null) {
         this.k(var2, var1);
      }
   }

   private void k(Player var1, String var2) {
      List var3 = null;
      switch (var2) {
         case "farmAttackSkills":
            var3 = this.getAttackSpells();
            break;
         case "farmChanceSkills":
            var3 = this.getChanceSpells();
            break;
         case "farmSelfSkills":
            var3 = this.getSelfSpells();
            break;
         case "farmHealSkills":
            var3 = this.getLowLifeSpells();
            break;
         case "farmAttackSummonSkills":
            var3 = this.getSummonAttackSpells();
            break;
         case "farmSelfSummonSkills":
            var3 = this.getSummonSelfSpells();
            break;
         case "farmHealSummonSkills":
            var3 = this.getSummonHealSpells();
      }

      if (var3 != null && !var3.isEmpty()) {
         String var7 = "";

         for (Object __raw_var6_374 : (Iterable<?>)(Object)(var3)) {
            int var6 = ((Number)__raw_var6_374).intValue();
            var7 = var7 + var6 + ";";
         }

         var1.setVar(var2, var7, -1L);
      }
   }

   public int getShortcutsIndex() {
      return this.Mz;
   }

   public int getFarmRadius() {
      return this.MA;
   }

   private List<Integer> k(List<Integer> var1) {
      Player var2 = this.getPlayer();
      return var2 == null
         ? Collections.emptyList()
         : Arrays.stream(var2.getShortCuts())
            .filter(var2x -> var2x.getPage() == this.getShortcutsIndex() && var2x.getType() == 2 && var1.contains(var2x.getSlot()))
            .map(ShortCut::getId)
            .collect(Collectors.toList());
   }

   private void O(Player var1) {
      this.Ms.clear();
      List var2 = this.k(this.Mo);
      if (!var2.isEmpty()) {
         for (Object __raw_var4_404 : (Iterable<?>)(Object)(var2)) {
            int var4 = ((Number)__raw_var4_404).intValue();
            Skill var5 = var1.getKnownSkill(var4);
            if (var5 != null
               && (
                  var5.getSkillType() == Skill.SkillType.DOT
                     || var5.getSkillType() == Skill.SkillType.MDOT
                     || var5.getSkillType() == Skill.SkillType.POISON
                     || var5.getSkillType() == Skill.SkillType.BLEED
                     || var5.getSkillType() == Skill.SkillType.DEBUFF
                     || var5.getSkillType() == Skill.SkillType.SLEEP
                     || var5.getSkillType() == Skill.SkillType.ROOT
                     || var5.getSkillType() == Skill.SkillType.PARALYZE
                     || var5.getSkillType() == Skill.SkillType.MUTE
                     || var5.isSpoilSkill()
                     || var5.isSweepSkill()
                     || var5.getId() == 1263
               )) {
               this.Ms.add(var4);
            }
         }

         this.k(var1, "farmChanceSkills");
         var2.clear();
      }
   }

   public List<Integer> getChanceSpells() {
      return this.Ms;
   }

   private void P(Player var1) {
      this.Mr.clear();
      List var2 = this.k(this.Mn);
      if (!var2.isEmpty()) {
         for (Object __raw_var4_438 : (Iterable<?>)(Object)(var2)) {
            int var4 = ((Number)__raw_var4_438).intValue();
            Skill var5 = var1.getKnownSkill(var4);
            if (var5 != null
               && !var5.isSpoilSkill()
               && !var5.isSweepSkill()
               && var5.getId() != 1263
               && (
                  var5.getSkillType() == Skill.SkillType.AGGRESSION
                     || var5.getSkillType() == Skill.SkillType.PDAM
                     || var5.getSkillType() == Skill.SkillType.MANADAM
                     || var5.getSkillType() == Skill.SkillType.MDAM
                     || var5.getSkillType() == Skill.SkillType.DRAIN
                     || var5.getSkillType() == Skill.SkillType.CPDAM
                     || var5.getSkillType() == Skill.SkillType.STUN
               )) {
               this.Mr.add(var4);
            }
         }

         this.k(var1, "farmAttackSkills");
         var2.clear();
      }
   }

   public List<Integer> getAttackSpells() {
      return this.Mr;
   }

   private void Q(Player var1) {
      this.Mt.clear();
      List var2 = this.k(this.Mp);
      if (!var2.isEmpty()) {
         for (Object __raw_var4_470 : (Iterable<?>)(Object)(var2)) {
            int var4 = ((Number)__raw_var4_470).intValue();
            Skill var5 = var1.getKnownSkill(var4);
            if (var5 != null && (var5.isToggle() || var5.isMusic() || var5.getSkillType() == Skill.SkillType.BUFF || var5.isCubicSkill())) {
               this.Mt.add(var4);
            }
         }

         this.k(var1, "farmSelfSkills");
         var2.clear();
      }
   }

   public List<Integer> getSelfSpells() {
      return this.Mt;
   }

   public void refreshLowLifeSkills(Player var1) {
      this.Mu.clear();
      List var2 = this.k(this.Mq);
      if (!var2.isEmpty()) {
         for (Object __raw_var4_490 : (Iterable<?>)(Object)(var2)) {
            int var4 = ((Number)__raw_var4_490).intValue();
            Skill var5 = var1.getKnownSkill(var4);
            if (var5 != null
               && (
                  var5.getSkillType() == Skill.SkillType.DRAIN
                     || var5.getSkillType() == Skill.SkillType.HEAL
                     || var5.getSkillType() == Skill.SkillType.HEAL_PERCENT
                     || var5.getSkillType() == Skill.SkillType.MANAHEAL
                     || var5.getSkillType() == Skill.SkillType.MANAHEAL_PERCENT
               )) {
               this.Mu.add(var4);
            }
         }

         this.k(var1, "farmHealSkills");
         var2.clear();
      }
   }

   public List<Integer> getLowLifeSpells() {
      return this.Mu;
   }

   public void checkAllSlots() {
      Player var1 = this.getPlayer();
      if (var1 != null) {
         this.O(var1);
         this.P(var1);
         this.Q(var1);
         this.refreshLowLifeSkills(var1);
      }
   }

   public void startFarmTask() {
      Player var1 = this.getPlayer();
      if (var1 != null && !this.isAutofarming() && Config.ALLOW_AUTO_FARM && (!Config.AUTO_FARM_FOR_PREMIUM || var1.hasBonus())) {
         int var2 = AutoFarmManager.getInstance().getActiveFarms(Config.ALLOW_CHECK_HWID_LIMIT ? var1.getNetConnection().getHwid() : var1.getIP());
         if (var2 <= 0 && !AutoFarmManager.getInstance().isNonCheckPlayer(var1.getObjectId())) {
            var1.sendMessage(new CustomMessage("EXCEEDED_LIMIT_ON_USE_OF_SERVICE", var1));
         } else {
            if (!Config.AUTO_FARM_LIMIT_ZONE_NAMES.isEmpty()) {
               for (Zone var4 : var1.getZones()) {
                  if (Config.AUTO_FARM_LIMIT_ZONE_NAMES.contains(var4.getName())) {
                     var1.sendMessage(new CustomMessage("AUTO_HUNTING_PROHIBITED", var1));
                     return;
                  }
               }
            }

            try {
               if (this.Nj != null) {
                  this.Nj.cancel(false);
                  this.Nj = null;
               }
            } catch (Exception var9) {
            }

            AutoFarmManager.getInstance().addActiveFarm(Config.ALLOW_CHECK_HWID_LIMIT ? var1.getNetConnection().getHwid() : var1.getIP(), var1.getObjectId());
            if (this.isKeepLocation()) {
               this.setKeepLocation(var1.getLoc());
            }

            int var10 = Config.FARM_INTERVAL_TASK;
            switch (this.cC()) {
               case 0:
                  if (var10 <= 0) {
                     var10 = var1.getPAtkSpd() > 1000 ? 500 : 1000;
                  }

                  this.Nj = ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoPhysicalFarmTask(this), 1000L, var10);
                  break;
               case 1:
                  if (var10 <= 0) {
                     var10 = var1.getPAtkSpd() > 1000 ? 500 : 1000;
                  }

                  this.Nj = ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoArcherFarmTask(this), 1000L, var10);
                  break;
               case 2:
                  if (var10 <= 0) {
                     var10 = var1.getMAtkSpd() > 1000 ? 500 : 1000;
                  }

                  this.Nj = ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoMagicFarmTask(this), 1000L, var10);
                  break;
               case 3:
                  if (var10 <= 0) {
                     if (var1.getPAtkSpd() > var1.getMAtkSpd()) {
                        var10 = var1.getPAtkSpd() > 1000 ? 500 : 1000;
                     } else {
                        var10 = var1.getMAtkSpd() > 1000 ? 500 : 1000;
                     }
                  }

                  this.Nj = ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoHealFarmTask(this), 1000L, var10);
                  break;
               case 4:
                  if (var1.getPet() == null) {
                     var1.sendMessage(new CustomMessage("YOU_HAVE_NO_SUMMON_AUTOFARMING_DEACTIVATE", var1));
                     AutoFarmManager.getInstance()
                        .removeActiveFarm(Config.ALLOW_CHECK_HWID_LIMIT ? var1.getNetConnection().getHwid() : var1.getIP(), var1.getObjectId());
                     return;
                  }

                  if (var10 <= 0) {
                     if (var1.getPAtkSpd() > var1.getMAtkSpd()) {
                        var10 = var1.getPAtkSpd() > 1000 ? 700 : 1000;
                     } else {
                        var10 = var1.getMAtkSpd() > 1000 ? 700 : 1000;
                     }
                  }

                  this.Nj = ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoSummonFarmTask(this), 1000L, var10);
            }

            boolean var11 = Config.PREMIUM_FARM_FREE && var1.hasBonus();
            if (Config.FARM_ONLINE_TYPE && !var11) {
               long var5 = var1.getVarLong("activeFarmOnlineTask", 0L) - this.getLastFarmOnlineTime();

               try {
                  if (this.Nk != null) {
                     this.Nk.cancel(false);
                     this.Nk = null;
                  }
               } catch (Exception var8) {
               }

               this.Nk = ThreadPoolManager.getInstance().schedule(new AutoFarmEndTask(this), var5);
               this.setFarmOnlineTime();
            }

            if (Config.SERVICES_AUTO_FARM_ABNORMAL != AbnormalEffect.NULL) {
               var1.startAbnormalEffect(Config.SERVICES_AUTO_FARM_ABNORMAL);
            }

            if (Config.SERVICE_AUTO_FARM_SET_RED_RING) {
               var1.setTeam(TeamType.RED);
            }

            var1.sendMessage(new CustomMessage("AUTOFARMING_ACTIVATED", var1));
         }
      } else {
         var1.sendMessage(new CustomMessage("AUTO_FARM_PREMIUM_ONLY", var1));
      }
   }

   public void stopFarmTask(boolean var1) {
      Player var2 = this.getPlayer();
      if (var2 != null && this.isAutofarming() && Config.ALLOW_AUTO_FARM) {
         try {
            if (this.Nj != null) {
               this.Nj.cancel(false);
               this.Nj = null;
            }
         } catch (Exception var7) {
         }

         boolean var3 = Config.PREMIUM_FARM_FREE && var2.hasBonus();
         if (Config.FARM_ONLINE_TYPE && !var3) {
            try {
               if (this.Nk != null) {
                  this.Nk.cancel(false);
                  this.Nk = null;
               }
            } catch (Exception var6) {
            }

            long var4 = this.getLastFarmOnlineTime() + (System.currentTimeMillis() - this.getFarmOnlineTime());
            var2.setVar("activeFarmOnlineTime", var4, -1L);
            this.Ng = var4;
            this.Nf = 0L;
         }

         AutoFarmManager.getInstance().removeActiveFarm(Config.ALLOW_CHECK_HWID_LIMIT ? var2.getNetConnection().getHwid() : var2.getIP(), var2.getObjectId());
         if (Config.SERVICES_AUTO_FARM_ABNORMAL != AbnormalEffect.NULL) {
            var2.stopAbnormalEffect(Config.SERVICES_AUTO_FARM_ABNORMAL);
         }

         if (Config.SERVICE_AUTO_FARM_SET_RED_RING) {
            var2.setTeam(TeamType.NONE);
         }

         var2.sendMessage(new CustomMessage("AUTOFARMING_DEACTIVATED", var2));
         if (var1) {
            this.startFarmTask();
         }
      }
   }

   public boolean isAutofarming() {
      return this.Nj != null;
   }

   public void checkFarmTask() {
      Player var1 = this.getPlayer();
      if (var1 != null) {
         if (Config.FARM_ONLINE_TYPE) {
            long var2 = var1.getVarLong("activeFarmOnlineTask", 0L);
            if (var1.getVarLong("activeFarmOnlineTime", 0L) <= var2 && var2 != 0L) {
               this.Nh = true;
               this.Ng = var1.getVarLong("activeFarmOnlineTime", 0L);
            } else {
               this.Nh = false;
            }
         } else {
            long var4 = var1.getVarLong("activeFarmTask", 0L);
            if (var4 > System.currentTimeMillis()) {
               if (this.Nk == null) {
                  this.Nk = ThreadPoolManager.getInstance().schedule(new AutoFarmEndTask(this), var4 - System.currentTimeMillis());
               }

               this.Ne = var4;
            } else {
               this.Ne = 0L;
            }
         }
      }
   }

   public void setAutoFarmEndTask(long var1) {
      if (var1 == 0L && this.Nk != null) {
         this.Nk.cancel(false);
         this.Nk = null;
      }

      this.Ne = var1;
   }

   public long getAutoFarmEnd() {
      return this.Ne;
   }

   public boolean isActiveAutofarm() {
      Player var1 = this.getPlayer();
      return var1 == null
         ? false
         : this.Nk != null || Config.AUTO_FARM_FREE || Config.PREMIUM_FARM_FREE && var1.hasBonus() || Config.FARM_ONLINE_TYPE && this.isActiveFarmOnlineTime();
   }

   public boolean isActiveFarmTask() {
      return this.Nk != null;
   }

   public boolean isRndAttackSkills() {
      return this.MC;
   }

   public boolean isRndChanceSkills() {
      return this.ME;
   }

   public boolean isRndSelfSkills() {
      return this.MG;
   }

   public boolean isRndLifeSkills() {
      return this.MI;
   }

   public void setRndAttackSkills(boolean var1, boolean var2) {
      this.MC = var1;
      if (!var2) {
         this.e("farmRndAttackSkills", this.MC ? 1 : 0);
      }
   }

   public void setRndChanceSkills(boolean var1, boolean var2) {
      this.ME = var1;
      if (!var2) {
         this.e("farmRndChanceSkills", this.ME ? 1 : 0);
      }
   }

   public void setRndSelfSkills(boolean var1, boolean var2) {
      this.MG = var1;
      if (!var2) {
         this.e("farmRndSelfSkills", this.MG ? 1 : 0);
      }
   }

   public void setRndLifeSkills(boolean var1, boolean var2) {
      this.MI = var1;
      if (!var2) {
         this.e("farmRndLifeSkills", this.MI ? 1 : 0);
      }
   }

   public boolean isLeaderAssist() {
      return this.MW;
   }

   public boolean isKeepLocation() {
      return this.MX;
   }

   public boolean isExtraDelaySkill() {
      return this.MY;
   }

   public boolean isExtraSummonDelaySkill() {
      return this.MZ;
   }

   public boolean isRunTargetCloseUp() {
      return this.Na;
   }

   public boolean isUseSummonSkills() {
      return this.Nd;
   }

   public void setLeaderAssist(boolean var1, boolean var2) {
      Player var3 = this.getPlayer();
      if (var3 != null) {
         if (var3.getParty() != null && var3.getParty().isLeader(var3)) {
            this.MW = false;
         } else {
            this.MW = var1;
         }

         if (!var2) {
            this.e("farmLeaderAssist", this.MW ? 1 : 0);
         }
      }
   }

   public void setKeepLocation(Location var1, boolean var2, boolean var3) {
      this.MX = var2;
      if (!var3) {
         this.e("farmKeepLocation", this.MX ? 1 : 0);
         if (this.MX) {
            this.setKeepLocation(var1);
         }
      }
   }

   public void setExDelaySkill(boolean var1, boolean var2) {
      this.MY = var1;
      if (!var2) {
         this.e("farmExDelaySkill", this.MY ? 1 : 0);
      }
   }

   public void setExSummonDelaySkill(boolean var1, boolean var2) {
      this.MZ = var1;
      if (!var2) {
         this.e("farmExSummonDelaySkill", this.MZ ? 1 : 0);
      }
   }

   public void setRunTargetCloseUp(boolean var1, boolean var2) {
      this.Na = var1;
      if (!var2) {
         this.e("farmRunTargetCloseUp", this.Na ? 1 : 0);
      }
   }

   public void setUseSummonSkills(boolean var1, boolean var2) {
      this.Nd = var1;
      if (!var2) {
         this.e("farmUseSummonSkills", this.Nd ? 1 : 0);
      }
   }

   public boolean isAssistMonsterAttack() {
      return this.Nb;
   }

   public boolean isTargetRestoreMp() {
      return this.Nc;
   }

   public void setAssistMonsterAttack(boolean var1, boolean var2) {
      this.Nb = var1;
      if (!var2) {
         this.e("farmAssistMonsterAttack", this.Nb ? 1 : 0);
      }
   }

   public void setTargetRestoreMp(boolean var1, boolean var2) {
      this.Nc = var1;
      if (!var2) {
         this.e("farmTargetRestoreMp", this.Nc ? 1 : 0);
      }
   }

   public List<Integer> getSummonAttackSpells() {
      return this.Mv;
   }

   public List<Integer> getSummonSelfSpells() {
      return this.Mw;
   }

   public List<Integer> getSummonHealSpells() {
      return this.Mx;
   }

   public int getSummonAttackPercent() {
      return this.MP;
   }

   public int getSummonAttackChance() {
      return this.MJ;
   }

   public int getSummonSelfPercent() {
      return this.MQ;
   }

   public int getSummonSelfChance() {
      return this.ML;
   }

   public int getSummonLifePercent() {
      return this.MR;
   }

   public int getSummonLifeChance() {
      return this.MN;
   }

   public void setSummonAttackSkillValue(boolean var1, int var2) {
      if (var1) {
         this.MP = var2;
      } else {
         this.MJ = var2;
      }
   }

   public void setSummonSelfSkillValue(boolean var1, int var2) {
      if (var1) {
         this.MQ = var2;
      } else {
         this.ML = var2;
      }
   }

   public void setSummonLifeSkillValue(boolean var1, int var2) {
      if (var1) {
         this.MR = var2;
      } else {
         this.MN = var2;
      }
   }

   public boolean isRndSummonAttackSkills() {
      return this.MK;
   }

   public boolean isRndSummonSelfSkills() {
      return this.MM;
   }

   public boolean isRndSummonLifeSkills() {
      return this.MO;
   }

   public void setRndSummonAttackSkills(boolean var1, boolean var2) {
      this.MK = var1;
      if (!var2) {
         this.e("farmRndSummonAttackSkills", this.MK ? 1 : 0);
      }
   }

   public void setRndSummonSelfSkills(boolean var1, boolean var2) {
      this.MM = var1;
      if (!var2) {
         this.e("farmRndSummonSelfSkills", this.MM ? 1 : 0);
      }
   }

   public void setRndSummonLifeSkills(boolean var1, boolean var2) {
      this.MO = var1;
      if (!var2) {
         this.e("farmRndSummonLifeSkills", this.MO ? 1 : 0);
      }
   }

   private void e(String var1, int var2) {
      Player var3 = this.getPlayer();
      if (var3 != null) {
         var3.setVar(var1, var2, -1L);
      }
   }

   public final List<NpcInstance> getAroundNpc(Player var1, Function<NpcInstance, Boolean> var2) {
      ArrayList var3 = new ArrayList();

      for (NpcInstance var5 : World.getAroundNpc(var1, this.getFarmRadius(), 600)) {
         if (var5.isMonster()
            && !var5.isDead()
            && var5.isVisible()
            && !(var5 instanceof ChestInstance)
            && !var5.isRaid()
            && (Boolean)var2.apply(var5)
            && !ArrayUtils.contains(Config.AUTO_FARM_IGNORED_NPC_ID, var5.getNpcId())
            && var5.hasAI()) {
            if (var5.getAI().getTargetList().isEmpty() || var5.getAI().getTargetList().contains(var1)) {
               var3.add(var5);
            } else if (var1.getParty() != null) {
               for (Player var7 : var1.getParty()) {
                  if (var7 != null && var5.getAI().getTargetList().contains(var7)) {
                     var3.add(var5);
                  }
               }
            }
         }
      }

      return var3;
   }

   public Skill nextAttackSkill(NpcInstance var1, long var2) {
      Player var4 = this.getPlayer();
      if (var4 == null || this.getAttackSpells().isEmpty() || !Rnd.chance(this.getAttackChance())) {
         return null;
      }

      if (this.isExtraDelaySkill() && var2 > System.currentTimeMillis()) {
         return null;
      }

      double var5 = var4.getCurrentMpPercents();
      if (var5 < this.getAttackPercent()) {
         return null;
      }

      if (this.isRndAttackSkills()) {
         return this.f(var1);
      }

      for (int var8 : this.getAttackSpells()) {
         Skill var9 = var4.getKnownSkill(var8);
         if (var9 != null
            && var9.checkCondition(var4, var1, false, false, false, true)
            && (!var9.isOffensive() || var9.getTargetType() != Skill.SkillTargetType.TARGET_ONE || var1 != null)) {
            assert var1 != null;
            var4.setTarget(var1);
            var4.sendPacket(new MyTargetSelected(var1.getObjectId(), var4.getLevel() - var1.getLevel()));
            var4.sendPacket(var1.makeStatusUpdate(9, 10));
            return var9;
         }
      }

      return null;
   }

   private Skill f(NpcInstance var1) {
      Player var2 = this.getPlayer();
      if (var2 == null) {
         return null;
      }

      ArrayList var3 = new ArrayList();
      Skill var4 = null;

      for (int var6 : this.getAttackSpells()) {
         Skill var7 = var2.getKnownSkill(var6);
         if (var7 != null
            && var7.checkCondition(var2, var1, false, false, false, true)
            && (!var7.isOffensive() || var7.getTargetType() != Skill.SkillTargetType.TARGET_ONE || var1 != null)) {
            var3.add(var7);
         }
      }

      if (!var3.isEmpty()) {
         var4 = (Skill)var3.get(Rnd.get(var3.size()));
         assert var1 != null;
         var2.setTarget(var1);
         var2.sendPacket(new MyTargetSelected(var1.getObjectId(), var2.getLevel() - var1.getLevel()));
         var2.sendPacket(var1.makeStatusUpdate(9, 10));
      }

      var3.clear();
      return var4;
   }

   public Skill nextChanceSkill(NpcInstance var1, long var2) {
      Player var4 = this.getPlayer();
      if (var4 == null || this.getChanceSpells().isEmpty() || !Rnd.chance(this.getChanceChance())) {
         return null;
      }

      if (this.isExtraDelaySkill() && var2 > System.currentTimeMillis()) {
         return null;
      }

      double var5 = var4.getCurrentMpPercents();
      if (var1 != null && !(var5 < this.getChancePercent())) {
         if (this.isRndChanceSkills()) {
            return this.g(var1);
         }

         for (int var8 : this.getChanceSpells()) {
            Skill var9 = var4.getKnownSkill(var8);
            if (var9 != null
               && var9.checkCondition(var4, var1, false, false, false, true)
               && (!var9.isSpoilSkill() || !((MonsterInstance)var1).isSpoiled())
               && (!var9.isSweepSkill() || var1.isDead())
               && var1.getEffectList().getEffectsBySkillId(var8) == null) {
               return var9;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Skill g(NpcInstance var1) {
      Player var2 = this.getPlayer();
      if (var2 == null) {
         return null;
      }

      ArrayList var3 = new ArrayList();
      Skill var4 = null;

      for (int var6 : this.getChanceSpells()) {
         Skill var7 = var2.getKnownSkill(var6);
         if (var7 != null
            && var7.checkCondition(var2, var1, false, false, false, true)
            && (!var7.isSpoilSkill() || !((MonsterInstance)var1).isSpoiled())
            && (!var7.isSweepSkill() || var1.isDead())
            && var1.getEffectList().getEffectsBySkillId(var6) == null) {
            var3.add(var7);
         }
      }

      if (!var3.isEmpty()) {
         var4 = (Skill)var3.get(Rnd.get(var3.size()));
      }

      var3.clear();
      return var4;
   }

   public Skill nextSelfSkill(Creature var1) {
      Player var2 = this.getPlayer();
      if (var2 != null && !this.getSelfSpells().isEmpty() && Rnd.chance(this.getSelfChance())) {
         double var3 = var2.getCurrentMpPercents();
         if (var3 < this.getSelfPercent()) {
            return null;
         }

         if (this.isRndSelfSkills()) {
            return this.j(var1);
         }

         for (int var6 : this.getSelfSpells()) {
            Skill var7 = var2.getKnownSkill(var6);
            if (var7 != null && var7.checkCondition(var2, var1 != null ? var1 : var2, false, false, false, true)) {
               if (var7.isToggle() && var2.getEffectList().getEffectsBySkillId(var6) == null) {
                  return var7;
               }

               if (var1 != null && var1.getEffectList().getEffectsBySkillId(var6) == null && var7.getTargetType() != Skill.SkillTargetType.TARGET_SELF) {
                  var2.setTarget(var1);
                  return var7;
               }

               if (var2.getEffectList().getEffectsBySkillId(var6) == null && var7.getTargetType() != Skill.SkillTargetType.TARGET_PET) {
                  var2.setTarget(var2);
                  return var7;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Skill j(Creature var1) {
      Player var2 = this.getPlayer();
      if (var2 == null) {
         return null;
      }

      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      Skill var5 = null;

      for (int var7 : this.getSelfSpells()) {
         Skill var8 = var2.getKnownSkill(var7);
         if (var8 != null && var8.checkCondition(var2, var1 != null ? var1 : var2, false, false, false, true)) {
            if (var8.isToggle() && var2.getEffectList().getEffectsBySkillId(var7) == null) {
               var3.add(var8);
            } else {
               if (var2.getEffectList().getEffectsBySkillId(var7) == null && var8.getTargetType() != Skill.SkillTargetType.TARGET_PET) {
                  var3.add(var8);
               }

               if (var1 != null && var1.getEffectList().getEffectsBySkillId(var7) == null && var8.getTargetType() != Skill.SkillTargetType.TARGET_SELF) {
                  var4.add(var8);
               }
            }
         }
      }

      boolean var9 = true;
      if (!var4.isEmpty()) {
         var5 = (Skill)var4.get(Rnd.get(var4.size()));
         var9 = false;
      } else if (!var3.isEmpty()) {
         var5 = (Skill)var3.get(Rnd.get(var3.size()));
      }

      var3.clear();
      var4.clear();
      if (var5 == null) {
         return null;
      }

      if (var1 != null && !var9) {
         var2.setTarget(var1);
      } else {
         var2.setTarget(var2);
      }

      return var5;
   }

   public Skill nextHealSkill(NpcInstance var1, Creature var2) {
      Player var3 = this.getPlayer();
      if (var3 != null && !this.getLowLifeSpells().isEmpty() && Rnd.chance(this.getLifeChance())) {
         double var4 = var3.getCurrentHpPercents();
         double var6 = var2 != null ? var2.getCurrentHpPercents() : 100.0;
         double var8 = var2 != null ? var2.getCurrentMpPercents() : 100.0;
         boolean var10 = var6 < this.getLifePercent();
         boolean var11 = var4 < this.getLifePercent();
         boolean var12 = var8 < this.getLifePercent();
         if (!var10 && !var11 && !var12) {
            return null;
         }

         if (this.isRndLifeSkills()) {
            return this.b(var1, var2);
         }

         for (int var14 : this.getLowLifeSpells()) {
            Skill var15 = var3.getKnownSkill(var14);
            if (var15 != null
               && var15.checkCondition(var3, var15.isOffensive() ? var1 : var3, false, false, false, true)
               && (!var15.isOffensive() || var1 != null)) {
               if (!d(var15)) {
                  if (c(var15)
                     && this.isTargetRestoreMp()
                     && var2 != null
                     && var12
                     && !var2.isDead()
                     && var15.getTargetType() != Skill.SkillTargetType.TARGET_SELF) {
                     var3.setTarget(var2);
                     return var15;
                  }

                  return var15;
               }

               if (var10 || var11) {
                  if (var10 && var2 != null && !var2.isDead() && var15.getTargetType() != Skill.SkillTargetType.TARGET_SELF) {
                     if (var15.getTargetType() != Skill.SkillTargetType.TARGET_PET || var2.isSummon()) {
                        var3.setTarget(var2);
                        return var15;
                     }
                  } else {
                     if (!var11) {
                        return null;
                     }

                     if (var15.getTargetType() != Skill.SkillTargetType.TARGET_PET) {
                        var3.setTarget(var3);
                        return var15;
                     }
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Skill b(NpcInstance var1, Creature var2) {
      Player var3 = this.getPlayer();
      if (var3 == null) {
         return null;
      }

      ArrayList var4 = new ArrayList();
      Skill var5 = null;
      double var6 = var3.getCurrentHpPercents();
      double var8 = var2 != null ? var2.getCurrentHpPercents() : 100.0;
      double var10 = var2 != null ? var2.getCurrentMpPercents() : 100.0;
      boolean var12 = var8 < this.getLifePercent();
      boolean var13 = var6 < this.getLifePercent();
      boolean var14 = var10 < this.getLifePercent();
      if (!var12 && !var13 && !var14) {
         return null;
      }

      for (int var16 : this.getLowLifeSpells()) {
         Skill var17 = var3.getKnownSkill(var16);
         if (var17 != null
            && var17.checkCondition(var3, var17.isOffensive() ? var1 : var3, false, false, false, true)
            && (!var17.isOffensive() || var1 != null)) {
            if (!var12 && !var13) {
               if (var14
                  && c(var17)
                  && this.isTargetRestoreMp()
                  && var2 != null
                  && !var2.isDead()
                  && var17.getTargetType() != Skill.SkillTargetType.TARGET_SELF) {
                  var4.add(var17);
               }
            } else if (d(var17)) {
               if (var12) {
                  if (var2 != null
                     && !var2.isDead()
                     && var17.getTargetType() != Skill.SkillTargetType.TARGET_SELF
                     && (var17.getTargetType() != Skill.SkillTargetType.TARGET_PET || var2.isSummon())) {
                     var4.add(var17);
                  }
               } else if (var13 && var17.getTargetType() != Skill.SkillTargetType.TARGET_PET) {
                  var4.add(var17);
               }
            }
         }
      }

      if (!var4.isEmpty()) {
         var5 = (Skill)var4.get(Rnd.get(var4.size()));
      }

      var4.clear();
      if (var5 == null) {
         return null;
      }

      if (!var12 && !var14) {
         var3.setTarget(var3);
      } else {
         var3.setTarget(var2);
      }

      return var5;
   }

   public Location getKeepLocation() {
      return this.Ni;
   }

   public void setKeepLocation(Location var1) {
      this.Ni = var1;
   }

   public Skill nextSummonAttackSkill(NpcInstance var1, Summon var2, long var3) {
      if (this.getSummonAttackSpells().isEmpty() || !Rnd.chance(this.getSummonAttackChance())) {
         return null;
      }

      if (this.isExtraSummonDelaySkill() && var3 > System.currentTimeMillis()) {
         return null;
      }

      double var5 = var2.getCurrentMpPercents();
      if (var5 < this.getSummonAttackPercent()) {
         return null;
      }

      if (this.isRndSummonAttackSkills()) {
         return this.a(var1, var2);
      }

      for (int var8 : this.getSummonAttackSpells()) {
         int var9 = PetSkillsTable.getInstance().getAvailableLevel(var2, var8);
         if (var9 > 0) {
            Skill var10 = SkillTable.getInstance().getInfo(var8, var9);
            if (var10.checkCondition(var2, var1, false, false, false, true)
               && (!var10.isOffensive() || var10.getTargetType() != Skill.SkillTargetType.TARGET_ONE || var1 != null)) {
               return var10;
            }
         }
      }

      return null;
   }

   private Skill a(NpcInstance var1, Summon var2) {
      ArrayList var3 = new ArrayList();
      Skill var4 = null;

      for (int var6 : this.getSummonAttackSpells()) {
         int var7 = PetSkillsTable.getInstance().getAvailableLevel(var2, var6);
         if (var7 > 0) {
            Skill var8 = SkillTable.getInstance().getInfo(var6, var7);
            if (var8.checkCondition(var2, var1, false, false, false, true)
               && (!var8.isOffensive() || var8.getTargetType() != Skill.SkillTargetType.TARGET_ONE || var1 != null)) {
               var3.add(var8);
            }
         }
      }

      if (!var3.isEmpty()) {
         var4 = (Skill)var3.get(Rnd.get(var3.size()));
      }

      var3.clear();
      return var4;
   }

   public Skill nextSummonSelfSkill(Summon var1, Creature var2) {
      if (!this.getSummonSelfSpells().isEmpty() && Rnd.chance(this.getSummonSelfChance())) {
         double var3 = var1.getCurrentMpPercents();
         if (var3 < this.getSummonSelfPercent()) {
            return null;
         }

         if (this.isRndSummonSelfSkills()) {
            return this.a(var1, var2);
         }

         for (int var6 : this.getSummonSelfSpells()) {
            int var7 = PetSkillsTable.getInstance().getAvailableLevel(var1, var6);
            if (var7 > 0) {
               Skill var8 = SkillTable.getInstance().getInfo(var6, var7);
               if (var8.checkCondition(var1, var2 != null ? var2 : var1, false, false, false, true)) {
                  if (var8.isToggle() && var1.getEffectList().getEffectsBySkillId(var6) == null) {
                     return var8;
                  }

                  if (var2 != null
                     && var2.getEffectList().getEffectsBySkillId(var6) == null
                     && var8.getTargetType() != Skill.SkillTargetType.TARGET_SELF
                     && var8.getTargetType() != Skill.SkillTargetType.TARGET_PET) {
                     var1.setTarget(var2);
                     return var8;
                  }

                  if (var1.getEffectList().getEffectsBySkillId(var6) == null) {
                     return var8;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Skill a(Summon var1, Creature var2) {
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      Skill var5 = null;

      for (int var7 : this.getSelfSpells()) {
         int var8 = PetSkillsTable.getInstance().getAvailableLevel(var1, var7);
         if (var8 > 0) {
            Skill var9 = SkillTable.getInstance().getInfo(var7, var8);
            if (var9.checkCondition(var1, var2 != null ? var2 : var1, false, false, false, true)) {
               if (var9.isToggle() && var1.getEffectList().getEffectsBySkillId(var7) == null) {
                  var3.add(var9);
               } else {
                  if (var2 != null
                     && var2.getEffectList().getEffectsBySkillId(var7) == null
                     && var9.getTargetType() != Skill.SkillTargetType.TARGET_SELF
                     && var9.getTargetType() != Skill.SkillTargetType.TARGET_PET) {
                     var4.add(var9);
                  }

                  if (var1.getEffectList().getEffectsBySkillId(var7) == null) {
                     var3.add(var9);
                  }
               }
            }
         }
      }

      boolean var10 = true;
      if (!var4.isEmpty()) {
         var5 = (Skill)var4.get(Rnd.get(var4.size()));
         var10 = false;
      } else if (!var3.isEmpty()) {
         var5 = (Skill)var3.get(Rnd.get(var3.size()));
      }

      var3.clear();
      var4.clear();
      if (var5 == null) {
         return null;
      }

      if (var2 != null && !var10) {
         var1.setTarget(var2);
      } else {
         var1.setTarget(var1);
      }

      return var5;
   }

   public Skill nextSummonHealSkill(NpcInstance var1, Summon var2, Creature var3) {
      if (!this.getSummonHealSpells().isEmpty() && Rnd.chance(this.getSummonLifeChance())) {
         double var4 = var2.getCurrentHpPercents();
         double var6 = var3 != null ? var3.getCurrentHpPercents() : 100.0;
         double var8 = var3 != null ? var3.getCurrentMpPercents() : 100.0;
         boolean var10 = var6 < this.getSummonLifePercent();
         boolean var11 = var4 < this.getSummonLifePercent();
         boolean var12 = var8 < this.getSummonLifePercent();
         if (!var10 && !var11 && !var12) {
            return null;
         }

         if (this.isRndLifeSkills()) {
            return this.a(var1, var2, var3);
         }

         for (int var14 : this.getSummonHealSpells()) {
            int var15 = PetSkillsTable.getInstance().getAvailableLevel(var2, var14);
            if (var15 > 0) {
               Skill var16 = SkillTable.getInstance().getInfo(var14, var15);
               if (var16.checkCondition(var2, var3 != null && var10 ? var3 : var2, false, false, false, true) && (!var16.isOffensive() || var1 != null)) {
                  if (!d(var16)) {
                     if (c(var16) && var3 != null && var12 && !var3.isDead() && var16.getTargetType() != Skill.SkillTargetType.TARGET_SELF) {
                        var2.setTarget(var3);
                        return var16;
                     }

                     return var16;
                  }

                  if (var10 || var11) {
                     if (var10 && var3 != null && !var3.isDead() && var16.getTargetType() != Skill.SkillTargetType.TARGET_SELF) {
                        if (var16.getTargetType() == Skill.SkillTargetType.TARGET_PET && !var3.isSummon()) {
                           continue;
                        }

                        var2.setTarget(var3);
                        return var16;
                     }

                     if (var11) {
                        var2.setTarget(var2);
                        return var16;
                     }

                     return null;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Skill a(NpcInstance var1, Summon var2, Creature var3) {
      ArrayList var4 = new ArrayList();
      Skill var5 = null;
      double var6 = var2.getCurrentHpPercents();
      double var8 = var3 != null ? var3.getCurrentHpPercents() : 100.0;
      double var10 = var3 != null ? var3.getCurrentMpPercents() : 100.0;
      boolean var12 = var8 < this.getSummonLifePercent();
      boolean var13 = var6 < this.getSummonLifePercent();
      boolean var14 = var10 < this.getSummonLifePercent();
      if (!var12 && !var13 && !var14) {
         return null;
      }

      for (int var16 : this.getSummonHealSpells()) {
         int var17 = PetSkillsTable.getInstance().getAvailableLevel(var2, var16);
         if (var17 > 0) {
            Skill var18 = SkillTable.getInstance().getInfo(var16, var17);
            if (var18.checkCondition(var2, var3 != null && var12 ? var3 : var2, false, false, false, true) && (!var18.isOffensive() || var1 != null)) {
               if (!var12 && !var13) {
                  if (var14 && c(var18) && var3 != null && !var3.isDead() && var18.getTargetType() != Skill.SkillTargetType.TARGET_SELF) {
                     var4.add(var18);
                  }
               } else if (d(var18)) {
                  if (var12) {
                     if (var3 != null
                        && !var3.isDead()
                        && var18.getTargetType() != Skill.SkillTargetType.TARGET_SELF
                        && (var18.getTargetType() != Skill.SkillTargetType.TARGET_PET || var3.isSummon())) {
                        var4.add(var18);
                     }
                  } else if (var13) {
                     var4.add(var18);
                  }
               }
            }
         }
      }

      if (!var4.isEmpty()) {
         var5 = (Skill)var4.get(Rnd.get(var4.size()));
      }

      var4.clear();
      if (var5 == null) {
         return null;
      }

      if (!var12 && !var14) {
         var2.setTarget(var2);
      } else {
         var2.setTarget(var3);
      }

      return var5;
   }

   public NpcInstance getLeaderTarget(Player var1) {
      GameObject var2 = var1.getTarget();
      return var2 != null
            && var2 != var1
            && var2 instanceof NpcInstance
            && ((NpcInstance)var2).hasAI()
            && ((NpcInstance)var2).getAI().getTargetList().contains(var1)
         ? (NpcInstance)var2
         : null;
   }

   public long getLastFarmOnlineTime() {
      return this.Ng;
   }

   public boolean isActiveFarmOnlineTime() {
      return this.Nh;
   }

   public void setFarmOnlineTime() {
      this.Nf = System.currentTimeMillis();
   }

   public void refreshFarmOnlineTime() {
      this.Nf = 0L;
   }

   public long getFarmOnlineTime() {
      return this.Nf;
   }

   public enum SpellType {
      ATTACK,
      CHANCE,
      SELF,
      LOWLIFE;
   }
}
