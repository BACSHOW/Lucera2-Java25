package l2.gameserver.model.entity.oly;

import gnu.trove.TIntIntHashMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.ExOlympiadMode;
import l2.gameserver.network.l2.s2c.ExOlympiadSpelledInfo;
import l2.gameserver.network.l2.s2c.ExOlympiadUserInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;

public class Competition {
   private final Stadium Ro;
   public Participant[] _participants;
   private final CompetitionType Rp;
   private long Rq;
   private static final double Rr = 0.8;
   private volatile CompetitionState Rs;
   private ScheduledFuture<?> Rt;

   public Competition(CompetitionType var1, Stadium var2) {
      this.Rp = var1;
      this.Ro = var2;
      this.Rs = null;
      this.Rq = 0L;
   }

   public CompetitionState getState() {
      return this.Rs;
   }

   public void setState(CompetitionState var1) {
      if (this.Rs == CompetitionState.STAND_BY && var1 == CompetitionState.PLAYING) {
         this.Rq = System.currentTimeMillis();
      }

      this.Rs = var1;
   }

   public CompetitionType getType() {
      return this.Rp;
   }

   public Stadium getStadium() {
      return this.Ro;
   }

   public void setPlayers(Participant[] var1) {
      this._participants = var1;

      for (Participant var5 : this._participants) {
         for (Player var9 : var5.getPlayers()) {
            var9.setOlyParticipant(var5);
         }
      }
   }

   public void start() {
      for (Participant var4 : this._participants) {
         var4.OnStart();
      }
   }

   private void cM() {
      for (Participant var4 : this._participants) {
         for (Player var8 : var4.getPlayers()) {
            try {
               if (var8.getClan() != null) {
                  var8.getClan().enableSkills(var8);
               }

               for (int var9 = 0; var9 < Config.OLY_RESTRICTED_SKILL_IDS.length; var9++) {
                  int var10 = Config.OLY_RESTRICTED_SKILL_IDS[var9];
                  if (var8.isUnActiveSkill(var10)) {
                     Skill var11 = var8.getKnownSkill(var10);
                     if (var11 != null) {
                        var8.removeUnActiveSkill(var11);
                     }
                  }
               }

               Collection var14 = var8.getSkillReuses();
               if (Config.OLYMPIAD_REMOVE_SKILL_REUSE_FROM_STADIUM) {
                  for (Object __raw_var16_108 : (Iterable<?>)(Object)(var14)) {
                     TimeStamp var16 = (TimeStamp)__raw_var16_108;
                     Skill var12 = SkillTable.getInstance().getInfo(var16.getId(), var16.getLevel());
                     var8.enableSkill(var12);
                  }
               }

               if (var8.isHero() && (HeroController.getInstance().isCurrentHero(var8) || Config.ALT_ALLOW_CUSTOM_HERO_SKILLS)) {
                  HeroController.addSkills(var8);
               }

               var8.sendPacket(new ExOlympiadMode(0), new SkillList(var8), new SkillCoolTime(var8));
               var8.updateStats();
               var8.updateEffectIcons();
            } catch (Exception var13) {
               var13.printStackTrace();
            }
         }
      }
   }

