package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.events.objects.ZoneObject;
import l2.gameserver.model.instances.residences.SiegeFlagInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncMul;
import l2.gameserver.templates.StatsSet;

public class SummonSiegeFlag extends Skill {
   private final SummonSiegeFlag.FlagType apF;
   private final double apG;

   public SummonSiegeFlag(StatsSet var1) {
      super(var1);
      this.apF = var1.getEnum("flagType", SummonSiegeFlag.FlagType.class);
      this.apG = var1.getDouble("advancedMultiplier", 1.0);
   }

   @Override
   public boolean checkCondition(Creature var1, Creature var2, boolean var3, boolean var4, boolean var5) {
      if (!var1.isPlayer()) {
         return false;
      }

      if (!super.checkCondition(var1, var2, var3, var4, var5)) {
         return false;
      }

      Player var6 = (Player)var1;
      if (var6.getClan() != null && var6.isClanLeader()) {
         switch (this.apF) {
            case OUTPOST:
            case NORMAL:
            case ADVANCED:
               if (var6.isInZone(Zone.ZoneType.RESIDENCE)) {
                  var6.sendPacket(
                     SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this)
                  );
                  return false;
               } else {
                  SiegeEvent var7 = var1.getEvent(SiegeEvent.class);
                  if (var7 == null) {
                     var6.sendPacket(
                        SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this)
                     );
                     return false;
                  } else {
                     boolean var8 = false;

                     for (Object __raw_var11_62 : (Iterable<?>)(Object)(var7.getObjects("flag_zones"))) {
                        ZoneObject var11 = (ZoneObject)__raw_var11_62;
                        if (var6.isInZone(var11.getZone())) {
                           var8 = true;
                        }
                     }

                     if (!var8) {
                        var6.sendPacket(
                           SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this)
                        );
                        return false;
                     } else {
                        SiegeClanObject var12 = var7.getSiegeClan("attackers", var6.getClan());
                        if (var12 == null) {
                           var6.sendPacket(
                              SystemMsg.YOU_CANNOT_SUMMON_THE_ENCAMPMENT_BECAUSE_YOU_ARE_NOT_A_MEMBER_OF_THE_SIEGE_CLAN_INVOLVED_IN_THE_CASTLE__FORTRESS__HIDEOUT_SIEGE,
                              new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this)
                           );
                           return false;
                        } else if (var12.getFlag() != null) {
                           var6.sendPacket(
                              SystemMsg.AN_OUTPOST_OR_HEADQUARTERS_CANNOT_BE_BUILT_BECAUSE_ONE_ALREADY_EXISTS,
                              new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this)
                           );
                           return false;
                        }
                     }
                  }
               }
            case DESTROY:
            default:
               return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void useSkill(Creature var1, List<Creature> var2) {
      Player var3 = (Player)var1;
      Clan var4 = var3.getClan();
      if (var4 != null && var3.isClanLeader()) {
         SiegeEvent var5 = var1.getEvent(SiegeEvent.class);
         if (var5 != null) {
            SiegeClanObject var6 = var5.getSiegeClan("attackers", var4);
            if (var6 != null) {
               switch (this.apF) {
                  case DESTROY:
                     var6.deleteFlag();
                     break;
                  default:
                     if (var6.getFlag() != null) {
                        return;
                     }

                     SiegeFlagInstance var7 = (SiegeFlagInstance)NpcHolder.getInstance()
                        .getTemplate(this.apF == SummonSiegeFlag.FlagType.OUTPOST ? 36590 : 35062)
                        .getNewInstance();
                     var7.setClan(var6);
                     var7.addEvent(var5);
                     if (this.apF == SummonSiegeFlag.FlagType.ADVANCED) {
                        var7.addStatFunc(new FuncMul(Stats.MAX_HP, 80, var7, this.apG));
                     }

                     var7.setCurrentHpMp(var7.getMaxHp(), var7.getMaxMp(), true);
                     var7.setHeading(var3.getHeading());
                     int var8 = (int)(var3.getX() + 100.0 * Math.cos(var3.headingToRadians(var3.getHeading() - 32768)));
                     int var9 = (int)(var3.getY() + 100.0 * Math.sin(var3.headingToRadians(var3.getHeading() - 32768)));
                     var7.spawnMe(GeoEngine.moveCheck(var3.getX(), var3.getY(), var3.getZ(), var8, var9, var3.getGeoIndex()));
                     var6.setFlag(var7);
               }
            }
         }
      }
   }

   public enum FlagType {
      DESTROY,
      NORMAL,
      ADVANCED,
      OUTPOST;
   }
}
