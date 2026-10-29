package l2.gameserver.model;

import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.gameserver.Config;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.skills.skillclasses.Transformation;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncTemplate;
import org.apache.commons.lang3.ArrayUtils;

public class EffectList {
   public static final int NONE_SLOT_TYPE = -1;
   public static final int BUFF_SLOT_TYPE = 0;
   public static final int MUSIC_SLOT_TYPE = 1;
   public static final int TRIGGER_SLOT_TYPE = 2;
   public static final int DEBUFF_SLOT_TYPE = 3;
   public static final int DEBUFF_LIMIT = 8;
   public static final int MUSIC_LIMIT = 12;
   public static final int TRIGGER_LIMIT = 12;
   private Creature _actor;
   private List<Effect> Fe;
   private Lock lock = new ReentrantLock();

   public EffectList(Creature var1) {
      this._actor = var1;
   }

   public int getEffectsCountForSkill(int var1) {
      if (this.isEmpty()) {
         return 0;
      }

      int var2 = 0;

      for (Effect var4 : this.Fe) {
         if (var4.getSkill().getId() == var1) {
            var2++;
         }
      }

      return var2;
   }

   public Effect getEffectByType(EffectType var1) {
      if (this.isEmpty()) {
         return null;
      }

      for (Effect var3 : this.Fe) {
         if (var3.getEffectType() == var1) {
            return var3;
         }
      }

      return null;
   }

   public List<Effect> getEffectsBySkill(Skill var1) {
      return var1 == null ? null : this.getEffectsBySkillId(var1.getId());
   }

   public int getActiveMusicCount(int var1) {
      if (this.isEmpty()) {
         return 0;
      }

      int var2 = 0;

      for (Effect var4 : this.Fe) {
         if (Config.ALT_ADDITIONAL_DANCE_SONG_MANA_CONSUME
            && var4.getSkill().isMusic()
            && var4.getSkill().getId() != var1
            && var4.getTimeLeft() > Config.ALT_MUSIC_COST_GUARD_INTERVAL) {
            var2++;
         }
      }

      return var2;
   }

   public List<Effect> getEffectsBySkillId(int var1) {
      if (this.isEmpty()) {
         return null;
      }

      ArrayList var2 = new ArrayList(2);

      for (Effect var4 : this.Fe) {
         if (var4.getSkill().getId() == var1) {
            var2.add(var4);
         }
      }

      return var2.isEmpty() ? null : var2;
   }

   public Effect getEffectByIndexAndType(int var1, EffectType var2) {
      if (this.isEmpty()) {
         return null;
      }

      for (Effect var4 : this.Fe) {
         if (var4.getSkill().getId() == var1 && var4.getEffectType() == var2) {
            return var4;
         }
      }

      return null;
   }

