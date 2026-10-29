package npc.model.residences.clanhall;

import java.util.HashMap;
import java.util.Map.Entry;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.AggroList.HateInfo;
import l2.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.SiegeGuardInstance;

public class LidiaVonHellmannInstance extends SiegeGuardInstance {
   public LidiaVonHellmannInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
   }

   @Override
   public void onDeath(Creature var1) {
      SiegeEvent var2 = this.getEvent(SiegeEvent.class);
      if (var2 != null) {
         var2.processStep(this.getMostDamagedClan());
         super.onDeath(var1);
      }
   }

   public Clan getMostDamagedClan() {
      ClanHallSiegeEvent var1 = this.getEvent(ClanHallSiegeEvent.class);
      Player var2 = null;
      HashMap var3 = new HashMap();

      for (HateInfo var5 : this.getAggroList().getPlayableMap().values()) {
         Playable var6 = (Playable)var5.attacker;
         int var7 = var5.damage;
         if (var6.isPet() || var6.isSummon()) {
            var2 = var6.getPlayer();
         } else if (var6.isPlayer()) {
            var2 = (Player)var6;
         }

         if (var2 != null && var1.getSiegeClan("attackers", var2.getClan()) != null) {
            if (!var3.containsKey(var2)) {
               var3.put(var2, var7);
            } else {
               int var8 = (Integer)var3.get(var2) + var7;
               var3.put(var2, var8);
            }
         }
      }

      int var10 = 0;
      Player var11 = null;

      for (Object __raw_var13_56 : (Iterable<?>)(Object)(var3.entrySet())) {
         Entry var13 = (Entry)__raw_var13_56;
         int var14 = (Integer)var13.getValue();
         Player var9 = (Player)var13.getKey();
         if (var14 > var10) {
            var10 = var14;
            var11 = var9;
         }
      }

      return var11 == null ? null : var11.getClan();
   }

   @Override
   public boolean isEffectImmune() {
      return true;
   }
}
