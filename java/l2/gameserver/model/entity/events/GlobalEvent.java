package l2.gameserver.model.entity.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.collections.MultiValueSet;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.logging.LoggerObject;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.event.OnStartStopListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.objects.DoorObject;
import l2.gameserver.model.entity.events.objects.InitableObject;
import l2.gameserver.model.entity.events.objects.SpawnableObject;
import l2.gameserver.model.entity.events.objects.ZoneObject;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.taskmanager.actionrunner.ActionRunner;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.TimeUtils;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.IntObjectMap.Entry;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

public abstract class GlobalEvent extends LoggerObject {
   public static final String EVENT = "event";
   protected final IntObjectMap<List<EventAction>> _onTimeActions = new TreeIntObjectMap();
   protected final List<EventAction> _onStartActions = new ArrayList<>(0);
   protected final List<EventAction> _onStopActions = new ArrayList<>(0);
   protected final List<EventAction> _onInitActions = new ArrayList<>(0);
   protected final Map<String, List<Serializable>> _objects = new HashMap<>(0);
   protected final int _id;
   protected final String _name;
   protected final String _timerName;
   protected final GlobalEvent.ListenerListImpl _listenerList = new GlobalEvent.ListenerListImpl();
   protected IntObjectMap<ItemInstance> _banishedItems = Containers.emptyIntObjectMap();

   protected GlobalEvent(MultiValueSet<String> var1) {
      this(var1.getInteger("id"), var1.getString("name"));
   }

   protected GlobalEvent(int var1, String var2) {
      this._id = var1;
      this._name = var2;
      this._timerName = var1 + "_" + var2.toLowerCase().replace(" ", "_");
   }

   public void initEvent() {
      this.callActions(this._onInitActions);
      this.reCalcNextTime(true);
      this.printInfo();
   }

   public void startEvent() {
      this.callActions(this._onStartActions);
      this._listenerList.onStart();
   }

   public void stopEvent() {
      this.callActions(this._onStopActions);
      this._listenerList.onStop();
   }

   protected void printInfo() {
      // Se for leilão comum de Clan Hall, não polui o console com 44 linhas repetidas
      if (this.getClass().getSimpleName().contains("ClanHallAuction")) {
         this.debug(this.getName() + " time - " + TimeUtils.toSimpleFormat(this.startTimeMillis()));
      } else {
         this.info(this.getName() + " time - " + TimeUtils.toSimpleFormat(this.startTimeMillis()));
      }
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "[" + this.getId() + ";" + this.getName() + "]";
   }

   protected void callActions(List<EventAction> var1) {
      for (EventAction var3 : var1) {
         var3.call(this);
      }
   }

   public void addOnStartActions(List<EventAction> var1) {
      this._onStartActions.addAll(var1);
   }

   public void addOnStopActions(List<EventAction> var1) {
      this._onStopActions.addAll(var1);
   }

   public void addOnInitActions(List<EventAction> var1) {
      this._onInitActions.addAll(var1);
   }

   public void addOnTimeAction(int var1, EventAction var2) {
      List var3 = (List)this._onTimeActions.get(var1);
      if (var3 != null) {
         var3.add(var2);
      } else {
         ArrayList var4 = new ArrayList(1);
         var4.add(var2);
         this._onTimeActions.put(var1, var4);
      }
   }

   public void addOnTimeActions(int var1, List<EventAction> var2) {
      if (!var2.isEmpty()) {
         List var3 = (List)this._onTimeActions.get(var1);
         if (var3 != null) {
            var3.addAll(var2);
         } else {
            this._onTimeActions.put(var1, new ArrayList(var2));
         }
      }
   }

   public void timeActions(int var1) {
      List var2 = (List)this._onTimeActions.get(var1);
      if (var2 == null) {
         this.info("Undefined time : " + var1 + " for " + this.toString());
      } else {
         this.callActions(var2);
      }
   }

   public int[] timeActions() {
      return this._onTimeActions.keySet().toArray();
   }

   public void registerActions() {
      long var1 = this.startTimeMillis();
      if (var1 != 0L) {
         for (int var6 : this._onTimeActions.keySet().toArray()) {
            ActionRunner.getInstance().register(var1 + var6 * 1000L, new EventWrapper(this._timerName, this, var6));
         }
      }
   }

   public void clearActions() {
      ActionRunner.getInstance().clear(this._timerName);
   }

