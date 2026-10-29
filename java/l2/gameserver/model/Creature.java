package l2.gameserver.model;

import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.collections.LazyArrayList;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.listener.Listener;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.commons.util.concurrent.atomic.AtomicState;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.debug.NpcMovementDebugTracer;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.ai.NextAction;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.geodata.GeoMove;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.actor.recorder.CharStatsChangeRecorder;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.model.base.SpecialEffectState;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.model.reference.L2Reference;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.Attack;
import l2.gameserver.network.l2.s2c.AutoAttackStart;
import l2.gameserver.network.l2.s2c.AutoAttackStop;
import l2.gameserver.network.l2.s2c.ChangeMoveType;
import l2.gameserver.network.l2.s2c.CharMoveToLocation;
import l2.gameserver.network.l2.s2c.FlyToLocation;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillCanceled;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.MoveToPawn;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.SetupGauge;
import l2.gameserver.network.l2.s2c.StatusUpdate;
import l2.gameserver.network.l2.s2c.StopMove;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.TeleportToLocation;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.stats.Calculator;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.StatFunctions;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.stats.triggers.TriggerType;
import l2.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2.gameserver.taskmanager.RegenTaskManager;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Creature extends GameObject {
   @FunctionalInterface
   public interface BulkBuffScope extends AutoCloseable {
       @Override
       void close();
   }

   private final AtomicInteger _bulkBuffDepth = new AtomicInteger(0);
   private volatile boolean _statsModifiedInBulk = false;
   private static final Logger _log = LoggerFactory.getLogger(Creature.class);
   public static final double HEADINGS_IN_PI = 10430.378350470453;
   private Skill Do;
   private long Dp;
   private long Dq;
   public int _scheduledCastInterval;
   public Future<?> _skillTask;
   public Future<?> _skillLaunchedTask;
   private Future<?> Dr;
   private Runnable Ds;
   private long Dt;
   public static final int CLIENT_BAR_SIZE = 352;
   private int Du = -1;
   private int Dv = -1;
   private int Dw = -1;
   protected double _currentCp = 0.0;
   protected double _currentHp = 1.0;
   protected double _currentMp = 1.0;
   protected boolean _isAttackAborted;
   protected long _attackEndTime;
   protected long _attackReuseEndTime;
   private int Dx = 0;
   private static final double[] Dy = new double[]{1.0, 0.9, 0.0, 7.0, 0.2, 0.01};
   protected final Map<Integer, Skill> _skills = new ConcurrentSkipListMap<>();
   protected Map<TriggerType, Set<TriggerInfo>> _triggers;
   protected IntObjectMap<TimeStamp> _skillReuses = new CHashIntObjectMap();
   protected volatile EffectList _effectList;
   protected volatile CharStatsChangeRecorder<? extends Creature> _statsRecorder;
   private List<Stats> Dz;
   private int _abnormalEffects;
   private int _abnormalEffects2;
   private int _abnormalEffects3;
   protected AtomicBoolean isDead = new AtomicBoolean();
   protected AtomicBoolean isTeleporting = new AtomicBoolean();
   private Map<Integer, Integer> DA;
   protected boolean _isInvul;
   private boolean DB;
   private int DC = -1;
   private boolean DD;
   private boolean DE;
   private boolean DF;
   private AtomicState DG = new AtomicState();
   private AtomicState DH = new AtomicState();
   private AtomicState DI = new AtomicState();
   private AtomicState DJ = new AtomicState();
   private AtomicState DK = new AtomicState();
   private AtomicState DL = new AtomicState();
   private AtomicState DN = new AtomicState();
   private AtomicState DO = new AtomicState();
   private AtomicState DP = new AtomicState();
   private AtomicState DQ = new AtomicState();
   private AtomicState DR = new AtomicState();
   private AtomicState DS = new AtomicState();
   private AtomicState DT = new AtomicState();
   private AtomicState DU = new AtomicState();
   private AtomicState DV = new AtomicState();
   private AtomicState DW = new AtomicState();
   private AtomicState DX = new AtomicState();
   private boolean DY;
   private boolean DZ;
   protected Creature.MoveActionBase moveAction = null;
   private final Lock Ea = new ReentrantLock();
   private Future<?> Eb;
   private Runnable Ec;
   private volatile HardReference<? extends GameObject> target = HardReferences.emptyRef();
   private volatile HardReference<? extends Creature> Ed = HardReferences.emptyRef();
   private volatile HardReference<? extends Creature> Ee = HardReferences.emptyRef();
   private int Ef;
   private final Calculator[] Eg;
   protected CharTemplate _template;
   protected CharTemplate _baseTemplate;
   protected volatile CharacterAI _ai;
   protected String _name;
   protected String _title;
   protected TeamType _team = TeamType.NONE;
   private boolean Eh;
   private final Lock Ei = new ReentrantLock();
   private Future<?> Ej;
   private Runnable Ek;
   private volatile SpecialEffectState El = SpecialEffectState.FALSE;
   private Zones Em = new Zones();
   protected volatile CharListenerList listeners;
   private List<Player> En;
   private final Lock Eo = new ReentrantLock();
   protected Long _storedId;
   protected HardReference<? extends Creature> reference;
   private Location Ep;
   private TIntHashSet Eq = new TIntHashSet();

   @Override
   public int getActingRange() {
      return 150;
   }

   public final Long getStoredId() {
      return this._storedId;
   }

   public Creature(int var1, CharTemplate var2) {
      super(var1);
      this._template = var2;
      this._baseTemplate = var2;
      this.Eg = new Calculator[Stats.NUM_STATS];
      StatFunctions.addPredefinedFuncs(this);
      this.reference = new L2Reference<>(this);
      this._storedId = GameObjectsStorage.put(this);
   }

   @Override
   public HardReference<? extends Creature> getRef() {
      return this.reference;
   }

   public boolean isAttackAborted() {
      return this._isAttackAborted;
   }

   public final void abortAttack(boolean var1, boolean var2) {
      if (this.isAttackingNow() || this.getAI().getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
         this._attackEndTime = 0L;
         if (var1) {
            this._isAttackAborted = true;
         }

         this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
         if (this.isPlayer() && var2) {
            this.sendActionFailed();
            this.sendPacket(new SystemMessage(158));
         }
      }
   }

   public final void abortCast(boolean var1, boolean var2) {
      if (this.isCastingNow() && (var1 || this.canAbortCast())) {
         Skill var3 = this.Do;
         Future var4 = this._skillTask;
         Future var5 = this._skillLaunchedTask;
         this.bW();
         this.clearCastVars();
         if (var4 != null) {
            var4.cancel(false);
         }

         if (var5 != null) {
            var5.cancel(false);
         }

         if (var3 != null) {
            var3.onAbortCast(this, this.getAI().getAttackTarget());
            this.removeSkillMastery(var3.getId());
         }

         this.broadcastPacket(new MagicSkillCanceled(this));
         this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
         if (this.isPlayer() && var2) {
            this.sendPacket(Msg.CASTING_HAS_BEEN_INTERRUPTED);
         }
      }
   }

   public final boolean canAbortCast() {
      return this.Dp >= System.currentTimeMillis();
   }

   public boolean absorbAndReflect(Creature var1, Skill var2, double var3, boolean var5) {
      if (var1.isDead()) {
         return false;
      }

      boolean var6 = this.getActiveWeaponItem() != null && this.getActiveWeaponItem().getItemType() == WeaponTemplate.WeaponType.BOW;
      double var7 = 0.0;
      if (var2 != null && var2.isMagic()) {
         var7 = var1.calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, 0.0, this, var2);
      } else if (var2 != null && var2.getCastRange() <= 200) {
         var7 = var1.calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, 0.0, this, var2);
      } else if (var2 == null && !var6) {
         var7 = var1.calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, 0.0, this, null);
      }

      if (var7 > 0.0 && Rnd.chance(var7)) {
         this.reduceCurrentHp(var3, var1, null, true, true, false, false, false, false, true);
         return true;
      }

      if (var2 != null && var2.isMagic()) {
         var7 = var1.calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, 0.0, this, var2);
      } else if (var2 != null && var2.getCastRange() <= 200) {
         var7 = var1.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, 0.0, this, var2);
      } else if (var2 == null && !var6) {
         var7 = var1.calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0.0, this, null);
      }

      if (var7 > 0.0 && var1.getCurrentHp() + var1.getCurrentCp() > var3) {
         double var9 = var7 / 100.0 * var3;
         this.reduceCurrentHp(var9, var1, null, true, true, false, false, false, false, var5);
         if (var5 && var1.isPlayable()) {
            var1.sendPacket(new SystemMessage(35).addNumber((int)var9));
         }
      }

      if (var2 == null && !var6) {
         var3 = (int)(var3 - var1.getCurrentCp());
         if (var3 <= 0.0) {
            return false;
         }

         double var16 = this.Dx < Dy.length ? Dy[this.Dx] : 0.0;
         double var11 = var16 * this.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0.0, var1, null);
         if (var11 > 0.0 && !var1.isDamageBlocked()) {
            double var13 = this.calcStat(Stats.HP_LIMIT, null, null) * this.getMaxHp() / 100.0;
            if (this.getCurrentHp() < var13) {
               this.setCurrentHp(Math.min(this._currentHp + var3 * var11 * Config.ALT_ABSORB_DAMAGE_MODIFIER / 100.0, var13), false);
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public double absorbToEffector(Creature var1, double var2) {
      double var4 = this.calcStat(Stats.TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT, 0.0);
      if (var4 > 0.0) {
         Effect var6 = this.getEffectList().getEffectByType(EffectType.AbsorbDamageToEffector);
         if (var6 == null) {
            return var2;
         }

         Creature var7 = var6.getEffector();
         if (var7 == this || var7.isDead() || !this.isInRange(var7, 1200L)) {
            return var2;
         }

         Player var8 = this.getPlayer();
         Player var9 = var7.getPlayer();
         if (var8 == null || var9 == null) {
            return var2;
         }

         if (var8 != var9 && (!var8.isOnline() || !var8.isInParty() || var8.getParty() != var9.getParty())) {
            return var2;
         }

         double var10 = var2 * var4 * 0.01;
         var2 -= var10;
         var7.reduceCurrentHp(var10, var7, null, false, false, !var1.isPlayable(), false, true, false, true);
      }

      return var2;
   }

   public double absorbToSummon(Creature var1, double var2) {
      double var4 = this.calcStat(Stats.TRANSFER_TO_SUMMON_DAMAGE_PERCENT, 0.0);
      if (var4 > 0.0) {
         Summon var6 = this.getPet();
         double var7 = var2 * var4 * 0.01;
         if (var6 == null || var6.isDead() || var6.getCurrentHp() < var7) {
            this.getEffectList().stopEffects(EffectType.AbsorbDamageToSummon);
         } else if (var6.isSummon() && var6.isInRangeZ(this, 1200L)) {
            var2 -= var7;
            var6.reduceCurrentHp(var7, var6, null, false, false, false, false, true, false, true);
         }
      }

      return var2;
   }

   public void addBlockStats(List<Stats> var1) {
      if (this.Dz == null) {
         this.Dz = new ArrayList<>();
      }

      this.Dz.addAll(var1);
   }

   public Skill addSkill(Skill var1) {
      if (var1 == null) {
         return null;
      }

      Skill var2 = this._skills.get(var1.getId());
      if (var2 != null && var2.getLevel() == var1.getLevel()) {
         return var1;
      }

      this._skills.put(var1.getId(), var1);
      if (var2 != null) {
         this.removeStatsOwner(var2);
         this.removeTriggers(var2);
      }

      this.addTriggers(var1);
      this.addStatFuncs(var1.getStatFuncs());
      return var2;
   }

   public Calculator[] getCalculators() {
      return this.Eg;
   }

   public final void addStatFunc(Func var1) {
      if (var1 != null) {
         int var2 = var1.stat.ordinal();
         synchronized (this.Eg) {
            if (this.Eg[var2] == null) {
               this.Eg[var2] = new Calculator(var1.stat, this);
            }

            this.Eg[var2].addFunc(var1);
         }
      }
   }

   public final void addStatFuncs(Func[] var1) {
      for (Func var5 : var1) {
         this.addStatFunc(var5);
      }
   }

   public final void removeStatFunc(Func var1) {
      if (var1 != null) {
         int var2 = var1.stat.ordinal();
         synchronized (this.Eg) {
            if (this.Eg[var2] != null) {
               this.Eg[var2].removeFunc(var1);
            }
         }
      }
   }

   public final void removeStatFuncs(Func[] var1) {
      for (Func var5 : var1) {
         this.removeStatFunc(var5);
      }
   }

   public final void removeStatsOwner(Object var1) {
      synchronized (this.Eg) {
         for (int var3 = 0; var3 < this.Eg.length; var3++) {
            if (this.Eg[var3] != null) {
               this.Eg[var3].removeOwner(var1);
            }
         }
      }
   }

   public void altOnMagicUseTimer(Creature var1, Skill var2) {
      if (!this.isAlikeDead()) {
         List var3 = var2.getTargets(this, var1, true);
         double var4 = var2.getMpConsume2();
         if (var4 > 0.0) {
            if (this._currentMp < var4) {
               this.sendPacket(Msg.NOT_ENOUGH_MP);
               return;
            }

            if (var2.isMagic()) {
               this.reduceCurrentMp(this.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, var4, var1, var2), null);
            } else {
               this.reduceCurrentMp(this.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, var4, var1, var2), null);
            }
         }

         this.callSkill(var2, var3, false);
         this.broadcastPacket(new MagicSkillLaunched(this, var2, var3));
      }
   }

   public void altUseSkill(Skill var1, Creature var2) {
      if (var1 != null) {
         int var3 = var1.getId();
         if (!this.isUnActiveSkill(var3)) {
            if (this.isSkillDisabled(var1)) {
               this.sendReuseMessage(var1);
            } else {
               if (var2 == null) {
                  var2 = var1.getAimingTarget(this, this.getTarget());
                  if (var2 == null) {
                     return;
                  }
               }

               this.getListeners().onMagicUse(var1, var2, true);
               double var4 = var1.getMpConsume1();
               if (var4 > 0.0) {
                  if (this._currentMp < var4) {
                     this.sendPacket(Msg.NOT_ENOUGH_MP);
                     return;
                  }

                  this.reduceCurrentMp(var4, null);
               }

               int[] var6 = var1.getItemConsume();
               if (var6[0] > 0) {
                  for (int var7 = 0; var7 < var6.length; var7++) {
                     if (!this.consumeItem(var1.getItemConsumeId()[var7], var6[var7])) {
                        this.sendPacket(
                           var1.isHandler() ? SystemMsg.INCORRECT_ITEM_COUNT : new SystemMessage(113).addSkillName(var1.getDisplayId(), var1.getDisplayLevel())
                        );
                        return;
                     }
                  }
               }

               if (var1.getReferenceItemId() <= 0 || this.consumeItemMp(var1.getReferenceItemId(), var1.getReferenceItemMpConsume())) {
                  if (var1.getSoulsConsume() > this.getConsumedSouls()) {
                     this.sendPacket(Msg.THERE_IS_NOT_ENOUGHT_SOUL);
                  } else if (var1.getEnergyConsume() > this.getAgathionEnergy()) {
                     this.sendPacket(SystemMsg.THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_ENERGY);
                  } else {
                     if (var1.getSoulsConsume() > 0) {
                        this.setConsumedSouls(this.getConsumedSouls() - var1.getSoulsConsume(), null);
                     }

                     if (var1.getEnergyConsume() > 0) {
                        this.setAgathionEnergy(this.getAgathionEnergy() - var1.getEnergyConsume());
                     }

                     int var10 = Math.max(1, this.getSkillDisplayLevel(var3));
                     Formulas.calcSkillMastery(var1, this);
                     long var8 = Formulas.calcSkillReuseDelay(this, var1);
                     if (!var1.isToggle()) {
                        this.broadcastPacket(new MagicSkillUse(this, var2, var1.getDisplayId(), var10, var1.getHitTime(), var8));
                     }

                     if (!var1.isHideUseMessage()) {
                        if (var1.getSkillType() == Skill.SkillType.PET_SUMMON) {
                           this.sendPacket(new SystemMessage(547));
                        } else if (!var1.isHandler()) {
                           this.sendPacket(new SystemMessage(46).addSkillName(var3, var10));
                        } else {
                           this.sendPacket(new SystemMessage(46).addItemName(var1.getItemConsumeId()[0]));
                        }
                     }

                     if (!var1.isHandler()) {
                        this.disableSkill(var1, var8);
                     }

                     ThreadPoolManager.getInstance().schedule(new GameObjectTasks.AltMagicUseTask(this, var2, var1), var1.getHitTime());
                  }
               }
            }
         }
      }
   }

   public void sendReuseMessage(Skill var1) {
   }

   public void broadcastPacket(L2GameServerPacket... var1) {
      this.sendPacket(var1);
      this.broadcastPacketToOthers(var1);
   }

   public void broadcastPacket(List<L2GameServerPacket> var1) {
      this.sendPacket(var1);
      this.broadcastPacketToOthers(var1);
   }

   public void broadcastPacketToOthers(L2GameServerPacket... var1) {
      if (this.isVisible() && var1.length != 0) {
         List var2 = World.getAroundPlayers(this);

         for (int var4 = 0; var4 < var2.size(); var4++) {
            Player var3 = (Player)var2.get(var4);
            var3.sendPacket(var1);
         }
      }
   }

   public void broadcastPacketToOthers(List<L2GameServerPacket> var1) {
      if (this.isVisible() && !var1.isEmpty()) {
         List var2 = World.getAroundPlayers(this);

         for (int var4 = 0; var4 < var2.size(); var4++) {
            Player var3 = (Player)var2.get(var4);
            var3.sendPacket(var1);
         }
      }
   }

   public void broadcastToStatusListeners(L2GameServerPacket... var1) {
      if (this.isVisible() && var1.length != 0) {
         this.Eo.lock();

         try {
            if (this.En == null || this.En.isEmpty()) {
               return;
            }

            for (int var3 = 0; var3 < this.En.size(); var3++) {
               Player var2 = this.En.get(var3);
               var2.sendPacket(var1);
            }
         } finally {
            this.Eo.unlock();
         }
      }
   }

   public void broadCastCustomMessage(String var1, Player var2, Object... var3) {
      for (Player var5 : World.getAroundPlayers(var2)) {
         var5.sendMessage(new CustomMessage(var1, var5, var3));
      }

      var2.sendMessage(new CustomMessage(var1, var2, var3));
   }

   public void addStatusListener(Player var1) {
      if (var1 != this) {
         this.Eo.lock();

         try {
            if (this.En == null) {
               this.En = new LazyArrayList<>();
            }

            if (!this.En.contains(var1)) {
               this.En.add(var1);
            }
         } finally {
            this.Eo.unlock();
         }
      }
   }

   public void removeStatusListener(Creature var1) {
      this.Eo.lock();

      try {
         if (this.En != null) {
            this.En.remove(var1);
            return;
         }
      } finally {
         this.Eo.unlock();
      }
   }

   public void clearStatusListeners() {
      this.Eo.lock();

      try {
         if (this.En != null) {
            this.En.clear();
            return;
         }
      } finally {
         this.Eo.unlock();
      }
   }

   public StatusUpdate makeStatusUpdate(int... var1) {
      StatusUpdate var2 = new StatusUpdate(this.getObjectId());

      for (int var6 : var1) {
         switch (var6) {
            case 9:
               var2.addAttribute(var6, (int)this.getCurrentHp());
               break;
            case 10:
               var2.addAttribute(var6, this.getMaxHp());
               break;
            case 11:
               var2.addAttribute(var6, (int)this.getCurrentMp());
               break;
            case 12:
               var2.addAttribute(var6, this.getMaxMp());
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            default:
               break;
            case 26:
               var2.addAttribute(var6, this.getPvpFlag());
               break;
            case 27:
               var2.addAttribute(var6, this.getKarma());
               break;
            case 33:
               var2.addAttribute(var6, (int)this.getCurrentCp());
               break;
            case 34:
               var2.addAttribute(var6, this.getMaxCp());
         }
      }

      return var2;
   }

   public void broadcastStatusUpdate() {
      if (this.needStatusUpdate()) {
         StatusUpdate var1 = this.makeStatusUpdate(10, 12, 9, 11);
         this.broadcastToStatusListeners(var1);
      }
   }

   public int calcHeading(int var1, int var2) {
      return (int)(Math.atan2(this.getY() - var2, this.getX() - var1) * 10430.378350470453) + 32768;
   }

   public final double calcStat(Stats var1, double var2) {
      return this.calcStat(var1, var2, null, null);
   }

   public final double calcStat(Stats var1, double var2, Creature var4, Skill var5) {
      int var6 = var1.ordinal();
      Calculator var7 = this.Eg[var6];
      if (var7 == null) {
         return var2;
      }

      Env var8 = new Env();
      var8.character = this;
      var8.target = var4;
      var8.skill = var5;
      var8.value = var2;
      var7.calc(var8);
      return var8.value;
   }

   public final double calcStat(Stats var1, Creature var2, Skill var3) {
      Env var4 = new Env(this, var2, var3);
      var4.value = var1.getInit();
      int var5 = var1.ordinal();
      Calculator var6 = this.Eg[var5];
      if (var6 != null) {
         var6.calc(var4);
      }

      return var4.value;
   }

   public int calculateAttackDelay() {
      return Formulas.calcPAtkSpd(this.getPAtkSpd());
   }

   public void callSkill(Skill var1, List<Creature> var2, boolean var3) {
      try {
         if (var3 && !var1.isUsingWhileCasting() && this._triggers != null) {
            if (var1.isOffensive()) {
               if (var1.isMagic()) {
                  this.useTriggers(this.getTarget(), TriggerType.OFFENSIVE_MAGICAL_SKILL_USE, null, var1, 0.0);
               } else {
                  this.useTriggers(this.getTarget(), TriggerType.OFFENSIVE_PHYSICAL_SKILL_USE, null, var1, 0.0);
               }
            } else if (Config.BUFF_STICK_FOR_ALL || var1.isMagic()) {
               boolean var4 = var1.isAoE() || var1.isNotTargetAoE() || var1.getTargetType() == Skill.SkillTargetType.TARGET_SELF;
               this.useTriggers(var4 ? this : this.getTarget(), TriggerType.SUPPORT_MAGICAL_SKILL_USE, null, var1, 0.0);
            }
         }

         Player var13 = this.getPlayer();
         Iterator var6 = var2.iterator();

         while (var6.hasNext()) {
            Creature var5 = (Creature)var6.next();
            if (var1.isOffensive() && var5.isInvul()) {
               Player var7 = var5.getPlayer();
               if ((!var1.isIgnoreInvul() || var7 != null && var7.isGM()) && !var5.isArtefact()) {
                  var6.remove();
                  continue;
               }
            }

            Effect var14 = var5.getEffectList().getEffectByType(EffectType.IgnoreSkill);
            if (var14 != null && ArrayUtils.contains(var14.getTemplate().getParam().getIntegerArray("skillId"), var1.getId())) {
               var6.remove();
            } else {
               var5.getListeners().onMagicHit(var1, this);
               if (var13 != null && var5 != null && var5.isNpc()) {
                  NpcInstance var8 = (NpcInstance)var5;
                  List var9 = var13.getQuestsForEvent(var8, QuestEventType.MOB_TARGETED_BY_SKILL);
                  if (var9 != null) {
                     for (Object __raw_var11_795 : (Iterable<?>)(Object)(var9)) {
                        QuestState var11 = (QuestState)__raw_var11_795;
                        var11.getQuest().notifySkillUse(var8, var1, var11);
                     }
                  }
               }

               if (var1.getNegateSkill() > 0) {
                  for (Effect var16 : var5.getEffectList().getAllEffects()) {
                     Skill var17 = var16.getSkill();
                     if (var17.getId() == var1.getNegateSkill()
                        && var16.isCancelable()
                        && (var1.getNegatePower() <= 0 || var17.getPower() <= var1.getNegatePower())) {
                        var16.exit();
                     }
                  }
               }

               if (var1.getCancelTarget() > 0
                  && Rnd.chance(var1.getCancelTarget())
                  && (var5.getCastingSkill() == null || var5.getCastingSkill().getSkillType() != Skill.SkillType.TAKECASTLE)
                  && !var5.isRaid()) {
                  var5.abortAttack(true, true);
                  var5.abortCast(true, true);
                  var5.setTarget(null);
               }
            }
         }

         if (var1.isOffensive()) {
            this.startAttackStanceTask();
         }

         if (!var1.isNotTargetAoE() || !var1.isOffensive() || var2.size() != 0) {
            var1.getEffects(this, this, false, true);
         }

         var1.useSkill(this, var2);
      } catch (Exception var12) {
         _log.error("", var12);
      }
   }

   public void useTriggers(GameObject var1, TriggerType var2, Skill var3, Skill var4, double var5) {
      if (this._triggers != null) {
         Set var7 = this._triggers.get(var2);
         if (var7 != null) {
            for (Object __raw_var9_841 : (Iterable<?>)(Object)(var7)) {
               TriggerInfo var9 = (TriggerInfo)__raw_var9_841;
               if (var9.getSkill() != var3) {
                  this.useTriggerSkill(var1 == null ? this.getTarget() : var1, null, var9, var4, var5);
               }
            }
         }
      }
   }

   public void useTriggerSkill(GameObject var1, List<Creature> var2, TriggerInfo var3, Skill var4, double var5) {
      Skill var7 = var3.getSkill();
      if (var7.getReuseDelay() <= 0L || !this.isSkillDisabled(var7)) {
         Creature var8 = var7.getAimingTarget(this, var1);
         Creature var9 = var1 != null && var1.isCreature() ? (Creature)var1 : null;
         if (Rnd.chance(var3.getChance()) && var3.checkCondition(this, var9, var8, var4, var5) && var7.checkCondition(this, var8, false, true, true)) {
            if (var2 == null) {
               var2 = var7.getTargets(this, var8, false);
            }

            int var10 = 0;
            int var11 = 0;
            if (var7.hasEffects()) {
               var10 = var7.getEffectTemplates()[0]._displayId;
               var11 = var7.getEffectTemplates()[0]._displayLevel;
            }

            if (var10 == 0) {
               var10 = var7.getDisplayId();
            }

            if (var11 == 0) {
               var11 = var7.getDisplayLevel();
            }

            if (var3.getType() != TriggerType.SUPPORT_MAGICAL_SKILL_USE) {
               this.broadcastPacket(new MagicSkillLaunched(this, var10, var11, var2));
            }

            this.callSkill(var7, var2, false);
            this.disableSkill(var7, var7.getReuseDelay());
         }
      }
   }

   public boolean checkBlockedStat(Stats var1) {
      return this.Dz != null && this.Dz.contains(var1);
   }

   public boolean checkReflectSkill(Creature var1, Skill var2) {
      if (!var2.isReflectable()) {
         return false;
      }

      if (!this.isInvul() && !var1.isInvul() && var2.isOffensive()) {
         if (var2.isMagic() && var2.getSkillType() != Skill.SkillType.MDAM) {
            return false;
         } else if (Rnd.chance(this.calcStat(var2.isMagic() ? Stats.REFLECT_MAGIC_SKILL : Stats.REFLECT_PHYSIC_SKILL, 0.0, var1, var2))) {
            this.sendPacket(new SystemMessage(1998).addName(var1));
            var1.sendPacket(new SystemMessage(1999).addName(this));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void doCounterAttack(Skill var1, Creature var2, boolean var3) {
      if (!this.isDead()) {
         if (!this.isDamageBlocked() && !var2.isDamageBlocked()) {
            if (var1 != null && !var1.hasEffects() && !var1.isMagic() && var1.isOffensive() && var1.getCastRange() <= Config.COUNTERATTACK_MAX_SKILL_RANGE) {
               if (Rnd.chance(this.calcStat(Stats.COUNTER_ATTACK, 0.0, var2, var1))) {
                  double var4 = 700.94 * this.getPAtk(var2) / Math.max(var2.getPDef(this), 1);
                  var2.sendPacket(new SystemMessage(1997).addName(this));
                  if (var3) {
                     this.sendPacket(new SystemMessage(35).addNumber((long)var4));
                     this.sendPacket(new SystemMessage(35).addNumber((long)var4));
                     var2.reduceCurrentHp(var4, this, var1, true, true, false, false, false, false, true);
                  } else {
                     this.sendPacket(new SystemMessage(35).addNumber((long)var4));
                  }

                  var2.reduceCurrentHp(var4, this, var1, true, true, false, false, false, false, true);
               }
            }
         }
      }
   }

   public void disableSkill(Skill var1, long var2) {
      this._skillReuses.put(var1.hashCode(), new TimeStamp(var1, var2));
   }

   public abstract boolean isAutoAttackable(Creature var1);

   public void doAttack(Creature var1) {
      if (var1 != null
         && !this.isAMuted()
         && !this.isAttackingNow()
         && !this.isAlikeDead()
         && !var1.isAlikeDead()
         && this.isInRange(var1, 2048L)
         && (!this.isPlayer() || !this.getPlayer().isInMountTransform())) {
         this.getListeners().onAttack(var1);
         if (this.isPlayer()) {
            Player var2 = this.getPlayer();
            var2.triggerAfterTeleportProtection();
            var2.triggerNoCarrierProtection();
         }

         int var8 = Math.max(this.calculateAttackDelay(), Config.MIN_ATK_DELAY);
         int var3 = 0;
         int var4 = var8;
         if (this.isNpc()) {
             NpcMovementDebugTracer.getInstance().traceCombat((NpcInstance) this, "DO_ATTACK_START", var1,
                 "animDelay=" + var8 + "ms, pAtkSpd=" + this.getPAtkSpd() + ", dist=" + String.format("%.1f", this.getDistance(var1)));
         }
         this._attackEndTime = var8 + System.currentTimeMillis() - Config.ATTACK_END_DELAY;
         this._isAttackAborted = false;
         WeaponTemplate var5 = this.getActiveWeaponItem();
         if (var5 != null) {
            double dexRecoveryFactor = Formulas.calcDexAttackRecoveryFactor(this);
            int reuseDelay = (int)((var5.getAttackReuseDelay() / (this.getPAtkSpd() / 333.0F)) * dexRecoveryFactor);
            var4 = var8 + reuseDelay;
            if (this.isPlayer() && var5.getAttackReuseDelay() > 0 && var4 > 0) {
               this.sendPacket(new SetupGauge(this, 1, var4));
               this._attackReuseEndTime = var4 + System.currentTimeMillis() - Config.ATTACK_END_DELAY;
            }

            var3 = var5.getCrystalType().gradeOrd();
         }

         Attack var6 = new Attack(this, var1, this.getChargedSoulShot(), var3);
         if (!PositionUtils.isFacing(this, var1, 35)) {
            this.setHeading(PositionUtils.calculateHeadingFrom(this, var1));
         }
         int var7 = var4 / 2;
         if (var5 == null) {
            this.a(var6, var1, 1.0, !this.isPlayer(), var7, true);
         } else {
            switch (var5.getItemType()) {
               case BOW:
                  this.a(var6, var1, var7);
                  break;
               case POLE:
                  this.c(var6, var1, var7);
                  break;
               case DUAL:
               case DUALFIST:
                  this.b(var6, var1, var7);
                  break;
               default:
                  this.a(var6, var1, 1.0, true, var7, true);
            }
         }

         if (var6.hasHits()) {
            this.broadcastPacket(var6);
         }
      }
   }

   private void a(Attack var1, Creature var2, double var3, boolean var5, long var6, boolean var8) {
      int var9 = 0;
      boolean var10 = false;
      boolean var11 = false;
      boolean var12 = Formulas.calcHitMiss(this, var2);
      if (!var12) {
         Formulas.AttackInfo var13 = Formulas.calcPhysDam(this, var2, null, false, false, var1._soulshot, false);
         var9 = (int)(var13.damage * var3);
         var10 = var13.shld;
         var11 = var13.crit;
      }

      ThreadPoolManager.getInstance().schedule(new GameObjectTasks.HitTask(this, var2, var9, var11, var12, var1._soulshot, var10, var5, var8, var6), var6);
      var1.addHit(var2, var9, var12, var11, var10);
   }

   private void a(Attack var1, Creature var2, long var3) {
      WeaponTemplate var5 = this.getActiveWeaponItem();
      if (var5 != null) {
         int var6 = 0;
         boolean var7 = false;
         boolean var8 = false;
         boolean var9 = Formulas.calcHitMiss(this, var2);
         if (Config.ALT_CONSUME_ARROWS && (this.isPlayable() && !this.getPlayer().hasBonus() || Config.ALT_PA_CONSUME_ARROWS)) {
            this.reduceArrowCount();
         }

         if (!var9) {
            Formulas.AttackInfo var10 = Formulas.calcPhysDam(this, var2, null, false, false, var1._soulshot, false);
            var6 = (int)var10.damage;
            var7 = var10.shld;
            var8 = var10.crit;
         }

         ThreadPoolManager.getInstance().schedule(new GameObjectTasks.HitTask(this, var2, var6, var8, var9, var1._soulshot, var7, true, true, var3), var3);
         var1.addHit(var2, var6, var9, var8, var7);
      }
   }

   private void b(Attack var1, Creature var2, long var3) {
      int var5 = 0;
      int var6 = 0;
      boolean var7 = false;
      boolean var8 = false;
      boolean var9 = false;
      boolean var10 = false;
      boolean var11 = Formulas.calcHitMiss(this, var2);
      boolean var12 = Formulas.calcHitMiss(this, var2);
      if (!var11) {
         Formulas.AttackInfo var13 = Formulas.calcPhysDam(this, var2, null, true, false, var1._soulshot, false);
         var5 = (int)var13.damage;
         var7 = var13.shld;
         var9 = var13.crit;
      }

      if (!var12) {
         Formulas.AttackInfo var14 = Formulas.calcPhysDam(this, var2, null, true, false, var1._soulshot, false);
         var6 = (int)var14.damage;
         var8 = var14.shld;
         var10 = var14.crit;
      }

      ThreadPoolManager.getInstance().schedule(new GameObjectTasks.HitTask(this, var2, var5, var9, var11, var1._soulshot, var7, true, false), var3 / 2L);
      ThreadPoolManager.getInstance().schedule(new GameObjectTasks.HitTask(this, var2, var6, var10, var12, var1._soulshot, var8, false, true, var3), var3);
      var1.addHit(var2, var5, var11, var9, var7);
      var1.addHit(var2, var6, var12, var10, var8);
   }

   private void c(Attack var1, Creature var2, long var3) {
      int var5 = (int)this.calcStat(Stats.POLE_ATTACK_ANGLE, 90.0, var2, null);
      int var6 = (int)this.calcStat(Stats.POWER_ATTACK_RANGE, this.getTemplate().baseAtkRange, var2, null);
      int var7 = (int)Math.round(this.calcStat(Stats.POLE_TARGET_COUNT, 0.0, var2, null));
      if (this.isBoss()) {
         var7 += 27;
      } else if (this.isRaid()) {
         var7 += 12;
      } else if (this.isMonster() && this.getLevel() > 0) {
         var7 = (int)(var7 + this.getLevel() / 7.5);
      }

      double var8 = 1.0;
      this.Dx = 1;
      if (!this.isInZonePeace()) {
         for (Creature var11 : this.getAroundCharacters(var6, 200)) {
            if (this.Dx > var7) {
               break;
            }

            if (var11 != var2 && !var11.isDead() && PositionUtils.isFacing(this, var11, var5) && var11.isAutoAttackable(this)) {
               this.a(var1, var11, var8, false, var3, false);
               var8 *= Config.ALT_POLE_DAMAGE_MODIFIER;
               this.Dx++;
            }
         }
      }

      this.Dx = 0;
      this.a(var1, var2, 1.0, true, var3, true);
   }

   public long getAnimationEndTime() {
      return this.Dq;
   }

   public void doCast(Skill var1, Creature var2, boolean var3) {
      if (var1 != null) {
         int[] var4 = var1.getItemConsume();
         if (var4[0] > 0) {
            for (int var5 = 0; var5 < var4.length; var5++) {
               if (!this.consumeItem(var1.getItemConsumeId()[var5], var4[var5])) {
                  this.sendPacket(var1.isHandler() ? SystemMsg.INCORRECT_ITEM_COUNT : new SystemMessage(113).addSkillName(var1.getId(), var1.getLevel()));
                  return;
               }
            }
         }

         if (var1.getReferenceItemId() <= 0 || this.consumeItemMp(var1.getReferenceItemId(), var1.getReferenceItemMpConsume())) {
            int var15 = var1.getId();
            if (var2 == null) {
               var2 = var1.getAimingTarget(this, this.getTarget());
            }

            if (var2 != null) {
               this.getListeners().onMagicUse(var1, var2, false);
               if (this != var2) {
                  this.setHeading(PositionUtils.calculateHeadingFrom(this, var2));
               }

               int var6 = Math.max(1, this.getSkillDisplayLevel(var15));
               int var7 = var1.isSkillTimePermanent() ? var1.getHitTime() : Formulas.calcMAtkSpd(this, var1, var1.getHitTime());
               int var8 = var1.getSkillInterruptTime();
               int var9 = Math.min(Config.SKILLS_CAST_TIME_MIN, var1.getHitTime());
               if (var7 < var9) {
                  var7 = var9;
                  var8 = 0;
               }

               this.Dq = System.currentTimeMillis() + var7;
               if (var1.isMagic() && !var1.isSkillTimePermanent() && this.getChargedSpiritShot() > 0) {
                  var7 = (int)(0.7 * var7);
                  var8 = (int)(0.7 * var8);
               }

               Formulas.calcSkillMastery(var1, this);
               long var10 = Math.max(0L, Formulas.calcSkillReuseDelay(this, var1));
               this.broadcastPacket(new MagicSkillUse(this, var2, var1, var7, var10));
               if (!var1.isHandler()) {
                  this.disableSkill(var1, var10);
               }

               if (this.isPlayer()) {
                  if (var1.getSkillType() == Skill.SkillType.PET_SUMMON) {
                     this.sendPacket(Msg.SUMMON_A_PET);
                  } else if (!var1.isHandler()) {
                     this.sendPacket(new SystemMessage(46).addSkillName(var15, var6));
                  } else {
                     this.sendPacket(new SystemMessage(46).addItemName(var1.getItemConsumeId()[0]));
                  }
               }

               if (var1.getTargetType() == Skill.SkillTargetType.TARGET_HOLY) {
                  var2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, 1);
               }

               double var12 = var1.isUsingWhileCasting() ? var1.getMpConsume() : var1.getMpConsume1();
               if (var12 > 0.0) {
                  if (this._currentMp < var12) {
                     this.sendPacket(Msg.NOT_ENOUGH_MP);
                     this.onCastEndTime();
                     return;
                  }

                  this.reduceCurrentMp(var12, null);
               }

               this.Ep = null;
               switch (var1.getFlyType()) {
                  case DUMMY:
                  case CHARGE:
                     Location var14 = this.a(var2, var1);
                     if (var14 == null) {
                        this.Dq = 0L;
                        this.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                        return;
                     } else {
                        this.Ep = var14;
                        this.broadcastPacket(new FlyToLocation(this, var14, var1.getFlyType()));
                     }
                  default:
                     this.Do = var1;
                     int var16 = var8 > 0 ? Math.max(0, var7 - var8) : 0;
                     this.Dp = System.currentTimeMillis() + var16;
                     this.setCastingTarget(var2);
                     if (var1.isUsingWhileCasting()) {
                        this.callSkill(var1, var1.getTargets(this, var2, var3), true);
                     }

                     this._scheduledCastInterval = var7;
                     if (var7 > 333 && this.isPlayer()) {
                        this.sendPacket(new SetupGauge(this, 0, var7));
                     }

                     this.scheduleSkillLaunchedTask(var3, var16);
                     this.scheduleSkillUseTask(var3, var7);
               }
            }
         }
      }
   }

   protected void scheduleSkillLaunchedTask(boolean var1, int var2) {
      this._skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new GameObjectTasks.MagicLaunchedTask(this, var1), var2);
   }

   protected void scheduleSkillUseTask(boolean var1, int var2) {
      this._skillTask = ThreadPoolManager.getInstance().schedule(new GameObjectTasks.MagicUseTask(this, var1), var2);
   }

   public void clearCastVars() {
      this.Dq = 0L;
      this.Dp = 0L;
      this.Do = null;
      this._skillTask = null;
      this._skillLaunchedTask = null;
      this.Ep = null;
   }

   private Location a(GameObject var1, Skill var2) {
      if (var1 != null && var1 != this) {
         Location var7;
         if (var2.isFlyToBack()) {
            double var4 = PositionUtils.convertHeadingToRadian(var1.getHeading());
            var7 = new Location(var1.getX() + (int)(Math.sin(var4) * 40.0), var1.getY() - (int)(Math.cos(var4) * 40.0), var1.getZ());
         } else {
            double var8 = Math.atan2(this.getY() - var1.getY(), this.getX() - var1.getX());
            var7 = new Location(var1.getX() + (int)Math.round(Math.cos(var8) * 40.0), var1.getY() + (int)Math.round(Math.sin(var8) * 40.0), var1.getZ());
         }

         if (this.isFlying()) {
            if (this.isPlayer() && ((Player)this).isInFlyingTransform() && (var7.z <= 0 || var7.z >= 6000)) {
               return null;
            }

            if (GeoEngine.moveCheckInAir(this.getX(), this.getY(), this.getZ(), var7.x, var7.y, var7.z, this.getColRadius(), this.getGeoIndex()) == null) {
               return null;
            }
         } else {
            var7.correctGeoZ();
            if (!GeoEngine.canMoveToCoord(this.getX(), this.getY(), this.getZ(), var7.x, var7.y, var7.z, this.getGeoIndex())) {
               var7 = var1.getLoc();
               if (!GeoEngine.canMoveToCoord(this.getX(), this.getY(), this.getZ(), var7.x, var7.y, var7.z, this.getGeoIndex())) {
                  return null;
               }
            }
         }

         return var7;
      } else {
         double var3 = PositionUtils.convertHeadingToRadian(this.getHeading());
         int var5 = -((int)(Math.sin(var3) * var2.getFlyRadius()));
         int var6 = (int)(Math.cos(var3) * var2.getFlyRadius());
         return this.isFlying()
            ? GeoEngine.moveCheckInAir(
               this.getX(), this.getY(), this.getZ(), this.getX() + var5, this.getY() + var6, this.getZ(), this.getColRadius(), this.getGeoIndex()
            )
            : GeoEngine.moveCheck(this.getX(), this.getY(), this.getZ(), this.getX() + var5, this.getY() + var6, this.getGeoIndex());
      }
   }

   public final void doDie(Creature var1) {
      if (this.isDead.compareAndSet(false, true)) {
         this.onDeath(var1);
      }
   }

   protected void onDeath(Creature var1) {
      if (var1 != null) {
         Player var2 = var1.getPlayer();
         if (var2 != null) {
            var2.getListeners().onKillIgnorePetOrSummon(this);
         }

         var1.getListeners().onKill(this);
         if (this.isPlayer() && var1.isPlayable()) {
            this._currentCp = 0.0;
         }
      }

      this.setTarget(null);
      this.stopMove();
      this.stopAttackStanceTask();
      this.stopRegeneration();
      this._currentHp = 0.0;
      if (!this.isBlessedByNoblesse() && !this.isSalvation()) {
         if (Config.ALT_PASSIVE_NOBLESS_ID == 0 || this.getKnownSkill(Config.ALT_PASSIVE_NOBLESS_ID) == null) {
            for (Effect var6 : this.getEffectList().getAllEffects()) {
               if (var6.getEffectType() != EffectType.Transformation && !var6.getSkill().isPreservedOnDeath()) {
                  var6.exit();
               }
            }
         }
      } else {
         if (this.isSalvation() && this.isPlayer() && !this.getPlayer().isOlyParticipant() && !this.getPlayer().isResurectProhibited()) {
            this.getPlayer().reviveRequest(this.getPlayer(), 100.0, false, this.DC);
         }

         for (Effect var3 : this.getEffectList().getAllEffects()) {
            if (var3.getEffectType() == EffectType.BlessNoblesse || var3.getSkill().getId() == 1325 || var3.getSkill().getId() == 2168) {
               var3.exit();
            }
         }
      }

      ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(this, CtrlEvent.EVT_DEAD, var1, null));
      this.getListeners().onDeath(var1);
      this.updateEffectIcons();
      this.updateStats();
      this.broadcastStatusUpdate();
   }

   protected void onRevive() {
   }

   public void enableSkill(Skill var1) {
      this._skillReuses.remove(var1.hashCode());
   }

   public int getAbnormalEffect() {
      return this._abnormalEffects;
   }

   public int getAbnormalEffect2() {
      return this._abnormalEffects2;
   }

   public int getAbnormalEffect3() {
      return this._abnormalEffects3;
   }

   public int getAccuracy() {
      return (int)this.calcStat(Stats.ACCURACY_COMBAT, 0.0, null, null);
   }

   public Collection<Skill> getAllSkills() {
      return this._skills.values();
   }

   public final Skill[] getAllSkillsArray() {
      Collection var1 = this._skills.values();
      return (Skill[])var1.toArray(new Skill[var1.size()]);
   }

   public final double getAttackSpeedMultiplier() {
      return 1.1 * this.getPAtkSpd() / this.getTemplate().basePAtkSpd;
   }

   public int getBuffLimit() {
      return (int)this.calcStat(Stats.BUFF_LIMIT, Config.ALT_BUFF_LIMIT, null, null);
   }

   public Skill getCastingSkill() {
      return this.Do;
   }

   public int getCON() {
      return (int)this.calcStat(Stats.STAT_CON, this._template.baseCON, null, null);
   }

   public int getCriticalHit(Creature var1, Skill var2) {
      return (int)this.calcStat(Stats.CRITICAL_BASE, this._template.baseCritRate, var1, var2);
   }

   public double getMagicCriticalRate(Creature var1, Skill var2) {
      return this.calcStat(Stats.MCRITICAL_RATE, var1, var2);
   }

   public final double getCurrentCp() {
      return this._currentCp;
   }

   public final double getCurrentCpRatio() {
      return this.getCurrentCp() / this.getMaxCp();
   }

   public final double getCurrentCpPercents() {
      return this.getCurrentCpRatio() * 100.0;
   }

   public final boolean isCurrentCpFull() {
      return this.getCurrentCp() >= this.getMaxCp();
   }

   public final boolean isCurrentCpZero() {
      return this.getCurrentCp() < 1.0;
   }

   public final double getCurrentHp() {
      return this._currentHp;
   }

   public final double getCurrentHpRatio() {
      return this.getCurrentHp() / this.getMaxHp();
   }

   public final double getCurrentHpPercents() {
      return this.getCurrentHpRatio() * 100.0;
   }

   public final boolean isCurrentHpFull() {
      return this.getCurrentHp() >= this.getMaxHp();
   }

   public final boolean isCurrentHpZero() {
      return this.getCurrentHp() < 1.0;
   }

   public final double getCurrentMp() {
      return this._currentMp;
   }

   public final double getCurrentMpRatio() {
      return this.getCurrentMp() / this.getMaxMp();
   }

   public final double getCurrentMpPercents() {
      return this.getCurrentMpRatio() * 100.0;
   }

   public final boolean isCurrentMpFull() {
      return this.getCurrentMp() >= this.getMaxMp();
   }

   public final boolean isCurrentMpZero() {
      return this.getCurrentMp() < 1.0;
   }

   public int getDEX() {
      return (int)this.calcStat(Stats.STAT_DEX, this._template.baseDEX, null, null);
   }

   public int getEvasionRate(Creature var1) {
      return (int)this.calcStat(Stats.EVASION_RATE, 0.0, var1, null);
   }

   public int getINT() {
      return (int)this.calcStat(Stats.STAT_INT, this._template.baseINT, null, null);
   }

   public List<Creature> getAroundCharacters(int var1, int var2) {
      return !this.isVisible() ? Collections.emptyList() : World.getAroundCharacters(this, var1, var2);
   }

   public List<NpcInstance> getAroundNpc(int var1, int var2) {
      return !this.isVisible() ? Collections.emptyList() : World.getAroundNpc(this, var1, var2);
   }

   public boolean knowsObject(GameObject var1) {
      return World.getAroundObjectById(this, var1.getObjectId()) != null;
   }

   public final Skill getKnownSkill(int var1) {
      return this._skills.get(var1);
   }

   public final int getMagicalAttackRange(Skill var1) {
      return var1 != null ? (int)this.calcStat(Stats.MAGIC_ATTACK_RANGE, var1.getCastRange(), null, var1) : this.getTemplate().baseAtkRange;
   }

   public final int getMagicalAttackRange(double var1, Skill var3) {
      return var3 != null ? (int)this.calcStat(Stats.MAGIC_ATTACK_RANGE, var1, null, var3) : this.getTemplate().baseAtkRange;
   }

   public int getMAtk(Creature var1, Skill var2) {
      return var2 != null && var2.getMatak() > 0 ? var2.getMatak() : (int)this.calcStat(Stats.MAGIC_ATTACK, this._template.baseMAtk, var1, var2);
   }

   public int getMAtkSpd() {
      return (int)this.calcStat(Stats.MAGIC_ATTACK_SPEED, this._template.baseMAtkSpd, null, null);
   }

   public final int getMaxCp() {
      return (int)this.calcStat(Stats.MAX_CP, this._template.baseCpMax, null, null);
   }

   public int getMaxHp() {
      return (int)this.calcStat(Stats.MAX_HP, this._template.baseHpMax, null, null);
   }

   public int getMaxMp() {
      return (int)this.calcStat(Stats.MAX_MP, this._template.baseMpMax, null, null);
   }

   public int getMDef(Creature var1, Skill var2) {
      return Math.max((int)this.calcStat(Stats.MAGIC_DEFENCE, this._template.baseMDef, var1, var2), 1);
   }

   public int getMEN() {
      return (int)this.calcStat(Stats.STAT_MEN, this._template.baseMEN, null, null);
   }

   public double getMinDistance(GameObject var1) {
      double var2 = this.getTemplate().collisionRadius;
      if (var1 != null && var1.isCreature()) {
         var2 += ((Creature)var1).getTemplate().collisionRadius;
      }

      return var2;
   }

   public double getMovementSpeedMultiplier() {
      return this.isRunning() ? (double)this.getRunSpeed() / this._template.baseRunSpd : (double)this.getWalkSpeed() / this._template.baseWalkSpd;
   }

   @Override
   public int getMoveSpeed() {
      return this.isRunning() ? this.getRunSpeed() : this.getWalkSpeed();
   }

   @Override
   public String getName() {
      return StringUtils.defaultString(this._name);
   }

   public int getPAtk(Creature var1) {
      return (int)this.calcStat(Stats.POWER_ATTACK, this._template.basePAtk, var1, null);
   }

   public int getPAtkSpd() {
      return (int)this.calcStat(Stats.POWER_ATTACK_SPEED, this._template.basePAtkSpd, null, null);
   }

   public int getPDef(Creature var1) {
      return (int)this.calcStat(Stats.POWER_DEFENCE, this._template.basePDef, var1, null);
   }

   public int getPhysicalAttackRange() {
      WeaponTemplate var1 = this.getActiveWeaponItem();
      return var1 == null
         ? (int)this.calcStat(Stats.POWER_ATTACK_RANGE, this.getTemplate().baseAtkRange, null, null)
         : (int)this.calcStat(Stats.POWER_ATTACK_RANGE, var1.getAttackRange(), null, null);
   }

   @Deprecated
   public final int getRandomDamage() {
      WeaponTemplate var1 = this.getActiveWeaponItem();
      return var1 == null ? 5 + (int)Math.sqrt(this.getLevel()) : var1.getRandomDamage();
   }

   public double getReuseModifier(Creature var1) {
      return this.calcStat(Stats.ATK_REUSE, 1.0, var1, null);
   }

   public int getRunSpeed() {
      return this.getSpeed(this._template.baseRunSpd);
   }

   public final int getShldDef() {
      return this.isPlayer()
         ? (int)this.calcStat(Stats.SHIELD_DEFENCE, 0.0, null, null)
         : (int)this.calcStat(Stats.SHIELD_DEFENCE, this._template.baseShldDef, null, null);
   }

   public final int getSkillDisplayLevel(Integer var1) {
      Skill var2 = this._skills.get(var1);
      return var2 == null ? -1 : var2.getDisplayLevel();
   }

   public final int getSkillLevel(Integer var1) {
      return this.getSkillLevel(var1, -1);
   }

   public final int getSkillLevel(Integer var1, int var2) {
      Skill var3 = this._skills.get(var1);
      return var3 == null ? var2 : var3.getLevel();
   }

   public int getSkillMastery(Integer var1) {
      if (this.DA == null) {
         return 0;
      }

      Integer var2 = this.DA.get(var1);
      return var2 == null ? 0 : var2;
   }

   public void removeSkillMastery(Integer var1) {
      if (this.DA != null) {
         this.DA.remove(var1);
      }
   }

   public int getSpeed(int var1) {
      return this.isInWater() ? this.getSwimSpeed() : (int)this.calcStat(Stats.RUN_SPEED, var1, null, null);
   }

   public int getSTR() {
      return (int)this.calcStat(Stats.STAT_STR, this._template.baseSTR, null, null);
   }

   public int getSwimSpeed() {
      return (int)this.calcStat(Stats.RUN_SPEED, Config.SWIMING_SPEED, null, null);
   }

   public GameObject getTarget() {
      return this.target.get();
   }

   public final int getTargetId() {
      GameObject var1 = this.getTarget();
      return var1 == null ? -1 : var1.getObjectId();
   }

   public CharTemplate getTemplate() {
      return this._template;
   }

   public CharTemplate getBaseTemplate() {
      return this._baseTemplate;
   }

   public String getTitle() {
      return StringUtils.defaultString(this._title);
   }

   public final int getWalkSpeed() {
      return this.isInWater() ? this.getSwimSpeed() : this.getSpeed(this._template.baseWalkSpd);
   }

   public int getWIT() {
      return (int)this.calcStat(Stats.STAT_WIT, this._template.baseWIT, null, null);
   }

   public double headingToRadians(int var1) {
      return (var1 - 32768) / 10430.378350470453;
   }

   public boolean isAlikeDead() {
      return this.isDead();
   }

   public final long getAttackEndTime() {
      return this._attackEndTime;
   }

   public final boolean isAttackingNow() {
      return this._attackEndTime > System.currentTimeMillis();
   }

   public final boolean isBlessedByNoblesse() {
      return this.DB;
   }

   public final boolean isSalvation() {
      return this.DC >= 0;
   }

   public boolean isEffectImmune() {
      return this.DW.get();
   }

   public boolean isBuffImmune() {
      return this.DU.get();
   }

   public boolean isDebuffImmune() {
      return this.DV.get();
   }

   public boolean isDead() {
      return this._currentHp < 0.5 || this.isDead.get();
   }

   @Override
   public final boolean isFlying() {
      return this.DY;
   }

   public final boolean isInCombat() {
      return System.currentTimeMillis() < this.Dt;
   }

   public boolean isInvul() {
      return this._isInvul;
   }

   public boolean isMageClass() {
      return this.getTemplate().baseMAtk > 3;
   }

   public final boolean isRunning() {
      return this.DZ;
   }

   public boolean isSkillDisabled(Skill var1) {
      TimeStamp var2 = (TimeStamp)this._skillReuses.get(var1.hashCode());
      if (var2 == null) {
         return false;
      }

      if (var2.hasNotPassed()) {
         return true;
      }

      this._skillReuses.remove(var1.hashCode());
      return false;
   }

   public final boolean isTeleporting() {
      return this.isTeleporting.get();
   }

   public Location getDestination() {
      return this.moveAction != null && this.moveAction instanceof Creature.MoveToLocationAction
         ? ((Creature.MoveToLocationAction)this.moveAction).moveTo().clone()
         : null;
   }

   public Location getFinalDestination() {
      return this.moveAction != null && this.moveAction instanceof Creature.MoveToLocationAction
         ? ((Creature.MoveToLocationAction)this.moveAction).getFinalDest().clone()
         : null;
   }

   public boolean isMoving() {
      Creature.MoveActionBase var1 = this.moveAction;
      return var1 != null && !var1.isFinished();
   }

   public boolean isFollowing() {
      Creature.MoveActionBase var1 = this.moveAction;
      return var1 != null && var1 instanceof Creature.MoveToRelativeAction && !var1.isFinished();
   }

   public int maxZDiff() {
      Creature.MoveActionBase var1 = this.moveAction;
      if (var1 != null) {
         Location var2 = var1.moveFrom();
         Location var3 = var1.moveTo();
         if (var2.getZ() > var3.getZ()) {
            return var2.getZ() - var3.getZ();
         }
      }

      return Config.MAX_Z_DIFF;
   }

   public Creature getFollowTarget() {
      Creature.MoveActionBase var1 = this.moveAction;
      Creature.MoveToRelativeAction var2 = var1 != null ? var1.getAsMoveToRelative() : null;
      if (var2 != null && !var2.isFinished()) {
         GameObject var3 = var2.getTarget();
         if (var3 != null && var3 instanceof Creature) {
            return (Creature)var3;
         }
      }

      return null;
   }

   protected Creature.MoveToRelativeAction createMoveToRelative(GameObject var1, int var2, int var3, boolean var4) {
      return new Creature.MoveToRelativeAction(this, var1, !Config.ALLOW_GEODATA, var2, var3, var4);
   }

   protected Creature.MoveToLocationAction createMoveToLocation(Location var1, int var2, boolean var3) {
      return new Creature.MoveToLocationAction(this, this.getLoc(), var1, this.isInBoat() || this.isBoat() || !Config.ALLOW_GEODATA, var2, var3);
   }

   public boolean moveToLocation(Location var1, int var2, boolean var3) {
      return this.moveToLocation(var1.x, var1.y, var1.z, var2, var3);
   }

   public boolean moveToLocation(int var1, int var2, int var3, int var4, boolean var5) {
      this.Ea.lock();

      try {
         var4 = Math.max(var4, 0);
         Location var6 = new Location(var1, var2, var3);
         Creature.MoveActionBase var7 = this.moveAction;
         Creature.MoveToLocationAction var8 = var7 != null ? var7.getAsMoveToLocation() : null;
         if (var8 != null && var8.isSameDest(var6)) {
            this.sendActionFailed();
            return false;
         }

         if (this.isMovementDisabled()) {
            this.getAI().setNextAction(NextAction.MOVE, new Location(var1, var2, var3), var4, var5, false);
            this.sendActionFailed();
            return false;
         }

         this.getAI().clearNextAction();
         if (this.isPlayer()) {
            Player var9 = this.getPlayer();
            this.getAI().changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
            var9.triggerAfterTeleportProtection();
            var9.triggerNoCarrierProtection();
         }

         this.stopMove(false, false);
         var8 = this.createMoveToLocation(var6, var4, var5);
         this.moveAction = var8;
         if (!var8.start()) {
            this.moveAction = null;
            this.sendActionFailed();
            return false;
         } else {
            this.getListeners().onMove(var6);
            var8.scheduleNextTick();
            return true;
         }
      } finally {
         this.Ea.unlock();
      }
   }

   public boolean moveToRelative(GameObject var1, int var2, int var3) {
      return this.moveToRelative(var1, var2, var3, Config.ALLOW_PAWN_PATHFIND);
   }

   public boolean moveToRelative(GameObject var1, int var2, int var3, boolean var4) {
      this.Ea.lock();

      try {
         if (!this.isMovementDisabled() && var1 != null && !this.isInBoat()) {
            Creature.MoveActionBase var5 = this.moveAction;
            Creature.MoveToRelativeAction var6 = var5 != null ? var5.getAsMoveToRelative() : null;
            if (var6 != null && !var5.isFinished() && var6.isSameTarget(var1) && !var6.hasTargetMovedSignificantly()) {
               this.sendActionFailed();
               return false;
            }

            var3 = Math.max(var3, 10);
            var2 = Math.min(var2, var3);
            this.getAI().clearNextAction();
            if (this.isPlayer()) {
               Player var7 = this.getPlayer();
               var7.triggerAfterTeleportProtection();
               var7.triggerNoCarrierProtection();
            }

            this.stopMove(false, false);
            var6 = this.createMoveToRelative(var1, var2, var3, var4);
            this.moveAction = var6;
            if (!var6.start()) {
               this.moveAction = null;
               this.sendActionFailed();
               return false;
            } else {
               this.getListeners().onMove(var1.getLoc());
               var6.scheduleNextTick();
               return true;
            }
         } else {
            return false;
         }
      } finally {
         this.Ea.unlock();
      }
   }

   public void broadcastMove() {
      this.validateLocation(this.isPlayer() ? 2 : 1);
      this.broadcastPacket(this.movePacket());
   }

   public void stopMove() {
      this.stopMove(true, true);
   }

   public void stopMove(boolean var1) {
      this.stopMove(true, var1);
   }

   public void stopMove(boolean var1, boolean var2) {
      this.stopMove(var1, var2, true);
   }

   public void stopMove(boolean var1, boolean var2, boolean var3) {
      if (this.isMoving()) {
         if (this.isNpc() && NpcMovementDebugTracer.isTracking((NpcInstance) this)) {
            NpcMovementDebugTracer.getInstance().traceEvent((NpcInstance) this, "STOP_MOVE",
                "sendStop=" + var1 + ", validate=" + var2 + ", interrupt=" + var3 + ", pos=(" + this.getX() + "," + this.getY() + "," + this.getZ() + ")");
         }
         this.Ea.lock();

         try {
            if (this.isMoving()) {
               if (var3 && this.moveAction != null && !this.moveAction.isFinished()) {
                  this.moveAction.interrupt();
                  this.moveAction = null;
               }

               if (this.Eb != null) {
                  this.Eb.cancel(false);
                  this.Eb = null;
               }

               if (var2) {
                  this.validateLocation(this.isPlayer() ? 2 : 1);
               }

               if (var1) {
                  this.broadcastPacket(this.stopMovePacket());
               }

               return;
            }
         } finally {
            this.Ea.unlock();
         }
      }
   }

   public int getWaterZ() {
      return !this.isInWater() ? Integer.MIN_VALUE : this.getZones().getWaterZ();
   }

   protected L2GameServerPacket stopMovePacket() {
      return new StopMove(this);
   }

   public L2GameServerPacket movePacket() {
      Creature.MoveActionBase var1 = this.moveAction;
      return var1 != null ? var1.movePacket() : new CharMoveToLocation(this);
   }

   public boolean updateZones() {
      return this.isInObserverMode() ? false : this.getZones().update(this);
   }

   protected void onUpdateZones(List<Zone> var1, List<Zone> var2) {
      var1.forEach(var1x -> var1x.doLeave(this));
      var2.forEach(var1x -> var1x.doEnter(this));
   }

   public boolean isInZonePeace() {
      return this.isInZone(Zone.ZoneType.peace_zone) && !this.isInZoneBattle();
   }

   public boolean isInZoneBattle() {
      return this.isInZone(Zone.ZoneType.battle_zone);
   }

   public boolean isInWater() {
      return this.isInZone(Zone.ZoneType.water) && !this.isInBoat() && !this.isBoat() && !this.isFlying();
   }

   public Zones getZones() {
      return this.Em;
   }

   public boolean isInZone(Zone.ZoneType var1) {
      return this.getZones().isInZone(var1);
   }

   public boolean isInAnyZone(Zone.ZoneType... var1) {
      return this.getZones().isInAnyZone(var1);
   }

   public boolean isInZone(String var1) {
      return this.getZones().isInZone(var1);
   }

   public boolean isInZone(Zone var1) {
      return this.getZones().isInZone(var1);
   }

   public Zone getZone(Zone.ZoneType var1) {
      return this.getZones().getZone(var1);
   }

   public Location getRestartPoint() {
      for (Zone var2 : this.getZones()) {
         if (var2.getRestartPoints() != null
            && var2.isAnyType(Zone.ZoneType.battle_zone, Zone.ZoneType.peace_zone, Zone.ZoneType.offshore, Zone.ZoneType.dummy)) {
            return var2.getSpawn();
         }
      }

      return null;
   }

   public Location getPKRestartPoint() {
      for (Zone var2 : this.getZones()) {
         if (var2.getRestartPoints() != null
            && var2.isAnyType(Zone.ZoneType.battle_zone, Zone.ZoneType.peace_zone, Zone.ZoneType.offshore, Zone.ZoneType.dummy)) {
            return var2.getPKSpawn();
         }
      }

      return null;
   }

   @Override
   public int getGeoZ(Location var1) {
      return !this.isFlying() && !this.isInWater() && !this.isInBoat() && !this.isBoat() && !this.isDoor() ? super.getGeoZ(var1) : var1.z;
   }

   protected boolean needStatusUpdate() {
      if (!this.isVisible()) {
         return false;
      }

      boolean var1 = false;
      int var2 = (int)(this.getCurrentHp() * 352.0 / this.getMaxHp());
      if (var2 == 0 || var2 != this.Dv) {
         this.Dv = var2;
         var1 = true;
      }

      var2 = (int)(this.getCurrentMp() * 352.0 / this.getMaxMp());
      if (var2 == 0 || var2 != this.Dw) {
         this.Dw = var2;
         var1 = true;
      }

      if (this.isPlayer()) {
         var2 = (int)(this.getCurrentCp() * 352.0 / this.getMaxCp());
         if (var2 == 0 || var2 != this.Du) {
            this.Du = var2;
            var1 = true;
         }
      }

      return var1;
   }

   @Override
   public void onForcedAttack(Player var1, boolean var2) {
      var1.sendPacket(new MyTargetSelected(this.getObjectId(), var1.getLevel() - this.getLevel()));
      if (this.isAttackable(var1) && !var1.isConfused() && !var1.isBlocked()) {
         var1.getAI().Attack(this, true, var2);
      } else {
         var1.sendActionFailed();
      }
   }

   public void onHitTimer(Creature var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      if (this.isAlikeDead()) {
         this.sendActionFailed();
      } else if (!var1.isDead() && this.isInRange(var1, 2000L)) {
         if (this.isPlayable() && var1.isPlayable() && this.isInZoneBattle() != var1.isInZoneBattle()) {
            Player var9 = this.getPlayer();
            if (var9 != null) {
               var9.sendPacket(Msg.INVALID_TARGET);
               var9.sendActionFailed();
            }
         } else {
            var1.getListeners().onAttackHit(this);
            if (!var4
               && var1.isPlayer()
               && (
                  this.isCursedWeaponEquipped()
                     || this.getActiveWeaponInstance() != null && this.getActiveWeaponInstance().isHeroWeapon() && var1.isCursedWeaponEquipped()
               )) {
               var1.setCurrentCp(0.0);
            }

            if (var1.isStunned() && Formulas.calcStunBreak(var3)) {
               var1.getEffectList().stopEffects(EffectType.Stun);
            }

            this.displayGiveDamageMessage(var1, var2, var3, var4, var6, false);
            ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(var1, CtrlEvent.EVT_ATTACKED, this, var2));
            boolean var8 = this.checkPvP(var1, null);
            if (!var4 && var2 > 0) {
               var1.reduceCurrentHp(var2, this, null, true, true, false, true, false, false, true);
               if (!var1.isDead()) {
                  if (var3) {
                     this.useTriggers(var1, TriggerType.CRIT, null, null, var2);
                  }

                  this.useTriggers(var1, TriggerType.ATTACK, null, null, var2);
                  if (Formulas.calcCastBreak(var1, var3)) {
                     var1.abortCast(false, true);
                  }
               }

               if (var5 && var7) {
                  this.unChargeShots(false);
               }
            }

            if (var4) {
               var1.useTriggers(this, TriggerType.UNDER_MISSED_ATTACK, null, null, var2);
            }

            this.startAttackStanceTask();
            if (var8) {
               this.startPvPFlag(var1);
            }
         }
      } else {
         this.sendActionFailed();
      }
   }

   public void onMagicUseTimer(Creature var1, Skill var2, boolean var3) {
      this.Dp = 0L;
      if (var2.isUsingWhileCasting()) {
         var1.getEffectList().stopEffect(var2.getId());
         this.onCastEndTime();
      } else {
         if (!var2.isOffensive() && this.getAggressionTarget() != null) {
            var3 = true;
         }

         if (!var2.checkCondition(this, var1, var3, false, false)) {
            if (var2.getSkillType() == Skill.SkillType.PET_SUMMON && this.isPlayer()) {
               this.getPlayer().setPetControlItem(null);
            }

            this.onCastEndTime();
         } else {
            List var4 = var2.getTargets(this, var1, var3);
            int var5 = var2.getHpConsume();
            if (var5 > 0) {
               this.setCurrentHp(Math.max(0.0, this._currentHp - var5), false);
            }

            double var6 = var2.getMpConsume2();
            if (var6 > 0.0) {
               if (var2.isMusic()) {
                  var6 += this.getEffectList().getActiveMusicCount(var2.getId()) * var6 / 2.0;
                  var6 = this.calcStat(Stats.MP_DANCE_SKILL_CONSUME, var6, var1, var2);
               } else if (var2.isMagic()) {
                  var6 = this.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, var6, var1, var2);
               } else {
                  var6 = this.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, var6, var1, var2);
               }

               if (this._currentMp < var6 && this.isPlayable()) {
                  this.sendPacket(Msg.NOT_ENOUGH_MP);
                  this.onCastEndTime();
                  return;
               }

               this.reduceCurrentMp(var6, null);
            }

            this.callSkill(var2, var4, true);
            if (var2.getNumCharges() > 0) {
               this.setIncreasedForce(this.getIncreasedForce() - var2.getNumCharges());
            }

            if (var2.isSoulBoost()) {
               this.setConsumedSouls(this.getConsumedSouls() - Math.min(this.getConsumedSouls(), 5), null);
            } else if (var2.getSoulsConsume() > 0) {
               this.setConsumedSouls(this.getConsumedSouls() - var2.getSoulsConsume(), null);
            }

            switch (var2.getFlyType()) {
               case THROW_UP:
               case THROW_HORIZONTAL:
                  for (Object __raw_var10_2142 : (Iterable<?>)(Object)(var4)) {
                     Creature var10 = (Creature)__raw_var10_2142;
                     Location var8 = this.a(null, var2);
                     var10.setLoc(var8);
                     this.broadcastPacket(new FlyToLocation(var10, var8, var2.getFlyType()));
                  }
               default:
                  int var13 = Formulas.calcMAtkSpd(this, var2, var2.getCoolTime());
                  if (var13 > 0) {
                     ThreadPoolManager.getInstance().schedule(new GameObjectTasks.CastEndTimeTask(this), var13);
                  } else {
                     this.onCastEndTime();
                  }
            }
         }
      }
   }

   public void onCastEndTime() {
      this.bW();
      Skill var1 = this.getCastingSkill();
      Creature var2 = this.getCastingTarget();
      this.clearCastVars();
      this.getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING, var1, var2);
   }

   private void bW() {
      Location var1 = this.Ep;
      this.Ep = null;
      if (var1 != null) {
         this.setLoc(var1);
         this.validateLocation(1);
      }
   }

   public void reduceCurrentHp(
      double var1, Creature var3, Skill var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9, boolean var10, boolean var11
   ) {
      if (var3 != null && !this.isDead() && (!var3.isDead() || var10)) {
         if (!this.isDamageBlocked() || !var9) {
            if (this.isDamageBlocked() && var3 != this) {
               if (var11) {
                  var3.sendPacket(Msg.THE_ATTACK_HAS_BEEN_BLOCKED);
               }
            } else {
               if (var8) {
                  if (var3.absorbAndReflect(this, var4, var1, var11)) {
                     return;
                  }

                  var1 = this.absorbToEffector(var3, var1);
                  var1 = this.absorbToSummon(var3, var1);
               }

               this.getListeners().onCurrentHpDamage(var1, var3, var4);
               if (var3 != this) {
                  if (var11) {
                     this.displayReceiveDamageMessage(var3, (int)var1);
                  }

                  if (!var10) {
                     this.useTriggers(var3, TriggerType.RECEIVE_DAMAGE, null, null, var1);
                  }
               }

               this.onReduceCurrentHp(var1, var3, var4, var5, var6, var7);
            }
         }
      }
   }

   protected void onReduceCurrentHp(double var1, Creature var3, Skill var4, boolean var5, boolean var6, boolean var7) {
      if (var5 && this.isSleeping()) {
         this.getEffectList().stopEffects(EffectType.Sleep);
      }

      boolean var8 = this.isUndying(var3);
      if (var3 != this || var4 != null && var4.isOffensive()) {
         if (this.isMeditated()) {
            Effect var9 = this.getEffectList().getEffectByType(EffectType.Meditation);
            if (var9 != null) {
               this.getEffectList().stopEffect(var9.getSkill());
            }
         }

         this.startAttackStanceTask();
         this.checkAndRemoveInvisible();
         if (this.getCurrentHp() - var1 < 0.5 && !var8) {
            this.useTriggers(var3, TriggerType.DIE, null, null, var1);
         }
      }

      this.setCurrentHp(Math.max(this.getCurrentHp() - var1, var8 ? 0.5 : 0.0), false);
      if (!var8 && this.getCurrentHp() < 0.5) {
         this.doDie(var3);
      }
   }

   public void reduceCurrentMp(double var1, Creature var3) {
      this.reduceCurrentMp(var1, var3, false);
   }

   public void reduceCurrentMp(double var1, Creature var3, boolean var4) {
      if (var3 != null && var3 != this) {
         if (this.isSleeping()) {
            this.getEffectList().stopEffects(EffectType.Sleep);
         }

         if (this.isMeditated()) {
            Effect var5 = this.getEffectList().getEffectByType(EffectType.Meditation);
            if (var5 != null) {
               this.getEffectList().stopEffect(var5.getSkill());
            }
         }
      }

      if (this.isDamageBlocked() && var3 != null && var3 != this) {
         var3.sendPacket(Msg.THE_ATTACK_HAS_BEEN_BLOCKED);
      } else {
         if (var3 != null && var3.isPlayer() && Math.abs(var3.getLevel() - this.getLevel()) > 10) {
            if (var3.getKarma() > 0 && this.getEffectList().getEffectsBySkillId(5182) != null && !this.isInZone(Zone.ZoneType.SIEGE)) {
               return;
            }

            if (this.getKarma() > 0 && var3.getEffectList().getEffectsBySkillId(5182) != null && !var3.isInZone(Zone.ZoneType.SIEGE)) {
               return;
            }
         }

         this.getListeners().onCurrentMpReduce(var1, var3);
         if (var4) {
            int var7 = (int)Math.min(this._currentMp, var1);
            this.sendPacket(new SystemMessage(1866).addNumber(var7));
            if (var3 != null && var3.isPlayer()) {
               var3.sendPacket(new SystemMessage(1867).addNumber(var7));
            }
         }

         var1 = Math.max(0.0, this._currentMp - var1);
         this.setCurrentMp(var1);
         if (var3 != null && var3 != this) {
            this.startAttackStanceTask();
         }
      }
   }

   public double relativeSpeed(GameObject var1) {
      return this.getMoveSpeed() - var1.getMoveSpeed() * Math.cos(this.headingToRadians(this.getHeading()) - this.headingToRadians(var1.getHeading()));
   }

   public void removeAllSkills() {
      for (Skill var4 : this.getAllSkillsArray()) {
         this.removeSkill(var4);
      }
   }

   public void removeBlockStats(List<Stats> var1) {
      if (this.Dz != null) {
         this.Dz.removeAll(var1);
         if (this.Dz.isEmpty()) {
            this.Dz = null;
         }
      }
   }

   public Skill removeSkill(Skill var1) {
      return var1 == null ? null : this.removeSkillById(var1.getId());
   }

   public Skill removeSkillById(Integer var1) {
      Skill var2 = this._skills.remove(var1);
      if (var2 != null) {
         this.removeTriggers(var2);
         this.removeStatsOwner(var2);
         if (Config.ALT_DELETE_SA_BUFFS && (var2.isItemSkill() || var2.isHandler())) {
            List var3 = this.getEffectList().getEffectsBySkill(var2);
            if (var3 != null) {
               for (Object __raw_var5_2318 : (Iterable<?>)(Object)(var3)) {
                  Effect var5 = (Effect)__raw_var5_2318;
                  var5.exit();
               }
            }

            Summon var8 = this.getPet();
            if (var8 != null) {
               var3 = var8.getEffectList().getEffectsBySkill(var2);
               if (var3 != null) {
                  for (Object __raw_var6_2327 : (Iterable<?>)(Object)(var3)) {
                     Effect var6 = (Effect)__raw_var6_2327;
                     var6.exit();
                  }
               }
            }
         }
      }

      return var2;
   }

   public void addTriggers(StatTemplate var1) {
      if (!var1.getTriggerList().isEmpty()) {
         for (TriggerInfo var3 : var1.getTriggerList()) {
            this.addTrigger(var3);
         }
      }
   }

   public void addTrigger(TriggerInfo var1) {
      if (this._triggers == null) {
         this._triggers = new ConcurrentHashMap<>();
      }

      Set var2 = this._triggers.get(var1.getType());
      if (var2 == null) {
         var2 = new CopyOnWriteArraySet();
         this._triggers.put(var1.getType(), var2);
      }

      var2.add(var1);
      if (var1.getType() == TriggerType.ADD) {
         this.useTriggerSkill(this, null, var1, null, 0.0);
      }
   }

   public void removeTriggers(StatTemplate var1) {
      if (this._triggers != null && !var1.getTriggerList().isEmpty()) {
         for (TriggerInfo var3 : var1.getTriggerList()) {
            this.removeTrigger(var3);
         }
      }
   }

   public void removeTrigger(TriggerInfo var1) {
      if (this._triggers != null) {
         Set var2 = this._triggers.get(var1.getType());
         if (var2 != null) {
            var2.remove(var1);
         }
      }
   }

   public void sendActionFailed() {
      this.sendPacket(ActionFail.STATIC);
   }

   public boolean hasAI() {
      return this._ai != null;
   }

   public CharacterAI getAI() {
      if (this._ai == null) {
         synchronized (this) {
            if (this._ai == null) {
               this._ai = new CharacterAI(this);
            }
         }
      }

      return this._ai;
   }

   public void setAI(CharacterAI var1) {
      if (var1 != null) {
         CharacterAI var2 = this._ai;
         synchronized (this) {
            this._ai = var1;
         }

         if (var2 != null && var2.isActive()) {
            var2.stopAITask();
            var1.startAITask();
            var1.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
         }
      }
   }

   public final void setCurrentHp(double var1, boolean var3, boolean var4) {
      int var5 = this.getMaxHp();
      var1 = Math.min(var5, Math.max(0.0, var1));
      if (this._currentHp != var1) {
         if (!(var1 >= 0.5) || !this.isDead() || var3) {
            double var6 = this._currentHp;
            this._currentHp = var1;
            if (this.isDead.compareAndSet(true, false)) {
               this.onRevive();
            }

            this.checkHpMessages(var6, this._currentHp);
            if (var4) {
               this.broadcastStatusUpdate();
               this.sendChanges();
            }

            if (this._currentHp < var5) {
               this.startRegeneration();
            }

            if (this._currentHp > var6 && this.listeners != null) {
               this.listeners.onCurrentHpHeal(this._currentHp - var6, null, null);
            }
         }
      }
   }

   public final void setCurrentHp(double var1, boolean var3) {
      this.setCurrentHp(var1, var3, true);
   }

   public final void setCurrentMp(double var1, boolean var3) {
      int var4 = this.getMaxMp();
      var1 = Math.min(var4, Math.max(0.0, var1));
      if (this._currentMp != var1) {
         if (!(var1 >= 0.5) || !this.isDead()) {
            this._currentMp = var1;
            if (var3) {
               this.broadcastStatusUpdate();
               this.sendChanges();
            }

            if (this._currentMp < var4) {
               this.startRegeneration();
            }
         }
      }
   }

   public final void setCurrentMp(double var1) {
      this.setCurrentMp(var1, true);
   }

   public final void setCurrentCp(double var1, boolean var3) {
      if (this.isPlayer()) {
         int var4 = this.getMaxCp();
         var1 = Math.min(var4, Math.max(0.0, var1));
         if (this._currentCp != var1) {
            if (!(var1 >= 0.5) || !this.isDead()) {
               this._currentCp = var1;
               if (var3) {
                  this.broadcastStatusUpdate();
                  this.sendChanges();
               }

               if (this._currentCp < var4) {
                  this.startRegeneration();
               }
            }
         }
      }
   }

   public final void setCurrentCp(double var1) {
      this.setCurrentCp(var1, true);
   }

   public void setCurrentHpMp(double var1, double var3, boolean var5) {
      int var6 = this.getMaxHp();
      int var7 = this.getMaxMp();
      var1 = Math.min(var6, Math.max(0.0, var1));
      var3 = Math.min(var7, Math.max(0.0, var3));
      if (this._currentHp != var1 || this._currentMp != var3) {
         if (!(var1 >= 0.5) || !this.isDead() || var5) {
            double var8 = this._currentHp;
            this._currentHp = var1;
            this._currentMp = var3;
            if (this.isDead.compareAndSet(true, false)) {
               this.onRevive();
            }

            this.checkHpMessages(var8, this._currentHp);
            this.broadcastStatusUpdate();
            this.sendChanges();
            if (this._currentHp < var6 || this._currentMp < var7) {
               this.startRegeneration();
            }
         }
      }
   }

   public void setCurrentHpMp(double var1, double var3) {
      this.setCurrentHpMp(var1, var3, false);
   }

   public final void setFlying(boolean var1) {
      this.DY = var1;
   }

   @Override
   public final int getHeading() {
      return this.Ef;
   }

   public void setHeading(int var1) {
      this.Ef = var1;
   }

   public final void setIsTeleporting(boolean var1) {
      this.isTeleporting.compareAndSet(!var1, var1);
   }

   public final void setName(String var1) {
      this._name = var1;
   }

   public Creature getCastingTarget() {
      return this.Ed.get();
   }

   public void setCastingTarget(Creature var1) {
      if (var1 == null) {
         this.Ed = HardReferences.emptyRef();
      } else {
         this.Ed = var1.getRef();
      }
   }

   public final void setRunning() {
      if (!this.DZ) {
         this.DZ = true;
         this.broadcastPacket(new ChangeMoveType(this));
      }
   }

   public void setSkillMastery(Integer var1, int var2) {
      if (this.DA == null) {
         this.DA = new HashMap<>();
      }

      this.DA.put(var1, var2);
   }

   public void setAggressionTarget(Creature var1) {
      if (var1 == null) {
         this.Ee = HardReferences.emptyRef();
      } else {
         this.Ee = var1.getRef();
      }
   }

   public Creature getAggressionTarget() {
      return this.Ee.get();
   }

   public void setTarget(GameObject var1) {
      if (var1 != null && !var1.isVisible()) {
         var1 = null;
      }

      if (var1 == null) {
         this.target = HardReferences.emptyRef();
      } else {
         this.target = var1.getRef();
      }
   }

   public void setTitle(String var1) {
      this._title = var1;
   }

   public void setWalking() {
      if (this.DZ) {
         this.DZ = false;
         this.broadcastPacket(new ChangeMoveType(this));
      }
   }

   public void startAbnormalEffect(AbnormalEffect var1) {
      if (var1 == AbnormalEffect.NULL) {
         this._abnormalEffects = AbnormalEffect.NULL.getMask();
         this._abnormalEffects2 = AbnormalEffect.NULL.getMask();
         this._abnormalEffects3 = AbnormalEffect.NULL.getMask();
      } else if (var1.isSpecial()) {
         this._abnormalEffects2 = this._abnormalEffects2 | var1.getMask();
      } else if (var1.isEvent()) {
         this._abnormalEffects3 = this._abnormalEffects3 | var1.getMask();
      } else {
         this._abnormalEffects = this._abnormalEffects | var1.getMask();
      }

      this.sendChanges();
   }

   public void startAttackStanceTask() {
      this.startAttackStanceTask0();
   }

   protected void startAttackStanceTask0() {
      if (this.isInCombat()) {
         this.Dt = System.currentTimeMillis() + 15000L;
      } else {
         this.Dt = System.currentTimeMillis() + 15000L;
         this.broadcastPacket(new AutoAttackStart(this.getObjectId()));
         Future var1 = this.Dr;
         if (var1 != null) {
            var1.cancel(false);
         }

         this.Dr = LazyPrecisionTaskManager.getInstance()
            .scheduleAtFixedRate(this.Ds == null ? (this.Ds = new Creature.AttackStanceTask()) : this.Ds, 1000L, 1000L);
      }
   }

   public void stopAttackStanceTask() {
      this.Dt = 0L;
      Future var1 = this.Dr;
      if (var1 != null) {
         var1.cancel(false);
         this.Dr = null;
         this.broadcastPacket(new AutoAttackStop(this.getObjectId()));
      }
   }

   protected void stopRegeneration() {
      this.Ei.lock();

      try {
         if (this.Eh) {
            this.Eh = false;
            if (this.Ej != null) {
               this.Ej.cancel(false);
               this.Ej = null;
            }
         }
      } finally {
         this.Ei.unlock();
      }
   }

   protected void startRegeneration() {
      if (this.isVisible() && !this.isDead() && this.getRegenTick() != 0L) {
         if (!this.Eh) {
            this.Ei.lock();

            try {
               if (!this.Eh) {
                  this.Eh = true;
                  this.Ej = RegenTaskManager.getInstance()
                     .scheduleAtFixedRate(this.Ek == null ? (this.Ek = new Creature.RegenTask()) : this.Ek, 0L, this.getRegenTick());
               }
            } finally {
               this.Ei.unlock();
            }
         }
      }
   }

   public long getRegenTick() {
      return 3000L;
   }

   public void stopAbnormalEffect(AbnormalEffect var1) {
      if (var1.isSpecial()) {
         this._abnormalEffects2 = this._abnormalEffects2 & ~var1.getMask();
      }

      if (var1.isEvent()) {
         this._abnormalEffects3 = this._abnormalEffects3 & ~var1.getMask();
      } else {
         this._abnormalEffects = this._abnormalEffects & ~var1.getMask();
      }

      this.sendChanges();
   }

   public void setUndying(SpecialEffectState var1) {
      this.El = var1;
   }

   public boolean isUndying(Creature var1) {
      return this.El != SpecialEffectState.FALSE;
   }

   public void block() {
      this.DF = true;
   }

   public void unblock() {
      this.DF = false;
   }

   public boolean startConfused() {
      return this.DQ.getAndSet(true);
   }

   public boolean stopConfused() {
      return this.DQ.setAndGet(false);
   }

   public boolean startFear() {
      return this.DG.getAndSet(true);
   }

   public boolean stopFear() {
      return this.DG.setAndGet(false);
   }

   public boolean startMuted() {
      return this.DH.getAndSet(true);
   }

   public boolean stopMuted() {
      return this.DH.setAndGet(false);
   }

   public boolean startPMuted() {
      return this.DI.getAndSet(true);
   }

   public boolean stopPMuted() {
      return this.DI.setAndGet(false);
   }

   public boolean startAMuted() {
      return this.DJ.getAndSet(true);
   }

   public boolean stopAMuted() {
      return this.DJ.setAndGet(false);
   }

   public boolean startRooted() {
      return this.DL.getAndSet(true);
   }

   public boolean stopRooted() {
      return this.DL.setAndGet(false);
   }

   public boolean startSleeping() {
      return this.DN.getAndSet(true);
   }

   public boolean stopSleeping() {
      return this.DN.setAndGet(false);
   }

   public boolean startStunning() {
      return this.DO.getAndSet(true);
   }

   public boolean stopStunning() {
      return this.DO.setAndGet(false);
   }

   public boolean startParalyzed() {
      return this.DK.getAndSet(true);
   }

   public boolean stopParalyzed() {
      return this.DK.setAndGet(false);
   }

   public boolean startImmobilized() {
      return this.DP.getAndSet(true);
   }

   public boolean stopImmobilized() {
      return this.DP.setAndGet(false);
   }

   public boolean startHealBlocked() {
      return this.DS.getAndSet(true);
   }

   public boolean stopHealBlocked() {
      return this.DS.setAndGet(false);
   }

   public boolean startDamageBlocked() {
      return this.DT.getAndSet(true);
   }

   public boolean stopDamageBlocked() {
      return this.DT.setAndGet(false);
   }

   public boolean startBuffImmunity() {
      return this.DU.getAndSet(true);
   }

   public boolean stopBuffImmunity() {
      return this.DU.setAndGet(false);
   }

   public boolean startDebuffImmunity() {
      return this.DV.getAndSet(true);
   }

   public boolean stopDebuffImmunity() {
      return this.DV.setAndGet(false);
   }

   public boolean startEffectImmunity() {
      return this.DW.getAndSet(true);
   }

   public boolean stopEffectImmunity() {
      return this.DW.setAndGet(false);
   }

   public boolean startWeaponEquipBlocked() {
      return this.DX.getAndSet(true);
   }

   public boolean stopWeaponEquipBlocked() {
      return this.DX.getAndSet(false);
   }

   public boolean startFrozen() {
      return this.DR.getAndSet(true);
   }

   public boolean stopFrozen() {
      return this.DR.setAndGet(false);
   }

   public void setMeditated(boolean var1) {
      this.DD = var1;
   }

   public final void setIsBlessedByNoblesse(boolean var1) {
      this.DB = var1;
   }

   public final void setSalvationWindowTime(int var1) {
      this.DC = var1;
   }

   public void setIsInvul(boolean var1) {
      this._isInvul = var1;
   }

   public void setLockedTarget(boolean var1) {
      this.DE = var1;
   }

   public boolean isConfused() {
      return this.DQ.get();
   }

   public boolean isAfraid() {
      return this.DG.get();
   }

   public boolean isBlocked() {
      return this.DF;
   }

   public boolean isMuted(Skill var1) {
      return var1 != null && !var1.isNotAffectedByMute() ? this.isMMuted() && var1.isMagic() || this.isPMuted() && !var1.isMagic() : false;
   }

   public boolean isPMuted() {
      return this.DI.get();
   }

   public boolean isMMuted() {
      return this.DH.get();
   }

   public boolean isAMuted() {
      return this.DJ.get();
   }

   public boolean isRooted() {
      return this.DL.get();
   }

   public boolean isSleeping() {
      return this.DN.get();
   }

   public boolean isStunned() {
      return this.DO.get();
   }

   public boolean isMeditated() {
      return this.DD;
   }

   public boolean isWeaponEquipBlocked() {
      return this.DX.get();
   }

   public boolean isParalyzed() {
      return this.DK.get();
   }

   public boolean isFrozen() {
      return this.DR.get();
   }

   public boolean isImmobilized() {
      return this.DP.get() || this.getRunSpeed() < 1;
   }

   public boolean isHealBlocked() {
      return this.isAlikeDead() || this.DS.get();
   }

   public boolean isDamageBlocked() {
      return this.isInvul() || this.DT.get();
   }

   public boolean isCastingNow() {
      return this._skillTask != null;
   }

   public boolean isLockedTarget() {
      return this.DE;
   }

   public boolean isMovementDisabled() {
      return this.isBlocked()
         || this.isRooted()
         || this.isImmobilized()
         || this.isAlikeDead()
         || this.isStunned()
         || this.isSleeping()
         || this.isParalyzed()
         || this.isAttackingNow()
         || this.isCastingNow()
         || this.isFrozen();
   }

   public boolean isActionsDisabled() {
      return this.isBlocked()
         || this.isAlikeDead()
         || this.isStunned()
         || this.isSleeping()
         || this.isParalyzed()
         || this.isAttackingNow()
         || this.isCastingNow()
         || this.isFrozen();
   }

   public boolean isPotionsDisabled() {
      return this.isActionsDisabled() || this.isStunned() || this.isSleeping() || this.isParalyzed() || this.isAlikeDead() || this.isAfraid();
   }

   public final boolean isAttackingDisabled() {
      return this._attackReuseEndTime > System.currentTimeMillis();
   }

   public boolean isOutOfControl() {
      return this.isBlocked() || this.isConfused() || this.isAfraid() || this.isFrozen();
   }

   public void teleToLocation(Location var1) {
      this.teleToLocation(var1.x, var1.y, var1.z, this.getReflection());
   }

   public void teleToLocation(Location var1, int var2) {
      this.teleToLocation(var1.x, var1.y, var1.z, var2);
   }

   public void teleToLocation(Location var1, Reflection var2) {
      this.teleToLocation(var1.x, var1.y, var1.z, var2);
   }

   public void teleToLocation(int var1, int var2, int var3) {
      this.teleToLocation(var1, var2, var3, this.getReflection());
   }

   public void checkAndRemoveInvisible() {
      InvisibleType var1 = this.getInvisibleType();
      if (var1 == InvisibleType.EFFECT) {
         this.getEffectList().stopEffects(EffectType.Invisible);
      }
   }

   public void teleToLocation(int var1, int var2, int var3, int var4) {
      Reflection var5 = ReflectionManager.getInstance().get(var4);
      if (var5 != null) {
         this.teleToLocation(var1, var2, var3, var5);
      }
   }

   public void teleToLocation(int var1, int var2, int var3, Reflection var4) {
      if (this.isTeleporting.compareAndSet(false, true)) {
         this.abortCast(true, false);
         if (!this.isLockedTarget()) {
            this.setTarget(null);
         }

         this.stopMove(true, true, false);
         if (!this.isBoat() && !this.isFlying() && !World.isWater(new Location(var1, var2, var3), var4)) {
            var3 = GeoEngine.getHeight(var1, var2, var3, var4.getGeoIndex());
         }

         if (this.isPlayer() && DimensionalRiftManager.getInstance().checkIfInRiftZone(this.getLoc(), true)) {
            Player var5 = (Player)this;
            if (var5.isInParty() && var5.getParty().isInDimensionalRift()) {
               Location var6 = DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords();
               var1 = var6.x;
               var2 = var6.y;
               var3 = var6.z;
               var5.getParty().getDimensionalRift().usedTeleport(var5);
            }
         }

         if (this.isPlayer()) {
            Player var7 = (Player)this;
            if (var7.isProcessingRequest()) {
               var7.getRequest().cancel();
            }
            var7.getListeners().onTeleport(var1, var2, var3, var4);
            this.decayMe();
            this.setXYZ(var1, var2, var3);
            this.setReflection(var4);
            var7.setLastClientPosition(null);
            var7.setLastServerPosition(null);
            var7.sendPacket(new TeleportToLocation(var7, var1, var2, var3));
         } else {
            this.setXYZ(var1, var2, var3);
            this.setReflection(var4);
            this.broadcastPacket(new TeleportToLocation(this, var1, var2, var3));
            this.onTeleported();
         }
      }
   }

   public boolean onTeleported() {
      return this.isTeleporting.compareAndSet(true, false);
   }

   public void sendMessage(CustomMessage var1) {
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "[" + this.getObjectId() + "]";
   }

   @Override
   public double getColRadius() {
      return this.getTemplate().collisionRadius;
   }

   @Override
   public double getColHeight() {
      return this.getTemplate().collisionHeight;
   }

   public EffectList getEffectList() {
      if (this._effectList == null) {
         synchronized (this) {
            if (this._effectList == null) {
               this._effectList = new EffectList(this);
            }
         }
      }

      return this._effectList;
   }

   public boolean paralizeOnAttack(Creature var1) {
      int var2 = 65535;
      MonsterInstance var3;
      if (!this.isRaid() && (!this.isMinion() || (var3 = ((MinionInstance)this).getLeader()) == null || !var3.isRaid())) {
         if (this.isNpc()) {
            int var4 = ((NpcInstance)this).getParameter("ParalizeOnAttack", -1000);
            if (var4 != -1000) {
               var2 = this.getLevel() + var4;
            }
         }
      } else {
         var2 = this.getLevel() + Config.RAID_MAX_LEVEL_DIFF;
      }

      return var1.getLevel() > var2;
   }

   @Override
   protected void onDelete() {
      GameObjectsStorage.remove(this._storedId);
      this.getEffectList().stopAllEffects();
      super.onDelete();
   }

   public void addExpAndSp(long var1, long var3) {
   }

   public void broadcastCharInfo() {
   }

   public void checkHpMessages(double var1, double var3) {
   }

   public boolean checkPvP(Creature var1, Skill var2) {
      return false;
   }

   public boolean consumeItem(int var1, long var2) {
      return true;
   }

   public boolean consumeItemMp(int var1, int var2) {
      return true;
   }

   public boolean isFearImmune() {
      return false;
   }

   public boolean isLethalImmune() {
      return this.getMaxHp() >= 50000;
   }

   public boolean getChargedSoulShot() {
      return false;
   }

   public int getChargedSpiritShot() {
      return 0;
   }

   public int getIncreasedForce() {
      return 0;
   }

   public int getConsumedSouls() {
      return 0;
   }

   public int getAgathionEnergy() {
      return 0;
   }

   public void setAgathionEnergy(int var1) {
   }

   public int getKarma() {
      return 0;
   }

   public double getLevelMod() {
      return 1.0;
   }

   public int getNpcId() {
      return 0;
   }

   public Summon getPet() {
      return null;
   }

   public int getPvpFlag() {
      return 0;
   }

   public void setTeam(TeamType var1) {
      this._team = var1;
      this.sendChanges();
   }

   public TeamType getTeam() {
      return this._team;
   }

   public boolean isUndead() {
      return false;
   }

   public boolean isParalyzeImmune() {
      return false;
   }

   public void reduceArrowCount() {
   }

   public void sendChanges() {
      this.getStatsRecorder().sendChanges();
   }

   public void sendMessage(String var1) {
   }

   public void sendPacket(IStaticPacket var1) {
   }

   public void sendPacket(IStaticPacket... var1) {
   }

   public void sendPacket(List<? extends IStaticPacket> var1) {
   }

   public void setIncreasedForce(int var1) {
   }

   public void setConsumedSouls(int var1, NpcInstance var2) {
   }

   public void startPvPFlag(Creature var1) {
   }

   public boolean unChargeShots(boolean var1) {
      return false;
   }

   public void updateEffectIcons() {
   }

   public final boolean isBulkBuffing() {
      return this._bulkBuffDepth.get() > 0;
   }

   public final void setStatsModifiedInBulk(boolean modified) {
      this._statsModifiedInBulk = modified;
   }

   public BulkBuffScope openBulkBuffScope() {
      this._bulkBuffDepth.incrementAndGet();
      return () -> {
         if (this._bulkBuffDepth.decrementAndGet() == 0) {
            this.updateEffectIcons();
            if (this._statsModifiedInBulk) {
               this._statsModifiedInBulk = false;
               this.updateStats();
               if (this.isPlayer()) {
                  this.getPlayer().sendChanges();
                  this.getPlayer().broadcastCharInfo();
               } else if (this.isPet() || this.isSummon()) {
                  this.broadcastStatusUpdate();
               }
            }
         }
      };
   }

   protected void refreshHpMpCp() {
      int var1 = this.getMaxHp();
      int var2 = this.getMaxMp();
      int var3 = this.isPlayer() ? this.getMaxCp() : 0;
      if (this._currentHp > var1) {
         this.setCurrentHp(var1, false);
      }

      if (this._currentMp > var2) {
         this.setCurrentMp(var2, false);
      }

      if (this._currentCp > var3) {
         this.setCurrentCp(var3, false);
      }

      if (this._currentHp < var1 || this._currentMp < var2 || this._currentCp < var3) {
         this.startRegeneration();
      }
   }

   public void updateStats() {
      this.refreshHpMpCp();
      this.sendChanges();
   }

   public void setOverhitAttacker(Creature var1) {
   }

   public void setOverhitDamage(double var1) {
   }

   public boolean isCursedWeaponEquipped() {
      return false;
   }

   public boolean isHero() {
      return false;
   }

   public int getAccessLevel() {
      return 0;
   }

   public Clan getClan() {
      return null;
   }

   public double getRateAdena() {
      return 1.0;
   }

   public double getRateItems() {
      return 1.0;
   }

   public double getRateExp() {
      return 1.0;
   }

   public double getRateSp() {
      return 1.0;
   }

   public double getRateSpoil() {
      return 1.0;
   }

   public int getFormId() {
      return 0;
   }

   public boolean isNameAbove() {
      return true;
   }

   @Override
   public void setLoc(Location var1) {
      this.setXYZ(var1.x, var1.y, var1.z);
   }

   public void setLoc(Location var1, boolean var2) {
      this.setXYZ(var1.x, var1.y, var1.z, var2);
   }

   @Override
   public void setXYZ(int var1, int var2, int var3) {
      this.setXYZ(var1, var2, var3, false);
   }

   public void setXYZ(int var1, int var2, int var3, boolean var4) {
      if (!var4) {
         this.stopMove();
      }

      this.Ea.lock();

      try {
         super.setXYZ(var1, var2, var3);
      } finally {
         this.Ea.unlock();
      }

      this.updateZones();
   }

   @Override
   protected void onSpawn() {
      super.onSpawn();
      this.updateStats();
      this.updateZones();
   }

   @Override
   public void spawnMe(Location var1) {
      if (var1.h > 0) {
         this.setHeading(var1.h);
      }

      try {
         super.spawnMe(var1);
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }

   @Override
   protected void onDespawn() {
      if (!this.isLockedTarget()) {
         this.setTarget(null);
      }

      this.stopMove();
      this.stopAttackStanceTask();
      this.stopRegeneration();
      this.updateZones();
      this.clearStatusListeners();
      super.onDespawn();
   }

   public final void doDecay() {
      if (this.isDead()) {
         this.onDecay();
      }
   }

   protected void onDecay() {
      this.decayMe();
   }

   public void validateLocation(int var1) {
      ValidateLocation var2 = new ValidateLocation(this);
      if (var1 == 0) {
         this.sendPacket(var2);
      } else if (var1 == 1) {
         this.broadcastPacket(var2);
      } else {
         this.broadcastPacketToOthers(var2);
      }
   }

   public void addUnActiveSkill(Skill var1) {
      if (var1 != null && !this.isUnActiveSkill(var1.getId())) {
         this.removeStatsOwner(var1);
         this.removeTriggers(var1);
         this.Eq.add(var1.getId());
      }
   }

   public void removeUnActiveSkill(Skill var1) {
      if (var1 != null && this.isUnActiveSkill(var1.getId())) {
         this.addStatFuncs(var1.getStatFuncs());
         this.addTriggers(var1);
         this.Eq.remove(var1.getId());
      }
   }

   public boolean isUnActiveSkill(int var1) {
      return this.Eq.contains(var1);
   }

   public abstract int getLevel();

   public abstract ItemInstance getActiveWeaponInstance();

   public abstract WeaponTemplate getActiveWeaponItem();

   public abstract ItemInstance getSecondaryWeaponInstance();

   public abstract WeaponTemplate getSecondaryWeaponItem();

   public CharListenerList getListeners() {
      if (this.listeners == null) {
         synchronized (this) {
            if (this.listeners == null) {
               this.listeners = new CharListenerList(this);
            }
         }
      }

      return this.listeners;
   }

   public <T extends Listener<Creature>> boolean addListener(T var1) {
      return this.getListeners().add(var1);
   }

   public <T extends Listener<Creature>> boolean removeListener(T var1) {
      return this.getListeners().remove(var1);
   }

   public CharStatsChangeRecorder<? extends Creature> getStatsRecorder() {
      if (this._statsRecorder == null) {
         synchronized (this) {
            if (this._statsRecorder == null) {
               this._statsRecorder = new CharStatsChangeRecorder<>(this);
            }
         }
      }

      return this._statsRecorder;
   }

   @Override
   public boolean isCreature() {
      return true;
   }

   public void displayGiveDamageMessage(Creature var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      if (var4 && var1.isPlayer() && !var1.isDamageBlocked()) {
         var1.sendPacket(new SystemMessage(42).addName(this));
      }
   }

   public void displayReceiveDamageMessage(Creature var1, int var2) {
   }

   public Collection<TimeStamp> getSkillReuses() {
      return this._skillReuses.values();
   }

   public TimeStamp getSkillReuse(Skill var1) {
      return (TimeStamp)this._skillReuses.get(var1.hashCode());
   }

   public AutoFarmContext getFarmSystem() {
      return null;
   }

   private class AttackStanceTask extends RunnableImpl {
      private AttackStanceTask() {
      }

      @Override
      public void runImpl() throws Exception {
         if (!Creature.this.isInCombat()) {
            Creature.this.stopAttackStanceTask();
         }
      }
   }

   protected static class CreatureMoveActionTask extends RunnableImpl {
      private final HardReference<? extends Creature> Er;

      public CreatureMoveActionTask(Creature var1) {
         this.Er = var1.getRef();
      }

      @Override
      public void runImpl() throws Exception {
         Creature var1 = this.Er.get();
         if (var1 != null) {
            var1.Ea.lock();

            try {
               Creature.MoveActionBase var2 = var1.moveAction;
               if (var1.Ec == this && var2 != null && !var2.isFinished() && var2.f(var1) && var1.Ec == this) {
                  var2.scheduleNextTick();
               }
            } finally {
               var1.Ea.unlock();
            }
         }
      }
   }

   protected abstract static class MoveActionBase {
      private final HardReference<? extends Creature> Es;
      private final boolean Et;
      private long Eu;
      private int Ev;
      private double Ew;
      protected volatile boolean isFinished = false;

      public MoveActionBase(Creature var1) {
         this.Es = var1.getRef();
         this.Et = var1.isPlayable();
         this.Eu = 0L;
         this.Ev = 0;
         this.Ew = 0.0;
         this.isFinished = false;
      }

      protected boolean isForPlayable() {
         return this.Et;
      }

      protected Creature getActor() {
         return this.Es.get();
      }

      protected void setIsFinished(boolean var1) {
         this.isFinished = var1;
      }

      public boolean isFinished() {
         return this.isFinished;
      }

      protected long getPrevTick() {
         return this.Eu;
      }

      protected void setPrevTick(long var1) {
         this.Eu = var1;
      }

      protected int getPrevSpeed() {
         return this.Ev;
      }

      protected void setPrevSpeed(int var1) {
         this.Ev = var1;
      }

      protected double getPassDist() {
         return this.Ew;
      }

      protected void setPassDist(double var1) {
         this.Ew = var1;
      }

      public boolean start() {
         Creature var1 = this.getActor();
         if (var1 == null) {
            return false;
         }

         this.setPrevTick(System.currentTimeMillis());
         this.setPrevSpeed(var1.getMoveSpeed());
         this.setPassDist(0.0);
         this.setIsFinished(false);
         return this.weightCheck(var1);
      }

      public abstract Location moveFrom();

      public abstract Location moveTo();

      protected double getMoveLen() {
         return PositionUtils.calculateDistance(this.moveFrom(), this.moveTo(), this.includeMoveZ());
      }

      protected boolean includeMoveZ() {
         Creature var1 = this.getActor();
         return var1 == null || var1.isInWater() || var1.isFlying() || var1.isBoat() || var1.isInBoat();
      }

      public int getNextTickInterval() {
         return !this.isForPlayable()
            ? Math.min(Config.MOVE_TASK_QUANTUM_NPC, (int)(1000.0 * (this.getMoveLen() - this.getPassDist()) / Math.max(this.getPrevSpeed(), 1)))
            : Math.min(Config.MOVE_TASK_QUANTUM_PC, (int)(1000.0 * (this.getMoveLen() - this.getPassDist()) / Math.max(this.getPrevSpeed(), 1)));
      }

      protected boolean onEnd() {
         return true;
      }

      protected void onFinish(boolean var1, boolean var2) {
         this.setIsFinished(true);
      }

      public void interrupt() {
         this.tick();
         this.onFinish(false, true);
      }

      protected boolean onTick(double var1) {
         Creature var3 = this.getActor();
         if (var3 == null) {
            this.onFinish(false, true);
            return false;
         } else {
            return true;
         }
      }

      public boolean scheduleNextTick() {
         Creature var1 = this.getActor();
         if (var1 == null) {
            return false;
         }

         Runnable var2 = var1.Ec;
         var1.Ec = var2 = new Creature.CreatureMoveActionTask(var1);
         var1.Eb = ThreadPoolManager.getInstance().schedule(var2, this.getNextTickInterval());
         return true;
      }

      public boolean tick() {
         Creature var1 = this.getActor();
         if (var1 == null) {
            return false;
         }

         var1.Ea.lock();

         try {
            return this.f(var1);
         } finally {
            var1.Ea.unlock();
         }
      }

      private boolean f(Creature var1) {
         if (this.isFinished()) {
            return false;
         }

         if (var1.moveAction != this) {
            this.setIsFinished(true);
            return false;
         }

         if (var1.isMovementDisabled()) {
            this.onFinish(false, false);
            return false;
         }

         int var2 = var1.getMoveSpeed();
         if (var2 <= 0) {
            this.onFinish(false, false);
            return false;
         }

         long var3 = System.currentTimeMillis();
         long elapsed = var3 - this.getPrevTick();
         if (elapsed <= 0) {
            return true;
         }
         float var5 = (float)elapsed / 1000.0F;
         boolean var6 = this.includeMoveZ();
         double var7 = this.getPassDist();
         var7 += (double)var5 * (Math.max(this.getPrevSpeed() + var2, 2) / 2.0);
         this.setPrevTick(var3);
         this.setPrevSpeed(var2);
         this.setPassDist(var7);
         double var9 = this.getMoveLen();
         double var11 = Math.max(0.0, Math.min(var7 / Math.max(var9, 1.0), 1.0));
         Location var13 = var1.getLoc();
         Location var14 = var13.clone();
         if (!this.calcMidDest(var1, var14, var6, var11, var7, var9)) {
            this.onFinish(false, false);
            return false;
         }

         if (!var6) {
         }

         var1.setLoc(var14, true);
         if (var11 == 1.0) {
            return !this.onEnd();
         } else if (!this.onTick(var11)) {
            this.setIsFinished(true);
            return false;
         } else {
            return true;
         }
      }

      protected boolean weightCheck(Creature var1) {
         if (!var1.isPlayer()) {
            return true;
         } else if (var1.getPlayer().getCurrentLoad() >= 2 * var1.getPlayer().getMaxLoad()) {
            var1.sendPacket(new SystemMessage(555));
            return false;
         } else {
            return true;
         }
      }

      protected boolean calcMidDest(Creature var1, Location var2, boolean var3, double var4, double var6, double var8) {
         var2.set(this.moveTo().clone().indent(this.moveFrom(), (int)Math.round(var8 - var6), var1.isFlying() || var1.isInWater())).correctGeoZ();
         return true;
      }

      public abstract L2GameServerPacket movePacket();

      public Creature.MoveToLocationAction getAsMoveToLocation() {
         return null;
      }

      public Creature.MoveToRelativeAction getAsMoveToRelative() {
         return null;
      }
   }

   public abstract static class MoveToAction extends Creature.MoveActionBase {
      protected final int indent;
      protected final boolean pathFind;
      protected final boolean ignoreGeo;
      protected Queue<List<Location>> geoPathLines;
      protected List<Location> currentGeoPathLine;
      protected Location moveFrom;
      protected Location moveTo;
      protected double prevMoveLen;
      protected boolean prevIncZ;

      protected MoveToAction(Creature var1, boolean var2, int var3, boolean var4) {
         super(var1);
         this.indent = var3;
         this.pathFind = var4;
         this.ignoreGeo = var2;
         this.geoPathLines = new LinkedList<>();
         this.currentGeoPathLine = Collections.emptyList();
         this.moveFrom = var1.getLoc();
         this.moveTo = var1.getLoc();
         this.prevMoveLen = 0.0;
         this.prevIncZ = false;
      }

      protected boolean buildPathLines(Location var1, Location var2) {
         Creature var3 = this.getActor();
         if (var3 == null) {
            return false;
         }

         LinkedList var4 = new LinkedList();
         if (!GeoMove.buildGeoPath(
            var4,
            var1.clone().world2geo(),
            var2.clone().world2geo(),
            var3.getGeoIndex(),
            (int)var3.getColRadius(),
            (int)var3.getColHeight(),
            this.indent,
            this.pathFind && !this.ignoreGeo,
            this.isForPlayable(),
            var3.isFlying(),
            var3.isInWater(),
            var3.getWaterZ(),
            this.ignoreGeo
         )) {
            return false;
         }

         this.geoPathLines.clear();
         this.geoPathLines.addAll(var4);
         return true;
      }

      protected Queue<List<Location>> getGeoPathLines() {
         return this.geoPathLines;
      }

      public List<Location> currentGeoPathLine(List<Location> var1) {
         return this.currentGeoPathLine = var1;
      }

      public List<Location> currentGeoPathLine() {
         return this.currentGeoPathLine;
      }

      public boolean isPrevIncZ() {
         return this.prevIncZ;
      }

      public void setPrevIncZ(boolean var1) {
         this.prevIncZ = var1;
      }

      public double getPrevMoveLen() {
         return this.prevMoveLen;
      }

      public void setPrevMoveLen(double var1) {
         this.prevMoveLen = var1;
      }

      protected boolean pollPathLine() {
         if (this.currentGeoPathLine(this.getGeoPathLines().poll()) != null) {
            Creature var1 = this.getActor();
            Location var2 = this.currentGeoPathLine().get(0).clone().geo2world();
            Location var3 = this.currentGeoPathLine().get(this.currentGeoPathLine().size() - 1).clone().geo2world();
            this.setMoveFrom(var2);
            this.setMoveTo(var3);
            this.setPrevIncZ(this.includeMoveZ());
            this.setPrevMoveLen(PositionUtils.calculateDistance(var2, var3, this.isPrevIncZ()));
            this.setPassDist(0.0);
            this.setPrevTick(System.currentTimeMillis());
            if (this.getPrevMoveLen() > 16.0) {
               var1.setHeading(PositionUtils.calculateHeadingFrom(var2.getX(), var2.getY(), var3.getX(), var3.getY()));
            }

            return true;
         } else {
            return false;
         }
      }

      protected int remainingLinesCount() {
         return this.geoPathLines.size();
      }

      protected abstract boolean isRelativeMove();

      @Override
      protected boolean calcMidDest(Creature var1, Location var2, boolean var3, double var4, double var6, double var8) {
         if (this.currentGeoPathLine == null) {
            return false;
         }

         Location var10 = var1.getLoc();
         if (!(var8 < 16.0) && var4 != 0.0 && var6 != 0.0 && !this.currentGeoPathLine.isEmpty()) {
            int var11 = this.currentGeoPathLine.size() - 1;
            var2.set(this.moveFrom)
               .indent(this.moveTo, (int)(var6 + 0.5), var3)
               .setZ(this.currentGeoPathLine.get(Math.min(var11, (int)(var11 * var4 + 0.5))).getZ());
            if (var2.equalsGeo(var10) || this.ignoreGeo || !Config.ALLOW_GEODATA) {
               return true;
            } else {
               return var3
                  ? true
                  : GeoEngine.canMoveToCoord(var10.getX(), var10.getY(), var10.getZ(), var2.getX(), var2.getY(), var2.getZ(), var1.getGeoIndex());
            }
         } else {
            var2.set(var10);
            return true;
         }
      }

      @Override
      public Location moveFrom() {
         return this.moveFrom;
      }

      @Override
      public Location moveTo() {
         return this.moveTo;
      }

      protected void setMoveFrom(Location var1) {
         this.moveFrom = var1;
      }

      protected void setMoveTo(Location var1) {
         this.moveTo = var1;
      }

      @Override
      protected double getMoveLen() {
         boolean var1 = this.includeMoveZ();
         if (var1 != this.prevIncZ) {
            this.prevMoveLen = PositionUtils.calculateDistance(this.moveFrom, this.moveTo, var1);
            this.prevIncZ = var1;
         }

         return this.prevMoveLen;
      }
   }

   public static class MoveToLocationAction extends Creature.MoveToAction {
      private final Location Ex;
      private final Location Ey;

      public MoveToLocationAction(Creature var1, Location var2, Location var3, boolean var4, int var5, boolean var6) {
         super(var1, var4, var5, var6);
         this.Ey = var2.clone();
         this.Ex = var3.clone();
      }

      public MoveToLocationAction(Creature var1, Location var2, int var3, boolean var4) {
         this(var1, var1.getLoc(), var2, var1.isBoat() || var1.isInBoat(), var3, var4);
      }

      public boolean isSameDest(Location var1) {
         return this.Ex.equalsGeo(var1);
      }

      public Location getFinalDest() {
         return this.Ex;
      }

      @Override
      public boolean start() {
         if (!super.start()) {
            return false;
         } else {
            return !this.buildPathLines(this.Ey, this.Ex) ? false : !this.onEnd();
         }
      }

      @Override
      protected boolean onEnd() {
         Creature var1 = this.getActor();
         if (var1 == null) {
            return true;
         } else if (!this.pollPathLine()) {
            this.onFinish(true, false);
            return true;
         } else {
            var1.broadcastMove();
            return false;
         }
      }

      @Override
      protected void onFinish(boolean var1, boolean var2) {
         Creature var3 = this.getActor();
         if (!this.isFinished() && var3 != null) {
            if (var2) {
               this.setIsFinished(true);
            } else {
               if (var1) {
                  ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(var3, CtrlEvent.EVT_ARRIVED));
               } else {
                  var3.stopMove(true, true, false);
                  ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(var3, CtrlEvent.EVT_ARRIVED_BLOCKED, var3.getLoc()));
               }

               super.onFinish(var1, var2);
            }
         }
      }

      @Override
      public L2GameServerPacket movePacket() {
         Creature var1 = this.getActor();
         return var1 != null ? new CharMoveToLocation(var1, var1.getLoc(), this.moveTo().clone()) : null;
      }

      @Override
      protected boolean isRelativeMove() {
         return false;
      }

      @Override
      protected boolean pollPathLine() {
         if (this.currentGeoPathLine(this.getGeoPathLines().poll()) == null) {
            return false;
         }

         Creature var1 = this.getActor();
         Location var2 = this.currentGeoPathLine().get(0).clone().geo2world();
         Location var3 = this.currentGeoPathLine().get(this.currentGeoPathLine().size() - 1).clone().geo2world();
         this.setMoveFrom(var2);
         this.setMoveTo(this.isForPlayable() && this.remainingLinesCount() == 0 && this.getFinalDest().equalsGeo(var3) ? this.getFinalDest() : var3);
         this.setPrevIncZ(this.includeMoveZ());
         this.setPrevMoveLen(PositionUtils.calculateDistance(var2, var3, this.isPrevIncZ()));
         this.setPassDist(0.0);
         this.setPrevTick(System.currentTimeMillis());
         if (this.getPrevMoveLen() > 16.0) {
            var1.setHeading(PositionUtils.calculateHeadingFrom(var2.getX(), var2.getY(), var3.getX(), var3.getY()));
         }

         return true;
      }

      @Override
      public Creature.MoveToLocationAction getAsMoveToLocation() {
         return this;
      }
   }

   public static class MoveToRelativeAction extends Creature.MoveToAction {
      private final HardReference<? extends GameObject> Ez;
      private Location EA;
      private boolean EB;
      private final int EC;

      protected MoveToRelativeAction(Creature var1, GameObject var2, boolean var3, int var4, int var5, boolean var6) {
         super(var1, var3, var4, var6);
         this.Ez = var2.getRef();
         this.EA = var2.getLoc().clone();
         this.EC = Math.max(var5, var4 + 16);
         this.EB = false;
      }

      private GameObject getTarget() {
         return this.Ez.get();
      }

      public boolean isSameTarget(GameObject var1) {
         return this.getTarget() == var1;
      }

      public boolean hasTargetMovedSignificantly() {
         GameObject target = this.getTarget();
         if (target == null) {
            return true;
         }
         return this.EA.distance(target.getLoc()) > 64.0;
      }

      @Override
      public boolean start() {
         if (!super.start()) {
            return false;
         }

         Creature var1 = this.getActor();
         GameObject var2 = this.getTarget();
         if (var1 != null && var2 != null) {
            Location var3 = var1.getLoc();
            Location var4 = var2.getLoc().clone();
            if (!this.buildPathLines(var3, var4)) {
               return false;
            }

            this.EA = var4.clone();
            return !this.onEnd();
         } else {
            return false;
         }
      }

      protected boolean isPathRebuildRequired() {
         Creature var1 = this.getActor();
         GameObject var2 = this.getTarget();
         if (var1 != null && var2 != null) {
            Location var3 = var2.getLoc();
            return !this.EA.equalsGeo(var3);
         } else {
            return true;
         }
      }

      @Override
      protected boolean onEnd() {
         Creature var1 = this.getActor();
         GameObject var2 = this.getTarget();
         if (var1 != null && var2 != null) {
            int var3 = this.remainingLinesCount();
            if (var3 > 1) {
               if (!this.pollPathLine()) {
                  this.onFinish(false, false);
                  return true;
               }
            } else {
               if (var3 != 1) {
                  this.onFinish(true, false);
                  return true;
               }

               this.EB = true;
               if (this.isPathRebuildRequired()) {
                  if (this.isArrived()) {
                     this.onFinish(true, false);
                     return true;
                  }

                  Location var4 = var1.getLoc();
                  Location var5 = this.bX();
                  if (!this.buildPathLines(var4, var5)) {
                     this.onFinish(false, false);
                     return true;
                  }

                  if (!this.pollPathLine()) {
                     this.onFinish(false, false);
                     return true;
                  }

                  this.EA = var5.clone();
               } else if (!this.pollPathLine()) {
                  this.onFinish(false, false);
                  return true;
               }
            }

            var1.broadcastMove();
            return false;
         } else {
            return true;
         }
      }

      protected boolean isArrived() {
         Creature var1 = this.getActor();
         GameObject var2 = this.getTarget();
         if (var1 == null || var2 == null) {
            return false;
         } else if (var2.isCreature() && ((Creature)var2).isMoving()) {
            int var3 = this.indent + 16;
            return this.includeMoveZ() ? var2.isInRangeZ(var1, var3) : var2.isInRange(var1, var3);
         } else {
            return this.includeMoveZ() ? var2.isInRangeZ(var1, this.indent + 16) : var2.isInRange(var1, this.indent + 16);
         }
      }

      private Location bX() {
         Creature var1 = this.getActor();
         GameObject var2 = this.getTarget();
         if (var1 == null || var2 == null) {
            return null;
         }

         if (!var2.isCreature()) {
            return var2.getLoc();
         }

         Creature var3 = (Creature)var2;
         Location var4 = var2.getLoc();
         int lookahead = var1.isPlayable() ? Math.max(128, Config.MOVE_TASK_QUANTUM_PC / 2) : Math.max(128, Config.MOVE_TASK_QUANTUM_NPC / 2);
         Location predictedLoc = !var3.isMoving() ? var4 : GeoMove.getIntersectPoint(var1.getLoc(), var4, var3.getMoveSpeed(), lookahead);

         // L2OFF standard: Dispersão radial de cerco (Surround Ring) para evitar sobreposição de monstros
         if (var1.isNpc() && var3.isPlayer()) {
            int slot = Math.abs(var1.getObjectId()) % 8;
            double angleRad = Math.toRadians(slot * 45.0);
            int radialDist = (int) (var3.getColRadius() + var1.getColRadius() / 2.0);
            int offsetX = (int) (Math.cos(angleRad) * radialDist);
            int offsetY = (int) (Math.sin(angleRad) * radialDist);
            return new Location(predictedLoc.x + offsetX, predictedLoc.y + offsetY, predictedLoc.z);
         }
         return predictedLoc;
      }

      @Override
      protected boolean onTick(double var1) {
         if (!super.onTick(var1)) {
            return false;
         }

         Creature var3 = this.getActor();
         GameObject var4 = this.getTarget();
         if (var3 != null && var4 != null) {
            if (var1 < 1.0) {
               if (this.isPathRebuildRequired()) {
                  Location var5 = var3.getLoc();
                  Location var6 = this.bX();
                  if (var3.isPlayer() && var3.getPlayer().getNetConnection() != null) {
                     int var7 = var3.getPlayer().getNetConnection().getPawnClippingRange();
                     if (var5.distance3D(var6) > var7) {
                        this.onFinish(false, false);
                        return false;
                     }
                  }

                  if (!this.buildPathLines(var5, var6)) {
                     this.onFinish(false, false);
                     return false;
                  }

                  if (!this.pollPathLine()) {
                     this.onFinish(false, false);
                     return false;
                  }

                  this.EA = var6.clone();
                  var3.broadcastMove();
               } else if (this.EB && this.isArrived()) {
                  this.onFinish(true, false);
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      protected void onFinish(boolean var1, boolean var2) {
         Creature var3 = this.getActor();
         GameObject var4 = this.getTarget();
         if (!this.isFinished() && var3 != null && var4 != null) {
            if (var2) {
               this.setIsFinished(true);
            } else {
               boolean sendStop = var1 && !var3.isPlayer() && !(var4 instanceof StaticObjectInstance) && !var4.isDoor();
               var3.stopMove(sendStop, true, false);
               boolean var5 = false;
               if (var1) {
                  var5 = (this.includeMoveZ() ? var3.getRealDistance3D(var4) : var3.getRealDistance(var4)) <= this.EC + 16;
               }

               this.setIsFinished(true);
               if (var5) {
                  ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(var3, CtrlEvent.EVT_ARRIVED_TARGET));
               } else {
                  ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(var3, CtrlEvent.EVT_ARRIVED_BLOCKED, var3.getLoc()));
               }
            }
         }
      }

      @Override
      protected boolean isRelativeMove() {
         return this.EB;
      }

      @Override
      public L2GameServerPacket movePacket() {
         Creature var1 = this.getActor();
         if (var1 == null) {
            return null;
         } else {
            GameObject var2 = this.getTarget();
            // L2OFF/Interlude: Playables (players/summons) usam MoveToPawn para seguir alvos.
            // Monstros/NPCs DEVEM usar CharMoveToLocation para que a UE2.5 renderize e interpole a corrida suavemente sem congelar!
            if (var1.isPlayable() && var2 != null && var2.isCreature()) {
               return new MoveToPawn(var1, var2, this.indent);
            } else {
               return new CharMoveToLocation(var1, var1.getLoc(), this.moveTo().clone());
            }
         }
      }

      @Override
      public Creature.MoveToRelativeAction getAsMoveToRelative() {
         return this;
      }
   }

   private class RegenTask implements Runnable {
      private RegenTask() {
      }

      @Override
      public void run() {
         if (!Creature.this.isAlikeDead() && Creature.this.getRegenTick() != 0L) {
            double var1 = Creature.this._currentHp;
            int var3 = Creature.this.getMaxHp();
            int var4 = Creature.this.getMaxMp();
            int var5 = Creature.this.isPlayer() ? Creature.this.getMaxCp() : 0;
            double var6 = 0.0;
            double var8 = 0.0;
            Creature.this.Ei.lock();

            try {
               if (Creature.this._currentHp < var3) {
                  var6 += Formulas.calcHpRegen(Creature.this);
               }

               if (Creature.this._currentMp < var4) {
                  var8 += Formulas.calcMpRegen(Creature.this);
               }

               if (Creature.this.isPlayer() && Config.REGEN_SIT_WAIT) {
                  Player var10 = (Player)Creature.this;
                  if (var10.isSitting()) {
                     var10.updateWaitSitTime();
                     if (var10.getWaitSitTime() > 5) {
                        var6 += var10.getWaitSitTime();
                        var8 += var10.getWaitSitTime();
                     }
                  }
               } else if (Creature.this.isRaid()
                  && Creature.this.getLevel() >= Config.RATE_MOD_MIN_LEVEL_LIMIT
                  && Creature.this.getLevel() <= Config.RATE_MOD_MAX_LEVEL_LIMIT) {
                  var6 *= Config.RATE_RAID_REGEN;
                  var8 *= Config.RATE_RAID_REGEN;
               }

               Creature.this._currentHp = Creature.this._currentHp
                  + Math.max(0.0, Math.min(var6, Creature.this.calcStat(Stats.HP_LIMIT, null, null) * var3 / 100.0 - Creature.this._currentHp));
               Creature.this._currentMp = Creature.this._currentMp
                  + Math.max(0.0, Math.min(var8, Creature.this.calcStat(Stats.MP_LIMIT, null, null) * var4 / 100.0 - Creature.this._currentMp));
               Creature.this._currentHp = Math.min(var3, Creature.this._currentHp);
               Creature.this._currentMp = Math.min(var4, Creature.this._currentMp);
               if (Creature.this.isPlayer()) {
                  Creature.this._currentCp = Creature.this._currentCp
                     + Math.max(
                        0.0,
                        Math.min(
                           Formulas.calcCpRegen(Creature.this), Creature.this.calcStat(Stats.CP_LIMIT, null, null) * var5 / 100.0 - Creature.this._currentCp
                        )
                     );
                  Creature.this._currentCp = Math.min(var5, Creature.this._currentCp);
               }

               if (Creature.this._currentHp == var3 && Creature.this._currentMp == var4 && Creature.this._currentCp == var5) {
                  Creature.this.stopRegeneration();
               }
            } finally {
               Creature.this.Ei.unlock();
            }

            Creature.this.broadcastStatusUpdate();
            Creature.this.sendChanges();
            Creature.this.checkHpMessages(var1, Creature.this._currentHp);
         }
      }
   }
}