   private void cN() {
      for (Participant var4 : this._participants) {
         for (Player var8 : var4.getPlayers()) {
            try {
               boolean var9 = false;

               for (Effect var11 : var8.getEffectList().getAllEffects()) {
                  if (var11 != null
                     && var11.getSkill() != null
                     && !var11.getSkill().isToggle()
                     && (var11.getEffectType() != EffectType.Cubic || var8.getSkillLevel(var11.getSkill().getId()) <= 0)) {
                     if (Config.OLY_REMOVE_FORCE_BUFFS) {
                        var8.setIncreasedForce(0);
                     }

                     var8.sendPacket(new SystemMessage(749).addSkillName(var11.getSkill().getId(), var11.getSkill().getDisplayLevel()));
                     var11.exit();
                     var9 = true;
                  }
               }

               Collection var24 = var8.getSkillReuses();
               if (Config.OLYMPIAD_REMOVE_SKILL_REUSE_ON_STADIUM) {
                  for (Object __raw_var12_151 : (Iterable<?>)(Object)(var24)) {
                     TimeStamp var12 = (TimeStamp)__raw_var12_151;
                     Skill var13 = SkillTable.getInstance().getInfo(var12.getId(), var12.getLevel());
                     var8.enableSkill(var13);
                     var9 = true;
                  }
               }

               if (Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR >= 0) {
                  var8.sendMessage(
                     new CustomMessage(
                        "l2p.gameserver.model.entity.OlympiadGame.Competition.EnchantArmorLevelLimited", var8, Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR
                     )
                  );
               }

               if (Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS >= 0) {
                  var8.sendMessage(
                     new CustomMessage(
                        "l2p.gameserver.model.entity.OlympiadGame.Competition.EnchantWeaponPhysLevelLimited",
                        var8,
                        Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS
                     )
                  );
               }

               if (Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE >= 0) {
                  var8.sendMessage(
                     new CustomMessage(
                        "l2p.gameserver.model.entity.OlympiadGame.Competition.EnchantWeaponMageLevelLimited",
                        var8,
                        Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE
                     )
                  );
               }

               if (Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY >= 0) {
                  var8.sendMessage(
                     new CustomMessage(
                        "l2p.gameserver.model.entity.OlympiadGame.Competition.EnchantAccessoryLevelLimited",
                        var8,
                        Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY
                     )
                  );
               }

               if (var8.getClan() != null) {
                  var8.getClan().disableSkills(var8);
               }

               LinkedList var26 = new LinkedList();

               for (int var27 = 0; var27 < Config.OLY_RESTRICTED_SKILL_IDS.length; var27++) {
                  int var31 = Config.OLY_RESTRICTED_SKILL_IDS[var27];
                  Skill var14 = var8.getKnownSkill(var31);
                  if (var14 != null) {
                     if (var14.isToggle()) {
                        List var15 = var8.getEffectList().getEffectsBySkill(var14);
                        if (var15 != null && !var15.isEmpty()) {
                           var26.addAll(var15);
                        }
                     }

                     var8.addUnActiveSkill(var14);
                     var9 = true;
                  }
               }

               for (Object __raw_var32_218 : (Iterable<?>)(Object)(var26)) {
                  Effect var32 = (Effect)__raw_var32_218;
                  var8.sendPacket(new SystemMessage(749).addSkillName(var32.getSkill().getId(), var32.getSkill().getDisplayLevel()));
                  var32.exit();
               }

               if (var8.isHero()) {
                  HeroController.removeSkills(var8);
               }

               if (var9) {
                  var8.sendPacket(new SkillCoolTime(var8));
                  var8.updateStats();
                  var8.updateEffectIcons();
               }

               if (var8.isCastingNow()) {
                  var8.abortCast(true, false);
               }

               if (var8.isMounted()) {
                  var8.setMount(0, 0, 0);
               }

               if (var8.getPet() != null) {
                  Summon var29 = var8.getPet();
                  if (var29.isPet()) {
                     var29.unSummon();
                  } else {
                     var29.getEffectList().stopAllEffects();
                  }
               }

               if (var8.getAgathionId() > 0) {
                  var8.setAgathion(0);
               }

               var8.sendPacket(new SkillList(var8));
               ItemInstance var30 = var8.getInventory().getPaperdollItem(7);
               if (var30 != null && var30.isHeroWeapon()) {
                  var8.getInventory().unEquipItem(var30);
                  var8.abortAttack(true, true);
                  var8.refreshExpertisePenalty();
               }

               for (int var36 : var8.getAutoSoulShot()) {
                  var8.removeAutoSoulShot(var36);
                  var8.sendPacket(new ExAutoSoulShot(var36, false));
               }

               ItemInstance var35 = var8.getActiveWeaponInstance();
               if (var35 != null) {
                  var35.setChargedSpiritshot(0);
                  var35.setChargedSoulshot(0);
               }

               this.restoreHPCPMP();
               var8.broadcastUserInfo(true);
               if (this.getType() == CompetitionType.TEAM_CLASS_FREE) {
                  boolean var37 = false;
                  if (var8.getParty() != null) {
                     if (var8.getParty().getPartyMembers().size() != var4.getPlayers().length) {
                        var37 = true;
                     } else {
                        for (Player var17 : var8.getParty().getPartyMembers()) {
                           boolean var18 = false;

                           for (Player var22 : var4.getPlayers()) {
                              if (var17 == var22) {
                                 var18 = true;
                              }
                           }

                           if (!var18) {
                              var37 = true;
                           }
                        }
                     }
                  } else {
                     var37 = true;
                  }

                  if (var37) {
                     Player[] var38 = var4.getPlayers();
                     if (var38[0].getParty() != null) {
                        var38[0].getParty().removePartyMember(var38[0], false);
                     }

                     Party var39 = new Party(var38[0], 0);
                     var38[0].setParty(var39);
                     if (var38[1].isInParty()) {
                        var38[1].getParty().removePartyMember(var38[1], false);
                     }

                     var39.addPartyMember(var38[1]);
                     if (var38[2].isInParty()) {
                        var38[2].getParty().removePartyMember(var38[2], false);
                     }

                     var39.addPartyMember(var38[2]);
                  }
               } else if (var8.getParty() != null) {
                  var8.getParty().removePartyMember(var8, false);
               }
            } catch (Exception var23) {
               var23.printStackTrace();
            }
         }
      }
   }