   public <O extends Serializable> List<O> getObjects(String var1) {
      List var2 = this._objects.get(var1);
      return var2 == null ? Collections.emptyList() : var2;
   }

   public <O extends Serializable> O getFirstObject(String var1) {
      List var2 = this.getObjects(var1);
      return (O)(var2.size() > 0 ? var2.get(0) : null);
   }

   public void addObject(String var1, Serializable var2) {
      if (var2 != null) {
         CopyOnWriteArrayList<Serializable> var3 = (CopyOnWriteArrayList<Serializable>)this._objects.get(var1);
         if (var3 != null) {
            var3.add(var2);
         } else {
            var3 = new CopyOnWriteArrayList();
            var3.add(var2);
            this._objects.put(var1, var3);
         }
      }
   }

   public void removeObject(String var1, Serializable var2) {
      if (var2 != null) {
         List var3 = this._objects.get(var1);
         if (var3 != null) {
            var3.remove(var2);
         }
      }
   }

   public <O extends Serializable> List<O> removeObjects(String var1) {
      List var2 = this._objects.remove(var1);
      return var2 == null ? Collections.emptyList() : var2;
   }

   public void addObjects(String var1, List<? extends Serializable> var2) {
      if (!var2.isEmpty()) {
         List var3 = this._objects.get(var1);
         if (var3 != null) {
            var3.addAll(var2);
         } else {
            this._objects.put(var1, new CopyOnWriteArrayList<Serializable>(var2));
         }
      }
   }

   public Map<String, List<Serializable>> getObjects() {
      return this._objects;
   }

   public void spawnAction(String var1, boolean var2) {
      List var3 = this.getObjects(var1);
      if (var3.isEmpty()) {
         this.info("Undefined objects: " + var1);
      } else {
         for (Object __raw_var5_216 : (Iterable<?>)(Object)(var3)) {
            Serializable var5 = (Serializable)__raw_var5_216;
            if (var5 instanceof SpawnableObject) {
               if (var2) {
                  ((SpawnableObject)var5).spawnObject(this);
               } else {
                  ((SpawnableObject)var5).despawnObject(this);
               }
            }
         }
      }
   }

   public void doorAction(String var1, boolean var2) {
      List var3 = this.getObjects(var1);
      if (var3.isEmpty()) {
         this.info("Undefined objects: " + var1);
      } else {
         for (Object __raw_var5_233 : (Iterable<?>)(Object)(var3)) {
            Serializable var5 = (Serializable)__raw_var5_233;
            if (var5 instanceof DoorObject) {
               if (var2) {
                  ((DoorObject)var5).open(this);
               } else {
                  ((DoorObject)var5).close(this);
               }
            }
         }
      }
   }

   public void zoneAction(String var1, boolean var2) {
      List var3 = this.getObjects(var1);
      if (var3.isEmpty()) {
         this.info("Undefined objects: " + var1);
      } else {
         for (Object __raw_var5_250 : (Iterable<?>)(Object)(var3)) {
            Serializable var5 = (Serializable)__raw_var5_250;
            if (var5 instanceof ZoneObject) {
               ((ZoneObject)var5).setActive(var2, this);
            }

            if (var5 instanceof String) {
               this.zoneAction((String)var5, var2);
            }
         }
      }
   }

   public void initAction(String var1) {
      List var2 = this.getObjects(var1);
      if (var2.isEmpty()) {
         this.info("Undefined objects: " + var1);
      } else {
         for (Object __raw_var4_267 : (Iterable<?>)(Object)(var2)) {
            Serializable var4 = (Serializable)__raw_var4_267;
            if (var4 instanceof InitableObject) {
               ((InitableObject)var4).initObject(this);
            }
         }
      }
   }

   public void action(String var1, boolean var2) {
      if (var1.equalsIgnoreCase("event")) {
         if (var2) {
            this.startEvent();
         } else {
            this.stopEvent();
         }
      }
   }

   public void refreshAction(String var1) {
      List var2 = this.getObjects(var1);
      if (var2.isEmpty()) {
         this.info("Undefined objects: " + var1);
      } else {
         for (Object __raw_var4_290 : (Iterable<?>)(Object)(var2)) {
            Serializable var4 = (Serializable)__raw_var4_290;
            if (var4 instanceof SpawnableObject) {
               ((SpawnableObject)var4).refreshObject(this);
            }
         }
      }
   }

