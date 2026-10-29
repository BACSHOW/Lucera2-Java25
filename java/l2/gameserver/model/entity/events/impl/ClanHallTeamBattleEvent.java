package l2.gameserver.model.entity.events.impl;

import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.dao.SiegePlayerDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2.gameserver.model.entity.events.objects.CTBTeamObject;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.utils.Location;

public class ClanHallTeamBattleEvent extends SiegeEvent<ClanHall, CTBSiegeClanObject> {
   public static final String TRYOUT_PART = "tryout_part";
   public static final String CHALLENGER_RESTART_POINTS = "challenger_restart_points";
   public static final String FIRST_DOORS = "first_doors";
   public static final String SECOND_DOORS = "second_doors";
   public static final String NEXT_STEP = "next_step";

   public ClanHallTeamBattleEvent(MultiValueSet<String> var1) {
      super(var1);
   }

   @Override
   public void startEvent() {
      this._oldOwner = this.getResidence().getOwner();
      List var1 = this.getObjects("attackers");
      if (var1.isEmpty()) {
         if (this._oldOwner == null) {
            this.broadcastInZone2(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(this.getResidence()));
         } else {
            this.broadcastInZone2(
               new SystemMessage2(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addResidenceName(this.getResidence())
            );
         }

         this.reCalcNextTime(false);
      } else {
         if (this._oldOwner != null) {
            this.addObject("defenders", new SiegeClanObject("defenders", this._oldOwner, 0L));
         }

         SiegeClanDAO.getInstance().delete(this.getResidence());
         SiegePlayerDAO.getInstance().delete(this.getResidence());
         List var2 = this.getObjects("tryout_part");

         for (int var3 = 0; var3 < 5; var3++) {
            CTBTeamObject var4 = (CTBTeamObject)var2.get(var3);
            var4.setSiegeClan((l2.gameserver.model.entity.events.objects.CTBSiegeClanObject)CollectionUtils.safeGet(var1, var3));
         }

         this.broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(this.getResidence()), "attackers", "defenders");
         this.broadcastTo(SystemMsg.THE_TRYOUTS_ARE_ABOUT_TO_BEGIN, "attackers");
         super.startEvent();
      }
   }

   public void nextStep() {
      this.broadcastTo(SystemMsg.THE_TRYOUTS_HAVE_BEGUN, "attackers", "defenders");
      this.updateParticles(true, "attackers", "defenders");
   }

   public void processStep(CTBTeamObject var1) {
      if (var1.getSiegeClan() != null) {
         CTBSiegeClanObject var2 = var1.getSiegeClan();
         var2.setEvent(false, this);
         this.teleportPlayers("spectators");
      }

      var1.despawnObject(this);
      List var7 = this.getObjects("tryout_part");
      boolean var3 = false;
      CTBTeamObject var4 = null;

      for (Object __raw_var6_83 : (Iterable<?>)(Object)(var7)) {
         CTBTeamObject var6 = (CTBTeamObject)__raw_var6_83;
         if (var6.isParticle()) {
            var3 = var4 == null;
            var4 = var6;
         }
      }

      if (var3) {
         CTBSiegeClanObject var8 = var4.getSiegeClan();
         if (var8 != null) {
            this.getResidence().changeOwner(var8.getClan());
         }

         this.stopEvent(true);
      }
   }

   @Override
   public void announce(int var1) {
      int var2 = var1 / 60;
      if (var2 > 0) {
         this.broadcastTo(new SystemMessage2(SystemMsg.THE_CONTEST_WILL_BEGIN_IN_S1_MINUTES).addInteger(var2), "attackers", "defenders");
      } else {
         this.broadcastTo(new SystemMessage2(SystemMsg.THE_PRELIMINARY_MATCH_WILL_BEGIN_IN_S1_SECONDS).addInteger(var1), "attackers", "defenders");
      }
   }

   @Override
   public void stopEvent(boolean var1) {
      Clan var2 = this.getResidence().getOwner();
      if (var2 != null) {
         if (this._oldOwner != var2) {
            var2.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);
            var2.incReputation(1700, false, this.toString());
         }

         this.broadcastTo(
            new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(var2.getName()).addResidenceName(this.getResidence()), "attackers", "defenders"
         );
         this.broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(this.getResidence()), "attackers", "defenders");
      } else {
         this.broadcastTo(new SystemMessage2(SystemMsg.THE_PRELIMINARY_MATCH_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(this.getResidence()), "attackers");
      }

      this.updateParticles(false, "attackers", "defenders");
      this.removeObjects("defenders");
      this.removeObjects("attackers");
      super.stopEvent(var1);
      this._oldOwner = null;
   }

   @Override
   public void loadSiegeClans() {
      List var1 = SiegeClanDAO.getInstance().load(this.getResidence(), "attackers");
      this.addObjects("attackers", var1);

      for (Object __raw_var4_139 : (Iterable<?>)(Object)(this.getObjects("attackers"))) {
         CTBSiegeClanObject var4 = (CTBSiegeClanObject)__raw_var4_139;
         var4.select(this.getResidence());
      }
   }

   public CTBSiegeClanObject newSiegeClan(String var1, int var2, long var3, long var5) {
      Clan var7 = ClanTable.getInstance().getClan(var2);
      return var7 == null ? null : new CTBSiegeClanObject(var1, var7, var3, var5);
   }

   @Override
   public boolean isParticle(Player var1) {
      if (this.isInProgress() && var1.getClan() != null) {
         CTBSiegeClanObject var2 = this.getSiegeClan("attackers", var1.getClan());
         return var2 != null && var2.getPlayers().contains(var1.getObjectId());
      } else {
         return false;
      }
   }

   @Override
   public Location getRestartLoc(Player var1, RestartType var2) {
      if (!this.checkIfInZone(var1)) {
         return null;
      }

      SiegeClanObject var3 = this.getSiegeClan("attackers", var1.getClan());
      Location var4 = null;
      switch (var2) {
         case TO_VILLAGE:
            if (var3 != null && this.checkIfInZone(var1)) {
               List var5 = this.getObjects("attackers");
               List var6 = this.getObjects("challenger_restart_points");
               int var7 = var5.indexOf(var3);
               var4 = (Location)var6.get(var7);
            }
         default:
            return var4;
      }
   }

   @Override
   public void action(String var1, boolean var2) {
      if (var1.equalsIgnoreCase("next_step")) {
         this.nextStep();
      } else {
         super.action(var1, var2);
      }
   }

   @Override
   public int getUserRelation(Player var1, int var2) {
      return var2;
   }

   @Override
   public int getRelation(Player var1, Player var2, int var3) {
      return var3;
   }
}