   public void applyBuffs() {
      for (Participant var4 : this._participants) {
         for (Player var8 : var4.getPlayers()) {
            try {
               var8.getEffectList().stopEffects(EffectType.BuffImmunity);
               TIntIntHashMap var9 = (TIntIntHashMap)Config.OLY_BUFFS.get(var8.getActiveClassId());
               if (var9 != null && !var9.isEmpty()) {
                  for (int var13 : var9.keys()) {
                     Skill var14 = SkillTable.getInstance().getInfo(var13, var9.get(var13));
                     var14.getEffects(var8, var8, false, false);
                  }
               }
            } catch (Exception var15) {
               var15.printStackTrace();
            }
         }
      }
   }

   public void restoreHPCPMP() {
      for (Participant var4 : this._participants) {
         for (Player var8 : var4.getPlayers()) {
            try {
               var8.setCurrentHpMp(var8.getMaxHp(), var8.getMaxMp());
               var8.setCurrentCp(var8.getMaxCp());
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }
      }
   }

   public void finish() {
      for (Participant var4 : this._participants) {
         try {
            for (Player var8 : var4.getPlayers()) {
               if (this.getState() != CompetitionState.INIT) {
                  if (var8.getMaxCp() * 0.8 > var8.getCurrentCp()) {
                     var8.setCurrentCp(var8.getMaxCp() * 0.8);
                  }

                  if (var8.getMaxHp() * 0.8 > var8.getCurrentHp()) {
                     var8.setCurrentHp(var8.getMaxHp() * 0.8, false);
                  }

                  if (var8.getMaxMp() * 0.8 > var8.getCurrentMp()) {
                     var8.setCurrentMp(var8.getMaxMp() * 0.8);
                  }
               }
            }
         } finally {
            var4.OnFinish();
         }
      }
   }

   private static int G(int var0) {
      return Math.max(1, (Math.min(50, var0) - 1) / 5 + 1);
   }

   private int cO() {
      int var1 = Integer.MAX_VALUE;

      for (Participant var5 : this._participants) {
         for (Player var9 : var5.getPlayers()) {
            if (var9 != null) {
               int var10 = NoblesController.getInstance().getPointsOf(var9.getObjectId());
               if (var10 < var1) {
                  var1 = var10;
               }
            }
         }
      }

      return var1;
   }

   private void a(Participant var1, Participant var2, boolean var3) {
      this.a(var1, var2, var3, false);
   }

   private void a(Participant var1, Participant var2, boolean var3, boolean var4) {
      if (!var4) {
         this.broadcastPacket(var3 ? Msg.THE_GAME_ENDED_IN_A_TIE : new SystemMessage(1497).addString(var1.getName()), true, true);
      }

      long var5 = 0L;
      if (this.Rq > 0L) {
         var5 = Math.min(Config.OLYMPIAD_COMPETITION_TIME, System.currentTimeMillis() - this.Rq) / 1000L;
      }

      if (var3) {
         if (NoblesController.getInstance() != null) {
            for (Participant var30 : new Participant[]{var1, var2}) {
               for (Player var40 : var30.getPlayers()) {
                  if (var40 != null) {
                     NoblesController.NobleRecord var42 = NoblesController.getInstance().getNobleRecord(var40.getObjectId());
                     var42.comp_done++;
                     switch (this.getType()) {
                        case CLASS_FREE:
                           var42.class_free_cnt++;
                           break;
                        case CLASS_INDIVIDUAL:
                        case CLASS_TYPE_INDIVIDUAL:
                           var42.class_based_cnt++;
                           break;
                        case TEAM_CLASS_FREE:
                           var42.team_cnt++;
                     }

                     NoblesController.getInstance().SaveNobleRecord(var42);

                     for (Effect var47 : var40.getEffectList().getAllEffects()) {
                        if (var47 != null && var47.isCancelable()) {
                           var47.exit();
                        }
                     }

                     var40.sendChanges();
                     var40.updateEffectIcons();
                     Participant var45 = var30 == var2 ? var1 : var2;
                     NoblesController.NobleRecord var48 = var45.getPlayers().length > 0 && var45.getPlayers()[0] != null
                        ? NoblesController.getInstance().getNobleRecord(var45.getPlayers()[0].getObjectId())
                        : null;
                     CompetitionController.getInstance()
                        .addCompetitionResult(OlyController.getInstance().getCurrentSeason(), var42, 0, var48, 0, this.getType(), var3, var4, var5);
                     var40.getListeners().onOlyCompetitionCompleted(this, false);
                  }
               }
            }
         }
      } else {
         Player[] var7 = var2.getPlayers();
         Player[] var8 = var1.getPlayers();
         int var9 = 0;
         int var10 = 0;
         int var11 = 0;

         for (int var12 = 0; var12 < var7.length; var12++) {
            try {
               Player var13 = var7[var12];
               if (var13 != null) {
                  var10 += NoblesController.getInstance().getPointsOf(var13.getObjectId());
               }
            } catch (Exception var24) {
               var24.printStackTrace();
            }
         }

         for (int var32 = 0; var32 < var8.length; var32++) {
            try {
               Player var35 = var8[var32];
               if (var35 != null) {
                  var11 += NoblesController.getInstance().getPointsOf(var35.getObjectId());
               }
            } catch (Exception var23) {
               var23.printStackTrace();
            }
         }

         int var33 = Math.max(0, Math.min(var11, var10));

         for (int var36 = 0; var36 < var7.length; var36++) {
            try {
               Player var14 = var7[var36];
               if (var14 != null && NoblesController.getInstance() != null) {
                  NoblesController.NobleRecord var15 = NoblesController.getInstance().getNobleRecord(var14.getObjectId());
                  int var16 = var15.points_current;
                  int var17 = Math.max(1, (int)(var33 * Config.OLY_LOOSE_POINTS_MUL));
                  int var18 = Math.max(0, var16 - var17);
                  var9 += var17;
                  var15.points_current = var18;
                  var15.comp_loose++;
                  var15.comp_done++;
                  switch (this.getType()) {
                     case CLASS_FREE:
                        var15.class_free_cnt++;
                        break;
                     case CLASS_INDIVIDUAL:
                     case CLASS_TYPE_INDIVIDUAL:
                        var15.class_based_cnt++;
                        break;
                     case TEAM_CLASS_FREE:
                        var15.team_cnt++;
                  }

                  NoblesController.getInstance().SaveNobleRecord(var15);
                  var14.sendPacket(new SystemMessage(1658).addName(var14).addNumber(var17));

                  for (QuestState var22 : var14.getAllQuestsStates()) {
                     if (var22.isStarted()) {
                        var22.getQuest().notifyOlympiadResult(var22, this.getType(), false);
                     }
                  }

                  for (Effect var56 : var14.getEffectList().getAllEffects()) {
                     if (var56 != null && var56.isCancelable()) {
                        var56.exit();
                     }
                  }

                  var14.sendChanges();
                  var14.updateEffectIcons();
                  CompetitionController.getInstance()
                     .addCompetitionResult(
                        OlyController.getInstance().getCurrentSeason(),
                        NoblesController.getInstance().getNobleRecord(var8[var36].getObjectId()),
                        var17,
                        NoblesController.getInstance().getNobleRecord(var14.getObjectId()),
                        var17,
                        this.getType(),
                        var3,
                        var4,
                        var5
                     );
                  var14.getListeners().onOlyCompetitionCompleted(this, false);
               }
            } catch (Exception var26) {
               var26.printStackTrace();
            }
         }

         if (!var4) {
            int var37 = var9 / var7.length;

            for (int var39 = 0; var39 < var8.length; var39++) {
               try {
                  Player var41 = var8[var39];
                  NoblesController.NobleRecord var43 = NoblesController.getInstance().getNobleRecord(var41.getObjectId());
                  int var46 = Math.max(0, var43.points_current + var37);
                  NoblesController.getInstance().setPointsOf(var41.getObjectId(), var46);
                  var43.points_current = var46;
                  var43.comp_win++;
                  var43.comp_done++;
                  switch (this.getType()) {
                     case CLASS_FREE:
                        var43.class_free_cnt++;
                        break;
                     case CLASS_INDIVIDUAL:
                     case CLASS_TYPE_INDIVIDUAL:
                        var43.class_based_cnt++;
                        break;
                     case TEAM_CLASS_FREE:
                        var43.team_cnt++;
                  }

                  NoblesController.getInstance().SaveNobleRecord(var43);
                  var41.sendPacket(new SystemMessage(1657).addName(var41).addNumber(var37));

                  for (QuestState var58 : var41.getAllQuestsStates()) {
                     if (var58.isStarted()) {
                        var58.getQuest().notifyOlympiadResult(var58, this.getType(), !var3);
                     }
                  }

                  for (Effect var54 : var41.getEffectList().getAllEffects()) {
                     if (var54 != null && var54.isCancelable()) {
                        var54.exit();
                     }
                  }

                  var41.sendChanges();
                  var41.updateEffectIcons();
                  int[] var51 = null;
                  switch (this.getType()) {
                     case CLASS_FREE:
                        var51 = Config.OLY_VICTORY_CFREE_RITEMCNT;
                        break;
                     case CLASS_INDIVIDUAL:
                     case CLASS_TYPE_INDIVIDUAL:
                        var51 = Config.OLY_VICTORY_CBASE_RITEMCNT;
                        break;
                     case TEAM_CLASS_FREE:
                        var51 = Config.OLY_VICTORY_3TEAM_RITEMCNT;
                  }

                  if (!ArrayUtils.isEmpty(var51)) {
                     for (int var55 = 0; var55 < Config.OLY_VICTORY_RITEMID.length; var55++) {
                        if (Config.OLY_VICTORY_RITEMID[var55] != 0 && var51[var55] > 0 && Rnd.chance(Config.OLY_VICTORY_RITEMID_CHANCE[var55])) {
                           var41.getInventory().addItem(Config.OLY_VICTORY_RITEMID[var55], var51[var55]);
                           var41.sendPacket(SystemMessage2.obtainItems(Config.OLY_VICTORY_RITEMID[var55], var51[var55], 0));
                        }
                     }
                  }

                  CompetitionController.getInstance()
                     .addCompetitionResult(
                        OlyController.getInstance().getCurrentSeason(),
                        NoblesController.getInstance().getNobleRecord(var41.getObjectId()),
                        var37,
                        NoblesController.getInstance().getNobleRecord(var7[var39].getObjectId()),
                        var37,
                        this.getType(),
                        var3,
                        var4,
                        var5
                     );
                  var41.getListeners().onOlyCompetitionCompleted(this, !var3);
               } catch (Exception var25) {
                  var25.printStackTrace();
               }
            }
         }
      }
   }

   public boolean ValidateParticipants() {
      boolean var1 = false;
      Participant var2 = null;
      Participant var3 = null;

      for (Participant var7 : this._participants) {
         if (!var7.validateThis()) {
            var1 = true;
            var3 = var7;
            break;
         }

         var2 = var7;
      }

      if (!var1) {
         return false;
      }

      if (var3 != null) {
         if (var2 == null) {
            for (Participant var11 : this._participants) {
               if (var11 != var3) {
                  var2 = var11;
               }
            }
         }

         this.a(var2, var3, false, true);
      }

      this.cancelTask();
      CompetitionController.getInstance().FinishCompetition(this);
      return true;
   }

   public synchronized void ValidateWinner() {
      if (this.getState() == CompetitionState.INIT) {
         this.broadcastPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME, true, false);
         if (!this._participants[0].isAlive()) {
            this.a(this._participants[1], this._participants[0], false, true);
         } else if (!this._participants[1].isAlive()) {
            this.a(this._participants[0], this._participants[1], false, true);
         }

         this.cancelTask();
         CompetitionController.getInstance().FinishCompetition(this);
      } else {
         if (this.getState() != CompetitionState.FINISH && (!this._participants[0].isAlive() || !this._participants[1].isAlive())) {
            this.cancelTask();
            this.setState(CompetitionState.FINISH);
            CompetitionController.getInstance().scheduleFinishCompetition(this, 20, 100L);
         }

         if (this.getState() == CompetitionState.FINISH) {
            if (!this._participants[0].isAlive()) {
               this.a(this._participants[1], this._participants[0], false);
            } else if (!this._participants[1].isAlive()) {
               this.a(this._participants[0], this._participants[1], false);
            } else {
               double var1 = this._participants[0].getTotalDamage();
               double var3 = this._participants[1].getTotalDamage();
               if (var1 < var3) {
                  this.a(this._participants[0], this._participants[1], false);
               } else if (var1 > var3) {
                  this.a(this._participants[1], this._participants[0], false);
               } else {
                  this.a(this._participants[0], this._participants[1], true);
               }
            }

            for (Participant var4 : this._participants) {
               for (Player var8 : var4.getPlayers()) {
                  if (var8.getCurrentHp() <= 0.5) {
                     var8.doRevive(100.0);
                  }

                  var8.sendPacket(new ExOlympiadMode(0));
               }
            }

            this.broadcastPacket(new ExOlympiadMode(3), false, true);
         }
      }
   }

