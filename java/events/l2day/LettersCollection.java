package events.l2day;

import java.util.HashMap;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LettersCollection extends Functions implements OnDeathListener, OnPlayerEnterListener, ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(LettersCollection.class);
   protected static boolean _active;
   protected static String _name;
   protected static int[][] letters;
   protected static String _msgStarted;
   protected static String _msgEnded;
   protected static int A = 3875;
   protected static int C = 3876;
   protected static int E = 3877;
   protected static int F = 3878;
   protected static int G = 3879;
   protected static int H = 3880;
   protected static int I = 3881;
   protected static int L = 3882;
   protected static int N = 3883;
   protected static int O = 3884;
   protected static int R = 3885;
   protected static int S = 3886;
   protected static int T = 3887;
   protected static int II = 3888;
   private static String kZ = "[event_letter_collection_spawn]";
   protected static Map<String, Integer[][]> _words = new HashMap<>();
   protected static Map<String, RewardData[]> _rewards = new HashMap<>();

   @Override
   public void onLoad() {
      CharListenerList.addGlobal(this);
      if (isActive()) {
         _active = true;
         this.spawnEventManagers();
         _log.info("Loaded Event: " + _name + " [state: activated]");
      } else {
         _log.debug("Loaded Event: " + _name + " [state: deactivated]");
      }
   }

   protected static boolean isActive() {
      return IsActive(_name);
   }

   protected void spawnEventManagers() {
      SpawnManager.getInstance().spawn(kZ);
   }

   protected void unSpawnEventManagers() {
      SpawnManager.getInstance().despawn(kZ);
   }

   @Override
   public void onReload() {
      this.unSpawnEventManagers();
   }

   @Override
   public void onShutdown() {
      this.unSpawnEventManagers();
   }

   @Override
   public void onDeath(Creature var1, Creature var2) {
      if (_active && SimpleCheckDrop(var1, var2)) {
         int[] var3 = letters[Rnd.get(letters.length)];
         if (Rnd.chance(var3[1] * Config.EVENT_L2DAY_LETTER_CHANCE * ((NpcTemplate)var1.getTemplate()).rateHp)) {
            ((NpcInstance)var1).dropItem(var2.getPlayer(), var3[0], 1L);
         }
      }
   }

   public void startEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive(_name, true)) {
            this.spawnEventManagers();
            System.out.println("Event '" + _name + "' started.");
            Announcements.getInstance().announceByCustomMessage(_msgStarted, null);
         } else {
            var1.sendMessage("Event '" + _name + "' already started.");
         }

         _active = true;
         this.show("admin/events/events.htm", var1);
      }
   }

   public void stopEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive(_name, false)) {
            this.unSpawnEventManagers();
            System.out.println("Event '" + _name + "' stopped.");
            Announcements.getInstance().announceByCustomMessage(_msgEnded, null);
         } else {
            var1.sendMessage("Event '" + _name + "' not started.");
         }

         _active = false;
         this.show("admin/events/events.htm", var1);
      }
   }

   public void exchange(String[] var1) {
      Player var2 = this.getSelf();
      if (var2.isQuestContinuationPossible(true)) {
         if (NpcInstance.canBypassCheck(var2, var2.getLastNpc())) {
            Integer[][] var3 = _words.get(var1[0]);

            for (Integer[] var7 : var3) {
               if (getItemCount(var2, var7[0]) < var7[1].intValue()) {
                  var2.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                  return;
               }
            }

            for (Integer[] var19 : var3) {
               removeItem(var2, var19[0], var19[1].intValue(), false);
            }

            RewardData[] var12 = _rewards.get(var1[0]);
            int var14 = 0;

            for (RewardData var9 : var12) {
               var14 = (int)(var14 + var9.getChance());
            }

            int var18 = Rnd.get(var14);
            var14 = 0;

            for (RewardData var10 : var12) {
               var14 = (int)(var14 + var10.getChance());
               if (var14 > var18) {
                  addItem(var2, var10.getItemId(), Rnd.get(var10.getMinDrop(), var10.getMaxDrop()));
                  return;
               }
            }
         }
      }
   }

   @Override
   public void onPlayerEnter(Player var1) {
      if (_active) {
         Announcements.getInstance().announceToPlayerByCustomMessage(var1, _msgStarted, null);
      }
   }

   public String DialogAppend_31230(Integer var1) {
      if (!_active) {
         return "";
      }

      StringBuilder var2 = new StringBuilder("<br><br>");

      for (String var4 : _words.keySet()) {
         var2.append("[scripts_").append(this.getClass().getName()).append(":exchange ").append(var4).append("|").append(var4).append("]<br1>");
      }

      return var2.toString();
   }
}
