package l2.gameserver.ai;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaceManagerInstance;
import l2.gameserver.network.l2.s2c.MonRaceInfo;

public class RaceManager extends DefaultAI {
   private boolean uf = false;
   private List<Player> ug = new ArrayList<>();

   public RaceManager(NpcInstance var1) {
      super(var1);
      this.AI_TASK_ATTACK_DELAY = 5000L;
   }

   @Override
   public void runImpl() throws Exception {
      this.onEvtThink();
   }

   @Override
   protected void onEvtThink() {
      RaceManagerInstance var1 = this.getActor();
      if (var1 != null) {
         MonRaceInfo var2 = var1.getPacket();
         if (var2 != null) {
            synchronized (this) {
               if (this.uf) {
                  return;
               }

               this.uf = true;
            }

            try {
               ArrayList var3 = new ArrayList();

               for (Player var5 : World.getAroundPlayers(var1, 1200, 200)) {
                  if (var5 != null) {
                     var3.add(var5);
                     if (!this.ug.contains(var5)) {
                        var5.sendPacket(var2);
                     }

                     this.ug.remove(var5);
                  }
               }

               for (Player var12 : this.ug) {
                  var1.removeKnownPlayer(var12);
               }

               this.ug = var3;
            } finally {
               this.uf = false;
            }
         }
      }
   }

   public RaceManagerInstance getActor() {
      return (RaceManagerInstance)super.getActor();
   }
}
