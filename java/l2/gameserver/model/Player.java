package l2.gameserver.model;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.collections.LazyArrayList;
import l2.commons.collections.MultiValueSet;
import l2.commons.dbutils.DbUtils;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.GameTimeController;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.NextAction;
import l2.gameserver.ai.PlayerAI;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.AccountBonusDAO;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.dao.CharacterGroupReuseDAO;
import l2.gameserver.dao.CharacterHennaDAO;
import l2.gameserver.dao.CharacterPostFriendDAO;
import l2.gameserver.dao.CharacterVariablesDAO;
import l2.gameserver.dao.EffectsDAO;
import l2.gameserver.dao.InstanceReuseDAO;
import l2.gameserver.data.xml.holder.CharacterTemplateHolder;
import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.handler.items.IRefineryHandler;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2.gameserver.listener.actor.player.impl.ScriptAnswerListener;
import l2.gameserver.listener.actor.player.impl.SummonAnswerListener;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.actor.instances.player.Bonus;
import l2.gameserver.model.actor.instances.player.FriendList;
import l2.gameserver.model.actor.instances.player.Macro;
import l2.gameserver.model.actor.instances.player.MacroList;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.actor.instances.player.ShortCutList;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.actor.recorder.PlayerStatsChangeRecorder;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.model.base.PlayerAccess;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.chat.chatfilter.ChatMsg;
import l2.gameserver.model.entity.DimensionalRift;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.oly.CompetitionState;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.model.entity.oly.Participant;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.entity.oly.Stadium;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.FestivalMonsterInstance;
import l2.gameserver.model.instances.GuardInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.PetBabyInstance;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.instances.ReflectionBossInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.model.instances.TamedBeastInstance;
import l2.gameserver.model.instances.TrapInstance;
import l2.gameserver.model.items.ItemContainer;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.model.items.PcFreight;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.items.PcRefund;
import l2.gameserver.model.items.PcWarehouse;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.model.items.attachment.PickableAttachment;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.Privilege;
import l2.gameserver.model.pledge.RankPrivs;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SceneMovie;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.AbnormalStatusUpdate;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.AutoAttackStart;
import l2.gameserver.network.l2.s2c.CameraMode;
import l2.gameserver.network.l2.s2c.ChairSit;
import l2.gameserver.network.l2.s2c.ChangeWaitType;
import l2.gameserver.network.l2.s2c.CharInfo;
import l2.gameserver.network.l2.s2c.ConfirmDlg;
import l2.gameserver.network.l2.s2c.EtcStatusUpdate;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.ExBasicActionList;
import l2.gameserver.network.l2.s2c.ExDuelUpdateUserInfo;
import l2.gameserver.network.l2.s2c.ExOlympiadMode;
import l2.gameserver.network.l2.s2c.ExOlympiadUserInfo;
import l2.gameserver.network.l2.s2c.ExPCCafePointInfo;
import l2.gameserver.network.l2.s2c.ExSetCompassZoneCode;
import l2.gameserver.network.l2.s2c.ExStartScenePlayer;
import l2.gameserver.network.l2.s2c.ExStorageMaxCount;
import l2.gameserver.network.l2.s2c.GetItem;
import l2.gameserver.network.l2.s2c.HennaInfo;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.ItemList;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.LeaveWorld;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcInfoPoly;
import l2.gameserver.network.l2.s2c.ObserverEnd;
import l2.gameserver.network.l2.s2c.ObserverStart;
import l2.gameserver.network.l2.s2c.PartySmallWindowUpdate;
import l2.gameserver.network.l2.s2c.PartySpelled;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDelete;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDeleteAll;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListUpdate;
import l2.gameserver.network.l2.s2c.PrivateStoreListBuy;
import l2.gameserver.network.l2.s2c.PrivateStoreListSell;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgBuy;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgSell;
import l2.gameserver.network.l2.s2c.QuestList;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.network.l2.s2c.RecipeShopMsg;
import l2.gameserver.network.l2.s2c.RecipeShopSellList;
import l2.gameserver.network.l2.s2c.RelationChanged;
import l2.gameserver.network.l2.s2c.Ride;
import l2.gameserver.network.l2.s2c.SendTradeDone;
import l2.gameserver.network.l2.s2c.ServerClose;
import l2.gameserver.network.l2.s2c.SetupGauge;
import l2.gameserver.network.l2.s2c.ShortBuffStatusUpdate;
import l2.gameserver.network.l2.s2c.ShortCutInit;
import l2.gameserver.network.l2.s2c.ShortCutRegister;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SpecialCamera;
import l2.gameserver.network.l2.s2c.StatusUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.network.l2.s2c.TargetSelected;
import l2.gameserver.network.l2.s2c.TargetUnselected;
import l2.gameserver.network.l2.s2c.TeleportToLocation;
import l2.gameserver.network.l2.s2c.UserInfo;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.scripts.Events;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.skills.effects.EffectCubic;
import l2.gameserver.skills.skillclasses.Transformation;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.tables.SkillTreeTable;
import l2.gameserver.taskmanager.AutoSaveManager;
import l2.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2.gameserver.templates.FishTemplate;
import l2.gameserver.templates.Henna;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.templates.PlayerTemplate;
import l2.gameserver.templates.item.ArmorTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.GameStats;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Language;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.SiegeUtils;
import l2.gameserver.utils.SqlBatch;
import l2.gameserver.utils.Strings;
import l2.gameserver.utils.TeleportUtils;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.Containers;
import org.napile.primitive.iterators.IntIterator;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2.gameserver.network.pacing.NpcSpawnPacer;

public class Player extends Playable implements PlayerGroup {
   private static final GameClient.PacketBatchScope NOOP_PACKET_BATCH = () -> {};

   public GameClient.PacketBatchScope openPacketBatch() {
      return this.GW != null ? this.GW.openPacketBatch() : NOOP_PACKET_BATCH;
   }

   public void startPacketBatch() {
      if (this.GW != null) this.GW.startPacketBatch();
   }

   public void endPacketBatch() {
      if (this.GW != null) this.GW.endPacketBatch();
   }
   public static final int DEFAULT_TITLE_COLOR = 16777079;
   public static final int MAX_POST_FRIEND_SIZE = 100;
   public static final int MAX_FRIEND_SIZE = 128;
   private static final Logger _log = LoggerFactory.getLogger(Player.class);
   public static final String NO_TRADERS_VAR = "notraders";
   public static final String ANTISPAM_VAR = "antispam";
   public static final String CUSTOM_HERO_END_TIME_VAR = "CustomHeroEndTime";
   public static final String ANIMATION_OF_CAST_RANGE_VAR = "buffAnimRange";
   public static final String NO_SHOTS_ANIMATION_VAR = "noShotsAnim";
   public static final String LAST_PVP_PK_KILL_VAR_NAME = "LastPVPPKKill";
   private static final String GV = "<not connected>";
   public static final String USED_WORLD_CHAT_POINTS = "used_world_chat_points";
   public static final String BONUS_WORLD_CHAT_REUSE = "world_chat_reuse";
   public Map<Integer, SubClass> _classlist = new HashMap<>(4);
   public static final int OBSERVER_NONE = 0;
   public static final int OBSERVER_STARTING = 1;
   public static final int OBSERVER_STARTED = 3;
   public static final int OBSERVER_LEAVING = 2;
   public static final int STORE_PRIVATE_NONE = 0;
   public static final int STORE_PRIVATE_SELL = 1;
   public static final int STORE_PRIVATE_BUY = 3;
   public static final int STORE_PRIVATE_MANUFACTURE = 5;
   public static final int STORE_OBSERVING_GAMES = 7;
   public static final int STORE_PRIVATE_SELL_PACKAGE = 8;
   public static final int RANK_VAGABOND = 0;
   public static final int RANK_VASSAL = 1;
   public static final int RANK_HEIR = 2;
   public static final int RANK_KNIGHT = 3;
   public static final int RANK_WISEMAN = 4;
   public static final int RANK_BARON = 5;
   public static final int RANK_VISCOUNT = 6;
   public static final int RANK_COUNT = 7;
   public static final int RANK_MARQUIS = 8;
   public static final int LANG_ENG = 0;
   public static final int LANG_RUS = 1;
   public static final int LANG_UNK = -1;
   public static final int PLAYER_SEX_MALE = 0;
   public static final int PLAYER_SEX_FEMALE = 1;
   public static final int[] EXPERTISE_LEVELS = new int[]{0, 20, 40, 52, 61, 76, Integer.MAX_VALUE};
   private GameClient GW;
   private String pm;
   private int De;
   private int GX;
   private int GY;
   private int Da;
   private int Db;
   private int Dc;
   private boolean GZ = false;
   private int CY;
   private Stadium Ha;
   private volatile Participant Hb;
   private long Hc;
   private long Hd;
   private long He;
   private long Hf;
   private long Hg;
   private long Hh;
   private long Hi;
   private long Hj;
   private long Hk;
   private long Hl;
   private long CZ;
   private int Hm;
   private int Hn;
   private String Ho = Config.DISCONNECTED_PLAYER_TITLE;
   private int Hp = Config.DISCONNECTED_PLAYER_TITLE_COLOR;
   private boolean Hq;
   volatile boolean sittingTaskLaunched;
   private int Hr = 0;
   private int Hs;
   private boolean Ht = Config.AUTO_LOOT;
   private boolean Hu = Config.AUTO_LOOT_HERBS;
   private boolean Hv = Config.AUTO_LOOT_ADENA;
   private final PcInventory Hw = new PcInventory(this);
   private final Warehouse Hx = new PcWarehouse(this);
   private final ItemContainer Hy = new PcRefund(this);
   private final PcFreight Hz = new PcFreight(this);
   private long HA = 0L;
   private final Deque<ChatMsg> HB = new LinkedList<>();
   private final Map<Integer, Recipe> HC = new TreeMap<>();
   private final Map<Integer, Recipe> HD = new TreeMap<>();
   private Map<Integer, PremiumItem> HE = new TreeMap<>();
   private final Map<String, QuestState> HF = new HashMap<>();
   private final ShortCutList HG = new ShortCutList(this);
   private final MacroList HH = new MacroList(this);
   private AtomicInteger HI = new AtomicInteger(0);
   private String HJ;
   private List<ManufactureItem> HK = Collections.emptyList();
   private String HL;
   private List<TradeItem> HM = Collections.emptyList();
   private List<TradeItem> HN = Collections.emptyList();
   private String HO;
   private List<TradeItem> HP = Collections.emptyList();
   private List<TradeItem> HQ = Collections.emptyList();
   private final Henna[] HR = new Henna[3];
   private int HT;
   private int HU;
   private int HV;
   private int HW;
   private int HX;
   private int HY;
   private Party _party;
   private Location HZ;
   private Clan Ia;
   private int Ib = 0;
   private int Ic = -128;
   private int Id = 0;
   private int Ie = 0;
   private int If = 0;
   private int Ig;
   private PlayerAccess Ih = new PlayerAccess();
   private boolean Ii = false;
   private boolean Ij = false;
   private boolean Ik = false;
   private boolean Il = false;
   private Summon Im = null;
   private boolean In;
   private Map<Integer, EffectCubic> Io = null;
   private int Ip = 0;
   private Request Iq;
   private ItemInstance Ir;
   private WeaponTemplate Is;
   private Map<Integer, String> It = new HashMap<>(8);
   public int expertiseIndex = 0;
   private ItemInstance Iu = null;
   private IRefineryHandler Iv = null;
   private Warehouse.WarehouseType Iw;
   private boolean oo = false;
   private AtomicBoolean Ix = new AtomicBoolean();
   private HardReference<NpcInstance> Iy = HardReferences.emptyRef();
   private MultiSellHolder.MultiSellListContainer Iz = null;
   private Set<Integer> IA = new CopyOnWriteArraySet<>();
   private WorldRegion IB;
   private AtomicInteger IC = new AtomicInteger(0);
   public int _telemode = 0;
   public boolean entering = true;
   public Location _stablePoint = null;
   public int[] _loto = new int[5];
   public int[] _race = new int[2];
   private final Map<Integer, String> ID = new ConcurrentSkipListMap<>();
   private final FriendList IE = new FriendList(this);
   private boolean IF = false;
   private Boat IG;
   private Location IH;
   protected int _baseClass = -1;
   protected SubClass _activeClass = null;
   private Bonus IJ = new Bonus();
   private Future<?> IK;
   private boolean IL;
   private StaticObjectInstance IM;
   private boolean IN = false;
   private int IO = 0;
   private int IQ = 0;
   private int IR = 0;
   private byte[] IS = ArrayUtils.EMPTY_BYTE_ARRAY;
   private int IT = 0;
   private final Fishing IU = new Fishing(this);
   private boolean IV;
   private Future<?> IW;
   private Future<?> IX;
   private Future<?> IY;
   private Future<?> IZ;
   private Future<?> Ja;
   private Future<?> Jb;
   private final Lock Jc = new ReentrantLock();
   private int Jd;
   private boolean Je = false;
   private int Jf;
   private int Jg;
   private String ED;
   private String Jh;
   private int Ji;
   Map<Integer, Skill> _transformationSkills = new HashMap<>();
   private int Jj = 0;
   private int Jk = 0;
   private int Jl = 1500;
   private boolean Jm;
   private int Jn;
   private InvisibleType Jo = InvisibleType.NONE;
   private IntObjectMap<String> Jp = Containers.emptyIntObjectMap();
   private List<String> Jq = new ArrayList<>();
   private boolean Jr = false;
   private boolean Js = false;
   private long Jt;
   private long Ju;
   private IntObjectMap<TimeStamp> Jv = new CHashIntObjectMap();
   private Pair<Integer, OnAnswerListener> Jw = null;
   private MatchingRoom Dn;
   private final Map<Integer, Long> Jx = new ConcurrentHashMap<>();
   private final AutoFarmContext Jy = new AutoFarmContext(this);
   private int Jz = 0;
   private int JA = 0;
   private IntSet JB = new HashIntSet();
   private Future<?> JC;
   private final AtomicReference<Player.MoveToLocationOffloadData> JD = new AtomicReference<>(null);
   private ScheduledFuture<?> JE;
   private int JF;
   private Future<?> JG;
   private int JH;
   private int JI;
   private int JJ;
   private final MultiValueSet<String> JK = new MultiValueSet<>();
   private boolean JL = false;
   private boolean JM = false;
   private int JN = 0;
   private int JO = 0;
   private boolean JP = false;
   private boolean JQ = false;
   private boolean JR = false;
   private int JS = 0;
   private long JT = 0L;
   private Future<?> JU = null;
   private long JV;
   private Location JW;
   private Location JX;
   private int JY = 0;
   protected int _pvpFlag;
   private Future<?> JZ;
   private long Ka;
   private TamedBeastInstance Kb;
   private long Kc = 0L;
   private final SkillFloodTracker _skillFloodTracker = new SkillFloodTracker();
   private NpcSpawnPacer _npcSpawnPacer;
   private Location Kd;
   private int Ke;
   private int Kf = 0;
   private int Kg = 0;
   private boolean Kh;
   private ItemInstance Ki = null;
   private AtomicBoolean Kj = new AtomicBoolean();
   private Map<Integer, Long> Kk;
   private Future<?> Kl;
   private int Km = 0;
   private Map<String, String> Kn;
   private long Ko = 0L;
   private long Kp = 0L;

   public int getBuffAnimRange() {
      return this.Jl;
   }

   public void setBuffAnimRange(int var1) {
      this.Jl = var1;
   }

   public boolean isNoShotsAnim() {
      return this.Jm;
   }

   public void setNoShotsAnim(boolean var1) {
      this.Jm = var1;
   }

   public Player(int var1, PlayerTemplate var2, String var3) {
      super(var1, var2);
      this.pm = var3;
      this.Hm = 16777215;
      this.Hn = 16777079;
      this._baseClass = this.getClassId().getId();
   }

   private Player(int var1, PlayerTemplate var2) {
      this(var1, var2, null);
      this._ai = new PlayerAI(this);
      if (!Config.EVERYBODY_HAS_ADMIN_RIGHTS) {
         this.setPlayerAccess(Config.gmlist.get(var1));
      } else {
         this.setPlayerAccess(Config.gmlist.get(0));
      }
   }

   @Override
   public HardReference<Player> getRef() {
      return (HardReference<Player>)(HardReference<?>)super.getRef();
   }

   public String getAccountName() {
      return this.GW == null ? this.pm : this.GW.getLogin();
   }

   public String getIP() {
      return this.GW == null ? "<not connected>" : this.GW.getIpAddr();
   }

   public Map<Integer, String> getAccountChars() {
      return this.It;
   }

   public final PlayerTemplate getTemplate() {
      return (PlayerTemplate)this._template;
   }

   public PlayerTemplate getBaseTemplate() {
      return (PlayerTemplate)this._baseTemplate;
   }

   public void changeSex() {
      this._template = CharacterTemplateHolder.getInstance().getTemplate(this.getClassId(), this.getSex() != 0);
   }

   public PlayerAI getAI() {
      return (PlayerAI)this._ai;
   }

   @Override
   public void doCast(Skill var1, Creature var2, boolean var3) {
      if (var1 != null) {
         super.doCast(var1, var2, var3);
         this.triggerAfterTeleportProtection();
         this.triggerNoCarrierProtection();
      }
   }

   @Override
   public void altUseSkill(Skill var1, Creature var2) {
      super.altUseSkill(var1, var2);
      this.triggerAfterTeleportProtection();
      this.triggerNoCarrierProtection();
   }

   @Override
   public void sendReuseMessage(Skill var1) {
      if (!this.isCastingNow()) {
         TimeStamp var2 = this.getSkillReuse(var1);
         if (var2 != null && var2.hasNotPassed()) {
            long var3 = var2.getReuseCurrent();
            if ((Config.ALT_SHOW_REUSE_MSG || var3 >= 10000L) && var3 >= 500L) {
               this.sendPacket(new SystemMessage(48).addSkillName(var1.getDisplayId(), var1.getDisplayLevel()));
            }
         }
      }
   }

   @Override
   public final int getLevel() {
      return this._activeClass == null ? 1 : this._activeClass.getLevel();
   }

   public int getSex() {
      return this.getTemplate().isMale ? 0 : 1;
   }

   public int getFace() {
      return this.Da;
   }

   public void setFace(int var1) {
      this.Da = var1;
   }

   public int getHairColor() {
      return this.Dc;
   }

   public void setHairColor(int var1) {
      this.Dc = var1;
   }

   public int getHairStyle() {
      return this.Db;
   }

   public void setHairStyle(int var1) {
      this.Db = var1;
   }

   public void offline() {
      if (this.GW != null) {
         this.GW.setActiveChar(null);
         this.GW.close(ServerClose.STATIC);
         this.setNetConnection(null);
      }

      if (Config.SERVICES_OFFLINE_TRADE_NAME_COLOR_CHANGE) {
         this.setNameColor(Config.SERVICES_OFFLINE_TRADE_NAME_COLOR);
      }

      if (Config.SERVICES_OFFLINE_TRADE_ABNORMAL != AbnormalEffect.NULL) {
         this.startAbnormalEffect(Config.SERVICES_OFFLINE_TRADE_ABNORMAL);
      }

      this.setOfflineMode(true);
      this.setVar("offline", String.valueOf(System.currentTimeMillis() / 1000L), -1L);
      if (Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0L) {
         this.startKickTask(Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK * 1000L);
      }

      Party var1 = this.getParty();
      if (var1 != null) {
         if (this.isFestivalParticipant()) {
            var1.broadcastMessageToPartyMembers(this.getName() + " has been removed from the upcoming festival.");
         }

         this.leaveParty();
      }

      if (this.getPet() != null) {
         this.getPet().unSummon();
      }

      CursedWeaponsManager.getInstance().doLogout(this);
      if (this.isOlyParticipant()) {
         this.getOlyParticipant().OnDisconnect(this);
      }

      this.broadcastCharInfo();
      this.stopWaterTask();
      this.stopBonusTask();
      this.stopHourlyTask();
      this.stopPcBangPointsTask();
      this.stopAutoSaveTask();
      this.stopQuestTimers();

      try {
         this.getInventory().store();
      } catch (Throwable var4) {
         _log.error("", var4);
      }

      try {
         this.store(false);
      } catch (Throwable var3) {
         _log.error("", var3);
      }
   }

   public void kick() {
      if (this.GW != null) {
         this.GW.close(LeaveWorld.STATIC);
         this.setNetConnection(null);
      }

      this.co();
      this.deleteMe();
   }

   public void restart() {
      if (this.GW != null) {
         this.GW.setActiveChar(null);
         this.setNetConnection(null);
      }

      this.co();
      this.deleteMe();
   }

   public void logout() {
      if (this.GW != null) {
         this.GW.close(ServerClose.STATIC);
         this.setNetConnection(null);
      }

      this.co();
      this.deleteMe();
   }

