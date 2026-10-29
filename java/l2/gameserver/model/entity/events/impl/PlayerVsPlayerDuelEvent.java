package l2.gameserver.model.entity.events.impl;

import java.util.List;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExDuelAskStart;
import l2.gameserver.network.l2.s2c.ExDuelEnd;
import l2.gameserver.network.l2.s2c.ExDuelReady;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class PlayerVsPlayerDuelEvent extends DuelEvent {
   public PlayerVsPlayerDuelEvent(MultiValueSet<String> var1) {
      super(var1);
   }

   protected PlayerVsPlayerDuelEvent(int var1, String var2) {
      super(var1, var2);
   }

   @Override
   public boolean canDuel(Player var1, Player var2, boolean var3) {
      IStaticPacket var4 = this.canDuel0(var1, var2);
      if (var4 != null) {
         var1.sendPacket(var4);
         return false;
      } else {
         var4 = this.canDuel0(var2, var1);
         if (var4 != null) {
            var1.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
            return false;
         } else {
            return true;
         }
      }
   }

   @Override
   public void askDuel(Player var1, Player var2) {
      Request var3 = new Request(Request.L2RequestType.DUEL, var1, var2).setTimeout(10000L);
      var3.set("duelType", 0);
      var1.setRequest(var3);
      var2.setRequest(var3);
      var1.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_BEEN_CHALLENGED_TO_A_DUEL).addName(var2));
      var2.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_CHALLENGED_YOU_TO_A_DUEL).addName(var1), new ExDuelAskStart(var1.getName(), 0));
   }

   @Override
   public void createDuel(Player var1, Player var2) {
      PlayerVsPlayerDuelEvent var3 = new PlayerVsPlayerDuelEvent(this.getDuelType(), var1.getObjectId() + "_" + var2.getObjectId() + "_duel");
      this.cloneTo(var3);
      var3.addObject(BLUE_TEAM, new DuelSnapshotObject(var1, TeamType.BLUE));
      var3.addObject(RED_TEAM, new DuelSnapshotObject(var2, TeamType.RED));
      var3.sendPacket(new ExDuelReady(this));
      var3.reCalcNextTime(false);
   }

   @Override
   public void stopEvent() {
      this.clearActions();
      if (this._duelState.compareAndSet(DuelEvent.DuelState.EInProgress, DuelEvent.DuelState.EEnd)) {
         this.updatePlayers(false, false);

         for (DuelSnapshotObject var2 : this) {
            var2.getPlayer().sendPacket(new ExDuelEnd(this));
            GameObject var3 = var2.getPlayer().getTarget();
            if (var3 != null) {
               var2.getPlayer().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, var3);
            }
         }

         switch (this._winner) {
            case NONE:
               this.sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
               break;
            case RED:
            case BLUE:
               List var6 = this.getObjects(this._winner.name());
               List var7 = this.getObjects(this._winner.revert().name());
               this.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_WON_THE_DUEL).addName(((DuelSnapshotObject)var6.get(0)).getPlayer()));

               for (Object __raw_var4_89 : (Iterable<?>)(Object)(var7)) {
                  DuelSnapshotObject var4 = (DuelSnapshotObject)__raw_var4_89;
                  Player var5 = var4.getPlayer();
                  if (var5 != null) {
                     var5.broadcastPacket(new SocialAction(var4.getPlayer().getObjectId(), 7));
                  }
               }
         }
      }

      this.removeObjects(RED_TEAM);
      this.removeObjects(BLUE_TEAM);
   }

   @Override
   public void onDie(Player var1) {
      TeamType var2 = var1.getTeam();
      if (var2 != TeamType.NONE && !this._aborted) {
         var1.stopAttackStanceTask();
         boolean var3 = true;

         for (Object __raw_var6_109 : (Iterable<?>)(Object)(this.getObjects(var2.name()))) {
            DuelSnapshotObject var6 = (DuelSnapshotObject)__raw_var6_109;
            if (var6.getPlayer() == var1) {
               var6.setDead();
            }

            if (!var6.isDead()) {
               var3 = false;
            }
         }

         if (var3) {
            this._winner = var2.revert();
            this.stopEvent();
         }
      }
   }

   @Override
   public int getDuelType() {
      return 0;
   }

   @Override
   public void playerExit(Player var1) {
      if (this._winner == TeamType.NONE && !this._aborted) {
         this._winner = var1.getTeam().revert();
         this._aborted = false;
         this.stopEvent();
      }
   }

   @Override
   public void packetSurrender(Player var1) {
      this.playerExit(var1);
   }

   @Override
   protected long startTimeMillis() {
      return System.currentTimeMillis() + 5000L;
   }
}
