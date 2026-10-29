package l2.gameserver.model.base;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public enum SkillTrait {
   NONE,
   BLEED {
      @Override
      public final double calcVuln(Env var1) {
         return var1.target.calcStat(Stats.BLEED_RESIST, var1.character, var1.skill);
      }

      @Override
      public final double calcProf(Env var1) {
         return var1.character.calcStat(Stats.BLEED_POWER, var1.target, var1.skill);
      }
   },
   BOSS,
   DEATH,
   DERANGEMENT {
      @Override
      public final double calcVuln(Env var1) {
         return var1.target.calcStat(Stats.MENTAL_RESIST, var1.character, var1.skill);
      }

      @Override
      public final double calcProf(Env var1) {
         return Math.min(40.0, var1.character.calcStat(Stats.MENTAL_POWER, var1.target, var1.skill) + calcEnchantMod(var1));
      }
   },
   ETC,
   GUST,
   HOLD {
      @Override
      public final double calcVuln(Env var1) {
         return var1.target.calcStat(Stats.ROOT_RESIST, var1.character, var1.skill);
      }

      @Override
      public final double calcProf(Env var1) {
         return var1.character.calcStat(Stats.ROOT_POWER, var1.target, var1.skill);
      }
   },
   PARALYZE {
      @Override
      public final double calcVuln(Env var1) {
         return var1.target.calcStat(Stats.PARALYZE_RESIST, var1.character, var1.skill);
      }

      @Override
      public final double calcProf(Env var1) {
         return var1.character.calcStat(Stats.PARALYZE_POWER, var1.target, var1.skill);
      }
   },
   PHYSICAL_BLOCKADE,
   POISON {
      @Override
      public final double calcVuln(Env var1) {
         return var1.target.calcStat(Stats.POISON_RESIST, var1.character, var1.skill);
      }

      @Override
      public final double calcProf(Env var1) {
         return var1.character.calcStat(Stats.POISON_POWER, var1.target, var1.skill);
      }
   },
   SHOCK {
      @Override
      public final double calcVuln(Env var1) {
         return var1.target.calcStat(Stats.STUN_RESIST, var1.character, var1.skill);
      }

      @Override
      public final double calcProf(Env var1) {
         return Math.min(40.0, var1.character.calcStat(Stats.STUN_POWER, var1.target, var1.skill) + calcEnchantMod(var1));
      }
   },
   SLEEP {
      @Override
      public final double calcVuln(Env var1) {
         return var1.target.calcStat(Stats.SLEEP_RESIST, var1.character, var1.skill);
      }

      @Override
      public final double calcProf(Env var1) {
         return var1.character.calcStat(Stats.SLEEP_POWER, var1.target, var1.skill);
      }
   },
   VALAKAS;

   SkillTrait() {
   }

   public double calcVuln(Env var1) {
      return 0.0;
   }

   public double calcProf(Env var1) {
      return 0.0;
   }

   public static double calcEnchantMod(Env var0) {
      int var1 = var0.skill.getDisplayLevel();
      if (var1 <= 100) {
         return 0.0;
      }

      var1 %= 100;
      return var0.skill.getEnchantLevelCount() == 15 ? var1 * 2 : var1;
   }
}
