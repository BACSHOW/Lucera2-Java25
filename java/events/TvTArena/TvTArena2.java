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

public class TvTArena2 extends Functions implements OnDeathListener, OnPlayerExitListener, OnTeleportListener, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(TvTArena2.class);
   private static TvTTemplate kB;
   private List<NpcInstance> _spawns = new ArrayList<>();

   public static TvTTemplate getInstance() {
      if (kB == null) {
         kB = new TvTArena2.TvTArena2Impl();
      }

      return kB;
   }

   @Override
   public void onLoad() {
      CharListenerList.addGlobal(this);
      getInstance().onLoad();
      if (this.isActive()) {
         this.spawnEventManagers();
         _log.info("Loaded Event: TvT Arena 2 [state: activated]");
      } else {
         _log.debug("Loaded Event: TvT Arena 2 [state: deactivated]");
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

   public String DialogAppend_31391(Integer var1) {
      if (var1 == 0) {
         Player var2 = this.getSelf();
         return var2.isGM()
            ? HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31391.htm", var2)
               + HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31391-4.htm", var2)
            : HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31391.htm", var2);
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
      return IsActive("TvT Arena 2");
   }

   public void startEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive("TvT Arena 2", true)) {
            this.spawnEventManagers();
            System.out.println("Event: TvT Arena 2 started.");
            Announcements.getInstance().announceToAll("Начался TvT Arena 2 эвент.");
         } else {
            var1.sendMessage("TvT Arena 2 Event already started.");
         }

         this.show("admin/events/events.htm", var1);
      }
   }

   public void stopEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive("TvT Arena 2", false)) {
            ServerVariables.unset("TvT Arena 2");
            this.unSpawnEventManagers();
            this.stop();
            System.out.println("TvT Arena 2 Event stopped.");
            Announcements.getInstance().announceToAll("TvT Arena 2 эвент окончен.");
         } else {
            var1.sendMessage("TvT Arena 2 Event not started.");
         }

         this.show("admin/events/events.htm", var1);
      }
   }

   private void spawnEventManagers() {
      int[][] var1 = new int[][]{{82840, 149048, -3472, 0}};
      NpcTemplate var2 = NpcHolder.getInstance().getTemplate(31391);

      for (int[] var6 : var1) {
         SimpleSpawner var7 = new SimpleSpawner(var2);
         var7.setLocx(var6[0]);
         var7.setLocy(var6[1]);
         var7.setLocz(var6[2]);
         var7.setHeading(var6[3]);
         NpcInstance var8 = var7.doSpawn(true);
         var8.setName("Arena 2");
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

   private static class TvTArena2Impl extends TvTTemplate {
      private TvTArena2Impl() {
      }

      @Override
      protected void onLoad() {
         this._managerId = 31391;
         this._className = "TvTArena2";
         this._status = 0;
         this._team1list = new CopyOnWriteArrayList<>();
         this._team2list = new CopyOnWriteArrayList<>();
         this._team1live = new CopyOnWriteArrayList<>();
         this._team2live = new CopyOnWriteArrayList<>();
         this._zoneListener = new TvTTemplate.ZoneListener();
         this._zone = ReflectionUtils.getZone("[tvt_arena2]");
         this._zone.addListener(this._zoneListener);
         this._team1points = new ArrayList<>();
         this._team2points = new ArrayList<>();
         this._team1points.add(new Location(-77724, -47901, -11518, -11418));
         this._team1points.add(new Location(-77718, -48080, -11518, -11418));
         this._team1points.add(new Location(-77699, -48280, -11518, -11418));
         this._team1points.add(new Location(-77777, -48442, -11518, -11418));
         this._team1points.add(new Location(-77863, -48622, -11518, -11418));
         this._team1points.add(new Location(-78002, -48714, -11518, -11418));
         this._team1points.add(new Location(-78168, -48835, -11518, -11418));
         this._team1points.add(new Location(-78353, -48851, -11518, -11418));
         this._team1points.add(new Location(-78543, -48864, -11518, -11418));
         this._team1points.add(new Location(-78709, -48784, -11518, -11418));
         this._team1points.add(new Location(-78881, -48702, -11518, -11418));
         this._team1points.add(new Location(-78981, -48555, -11518, -11418));
         this._team2points.add(new Location(-79097, -48400, -11518, -11418));
         this._team2points.add(new Location(-79107, -48214, -11518, -11418));
         this._team2points.add(new Location(-79125, -48027, -11518, -11418));
         this._team2points.add(new Location(-79047, -47861, -11518, -11418));
         this._team2points.add(new Location(-78965, -47689, -11518, -11418));
         this._team2points.add(new Location(-78824, -47594, -11518, -11418));
         this._team2points.add(new Location(-78660, -47474, -11518, -11418));
         this._team2points.add(new Location(-78483, -47456, -11518, -11418));
         this._team2points.add(new Location(-78288, -47440, -11518, -11418));
         this._team2points.add(new Location(-78125, -47515, -11518, -11418));
         this._team2points.add(new Location(-77953, -47599, -11518, -11418));
         this._team2points.add(new Location(-77844, -47747, -11518, -11418));
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
