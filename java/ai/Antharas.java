package ai;

import bosses.AntharasManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;

public class Antharas extends DefaultAI {
   final Skill s_fear = this.a(4108, 1);
   final Skill s_fear2 = this.a(5092, 1);
   final Skill s_curse = this.a(4109, 1);
   final Skill s_paralyze = this.a(4111, 1);
   final Skill s_shock = this.a(4106, 1);
   final Skill s_shock2 = this.a(4107, 1);
   final Skill s_antharas_ordinary_attack = this.a(4112, 1);
   final Skill s_antharas_ordinary_attack2 = this.a(4113, 1);
   final Skill s_meteor = this.a(5093, 1);
   final Skill s_breath = this.a(4110, 1);
   final Skill s_regen1 = this.a(4239, 1);
   final Skill s_regen2 = this.a(4240, 1);
   final Skill s_regen3 = this.a(4241, 1);
   private int aV = 0;
   private static long aW = 0L;
   private List<NpcInstance> aX = new ArrayList<>();

   public Antharas(NpcInstance var1) {
      super(var1);
   }

   @Override
   protected void onEvtAttacked(Creature var1, int var2) {
      AntharasManager.setLastAttackTime();

      for (Playable var4 : AntharasManager.getZone().getInsidePlayables()) {
         this.notifyEvent(CtrlEvent.EVT_AGGRESSION, var4, 1);
      }

      super.onEvtAttacked(var1, var2);
   }

   @Override
   protected void onEvtSpawn() {
      super.onEvtSpawn();
      aW = System.currentTimeMillis() + 120000L;
   }

   @Override
   public boolean isGlobalAI() {
      return true;
   }

   @Override
   protected boolean createNewTask() {
      this.clearTasks();
      Creature var1;
      if ((var1 = this.prepareTarget()) == null) {
         return false;
      }

      NpcInstance var2 = this.getActor();
      if (var2.isDead()) {
         return false;
      }

      double var3 = var2.getDistance(var1);
      double var5 = var2.getCurrentHpPercents();
      if (this.aV == 0) {
         var2.altOnMagicUseTimer(var2, this.s_regen1);
         this.aV = 1;
      } else if (var5 < 75.0 && this.aV == 1) {
         var2.altOnMagicUseTimer(var2, this.s_regen2);
         this.aV = 2;
      } else if (var5 < 50.0 && this.aV == 2) {
         var2.altOnMagicUseTimer(var2, this.s_regen3);
         this.aV = 3;
      } else if (var5 < 30.0 && this.aV == 3) {
         var2.altOnMagicUseTimer(var2, this.s_regen3);
         this.aV = 4;
      }

      if (Config.FWA_ALLOW_ANTHARAS_MINION && aW < System.currentTimeMillis() && this.f() < 30 && Rnd.chance(5)) {
         NpcInstance var7 = Functions.spawn(Location.findPointToStay(var2.getLoc(), 400, 700, var2.getGeoIndex()), Rnd.chance(50) ? 29070 : 29069);
         this.aX.add(var7);
         AntharasManager.addSpawnedMinion(var7);
      }

      if (Rnd.chance(50)) {
         return this.chooseTaskAndTargets(Rnd.chance(50) ? this.s_antharas_ordinary_attack : this.s_antharas_ordinary_attack2, var1, var3);
      }

      HashMap var9 = new HashMap();
      switch (this.aV) {
         case 1:
            this.addDesiredSkill(var9, var1, var3, this.s_curse);
            this.addDesiredSkill(var9, var1, var3, this.s_paralyze);
            this.addDesiredSkill(var9, var1, var3, this.s_meteor);
            break;
         case 2:
            this.addDesiredSkill(var9, var1, var3, this.s_curse);
            this.addDesiredSkill(var9, var1, var3, this.s_paralyze);
            this.addDesiredSkill(var9, var1, var3, this.s_meteor);
            this.addDesiredSkill(var9, var1, var3, this.s_fear2);
            break;
         case 3:
            this.addDesiredSkill(var9, var1, var3, this.s_curse);
            this.addDesiredSkill(var9, var1, var3, this.s_paralyze);
            this.addDesiredSkill(var9, var1, var3, this.s_meteor);
            this.addDesiredSkill(var9, var1, var3, this.s_fear2);
            this.addDesiredSkill(var9, var1, var3, this.s_shock2);
            this.addDesiredSkill(var9, var1, var3, this.s_breath);
            break;
         case 4:
            this.addDesiredSkill(var9, var1, var3, this.s_curse);
            this.addDesiredSkill(var9, var1, var3, this.s_paralyze);
            this.addDesiredSkill(var9, var1, var3, this.s_meteor);
            this.addDesiredSkill(var9, var1, var3, this.s_fear2);
            this.addDesiredSkill(var9, var1, var3, this.s_shock2);
            this.addDesiredSkill(var9, var1, var3, this.s_fear);
            this.addDesiredSkill(var9, var1, var3, this.s_shock);
            this.addDesiredSkill(var9, var1, var3, this.s_breath);
      }

      Skill var8 = this.selectTopSkill(var9);
      if (var8 != null && !var8.isOffensive()) {
         var1 = var2;
      }

      return this.chooseTaskAndTargets(var8, var1, var3);
   }

   private int f() {
      int var1 = 0;

      for (NpcInstance var3 : this.aX) {
         if (var3 != null && !var3.isDead()) {
            var1++;
         }
      }

      return var1;
   }

   private Skill a(int var1, int var2) {
      return SkillTable.getInstance().getInfo(var1, var2);
   }

   @Override
   protected void onEvtDead(Creature var1) {
      if (this.aX != null && !this.aX.isEmpty()) {
         for (NpcInstance var3 : this.aX) {
            var3.deleteMe();
         }
      }

      super.onEvtDead(var1);
   }
}
