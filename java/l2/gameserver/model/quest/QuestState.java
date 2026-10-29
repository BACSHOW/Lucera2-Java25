package l2.gameserver.model.quest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.OnKillListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.ExShowQuestMark;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.QuestList;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.network.l2.s2c.TutorialEnableClientEvent;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.network.l2.s2c.TutorialShowQuestionMark;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.spawn.PeriodOfDay;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QuestState {
   private static final Logger _log = LoggerFactory.getLogger(QuestState.class);
   public static final int RESTART_HOUR = 6;
   public static final int RESTART_MINUTES = 30;
   public static final String VAR_COND = "cond";
   public static final QuestState[] EMPTY_ARRAY = new QuestState[0];
   private final Player _player;
   private Quest Ze;
   private int _state;
   private Integer Zf = null;
   private Map<String, String> Zg = new ConcurrentHashMap<>();
   private Map<String, QuestTimer> Zh = new ConcurrentHashMap<>();
   private OnKillListener Zi = null;

   public QuestState(Quest var1, Player var2, int var3) {
      this.Ze = var1;
      this._player = var2;
      var2.setQuestState(this);
      this._state = var3;
      var1.notifyCreate(this);
   }

   public void addExpAndSp(long var1, long var3) {
      Player var5 = this.getPlayer();
      if (var5 != null) {
         var1 = (long)(var1 * this.getRateQuestsRewardExp());
         var3 = (long)(var3 * this.getRateQuestsRewardSp());
         if (var1 > 0L && var3 > 0L) {
            var5.addExpAndSp(var1, var3);
         } else {
            if (var1 > 0L) {
               var5.addExpAndSp(var1, 0L);
            }

            if (var3 > 0L) {
               var5.addExpAndSp(0L, var3);
            }
         }
      }
   }

   public void addNotifyOfDeath(Player var1, boolean var2) {
      QuestState.OnDeathListenerImpl var3 = new QuestState.OnDeathListenerImpl();
      var1.addListener(var3);
      if (var2) {
         Summon var4 = var1.getPet();
         if (var4 != null) {
            var4.addListener(var3);
         }
      }
   }

   public void addPlayerOnKillListener() {
      if (this.Zi != null) {
         throw new IllegalArgumentException("Cant add twice kill listener to player");
      }

      this.Zi = new QuestState.PlayerOnKillListenerImpl();
      this._player.addListener(this.Zi);
   }

   public void removePlayerOnKillListener() {
      if (this.Zi != null) {
         this._player.removeListener(this.Zi);
      }
   }

   public void addRadar(int var1, int var2, int var3) {
      Player var4 = this.getPlayer();
      if (var4 != null) {
         var4.addRadar(var1, var2, var3);
      }
   }

   public void addRadarWithMap(int var1, int var2, int var3) {
      Player var4 = this.getPlayer();
      if (var4 != null) {
         var4.addRadarWithMap(var1, var2, var3);
      }
   }

   public void exitCurrentQuest(Quest var1) {
      Player var2 = this.getPlayer();
      this.exitCurrentQuest(true);
      var1.newQuestState(var2, 4);
      QuestState var3 = var2.getQuestState(var1.getClass());
      var3.setRestartTime();
   }

   public QuestState exitCurrentQuest(boolean var1) {
      Player var2 = this.getPlayer();
      if (var2 == null) {
         return this;
      }

      this.removePlayerOnKillListener();

      for (int var6 : this.Ze.getItems()) {
         ItemInstance var7 = var2.getInventory().getItemByItemId(var6);
         if (var7 != null && var6 != 57) {
            long var8 = var7.getCount();
            var2.getInventory().destroyItemByItemId(var6, var8);
            var2.getWarehouse().destroyItemByItemId(var6, var8);
         }
      }

      if (var1) {
         var2.removeQuestState(this.Ze.getName());
         Quest.deleteQuestInDb(this);
         this.Zg.clear();
      } else {
         for (String var11 : this.Zg.keySet()) {
            if (var11 != null) {
               this.unset(var11);
            }
         }

         this.setState(3);
         Quest.updateQuestInDb(this);
      }

      var2.sendPacket(new QuestList(var2));
      return this;
   }

   public void abortQuest() {
      this.Ze.onAbort(this);
      this.exitCurrentQuest(true);
   }

   public String get(String var1) {
      return this.Zg.get(var1);
   }

   public Map<String, String> getVars() {
      return this.Zg;
   }

   public int getInt(String var1) {
      int var2 = 0;

      try {
         String var3 = this.get(var1);
         if (var3 == null) {
            return 0;
         }

         var2 = Integer.parseInt(var3);
      } catch (Exception var4) {
         _log.error(this.getPlayer().getName() + ": variable " + var1 + " isn't an integer: " + var2, var4);
      }

      return var2;
   }

   public int getItemEquipped(int var1) {
      return this.getPlayer().getInventory().getPaperdollItemId(var1);
   }

   public Player getPlayer() {
      return this._player;
   }

   public Quest getQuest() {
      return this.Ze;
   }

   public boolean checkQuestItemsCount(int... var1) {
      Player var2 = this.getPlayer();
      if (var2 == null) {
         return false;
      }

      for (int var6 : var1) {
         if (var2.getInventory().getCountOf(var6) <= 0L) {
            return false;
         }
      }

      return true;
   }

   public long getSumQuestItemsCount(int... var1) {
      Player var2 = this.getPlayer();
      if (var2 == null) {
         return 0L;
      }

      long var3 = 0L;

      for (int var8 : var1) {
         var3 += var2.getInventory().getCountOf(var8);
      }

      return var3;
   }

   public long getQuestItemsCount(int var1) {
      Player var2 = this.getPlayer();
      return var2 == null ? 0L : var2.getInventory().getCountOf(var1);
   }

   public long getQuestItemsCount(int... var1) {
      long var2 = 0L;

      for (int var7 : var1) {
         var2 += this.getQuestItemsCount(var7);
      }

      return var2;
   }

   public boolean haveQuestItem(int var1, int var2) {
      return this.getQuestItemsCount(var1) >= var2;
   }

   public boolean haveQuestItem(int var1) {
      return this.haveQuestItem(var1, 1);
   }

   public int getState() {
      return this._state == 4 ? 1 : this._state;
   }

   public String getStateName() {
      return Quest.getStateName(this._state);
   }

   public void giveItems(int var1, long var2) {
      if (var1 == 57) {
         this.giveItems(var1, var2, true);
         Log.LogItem(this.getPlayer(), Log.ItemLog.QuestCreate, var1, var2, 0L, this.getQuest().getQuestIntId());
      } else {
         this.giveItems(var1, var2, false);
      }
   }

   public void giveItems(int var1, long var2, boolean var4) {
      Player var5 = this.getPlayer();
      if (var5 != null) {
         if (var2 <= 0L) {
            var2 = 1L;
         }

         if (var4) {
            var2 = (long)(var2 * (var1 != 57 ? this.getRateQuestsReward() : this.getRateQuestsAdenaReward()));
         }

         ItemFunctions.addItem(var5, var1, var2, true);
         var5.sendChanges();
      }
   }

   public void giveItems(int var1, long var2, Element var4, int var5) {
      Player var6 = this.getPlayer();
      if (var6 != null) {
         if (var2 <= 0L) {
            var2 = 1L;
         }

         ItemTemplate var7 = ItemHolder.getInstance().getTemplate(var1);
         if (var7 != null) {
            for (int var8 = 0; var8 < var2; var8++) {
               ItemInstance var9 = ItemFunctions.createItem(var1);
               if (var4 != Element.NONE) {
                  var9.setAttributeElement(var4, var5);
               }

               var6.getInventory().addItem(var9);
            }

            var6.sendPacket(SystemMessage2.obtainItems(var7.getItemId(), var2, 0));
            var6.sendChanges();
         }
      }
   }

   public void dropItem(NpcInstance var1, int var2, long var3) {
      Player var5 = this.getPlayer();
      if (var5 != null) {
         ItemInstance var6 = ItemFunctions.createItem(var2);
         var6.setCount(var3);
         var6.dropToTheGround(var5, var1);
      }
   }

   public int rollDrop(int var1, double var2) {
      return !(var2 <= 0.0) && var1 > 0 ? this.rollDrop(var1, var1, var2) : 0;
   }

   public int rollDrop(int var1, int var2, double var3) {
      if (!(var3 <= 0.0) && var1 > 0 && var2 > 0) {
         int var5 = 1;
         var3 *= this.getRateQuestsDrop();
         Party var6;
         if (Config.ALT_PARTY_BONUS_FOR_QUESTS && this.getQuest().getParty() != 0 && (var6 = this.getPlayer().getParty()) != null) {
            var3 *= Config.ALT_PARTY_BONUS[var6.getMemberCountInRange(this.getPlayer(), Config.ALT_PARTY_DISTRIBUTION_RANGE) - 1];
         }

         if (var3 > 100.0) {
            double var7 = var3 - 100.0;
            var3 = 100.0;
            var5 = 1 + Math.max(0, (int)(var7 / 100.0));
            if (Rnd.chance(var7 - (var5 - 1) * 100.0)) {
               var5++;
            }
         }

         return Rnd.chance(var3) ? Rnd.get(var1 * var5, var2 * var5) : 0;
      } else {
         return 0;
      }
   }

   public double getRateQuestsDrop() {
      Player var1 = this.getPlayer();
      double var2 = var1 == null ? 1.0 : var1.getBonus().getQuestDropRate();
      return Config.RATE_QUESTS_DROP * var2 * this.getQuest().getRates().getDropRate();
   }

   public double getRateQuestsReward() {
      Player var1 = this.getPlayer();
      double var2 = var1 == null ? 1.0 : var1.getBonus().getQuestRewardRate();
      return Config.RATE_QUESTS_REWARD * var2 * this.getQuest().getRates().getRewardRate();
   }

   public double getRateQuestsAdenaReward() {
      Player var1 = this.getPlayer();
      double var2 = var1 == null ? 1.0 : var1.getBonus().getQuestRewardAdenaRate();
      return Config.RATE_QUESTS_ADENA_REWARD * var2 * this.getQuest().getRates().getRewardAdenaRate();
   }

   public double getRateQuestsRewardExp() {
      Player var1 = this.getPlayer();
      double var2 = var1 == null ? 1.0 : var1.getBonus().getQuestRewardRate();
      return Config.RATE_QUESTS_REWARD_EXP_SP * var2 * this.getQuest().getRates().getExpRate();
   }

   public double getRateQuestsRewardSp() {
      Player var1 = this.getPlayer();
      double var2 = var1 == null ? 1.0 : var1.getBonus().getQuestRewardRate();
      return Config.RATE_QUESTS_REWARD_EXP_SP * var2 * this.getQuest().getRates().getSpRate();
   }

   public boolean rollAndGive(int var1, int var2, int var3, int var4, double var5) {
      if (!(var5 <= 0.0) && var2 > 0 && var3 > 0 && var4 > 0 && var1 > 0) {
         long var7 = this.rollDrop(var2, var3, var5);
         if (var7 > 0L) {
            long var9 = this.getQuestItemsCount(var1);
            if (var9 + var7 > var4) {
               var7 = var4 - var9;
            }

            if (var7 > 0L) {
               this.giveItems(var1, var7, false);
               if (var7 + var9 >= var4) {
                  this.playSound("ItemSound.quest_middle");
                  return true;
               }

               this.playSound("ItemSound.quest_itemget");
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void rollAndGive(int var1, int var2, int var3, double var4) {
      if (!(var4 <= 0.0) && var2 > 0 && var3 > 0 && var1 > 0) {
         int var6 = this.rollDrop(var2, var3, var4);
         if (var6 > 0) {
            this.giveItems(var1, var6, false);
            this.playSound("ItemSound.quest_itemget");
         }
      }
   }

   public boolean rollAndGive(int var1, int var2, double var3) {
      if (!(var3 <= 0.0) && var2 > 0 && var1 > 0) {
         int var5 = this.rollDrop(var2, var3);
         if (var5 > 0) {
            this.giveItems(var1, var5, false);
            this.playSound("ItemSound.quest_itemget");
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isCompleted() {
      return this.getState() == 3;
   }

   public boolean isStarted() {
      return this.getState() == 2;
   }

   public boolean isCreated() {
      return this.getState() == 1;
   }

   public void killNpcByObjectId(int var1) {
      NpcInstance var2 = GameObjectsStorage.getNpc(var1);
      if (var2 != null) {
         var2.doDie(null);
      } else {
         _log.warn("Attemp to kill object that is not npc in quest " + this.getQuest().getQuestIntId());
      }
   }

   public String set(String var1, String var2) {
      return this.set(var1, var2, true);
   }

   public String set(String var1, int var2) {
      return this.set(var1, String.valueOf(var2), true);
   }

   public String set(String var1, String var2, boolean var3) {
      if (var2 == null) {
         var2 = "";
      }

      this.Zg.put(var1, var2);
      if (var3) {
         Quest.updateQuestVarInDb(this, var1, var2);
      }

      return var2;
   }

   public Object setState(int var1) {
      Player var2 = this.getPlayer();
      if (var2 == null) {
         return null;
      }

      this._state = var1;
      if (this.getQuest().isVisible() && this.isStarted()) {
         var2.sendPacket(new ExShowQuestMark(this.getQuest().getQuestIntId()));
      }

      Quest.updateQuestInDb(this);
      var2.sendPacket(new QuestList(var2));
      var2.getListeners().onQuestStateChange(this);
      return var1;
   }

   public Object setStateAndNotSave(int var1) {
      Player var2 = this.getPlayer();
      if (var2 == null) {
         return null;
      }

      this._state = var1;
      if (this.getQuest().isVisible() && this.isStarted()) {
         var2.sendPacket(new ExShowQuestMark(this.getQuest().getQuestIntId()));
      }

      var2.sendPacket(new QuestList(var2));
      return var1;
   }

   public void playSound(String var1) {
      Player var2 = this.getPlayer();
      if (var2 != null) {
         var2.sendPacket(new PlaySound(var1));
      }
   }

   public void playTutorialVoice(String var1) {
      Player var2 = this.getPlayer();
      if (var2 != null) {
         var2.sendPacket(new PlaySound(PlaySound.Type.VOICE, var1, 0, 0, var2.getLoc()));
      }
   }

   public void onTutorialClientEvent(int var1) {
      Player var2 = this.getPlayer();
      if (var2 != null) {
         var2.sendPacket(new TutorialEnableClientEvent(var1));
      }
   }

   public void showQuestionMark(int var1) {
      Player var2 = this.getPlayer();
      if (var2 != null) {
         var2.sendPacket(new TutorialShowQuestionMark(var1));
      }
   }

   public void showTutorialHTML(String var1) {
      Player var2 = this.getPlayer();
      if (var2 != null) {
         String var3 = HtmCache.getInstance().getNotNull("quests/_255_Tutorial/" + var1, var2);
         var2.sendPacket(new TutorialShowHtml(var3));
      }
   }

   public void startQuestTimer(String var1, long var2) {
      this.startQuestTimer(var1, var2, null);
   }

   public void startQuestTimer(String var1, long var2, NpcInstance var4) {
      QuestTimer var5 = new QuestTimer(var1, var2, var4);
      var5.setQuestState(this);
      QuestTimer var6 = this.getTimers().put(var1, var5);
      if (var6 != null) {
         var6.stop();
      }

      var5.start();
   }

   public boolean isRunningQuestTimer(String var1) {
      return this.getTimers().get(var1) != null;
   }

   public boolean cancelQuestTimer(String var1) {
      QuestTimer var2 = this.removeQuestTimer(var1);
      if (var2 != null) {
         var2.stop();
      }

      return var2 != null;
   }

   QuestTimer removeQuestTimer(String var1) {
      QuestTimer var2 = this.getTimers().remove(var1);
      if (var2 != null) {
         var2.setQuestState(null);
      }

      return var2;
   }

   public void pauseQuestTimers() {
      this.getQuest().pauseQuestTimers(this);
   }

   public void stopQuestTimers() {
      for (QuestTimer var2 : this.getTimers().values()) {
         var2.setQuestState(null);
         var2.stop();
      }

      this.Zh.clear();
   }

   public void resumeQuestTimers() {
      this.getQuest().resumeQuestTimers(this);
   }

   Map<String, QuestTimer> getTimers() {
      return this.Zh;
   }

   public long takeItems(int var1, long var2) {
      Player var4 = this.getPlayer();
      if (var4 == null) {
         return 0L;
      }

      ItemInstance var5 = var4.getInventory().getItemByItemId(var1);
      if (var5 == null) {
         return 0L;
      }

      if (var2 < 0L || var2 > var5.getCount()) {
         var2 = var5.getCount();
      }

      var4.getInventory().destroyItemByItemId(var1, var2);
      var4.sendPacket(SystemMessage2.removeItems(var1, var2));
      Log.LogItem(var4, Log.ItemLog.QuestDelete, var1, var2, 0L, this.getQuest().getQuestIntId());
      return var2;
   }

   public long takeAllItems(int var1) {
      return this.takeItems(var1, -1L);
   }

   public long takeAllItems(int... var1) {
      long var2 = 0L;

      for (int var7 : var1) {
         var2 += this.takeAllItems(var7);
      }

      return var2;
   }

   public long takeAllItems(Collection<Integer> var1) {
      long var2 = 0L;

      for (int var5 : var1) {
         var2 += this.takeAllItems(var5);
      }

      return var2;
   }

   public String unset(String var1) {
      if (var1 == null) {
         return null;
      }

      String var2 = this.Zg.remove(var1);
      if (var2 != null) {
         Quest.deleteQuestVarInDb(this, var1);
      }

      return var2;
   }

   private boolean a(Player var1, int var2, int var3, GameObject var4) {
      if (var1 == null) {
         return false;
      }

      if (var4 != null && var3 > 0 && !var1.isInRange(var4, var3)) {
         return false;
      }

      QuestState var5 = var1.getQuestState(this.getQuest().getName());
      return var5 != null && var5.getState() == var2;
   }

   public List<Player> getPartyMembers(int var1, int var2, GameObject var3) {
      ArrayList var4 = new ArrayList();
      Party var5 = this.getPlayer().getParty();
      if (var5 == null) {
         if (this.a(this.getPlayer(), var1, var2, var3)) {
            var4.add(this.getPlayer());
         }

         return var4;
      } else {
         for (Player var7 : var5.getPartyMembers()) {
            if (this.a(var7, var1, var2, var3)) {
               var4.add(var7);
            }
         }

         return var4;
      }
   }

   public Player getRandomPartyMember(int var1, int var2) {
      return this.getRandomPartyMember(var1, var2, this.getPlayer());
   }

   public Player getRandomPartyMember(int var1, int var2, GameObject var3) {
      List var4 = this.getPartyMembers(var1, var2, var3);
      return var4.size() == 0 ? null : (Player)var4.get(Rnd.get(var4.size()));
   }

   public NpcInstance addSpawn(int var1) {
      return this.addSpawn(var1, this.getPlayer().getX(), this.getPlayer().getY(), this.getPlayer().getZ(), 0, 0, 0);
   }

   public NpcInstance addSpawn(int var1, int var2) {
      return this.addSpawn(var1, this.getPlayer().getX(), this.getPlayer().getY(), this.getPlayer().getZ(), 0, 0, var2);
   }

   public NpcInstance addSpawn(int var1, int var2, int var3, int var4) {
      return this.addSpawn(var1, var2, var3, var4, 0, 0, 0);
   }

   public NpcInstance addSpawn(int var1, int var2, int var3, int var4, int var5) {
      return this.addSpawn(var1, var2, var3, var4, 0, 0, var5);
   }

   public NpcInstance addSpawn(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return this.getQuest().addSpawn(var1, var2, var3, var4, var5, var6, var7);
   }

   public NpcInstance findTemplate(int var1) {
      for (Spawner var3 : SpawnManager.getInstance().getSpawners(PeriodOfDay.ALL.name())) {
         if (var3 != null && var3.getCurrentNpcId() == var1) {
            return var3.getLastSpawn();
         }
      }

      return null;
   }

   public int calculateLevelDiffForDrop(int var1, int var2) {
      return !Config.DEEPBLUE_DROP_RULES ? 0 : Math.max(var2 - var1 - Config.DEEPBLUE_DROP_MAXDIFF, 0);
   }

   public int getCond() {
      if (this.Zf == null) {
         int var1 = this.getInt("cond");
         if ((var1 & -2147483648) != 0) {
            var1 &= Integer.MAX_VALUE;

            for (int var2 = 1; var2 < 32; var2++) {
               var1 >>= 1;
               if (var1 == 0) {
                  var1 = var2;
                  break;
               }
            }
         }

         this.Zf = var1;
      }

      return this.Zf;
   }

   public String setCond(int var1) {
      return this.setCond(var1, true);
   }

   public String setCond(int var1, boolean var2) {
      if (var1 == this.getCond()) {
         return String.valueOf(var1);
      }

      int var3 = this.getInt("cond");
      this.Zf = var1;
      if ((var3 & -2147483648) != 0) {
         if (var1 > 2) {
            var3 &= -2147483647 | (1 << var1) - 1;
            var1 = var3 | 1 << var1 - 1;
         }
      } else if (var1 > 2) {
         var1 = -2147483647 | 1 << var1 - 1 | (1 << var3) - 1;
      }

      String var4 = String.valueOf(var1);
      String var5 = this.set("cond", var4, false);
      if (var2) {
         Quest.updateQuestVarInDb(this, "cond", var4);
      }

      Player var6 = this.getPlayer();
      if (var6 != null) {
         var6.sendPacket(new QuestList(var6));
         if (var1 != 0 && this.getQuest().isVisible() && this.isStarted()) {
            var6.sendPacket(new ExShowQuestMark(this.getQuest().getQuestIntId()));
         }
      }

      return var5;
   }

   public void setRestartTime() {
      Calendar var1 = Calendar.getInstance();
      if (var1.get(11) >= 6) {
         var1.add(5, 1);
      }

      var1.set(11, 6);
      var1.set(12, 30);
      this.set("restartTime", String.valueOf(var1.getTimeInMillis()));
   }

   public boolean isNowAvailable() {
      String var1 = this.get("restartTime");
      if (var1 == null) {
         return true;
      }

      long var2 = Long.parseLong(var1);
      return var2 <= System.currentTimeMillis();
   }

   public void dropItemDelay(NpcInstance var1, int var2, long var3) {
      ThreadPoolManager.getInstance()
         .schedule(
            () -> {
               Player var5 = this.getPlayer();
               if (var5 != null) {
                  ItemInstance var6 = ItemFunctions.createItem(var2);
                  var6.setCount(var3);
                  var6.dropToTheGround(var5, var1);
                  Log.add(
                     "Added itemId: " + var2 + " count: " + var3,
                     "QuestState:giveItems(int, long, int) QuestName: " + this.getQuest().getName(),
                     this.getPlayer()
                  );
               }
            },
            4000L
         );
   }

   public class OnDeathListenerImpl implements OnDeathListener {
      @Override
      public void onDeath(Creature var1, Creature var2) {
         Player var3 = var1.getPlayer();
         if (var3 != null) {
            var3.removeListener(this);
            QuestState.this.Ze.notifyDeath(var2, var1, QuestState.this);
         }
      }
   }

   public class PlayerOnKillListenerImpl implements OnKillListener {
      @Override
      public void onKill(Creature var1, Creature var2) {
         if (var2.isPlayer()) {
            Player var3 = (Player)var1;
            List var4 = null;
            switch (QuestState.this.Ze.getParty()) {
               case 0:
                  var4 = Collections.singletonList(var3);
                  break;
               case 2:
                  if (var3.getParty() == null) {
                     var4 = Collections.singletonList(var3);
                  } else {
                     var4 = new ArrayList(var3.getParty().getMemberCount());

                     for (Player var6 : var3.getParty().getPartyMembers()) {
                        if (var6.isInActingRange(var3)) {
                           var4.add(var6);
                        }
                     }
                  }
                  break;
               default:
                  var4 = Collections.emptyList();
            }

            for (Object __raw_var10_876 : (Iterable<?>)(Object)(var4)) {
               Player var10 = (Player)__raw_var10_876;
               QuestState var7 = var10.getQuestState(QuestState.this.Ze.getClass());
               if (var7 != null && !var7.isCompleted()) {
                  QuestState.this.Ze.notifyKill((Player)var2, var7);
               }
            }
         }
      }

      @Override
      public boolean ignorePetOrSummon() {
         return true;
      }
   }
}
