package l2.gameserver.model.instances;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.model.AggroList;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.PlayerGroup;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class RaidBossInstance extends MonsterInstance {
   private static final long serialVersionUID = 1L;
   private ScheduledFuture<?> TO;
   private static final int VM = 5000;

   public RaidBossInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
   }

   @Override
   public boolean isRaid() {
      return true;
   }

   protected int getMinionUnspawnInterval() {
      return 5000;
   }

   protected int getKilledInterval(MinionInstance var1) {
      return Config.MINIONS_RESPAWN_INTERVAL;
   }

   @Override
   public void notifyMinionDied(MinionInstance var1) {
      this.TO = ThreadPoolManager.getInstance().schedule(new RaidBossInstance.MaintainKilledMinion(var1), this.getKilledInterval(var1));
      super.notifyMinionDied(var1);
   }

   @Override
   protected void onDeath(Creature var1) {
      if (this.TO != null) {
         this.TO.cancel(false);
         this.TO = null;
      }

      int var2 = this.getTemplate().rewardRp;
      if (var2 > 0) {
         this.O(var2);
      }

      if (this instanceof ReflectionBossInstance) {
         super.onDeath(var1);
      } else {
         if (var1.isPlayable()) {
            Player var3 = var1.getPlayer();
            if (!var3.isInParty()) {
               if (var3.isNoble()) {
                  HeroController.getInstance().addHeroDiary(var3.getObjectId(), 1, this.getNpcId());
               }

               var3.sendPacket(Msg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL);
            } else {
               for (Player var5 : var3.getParty().getPartyMembers()) {
                  if (var5.isNoble()) {
                     HeroController.getInstance().addHeroDiary(var5.getObjectId(), 1, this.getNpcId());
                  }
               }

               var3.getParty().broadCast(Msg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL);
            }

            if (Config.ALT_RAID_DEATH_ANNOUNCE_IDS.contains(this.getNpcId())) {
               Clan var8;
               if ((var8 = var3.getClan()) == null) {
                  Announcements.getInstance()
                     .announceByCustomMessage(
                        "l2.gameserver.instancemanager.RaidBossSpawnManager.RaidBossKillAnnounce", new String[]{var3.getName(), this.getName()}
                     );
               } else {
                  Announcements.getInstance()
                     .announceByCustomMessage(
                        "l2.gameserver.instancemanager.RaidBossSpawnManager.RaidBossKillWithClanAnnounce",
                        new String[]{var3.getName(), this.getName(), var8.getName()}
                     );
               }
            }

            Clan var9;
            if ((var9 = var3.getClan()) != null && Config.EVENT_CLAN_RAID_BOSS_POINTS.containsKey(this.getNpcId())) {
               var9.setCustomPoints(var9.getCustomPoints() + Config.EVENT_CLAN_RAID_BOSS_POINTS.get(this.getNpcId()));
               if (Config.SERVICES_TOP_CLANS_STATISTIC_ANNOUNCE) {
                  Announcements.getInstance()
                     .announceByCustomMessage(
                        "scripts.services.CustomClanPointsRaidBoss",
                        new String[]{
                           String.valueOf(var9.getName()),
                           String.valueOf(this.getName()),
                           String.valueOf(Config.EVENT_CLAN_RAID_BOSS_POINTS.get(this.getNpcId()))
                        }
                     );
               }
            }

            Quest var10 = QuestManager.getQuest(508);
            if (var10 != null) {
               String var6 = var10.getName();
               if (var3.getClan() != null && var3.getClan().getLeader().isOnline() && var3.getClan().getLeader().getPlayer().getQuestState(var6) != null) {
                  QuestState var7 = var3.getClan().getLeader().getPlayer().getQuestState(var6);
                  var7.getQuest().onKill(this, var7);
               }
            }
         }

         if (this.getMinionList().hasAliveMinions()) {
            ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
               @Override
               public void runImpl() throws Exception {
                  if (RaidBossInstance.this.isDead()) {
                     RaidBossInstance.this.getMinionList().unspawnMinions();
                  }
               }
            }, this.getMinionUnspawnInterval());
         }

         super.onDeath(var1);
      }
   }

   private void O(int var1) {
      HashMap var2 = new HashMap();
      double var3 = this.getMaxHp();

      for (AggroList.HateInfo var6 : this.getAggroList().getPlayableMap().values()) {
         Player var7 = var6.attacker.getPlayer();
         PlayerGroup var8 = var7.getParty() != null
            ? (var7.getParty().getCommandChannel() != null ? var7.getParty().getCommandChannel() : var7.getParty())
            : var7.getPlayer();
         Object[] var9 = (Object[])var2.get(var8);
         if (var9 == null) {
            var9 = new Object[]{new HashSet(), Long.valueOf(0L)};
            var2.put(var8, var9);
         }

         if (var8 instanceof CommandChannel) {
            for (Player var11 : (CommandChannel)var8) {
               if (var11.isInRangeZ(this, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                  ((Set)var9[0]).add(var11);
               }
            }
         } else if (var8 instanceof Party) {
            for (Player var19 : ((Party)var8).getPartyMembers()) {
               if (var19.isInRangeZ(this, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                  ((Set)var9[0]).add(var19);
               }
            }
         } else {
            ((Set)var9[0]).add(var7);
         }

         var9[1] = (Long)var9[1] + var6.damage;
      }

      for (Object __raw_var13_181 : (Iterable<?>)(Object)(var2.values())) {
         Object[] var13 = (Object[])__raw_var13_181;
         HashSet var14 = (HashSet)var13[0];
         int var15 = (int)Math.round(var1 * (Long)var13[1] / (var3 * var14.size()));

         for (Object __raw_var18_185 : (Iterable<?>)(Object)(var14)) {
            Player var18 = (Player)__raw_var18_185;
            int var20 = var15;
            var20 = (int)Math.round(var20 * Experience.penaltyModifier(this.calculateLevelDiffForDrop(var18.getLevel()), 9.0));
            if (var20 != 0) {
               var18.sendPacket(new SystemMessage(1725).addNumber(var20));
               RaidBossSpawnManager.getInstance().addPoints(var18.getObjectId(), this.getNpcId(), var20);
            }
         }
      }

      RaidBossSpawnManager.getInstance().updatePointsDb();
      RaidBossSpawnManager.getInstance().calculateRanking();
   }

   @Override
   protected void onDecay() {
      super.onDecay();
      RaidBossSpawnManager.getInstance().onBossDespawned(this);
   }

   @Override
   protected void onSpawn() {
      super.onSpawn();
      this.addSkill(SkillTable.getInstance().getInfo(4045, 1));
      RaidBossSpawnManager.getInstance().onBossSpawned(this);
   }

   @Override
   public boolean isFearImmune() {
      return true;
   }

   @Override
   public boolean isParalyzeImmune() {
      return true;
   }

   @Override
   public boolean isLethalImmune() {
      return true;
   }

   @Override
   public boolean hasRandomWalk() {
      return false;
   }

   @Override
   public boolean canChampion() {
      return false;
   }

   private class MaintainKilledMinion extends RunnableImpl {
      private final MinionInstance VN;

      public MaintainKilledMinion(MinionInstance var2) {
         this.VN = var2;
      }

      @Override
      public void runImpl() throws Exception {
         if (!RaidBossInstance.this.isDead()) {
            this.VN.refreshID();
            RaidBossInstance.this.spawnMinion(this.VN);
         }
      }
   }
}