   public void teleportParticipantsOnStadium() {
      this.cN();

      for (Participant var4 : this._participants) {
         for (Player var8 : var4.getPlayers()) {
            try {
               if (var8 != null) {
                  Location var9 = Location.findAroundPosition(this.getStadium().getLocForParticipant(var4), 0, 32);
                  var8.setVar("backCoords", var8.getLoc().toXYZString(), -1L);
                  var8.teleToLocation(var9, this.Ro);
                  var8.sendPacket(new ExOlympiadMode(var4.getSide()));
                  if (this.getType() == CompetitionType.TEAM_CLASS_FREE) {
                     var8.setTeam(var4.getSide() == 1 ? TeamType.BLUE : TeamType.RED);
                  }

                  Summon var10 = var8.getPet();
                  if (var10 != null) {
                     if (var10.isPet()) {
                        var10.unSummon();
                     } else {
                        var10.teleToLocation(var9, this.Ro);
                     }
                  }
               }
            } catch (Exception var11) {
               var11.printStackTrace();
            }
         }
      }
   }

   public void teleportParticipantsBack() {
      this.cM();

      for (Participant var4 : this._participants) {
         for (Player var8 : var4.getPlayers()) {
            try {
               if (var8 != null && var8.getVar("backCoords") != null) {
                  Location var9 = Location.parseLoc(var8.getVar("backCoords"));
                  var8.unsetVar("backCoords");
                  var8.sendPacket(new ExOlympiadMode(0));
                  if (var8.isBlocked()) {
                     var8.unblock();
                  }

                  if (this.getType() == CompetitionType.TEAM_CLASS_FREE) {
                     var8.setTeam(TeamType.NONE);
                  }

                  var8.setReflection(0);
                  var8.teleToLocation(var9);
                  Summon var10 = var8.getPet();
                  if (var10 != null) {
                     if (var10.isPet()) {
                        var10.unSummon();
                     } else {
                        var10.setReflection(0);
                        var10.teleToLocation(var9);
                     }
                  }
               }
            } catch (Exception var11) {
               var11.printStackTrace();
            }
         }
      }
   }