   public Effect getEffectByStackType(String var1) {
      if (this.isEmpty()) {
         return null;
      }

      for (Effect var3 : this.Fe) {
         if (var3.getStackType().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public boolean containEffectFromSkills(int... var1) {
      if (this.isEmpty()) {
         return false;
      }

      for (Effect var4 : this.Fe) {
         int var2 = var4.getSkill().getId();
         if (ArrayUtils.contains(var1, var2)) {
            return true;
         }
      }

      return false;
   }

   public List<Effect> getAllEffects() {
      return this.isEmpty() ? Collections.emptyList() : new ArrayList<>(this.Fe);
   }

   public boolean isEmpty() {
      return this.Fe == null || this.Fe.isEmpty();
   }

   public Effect[] getAllFirstEffects() {
      if (this.isEmpty()) {
         return Effect.EMPTY_L2EFFECT_ARRAY;
      }

      LinkedHashMap var1 = new LinkedHashMap();

      for (Effect var3 : this.Fe) {
         if (!var1.containsKey(var3.getSkill())) {
            var1.put(var3.getSkill(), var3);
         }
      }

      Collection var4 = var1.values();
      return (Effect[])var4.toArray(new Effect[var4.size()]);
   }

   private void b(Effect var1) {
      if (this.Fe != null) {
         int var2 = getSlotType(var1);
         if (var2 != -1) {
            int var3 = 0;
            TIntArrayList var4 = new TIntArrayList();

            for (Effect var6 : this.Fe) {
               if (var6.isInUse()) {
                  if (var6.getSkill().equals(var1.getSkill())) {
                     return;
                  }

                  if (!var4.contains(var6.getSkill().getId())) {
                     int var7 = getSlotType(var6);
                     if (var7 == var2) {
                        var3++;
                        var4.add(var6.getSkill().getId());
                     }
                  }
               }
            }

            int var9 = 0;
            switch (var2) {
               case 0:
                  var9 = this._actor.getBuffLimit();
               case 1:
               default:
                  break;
               case 2:
                  var9 = 12 + Config.ALT_TRIGGER_SLOT_ADDER;
                  break;
               case 3:
                  var9 = Config.ALT_DEBUFF_LIMIT;
            }

            if (var3 >= var9) {
               int var10 = 0;

               for (Effect var8 : this.Fe) {
                  if (var8.isInUse() && getSlotType(var8) == var2) {
                     var10 = var8.getSkill().getId();
                     break;
                  }
               }

               if (var10 != 0) {
                  this.stopEffect(var10);
               }
            }
         }
      }
   }

   public static int getSlotType(Effect var0) {
      if (var0.getSkill().isPassive()
         || var0.getSkill().isSlotNone()
         || var0.getSkill().isToggle()
         || var0.getSkill() instanceof Transformation
         || var0.isStackTypeMatch("HpRecoverCast")
         || var0.getEffectType() == EffectType.Cubic) {
         return -1;
      } else if (var0.getSkill().isOffensive()) {
         return 3;
      } else {
         return var0.getSkill().isTrigger() ? 2 : 0;
      }
   }

   public static boolean checkStackType(EffectTemplate var0, EffectTemplate var1) {
      if (!var0._stackType.equals("none") && var0._stackType.equalsIgnoreCase(var1._stackType)) {
         return true;
      } else if (!var0._stackType.equals("none") && var0._stackType.equalsIgnoreCase(var1._stackType2)) {
         return true;
      } else {
         return !var0._stackType2.equals("none") && var0._stackType2.equalsIgnoreCase(var1._stackType)
            ? true
            : !var0._stackType2.equals("none") && var0._stackType2.equalsIgnoreCase(var1._stackType2);
      }
   }

   public void addEffect(Effect var1) {
      double var2 = this._actor.getCurrentHp();
      double var4 = this._actor.getCurrentMp();
      double var6 = this._actor.getCurrentCp();
      String var8 = var1.getStackType();
      boolean var9 = false;
      HashSet var10 = new HashSet();
      this.lock.lock();

      try {
         if (this.Fe == null) {
            this.Fe = new CopyOnWriteArrayList<>();
         }

         if (var8.equals("none")) {
            for (Effect var21 : this.Fe) {
               if (var21.isInUse()
                  && var21.getSkill().getId() == var1.getSkill().getId()
                  && var21.getEffectType() == var1.getEffectType()
                  && var21.getStackType().equals("none")) {
                  if (var1.getTimeLeft() <= var21.getTimeLeft()) {
                     return;
                  }

                  var10.add(var21.getSkill());
                  var21.exit();
               }
            }
         } else {
            for (Effect var12 : this.Fe) {
               if (var12.isInUse() && checkStackType(var12.getTemplate(), var1.getTemplate())) {
                  if (var12.getSkill().getId() == var1.getSkill().getId() && var12.getEffectType() != var1.getEffectType()) {
                     break;
                  }

                  if (var12.getStackOrder() == -1) {
                     return;
                  }

                  if (!var12.maybeScheduleNext(var1)) {
                     return;
                  }

                  var10.add(var12.getSkill());
               }
            }
         }

         this.b(var1);
         if (var9 = this.Fe.add(var1)) {
            var1.setInUse(true);
         }
      } finally {
         this.lock.unlock();
      }

      if (var9) {
         if (!var10.isEmpty()) {
            for (Object __raw_var22_317 : (Iterable<?>)(Object)(var10)) {
               Skill var22 = (Skill)__raw_var22_317;
               var1.getEffected().sendPacket(new SystemMessage(92).addSkillName(var22.getDisplayId(), var22.getDisplayLevel()));
            }
         }

         var1.start();

         for (FuncTemplate var14 : var1.getTemplate().getAttachedFuncs()) {
            if (var14._stat == Stats.MAX_HP) {
               this._actor.setCurrentHp(var2, false);
            } else if (var14._stat == Stats.MAX_MP) {
               this._actor.setCurrentMp(var4);
            } else if (var14._stat == Stats.MAX_CP) {
               this._actor.setCurrentCp(var6);
            }
         }

         if (this._actor.isBulkBuffing()) {
            this._actor.setStatsModifiedInBulk(true);
         } else {
            this._actor.updateStats();
            this._actor.updateEffectIcons();
            this.broadcastDebuffSnapshotIfMonster();
         }
      }
   }

   public void removeEffect(Effect var1) {
      if (var1 != null) {
         boolean var2 = false;
         this.lock.lock();

         try {
            if (this.Fe == null) {
               return;
            }

            if (!(var2 = this.Fe.remove(var1))) {
               return;
            }
         } finally {
            this.lock.unlock();
         }

         if (var2) {
            if (this._actor.isBulkBuffing()) {
               this._actor.setStatsModifiedInBulk(true);
            } else {
               this._actor.updateStats();
               this._actor.updateEffectIcons();
               this.broadcastDebuffSnapshotIfMonster();
            }
         }
      }
   }

   public void stopAllEffects() {
      if (!this.isEmpty()) {
         this.lock.lock();

         try {
            for (Effect var2 : this.Fe) {
               var2.exit();
            }
         } finally {
            this.lock.unlock();
         }

         this._actor.updateStats();
         this._actor.updateEffectIcons();
         this.broadcastDebuffSnapshotIfMonster();
      }
   }

   public void stopAllNegateEffects() {
      this.getAllEffects()
         .stream()
         .map(Effect::getSkill)
         .distinct()
         .filter(Skill::isOffensive)
         .flatMap(var1 -> this.getEffectsBySkill(var1).stream())
         .forEach(Effect::exit);
   }

   /**
    * Hook opcional Fermata Phase 2: emite {@code MonsterDebuffSnapshot} para os
    * jogadores visiveis com capacidade {@code BIT_MONSTER_DEBUFF_HUD} sempre
    * que a composicao de debuffs no alvo muda. No-op silencioso para alvos
    * que nao sao monstros (Player, Summon, Door etc.).
    */
   public void broadcastDebuffSnapshotIfMonster() {
      if (this._actor == null || !this._actor.isMonster()) {
         return;
      }
      try {
         java.util.List<l2.gameserver.model.Effect> all = this.getAllEffects();
         if (all == null || all.isEmpty()) {
            int[] zero = new int[0];
            l2.gameserver.network.l2.fermata.s2c.MonsterDebuffSnapshot snapshot = new l2.gameserver.network.l2.fermata.s2c.MonsterDebuffSnapshot(this._actor.getObjectId(), zero, zero, zero);
            l2.gameserver.network.l2.fermata.FermataBroadcaster.broadcastIfCapable(this._actor, snapshot, l2.gameserver.network.l2.fermata.FermataCapabilities.BIT_MONSTER_DEBUFF_HUD);
            return;
         }
         java.util.ArrayList<Integer> skillIds = new java.util.ArrayList<>();
         java.util.ArrayList<Integer> skillLevels = new java.util.ArrayList<>();
         java.util.ArrayList<Integer> remainingSeconds = new java.util.ArrayList<>();
         for (l2.gameserver.model.Effect eff : all) {
            if (eff == null || eff.getSkill() == null || !eff.getSkill().isOffensive()) {
               continue;
            }
            int left = eff.getTimeLeft();
            int seconds = left <= 0 ? 0 : (int) Math.min(Integer.MAX_VALUE, (long) left / 1000L);
            skillIds.add(eff.getSkill().getId());
            skillLevels.add(eff.getSkill().getLevel());
            remainingSeconds.add(seconds);
         }
         int n = skillIds.size();
         int[] idsArr = new int[n];
         int[] lvArr = new int[n];
         int[] remArr = new int[n];
         for (int i = 0; i < n; i++) {
            idsArr[i] = skillIds.get(i);
            lvArr[i] = skillLevels.get(i);
            remArr[i] = remainingSeconds.get(i);
         }
         l2.gameserver.network.l2.fermata.s2c.MonsterDebuffSnapshot snapshot = new l2.gameserver.network.l2.fermata.s2c.MonsterDebuffSnapshot(this._actor.getObjectId(), idsArr, lvArr, remArr);
         l2.gameserver.network.l2.fermata.FermataBroadcaster.broadcastIfCapable(this._actor, snapshot, l2.gameserver.network.l2.fermata.FermataCapabilities.BIT_MONSTER_DEBUFF_HUD);
      } catch (Exception ex) {
      }
   }

   public void stopEffect(int var1) {
      if (!this.isEmpty()) {
         for (Effect var3 : this.Fe) {
            if (var3.getSkill().getId() == var1) {
               var3.exit();
            }
         }
      }
   }

   public void stopEffect(Skill var1) {
      if (var1 != null) {
         this.stopEffect(var1.getId());
      }
   }

   public void stopEffectByDisplayId(int var1) {
      if (!this.isEmpty()) {
         for (Effect var3 : this.Fe) {
            if (var3.getSkill().getDisplayId() == var1) {
               var3.exit();
            }
         }
      }
   }

   public void stopEffects(EffectType var1) {
      if (!this.isEmpty()) {
         for (Effect var3 : this.Fe) {
            if (var3.getEffectType() == var1) {
               var3.exit();
            }
         }
      }
   }

   public void stopAllSkillEffects(EffectType var1) {
      if (!this.isEmpty()) {
         TIntHashSet var2 = new TIntHashSet();

         for (Effect var4 : this.Fe) {
            if (var4.getEffectType() == var1) {
               var2.add(var4.getSkill().getId());
            }
         }

         for (int var6 : var2.toArray()) {
            this.stopEffect(var6);
         }
      }
   }
}
