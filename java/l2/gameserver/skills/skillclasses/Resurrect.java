package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.BaseStats;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.StatsSet;
import org.apache.commons.lang3.tuple.Pair;

public class Resurrect extends Skill {
   private final boolean apu;
   private final int aoO;

   public Resurrect(StatsSet var1) {
      super(var1);
      this.apu = var1.getBool("canPet", false);
      this.aoO = var1.getInteger("expireResurrectTime", 0);
   }

   private boolean a(Player var1, Creature var2, boolean var3) {
      boolean var4 = true;

      for (GlobalEvent var6 : var1.getEvents()) {
         if (!var6.canResurrect(var1, var2, var3)) {
            var4 = false;
         }
      }

      if (var4) {
         SiegeEvent var9 = var1.getEvent(SiegeEvent.class);
         SiegeEvent var10 = var2.getEvent(SiegeEvent.class);
         boolean var7 = var1.isInZone(Zone.ZoneType.SIEGE);
         boolean var8 = var2.isInZone(Zone.ZoneType.SIEGE);
         if (var9 == null && var7 || var10 == null && var8) {
            var4 = false;
         }
      }

      if (!var4) {
         var1.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean checkCondition(Creature var1, Creature var2, boolean var3, boolean var4, boolean var5) {
      if (!var1.isPlayer()) {
         return false;
      }

      if (var2 != null && (var2 == var1 || var2.isDead())) {
         Player var6 = (Player)var1;
         Player var7 = var2.getPlayer();
         if (var7 == null) {
            var6.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
         }

         if (var6.isOlyParticipant() || var7.isOlyParticipant()) {
            var6.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
         }

         if (!this.a(var6, var2, var3)) {
            return false;
         }

         if (this.oneTarget()) {
            if (var2.isPet()) {
               Pair var8 = var7.getAskListener(false);
               ReviveAnswerListener var9 = var8 != null && var8.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener)var8.getValue() : null;
               if (var9 != null) {
                  if (var9.isForPet()) {
                     var1.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
                  } else {
                     var1.sendPacket(Msg.SINCE_THE_MASTER_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_THE_PET_HAS_BEEN_CANCELLED);
                  }

                  return false;
               }

               if (!this.apu && this._targetType != Skill.SkillTargetType.TARGET_PET) {
                  var6.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                  return false;
               }
            } else if (var2.isPlayer()) {
               Pair var10 = var7.getAskListener(false);
               ReviveAnswerListener var11 = var10 != null && var10.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener)var10.getValue() : null;
               if (var11 != null) {
                  if (var11.isForPet()) {
                     var1.sendPacket(Msg.WHILE_A_PET_IS_ATTEMPTING_TO_RESURRECT_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
                  } else {
                     var1.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
                  }

                  return false;
               }

               if (this._targetType == Skill.SkillTargetType.TARGET_PET) {
                  var6.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                  return false;
               }

               if (var7.isFestivalParticipant()) {
                  var6.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Resurrect", var6));
                  return false;
               }
            }
         }

         return super.checkCondition(var1, var2, var3, var4, var5);
      } else {
         var1.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
         return false;
      }
   }

   @Override
   public void useSkill(Creature var1, List<Creature> var2) {
      double var3 = this._power;
      if (var3 < 100.0 && !this.isHandler()) {
         double var5 = this._power * (BaseStats.WIT.calcBonus(var1) - 1.0);
         var3 += var5 > 20.0 ? 20.0 : var5;
         if (var3 > 90.0) {
            var3 = 90.0;
         }
      }

      label94:
      for (Creature var6 : var2) {
         if (var6 != null && var6.getPlayer() != null) {
            for (GlobalEvent var8 : var6.getEvents()) {
               if (!var8.canResurrect((Player)var1, var6, true)) {
                  continue label94;
               }
            }

            if (var6.isPet() && this.apu) {
               if (var6.getPlayer() == var1) {
                  ((PetInstance)var6).doRevive(var3);
               } else {
                  var6.getPlayer().reviveRequest((Player)var1, var3, true, this.aoO);
               }
            } else {
               if (!var6.isPlayer() || this._targetType == Skill.SkillTargetType.TARGET_PET) {
                  continue;
               }

               Player var11 = (Player)var6;
               Pair var12 = var11.getAskListener(false);
               ReviveAnswerListener var9 = var12 != null && var12.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener)var12.getValue() : null;
               if (var9 != null || var11.isFestivalParticipant()) {
                  continue;
               }

               var11.reviveRequest((Player)var1, var3, false, this.aoO);
            }

            this.getEffects(var1, var6, this.getActivateRate() > 0, false);
         }
      }

      if (this.isSSPossible()) {
         var1.unChargeShots(this.isMagic());
      }
   }
}