   public abstract void reCalcNextTime(boolean var1);

   protected abstract long startTimeMillis();

   public void broadcastToWorld(IStaticPacket var1) {
      for (Player var3 : GameObjectsStorage.getAllPlayersForIterate()) {
         if (var3 != null) {
            var3.sendPacket(var1);
         }
      }
   }

   public void broadcastToWorld(L2GameServerPacket var1) {
      for (Player var3 : GameObjectsStorage.getAllPlayersForIterate()) {
         if (var3 != null) {
            var3.sendPacket(var1);
         }
      }
   }

   public int getId() {
      return this._id;
   }

   public String getName() {
      return this._name;
   }

   public GameObject getCenterObject() {
      return null;
   }

   public Reflection getReflection() {
      return ReflectionManager.DEFAULT;
   }

   public int getRelation(Player var1, Player var2, int var3) {
      return var3;
   }

   public int getUserRelation(Player var1, int var2) {
      return var2;
   }

   public void checkRestartLocs(Player var1, Map<RestartType, Boolean> var2) {
   }

   public Location getRestartLoc(Player var1, RestartType var2) {
      return null;
   }

   public boolean canAttack(Creature var1, Creature var2, Skill var3, boolean var4) {
      return false;
   }

   public SystemMsg checkForAttack(Creature var1, Creature var2, Skill var3, boolean var4) {
      return null;
   }

   public boolean isInProgress() {
      return false;
   }

   public boolean isParticle(Player var1) {
      return false;
   }

   public void announce(int var1) {
      throw new UnsupportedOperationException();
   }

   public void teleportPlayers(String var1) {
      throw new UnsupportedOperationException();
   }

   public boolean ifVar(String var1) {
      throw new UnsupportedOperationException();
   }

   public List<Player> itemObtainPlayers() {
      throw new UnsupportedOperationException();
   }

   public void giveItem(Player var1, int var2, long var3) {
      Functions.addItem(var1, var2, var3);
   }

   public List<Player> broadcastPlayers(int var1) {
      throw new UnsupportedOperationException();
   }

   public boolean canResurrect(Player var1, Creature var2, boolean var3) {
      return true;
   }

   public void onAddEvent(GameObject var1) {
   }

   public void onRemoveEvent(GameObject var1) {
   }

   public void addBanishItem(ItemInstance var1) {
      if (this._banishedItems.isEmpty()) {
         this._banishedItems = new CHashIntObjectMap();
      }

      this._banishedItems.put(var1.getObjectId(), var1);
   }

   public void removeBanishItems() {
      Iterator var1 = this._banishedItems.entrySet().iterator();

      while (var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         var1.remove();
         ItemInstance var3 = ItemsDAO.getInstance().load(var2.getKey());
         if (var3 != null) {
            if (var3.getOwnerId() > 0) {
               GameObject var4 = GameObjectsStorage.findObject(var3.getOwnerId());
               if (var4 != null && var4.isPlayable()) {
                  ((Playable)var4).getInventory().destroyItem(var3);
                  var4.getPlayer().sendPacket(SystemMessage2.removeItems(var3));
               }
            }

            var3.delete();
         } else {
            var3 = (ItemInstance)var2.getValue();
         }

         var3.deleteMe();
      }
   }

   public void addListener(Listener<GlobalEvent> var1) {
      this._listenerList.add(var1);
   }

   public void removeListener(Listener<GlobalEvent> var1) {
      this._listenerList.remove(var1);
   }

   public void cloneTo(GlobalEvent var1) {
      for (EventAction var3 : this._onInitActions) {
         var1._onInitActions.add(var3);
      }

      for (EventAction var7 : this._onStartActions) {
         var1._onStartActions.add(var7);
      }

      for (EventAction var8 : this._onStopActions) {
         var1._onStopActions.add(var8);
      }

      for (Entry var9 : this._onTimeActions.entrySet()) {
         var1.addOnTimeActions(var9.getKey(), (List<EventAction>)var9.getValue());
      }
   }

   private class ListenerListImpl extends ListenerList<GlobalEvent> {
      private ListenerListImpl() {
      }

      public void onStart() {
         this.forEachListener(OnStartStopListener.class, var1 -> var1.onStart(GlobalEvent.this));
      }

      public void onStop() {
         this.forEachListener(OnStartStopListener.class, var1 -> var1.onStop(GlobalEvent.this));
      }
   }
}
