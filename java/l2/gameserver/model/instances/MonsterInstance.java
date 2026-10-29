package l2.gameserver.model.instances;

import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.AggroList;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Manor;
import l2.gameserver.model.MinionList;
import l2.gameserver.model.Party;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.PlayerGroup;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.base.SpecialEffectState;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.model.reward.RewardItem;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.Faction;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class MonsterInstance extends NpcInstance {
   private static final int TN = 1000;
   private ScheduledFuture<?> TO;
   private MinionList TP;
   private boolean TQ;
   private int TR;
   private boolean TS;
   private RewardItem TT;
   private final Lock TU = new ReentrantLock();
   private int TV;
   private double TW;
   private TIntHashSet TX;
   private final Lock TY = new ReentrantLock();
   private boolean TZ;
   private int Ua;
   private List<RewardItem> Ub;
   private final Lock Uc = new ReentrantLock();
   private int Ud;
   private final double Ue = 200.0;
   private final double Uf = 50.0;
   private final double Ug = 30.0;

   public MonsterInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
      this.setUndying(SpecialEffectState.FALSE);
      this.TP = new MinionList(this);
   }

   @Override
   public boolean isMovementDisabled() {
      return this.getNpcId() == 18344 || this.getNpcId() == 18345 || super.isMovementDisabled();
   }

   @Override
   public boolean isLethalImmune() {
      return this.getMaxHp() >= 50000
         || this.Ud > 0
         || this.getNpcId() == 22215
         || this.getNpcId() == 22216
         || this.getNpcId() == 22217
         || super.isLethalImmune();
   }

   @Override
   public boolean isFearImmune() {
      return this.Ud > 0 || super.isFearImmune();
   }

   @Override
   public boolean isParalyzeImmune() {
      return this.Ud > 0 || super.isParalyzeImmune();
   }

   @Override
   public boolean isAutoAttackable(Creature var1) {
      return !var1.isMonster();
   }

   public int getChampion() {
      return this.Ud;
   }

   public void setChampion() {
      if (this.getReflection().canChampions() && this.canChampion()) {
         double var1 = Rnd.nextDouble();
         if (Config.ALT_CHAMPION_CHANCE2 / 100.0 >= var1) {
            this.setChampion(2);
         } else if ((Config.ALT_CHAMPION_CHANCE1 + Config.ALT_CHAMPION_CHANCE2) / 100.0 >= var1) {
            this.setChampion(1);
         } else {
            this.setChampion(0);
         }
      } else {
         this.setChampion(0);
      }
   }

   public void setChampion(int var1) {
      if (var1 == 0) {
         this.removeSkillById(4407);
         this.Ud = 0;
      } else {
         this.addSkill(SkillTable.getInstance().getInfo(4407, var1));
         this.Ud = var1;
      }
   }

   public boolean canChampion() {
      return (Config.ALT_CHAMPION_CAN_BE_SEVEN_SIGN_MONSTERS || !this.isSevenSignsMonster())
         && !this.isMinion()
         && !Config.ALT_IGNORE_CHAMPIONS_LIST.contains(this.getNpcId())
         && (Config.ALT_CHAMPIONS_LIST.isEmpty() || Config.ALT_CHAMPIONS_LIST.contains(this.getNpcId()))
         && this.getTemplate().rewardExp > 0L
         && this.getTemplate().level >= Config.ALT_CHAMPION_MIN_LEVEL
         && this.getTemplate().level <= Config.ALT_CHAMPION_TOP_LEVEL;
   }

   @Override
   public TeamType getTeam() {
      return this.getChampion() == 2 ? TeamType.RED : (this.getChampion() == 1 ? TeamType.BLUE : TeamType.NONE);
   }

   @Override
   protected void onSpawn() {
      super.onSpawn();
      this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp(), true);
      if (this.getMinionList().hasMinions()) {
         if (this.TO != null) {
            this.TO.cancel(false);
            this.TO = null;
         }

         this.TO = ThreadPoolManager.getInstance().schedule(new MonsterInstance.MinionMaintainTask(), 1000L);
      }
   }

   @Override
   protected void onDespawn() {
      this.setOverhitDamage(0.0);
      this.setOverhitAttacker(null);
      this.clearSweep();
      this.clearHarvest();
      this.clearAbsorbers();
      super.onDespawn();
   }

   @Override
   public MinionList getMinionList() {
      return this.TP;
   }

   public Location getMinionPosition() {
      return Location.findPointToStay(this, 100, 150);
   }

   public void notifyMinionDied(MinionInstance var1) {
   }

   public void spawnMinion(MonsterInstance var1) {
      var1.setReflection(this.getReflection());
      if (this.getChampion() == 2) {
         var1.setChampion(1);
      } else {
         var1.setChampion(0);
      }

      var1.setHeading(this.getHeading());
      var1.setCurrentHpMp(var1.getMaxHp(), var1.getMaxMp(), true);
      var1.spawnMe(this.getMinionPosition());
   }

   @Override
   public boolean hasMinions() {
      return this.getMinionList().hasMinions();
   }

   @Override
   public void setReflection(Reflection var1) {
      super.setReflection(var1);
      if (this.hasMinions()) {
         for (MinionInstance var3 : this.getMinionList().getAliveMinions()) {
            var3.setReflection(var1);
         }
      }
   }

   @Override
   protected void onDelete() {
      if (this.TO != null) {
         this.TO.cancel(false);
         this.TO = null;
      }

      this.getMinionList().deleteMinions();
      super.onDelete();
   }

   @Override
   protected void onDeath(Creature var1) {
      if (this.TO != null) {
         this.TO.cancel(false);
         this.TO = null;
      }

      this.calculateRewards(var1);
      super.onDeath(var1);
   }

   @Override
   protected void onReduceCurrentHp(double var1, Creature var3, Skill var4, boolean var5, boolean var6, boolean var7) {
      if (var4 != null && var4.isOverhit()) {
         double var8 = (this.getCurrentHp() - var1) * -1.0;
         if (var8 <= 0.0) {
            this.setOverhitDamage(0.0);
            this.setOverhitAttacker(null);
         } else {
            this.setOverhitDamage(var8);
            this.setOverhitAttacker(var3);
         }
      }

      super.onReduceCurrentHp(var1, var3, var4, var5, var6, var7);
   }

   private int a(Iterable<Player> var1, Map<Playable, AggroList.HateInfo> var2) {
      int var3 = 0;

      for (Player var5 : var1) {
         AggroList.HateInfo var6 = (AggroList.HateInfo)var2.get(var5);
         if (var6 != null) {
            var3 += var6.damage;
            Summon var7;
            if ((var7 = var5.getPet()) != null) {
               AggroList.HateInfo var8 = (AggroList.HateInfo)var2.get(var7);
               if (var8 != null) {
                  var3 += var8.damage;
               }
            }
         }
      }

      return var3;
   }

   private Creature b(Iterable<? extends PlayerGroup> var1, Map<Playable, AggroList.HateInfo> var2) {
      if (var2 != null && !var2.isEmpty()) {
         int var3 = Integer.MIN_VALUE;
         PlayerGroup var4 = null;

         for (PlayerGroup var6 : var1) {
            if (var6 != var4) {
               int var7 = this.a(var6, var2);
               if (var7 > var3) {
                  var3 = var7;
                  var4 = var6;
               }
            }
         }

         if (var4 == null) {
            return null;
         } else if (var4 instanceof Player) {
            return (Player)var4;
         } else if (var4 instanceof Party) {
            Party var9 = (Party)var4;
            return this.b(var9.getPartyMembers(), var2);
         } else if (var4 instanceof CommandChannel) {
            CommandChannel var8 = (CommandChannel)var4;
            return this.b(var8.getParties(), var2);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private Creature b(Map<Playable, AggroList.HateInfo> var1) {
      HashSet var2 = new HashSet();

      for (Playable var4 : var1.keySet()) {
         if (var4 instanceof Player) {
            var2.add(((Player)var4).getPlayerGroup());
         }
      }

      return this.b(var2, var1);
   }

   protected Creature getTopDamager() {
      return this.b(this.getAggroList().getPlayableMap());
   }

   public void calculateRewards(Creature var1) {
      Creature var2 = this.getTopDamager();
      if (var1 == null || !var1.isPlayable()) {
         var1 = var2;
      }

      if (var1 != null && var1.isPlayable()) {
         Player var3 = var1.getPlayer();
         if (var3 != null) {
            if (Config.ALT_DROP_LASTHIT) {
               var2 = var3;
            }

            Map var4 = this.getAggroList().getPlayableMap();
            Quest[] var5 = this.getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
            if (var5 != null && var5.length > 0) {
               ArrayList var6 = null;
               if (this.isRaid() && Config.ALT_NO_LASTHIT) {
                  var6 = new ArrayList();

                  for (Object __raw_var27_344 : (Iterable<?>)(Object)(var4.keySet())) {
                     Playable var27 = (Playable)__raw_var27_344;
                     if (!var27.isDead()
                        && (this.isInRangeZ(var27, Config.ALT_PARTY_DISTRIBUTION_RANGE) || var3.isInRangeZ(var27, Config.ALT_PARTY_DISTRIBUTION_RANGE))
                        && !var6.contains(var27.getPlayer())) {
                        var6.add(var27.getPlayer());
                     }
                  }
               } else if (var3.getParty() != null) {
                  var6 = new ArrayList(var3.getParty().getMemberCount());

                  for (Player var8 : var3.getParty().getPartyMembers()) {
                     if (!var8.isDead()
                        && (this.isInRangeZ(var8, Config.ALT_PARTY_DISTRIBUTION_RANGE) || var3.isInRangeZ(var8, Config.ALT_PARTY_DISTRIBUTION_RANGE))) {
                        var6.add(var8);
                     }
                  }
               }

               for (Quest var10 : var5) {
                  Player var11 = var3;
                  if (var10.getParty() != 0 && var6 != null) {
                     if (!this.isRaid() && var10.getParty() != 2) {
                        ArrayList var40 = new ArrayList(var6.size());

                        for (Object __raw_var45_368 : (Iterable<?>)(Object)(var6)) {
                           Player var45 = (Player)__raw_var45_368;
                           QuestState var15 = var45.getQuestState(var10.getName());
                           if (var15 != null && !var15.isCompleted()) {
                              var40.add(var45);
                           }
                        }

                        if (var40.isEmpty()) {
                           continue;
                        }

                        var11 = (Player)var40.get(Rnd.get(var40.size()));
                        if (var11 == null) {
                           var11 = var3;
                        }
                     } else {
                        for (Object __raw_var13_384 : (Iterable<?>)(Object)(var6)) {
                           Player var13 = (Player)__raw_var13_384;
                           QuestState var14 = var13.getQuestState(var10.getName());
                           if (var14 != null && !var14.isCompleted()) {
                              var10.notifyKill(this, var14);
                           }
                        }

                        var11 = null;
                     }
                  }

                  if (var11 != null) {
                     QuestState var41 = var11.getQuestState(var10.getName());
                     if (var41 != null && !var41.isCompleted()) {
                        var10.notifyKill(this, var41);
                     }
                  }
               }
            }

            HashMap var22 = new HashMap();

            for (Object __raw_var29_406 : (Iterable<?>)(Object)(var4.values())) {
               AggroList.HateInfo var29 = (AggroList.HateInfo)__raw_var29_406;
               if (var29.damage > 1) {
                  Playable var31 = (Playable)var29.attacker;
                  Player var35 = var31.getPlayer();
                  MonsterInstance.RewardInfo var38 = (MonsterInstance.RewardInfo)var22.get(var35);
                  if (var38 == null) {
                     var22.put(var35, new MonsterInstance.RewardInfo(var35, var29.damage));
                  } else {
                     var38.addDamage(var29.damage);
                  }
               }
            }

            Player[] var26 = (Player[])var22.keySet().toArray(new Player[var22.size()]);
            double[] var30 = new double[2];

            for (Player var42 : var26) {
               if (!var42.isDead()) {
                  MonsterInstance.RewardInfo var44 = (MonsterInstance.RewardInfo)var22.get(var42);
                  if (var44 != null) {
                     Party var46 = var42.getParty();
                     int var47 = this.getMaxHp();
                     var30[0] = 0.0;
                     var30[1] = 0.0;
                     if (var46 == null) {
                        int var49 = Math.min(var44._dmg, var47);
                        if (var49 > 0) {
                           if (this.isInRangeZ(var42, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                              var30 = this.b(var42.getLevel(), var49);
                           }

                           var30[0] = this.a(var3, var30[0]);
                           if (!var42.isCursedWeaponEquipped() && var30[0] > 0.0 && var42.getKarma() > 0) {
                              int var50 = Formulas.calculateKarmaLost(var42, (long)var30[0]);
                              var42.decreaseKarma(Math.max(var50, 0));
                           }

                           var42.addExpAndCheckBonus(this, (long)var30[0], (long)var30[1]);
                        }

                        var22.remove(var42);
                     } else {
                        int var16 = 0;
                        int var17 = 1;
                        ArrayList var18 = new ArrayList();

                        for (Player var20 : var46.getPartyMembers()) {
                           MonsterInstance.RewardInfo var21 = (MonsterInstance.RewardInfo)var22.remove(var20);
                           if (!var20.isDead() && this.isInRangeZ(var20, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                              if (var21 != null) {
                                 var16 += var21._dmg;
                              }

                              var18.add(var20);
                              if (var20.getLevel() > var17) {
                                 var17 = var20.getLevel();
                              }
                           }
                        }

                        var16 = Math.min(var16, var47);
                        if (var16 > 0) {
                           var30 = this.b(var17, var16);
                           double var51 = (double)var16 / var47;
                           var30[0] *= var51;
                           var30[1] *= var51;
                           var30[0] = this.a(var3, var30[0]);
                           var46.distributeXpAndSp(var30[0], var30[1], var18, var1, this);
                        }
                     }
                  }
               }
            }

            CursedWeaponsManager.getInstance().dropAttackable(this, var3);
            if (var2 != null && var2.isPlayable()) {
               for (Entry var37 : this.getTemplate().getRewards().entrySet()) {
                  this.rollRewards(var37, var1, var2);
               }

               if (this.getChampion() > 0
                  && Config.ALT_CHAMPION_DROP_ITEM_IDS != null
                  && Config.ALT_CHAMPION_DROP_ITEM_IDS.get(this.getChampion()).length > 0
                  && Math.abs(this.getLevel() - var2.getLevel()) < Config.ALT_CHAMPION_DROP_LEVEL_DIFF) {
                  for (int var34 = 0; var34 < ((int[])Config.ALT_CHAMPION_DROP_ITEM_IDS.get(this.getChampion())).length; var34++) {
                     if (Config.ALT_CHAMPION_DROP_ITEM_IDS.get(this.getChampion())[var34] > 0
                        && Rnd.chance(Config.ALT_CHAMPION_DROP_CHANCES.get(this.getChampion())[var34])) {
                        this.dropItem(
                           var2.getPlayer(),
                           Config.ALT_CHAMPION_DROP_ITEM_IDS.get(this.getChampion())[var34],
                           Config.ALT_CHAMPION_DROP_COUNTS.get(this.getChampion())[var34]
                        );
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onRandomAnimation() {
      if (System.currentTimeMillis() - this._lastSocialAction > 10000L) {
         this.broadcastPacket(new SocialAction(this.getObjectId(), 1));
         this._lastSocialAction = System.currentTimeMillis();
      }
   }

   @Override
   public void startRandomAnimation() {
   }

   @Override
   public int getKarma() {
      return 0;
   }

   public void addAbsorber(Player var1) {
      if (var1 != null) {
         if (!(this.getCurrentHpPercents() > 50.0)) {
            this.TY.lock();

            try {
               if (this.TX == null) {
                  this.TX = new TIntHashSet();
               }

               this.TX.add(var1.getObjectId());
            } finally {
               this.TY.unlock();
            }
         }
      }
   }

   public boolean isAbsorbed(Player var1) {
      this.TY.lock();

      try {
         if (this.TX == null) {
            return false;
         }

         if (!this.TX.contains(var1.getObjectId())) {
            return false;
         }
      } finally {
         this.TY.unlock();
      }

      return true;
   }

   public void clearAbsorbers() {
      this.TY.lock();

      try {
         if (this.TX != null) {
            this.TX.clear();
         }
      } finally {
         this.TY.unlock();
      }
   }

   public RewardItem takeHarvest() {
      this.TU.lock();

      try {
         RewardItem var1 = this.TT;
         this.clearHarvest();
         return var1;
      } finally {
         this.TU.unlock();
      }
   }

   public void clearHarvest() {
      this.TU.lock();

      try {
         this.TT = null;
         this.TS = false;
         this.TR = 0;
         this.TQ = false;
      } finally {
         this.TU.unlock();
      }
   }

   public boolean setSeeded(Player var1, int var2, boolean var3) {
      this.TU.lock();

      try {
         if (this.isSeeded()) {
            return false;
         }

         this.TQ = true;
         this.TS = var3;
         this.TR = var1.getObjectId();
         this.TT = new RewardItem(Manor.getInstance().getCropType(var2));
         if (this.getTemplate().rateHp > 1.0) {
            this.TT.count = Rnd.get(Math.round(this.getTemplate().rateHp), Math.round(1.5 * this.getTemplate().rateHp));
         }
      } finally {
         this.TU.unlock();
      }

      return true;
   }

   public boolean isSeeded(Player var1) {
      return this.isSeeded() && this.TR == var1.getObjectId() && this.getDeadTime() < 20000L;
   }

   public boolean isSeeded() {
      return this.TQ;
   }

   public boolean isSpoiled() {
      return this.TZ;
   }

   public boolean isSpoiled(Player var1) {
      if (!this.isSpoiled()) {
         return false;
      }

      if (var1.getObjectId() == this.Ua && this.getDeadTime() < 20000L) {
         return true;
      }

      if (var1.isInParty()) {
         for (Player var3 : var1.getParty().getPartyMembers()) {
            if (var3.getObjectId() == this.Ua && this.getDistance(var3) < Config.ALT_PARTY_DISTRIBUTION_RANGE) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean setSpoiled(Player var1) {
      this.Uc.lock();

      try {
         if (this.isSpoiled()) {
            return false;
         }

         this.TZ = true;
         this.Ua = var1.getObjectId();
      } finally {
         this.Uc.unlock();
      }

      return true;
   }

   public boolean isSweepActive() {
      this.Uc.lock();

      try {
         return this.Ub != null && this.Ub.size() > 0;
      } finally {
         this.Uc.unlock();
      }
   }

   public List<RewardItem> takeSweep() {
      this.Uc.lock();

      try {
         List var1 = this.Ub;
         this.clearSweep();
         return var1;
      } finally {
         this.Uc.unlock();
      }
   }

   public void clearSweep() {
      this.Uc.lock();

      try {
         this.TZ = false;
         this.Ua = 0;
         this.Ub = null;
      } finally {
         this.Uc.unlock();
      }
   }

   public void rollRewards(Entry<RewardType, RewardList> var1, Creature var2, Creature var3) {
      RewardType var4 = (RewardType)var1.getKey();
      RewardList var5 = (RewardList)var1.getValue();
      if (var4 != RewardType.SWEEP || this.isSpoiled()) {
         Creature var6 = var4 == RewardType.SWEEP ? var2 : var3;
         Player var7 = var6.getPlayer();
         if (var7 != null) {
            int var8 = this.calculateLevelDiffForDrop(var3.getLevel());
            double var9 = this.calcStat(Stats.ITEM_REWARD_MULTIPLIER, 1.0, var6, null);
            var9 *= Experience.penaltyModifier(var8, 9.0);
            List var11 = var5.roll(var7, var9, this instanceof RaidBossInstance);
            switch (var4) {
               case SWEEP:
                  this.Ub = var11;
                  break;
               default:
                  for (Object __raw_var13_717 : (Iterable<?>)(Object)(var11)) {
                     RewardItem var13 = (RewardItem)__raw_var13_717;
                     if (!this.isSeeded() || this.TS || var13.isAdena || var13.isSealStone) {
                        this.dropItem(var7, var13.itemId, var13.count);
                     }
                  }
            }
         }
      }
   }

   private double[] b(int var1, long var2) {
      int var4 = var1 - this.getLevel();
      double var5 = this.getExpReward() * var2 / this.getMaxHp();
      double var7 = this.getSpReward() * var2 / this.getMaxHp();
      if (Config.EXP_SP_DIFF_LIMIT != 0 && Math.abs(var4) > Config.EXP_SP_DIFF_LIMIT) {
         var5 = 0.0;
         var7 = 0.0;
      }

      if (var4 > Config.THRESHOLD_LEVEL_DIFF) {
         double var9 = Math.pow(0.83, var4 - 5);
         var5 *= var9;
         var7 *= var9;
      }

      var5 = Math.max(0.0, var5);
      var7 = Math.max(0.0, var7);
      return new double[]{var5, var7};
   }

   private double a(Player var1, double var2) {
      if (var2 > 0.0 && var1.getObjectId() == this.TV) {
         int var4 = this.calculateOverhitExp(var2 * Config.RATE_OVERHIT);
         var1.sendPacket(Msg.OVER_HIT, new SystemMessage(362).addNumber(var4));
         var2 += var4;
      }

      return var2;
   }

   @Override
   public void setOverhitAttacker(Creature var1) {
      this.TV = var1 == null ? 0 : var1.getObjectId();
   }

   public double getOverhitDamage() {
      return this.TW;
   }

   @Override
   public void setOverhitDamage(double var1) {
      this.TW = var1;
   }

   public int calculateOverhitExp(double var1) {
      double var3 = this.getOverhitDamage() * 100.0 / this.getMaxHp();
      if (var3 > 25.0) {
         var3 = 25.0;
      }

      double var5 = var3 / 100.0 * var1;
      this.setOverhitAttacker(null);
      this.setOverhitDamage(0.0);
      return (int)Math.round(var5);
   }

   @Override
   public boolean isAggressive() {
      return (Config.ALT_CHAMPION_CAN_BE_AGGRO || this.getChampion() == 0) && super.isAggressive();
   }

   @Override
   public Faction getFaction() {
      return !Config.ALT_CHAMPION_CAN_BE_SOCIAL && this.getChampion() != 0 ? Faction.NONE : super.getFaction();
   }

   @Override
   public void reduceCurrentHp(
      double var1, Creature var3, Skill var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9, boolean var10, boolean var11
   ) {
      this.a(var3, var1);
      super.reduceCurrentHp(var1, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   private void a(Creature var1, double var2) {
      if (!(this.getTemplate().baseAtkRange > 200.0)
         && this.getLevel() >= 20
         && this.getLevel() <= 78
         && var1.getLevel() - this.getLevel() <= 9
         && this.getLevel() - var1.getLevel() <= 9) {
         if (!this.isMinion()
            && this.getMinionList() == null
            && !this.isRaid()
            && !(this instanceof ReflectionBossInstance)
            && !(this instanceof ChestInstance)
            && this.getChampion() <= 0) {
            short var4 = 5044;
            byte var5 = 1;
            if (this.getLevel() >= 41 || this.getLevel() <= 60) {
               var5 = 2;
            } else if (this.getLevel() > 60) {
               var5 = 3;
            }

            double var6 = this.getDistance(var1);
            if (var6 <= 50.0) {
               if (this.getEffectList() != null && this.getEffectList().getEffectsBySkillId(var4) != null) {
                  for (Effect var9 : this.getEffectList().getEffectsBySkillId(var4)) {
                     var9.exit();
                  }
               }
            } else if (var6 >= 200.0) {
               double var11 = 30.0 / (this.getMaxHp() / var2);
               if (Rnd.chance(var11)) {
                  Skill var10 = SkillTable.getInstance().getInfo(var4, var5);
                  if (var10 != null) {
                     var10.getEffects(this, this, false, false);
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean isMonster() {
      return true;
   }

   @Override
   public Clan getClan() {
      return null;
   }

   @Override
   public boolean isInvul() {
      return this._isInvul;
   }

   public class MinionMaintainTask extends RunnableImpl {
      @Override
      public void runImpl() throws Exception {
         if (!MonsterInstance.this.isDead()) {
            MonsterInstance.this.getMinionList().spawnMinions();
         }
      }
   }

   protected static final class RewardInfo {
      protected Creature _attacker;
      protected int _dmg = 0;

      public RewardInfo(Creature var1, int var2) {
         this._attacker = var1;
         this._dmg = var2;
      }

      public void addDamage(int var1) {
         if (var1 < 0) {
            var1 = 0;
         }

         this._dmg += var1;
      }

      @Override
      public int hashCode() {
         return this._attacker.getObjectId();
      }
   }
}
