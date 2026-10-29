package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.EffectsDAO;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectTasks;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.model.instances.TrapInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncAdd;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;

public class Summon extends Skill {
   private final Summon.SummonType apx;
   private final double VV;
   private final int VW;
   private final int VX;
   private final int VY;
   private final int apy;
   private final int apz;

   public Summon(StatsSet var1) {
      super(var1);
      this.apx = Enum.valueOf(Summon.SummonType.class, var1.getString("summonType", "PET").toUpperCase());
      this.VV = var1.getDouble("expPenalty", 0.0);
      this.VW = var1.getInteger("itemConsumeIdInTime", 0);
      this.VX = var1.getInteger("itemConsumeCountInTime", 0);
      this.VY = var1.getInteger("itemConsumeDelay", 240) * 1000;
      this.apy = var1.getInteger("lifeTime", 1200) * 1000;
      this.apz = var1.getInteger("minRadius", 0);
   }

   @Override
   public boolean checkCondition(Creature var1, Creature var2, boolean var3, boolean var4, boolean var5) {
      Player var6 = var1.getPlayer();
      if (var6 == null) {
         return false;
      }

      switch (this.apx) {
         case TRAP:
            if (var6.isInZonePeace()) {
               var1.sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
               return false;
            }
            break;
         case PET:
         case SIEGE_SUMMON:
            if (var6.isProcessingRequest()) {
               var6.sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
               return false;
            }

            if (var6.getPet() != null || var6.isMounted()) {
               var6.sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
               return false;
            }
            break;
         case MERCHANT:
            if (var6.isProcessingRequest()) {
               var6.sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
               return false;
            }
            break;
         case AGATHION:
            if (var6.getAgathionId() > 0 && this._npcId != 0) {
               var6.sendPacket(SystemMsg.AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED);
               return false;
            }
         case NPC:
            if (this.apz > 0) {
               for (NpcInstance var8 : World.getAroundNpc(var6, this.apz, 200)) {
                  if (var8 != null && var8.getNpcId() == this.getNpcId()) {
                     var6.sendPacket(new SystemMessage(SystemMsg.SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN).addName(var8));
                     return false;
                  }
               }
            }
      }

      return super.checkCondition(var1, var2, var3, var4, var5);
   }

   @Override
   public void useSkill(Creature var1, List<Creature> var2) {
      Player var3 = var1.getPlayer();
      switch (this.apx) {
         case TRAP:
            Skill var4 = this.getFirstAddedSkill();
            if (var3.getTrapsCount() >= 5) {
               var3.destroyFirstTrap();
            }

            TrapInstance var5 = new TrapInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(this.getNpcId()), var3, var4);
            var3.addTrap(var5);
            var5.spawnMe();
            break;
         case PET:
         case SIEGE_SUMMON:
            Location var6 = null;
            if (this._targetType == Skill.SkillTargetType.TARGET_CORPSE) {
               for (Creature var8 : var2) {
                  if (var8 != null && var8.isDead()) {
                     var3.getAI().setAttackTarget(null);
                     var6 = var8.getLoc();
                     if (var8.isNpc()) {
                        ((NpcInstance)var8).endDecayTask();
                     } else {
                        if (!var8.isSummon()) {
                           return;
                        }

                        ((SummonInstance)var8).endDecayTask();
                     }
                  }
               }
            }

            if (var3.getPet() != null || var3.isMounted()) {
               return;
            }

            NpcTemplate var11 = NpcHolder.getInstance().getTemplate(this.getNpcId());
            SummonInstance var12 = new SummonInstance(IdFactory.getInstance().getNextId(), var11, var3, this.apy, this.VW, this.VX, this.VY, this);
            var3.setPet(var12);
            var12.setExpPenalty(this.VV);
            var12.setExp(Experience.LEVEL[Math.min(var12.getLevel(), Experience.LEVEL.length - 1)]);
            var12.setHeading(var3.getHeading());
            var12.setReflection(var3.getReflection());
            var12.spawnMe(var6 == null ? Location.findAroundPosition(var3, 50, 70) : var6);
            var12.setRunning();
            var12.setFollowMode(true);
            if (var12.getSkillLevel(4140) > 0) {
               var12.altUseSkill(SkillTable.getInstance().getInfo(4140, var12.getSkillLevel(4140)), var3);
            }

            if (var12.getName().equalsIgnoreCase("Shadow")) {
               var12.addStatFunc(new FuncAdd(Stats.ABSORB_DAMAGE_PERCENT, 64, this, 15.0));
            }

            EffectsDAO.getInstance().restoreEffects(var12);
            if (var3.isOlyParticipant()) {
               var12.getEffectList().stopAllEffects();
            }

            var12.setCurrentHpMp(var12.getMaxHp(), var12.getMaxMp(), false);
            if (this.apx == Summon.SummonType.SIEGE_SUMMON) {
               SiegeEvent var13 = var3.getEvent(SiegeEvent.class);
               var13.addSiegeSummon(var12);
            }
            break;
         case MERCHANT:
            if (var3.getPet() != null || var3.isMounted()) {
               return;
            }

            NpcTemplate var9 = NpcHolder.getInstance().getTemplate(this.getNpcId());
            MerchantInstance var10 = new MerchantInstance(IdFactory.getInstance().getNextId(), var9);
            var10.setCurrentHp(var10.getMaxHp(), false);
            var10.setCurrentMp(var10.getMaxMp());
            var10.setHeading(var3.getHeading());
            var10.setReflection(var3.getReflection());
            var10.spawnMe(var3.getLoc());
            ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(var10), this.apy);
            break;
         case AGATHION:
            var3.setAgathion(this.getNpcId());
            break;
         case NPC:
            NpcUtils.spawnSingle(this.getNpcId(), var3.getLoc(), var3.getReflection(), this.apy, var3.getName());
      }

      if (this.isSSPossible()) {
         var1.unChargeShots(this.isMagic());
      }
   }

   @Override
   public boolean isOffensive() {
      return this._targetType == Skill.SkillTargetType.TARGET_CORPSE;
   }

   private enum SummonType {
      PET,
      SIEGE_SUMMON,
      AGATHION,
      TRAP,
      MERCHANT,
      NPC;
   }
}
