package events.TvTArena;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.gameserver.Announcements;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TvTArena1 extends Functions implements OnDeathListener, OnPlayerExitListener, OnTeleportListener, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(TvTArena1.class);
   private static TvTTemplate kB;
   private List<NpcInstance> _spawns = new ArrayList<>();

   public static TvTTemplate getInstance() {
      if (kB == null) {
         kB = new TvTArena1.TvTArena1Impl();
      }

      return kB;
   }

   @Override
   public void onLoad() {
      CharListenerList.addGlobal(this);
      getInstance().onLoad();
      if (this.isActive()) {
         this.spawnEventManagers();
         _log.info("Loaded Event: TvT Arena 1 [state: activated]");
      } else {
         _log.debug("Loaded Event: TvT Arena 1 [state: deactivated]");
      }
   }

   @Override
   public void onReload() {
      getInstance().onReload();
      this.unSpawnEventManagers();
      kB = null;
   }

   @Override
   public void onShutdown() {
   }

   @Override
   public void onDeath(Creature var1, Creature var2) {
      getInstance().onDeath(var1, var2);
   }

   @Override
   public void onPlayerExit(Player var1) {
      getInstance().onPlayerExit(var1);
   }

   @Override
   public void onTeleport(Player var1, int var2, int var3, int var4, Reflection var5) {
      getInstance().onTeleport(var1);
   }

   public String DialogAppend_31390(Integer var1) {
      if (var1 == 0) {
         Player var2 = this.getSelf();
         return var2.isGM()
            ? HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31390.htm", var2)
               + HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31390-4.htm", var2)
            : HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31390.htm", var2);
      } else {
         return "";
      }
   }

   public void create1() {
      getInstance().template_create1(this.getSelf());
   }

   public void register() {
      getInstance().template_register(this.getSelf());
   }

   public void check1(String[] var1) {
      getInstance().template_check1(this.getSelf(), this.getNpc(), var1);
   }

   public void register_check() {
      getInstance().template_register_check(this.getSelf());
   }

   public void stop() {
      getInstance().template_stop();
   }

   public void announce() {
      getInstance().template_announce();
   }

   public void prepare() {
      getInstance().template_prepare();
   }

   public void start() {
      getInstance().template_start();
   }

   public void timeOut() {
      getInstance().template_timeOut();
   }

   private boolean isActive() {
      return IsActive("TvT Arena 1");
   }

   public void startEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive("TvT Arena 1", true)) {
            this.spawnEventManagers();
            System.out.println("Event: TvT Arena 1 started.");
            Announcements.getInstance().announceToAll("Начался TvT Arena 1 эвент.");
         } else {
            var1.sendMessage("TvT Arena 1 Event already started.");
         }

         this.show("admin/events/events.htm", var1);
      }
   }

   public void stopEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive("TvT Arena 1", false)) {
            ServerVariables.unset("TvT Arena 1");
            this.unSpawnEventManagers();
            this.stop();
            System.out.println("TvT Arena 1 Event stopped.");
            Announcements.getInstance().announceToAll("TvT Arena 1 эвент окончен.");
         } else {
            var1.sendMessage("TvT Arena 1 Event not started.");
         }

         this.show("admin/events/events.htm", var1);
      }
   }

   private void spawnEventManagers() {
      int[][] var1 = new int[][]{{82840, 149167, -3495, 0}};
      NpcTemplate var2 = NpcHolder.getInstance().getTemplate(31390);

      for (int[] var6 : var1) {
         SimpleSpawner var7 = new SimpleSpawner(var2);
         var7.setLocx(var6[0]);
         var7.setLocy(var6[1]);
         var7.setLocz(var6[2]);
         var7.setHeading(var6[3]);
         NpcInstance var8 = var7.doSpawn(true);
         var8.setName("Arena 1");
         var8.setTitle("TvT Event");
         this._spawns.add(var8);
      }
   }

   private void unSpawnEventManagers() {
      for (NpcInstance var2 : this._spawns) {
         var2.deleteMe();
      }

      this._spawns.clear();
   }

   private static class TvTArena1Impl extends TvTTemplate {
      private TvTArena1Impl() {
      }

      @Override
      protected void onLoad() {
         this._managerId = 31390;
         this._className = "TvTArena1";
         this._status = 0;
         this._team1list = new CopyOnWriteArrayList<>();
         this._team2list = new CopyOnWriteArrayList<>();
         this._team1live = new CopyOnWriteArrayList<>();
         this._team2live = new CopyOnWriteArrayList<>();
         this._zoneListener = new TvTTemplate.ZoneListener();
         this._zone = ReflectionUtils.getZone("[tvt_arena1]");
         this._zone.addListener(this._zoneListener);
         this._team1points = new ArrayList<>();
         this._team2points = new ArrayList<>();
         this._team1points.add(new Location(-81806, -44865, -11418));
         this._team1points.add(new Location(-81617, -44893, -11418));
         this._team1points.add(new Location(-81440, -44945, -11418));
         this._team1points.add(new Location(-81301, -48066, -11418));
         this._team1points.add(new Location(-81168, -45208, -11418));
         this._team1points.add(new Location(-81114, -46379, -11418));
         this._team1points.add(new Location(-81068, -45570, -11418));
         this._team1points.add(new Location(-81114, -45728, -11418));
         this._team1points.add(new Location(-81162, -45934, -11418));
         this._team1points.add(new Location(-81280, -46045, -11418));
         this._team1points.add(new Location(-81424, -46196, -11418));
         this._team1points.add(new Location(-81578, -46238, -11418));
         this._team2points.add(new Location(-81792, -46299, -11418));
         this._team2points.add(new Location(-81959, -46247, -11418));
         this._team2points.add(new Location(-82147, -46206, -11418));
         this._team2points.add(new Location(-82256, -46093, -11418));
         this._team2points.add(new Location(-82418, -45940, -11418));
         this._team2points.add(new Location(-82455, -45779, -11418));
         this._team2points.add(new Location(-82513, -45573, -11418));
         this._team2points.add(new Location(-82464, -45499, -11418));
         this._team2points.add(new Location(-82421, -45215, -11418));
         this._team2points.add(new Location(-82308, -45106, -11418));
         this._team2points.add(new Location(-82160, -44948, -11418));
         this._team2points.add(new Location(-81978, -44904, -11418));
      }

      @Override
      protected void onReload() {
         if (this._status > 0) {
            this.template_stop();
         }

         this._zone.removeListener(this._zoneListener);
      }
   }
}
