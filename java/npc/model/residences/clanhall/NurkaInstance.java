package npc.model.residences.clanhall;

import java.util.HashMap;
import java.util.Map.Entry;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.AggroList.HateInfo;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.SiegeGuardInstance;

public class NurkaInstance extends SiegeGuardInstance {
   public static final Skill SKILL = SkillTable.getInstance().getInfo(5456, 1);

   public NurkaInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
   }

   @Override
   public void reduceCurrentHp(
      double var1, Creature var3, Skill var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9, boolean var10, boolean var11
   ) {
      if (var3.getLevel() > this.getLevel() + 8 && var3.getEffectList().getEffectsCountForSkill(SKILL.getId()) == 0) {
         this.doCast(SKILL, var3, false);
      } else {
         super.reduceCurrentHp(var1, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   @Override
   public void onDeath(Creature var1) {
      SiegeEvent var2 = this.getEvent(SiegeEvent.class);
      if (var2 != null) {
         var2.processStep(this.getMostDamagedClan());
         super.onDeath(var1);
         this.deleteMe();
      }
   }

   public Clan getMostDamagedClan() {
      Player var1 = null;
      HashMap var2 = new HashMap();

      for (HateInfo var4 : this.getAggroList().getPlayableMap().values()) {
         Playable var5 = (Playable)var4.attacker;
         int var6 = var4.damage;
         if (var5.isPet() || var5.isSummon()) {
            var1 = var5.getPlayer();
         } else if (var5.isPlayer()) {
            var1 = (Player)var5;
         }

         if (var1 != null && var1.getClan() != null && var1.getClan().getHasHideout() <= 0) {
            if (!var2.containsKey(var1)) {
               var2.put(var1, var6);
            } else {
               int var7 = (Integer)var2.get(var1) + var6;
               var2.put(var1, var7);
            }
         }
      }

      int var9 = 0;
      Player var10 = null;

      for (Object __raw_var12_70 : (Iterable<?>)(Object)(var2.entrySet())) {
         Entry var12 = (Entry)__raw_var12_70;
         int var13 = (Integer)var12.getValue();
         Player var8 = (Player)var12.getKey();
         if (var13 > var9) {
            var9 = var13;
            var10 = var8;
         }
      }

      return var10 == null ? null : var10.getClan();
   }

   @Override
   public boolean isEffectImmune() {
      return true;
   }
}
