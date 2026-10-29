package l2.gameserver.skills.skillclasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

public class NegateEffects extends Skill {
   private Map<EffectType, Integer> apg = new HashMap<>();
   private Map<String, Integer> aph = new HashMap<>();
   private final boolean api;
   private final boolean apj;

   public NegateEffects(StatsSet var1) {
      super(var1);
      String[] var2 = var1.getString("negateEffects", "").split(";");

      for (int var3 = 0; var3 < var2.length; var3++) {
         if (!var2[var3].isEmpty()) {
            String[] var4 = var2[var3].split(":");
            this.apg.put(Enum.valueOf(EffectType.class, var4[0]), var4.length > 1 ? Integer.decode(var4[1]) : Integer.MAX_VALUE);
         }
      }

      String[] var6 = var1.getString("negateStackType", "").split(";");

      for (int var7 = 0; var7 < var6.length; var7++) {
         if (!var6[var7].isEmpty()) {
            String[] var5 = var6[var7].split(":");
            this.aph.put(var5[0], var5.length > 1 ? Integer.decode(var5[1]) : Integer.MAX_VALUE);
         }
      }

      this.api = var1.getBool("onlyPhysical", false);
      this.apj = var1.getBool("negateDebuffs", true);
   }

   @Override
   public void useSkill(Creature var1, List<Creature> var2) {
      for (Creature var4 : var2) {
         if (var4 != null) {
            if (!this.apj && !Formulas.calcSkillSuccess(var1, var4, this, this.getActivateRate())) {
               var1.sendPacket(new SystemMessage(139).addString(var4.getName()).addSkillName(this.getDisplayId(), this.getDisplayLevel()));
            } else {
               if (!this.apg.isEmpty()) {
                  for (Entry var6 : this.apg.entrySet()) {
                     this.a(var4, (EffectType)var6.getKey(), (Integer)var6.getValue());
                  }
               }

               if (!this.aph.isEmpty()) {
                  for (Entry var8 : this.aph.entrySet()) {
                     this.a(var4, (String)var8.getKey(), (Integer)var8.getValue());
                  }
               }

               this.getEffects(var1, var4, this.getActivateRate() > 0, false);
            }
         }
      }

      if (this.isSSPossible()) {
         var1.unChargeShots(this.isMagic());
      }
   }

   private void a(Creature var1, EffectType var2, int var3) {
      for (l2.gameserver.model.Effect var5 : var1.getEffectList().getAllEffects()) {
         Skill var6 = var5.getSkill();
         if ((!this.api || !var6.isMagic())
            && var6.isCancelable()
            && (!var6.isOffensive() || this.apj)
            && (var6.isOffensive() || var6.getMagicLevel() <= this.getMagicLevel() || !Rnd.chance(var6.getMagicLevel() - this.getMagicLevel()))
            && var5.getEffectType() == var2
            && var5.getStackOrder() <= var3) {
            var5.exit();
         }
      }
   }

   private void a(Creature var1, String var2, int var3) {
      for (l2.gameserver.model.Effect var5 : var1.getEffectList().getAllEffects()) {
         Skill var6 = var5.getSkill();
         if ((!this.api || !var6.isMagic())
            && var6.isCancelable()
            && (!var6.isOffensive() || this.apj)
            && (var6.isOffensive() || var6.getMagicLevel() <= this.getMagicLevel() || !Rnd.chance(var6.getMagicLevel() - this.getMagicLevel()))
            && var5.isStackTypeMatch(var2)
            && var5.getStackOrder() <= var3) {
            var5.exit();
         }
      }
   }
}