   public void broadcastEverybodyOlympiadUserInfo() {
      for (Participant var4 : this._participants) {
         for (Player var8 : var4.getPlayers()) {
            if (var8 != null) {
               ExOlympiadUserInfo var9 = new ExOlympiadUserInfo(var8);
               this.broadcastPacket(var9, true, true);
               var8.broadcastRelationChanged();
            }
         }
      }
   }

   public void broadcastEverybodyEffectIcons() {
      for (Participant var4 : this._participants) {
         for (Player var8 : var4.getPlayers()) {
            this.broadcastEffectIcons(var8, var8.getEffectList().getAllFirstEffects());
         }
      }
   }

   public void broadcastEffectIcons(Player var1, Effect[] var2) {
      ExOlympiadSpelledInfo var3 = new ExOlympiadSpelledInfo();

      for (Effect var7 : var2) {
         if (var7 != null && var7.isInUse()) {
            var7.addOlympiadSpelledIcon(var1, var3);
         }
      }

      if (this.getState() == CompetitionState.PLAYING) {
         this.broadcastPacket(var3, true, true);
      } else {
         var1.getOlyParticipant().sendPacket(var3);
      }
   }

   public void broadcastPacket(L2GameServerPacket var1, boolean var2, boolean var3) {
      if (this.getState() != null) {
         if (this.getState() == CompetitionState.INIT && var2) {
            for (Participant var7 : this._participants) {
               for (Player var11 : var7.getPlayers()) {
                  var11.sendPacket(var1);
               }
            }
         } else {
            for (Player var5 : this.getStadium().getPlayers()) {
               if (var2 && var5.isOlyParticipant() || var3 && var5.isOlyObserver()) {
                  var5.sendPacket(var1);
               }
            }
         }
      }
   }

