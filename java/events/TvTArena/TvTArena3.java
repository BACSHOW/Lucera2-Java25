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

public class TvTArena3 extends Functions implements OnDeathListener, OnPlayerExitListener, OnTeleportListener, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(TvTArena3.class);
   private static TvTTemplate kB;
   private List<NpcInstance> _spawns = new ArrayList<>();

   public static TvTTemplate getInstance() {
      if (kB == null) {
         kB = new TvTArena3.TvTArena3Impl();
      }

      return kB;
   }

   @Override
   public void onLoad() {
      CharListenerList.addGlobal(this);
      getInstance().onLoad();
      if (this.isActive()) {
         this.spawnEventManagers();
         _log.info("Loaded Event: TvT Arena 3 [state: activated]");
      } else {
         _log.debug("Loaded Event: TvT Arena 3 [state: deactivated]");
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
      this.onReload();
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

   public String DialogAppend_31392(Integer var1) {
      if (var1 == 0) {
         Player var2 = this.getSelf();
         return var2.isGM()
            ? HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31392.htm", var2)
               + HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31392-4.htm", var2)
            : HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31392.htm", var2);
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
      return IsActive("TvT Arena 3");
   }

   public void startEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive("TvT Arena 3", true)) {
            this.spawnEventManagers();
            System.out.println("Event: TvT Arena 3 started.");
            Announcements.getInstance().announceToAll("Начался TvT Arena 3 эвент.");
         } else {
            var1.sendMessage("TvT Arena 3 Event already started.");
         }

         this.show("admin/events/events.htm", var1);
      }
   }

   public void stopEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive("TvT Arena 3", false)) {
            ServerVariables.unset("TvT Arena 3");
            this.unSpawnEventManagers();
            this.stop();
            System.out.println("TvT Arena 3 Event stopped.");
            Announcements.getInstance().announceToAll("TvT Arena 3 эвент окончен.");
         } else {
            var1.sendMessage("TvT Arena 3 Event not started.");
         }

         this.show("admin/events/events.htm", var1);
      }
   }

   private void spawnEventManagers() {
      int[][] var1 = new int[][]{{82840, 148936, -3472, 0}};
      NpcTemplate var2 = NpcHolder.getInstance().getTemplate(31392);

      for (int[] var6 : var1) {
         SimpleSpawner var7 = new SimpleSpawner(var2);
         var7.setLocx(var6[0]);
         var7.setLocy(var6[1]);
         var7.setLocz(var6[2]);
         var7.setHeading(var6[3]);
         NpcInstance var8 = var7.doSpawn(true);
         var8.setName("Arena 3");
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

   private static class TvTArena3Impl extends TvTTemplate {
      private TvTArena3Impl() {
      }

      @Override
      protected void onLoad() {
         this._managerId = 31392;
         this._className = "TvTArena3";
         this._status = 0;
         this._team1list = new CopyOnWriteArrayList<>();
         this._team2list = new CopyOnWriteArrayList<>();
         this._team1live = new CopyOnWriteArrayList<>();
         this._team2live = new CopyOnWriteArrayList<>();
         this._zoneListener = new TvTTemplate.ZoneListener();
         this._zone = ReflectionUtils.getZone("[tvt_arena3]");
         this._zone.addListener(this._zoneListener);
         this._team1points = new ArrayList<>();
         this._team2points = new ArrayList<>();
         this._team1points.add(new Location(-79383, -52724, -11518, -11418));
         this._team1points.add(new Location(-79558, -52793, -11518, -11418));
         this._team1points.add(new Location(-79726, -52867, -11518, -11418));
         this._team1points.add(new Location(-79911, -52845, -11518, -11418));
         this._team1points.add(new Location(-80098, -52822, -11518, -11418));
         this._team1points.add(new Location(-80242, -52714, -11518, -11418));
         this._team1points.add(new Location(-80396, -52597, -11518, -11418));
         this._team1points.add(new Location(-80466, -52422, -11518, -11418));
         this._team1points.add(new Location(-80544, -52250, -11518, -11418));
         this._team1points.add(new Location(-80515, -52054, -11518, -11418));
         this._team1points.add(new Location(-80496, -51878, -11518, -11418));
         this._team1points.add(new Location(-80386, -51739, -11518, -11418));
         this._team2points.add(new Location(-80270, -51582, -11518, -11418));
         this._team2points.add(new Location(-80107, -51519, -11518, -11418));
         this._team2points.add(new Location(-79926, -51435, -11518, -11418));
         this._team2points.add(new Location(-79739, -51465, -11518, -11418));
         this._team2points.add(new Location(-79554, -51482, -11518, -11418));
         this._team2points.add(new Location(-79399, -51600, -11518, -11418));
         this._team2points.add(new Location(-79254, -51711, -11518, -11418));
         this._team2points.add(new Location(-79181, -51884, -11518, -11418));
         this._team2points.add(new Location(-79114, -52057, -11518, -11418));
         this._team2points.add(new Location(-79133, -52246, -11518, -11418));
         this._team2points.add(new Location(-79156, -52427, -11518, -11418));
         this._team2points.add(new Location(-79275, -52583, -11518, -11418));
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
