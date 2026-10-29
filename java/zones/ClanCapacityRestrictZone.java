package zones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClanCapacityRestrictZone extends Functions implements ScriptFile {
   private static final Logger sQ = LoggerFactory.getLogger(ClanCapacityRestrictZone.class);
   private static final ClanCapacityRestrictZone bZz = new ClanCapacityRestrictZone();
   private static final String bZA = "restrictInsideClanCount";
   private static final String bZB = "restrictInsidePerClanPlayerCount";
   private static final String bZC = "restrictInsidePerAlliancePlayerCount";
   private static final String bZD = "restrictClanCapacityBackLoc";
   private volatile boolean bZu = false;
   private final List<ClanCapacityRestrictZone.ClanCapacityRestrictZoneListener> bZv = new ArrayList<>();

   public void init() {
      if (!this.bZu) {
         for (Zone var2 : ReflectionManager.DEFAULT.getZones()) {
            boolean var3 = var2.getParams().isSet("restrictInsideClanCount");
            boolean var4 = var2.getParams().isSet("restrictInsidePerClanPlayerCount");
            boolean var5 = var2.getParams().isSet("restrictInsidePerAlliancePlayerCount");
            if (var3 || var4 || var5) {
               int var6 = var2.getParams().getInteger("restrictInsideClanCount", Integer.MAX_VALUE);
               int var7 = var2.getParams().getInteger("restrictInsidePerClanPlayerCount", Integer.MAX_VALUE);
               int var8 = var2.getParams().getInteger("restrictInsidePerAlliancePlayerCount", Integer.MAX_VALUE);
               Location var9 = var2.getParams().isSet("restrictClanCapacityBackLoc")
                  ? Location.parseLoc(var2.getParams().getString("restrictClanCapacityBackLoc"))
                  : null;
               if (var9 == null) {
                  sQ.warn("Capacity Clan restricted zone {} have no back loc. Will use closest town.", var2);
               }

               this.bZv.add(new ClanCapacityRestrictZone.ClanCapacityRestrictZoneListener(var6, var7, var8, var9).addTo(var2));
            }
         }

         this.bZu = true;
         sQ.info("Player limit zone : Loaded {} capacity restricted zones.", this.bZv.size());
      }
   }

   private void done() throws Exception {
      if (this.bZu) {
         for (ClanCapacityRestrictZone.ClanCapacityRestrictZoneListener var2 : this.bZv) {
            var2.remove();
         }

         this.bZv.clear();
         this.bZu = false;
      }
   }

   @Override
   public void onLoad() {
      try {
         bZz.init();
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }

   @Override
   public void onReload() {
      this.onShutdown();
      this.onLoad();
   }

   @Override
   public void onShutdown() {
      try {
         bZz.done();
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }

   private static class ClanCapacityRestrictZoneListener implements OnZoneEnterLeaveListener {
      private final int bZE;
      private final int bZF;
      private final int bZG;
      private final Location bZy;
      private Zone Cb;

      private ClanCapacityRestrictZoneListener(int var1, int var2, int var3, Location var4) {
         this.bZE = var1;
         this.bZF = var2;
         this.bZG = var3;
         this.bZy = var4;
      }

      public ClanCapacityRestrictZone.ClanCapacityRestrictZoneListener addTo(Zone var1) {
         var1.addListener(this);
         this.Cb = var1;
         return this;
      }

      public boolean remove() {
         return this.Cb.removeListener(this);
      }

      private void aP(Player var1) {
         HardReference var2 = var1.getPlayer().getRef();
         ThreadPoolManager.getInstance().execute(() -> {
            Player var2x = (Player)var2.get();
            if (var2x != null) {
               if (this.bZy == null) {
                  var2x.teleToClosestTown();
               } else {
                  var2x.teleToLocation(this.bZy);
               }
            }
         });
      }

      private int c(Zone var1) {
         HashSet var2 = new HashSet();

         for (Player var4 : var1.getInsidePlayers()) {
            Clan var5 = var4.getClan();
            if (var5 == null) {
               this.aP(var4);
            } else {
               var2.add(var5);
            }
         }

         return var2.size();
      }

      private int a(Zone var1, Clan var2) {
         int var3 = 0;
         if (var2 != null) {
            for (Player var5 : var1.getInsidePlayers()) {
               Clan var6 = var5.getClan();
               if (var6 == null) {
                  this.aP(var5);
               } else if (var2 == var6) {
                  var3++;
               }
            }
         }

         return var3;
      }

      private int a(Zone var1, Alliance var2) {
         int var3 = 0;
         if (var2 != null) {
            for (Player var5 : var1.getInsidePlayers()) {
               Clan var6 = var5.getClan();
               if (var6 == null) {
                  this.aP(var5);
               } else if (var6.getAlliance() == var2) {
                  var3++;
               }
            }
         }

         return var3;
      }

      @Override
      public void onZoneEnter(Zone var1, Creature var2) {
         if (var2.isPlayer() && !var2.getPlayer().isGM()) {
            Player var3 = var2.getPlayer();
            Clan var4 = var3.getClan();
            if (var4 == null) {
               String var6 = new CustomMessage("zone.services.ClanCapacityRestrictZone.NoClan", var3).toString();
               var3.sendPacket(new ExShowScreenMessage(var6, 10000, ScreenMessageAlign.TOP_CENTER, false));
               var3.sendMessage(var6);
               this.aP(var3);
            } else if (this.c(var1) > this.bZE || this.a(var1, var4) > this.bZF || this.a(var1, var4.getAlliance()) > this.bZG) {
               String var5 = new CustomMessage("zone.services.ClanCapacityRestrictZone.CountLimit", var3).toString();
               var3.sendPacket(new ExShowScreenMessage(var5, 10000, ScreenMessageAlign.TOP_CENTER, false));
               var3.sendMessage(var5);
               this.aP(var3);
            }
         }
      }

      @Override
      public void onZoneLeave(Zone var1, Creature var2) {
      }
   }
}