   public synchronized void scheduleTask(Runnable var1, long var2) {
      this.Rt = ThreadPoolManager.getInstance().schedule(var1, var2);
   }

   public synchronized void cancelTask() {
      if (this.Rt != null) {
         this.Rt.cancel(false);
         this.Rt = null;
      }
   }

   public Participant[] getParticipants() {
      return this._participants;
   }

   public static SystemMessage checkPlayer(Player var0) {
      if (!var0.isNoble()) {
         return new SystemMessage(1501).addName(var0);
      } else if (var0.isInDuel()) {
         return new SystemMessage(1599);
      } else if (var0.getBaseClassId() != var0.getClassId().getId() || var0.getClassId().getLevel() < 4) {
         return new SystemMessage(1500).addName(var0);
      } else if (var0.getInventoryLimit() * 0.8 <= var0.getInventory().getSize()) {
         return new SystemMessage(1691).addName(var0);
      } else if (var0.isCursedWeaponEquipped()) {
         return new SystemMessage(1857).addName(var0).addItemName(var0.getCursedWeaponEquippedId());
      } else {
         return NoblesController.getInstance().getPointsOf(var0.getObjectId()) < 1
            ? new SystemMessage(1983)
               .addString(new CustomMessage("THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_REQUIREMENTS_ARE_NOT_MET_IN_ORDER_TO_PARTICIPATE_IN", var0).toString())
            : null;
      }
   }
}