   private void co() {
      if (!this.Ix.getAndSet(true)) {
         this.setNetConnection(null);
         this.setIsOnline(false);
         this.getListeners().onExit();
         if (this.isFlying() && !this.checkLandingState()) {
            this._stablePoint = TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE);
         }

         if (this.isCastingNow()) {
            this.abortCast(true, true);
         }

         Party var1 = this.getParty();
         if (var1 != null) {
            if (this.isFestivalParticipant()) {
               var1.broadcastMessageToPartyMembers(this.getName() + " has been removed from the upcoming festival.");
            }

            this.leaveParty();
         }

         if (Config.OLY_ENABLED && OlyController.getInstance().isCompetitionsActive()) {
            if (this.isOlyParticipant()) {
               this.getOlyParticipant().OnDisconnect(this);
            }

            if (ParticipantPool.getInstance().isRegistred(this)) {
               ParticipantPool.getInstance().onLogout(this);
            }
         }

         CursedWeaponsManager.getInstance().doLogout(this);
         if (this.isOlyObserver()) {
            this.leaveOlympiadObserverMode();
         }

         if (this.isInObserverMode()) {
            this.leaveObserverMode();
         }

         this.stopFishing();
         if (this._stablePoint != null) {
            this.teleToLocation(this._stablePoint);
         }

         Summon var2 = this.getPet();
         if (var2 != null) {
            if (Config.ALT_SAVE_SERVITOR_BUFF) {
               var2.saveEffects();
            }

            var2.unSummon();
         }

         this.IE.notifyFriends(false);
         if (this.isProcessingRequest()) {
            this.getRequest().cancel();
         }

         this.stopAllTimers();
         if (this.isInBoat()) {
            this.getBoat().removePlayer(this);
         }

         SubUnit var3 = this.getSubUnit();
         UnitMember var4 = var3 == null ? null : var3.getUnitMember(this.getObjectId());
         if (var4 != null) {
            int var5 = var4.getSponsor();
            int var6 = this.getApprentice();
            PledgeShowMemberListUpdate var7 = new PledgeShowMemberListUpdate(this);

            for (Player var9 : this.Ia.getOnlineMembers(this.getObjectId())) {
               var9.sendPacket(var7);
               if (var9.getObjectId() == var5) {
                  var9.sendPacket(new SystemMessage(1757).addString(this._name));
               } else if (var9.getObjectId() == var6) {
                  var9.sendPacket(new SystemMessage(1759).addString(this._name));
               }
            }

            var4.setPlayerInstance(this, true);
         }

         FlagItemAttachment var12 = this.getActiveWeaponFlagAttachment();
         if (var12 != null) {
            var12.onLogout(this);
         }

         if (CursedWeaponsManager.getInstance().getCursedWeapon(this.getCursedWeaponEquippedId()) != null) {
            CursedWeaponsManager.getInstance().getCursedWeapon(this.getCursedWeaponEquippedId()).setPlayer(null);
         }

         MatchingRoom var13 = this.getMatchingRoom();
         if (var13 != null) {
            if (var13.getLeader() == this) {
               var13.disband();
            } else {
               var13.removeMember(this, false);
            }
         }

         this.setMatchingRoom(null);
         MatchingRoomManager.getInstance().removeFromWaitingList(this);
         this.destroyAllTraps();
         this.stopPvPFlag();
         Reflection var14 = this.getReflection();
         if (var14 != ReflectionManager.DEFAULT) {
            if (var14.getReturnLoc() != null) {
               this._stablePoint = var14.getReturnLoc();
            }

            var14.removeObject(this);
         }

         try {
            this.getInventory().store();
            this.getRefund().clear();
         } catch (Throwable var11) {
            _log.error("", var11);
         }

         try {
            this.store(false);
         } catch (Throwable var10) {
            _log.error("", var10);
         }
      }
   }

   public Collection<Recipe> getDwarvenRecipeBook() {
      return this.HC.values();
   }

   public Collection<Recipe> getCommonRecipeBook() {
      return this.HD.values();
   }

   public int recipesCount() {
      return this.HD.size() + this.HC.size();
   }

   public boolean hasRecipe(Recipe var1) {
      return this.HC.containsValue(var1) || this.HD.containsValue(var1);
   }

   public boolean findRecipe(int var1) {
      return this.HC.containsKey(var1) || this.HD.containsKey(var1);
   }

   public void registerRecipe(Recipe var1, boolean var2) {
      if (var1 != null) {
         switch (var1.getType()) {
            case ERT_COMMON:
               this.HD.put(var1.getId(), var1);
               break;
            case ERT_DWARF:
               this.HC.put(var1.getId(), var1);
               break;
            default:
               return;
         }

         if (var2) {
            mysql.set("REPLACE INTO character_recipebook (char_id, id) VALUES(?,?)", this.getObjectId(), var1.getId());
         }
      }
   }

   public void unregisterRecipe(int var1) {
      if (this.HC.containsKey(var1)) {
         mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", this.getObjectId(), var1);
         this.HC.remove(var1);
      } else if (this.HD.containsKey(var1)) {
         mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", this.getObjectId(), var1);
         this.HD.remove(var1);
      } else {
         _log.warn("Attempted to remove unknown RecipeList" + var1);
      }
   }

   public QuestState getQuestState(Quest var1) {
      return this.getQuestState(var1.getName());
   }

   public QuestState getQuestState(String var1) {
      this.questRead.lock();

      try {
         return this.HF.get(var1);
      } finally {
         this.questRead.unlock();
      }
   }

   public QuestState getQuestState(Class<?> var1) {
      return this.getQuestState(var1.getSimpleName());
   }

   public boolean isQuestCompleted(String var1) {
      QuestState var2 = this.getQuestState(var1);
      return var2 != null && var2.isCompleted();
   }

   public boolean isQuestCompleted(Class<?> var1) {
      QuestState var2 = this.getQuestState(var1);
      return var2 != null && var2.isCompleted();
   }

   public void setQuestState(QuestState var1) {
      this.questWrite.lock();

      try {
         this.HF.put(var1.getQuest().getName(), var1);
      } finally {
         this.questWrite.unlock();
      }
   }

   public void removeQuestState(String var1) {
      this.questWrite.lock();

      try {
         this.HF.remove(var1);
      } finally {
         this.questWrite.unlock();
      }
   }

   public Quest[] getAllActiveQuests() {
      ArrayList var1 = new ArrayList(this.HF.size());
      this.questRead.lock();

      try {
         for (QuestState var3 : this.HF.values()) {
            if (var3.isStarted()) {
               var1.add(var3.getQuest());
            }
         }
      } finally {
         this.questRead.unlock();
      }

      return (Quest[])var1.toArray(new Quest[var1.size()]);
   }

   public QuestState[] getAllQuestsStates() {
      this.questRead.lock();

      try {
         return this.HF.values().toArray(new QuestState[this.HF.size()]);
      } finally {
         this.questRead.unlock();
      }
   }

   public List<QuestState> getQuestsForEvent(NpcInstance var1, QuestEventType var2) {
      ArrayList var3 = new ArrayList();
      Quest[] var4 = var1.getTemplate().getEventQuests(var2);
      if (var4 != null) {
         for (Quest var9 : var4) {
            QuestState var5 = this.getQuestState(var9.getName());
            if (var5 != null && !var5.isCompleted()) {
               var3.add(this.getQuestState(var9.getName()));
            }
         }
      }

      return var3;
   }

   public void processQuestEvent(String var1, String var2, NpcInstance var3) {
      if (var2 == null) {
         var2 = "";
      }

      QuestState var4 = this.getQuestState(var1);
      if (var4 == null) {
         Quest var5 = QuestManager.getQuest(var1);
         if (var5 == null) {
            _log.warn("Quest " + var1 + " not found!");
            return;
         }

         var4 = var5.newQuestState(this, 1);
      }

      if (var4 != null && !var4.isCompleted()) {
         var4.getQuest().notifyEvent(var2, var4, var3);
         this.sendPacket(new QuestList(this));
      }
   }

   public boolean isQuestContinuationPossible(boolean var1) {
      if (this.getWeightPenalty() < 3 && !(this.getInventoryLimit() * 0.8 < this.getInventory().getSize())) {
         return true;
      }

      if (var1) {
         this.sendPacket(Msg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_VOLUME_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
      }

      return false;
   }

   public void stopQuestTimers() {
      for (QuestState var4 : this.getAllQuestsStates()) {
         if (var4.isStarted()) {
            var4.pauseQuestTimers();
         } else {
            var4.stopQuestTimers();
         }
      }
   }

   public void resumeQuestTimers() {
      for (QuestState var4 : this.getAllQuestsStates()) {
         var4.resumeQuestTimers();
      }
   }

   public Collection<ShortCut> getAllShortCuts() {
      return this.HG.getAllShortCuts();
   }

   public ShortCut[] getShortCuts() {
      return this.HG.getShortCuts();
   }

   public ShortCut getShortCut(int var1, int var2) {
      return this.HG.getShortCut(var1, var2);
   }

   public void registerShortCut(ShortCut var1) {
      this.HG.registerShortCut(var1);
   }

   public void deleteShortCut(int var1, int var2) {
      this.HG.deleteShortCut(var1, var2);
   }

   public void registerMacro(Macro var1) {
      this.HH.registerMacro(var1);
   }

   public void deleteMacro(int var1) {
      this.HH.deleteMacro(var1);
   }

   public MacroList getMacroses() {
      return this.HH;
   }

   public boolean isCastleLord(int var1) {
      return this.Ia != null && this.isClanLeader() && this.Ia.getCastle() == var1;
   }

   public int getPkKills() {
      return this.GX;
   }

   public void setPkKills(int var1) {
      this.GX = var1;
   }

   public long getCreateTime() {
      return this.Hc;
   }

   public void setCreateTime(long var1) {
      this.Hc = var1;
   }

   public int getDeleteTimer() {
      return this.CY;
   }

   public void setDeleteTimer(int var1) {
      this.CY = var1;
   }

   public int getCurrentLoad() {
      return this.getInventory().getTotalWeight();
   }

   public long getLastAccess() {
      return this.CZ;
   }

   public void setLastAccess(long var1) {
      this.CZ = var1;
   }

   public boolean isRecommended(Player var1) {
      return this.JB.contains(var1.getObjectId());
   }

   public void setReceivedRec(int var1) {
      this.Jz = var1;
   }

   public int getReceivedRec() {
      return this.Jz;
   }

   public void setGivableRec(int var1) {
      this.JA = var1;
   }

   public int getGivableRec() {
      return this.JA;
   }

   public void updateRecommends() {
      this.JB.clear();
      if (this.getLevel() >= 40) {
         this.JA = 9;
         this.Jz = Math.max(0, this.Jz - 4);
      } else if (this.getLevel() >= 20) {
         this.JA = 6;
         this.Jz = Math.max(0, this.Jz - 2);
      } else if (this.getLevel() >= 10) {
         this.JA = 3;
         this.Jz = Math.max(0, this.Jz - 1);
      } else {
         this.JA = 0;
         this.Jz = 0;
      }
   }

   public void restoreGivableAndReceivedRec(int var1, int var2) {
      this.JA = var1;
      this.Jz = var2;
      Calendar var3 = Calendar.getInstance();
      var3.set(11, Config.REC_FLUSH_HOUR);
      var3.set(12, Config.REC_FLUSH_MINUTE);
      var3.set(13, 0);
      var3.set(14, 0);
      long var4 = Math.round((float)((System.currentTimeMillis() / 1000L - this.getLastAccess()) / 86400L));
      if (var4 == 0L && this.getLastAccess() < var3.getTimeInMillis() / 1000L && System.currentTimeMillis() > var3.getTimeInMillis()) {
         var4++;
      }

      for (int var6 = 1; var6 < var4; var6++) {
         this.updateRecommends();
      }

      if (var4 > 0L) {
         this.restartDailyCounters(true);
      }
   }

   public void giveRecommendation(Player var1) {
      if (var1 != null) {
         if (this.getGivableRec() > 0 && var1.getReceivedRec() < 255) {
            if (!this.JB.contains(var1.getObjectId())) {
               this.JB.add(var1.getObjectId());
               this.setGivableRec(this.getGivableRec() - 1);
               this.sendUserInfo();
               var1.setReceivedRec(var1.getReceivedRec() + 1);
               var1.broadcastUserInfo(true);
            }
         }
      }
   }

   private void cp() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT `targetId` AS `recommendedObjId` FROM `character_recommends` WHERE `objId` = ?");
         var2.setInt(1, this.getObjectId());
         var3 = var2.executeQuery();
         this.JB.clear();

         while (var3.next()) {
            int var4 = var3.getInt("recommendedObjId");
            this.JB.add(var4);
         }
      } catch (SQLException var8) {
         _log.error("Can't load recommended characters", var8);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   private void cq() {
      Connection var1 = null;
      PreparedStatement var2 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("DELETE FROM `character_recommends` WHERE `objId` = ?");
         var2.setInt(1, this.getObjectId());
         var2.executeUpdate();
         DbUtils.close(var2);
         if (!this.JB.isEmpty()) {
            var2 = var1.prepareStatement("INSERT INTO `character_recommends` (`objId`, `targetId`) VALUES (?, ?)");
            IntIterator var3 = this.JB.iterator();

            while (var3.hasNext()) {
               var2.setInt(1, this.getObjectId());
               var2.setInt(2, var3.next());
               var2.executeUpdate();
            }
         }
      } catch (SQLException var7) {
         _log.error("Can't store recommended characters", var7);
      } finally {
         DbUtils.closeQuietly(var1, var2);
      }
   }

   public void restartDailyCounters(boolean var1) {
      if (!var1) {
         this.sendUserInfo(true);
      }

      if (Config.ENABLE_WORLD_CHAT) {
         this.setVar("used_world_chat_points", 0, -1L);
      }
   }

   @Override
   public int getKarma() {
      return this.De;
   }

   public void setKarma(int var1, boolean var2) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (this.De != var1) {
         this.De = var1;
         if (var2) {
            this.sendChanges();
         }

         if (this.getPet() != null) {
            this.getPet().broadcastCharInfo();
         }
      }
   }

   @Override
   public int getMaxLoad() {
      int var1 = this.getCON();
      return var1 < 1
         ? (int)(31000.0 * Config.MAXLOAD_MODIFIER)
         : (int)this.calcStat(Stats.MAX_LOAD, Math.pow(1.029993928, var1) * 30495.627366 * Config.MAXLOAD_MODIFIER, this, null);
   }

   @Override
   public void updateEffectIcons() {
      if (!this.entering && !this.isLogoutStarted()) {
         if (Config.USER_INFO_INTERVAL == 0L) {
            if (this.JC != null) {
               this.JC.cancel(false);
               this.JC = null;
            }

            this.updateEffectIconsImpl();
         } else if (this.JC == null) {
            this.JC = ThreadPoolManager.getInstance().schedule(new Player.UpdateEffectIcons(), Config.USER_INFO_INTERVAL);
         }
      }
   }

   private void updateEffectIconsImpl() {
      Effect[] var1 = this.getEffectList().getAllFirstEffects();
      PartySpelled var2 = new PartySpelled(this, false);
      AbnormalStatusUpdate var3 = new AbnormalStatusUpdate();

      for (Effect.EEffectSlot var7 : Effect.EEffectSlot.VALUES) {
         for (Effect var11 : var1) {
            if (var11.isInUse() && var11.getEffectSlot() == var7) {
               if (var11.isStackTypeMatch("HpRecoverCast")) {
                  this.sendPacket(new ShortBuffStatusUpdate(var11));
               } else {
                  var11.addIcon(var3);
               }

               if (this._party != null) {
                  var11.addPartySpelledIcon(var2);
               }
            }
         }
      }

      this.sendPacket(var3);
      if (this._party != null) {
         this._party.broadCast(var2);
      }

      if (this.isOlyParticipant()) {
         this.getOlyParticipant().getCompetition().broadcastEffectIcons(this, var1);
      }
   }

   public int getWeightPenalty() {
      return this.getSkillLevel(4270, 0);
   }

   public void refreshOverloaded() {
      if (!this.isLogoutStarted() && this.getMaxLoad() > 0) {
         this.setOverloaded(this.getCurrentLoad() > this.getMaxLoad());
         double var1 = 100.0 * (this.getCurrentLoad() - this.calcStat(Stats.MAX_NO_PENALTY_LOAD, 0.0, this, null)) / this.getMaxLoad();
         byte var3 = 0;
         if (var1 < 50.0) {
            var3 = 0;
         } else if (var1 < 66.6) {
            var3 = 1;
         } else if (var1 < 80.0) {
            var3 = 2;
         } else if (var1 < 100.0) {
            var3 = 3;
         } else {
            var3 = 4;
         }

         int var4 = this.getWeightPenalty();
         if (var4 != var3) {
            if (var3 > 0) {
               super.addSkill(SkillTable.getInstance().getInfo(4270, var3));
            } else {
               super.removeSkill(this.getKnownSkill(4270));
            }

            this.sendPacket(new SkillList(this));
            this.sendEtcStatusUpdate();
            this.updateStats();
         }
      }
   }

   public int getGradePenalty() {
      return this.getSkillLevel(4267, 0);
   }

   public int getExpertisePenalty(ItemInstance var1) {
      return var1.getTemplate().getType2() != 0 && var1.getTemplate().getType2() != 1 && var1.getTemplate().getType2() != 2 ? 0 : this.getGradePenalty();
   }

   public void refreshExpertisePenalty() {
      if (!this.isLogoutStarted() && !Config.DISABLE_GRADE_PENALTY) {
         int var1 = (int)this.calcStat(Stats.GRADE_EXPERTISE_LEVEL, this.getLevel(), null, null);
         int var2 = 0;
         var2 = 0;

         while (var2 < EXPERTISE_LEVELS.length && var1 >= EXPERTISE_LEVELS[var2 + 1]) {
            var2++;
         }

         boolean var3 = false;
         if (this.expertiseIndex != var2) {
            this.expertiseIndex = var2;
            if (this.expertiseIndex > 0) {
               this.addSkill(SkillTable.getInstance().getInfo(239, this.expertiseIndex), false);
               var3 = true;
            }
         }

         int var4 = 0;
         ItemInstance[] var5 = this.getInventory().getPaperdollItems();

         for (ItemInstance var9 : var5) {
            if (var9 != null) {
               int var10 = var9.getTemplate().getCrystalType().ordinal();
               if ((var9.getTemplate().getType2() == 0 || var9.getTemplate().getType2() == 1 || var9.getTemplate().getType2() == 2) && var10 > var4) {
                  var4 = var10;
               }
            }
         }

         var4 -= this.expertiseIndex;
         if (var4 <= 0) {
            var4 = 0;
         } else if (var4 >= 4) {
            var4 = 4;
         }

         int var13 = this.getGradePenalty();
         if (var13 != var4) {
            var13 = var4;
            if (var4 > 0) {
               super.addSkill(SkillTable.getInstance().getInfo(4267, var13));
            } else {
               super.removeSkill(this.getKnownSkill(4267));
            }

            var3 = true;
         }

         if (var3) {
            this.getInventory().validateItemsSkills();
            this.sendPacket(new SkillList(this));
            this.sendEtcStatusUpdate();
            this.updateStats();
         }
      }
   }

   public int getPvpKills() {
      return this.GY;
   }

   public void setPvpKills(int var1) {
      this.GY = var1;
   }

   public ClassId getClassId() {
      return this.getTemplate().classId;
   }

   public void addClanPointsOnProfession(int var1) {
      if (this.getLvlJoinedAcademy() != 0 && this.Ia != null && this.Ia.getLevel() >= 5 && ClassId.VALUES[var1].getLevel() == 2) {
         this.Ia.incReputation(100, true, "Academy");
      } else if (this.getLvlJoinedAcademy() != 0 && this.Ia != null && this.Ia.getLevel() >= 5 && ClassId.VALUES[var1].getLevel() == 3) {
         int var2 = 0;
         if (this.getLvlJoinedAcademy() > 39) {
            var2 = 160;
         } else if (this.getLvlJoinedAcademy() > 16) {
            var2 = 400 - (this.getLvlJoinedAcademy() - 16) * 10;
         } else {
            var2 = 400;
         }

         this.Ia.removeClanMember(this.getObjectId());
         SystemMessage var3 = new SystemMessage(1748);
         var3.addString(this.getName());
         var3.addNumber(this.Ia.incReputation(var2, true, "Academy"));
         this.Ia.broadcastToOnlineMembers(var3);
         this.Ia.broadcastToOtherOnlineMembers(new PledgeShowMemberListDelete(this.getName()), this);
         this.setClan(null);
         this.setTitle("");
         this.sendPacket(
            Msg.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_AS_A_GRADUATE_OF_THE_ACADEMY_YOU_CAN_IMMEDIATELY_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES
         );
         this.setLeaveClanTime(0L);
         this.broadcastCharInfo();
         this.sendPacket(PledgeShowMemberListDeleteAll.STATIC);
         ItemFunctions.addItem(this, 8181, 1L, true);
      }
   }

   public synchronized void setClassId(int var1, boolean var2, boolean var3) {
      if (!var2
         && !ClassId.VALUES[var1].equalsOrChildOf(ClassId.VALUES[this.getActiveClassId()])
         && !this.getPlayerAccess().CanChangeClass
         && !Config.EVERYBODY_HAS_ADMIN_RIGHTS) {
         Thread.dumpStack();
      } else {
         boolean var4 = !this.getSubClasses().containsKey(var1);
         if (var4) {
            SubClass var5 = this.getActiveClass();
            this.getSubClasses().remove(this.getActiveClassId());
            this.changeClassInDb(var5.getClassId(), var1);
            if (var5.isBase()) {
               this.setBaseClass(var1);
               this.addClanPointsOnProfession(var1);
               ItemInstance var6 = null;
               if (ClassId.VALUES[var1].getLevel() == 2) {
                  if (var3 && Config.ALT_ALLOW_SHADOW_WEAPONS) {
                     var6 = ItemFunctions.createItem(8869);
                  }

                  this.unsetVar("newbieweapon");
                  this.unsetVar("p1q2");
                  this.unsetVar("p1q3");
                  this.unsetVar("p1q4");
                  this.unsetVar("prof1");
                  this.unsetVar("ng1");
                  this.unsetVar("ng2");
                  this.unsetVar("ng3");
                  this.unsetVar("ng4");
               } else if (ClassId.VALUES[var1].getLevel() == 3) {
                  if (var3 && Config.ALT_ALLOW_SHADOW_WEAPONS) {
                     var6 = ItemFunctions.createItem(8870);
                  }

                  this.unsetVar("newbiearmor");
                  this.unsetVar("dd1");
                  this.unsetVar("dd2");
                  this.unsetVar("dd3");
                  this.unsetVar("prof2.1");
                  this.unsetVar("prof2.2");
                  this.unsetVar("prof2.3");
               }

               if (var6 != null) {
                  var6.setCount(15L);
                  this.sendPacket(SystemMessage2.obtainItems(var6));
                  this.getInventory().addItem(var6);
               }
            }

            var5.setClassId(var1);
            this.getSubClasses().put(var1, var5);
            this.d(true);
            this.storeCharSubClasses();
            if (var3) {
               this.broadcastPacket(new SocialAction(this.getObjectId(), 16));
               this.broadcastPacket(new SocialAction(this.getObjectId(), 3));
               this.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
            }

            this.broadcastCharInfo();
         }

         PlayerTemplate var7 = CharacterTemplateHolder.getInstance().getTemplate(ClassId.getClassById(var1), this.getSex() == 0);
         if (var7 == null) {
            _log.error("Missing template for classId: " + var1);
         } else {
            this._template = var7;
            if (this.isInParty()) {
               this.getParty().broadCast(new PartySmallWindowUpdate(this));
            }

            if (this.getClan() != null) {
               this.getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
            }

            if (this.Dn != null) {
               this.Dn.broadcastPlayerUpdate(this);
            }

            this.sendPacket(new SkillList(this));
            this.getListeners().onSetClass(var1);
         }
      }
   }

   public long getExp() {
      return this._activeClass == null ? 0L : this._activeClass.getExp();
   }

   public long getMaxExp() {
      return this._activeClass == null ? Experience.LEVEL[Experience.getMaxLevel() + 1] : this._activeClass.getMaxExp();
   }

   public void setEnchantScroll(ItemInstance var1) {
      this.Iu = var1;
   }

   public ItemInstance getEnchantScroll() {
      return this.Iu;
   }

   public IRefineryHandler getRefineryHandler() {
      return this.Iv;
   }

   public void setRefineryHandler(IRefineryHandler var1) {
      this.Iv = var1;
   }

   public void setFistsWeaponItem(WeaponTemplate var1) {
      this.Is = var1;
   }

   public WeaponTemplate getFistsWeaponItem() {
      return this.Is;
   }

   public WeaponTemplate findFistsWeaponItem(int var1) {
      if (var1 >= 0 && var1 <= 9) {
         return (WeaponTemplate)ItemHolder.getInstance().getTemplate(246);
      } else if (var1 >= 10 && var1 <= 17) {
         return (WeaponTemplate)ItemHolder.getInstance().getTemplate(251);
      } else if (var1 >= 18 && var1 <= 24) {
         return (WeaponTemplate)ItemHolder.getInstance().getTemplate(244);
      } else if (var1 >= 25 && var1 <= 30) {
         return (WeaponTemplate)ItemHolder.getInstance().getTemplate(249);
      } else if (var1 >= 31 && var1 <= 37) {
         return (WeaponTemplate)ItemHolder.getInstance().getTemplate(245);
      } else if (var1 >= 38 && var1 <= 43) {
         return (WeaponTemplate)ItemHolder.getInstance().getTemplate(250);
      } else if (var1 >= 44 && var1 <= 48) {
         return (WeaponTemplate)ItemHolder.getInstance().getTemplate(248);
      } else if (var1 >= 49 && var1 <= 52) {
         return (WeaponTemplate)ItemHolder.getInstance().getTemplate(252);
      } else {
         return var1 >= 53 && var1 <= 57 ? (WeaponTemplate)ItemHolder.getInstance().getTemplate(247) : null;
      }
   }

   public void addExpAndCheckBonus(MonsterInstance var1, double var2, double var4) {
      if (this._activeClass != null) {
         double var6 = Config.RATE_XP * this.getRateExp();
         double var8 = Config.RATE_SP * this.getRateSp();
         if (var1.isRaid()) {
            var6 = Config.RATE_RAIDBOSS_XP * this.getRaidRateExp();
            var8 = Config.RATE_RAIDBOSS_SP * this.getRaidRateSp();
         }

         this.addExpAndSp((long)(var2 * var6), (long)(var4 * var8), false, true);
      }
   }

   @Override
   public void addExpAndSp(long var1, long var3) {
      this.addExpAndSp(var1, var3, false, false);
   }

   public void addExpAndSp(long var1, long var3, boolean var5, boolean var6) {
      if (this._activeClass != null) {
         if (var5) {
            var1 = (long)(var1 * (Config.RATE_XP * this.getRateExp()));
            var3 = (long)(var3 * (Config.RATE_SP * this.getRateSp()));
         }

         Summon var7 = this.getPet();
         boolean var8 = false;
         if (var1 > 0L) {
            if (var6 && var7 != null && !var7.isDead()) {
               if (var7.getNpcId() == 12564) {
                  var7.addExpAndSp(var1, 0L);
                  var1 = 0L;
               } else if (var7.isPet() && var7.getExpPenalty() > 0.0) {
                  if (var7.getLevel() > this.getLevel() - 20 && var7.getLevel() < this.getLevel() + 5) {
                     var7.addExpAndSp((long)(var1 * var7.getExpPenalty()), 0L);
                     var1 = (long)(var1 * (1.0 - var7.getExpPenalty()));
                  } else {
                     var7.addExpAndSp((long)(var1 * var7.getExpPenalty() / 5.0), 0L);
                     var1 = (long)(var1 * (1.0 - var7.getExpPenalty() / 5.0));
                  }
               } else if (var7.isSummon()) {
                  var1 = (long)(var1 * (1.0 - var7.getExpPenalty()));
               }
            }

            long var9 = this.getVarB("NoExp") ? Experience.LEVEL[this.getLevel() + 1] - 1L : this.getMaxExp();
            var1 = Math.min(var1, var9 - this.getExp());
         }

         int var12 = this._activeClass.getLevel();
         this._activeClass.addExp(var1);
         this._activeClass.addSp(var3);
         if (var3 > 0L && var1 == 0L) {
            this.sendPacket(new SystemMessage(331).addNumber(var3));
         } else if (var3 > 0L && var1 > 0L) {
            this.sendPacket(new SystemMessage(95).addNumber(var1).addNumber(var3));
         } else if (var3 == 0L && var1 > 0L) {
            this.sendPacket(new SystemMessage(45).addNumber(var1));
         }

         int var10 = this._activeClass.getLevel();
         if (var10 != var12) {
            int var11 = var10 - var12;
            this.z(var11);
            this.getListeners().onLevelUp(var10);
         }

         this.updateStats();
         if (var7 != null && var8) {
            var7.broadcastCharInfo();
         }

         this.getListeners().onGainExpSp(var1, var3);
      }
   }

   private void d(boolean var1) {
      boolean var2 = false;
      if (Config.AUTO_LEARN_SKILLS) {
         int var3 = 0;

         for (Collection var4 = SkillAcquireHolder.getInstance().getAvailableSkills(this, ClassId.VALUES[this.getActiveClassId()], AcquireType.NORMAL, null);
            var4.size() > var3;
            var4 = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL)
         ) {
            var3 = 0;

            for (Object __raw_var6_1696 : (Iterable<?>)(Object)(var4)) {
               SkillLearn var6 = (SkillLearn)__raw_var6_1696;
               Skill var7 = SkillTable.getInstance().getInfo(var6.getId(), var6.getLevel());
               if (var7 != null && var7.getCanLearn(this.getClassId()) && var6.canAutoLearn()) {
                  this.addSkill(var7, true);
               } else {
                  var3++;
               }
            }
         }

         var2 = true;
      } else {
         for (SkillLearn var10 : SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL)) {
            if (var10.getCost() == 0 && var10.getItemId() == 0) {
               Skill var11 = SkillTable.getInstance().getInfo(var10.getId(), var10.getLevel());
               this.addSkill(var11, true);
               if (this.getAllShortCuts().size() > 0 && var11.getLevel() > 1) {
                  for (ShortCut var13 : this.getAllShortCuts()) {
                     if (var13.getId() == var11.getId() && var13.getType() == 2) {
                        ShortCut var8 = new ShortCut(var13.getSlot(), var13.getPage(), var13.getType(), var13.getId(), var11.getLevel(), 1);
                        this.sendPacket(new ShortCutRegister(this, var8));
                        this.registerShortCut(var8);
                     }
                  }
               }

               var2 = true;
            }
         }
      }

      if (var1 && var2) {
         this.sendPacket(new SkillList(this));
      }

      this.updateStats();
   }

   public Race getRace() {
      return this.getBaseTemplate().race;
   }

   public int getIntSp() {
      return (int)this.getSp();
   }

   public long getSp() {
      return this._activeClass == null ? 0L : this._activeClass.getSp();
   }

   public void setSp(long var1) {
      if (this._activeClass != null) {
         this._activeClass.setSp(var1);
      }
   }

   public int getClanId() {
      return this.Ia == null ? 0 : this.Ia.getClanId();
   }

   public long getLeaveClanTime() {
      return this.Hf;
   }

   public long getDeleteClanTime() {
      return this.Hg;
   }

   public void setLeaveClanTime(long var1) {
      this.Hf = var1;
   }

   public void setDeleteClanTime(long var1) {
      this.Hg = var1;
   }

   public void setOnlineTime(long var1) {
      this.Hd = var1;
      this.He = System.currentTimeMillis();
   }

   public long getOnlineBeginTime() {
      return this.He;
   }

   public long getOnlineTime() {
      return this.Hd;
   }

   public void setNoChannel(long var1) {
      this.Hh = var1;
      if (this.Hh > 2145909600000L || this.Hh < 0L) {
         this.Hh = -1L;
      }

      if (this.Hh > 0L) {
         this.Hi = System.currentTimeMillis();
      } else {
         this.Hi = 0L;
      }
   }

   public long getNoChannel() {
      return this.Hh;
   }

   public long getNoChannelRemained() {
      if (this.Hh == 0L) {
         return 0L;
      }

      if (this.Hh < 0L) {
         return -1L;
      }

      long var1 = this.Hh - System.currentTimeMillis() + this.Hi;
      return var1 < 0L ? 0L : var1;
   }

   public void setAntiSpam(long var1, boolean var3) {
      if (var1 > 0L) {
         this.Hj = var1;
         this.Hk = System.currentTimeMillis();
      } else if (var1 < 0L) {
         this.Hj = -1L;
         this.Hk = System.currentTimeMillis();
      } else {
         this.Hj = 0L;
         this.Hk = 0L;
      }

      if (var3) {
         if (var1 != 0L) {
            this.setVar("antispam", var1, -1L);
         } else {
            this.unsetVar("antispam");
         }
      }
   }

   public long getAntiSpam() {
      return this.Hj;
   }

   public long getAntiSpamRemained() {
      if (this.Hj <= 0L) {
         return this.Hj == 0L ? 0L : -1L;
      } else {
         return Math.max(0L, this.Hj - System.currentTimeMillis() + this.Hk);
      }
   }

   public void setLeaveClanCurTime() {
      this.Hf = System.currentTimeMillis();
   }

   public void setDeleteClanCurTime() {
      this.Hg = System.currentTimeMillis();
   }

   public boolean canJoinClan() {
      if (this.Hf == 0L) {
         return true;
      } else if (System.currentTimeMillis() - this.Hf >= Config.CLAN_LEAVE_TIME_PERNALTY) {
         this.Hf = 0L;
         return true;
      } else {
         return false;
      }
   }

   public boolean canCreateClan() {
      if (this.Hg == 0L) {
         return true;
      } else if (System.currentTimeMillis() - this.Hg >= Config.NEW_CLAN_CREATE_PENALTY) {
         this.Hg = 0L;
         return true;
      } else {
         return false;
      }
   }

   public IStaticPacket canJoinParty(Player var1) {
      Request var2 = this.getRequest();
      if (var2 != null && var2.isInProgress() && var2.getOtherPlayer(this) != var1) {
         return SystemMsg.WAITING_FOR_ANOTHER_REPLY.packet(var1);
      }

      if (this.isBlockAll() || this.getMessageRefusal()) {
         return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE.packet(var1);
      }

      if (this.isInParty()) {
         return new SystemMessage2(SystemMsg.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addName(this);
      }

      if (var1.getReflection() != this.getReflection()
         && var1.getReflection() != ReflectionManager.DEFAULT
         && this.getReflection() != ReflectionManager.DEFAULT) {
         return SystemMsg.INVALID_TARGET.packet(var1);
      }

      if (this.isCursedWeaponEquipped() || var1.isCursedWeaponEquipped()) {
         return SystemMsg.INVALID_TARGET.packet(var1);
      }

      if (var1.isOlyParticipant() || this.isOlyParticipant()) {
         return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS.packet(var1);
      }

      if (var1.getPlayerAccess().CanJoinParty && this.getPlayerAccess().CanJoinParty) {
         if (this.getTeam() != TeamType.NONE) {
            return SystemMsg.INVALID_TARGET.packet(var1);
         } else {
            return !this.isPartyRefusal() && !var1.isPartyRefusal() ? null : SystemMsg.INVALID_TARGET.packet(var1);
         }
      } else {
         return SystemMsg.INVALID_TARGET.packet(var1);
      }
   }

   public PcInventory getInventory() {
      return this.Hw;
   }

   @Override
   public long getWearedMask() {
      return this.Hw.getWearedMask();
   }

   public PcFreight getFreight() {
      return this.Hz;
   }

   public void removeItemFromShortCut(int var1) {
      this.HG.deleteShortCutByObjectId(var1);
   }

   public void removeSkillFromShortCut(int var1) {
      this.HG.deleteShortCutBySkillId(var1);
   }

   public boolean isSitting() {
      return this.IL;
   }

   public void setSitting(boolean var1) {
      this.IL = var1;
   }

   public boolean getSittingTask() {
      return this.sittingTaskLaunched;
   }

   @Override
   public void sitDown(StaticObjectInstance var1) {
      if (!this.isSitting() && !this.sittingTaskLaunched && !this.isAlikeDead()) {
         if (!this.isStunned() && !this.isSleeping() && !this.isParalyzed() && !this.isAttackingNow() && !this.isCastingNow() && !this.isMoving()) {
            this.resetWaitSitTime();
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_REST, null, null);
            if (var1 == null) {
               this.broadcastPacket(new ChangeWaitType(this, 0));
            } else {
               this.broadcastPacket(new ChairSit(this, var1));
            }

            this.IM = var1;
            this.setSitting(true);
            this.sittingTaskLaunched = true;
            ThreadPoolManager.getInstance().schedule(new GameObjectTasks.EndSitDownTask(this), 2500L);
         } else {
            this.getAI().setNextAction(NextAction.REST, null, null, false, false);
         }
      }
   }

   @Override
   public void standUp() {
      if (this.isSitting() && !this.sittingTaskLaunched && !this.isInStoreMode() && !this.isAlikeDead()) {
         this.getAI().clearNextAction();
         this.broadcastPacket(new ChangeWaitType(this, 1));
         this.IM = null;
         this.sittingTaskLaunched = true;
         ThreadPoolManager.getInstance().schedule(new GameObjectTasks.EndStandUpTask(this), 2500L);
      }
   }

   @Override
   protected Creature.MoveToLocationAction createMoveToLocation(Location var1, int var2, boolean var3) {
      boolean var4 = !Config.ALLOW_GEODATA;
      Location var5 = this.getLoc();
      Location var6 = var1.clone();
      if (this.isInBoat()) {
         var2 = (int)(var2 + (var5.distance(var6) - 3 * this.getBoat().getActingRange()));
         var4 = true;
      }

      return Config.MOVE_OFFLOAD_MTL_PC
         ? new Player.MoveToLocationActionForOffload(this, var5, var6, var4, var2, var3)
         : new Creature.MoveToLocationAction(this, var5, var6, var4, var2, var3);
   }

   public void moveBackwardToLocationForPacket(Location var1, boolean var2) {
      if (this.isMoving() && Config.MOVE_OFFLOAD_MTL_PC) {
         this.JD.set(new Player.MoveToLocationOffloadData(var1, 0, var2));
      } else {
         this.moveToLocation(var1, 0, var2);
      }
   }

   public void updateWaitSitTime() {
      if (this.Hs < 200) {
         this.Hs += 2;
      }
   }

   public int getWaitSitTime() {
      return this.Hs;
   }

   public void resetWaitSitTime() {
      this.Hs = 0;
   }

   public Warehouse getWarehouse() {
      return this.Hx;
   }

   public ItemContainer getRefund() {
      return this.Hy;
   }

   public long getAdena() {
      return this.getInventory().getAdena();
   }

   public boolean reduceAdena(long var1) {
      return this.reduceAdena(var1, false);
   }

   public boolean reduceAdena(long var1, boolean var3) {
      if (var1 < 0L) {
         return false;
      }

      if (var1 == 0L) {
         return true;
      }

      boolean var4 = this.getInventory().reduceAdena(var1);
      if (var3 && var4) {
         this.sendPacket(SystemMessage2.removeItems(57, var1));
      }

      return var4;
   }

   public ItemInstance addAdena(long var1) {
      return this.addAdena(var1, false);
   }

   public ItemInstance addAdena(long var1, boolean var3) {
      if (var1 < 1L) {
         return null;
      }

      ItemInstance var4 = this.getInventory().addAdena(var1);
      if (var4 != null && var3) {
         this.sendPacket(SystemMessage2.obtainItems(57, var1, 0));
      }

      return var4;
   }

   public GameClient getNetConnection() {
      return this.GW;
   }

   public int getRevision() {
      return this.GW == null ? 0 : this.GW.getRevision();
   }

   public void setNetConnection(GameClient var1) {
      if (var1 == null) {
         this.getFarmSystem().stopFarmTask(false);
      }

      this.GW = var1;
   }

   public boolean isConnected() {
      return this.GW != null && this.GW.isConnected();
   }

   @Override
   public void onAction(Player var1, boolean var2) {
      if (this.isFrozen()) {
         var1.sendActionFailed();
      } else if (Events.onAction(var1, this, var2)) {
         var1.sendActionFailed();
      } else {
         if (var1.getTarget() != this) {
            var1.setTarget(this);
            if (var1.getTarget() == this) {
               var1.sendPacket(new MyTargetSelected(this.getObjectId(), 0));
               var1.sendPacket(this.makeStatusUpdate(9, 10, 11, 12));
            } else {
               var1.sendActionFailed();
            }
         } else if (this.getPrivateStoreType() != 0) {
            if (!(this.getRealDistance(var1) > this.getActingRange()) || var1.getAI().getIntention() == CtrlIntention.AI_INTENTION_INTERACT) {
               var1.doInteract(this);
            } else if (!var2) {
               var1.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
            } else {
               var1.sendActionFailed();
            }
         } else if (this.isAutoAttackable(var1)) {
            var1.getAI().Attack(this, false, var2);
         } else if (var1 != this) {
            if (var1.getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW && var1.getFollowTarget() == this) {
               var1.sendActionFailed();
            } else if (!var2) {
               var1.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this);
            } else {
               var1.sendActionFailed();
            }
         } else {
            var1.sendActionFailed();
         }
      }
   }

   @Override
   public void broadcastStatusUpdate() {
      if (this.needStatusUpdate()) {
         try (GameClient.PacketBatchScope ignored = this.openPacketBatch()) {
            StatusUpdate var1 = this.makeStatusUpdate(10, 12, 34, 9, 11, 33);
            this.sendPacket(var1);
            if (this.isInParty()) {
               this.getParty().broadcastToPartyMembers(this, new PartySmallWindowUpdate(this));
            }

            DuelEvent var2 = this.getEvent(DuelEvent.class);
            if (var2 != null) {
               var2.sendPacket(new ExDuelUpdateUserInfo(this), this.getTeam().revert().name());
            }

            if (this.isOlyCompetitionStarted()) {
               this.broadcastPacket(new ExOlympiadUserInfo(this));
            }
         }
      }
   }

   @Override
   public void broadcastCharInfo() {
      this.broadcastUserInfo(false);
   }

   public void broadcastUserInfo(boolean var1) {
      this.sendUserInfo(var1);
      if (this.isVisible() && !this.isInvisible()) {
         if (Config.BROADCAST_CHAR_INFO_INTERVAL == 0L) {
            var1 = true;
         }

         if (var1) {
            if (this.JE != null) {
               this.JE.cancel(false);
               this.JE = null;
            }

            this.broadcastCharInfoImpl();
         } else if (this.JE == null) {
            this.JE = ThreadPoolManager.getInstance().schedule(new Player.BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
         }
      }
   }

   public void setPolyId(int var1) {
      this.JF = var1;
      this.teleToLocation(this.getLoc());
      this.broadcastUserInfo(true);
   }

   public boolean isPolymorphed() {
      return this.JF != 0;
   }

   public int getPolyId() {
      return this.JF;
   }

   private void broadcastCharInfoImpl() {
      if (this.isVisible() && !this.isInvisible()) {
         L2GameServerPacket var1 = this.isPolymorphed() ? new NpcInfoPoly(this) : new CharInfo(this);
         try (GameClient.PacketBatchScope ignored = this.openPacketBatch()) {
            for (Player var3 : World.getAroundPlayers(this)) {
               var3.sendPacket(var1);
               var3.sendPacket(RelationChanged.create(var3, this, var3));
            }
         }
      }
   }

   public void setLastNpcInteractionTime() {
      this.HA = System.currentTimeMillis();
   }

   public boolean canMoveAfterInteraction() {
      return this.HA + 1000L < System.currentTimeMillis();
   }

   public void broadcastRelationChanged() {
      if (this.isVisible() && !this.isInvisible()) {
         for (Player var2 : World.getAroundPlayers(this)) {
            var2.sendPacket(RelationChanged.create(var2, this, var2));
         }
      }
   }

   public void sendEtcStatusUpdate() {
      if (this.isVisible()) {
         this.sendPacket(new EtcStatusUpdate(this));
      }
   }

   private void cr() {
      this.sendPacket(new UserInfo(this));
   }

   public void sendUserInfo() {
      this.sendUserInfo(false);
   }

   public void sendUserInfo(boolean var1) {
      if (this.isVisible() && !this.entering && !this.isLogoutStarted()) {
         if (Config.USER_INFO_INTERVAL != 0L && !var1) {
            if (this.JG == null) {
               this.JG = ThreadPoolManager.getInstance().schedule(new Player.UserInfoTask(), Config.USER_INFO_INTERVAL);
            }
         } else {
            if (this.JG != null) {
               this.JG.cancel(false);
               this.JG = null;
            }

            this.cr();
         }
      }
   }

   @Override
   public StatusUpdate makeStatusUpdate(int... var1) {
      StatusUpdate var2 = new StatusUpdate(this);

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
            case 14:
               var2.addAttribute(var6, this.getCurrentLoad());
               break;
            case 15:
               var2.addAttribute(var6, this.getMaxLoad());
               break;
            case 26:
               var2.addAttribute(var6, this._pvpFlag);
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

   public void sendStatusUpdate(boolean var1, boolean var2, int... var3) {
      if (var3.length != 0 && (!this.entering || var1)) {
         StatusUpdate var4 = this.makeStatusUpdate(var3);
         if (var4.hasAttributes()) {
            ArrayList var5 = new ArrayList(var2 ? 2 : 1);
            if (var2 && this.getPet() != null) {
               var5.add(this.getPet().makeStatusUpdate(var3));
            }

            var5.add(var4);
            if (!var1) {
               this.sendPacket(var5);
            } else if (this.entering) {
               this.broadcastPacketToOthers(var5);
            } else {
               this.broadcastPacket(var5);
            }
         }
      }
   }

   public int getAllyId() {
      return this.Ia == null ? 0 : this.Ia.getAllyId();
   }

   @Override
   public void sendPacket(IStaticPacket var1) {
      if (this.isConnected()) {
         if (!this.a(var1.packet(this))) {
            this.GW.sendPacket(var1.packet(this));
         }
      }
   }

   @Override
   public void sendPacket(IStaticPacket... var1) {
      if (this.isConnected() && this.GW != null) {
         List<l2.gameserver.network.l2.s2c.L2GameServerPacket> packets = new ArrayList<>(var1.length);
         for (IStaticPacket var5 : var1) {
            if (!this.a(var5)) {
               l2.gameserver.network.l2.s2c.L2GameServerPacket pkt = var5.packet(this);
               if (pkt != null) {
                  packets.add(pkt);
               }
            }
         }
         this.GW.sendPackets(packets);
      }
   }

   private boolean a(IStaticPacket var1) {
      return var1 == null;
   }

   @Override
   public void sendPacket(List<? extends IStaticPacket> var1) {
      if (this.isConnected() && this.GW != null) {
         List<l2.gameserver.network.l2.s2c.L2GameServerPacket> packets = new ArrayList<>(var1.size());
         for (IStaticPacket var3 : var1) {
            if (var3 != null) {
               l2.gameserver.network.l2.s2c.L2GameServerPacket pkt = var3.packet(this);
               if (pkt != null) {
                  packets.add(pkt);
               }
            }
         }
         this.GW.sendPackets(packets);
      }
   }

   public void doInteract(GameObject var1) {
      if (var1 != null && !this.isActionsDisabled()) {
         if (var1.isPlayer()) {
            Player var2 = (Player)var1;
            if (this.getRealDistance(var1) <= var1.getActingRange()) {
               switch (var2.getPrivateStoreType()) {
                  case 1:
                  case 8:
                     this.sendPacket(new PrivateStoreListSell(this, var2));
                  case 2:
                  case 4:
                  case 6:
                  case 7:
                  default:
                     break;
                  case 3:
                     this.sendPacket(new PrivateStoreListBuy(this, var2));
                     break;
                  case 5:
                     this.sendPacket(new RecipeShopSellList(this, var2));
               }

               this.sendActionFailed();
            } else if (!this.getAI().isIntendingInteract(var2)) {
               this.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, var2);
            }
         } else {
            var1.onAction(this, false);
         }
      } else {
         this.sendActionFailed();
      }
   }

   public void doAutoLootOrDrop(ItemInstance var1, NpcInstance var2) {
      boolean var3 = var2.isFlying() || this.getReflection().isAutolootForced();
      if ((var2.isRaid() || var2 instanceof ReflectionBossInstance) && !Config.AUTO_LOOT_FROM_RAIDS && !var1.isHerb() && !var3) {
         var1.dropToTheGround(this, var2);
      } else if (var1.isHerb()) {
         if (!this.Hu && !var3) {
            var1.dropToTheGround(this, var2);
         } else {
            Skill[] var4 = var1.getTemplate().getAttachedSkills();
            if (var4.length > 0) {
               for (Skill var8 : var4) {
                  this.altUseSkill(var8, this);
                  if (this.getPet() != null && this.getPet().isSummon() && !this.getPet().isDead()) {
                     this.getPet().altUseSkill(var8, this.getPet());
                  }
               }
            }

            var1.deleteMe();
         }
      } else if (!var3 && ArrayUtils.contains(Config.AUTO_LOOT_EXCLUDE_ITEM_IDS, var1.getItemId())) {
         var1.dropToTheGround(this, var2);
      } else if (var3 || this.Ht || var1.getItemId() == 57 && this.Hv) {
         if (!this.isInParty()) {
            if (!this.pickupItem(var1, Log.ItemLog.Pickup)) {
               var1.dropToTheGround(this, var2);
               return;
            }
         } else {
            this.getParty().distributeItem(this, var1, var2);
         }

         this.broadcastPickUpMsg(var1);
      } else {
         var1.dropToTheGround(this, var2);
      }
   }

   @Override
   public void doPickupItem(GameObject var1) {
      if (!var1.isItem()) {
         _log.warn("trying to pickup wrong target." + this.getTarget());
      } else {
         this.sendActionFailed();
         this.stopMove();
         Request var2 = this.getRequest();
         if (var2 != null && var2.isInProgress()) {
            if (var2.isTypeOf(Request.L2RequestType.TRADE)) {
               Player var3 = var2.getOtherPlayer(this);
               this.sendPacket(SendTradeDone.FAIL);
               var3.sendPacket(SendTradeDone.FAIL);
               this.sendPacket(new SystemMessage(149));
            }

            var2.cancel();
         }

         ItemInstance var12 = (ItemInstance)var1;
         synchronized (var12) {
            if (var12.isVisible()) {
               if (!ItemFunctions.checkIfCanPickup(this, var12)) {
                  SystemMessage var14;
                  if (var12.getItemId() == 57) {
                     var14 = new SystemMessage(55);
                     var14.addNumber(var12.getCount());
                  } else {
                     var14 = new SystemMessage(56);
                     var14.addItemName(var12.getItemId());
                  }

                  this.sendPacket(var14);
               } else if (var12.isHerb()) {
                  Skill[] var13 = var12.getTemplate().getAttachedSkills();
                  if (var13.length > 0) {
                     for (Skill var9 : var13) {
                        this.altUseSkill(var9, this);
                        if (this.getPet() != null && this.getPet().isSummon() && !this.getPet().isDead()) {
                           this.getPet().altUseSkill(var9, this.getPet());
                        }
                     }
                  }

                  this.broadcastPacket(new GetItem(var12, this.getObjectId()));
                  var12.deleteMe();
               } else {
                  FlagItemAttachment var5 = var12.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment)var12.getAttachment() : null;
                  if (this.isInParty() && var5 == null) {
                     this.getParty().distributeItem(this, var12, null);
                  } else if (this.pickupItem(var12, Log.ItemLog.Pickup)) {
                     this.broadcastPacket(new GetItem(var12, this.getObjectId()));
                     this.broadcastPickUpMsg(var12);
                     var12.pickupMe();
                  }
               }
            }
         }
      }
   }

   public boolean pickupItem(ItemInstance var1, Log.ItemLog var2) {
      PickableAttachment var3 = var1.getAttachment() instanceof PickableAttachment ? (PickableAttachment)var1.getAttachment() : null;
      if (!ItemFunctions.canAddItem(this, var1)) {
         return false;
      }

      Log.LogItem(this, var2, var1);
      this.sendPacket(SystemMessage2.obtainItems(var1));
      this.getInventory().addItem(var1);
      this.getListeners().onItemPickup(var1);
      if (var3 != null) {
         var3.pickUp(this);
      }

      this.sendChanges();
      return true;
   }

   public void setObjectTarget(GameObject var1) {
      this.setTarget(var1);
      if (var1 != null) {
         if (var1 == this.getTarget()) {
            if (var1.isNpc()) {
               NpcInstance var2 = (NpcInstance)var1;
               this.sendPacket(new MyTargetSelected(var2.getObjectId(), this.getLevel() - var2.getLevel()));
               this.sendPacket(var2.makeStatusUpdate(9, 10));
               this.sendPacket(new ValidateLocation(var2), ActionFail.STATIC);
            } else {
               this.sendPacket(new MyTargetSelected(var1.getObjectId(), 0));
            }
         }
      }
   }

   @Override
   public void setTarget(GameObject var1) {
      if (var1 != null && !var1.isVisible()) {
         var1 = null;
      }

      if (var1 instanceof FestivalMonsterInstance && !this.isFestivalParticipant()) {
         var1 = null;
      }

      Party var2 = this.getParty();
      if (var2 != null && var2.isInDimensionalRift()) {
         int var3 = var2.getDimensionalRift().getType();
         int var4 = var2.getDimensionalRift().getCurrentRoom();
         if (var1 != null && !DimensionalRiftManager.getInstance().getRoom(var3, var4).checkIfInZone(var1.getX(), var1.getY(), var1.getZ())) {
            var1 = null;
         }
      }

      GameObject var5 = this.getTarget();
      if (var5 != null) {
         if (var5.equals(var1)) {
            return;
         }

         if (var5.isCreature()) {
            ((Creature)var5).removeStatusListener(this);
         }

         this.broadcastPacket(new TargetUnselected(this));
      }

      if (var1 != null) {
         if (var1.isCreature()) {
            ((Creature)var1).addStatusListener(this);
         }

         this.broadcastPacket(new TargetSelected(this.getObjectId(), var1.getObjectId(), this.getLoc()));
      }

      super.setTarget(var1);
   }

   @Override
   public ItemInstance getActiveWeaponInstance() {
      return this.getInventory().getPaperdollItem(7);
   }

   @Override
   public WeaponTemplate getActiveWeaponItem() {
      ItemInstance var1 = this.getActiveWeaponInstance();
      return var1 == null ? this.getFistsWeaponItem() : (WeaponTemplate)var1.getTemplate();
   }

   @Override
   public ItemInstance getSecondaryWeaponInstance() {
      return this.getInventory().getPaperdollItem(8);
   }

   @Override
   public WeaponTemplate getSecondaryWeaponItem() {
      ItemInstance var1 = this.getSecondaryWeaponInstance();
      if (var1 == null) {
         return this.getFistsWeaponItem();
      }

      ItemTemplate var2 = var1.getTemplate();
      return var2 instanceof WeaponTemplate ? (WeaponTemplate)var2 : null;
   }

   public boolean isWearingArmor(ArmorTemplate.ArmorType var1) {
      ItemInstance var2 = this.getInventory().getPaperdollItem(10);
      if (var2 == null) {
         return var1 == ArmorTemplate.ArmorType.NONE;
      }

      if (var2.getItemType() != var1) {
         return false;
      }

      if (var2.getBodyPart() == 32768) {
         return true;
      }

      if (var2.getBodyPart() == 131072) {
         return true;
      }

      ItemInstance var3 = this.getInventory().getPaperdollItem(11);
      return var3 == null ? var1 == ArmorTemplate.ArmorType.NONE : var3.getItemType() == var1;
   }

   @Override
   public void reduceCurrentHp(
      double var1, Creature var3, Skill var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9, boolean var10, boolean var11
   ) {
      if (var3 != null && !this.isDead() && (!var3.isDead() || var10)) {
         if (var3.isPlayer() && Math.abs(var3.getLevel() - this.getLevel()) > 10) {
            if (var3.getKarma() > 0 && this.getEffectList().getEffectsBySkillId(5182) != null && !this.isInZone(Zone.ZoneType.SIEGE)) {
               return;
            }

            if (this.getKarma() > 0 && var3.getEffectList().getEffectsBySkillId(5182) != null && !var3.isInZone(Zone.ZoneType.SIEGE)) {
               return;
            }
         }

         super.reduceCurrentHp(var1, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   @Override
   protected void onReduceCurrentHp(double var1, Creature var3, Skill var4, boolean var5, boolean var6, boolean var7) {
      double var8 = var1;
      if (var6) {
         this.standUp();
         if (this.isFakeDeath()) {
            this.breakFakeDeath();
         }
      }

      if (var3.isPlayable() && !var7 && this.getCurrentCp() > 0.0) {
         double var10 = this.getCurrentCp();
         if (var10 >= var1) {
            var10 -= var1;
            var1 = 0.0;
         } else {
            var1 -= var10;
            var10 = 0.0;
         }

         this.setCurrentCp(var10);
      }

      double var14 = this.getCurrentHp();
      if (this.isOlyParticipant()) {
         if (this.isOlyCompetitionStarted() && !this.getOlyParticipant().OnDamaged(this, var3, var1, var14, var8)) {
            return;
         }

         if (!this.getOlyParticipant().isAlive()) {
            return;
         }
      }

      DuelEvent var12 = this.getEvent(DuelEvent.class);
      if (var12 != null && var14 - var1 <= 1.0) {
         this.setCurrentHp(1.0, false);
         var12.onDie(this);
      } else {
         super.onReduceCurrentHp(var1, var3, var4, var5, var6, var7);
      }
   }

   @Override
   public boolean isAlikeDead() {
      return this.Hr == 1 || super.isAlikeDead();
   }

   @Override
   public boolean isMovementDisabled() {
      return this.isFakeDeath() || super.isMovementDisabled();
   }

   @Override
   public boolean isActionsDisabled() {
      return this.isFakeDeath() || super.isActionsDisabled();
   }

   @Override
   public void doAttack(Creature var1) {
      if (!this.isFakeDeath() && !this.isInMountTransform()) {
         super.doAttack(var1);
      }
   }

   @Override
   public void onHitTimer(Creature var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      if (this.isFakeDeath()) {
         this.sendActionFailed();
      } else {
         super.onHitTimer(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public boolean isFakeDeath() {
      return this.Hr != 0;
   }

   public void setFakeDeath(int var1) {
      this.Hr = var1;
   }

   public void breakFakeDeath() {
      this.getEffectList().stopAllSkillEffects(EffectType.FakeDeath);
   }

   private void g(Creature var1) {
      if (Config.ALT_GAME_DELEVEL) {
         if (!this.isInZoneBattle() && !this.isInZone(Zone.ZoneType.fun)) {
            this.deathPenalty(var1);
         }
      }
   }

   public final boolean atWarWith(Player var1) {
      return this.Ia != null
         && var1.getClan() != null
         && this.getPledgeType() != -1
         && var1.getPledgeType() != -1
         && this.Ia.isAtWarWith(var1.getClan().getClanId());
   }

   public boolean atMutualWarWith(Player var1) {
      return this.Ia != null
         && var1.getClan() != null
         && this.getPledgeType() != -1
         && var1.getPledgeType() != -1
         && this.Ia.isAtWarWith(var1.getClan().getClanId())
         && var1.getClan().isAtWarWith(this.Ia.getClanId());
   }

   @Override
   public void doPurePk(Player var1) {
      super.doPurePk(var1);
      var1.setPkKills(var1.getPkKills() + 1);
      if (Config.SERVICES_PK_ANNOUNCE > 0) {
         if (Config.SERVICES_PK_ANNOUNCE == 1) {
            Announcements.getInstance().announceByCustomMessage("player.pkannounce", new String[]{var1.getName(), this.getName()});
         } else {
            Announcements.getInstance()
               .announceToAll(
                  new SystemMessage(2010).addString(String.format(": %s PK killed by %s", this.getName(), var1.getName())).addZoneName(var1.getLoc())
               );
         }
      }
   }

   private final void d(Player var1, boolean var2) {
      if (var2) {
         this.doPurePk(var1);
         var1.getListeners().onPvpPkKill(this, true);
      } else {
         if (Config.PVP_INCREASE_SAME_IP_CHECK && StringUtils.isNotEmpty(this.getIP()) && Objects.equals(this.getIP(), var1.getIP())) {
            return;
         }

         if (Config.PVP_INCREASE_SAME_HWID_CHECK
            && !Objects.isNull(this.getNetConnection())
            && !Objects.isNull(var1.getNetConnection())
            && StringUtils.isNotEmpty(this.getNetConnection().getHwid())
            && Objects.equals(this.getNetConnection().getHwid(), var1.getNetConnection().getHwid())) {
            return;
         }

         var1.setPvpKills(var1.getPvpKills() + Config.PVP_POINTS_AMOUNT_ADD);
         var1.getListeners().onPvpPkKill(this, false);
         if (Config.SERVICES_PK_ANNOUNCE > 1) {
            Announcements.getInstance()
               .announceToAll(
                  new SystemMessage(2010).addString(String.format(": %s PvP killed by %s", this.getName(), var1.getName())).addZoneName(var1.getLoc())
               );
         }
      }

      if (Config.SERVICES_PK_KILL_BONUS_ENABLE || Config.SERVICES_PVP_KILL_BONUS_ENABLE) {
         boolean var3 = true;
         boolean var4 = true;
         if (Config.SERVICES_PK_PVP_BONUS_TIE_IF_SAME_IP) {
            var3 = this.getIP() == null && var1.getIP() != null || this.getIP() != null && !this.getIP().equals(var1.getIP());
         }

         if (Config.SERVICES_PK_PVP_BONUS_TIE_IF_SAME_HWID) {
            String var5 = this.getNetConnection() != null ? this.getNetConnection().getHwid() : null;
            String var6 = var1.getNetConnection() != null ? var1.getNetConnection().getHwid() : null;
            var4 = var5 == null && var6 == null || var5 != null && !var5.equals(var6);
         }

         long var10 = System.currentTimeMillis();
         long var7 = var1.getVarLong("LastPVPPKKill", 0L);
         if (this.isConnected() && var3 && var4 && var10 - var7 > Config.SERVICES_PK_KILL_BONUS_INTERVAL) {
            if (var2) {
               if (Config.SERVICES_PK_KILL_BONUS_REWARD_ITEM != null && Config.SERVICES_PK_KILL_BONUS_REWARD_ITEM.length > 0) {
                  for (int var11 = 0; var11 < Config.SERVICES_PK_KILL_BONUS_REWARD_ITEM.length; var11++) {
                     if (Config.SERVICES_PK_KILL_BONUS_REWARD_ITEM[var11] > 0 && Rnd.chance(Config.SERVICES_PK_KILL_BONUS_REWARD_CHANCE[var11])) {
                        ItemFunctions.addItem(var1, Config.SERVICES_PK_KILL_BONUS_REWARD_ITEM[var11], Config.SERVICES_PK_KILL_BONUS_REWARD_COUNT[var11], true);
                     }
                  }
               }
            } else if (Config.SERVICES_PVP_KILL_BONUS_REWARD_ITEM != null && Config.SERVICES_PVP_KILL_BONUS_REWARD_ITEM.length > 0) {
               for (int var9 = 0; var9 < Config.SERVICES_PVP_KILL_BONUS_REWARD_ITEM.length; var9++) {
                  if (Config.SERVICES_PVP_KILL_BONUS_REWARD_ITEM[var9] > 0 && Rnd.chance(Config.SERVICES_PVP_KILL_BONUS_REWARD_CHANCE[var9])) {
                     ItemFunctions.addItem(var1, Config.SERVICES_PVP_KILL_BONUS_REWARD_ITEM[var9], Config.SERVICES_PVP_KILL_BONUS_REWARD_COUNT[var9], true);
                  }
               }
            }

            var1.setVar("LastPVPPKKill", var10, -1L);
         }
      }
   }

   public void checkAddItemToDrop(List<ItemInstance> var1, List<ItemInstance> var2, int var3) {
      for (int var4 = 0; var4 < var3 && !var2.isEmpty(); var4++) {
         var1.add(var2.remove(Rnd.get(var2.size())));
      }
   }

   public FlagItemAttachment getActiveWeaponFlagAttachment() {
      ItemInstance var1 = this.getActiveWeaponInstance();
      return var1 != null && var1.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment)var1.getAttachment() : null;
   }

   protected void doPKPVPManage(Creature var1) {
      FlagItemAttachment var2 = this.getActiveWeaponFlagAttachment();
      if (var2 != null) {
         var2.onDeath(this, var1);
      }

      if (var1 != null && var1 != this.Im && var1 != this) {
         if (!this.isInZoneBattle() && !var1.isInZoneBattle() || Config.BATTLE_ZONE_PVP_COUNT) {
            boolean var3 = this.isInZone(Zone.ZoneType.fun);
            if (Config.FUN_ZONE_PVP_COUNT || !var3) {
               if (!(var1 instanceof Summon) || (var1 = var1.getPlayer()) != null) {
                  if (var1.isPlayer()) {
                     Player var4 = (Player)var1;
                     int var5 = this.getLevel() - var4.getLevel() >= 20 ? Config.CRP_REWARD_ON_WAR_KILL_OVER_LEVEL : Config.CRP_REWARD_ON_WAR_KILL;
                     boolean var6 = this.atMutualWarWith(var4);
                     if (var6
                        && var4.getClan().getReputationScore() > 0
                        && this.Ia.getLevel() >= 5
                        && this.Ia.getReputationScore() > 0
                        && var4.getClan().getLevel() >= 5) {
                        this.Ia
                           .broadcastToOtherOnlineMembers(
                              new SystemMessage(1782).addString(this.getName()).addNumber(-this.Ia.incReputation(-var5, true, "ClanWar")), this
                           );
                        var4.getClan()
                           .broadcastToOtherOnlineMembers(new SystemMessage(1783).addNumber(var4.getClan().incReputation(var5, true, "ClanWar")), var4);
                     }

                     if (!Config.SIEGE_ZONE_PVP_COUNT && this.isOnSiegeField()) {
                        return;
                     }

                     if (Config.FUN_ZONE_PVP_COUNT && var3
                        || Config.BATTLE_ZONE_PVP_COUNT && (this.isInZoneBattle() || var1.isInZoneBattle())
                        || Config.SIEGE_ZONE_PVP_COUNT && this.isOnSiegeField()) {
                        this.d(var4, false);
                        var4.sendChanges();
                        return;
                     }

                     if (this._pvpFlag <= 0 && !var6) {
                        this.d(var4, this.De <= 0);
                     } else {
                        this.d(var4, false);
                     }

                     var4.sendChanges();
                  }

                  int var23 = this.De;
                  boolean var24 = var1.isPlayable() || var1 instanceof GuardInstance;
                  if ((!var1.isMonster() || Config.DROP_ITEMS_ON_DIE)
                     && (!var24 || this.GX >= Config.MIN_PK_TO_ITEMS_DROP && (var23 != 0 || !Config.KARMA_NEEDED_TO_DROP))
                     && !this.isFestivalParticipant()
                     && (var1.isMonster() || var24)) {
                     if (Config.KARMA_DROP_GM || !this.isGM()) {
                        if (!this.isTradeBannedByGM()) {
                           if (Config.ITEM_ANTIDROP_FROM_PK <= 0 || this.getInventory().getItemByItemId(Config.ITEM_ANTIDROP_FROM_PK) == null) {
                              int var25 = var24 ? Config.KARMA_DROP_ITEM_LIMIT : 1;
                              double var7;
                              if (var24) {
                                 var7 = this.GX * Config.KARMA_DROPCHANCE_MOD + Config.KARMA_DROPCHANCE_BASE;
                              } else {
                                 var7 = Config.NORMAL_DROPCHANCE_BASE;
                              }

                              int var9 = 0;
                              int var10 = 0;
                              int var11 = 0;

                              for (int var12 = 0; var12 < Math.ceil(var7 / 100.0) && var12 < var25; var12++) {
                                 if (Rnd.chance(var7)) {
                                    int var13 = Rnd.get(Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT + Config.DROPCHANCE_ITEM) + 1;
                                    if (var13 > Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT) {
                                       var11++;
                                    } else if (var13 > Config.DROPCHANCE_EQUIPPED_WEAPON) {
                                       var9++;
                                    } else {
                                       var10++;
                                    }
                                 }
                              }

                              LazyArrayList var26 = new LazyArrayList();
                              LazyArrayList var27 = new LazyArrayList();
                              LazyArrayList var14 = new LazyArrayList();
                              LazyArrayList var15 = new LazyArrayList();
                              this.getInventory().writeLock();

                              try {
                                 for (ItemInstance var19 : this.getInventory().getItems()) {
                                    if (var19.canBeDropped(this, true) && !Config.KARMA_LIST_NONDROPPABLE_ITEMS.contains(var19.getItemId())) {
                                       if (var19.getTemplate().getType2() == 0) {
                                          var15.add(var19);
                                       } else if (var19.getTemplate().getType2() == 1 || var19.getTemplate().getType2() == 2) {
                                          var14.add(var19);
                                       } else if (var19.getTemplate().getType2() == 5) {
                                          var27.add(var19);
                                       }
                                    }
                                 }

                                 this.checkAddItemToDrop(var26, var15, var10);
                                 this.checkAddItemToDrop(var26, var14, var9);
                                 this.checkAddItemToDrop(var26, var27, var11);
                                 if (var26.isEmpty()) {
                                    return;
                                 }

                                 for (Object __raw_var29_2951 : (Iterable<?>)(Object)(var26)) {
                                    ItemInstance var29 = (ItemInstance)__raw_var29_2951;
                                    if (var29.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED) {
                                       var29.setVariationStat1(0);
                                       var29.setVariationStat2(0);
                                    }

                                    var29 = this.getInventory().removeItem(var29);
                                    Log.LogItem(this, Log.ItemLog.PvPDrop, var29);
                                    if (var29.getEnchantLevel() > 0) {
                                       this.sendPacket(new SystemMessage(375).addNumber(var29.getEnchantLevel()).addItemName(var29.getItemId()));
                                    } else {
                                       this.sendPacket(new SystemMessage(298).addItemName(var29.getItemId()));
                                    }

                                    if (!var1.isPlayable() || (!Config.AUTO_LOOT || !Config.AUTO_LOOT_PK) && !this.isInFlyingTransform()) {
                                       var29.dropToTheGround(this, Location.findAroundPosition(this, Config.KARMA_RANDOM_DROP_LOCATION_LIMIT));
                                    } else {
                                       var1.getPlayer().getInventory().addItem(var29);
                                       Log.LogItem(this, Log.ItemLog.Pickup, var29);
                                       var1.getPlayer().sendPacket(SystemMessage2.obtainItems(var29));
                                    }
                                 }
                              } finally {
                                 this.getInventory().writeUnlock();
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   protected void onDeath(Creature var1) {
      this.getDeathPenalty().checkCharmOfLuck();
      this.getFarmSystem().stopFarmTask(false);
      if (this.isInStoreMode()) {
         this.setPrivateStoreType(0);
      }

      if (this.isProcessingRequest()) {
         Request var2 = this.getRequest();
         if (this.isInTrade()) {
            Player var3 = var2.getOtherPlayer(this);
            this.sendPacket(SendTradeDone.FAIL);
            var3.sendPacket(SendTradeDone.FAIL);
         }

         var2.cancel();
      }

      this.setAgathion(0);
      boolean var4 = true;
      if (Config.ALLOW_CURSED_WEAPONS) {
         if (this.isCursedWeaponEquipped()) {
            CursedWeaponsManager.getInstance().dropPlayer(this);
            var4 = false;
         } else if (var1 != null && var1.isPlayer() && var1.isCursedWeaponEquipped()) {
            CursedWeaponsManager.getInstance().increaseKills(((Player)var1).getCursedWeaponEquippedId());
            var4 = false;
         }
      }

      if (var4) {
         this.doPKPVPManage(var1);
         this.g(var1);
      }

      this.getDeathPenalty().notifyDead(var1);
      this.setIncreasedForce(0);
      if (this.isInParty() && this.getParty().isInReflection() && this.getParty().getReflection() instanceof DimensionalRift) {
         ((DimensionalRift)this.getParty().getReflection()).memberDead(this);
      }

      this.stopWaterTask();
      if (!this.isSalvation() && this.isOnSiegeField() && this.isCharmOfCourage()) {
         this.setCharmOfCourage(false);
      }

      if (this.getLevel() < 6) {
         Quest var5 = QuestManager.getQuest(255);
         if (var5 != null) {
            this.processQuestEvent(var5.getName(), "CE30", null);
         }
      }

      super.onDeath(var1);
   }

   public void restoreExp() {
      this.restoreExp(100.0);
   }

   public void restoreExp(double var1) {
      if (var1 != 0.0) {
         int var3 = 0;
         String var4 = this.getVar("lostexp");
         if (var4 != null) {
            var3 = Integer.parseInt(var4);
            this.unsetVar("lostexp");
         }

         if (var3 != 0) {
            this.addExpAndSp((long)(var3 * var1 / 100.0), 0L);
         }
      }
   }

   public void deathPenalty(Creature var1) {
      if (var1 != null) {
         boolean var2 = var1.getPlayer() != null && this.atWarWith(var1.getPlayer());
         double var3 = this.getDeathPenalty().getLevel() * Config.ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
         if (var3 < 2.0) {
            var3 = 1.0;
         } else {
            var3 /= 2.0;
         }

         double var5 = 8.0;
         int var7 = this.getLevel();
         if (var7 >= 79) {
            var5 = 1.0;
         } else if (var7 >= 78) {
            var5 = 1.5;
         } else if (var7 >= 76) {
            var5 = 2.0;
         } else if (var7 >= 40) {
            var5 = 4.0;
         }

         if (Config.ALT_DEATH_PENALTY) {
            var5 = var5 * Config.RATE_XP + this.GX * Config.ALT_PK_DEATH_RATE;
         }

         if (this.isFestivalParticipant() || var2) {
            var5 /= 4.0;
         }

         int var8 = (int)Math.round((Experience.LEVEL[var7 + 1] - Experience.LEVEL[var7]) * var5 / 100.0);
         var8 = (int)(var8 * var3);
         var8 = (int)this.calcStat(Stats.EXP_LOST, var8, var1, null);
         if (this.isOnSiegeField()) {
            SiegeEvent var9 = this.getEvent(SiegeEvent.class);
            if (var9 != null) {
               var8 = 0;
            }
         }

         long var18 = this.getExp();
         this.addExpAndSp(-var8, 0L);
         long var11 = var18 - this.getExp();
         if (!this.isCursedWeaponEquipped() && var8 > 0 && this.getKarma() > 0) {
            int var13 = Config.KARMA_STATIC_LOST_ON_DEATH;
            int var14 = Formulas.calculateKarmaLost(this, var8);
            if (var13 != -1) {
               this.decreaseKarma(Math.max(var13, 0));
            } else {
               this.decreaseKarma(Math.max(var14, 0));
            }
         }

         if (var11 > 0L) {
            this.setVar("lostexp", String.valueOf(var11), -1L);
         }
      }
   }

   public void setRequest(Request var1) {
      this.Iq = var1;
   }

   public Request getRequest() {
      return this.Iq;
   }

   public boolean isBusy() {
      return this.isProcessingRequest()
         || this.isOutOfControl()
         || this.isOlyParticipant()
         || this.getTeam() != TeamType.NONE
         || this.isInStoreMode()
         || this.isInDuel()
         || this.getMessageRefusal()
         || this.isBlockAll()
         || this.isInvisible();
   }

   public boolean isProcessingRequest() {
      return this.Iq == null ? false : this.Iq.isInProgress();
   }

   public boolean isInTrade() {
      return this.isProcessingRequest() && this.getRequest().isTypeOf(Request.L2RequestType.TRADE);
   }

   public List<L2GameServerPacket> addVisibleObject(GameObject var1, Creature var2) {
      return !this.isLogoutStarted() && var1 != null && var1.getObjectId() != this.getObjectId() && var1.isVisible()
         ? var1.addPacketList(this, var2)
         : Collections.emptyList();
   }

   @Override
   public List<L2GameServerPacket> addPacketList(Player var1, Creature var2) {
      if (this.isInvisible() && var1.getObjectId() != this.getObjectId()) {
         return Collections.emptyList();
      }

      if (this.getPrivateStoreType() != 0 && var1.getVarB("notraders")) {
         return Collections.emptyList();
      }

      if (this.isInObserverMode() && this.getCurrentRegion() != this.getObserverRegion() && this.getObserverRegion() == var1.getCurrentRegion()) {
         return Collections.emptyList();
      }

      ArrayList var3 = new ArrayList();
      if (var1.getObjectId() != this.getObjectId()) {
         var3.add(this.isPolymorphed() ? new NpcInfoPoly(this) : new CharInfo(this));
      }

      if (this.isSitting() && this.IM != null) {
         var3.add(new ChairSit(this, this.IM));
      }

      if (this.getPrivateStoreType() != 0) {
         if (this.getPrivateStoreType() == 3) {
            var3.add(new PrivateStoreMsgBuy(this));
         } else if (this.getPrivateStoreType() == 1 || this.getPrivateStoreType() == 8) {
            var3.add(new PrivateStoreMsgSell(this));
         } else if (this.getPrivateStoreType() == 5) {
            var3.add(new RecipeShopMsg(this));
         }

         if (var1.isInZonePeace()) {
            return var3;
         }
      }

      if (this.isCastingNow()) {
         Creature var4 = this.getCastingTarget();
         Skill var5 = this.getCastingSkill();
         long var6 = this.getAnimationEndTime();
         if (var5 != null && var4 != null && var4.isCreature() && this.getAnimationEndTime() > 0L) {
            var3.add(new MagicSkillUse(this, var4, var5, (int)(var6 - System.currentTimeMillis()), 0L));
         }
      }

      if (this.isInCombat()) {
         var3.add(new AutoAttackStart(this.getObjectId()));
      }

      var3.add(RelationChanged.create(var1, this, var1));
      if (this.isInBoat()) {
         var3.add(this.getBoat().getOnPacket(this, this.getInBoatPosition()));
      } else if (this.isMoving() || this.isFollowing()) {
         var3.add(this.movePacket());
      }

      return var3;
   }

   public List<L2GameServerPacket> removeVisibleObject(GameObject var1, List<L2GameServerPacket> var2) {
      if (!this.isLogoutStarted() && var1 != null && var1.getObjectId() != this.getObjectId()) {
         List var3 = var2 == null ? var1.deletePacketList() : var2;
         this.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, var1);
         return var3;
      } else {
         return null;
      }
   }

   private void z(int var1) {
      if (var1 > 0) {
         this.sendPacket(Msg.YOU_HAVE_INCREASED_YOUR_LEVEL);
         this.broadcastPacket(new SocialAction(this.getObjectId(), 15));
         this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp());
         this.setCurrentCp(this.getMaxCp());
         Quest var2 = QuestManager.getQuest(255);
         if (var2 != null) {
            this.processQuestEvent(var2.getName(), "CE40", null);
         }
      } else if (var1 < 0) {
         this.checkSkills();
      }

      if (this.isInParty()) {
         this.getParty().recalculatePartyData();
      }

      if (this.Ia != null) {
         this.Ia.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
      }

      if (this.Dn != null) {
         this.Dn.broadcastPlayerUpdate(this);
      }

      this.d(true);
      this.getListeners().onSetLevel(this.getLevel());
   }

   public void checkSkills() {
      if (!Config.ALT_WEAK_SKILL_LEARN) {
         for (Skill var4 : this.getAllSkillsArray()) {
            SkillTreeTable.checkSkill(this, var4);
         }

         this.sendPacket(new SkillList(this));
      }
   }

   public void startTimers() {
      this.startAutoSaveTask();
      this.startPcBangPointsTask();
      this.startHourlyTask();
      this.startBonusTask();
      this.getInventory().startTimers();
      this.resumeQuestTimers();
   }

   public void stopAllTimers() {
      this.setAgathion(0);
      this.stopWaterTask();
      this.stopBonusTask();
      this.stopHourlyTask();
      this.stopKickTask();
      this.stopPcBangPointsTask();
      this.stopAutoSaveTask();
      this.getInventory().stopAllTimers();
      this.stopQuestTimers();
      this.cx();
      this.stopUnjailTask();
      this.getFarmSystem().stopFarmTask(false);
      Future var1 = this.JU;
      if (var1 != null) {
         var1.cancel(false);
         this.JU = null;
      }
   }

   @Override
   public Summon getPet() {
      return this.Im;
   }

   public void setPet(Summon var1) {
      boolean var2 = false;
      if (this.Im != null && this.Im.isPet()) {
         var2 = true;
      }

      this.unsetVar("pet");
      this.Im = var1;
      this.autoShot();
      if (var1 == null) {
         if (var2) {
            if (this.isLogoutStarted() && this.getPetControlItem() != null) {
               this.setVar("pet", String.valueOf(this.getPetControlItem().getObjectId()), -1L);
            }

            this.setPetControlItem(null);
         }

         this.getEffectList().stopEffect(4140);
      }
   }

   public void scheduleDelete() {
      long var1 = 0L;
      if (Config.SERVICES_ENABLE_NO_CARRIER) {
         var1 = NumberUtils.toInt(this.getVar("noCarrier"), Config.SERVICES_NO_CARRIER_DEFAULT_TIME);
      }

      this.scheduleDelete(var1 * 1000L);
   }

   public void scheduleDelete(long var1) {
      if (!this.isLogoutStarted() && !this.isInOfflineMode()) {
         this.broadcastCharInfo();
         ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
               if (!Player.this.isConnected()) {
                  Player.this.co();
                  Player.this.deleteMe();
               }
            }
         }, var1);
         if (var1 > 0L
            && this.getTeam() == TeamType.NONE
            && !this.isOlyParticipant()
            && !this.isInAnyZone(Zone.ZoneType.peace_zone, Zone.ZoneType.SIEGE, Zone.ZoneType.offshore)) {
            this.setNoCarrierProtectionTime(System.currentTimeMillis() + 1000L * Math.min(var1, Config.NO_CARRIER_PROTECTION_TIME));
         }
      }
   }

   @Override
   protected void onDelete() {
      this.clearNpcSpawnPacer();
      super.onDelete();
      WorldRegion var1 = this.getObserverRegion();
      if (var1 != null) {
         var1.removeObject(this);
      }

      this.IE.notifyFriends(false);
      this.Hw.clear();
      this.Hx.clear();
      this.Im = null;
      this.Ir = null;
      this.Is = null;
      this.It = null;
      this.Iu = null;
      this.Iy = HardReferences.emptyRef();
      this.IB = null;
   }

   public void setTradeList(List<TradeItem> var1) {
      this.HQ = var1;
   }

   public List<TradeItem> getTradeList() {
      return this.HQ;
   }

   public String getSellStoreName() {
      return this.HL;
   }

   public void setSellStoreName(String var1) {
      this.HL = Strings.stripToSingleLine(var1);
   }

   public void setSellList(boolean var1, List<TradeItem> var2) {
      if (var1) {
         this.HM = Collections.emptyList();
         this.HN = var2;
      } else {
         this.HM = var2;
         this.HN = Collections.emptyList();
      }
   }

   public List<TradeItem> getSellList() {
      switch (this.getPrivateStoreType()) {
         case 1:
            return this.getSellList(false);
         case 8:
            return this.getSellList(true);
         default:
            return Collections.emptyList();
      }
   }

   public List<TradeItem> getSellList(boolean var1) {
      return var1 ? this.HN : this.HM;
   }

   public String getBuyStoreName() {
      return this.HO;
   }

   public void setBuyStoreName(String var1) {
      this.HO = Strings.stripToSingleLine(var1);
   }

   public void setBuyList(List<TradeItem> var1) {
      this.HP = var1;
   }

   public List<TradeItem> getBuyList() {
      List var1 = this.HP;
      return var1 != null ? var1 : Collections.emptyList();
   }

   public void setManufactureName(String var1) {
      this.HJ = Strings.stripToSingleLine(var1);
   }

   public String getManufactureName() {
      return this.HJ;
   }

   public List<ManufactureItem> getCreateList() {
      return this.HK;
   }

   public void setCreateList(List<ManufactureItem> var1) {
      this.HK = var1;
   }

   public void setPrivateStoreType(int var1) {
      int var2 = this.HI.get();
      if (var2 != var1 && this.HI.compareAndSet(var2, var1)) {
         if (var1 != 0) {
            if (var2 == 0) {
               this.sitDown(null);
               this.broadcastCharInfo();
            }

            this.setVar("storemode", String.valueOf(var1), -1L);
         } else if (var2 != 0) {
            this.unsetVar("storemode");
            if (!this.isDead()) {
               this.standUp();
               this.broadcastCharInfo();
            }
         }
      }

      this.getListeners().onSetPrivateStoreType(var1);
   }

   public boolean isInStoreMode() {
      return this.HI.get() != 0;
   }

   public int getPrivateStoreType() {
      return this.HI.get();
   }

   public void setClan(Clan var1) {
      if (this.Ia != var1 && this.Ia != null) {
         this.unsetVar("canWhWithdraw");
      }

      Clan var2 = this.Ia;
      if (var2 != null && var1 == null) {
         for (Skill var6 : var2.getAllSkills()) {
            this.removeSkill(var6, false);
         }
      }

      this.Ia = var1;
      if (var1 == null) {
         this.Ic = -128;
         this.Ib = 0;
         this.Id = 0;
         this.If = 0;
         this.getInventory().validateItems();
      } else {
         if (!var1.isAnyMember(this.getObjectId())) {
            this.setClan(null);
            if (!this.isNoble()) {
               this.setTitle("");
            }
         }
      }
   }

   @Override
   public Clan getClan() {
      return this.Ia;
   }

   public SubUnit getSubUnit() {
      return this.Ia == null ? null : this.Ia.getSubUnit(this.Ic);
   }

   public ClanHall getClanHall() {
      int var1 = this.Ia != null ? this.Ia.getHasHideout() : 0;
      return ResidenceHolder.getInstance().getResidence(ClanHall.class, var1);
   }

   public Castle getCastle() {
      int var1 = this.Ia != null ? this.Ia.getCastle() : 0;
      return ResidenceHolder.getInstance().getResidence(Castle.class, var1);
   }

   public Alliance getAlliance() {
      return this.Ia == null ? null : this.Ia.getAlliance();
   }

   public boolean isClanLeader() {
      return this.Ia != null && this.getObjectId() == this.Ia.getLeaderId();
   }

   public boolean isAllyLeader() {
      return this.getAlliance() != null && this.getAlliance().getLeader().getLeaderId() == this.getObjectId();
   }

   @Override
   public void reduceArrowCount() {
      this.sendPacket(SystemMsg.YOU_CAREFULLY_NOCK_AN_ARROW);
      if (!this.getInventory().destroyItemByObjectId(this.getInventory().getPaperdollObjectId(8), 1L)) {
         this.getInventory().setPaperdollItem(8, null);
         this.Ir = null;
      }
   }

   protected boolean checkAndEquipArrows() {
      if (this.getInventory().getPaperdollItem(8) == null) {
         ItemInstance var1 = this.getActiveWeaponInstance();
         if (var1 != null && var1.getItemType() == WeaponTemplate.WeaponType.BOW) {
            this.Ir = this.getInventory().findArrowForBow(var1.getTemplate());
         }

         if (this.Ir != null) {
            this.getInventory().setPaperdollItem(8, this.Ir);
         }
      } else {
         this.Ir = this.getInventory().getPaperdollItem(8);
      }

      return this.Ir != null;
   }

   public void setUptime(long var1) {
      this.Hl = var1;
   }

   public long getUptime() {
      return System.currentTimeMillis() - this.Hl;
   }

   public boolean isInParty() {
      return this._party != null;
   }

   public void setParty(Party var1) {
      this._party = var1;
   }

   public void joinParty(Party var1) {
      if (var1 != null) {
         var1.addPartyMember(this);
      }
   }

   public void leaveParty() {
      if (this.isInParty()) {
         this._party.removePartyMember(this, false);
      }
   }

   public Party getParty() {
      return this._party;
   }

   public void setLastPartyPosition(Location var1) {
      this.HZ = var1;
   }

   public Location getLastPartyPosition() {
      return this.HZ;
   }

   public boolean isGM() {
      return this.Ih == null ? false : this.Ih.IsGM;
   }

   public void setAccessLevel(int var1) {
      this.Ig = var1;
   }

   @Override
   public int getAccessLevel() {
      return this.Ig;
   }

   public void setPlayerAccess(PlayerAccess var1) {
      if (var1 != null) {
         this.Ih = var1;
      } else {
         this.Ih = new PlayerAccess();
      }

      this.setAccessLevel(!this.isGM() && !this.Ih.Menu ? 0 : 100);
   }

   public PlayerAccess getPlayerAccess() {
      return this.Ih;
   }

   @Override
   public double getLevelMod() {
      return (89.0 + this.getLevel()) / 100.0;
   }

   @Override
   public void updateStats() {
      if (!this.entering && !this.isLogoutStarted()) {
         this.refreshOverloaded();
         this.refreshExpertisePenalty();
         super.updateStats();
      }
   }

   @Override
   public void sendChanges() {
      if (!this.entering && !this.isLogoutStarted()) {
         super.sendChanges();
      }
   }

   public void updateKarma(boolean var1) {
      this.sendStatusUpdate(true, true, 27);
      if (var1) {
         this.broadcastRelationChanged();
      }
   }

   public boolean isOnline() {
      return this.oo;
   }

   public void setIsOnline(boolean var1) {
      this.oo = var1;
   }

   public void setOnlineStatus(boolean var1) {
      this.oo = var1;
      this.cs();
   }

   private void cs() {
      Connection var1 = null;
      PreparedStatement var2 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("UPDATE `characters` SET `online`=?, `lastAccess`=? WHERE `obj_id`=?");
         var2.setInt(1, this.isOnline() && !this.isInOfflineMode() ? 1 : 0);
         var2.setLong(2, System.currentTimeMillis() / 1000L);
         var2.setInt(3, this.getObjectId());
         var2.execute();
      } catch (Exception var7) {
         _log.error("", var7);
      } finally {
         DbUtils.closeQuietly(var1, var2);
      }
   }

   public void increaseKarma(long var1) {
      boolean var3 = this.De == 0;
      long var4 = this.De + var1;
      if (var4 > 2147483647L) {
         var4 = 2147483647L;
      }

      if (this.De == 0 && var4 > 0L) {
         if (this._pvpFlag > 0) {
            this._pvpFlag = 0;
            if (this.JZ != null) {
               this.JZ.cancel(true);
               this.JZ = null;
            }

            this.sendStatusUpdate(true, true, 26);
         }

         this.De = (int)var4;
      } else {
         this.De = (int)var4;
      }

      this.updateKarma(var3);
   }

   public void decreaseKarma(int var1) {
      boolean var2 = this.De > 0;
      this.De -= var1;
      if (this.De <= 0) {
         this.De = 0;
         this.updateKarma(var2);
      } else {
         this.updateKarma(false);
      }
   }

   public static Player create(int var0, int var1, String var2, String var3, int var4, int var5, int var6) {
      PlayerTemplate var7 = CharacterTemplateHolder.getInstance().getTemplate(ClassId.getClassById(var0), var1 == 0);
      Player var8 = new Player(IdFactory.getInstance().getNextId(), var7, var2);
      var8.setName(var3);
      var8.setTitle("");
      var8.setHairStyle(var4);
      var8.setHairColor(var5);
      var8.setFace(var6);
      var8.setCreateTime(System.currentTimeMillis());
      return !CharacterDAO.getInstance().insert(var8) ? null : var8;
   }

   public static Player restore(int var0) {
      Player var1 = null;
      Connection var2 = null;
      Statement var3 = null;
      Statement var4 = null;
      PreparedStatement var5 = null;
      ResultSet var6 = null;
      ResultSet var7 = null;
      ResultSet var8 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.createStatement();
         var4 = var2.createStatement();
         var6 = var3.executeQuery("SELECT * FROM `characters` WHERE `obj_Id`=" + var0 + " LIMIT 1");
         var7 = var4.executeQuery("SELECT `class_id` FROM `character_subclasses` WHERE `char_obj_id`=" + var0 + " AND `isBase`=1 LIMIT 1");
         if (var6.next() && var7.next()) {
            int var9 = var7.getInt("class_id");
            boolean var10 = var6.getInt("sex") == 1;
            PlayerTemplate var11 = CharacterTemplateHolder.getInstance().getTemplate(ClassId.getClassById(var9), !var10);
            var1 = new Player(var0, var11);
            CharacterVariablesDAO.getInstance().loadVariables(var0, var1.getVars());
            var1.Jx.putAll(InstanceReuseDAO.getInstance().load(var1));
            var1.ct();
            var1.IE.restore();
            var1.Jp = CharacterPostFriendDAO.getInstance().select(var1);
            CharacterGroupReuseDAO.getInstance().select(var1);
            var1.setBaseClass(var9);
            var1.pm = var6.getString("account_name");
            String var12 = var6.getString("char_name");
            var1.setName(var12);
            var1.setFace(var6.getInt("face"));
            var1.setHairStyle(var6.getInt("hairStyle"));
            var1.setHairColor(var6.getInt("hairColor"));
            var1.setHeading(0);
            var1.setKarma(var6.getInt("karma"), false);
            var1.setPvpKills(var6.getInt("pvpkills"));
            var1.setPkKills(var6.getInt("pkkills"));
            var1.setLeaveClanTime(var6.getLong("leaveclan") * 1000L);
            if (var1.getLeaveClanTime() > 0L && var1.canJoinClan()) {
               var1.setLeaveClanTime(0L);
            }

            var1.setDeleteClanTime(var6.getLong("deleteclan") * 1000L);
            if (var1.getDeleteClanTime() > 0L && var1.canCreateClan()) {
               var1.setDeleteClanTime(0L);
            }

            var1.setNoChannel(var6.getLong("nochannel") * 1000L);
            if (var1.getNoChannel() > 0L && var1.getNoChannelRemained() < 0L) {
               var1.setNoChannel(0L);
            }

            var1.setOnlineTime(var6.getLong("onlinetime") * 1000L);
            int var13 = var6.getInt("clanid");
            if (var13 > 0) {
               var1.setClan(ClanTable.getInstance().getClan(var13));
               var1.setPledgeType(var6.getInt("pledge_type"));
               var1.setPowerGrade(var6.getInt("pledge_rank"));
               var1.setLvlJoinedAcademy(var6.getInt("lvl_joined_academy"));
               var1.setApprentice(var6.getInt("apprentice"));
            }

            var1.setCreateTime(var6.getLong("createtime") * 1000L);
            var1.setDeleteTimer(var6.getInt("deletetime"));
            var1.setTitle(var6.getString("title"));
            if (var1.getVar("titlecolor") != null) {
               var1.setTitleColor(Integer.decode("0x" + var1.getVar("titlecolor")));
            }

            if (var1.getVar("namecolor") == null) {
               if (var1.isGM()) {
                  var1.setNameColor(Config.GM_NAME_COLOUR);
               } else if (var1.getClan() != null && var1.getClan().getLeaderId() == var1.getObjectId()) {
                  var1.setNameColor(Config.CLANLEADER_NAME_COLOUR);
               } else {
                  var1.setNameColor(Config.NORMAL_NAME_COLOUR);
               }
            } else {
               var1.setNameColor(Integer.decode("0x" + var1.getVar("namecolor")));
            }

            if (Config.AUTO_LOOT_INDIVIDUAL) {
               var1.Ht = var1.getVarB("AutoLoot", Config.AUTO_LOOT);
               var1.Hu = var1.getVarB("AutoLootHerbs", Config.AUTO_LOOT_HERBS);
            }

            var1.setFistsWeaponItem(var1.findFistsWeaponItem(var9));
            var1.setUptime(System.currentTimeMillis());
            var1.setLastAccess(var6.getLong("lastAccess"));
            int var14 = var6.getInt("rec_left");
            int var15 = var6.getInt("rec_have");
            var1.setKeyBindings(var6.getBytes("key_bindings"));
            var1.setPcBangPoints(var6.getInt("pcBangPoints"));
            var1.restoreRecipeBook();
            boolean var16 = false;
            var1.setNoble(NoblesController.getInstance().isNobles(var1));
            if (Config.OLY_ENABLED) {
               var1.setHero(HeroController.getInstance().isCurrentHero(var1));
               if (var1.isHero()) {
                  HeroController.getInstance().loadDiary(var1.getObjectId());
               }
            }

            if (Config.ALT_ALLOW_CUSTOM_HERO && !var1.isHero() && var1.getVar("CustomHeroEndTime") != null) {
               long var17 = var1.getVarLong("CustomHeroEndTime", 0L);
               long var19 = var17 - System.currentTimeMillis() / 1000L;
               if (var19 > 0L) {
                  var1.setCustomHero(true, var19, Config.ALT_ALLOW_CUSTOM_HERO_SKILLS);
                  var16 = !Config.ALT_ALLOW_CUSTOM_HERO_SKILLS;
               } else {
                  var1.setCustomHero(false, 0L, true);
                  var16 = true;
               }
            }

            var1.updatePledgeClass();
            int var44 = 0;
            String var18 = var1.getVar("jailed");
            boolean var45 = false;
            if (!StringUtils.isBlank(var18)) {
               int var20 = var18.indexOf(59);
               if (var20 > 0) {
                  long var21 = Long.parseLong(var18.substring(0, var20)) - System.currentTimeMillis();
                  if (var21 > 0L) {
                     var1.startUnjailTask(var1, (int)var21);
                     Location var23 = Location.findPointToStay(var1, Config.SERVICE_JAIL_COORDINATES, 50, 200);
                     var1.setXYZ(var23.getX(), var23.getY(), var23.getZ());
                     var44 = ReflectionManager.JAIL.getId();
                     var1.sitDown(null);
                     var1.block();
                     var45 = true;
                  } else {
                     var1.startUnjailTask(var1, 1000);
                  }
               }
            }

            if (!var45) {
               var1.setXYZ(var6.getInt("x"), var6.getInt("y"), var6.getInt("z"));
               String var46 = var1.getVar("reflection");
               if (var46 != null) {
                  var44 = Integer.parseInt(var46);
                  if (var44 > 0) {
                     String var56 = var1.getVar("backCoords");
                     if (var56 != null) {
                        var1.setLoc(Location.parseLoc(var56));
                        var1.unsetVar("backCoords");
                     }

                     var44 = 0;
                  }
               }
            }

            var1.setReflection(var44);
            EventHolder.getInstance().findEvent(var1);
            Quest.restoreQuestStates(var1);
            var1.getInventory().restore();
            restoreCharSubClasses(var1);
            var1.cp();
            var1.restoreGivableAndReceivedRec(var14, var15);

            for (ItemInstance var61 : var1.getInventory().getPaperdollItems()) {
               if (var61 != null && var61.isCursed()) {
                  CursedWeaponsManager.getInstance().checkPlayer(var1, var61);
               }
            }

            try {
               String var48 = var1.getVar("ExpandInventory");
               if (var48 != null) {
                  var1.setExpandInventory(Integer.parseInt(var48));
               }
            } catch (Exception var41) {
               _log.error("", var41);
            }

            try {
               String var49 = var1.getVar("ExpandWarehouse");
               if (var49 != null) {
                  var1.setExpandWarehouse(Integer.parseInt(var49));
               }
            } catch (Exception var40) {
               _log.error("", var40);
            }

            try {
               String var50 = var1.getVar("buffAnimRange");
               if (var50 != null) {
                  var1.setBuffAnimRange(Integer.parseInt(var50));
               }
            } catch (Exception var39) {
               _log.error("", var39);
            }

            try {
               var1.setNoShotsAnim(var1.getVarB("noShotsAnim", false));
            } catch (Exception var38) {
               _log.error("", var38);
            }

            try {
               String var51 = var1.getVar("notraders");
               if (var51 != null) {
                  var1.setNotShowTraders(Boolean.parseBoolean(var51));
               }
            } catch (Exception var37) {
               _log.error("", var37);
            }

            try {
               String var52 = var1.getVar("pet");
               if (var52 != null) {
                  var1.setPetControlItem(Integer.parseInt(var52));
               }
            } catch (Exception var36) {
               _log.error("", var36);
            }

            try {
               var1.setAntiSpam(var1.getVarLong("antispam", 0L), false);
            } catch (Exception var35) {
               _log.error("", var35);
            }

            var5 = var2.prepareStatement("SELECT `obj_Id`, `char_name` FROM `characters` WHERE `account_name`=? AND `obj_Id`!=?");
            var5.setString(1, var1.pm);
            var5.setInt(2, var0);
            var8 = var5.executeQuery();

            while (var8.next()) {
               Integer var53 = var8.getInt("obj_Id");
               String var58 = var8.getString("char_name");
               var1.It.put(var53, var58);
            }

            DbUtils.close(var5, var8);
            if (var16) {
               HeroController.removeSkills(var1);
            }

            LazyArrayList var54 = LazyArrayList.newInstance();
            World.getZones(var54, var1.getLoc(), var1.getReflection());
            if (!var54.isEmpty()) {
               for (Object __raw_var60_3981 : (Iterable<?>)(Object)(var54)) {
                  Zone var60 = (Zone)__raw_var60_3981;
                  if (var60.isType(Zone.ZoneType.no_restart)) {
                     if (System.currentTimeMillis() / 1000L - var1.getLastAccess() > var60.getRestartTime()) {
                        var1.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.EnterWorld.TeleportedReasonNoRestart", var1));
                        var1.setLoc(TeleportUtils.getRestartLocation(var1, RestartType.TO_VILLAGE));
                     }
                  } else if (var60.getType() == Zone.ZoneType.SIEGE) {
                     SiegeEvent var62 = var1.getEvent(SiegeEvent.class);
                     if (var62 != null) {
                        var1.setLoc(var62.getEnterLoc(var1));
                     } else {
                        Residence var24 = ResidenceHolder.getInstance().getResidence(var60.getParams().getInteger("residence"));
                        var1.setLoc(var24.getNotOwnerRestartPoint(var1));
                     }
                  }
               }
            }

            LazyArrayList.recycle(var54);
            if (DimensionalRiftManager.getInstance().checkIfInRiftZone(var1.getLoc(), false)) {
               var1.setLoc(DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords());
            }

            var1.cv();
            var1.HH.restore();
            var1.refreshExpertisePenalty();
            var1.refreshOverloaded();
            var1.getWarehouse().restore();
            var1.getFreight().restore();
            var1.restoreTradeList();
            int var55 = var1.getVarInt("storemode", 0);
            if (var55 != 0) {
               var1.setPrivateStoreType(var55);
               var1.setSitting(true);
            }

            var1.updateRam();
            var1.getFarmSystem().checkFarmTask();
            if (var1.getVar("lang@") == null) {
               var1.setVar("lang@", Config.DEFAULT_LANG, -1L);
            }

            if (Config.SERVICES_ENABLE_NO_CARRIER && var1.getVar("noCarrier") == null) {
               var1.setVar("noCarrier", Config.SERVICES_NO_CARRIER_DEFAULT_TIME, -1L);
            }
         }
      } catch (Exception var42) {
         _log.error("Could not restore char data!", var42);
      } finally {
         DbUtils.closeQuietly(var4, var7);
         DbUtils.closeQuietly(var5, var8);
         DbUtils.closeQuietly(var2, var3, var6);
      }

      return var1;
   }

   private void ct() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT `itemNum`, `itemId`, `itemCount`, `itemSender` FROM `character_premium_items` WHERE `charId`=?");
         var2.setInt(1, this.getObjectId());
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("itemNum");
            int var5 = var3.getInt("itemId");
            long var6 = var3.getLong("itemCount");
            String var8 = var3.getString("itemSender");
            PremiumItem var9 = new PremiumItem(var5, var6, var8);
            this.HE.put(var4, var9);
         }
      } catch (Exception var13) {
         _log.error("", var13);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public void updatePremiumItem(int var1, long var2) {
      Connection var4 = null;
      PreparedStatement var5 = null;

      try {
         var4 = DatabaseFactory.getInstance().getConnection();
         var5 = var4.prepareStatement("UPDATE `character_premium_items` SET `itemCount`=? WHERE `charId`=? AND `itemNum`=?");
         var5.setLong(1, var2);
         var5.setInt(2, this.getObjectId());
         var5.setInt(3, var1);
         var5.execute();
      } catch (Exception var10) {
         _log.error("", var10);
      } finally {
         DbUtils.closeQuietly(var4, var5);
      }
   }

   public void deletePremiumItem(int var1) {
      Connection var2 = null;
      PreparedStatement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("DELETE FROM `character_premium_items` WHERE `charId`=? AND `itemNum`=?");
         var3.setInt(1, this.getObjectId());
         var3.setInt(2, var1);
         var3.execute();
      } catch (Exception var8) {
         _log.error("", var8);
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }
   }

   public Map<Integer, PremiumItem> getPremiumItemList() {
      return this.HE;
   }

   public void store(boolean var1) {
      if (var1 && Config.ENABLE_ASYNC_CHARACTER_UPDATES) {
         int posX = this._stablePoint == null ? this.getX() : this._stablePoint.x;
         int posY = this._stablePoint == null ? this.getY() : this._stablePoint.y;
         int posZ = this._stablePoint == null ? this.getZ() : this._stablePoint.z;
         l2.gameserver.dao.AsyncCharacterDAO.getInstance().updateCoordinatesAsync(this.getObjectId(), posX, posY, posZ, this.getHeading());
         l2.gameserver.dao.AsyncCharacterDAO.getInstance().updatePvPPkAsync(this.getObjectId(), this.getPvpKills(), this.getPkKills(), this.getKarma());
         int onlineTime = (int)(this.He > 0L ? (this.Hd + System.currentTimeMillis() - this.He) / 1000L : this.Hd / 1000L);
         l2.gameserver.dao.AsyncCharacterDAO.getInstance().updateOnlineTimeAsync(this.getObjectId(), onlineTime, (int)(System.currentTimeMillis() / 1000L));
         SubClass actClass = this.getActiveClass();
         if (actClass != null) {
            l2.gameserver.dao.AsyncCharacterDAO.getInstance().updateHpMpCpAsync(this.getObjectId(), actClass.getClassId(), this.getCurrentHp(), this.getCurrentMp(), this.getCurrentCp());
            l2.gameserver.dao.AsyncCharacterDAO.getInstance().updateExpSpLevelAsync(this.getObjectId(), actClass.getClassId(), actClass.getExp(), actClass.getSp(), actClass.getLevel());
         }
         return;
      }

      if (this.Jc.tryLock()) {
         try {
            Connection var2 = null;
            PreparedStatement var3 = null;

            try {
               var2 = DatabaseFactory.getInstance().getConnection();
               var3 = var2.prepareStatement(
                  "UPDATE characters SET face=?,hairStyle=?,hairColor=?,x=?,y=?,z=?,karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,rec_bonus_time=?,hunting_bonus_time=?,rec_tick_cnt=?,hunting_bonus=?,clanid=?,deletetime=?,title=?,accesslevel=?,online=?,leaveclan=?,deleteclan=?,nochannel=?,onlinetime=?,pledge_type=?,pledge_rank=?,lvl_joined_academy=?,apprentice=?,key_bindings=?,pcBangPoints=?,char_name=?,bookmarks=? WHERE obj_Id=? LIMIT 1"
               );
               var3.setInt(1, this.getFace());
               var3.setInt(2, this.getHairStyle());
               var3.setInt(3, this.getHairColor());
               if (this._stablePoint == null) {
                  var3.setInt(4, this.getX());
                  var3.setInt(5, this.getY());
                  var3.setInt(6, this.getZ());
               } else {
                  var3.setInt(4, this._stablePoint.x);
                  var3.setInt(5, this._stablePoint.y);
                  var3.setInt(6, this._stablePoint.z);
               }

               var3.setInt(7, this.getKarma());
               var3.setInt(8, this.getPvpKills());
               var3.setInt(9, this.getPkKills());
               var3.setInt(10, this.getReceivedRec());
               var3.setInt(11, this.getGivableRec());
               var3.setInt(12, 0);
               var3.setInt(13, 0);
               var3.setInt(14, 0);
               var3.setInt(15, 0);
               var3.setInt(16, this.getClanId());
               var3.setInt(17, this.getDeleteTimer());
               var3.setString(18, this._title);
               var3.setInt(19, this.Ig);
               var3.setInt(20, this.isOnline() && !this.isInOfflineMode() ? 1 : 0);
               var3.setLong(21, this.getLeaveClanTime() / 1000L);
               var3.setLong(22, this.getDeleteClanTime() / 1000L);
               var3.setLong(23, this.Hh > 0L ? this.getNoChannelRemained() / 1000L : this.Hh);
               var3.setInt(24, (int)(this.He > 0L ? (this.Hd + System.currentTimeMillis() - this.He) / 1000L : this.Hd / 1000L));
               var3.setInt(25, this.getPledgeType());
               var3.setInt(26, this.getPowerGrade());
               var3.setInt(27, this.getLvlJoinedAcademy());
               var3.setInt(28, this.getApprentice());
               var3.setBytes(29, this.getKeyBindings());
               var3.setInt(30, this.getPcBangPoints());
               var3.setString(31, this.getName());
               var3.setInt(32, 0);
               var3.setInt(33, this.getObjectId());
               var3.executeUpdate();
               GameStats.increaseUpdatePlayerBase();
               if (!var1) {
                  EffectsDAO.getInstance().insert(this);
                  CharacterGroupReuseDAO.getInstance().insert(this);
                  this.storeDisableSkills();
                  this.cw();
               }

               this.storeCharSubClasses();
               this.cq();
            } catch (Exception var13) {
               _log.error("Could not store char data: " + this + "!", var13);
            } finally {
               DbUtils.closeQuietly(var2, var3);
            }
         } finally {
            this.Jc.unlock();
         }
      }
   }

   public Skill addSkill(Skill var1, boolean var2) {
      if (var1 == null) {
         return null;
      }

      Skill var3 = super.addSkill(var1);
      if (var1.equals(var3)) {
         return var3;
      }

      if (var2) {
         this.a(var1, var3);
      }

      return var3;
   }

   public Skill removeSkill(Skill var1, boolean var2) {
      return var1 == null ? null : this.removeSkill(var1.getId(), var2);
   }

   public Skill removeSkill(int var1, boolean var2) {
      Skill var3 = super.removeSkillById(var1);
      if (!var2) {
         return var3;
      }

      if (var3 != null) {
         Connection var4 = null;
         PreparedStatement var5 = null;

         try {
            var4 = DatabaseFactory.getInstance().getConnection();
            var5 = var4.prepareStatement("DELETE FROM `character_skills` WHERE `skill_id`=? AND `char_obj_id`=? AND `class_index`=?");
            var5.setInt(1, var3.getId());
            var5.setInt(2, this.getObjectId());
            var5.setInt(3, this.getActiveClassId());
            var5.execute();
         } catch (Exception var10) {
            _log.error("Could not delete skill!", var10);
         } finally {
            DbUtils.closeQuietly(var4, var5);
         }
      }

      return var3;
   }

   private void a(Skill var1, Skill var2) {
      if (var1 == null) {
         _log.warn("could not store new skill. its NULL");
      } else {
         Connection var3 = null;
         PreparedStatement var4 = null;

         try {
            var3 = DatabaseFactory.getInstance().getConnection();
            var4 = var3.prepareStatement("REPLACE INTO `character_skills` (`char_obj_id`,`skill_id`,`skill_level`,`class_index`) values(?,?,?,?)");
            var4.setInt(1, this.getObjectId());
            var4.setInt(2, var1.getId());
            var4.setInt(3, var1.getLevel());
            var4.setInt(4, this.getActiveClassId());
            var4.execute();
         } catch (Exception var9) {
            _log.error("Error could not store skills!", var9);
         } finally {
            DbUtils.closeQuietly(var3, var4);
         }
      }
   }

   private void restoreSkills() {
      this.A(this.getActiveClassId());
   }

   private void A(int var1) {
      Connection var2 = null;
      PreparedStatement var3 = null;
      ResultSet var4 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("SELECT `skill_id`,`skill_level` FROM `character_skills` WHERE `char_obj_id`=? AND `class_index`=?");
         var3.setInt(1, this.getObjectId());
         var3.setInt(2, var1);
         var4 = var3.executeQuery();

         while (var4.next()) {
            int var5 = var4.getInt("skill_id");
            int var6 = var4.getInt("skill_level");
            Skill var7 = SkillTable.getInstance().getInfo(var5, var6);
            if (var7 != null) {
               if (!this.isGM() && !Config.ALT_WEAK_SKILL_LEARN && !SkillAcquireHolder.getInstance().isSkillPossible(this, var7)) {
                  _log.warn("Skill " + var7.toString() + " not possible for player " + this.toString() + " with classId " + this.getActiveClassId());
                  this.removeSkill(var7, true);
                  this.removeSkillFromShortCut(var7.getId());
               } else {
                  super.addSkill(var7);
               }
            }
         }

         if (this.getActiveClassId() == var1) {
            this.updateNobleSkills();
            if (this.IF
               && (this.getBaseClassId() == this.getActiveClassId() || Config.ALT_ALLOW_HERO_SKILLS_ON_SUB_CLASS)
               && (HeroController.getInstance().isCurrentHero(this) || Config.ALT_ALLOW_CUSTOM_HERO_SKILLS)) {
               HeroController.addSkills(this);
            }

            if (this.Ia != null) {
               this.Ia.addSkillsQuietly(this);
               if (this.Ia.getLeaderId() == this.getObjectId() && this.Ia.getLevel() >= Config.MIN_CLAN_LEVEL_FOR_SIEGE_REGISTRATION) {
                  SiegeUtils.addSiegeSkills(this);
               }
            }

            ClassId var15 = null;

            for (ClassId var9 : ClassId.VALUES) {
               if (var9.getId() == this.getActiveClassId()) {
                  var15 = var9;
               }
            }

            switch (var15) {
               case dwarven_fighter:
               case scavenger:
               case bounty_hunter:
               case artisan:
               case warsmith:
               case fortune_seeker:
               case maestro:
                  this.addSkill(SkillTable.getInstance().getInfo(1321, 1));
               default:
                  this.addSkill(SkillTable.getInstance().getInfo(1322, 1));
                  if (Config.UNSTUCK_SKILL && this.getSkillLevel(1050) < 0) {
                     this.addSkill(SkillTable.getInstance().getInfo(2099, 1));
                  }

                  if (Config.BLOCK_BUFF_SKILL) {
                     this.addSkill(SkillTable.getInstance().getInfo(5088, 1));
                  }

                  if (Config.NOBLES_BUFF_SKILL) {
                     this.addSkill(SkillTable.getInstance().getInfo(1323, 1));
                  }

                  for (byte var17 = 0; var17 < Config.ADDITIONALS_SKILLS.length; var17 += 2) {
                     this.addSkill(SkillTable.getInstance().getInfo(Config.ADDITIONALS_SKILLS[var17], Config.ADDITIONALS_SKILLS[var17 + 1]));
                  }

                  return;
            }
         }
      } catch (Exception var13) {
         _log.warn("Could not restore skills for player objId: " + this.getObjectId());
         _log.error("", var13);
         return;
      } finally {
         DbUtils.closeQuietly(var2, var3, var4);
      }
   }

   public void storeDisableSkills() {
      Connection var1 = null;
      PreparedStatement var2 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement(
            "DELETE FROM `character_skills_save` WHERE `char_obj_id` = ? AND (`class_index`=? OR `class_index`=-1) AND `end_time` < ?"
         );
         var2.setInt(1, this.getObjectId());
         var2.setInt(2, this.getActiveClassId());
         var2.setLong(3, System.currentTimeMillis());
         var2.executeUpdate();
         DbUtils.close(var2);
         if (!this._skillReuses.isEmpty()) {
            var2 = var1.prepareStatement(
               "REPLACE INTO `character_skills_save`(`char_obj_id`, `skill_id`, `skill_level`, `class_index`, `end_time`, `reuse_delay_org`) VALUES\t(?,?,?,?,?,?)"
            );
            CHashIntObjectMap var3 = new CHashIntObjectMap();
            synchronized (this._skillReuses) {
               var3.putAll(this._skillReuses);
            }

            for (Object __raw_var5_4363 : (Iterable<?>)(Object)(var3.values())) {
               TimeStamp var5 = (TimeStamp)__raw_var5_4363;
               Skill var6 = SkillTable.getInstance().getInfo(var5.getId(), var5.getLevel());
               if (var6 != null) {
                  var2.setInt(1, this.getObjectId());
                  var2.setInt(2, var6.getId());
                  var2.setInt(3, var6.getLevel());
                  var2.setInt(4, !var6.isSharedClassReuse() ? this.getActiveClassId() : -1);
                  var2.setLong(5, var5.getEndTime());
                  var2.setLong(6, var5.getReuseBasic());
                  var2.executeUpdate();
               }
            }

            return;
         }
      } catch (Exception var12) {
         _log.warn("Could not store disable skills data: " + var12);
         return;
      } finally {
         DbUtils.closeQuietly(var1, var2);
      }
   }

   public void restoreDisableSkills() {
      this._skillReuses.clear();
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement(
            "SELECT `skill_id`, `skill_level`, `end_time`, `reuse_delay_org` FROM `character_skills_save` WHERE `char_obj_id`=? AND (`class_index`=? OR `class_index`=-1)"
         );
         var2.setInt(1, this.getObjectId());
         var2.setInt(2, this.getActiveClassId());
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("skill_id");
            int var5 = var3.getInt("skill_level");
            long var6 = var3.getLong("end_time");
            long var8 = var3.getLong("reuse_delay_org");
            long var10 = System.currentTimeMillis();
            Skill var12 = SkillTable.getInstance().getInfo(var4, var5);
            if (var12 != null && var6 - var10 > 500L) {
               this._skillReuses.put(var12.hashCode(), new TimeStamp(var12, var6, var8));
            }
         }

         DbUtils.close(var2);
         var2 = var1.prepareStatement(
            "DELETE FROM `character_skills_save` WHERE `char_obj_id` = ? AND (`class_index`=? OR `class_index`=-1) AND `end_time` < ?"
         );
         var2.setInt(1, this.getObjectId());
         var2.setInt(2, this.getActiveClassId());
         var2.setLong(3, System.currentTimeMillis());
         var2.executeUpdate();
      } catch (Exception var16) {
         _log.error("Could not restore active skills data!", var16);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public int getHennaEmptySlots() {
      int var1 = 1 + this.getClassId().level();

      for (int var2 = 0; var2 < 3; var2++) {
         if (this.HR[var2] != null) {
            var1--;
         }
      }

      return var1 <= 0 ? 0 : var1;
   }

   public boolean removeHenna(int var1) {
      if (var1 >= 1 && var1 <= 3) {
         if (this.HR[--var1] == null) {
            return false;
         }

         Henna var2 = this.HR[var1];
         int var3 = var2.getDyeId();
         this.HR[var1] = null;
         CharacterHennaDAO.getInstance().delete(this, var1 + 1);
         this.cu();
         this.sendPacket(new HennaInfo(this));
         this.sendUserInfo(true);
         ItemFunctions.addItem(this, var3, var2.getDrawCount() / 2L, true);
         return true;
      } else {
         return false;
      }
   }

   public boolean addHenna(Henna var1) {
      if (this.getHennaEmptySlots() == 0) {
         this.sendPacket(SystemMsg.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
         return false;
      }

      for (int var2 = 0; var2 < 3; var2++) {
         if (this.HR[var2] == null) {
            this.HR[var2] = var1;
            this.cu();
            CharacterHennaDAO.getInstance().insert(this, var2 + 1, var1.getSymbolId());
            this.sendPacket(new HennaInfo(this));
            this.sendUserInfo(true);
            return true;
         }
      }

      return false;
   }

   private void cu() {
      this.HU = 0;
      this.HT = 0;
      this.HY = 0;
      this.HW = 0;
      this.HX = 0;
      this.HV = 0;

      for (int var1 = 0; var1 < 3; var1++) {
         Henna var2 = this.HR[var1];
         if (var2 != null && var2.isForThisClass(this)) {
            this.HU = this.HU + var2.getStatINT();
            this.HT = this.HT + var2.getStatSTR();
            this.HW = this.HW + var2.getStatMEN();
            this.HY = this.HY + var2.getStatCON();
            this.HX = this.HX + var2.getStatWIT();
            this.HV = this.HV + var2.getStatDEX();
         }
      }

      if (this.HU > Config.LIMIT_HENNA_INT) {
         this.HU = Config.LIMIT_HENNA_INT;
      }

      if (this.HT > Config.LIMIT_HENNA_STR) {
         this.HT = Config.LIMIT_HENNA_STR;
      }

      if (this.HW > Config.LIMIT_HENNA_MEN) {
         this.HW = Config.LIMIT_HENNA_MEN;
      }

      if (this.HY > Config.LIMIT_HENNA_CON) {
         this.HY = Config.LIMIT_HENNA_CON;
      }

      if (this.HX > Config.LIMIT_HENNA_WIT) {
         this.HX = Config.LIMIT_HENNA_WIT;
      }

      if (this.HV > Config.LIMIT_HENNA_DEX) {
         this.HV = Config.LIMIT_HENNA_DEX;
      }
   }

   public Henna getHenna(int var1) {
      return var1 >= 1 && var1 <= 3 ? this.HR[var1 - 1] : null;
   }

   public int getHennaStatINT() {
      return this.HU;
   }

   public int getHennaStatSTR() {
      return this.HT;
   }

   public int getHennaStatCON() {
      return this.HY;
   }

   public int getHennaStatMEN() {
      return this.HW;
   }

   public int getHennaStatWIT() {
      return this.HX;
   }

   public int getHennaStatDEX() {
      return this.HV;
   }

   @Override
   public boolean consumeItem(int var1, long var2) {
      if (this.getInventory().destroyItemByItemId(var1, var2)) {
         this.sendPacket(SystemMessage2.removeItems(var1, var2));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean consumeItemMp(int var1, int var2) {
      for (ItemInstance var6 : this.getInventory().getPaperdollItems()) {
         if (var6 != null && var6.getItemId() == var1) {
            int var7 = var6.getDuration() - var2;
            if (var7 >= 0) {
               var6.setDuration(var7);
               this.sendPacket(new InventoryUpdate().addModifiedItem(var6));
               return true;
            }
            break;
         }
      }

      return false;
   }

   @Override
   public boolean isMageClass() {
      ClassId var1 = this.getClassId();
      return var1.isMage();
   }

   public boolean isMounted() {
      return this.JH > 0;
   }

   public final boolean isRiding() {
      return this.In;
   }

   public final void setRiding(boolean var1) {
      this.In = var1;
   }

   public boolean checkLandingState() {
      if (this.isInZone(Zone.ZoneType.no_landing)) {
         return false;
      } else {
         SiegeEvent var1 = this.getEvent(SiegeEvent.class);
         if (var1 != null) {
            Residence var2 = var1.getResidence();
            return var2 != null && this.getClan() != null && this.isClanLeader() && this.getClan().getCastle() == var2.getId();
         } else {
            return true;
         }
      }
   }

   public void setMount(int var1, int var2, int var3) {
      if (!this.isCursedWeaponEquipped()) {
         switch (var1) {
            case 0:
               this.setFlying(false);
               this.setRiding(false);
               if (this.getTransformation() > 0) {
                  this.setTransformation(0);
               }

               this.removeSkillById(4289);
               this.getEffectList().stopEffect(4258);
               break;
            case 12526:
            case 12527:
            case 12528:
            case 16038:
            case 16039:
            case 16040:
            case 16068:
               this.setRiding(true);
               break;
            case 12621:
               this.setFlying(true);
               this.setLoc(this.getLoc().changeZ(32));
               this.addSkill(SkillTable.getInstance().getInfo(4289, 1), false);
               break;
            case 16037:
            case 16041:
            case 16042:
               this.setRiding(true);
         }

         if (var1 > 0) {
            this.unEquipWeapon();
         }

         this.JH = var1;
         this.JI = var2;
         this.JJ = var3;
         this.broadcastUserInfo(true);
         this.broadcastPacket(new Ride(this));
         this.broadcastUserInfo(true);
         this.sendPacket(new SkillList(this));
      }
   }

   public void unEquipWeapon() {
      ItemInstance var1 = this.getSecondaryWeaponInstance();
      if (var1 != null) {
         this.sendDisarmMessage(var1);
         this.getInventory().unEquipItem(var1);
      }

      var1 = this.getActiveWeaponInstance();
      if (var1 != null) {
         this.sendDisarmMessage(var1);
         this.getInventory().unEquipItem(var1);
      }

      this.abortAttack(true, true);
      this.abortCast(true, true);
   }

   @Override
   public int getSpeed(int var1) {
      if (this.isMounted()) {
         PetData var2 = PetDataTable.getInstance().getInfo(this.JH, this.JJ);
         int var3 = 187;
         if (var2 != null) {
            var3 = var2.getSpeed();
         }

         double var4 = 1.0;
         int var6 = this.getLevel();
         if (this.JJ > var6 && var6 - this.JJ > 10) {
            var4 = 0.5;
         }

         var1 = (int)(var4 * var3);
      }

      return super.getSpeed(var1);
   }

   public int getMountNpcId() {
      return this.JH;
   }

   public int getMountObjId() {
      return this.JI;
   }

   public int getMountLevel() {
      return this.JJ;
   }

   public void sendDisarmMessage(ItemInstance var1) {
      if (var1.getEnchantLevel() > 0) {
         SystemMessage var2 = new SystemMessage(1064);
         var2.addNumber(var1.getEnchantLevel());
         var2.addItemName(var1.getItemId());
         this.sendPacket(var2);
      } else {
         SystemMessage var3 = new SystemMessage(417);
         var3.addItemName(var1.getItemId());
         this.sendPacket(var3);
      }
   }

   public void setUsingWarehouseType(Warehouse.WarehouseType var1) {
      this.Iw = var1;
   }

   public Warehouse.WarehouseType getUsingWarehouseType() {
      return this.Iw;
   }

   public Collection<EffectCubic> getCubics() {
      return this.Io == null ? Collections.emptyList() : this.Io.values();
   }

   public void addCubic(EffectCubic var1) {
      if (this.Io == null) {
         this.Io = new ConcurrentHashMap<>(3);
      }

      this.Io.put(var1.getId(), var1);
   }

   public void removeCubic(int var1) {
      if (this.Io != null) {
         this.Io.remove(var1);
      }
   }

   public EffectCubic getCubic(int var1) {
      return this.Io == null ? null : this.Io.get(var1);
   }

   @Override
   public String toString() {
      return this.getName() + "[" + this.getObjectId() + "]";
   }

   public int getEnchantEffect() {
      ItemInstance var1 = this.getActiveWeaponInstance();
      return var1 == null ? 0 : Math.min(127, var1.getEnchantLevel());
   }

   public void setLastNpc(NpcInstance var1) {
      if (var1 == null) {
         this.Iy = HardReferences.emptyRef();
      } else {
         this.Iy = var1.getRef();
      }
   }

   public NpcInstance getLastNpc() {
      return this.Iy.get();
   }

   private String _lastBypassCommand = "";
   private long _lastBypassTime = 0L;
   private int _bypassCountInSecond = 0;
   private long _bypassSecondStartTick = 0L;

   /**
    * By Dhousefe
    * Verifica e atualiza o debounce para requisicoes de bypass com Mechanical Sympathy.
    * 1. Teto global anti-DOS: Se ultrapassar maxPerSec dentro do mesmo segundo, retorna false.
    * 2. Deduplicacao: Se o comando for identico ao anterior dentro da janela windowMs, retorna false (drop silencioso).
    * 3. Se for um comando novo ou fora da janela, atualiza o estado e retorna true.
    */
   public synchronized boolean checkAndSetBypassDebounce(String cmd, long windowMs, int maxPerSec) {
      long now = System.currentTimeMillis();
      if (maxPerSec > 0) {
         if (now - this._bypassSecondStartTick >= 1000L) {
            this._bypassSecondStartTick = now;
            this._bypassCountInSecond = 1;
         } else {
            this._bypassCountInSecond++;
            if (this._bypassCountInSecond > maxPerSec) {
               return false;
            }
         }
      }
      if (windowMs > 0L && cmd != null && cmd.equals(this._lastBypassCommand)) {
         if (now - this._lastBypassTime < windowMs) {
            return false;
         }
      }
      this._lastBypassCommand = (cmd != null) ? cmd : "";
      this._lastBypassTime = now;
      return true;
   }

   public void setMultisell(MultiSellHolder.MultiSellListContainer var1) {
      this.Iz = var1;
   }

   public MultiSellHolder.MultiSellListContainer getMultisell() {
      return this.Iz;
   }

   public boolean isFestivalParticipant() {
      return this.getReflection() instanceof DarknessFestival;
   }

   @Override
   public boolean unChargeShots(boolean var1) {
      ItemInstance var2 = this.getActiveWeaponInstance();
      if (var2 == null) {
         return false;
      }

      if (var1) {
         var2.setChargedSpiritshot(0);
      } else {
         var2.setChargedSoulshot(0);
      }

      this.autoShot();
      return true;
   }

   public boolean unChargeFishShot() {
      ItemInstance var1 = this.getActiveWeaponInstance();
      if (var1 == null) {
         return false;
      }

      var1.setChargedFishshot(false);
      this.autoShot();
      return true;
   }

   public void autoShot() {
      for (Integer var2 : this.IA) {
         ItemInstance var3 = this.getInventory().getItemByItemId(var2);
         if (var3 == null) {
            this.removeAutoSoulShot(var2);
         } else if (var3.getTemplate().testCondition(this, var3, false)) {
            IItemHandler var4 = var3.getTemplate().getHandler();
            if (var4 != null) {
               var4.useItem(this, var3, false);
            }
         }
      }
   }

   public boolean getChargedFishShot() {
      ItemInstance var1 = this.getActiveWeaponInstance();
      return var1 != null && var1.getChargedFishshot();
   }

   @Override
   public boolean getChargedSoulShot() {
      ItemInstance var1 = this.getActiveWeaponInstance();
      return var1 != null && var1.getChargedSoulshot() == 1;
   }

   @Override
   public int getChargedSpiritShot() {
      ItemInstance var1 = this.getActiveWeaponInstance();
      return var1 == null ? 0 : var1.getChargedSpiritshot();
   }

   public void addAutoSoulShot(Integer var1) {
      this.IA.add(var1);
      this.getListeners().onAutoSoulShot(var1, true);
   }

   public void removeAutoSoulShot(Integer var1) {
      this.IA.remove(var1);
      this.getListeners().onAutoSoulShot(var1, false);
   }

   public Set<Integer> getAutoSoulShot() {
      return this.IA;
   }

   public void setInvisibleType(InvisibleType var1) {
      this.Jo = var1;
   }

   @Override
   public InvisibleType getInvisibleType() {
      return this.Jo;
   }

   public int getClanPrivileges() {
      if (this.Ia == null) {
         return 0;
      } else if (this.isClanLeader()) {
         return 8388606;
      } else if (this.Id >= 1 && this.Id <= 9) {
         RankPrivs var1 = this.Ia.getRankPrivs(this.Id);
         return var1 != null ? var1.getPrivs() : 0;
      } else {
         return 0;
      }
   }

   public void teleToClosestTown() {
      this.teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE), ReflectionManager.DEFAULT);
   }

   public void teleToCastle() {
      this.teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CASTLE), ReflectionManager.DEFAULT);
   }

   public void teleToClanhall() {
      this.teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CLANHALL), ReflectionManager.DEFAULT);
   }

   @Override
   public void sendMessage(CustomMessage var1) {
      this.sendMessage(var1.toString());
   }

   @Override
   public void teleToLocation(int var1, int var2, int var3, int var4) {
      if (!this.isDeleted()) {
         super.teleToLocation(var1, var2, var3, var4);
      }
   }

   @Override
   public boolean onTeleported() {
      if (!super.onTeleported()) {
         return false;
      }

      if (this.isFakeDeath()) {
         this.breakFakeDeath();
      }

      if (this.isInBoat()) {
         this.setLoc(this.getBoat().getLoc());
      }

      this.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
      this.spawnMe();
      this.setLastClientPosition(this.getLoc());
      this.setLastServerPosition(this.getLoc());
      if (this.isPendingRevive()) {
         this.doRevive();
      }

      this.sendActionFailed();
      this.getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);
      if (this.isLockedTarget() && this.getTarget() != null) {
         this.sendPacket(new MyTargetSelected(this.getTarget().getObjectId(), 0));
      }

      this.sendUserInfo(true);
      if (this.getPet() != null) {
         this.getPet().teleportToOwner();
      }

      if (Config.ALT_TELEPORT_PROTECTION
         && !this.isInAnyZone(Zone.ZoneType.peace_zone, Zone.ZoneType.SIEGE, Zone.ZoneType.offshore)
         && !this.isOlyParticipant()) {
         this.setAfterTeleportPortectionTime(System.currentTimeMillis() + 1000L * Config.ALT_TELEPORT_PROTECTION_TIME);
         this.sendMessage(new CustomMessage("alt.teleport_protect", this, Config.ALT_TELEPORT_PROTECTION_TIME));
      }

      if (Config.RESEND_MSU_AFTER_TELEPORT > 0) {
         final HardReference var1 = this.getRef();
         ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
               Player var1x = (Player)var1.get();
               if (var1x != null && !var1x.isTeleporting()) {
                  LinkedList var2 = new LinkedList();

                  for (Player var4 : World.getAroundPlayers(var1x)) {
                     if (var4 != null && !var4.isTeleporting() && var4.isCastingNow()) {
                        Creature var5 = var4.getCastingTarget();
                        Skill var6 = var4.getCastingSkill();
                        long var7 = var4.getAnimationEndTime();
                        if (var6 != null && var5 != null && var5.isCreature() && var7 > 0L) {
                           var2.add(new MagicSkillUse(var4, var5, var6, (int)(var7 - System.currentTimeMillis()), 0L));
                        }
                     }
                  }

                  var1x.sendActionFailed();
                  if (!var2.isEmpty()) {
                     var1x.sendPacket(var2);
                  }
               }
            }
         }, Config.RESEND_MSU_AFTER_TELEPORT);
      }

      return true;
   }

   public boolean enterObserverMode(Location var1) {
      WorldRegion var2 = World.getRegion(var1);
      if (var2 == null) {
         return false;
      }

      if (!this.IC.compareAndSet(0, 1)) {
         return false;
      }

      this.setTarget(null);
      this.stopMove();
      this.sitDown(null);
      this.setFlying(true);
      World.removeObjectsFromPlayer(this);
      this.setObserverRegion(var2);
      this.broadcastCharInfo();
      this.sendPacket(new ObserverStart(var1));
      return true;
   }

   public void appearObserverMode() {
      if (this.IC.compareAndSet(1, 3)) {
         WorldRegion var1 = this.getCurrentRegion();
         WorldRegion var2 = this.getObserverRegion();
         if (!var2.equals(var1)) {
            var2.addObject(this);
         }

         World.showObjectsToPlayer(this);
         if (this.isOlyObserver()) {
            for (Player var4 : this.getOlyObservingStadium().getPlayers()) {
               if (var4.isOlyCompetitionStarted()) {
                  this.sendPacket(new ExOlympiadUserInfo(var4));
               }
            }
         }
      }
   }

   public void leaveObserverMode() {
      if (this.IC.compareAndSet(3, 2)) {
         WorldRegion var1 = this.getCurrentRegion();
         WorldRegion var2 = this.getObserverRegion();
         if (!var2.equals(var1)) {
            var2.removeObject(this);
         }

         World.removeObjectsFromPlayer(this);
         this.setObserverRegion(null);
         this.setTarget(null);
         this.stopMove();
         this.sendPacket(new ObserverEnd(this.getLoc()));
      }
   }

   public void returnFromObserverMode() {
      if (this.IC.compareAndSet(2, 0)) {
         this.setLastClientPosition(null);
         this.setLastServerPosition(null);
         this.unblock();
         this.standUp();
         this.setFlying(false);
         this.broadcastCharInfo();
         World.showObjectsToPlayer(this);
      }
   }

   public void enterOlympiadObserverMode(Stadium var1) {
      WorldRegion var2 = World.getRegion(var1.getObservingLoc());
      if (var2 != null && this.Ha == null) {
         if (this.IC.compareAndSet(0, 1)) {
            this.setTarget(null);
            this.setLastNpc(null);
            this.stopMove();
            this.Ha = var1;
            World.removeObjectsFromPlayer(this);
            this.setObserverRegion(var2);
            this.block();
            this.broadcastCharInfo();
            this.setReflection(var1);
            this.setLastClientPosition(null);
            this.setLastServerPosition(null);
            this.sendPacket(new ExOlympiadMode(3), new TeleportToLocation(this, var1.getObservingLoc()));
         }
      }
   }

   public void switchOlympiadObserverArena(Stadium var1) {
      if (this.Ha != null && var1 != this.Ha) {
         WorldRegion var2 = World.getRegion(this.Ha.getObservingLoc());
         if (this.IC.compareAndSet(3, 0)) {
            if (var2 != null) {
               var2.removeObject(this);
               var2.removeFromPlayers(this);
            }

            this.Ha = null;
            World.removeObjectsFromPlayer(this);
            this.sendPacket(new ExOlympiadMode(0));
            this.enterOlympiadObserverMode(var1);
         }
      }
   }

   public void leaveOlympiadObserverMode() {
      if (this.Ha != null) {
         if (this.IC.compareAndSet(3, 2)) {
            WorldRegion var1 = this.getCurrentRegion();
            WorldRegion var2 = this.getObserverRegion();
            if (var2 != null && var1 != null && !var2.equals(var1)) {
               var2.removeObject(this);
            }

            World.removeObjectsFromPlayer(this);
            this.setObserverRegion(null);
            this.Ha = null;
            this.setTarget(null);
            this.stopMove();
            this.sendPacket(new ExOlympiadMode(0));
            this.setReflection(ReflectionManager.DEFAULT);
            this.sendPacket(new TeleportToLocation(this, this.getLoc()));
         }
      }
   }

   public boolean isOlyObserver() {
      return this.Ha != null;
   }

   public Stadium getOlyObservingStadium() {
      return this.Ha;
   }

   @Override
   public boolean isInObserverMode() {
      return this.IC.get() > 0;
   }

   public int getObserverMode() {
      return this.IC.get();
   }

   public Participant getOlyParticipant() {
      return this.Hb;
   }

   public void setOlyParticipant(Participant var1) {
      this.Hb = var1;
   }

   @Override
   public boolean isOlyParticipant() {
      return this.Hb != null;
   }

   public boolean isOlyCompetitionStarted() {
      return this.isOlyParticipant() && this.Hb.getCompetition().getState() == CompetitionState.PLAYING;
   }

   public boolean isOlyCompetitionStandby() {
      return this.isOlyParticipant() && this.Hb.getCompetition().getState() == CompetitionState.STAND_BY;
   }

   public boolean isOlyCompetitionPreparing() {
      return this.isOlyParticipant()
         && (this.Hb.getCompetition().getState() == CompetitionState.INIT || this.Hb.getCompetition().getState() == CompetitionState.STAND_BY);
   }

   public boolean isOlyCompetitionFinished() {
      return this.isOlyParticipant() && this.Hb.getCompetition().getState() == CompetitionState.FINISH;
   }

   public boolean isLooseOlyCompetition() {
      if (this.isOlyParticipant()) {
         return this.isOlyCompetitionFinished() ? !this.Hb.isAlive() : this.Hb.isPlayerLoose(this);
      } else {
         return false;
      }
   }

   public WorldRegion getObserverRegion() {
      return this.IB;
   }

   public void setObserverRegion(WorldRegion var1) {
      this.IB = var1;
   }

   public int getTeleMode() {
      return this._telemode;
   }

   public void setTeleMode(int var1) {
      this._telemode = var1;
   }

   public void setLoto(int var1, int var2) {
      this._loto[var1] = var2;
   }

   public int getLoto(int var1) {
      return this._loto[var1];
   }

   public void setRace(int var1, int var2) {
      this._race[var1] = var2;
   }

   public int getRace(int var1) {
      return this._race[var1];
   }

   public boolean getMessageRefusal() {
      return this.Ii;
   }

   public void setMessageRefusal(boolean var1) {
      this.Ii = var1;
   }

   public void setTradeRefusal(boolean var1) {
      this.Ij = var1;
   }

   public boolean getTradeRefusal() {
      return this.Ij;
   }

   public boolean isPartyRefusal() {
      return this.Ik;
   }

   public void setPartyRefusal(boolean var1) {
      this.Ik = var1;
   }

   public void addToBlockList(String var1) {
      if (var1 != null && !var1.equalsIgnoreCase(this.getName()) && !this.isInBlockList(var1)) {
         Player var2 = World.getPlayer(var1);
         if (var2 == null) {
            int var3 = CharacterDAO.getInstance().getObjectIdByName(var1);
            if (var3 == 0) {
               this.sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
            } else if (Config.gmlist.containsKey(var3) && Config.gmlist.get(var3).IsGM) {
               this.sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
            } else {
               this.ID.put(var3, var1);
               this.sendPacket(new SystemMessage(617).addString(var1));
            }
         } else if (var2.isGM()) {
            this.sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
         } else {
            this.ID.put(var2.getObjectId(), var2.getName());
            this.sendPacket(new SystemMessage(617).addString(var2.getName()));
            var2.sendPacket(new SystemMessage(619).addString(this.getName()));
         }
      } else {
         this.sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
      }
   }

   public void removeFromBlockList(String var1) {
      int var2 = 0;

      for (int var4 : this.ID.keySet()) {
         if (var1.equalsIgnoreCase(this.ID.get(var4))) {
            var2 = var4;
            break;
         }
      }

      if (var2 == 0) {
         this.sendPacket(Msg.YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_FROM_IGNORE_LIST);
      } else {
         this.sendPacket(new SystemMessage(618).addString(this.ID.remove(var2)));
         Player var5 = GameObjectsStorage.getPlayer(var2);
         if (var5 != null) {
            var5.sendMessage(this.getName() + " has removed you from his/her Ignore List.");
         }
      }
   }

   public boolean isInBlockList(Player var1) {
      return this.isInBlockList(var1.getObjectId());
   }

   public boolean isInBlockList(int var1) {
      return this.ID != null && this.ID.containsKey(var1);
   }

   public boolean isInBlockList(String var1) {
      for (int var3 : this.ID.keySet()) {
         if (var1.equalsIgnoreCase(this.ID.get(var3))) {
            return true;
         }
      }

      return false;
   }

   private void cv() {
      this.ID.clear();
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement(
            "SELECT target_Id, char_name FROM character_blocklist LEFT JOIN characters ON ( character_blocklist.target_Id = characters.obj_Id ) WHERE character_blocklist.obj_Id = ?"
         );
         var2.setInt(1, this.getObjectId());
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("target_Id");
            String var5 = var3.getString("char_name");
            if (var5 != null) {
               this.ID.put(var4, var5);
            }
         }
      } catch (SQLException var9) {
         _log.warn("Can't restore player blocklist " + var9, var9);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   private void cw() {
      Connection var1 = null;
      Statement var2 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.createStatement();
         var2.executeUpdate("DELETE FROM character_blocklist WHERE obj_Id=" + this.getObjectId());
         if (!this.ID.isEmpty()) {
            SqlBatch var3 = new SqlBatch("INSERT IGNORE INTO `character_blocklist` (`obj_Id`,`target_Id`) VALUES");
            synchronized (this.ID) {
               for (Entry var7 : this.ID.entrySet()) {
                  StringBuilder var5 = new StringBuilder("(");
                  var5.append(this.getObjectId()).append(",");
                  var5.append(var7.getKey()).append(")");
                  var3.write(var5.toString());
               }
            }

            if (!var3.isEmpty()) {
               var2.executeUpdate(var3.close());
            }

            return;
         }
      } catch (Exception var14) {
         _log.warn("Can't store player blocklist " + var14);
         return;
      } finally {
         DbUtils.closeQuietly(var1, var2);
      }
   }

   public boolean isBlockAll() {
      return this.Il;
   }

   public void setBlockAll(boolean var1) {
      this.Il = var1;
   }

   public Collection<String> getBlockList() {
      return this.ID.values();
   }

   public Map<Integer, String> getBlockListMap() {
      return this.ID;
   }

   public void setHero(boolean var1) {
      this.IF = var1;
   }

   private void cx() {
      if (this.Jb != null) {
         this.Jb.cancel(true);
         this.Jb = null;
      }
   }

   public void setCustomHero(boolean var1, long var2, boolean var4) {
      if (!this.isHero() && var1 && var2 > 0L) {
         this.setVar("CustomHeroEndTime", System.currentTimeMillis() / 1000L + var2, -1L);
         this.setHero(true);
         if (var4) {
            HeroController.addSkills(this, true);
         }

         this.Jb = ThreadPoolManager.getInstance().schedule(new GameObjectTasks.EndCustomHeroTask(this), var2 * 1000L);
      } else if (!var1) {
         this.unsetVar("CustomHeroEndTime");
         this.cx();
         if (HeroController.getInstance().isCurrentHero(this)) {
            return;
         }

         this.setHero(false);
         if (var4) {
            HeroController.removeSkills(this, true);
         }

         HeroController.checkHeroWeaponary(this);
      }
   }

   @Override
   public boolean isHero() {
      return this.IF;
   }

   public void updateNobleSkills() {
      boolean var1 = this.isNoble();
      if (!var1) {
         for (SkillLearn var3 : SkillAcquireHolder.getInstance().getAllSkillLearn(this, this.getClassId(), AcquireType.NOBLES)) {
            Skill var4 = SkillTable.getInstance().getInfo(var3.getId(), var3.getLevel());
            if (var4 != null) {
               this.removeSkill(var4, true);
            }
         }
      } else {
         for (SkillLearn var6 : SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NOBLES)) {
            Skill var7 = SkillTable.getInstance().getInfo(var6.getId(), var6.getLevel());
            if (var7 != null && this.getSkillLevel(var7.getId()) < var7.getLevel()) {
               this.addSkill(var7, true);
            }
         }
      }
   }

   public void setNoble(boolean var1) {
      this.IN = var1;
      if (var1) {
         this.broadcastPacket(new SocialAction(this.getObjectId(), 16));
      }
   }

   public boolean isNoble() {
      return this.IN;
   }

   public int getSubLevel() {
      return this.isSubClassActive() ? this.getLevel() : 0;
   }

   public void updateRam() {
      if (ItemFunctions.getItemCount(this, 7247) > 0L) {
         this.IR = 2;
      } else if (ItemFunctions.getItemCount(this, 7246) > 0L) {
         this.IR = 1;
      } else {
         this.IR = 0;
      }
   }

   public int getRam() {
      return this.IR;
   }

   public void setPledgeType(int var1) {
      this.Ic = var1;
   }

   public int getPledgeType() {
      return this.Ic;
   }

   public void setLvlJoinedAcademy(int var1) {
      this.Ie = var1;
   }

   public int getLvlJoinedAcademy() {
      return this.Ie;
   }

   public int getPledgeClass() {
      return this.Ib;
   }

   public Player.EPledgeRank getPledgeRank() {
      return Player.EPledgeRank.getPledgeRank(this.getPledgeClass());
   }

   public void updatePledgeClass() {
      int var1 = this.Ia == null ? -1 : this.Ia.getLevel();
      boolean var2 = this.Ia != null && Clan.isAcademy(this.Ic);
      boolean var3 = this.Ia != null && Clan.isRoyalGuard(this.Ic);
      boolean var4 = this.Ia != null && Clan.isOrderOfKnights(this.Ic);
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      SubUnit var8 = this.getSubUnit();
      if (var8 != null) {
         UnitMember var9 = var8.getUnitMember(this.getObjectId());
         if (var9 == null) {
            _log.warn("Player: unitMember null, clan: " + this.Ia.getClanId() + "; pledgeType: " + var8.getType());
            return;
         }

         var5 = Clan.isRoyalGuard(var9.getLeaderOf());
         var6 = Clan.isOrderOfKnights(var9.getLeaderOf());
         var7 = var9.getLeaderOf() == 0;
      }

      switch (var1) {
         case -1:
            this.Ib = 0;
            break;
         case 0:
         case 1:
         case 2:
         case 3:
            if (var7) {
               this.Ib = 2;
            } else {
               this.Ib = 1;
            }
            break;
         case 4:
            if (var7) {
               this.Ib = 3;
            } else {
               this.Ib = 2;
            }
            break;
         case 5:
            if (var7) {
               this.Ib = 4;
            } else if (var2) {
               this.Ib = 1;
            } else {
               this.Ib = 2;
            }
            break;
         case 6:
            if (var7) {
               this.Ib = 5;
            } else if (var2) {
               this.Ib = 1;
            } else if (var5) {
               this.Ib = 4;
            } else if (var3) {
               this.Ib = 2;
            } else {
               this.Ib = 3;
            }
            break;
         case 7:
            if (var7) {
               this.Ib = 7;
            } else if (var2) {
               this.Ib = 1;
            } else if (var5) {
               this.Ib = 6;
            } else if (var3) {
               this.Ib = 3;
            } else if (var6) {
               this.Ib = 5;
            } else if (var4) {
               this.Ib = 2;
            } else {
               this.Ib = 4;
            }
            break;
         case 8:
            if (var7) {
               this.Ib = 8;
            } else if (var2) {
               this.Ib = 1;
            } else if (var5) {
               this.Ib = 7;
            } else if (var3) {
               this.Ib = 4;
            } else if (var6) {
               this.Ib = 6;
            } else if (var4) {
               this.Ib = 3;
            } else {
               this.Ib = 5;
            }
      }

      if (this.IF && this.Ib < 8) {
         this.Ib = 8;
      } else if (this.IN && this.Ib < 5) {
         this.Ib = 5;
      }
   }

   public void setPowerGrade(int var1) {
      this.Id = var1;
   }

   public int getPowerGrade() {
      return this.Id;
   }

   public void setApprentice(int var1) {
      this.If = var1;
   }

   public int getApprentice() {
      return this.If;
   }

   public int getSponsor() {
      return this.Ia == null ? 0 : this.Ia.getAnyMember(this.getObjectId()).getSponsor();
   }

   public int getNameColor() {
      return this.isInObserverMode() ? Color.black.getRGB() : this.Hm;
   }

   public void setNameColor(int var1) {
      if (var1 != Config.NORMAL_NAME_COLOUR
         && var1 != Config.CLANLEADER_NAME_COLOUR
         && var1 != Config.GM_NAME_COLOUR
         && var1 != Config.SERVICES_OFFLINE_TRADE_NAME_COLOR) {
         this.setVar("namecolor", Integer.toHexString(var1), -1L);
      } else if (var1 == Config.NORMAL_NAME_COLOUR) {
         this.unsetVar("namecolor");
      }

      this.Hm = var1;
   }

   public void setVar(String var1, String var2, long var3) {
      this.JK.put(var1, var2);
      CharacterVariablesDAO.getInstance().setVar(this.getObjectId(), var1, var2, var3);
   }

   public void setVar(String var1, int var2, long var3) {
      this.setVar(var1, String.valueOf(var2), var3);
   }

   public void setVar(String var1, long var2, long var4) {
      this.setVar(var1, String.valueOf(var2), var4);
   }

   public void unsetVar(String var1) {
      if (var1 != null) {
         if (this.JK.remove(var1) != null) {
            CharacterVariablesDAO.getInstance().deleteVar(this.getObjectId(), var1);
         }
      }
   }

   public String getVar(String var1) {
      return this.JK.getString(var1, null);
   }

   public boolean getVarB(String var1, boolean var2) {
      String var3 = this.JK.getString(var1, null);
      return var3 == null ? var2 : !var3.equals("0") && !var3.equalsIgnoreCase("false");
   }

   public boolean getVarB(String var1) {
      String var2 = this.JK.getString(var1, null);
      return var2 != null && !var2.equals("0") && !var2.equalsIgnoreCase("false");
   }

   public long getVarLong(String var1) {
      return this.getVarLong(var1, 0L);
   }

   public long getVarLong(String var1, long var2) {
      long var4 = var2;
      String var6 = this.getVar(var1);
      if (var6 != null) {
         var4 = Long.parseLong(var6);
      }

      return var4;
   }

   public int getVarInt(String var1) {
      return this.getVarInt(var1, 0);
   }

   public int getVarInt(String var1, int var2) {
      int var3 = var2;
      String var4 = this.getVar(var1);
      if (var4 != null) {
         var3 = Integer.parseInt(var4);
      }

      return var3;
   }

   public MultiValueSet<String> getVars() {
      return this.JK;
   }

   public String getLang() {
      return this.getVar("lang@");
   }

   public String getHWIDLock() {
      return this.getVar("hwidlock@");
   }

   public void setHWIDLock(String var1) {
      if (var1 == null) {
         this.unsetVar("hwidlock@");
      } else {
         this.setVar("hwidlock@", var1, -1L);
      }
   }

   public String getIPLock() {
      return this.getVar("iplock@");
   }

   public void setIPLock(String var1) {
      if (var1 == null) {
         this.unsetVar("iplock@");
      } else {
         this.setVar("iplock@", var1, -1L);
      }
   }

   public int getLangId() {
      String var1 = this.getLang();
      if (var1.equalsIgnoreCase("en") || var1.equalsIgnoreCase("e") || var1.equalsIgnoreCase("eng")) {
         return 0;
      } else {
         return !var1.equalsIgnoreCase("ru") && !var1.equalsIgnoreCase("r") && !var1.equalsIgnoreCase("rus") ? -1 : 1;
      }
   }

   public Language getLanguage() {
      String var1 = this.getLang();
      if (var1 == null || var1.equalsIgnoreCase("en") || var1.equalsIgnoreCase("e") || var1.equalsIgnoreCase("eng")) {
         return Language.ENGLISH;
      } else {
         return !var1.equalsIgnoreCase("ru") && !var1.equalsIgnoreCase("r") && !var1.equalsIgnoreCase("rus") ? Language.ENGLISH : Language.RUSSIAN;
      }
   }

   public boolean isLangRus() {
      return this.getLangId() == 1;
   }

   public int isAtWarWith(Integer var1) {
      return this.Ia != null && this.Ia.isAtWarWith(var1) ? 1 : 0;
   }

   public int isAtWar() {
      return this.Ia != null && this.Ia.isAtWarOrUnderAttack() > 0 ? 1 : 0;
   }

   public void stopWaterTask() {
      if (this.IW != null) {
         this.IW.cancel(false);
         this.IW = null;
         this.sendPacket(new SetupGauge(this, 2, 0));
         this.sendChanges();
      }
   }

   public void startWaterTask() {
      if (this.isDead()) {
         this.stopWaterTask();
      } else if (Config.ALLOW_WATER && this.IW == null) {
         int var1 = (int)(this.calcStat(Stats.BREATH, 86.0, null, null) * 1000.0);
         this.sendPacket(new SetupGauge(this, 2, var1));
         if (this.getTransformation() > 0 && this.getTransformationTemplate() > 0 && !this.isCursedWeaponEquipped()) {
            this.setTransformation(0);
         }

         this.IW = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GameObjectTasks.WaterTask(this), var1, 1000L);
         this.sendChanges();
      }
   }

   public void doRevive(double var1) {
      this.restoreExp(var1);
      this.doRevive();
   }

   @Override
   public void doRevive() {
      super.doRevive();
      this.unsetVar("lostexp");
      this.updateEffectIcons();
      this.autoShot();
   }

   public void reviveRequest(Player var1, double var2, boolean var4, int var5) {
      ReviveAnswerListener var6 = this.Jw != null && this.Jw.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener)this.Jw.getValue() : null;
      if (var6 != null) {
         if (var6.isForPet() == var4 && var6.getPower() >= var2) {
            var1.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
            return;
         }

         if (var4 && !var6.isForPet()) {
            var1.sendPacket(Msg.SINCE_THE_MASTER_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_THE_PET_HAS_BEEN_CANCELLED);
            return;
         }

         if (var4 && this.isDead()) {
            var1.sendPacket(Msg.WHILE_A_PET_IS_ATTEMPTING_TO_RESURRECT_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
            return;
         }
      }

      if (var4 && this.getPet() != null && this.getPet().isDead() || !var4 && this.isDead()) {
         ConfirmDlg var7 = new ConfirmDlg(
            SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, var5
         );
         var7.addName(var1).addString(Math.round(var2) + " percent");
         this.ask(var7, new ReviveAnswerListener(this, var2, var4, var5));
      }
   }

   public void summonCharacterRequest(Creature var1, Location var2, int var3) {
      ConfirmDlg var4 = new ConfirmDlg(SystemMsg.C1_WISHES_TO_SUMMON_YOU_FROM_S2, 60000);
      var4.addName(var1).addZoneName(var2);
      this.ask(var4, new SummonAnswerListener(this, var2, var3, 60000));
   }

   public void scriptRequest(String var1, String var2, Object[] var3) {
      this.ask(new ConfirmDlg(SystemMsg.S1, 30000).addString(var1), new ScriptAnswerListener(this, var2, var3, 30000L));
   }

   public void updateNoChannel(long var1) {
      this.setNoChannel(var1);
      Connection var3 = null;
      PreparedStatement var4 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("UPDATE characters SET nochannel = ? WHERE obj_Id=?");
         var4.setLong(1, this.Hh > 0L ? this.Hh / 1000L : this.Hh);
         var4.setInt(2, this.getObjectId());
         var4.executeUpdate();
      } catch (Exception var9) {
         _log.warn("Could not activate nochannel:" + var9);
      } finally {
         DbUtils.closeQuietly(var3, var4);
      }

      this.sendPacket(new EtcStatusUpdate(this));
   }

   public boolean canTalkWith(Player var1) {
      return this.Hh >= 0L || var1 == this;
   }

   public Deque<ChatMsg> getMessageBucket() {
      return this.HB;
   }

   @Override
   public boolean isInBoat() {
      return this.IG != null;
   }

   public Boat getBoat() {
      return this.IG;
   }

   public void setBoat(Boat var1) {
      this.IG = var1;
   }

   @Override
   protected L2GameServerPacket stopMovePacket() {
      if (this.isInBoat()) {
         this.getBoat().inStopMovePacket(this);
      }

      return super.stopMovePacket();
   }

   public Location getInBoatPosition() {
      return this.IH;
   }

   public void setInBoatPosition(Location var1) {
      this.IH = var1;
   }

   public Map<Integer, SubClass> getSubClasses() {
      return this._classlist;
   }

   public void setBaseClass(int var1) {
      this._baseClass = var1;
   }

   public int getBaseClassId() {
      return this._baseClass;
   }

   public void setActiveClass(SubClass var1) {
      this._activeClass = var1;
   }

   public SubClass getActiveClass() {
      return this._activeClass;
   }

   public int getActiveClassId() {
      return this.getActiveClass().getClassId();
   }

   public synchronized void changeClassInDb(int var1, int var2) {
      Connection var3 = null;
      PreparedStatement var4 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("UPDATE `character_subclasses` SET `class_id`=? WHERE `char_obj_id`=? AND `class_id`=?");
         var4.setInt(1, var2);
         var4.setInt(2, this.getObjectId());
         var4.setInt(3, var1);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("DELETE FROM `character_hennas` WHERE `char_obj_id`=? AND `class_index`=?");
         var4.setInt(1, this.getObjectId());
         var4.setInt(2, var2);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("UPDATE `character_hennas` SET `class_index`=? WHERE `char_obj_id`=? AND `class_index`=?");
         var4.setInt(1, var2);
         var4.setInt(2, this.getObjectId());
         var4.setInt(3, var1);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("DELETE FROM `character_shortcuts` WHERE `object_id`=? AND `class_index`=?");
         var4.setInt(1, this.getObjectId());
         var4.setInt(2, var2);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("UPDATE `character_shortcuts` SET `class_index`=? WHERE `object_id`=? AND `class_index`=?");
         var4.setInt(1, var2);
         var4.setInt(2, this.getObjectId());
         var4.setInt(3, var1);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("DELETE FROM `character_skills` WHERE `char_obj_id`=? AND `class_index`=?");
         var4.setInt(1, this.getObjectId());
         var4.setInt(2, var2);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("UPDATE `character_skills` SET `class_index`=? WHERE `char_obj_id`=? AND `class_index`=?");
         var4.setInt(1, var2);
         var4.setInt(2, this.getObjectId());
         var4.setInt(3, var1);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("DELETE FROM `character_effects_save` WHERE `object_id`=? AND `id`=?");
         var4.setInt(1, this.getObjectId());
         var4.setInt(2, var2);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("UPDATE `character_effects_save` SET `id`=? WHERE `object_id`=? AND `id`=?");
         var4.setInt(1, var2);
         var4.setInt(2, this.getObjectId());
         var4.setInt(3, var1);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("DELETE FROM `character_skills_save` WHERE `char_obj_id`=? AND `class_index`=?");
         var4.setInt(1, this.getObjectId());
         var4.setInt(2, var2);
         var4.executeUpdate();
         DbUtils.close(var4);
         var4 = var3.prepareStatement("UPDATE `character_skills_save` SET `class_index`=? WHERE `char_obj_id`=? AND `class_index`=?");
         var4.setInt(1, var2);
         var4.setInt(2, this.getObjectId());
         var4.setInt(3, var1);
         var4.executeUpdate();
         DbUtils.close(var4);
      } catch (SQLException var9) {
         _log.error("", var9);
      } finally {
         DbUtils.closeQuietly(var3, var4);
      }
   }

   public void storeCharSubClasses() {
      SubClass var1 = this.getActiveClass();
      if (var1 != null) {
         var1.setCp(this.getCurrentCp());
         var1.setHp(this.getCurrentHp());
         var1.setMp(this.getCurrentMp());
         var1.setActive(true);
         this.getSubClasses().put(this.getActiveClassId(), var1);
      } else {
         _log.warn("Could not store char sub data, main class " + this.getActiveClassId() + " not found for " + this);
      }

      Connection var2 = null;
      Statement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.createStatement();

         for (SubClass var6 : this.getSubClasses().values()) {
            StringBuilder var4 = new StringBuilder("UPDATE character_subclasses SET ");
            var4.append("exp=").append(var6.getExp()).append(",");
            var4.append("sp=").append(var6.getSp()).append(",");
            var4.append("curHp=").append(var6.getHp()).append(",");
            var4.append("curMp=").append(var6.getMp()).append(",");
            var4.append("curCp=").append(var6.getCp()).append(",");
            var4.append("level=").append(var6.getLevel()).append(",");
            var4.append("active=").append(var6.isActive() ? 1 : 0).append(",");
            var4.append("isBase=").append(var6.isBase() ? 1 : 0).append(",");
            var4.append("death_penalty=").append(var6.getDeathPenalty(this).getLevelOnSaveDB());
            var4.append(" WHERE char_obj_id=").append(this.getObjectId()).append(" AND class_id=").append(var6.getClassId()).append(" LIMIT 1");
            var3.executeUpdate(var4.toString());
         }

         StringBuilder var12 = new StringBuilder("UPDATE character_subclasses SET ");
         var12.append("maxHp=").append(this.getMaxHp()).append(",");
         var12.append("maxMp=").append(this.getMaxMp()).append(",");
         var12.append("maxCp=").append(this.getMaxCp());
         var12.append(" WHERE char_obj_id=").append(this.getObjectId()).append(" AND active=1 LIMIT 1");
         var3.executeUpdate(var12.toString());
      } catch (Exception var10) {
         _log.warn("Could not store char sub data: " + var10);
         _log.error("", var10);
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }
   }

   public static void restoreCharSubClasses(Player var0) {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement(
            "SELECT `class_id`,`exp`,`sp`,`curHp`,`curCp`,`curMp`,`active`,`isBase`,`death_penalty` FROM `character_subclasses` WHERE `char_obj_id`=?"
         );
         var2.setInt(1, var0.getObjectId());
         var3 = var2.executeQuery();
         SubClass var4 = null;

         while (var3.next()) {
            SubClass var5 = new SubClass();
            var5.setBase(var3.getInt("isBase") != 0);
            var5.setClassId(var3.getInt("class_id"));
            var5.setExp(var3.getLong("exp"));
            var5.setSp(var3.getInt("sp"));
            var5.setHp(var3.getDouble("curHp"));
            var5.setMp(var3.getDouble("curMp"));
            var5.setCp(var3.getDouble("curCp"));
            var5.setDeathPenalty(new DeathPenalty(var0, var3.getInt("death_penalty")));
            boolean var6 = var3.getInt("active") != 0;
            if (var6) {
               var4 = var5;
            }

            var0.getSubClasses().put(var5.getClassId(), var5);
         }

         if (var0.getSubClasses().size() == 0) {
            throw new Exception("There are no one subclass for player: " + var0);
         }

         int var12 = var0.getBaseClassId();
         if (var12 == -1) {
            throw new Exception("There are no base subclass for player: " + var0);
         }

         if (var4 != null) {
            var0.setActiveSubClass(var4.getClassId(), false);
         }

         if (var0.getActiveClass() == null) {
            SubClass var13 = var0.getSubClasses().get(var12);
            var13.setActive(true);
            var0.setActiveSubClass(var13.getClassId(), false);
         }
      } catch (Exception var10) {
         _log.warn("Could not restore char sub-classes: " + var10);
         _log.error("", var10);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public boolean addSubClass(int var1, boolean var2) {
      if (this._classlist.size() >= Config.ALT_GAME_BASE_SUB) {
         return false;
      }

      ClassId var3 = ClassId.VALUES[var1];
      SubClass var4 = new SubClass();
      var4.setBase(false);
      if (var3.getRace() == null) {
         return false;
      }

      var4.setClassId(var1);
      this._classlist.put(var1, var4);
      Connection var5 = null;
      PreparedStatement var6 = null;

      try {
         var5 = DatabaseFactory.getInstance().getConnection();
         var6 = var5.prepareStatement(
            "INSERT INTO  `character_subclasses`  (\t`char_obj_id`,   `class_id`,   `exp`,   `sp`,   `curHp`,   `curMp`,   `curCp`,   `maxHp`,   `maxMp`,   `maxCp`,   `level`,   `active`,   `isBase`,   `death_penalty`)VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
         );
         var6.setInt(1, this.getObjectId());
         var6.setInt(2, var4.getClassId());
         var6.setLong(3, var4.getExp());
         var6.setInt(4, 0);
         var6.setDouble(5, this.getCurrentHp());
         var6.setDouble(6, this.getCurrentMp());
         var6.setDouble(7, this.getCurrentCp());
         var6.setDouble(8, this.getCurrentHp());
         var6.setDouble(9, this.getCurrentMp());
         var6.setDouble(10, this.getCurrentCp());
         var6.setInt(11, var4.getLevel());
         var6.setInt(12, 0);
         var6.setInt(13, 0);
         var6.setInt(14, 0);
         var6.execute();
      } catch (Exception var15) {
         _log.warn("Could not add character sub-class: " + var15, var15);
         return false;
      } finally {
         DbUtils.closeQuietly(var5, var6);
      }

      this.setActiveSubClass(var1, var2);
      boolean var7 = true;
      int var8 = 0;

      for (Collection var9 = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
         var9.size() > var8;
         var9 = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL)
      ) {
         for (Object __raw_var11_6125 : (Iterable<?>)(Object)(var9)) {
            SkillLearn var11 = (SkillLearn)__raw_var11_6125;
            Skill var12 = SkillTable.getInstance().getInfo(var11.getId(), var11.getLevel());
            if (var12 != null && var12.getCanLearn(var3)) {
               this.addSkill(var12, true);
            } else if (var7) {
               var8++;
            }
         }

         var7 = false;
      }

      this.sendPacket(new SkillList(this));
      this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp(), true);
      this.setCurrentCp(this.getMaxCp());
      return true;
   }

   public boolean modifySubClass(int var1, int var2) {
      SubClass var3 = this._classlist.get(var1);
      if (var3 != null && !var3.isBase()) {
         Connection var4 = null;
         PreparedStatement var5 = null;

         try {
            var4 = DatabaseFactory.getInstance().getConnection();
            var5 = var4.prepareStatement("DELETE FROM `character_subclasses` WHERE `char_obj_id`=? AND `class_id`=? AND `isBase` = 0");
            var5.setInt(1, this.getObjectId());
            var5.setInt(2, var1);
            var5.execute();
            DbUtils.close(var5);
            var5 = var4.prepareStatement("DELETE FROM `character_skills` WHERE `char_obj_id`=? AND `class_index`=?");
            var5.setInt(1, this.getObjectId());
            var5.setInt(2, var1);
            var5.execute();
            DbUtils.close(var5);
            var5 = var4.prepareStatement("DELETE FROM `character_skills_save` WHERE `char_obj_id`=? AND `class_index`=?");
            var5.setInt(1, this.getObjectId());
            var5.setInt(2, var1);
            var5.execute();
            DbUtils.close(var5);
            var5 = var4.prepareStatement("DELETE FROM `character_effects_save` WHERE `object_id`=? AND `id`=?");
            var5.setInt(1, this.getObjectId());
            var5.setInt(2, var1);
            var5.execute();
            DbUtils.close(var5);
            var5 = var4.prepareStatement("DELETE FROM `character_hennas` WHERE `char_obj_id`=? AND `class_index`=?");
            var5.setInt(1, this.getObjectId());
            var5.setInt(2, var1);
            var5.execute();
            DbUtils.close(var5);
            var5 = var4.prepareStatement("DELETE FROM `character_shortcuts` WHERE `object_id`=? AND `class_index`=?");
            var5.setInt(1, this.getObjectId());
            var5.setInt(2, var1);
            var5.execute();
            DbUtils.close(var5);
         } catch (Exception var10) {
            _log.warn("Could not delete char sub-class: " + var10);
            _log.error("", var10);
         } finally {
            DbUtils.closeQuietly(var4, var5);
         }

         this._classlist.remove(var1);
         return var2 <= 0 || this.addSubClass(var2, false);
      } else {
         return false;
      }
   }

   public void setActiveSubClass(int var1, boolean var2) {
      SubClass var3 = this.getSubClasses().get(var1);
      if (var3 != null) {
         try {
            if (this.getActiveClass() != null) {
               EffectsDAO.getInstance().insert(this);
               this.storeDisableSkills();
               if (QuestManager.getQuest(422) != null) {
                  String var4 = QuestManager.getQuest(422).getName();
                  if (var4 != null) {
                     QuestState var5 = this.getQuestState(var4);
                     if (var5 != null) {
                        var5.exitCurrentQuest(true);
                     }
                  }
               }
            }
         } catch (Exception var7) {
            _log.warn("", var7);
         }

         SubClass var8 = this.getActiveClass();
         if (var8 != null) {
            var8.setActive(false);
            if (var2) {
               var8.setCp(this.getCurrentCp());
               var8.setHp(this.getCurrentHp());
               var8.setMp(this.getCurrentMp());
               this.getSubClasses().put(this.getActiveClassId(), var8);
            }
         }

         var3.setActive(true);
         this.setActiveClass(var3);
         this.getSubClasses().put(this.getActiveClassId(), var3);
         this.setClassId(var1, false, false);
         this.removeAllSkills();
         this.getEffectList().stopAllEffects();
         if (this.getPet() != null
            && (
               this.getPet().isSummon()
                  || Config.ALT_IMPROVED_PETS_LIMITED_USE
                     && (this.getPet().getNpcId() == 16035 && !this.isMageClass() || this.getPet().getNpcId() == 16034 && this.isMageClass())
            )) {
            this.getPet().unSummon();
         }

         this.setAgathion(0);
         this.restoreSkills();
         if (Config.ALT_SUBLASS_SKILL_TRANSFER && this.getBaseClassId() == var1) {
            for (SubClass var6 : this.getSubClasses().values()) {
               if (var6.getClassId() != var1) {
                  this.A(var6.getClassId());
               }
            }
         }

         this.d(false);
         this.checkSkills();
         this.sendPacket(new ExStorageMaxCount(this));
         this.refreshExpertisePenalty();
         this.sendPacket(new SkillList(this));
         this.getInventory().refreshEquip();
         this.getInventory().validateItems();

         for (int var10 = 0; var10 < 3; var10++) {
            this.HR[var10] = null;
         }

         CharacterHennaDAO.getInstance().select(this);
         this.cu();
         this.sendPacket(new HennaInfo(this));
         EffectsDAO.getInstance().restoreEffects(this);
         this.restoreDisableSkills();
         this.setCurrentHpMp(var3.getHp(), var3.getMp());
         this.setCurrentCp(var3.getCp());
         this.HG.restore();
         this.sendPacket(new ShortCutInit(this));

         for (int var12 : this.getAutoSoulShot()) {
            this.sendPacket(new ExAutoSoulShot(var12, true));
         }

         this.sendPacket(new SkillCoolTime(this));
         this.broadcastPacket(new SocialAction(this.getObjectId(), 15));
         this.getDeathPenalty().restore(this);
         this.setIncreasedForce(0);
         this.broadcastCharInfo();
         this.updateEffectIcons();
         this.updateStats();
      }
   }

   public void startKickTask(long var1) {
      this.stopKickTask();
      this.IY = ThreadPoolManager.getInstance().schedule(new GameObjectTasks.KickTask(this), var1);
   }

   public void stopKickTask() {
      if (this.IY != null) {
         this.IY.cancel(false);
         this.IY = null;
      }
   }

   public void startBonusTask() {
      if (Config.SERVICES_RATE_ENABLED) {
         AccountBonusDAO.getInstance().load(this.getAccountName(), this.getBonus());
         long var1 = this.getBonus().getBonusExpire();
         if (var1 > System.currentTimeMillis() / 1000L) {
            if (this.IK == null) {
               this.IK = LazyPrecisionTaskManager.getInstance().startBonusExpirationTask(this);
            }
         } else if (var1 > 0L) {
            AccountBonusDAO.getInstance().delete(this.getAccountName());
         }
      }
   }

   public void stopBonusTask() {
      if (this.IK != null) {
         this.IK.cancel(false);
         this.IK = null;
      }
   }

   @Override
   public int getInventoryLimit() {
      return (int)this.calcStat(Stats.INVENTORY_LIMIT, 0.0, null, null);
   }

   public int getWarehouseLimit() {
      return (int)this.calcStat(Stats.STORAGE_LIMIT, 0.0, null, null);
   }

   public int getTradeLimit() {
      return (int)this.calcStat(Stats.TRADE_LIMIT, 0.0, null, null);
   }

   public int getDwarvenRecipeLimit() {
      return (int)this.calcStat(Stats.DWARVEN_RECIPE_LIMIT, 50.0, null, null) + Config.ALT_ADD_RECIPES;
   }

   public int getCommonRecipeLimit() {
      return (int)this.calcStat(Stats.COMMON_RECIPE_LIMIT, 50.0, null, null) + Config.ALT_ADD_RECIPES;
   }

   public Element getAttackElement() {
      return Formulas.getAttackElement(this, null);
   }

   public int getAttack(Element var1) {
      return var1 == Element.NONE ? 0 : (int)this.calcStat(var1.getAttack(), 0.0, null, null);
   }

   public int getDefence(Element var1) {
      return var1 == Element.NONE ? 0 : (int)this.calcStat(var1.getDefence(), 0.0, null, null);
   }

   public boolean getAndSetLastItemAuctionRequest() {
      if (this.Ju + 2000L < System.currentTimeMillis()) {
         this.Ju = System.currentTimeMillis();
         return true;
      } else {
         this.Ju = System.currentTimeMillis();
         return false;
      }
   }

   @Override
   public int getNpcId() {
      return -2;
   }

   public GameObject getVisibleObject(int var1) {
      if (this.getObjectId() == var1) {
         return this;
      }

      GameObject var2 = null;
      if (this.getTargetId() == var1) {
         var2 = this.getTarget();
      }

      if (var2 == null && this._party != null) {
         for (Player var4 : this._party.getPartyMembers()) {
            if (var4 != null && var4.getObjectId() == var1) {
               var2 = var4;
               break;
            }
         }
      }

      if (var2 == null) {
         var2 = World.getAroundObjectById(this, var1);
      }

      return var2 != null && !var2.isInvisible() ? var2 : null;
   }

   @Override
   public int getPAtk(Creature var1) {
      double var2 = this.getActiveWeaponInstance() == null ? (this.isMageClass() ? 3 : 4) : 0.0;
      return (int)this.calcStat(Stats.POWER_ATTACK, var2, var1, null);
   }

   @Override
   public int getPDef(Creature var1) {
      double var2 = 4.0;
      ItemInstance var4 = this.getInventory().getPaperdollItem(10);
      if (var4 == null) {
         var2 += this.isMageClass() ? 15.0 : 31.0;
      }

      if (this.getInventory().getPaperdollItem(11) == null && (var4 == null || var4.getBodyPart() != 32768)) {
         var2 += this.isMageClass() ? 8.0 : 18.0;
      }

      if (this.getInventory().getPaperdollItem(6) == null) {
         var2 += 12.0;
      }

      if (this.getInventory().getPaperdollItem(9) == null) {
         var2 += 8.0;
      }

      if (this.getInventory().getPaperdollItem(12) == null) {
         var2 += 7.0;
      }

      return (int)this.calcStat(Stats.POWER_DEFENCE, var2, var1, null);
   }

   @Override
   public int getMDef(Creature var1, Skill var2) {
      double var3 = 0.0;
      if (this.getInventory().getPaperdollItem(2) == null) {
         var3 += 9.0;
      }

      if (this.getInventory().getPaperdollItem(1) == null) {
         var3 += 9.0;
      }

      if (this.getInventory().getPaperdollItem(3) == null) {
         var3 += 13.0;
      }

      if (this.getInventory().getPaperdollItem(5) == null) {
         var3 += 5.0;
      }

      if (this.getInventory().getPaperdollItem(4) == null) {
         var3 += 5.0;
      }

      return (int)this.calcStat(Stats.MAGIC_DEFENCE, var3, var1, var2);
   }

   public boolean isSubClassActive() {
      return this.getBaseClassId() != this.getActiveClassId();
   }

   @Override
   public String getTitle() {
      return super.getTitle();
   }

   public int getTitleColor() {
      return this.Hn;
   }

   public void setTitleColor(int var1) {
      if (var1 != 16777079) {
         this.setVar("titlecolor", Integer.toHexString(var1), -1L);
      } else {
         this.unsetVar("titlecolor");
      }

      this.Hn = var1;
   }

   public String getDisconnectedTitle() {
      return this.Ho;
   }

   public void setDisconnectedTitle(String var1) {
      this.Ho = var1;
   }

   public int getDisconnectedTitleColor() {
      return this.Hp;
   }

   public void setDisconnectedTitleColor(int var1) {
      this.Hp = var1;
   }

   @Override
   public boolean isCursedWeaponEquipped() {
      return this.IT != 0;
   }

   public void setCursedWeaponEquippedId(int var1) {
      this.IT = var1;
   }

   public int getCursedWeaponEquippedId() {
      return this.IT;
   }

   @Override
   public boolean isImmobilized() {
      return super.isImmobilized() || this.isOverloaded() || this.isSitting() || this.isFishing();
   }

   @Override
   public boolean isBlocked() {
      return super.isBlocked() || this.isInMovie() || this.isInObserverMode() || this.isTeleporting() || this.isLogoutStarted();
   }

   @Override
   public boolean isInvul() {
      return super.isInvul()
         || this.isInMovie()
         || this.getAfterTeleportPortectionTime() > System.currentTimeMillis()
         || this.getNoCarrierProtectionTime() > System.currentTimeMillis();
   }

   public boolean isResurectProhibited() {
      return this.JL;
   }

   public void setResurectProhibited(boolean var1) {
      this.JL = var1;
   }

   public void setOverloaded(boolean var1) {
      this.Hq = var1;
   }

   public boolean isOverloaded() {
      return this.Hq;
   }

   public boolean isFishing() {
      return this.IV;
   }

   public Fishing getFishing() {
      return this.IU;
   }

   public void setFishing(boolean var1) {
      this.IV = var1;
   }

   public void startFishing(FishTemplate var1, int var2) {
      this.IU.setFish(var1);
      this.IU.setLureId(var2);
      this.IU.startFishing();
   }

   public void stopFishing() {
      this.IU.stopFishing();
   }

   public Location getFishLoc() {
      return this.IU.getFishLoc();
   }

   public Bonus getBonus() {
      return this.IJ;
   }

   public boolean hasBonus() {
      return !this.IJ.isExpired();
   }

   @Override
   public double getRateAdena() {
      return this.calcStat(Stats.ADENA_REWARD_MULTIPLIER, this._party == null ? this.IJ.getDropAdena() : this._party._rateAdena);
   }

   @Override
   public double getRateItems() {
      return this.calcStat(Stats.ITEM_REWARD_MULTIPLIER, this._party == null ? this.IJ.getDropItems() : this._party._rateDrop);
   }

   @Override
   public double getRateExp() {
      return this.calcStat(Stats.EXP, this._party == null ? this.IJ.getRateXp() : this._party._rateExp, null, null);
   }

   @Override
   public double getRateSp() {
      return this.calcStat(Stats.SP, this._party == null ? this.IJ.getRateSp() : this._party._rateSp, null, null);
   }

   @Override
   public double getRateSpoil() {
      return this.calcStat(Stats.SPOIL_REWARD_MULTIPLIER, this._party == null ? this.IJ.getDropSpoil() : this._party._rateSpoil, null, null);
   }

   public double getRaidRateExp() {
      return this.calcStat(Stats.RAID_EXP, this._party == null ? this.IJ.getRateRaidXp() : this._party._rateRaidExp, null, null);
   }

   public double getRaidRateSp() {
      return this.calcStat(Stats.RAID_SP, this._party == null ? this.IJ.getRateRaidSp() : this._party._rateRaidSp, null, null);
   }

   public double getEnchantBonusMul() {
      return this.calcStat(Stats.ENCHANT_BONUS_MULTIPLIER, this.IJ.getEnchantItemMul());
   }

   public boolean isMaried() {
      return this.JM;
   }

   public void setMaried(boolean var1) {
      this.JM = var1;
   }

   public void setMaryRequest(boolean var1) {
      this.JP = var1;
   }

   public boolean isMaryRequest() {
      return this.JP;
   }

   public void setMaryAccepted(boolean var1) {
      this.JQ = var1;
   }

   public boolean isMaryAccepted() {
      return this.JQ;
   }

   public int getPartnerId() {
      return this.JN;
   }

   public void setPartnerId(int var1) {
      this.JN = var1;
   }

   public int getCoupleId() {
      return this.JO;
   }

   public void setCoupleId(int var1) {
      this.JO = var1;
   }

   public void setUndying(boolean var1) {
      if (this.isGM()) {
         this.GZ = var1;
      }
   }

   public boolean isUndying() {
      return this.GZ;
   }

   public void resetReuse() {
      this._skillReuses.clear();
      this.Jv.clear();
   }

   public DeathPenalty getDeathPenalty() {
      return this._activeClass == null ? null : this._activeClass.getDeathPenalty(this);
   }

   public boolean isCharmOfCourage() {
      return this.JR;
   }

   public void setCharmOfCourage(boolean var1) {
      this.JR = var1;
      if (!var1) {
         this.getEffectList().stopEffect(5041);
      }

      this.sendEtcStatusUpdate();
   }

   @Override
   public int getIncreasedForce() {
      return this.JS;
   }

   @Override
   public void setIncreasedForce(int var1) {
      if (this.JS != var1) {
         var1 = Math.min(var1, 7);
         var1 = Math.max(var1, 0);
         if (var1 != 0 && var1 > this.JS) {
            this.JT = System.currentTimeMillis();
            if (this.JU == null) {
               this.JU = ThreadPoolManager.getInstance().schedule(new Player.ForceCleanupTask(), 600000L);
            }

            this.sendPacket(new SystemMessage(323).addNumber(var1));
         }

         this.JS = var1;
         this.sendEtcStatusUpdate();
      }
   }

   public boolean isFalling() {
      return System.currentTimeMillis() - this.JV < 5000L;
   }

   public void falling(int var1) {
      if (Config.DAMAGE_FROM_FALLING && !this.isDead() && !this.isFlying() && !this.isInWater() && !this.isInBoat()) {
         this.JV = System.currentTimeMillis();
         int var2 = (int)this.calcStat(Stats.FALL, this.getMaxHp() / 2000.0 * var1, null, null);
         if (var2 > 0) {
            int var3 = (int)this.getCurrentHp();
            if (var3 - var2 < 1) {
               this.setCurrentHp(1.0, false);
            } else {
               this.setCurrentHp(var3 - var2, false);
            }

            this.sendPacket(new SystemMessage(296).addNumber(var2));
         }
      }
   }

   @Override
   public void checkHpMessages(double var1, double var3) {
      int[] var5 = new int[]{30, 30};
      int[] var6 = new int[]{290, 291};
      double var7 = this.getMaxHp() / 100;
      double var9 = var1 / var7;
      double var11 = var3 / var7;
      boolean var13 = false;

      for (int var14 = 0; var14 < var6.length; var14++) {
         int var15 = this.getSkillLevel(var6[var14]);
         if (var15 > 0) {
            if (var9 > var5[var14] && var11 <= var5[var14]) {
               this.sendPacket(new SystemMessage(1133).addSkillName(var6[var14], var15));
               var13 = true;
            } else if (var9 <= var5[var14] && var11 > var5[var14]) {
               this.sendPacket(new SystemMessage(1134).addSkillName(var6[var14], var15));
               var13 = true;
            }
         }
      }

      if (var13) {
         this.sendChanges();
      }
   }

   public void checkDayNightMessages() {
      int var1 = this.getSkillLevel(294);
      if (var1 > 0) {
         if (GameTimeController.getInstance().isNowNight()) {
            this.sendPacket(new SystemMessage(1131).addSkillName(294, var1));
         } else {
            this.sendPacket(new SystemMessage(1132).addSkillName(294, var1));
         }
      }

      this.sendChanges();
   }

   public int getZoneMask() {
      return this.Jd;
   }

   @Override
   protected void onUpdateZones(List<Zone> var1, List<Zone> var2) {
      super.onUpdateZones(var1, var2);
      if (!var1.isEmpty() || !var2.isEmpty()) {
         boolean var3 = (this.Jd & 16384) == 16384;
         boolean var4 = (this.Jd & 256) == 256;
         boolean var5 = (this.Jd & 2048) == 2048;
         boolean var6 = (this.Jd & 4096) == 4096;
         boolean var7 = this.isInCombatZone();
         boolean var8 = this.isInDangerArea();
         boolean var9 = this.isInZone(Zone.ZoneType.fun);
         boolean var10 = this.isOnSiegeField() || var9;
         boolean var11 = this.isInPeaceZone();
         boolean var12 = this.isInSSQZone();
         int var13 = this.Jd;
         this.Jd = 0;
         if (var7) {
            this.Jd |= 16384;
         }

         if (var8) {
            this.Jd |= 256;
         }

         if (var10) {
            this.Jd |= 2048;
         }

         if (var11) {
            this.Jd |= 4096;
         }

         if (var12) {
            this.Jd |= 8192;
         }

         if (var13 != this.Jd) {
            this.sendPacket(new ExSetCompassZoneCode(this));
         }

         if (var3 != var7) {
            this.broadcastRelationChanged();
         }

         if (var4 != var8) {
            this.sendPacket(new EtcStatusUpdate(this));
         }

         if (var5 != var10) {
            this.broadcastRelationChanged();
            if (var10) {
               this.sendPacket(Msg.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
               if (Config.FUN_ZONE_FLAG_ON_ENTER && var9 && !this.isTeleporting() && this.getPvpFlag() == 0) {
                  this.startPvPFlag(null);
               }
            } else {
               this.sendPacket(Msg.YOU_HAVE_LEFT_A_COMBAT_ZONE);
               if (!this.isTeleporting() && this.getPvpFlag() == 0) {
                  this.startPvPFlag(null);
               }
            }
         }

         if (var11 && !var6) {
            FlagItemAttachment var14 = this.getActiveWeaponFlagAttachment();
            if (var14 != null) {
               var14.onEnterPeace(this);
            }
         }

         if (this.isInWater()) {
            this.startWaterTask();
         } else {
            this.stopWaterTask();
         }
      }
   }

   public void startAutoSaveTask() {
      if (Config.AUTOSAVE) {
         if (this.IX == null) {
            this.IX = AutoSaveManager.getInstance().addAutoSaveTask(this);
         }
      }
   }

   public void stopAutoSaveTask() {
      if (this.IX != null) {
         this.IX.cancel(false);
      }

      this.IX = null;
   }

   public void startPcBangPointsTask() {
      if (Config.ALT_PCBANG_POINTS_ENABLED && Config.ALT_PCBANG_POINTS_DELAY > 0) {
         if (Config.ALT_PCBANG_CHECK_HWID) {
            GameClient var1 = this.getNetConnection();
            String var2;
            if (var1 != null && !StringUtils.isBlank(var2 = var1.getHwid())) {
               for (Player var4 : GameObjectsStorage.getAllPlayersForIterate()) {
                  if (var4 != null && var4 != this && var4.isOnline()) {
                     GameClient var5 = var4.getNetConnection();
                     if (var5 != null && StringUtils.equalsIgnoreCase(var2, var5.getHwid())) {
                        return;
                     }
                  }
               }
            }
         }

         if (this.IZ == null) {
            this.IZ = LazyPrecisionTaskManager.getInstance().addPCCafePointsTask(this);
         }
      }
   }

   public void stopPcBangPointsTask() {
      if (this.IZ != null) {
         this.IZ.cancel(false);
      }

      this.IZ = null;
   }

   public void startUnjailTask(Player var1, int var2) {
      if (this.Ja != null) {
         this.Ja.cancel(false);
      }

      this.Ja = ThreadPoolManager.getInstance().schedule(new GameObjectTasks.UnJailTask(var1), var2);
   }

   public void stopUnjailTask() {
      if (this.Ja != null) {
         this.Ja.cancel(false);
      }

      this.Ja = null;
   }

   @Override
   public void sendMessage(String var1) {
      this.sendPacket(new SystemMessage(var1));
   }

   public void setLastClientPosition(Location var1) {
      this.JW = var1;
   }

   public Location getLastClientPosition() {
      return this.JW;
   }

   public void setLastServerPosition(Location var1) {
      this.JX = var1;
   }

   public Location getLastServerPosition() {
      return this.JX;
   }

   public void setUseSeed(int var1) {
      this.JY = var1;
   }

   public int getUseSeed() {
      return this.JY;
   }

   public int getRelation(Player var1) {
      int var2 = 0;
      if (this.getClan() != null) {
         var2 |= 64;
      }

      if (this.isClanLeader()) {
         var2 |= 128;
      }

      Party var3 = this.getParty();
      if (var3 != null && var3 == var1.getParty()) {
         var2 |= 32;
         switch (var3.getPartyMembers().indexOf(this)) {
            case 0:
               var2 |= 16;
               break;
            case 1:
               var2 |= 8;
               break;
            case 2:
               var2 |= 7;
               break;
            case 3:
               var2 |= 6;
               break;
            case 4:
               var2 |= 5;
               break;
            case 5:
               var2 |= 4;
               break;
            case 6:
               var2 |= 3;
               break;
            case 7:
               var2 |= 2;
               break;
            case 8:
               var2 |= 1;
         }
      }

      Clan var4 = this.getClan();
      Clan var5 = var1.getClan();
      if (var4 != null && var5 != null && var1.getPledgeType() != -1 && this.getPledgeType() != -1 && var5.isAtWarWith(var4.getClanId())) {
         var2 |= 65536;
         if (var4.isAtWarWith(var5.getClanId())) {
            var2 |= 32768;
         }
      }

      for (GlobalEvent var7 : this.getEvents()) {
         var2 = var7.getRelation(this, var1, var2);
      }

      return var2;
   }

   public long getlastPvpAttack() {
      return this.Ka;
   }

   @Override
   public void startPvPFlag(Creature var1) {
      if (this.De <= 0) {
         long var2 = System.currentTimeMillis();
         if (var1 != null && var1.getPvpFlag() != 0) {
            var2 -= Math.max(0, Config.PVP_TIME - Config.PVP_FLAG_ON_UN_FLAG_TIME);
         }

         if (this._pvpFlag == 0 || this.Ka <= var2) {
            if (this.getFarmSystem().isAutofarming()) {
               this.getFarmSystem().stopFarmTask(false);
            }

            this.Ka = var2;
            this.updatePvPFlag(1);
            if (this.JZ == null) {
               this.JZ = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GameObjectTasks.PvPFlagTask(this), 1000L, 1000L);
            }
         }
      }
   }

   public void stopPvPFlag() {
      if (this.JZ != null) {
         this.JZ.cancel(false);
         this.JZ = null;
      }

      this.updatePvPFlag(0);
   }

   public void updatePvPFlag(int var1) {
      if (this._pvpFlag != var1) {
         this.setPvpFlag(var1);
         this.sendStatusUpdate(true, true, 26);
         this.broadcastRelationChanged();
      }
   }

   public void setPvpFlag(int var1) {
      this._pvpFlag = var1;
   }

   @Override
   public int getPvpFlag() {
      return this._pvpFlag;
   }

   public boolean isInDuel() {
      return this.getEvent(DuelEvent.class) != null;
   }

   public TamedBeastInstance getTrainedBeast() {
      return this.Kb;
   }

   public void setTrainedBeast(TamedBeastInstance var1) {
      this.Kb = var1;
   }

   public long getLastAttackPacket() {
      return this.Kc;
   }

   public void setLastAttackPacket() {
      this.Kc = System.currentTimeMillis();
   }

   public SkillFloodTracker getSkillFloodTracker() {
      return this._skillFloodTracker;
   }

   public synchronized NpcSpawnPacer getNpcSpawnPacer() {
      if (this._npcSpawnPacer == null) {
         this._npcSpawnPacer = new NpcSpawnPacer(this);
      }
      return this._npcSpawnPacer;
   }

   public synchronized void clearNpcSpawnPacer() {
      if (this._npcSpawnPacer != null) {
         this._npcSpawnPacer.clear();
         this._npcSpawnPacer = null;
      }
   }

   public byte[] getKeyBindings() {
      return this.IS;
   }

   public void setKeyBindings(byte[] var1) {
      if (var1 == null) {
         var1 = ArrayUtils.EMPTY_BYTE_ARRAY;
      }

      this.IS = var1;
   }

   public void setTransformation(int var1) {
      if (var1 != this.Jf && (this.Jf == 0 || var1 == 0)) {
         if (var1 == 0) {
            for (Effect var3 : this.getEffectList().getAllEffects()) {
               if (var3 != null && var3.getEffectType() == EffectType.Transformation && var3.calc() != 0.0) {
                  var3.exit();
                  this.b(var3.getSkill());
                  break;
               }
            }

            if (!this._transformationSkills.isEmpty()) {
               for (Skill var15 : this._transformationSkills.values()) {
                  if (!var15.isCommon() && !SkillAcquireHolder.getInstance().isSkillPossible(this, var15) && !var15.isHeroic()) {
                     super.removeSkill(var15);
                  }
               }

               this._transformationSkills.clear();
            }
         } else {
            if (this.isCursedWeaponEquipped()) {
               this.b(null);
            } else {
               for (Effect var16 : this.getEffectList().getAllEffects()) {
                  if (var16 != null && var16.getEffectType() == EffectType.Transformation) {
                     if (var16.getSkill() instanceof Transformation && ((Transformation)var16.getSkill()).isDisguise) {
                        for (Skill var20 : this.getAllSkills()) {
                           if (var20 != null && (var20.isActive() || var20.isToggle())) {
                              this._transformationSkills.put(var20.getId(), var20);
                           }
                        }
                     } else {
                        for (Skill.AddedSkill var7 : var16.getSkill().getAddedSkills()) {
                           if (var7.level == 0) {
                              int var8 = this.getSkillLevel(var7.id);
                              if (var8 > 0) {
                                 this._transformationSkills.put(var7.id, SkillTable.getInstance().getInfo(var7.id, var8));
                              }
                           } else if (var7.level == -2) {
                              int var21 = Math.max(var16.getSkill().getMagicLevel(), 40);
                              int var9 = SkillTable.getInstance().getBaseLevel(var7.id);
                              int var10 = 1;
                              if (var9 > 3) {
                                 var10 += this.getLevel() - var21;
                              } else {
                                 var10 += (this.getLevel() - var21) / ((76 - var21) / var9);
                              }

                              var10 = Math.min(Math.max(var10, 1), var9);
                              this._transformationSkills.put(var7.id, SkillTable.getInstance().getInfo(var7.id, var10));
                           } else {
                              this._transformationSkills.put(var7.id, var7.getSkill());
                           }
                        }
                     }

                     this.b(var16.getSkill());
                     break;
                  }
               }
            }

            if (!this.isOlyParticipant() && this.isCursedWeaponEquipped() && this.IF && this.getBaseClassId() == this.getActiveClassId()) {
               this._transformationSkills.put(395, SkillTable.getInstance().getInfo(395, 1));
               this._transformationSkills.put(396, SkillTable.getInstance().getInfo(396, 1));
               this._transformationSkills.put(1374, SkillTable.getInstance().getInfo(1374, 1));
               this._transformationSkills.put(1375, SkillTable.getInstance().getInfo(1375, 1));
               this._transformationSkills.put(1376, SkillTable.getInstance().getInfo(1376, 1));
            }

            for (Skill var17 : this._transformationSkills.values()) {
               this.addSkill(var17, false);
            }
         }

         this.Jf = var1;
         this.sendPacket(new ExBasicActionList(this));
         this.sendPacket(new SkillList(this));
         this.sendPacket(new ShortCutInit(this));

         for (int var18 : this.getAutoSoulShot()) {
            this.sendPacket(new ExAutoSoulShot(var18, true));
         }

         this.broadcastUserInfo(true);
      }
   }

   private void b(Skill var1) {
      if (var1 == null || !var1.isBaseTransformation()) {
         for (Effect var3 : this.getEffectList().getAllEffects()) {
            if (var3 != null && var3.getSkill().isToggle()) {
               var3.exit();
            }
         }
      }
   }

   public boolean isInFlyingTransform() {
      return this.Jf == 8 || this.Jf == 9 || this.Jf == 260;
   }

   public boolean isInMountTransform() {
      return this.Jf == 106 || this.Jf == 109 || this.Jf == 110 || this.Jf == 20001;
   }

   public int getTransformation() {
      return this.Jf;
   }

   public String getTransformationName() {
      return this.ED;
   }

   public void setTransformationName(String var1) {
      this.ED = var1;
   }

   public String getTransformationTitle() {
      return this.Jh;
   }

   public void setTransformationTitle(String var1) {
      this.Jh = var1;
   }

   public void setTransformationTemplate(int var1) {
      this.Jg = var1;
   }

   public int getTransformationTemplate() {
      return this.Jg;
   }

   @Override
   public final Collection<Skill> getAllSkills() {
      if (this.Jf == 0 && !this.isCursedWeaponEquipped()) {
         return super.getAllSkills();
      }

      HashMap var1 = new HashMap();

      for (Skill var3 : super.getAllSkills()) {
         if (var3 != null && !var3.isActive() && !var3.isToggle()) {
            var1.put(var3.getId(), var3);
         }
      }

      var1.putAll(this._transformationSkills);
      return var1.values();
   }

   public void setAgathion(int var1) {
      if (this.Ip != var1) {
         this.Ip = var1;
         this.broadcastCharInfo();
      }
   }

   public int getAgathionId() {
      return this.Ip;
   }

   public int getPcBangPoints() {
      return this.Ji;
   }

   public void setPcBangPoints(int var1) {
      this.Ji = var1;
   }

   public void addPcBangPoints(int var1, boolean var2) {
      if (var2) {
         var1 = (int)(var1 * Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_RATE);
      }

      if (!this.getBonus().isExpired()) {
         var1 = (int)(var1 * Config.ALT_PCBANG_BONUS_WITH_PREMIUM_RATE);
      }

      this.Ji += var1;
      this.sendPacket(new SystemMessage(var2 ? 1708 : 1707).addNumber(var1));
      this.sendPacket(new ExPCCafePointInfo(this, var1, 1, 2, 12));
   }

   public boolean reducePcBangPoints(int var1) {
      if (this.Ji < var1) {
         return false;
      }

      this.Ji -= var1;
      this.sendPacket(new SystemMessage(1709).addNumber(var1));
      this.sendPacket(new ExPCCafePointInfo(this, 0, 1, 2, 12));
      return true;
   }

   public void setGroundSkillLoc(Location var1) {
      this.Kd = var1;
   }

   public Location getGroundSkillLoc() {
      return this.Kd;
   }

   public boolean isLogoutStarted() {
      return this.Ix.get();
   }

   public void setOfflineMode(boolean var1) {
      if (!var1) {
         this.unsetVar("offline");
      }

      this.Je = var1;
   }

   public boolean isInOfflineMode() {
      return this.Je;
   }

   public void saveTradeList() {
      String var1 = "";
      if (this.HM != null && !this.HM.isEmpty()) {
         for (TradeItem var3 : this.HM) {
            var1 = var1 + var3.getObjectId() + ";" + var3.getCount() + ";" + var3.getOwnersPrice() + ":";
         }

         this.setVar("selllist", var1, -1L);
         var1 = "";
         if (this.HQ != null && this.getSellStoreName() != null) {
            this.setVar("sellstorename", this.getSellStoreName(), -1L);
         }
      } else {
         this.unsetVar("selllist");
      }

      if (this.HN != null && !this.HN.isEmpty()) {
         for (TradeItem var7 : this.HN) {
            var1 = var1 + var7.getObjectId() + ";" + var7.getCount() + ";" + var7.getOwnersPrice() + ":";
         }

         this.setVar("packageselllist", var1, -1L);
         var1 = "";
         if (this.HQ != null && this.getSellStoreName() != null) {
            this.setVar("sellstorename", this.getSellStoreName(), -1L);
         }
      } else {
         this.unsetVar("packageselllist");
      }

      if (this.HP != null && !this.HP.isEmpty()) {
         for (TradeItem var8 : this.HP) {
            var1 = var1 + var8.getItemId() + ";" + var8.getCount() + ";" + var8.getOwnersPrice() + ";" + var8.getEnchantLevel() + ":";
         }

         this.setVar("buylist", var1, -1L);
         var1 = "";
         if (this.HQ != null && this.getBuyStoreName() != null) {
            this.setVar("buystorename", this.getBuyStoreName(), -1L);
         }
      } else {
         this.unsetVar("buylist");
      }

      if (this.HK != null && !this.HK.isEmpty()) {
         for (ManufactureItem var9 : this.HK) {
            var1 = var1 + var9.getRecipeId() + ";" + var9.getCost() + ":";
         }

         this.setVar("createlist", var1, -1L);
         if (this.getManufactureName() != null) {
            this.setVar("manufacturename", this.getManufactureName(), -1L);
         }
      } else {
         this.unsetVar("createlist");
      }
   }

   public void restoreTradeList() {
      String var1 = this.getVar("selllist");
      if (var1 != null) {
         this.HM = new CopyOnWriteArrayList<>();
         String[] var2 = var1.split(":");

         for (String var6 : var2) {
            if (!var6.equals("")) {
               String[] var7 = var6.split(";");
               if (var7.length >= 3) {
                  int var8 = Integer.parseInt(var7[0]);
                  long var9 = Long.parseLong(var7[1]);
                  long var11 = Long.parseLong(var7[2]);
                  ItemInstance var13 = this.getInventory().getItemByObjectId(var8);
                  if (var9 >= 1L && var13 != null) {
                     if (var9 > var13.getCount()) {
                        var9 = var13.getCount();
                     }

                     TradeItem var14 = new TradeItem(var13);
                     var14.setCount(var9);
                     var14.setOwnersPrice(var11);
                     this.HM.add(var14);
                  }
               }
            }
         }

         var1 = this.getVar("sellstorename");
         if (var1 != null) {
            this.setSellStoreName(var1);
         }
      }

      var1 = this.getVar("packageselllist");
      if (var1 != null) {
         this.HN = new CopyOnWriteArrayList<>();
         String[] var22 = var1.split(":");

         for (String var34 : var22) {
            if (!var34.equals("")) {
               String[] var37 = var34.split(";");
               if (var37.length >= 3) {
                  int var40 = Integer.parseInt(var37[0]);
                  long var43 = Long.parseLong(var37[1]);
                  long var45 = Long.parseLong(var37[2]);
                  ItemInstance var46 = this.getInventory().getItemByObjectId(var40);
                  if (var43 >= 1L && var46 != null) {
                     if (var43 > var46.getCount()) {
                        var43 = var46.getCount();
                     }

                     TradeItem var47 = new TradeItem(var46);
                     var47.setCount(var43);
                     var47.setOwnersPrice(var45);
                     this.HN.add(var47);
                  }
               }
            }
         }

         var1 = this.getVar("sellstorename");
         if (var1 != null) {
            this.setSellStoreName(var1);
         }
      }

      var1 = this.getVar("buylist");
      if (var1 != null) {
         this.HP = new CopyOnWriteArrayList<>();
         String[] var23 = var1.split(":");

         for (String var35 : var23) {
            if (!var35.equals("")) {
               String[] var38 = var35.split(";");
               if (var38.length >= 3) {
                  TradeItem var41 = new TradeItem();
                  var41.setItemId(Integer.parseInt(var38[0]));
                  var41.setCount(Long.parseLong(var38[1]));
                  var41.setOwnersPrice(Long.parseLong(var38[2]));
                  if (var38.length >= 4) {
                     var41.setEnchantLevel(Integer.parseInt(var38[3]));
                  }

                  this.HP.add(var41);
               }
            }
         }

         var1 = this.getVar("buystorename");
         if (var1 != null) {
            this.setBuyStoreName(var1);
         }
      }

      var1 = this.getVar("createlist");
      if (var1 != null) {
         this.HK = new CopyOnWriteArrayList<>();
         String[] var24 = var1.split(":");

         for (String var36 : var24) {
            if (!var36.equals("")) {
               String[] var39 = var36.split(";");
               if (var39.length >= 2) {
                  int var42 = Integer.parseInt(var39[0]);
                  long var44 = Long.parseLong(var39[1]);
                  if (this.findRecipe(var42)) {
                     this.HK.add(new ManufactureItem(var42, var44));
                  }
               }
            }
         }

         var1 = this.getVar("manufacturename");
         if (var1 != null) {
            this.setManufactureName(var1);
         }
      }
   }

   public void restoreRecipeBook() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT `id` FROM `character_recipebook` WHERE `char_id`=?");
         var2.setInt(1, this.getObjectId());
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("id");
            Recipe var5 = RecipeHolder.getInstance().getRecipeById(var4);
            this.registerRecipe(var5, false);
         }
      } catch (Exception var9) {
         _log.warn("count not recipe skills:" + var9);
         _log.error("", var9);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public int getMountType() {
      switch (this.getMountNpcId()) {
         case 12526:
         case 12527:
         case 12528:
         case 16038:
         case 16039:
         case 16040:
         case 16068:
            return 1;
         case 12621:
            return 2;
         case 16037:
         case 16041:
         case 16042:
            return 3;
         default:
            return 0;
      }
   }

   @Override
   public double getColRadius() {
      if (this.getTransformation() != 0) {
         int var1 = this.getTransformationTemplate();
         if (var1 != 0) {
            NpcTemplate var2 = NpcHolder.getInstance().getTemplate(var1);
            if (var2 != null) {
               return var2.collisionRadius;
            }
         }
      } else if (this.isMounted()) {
         int var3 = this.getMountNpcId();
         if (var3 != 0) {
            NpcTemplate var4 = NpcHolder.getInstance().getTemplate(var3);
            if (var4 != null) {
               return var4.collisionRadius;
            }
         }
      }

      return this.getBaseTemplate().collisionRadius;
   }

   @Override
   public double getColHeight() {
      if (this.getTransformation() != 0) {
         int var1 = this.getTransformationTemplate();
         if (var1 != 0) {
            NpcTemplate var2 = NpcHolder.getInstance().getTemplate(var1);
            if (var2 != null) {
               return var2.collisionHeight;
            }
         }
      } else if (this.isMounted()) {
         int var3 = this.getMountNpcId();
         if (var3 != 0) {
            NpcTemplate var4 = NpcHolder.getInstance().getTemplate(var3);
            if (var4 != null) {
               return var4.collisionHeight;
            }
         }
      }

      return this.getBaseTemplate().collisionHeight;
   }

   @Override
   public void setReflection(Reflection var1) {
      if (this.getReflection() != var1) {
         super.setReflection(var1);
         if (this.Im != null && !this.Im.isDead()) {
            this.Im.setReflection(var1);
         }

         if (var1 != ReflectionManager.DEFAULT) {
            String var2 = this.getVar("reflection");
            if (var2 == null || !var2.equals(String.valueOf(var1.getId()))) {
               this.setVar("reflection", String.valueOf(var1.getId()), -1L);
            }
         } else {
            this.unsetVar("reflection");
         }

         if (this.getActiveClass() != null) {
            this.getInventory().validateItems();
            if (this.getPet() != null && (this.getPet().getNpcId() == 14916 || this.getPet().getNpcId() == 14917)) {
               this.getPet().unSummon();
            }
         }
      }
   }

   public void setBuyListId(int var1) {
      this.Ke = var1;
   }

   public int getBuyListId() {
      return this.Ke;
   }

   public int getIncorrectValidateCount() {
      return this.Kf;
   }

   public int setIncorrectValidateCount(int var1) {
      return this.Kf = var1;
   }

   public int getExpandInventory() {
      return this.Jj;
   }

   public void setExpandInventory(int var1) {
      this.Jj = var1;
   }

   public int getExpandWarehouse() {
      return this.Jk;
   }

   public void setExpandWarehouse(int var1) {
      this.Jk = var1;
   }

   public void enterMovieMode() {
      if (!this.isInMovie()) {
         this.setTarget(null);
         this.stopMove();
         this.setIsInMovie(true);
         this.sendPacket(new CameraMode(1));
      }
   }

   public void leaveMovieMode() {
      this.setIsInMovie(false);
      this.sendPacket(new CameraMode(0));
      this.broadcastCharInfo();
   }

   public void specialCamera(GameObject var1, int var2, int var3, int var4, int var5, int var6) {
      this.sendPacket(new SpecialCamera(var1.getObjectId(), var2, var3, var4, var5, var6));
   }

   public void specialCamera(GameObject var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      this.sendPacket(new SpecialCamera(var1.getObjectId(), var2, var3, var4, var5, var6, var7, var8, var9, var10));
   }

   public void setMovieId(int var1) {
      this.Kg = var1;
   }

   public int getMovieId() {
      return this.Kg;
   }

   public boolean isInMovie() {
      return this.Kh;
   }

   public void setIsInMovie(boolean var1) {
      this.Kh = var1;
   }

   public void showQuestMovie(SceneMovie var1) {
      if (!this.isInMovie()) {
         this.sendActionFailed();
         this.setTarget(null);
         this.stopMove();
         this.setMovieId(var1.getId());
         this.setIsInMovie(true);
         this.sendPacket(var1.packet(this));
      }
   }

   public void showQuestMovie(int var1) {
      if (!this.isInMovie()) {
         this.sendActionFailed();
         this.setTarget(null);
         this.stopMove();
         this.setMovieId(var1);
         this.setIsInMovie(true);
         this.sendPacket(new ExStartScenePlayer(var1));
      }
   }

   public void setAutoLoot(boolean var1) {
      if (Config.AUTO_LOOT_INDIVIDUAL) {
         this.Ht = var1;
         this.setVar("AutoLoot", String.valueOf(var1), -1L);
      }
   }

   public void setAutoLootHerbs(boolean var1) {
      if (Config.AUTO_LOOT_INDIVIDUAL) {
         this.Hu = var1;
         this.setVar("AutoLootHerbs", String.valueOf(var1), -1L);
      }
   }

   public void setAutoLootAdena(boolean var1) {
      if (Config.AUTO_LOOT_INDIVIDUAL) {
         this.Hv = var1;
         this.setVar("AutoLootAdend", String.valueOf(var1), -1L);
      }
   }

   public boolean isAutoLootEnabled() {
      return this.Ht;
   }

   public boolean isAutoLootHerbsEnabled() {
      return this.Hu;
   }

   public boolean isAutoLootAdenaEnabled() {
      return this.Hv;
   }

   public final void reName(String var1, boolean var2) {
      this.setName(var1);
      if (var2) {
         this.saveNameToDB();
      }

      if (this.isNoble()) {
         NoblesController.getInstance().renameNoble(this.getObjectId(), var1);
      }
   }

   public final void reName(String var1) {
      this.reName(var1, false);
   }

   public final void saveNameToDB() {
      Connection var1 = null;
      PreparedStatement var2 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("UPDATE characters SET `char_name` = ? WHERE `obj_Id` = ?");
         var2.setString(1, this.getName());
         var2.setInt(2, this.getObjectId());
         var2.executeUpdate();
      } catch (Exception var7) {
         _log.error("", var7);
      } finally {
         DbUtils.closeQuietly(var1, var2);
      }
   }

   @Override
   public Player getPlayer() {
      return this;
   }

   public final void disableDrop(int var1) {
      this.Jt = System.currentTimeMillis() + var1;
   }

   public final boolean isDropDisabled() {
      return this.Jt > System.currentTimeMillis();
   }

   public void setPetControlItem(int var1) {
      this.setPetControlItem(this.getInventory().getItemByObjectId(var1));
   }

   public void setPetControlItem(ItemInstance var1) {
      this.Ki = var1;
   }

   public ItemInstance getPetControlItem() {
      return this.Ki;
   }

   public boolean isActive() {
      return this.Kj.get();
   }

   public void setActive() {
      this.setNonAggroTime(0L);
      if (!this.Kj.getAndSet(true)) {
         this.cy();
      }
   }

   private void cy() {
      this.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONLOGIN);
      if (this.getPetControlItem() != null) {
         ThreadPoolManager.getInstance().execute(new RunnableImpl() {
            @Override
            public void runImpl() {
               if (Player.this.getPetControlItem() != null) {
                  Player.this.summonPet();
               }
            }
         });
      }
   }

   public void summonPet() {
      if (this.getPet() == null) {
         ItemInstance var1 = this.getPetControlItem();
         if (var1 != null) {
            int var2 = PetDataTable.getSummonId(var1);
            if (var2 != 0) {
               NpcTemplate var3 = NpcHolder.getInstance().getTemplate(var2);
               if (var3 != null) {
                  PetInstance var4 = PetInstance.restore(var1, var3, this);
                  if (var4 != null) {
                     this.setPet(var4);
                     var4.setTitle(this.getName());
                     if (!var4.isRespawned()) {
                        var4.setCurrentHp(var4.getMaxHp(), false);
                        var4.setCurrentMp(var4.getMaxMp());
                        var4.setCurrentFed(var4.getMaxFed());
                        var4.updateControlItem();
                        var4.store();
                     }

                     var4.getInventory().restore();
                     var4.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
                     var4.setReflection(this.getReflection());
                     var4.spawnMe(Location.findPointToStay(this, 50, 70));
                     var4.setRunning();
                     var4.setFollowMode(true);
                     var4.getInventory().validateItems();
                     if (var4 instanceof PetBabyInstance) {
                        ((PetBabyInstance)var4).startBuffTask();
                     }
                  }
               }
            }
         }
      }
   }

   public Collection<TrapInstance> getTraps() {
      if (this.Kk == null) {
         return null;
      }

      ArrayList var1 = new ArrayList(this.getTrapsCount());

      for (Integer var4 : this.Kk.keySet()) {
         TrapInstance var2;
         if ((var2 = (TrapInstance)GameObjectsStorage.get(this.Kk.get(var4))) != null) {
            var1.add(var2);
         } else {
            this.Kk.remove(var4);
         }
      }

      return var1;
   }

   public int getTrapsCount() {
      return this.Kk == null ? 0 : this.Kk.size();
   }

   public void addTrap(TrapInstance var1) {
      if (this.Kk == null) {
         this.Kk = new HashMap<>();
      }

      this.Kk.put(var1.getObjectId(), var1.getStoredId());
   }

   public void removeTrap(TrapInstance var1) {
      Map var2 = this.Kk;
      if (var2 != null && !var2.isEmpty()) {
         var2.remove(var1.getObjectId());
      }
   }

   public void destroyFirstTrap() {
      Map var1 = this.Kk;
      if (var1 != null && !var1.isEmpty()) {
         Iterator var3 = var1.keySet().iterator();
         if (var3.hasNext()) {
            Integer var4 = (Integer)var3.next();
            TrapInstance var2;
            if ((var2 = (TrapInstance)GameObjectsStorage.get((Long)var1.get(var4))) != null) {
               var2.deleteMe();
            }
         }
      }
   }

   public void destroyAllTraps() {
      Map var1 = this.Kk;
      if (var1 != null && !var1.isEmpty()) {
         ArrayList var2 = new ArrayList();

         for (Object __raw_var4_7903 : (Iterable<?>)(Object)(var1.keySet())) {
            Integer var4 = (Integer)__raw_var4_7903;
            var2.add((TrapInstance)GameObjectsStorage.get((Long)var1.get(var4)));
         }

         for (Object __raw_var6_7907 : (Iterable<?>)(Object)(var2)) {
            TrapInstance var6 = (TrapInstance)__raw_var6_7907;
            if (var6 != null) {
               var6.deleteMe();
            }
         }
      }
   }

   public PlayerListenerList getListeners() {
      if (this.listeners == null) {
         synchronized (this) {
            if (this.listeners == null) {
               this.listeners = new PlayerListenerList(this);
            }
         }
      }

      return (PlayerListenerList)this.listeners;
   }

   public PlayerStatsChangeRecorder getStatsRecorder() {
      if (this._statsRecorder == null) {
         synchronized (this) {
            if (this._statsRecorder == null) {
               this._statsRecorder = new PlayerStatsChangeRecorder(this);
            }
         }
      }

      return (PlayerStatsChangeRecorder)this._statsRecorder;
   }

   public int getHoursInGame() {
      this.Km++;
      return this.Km;
   }

   public void startHourlyTask() {
      this.Kl = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GameObjectTasks.HourlyTask(this), 3600000L, 3600000L);
   }

   public void stopHourlyTask() {
      if (this.Kl != null) {
         this.Kl.cancel(false);
         this.Kl = null;
      }
   }

   public long getPremiumPoints() {
      return Config.GAME_POINT_ITEM_ID > 0 ? ItemFunctions.getItemCount(this, Config.GAME_POINT_ITEM_ID) : 0L;
   }

   public void reducePremiumPoints(int var1) {
      if (Config.GAME_POINT_ITEM_ID > 0) {
         ItemFunctions.removeItem(this, Config.GAME_POINT_ITEM_ID, var1, true);
      }
   }

   public String getSessionVar(String var1) {
      return this.Kn == null ? null : this.Kn.get(var1);
   }

   public void setSessionVar(String var1, String var2) {
      if (this.Kn == null) {
         this.Kn = new ConcurrentHashMap<>();
      }

      if (var2 != null && !var2.isEmpty()) {
         this.Kn.put(var1, var2);
      } else {
         this.Kn.remove(var1);
      }
   }

   public FriendList getFriendList() {
      return this.IE;
   }

   public boolean isNotShowTraders() {
      return this.Jr;
   }

   public void setNotShowTraders(boolean var1) {
      this.Jr = var1;
   }

   public boolean isDebug() {
      return this.Js;
   }

   public void setDebug(boolean var1) {
      this.Js = var1;
   }

   public void sendItemList(boolean var1) {
      ItemInstance[] var2 = this.getInventory().getItems();
      LockType var3 = this.getInventory().getLockType();
      int[] var4 = this.getInventory().getLockItems();
      this.sendPacket(new ItemList(var2.length, var2, var1, var3, var4));
   }

   @Override
   public boolean isPlayer() {
      return true;
   }

   @Override
   public void startAttackStanceTask() {
      this.startAttackStanceTask0();
      Summon var1 = this.getPet();
      if (var1 != null) {
         var1.startAttackStanceTask0();
      }
   }

   @Override
   public void displayGiveDamageMessage(Creature var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      super.displayGiveDamageMessage(var1, var2, var3, var4, var5, var6);
      if (var3) {
         if (var6) {
            this.sendPacket(new SystemMessage(1280));
         } else {
            this.sendPacket(new SystemMessage(44));
         }
      }

      if (var4) {
         this.sendPacket(new SystemMessage(43));
      } else if (!var1.isDamageBlocked()) {
         this.sendPacket(new SystemMessage(35).addNumber(var2));
      }

      if (var1.isPlayer()) {
         if (var5 && var2 > 1) {
            var1.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
         } else if (var5 && var2 == 1) {
            var1.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
         }
      }
   }

   @Override
   public void displayReceiveDamageMessage(Creature var1, int var2) {
      if (var1 != this) {
         this.sendPacket(new SystemMessage(36).addName(var1).addNumber((long)var2));
      }
   }

   public IntObjectMap<String> getPostFriends() {
      return this.Jp;
   }

   public boolean isSharedGroupDisabled(int var1) {
      TimeStamp var2 = (TimeStamp)this.Jv.get(var1);
      if (var2 == null) {
         return false;
      }

      if (var2.hasNotPassed()) {
         return true;
      }

      this.Jv.remove(var1);
      return false;
   }

   public TimeStamp getSharedGroupReuse(int var1) {
      return (TimeStamp)this.Jv.get(var1);
   }

   public void addSharedGroupReuse(int var1, TimeStamp var2) {
      this.Jv.put(var1, var2);
   }

   public Collection<org.napile.primitive.maps.IntObjectMap.Entry<TimeStamp>> getSharedGroupReuses() {
      return this.Jv.entrySet();
   }

   public void sendReuseMessage(ItemInstance var1) {
      TimeStamp var2 = this.getSharedGroupReuse(var1.getTemplate().getReuseGroup());
      if (var2 != null && var2.hasNotPassed()) {
         long var3 = var2.getReuseCurrent();
         long var5 = TimeUnit.MILLISECONDS.toHours(var3);
         long var7 = TimeUnit.MILLISECONDS.toMinutes(var3 - TimeUnit.HOURS.toMillis(var5));
         long var9 = TimeUnit.MILLISECONDS.toSeconds(var3 - TimeUnit.MINUTES.toMillis(var7) - TimeUnit.HOURS.toMillis(var5));
         if (var5 > 0L) {
            this.sendMessage(
               new CustomMessage(var1.getTemplate().getReuseType().getMessageAddresses()[2], this)
                  .addItemName(var1.getTemplate().getItemId())
                  .add(var5)
                  .add(var7)
                  .add(var9)
            );
         } else if (var7 > 0L) {
            this.sendMessage(
               new CustomMessage(var1.getTemplate().getReuseType().getMessageAddresses()[1], this)
                  .addItemName(var1.getTemplate().getItemId())
                  .add(var7)
                  .add(var9)
            );
         } else {
            this.sendMessage(
               new CustomMessage(var1.getTemplate().getReuseType().getMessageAddresses()[0], this)
                  .addItemName(var1.getTemplate().getItemId())
                  .add(Math.max(1L, var9))
            );
         }
      } else {
         this.sendPacket(new SystemMessage(48).addItemName(var1.getTemplate().getItemId()));
      }
   }

   public void ask(ConfirmDlg var1, OnAnswerListener var2) {
      if (this.Jw == null) {
         int var3 = Rnd.nextInt();
         this.Jw = new ImmutablePair(var3, var2);
         var1.setRequestId(var3);
         this.sendPacket(var1);
      }
   }

   public Pair<Integer, OnAnswerListener> getAskListener(boolean var1) {
      if (!var1) {
         return this.Jw;
      }

      Pair var2 = this.Jw;
      this.Jw = null;
      return var2;
   }

   @Override
   public boolean isDead() {
      if (!this.isOlyParticipant()) {
         return this.isInDuel() ? this.getCurrentHp() <= 1.0 : super.isDead();
      } else {
         return this.isOlyCompetitionStarted() && this.isLooseOlyCompetition();
      }
   }

   public boolean hasPrivilege(Privilege var1) {
      return this.Ia != null && (this.getClanPrivileges() & var1.mask()) == var1.mask();
   }

   public MatchingRoom getMatchingRoom() {
      return this.Dn;
   }

   public void setMatchingRoom(MatchingRoom var1) {
      this.Dn = var1;
   }

   public void dispelBuffs() {
      for (Effect var2 : this.getEffectList().getAllEffects()) {
         if (!var2.getSkill().isOffensive() && !var2.getSkill().isNewbie() && var2.isCancelable() && !var2.getSkill().isPreservedOnDeath()) {
            this.sendPacket(new SystemMessage(749).addSkillName(var2.getSkill().getId(), var2.getSkill().getLevel()));
            var2.exit();
         }
      }

      if (this.getPet() != null) {
         for (Effect var4 : this.getPet().getEffectList().getAllEffects()) {
            if (!var4.getSkill().isOffensive() && !var4.getSkill().isNewbie() && var4.isCancelable() && !var4.getSkill().isPreservedOnDeath()) {
               var4.exit();
            }
         }
      }
   }

   public void setInstanceReuse(int var1, long var2) {
      CustomMessage var4 = new CustomMessage("INSTANT_ZONE_FROM_HERE__S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_ENTRY_POSSIBLE", this)
         .addString(this.getName());
      this.sendMessage(var4);
      this.Jx.put(var1, var2);
      InstanceReuseDAO.getInstance().setReuse(this, var1, var2);
   }

   public void removeInstanceReuse(int var1) {
      if (this.Jx.remove(var1) != null) {
         InstanceReuseDAO.getInstance().removeReuse(this, var1);
      }
   }

   public void removeAllInstanceReuses() {
      this.Jx.clear();
      InstanceReuseDAO.getInstance().removeAllReuse(this);
   }

   public void removeInstanceReusesByGroupId(int var1) {
      for (int var3 : InstantZoneHolder.getInstance().getSharedReuseInstanceIdsByGroup(var1)) {
         if (this.getInstanceReuse(var3) != null) {
            this.removeInstanceReuse(var3);
         }
      }
   }

   public Long getInstanceReuse(int var1) {
      return this.Jx.get(var1);
   }

   public Map<Integer, Long> getInstanceReuses() {
      return this.Jx;
   }

   public Reflection getActiveReflection() {
      for (Reflection var4 : ReflectionManager.getInstance().getAll()) {
         if (var4 != null && ArrayUtils.contains(var4.getVisitors(), this.getObjectId())) {
            return var4;
         }
      }

      return null;
   }

   public boolean canEnterInstance(int var1) {
      InstantZone var2 = InstantZoneHolder.getInstance().getInstantZone(var1);
      if (this.isDead()) {
         return false;
      } else if (ReflectionManager.getInstance().size() > Config.MAX_REFLECTIONS_COUNT) {
         this.sendMessage(new CustomMessage("THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED", this));
         return false;
      } else if (var2 == null) {
         this.sendPacket(SystemMsg.SYSTEM_ERROR);
         return false;
      } else if (ReflectionManager.getInstance().getCountByIzId(var1) >= var2.getMaxChannels()) {
         this.sendMessage(new CustomMessage("THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED", this));
         return false;
      } else {
         return var2.getEntryType().canEnter(this, var2);
      }
   }

   public boolean canReenterInstance(int var1) {
      InstantZone var2 = InstantZoneHolder.getInstance().getInstantZone(var1);
      if (this.getActiveReflection() != null && this.getActiveReflection().getInstancedZoneId() != var1) {
         this.sendMessage(new CustomMessage("YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON", this));
         return false;
      }

      if (var2.isDispelBuffs()) {
         this.dispelBuffs();
      }

      return var2.getEntryType().canReEnter(this, var2);
   }

   public int getBattlefieldChatId() {
      return this.Jn;
   }

   public void setBattlefieldChatId(int var1) {
      this.Jn = var1;
   }

   @Override
   public void broadCast(IStaticPacket... var1) {
      this.sendPacket(var1);
   }

   @Override
   public Iterator<Player> iterator() {
      return Collections.singleton(this).iterator();
   }

   public PlayerGroup getPlayerGroup() {
      if (this.getParty() != null) {
         return this.getParty().getCommandChannel() != null ? this.getParty().getCommandChannel() : this.getParty();
      } else {
         return this;
      }
   }

   public boolean isActionBlocked(String var1) {
      return this.Jq.contains(var1);
   }

   public void blockActions(String... var1) {
      Collections.addAll(this.Jq, var1);
   }

   public void unblockActions(String... var1) {
      for (String var5 : var1) {
         this.Jq.remove(var5);
      }
   }

   public void addRadar(int var1, int var2, int var3) {
      this.sendPacket(new RadarControl(0, 1, var1, var2, var3));
   }

   public void addRadarWithMap(int var1, int var2, int var3) {
      this.sendPacket(new RadarControl(0, 2, var1, var2, var3));
   }

   public long getAfterTeleportPortectionTime() {
      return this.Ko;
   }

   public void setAfterTeleportPortectionTime(long var1) {
      this.Ko = var1;
   }

   public void triggerAfterTeleportProtection() {
      if (Config.ALT_TELEPORT_PROTECTION && this.getAfterTeleportPortectionTime() > System.currentTimeMillis()) {
         this.setAfterTeleportPortectionTime(0L);
         this.sendMessage(new CustomMessage("alt.teleport_protect_gonna", this));
      }
   }

   public long getNoCarrierProtectionTime() {
      return this.Kp;
   }

   public void setNoCarrierProtectionTime(long var1) {
      this.Kp = var1;
   }

   public void triggerNoCarrierProtection() {
      if (Config.SERVICES_ENABLE_NO_CARRIER && Config.NO_CARRIER_PROTECTION && this.getNoCarrierProtectionTime() > System.currentTimeMillis()) {
         this.setNoCarrierProtectionTime(0L);
         this.sendMessage(new CustomMessage("alt.no_carrier_protect_gonna", this));
      }
   }

   public Henna[] getHennas() {
      return this.HR;
   }

   public boolean isTradeBannedByGM() {
      String var1 = this.getVar("tradeBan");
      long var4 = System.currentTimeMillis();
      long var2;
      if (!StringUtils.isBlank(var1) && ((var2 = Long.parseLong(var1)) == -1L || var2 >= var4)) {
         if (var2 == -1L) {
            this.sendMessage(new CustomMessage("common.TradeBannedPermanently", this));
         } else {
            this.sendMessage(new CustomMessage("common.TradeBanned", this).addString(Util.formatTime((int)TimeUnit.MILLISECONDS.toSeconds(var2 - var4))));
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public AutoFarmContext getFarmSystem() {
      return this.Jy;
   }

   public class BroadcastCharInfoTask extends RunnableImpl {
      @Override
      public void runImpl() throws Exception {
         Player.this.broadcastCharInfoImpl();
         Player.this.JE = null;
      }
   }

   public enum EPledgeRank {
      VAGABOND(0),
      VASSAL(1),
      HEIR(2),
      KNIGHT(3),
      WISEMAN(4),
      BARON(5),
      VISCOUNT(6),
      COUNT(7),
      MARQUIS(8);

      private final int Kq;
      public static Player.EPledgeRank[] VALUES = values();

      EPledgeRank(int var3) {
         this.Kq = var3;
      }

      public int getRankId() {
         return this.Kq;
      }

      public static Player.EPledgeRank getPledgeRank(int var0) {
         for (Player.EPledgeRank var4 : VALUES) {
            if (var4.getRankId() == var0) {
               return var4;
            }
         }

         return null;
      }
   }

   private class ForceCleanupTask implements Runnable {
      private ForceCleanupTask() {
      }

      @Override
      public void run() {
         long var1 = 600000L - (System.currentTimeMillis() - Player.this.JT);
         if (var1 > 1000L) {
            Player.this.JU = ThreadPoolManager.getInstance().schedule(Player.this.new ForceCleanupTask(), var1);
         } else {
            Player.this.JS = 0;
            Player.this.sendEtcStatusUpdate();
            Player.this.JU = null;
         }
      }
   }

   private static class MoveToLocationActionForOffload extends Creature.MoveToLocationAction {
      public MoveToLocationActionForOffload(Creature var1, Location var2, Location var3, boolean var4, int var5, boolean var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      private void cz() {
         Player var1 = (Player)this.getActor();
         Player.MoveToLocationOffloadData var2 = null;
         if (var1 != null && (var2 = var1.JD.get()) != null && var1.JD.compareAndSet(var2, null)) {
            var1.moveToLocation(var2.getDest(), var2.getIndent(), var2.isPathfind());
         }
      }

      @Override
      protected boolean onTick(double var1) {
         boolean var3;
         try {
            var3 = super.onTick(var1);
         } finally {
            this.cz();
         }

         return var3;
      }

      @Override
      protected void onFinish(boolean var1, boolean var2) {
         try {
            super.onFinish(var1, var2);
         } finally {
            this.cz();
         }
      }
   }

   private static class MoveToLocationOffloadData {
      private final Location Ks;
      private final int Kt;
      private final boolean Ku;

      public MoveToLocationOffloadData(Location var1, int var2, boolean var3) {
         this.Ks = var1;
         this.Kt = var2;
         this.Ku = var3;
      }

      public Location getDest() {
         return this.Ks;
      }

      public int getIndent() {
         return this.Kt;
      }

      public boolean isPathfind() {
         return this.Ku;
      }
   }

   private class UpdateEffectIcons extends RunnableImpl {
      private UpdateEffectIcons() {
      }

      @Override
      public void runImpl() throws Exception {
         Player.this.updateEffectIconsImpl();
         Player.this.JC = null;
      }
   }

   private class UserInfoTask extends RunnableImpl {
      private UserInfoTask() {
      }

      @Override
      public void runImpl() throws Exception {
         Player.this.cr();
         Player.this.JG = null;
      }
   }
}
