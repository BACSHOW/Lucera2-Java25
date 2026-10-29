package l2.gameserver;

import gnu.trove.TIntHash;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.commons.configuration.ExProperties;
import l2.commons.net.nio.impl.SelectorConfig;
import l2.gameserver.data.xml.parser.ChatFilterParser;
import l2.gameserver.model.actor.instances.player.Bonus;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.base.PlayerAccess;
import l2.gameserver.model.quest.QuestRates;
import l2.gameserver.network.authcomm.ServerType;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.pfilter.PacketFilterParser;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.napile.primitive.lists.impl.CArrayIntList;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class Config {
   private static final Logger _log = LoggerFactory.getLogger(Config.class);
   public static final int NCPUS = Runtime.getRuntime().availableProcessors();
   public static final String OTHER_CONFIG_FILE = "config/other.properties";
   public static final String RESIDENCE_CONFIG_FILE = "config/residence.properties";
   public static final String SPOIL_CONFIG_FILE = "config/spoil.properties";
   public static final String CLAN_CONFIG_FILE = "config/clan.properties";
   public static final String ALT_SETTINGS_FILE = "config/altsettings.properties";
   public static final String BOSS_SETTINGS_FILE = "config/bosses.properties";
   public static final String FORMULAS_CONFIGURATION_FILE = "config/formulas.properties";
   public static final String EXPERIENCE_CONFIG_FILE = "config/experience.csv";
   public static final String PVP_CONFIG_FILE = "config/pvp.properties";
   public static final String TELNET_CONFIGURATION_FILE = "config/telnet.properties";
   public static final String CONFIGURATION_FILE = "config/server.properties";
   public static final String AI_CONFIG_FILE = "config/ai.properties";
   public static final String GEODATA_CONFIG_FILE = "config/geodata.properties";
   public static final String EVENTS_CONFIG_FILE = "config/events.properties";
   public static final String SERVICES_FILE = "config/services.properties";
   public static final String SERVICES_RATE_BONUS_XML_FILE = "config/services_rate_bonus.xml";
   public static final String OLYMPIAD = "config/olympiad.properties";
   public static final String QUEST_RATE_FILE = "config/quest_rates.properties";
   public static final String CUSTOM_CONFIG_FILE = "config/custom.properties";
   public static final String AUTO_FARM_FILE = "config/auto_farm.properties";
   public static final String CHATFILTERS_CONFIG_FILE = "config/chatfilters.xml";
   public static final String PACKET_FILTER_CONFIG_FILE = "config/packetfilter.xml";
   public static final String GM_PERSONAL_ACCESS_FILE = "config/GMAccess.xml";
   public static final String GM_ACCESS_FILES_DIR = "config/GMAccess.d/";
   public static int HTM_CACHE_MODE;
   public static boolean USE_NETTY;
   public static int NETTY_BOSS_THREADS;
   public static int NETTY_WORKER_THREADS;
   public static int NETTY_SO_BACKLOG;
   public static int NETTY_WRITE_LOW_WATER_MARK;
   public static int NETTY_WRITE_HIGH_WATER_MARK;
   public static boolean USE_DISRUPTOR;
   public static boolean ENABLE_VISIBLE_OBJECT_RADIUS_FILTER;
   public static int VISIBLE_OBJECT_RADIUS;
   public static boolean ENABLE_NPC_INFO_PACING;
   public static int NPC_INFO_IMMEDIATE_BURST_LIMIT;
   public static int NPC_INFO_PACING_INTERVAL_MS;
   public static int NPC_INFO_PACED_BATCH_SIZE;
   public static boolean ENABLE_DELETE_OBJECT_COALESCING;
   public static int NPC_INFO_BURST_RESET_MS;
   public static boolean ENABLE_DETERMINISTIC_120HZ_PIPELINE;
   public static boolean ENABLE_FERMATA_EXTENSIONS;
   public static boolean ENABLE_ASYNC_CHARACTER_UPDATES;
   public static boolean ENABLE_ZSTD_GEODATA_CACHE;
   public static boolean ENABLE_DEX_ATTACK_RECOVERY_SCALING;
   public static double DEX_ATTACK_RECOVERY_MIN_FACTOR;
   public static double DEX_ATTACK_RECOVERY_MAX_FACTOR;
   public static int DEX_ATTACK_RECOVERY_BASE_DEX;
   public static double DEX_ATTACK_RECOVERY_DEX_DIVISOR;
   public static double DEX_ATTACK_RECOVERY_MAX_BONUS;
   public static int PHYSICAL_CRITICAL_RATE_CAP;
   public static double MAGIC_CRITICAL_RATE_CAP;
   public static int BLOW_RATE_CAP_GENERAL;
   public static int BLOW_RATE_CAP_BACKSTAB;
   public static int REQUEST_BYPASS_DEBOUNCE_TIME_MS;
   public static int REQUEST_BYPASS_MAX_PACKETS_PER_SECOND;
   public static int DETERMINISTIC_120HZ_PORT;
   public static int DISRUPTOR_RING_BUFFER_SIZE;
   public static boolean ENABLE_NETWORK_JIT_WARMUP;
   public static int DISRUPTOR_WARMUP_ITERATIONS;
   public static int[] PORTS_GAME;
   public static String GAMESERVER_HOSTNAME;
   public static int HAPROXY_PORT_GAME;
   public static String DATABASE_HOST;
   public static int DATABASE_PORT;
   public static String DATABASE_NAME;
   public static String DATABASE_USER;
   public static String DATABASE_PASS;
   public static int DATABASE_MAX_CONN;
   public static int DATABASE_TIMEOUT;
   public static String DATABASE_EX_PROPERTIES;
   public static boolean DATABASE_EX_STRUCTURE_MANAGER;
   public static String[] DATABASE_DUMP_TABLES;
   public static String DATABASE_DUMP_FILENAME_FORMAT;
   public static String DATABASE_DUMP_ZIP_OUT_FILENAME_FORMAT;
   public static boolean AUTOSAVE;
   public static boolean AUTOSAVE_ITEMS;
   public static boolean ALLOW_MULILOGIN;
   public static long USER_INFO_INTERVAL;
   public static boolean BROADCAST_STATS_INTERVAL;
   public static long BROADCAST_CHAR_INFO_INTERVAL;
   public static int EFFECT_TASK_MANAGER_COUNT;
   public static int MAXIMUM_ONLINE_USERS;
   public static boolean DONTLOADSPAWN;
   public static boolean DONTLOADQUEST;
   public static int[] IGNORE_QUESTS;
   public static int MAX_REFLECTIONS_COUNT;
   public static int SHIFT_BY;
   public static int SHIFT_BY_Z;
   public static int MAP_MIN_Z;
   public static int MAP_MAX_Z;
   public static int CHAT_MESSAGE_MAX_LEN;
   public static ChatType[] BAN_CHANNEL_LIST;
   public static boolean BANCHAT_ANNOUNCE;
   public static boolean BANCHAT_ANNOUNCE_FOR_ALL_WORLD;
   public static boolean BANCHAT_ANNOUNCE_NICK;
   public static boolean NOSPAM_FILTER_HWID;
   public static boolean NOSPAM_FILTER_IP;
   public static boolean SAVING_SPS;
   public static boolean MANAHEAL_SPS_BONUS;
   public static int ALT_ADD_RECIPES;
   public static int ALT_PARTY_DISTRIBUTION_RANGE;
   public static int ALT_PARTY_DISTRIBUTION_DIFF_LEVEL_LIMIT;
   public static double[] ALT_PARTY_BONUS;
   public static boolean ALT_PARTY_BONUS_FOR_QUESTS;
   public static double ALT_ABSORB_DAMAGE_MODIFIER;
   public static double ALT_POLE_DAMAGE_MODIFIER;
   public static int ALT_REMOVE_SKILLS_ON_DELEVEL;
   public static int ALT_HOT_SPIRIT_CHANCE_DEBUFF;
   public static boolean ALT_TELEPORT_FROM_SEVEN_SING_MONSTER;
   public static boolean ALT_ENABLE_SEVEN_SING_TELEPORTER_PROTECTION;
   public static boolean ALT_MAMONS_CHECK_SEVEN_SING_STATUS;
   public static boolean ALT_CABAL_SEVEN_SING_SHOUT;
   public static boolean ALT_SEVEN_SING_STATIC_EVENT_PERIOD_SPAWN;
   public static long L2TOPRU_DELAY;
   public static String L2TOPRU_PREFIX;
   public static String L2TOPRU_WEB_VOTE_URL;
   public static String L2TOPRU_SMS_VOTE_URL;
   public static int L2TOPRU_WEB_REWARD_ITEMID;
   public static int L2TOPRU_WEB_REWARD_ITEMCOUNT;
   public static int L2TOPRU_SMS_REWARD_ITEMID;
   public static int L2TOPRU_SMS_REWARD_ITEMCOUNT;
   public static boolean L2TOPRU_SMS_REWARD_VOTE_MULTI;
   public static boolean L2TOPZONE_ENABLED;
   public static long L2TOPZONE_VOTE_TIME_TO_LIVE;
   public static String L2TOPZONE_API_KEY;
   public static int[] L2TOPZONE_REWARD_ITEM_ID;
   public static int[] L2TOPZONE_REWARD_ITEM_COUNT;
   public static int[] L2TOPZONE_REWARD_CHANCE;
   public static boolean L2HOPZONE_ENABLED;
   public static long L2HOPZONE_VOTE_TIME_TO_LIVE;
   public static String L2HOPZONE_API_KEY;
   public static int[] L2HOPZONE_REWARD_ITEM_ID;
   public static int[] L2HOPZONE_REWARD_ITEM_COUNT;
   public static int[] L2HOPZONE_REWARD_CHANCE;
   public static long L2HOPZONE_REUSE_TIME;
   public static String L2HOPZONE_ADD_COMMAND;
   public static boolean L2JBRAZIL_ENABLED;
   public static long L2JBRAZIL_VOTE_TIME_TO_LIVE;
   public static String L2JBRAZIL_API_KEY;
   public static int[] L2JBRAZIL_REWARD_ITEM_ID;
   public static int[] L2JBRAZIL_REWARD_ITEM_COUNT;
   public static int[] L2JBRAZIL_REWARD_CHANCE;
   public static String MMO_TOP_CRON_TASK;
   public static String MMO_TOP_VOTES_LINK;
   public static String MMO_TOP_REWARD;
   public static int MMO_TOP_VOTE_WINDOW_DAYS;
   public static int MMO_TOP_VOTE_REWARD_HOURS;
   public static Calendar CASTLE_VALIDATION_DATE;
   public static boolean ALT_PCBANG_POINTS_ENABLED;
   public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE;
   public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_RATE;
   public static double ALT_PCBANG_BONUS_WITH_PREMIUM_RATE;
   public static int ALT_PCBANG_POINTS_BONUS;
   public static int ALT_PCBANG_POINTS_DELAY;
   public static int ALT_PCBANG_POINTS_MIN_LVL;
   public static boolean ALT_PCBANG_CHECK_HWID;
   public static boolean ALT_DEBUG_ENABLED;
   public static boolean ALT_DEBUG_PVP_ENABLED;
   public static boolean ALT_DEBUG_PVP_DUEL_ONLY;
   public static boolean ALT_DEBUG_PVE_ENABLED;
   public static int ALT_LIMIT_IP_WINDOW;
   public static int ALT_LIMIT_HWID_WINDOW;
   public static boolean ALT_LIMIT_PREMIUM_ACCOUNT;
   public static boolean ALT_MULTISELL_DEBUG;
   public static boolean ALT_CHECK_CERTIFICATION_ITEMS;
   public static int SCHEDULED_THREAD_POOL_SIZE;
   public static int EXECUTOR_THREAD_POOL_SIZE;
   public static boolean ENABLE_RUNNABLE_STATS;
   public static SelectorConfig SELECTOR_CONFIG = new SelectorConfig();
   public static boolean AUTO_LOOT;
   public static boolean AUTO_LOOT_HERBS;
   public static boolean AUTO_LOOT_INDIVIDUAL;
   public static boolean AUTO_LOOT_ONLY_FOR_PREMIUM;
   public static boolean AUTO_LOOT_FROM_RAIDS;
   public static boolean AUTO_LOOT_ADENA;
   public static int[] AUTO_LOOT_EXCLUDE_ITEM_IDS;
   public static boolean XP_FREEZ_ONLY_FOR_PREMIUM;
   public static boolean AUTO_LOOT_PK;
   public static String CNAME_TEMPLATE;
   public static String CNAME_FORBIDDEN_PATTERN;
   public static String[] CNAME_FORBIDDEN_NAMES;
   public static String CUSTOM_CNAME_TEMPLATE;
   public static int CNAME_MAXLEN = 32;
   public static String CLAN_NAME_TEMPLATE;
   public static String CLAN_TITLE_TEMPLATE;
   public static String ALLY_NAME_TEMPLATE;
   public static boolean GLOBAL_SHOUT;
   public static int GLOBAL_SHOUT_MIN_LEVEL;
   public static int GLOBAL_SHOUT_MIN_PVP_COUNT;
   public static boolean GLOBAL_TRADE_CHAT;
   public static int GLOBAL_TRADE_CHAT_MIN_LEVEL;
   public static int GLOBAL_TRADE_MIN_PVP_COUNT;
   public static int CHAT_RANGE;
   public static int SHOUT_OFFSET;
   public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
   public static boolean RAID_TELE_TO_HOME_FROM_PVP_ZONES;
   public static boolean RAID_TELE_TO_HOME_FROM_TOWN_ZONES;
   public static double ALT_RAID_RESPAWN_MULTIPLIER;
   public static long FWA_LIMITUNTILSLEEPANTHARAS;
   public static long FWA_LIMITMAXUNTILSLEEPANTHARAS;
   public static long VALAKAS_CLEAR_ZONE_IF_ALL_DIE;
   public static long FWA_FIXTIMEINTERVALOFANTHARAS;
   public static long FWA_RANDOMINTERVALOFANTHARAS;
   public static String FWA_FIXTIMEPATTERNOFANTHARAS;
   public static long FWA_APPTIMEOFANTHARAS;
   public static boolean FWA_ALLOW_ANTHARAS_MINION;
   public static long FWV_LIMITUNTILSLEEPVALAKAS;
   public static String FWA_FIXTIMEPATTERNOFVALAKAS;
   public static long ANTHARAS_CLEAR_ZONE_IF_ALL_DIE;
   public static long SAILREN_CLEAR_ZONE_IF_ALL_DIE;
   public static long BAIUM_CLEAR_ZONE_IF_ALL_DIE;
   public static long FWV_APPTIMEOFVALAKAS;
   public static long FWA_LIMITMAXUNTILSLEEPVALAKAS;
   public static long FWV_FIXINTERVALOFVALAKAS;
   public static long FWV_RANDOMINTERVALOFVALAKAS;
   public static long FWB_LIMITUNTILSLEEPBAIUM;
   public static long FWB_FIXINTERVALOFBAIUM;
   public static long FWB_RANDOMINTERVALOFBAIUM;
   public static long FWA_LIMITMAXUNTILSLEEPBAIUM;
   public static String FWA_FIXTIMEPATTERNOFBAIUM;
   public static long FWS_ACTIVITYTIMEOFMOBS;
   public static long FWS_FIXINTERVALOFSAILRENSPAWN;
   public static long FWS_RANDOMINTERVALOFSAILRENSPAWN;
   public static String FWA_FIXTIMEPATTERNOFSAILREN;
   public static long FWS_INTERVALOFNEXTMONSTER;
   public static long FWF_FIXINTERVALOFFRINTEZZA;
   public static long FWF_RANDOMINTERVALOFFRINTEZZA;
   public static String FWA_FIXTIMEPATTERNOFFRINTEZZA;
   public static int FRINTEZZA_TOMB_TIMEOUT;
   public static int FRINTEZZA_MIN_PARTY_IN_CC;
   public static int FRINTEZZA_MAX_PARTY_IN_CC;
   public static int FRINTEZZA_MIN_DISTANCE_FOR_ENTRANCE;
   public static int MINIONS_RESPAWN_INTERVAL;
   public static boolean ENABLE_ON_TIME_DOOR_OPEN;
   public static int ON_TIME_OPEN_DOOR_ID;
   public static String ON_TIME_OPEN_PATTERN;
   public static boolean ZAKEN_USE_TELEPORT;
   public static boolean ORFEN_USE_TELEPORT;
   public static boolean CAN_ATTACK_FROM_ANOTHER_ZONE_TO_EPIC;
   public static boolean ALLOW_BAIUM_RAID;
   public static boolean BAIUM_CHECK_QUEST_ON_AWAKE;
   public static boolean VALAKAS_ANNOUNCE_PREPARING_SPAWN;
   public static boolean ANTHARAS_ANNOUNCE_PREPARING_SPAWN;
   public static boolean ALT_ALLOW_DROP_AUGMENTED;
   public static boolean ALT_ALLOW_TRADE_AUGMENTED;
   public static boolean ALT_GAME_UNREGISTER_RECIPE;
   public static int SS_ANNOUNCE_PERIOD;
   public static boolean PETITIONING_ALLOWED;
   public static boolean CAN_PETITION_TO_OFFLINE_GM;
   public static int MAX_PETITIONS_PER_PLAYER;
   public static int MAX_PETITIONS_PENDING;
   public static boolean ALT_GAME_SHOW_DROPLIST;
   public static boolean ALT_FULL_NPC_STATS_PAGE;
   public static boolean ALLOW_NPC_SHIFTCLICK;
   public static int ALT_NPC_SHIFTCLICK_ITEM_COUNT;
   public static boolean ALT_ALLOW_SELL_COMMON;
   public static boolean ALT_ALLOW_SHADOW_WEAPONS;
   public static boolean ALT_ALLOW_REMOTE_SELL_ITEMS_TO_SHOP;
   public static boolean ALT_ALLOW_REMOTE_USE_CARGO_BOX;
   public static int[] ALT_DISABLED_MULTISELL;
   public static int[] ALT_SHOP_PRICE_LIMITS;
   public static int[] ALT_SHOP_UNALLOWED_ITEMS;
   public static long ALT_SHOP_REFUND_SELL_DIVISOR;
   public static long ALT_PRIVATE_STORE_BUY_DIVISOR;
   public static double ALT_SHOP_BUY_LIST_MULTIPLIER;
   public static double ALT_MULTISELL_ADENA_MULTIPLAYER;
   public static int[] ALT_ALLOWED_PET_POTIONS;
   public static int[] ALT_RAID_BOSS_SPAWN_ANNOUNCE_IDS;
   public static int ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY;
   public static TIntHash ALT_RAID_DEATH_ANNOUNCE_IDS;
   public static long[] EXPERIENCE;
   public static double SKILLS_CHANCE_MOD;
   public static double SKILLS_CHANCE_MIN;
   public static double SKILLS_CHANCE_POW;
   public static double SKILLS_CHANCE_CAP;
   public static boolean ALT_SAVE_UNSAVEABLE;
   public static int ALT_SAVE_EFFECTS_REMAINING_TIME;
   public static boolean ALT_SUBLASS_SKILL_TRANSFER;
   public static boolean ALT_SHOW_REUSE_MSG;
   public static boolean ALT_DELETE_SA_BUFFS;
   public static boolean BUFF_STICK_FOR_ALL;
   public static int ALT_PASSIVE_NOBLESS_ID;
   public static int SKILLS_CAST_TIME_MIN;
   public static int SKILLS_DISPEL_MOD_MAX;
   public static int SKILLS_DISPEL_MOD_MIN;
   public static int SKILLS_BANE_MOD_MAX;
   public static int SKILLS_BANE_MOD_MIN;
   public static boolean CHAR_TITLE;
   public static String ADD_CHAR_TITLE;
   public static String DISCONNECTED_PLAYER_TITLE;
   public static int DISCONNECTED_PLAYER_TITLE_COLOR;
   public static boolean ALT_SOCIAL_ACTION_REUSE;
   public static boolean ALT_DISABLE_SPELLBOOKS;
   public static boolean ALT_WEAK_SKILL_LEARN;
   public static boolean ALT_GAME_DELEVEL;
   public static boolean ALT_ALLOW_CUSTOM_HERO;
   public static boolean ALT_ALLOW_CUSTOM_HERO_SKILLS;
   public static long CUSTOM_HERO_STATUS_TIME;
   public static boolean ALT_HAIR_TO_ACC_SLOT;
   public static int ALT_NEW_CHARACTER_LEVEL;
   public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
   public static boolean ALT_GAME_SUBCLASS_NOT_CHECK_QUEST_234;
   public static boolean ALT_ALLOW_SUBCLASS_FOR_CUSTOM_ITEM;
   public static int[] ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID;
   public static int[] ALT_SUBCLASS_FOR_CUSTOM_ITEM_COUNT;
   public static boolean ALTSUBCLASS_LIST_ALL;
   public static boolean ALT_ALLOW_ALLCLASS_SKILLENCHANT;
   public static boolean ALT_ALLOW_ALLCLASS_SKILL_LEARN;
   public static boolean ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL;
   public static boolean ALTSUBCLASS_ALLOW_FOR_ELF_TO_DARK_ELF;
   public static int ALT_GAME_LEVEL_TO_GET_SUBCLASS;
   public static int ALT_LEVEL_AFTER_GET_SUBCLASS;
   public static int ALT_MAX_LEVEL;
   public static int ALT_MAX_SUB_LEVEL;
   public static boolean ALT_ALLOW_HERO_SKILLS_ON_SUB_CLASS;
   public static int ALT_GAME_BASE_SUB;
   public static boolean ALT_NO_LASTHIT;
   public static boolean ALT_DROP_LASTHIT;
   public static boolean ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY;
   public static boolean ALT_KAMALOKA_NIGHTMARE_REENTER;
   public static boolean ALT_KAMALOKA_ABYSS_REENTER;
   public static boolean ALT_KAMALOKA_LAB_REENTER;
   public static boolean ALT_PET_HEAL_BATTLE_ONLY;
   public static boolean ALT_SIMPLE_SIGNS;
   public static boolean ALT_TELE_TO_CATACOMBS;
   public static boolean ALT_BS_CRYSTALLIZE;
   public static boolean ALT_ALLOW_TATTOO;
   public static int ALT_BUFF_LIMIT;
   public static int ALT_DEBUFF_LIMIT;
   public static int ALT_TRIGGER_SLOT_ADDER;
   public static List<Pair<Integer, Integer>> OTHER_MAGE_BUFF_ON_CHAR_CREATE;
   public static List<Pair<Integer, Integer>> OTHER_WARRIOR_BUFF_ON_CHAR_CREATE;
   public static long ALT_NPC_BUFFER_EFFECT_TIME;
   public static long ALT_NPC_BUFFER_REUSE_DELAY;
   public static boolean ALT_NPC_BUFFER_PREMIUM_HTML_PREFIX;
   public static int[] ALT_NPC_BUFFER_PREMIUM_ITEM_IDS;
   public static boolean ALT_ALLOW_DELAY_NPC_TALK;
   public static int ALT_MAX_PARTY_SIZE;
   public static Map<Integer, Integer> ALT_PARTY_CLASS_LIMIT;
   public static boolean PRIVATE_BUY_MATCH_ENCHANT;
   public static int MULTISELL_SIZE;
   public static boolean SERVICES_CHANGE_NICK_ENABLED;
   public static int SERVICES_CHANGE_NICK_PRICE;
   public static int SERVICES_CHANGE_NICK_ITEM;
   public static boolean QUEST_SELL_ENABLE;
   public static String QUEST_SELL_QUEST_PRICES;
   public static boolean QUEST_CAPTCHA_ENABLE;
   public static int QUEST_CAPTCHA_MIN_LEVEL_FOR_CHECK;
   public static int QUEST_CAPTCHA_CHANCE_SHOW;
   public static int QUEST_CAPTCHA_CHANCE_TIMEOUT_REPEAT;
   public static int APPEARANCE_APPLY_ITEM_ID;
   public static int APPEARANCE_SUPPORT_ITEM_ID;
   public static long APPEARANCE_SUPPORT_ITEM_CNT;
   public static int APPEARANCE_CANCEL_ITEM_ID;
   public static long APPEARANCE_CANCEL_PRICE;
   public static int[] APPEARANCE_PROHIBITED_BE_CHANGED;
   public static int[] APPEARANCE_PROHIBITED_CHANGE;
   public static boolean SERVICES_CHANGE_CLAN_NAME_ENABLED;
   public static int SERVICES_CHANGE_CLAN_NAME_PRICE;
   public static int SERVICES_CHANGE_CLAN_NAME_ITEM;
   public static boolean SERVICES_CHANGE_PET_NAME_ENABLED;
   public static int SERVICES_CHANGE_PET_NAME_PRICE;
   public static int SERVICES_CHANGE_PET_NAME_ITEM;
   public static boolean SERVICES_EXCHANGE_BABY_PET_ENABLED;
   public static int SERVICES_EXCHANGE_BABY_PET_PRICE;
   public static int SERVICES_EXCHANGE_BABY_PET_ITEM;
   public static boolean SERVICES_CHANGE_SEX_ENABLED;
   public static int SERVICES_CHANGE_SEX_PRICE;
   public static int SERVICES_CHANGE_SEX_ITEM;
   public static boolean SERVICES_CHANGE_BASE_ENABLED;
   public static boolean SERVICES_CHANGE_BASE_LIST_UNCOMPATABLE;
   public static int SERVICES_CHANGE_BASE_PRICE;
   public static int SERVICES_CHANGE_BASE_ITEM;
   public static boolean SERVICES_SEPARATE_SUB_ENABLED;
   public static int SERVICES_SEPARATE_SUB_PRICE;
   public static int SERVICES_SEPARATE_SUB_ITEM;
   public static int SERVICES_SEPARATE_SUB_MIN_LEVEL;
   public static boolean SERVICES_CHANGE_NICK_COLOR_ENABLED;
   public static int[] SERVICES_CHANGE_NICK_COLOR_PRICE;
   public static int[] SERVICES_CHANGE_NICK_COLOR_ITEM;
   public static String[] SERVICES_CHANGE_NICK_COLOR_LIST;
   public static boolean SERVICES_CHANGE_TITLE_COLOR_ENABLED;
   public static int[] SERVICES_CHANGE_TITLE_COLOR_PRICE;
   public static int[] SERVICES_CHANGE_TITLE_COLOR_ITEM;
   public static String[] SERVICES_CHANGE_TITLE_COLOR_LIST;
   public static boolean SERVICE_CAPTCHA_BOT_CHECK;
   public static int CAPTCHA_MONSTER_KILLS_THRESHOLD;
   public static int CAPTCHA_PENALTY_SKILL_ID;
   public static int CAPTCHA_PENALTY_SKILL_LEVEL;
   public static boolean FARM_ONLINE_TYPE;
   public static boolean PREMIUM_FARM_FREE;
   public static boolean ALLOW_AUTO_FARM;
   public static boolean AUTO_FARM_FOR_PREMIUM;
   public static boolean AUTO_FARM_FREE;
   public static Map<Integer, String> AUTO_FARM_PRICES;
   public static int ATTACK_SKILL_CHANCE;
   public static int ATTACK_SKILL_PERCENT;
   public static int CHANCE_SKILL_CHANCE;
   public static int CHANCE_SKILL_PERCENT;
   public static int SELF_SKILL_CHANCE;
   public static int SELF_SKILL_PERCENT;
   public static int HEAL_SKILL_CHANCE;
   public static int HEAL_SKILL_PERCENT;
   public static int SUMMON_ATTACK_SKILL_CHANCE;
   public static int SUMMON_ATTACK_SKILL_PERCENT;
   public static int SUMMON_SELF_SKILL_CHANCE;
   public static int SUMMON_SELF_SKILL_PERCENT;
   public static int SUMMON_HEAL_SKILL_CHANCE;
   public static int SUMMON_HEAL_SKILL_PERCENT;
   public static long SKILLS_EXTRA_DELAY;
   public static long KEEP_LOCATION_DELAY;
   public static long RUN_CLOSE_UP_DELAY;
   public static int RUN_CLOSE_UP_DISTANCE;
   public static int SHORTCUT_PAGE;
   public static int SEARCH_DISTANCE;
   public static int FARM_TYPE;
   public static int FARM_INTERVAL_TASK;
   public static boolean ALLOW_FARM_FREE_TIME;
   public static boolean REFRESH_FARM_TIME;
   public static int FARM_FREE_TIME;
   public static boolean ALLOW_CHECK_HWID_LIMIT;
   public static int FARM_ACTIVE_LIMITS;
   public static int[] FARM_EXPEND_LIMIT_PRICE = new int[2];
   public static int[] AUTO_FARM_IGNORED_NPC_ID;
   public static AbnormalEffect SERVICES_AUTO_FARM_ABNORMAL;
   public static boolean SERVICE_AUTO_FARM_SET_RED_RING;
   public static Set<String> AUTO_FARM_LIMIT_ZONE_NAMES;
   public static boolean SERVICES_RATE_ENABLED;
   public static boolean SERVICES_RATE_COMMAND_ENABLED;
   public static boolean SERVICES_RATE_EXPIRE_TIME_AT_ENTER_WORLD;
   public static Config.RateBonusInfo[] SERVICES_RATE_BONUS_INFO = new Config.RateBonusInfo[0];
   public static boolean SERVICES_NOBLESS_SELL_ENABLED;
   public static boolean SERVICES_NOBLESS_SELL_WITHOUT_SUBCLASS;
   public static int SERVICES_NOBLESS_SELL_PRICE;
   public static int SERVICES_NOBLESS_SELL_ITEM;
   public static int NOBLESS_LEVEL_FOR_SELL;
   public static boolean SERVICES_ITEM_NOBLESS_SELL_ENABLED;
   public static int[] SERVICES_ITEM_NOBLESS_SELL_ITEM_ID;
   public static boolean SERVICES_ITEM_NOBLESS_SELL_WITHOUT_SUBCLASS;
   public static int NOBLESS_ITEM_LEVEL_FOR_SELL;
   public static boolean SERVICES_DELEVEL_SELL_ENABLED;
   public static int SERVICES_DELEVEL_SELL_PRICE;
   public static int SERVICES_DELEVEL_SELL_ITEM;
   public static int SERVICES_DELEVEL_REDUCTION;
   public static boolean SERVICES_DELEVEL_SUBCLASS_CHECK_THRESHOLD;
   public static boolean SERVICES_DELEVEL_ALLOW_FOR_SUBCLASS;
   public static boolean SERVICE_RESET_LEVEL_ENABLED;
   public static int SERVICE_RESET_LEVEL_ITEM_ID;
   public static int SERVICE_RESET_LEVEL_ITEM_COUNT;
   public static boolean SERVICE_RESET_LEVEL_SUBCLASS_CHECK_THRESHOLD;
   public static int SERVICE_RESET_LEVEL_NEED;
   public static int SERVICE_RESET_LEVEL_REMOVE;
   public static int SERVICE_RESET_LEVEL_LIMIT;
   public static long SERVICE_RESET_REUSE_DELAY;
   public static boolean SERVICE_RESET_ALLOW_FOR_SUBCLASS;
   public static boolean SERVICES_LEVEL_UP_SELL_ENABLED;
   public static int SERVICES_LEVEL_UP_SELL_PRICE;
   public static int SERVICES_LEVEL_UP_SELL_ITEM;
   public static int SERVICES_LEVEL_UP_COUNT;
   public static boolean SERVICES_HERO_SELL_ENABLED;
   public static int[] SERVICES_HERO_SELLER_ITEM_IDS;
   public static long[] SERVICES_HERO_SELLER_ITEM_COUNTS;
   public static long[] SERVICE_HERO_STATUS_DURATIONS;
   public static boolean SERVICES_OLY_POINTS_RESET;
   public static int SERVICES_OLY_POINTS_RESET_ITEM_ID;
   public static int SERVICES_OLY_POINTS_RESET_ITEM_AMOUNT;
   public static int SERVICES_OLY_POINTS_THRESHOLD;
   public static int SERVICES_OLY_POINTS_REWARD;
   public static boolean SERVICES_PK_CLEAN_ENABLED;
   public static int SERVICES_PK_CLEAN_SELL_ITEM;
   public static long SERVICES_PK_CLEAN_SELL_PRICE;
   public static boolean SERVICE_PROMOCODE_ENABLED;
   public static boolean SERVICE_PROMOCODE_COMMAND_ENABLED;
   public static int SERVICES_PK_ANNOUNCE;
   public static boolean SERVICES_ALLOW_WYVERN_RIDE;
   public static int SERVICES_WYVERN_ITEM_ID;
   public static boolean SERVICES_KARMA_CLEAN_ENABLED;
   public static int SERVICES_KARMA_CLEAN_SELL_ITEM;
   public static long SERVICES_KARMA_CLEAN_SELL_PRICE;
   public static boolean SERVICES_CLANLEVEL_SELL_ENABLED;
   public static long[] SERVICES_CLANLEVEL_SELL_PRICE;
   public static int[] SERVICES_CLANLEVEL_SELL_ITEM;
   public static boolean SERVICES_CLAN_REPUTATION_ENABLE;
   public static int SERVICES_CLAN_REPUTATION_ITEM_ID;
   public static int SERVICES_CLAN_REPUTATION_ITEM_COUNT;
   public static int SERVICES_CLAN_REPUTATION_AMOUNT;
   public static boolean SERVICES_CLANHELPER_ENABLED;
   public static String SERVICES_CLANHELPER_CONFIG;
   public static boolean SERVICES_CLANHELPER_ADD_FULL_CLAN_SKILLS;
   public static boolean SERVICES_CLAN_BUFF_ENABLED;
   public static int SERVICES_CLAN_BUFF_SKILL_ID;
   public static String SERVICES_CLAN_BUFF_LEVELS_MIN_ONLINE;
   public static long SERVICES_CLAN_BUFF_TASK_DELAY;
   public static boolean SERVICES_EXPAND_INVENTORY_ENABLED;
   public static int SERVICES_EXPAND_INVENTORY_PRICE;
   public static int SERVICES_EXPAND_INVENTORY_ITEM;
   public static int SERVICES_EXPAND_INVENTORY_MAX;
   public static int SERVICES_EXPAND_INVENTORY_SLOT_AMOUNT;
   public static boolean SERVICES_EXPAND_WAREHOUSE_ENABLED;
   public static int SERVICES_EXPAND_WAREHOUSE_PRICE;
   public static int SERVICES_EXPAND_WAREHOUSE_ITEM;
   public static int SERVICES_EXPAND_WAREHOUSE_MAX;
   public static int SERVICES_EXPAND_WAREHOUSE_SLOT_AMOUNT;
   public static boolean SERVICES_EXPAND_CWH_ENABLED;
   public static int SERVICES_EXPAND_CWH_PRICE;
   public static int SERVICES_EXPAND_CWH_SLOT_AMOUNT;
   public static int SERVICES_EXPAND_CWH_ITEM;
   public static int SEVICES_EXPAND_CWH_SLOT_LIMITS;
   public static boolean SERVICES_AUTO_HEAL_ACTIVE;
   public static String SERVICES_SELLPETS;
   public static boolean SERVICES_OFFLINE_TRADE_ALLOW;
   public static boolean ALLOW_TRADE_ON_THE_MOVE;
   public static boolean SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE;
   public static int SERVICES_OFFLINE_TRADE_MIN_LEVEL;
   public static int SERVICES_OFFLINE_TRADE_NAME_COLOR;
   public static boolean SERVICES_OFFLINE_TRADE_NAME_COLOR_CHANGE;
   public static AbnormalEffect SERVICES_OFFLINE_TRADE_ABNORMAL;
   public static int SERVICES_OFFLINE_TRADE_PRICE;
   public static int SERVICES_OFFLINE_TRADE_PRICE_ITEM;
   public static long SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK;
   public static boolean SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART;
   public static boolean SERVICES_GIRAN_HARBOR_ENABLED;
   public static boolean SERVICES_GIRAN_HARBOR_NOTAX;
   public static int SERVICES_GIRAN_HARBOR_PRICE;
   public static int SERVICES_GIRAN_HARBOR_MP_REG_PRICE;
   public static boolean SERVICES_ALLOW_LOTTERY;
   public static int SERVICE_LOTTERY_ITEM_ID;
   public static int SERVICES_LOTTERY_PRIZE;
   public static int SERVICES_ALT_LOTTERY_PRICE;
   public static int SERVICES_LOTTERY_TICKET_PRICE;
   public static double SERVICES_LOTTERY_5_NUMBER_RATE;
   public static double SERVICES_LOTTERY_4_NUMBER_RATE;
   public static double SERVICES_LOTTERY_3_NUMBER_RATE;
   public static int SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
   public static boolean SERVICES_ALLOW_ROULETTE;
   public static int SERVICES_ROULETTE_ITEM_ID;
   public static int SERVICES_ROULETTE_MIN_BET;
   public static int SERVICES_ROULETTE_MAX_BET;
   public static boolean SERVICE_ANNOUNCE_SERVER_LIFE_TIME;
   public static Location SERVICE_JAIL_COORDINATES;
   public static boolean ITEM_CLAN_REPUTATION_ENABLE;
   public static int[] ITEM_CLAN_REPUTATION_ID;
   public static int[] ITEM_CLAN_REPUTATION_AMOUNT;
   public static boolean ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE;
   public static boolean ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER;
   public static boolean ALT_ALLOW_CLAN_COMMAND_ALLOW_WH;
   public static boolean ALT_ALLOW_MENU_COMMAND;
   public static boolean ALT_ALLOW_RELOG_COMMAND;
   public static boolean ALT_ALLOW_LANG_COMMAND;
   public static String ALT_CHANGE_LANG_NAME_1;
   public static String ALT_CHANGE_LANG_NAME_2;
   public static boolean ALT_ALLOW_PING_COMMAND;
   public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
   public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
   public static boolean ALT_GAME_ALLOW_ADENA_DAWN;
   public static boolean ALT_ALLOW_HELP_COMMAND;
   public static boolean ALT_ALLOW_SERVER_INFO_COMMAND;
   public static boolean ALT_ALLOW_INSTANCE_COMMAND;
   public static boolean ALT_ALLOW_ITEM_REMAINING_COMMAND;
   public static boolean ALT_ALLOW_MAMMON_SEARCH_COMMAND;
   public static boolean ALT_ALLOW_SERVICES_COMMAND;
   public static boolean ALT_REVIVE_WINDOW_TO_TOWN;
   public static Location ALT_REVIVE_WINDOW_TO_TOWN_LOCATION;
   public static long NONOWNER_ITEM_PICKUP_DELAY;
   public static long NONOWNER_ITEM_PICKUP_DELAY_RAID;
   public static TIntHashSet IGNORE_ITEM_PICKUP_DELAY_MONSTER_IDS;
   public static long IGNORE_ITEM_PICKUP_DELAY;
   public static boolean LOG_CHAT;
   public static boolean LOG_GM;
   public static boolean LOG_ITEM;
   public static boolean LOG_SERVICES;
   public static Map<Integer, PlayerAccess> gmlist = new HashMap<>();
   public static double RATE_XP;
   public static double RATE_SP;
   public static double RATE_OVERHIT;
   public static double RATE_QUESTS_REWARD;
   public static double RATE_QUESTS_ADENA_REWARD;
   public static double RATE_QUESTS_REWARD_EXP_SP;
   public static double RATE_QUESTS_DROP;
   public static double RATE_CLAN_REP_SCORE;
   public static int RATE_CLAN_REP_SCORE_MAX_AFFECTED;
   public static boolean ALT_MULTI_DROP;
   public static double RATE_DROP_ADENA;
   public static double RATE_DROP_ITEMS;
   public static double RATE_DROP_SEAL_STONES;
   public static double RATE_DROP_HERBS;
   public static double RATE_DROP_RAIDBOSS;
   public static double RATE_RAIDBOSS_XP;
   public static double RATE_RAIDBOSS_SP;
   public static double RATE_DROP_SPOIL;
   public static int[] NO_RATE_ITEMS;
   public static int[] NO_DROP_ITEMS;
   public static int[] NO_DROP_ITEMS_FOR_SWEEP;
   public static int[] ALLOW_ONLY_THESE_DROP_ITEMS_ID;
   public static boolean NO_RATE_EQUIPMENT;
   public static boolean NO_RATE_KEY_MATERIAL;
   public static boolean NO_RATE_RECIPES;
   public static double RATE_DROP_SIEGE_GUARD;
   public static double RATE_MANOR;
   public static double RATE_FISH_DROP_COUNT;
   public static boolean RATE_PARTY_MIN;
   public static int SKILL_COST_RATE;
   public static int RATE_MOB_SPAWN;
   public static int RATE_MOB_SPAWN_MIN_LEVEL;
   public static int RATE_MOB_SPAWN_MAX_LEVEL;
   public static boolean KARMA_DROP_GM;
   public static boolean KARMA_NEEDED_TO_DROP;
   public static int ITEM_ANTIDROP_FROM_PK;
   public static long SERVICES_PK_KILL_BONUS_INTERVAL;
   public static boolean SERVICES_PK_KILL_BONUS_ENABLE;
   public static int[] SERVICES_PVP_KILL_BONUS_REWARD_ITEM;
   public static int[] SERVICES_PVP_KILL_BONUS_REWARD_COUNT;
   public static float[] SERVICES_PVP_KILL_BONUS_REWARD_CHANCE;
   public static boolean SERVICES_PVP_KILL_BONUS_ENABLE;
   public static int[] SERVICES_PK_KILL_BONUS_REWARD_COUNT;
   public static int[] SERVICES_PK_KILL_BONUS_REWARD_ITEM;
   public static float[] SERVICES_PK_KILL_BONUS_REWARD_CHANCE;
   public static boolean SERVICES_PK_PVP_BONUS_TIE_IF_SAME_IP;
   public static boolean SERVICES_PK_PVP_BONUS_TIE_IF_SAME_HWID;
   public static int PVP_COUNT_TO_CHAT;
   public static boolean PVP_HERO_CHAT_SYSTEM;
   public static boolean PVP_INCREASE_SAME_IP_CHECK;
   public static boolean PVP_INCREASE_SAME_HWID_CHECK;
   public static boolean ALLOW_DUEL;
   public static boolean ENABLE_WORLD_CHAT;
   public static int WORLD_CHAT_MESSAGE_HANDLER;
   public static String WORLD_CHAT_MESSAGE_APPEND;
   public static int WORLD_CHAT_MESSAGE_COUNT;
   public static int WORLD_CHAT_MIN_LEVEL;
   public static boolean WORLD_CHAT_FOR_PREMIUM_ONLY;
   public static long WORLD_CHAT_INTERVAL;
   public static int WORLD_CHAT_BONUS_ITEM;
   public static int WORLD_CHAT_BONUS_ITEM_CHAT_POINTS;
   public static ChatType WORLD_CHAT_MESSAGE_COLOR;
   public static int KARMA_DROP_ITEM_LIMIT;
   public static int KARMA_RANDOM_DROP_LOCATION_LIMIT;
   public static double KARMA_DROPCHANCE_BASE;
   public static double KARMA_DROPCHANCE_MOD;
   public static double NORMAL_DROPCHANCE_BASE;
   public static int DROPCHANCE_EQUIPMENT;
   public static int DROPCHANCE_EQUIPPED_WEAPON;
   public static int DROPCHANCE_ITEM;
   public static int PVP_POINTS_AMOUNT_ADD;
   public static boolean FUN_ZONE_PVP_COUNT;
   public static boolean BATTLE_ZONE_PVP_COUNT;
   public static boolean SIEGE_ZONE_PVP_COUNT;
   public static boolean FUN_ZONE_FLAG_ON_ENTER;
   public static int AUTODESTROY_ITEM_AFTER;
   public static int AUTODESTROY_PLAYER_ITEM_AFTER;
   public static int DELETE_DAYS;
   public static int PURGE_BYPASS_TASK_FREQUENCY;
   public static File DATAPACK_ROOT;
   public static double CLANHALL_BUFFTIME_MODIFIER;
   public static double SONGDANCETIME_MODIFIER;
   public static int CLNHALL_REWARD_CYCLE;
   public static int CLAN_MIN_LEVEL_FOR_BID;
   public static Map<Integer, Integer> SKILL_DURATION_MOD = new ConcurrentHashMap<>();
   public static boolean SKILL_DURATION_MOD_AT_OLY;
   public static double MAXLOAD_MODIFIER;
   public static Map<Integer, Float> GATEKEEPER_MODIFIER;
   public static boolean ALT_IMPROVED_PETS_LIMITED_USE;
   public static int GATEKEEPER_FREE;
   public static int KRUMA_TELEPORT_MAX_LEVEL;
   public static double ALT_CHAMPION_CHANCE1;
   public static double ALT_CHAMPION_CHANCE2;
   public static boolean ALT_CHAMPION_CAN_BE_AGGRO;
   public static boolean ALT_CHAMPION_CAN_BE_SOCIAL;
   public static boolean ALT_CHAMPION_CAN_BE_SPECIAL_MONSTERS;
   public static boolean ALT_CHAMPION_CAN_BE_SEVEN_SIGN_MONSTERS;
   public static int ALT_CHAMPION_TOP_LEVEL;
   public static int ALT_CHAMPION_MIN_LEVEL;
   public static Set<Integer> ALT_CHAMPIONS_LIST;
   public static Set<Integer> ALT_IGNORE_CHAMPIONS_LIST;
   public static Map<Integer, int[]> ALT_CHAMPION_DROP_ITEM_IDS = new HashMap<>();
   public static Map<Integer, float[]> ALT_CHAMPION_DROP_CHANCES = new HashMap<>();
   public static Map<Integer, long[]> ALT_CHAMPION_DROP_COUNTS = new HashMap<>();
   public static int ALT_CHAMPION_DROP_LEVEL_DIFF;
   public static int[] ALT_INITIAL_QUESTS;
   public static boolean ALLOW_DISCARDITEM;
   public static boolean ALLOW_MAIL;
   public static boolean ALLOW_WAREHOUSE;
   public static boolean ALLOW_WATER;
   public static boolean ALLOW_CURSED_WEAPONS;
   public static boolean DROP_CURSED_WEAPONS_ON_KICK;
   public static boolean ALLOW_NOBLE_TP_TO_ALL;
   public static int SWIMING_SPEED;
   public static int MIN_PROTOCOL_REVISION;
   public static int MAX_PROTOCOL_REVISION;
   public static String ALT_CG_MODULE;
   public static boolean PFILTER_ENABLED;
   public static int MIN_NPC_ANIMATION;
   public static int MAX_NPC_ANIMATION;
   public static String DEFAULT_LANG;
   public static String RESTART_AT_TIME;
   public static int GAME_SERVER_LOGIN_PORT;
   public static boolean GAME_SERVER_LOGIN_CRYPT;
   public static String GAME_SERVER_LOGIN_HOST;
   public static String INTERNAL_HOSTNAME;
   public static String EXTERNAL_HOSTNAME;
   public static boolean SERVER_SIDE_NPC_NAME;
   public static boolean SERVER_SIDE_NPC_TITLE;
   public static boolean SERVER_SIDE_MONSTER_LEVEL_TITLE;
   public static boolean TEST_SERVER_HELPER_ENABLED;
   public static String CLASS_MASTERS_CLASSES;
   public static boolean ALLOW_EVENT_GATEKEEPER;
   public static boolean ALLOW_GLOBAL_GK;
   public static boolean ALLOW_BUFFER;
   public static boolean ALLOW_GMSHOP;
   public static boolean ALLOW_AUCTIONER;
   public static boolean ALLOW_GLOBAL_SERVICES;
   public static boolean ALLOW_PVP_EVENT_MANAGER;
   public static boolean ALLOW_TREASURE_BOX;
   public static boolean SERVICE_FEATHER_REVIVE_ENABLE;
   public static boolean SERVICE_DISABLE_FEATHER_ON_SIEGES_AND_EPIC;
   public static int SERVICE_FEATHER_ITEM_ID;
   public static boolean COMMAND_CLASS_MASTER_ENABLED;
   public static String COMMAND_CLASS_MASTER_CLASSES;
   public static int COMMAND_CLASS_POPUP_LIMIT;
   public static String[] COMMAND_CLASS_MASTER_VOICE_COMMANDS;
   public static boolean PAWNSHOP_ENABLED;
   public static ItemTemplate.ItemClass[] PAWNSHOP_ITEMS_CLASSES;
   public static int PAWNSHOP_MIN_ENCHANT_LEVEL;
   public static boolean PAWNSHOP_ALLOW_SELL_AUGMENTED_ITEMS;
   public static ItemTemplate.Grade PAWNSHOP_MIN_GRADE;
   public static int PAWNSHOP_ITEMS_PER_PAGE;
   public static int[] PAWNSHOP_CURRENCY_ITEM_IDS;
   public static int[] PAWNSHOP_PROHIBITED_ITEM_IDS;
   public static int PAWNSHOP_MIN_QUERY_LENGTH;
   public static int PAWNSHOP_TAX_ITEM_ID;
   public static long PAWNSHOP_TAX_ITEM_COUNT;
   public static int PAWNSHOP_TAX_AUGMENTED_ITEM_ID;
   public static long PAWNSHOP_TAX_AUGMENTED_ITEM_COUNT;
   public static int PAWNSHOP_REFUND_ITEM_ID;
   public static long PAWNSHOP_REFUND_ITEM_COUNT;
   public static boolean PAWNSHOP_PRICE_SORT;
   public static boolean AUGMENTATION_TRANSFER_ENABLE;
   public static int AUGMENTATION_TRANSFER_REQUIRED_ITEM_ID;
   public static int AUGMENTATION_TRANSFER_REQUIRED_ITEM_COUNT;
   public static int AUGMENTATION_TRANSFER_MAX_LIST_LENGTH;
   public static int[] AUGMENTATION_TRANSFER_PROHIBITED_ITEM_IDS;
   public static boolean ITEM_BROKER_ITEM_SEARCH;
   public static long ITEM_BROKER_UPDATE_TIME;
   public static int INVENTORY_MAXIMUM_NO_DWARF;
   public static int INVENTORY_MAXIMUM_DWARF;
   public static boolean DWARF_AUTOMATICALLY_CRYSTALLIZE_ON_ITEM_DELETE;
   public static boolean CRYSTALLIZE_BONUS_AT_ENCHANT;
   public static int INVENTORY_MAXIMUM_GM;
   public static int WAREHOUSE_SLOTS_NO_DWARF;
   public static int WAREHOUSE_SLOTS_DWARF;
   public static int WAREHOUSE_SLOTS_CLAN;
   public static int FREIGHT_SLOTS;
   public static int FOLLOW_ARRIVE_DISTANCE;
   public static boolean SEND_LINEAGE2_WELCOME_MESSAGE;
   public static boolean SEND_SSQ_WELCOME_MESSAGE;
   public static boolean SEND_SSQ_SEAL_STATUS;
   public static double MINIMUM_SPOIL_RATE;
   public static int[] SPOIL_ITEMS_ID_FOR_RATE;
   public static double SPOIL_ITEMS_CHANCE_RATE;
   public static double MANOR_SOWING_BASIC_SUCCESS;
   public static double MANOR_SOWING_ALT_BASIC_SUCCESS;
   public static double MANOR_HARVESTING_BASIC_SUCCESS;
   public static double MANOR_HARVESTING_REWARD_RATE;
   public static int MANOR_DIFF_PLAYER_TARGET;
   public static double MANOR_DIFF_PLAYER_TARGET_PENALTY;
   public static int MANOR_DIFF_SEED_TARGET;
   public static double MANOR_DIFF_SEED_TARGET_PENALTY;
   public static int KARMA_MIN_INCREASE;
   public static int KARMA_STATIC_INCREASES;
   public static int KARMA_STATIC_LOST_ON_DEATH;
   public static int RATE_KARMA_LOST_STATIC;
   public static int KARMA_RATE_LOST;
   public static int MIN_PK_TO_ITEMS_DROP;
   public static boolean DROP_ITEMS_ON_DIE;
   public static boolean DROP_ITEMS_AUGMENTED;
   public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<>();
   public static int PVP_TIME;
   public static int PVP_BLINKING_UNFLAG_TIME;
   public static int PVP_FLAG_ON_UN_FLAG_TIME;
   public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
   public static boolean ALT_CAN_ATTACK_NPC_AT_PEACE_ZONE;
   public static int ENCHANT_MAX;
   public static int ARMOR_ENCHANT_6_SKILL;
   public static int ARMOR_ENCHANT_10_SKILL;
   public static int ENCHANT_ATTRIBUTE_STONE_CHANCE;
   public static int ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE;
   public static int ENCHANT_ATTRIBUTE_ENERGY_CHANCE;
   public static int ARMOR_OVERENCHANT_HPBONUS_LIMIT;
   public static boolean SHOW_ENCHANT_EFFECT_RESULT;
   public static boolean SHOW_ENCHANT_EFFECT_RESULT_EVERY_NEXT_SUCCESS;
   public static int WEAPON_FIRST_ENCHANT_EFFECT_LEVEL;
   public static int WEAPON_SECOND_ENCHANT_EFFECT_LEVEL;
   public static int ARMOR_ENCHANT_EFFECT_LEVEL;
   public static boolean REGEN_SIT_WAIT;
   public static double RATE_RAID_REGEN;
   public static double RATE_RAID_DEFENSE;
   public static double RATE_RAID_ATTACK;
   public static int RATE_MOD_MIN_LEVEL_LIMIT;
   public static int RATE_MOD_MAX_LEVEL_LIMIT;
   public static double RATE_EPIC_DEFENSE;
   public static double RATE_EPIC_ATTACK;
   public static int RAID_MAX_LEVEL_DIFF;
   public static boolean PARALIZE_ON_RAID_DIFF;
   public static double ALT_PK_DEATH_RATE;
   public static int STARTING_ADENA;
   public static int[] STARTING_ITEMS_MAGE;
   public static int[] STARTING_ITEMS_FIGHTER;
   public static boolean ALT_CONSUME_ARROWS;
   public static boolean ALT_CONSUME_SOULSHOTS;
   public static boolean ALT_CONSUME_SPIRITSHOTS;
   public static boolean ALT_CONSUME_BEAST_SHOTS;
   public static boolean ALT_PA_CONSUME_ARROWS;
   public static boolean ALT_PA_CONSUME_SOULSHOTS;
   public static boolean ALT_PA_CONSUME_SPIRITSHOTS;
   public static boolean ALT_PA_CONSUME_BEAST_SHOTS;
   public static boolean DEEPBLUE_DROP_RULES;
   public static int DEEPBLUE_DROP_MAXDIFF;
   public static int DEEPBLUE_DROP_RAID_MAXDIFF;
   public static int EXP_SP_DIFF_LIMIT;
   public static int THRESHOLD_LEVEL_DIFF;
   public static int PARTY_PENALTY_EXP_SP_MAX_LEVEL;
   public static int PARTY_PENALTY_EXP_SP_MIN_LEVEL;
   public static boolean UNSTUCK_SKILL;
   public static boolean BLOCK_BUFF_SKILL;
   public static boolean NOBLES_BUFF_SKILL;
   public static int[] ADDITIONALS_SKILLS;
   public static IntSet BLOCK_BUFF_EXCLUDE;
   public static boolean IS_TELNET_ENABLED;
   public static String TELNET_DEFAULT_ENCODING;
   public static String TELNET_PASSWORD;
   public static String TELNET_HOSTNAME;
   public static int TELNET_PORT;
   public static double RESPAWN_RESTORE_CP;
   public static double RESPAWN_RESTORE_HP;
   public static double RESPAWN_RESTORE_MP;
   public static int MAX_PVTSTORE_SLOTS_DWARF;
   public static int MAX_PVTSTORE_SLOTS_DWARF_FIRST_JOB;
   public static int MAX_PVTSTORE_SLOTS_OTHER;
   public static int MAX_PVTSTORE_SLOTS_OTHER_FIRST_JOB;
   public static int MAX_PVTCRAFT_SLOTS;
   public static boolean SENDSTATUS_TRADE_JUST_OFFLINE;
   public static double SENDSTATUS_TRADE_MOD;
   public static double MUL_PLAYERS_ONLINE;
   public static boolean ALLOW_CH_DOOR_OPEN_ON_CLICK;
   public static boolean ALT_CH_ALL_BUFFS;
   public static boolean ALT_CH_ALLOW_1H_BUFFS;
   public static boolean ALT_CH_SIMPLE_DIALOG;
   public static int CH_BID_CURRENCY_ITEM_ID;
   public static double RESIDENCE_LEASE_FUNC_MULTIPLIER;
   public static double RESIDENCE_LEASE_MULTIPLIER;
   public static TIntHashSet CH_DISPLAY_IDS;
   public static boolean ACCEPT_ALTERNATE_ID;
   public static int REQUEST_ID;
   public static boolean ANNOUNCE_MAMMON_SPAWN;
   public static int GM_NAME_COLOUR;
   public static boolean GM_HERO_AURA;
   public static int NORMAL_NAME_COLOUR;
   public static int CLANLEADER_NAME_COLOUR;
   public static boolean SHOW_HTML_WELCOME;
   public static boolean CUSTOM_DISPELL_EFFECTS;
   public static boolean CUSTOM_DISPELL_EFFECTS_ON_OLYMPIAD;
   public static boolean CUSTOM_DISPELL_MUSIC_EFFECTS;
   public static boolean CUSTOM_PATCH_OPTIONS_ENABLE;
   public static boolean CUSTOM_URL_COMMAND_SERVICE;
   public static String CUSTOM_PATCH_DONATE_URL;
   public static String CUSTOM_PATCH_VK_URL;
   public static String CUSTOM_PATCH_FACEBOOK_URL;
   public static boolean CUSTOM_SEND_MESSAGE_ON_PA_ACCEPT;
   public static int AI_TASK_MANAGER_COUNT;
   public static long AI_TASK_ATTACK_DELAY;
   public static long AI_TASK_ACTIVE_DELAY;
   public static boolean BLOCK_ACTIVE_TASKS;
   public static boolean ALWAYS_TELEPORT_HOME;
   public static boolean RND_WALK;
   public static int RND_WALK_RATE;
   public static int RND_ANIMATION_RATE;
   public static int AGGRO_CHECK_INTERVAL;
   public static long NONAGGRO_TIME_ONTELEPORT;
   public static long NONAGGRO_TIME_ONLOGIN;
   public static long GLOBAL_AGGRO_CHECK_INTERVAL;
   public static long FACTION_NOTIFY_INTERVAL;
   public static boolean ALT_TELEPORT_PROTECTION;
   public static long ALT_TELEPORT_PROTECTION_TIME;
   public static boolean ALT_SPREADING_AFTER_TELEPORT;
   public static boolean NO_CARRIER_PROTECTION;
   public static long NO_CARRIER_PROTECTION_TIME;
   public static int MAX_DRIFT_RANGE;
   public static int MAX_PURSUE_RANGE;
   public static int MAX_PURSUE_UNDERGROUND_RANGE;
   public static int MAX_PURSUE_RANGE_RAID;
   public static boolean RESTORE_HP_MP_ON_TELEPORT_HOME;
   public static int[] RESTORE_HP_MP_ON_EXCLUDED_IDS;
   public static boolean ALT_DEATH_PENALTY;
   public static boolean ALLOW_DEATH_PENALTY_C5;
   public static int ALT_DEATH_PENALTY_C5_CHANCE;
   public static boolean ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY;
   public static int ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
   public static int ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
   public static int ALT_MUSIC_COST_GUARD_INTERVAL;
   public static boolean ALT_ADDITIONAL_DANCE_SONG_MANA_CONSUME;
   public static boolean DISABLE_GRADE_PENALTY;
   public static boolean HIDE_GM_STATUS;
   public static boolean SHOW_GM_LOGIN;
   public static boolean SAVE_GM_EFFECTS;
   public static boolean AUTO_LEARN_SKILLS;
   public static boolean AUTO_LEARN_FORGOTTEN_SKILLS;
   public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
   public static int MOVE_TASK_QUANTUM_PC;
   public static int MOVE_TASK_QUANTUM_NPC;
   public static boolean MOVE_OFFLOAD_MTL_PC;
   public static boolean MOVE_TO_TOWN_ON_FAIL_VALIDATE;
   public static int MOVE_TO_TOWN_ON_FAIL_TRY_COUNT;
   public static int ATTACK_PACKET_DELAY;
   public static int ATTACK_END_DELAY;
   public static int PICKUP_PACKET_DELAY;
   public static int USE_ITEM_PACKET_DELAY;
   public static int UNKNOWN_PACKET_AMOUNT_LIMIT;
   public static int FAIL_PACKET_AMOUNT_LIMIT;
   public static int RESEND_MSU_AFTER_TELEPORT;
   public static boolean DAMAGE_FROM_FALLING;
   public static boolean COMMUNITYBOARD_ENABLED;
   public static String BBS_DEFAULT;
   public static boolean BBS_PEACE_ONLY;
   public static boolean BBS_BUFFER_PEACE_ONLY;
   public static boolean ALLOW_WEDDING;
   public static int WEDDING_PRICE;
   public static int WEDDING_ITEM_ID_PRICE;
   public static boolean WEDDING_PUNISH_INFIDELITY;
   public static boolean WEDDING_TELEPORT;
   public static int WEDDING_TELEPORT_PRICE;
   public static int WEDDING_TELEPORT_INTERVAL;
   public static boolean WEDDING_SAMESEX;
   public static boolean WEDDING_FORMALWEAR;
   public static int WEDDING_DIVORCE_COSTS;
   public static boolean WEDDING_GIVE_SALVATION_BOW;
   public static boolean WEDDING_USE_COLOR;
   public static int WEDDING_NORMAL_COLOR;
   public static int WEDDING_GAY_COLOR;
   public static int WEDDING_LESBIAN_COLOR;
   public static boolean WEDDING_ANNOUNCE;
   public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
   public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
   public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
   public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
   public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
   public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
   public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;
   public static boolean ALT_HBCE_FAIR_PLAY;
   public static int ALT_PET_INVENTORY_LIMIT;
   public static boolean ALT_SET_TITLE_OWNER_TO_SUMMON;
   public static boolean ALT_SAVE_SERVITOR_BUFF;
   public static int LIM_PATK;
   public static int LIM_MATK;
   public static int LIM_PDEF;
   public static int LIM_MDEF;
   public static int LIM_MATK_SPD;
   public static int LIM_PATK_SPD;
   public static int LIM_CRIT_DAM;
   public static int LIM_CRIT;
   public static int LIM_MCRIT;
   public static double MCRITICAL_CRIT_POWER;
   public static int LIM_ACCURACY;
   public static int LIM_EVASION;
   public static int LIM_MOVE;
   public static int LIM_MOVE_GM;
   public static int LIM_MAX_CP;
   public static int LIM_MAX_HP;
   public static int LIM_MAX_MP;
   public static int LIM_FAME;
   public static double MCRITICAL_BASE_STAT;
   public static boolean MDAM_CRIT_POSSIBLE;
   public static boolean HEAL_CRIT_POSSIBLE;
   public static boolean ALT_DISABLE_MAGICFAILURES;
   public static int MAGIC_FAIL_LEVEL_MOD;
   public static double BLOW_RATE_CHANCE_LIMIT;
   public static double BLOW_SKILLS_MAX_CHANCE_LIMIT;
   public static int LIMIT_HENNA_INT;
   public static int LIMIT_HENNA_STR;
   public static int LIMIT_HENNA_MEN;
   public static int LIMIT_HENNA_CON;
   public static int LIMIT_HENNA_WIT;
   public static int LIMIT_HENNA_DEX;
   public static boolean CALC_EFFECT_TIME_YIELD_AND_RESIST;
   public static boolean FIRST_CONDITION_CHECK_ON_DEATH_TARGET;
   public static int COUNTERATTACK_MAX_SKILL_RANGE;
   public static int MIN_ATK_DELAY;
   public static boolean AUTO_ATTACK_ON_WEAPON_CHANGE;
   public static double ALT_NPC_PATK_MODIFIER;
   public static double ALT_NPC_MATK_MODIFIER;
   public static double ALT_NPC_MAXHP_MODIFIER;
   public static double ALT_NPC_MAXMP_MODIFIER;
   public static double ALT_NPC_PDEF_MODIFIER;
   public static double ALT_NPC_MDEF_MODIFIER;
   public static int ALT_NPC_MIN_LEVEL_MODIFIER;
   public static int ALT_NPC_MAX_LEVEL_MODIFIER;
   public static double ALT_EPIC_BOSS_PATK_MODIFIER;
   public static double ALT_EPIC_BOSS_MATK_MODIFIER;
   public static double ALT_EPIC_BOSS_MAXHP_MODIFIER;
   public static double ALT_EPIC_BOSS_MAXMP_MODIFIER;
   public static double ALT_EPIC_BOSS_PDEF_MODIFIER;
   public static double ALT_EPIC_BOSS_MDEF_MODIFIER;
   public static int ALT_EPIC_BOSS_MIN_LEVEL_MODIFIER;
   public static int ALT_EPIC_BOSS_MAX_LEVEL_MODIFIER;
   public static double ALT_RAID_BOSS_PATK_MODIFIER;
   public static double ALT_RAID_BOSS_MATK_MODIFIER;
   public static double ALT_RAID_BOSS_MAXHP_MODIFIER;
   public static double ALT_RAID_BOSS_MAXMP_MODIFIER;
   public static double ALT_RAID_BOSS_PDEF_MODIFIER;
   public static double ALT_RAID_BOSS_MDEF_MODIFIER;
   public static int ALT_RAID_BOSS_MIN_LEVEL_MODIFIER;
   public static int ALT_RAID_BOSS_MAX_LEVEL_MODIFIER;
   public static int ALT_NPC_LIM_MCRIT;
   public static int FESTIVAL_MIN_PARTY_SIZE;
   public static double FESTIVAL_RATE_PRICE;
   public static int RIFT_MIN_PARTY_SIZE;
   public static int RIFT_SPAWN_DELAY;
   public static int RIFT_MAX_JUMPS;
   public static int RIFT_AUTO_JUMPS_TIME;
   public static int RIFT_AUTO_JUMPS_TIME_RAND;
   public static int RIFT_ENTER_COST_RECRUIT;
   public static int RIFT_ENTER_COST_SOLDIER;
   public static int RIFT_ENTER_COST_OFFICER;
   public static int RIFT_ENTER_COST_CAPTAIN;
   public static int RIFT_ENTER_COST_COMMANDER;
   public static int RIFT_ENTER_COST_HERO;
   public static int RIFT_BOSS_ROOM_CHANCE;
   public static boolean FOUR_SEPULCHER_ENABLE;
   public static boolean FOUR_DEBUG_SEPULCHER_ENABLE;
   public static int FOUR_SEPULCHER_MIN_PARTY_MEMBERS;
   public static int FOUR_SEPULCHER_NEW_CYCLE_MIN;
   public static boolean ALLOW_TALK_WHILE_SITTING;
   public static boolean PARTY_LEADER_ONLY_CAN_INVITE;
   public static long EXPELLED_MEMBER_PENALTY;
   public static long NEW_CLAN_CREATE_PENALTY;
   public static long CLAN_LEAVE_TIME_PERNALTY;
   public static long CLAN_DISBAND_TIME;
   public static long CLAN_DISBAND_PENALTY;
   public static boolean ALLY_ALLOW_BUFF_DEBUFFS;
   public static int ALT_NPC_CLAN;
   public static boolean ALLOW_TEMPORARILY_ALLY_ON_FIRST_SIEGE;
   public static long LEAVED_ALLY_PENALTY;
   public static long DISSOLVED_ALLY_PENALTY;
   public static boolean ALLOW_CLANSKILLS;
   public static int CLAN_INIT_LEVEL;
   public static int CLAN_REPUTATION_BONUS_ON_CREATE;
   public static boolean FULL_CLAN_SKILLS_ON_CREATE;
   public static int CHARACTER_MIN_LEVEL_FOR_CLAN_CREATE;
   public static boolean CLAN_LEADER_CHANGE_METHOD;
   public static int LIMIT_CLAN_LEVEL0;
   public static int LIMIT_CLAN_LEVEL1;
   public static int LIMIT_CLAN_LEVEL2;
   public static int LIMIT_CLAN_LEVEL3;
   public static int LIMIT_CLAN_LEVEL_4_AND_HIGH;
   public static int MIN_CLAN_LEVEL_FOR_DECLARED_WAR;
   public static int MIN_CLAN_MEMBER_FOR_DECLARED_WAR;
   public static int CRP_REWARD_ON_WAR_KILL_OVER_LEVEL;
   public static int CRP_REWARD_ON_WAR_KILL;
   public static int LIMIT_CLAN_HIGH_UNITS;
   public static int LIMIT_CLAN_LOW_UNITS;
   public static int LIMIT_CLAN_ACADEMY;
   public static int CLAN_FIRST_LEVEL_SP;
   public static int CLAN_FIRST_LEVEL_ADENA;
   public static int CLAN_SECOND_LEVEL_SP;
   public static int CLAN_SECOND_LEVEL_ADENA;
   public static int CLAN_THIRD_LEVEL_SP;
   public static int CLAN_FOUR_LEVEL_SP;
   public static int CLAN_FIVE_LEVEL_SP;
   public static int CLAN_SIX_LEVEL_CLAN_REPUTATION;
   public static int CLAN_SIX_LEVEL_CLAN_MEMBER_COUNT;
   public static int CLAN_SEVEN_LEVEL_CLAN_REPUTATION;
   public static int CLAN_SEVEN_LEVEL_CLAN_MEMBER_COUNT;
   public static int CLAN_EIGHT_LEVEL_CLAN_REPUTATION;
   public static int CLAN_EIGHT_LEVEL_CLAN_MEMBER_COUNT;
   public static boolean CHECK_CLAN_RANK_ON_COMMAND_CHANNEL_CREATE;
   public static int COMMAND_CHANNEL_PARY_COUNT_LIMIT;
   public static int MIN_CLAN_LEVEL_FOR_REPUTATION;
   public static int MIN_CLAN_LEVEL_FOR_SIEGE_REGISTRATION;
   public static int MAX_ALLY_SIZE;
   public static int SIEGE_CLAN_REPUTATION_REWARD;
   public static int SIEGE_CLAN_REPUTATION_DEFEND_REWARD;
   public static int SIEGE_CLAN_REPUTATION_DEFEND_CONSUME;
   public static boolean ALLOW_MANOR;
   public static int MANOR_REFRESH_TIME;
   public static int MANOR_REFRESH_MIN;
   public static int MANOR_APPROVE_TIME;
   public static int MANOR_APPROVE_MIN;
   public static int MANOR_MAINTENANCE_PERIOD;
   public static double EVENT_CofferOfShadowsPriceRate;
   public static double EVENT_APIL_FOOLS_DROP_CHANCE;
   public static int EVENT_LastHeroItemID;
   public static double EVENT_LastHeroItemCOUNT;
   public static int EVENT_LastHeroTime;
   public static boolean EVENT_LastHeroRate;
   public static double EVENT_LastHeroItemCOUNTFinal;
   public static boolean EVENT_LastHeroRateFinal;
   public static int EVENT_LastHeroChanceToStart;
   public static int EVENT_TvTItemID;
   public static double EVENT_TvTItemCOUNT;
   public static int EVENT_TvTTime;
   public static boolean EVENT_TvT_rate;
   public static int EVENT_TvTChanceToStart;
   public static int EVENT_CtFItemID;
   public static double EVENT_CtFItemCOUNT;
   public static int EVENT_CtFTime;
   public static boolean EVENT_CtF_rate;
   public static int EVENT_CtFChanceToStart;
   public static String EVENT_DropEvent_Items;
   public static String EVENT_DropEvent_PartyItems;
   public static boolean EVENT_DropEvent_PartyItems_Distribute_Random;
   public static boolean EVENT_DropEvent_Rate;
   public static int Event_DropEvent_Level_Penalty;
   public static boolean Event_DropEvent_Check_Hwid;
   public static double EVENT_TFH_POLLEN_CHANCE;
   public static double EVENT_GLITTMEDAL_NORMAL_CHANCE;
   public static double EVENT_GLITTMEDAL_GLIT_CHANCE;
   public static double EVENT_L2DAY_LETTER_CHANCE;
   public static double EVENT_CHANGE_OF_HEART_CHANCE;
   public static double EVENT_CHRISTMAS_CHANCE;
   public static double EVENT_TRICK_OF_TRANS_CHANCE;
   public static double EVENT_MARCH8_DROP_CHANCE;
   public static double EVENT_MARCH8_PRICE_RATE;
   public static int GVG_REWARD_ID;
   public static long GVG_REWARD_AMOUNT;
   public static long EVENT_SAVING_SNOWMAN_LOTERY_PRICE;
   public static int EVENT_SAVING_SNOWMAN_LOTERY_CURENCY;
   public static int EVENT_SAVING_SNOWMAN_REWARDER_CHANCE;
   public static int[] EVENT_StraightHands_Items;
   public static String[] EVENT_FinderHostageStartTime;
   public static int EVENT_FINDER_REWARD_ID;
   public static int EVENT_FINDER_ITEM_COUNT;
   public static String EVENT_GVG_START_TIME;
   public static int EVENT_GVG_MIN_LEVEL;
   public static int EVENT_GVG_MAX_LEVEL;
   public static int EVENT_GVG_GROUPS_LIMIT;
   public static int EVENT_GVG_MIN_PARTY_SIZE;
   public static long EVENT_GVG_REG_TIME;
   public static int EVENT_PUMPKIN_GHOST_ID;
   public static int EVENT_SKOOLDIE_REWARDER;
   public static int EVENT_PUMPKIN_DELAY;
   public static int EVENT_PUMPKIN_GHOST_SHOW_TIME;
   public static int EVENT_SKOOLDIE_TIME;
   public static int EVENT_HALLOWEEN_CANDY;
   public static int EVENT_HALLOWEEN_CANDY_ITEM_COUNT_NEEDED;
   public static int EVENT_HALLOWEEN_TOY_CHEST_REWARD_AMOUNT;
   public static int EVENT_HALLOWEEN_TOY_CHEST;
   public static int EVENT_PUMPKIN_DROP_CHANCE;
   public static int[] EVENT_PUMPKIN_DROP_ITEMS;
   public static int[] PVP_EVENTS_RESTRICTED_SKILL_IDS;
   public static boolean PVP_EVENTS_RESTRICTED_CLAN_SKILLS;
   public static boolean PVP_EVENTS_RESTRICT_ENCHANT;
   public static int PVP_EVENTS_RESTRICT_ENCHANT_ARMOR_LEVEL;
   public static int PVP_EVENTS_RESTRICT_ENCHANT_WEAPON_MAGE;
   public static int PVP_EVENTS_RESTRICT_ENCHANT_WEAPON_PHYS;
   public static int PVP_EVENTS_RESTRICT_ENCHANT_ACCESSORY;
   public static List<Pair<Integer, Integer>> PVP_EVENTS_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> PVP_EVENTS_MAGE_BUFF;
   public static Set<String> ENCHANT_LIMIT_ZONE_NAMES;
   public static int ENCHANT_LIMIT_ZONE_ARMOR_LEVEL;
   public static int ENCHANT_LIMIT_ZONE_WEAPON_MAGE;
   public static int ENCHANT_LIMIT_ZONE_WEAPON_PHYS;
   public static int ENCHANT_LIMIT_ZONE_ACCESSORY;
   public static boolean SERVICES_TOP_CLANS_STATISTIC;
   public static int SERVICES_TOP_CLANS_STATISTIC_LIST;
   public static Map<Integer, Integer> EVENT_CLAN_CASTLE_POINTS;
   public static int EVENT_CLAN_HERO_POINTS;
   public static Map<Integer, Integer> EVENT_CLAN_RAID_BOSS_POINTS;
   public static boolean SERVICES_TOP_CLANS_STATISTIC_ANNOUNCE;
   public static boolean SERVICES_NO_TRADE_ONLY_OFFLINE;
   public static double SERVICES_TRADE_TAX;
   public static double SERVICES_OFFSHORE_TRADE_TAX;
   public static boolean SERVICES_OFFSHORE_NO_CASTLE_TAX;
   public static boolean SERVICES_TRADE_TAX_ONLY_OFFLINE;
   public static boolean SERVICES_TRADE_ONLY_FAR;
   public static int SERVICES_TRADE_RADIUS;
   public static int SERVICES_TRADE_MIN_LEVEL;
   public static boolean SERVICES_CLANSKILL_SELL_ENABLED;
   public static int SERVICES_CLAN_SKILL_SELL_ITEM;
   public static int SERVICES_CLAN_SKILL_SELL_PRICE;
   public static int SERVICES_CLANSKIL_SELL_MIN_LEVEL;
   public static boolean SERVICES_ENABLE_NO_CARRIER;
   public static int SERVICES_NO_CARRIER_DEFAULT_TIME;
   public static int SERVICES_NO_CARRIER_MAX_TIME;
   public static int SERVICES_NO_CARRIER_MIN_TIME;
   public static int SERVICES_CLAN_MAX_SELL_LEVEL;
   public static boolean SERVICES_HPACP_ENABLE;
   public static boolean SERVICES_HPACP_WORK_IN_PEACE_ZONE;
   public static int SERVICES_HPACP_MIN_PERCENT;
   public static int SERVICES_HPACP_MAX_PERCENT;
   public static int SERVICES_HPACP_DEF_PERCENT;
   public static int[] SERVICES_HPACP_POTIONS_ITEM_IDS;
   public static boolean ENABLE_ACP_ON_CHARACTER_CREATE;
   public static boolean SERVICES_HPACP_AVAILABLE_ONLY_PREMIUM;
   public static boolean SERVICES_ONLINE_COMMAND_ENABLE;
   public static double SERVICE_COMMAND_MULTIPLIER;
   public static boolean SERVICES_BANKING_ENABLED;
   public static int SERVICES_DEPOSIT_ITEM_ID_NEEDED;
   public static int SERVICES_DEPOSIT_ITEM_COUNT_NEEDED;
   public static int SERVICES_DEPOSIT_ITEM_ID_GIVED;
   public static int SERVICES_DEPOSIT_ITEM_COUNT_GIVED;
   public static int SERVICES_WITHDRAW_ITEM_ID_NEEDED;
   public static int SERVICES_WITHDRAW_ITEM_COUNT_NEEDED;
   public static int SERVICES_WITHDRAW_ITEM_ID_GIVED;
   public static int SERVICES_WITHDRAW_ITEM_COUNT_GIVED;
   public static boolean SERVICES_CLAN_SUMMON_COMMAND_ENABLE;
   public static int SERVICES_CLAN_SUMMON_COMMAND_SELL_ITEM;
   public static int SERVICES_CLAN_SUMMON_COMMAND_SELL_PRICE;
   public static int SERVICES_CLAN_SUMMON_COMMAND_SUMMON_CRYSTAL_COUNT;
   public static long REUSE_DELAY_FOR_CLAN_SUMMON;
   public static boolean SERVICES_WHOIAM_COMMAND_ENABLE;
   public static boolean SERVICES_CPACP_ENABLE;
   public static int SERVICES_CPACP_MIN_PERCENT;
   public static int SERVICES_CPACP_MAX_PERCENT;
   public static int SERVICES_CPACP_DEF_PERCENT;
   public static int[] SERVICES_CPACP_POTIONS_ITEM_IDS;
   public static boolean SERVICES_MPACP_ENABLE;
   public static int SERVICES_MPACP_MIN_PERCENT;
   public static int SERVICES_MPACP_MAX_PERCENT;
   public static int SERVICES_MPACP_DEF_PERCENT;
   public static int[] SERVICES_MPACP_POTIONS_ITEM_IDS;
   public static int[] SERVICES_BOSS_STATUS_ADDITIONAL_IDS;
   public static boolean SERVICES_BOSS_STATUS_ENABLE;
   public static String SERVICES_BOSS_STATUS_FORMAT;
   public static int[] SERVICES_RAID_STATUS_ADDITIONAL_IDS;
   public static boolean SERVICES_RAID_STATUS_ENABLE;
   public static String SERVICES_RAID_STATUS_FORMAT;
   public static boolean SERVICES_PVP_PK_STATISTIC;
   public static long PVP_PK_STAT_CACHE_UPDATE_INTERVAL;
   public static int PVP_PK_STAT_RECORD_LIMIT;
   public static boolean SERVICE_AUTO_ANNOUNCE;
   public static boolean ALT_SHOW_SERVER_TIME;
   public static int GEO_X_FIRST;
   public static int GEO_Y_FIRST;
   public static int GEO_X_LAST;
   public static int GEO_Y_LAST;
   public static String GEOFILES_PATTERN;
   public static boolean ALLOW_GEODATA;
   public static boolean ALLOW_FALL_FROM_WALLS;
   public static boolean ALLOW_KEYBOARD_MOVE;
   public static boolean COMPACT_GEO;
   public static int CLIENT_Z_SHIFT;
   public static int MAX_Z_DIFF;
   public static int MIN_LAYER_HEIGHT;
   public static int PATHFIND_BOOST;
   public static boolean PATHFIND_DIAGONAL;
   public static boolean PATH_CLEAN;
   public static int PATHFIND_MAX_Z_DIFF;
   public static long PATHFIND_MAX_TIME;
   public static String PATHFIND_BUFFERS;
   public static boolean ALLOW_PAWN_PATHFIND;
   public static boolean DEBUG;
   public static boolean DEBUG_PKG;
   public static int GAME_POINT_ITEM_ID;
   public static int WEAR_DELAY;
   public static int WEAING_D_GRADE_PRICE;
   public static int WEAING_C_GRADE_PRICE;
   public static int WEAING_B_GRADE_PRICE;
   public static int WEAING_A_GRADE_PRICE;
   public static int WEAING_S_GRADE_PRICE;
   public static int WEAING_NON_GRADE_PRICE;
   public static boolean GOODS_INVENTORY_ENABLED = false;
   public static boolean AUTH_SERVER_GM_ONLY;
   public static boolean AUTH_SERVER_BRACKETS;
   public static boolean AUTH_SERVER_IS_PVP;
   public static int AUTH_SERVER_AGE_LIMIT;
   public static int AUTH_SERVER_SERVER_TYPE;
   public static boolean USE_SECOND_PASSWORD_AUTH;
   public static long SECOND_AUTH_BLOCK_TIME;
   public static int SECOND_AUTH_MAX_TRYS;
   public static int SECOND_AUTH_MIN_LENG;
   public static int SECOND_AUTH_MAX_LENG;
   public static int REC_FLUSH_HOUR;
   public static int REC_FLUSH_MINUTE;
   public static boolean OLY_ENABLED;
   public static boolean OLY_SPECTATION_ALLOWED;
   public static boolean NPC_OLYMPIAD_GAME_ANNOUNCE;
   public static boolean ANNOUNCE_OLYMPIAD_GAME_END;
   public static boolean OLY_RECALC_NEW_SEASON;
   public static boolean OLY_RESTRICT_HWID;
   public static boolean OLY_RESTRICT_IP;
   public static boolean OLY_RESTORE_HPCPMP_ON_START_MATCH;
   public static long OLYMPIAD_COMPETITION_TIME;
   public static int OLYMPIAD_STADIUM_TELEPORT_DELAY;
   public static int OLY_MAX_SPECTATORS_PER_STADIUM;
   public static int[] OLY_RESTRICT_CLASS_IDS;
   public static Config.OlySeasonTimeCalcMode OLY_SEASON_TIME_CALC_MODE;
   public static String OLY_SEASON_START_TIME;
   public static String OLY_SEASON_END_TIME;
   public static String OLY_COMPETITION_START_TIME;
   public static String OLY_COMPETITION_END_TIME;
   public static int OLY_COMPETITION_CUSTOM_START_TIME;
   public static int OLY_COMPETITION_CUSTOM_END_TIME;
   public static String OLY_BONUS_TIME;
   public static String OLY_NOMINATE_TIME;
   public static boolean OLY_STATISTIC_PAST_PERIOD;
   public static Set<Integer> ALT_OLY_COMPETITION_DAYS;
   public static int OLY_MIN_HERO_COMPS;
   public static int OLY_MIN_HERO_WIN;
   public static int OLY_MIN_NOBLE_COMPS;
   public static int OLY_SEASON_START_POINTS;
   public static int[] OLY_POINTS_SETTLEMENT;
   public static TIntObjectHashMap<TIntIntHashMap> OLY_BUFFS;
   public static int OLY_HERO_POINT_BONUS;
   public static int OLY_DEFAULT_POINTS;
   public static int OLY_WBONUS_POINTS;
   public static int OLY_HERO_REWARD_RITEMID;
   public static int[] OLY_VICTORY_RITEMID;
   public static int[] OLY_VICTORY_CFREE_RITEMCNT;
   public static int[] OLY_VICTORY_CBASE_RITEMCNT;
   public static int[] OLY_VICTORY_3TEAM_RITEMCNT;
   public static int[] OLY_VICTORY_RITEMID_CHANCE;
   public static int[] OLY_HERO_CUSTOM_REWARD_ID;
   public static int[] OLY_HERO_CUSTOM_REWARD_AMOUNT;
   public static int[] OLY_HERO_CUSTOM_REWARD_CHANCE;
   public static boolean OLY_PAID_REGISTRATION_FOR_CLASS_FREE;
   public static int OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_ID;
   public static int OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_COUNT;
   public static boolean OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL;
   public static int OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_ID;
   public static int OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_COUNT;
   public static boolean OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE;
   public static int OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_ID;
   public static int OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_COUNT;
   public static double OLY_LOOSE_POINTS_MUL;
   public static int OLY_ITEMS_SETTLEMENT_PER_POINT;
   public static int OLY_MAX_TOTAL_MATCHES;
   public static int OLY_CF_MATCHES;
   public static int OLY_CB_MATCHES;
   public static int OLY_TB_MATCHES;
   public static int OLY_MIN_CF_START;
   public static int OLY_MIN_CB_START;
   public static int OLY_MIN_TB_START;
   public static int OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR;
   public static int OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE;
   public static int OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS;
   public static int OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY;
   public static boolean OLYMPIAD_WINNER_ANNOUCE;
   public static String OLYMPIAD_WINNER_MESSAGE;
   public static boolean OLYMPIAD_REMOVE_SKILL_REUSE_ON_STADIUM;
   public static boolean OLYMPIAD_REMOVE_SKILL_REUSE_FROM_STADIUM;
   public static boolean ADD_CLAN_REPUTATION_POINT_ON_HERO_GAIN;
   public static int AMOUNT_CLAN_REPUTATION_POINT_ON_HERO_GAIN;
   public static boolean OLY_REMOVE_FORCE_BUFFS;
   public static int[] OLY_RESTRICTED_SKILL_IDS;
   public static Map<Integer, QuestRates> QUEST_RATES = new ConcurrentHashMap<>();

   public static void loadServerConfig() {
      ExProperties var0 = load("config/server.properties");
      GAME_SERVER_LOGIN_HOST = var0.getProperty("LoginHost", "127.0.0.1");
      GAME_SERVER_LOGIN_PORT = var0.getProperty("LoginPort", 9013);
      GAME_SERVER_LOGIN_CRYPT = var0.getProperty("LoginUseCrypt", true);
      AUTH_SERVER_AGE_LIMIT = var0.getProperty("ServerAgeLimit", 0);
      AUTH_SERVER_GM_ONLY = var0.getProperty("ServerGMOnly", false);
      AUTH_SERVER_BRACKETS = var0.getProperty("ServerBrackets", false);
      AUTH_SERVER_IS_PVP = var0.getProperty("PvPServer", false);
      USE_SECOND_PASSWORD_AUTH = var0.getProperty("UseSecondPasswordAuth", false);
      SECOND_AUTH_BLOCK_TIME = var0.getProperty("SecondAuthBlockTime", 28800);
      SECOND_AUTH_MAX_TRYS = var0.getProperty("SecondAuthBlockMaxTry", 5);
      SECOND_AUTH_MIN_LENG = var0.getProperty("SecondAuthPinMinLength", 6);
      SECOND_AUTH_MAX_LENG = var0.getProperty("SecondAuthPinMaxLength", 8);

      for (String var4 : var0.getProperty("ServerType", ArrayUtils.EMPTY_STRING_ARRAY)) {
         if (!var4.trim().isEmpty()) {
            ServerType var5 = ServerType.valueOf(var4.toUpperCase());
            AUTH_SERVER_SERVER_TYPE = AUTH_SERVER_SERVER_TYPE | var5.getMask();
         }
      }

      INTERNAL_HOSTNAME = var0.getProperty("InternalHostname", "*");
      EXTERNAL_HOSTNAME = var0.getProperty("ExternalHostname", "*");
      REQUEST_ID = var0.getProperty("RequestServerID", 0);
      ACCEPT_ALTERNATE_ID = var0.getProperty("AcceptAlternateID", true);
      GAMESERVER_HOSTNAME = var0.getProperty("GameserverHostname");
      PORTS_GAME = var0.getProperty("GameserverPort", new int[]{7777});
      HAPROXY_PORT_GAME = var0.getProperty("HAProxyGameserverPort", -1);
      USE_NETTY = var0.getProperty("UseNetty", false);
      NETTY_BOSS_THREADS = var0.getProperty("NettyBossThreads", 1);
      NETTY_WORKER_THREADS = var0.getProperty("NettyWorkerThreads", 0);
      NETTY_SO_BACKLOG = var0.getProperty("NettySoBacklog", 1024);
      NETTY_WRITE_LOW_WATER_MARK = var0.getProperty("NettyWriteLowWaterMark", 262144);
      NETTY_WRITE_HIGH_WATER_MARK = var0.getProperty("NettyWriteHighWaterMark", 1048576);
      USE_DISRUPTOR = var0.getProperty("UseDisruptor", true);
      ENABLE_VISIBLE_OBJECT_RADIUS_FILTER = var0.getProperty("EnableVisibleObjectRadiusFilter", true);
      VISIBLE_OBJECT_RADIUS = Math.max(0, var0.getProperty("VisibleObjectRadius", 3500));
      ENABLE_NPC_INFO_PACING = var0.getProperty("EnableNpcInfoPacing", true);
      NPC_INFO_IMMEDIATE_BURST_LIMIT = Math.max(1, var0.getProperty("NpcInfoImmediateBurstLimit", 8));
      NPC_INFO_PACING_INTERVAL_MS = Math.max(10, var0.getProperty("NpcInfoPacingIntervalMs", 40));
      NPC_INFO_PACED_BATCH_SIZE = Math.max(1, var0.getProperty("NpcInfoPacedBatchSize", 8));
      ENABLE_DELETE_OBJECT_COALESCING = var0.getProperty("EnableDeleteObjectCoalescing", true);
      NPC_INFO_BURST_RESET_MS = Math.max(50, var0.getProperty("NpcInfoBurstResetMs", 150));
      ENABLE_DETERMINISTIC_120HZ_PIPELINE = var0.getProperty("EnableDeterministic120HzPipeline", true);
      ENABLE_FERMATA_EXTENSIONS = var0.getProperty("EnableFermataExtensions", true);
      ENABLE_ASYNC_CHARACTER_UPDATES = var0.getProperty("EnableAsyncCharacterUpdates", true);
      ENABLE_ZSTD_GEODATA_CACHE = var0.getProperty("EnableZstdGeodataCache", true);
      DETERMINISTIC_120HZ_PORT = var0.getProperty("Deterministic120HzPort", 7778);
      ENABLE_DEX_ATTACK_RECOVERY_SCALING = var0.getProperty("EnableDexAttackRecoveryScaling", true);
      DEX_ATTACK_RECOVERY_MIN_FACTOR = var0.getProperty("DexAttackRecoveryMinFactor", 0.50);
      DEX_ATTACK_RECOVERY_MAX_FACTOR = var0.getProperty("DexAttackRecoveryMaxFactor", 1.15);
      DEX_ATTACK_RECOVERY_BASE_DEX = var0.getProperty("DexAttackRecoveryBaseDex", 20);
      DEX_ATTACK_RECOVERY_DEX_DIVISOR = var0.getProperty("DexAttackRecoveryDexDivisor", 45.0);
      DEX_ATTACK_RECOVERY_MAX_BONUS = var0.getProperty("DexAttackRecoveryMaxBonus", 0.45);
      DISRUPTOR_RING_BUFFER_SIZE = var0.getProperty("DisruptorRingBufferSize", 65536);
      ENABLE_NETWORK_JIT_WARMUP = var0.getProperty("EnableNetworkJitWarmup", true);
      DISRUPTOR_WARMUP_ITERATIONS = var0.getProperty("DisruptorWarmupIterations", 100000);
      PHYSICAL_CRITICAL_RATE_CAP = var0.getProperty("PhysicalCriticalRateCap", 500);
      MAGIC_CRITICAL_RATE_CAP = var0.getProperty("MagicCriticalRateCap", 32.0);
      BLOW_RATE_CAP_GENERAL = var0.getProperty("BlowRateCapGeneral", 800);
      BLOW_RATE_CAP_BACKSTAB = var0.getProperty("BlowRateCapBackstab", 1000);
      REQUEST_BYPASS_DEBOUNCE_TIME_MS = var0.getProperty("RequestBypassDebounceTimeMs", 250);
      REQUEST_BYPASS_MAX_PACKETS_PER_SECOND = var0.getProperty("RequestBypassMaxPacketsPerSecond", 15);
      EVERYBODY_HAS_ADMIN_RIGHTS = var0.getProperty("EverybodyHasAdminRights", false);
      HIDE_GM_STATUS = var0.getProperty("HideGMStatus", false);
      SHOW_GM_LOGIN = var0.getProperty("ShowGMLogin", true);
      SAVE_GM_EFFECTS = var0.getProperty("SaveGMEffects", false);
      CNAME_TEMPLATE = var0.getProperty("CnameTemplate", "[A-Za-z0-9А-Яа-я]{2,16}");
      CLAN_NAME_TEMPLATE = var0.getProperty("ClanNameTemplate", "[A-Za-z0-9А-Яа-я]{3,16}");
      CLAN_TITLE_TEMPLATE = var0.getProperty("ClanTitleTemplate", "[A-Za-z0-9А-Яа-я \\p{Punct}]{1,16}");
      ALLY_NAME_TEMPLATE = var0.getProperty("AllyNameTemplate", "[A-Za-z0-9А-Яа-я]{3,16}");
      CNAME_FORBIDDEN_PATTERN = var0.getProperty("ForbiddenCharName", "(adm)|(admin)");
      CNAME_FORBIDDEN_NAMES = var0.getProperty("ForbiddenCharNames", ArrayUtils.EMPTY_STRING_ARRAY);
      GLOBAL_SHOUT = var0.getProperty("GlobalShout", false);
      GLOBAL_SHOUT_MIN_LEVEL = var0.getProperty("GlobalShoutMinLevel", 40);
      GLOBAL_SHOUT_MIN_PVP_COUNT = var0.getProperty("GlobalShoutMinPvPCount", 25);
      GLOBAL_TRADE_CHAT = var0.getProperty("GlobalTradeChat", false);
      GLOBAL_TRADE_CHAT_MIN_LEVEL = var0.getProperty("GlobalTradeChatMinLevel", 40);
      GLOBAL_TRADE_MIN_PVP_COUNT = var0.getProperty("GlobalTradeChatMinPvPCount", 25);
      CHAT_RANGE = var0.getProperty("ChatRange", 1250);
      SHOUT_OFFSET = var0.getProperty("ShoutOffset", 0);
      ENABLE_WORLD_CHAT = var0.getProperty("WorldChat", true);
      WORLD_CHAT_MESSAGE_COUNT = var0.getProperty("WorldChatMsgCountPerDay", 10);
      WORLD_CHAT_MESSAGE_HANDLER = ((String)StringUtils.defaultIfBlank(var0.getProperty("WorldChatMsgHandlerChar", ">"), "\u0000")).charAt(0);
      WORLD_CHAT_MESSAGE_APPEND = var0.getProperty("WorldChatMsgAppendString", "[VIP]");
      WORLD_CHAT_MIN_LEVEL = var0.getProperty("WorldChatMinLevel", 20);
      WORLD_CHAT_FOR_PREMIUM_ONLY = var0.getProperty("WorldChatForPremiumOnly", false);
      WORLD_CHAT_INTERVAL = TimeUnit.SECONDS.toMillis(var0.getProperty("WorldChatUseInterval", 20L));
      WORLD_CHAT_BONUS_ITEM = var0.getProperty("WorldChatBonusItem", 71339);
      WORLD_CHAT_BONUS_ITEM_CHAT_POINTS = var0.getProperty("WorldChatBonusItemPoints", 20);
      WORLD_CHAT_MESSAGE_COLOR = ChatType.valueOf(var0.getProperty("WorldChatMsgColor", "COMMANDCHANNEL_COMMANDER").toUpperCase());
      LOG_CHAT = var0.getProperty("LogChat", false);
      LOG_GM = var0.getProperty("LogGMActions", false);
      LOG_ITEM = var0.getProperty("LogItems", false);
      LOG_SERVICES = var0.getProperty("LogServices", false);
      RATE_XP = var0.getProperty("RateXp", 1.0);
      RATE_SP = var0.getProperty("RateSp", 1.0);
      RATE_QUESTS_REWARD = var0.getProperty("RateQuestsReward", 1.0);
      RATE_QUESTS_ADENA_REWARD = var0.getProperty("RateQuestsAdenaReward", 1.0);
      RATE_QUESTS_REWARD_EXP_SP = var0.getProperty("RateQuestsRewardExpSp", 1.0);
      RATE_QUESTS_DROP = var0.getProperty("RateQuestsDrop", 1.0);
      RATE_CLAN_REP_SCORE = var0.getProperty("RateClanRepScore", 1.0);
      RATE_CLAN_REP_SCORE_MAX_AFFECTED = var0.getProperty("RateClanRepScoreMaxAffected", 2);
      RATE_DROP_ADENA = var0.getProperty("RateDropAdena", 1.0);
      RATE_DROP_ITEMS = var0.getProperty("RateDropItems", 1.0);
      RATE_DROP_HERBS = var0.getProperty("RateDropHerbs", 1.0);
      ALLOW_TREASURE_BOX = var0.getProperty("TreasureBox", true);
      RATE_DROP_RAIDBOSS = var0.getProperty("RateRaidBoss", 1.0);
      RATE_RAIDBOSS_XP = var0.getProperty("RateExpRaidBoss", RATE_XP);
      RATE_RAIDBOSS_SP = var0.getProperty("RateSpRaidBoss", RATE_SP);
      RATE_DROP_SPOIL = var0.getProperty("RateDropSpoil", 1.0);
      RATE_DROP_SEAL_STONES = var0.getProperty("RateDropSealStones", 1.0);
      ALT_MULTI_DROP = var0.getProperty("AltMultiDrop", true);
      NO_RATE_ITEMS = var0.getProperty("NoRateItemIds", new int[]{6660, 6662, 6661, 6659, 6656, 6658, 8191, 6657});
      NO_DROP_ITEMS = var0.getProperty("NoDropItems", new int[0]);
      NO_DROP_ITEMS_FOR_SWEEP = var0.getProperty("NoDropItemsForSweep", new int[0]);
      ALLOW_ONLY_THESE_DROP_ITEMS_ID = var0.getProperty("DropOnlyTheseItemsId", new int[0]);
      NO_RATE_EQUIPMENT = var0.getProperty("NoRateEquipment", true);
      NO_RATE_KEY_MATERIAL = var0.getProperty("NoRateKeyMaterial", true);
      NO_RATE_RECIPES = var0.getProperty("NoRateRecipes", true);
      RATE_DROP_SIEGE_GUARD = var0.getProperty("RateSiegeGuard", 1.0);
      RATE_MANOR = var0.getProperty("RateManor", 1.0);
      RATE_FISH_DROP_COUNT = var0.getProperty("RateFishDropCount", 1.0);
      RATE_PARTY_MIN = var0.getProperty("RatePartyMin", false);
      RATE_MOB_SPAWN = var0.getProperty("RateMobSpawn", 1);
      RATE_MOB_SPAWN_MIN_LEVEL = var0.getProperty("RateMobMinLevel", 1);
      RATE_MOB_SPAWN_MAX_LEVEL = var0.getProperty("RateMobMaxLevel", 100);
      RATE_RAID_REGEN = var0.getProperty("RateRaidRegen", 1.0);
      RATE_RAID_DEFENSE = var0.getProperty("RateRaidDefense", 1.0);
      RATE_RAID_ATTACK = var0.getProperty("RateRaidAttack", 1.0);
      RATE_MOD_MIN_LEVEL_LIMIT = var0.getProperty("RateRaidMinLevelLimit", 1);
      RATE_MOD_MAX_LEVEL_LIMIT = var0.getProperty("RateRaidMaxLevelLimit", 90);
      RATE_EPIC_DEFENSE = var0.getProperty("RateEpicDefense", 1.0);
      RATE_EPIC_ATTACK = var0.getProperty("RateEpicAttack", 1.0);
      RAID_MAX_LEVEL_DIFF = var0.getProperty("RaidMaxLevelDiff", 8);
      PARALIZE_ON_RAID_DIFF = var0.getProperty("ParalizeOnRaidLevelDiff", true);
      SKILL_COST_RATE = var0.getProperty("SkillCostRate", 1);
      AUTODESTROY_ITEM_AFTER = var0.getProperty("AutoDestroyDroppedItemAfter", 0);
      AUTODESTROY_PLAYER_ITEM_AFTER = var0.getProperty("AutoDestroyPlayerDroppedItemAfter", 0);
      DELETE_DAYS = var0.getProperty("DeleteCharAfterDays", 7);
      PURGE_BYPASS_TASK_FREQUENCY = var0.getProperty("PurgeTaskFrequency", 60);

      try {
         DATAPACK_ROOT = new File(var0.getProperty("DatapackRoot", ".")).getCanonicalFile();
      } catch (IOException var6) {
         _log.error("", var6);
      }

      ALLOW_DISCARDITEM = var0.getProperty("AllowDiscardItem", true);
      ALLOW_MAIL = var0.getProperty("AllowMail", true);
      ALLOW_WAREHOUSE = var0.getProperty("AllowWarehouse", true);
      ALLOW_WATER = var0.getProperty("AllowWater", true);
      ALLOW_CURSED_WEAPONS = var0.getProperty("AllowCursedWeapons", false);
      DROP_CURSED_WEAPONS_ON_KICK = var0.getProperty("DropCursedWeaponsOnKick", false);
      MIN_PROTOCOL_REVISION = var0.getProperty("MinProtocolRevision", 740);
      MAX_PROTOCOL_REVISION = var0.getProperty("MaxProtocolRevision", 770);
      MIN_NPC_ANIMATION = var0.getProperty("MinNPCAnimation", 5);
      MAX_NPC_ANIMATION = var0.getProperty("MaxNPCAnimation", 90);
      SERVER_SIDE_NPC_NAME = var0.getProperty("ServerSideNpcName", false);
      SERVER_SIDE_NPC_TITLE = var0.getProperty("ServerSideNpcTitle", false);
      SERVER_SIDE_MONSTER_LEVEL_TITLE = var0.getProperty("ServerSideMonsterTitleLvl", false);
      AUTOSAVE = var0.getProperty("Autosave", true);
      AUTOSAVE_ITEMS = var0.getProperty("AutosaveWithItems", false);
      MAXIMUM_ONLINE_USERS = var0.getProperty("MaximumOnlineUsers", 3000);
      DATABASE_HOST = var0.getProperty("DatabaseHost", "127.0.0.1");
      DATABASE_PORT = var0.getProperty("DatabasePort", 3306);
      DATABASE_NAME = var0.getProperty("DatabaseName", "l2db");
      DATABASE_USER = var0.getProperty("DatabaseUser", "root");
      DATABASE_PASS = var0.getProperty("DatabasePassword", "");
      DATABASE_MAX_CONN = var0.getProperty("DatabaseMaxConnections", 64);
      DATABASE_TIMEOUT = var0.getProperty("DatabaseConnectionTimeout", 30);
      DATABASE_EX_PROPERTIES = var0.getProperty("DatabaseExProperties", "");
      DATABASE_EX_STRUCTURE_MANAGER = var0.getProperty("DatabaseStructureManager", true);
      DATABASE_DUMP_TABLES = var0.getProperty("DatabaseDumpTables", ArrayUtils.EMPTY_STRING_ARRAY);
      DATABASE_DUMP_FILENAME_FORMAT = var0.getProperty("DatabaseDumpFilenameFormat", "backup/%table_name%_%date%_%time%.sql");
      DATABASE_DUMP_ZIP_OUT_FILENAME_FORMAT = var0.getProperty("DatabaseDumpZipOutFilenameFormat", "backup/%date%_%time%.zip");
      USER_INFO_INTERVAL = var0.getProperty("UserInfoInterval", 100L);
      BROADCAST_STATS_INTERVAL = var0.getProperty("BroadcastStatsInterval", true);
      BROADCAST_CHAR_INFO_INTERVAL = var0.getProperty("BroadcastCharInfoInterval", 100L);
      EFFECT_TASK_MANAGER_COUNT = var0.getProperty("EffectTaskManagers", 2);
      SCHEDULED_THREAD_POOL_SIZE = var0.getProperty("ScheduledThreadPoolSize", NCPUS * 4);
      EXECUTOR_THREAD_POOL_SIZE = var0.getProperty("ExecutorThreadPoolSize", NCPUS * 2);
      ENABLE_RUNNABLE_STATS = var0.getProperty("EnableRunnableStats", false);
      SELECTOR_CONFIG.SLEEP_TIME = var0.getProperty("SelectorSleepTime", 10L);
      SELECTOR_CONFIG.INTEREST_DELAY = var0.getProperty("InterestDelay", 30L);
      SELECTOR_CONFIG.MAX_SEND_PER_PASS = var0.getProperty("MaxSendPerPass", 32);
      SELECTOR_CONFIG.READ_BUFFER_SIZE = var0.getProperty("ReadBufferSize", 65536);
      SELECTOR_CONFIG.WRITE_BUFFER_SIZE = var0.getProperty("WriteBufferSize", 131072);
      SELECTOR_CONFIG.HELPER_BUFFER_COUNT = var0.getProperty("BufferPoolSize", 64);
      CHAT_MESSAGE_MAX_LEN = var0.getProperty("ChatMessageLimit", 1000);
      StringTokenizer var7 = new StringTokenizer(var0.getProperty("ChatBanChannels", "ALL,SHOUT,TELL,TRADE,HERO_VOICE"), ",");
      ArrayList var8 = new ArrayList();

      while (var7.hasMoreTokens()) {
         var8.add(ChatType.valueOf(var7.nextToken()));
      }

      BAN_CHANNEL_LIST = (ChatType[])var8.toArray(new ChatType[var8.size()]);
      BANCHAT_ANNOUNCE = var0.getProperty("BANCHAT_ANNOUNCE", true);
      BANCHAT_ANNOUNCE_FOR_ALL_WORLD = var0.getProperty("BANCHAT_ANNOUNCE_FOR_ALL_WORLD", true);
      BANCHAT_ANNOUNCE_NICK = var0.getProperty("BANCHAT_ANNOUNCE_NICK", true);
      NOSPAM_FILTER_HWID = var0.getProperty("NoSpamFilterHwid", false);
      NOSPAM_FILTER_IP = var0.getProperty("NoSpamFilterIp", false);
      DEFAULT_LANG = var0.getProperty("DefaultLang", "ru");
      RESTART_AT_TIME = var0.getProperty("AutoRestartAt", "0 5 * * *");
      SHIFT_BY = var0.getProperty("HShift", 12);
      SHIFT_BY_Z = var0.getProperty("VShift", 11);
      MAP_MIN_Z = var0.getProperty("MapMinZ", -32768);
      MAP_MAX_Z = var0.getProperty("MapMaxZ", 32767);
      MOVE_TASK_QUANTUM_PC = var0.getProperty("MoveTaskQuantumForPC", 100);
      MOVE_TASK_QUANTUM_NPC = var0.getProperty("MoveTaskQuantumForNPC", 100);
      MOVE_OFFLOAD_MTL_PC = var0.getProperty("OffloadMTLForPC", false);
      MOVE_TO_TOWN_ON_FAIL_VALIDATE = var0.getProperty("MoveToTownOnFailValidate", true);
      MOVE_TO_TOWN_ON_FAIL_TRY_COUNT = var0.getProperty("MoveToTownOnFailValidateTryCount", 10);
      ATTACK_PACKET_DELAY = var0.getProperty("AttackPacketDelay", 500);
      ATTACK_END_DELAY = var0.getProperty("AttackEndTime", 50);
      PICKUP_PACKET_DELAY = var0.getProperty("PickUpPacketDelay", 200);
      USE_ITEM_PACKET_DELAY = var0.getProperty("UseItemPacketDelay", 200);
      UNKNOWN_PACKET_AMOUNT_LIMIT = var0.getProperty("UnknownPacketAmountLimit", 10);
      FAIL_PACKET_AMOUNT_LIMIT = var0.getProperty("FailPacketAmountLimit", 10);
      RESEND_MSU_AFTER_TELEPORT = var0.getProperty("ResendMSUPacketAfterTeleport", -1);
      DAMAGE_FROM_FALLING = var0.getProperty("DamageFromFalling", true);
      DONTLOADSPAWN = var0.getProperty("StartWithoutSpawn", false);
      DONTLOADQUEST = var0.getProperty("StartWithoutQuest", false);
      IGNORE_QUESTS = var0.getProperty("DisableQuestId", ArrayUtils.EMPTY_INT_ARRAY);
      MAX_REFLECTIONS_COUNT = var0.getProperty("MaxReflectionsCount", 300);
      COMMUNITYBOARD_ENABLED = var0.getProperty("AllowCommunityBoard", true);
      BBS_PEACE_ONLY = var0.getProperty("AllowCommunityBoardAtPeaceZoneOnly", false);
      BBS_BUFFER_PEACE_ONLY = var0.getProperty("AllowCommunityBoardBufferAtPeaceZoneOnly", false);
      BBS_DEFAULT = var0.getProperty("BBSDefault", "_bbshome");
      HTM_CACHE_MODE = var0.getProperty("HtmCacheMode", 1);
      ALLOW_MULILOGIN = var0.getProperty("AllowMultilogin", false);
      PFILTER_ENABLED = var0.getProperty("EnablePacketFilter", true);
      ALT_CG_MODULE = var0.getProperty("AltClientGuard", "NONE");
   }

   public static void loadTelnetConfig() {
      ExProperties var0 = load("config/telnet.properties");
      IS_TELNET_ENABLED = var0.getProperty("EnableTelnet", false);
      TELNET_DEFAULT_ENCODING = var0.getProperty("TelnetEncoding", "UTF-8");
      TELNET_PORT = var0.getProperty("Port", 7000);
      TELNET_HOSTNAME = var0.getProperty("BindAddress", "127.0.0.1");
      TELNET_PASSWORD = var0.getProperty("Password", "");
   }

   public static void loadResidenceConfig() {
      ExProperties var0 = load("config/residence.properties");
      CH_BID_CURRENCY_ITEM_ID = var0.getProperty("ClanHallCurrencyItemId", 57);
      RESIDENCE_LEASE_FUNC_MULTIPLIER = var0.getProperty("ResidenceLeaseFuncMultiplier", 1.0);
      RESIDENCE_LEASE_MULTIPLIER = var0.getProperty("ResidenceLeaseMultiplier", 1.0);
      CLNHALL_REWARD_CYCLE = var0.getProperty("ClanHallSellingCycle", 168);
      CLAN_MIN_LEVEL_FOR_BID = var0.getProperty("ClanMinLevelForBid", 2);
      CH_DISPLAY_IDS = new TIntHashSet();
      CH_DISPLAY_IDS.addAll(
         var0.getProperty(
            "ClanHallSellListIds",
            new int[]{
               22,
               23,
               24,
               25,
               26,
               27,
               28,
               29,
               30,
               31,
               32,
               33,
               36,
               37,
               38,
               39,
               40,
               41,
               42,
               43,
               44,
               45,
               46,
               47,
               48,
               49,
               50,
               51,
               52,
               53,
               54,
               55,
               56,
               57,
               58,
               59,
               60,
               61
            }
         )
      );
      int[] var1 = var0.getProperty("CastleValidationDate", new int[]{2, 4, 2003});
      CASTLE_VALIDATION_DATE = Calendar.getInstance();
      CASTLE_VALIDATION_DATE.set(5, var1[0]);
      CASTLE_VALIDATION_DATE.set(2, var1[1] - 1);
      CASTLE_VALIDATION_DATE.set(1, var1[2]);
      CASTLE_VALIDATION_DATE.set(11, 0);
      CASTLE_VALIDATION_DATE.set(12, 0);
      CASTLE_VALIDATION_DATE.set(13, 0);
      CASTLE_VALIDATION_DATE.set(14, 0);
   }

   public static void loadOtherConfig() {
      ExProperties var0 = load("config/other.properties");
      DEEPBLUE_DROP_RULES = var0.getProperty("UseDeepBlueDropRules", true);
      DEEPBLUE_DROP_MAXDIFF = var0.getProperty("DeepBlueDropMaxDiff", 8);
      DEEPBLUE_DROP_RAID_MAXDIFF = var0.getProperty("DeepBlueDropRaidMaxDiff", 2);
      EXP_SP_DIFF_LIMIT = var0.getProperty("ExpSpMaxDiffLimit", 0);
      SWIMING_SPEED = var0.getProperty("SwimingSpeedTemplate", 50);
      THRESHOLD_LEVEL_DIFF = var0.getProperty("ExpSpThresholdLevelDiff", 5);
      PARTY_PENALTY_EXP_SP_MAX_LEVEL = var0.getProperty("ExpSpPartyPenaltyLevelMax", 10);
      PARTY_PENALTY_EXP_SP_MIN_LEVEL = var0.getProperty("ExpSpPartyPenaltyLevelMin", 14);
      INVENTORY_MAXIMUM_NO_DWARF = var0.getProperty("MaximumSlotsForNoDwarf", 80);
      INVENTORY_MAXIMUM_DWARF = var0.getProperty("MaximumSlotsForDwarf", 100);
      DWARF_AUTOMATICALLY_CRYSTALLIZE_ON_ITEM_DELETE = var0.getProperty("DwarfAutoCrystallizeOnItemDelete", false);
      CRYSTALLIZE_BONUS_AT_ENCHANT = var0.getProperty("CrystallizeBonusAtEnchantLevel", true);
      INVENTORY_MAXIMUM_GM = var0.getProperty("MaximumSlotsForGMPlayer", 250);
      MULTISELL_SIZE = var0.getProperty("MultisellPageSize", 10);
      WAREHOUSE_SLOTS_NO_DWARF = var0.getProperty("BaseWarehouseSlotsForNoDwarf", 100);
      WAREHOUSE_SLOTS_DWARF = var0.getProperty("BaseWarehouseSlotsForDwarf", 120);
      WAREHOUSE_SLOTS_CLAN = var0.getProperty("MaximumWarehouseSlotsForClan", 200);
      FREIGHT_SLOTS = var0.getProperty("MaximumFreightSlots", 10);
      FOLLOW_ARRIVE_DISTANCE = var0.getProperty("FollowArriveDistance", 150);
      SEND_LINEAGE2_WELCOME_MESSAGE = var0.getProperty("SendLineage2WelcomeMessage", true);
      SEND_SSQ_WELCOME_MESSAGE = var0.getProperty("SendSSQWelcomeMessage", true);
      SEND_SSQ_SEAL_STATUS = var0.getProperty("SendSSQSealValidationStatus", true);
      ENCHANT_MAX = var0.getProperty("EnchantMax", 20);
      ARMOR_ENCHANT_6_SKILL = var0.getProperty("EnchantSkill6ChangeOnEnchantLevel", 6);
      ARMOR_ENCHANT_10_SKILL = var0.getProperty("EnchantSkill10ChangeOnEnchantLevel", 10);
      ARMOR_OVERENCHANT_HPBONUS_LIMIT = var0.getProperty("ArmorOverEnchantHPBonusLimit", 10) - 3;
      SHOW_ENCHANT_EFFECT_RESULT = var0.getProperty("ShowEnchantEffectResult", false);
      SHOW_ENCHANT_EFFECT_RESULT_EVERY_NEXT_SUCCESS = var0.getProperty("ShowEffectResultEveryNextSuccess", false);
      WEAPON_FIRST_ENCHANT_EFFECT_LEVEL = var0.getProperty("WeaponFirstEnchantEffectLevel", 7);
      WEAPON_SECOND_ENCHANT_EFFECT_LEVEL = var0.getProperty("WeaponSecondEnchantEffectLevel", 15);
      ARMOR_ENCHANT_EFFECT_LEVEL = var0.getProperty("ArmorEnchantEffectLevel", 6);
      ENCHANT_ATTRIBUTE_STONE_CHANCE = var0.getProperty("EnchantAttributeChance", 50);
      ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE = var0.getProperty("EnchantAttributeCrystalChance", 30);
      ENCHANT_ATTRIBUTE_ENERGY_CHANCE = var0.getProperty("EnchantAttributeEnergyChance", 100);
      REGEN_SIT_WAIT = var0.getProperty("RegenSitWait", false);
      STARTING_ADENA = var0.getProperty("StartingAdena", 0);
      STARTING_ITEMS_MAGE = var0.getProperty("StartingMageItems", ArrayUtils.EMPTY_INT_ARRAY);
      STARTING_ITEMS_FIGHTER = var0.getProperty("StartingFighterItems", ArrayUtils.EMPTY_INT_ARRAY);
      if (STARTING_ITEMS_MAGE.length % 2 == 0 && STARTING_ITEMS_FIGHTER.length % 2 == 0) {
         UNSTUCK_SKILL = var0.getProperty("UnstuckSkill", false);
         BLOCK_BUFF_SKILL = var0.getProperty("BlockBuffSkill", false);
         BLOCK_BUFF_EXCLUDE = new HashIntSet(new CArrayIntList(var0.getProperty("BlockBuffExcludeSkill", new int[]{1323})));
         NOBLES_BUFF_SKILL = var0.getProperty("NoblesBuffSkill", false);
         ADDITIONALS_SKILLS = var0.getProperty("AdditionalDefaultSkills", ArrayUtils.EMPTY_INT_ARRAY);
         if (ADDITIONALS_SKILLS.length % 2 != 0) {
            throw new RuntimeException("ERROR: CONFIG AdditionalDefaultSkills WRONG SIZE");
         }

         RESPAWN_RESTORE_CP = var0.getProperty("RespawnRestoreCP", 0.0) / 100.0;
         RESPAWN_RESTORE_HP = var0.getProperty("RespawnRestoreHP", 65.0) / 100.0;
         RESPAWN_RESTORE_MP = var0.getProperty("RespawnRestoreMP", 0.0) / 100.0;
         MAX_PVTSTORE_SLOTS_DWARF = var0.getProperty("MaxPvtStoreSlotsDwarf", 5);
         MAX_PVTSTORE_SLOTS_DWARF_FIRST_JOB = var0.getProperty("MaxPvtStoreSlotsDwarfOnFirstProf", 3);
         MAX_PVTSTORE_SLOTS_OTHER = var0.getProperty("MaxPvtStoreSlotsOther", 4);
         MAX_PVTSTORE_SLOTS_OTHER_FIRST_JOB = var0.getProperty("MaxPvtStoreSlotsOtherOnFirstProf", 2);
         MAX_PVTCRAFT_SLOTS = var0.getProperty("MaxPvtManufactureSlots", 20);
         PRIVATE_BUY_MATCH_ENCHANT = var0.getProperty("AltPrivateBuyMatchEnchant", true);
         SENDSTATUS_TRADE_JUST_OFFLINE = var0.getProperty("SendStatusTradeJustOffline", false);
         SENDSTATUS_TRADE_MOD = var0.getProperty("SendStatusTradeMod", 1.0);
         MUL_PLAYERS_ONLINE = var0.getProperty("MulOnlinePlayers", 1);
         ANNOUNCE_MAMMON_SPAWN = var0.getProperty("AnnounceMammonSpawn", true);
         GM_NAME_COLOUR = Integer.decode("0x" + var0.getProperty("GMNameColour", "FFFFFF"));
         GM_HERO_AURA = var0.getProperty("GMHeroAura", false);
         NORMAL_NAME_COLOUR = Integer.decode("0x" + var0.getProperty("NormalNameColour", "FFFFFF"));
         CLANLEADER_NAME_COLOUR = Integer.decode("0x" + var0.getProperty("ClanleaderNameColour", "FFFFFF"));
         SHOW_HTML_WELCOME = var0.getProperty("ShowHTMLWelcome", false);
         GAME_POINT_ITEM_ID = var0.getProperty("GamePointItemId", -1);
         OTHER_MAGE_BUFF_ON_CHAR_CREATE = new ArrayList<>();

         for (String var4 : StringUtils.split(var0.getProperty("StartMageBuff", "1303-1"), ";,")) {
            String[] var5 = StringUtils.split(var4, "-:");
            OTHER_MAGE_BUFF_ON_CHAR_CREATE.add(Pair.of(Integer.parseInt(var5[0]), Integer.parseInt(var5[1])));
         }

         OTHER_WARRIOR_BUFF_ON_CHAR_CREATE = new ArrayList<>();

         for (String var9 : StringUtils.split(var0.getProperty("StartWarriorBuff", "1086-1"), ";,")) {
            String[] var10 = StringUtils.split(var9, "-:");
            OTHER_WARRIOR_BUFF_ON_CHAR_CREATE.add(Pair.of(Integer.parseInt(var10[0]), Integer.parseInt(var10[1])));
         }
      } else {
         throw new RuntimeException("ERROR: CONFIG StartingItems WRONG SIZE");
      }
   }

   public static void loadCustomConfig() {
      ExProperties var0 = load("config/custom.properties");
      CUSTOM_DISPELL_EFFECTS = var0.getProperty("EnableCustomDispelSystem", false);
      CUSTOM_DISPELL_EFFECTS_ON_OLYMPIAD = var0.getProperty("CustomDispelWorkAtOly", false);
      CUSTOM_DISPELL_MUSIC_EFFECTS = var0.getProperty("CustomDispelDanceSong", false);
      CUSTOM_PATCH_OPTIONS_ENABLE = var0.getProperty("EnableCustomPatchSystem", false);
      CUSTOM_URL_COMMAND_SERVICE = var0.getProperty("EnableCustomPatchUrlService", false);
      CUSTOM_PATCH_DONATE_URL = var0.getProperty("EnableCustomPatchDonateUrl", "https://lucera2.ru/");
      CUSTOM_PATCH_VK_URL = var0.getProperty("EnableCustomPatchVkUrl", "https://lucera2.ru/");
      CUSTOM_PATCH_FACEBOOK_URL = var0.getProperty("EnableCustomPatchFacebookUrl", "https://lucera2.ru/");
      CUSTOM_SEND_MESSAGE_ON_PA_ACCEPT = var0.getProperty("EnableCustomPABuySendMessage", false);
   }

   public static void loadClanConfig() {
      ExProperties var0 = load("config/clan.properties");
      CLAN_LEAVE_TIME_PERNALTY = var0.getProperty("ClanLeaveTimePenalty", 24L) * 3600000L;
      EXPELLED_MEMBER_PENALTY = var0.getProperty("ExpelledClanPenalty", 24L) * 3600000L;
      LEAVED_ALLY_PENALTY = var0.getProperty("AllyLeavedPenalty", 24L) * 3600000L;
      DISSOLVED_ALLY_PENALTY = var0.getProperty("DissolvedAllyPenalty", 24L) * 3600000L;
      NEW_CLAN_CREATE_PENALTY = var0.getProperty("DissolvedClanPenalty", 240L) * 3600000L;
      CLAN_DISBAND_TIME = var0.getProperty("ClanDisbandTimeDelay", 48L) * 3600000L;
      CLAN_DISBAND_PENALTY = var0.getProperty("ClanDisbandCancelPenalty", 168L) * 3600000L;
      CLAN_INIT_LEVEL = var0.getProperty("ClanInitLevel", 0);
      CLAN_REPUTATION_BONUS_ON_CREATE = var0.getProperty("ClanReputationBonusOnCreate", 0);
      FULL_CLAN_SKILLS_ON_CREATE = var0.getProperty("FullClanSkillsOnCreate", false);
      CHARACTER_MIN_LEVEL_FOR_CLAN_CREATE = var0.getProperty("CharacterMinLevelForClanCreate", 10);
      CLAN_LEADER_CHANGE_METHOD = var0.getProperty("ClanLeaderChangeMethod", false);
      LIMIT_CLAN_LEVEL0 = var0.getProperty("MainClanMembersLimitOnLevel0", 10);
      LIMIT_CLAN_LEVEL1 = var0.getProperty("MainClanMembersLimitOnLevel1", 15);
      LIMIT_CLAN_LEVEL2 = var0.getProperty("MainClanMembersLimitOnLevel2", 20);
      LIMIT_CLAN_LEVEL3 = var0.getProperty("MainClanMembersLimitOnLevel3", 30);
      LIMIT_CLAN_LEVEL_4_AND_HIGH = var0.getProperty("MainClanMembersLimitOnLevel4", 40);
      LIMIT_CLAN_HIGH_UNITS = var0.getProperty("SubUnitRoyalLimitMembers", 20);
      LIMIT_CLAN_LOW_UNITS = var0.getProperty("SubUnitKnightLimitMembers", 10);
      LIMIT_CLAN_ACADEMY = var0.getProperty("ClanAcademyLimit", 20);
      MIN_CLAN_LEVEL_FOR_DECLARED_WAR = var0.getProperty("MinClanLevelForDeclaredWar", 3);
      MIN_CLAN_MEMBER_FOR_DECLARED_WAR = var0.getProperty("MinClanMembersForDeclaredWar", 15);
      CRP_REWARD_ON_WAR_KILL_OVER_LEVEL = var0.getProperty("OnWarKillCRPRewardOverLevel", 2);
      CRP_REWARD_ON_WAR_KILL = var0.getProperty("OnWarKillCRPReward", 1);
      CLAN_FIRST_LEVEL_SP = var0.getProperty("FirstClanLevelUpSP", 20000);
      CLAN_FIRST_LEVEL_ADENA = var0.getProperty("FirstClanLevelUpAdena", 650000);
      CLAN_SECOND_LEVEL_SP = var0.getProperty("SecondClanLevelUpSP", 100000);
      CLAN_SECOND_LEVEL_ADENA = var0.getProperty("SecondClanLevelUpAdena", 2500000);
      CLAN_THIRD_LEVEL_SP = var0.getProperty("ThirdClanLevelUpSP", 350000);
      CLAN_FOUR_LEVEL_SP = var0.getProperty("FourClanLevelUpSP", 1000000);
      CLAN_FIVE_LEVEL_SP = var0.getProperty("FiveClanLevelUpSP", 2500000);
      CLAN_SIX_LEVEL_CLAN_REPUTATION = var0.getProperty("SixClanLevelUpReputationCount", 10000);
      CLAN_SIX_LEVEL_CLAN_MEMBER_COUNT = var0.getProperty("SixClanLevelUpMemberCount", 30);
      CLAN_SEVEN_LEVEL_CLAN_REPUTATION = var0.getProperty("SevenClanLevelUpReputationCount", 20000);
      CLAN_SEVEN_LEVEL_CLAN_MEMBER_COUNT = var0.getProperty("SevenClanLevelUpMemberCount", 50);
      CLAN_EIGHT_LEVEL_CLAN_REPUTATION = var0.getProperty("EightClanLevelUpReputationCount", 40000);
      CLAN_EIGHT_LEVEL_CLAN_MEMBER_COUNT = var0.getProperty("EightClanLevelUpMemberCount", 80);
      ALLY_ALLOW_BUFF_DEBUFFS = var0.getProperty("AllyRelationLikeClan", false);
      ALLOW_TEMPORARILY_ALLY_ON_FIRST_SIEGE = var0.getProperty("AllowTemporarilyAllyOnFirstSiege", false);
      ALT_NPC_CLAN = var0.getProperty("NpcClanCrestDisplay", -1);
      CHECK_CLAN_RANK_ON_COMMAND_CHANNEL_CREATE = var0.getProperty("CheckClanRankForCommandChannelCreate", true);
      COMMAND_CHANNEL_PARY_COUNT_LIMIT = var0.getProperty("CommandChannelPartyCountLimit", 64);
      MIN_CLAN_LEVEL_FOR_REPUTATION = var0.getProperty("MinClanLevelForReputationReceiving", 5);
      MIN_CLAN_LEVEL_FOR_SIEGE_REGISTRATION = var0.getProperty("MinClanLevelForSiegeRegistration", 5);
      MAX_ALLY_SIZE = var0.getProperty("MaxAllianceSize", 3);
      SIEGE_CLAN_REPUTATION_REWARD = var0.getProperty("SiegeClanReputationReward", 1000);
      SIEGE_CLAN_REPUTATION_DEFEND_REWARD = var0.getProperty("DefendSiegeClanReputationReward", 1500);
      SIEGE_CLAN_REPUTATION_DEFEND_CONSUME = var0.getProperty("DefendSiegeClanReputationConsume", -1500);
   }

   public static void loadSpoilConfig() {
      ExProperties var0 = load("config/spoil.properties");
      MINIMUM_SPOIL_RATE = var0.getProperty("MinimumPercentChanceOfSpoilSuccess", 1.0);
      MANOR_SOWING_BASIC_SUCCESS = var0.getProperty("BasePercentChanceOfSowingSuccess", 100.0);
      MANOR_SOWING_ALT_BASIC_SUCCESS = var0.getProperty("BasePercentChanceOfSowingAltSuccess", 10.0);
      MANOR_HARVESTING_BASIC_SUCCESS = var0.getProperty("BasePercentChanceOfHarvestingSuccess", 90.0);
      MANOR_HARVESTING_REWARD_RATE = var0.getProperty("HarvestingRewardRate", 1.0);
      MANOR_DIFF_PLAYER_TARGET = var0.getProperty("MinDiffPlayerMob", 5);
      MANOR_DIFF_PLAYER_TARGET_PENALTY = var0.getProperty("DiffPlayerMobPenalty", 5.0);
      MANOR_DIFF_SEED_TARGET = var0.getProperty("MinDiffSeedMob", 5);
      MANOR_DIFF_SEED_TARGET_PENALTY = var0.getProperty("DiffSeedMobPenalty", 5.0);
      ALLOW_MANOR = var0.getProperty("AllowManor", true);
      MANOR_REFRESH_TIME = var0.getProperty("AltManorRefreshTime", 20);
      MANOR_REFRESH_MIN = var0.getProperty("AltManorRefreshMin", 0);
      MANOR_APPROVE_TIME = var0.getProperty("AltManorApproveTime", 6);
      MANOR_APPROVE_MIN = var0.getProperty("AltManorApproveMin", 0);
      MANOR_MAINTENANCE_PERIOD = var0.getProperty("AltManorMaintenancePeriod", 360000);
      SPOIL_ITEMS_ID_FOR_RATE = var0.getProperty("AltSpoilItemsIdForRate", new int[0]);
      SPOIL_ITEMS_CHANCE_RATE = var0.getProperty("AltSpoilItemChanceRate", 1.0);
   }

   public static void loadExperienceConfig() {
      FileInputStream var0 = null;
      InputStreamReader var1 = null;
      BufferedReader var2 = null;

      try {
         File var3 = new File("config/experience.csv");
         var0 = new FileInputStream(var3);
         var1 = new InputStreamReader(var0, StandardCharsets.UTF_8);
         var2 = new BufferedReader(var1);
         int var4 = 0;
         HashMap var5 = new HashMap();

         for (String var6 = var2.readLine(); var6 != null; var4++) {
            int var7 = var6.indexOf("//");
            if (var7 > -1) {
               var6 = var6.substring(0, var7);
            }

            long var8 = Long.parseLong(var6.trim());
            var5.put(var4, var8);
            var6 = var2.readLine();
         }

         long[] var19 = new long[var4];

         for (int var20 = 0; var20 < var4; var20++) {
            var19[var20] = ((Number)var5.getOrDefault(var20, Long.valueOf(0L))).longValue();
         }

         EXPERIENCE = var19;
      } catch (IOException var17) {
         throw new RuntimeException(var17);
      } finally {
         try {
            if (var2 != null) {
               var2.close();
            }

            if (var1 != null) {
               var1.close();
            }

            if (var0 != null) {
               var0.close();
            }
         } catch (Exception var16) {
         }
      }
   }

   public static void loadFormulasConfig() {
      ExProperties var0 = load("config/formulas.properties");
      SKILLS_CHANCE_MOD = var0.getProperty("SkillsChanceMod", 11.0);
      SKILLS_CHANCE_POW = var0.getProperty("SkillsChancePow", 0.5);
      SKILLS_CHANCE_MIN = var0.getProperty("SkillsChanceMin", 5.0);
      SKILLS_CHANCE_CAP = var0.getProperty("SkillsChanceCap", 95.0);
      SKILLS_CAST_TIME_MIN = var0.getProperty("SkillsCastTimeMin", 333);
      SKILLS_DISPEL_MOD_MAX = var0.getProperty("DispelMaxChance", 75);
      SKILLS_DISPEL_MOD_MIN = var0.getProperty("DispelMinChance", 25);
      SKILLS_BANE_MOD_MAX = var0.getProperty("BaneMaxChance", 90);
      SKILLS_BANE_MOD_MIN = var0.getProperty("BaneMinChance", 40);
      ALT_ABSORB_DAMAGE_MODIFIER = var0.getProperty("AbsorbDamageModifier", 1.0);
      RATE_OVERHIT = var0.getProperty("RateOverHitXp", 1.0);
      LIM_PATK = var0.getProperty("LimitPatk", 20000);
      LIM_MATK = var0.getProperty("LimitMAtk", 25000);
      LIM_PDEF = var0.getProperty("LimitPDef", 15000);
      LIM_MDEF = var0.getProperty("LimitMDef", 15000);
      LIM_PATK_SPD = var0.getProperty("LimitPatkSpd", 1500);
      LIM_MATK_SPD = var0.getProperty("LimitMatkSpd", 1999);
      LIM_CRIT_DAM = var0.getProperty("LimitCriticalDamage", 2000);
      LIM_CRIT = var0.getProperty("LimitCritical", 500);
      LIM_MCRIT = var0.getProperty("LimitMCritical", 20);
      MCRITICAL_CRIT_POWER = var0.getProperty("mCritDamagePower", 4.0);
      LIM_ACCURACY = var0.getProperty("LimitAccuracy", 200);
      LIM_EVASION = var0.getProperty("LimitEvasion", 200);
      LIM_MOVE = var0.getProperty("LimitMove", 250);
      LIM_MOVE_GM = var0.getProperty("LimitGmMove", 250);
      LIM_MAX_CP = var0.getProperty("LimitMaxCP", 100000);
      LIM_MAX_HP = var0.getProperty("LimitMaxHP", 40000);
      LIM_MAX_MP = var0.getProperty("LimitMaxMP", 40000);
      MCRITICAL_BASE_STAT = var0.getProperty("MCriticalBaseStat", 1);
      BLOW_RATE_CHANCE_LIMIT = var0.getProperty("LimitBlowRateChance", 10.0);
      BLOW_SKILLS_MAX_CHANCE_LIMIT = var0.getProperty("BlowSkillsMaxChance", 80.0);
      MDAM_CRIT_POSSIBLE = var0.getProperty("MDamCritPossibale", true);
      HEAL_CRIT_POSSIBLE = var0.getProperty("HealCritPossibale", false);
      ALT_DISABLE_MAGICFAILURES = var0.getProperty("DisableMagicFailures", false);
      MAGIC_FAIL_LEVEL_MOD = var0.getProperty("MagicFailuresLevelMod", 14);
      LIMIT_HENNA_INT = var0.getProperty("HennaLimitINT", 5);
      LIMIT_HENNA_STR = var0.getProperty("HennaLimitSTR", 5);
      LIMIT_HENNA_MEN = var0.getProperty("HennaLimitMEN", 5);
      LIMIT_HENNA_CON = var0.getProperty("HennaLimitCON", 5);
      LIMIT_HENNA_WIT = var0.getProperty("HennaLimitWIT", 5);
      LIMIT_HENNA_DEX = var0.getProperty("HennaLimitDEX", 5);
      LIM_FAME = var0.getProperty("LimitFame", 50000);
      ALT_NPC_PATK_MODIFIER = var0.getProperty("NpcPAtkModifier", 1.0);
      ALT_NPC_MATK_MODIFIER = var0.getProperty("NpcMAtkModifier", 1.0);
      ALT_NPC_MAXHP_MODIFIER = var0.getProperty("NpcMaxHpModifier", 1.58);
      ALT_NPC_MAXMP_MODIFIER = var0.getProperty("NpcMaxMpModifier", 1.11);
      ALT_NPC_PDEF_MODIFIER = var0.getProperty("NpcPDefModifier", 1.0);
      ALT_NPC_MDEF_MODIFIER = var0.getProperty("NpcMDefModifier", 1.0);
      ALT_NPC_MIN_LEVEL_MODIFIER = var0.getProperty("NpcMinLevelModifier", 1);
      ALT_NPC_MAX_LEVEL_MODIFIER = var0.getProperty("NpcMaxLevelModifier", 90);
      ALT_EPIC_BOSS_PATK_MODIFIER = var0.getProperty("EpicBossPAtkModifier", 1.0);
      ALT_EPIC_BOSS_MATK_MODIFIER = var0.getProperty("EpicBossMAtkModifier", 1.0);
      ALT_EPIC_BOSS_MAXHP_MODIFIER = var0.getProperty("EpicBossMaxHpModifier", 1.58);
      ALT_EPIC_BOSS_MAXMP_MODIFIER = var0.getProperty("EpicBossMaxMpModifier", 1.11);
      ALT_EPIC_BOSS_PDEF_MODIFIER = var0.getProperty("EpicBossPDefModifier", 1.0);
      ALT_EPIC_BOSS_MDEF_MODIFIER = var0.getProperty("EpicBossMDefModifier", 1.0);
      ALT_EPIC_BOSS_MIN_LEVEL_MODIFIER = var0.getProperty("EpicBossMinLevelModifier", 1);
      ALT_EPIC_BOSS_MAX_LEVEL_MODIFIER = var0.getProperty("EpicBossMaxLevelModifier", 90);
      ALT_RAID_BOSS_PATK_MODIFIER = var0.getProperty("RaidBossPAtkModifier", 1.0);
      ALT_RAID_BOSS_MATK_MODIFIER = var0.getProperty("RaidBossMAtkModifier", 1.0);
      ALT_RAID_BOSS_MAXHP_MODIFIER = var0.getProperty("RaidBossMaxHpModifier", 1.58);
      ALT_RAID_BOSS_MAXMP_MODIFIER = var0.getProperty("RaidBossMaxMpModifier", 1.11);
      ALT_RAID_BOSS_PDEF_MODIFIER = var0.getProperty("RaidBossPDefModifier", 1.0);
      ALT_RAID_BOSS_MDEF_MODIFIER = var0.getProperty("RaidBossMDefModifier", 1.0);
      ALT_RAID_BOSS_MIN_LEVEL_MODIFIER = var0.getProperty("RaidBossMinLevelModifier", 1);
      ALT_RAID_BOSS_MAX_LEVEL_MODIFIER = var0.getProperty("RaidBossMaxLevelModifier", 90);
      ALT_NPC_LIM_MCRIT = var0.getProperty("NpcLimitNpcMCritical", 20);
      ALT_POLE_DAMAGE_MODIFIER = var0.getProperty("PoleDamageModifier", 1.0);
      MIN_ATK_DELAY = var0.getProperty("MinAttackDelay", 333);
      AUTO_ATTACK_ON_WEAPON_CHANGE = var0.getProperty("StopAutoAttackOnWeaponChange", true);
      CALC_EFFECT_TIME_YIELD_AND_RESIST = var0.getProperty("CalcEffectTimeYieldAndResist", false);
      FIRST_CONDITION_CHECK_ON_DEATH_TARGET = var0.getProperty("FirstCheckSkillConditionOnDeathTarget", false);
      COUNTERATTACK_MAX_SKILL_RANGE = var0.getProperty("CounterAttackMaxSkillRange", 200);
   }

   public static void loadAltSettings() {
      ExProperties var0 = load("config/altsettings.properties");
      ALT_GAME_DELEVEL = var0.getProperty("Delevel", true);
      ALT_SAVE_UNSAVEABLE = var0.getProperty("AltSaveUnsaveable", false);
      ALT_SAVE_EFFECTS_REMAINING_TIME = var0.getProperty("AltSaveEffectsRemainingTime", 5);
      ALT_SHOW_REUSE_MSG = var0.getProperty("AltShowSkillReuseMessage", true);
      ALT_DELETE_SA_BUFFS = var0.getProperty("AltDeleteSABuffs", false);
      BUFF_STICK_FOR_ALL = var0.getProperty("AltBuffStickUse", false);
      AUTO_LOOT = var0.getProperty("AutoLoot", false);
      AUTO_LOOT_HERBS = var0.getProperty("AutoLootHerbs", false);
      AUTO_LOOT_ADENA = var0.getProperty("AutoLootAdena", false);
      AUTO_LOOT_EXCLUDE_ITEM_IDS = var0.getProperty("AutoLootExcludeItemIds", ArrayUtils.EMPTY_INT_ARRAY);
      AUTO_LOOT_INDIVIDUAL = var0.getProperty("AutoLootIndividual", false);
      AUTO_LOOT_ONLY_FOR_PREMIUM = var0.getProperty("AutoLootOnlyForPremium", false);
      AUTO_LOOT_FROM_RAIDS = var0.getProperty("AutoLootFromRaids", false);
      XP_FREEZ_ONLY_FOR_PREMIUM = var0.getProperty("ExpFreezingLootOnlyForPremium", false);
      AUTO_LOOT_PK = var0.getProperty("AutoLootPK", false);
      ALT_GAME_KARMA_PLAYER_CAN_SHOP = var0.getProperty("AltKarmaPlayerCanShop", false);
      ALT_CAN_ATTACK_NPC_AT_PEACE_ZONE = var0.getProperty("AltCanAttackNpcAtPeaceZone", true);
      SAVING_SPS = var0.getProperty("SavingSpS", false);
      MANAHEAL_SPS_BONUS = var0.getProperty("ManahealSpSBonus", false);
      ALT_RAID_RESPAWN_MULTIPLIER = var0.getProperty("AltRaidRespawnMultiplier", 1.0);
      ALT_RAID_BOSS_SPAWN_ANNOUNCE_IDS = var0.getProperty("AltRaidBossSpawnAnnounceIds", ArrayUtils.EMPTY_INT_ARRAY);
      ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY = var0.getProperty("AltRaidBossSpawnAnnounceDelay", 5) * 60;
      ALT_RAID_DEATH_ANNOUNCE_IDS = new TIntHashSet(var0.getProperty("AltRaidDeathAnnounceIds", ArrayUtils.EMPTY_INT_ARRAY));
      ALT_ALLOW_DROP_AUGMENTED = var0.getProperty("AlowDropAugmented", false);
      ALT_ALLOW_TRADE_AUGMENTED = var0.getProperty("AllowTradeAugmented", false);
      ALT_ALLOW_REMOTE_SELL_ITEMS_TO_SHOP = var0.getProperty("AltAllowRemoteSellItems", false);
      ALT_ALLOW_REMOTE_USE_CARGO_BOX = var0.getProperty("AltAllowRemoteUseCargoBox", false);
      ALT_GAME_UNREGISTER_RECIPE = var0.getProperty("AltUnregisterRecipe", true);
      ALT_GAME_SHOW_DROPLIST = var0.getProperty("AltShowDroplist", true);
      ALLOW_NPC_SHIFTCLICK = var0.getProperty("AllowShiftClick", true);
      ALT_FULL_NPC_STATS_PAGE = var0.getProperty("AltFullStatsPage", false);
      ALT_NPC_SHIFTCLICK_ITEM_COUNT = var0.getProperty("AltShowDropListItemCountPerPage", 15);
      ALT_GAME_SUBCLASS_WITHOUT_QUESTS = var0.getProperty("AltAllowSubClassWithoutQuest", false);
      ALT_GAME_SUBCLASS_NOT_CHECK_QUEST_234 = var0.getProperty("AltAllowSubClassNotCheckFatesWhisperQuest", false);
      ALT_ALLOW_SUBCLASS_FOR_CUSTOM_ITEM = var0.getProperty("AltAllowSubClassForCustomItem", false);
      ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID = var0.getProperty("AltSubClassForCustomItemId", ArrayUtils.EMPTY_INT_ARRAY);
      ALT_SUBCLASS_FOR_CUSTOM_ITEM_COUNT = var0.getProperty("AltSubClassForCustomItemCount", ArrayUtils.EMPTY_INT_ARRAY);
      ALTSUBCLASS_LIST_ALL = var0.getProperty("AltAllowShowAllSubclassesList", false);
      ALT_ALLOW_ALLCLASS_SKILLENCHANT = var0.getProperty("AltAllowAllNpcEnchantSkill", false);
      ALT_ALLOW_ALLCLASS_SKILL_LEARN = var0.getProperty("AltAllowAllNpcLearnSkill", false);
      ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL = var0.getProperty("AltAllowLearnOverlordAndWarsmithToAll", false);
      ALTSUBCLASS_ALLOW_FOR_ELF_TO_DARK_ELF = var0.getProperty("AltAllowLearnSubElfToDarkElf", false);
      ALT_GAME_LEVEL_TO_GET_SUBCLASS = var0.getProperty("AltLevelToGetSubclass", 75);
      ALT_LEVEL_AFTER_GET_SUBCLASS = var0.getProperty("AltLevelAfterGetSubClass", 40);
      ALT_NEW_CHARACTER_LEVEL = var0.getProperty("AltNewCharacterLevel", 0);
      ALT_GAME_BASE_SUB = var0.getProperty("AltSubLimit", 4);
      ALT_MAX_LEVEL = Math.min(var0.getProperty("AltMaxLevel", 80), Experience.LEVEL.length - 1);
      ALT_MAX_SUB_LEVEL = Math.min(var0.getProperty("AltMaxSubLevel", 80), Experience.LEVEL.length - 1);
      ALT_ALLOW_HERO_SKILLS_ON_SUB_CLASS = var0.getProperty("AltAllowHeroSkillsonSubClass", false);
      ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE = var0.getProperty("AltAllowOthersWithdrawFromClanWarehouse", false);
      ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER = var0.getProperty("AltAllowClanCommandOnlyForClanLeader", true);
      ALT_ALLOW_CLAN_COMMAND_ALLOW_WH = var0.getProperty("AltClanCommandAllowPrivToWH", false);
      ALT_ALLOW_MENU_COMMAND = var0.getProperty("AltAllowMenuCommand", true);
      ALT_ALLOW_RELOG_COMMAND = var0.getProperty("AltAllowRestartChatCommand", false);
      ALT_ALLOW_HELP_COMMAND = var0.getProperty("AltAllowHelpCommand", true);
      ALT_ALLOW_INSTANCE_COMMAND = var0.getProperty("AltAllowInstanceCommand", true);
      ALT_ALLOW_ITEM_REMAINING_COMMAND = var0.getProperty("AltAllowItemRemainingCommand", true);
      ALT_ALLOW_MAMMON_SEARCH_COMMAND = var0.getProperty("AltAllowMammonSearchCommand", true);
      ALT_ALLOW_SERVICES_COMMAND = var0.getProperty("AltAllowServicesCommand", true);
      ALT_ALLOW_SERVER_INFO_COMMAND = var0.getProperty("AltAllowServerInfoCommand", true);
      ALT_ALLOW_LANG_COMMAND = var0.getProperty("AltAllowChangeLangCommand", true);
      ALT_CHANGE_LANG_NAME_1 = var0.getProperty("AltChangeLangCommand1", "en");
      ALT_CHANGE_LANG_NAME_2 = var0.getProperty("AltChangeLangCommand2", "ru");
      ALT_ALLOW_PING_COMMAND = var0.getProperty("AltAllowPingCommand", false);
      ALT_SUBLASS_SKILL_TRANSFER = var0.getProperty("AltTransferSubSkillsToMain", false);
      ALT_GAME_REQUIRE_CLAN_CASTLE = var0.getProperty("AltRequireClanCastle", false);
      ALT_GAME_REQUIRE_CASTLE_DAWN = var0.getProperty("AltRequireCastleDawn", true);
      ALT_GAME_ALLOW_ADENA_DAWN = var0.getProperty("AltAllowAdenaDawn", true);
      ALT_ADD_RECIPES = var0.getProperty("AltAddRecipes", 0);
      SS_ANNOUNCE_PERIOD = var0.getProperty("SSAnnouncePeriod", 0);
      PETITIONING_ALLOWED = var0.getProperty("PetitioningAllowed", true);
      CAN_PETITION_TO_OFFLINE_GM = var0.getProperty("CanMakePetitionToOfflineGM", false);
      MAX_PETITIONS_PER_PLAYER = var0.getProperty("MaxPetitionsPerPlayer", 5);
      MAX_PETITIONS_PENDING = var0.getProperty("MaxPetitionsPending", 25);
      AUTO_LEARN_SKILLS = var0.getProperty("AutoLearnSkills", false);
      AUTO_LEARN_FORGOTTEN_SKILLS = var0.getProperty("AutoLearnForgottenSkills", false);
      AUTO_LEARN_DIVINE_INSPIRATION = var0.getProperty("AutoLearnDivineInspiration", false);
      ALT_SOCIAL_ACTION_REUSE = var0.getProperty("AltSocialActionReuse", false);
      ALT_DISABLE_SPELLBOOKS = var0.getProperty("AltDisableSpellbooks", false);
      ALT_WEAK_SKILL_LEARN = var0.getProperty("AltWeakSkillLearn", false);
      ALT_SIMPLE_SIGNS = var0.getProperty("PushkinSignsOptions", false);
      ALT_TELE_TO_CATACOMBS = var0.getProperty("TeleToCatacombs", false);
      ALT_BS_CRYSTALLIZE = var0.getProperty("BSCrystallize", false);
      ALT_ALLOW_TATTOO = var0.getProperty("AllowTattoo", false);
      ALT_ALLOW_CUSTOM_HERO = var0.getProperty("AltAllowCustomHero", false);
      ALT_ALLOW_CUSTOM_HERO_SKILLS = var0.getProperty("AltAllowCustomHeroSkills", false);
      ALT_BUFF_LIMIT = var0.getProperty("BuffLimit", 20);
      ALT_DEBUFF_LIMIT = var0.getProperty("DebuffLimit", 8);
      ALT_TRIGGER_SLOT_ADDER = var0.getProperty("AltTriggerSlotAdder", 0);
      ALT_NPC_BUFFER_EFFECT_TIME = var0.getProperty("AltNpcBufferEffectTime", 0L) * 1000L;
      ALT_NPC_BUFFER_REUSE_DELAY = var0.getProperty("AltNpcBufferReuseBuffDelay", 300L);
      ALT_NPC_BUFFER_PREMIUM_HTML_PREFIX = var0.getProperty("AltNpcBufferPremiumHtmlPrefix", false);
      ALT_NPC_BUFFER_PREMIUM_ITEM_IDS = var0.getProperty("AltNpcBufferPremiumItemPrefix", ArrayUtils.EMPTY_INT_ARRAY);
      ALT_DEATH_PENALTY = var0.getProperty("EnableAltDeathPenalty", false);
      ALLOW_DEATH_PENALTY_C5 = var0.getProperty("EnableDeathPenaltyC5", true);
      ALT_DEATH_PENALTY_C5_CHANCE = var0.getProperty("DeathPenaltyC5Chance", 10);
      ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY = var0.getProperty("ChaoticCanUseScrollOfRecovery", false);
      ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY = var0.getProperty("DeathPenaltyC5RateExpPenalty", 1);
      ALT_DEATH_PENALTY_C5_KARMA_PENALTY = var0.getProperty("DeathPenaltyC5RateKarma", 1);
      ALT_PK_DEATH_RATE = var0.getProperty("AltPKDeathRate", 0.0);
      DISABLE_GRADE_PENALTY = var0.getProperty("AltDisableGradePenalty", false);
      ALT_HAIR_TO_ACC_SLOT = var0.getProperty("AltEnchantHairAccessory", false);
      ALT_CONSUME_ARROWS = var0.getProperty("ConsumeArrows", true);
      ALT_CONSUME_SOULSHOTS = var0.getProperty("ConsumeSoulShots", true);
      ALT_CONSUME_SPIRITSHOTS = var0.getProperty("ConsumeSpiritShots", true);
      ALT_CONSUME_BEAST_SHOTS = var0.getProperty("ConsumeBeastShots", true);
      ALT_PA_CONSUME_ARROWS = var0.getProperty("PremiumAccountConsumeArrows", true);
      ALT_PA_CONSUME_SOULSHOTS = var0.getProperty("PremiumAccountConsumeSoulShots", true);
      ALT_PA_CONSUME_SPIRITSHOTS = var0.getProperty("PremiumAccountConsumeSpiritShots", true);
      ALT_PA_CONSUME_BEAST_SHOTS = var0.getProperty("PremiumAccountConsumeBeastShots", true);
      NONOWNER_ITEM_PICKUP_DELAY_RAID = var0.getProperty("NonOwnerItemFromRaidPickupDelay", 300L) * 1000L;
      IGNORE_ITEM_PICKUP_DELAY_MONSTER_IDS = new TIntHashSet(var0.getProperty("IgnoreDropDelayMonsterIds", ArrayUtils.EMPTY_INT_ARRAY));
      IGNORE_ITEM_PICKUP_DELAY = var0.getProperty("IgnoreDropPickupDelay", 0L) * 1000L;
      ALT_NO_LASTHIT = var0.getProperty("NoLasthitOnRaid", false);
      ALT_DROP_LASTHIT = var0.getProperty("DropOnLastHit", false);
      ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY = var0.getProperty("KamalokaNightmaresPremiumOnly", false);
      ALT_KAMALOKA_NIGHTMARE_REENTER = var0.getProperty("SellReenterNightmaresTicket", true);
      ALT_KAMALOKA_ABYSS_REENTER = var0.getProperty("SellReenterAbyssTicket", true);
      ALT_KAMALOKA_LAB_REENTER = var0.getProperty("SellReenterLabyrinthTicket", true);
      ALT_PET_HEAL_BATTLE_ONLY = var0.getProperty("PetsHealOnlyInBattle", true);
      CHAR_TITLE = var0.getProperty("CharTitle", false);
      ADD_CHAR_TITLE = var0.getProperty("CharAddTitle", "");
      ALT_ALLOW_SELL_COMMON = var0.getProperty("AllowSellCommon", true);
      ALT_ALLOW_SHADOW_WEAPONS = var0.getProperty("AllowShadowWeapons", true);
      ALT_DISABLED_MULTISELL = var0.getProperty("DisabledMultisells", ArrayUtils.EMPTY_INT_ARRAY);
      ALT_SHOP_PRICE_LIMITS = var0.getProperty("ShopPriceLimits", ArrayUtils.EMPTY_INT_ARRAY);
      ALT_SHOP_UNALLOWED_ITEMS = var0.getProperty("ShopUnallowedItems", ArrayUtils.EMPTY_INT_ARRAY);
      ALT_SHOP_REFUND_SELL_DIVISOR = var0.getProperty("ShopRefundSellDivisor", 2L);
      ALT_PRIVATE_STORE_BUY_DIVISOR = var0.getProperty("PrivateStoreBuyDivisor", 2L);
      ALT_SHOP_BUY_LIST_MULTIPLIER = var0.getProperty("ShopBuyItemMultiplier", 1.0);
      ALT_MULTISELL_ADENA_MULTIPLAYER = var0.getProperty("MultisellAdenaPriceMultiplier", 1.0);
      ALT_REVIVE_WINDOW_TO_TOWN = var0.getProperty("AltReviveWindowToTown", false);
      ALT_REVIVE_WINDOW_TO_TOWN_LOCATION = Location.parseLoc(var0.getProperty("AltReviveWindowToTownLoc", "81749,149171,-3464"));
      ALT_ALLOWED_PET_POTIONS = var0.getProperty("AllowedPetPotions", new int[]{735, 1060, 1061, 1062, 1374, 1375, 1539, 1540, 6035, 6036});
      FESTIVAL_MIN_PARTY_SIZE = var0.getProperty("FestivalMinPartySize", 5);
      FESTIVAL_RATE_PRICE = var0.getProperty("FestivalRatePrice", 1.0);
      RIFT_MIN_PARTY_SIZE = var0.getProperty("RiftMinPartySize", 5);
      RIFT_SPAWN_DELAY = var0.getProperty("RiftSpawnDelay", 10000);
      RIFT_MAX_JUMPS = var0.getProperty("MaxRiftJumps", 4);
      RIFT_AUTO_JUMPS_TIME = var0.getProperty("AutoJumpsDelay", 8);
      RIFT_AUTO_JUMPS_TIME_RAND = var0.getProperty("AutoJumpsDelayRandom", 120000);
      RIFT_ENTER_COST_RECRUIT = var0.getProperty("RecruitFC", 18);
      RIFT_ENTER_COST_SOLDIER = var0.getProperty("SoldierFC", 21);
      RIFT_ENTER_COST_OFFICER = var0.getProperty("OfficerFC", 24);
      RIFT_ENTER_COST_CAPTAIN = var0.getProperty("CaptainFC", 27);
      RIFT_ENTER_COST_COMMANDER = var0.getProperty("CommanderFC", 30);
      RIFT_ENTER_COST_HERO = var0.getProperty("HeroFC", 33);
      RIFT_BOSS_ROOM_CHANCE = var0.getProperty("RiftRoomBossChance", 0);
      FOUR_SEPULCHER_ENABLE = var0.getProperty("FourSepulcherEnable", true);
      FOUR_DEBUG_SEPULCHER_ENABLE = var0.getProperty("FourDebugSepulcherEnable", false);
      FOUR_SEPULCHER_MIN_PARTY_MEMBERS = var0.getProperty("FourSepulcherMinPartyMembers", 4);
      FOUR_SEPULCHER_NEW_CYCLE_MIN = var0.getProperty("FourSepulcherNewCycleMinutesStart", 55);
      ALLOW_CLANSKILLS = var0.getProperty("AllowClanSkills", true);
      PARTY_LEADER_ONLY_CAN_INVITE = var0.getProperty("PartyLeaderOnlyCanInvite", true);
      ALLOW_TALK_WHILE_SITTING = var0.getProperty("AllowTalkWhileSitting", true);
      ALLOW_NOBLE_TP_TO_ALL = var0.getProperty("AllowNobleTPToAll", false);
      CLANHALL_BUFFTIME_MODIFIER = var0.getProperty("ClanHallBuffTimeModifier", 1.0);
      SONGDANCETIME_MODIFIER = var0.getProperty("SongDanceTimeModifier", 1.0);
      ALT_MUSIC_COST_GUARD_INTERVAL = var0.getProperty("AltMusicCostInterval", 0);
      ALT_ADDITIONAL_DANCE_SONG_MANA_CONSUME = var0.getProperty("AltAddMusicCost", true);
      MAXLOAD_MODIFIER = var0.getProperty("MaxLoadModifier", 1.0);
      GATEKEEPER_MODIFIER = new HashMap<>();
      StringTokenizer var1 = new StringTokenizer(var0.getProperty("GatekeeperCostMultiplier", "0-40:0;41-:1"), ";");

      while (var1.hasMoreTokens()) {
         String var2 = var1.nextToken();
         int var3 = var2.indexOf(58);
         if (var3 < 1) {
            _log.warn("Can't process mod teleport \"" + var2 + "\"");
         } else {
            float var4 = Float.parseFloat(var2.substring(var3 + 1).trim());
            int var5 = var2.indexOf(45);
            if (var5 >= 1 && var5 < var3) {
               int var6 = Integer.parseInt(var2.substring(0, var5).trim());
               var2 = var2.substring(var5 + 1, var3).trim();
               int var7 = var2.isEmpty() ? ALT_MAX_LEVEL + 1 : Integer.parseInt(var2) + 1;

               for (int var8 = var6; var8 < var7; var8++) {
                  if (GATEKEEPER_MODIFIER.containsKey(var8)) {
                     _log.warn("Mod for lvl " + var8 + " redefined.");
                  }

                  GATEKEEPER_MODIFIER.put(var8, var4);
               }
            } else {
               _log.warn("Can't process mod teleport \"" + var2 + "\"");
            }
         }
      }

      GATEKEEPER_FREE = var0.getProperty("GatekeeperFreeToCatacombs", 40);
      KRUMA_TELEPORT_MAX_LEVEL = var0.getProperty("GatekeeperMozellaMaxLevelToCruma", 55);
      ALT_IMPROVED_PETS_LIMITED_USE = var0.getProperty("ImprovedPetsLimitedUse", false);
      SKILL_DURATION_MOD_AT_OLY = var0.getProperty("SkillDurationModWorkAtOlympiad", true);

      for (String var17 : var0.getProperty("SkillDurationMod", new String[]{""}, ";")) {
         String[] var19 = var17.trim().split("-");
         if (var19.length == 2) {
            SKILL_DURATION_MOD.put(Integer.parseInt(var19[0].trim()), Integer.parseInt(var19[1].trim()) * 1000);
         }
      }

      ALT_CHAMPION_CHANCE1 = var0.getProperty("AltChampionChance1", 0.0);
      ALT_CHAMPION_CHANCE2 = var0.getProperty("AltChampionChance2", 0.0);
      ALT_CHAMPION_CAN_BE_AGGRO = var0.getProperty("AltChampionAggro", false);
      ALT_CHAMPION_CAN_BE_SOCIAL = var0.getProperty("AltChampionSocial", false);
      ALT_CHAMPION_CAN_BE_SPECIAL_MONSTERS = var0.getProperty("AltChampionCanBeSpecialMonster", false);
      ALT_CHAMPION_CAN_BE_SEVEN_SIGN_MONSTERS = var0.getProperty("AltChampionCanBeSevenSignMonster", false);
      ALT_CHAMPION_TOP_LEVEL = var0.getProperty("AltChampionTopLevel", 75);
      ALT_CHAMPION_MIN_LEVEL = var0.getProperty("AltChampionMinLevel", 1);
      ALT_CHAMPIONS_LIST = new HashSet<>(Arrays.asList(ArrayUtils.toObject(var0.getProperty("AltChampionListId", ArrayUtils.EMPTY_INT_ARRAY))));
      ALT_IGNORE_CHAMPIONS_LIST = new HashSet<>(Arrays.asList(ArrayUtils.toObject(var0.getProperty("AltChampionIgnoreListId", ArrayUtils.EMPTY_INT_ARRAY))));
      ALT_CHAMPION_DROP_LEVEL_DIFF = var0.getProperty("AltChampionDropLevelDiff", 9);

      for (int var10 = 1; var10 <= 2; var10++) {
         ALT_CHAMPION_DROP_ITEM_IDS.put(var10, var0.getProperty("AltChampionDropItemId." + var10, ArrayUtils.EMPTY_INT_ARRAY));
         ALT_CHAMPION_DROP_COUNTS.put(var10, var0.getProperty("AltChampionDropItemCount." + var10, ArrayUtils.EMPTY_LONG_ARRAY));
         ALT_CHAMPION_DROP_CHANCES.put(var10, var0.getProperty("AltChampionDropItemChance." + var10, ArrayUtils.EMPTY_FLOAT_ARRAY));
      }

      ALT_PCBANG_POINTS_ENABLED = var0.getProperty("AltPcBangPointsEnabled", false);
      ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE = var0.getProperty("AltPcBangPointsDoubleChance", 10.0);
      ALT_PCBANG_POINTS_BONUS_DOUBLE_RATE = var0.getProperty("AltPcBangPointsDoubleRate", 2.0);
      ALT_PCBANG_BONUS_WITH_PREMIUM_RATE = var0.getProperty("AltPcBangPointsBonusForPremiumAccount", 1.0);
      ALT_PCBANG_POINTS_BONUS = var0.getProperty("AltPcBangPointsBonus", 0);
      ALT_PCBANG_POINTS_DELAY = var0.getProperty("AltPcBangPointsDelay", 20);
      ALT_PCBANG_POINTS_MIN_LVL = var0.getProperty("AltPcBangPointsMinLvl", 1);
      ALT_PCBANG_CHECK_HWID = var0.getProperty("AltPcBangPointsCheckHwid", false);
      ALT_DEBUG_ENABLED = var0.getProperty("AltDebugEnabled", false);
      ALT_DEBUG_PVP_ENABLED = var0.getProperty("AltDebugPvPEnabled", false);
      ALT_DEBUG_PVP_DUEL_ONLY = var0.getProperty("AltDebugPvPDuelOnly", true);
      ALT_DEBUG_PVE_ENABLED = var0.getProperty("AltDebugPvEEnabled", false);
      ALT_LIMIT_IP_WINDOW = var0.getProperty("AltWindowLimitsByIP", 0);
      ALT_LIMIT_HWID_WINDOW = var0.getProperty("AltWindowLimitsByHWID", 0);
      ALT_LIMIT_PREMIUM_ACCOUNT = var0.getProperty("AltCheckPremiumForStartGame", false);
      ALT_MULTISELL_DEBUG = var0.getProperty("AltDebugMultisellEnabled", true);
      ALT_PARTY_DISTRIBUTION_RANGE = var0.getProperty("AltPartyDistributionRange", 1500);
      ALT_PARTY_DISTRIBUTION_DIFF_LEVEL_LIMIT = var0.getProperty("AltPartyDistributionLevelLimit", 20);
      ALT_PARTY_BONUS = var0.getProperty("AltPartyBonus", new double[]{1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 2.0, 2.1, 2.2});
      ALT_PARTY_BONUS_FOR_QUESTS = var0.getProperty("AltPartyBonusForQuestDrop", false);
      ALT_REMOVE_SKILLS_ON_DELEVEL = var0.getProperty("AltRemoveSkillsOnDelevelThreshold", 10);
      ALT_HOT_SPIRIT_CHANCE_DEBUFF = var0.getProperty("AltDebuffsHotSpiritChance", 5);
      ALT_TELEPORT_FROM_SEVEN_SING_MONSTER = var0.getProperty("SeventSingMobProtection", true);
      ALT_ENABLE_SEVEN_SING_TELEPORTER_PROTECTION = var0.getProperty("SeventSingTeleporterProtection", true);
      ALT_MAMONS_CHECK_SEVEN_SING_STATUS = var0.getProperty("MammonCheckRegistrationOnSevenSing", true);
      ALT_CABAL_SEVEN_SING_SHOUT = var0.getProperty("SevenSingOratorCanTalk", true);
      ALT_SEVEN_SING_STATIC_EVENT_PERIOD_SPAWN = var0.getProperty("SevenSingStaticEventPeriodSpawn", false);
      ALLOW_CH_DOOR_OPEN_ON_CLICK = var0.getProperty("AllowChDoorOpenOnClick", true);
      ALT_CH_ALL_BUFFS = var0.getProperty("AltChAllBuffs", false);
      ALT_CH_ALLOW_1H_BUFFS = var0.getProperty("AltChAllowHourBuff", false);
      ALT_CH_SIMPLE_DIALOG = var0.getProperty("AltChSimpleDialog", false);
      ALT_SHOW_SERVER_TIME = var0.getProperty("ShowServerTime", false);
      ALT_FISH_CHAMPIONSHIP_ENABLED = var0.getProperty("AltFishChampionshipEnabled", true);
      ALT_FISH_CHAMPIONSHIP_REWARD_ITEM = var0.getProperty("AltFishChampionshipRewardItemId", 57);
      ALT_FISH_CHAMPIONSHIP_REWARD_1 = var0.getProperty("AltFishChampionshipReward1", 800000);
      ALT_FISH_CHAMPIONSHIP_REWARD_2 = var0.getProperty("AltFishChampionshipReward2", 500000);
      ALT_FISH_CHAMPIONSHIP_REWARD_3 = var0.getProperty("AltFishChampionshipReward3", 300000);
      ALT_FISH_CHAMPIONSHIP_REWARD_4 = var0.getProperty("AltFishChampionshipReward4", 200000);
      ALT_FISH_CHAMPIONSHIP_REWARD_5 = var0.getProperty("AltFishChampionshipReward5", 100000);
      ALT_HBCE_FAIR_PLAY = var0.getProperty("HBCEFairPlay", false);
      ALT_PET_INVENTORY_LIMIT = var0.getProperty("AltPetInventoryLimit", 12);
      ALT_SET_TITLE_OWNER_TO_SUMMON = var0.getProperty("AltOwnerNameToSummonTitle", false);
      ALT_SAVE_SERVITOR_BUFF = var0.getProperty("AltSaveServitorBuffs", false);
      String[] var11 = var0.getProperty("RecommendationFlushTime", new String[]{"13", "00"}, ":");
      REC_FLUSH_HOUR = Integer.parseInt(var11[0]);
      REC_FLUSH_MINUTE = Integer.parseInt(var11[1]);
      ALT_CHECK_CERTIFICATION_ITEMS = var0.getProperty("AltCheckCertificationItems", true);
      ALT_PASSIVE_NOBLESS_ID = var0.getProperty("NoblessPassiveSkillId", 0);
      CUSTOM_HERO_STATUS_TIME = var0.getProperty("CustomHeroExpireTime", 1L) * 60L * 60L;
      ALT_ALLOW_DELAY_NPC_TALK = var0.getProperty("AltDelayOnMoveAfterTalkWithNPC", false);
      ALT_SPREADING_AFTER_TELEPORT = var0.getProperty("AltSpreadingTeleportPoints", true);
      ALT_TELEPORT_PROTECTION = var0.getProperty("AltTeleportProtection", false);
      ALT_TELEPORT_PROTECTION_TIME = var0.getProperty("AltTeleportProtectionTime", 10);
      ALT_INITIAL_QUESTS = var0.getProperty("AltInitialQuests", new int[]{255});
      ALT_MAX_PARTY_SIZE = var0.getProperty("AltMaxPartySize", 9);
      ALT_PARTY_CLASS_LIMIT = new LinkedHashMap<>();

      for (String var20 : StringUtils.split(var0.getProperty("AltPartyClassLimits", ""), ';')) {
         int var21 = StringUtils.indexOfAny(var20, new char[]{':', ',', '='});
         if (var21 >= 0) {
            ALT_PARTY_CLASS_LIMIT.put(Integer.parseInt(var20.substring(0, var21).trim()), Integer.parseInt(var20.substring(var21 + 1).trim()));
         }
      }
   }

   public static void loadBossSettings() {
      ExProperties var0 = load("config/bosses.properties");
      FWA_LIMITUNTILSLEEPANTHARAS = var0.getProperty("AntharasSleepTime", 20L) * 60000L;
      FWA_FIXTIMEINTERVALOFANTHARAS = var0.getProperty("AntharasRespawnTimeInterval", 264L) * 3600000L;
      FWA_RANDOMINTERVALOFANTHARAS = var0.getProperty("AntharasRandomSpawnAddTime", 0L) * 60000L;
      ANTHARAS_CLEAR_ZONE_IF_ALL_DIE = var0.getProperty("AntharasClearZoneifAllDie", 1L) * 60000L;
      FWA_FIXTIMEPATTERNOFANTHARAS = var0.getProperty("AntharasRespawnTimePattern", "");
      FWA_APPTIMEOFANTHARAS = var0.getProperty("AntharasSpawnIntervalInZone", 10L) * 60000L;
      FWA_LIMITMAXUNTILSLEEPANTHARAS = var0.getProperty("AntharasMaxTime", -1L) * 60000L;
      FWA_ALLOW_ANTHARAS_MINION = var0.getProperty("AntharasAllowMinions", false);
      FWV_LIMITUNTILSLEEPVALAKAS = var0.getProperty("ValakasSleepTime", 20L) * 60000L;
      FWV_FIXINTERVALOFVALAKAS = var0.getProperty("ValakasRespawnTimeInterval", 264L) * 3600000L;
      FWV_RANDOMINTERVALOFVALAKAS = var0.getProperty("ValakasRandomSpawnAddTime", 0L) * 60000L;
      FWA_FIXTIMEPATTERNOFVALAKAS = var0.getProperty("ValakasRespawnTimePattern", "");
      VALAKAS_CLEAR_ZONE_IF_ALL_DIE = var0.getProperty("ValakasClearZoneifAllDie", 1L) * 60000L;
      FWA_LIMITMAXUNTILSLEEPVALAKAS = var0.getProperty("ValakasMaxTime", -1L) * 60000L;
      FWF_FIXINTERVALOFFRINTEZZA = var0.getProperty("FrintezzaRespawnTimeInterval", 48L) * 3600000L;
      FWF_RANDOMINTERVALOFFRINTEZZA = var0.getProperty("FrintezzaRandomSpawnAddTime", 0L) * 60000L;
      FWA_FIXTIMEPATTERNOFFRINTEZZA = var0.getProperty("FrintezzaRespawnTimePattern", "");
      FRINTEZZA_TOMB_TIMEOUT = var0.getProperty("FrintezzaTombPassTime", 35);
      FRINTEZZA_MIN_PARTY_IN_CC = var0.getProperty("FrintezzaMinPartyInCC", 4);
      FRINTEZZA_MAX_PARTY_IN_CC = var0.getProperty("FrintezzaMaxPartyInCC", 5);
      FRINTEZZA_MIN_DISTANCE_FOR_ENTRANCE = var0.getProperty("FrintezzaMinDistanceForEntrance", 300);
      FWV_APPTIMEOFVALAKAS = var0.getProperty("ValakasSpawnIntervalInZone", 10L) * 60000L;
      MINIONS_RESPAWN_INTERVAL = var0.getProperty("AllMinionsRespawnInterval", 6) * 60000;
      RAID_TELE_TO_HOME_FROM_PVP_ZONES = var0.getProperty("ReturnToHomeBossesFromPvPZones", false);
      RAID_TELE_TO_HOME_FROM_TOWN_ZONES = var0.getProperty("ReturnToHomeBossesFromTownZones", true);
      FWB_LIMITUNTILSLEEPBAIUM = var0.getProperty("BaiumSleepTime", 30L) * 60000L;
      FWB_FIXINTERVALOFBAIUM = var0.getProperty("BaiumSpawnTimeInterval", 120L) * 3600000L;
      FWA_FIXTIMEPATTERNOFBAIUM = var0.getProperty("BaiumRespawnTimePattern", "");
      BAIUM_CLEAR_ZONE_IF_ALL_DIE = var0.getProperty("BaiumClearZoneifAllDie", 1L) * 60000L;
      FWB_RANDOMINTERVALOFBAIUM = var0.getProperty("BaiumRandomSpawnAddTime", 480L) * 60000L;
      FWA_LIMITMAXUNTILSLEEPBAIUM = var0.getProperty("BaiumMaxTime", -1L) * 60000L;
      FWS_ACTIVITYTIMEOFMOBS = var0.getProperty("SailrenActivityMobsTime", 120L) * 60000L;
      SAILREN_CLEAR_ZONE_IF_ALL_DIE = var0.getProperty("SailrenClearZoneifAllDie", 1L) * 60000L;
      FWA_FIXTIMEPATTERNOFSAILREN = var0.getProperty("SailrenRespawnTimePattern", "");
      FWS_FIXINTERVALOFSAILRENSPAWN = var0.getProperty("RespawnSailrenInterval", 24L) * 3600000L;
      FWS_RANDOMINTERVALOFSAILRENSPAWN = var0.getProperty("RandomRespawnSailrenInterval", 1440L) * 60000L;
      FWS_INTERVALOFNEXTMONSTER = var0.getProperty("SailrenMobsRespawnInterval", 1L) * 60000L;
      ENABLE_ON_TIME_DOOR_OPEN = var0.getProperty("EnableOnTimeDoorOpen", false);
      ON_TIME_OPEN_DOOR_ID = var0.getProperty("EnableOnTimeDoorId", 21240006);
      ON_TIME_OPEN_PATTERN = var0.getProperty("DoorInTimePattern", "* 22 * * *");
      ZAKEN_USE_TELEPORT = var0.getProperty("ZakenUseTeleport", true);
      ORFEN_USE_TELEPORT = var0.getProperty("OrfenUseTeleport", true);
      CAN_ATTACK_FROM_ANOTHER_ZONE_TO_EPIC = var0.getProperty("CanAttackFromAnotherZoneToEpic", false);
      ALLOW_BAIUM_RAID = var0.getProperty("AllowBaiumRaid", true);
      BAIUM_CHECK_QUEST_ON_AWAKE = var0.getProperty("BaiumCheckQuestForAwake", true);
      VALAKAS_ANNOUNCE_PREPARING_SPAWN = var0.getProperty("ValakasAnnouncePreparing", false);
      ANTHARAS_ANNOUNCE_PREPARING_SPAWN = var0.getProperty("AntharasAnnouncePreparing", false);
   }

   public static void loadFarmConfig() {
      ExProperties var0 = load("config/auto_farm.properties");
      ALLOW_AUTO_FARM = var0.getProperty("AllowAutoFarm", false);
      FARM_ONLINE_TYPE = var0.getProperty("AutoFarmOnlineType", false);
      AUTO_FARM_FOR_PREMIUM = var0.getProperty("AutoFarmOnlyForPremium", false);
      AUTO_FARM_FREE = var0.getProperty("AutoFarmIsFree", false);
      PREMIUM_FARM_FREE = var0.getProperty("AutoFarmIsFreeForPremium", false);
      String[] var1 = var0.getProperty("AutoFarmPriceList", "").split(";");
      AUTO_FARM_PRICES = new HashMap<>(var1.length);

      for (String var5 : var1) {
         String[] var6 = var5.split(",");
         if (var6.length == 2) {
            try {
               AUTO_FARM_PRICES.put(Integer.parseInt(var6[0]), var6[1]);
            } catch (NumberFormatException var9) {
            }
         }
      }

      ATTACK_SKILL_CHANCE = var0.getProperty("AttackSkillChance", 100);
      ATTACK_SKILL_PERCENT = var0.getProperty("AttackSkillPercent", 5);
      CHANCE_SKILL_CHANCE = var0.getProperty("ChanceSkillChance", 100);
      CHANCE_SKILL_PERCENT = var0.getProperty("ChanceSkillPercent", 5);
      SELF_SKILL_CHANCE = var0.getProperty("SelfSkillChance", 100);
      SELF_SKILL_PERCENT = var0.getProperty("SelfSkillPercent", 5);
      HEAL_SKILL_CHANCE = var0.getProperty("HealSkillChance", 100);
      HEAL_SKILL_PERCENT = var0.getProperty("HealSkillPercent", 30);
      SUMMON_ATTACK_SKILL_CHANCE = var0.getProperty("SummonAttackSkillChance", 100);
      SUMMON_ATTACK_SKILL_PERCENT = var0.getProperty("SummonAttackSkillPercent", 5);
      SUMMON_SELF_SKILL_CHANCE = var0.getProperty("SummonSelfSkillChance", 100);
      SUMMON_SELF_SKILL_PERCENT = var0.getProperty("SummonSelfSkillPercent", 5);
      SUMMON_HEAL_SKILL_CHANCE = var0.getProperty("SummonHealSkillChance", 100);
      SUMMON_HEAL_SKILL_PERCENT = var0.getProperty("SummonHealSkillPercent", 30);
      SHORTCUT_PAGE = var0.getProperty("ShortCutPage", 10);
      SEARCH_DISTANCE = var0.getProperty("SearchDistance", 2000);
      FARM_TYPE = var0.getProperty("AutoFarmType", 0);
      FARM_INTERVAL_TASK = var0.getProperty("AutoFarmIntervalTask", 500);
      SKILLS_EXTRA_DELAY = var0.getProperty("SkillsExtraDelay", 5) * 1000L;
      KEEP_LOCATION_DELAY = var0.getProperty("KeepLocationDelay", 5) * 1000L;
      RUN_CLOSE_UP_DELAY = var0.getProperty("RunCloseUpDelay", 2) * 1000L;
      RUN_CLOSE_UP_DISTANCE = var0.getProperty("RunCloseUpDistance", 100);
      ALLOW_FARM_FREE_TIME = var0.getProperty("AllowFarmFreeTime", false);
      REFRESH_FARM_TIME = var0.getProperty("AllowRefreshFarmTime", false);
      FARM_FREE_TIME = var0.getProperty("FarmFreeTime", 3);
      ALLOW_CHECK_HWID_LIMIT = var0.getProperty("AllowCheckHwidLimits", false);
      FARM_ACTIVE_LIMITS = var0.getProperty("FarmActiveLimits", 3);
      AUTO_FARM_IGNORED_NPC_ID = var0.getProperty("AutoFarmIgnoreMobIds", ArrayUtils.EMPTY_INT_ARRAY);
      SERVICES_AUTO_FARM_ABNORMAL = AbnormalEffect.getByName(var0.getProperty("AutoFarmAbnormalEffectName", "null"));
      SERVICE_AUTO_FARM_SET_RED_RING = var0.getProperty("AutoFarmSetRedRing", false);
      AUTO_FARM_LIMIT_ZONE_NAMES = new HashSet<>(Arrays.asList(var0.getProperty("AutoFarmProhibitedZones", ArrayUtils.EMPTY_STRING_ARRAY)));
      String[] var10 = var0.getProperty("FarmExpendLimitPrice", "4037,1").split(",");

      try {
         FARM_EXPEND_LIMIT_PRICE[0] = Integer.parseInt(var10[0]);
         FARM_EXPEND_LIMIT_PRICE[1] = Integer.parseInt(var10[1]);
      } catch (NumberFormatException var8) {
      }
   }

   private static Config.RateBonusInfo[] bd() {
      ArrayList var0 = new ArrayList();

      try {
         SAXReader var1 = l2.commons.data.xml.helpers.XmlReaderHelper.createNonValidatingReader();
         Document var2 = var1.read(new File("config/services_rate_bonus.xml"));
         Element var3 = var2.getRootElement();
         Iterator var4 = var3.elementIterator();

         while (var4.hasNext()) {
            Element var5 = (Element)var4.next();
            if ("rate_bonus".equalsIgnoreCase(var5.getName())) {
               int var6 = Integer.parseInt(var5.attributeValue("id"));
               int var7 = Integer.parseInt(var5.attributeValue("consume_item_id"));
               long var8 = Long.parseLong(var5.attributeValue("consume_item_amount"));
               Config.RateBonusInfo var10 = new Config.RateBonusInfo(var6, var7, var8);
               Iterator var11 = var5.elementIterator();

               while (var11.hasNext()) {
                  Element var12 = (Element)var11.next();
                  if ("exp".equalsIgnoreCase(var12.getName())) {
                     var10.rateXp = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("sp".equalsIgnoreCase(var12.getName())) {
                     var10.rateSp = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("exp_raid".equalsIgnoreCase(var12.getName())) {
                     var10.rateRaidXp = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("sp_raid".equalsIgnoreCase(var12.getName())) {
                     var10.rateRaidSp = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("quest_reward".equalsIgnoreCase(var12.getName())) {
                     var10.questRewardRate = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("quest_adena_reward".equalsIgnoreCase(var12.getName())) {
                     var10.questAdenaRewardRate = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("quest_drop".equalsIgnoreCase(var12.getName())) {
                     var10.questDropRate = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("drop_adena".equalsIgnoreCase(var12.getName())) {
                     var10.dropAdena = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("drop_items".equalsIgnoreCase(var12.getName())) {
                     var10.dropItems = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("drop_spoil".equalsIgnoreCase(var12.getName())) {
                     var10.dropSpoil = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("drop_raid_item".equalsIgnoreCase(var12.getName())) {
                     var10.dropRaidItems = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("enchant_item_mul".equalsIgnoreCase(var12.getName())) {
                     var10.enchantItemMul = Float.parseFloat(var12.attributeValue("value"));
                  } else if ("bonus_days".equalsIgnoreCase(var12.getName())) {
                     String var13 = var12.attributeValue("value");
                     String var14 = var13;
                     String var15 = null;
                     int var16 = var13.indexOf(46);
                     if (var16 > -1) {
                        var13 = var13.substring(0, var16);
                        var15 = var13.substring(var16 + 1);
                     }

                     var10.bonusTimeSeconds = Integer.parseInt(var14) * 24 * 60 * 60;
                     if (var15 != null && !var15.isEmpty()) {
                        var10.bonusTimeSeconds = var10.bonusTimeSeconds + Integer.parseInt(var15) * 60 * 60;
                     }
                  } else if ("reward".equalsIgnoreCase(var12.getName())) {
                     var10.rewardItem.add(Pair.of(Integer.parseInt(var12.attributeValue("item_id")), Long.parseLong(var12.attributeValue("item_count"))));
                  } else if ("name_color".equalsIgnoreCase(var12.getName())) {
                     var10.nameColor = Integer.decode("0x" + var12.attributeValue("value"));
                  } else if ("hwid_limits".equalsIgnoreCase(var12.getName())) {
                     var10.hwidLimits = Integer.parseInt(var12.attributeValue("value"));
                  }
               }

               var0.add(var10);
            }
         }
      } catch (Exception var17) {
         _log.error("", var17);
      }

      return (Config.RateBonusInfo[])var0.toArray(new Config.RateBonusInfo[var0.size()]);
   }

   public static void loadServicesSettings() {
      ExProperties var0 = load("config/services.properties");
      TEST_SERVER_HELPER_ENABLED = var0.getProperty("TestServerHelperEnabled", false);
      CLASS_MASTERS_CLASSES = var0.getProperty("ClassMasterClasses", "");
      COMMAND_CLASS_MASTER_ENABLED = var0.getProperty("ChatClassMasterEnabled", false);
      COMMAND_CLASS_MASTER_CLASSES = var0.getProperty("ChatClassMasterId", "");
      COMMAND_CLASS_POPUP_LIMIT = var0.getProperty("ChatClassMasterPopUpOnExp", 0);
      COMMAND_CLASS_MASTER_VOICE_COMMANDS = var0.getProperty("ChatClassMasterVoiceCommands", new String[]{"prof", "class"});
      SERVICES_CHANGE_NICK_ENABLED = var0.getProperty("NickChangeEnabled", false);
      SERVICES_CHANGE_NICK_PRICE = var0.getProperty("NickChangePrice", 100);
      SERVICES_CHANGE_NICK_ITEM = var0.getProperty("NickChangeItem", 4037);
      CUSTOM_CNAME_TEMPLATE = var0.getProperty("NickNameCustomTemplate", "[A-Za-z0-9А-Яа-я]{2,16}");
      QUEST_SELL_ENABLE = var0.getProperty("QuestSellEnabled", false);
      QUEST_SELL_QUEST_PRICES = var0.getProperty("QuestSellQuestPrices", "");
      QUEST_CAPTCHA_ENABLE = var0.getProperty("QuestCaptchaCheck", false);
      QUEST_CAPTCHA_MIN_LEVEL_FOR_CHECK = var0.getProperty("QuestCaptchaCheckMinLevel", 1);
      QUEST_CAPTCHA_CHANCE_SHOW = var0.getProperty("QuestCaptchaChanceForShow", 100);
      QUEST_CAPTCHA_CHANCE_TIMEOUT_REPEAT = var0.getProperty("QuestCaptchaCheckReplay", 60);
      APPEARANCE_APPLY_ITEM_ID = var0.getProperty("ChangeAppearanceItemId", 3435);
      APPEARANCE_SUPPORT_ITEM_ID = var0.getProperty("ChangeAppearanceCostItemId", 57);
      APPEARANCE_SUPPORT_ITEM_CNT = var0.getProperty("ChangeAppearanceCostCount", 2000000);
      APPEARANCE_CANCEL_ITEM_ID = var0.getProperty("CancelAppearanceCostItemId", 3434);
      APPEARANCE_CANCEL_PRICE = var0.getProperty("CancelAppearanceAdenaPrice", 1000);
      APPEARANCE_PROHIBITED_CHANGE = var0.getProperty("ForbiddenToChangedAppearanceItemId", ArrayUtils.EMPTY_INT_ARRAY);
      APPEARANCE_PROHIBITED_BE_CHANGED = var0.getProperty("ForbiddenToBeChangedAppearanceItemId", ArrayUtils.EMPTY_INT_ARRAY);
      SERVICES_CHANGE_CLAN_NAME_ENABLED = var0.getProperty("ClanNameChangeEnabled", false);
      SERVICES_CHANGE_CLAN_NAME_PRICE = var0.getProperty("ClanNameChangePrice", 100);
      SERVICES_CHANGE_CLAN_NAME_ITEM = var0.getProperty("ClanNameChangeItem", 4037);
      SERVICES_CHANGE_PET_NAME_ENABLED = var0.getProperty("PetNameChangeEnabled", false);
      SERVICES_CHANGE_PET_NAME_PRICE = var0.getProperty("PetNameChangePrice", 100);
      SERVICES_CHANGE_PET_NAME_ITEM = var0.getProperty("PetNameChangeItem", 4037);
      SERVICES_EXCHANGE_BABY_PET_ENABLED = var0.getProperty("BabyPetExchangeEnabled", false);
      SERVICES_EXCHANGE_BABY_PET_PRICE = var0.getProperty("BabyPetExchangePrice", 100);
      SERVICES_EXCHANGE_BABY_PET_ITEM = var0.getProperty("BabyPetExchangeItem", 4037);
      SERVICES_CHANGE_SEX_ENABLED = var0.getProperty("SexChangeEnabled", false);
      SERVICES_CHANGE_SEX_PRICE = var0.getProperty("SexChangePrice", 100);
      SERVICES_CHANGE_SEX_ITEM = var0.getProperty("SexChangeItem", 4037);
      SERVICES_CHANGE_BASE_ENABLED = var0.getProperty("BaseChangeEnabled", false);
      SERVICES_CHANGE_BASE_PRICE = var0.getProperty("BaseChangePrice", 100);
      SERVICES_CHANGE_BASE_ITEM = var0.getProperty("BaseChangeItem", 4037);
      SERVICES_CHANGE_BASE_LIST_UNCOMPATABLE = var0.getProperty("BaseChangeForAnySubFromList", false);
      SERVICES_SEPARATE_SUB_ENABLED = var0.getProperty("SeparateSubEnabled", false);
      SERVICES_SEPARATE_SUB_PRICE = var0.getProperty("SeparateSubPrice", 100);
      SERVICES_SEPARATE_SUB_ITEM = var0.getProperty("SeparateSubItem", 4037);
      SERVICES_SEPARATE_SUB_MIN_LEVEL = var0.getProperty("SeparateSubMinLevel", 1);
      SERVICES_CHANGE_NICK_COLOR_ENABLED = var0.getProperty("NickColorChangeEnabled", false);
      SERVICES_CHANGE_NICK_COLOR_PRICE = var0.getProperty("NickColorChangePrice", new int[]{100});
      SERVICES_CHANGE_NICK_COLOR_ITEM = var0.getProperty("NickColorChangeItem", new int[]{100});
      SERVICES_CHANGE_NICK_COLOR_LIST = var0.getProperty("NickColorChangeList", new String[]{"00FF00"});
      SERVICES_CHANGE_TITLE_COLOR_ENABLED = var0.getProperty("TitleColorChangeEnabled", false);
      SERVICES_CHANGE_TITLE_COLOR_LIST = var0.getProperty("TitleColorChangeList", new String[]{"00FF00"});
      SERVICES_CHANGE_TITLE_COLOR_ITEM = var0.getProperty("TitleColorChangeItem", new int[]{4037});
      SERVICES_CHANGE_TITLE_COLOR_PRICE = var0.getProperty("TitleColorChangePrice", new int[]{100});
      SERVICE_CAPTCHA_BOT_CHECK = var0.getProperty("CaptchaBotCheckService", false);
      CAPTCHA_MONSTER_KILLS_THRESHOLD = var0.getProperty("CaptchaMonsterCheckThreshold", 100);
      CAPTCHA_PENALTY_SKILL_ID = var0.getProperty("CaptchaPenaltySkillId", 5098);
      CAPTCHA_PENALTY_SKILL_LEVEL = var0.getProperty("CaptchaPenaltySkillLevel", 1);
      SERVICES_RATE_ENABLED = var0.getProperty("RateBonusEnabled", false);
      SERVICES_RATE_COMMAND_ENABLED = var0.getProperty("RateBonusVoiceCommandEnabled", false);
      SERVICES_RATE_EXPIRE_TIME_AT_ENTER_WORLD = var0.getProperty("RateBonusReportExpireTime", false);
      SERVICES_RATE_BONUS_INFO = SERVICES_RATE_ENABLED ? bd() : new Config.RateBonusInfo[0];
      SERVICES_NOBLESS_SELL_ENABLED = var0.getProperty("NoblessSellEnabled", false);
      SERVICES_NOBLESS_SELL_WITHOUT_SUBCLASS = var0.getProperty("NoblessSellWithoutSubClass", true);
      SERVICES_NOBLESS_SELL_PRICE = var0.getProperty("NoblessSellPrice", 1000);
      SERVICES_NOBLESS_SELL_ITEM = var0.getProperty("NoblessSellItem", 4037);
      NOBLESS_LEVEL_FOR_SELL = var0.getProperty("NoblessLevelForSell", 76);
      SERVICES_ITEM_NOBLESS_SELL_ENABLED = var0.getProperty("ItemNoblessSellEnabled", false);
      SERVICES_ITEM_NOBLESS_SELL_ITEM_ID = var0.getProperty("ItemNoblessSellItemId", new int[]{6641});
      SERVICES_ITEM_NOBLESS_SELL_WITHOUT_SUBCLASS = var0.getProperty("ItemNoblessSellWithoutSubClass", true);
      NOBLESS_ITEM_LEVEL_FOR_SELL = var0.getProperty("ItemNoblessLevelForSell", 76);
      SERVICES_CLAN_MAX_SELL_LEVEL = var0.getProperty("ClanMaxLevelForSell", 8);
      SERVICES_CLANLEVEL_SELL_ENABLED = var0.getProperty("ClanLevelSellEnabled", false);
      SERVICES_CLANLEVEL_SELL_ITEM = var0.getProperty("ClanLevelSellItem", new int[]{4037, 4037, 4037, 4037, 4037, 4037, 4037});
      SERVICES_CLANLEVEL_SELL_PRICE = var0.getProperty("ClanLevelSellPrice", new long[]{100L, 200L, 300L, 400L, 500L, 600L, 700L});
      SERVICES_CLAN_REPUTATION_ENABLE = var0.getProperty("ClanReputationSellEnable", false);
      SERVICES_CLAN_REPUTATION_ITEM_ID = var0.getProperty("ClanReputationItemId", 4037);
      SERVICES_CLAN_REPUTATION_ITEM_COUNT = var0.getProperty("ClanReputationItemCount", 1000);
      SERVICES_CLAN_REPUTATION_AMOUNT = var0.getProperty("ClanReputationAmountAdd", 10);
      ITEM_CLAN_REPUTATION_ENABLE = var0.getProperty("ClanReputationItemEnable", false);
      ITEM_CLAN_REPUTATION_ID = var0.getProperty("ClanReputationItemHandlerId", new int[]{9299});
      ITEM_CLAN_REPUTATION_AMOUNT = var0.getProperty("ClanReputationItemAmount", new int[]{100});
      SERVICES_CLANHELPER_ENABLED = var0.getProperty("ClanHelperEnable", false);
      SERVICES_CLANHELPER_CONFIG = var0.getProperty("ClanHelperRewardParameters", "1=2:2;2=2:3,[-200-10000,57-100500];3=2:4,[-200-20000]");
      SERVICES_CLANHELPER_ADD_FULL_CLAN_SKILLS = var0.getProperty("ClanHelperAddFullSkills", false);
      SERVICES_CLAN_BUFF_ENABLED = var0.getProperty("ClanBuffServiceEnable", false);
      SERVICES_CLAN_BUFF_SKILL_ID = var0.getProperty("ClanBuffServiceBuffId", 9200);
      SERVICES_CLAN_BUFF_LEVELS_MIN_ONLINE = var0.getProperty("ClanBuffServiceOnlineLevelUp", "10-1;20-2;30-3");
      SERVICES_CLAN_BUFF_TASK_DELAY = var0.getProperty("ClanBuffServiceTaskDelay", 5);
      SERVICES_CLANSKILL_SELL_ENABLED = var0.getProperty("ClanSkillsSellEnable", false);
      SERVICES_CLAN_SKILL_SELL_ITEM = var0.getProperty("ClanSkillsSellItemId", 4037);
      SERVICES_CLAN_SKILL_SELL_PRICE = var0.getProperty("ClanSkillsSellItemCount", 1000);
      SERVICES_CLANSKIL_SELL_MIN_LEVEL = var0.getProperty("ClanSkillMinLevel", 8);
      SERVICES_DELEVEL_SELL_ENABLED = var0.getProperty("DelevelSellEnabled", false);
      SERVICES_DELEVEL_SUBCLASS_CHECK_THRESHOLD = var0.getProperty("DelevelSellSubclassLevelThreshold", false);
      SERVICES_DELEVEL_SELL_PRICE = var0.getProperty("DelevelSellPrice", 1000);
      SERVICES_DELEVEL_SELL_ITEM = var0.getProperty("DelevelSellItem", 4037);
      SERVICES_DELEVEL_REDUCTION = var0.getProperty("DelevelReduction", 2);
      SERVICES_DELEVEL_ALLOW_FOR_SUBCLASS = var0.getProperty("DelevelAllowForSubClass", true);
      SERVICE_RESET_LEVEL_ENABLED = var0.getProperty("ResetLevelService", false);
      SERVICE_RESET_LEVEL_SUBCLASS_CHECK_THRESHOLD = var0.getProperty("ResetLevelServiceSubclassThreshold", false);
      SERVICE_RESET_LEVEL_NEED = var0.getProperty("ResetLevelServiceLevelNeed", 80);
      SERVICE_RESET_LEVEL_REMOVE = var0.getProperty("ResetLevelServiceLevelRemove", 79);
      SERVICE_RESET_LEVEL_ITEM_ID = var0.getProperty("ResetLevelServiceRewardId", 57);
      SERVICE_RESET_LEVEL_ITEM_COUNT = var0.getProperty("ResetLevelServiceRewardCount", 100);
      SERVICE_RESET_LEVEL_LIMIT = var0.getProperty("ResetLevelServiceCountLimit", -1);
      SERVICE_RESET_REUSE_DELAY = var0.getProperty("ResetLevelServiceReuseDelay", 60) * 60 * 1000;
      SERVICE_RESET_ALLOW_FOR_SUBCLASS = var0.getProperty("ResetLevelServiceAllowForSubclass", false);
      SERVICES_LEVEL_UP_SELL_ENABLED = var0.getProperty("LevelUpSellEnabled", false);
      SERVICES_LEVEL_UP_SELL_PRICE = var0.getProperty("LevelUpSellPrice", 1000);
      SERVICES_LEVEL_UP_SELL_ITEM = var0.getProperty("LevelUpSellItem", 4037);
      SERVICES_LEVEL_UP_COUNT = var0.getProperty("LevelUpForPurchasing", 1);
      SERVICES_HERO_SELL_ENABLED = var0.getProperty("HeroSellEnabled", false);
      SERVICES_HERO_SELLER_ITEM_IDS = var0.getProperty("HeroSellItemIds", new int[]{4037});
      SERVICES_HERO_SELLER_ITEM_COUNTS = var0.getProperty("HeroSellItemCounts", new long[]{1000L});
      SERVICE_HERO_STATUS_DURATIONS = var0.getProperty("HeroSellDays", new long[]{1L});
      SERVICES_OLY_POINTS_RESET = var0.getProperty("ServiceOlyPointsReset", false);
      SERVICES_OLY_POINTS_RESET_ITEM_ID = var0.getProperty("ServiceOlyPointsResetItemId", 4037);
      SERVICES_OLY_POINTS_RESET_ITEM_AMOUNT = var0.getProperty("ServiceOlyPointsResetItemAmount", 1000);
      SERVICES_OLY_POINTS_THRESHOLD = var0.getProperty("ServiceOlyPointsResetThreshold", 18);
      SERVICES_OLY_POINTS_REWARD = var0.getProperty("ServiceOlyPointsResetAmount", 18);
      SERVICES_PK_CLEAN_ENABLED = var0.getProperty("PKCleanEnabled", false);
      SERVICES_PK_CLEAN_SELL_ITEM = var0.getProperty("PKCleanItemId", 4037);
      SERVICES_PK_CLEAN_SELL_PRICE = var0.getProperty("PKCleanPrice", 1000);
      SERVICE_PROMOCODE_ENABLED = var0.getProperty("PromoCodeServiceEnable", false);
      SERVICE_PROMOCODE_COMMAND_ENABLED = var0.getProperty("PromoCodeCommandEnable", false);
      SERVICES_PK_ANNOUNCE = var0.getProperty("PvPPkKillAnnounce", 0);
      SERVICES_ALLOW_WYVERN_RIDE = var0.getProperty("AllowRideWyvernService", false);
      SERVICES_WYVERN_ITEM_ID = var0.getProperty("RideWyvernServiceItemId", 4037);
      SERVICES_KARMA_CLEAN_ENABLED = var0.getProperty("KarmaCleanServiceEnabled", false);
      SERVICES_KARMA_CLEAN_SELL_ITEM = var0.getProperty("KarmaCleanItemId", 4037);
      SERVICES_KARMA_CLEAN_SELL_PRICE = var0.getProperty("KarmaCleanPrice", 1000);
      SERVICES_EXPAND_INVENTORY_ENABLED = var0.getProperty("ExpandInventoryEnabled", false);
      SERVICES_EXPAND_INVENTORY_PRICE = var0.getProperty("ExpandInventoryPrice", 1000);
      SERVICES_EXPAND_INVENTORY_ITEM = var0.getProperty("ExpandInventoryItem", 4037);
      SERVICES_EXPAND_INVENTORY_MAX = var0.getProperty("ExpandInventoryMax", 250);
      SERVICES_EXPAND_INVENTORY_SLOT_AMOUNT = var0.getProperty("ExpandInventorySlotAmount", 1);
      SERVICES_EXPAND_WAREHOUSE_ENABLED = var0.getProperty("ExpandWarehouseEnabled", false);
      SERVICES_EXPAND_WAREHOUSE_PRICE = var0.getProperty("ExpandWarehousePrice", 1000);
      SERVICES_EXPAND_WAREHOUSE_ITEM = var0.getProperty("ExpandWarehouseItem", 4037);
      SERVICES_EXPAND_WAREHOUSE_MAX = var0.getProperty("ExpandWarehouseMax", 400);
      SERVICES_EXPAND_WAREHOUSE_SLOT_AMOUNT = var0.getProperty("ExpandWarehouseSlotAmount", 1);
      SERVICES_EXPAND_CWH_ENABLED = var0.getProperty("ExpandCWHEnabled", false);
      SERVICES_EXPAND_CWH_PRICE = var0.getProperty("ExpandCWHPrice", 1000);
      SERVICES_EXPAND_CWH_ITEM = var0.getProperty("ExpandCWHItem", 4037);
      SERVICES_EXPAND_CWH_SLOT_AMOUNT = var0.getProperty("ExpandCWHSlotAmount", 1);
      SEVICES_EXPAND_CWH_SLOT_LIMITS = var0.getProperty("ExpandCWHSlotLimit", 100);
      SERVICES_AUTO_HEAL_ACTIVE = var0.getProperty("AutoHealActive", false);
      SERVICES_SELLPETS = var0.getProperty("SellPets", "");
      SERVICES_OFFLINE_TRADE_ALLOW = var0.getProperty("AllowOfflineTrade", false);
      ALLOW_TRADE_ON_THE_MOVE = var0.getProperty("AllowTradeOnTheMove", true);
      SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE = var0.getProperty("AllowOfflineTradeOnlyOffshore", true);
      SERVICES_OFFLINE_TRADE_MIN_LEVEL = var0.getProperty("OfflineMinLevel", 0);
      SERVICES_OFFLINE_TRADE_NAME_COLOR = Integer.decode("0x" + var0.getProperty("OfflineTradeNameColor", "B0FFFF"));
      SERVICES_OFFLINE_TRADE_NAME_COLOR_CHANGE = var0.getProperty("OfflineTradeNameColorChange", false);
      SERVICES_OFFLINE_TRADE_ABNORMAL = AbnormalEffect.getByName(var0.getProperty("OfflineTradeAbnormalEffectName", "null"));
      SERVICES_OFFLINE_TRADE_PRICE_ITEM = var0.getProperty("OfflineTradePriceItem", 0);
      SERVICES_OFFLINE_TRADE_PRICE = var0.getProperty("OfflineTradePrice", 0);
      SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK = var0.getProperty("OfflineTradeDaysToKick", 14) * 86400L;
      SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART = var0.getProperty("OfflineRestoreAfterRestart", true);
      SERVICES_NO_TRADE_ONLY_OFFLINE = var0.getProperty("NoTradeOnlyOffline", false);
      SERVICES_TRADE_TAX = var0.getProperty("TradeTax", 0.0);
      SERVICES_OFFSHORE_TRADE_TAX = var0.getProperty("OffshoreTradeTax", 0.0);
      SERVICES_TRADE_TAX_ONLY_OFFLINE = var0.getProperty("TradeTaxOnlyOffline", false);
      SERVICES_OFFSHORE_NO_CASTLE_TAX = var0.getProperty("NoCastleTaxInOffshore", false);
      SERVICES_TRADE_ONLY_FAR = var0.getProperty("TradeOnlyFar", false);
      SERVICES_TRADE_MIN_LEVEL = var0.getProperty("MinLevelForTrade", 0);
      SERVICES_TRADE_RADIUS = var0.getProperty("TradeRadius", 30);
      SERVICES_GIRAN_HARBOR_ENABLED = var0.getProperty("GiranHarborZone", false);
      SERVICES_GIRAN_HARBOR_NOTAX = var0.getProperty("GiranHarborNoTax", false);
      SERVICES_GIRAN_HARBOR_PRICE = var0.getProperty("GiranHarborTelePrice", 5000);
      SERVICES_GIRAN_HARBOR_MP_REG_PRICE = var0.getProperty("GiranHarborMpRegPricePerUnit", 5);
      SERVICES_ALLOW_LOTTERY = var0.getProperty("AllowLottery", false);
      SERVICE_LOTTERY_ITEM_ID = var0.getProperty("LotteryItemId", 57);
      SERVICES_LOTTERY_PRIZE = var0.getProperty("LotteryPrize", 50000);
      SERVICES_ALT_LOTTERY_PRICE = var0.getProperty("AltLotteryPrice", 2000);
      SERVICES_LOTTERY_TICKET_PRICE = var0.getProperty("LotteryTicketPrice", 2000);
      SERVICES_LOTTERY_5_NUMBER_RATE = var0.getProperty("Lottery5NumberRate", 0.6);
      SERVICES_LOTTERY_4_NUMBER_RATE = var0.getProperty("Lottery4NumberRate", 0.4);
      SERVICES_LOTTERY_3_NUMBER_RATE = var0.getProperty("Lottery3NumberRate", 0.2);
      SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE = var0.getProperty("Lottery2and1NumberPrize", 200);
      SERVICES_ALLOW_ROULETTE = var0.getProperty("AllowRoulette", false);
      SERVICES_ROULETTE_ITEM_ID = var0.getProperty("RouletteItemId", 57);
      SERVICES_ROULETTE_MIN_BET = var0.getProperty("RouletteMinBet", 1);
      SERVICES_ROULETTE_MAX_BET = var0.getProperty("RouletteMaxBet", Integer.MAX_VALUE);
      SERVICES_ENABLE_NO_CARRIER = var0.getProperty("EnableNoCarrier", false);
      SERVICES_NO_CARRIER_MIN_TIME = var0.getProperty("NoCarrierMinTime", 0);
      SERVICES_NO_CARRIER_MAX_TIME = var0.getProperty("NoCarrierMaxTime", 90);
      SERVICES_NO_CARRIER_DEFAULT_TIME = var0.getProperty("NoCarrierDefaultTime", 60);
      DISCONNECTED_PLAYER_TITLE = var0.getProperty("NoCarrierCharTitle", "DISCONNECTED");
      DISCONNECTED_PLAYER_TITLE_COLOR = Integer.decode("0x" + var0.getProperty("NoCarrierCharTitleColor", "FF0000"));
      NO_CARRIER_PROTECTION = var0.getProperty("NoCarrierProtection", false);
      NO_CARRIER_PROTECTION_TIME = var0.getProperty("NoCarrierProtectionTime", 10);
      SERVICES_HPACP_ENABLE = var0.getProperty("ACPServiceEnableHP", false);
      SERVICES_HPACP_WORK_IN_PEACE_ZONE = var0.getProperty("ACPServiceWokAtPeaceZone", false);
      SERVICES_HPACP_MIN_PERCENT = Math.min(Math.max(0, var0.getProperty("ACPServiceHPMinPercent", 10)), 100);
      SERVICES_HPACP_MAX_PERCENT = Math.min(Math.max(0, var0.getProperty("ACPServiceHPMaxPercent", 90)), 100);
      SERVICES_HPACP_DEF_PERCENT = Math.min(
         Math.max(SERVICES_HPACP_MIN_PERCENT, var0.getProperty("ACPServiceHPDefaultPercent", 50)), SERVICES_HPACP_MAX_PERCENT
      );
      SERVICES_HPACP_POTIONS_ITEM_IDS = var0.getProperty("ACPServiceHPItemIds", new int[]{1539, 1540, 8627});
      ENABLE_ACP_ON_CHARACTER_CREATE = var0.getProperty("ACPServiceEnableOnCharacterCreate", false);
      SERVICES_HPACP_AVAILABLE_ONLY_PREMIUM = var0.getProperty("ACPServiceAvailableOnlyPremium", false);
      SERVICES_ONLINE_COMMAND_ENABLE = var0.getProperty("OnlineCommandForAll", false);
      SERVICE_COMMAND_MULTIPLIER = var0.getProperty("OnlineCommandForAllMultiplier", 1.0);
      SERVICES_BANKING_ENABLED = var0.getProperty("BankingServiceEnabled", false);
      SERVICES_DEPOSIT_ITEM_ID_NEEDED = var0.getProperty("DepositItemIdRemove", 57);
      SERVICES_DEPOSIT_ITEM_COUNT_NEEDED = var0.getProperty("DepositItemCountRemove", 1000000);
      SERVICES_DEPOSIT_ITEM_ID_GIVED = var0.getProperty("DepositItemIdGive", 6673);
      SERVICES_DEPOSIT_ITEM_COUNT_GIVED = var0.getProperty("DepositCountGive", 1);
      SERVICES_WITHDRAW_ITEM_ID_NEEDED = var0.getProperty("WithdrawItemIdRemove", 6673);
      SERVICES_WITHDRAW_ITEM_COUNT_NEEDED = var0.getProperty("WithdrawItemCountRemove", 1);
      SERVICES_WITHDRAW_ITEM_ID_GIVED = var0.getProperty("WithdrawItemIdGive", 57);
      SERVICES_WITHDRAW_ITEM_COUNT_GIVED = var0.getProperty("WithdrawCountGive", 1000000);
      SERVICES_CLAN_SUMMON_COMMAND_ENABLE = var0.getProperty("ClanLeaderSummonCommand", false);
      SERVICES_CLAN_SUMMON_COMMAND_SELL_ITEM = var0.getProperty("ClanLeaderSummonItemId", 4037);
      SERVICES_CLAN_SUMMON_COMMAND_SELL_PRICE = var0.getProperty("ClanLeaderSummonItemCount", 1);
      SERVICES_CLAN_SUMMON_COMMAND_SUMMON_CRYSTAL_COUNT = var0.getProperty("ClanSummonCrystalCount", 1);
      REUSE_DELAY_FOR_CLAN_SUMMON = var0.getProperty("ClanSummonReuseDelay", 30) * 60;
      SERVICES_WHOIAM_COMMAND_ENABLE = var0.getProperty("WhoiameCommandForAll", true);
      SERVICES_CPACP_ENABLE = var0.getProperty("ACPServiceEnableCP", false);
      SERVICES_CPACP_MIN_PERCENT = Math.min(Math.max(0, var0.getProperty("ACPServiceCPMinPercent", 10)), 100);
      SERVICES_CPACP_MAX_PERCENT = Math.min(Math.max(0, var0.getProperty("ACPServiceCPMaxPercent", 90)), 100);
      SERVICES_CPACP_DEF_PERCENT = Math.min(
         Math.max(SERVICES_CPACP_MIN_PERCENT, var0.getProperty("ACPServiceCPDefaultPercent", 50)), SERVICES_CPACP_MAX_PERCENT
      );
      SERVICES_CPACP_POTIONS_ITEM_IDS = var0.getProperty("ACPServiceCPItemIds", new int[]{5592, 5591});
      SERVICES_MPACP_ENABLE = var0.getProperty("ACPServiceEnableMP", false);
      SERVICES_MPACP_MIN_PERCENT = Math.min(Math.max(0, var0.getProperty("ACPServiceMPMinPercent", 10)), 100);
      SERVICES_MPACP_MAX_PERCENT = Math.min(Math.max(0, var0.getProperty("ACPServiceMPMaxPercent", 90)), 100);
      SERVICES_MPACP_DEF_PERCENT = Math.min(
         Math.max(SERVICES_MPACP_MIN_PERCENT, var0.getProperty("ACPServiceMPDefaultPercent", 50)), SERVICES_MPACP_MAX_PERCENT
      );
      SERVICES_MPACP_POTIONS_ITEM_IDS = var0.getProperty("ACPServiceMPItemIds", new int[]{728});
      SERVICES_BOSS_STATUS_ENABLE = var0.getProperty("BossStatusServiceEnable", false);
      SERVICES_BOSS_STATUS_ADDITIONAL_IDS = var0.getProperty("BossStatusAdditionalIds", new int[]{29001, 29006, 29014, 29022});
      SERVICES_BOSS_STATUS_FORMAT = var0.getProperty("BossStatusRespawnFormat", "HH:mm dd.MM.yyyy");
      SERVICES_RAID_STATUS_ENABLE = var0.getProperty("RaidStatusServiceEnable", false);
      SERVICES_RAID_STATUS_ADDITIONAL_IDS = var0.getProperty("RaidStatusAdditionalIds", new int[]{29001});
      SERVICES_RAID_STATUS_FORMAT = var0.getProperty("RaidStatusRespawnFormat", "HH:mm dd.MM.yyyy");
      SERVICES_PVP_PK_STATISTIC = var0.getProperty("TopPvPPKStatistic", false);
      SERVICE_AUTO_ANNOUNCE = var0.getProperty("ServiceAutoAnnounce", false);
      PVP_PK_STAT_CACHE_UPDATE_INTERVAL = var0.getProperty("TopPvPPKCacheUpdateTimeInterval", 1) * 60 * 1000L;
      PVP_PK_STAT_RECORD_LIMIT = var0.getProperty("TopPvPPKRecordLimit", 15);
      SERVICES_TOP_CLANS_STATISTIC = var0.getProperty("ClanCustomPointsStatistic", false);
      SERVICES_TOP_CLANS_STATISTIC_ANNOUNCE = var0.getProperty("ClanCustomPointsAnnounce", true);
      SERVICES_TOP_CLANS_STATISTIC_LIST = var0.getProperty("ClanCustomStatisticCount", 15);
      EVENT_CLAN_CASTLE_POINTS = new LinkedHashMap<>();
      StringTokenizer var1 = new StringTokenizer(var0.getProperty("ClanCastleCustomPoints", ""), ";,");

      while (var1.hasMoreTokens()) {
         String[] var2 = var1.nextToken().split(":");
         EVENT_CLAN_CASTLE_POINTS.put(Integer.parseInt(var2[0]), Integer.parseInt(var2[1]));
      }

      EVENT_CLAN_HERO_POINTS = var0.getProperty("ClanHeroCustomPoints", 0);
      EVENT_CLAN_RAID_BOSS_POINTS = new LinkedHashMap<>();
      var1 = new StringTokenizer(var0.getProperty("ClanRaidBossCustomPoints", ""), ";,");

      while (var1.hasMoreTokens()) {
         String[] var8 = var1.nextToken().split(":");
         EVENT_CLAN_RAID_BOSS_POINTS.put(Integer.parseInt(var8[0]), Integer.parseInt(var8[1]));
      }

      PAWNSHOP_ENABLED = var0.getProperty("PawnShopEnabled", true);
      ArrayList var7 = new ArrayList();

      for (String var5 : var0.getProperty("PawnShopItemClasses", new String[]{"WEAPON", "ARMOR", "JEWELRY"})) {
         var7.add(ItemTemplate.ItemClass.valueOf(var5));
      }

      PAWNSHOP_ITEMS_CLASSES = (ItemTemplate.ItemClass[])var7.toArray(new ItemTemplate.ItemClass[var7.size()]);
      PAWNSHOP_ITEMS_PER_PAGE = var0.getProperty("PawnShopItemsPerPage", 5);
      PAWNSHOP_MIN_ENCHANT_LEVEL = var0.getProperty("PawnShopMinEnchantLevel", 0);
      PAWNSHOP_ALLOW_SELL_AUGMENTED_ITEMS = var0.getProperty("PawnShopAllowTradeAugmentedItems", false);
      PAWNSHOP_MIN_GRADE = ItemTemplate.Grade.valueOf(var0.getProperty("PawnShopMinGrade", "A").toUpperCase());
      PAWNSHOP_CURRENCY_ITEM_IDS = var0.getProperty("PawnShopCurrencyItemIds", new int[]{57});
      PAWNSHOP_MIN_QUERY_LENGTH = var0.getProperty("PawnShopMinQueryLength", 3);
      PAWNSHOP_PRICE_SORT = var0.getProperty("PawnShopPriceSort", true);
      PAWNSHOP_TAX_ITEM_ID = var0.getProperty("PawnShopTaxItemId", 57);
      PAWNSHOP_TAX_ITEM_COUNT = var0.getProperty("PawnShopTaxItemCount", 2000L);
      PAWNSHOP_TAX_AUGMENTED_ITEM_ID = var0.getProperty("PawnShopAugmentedTaxItemId", 57);
      PAWNSHOP_TAX_AUGMENTED_ITEM_COUNT = var0.getProperty("PawnShopAugmentedTaxItemCount", 2000L);
      PAWNSHOP_REFUND_ITEM_ID = var0.getProperty("PawnShopRefundItemId", 57);
      PAWNSHOP_REFUND_ITEM_COUNT = var0.getProperty("PawnShopRefundItemCount", 1000L);
      PAWNSHOP_PROHIBITED_ITEM_IDS = var0.getProperty(
         "PawnShopProhibitedItemIds", new int[]{6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621, 6842}
      );
      ITEM_BROKER_ITEM_SEARCH = var0.getProperty("UseItemBrokerItemSearch", false);
      ITEM_BROKER_UPDATE_TIME = var0.getProperty("BrokerItemListTimeUpdate", 2) * 60 * 1000L;
      AUGMENTATION_TRANSFER_ENABLE = var0.getProperty("AugmentationTransferServiceEnabled", false);
      AUGMENTATION_TRANSFER_REQUIRED_ITEM_ID = var0.getProperty("AugmentationTransferServiceItemId", 4037);
      AUGMENTATION_TRANSFER_REQUIRED_ITEM_COUNT = var0.getProperty("AugmentationTransferServiceItemCount", 10);
      AUGMENTATION_TRANSFER_MAX_LIST_LENGTH = var0.getProperty("AugmentationTransferServiceItemListLimit", 15);
      AUGMENTATION_TRANSFER_PROHIBITED_ITEM_IDS = var0.getProperty(
         "AugmentationTransferServiceProhibitedItemIds", new int[]{6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621, 6842}
      );
      ALLOW_EVENT_GATEKEEPER = var0.getProperty("AllowEventGatekeeper", false);
      ALLOW_GLOBAL_GK = var0.getProperty("AllowSpawnGlobalGatekeeper", false);
      ALLOW_BUFFER = var0.getProperty("AllowSpawnBuffer", false);
      ALLOW_GMSHOP = var0.getProperty("AllowSpawnGMShop", false);
      ALLOW_AUCTIONER = var0.getProperty("AllowSpawnAuctioner", false);
      ALLOW_GLOBAL_SERVICES = var0.getProperty("AllowSpawnGlobalServices", false);
      ALLOW_PVP_EVENT_MANAGER = var0.getProperty("AllowSpawnPvPEventManager", false);
      ALLOW_WEDDING = var0.getProperty("AllowWedding", false);
      WEDDING_PRICE = var0.getProperty("WeddingPrice", 500000);
      WEDDING_ITEM_ID_PRICE = var0.getProperty("WeddingPriceItemId", 57);
      WEDDING_PUNISH_INFIDELITY = var0.getProperty("WeddingPunishInfidelity", true);
      WEDDING_TELEPORT = var0.getProperty("WeddingTeleport", true);
      WEDDING_TELEPORT_PRICE = var0.getProperty("WeddingTeleportPrice", 500000);
      WEDDING_TELEPORT_INTERVAL = var0.getProperty("WeddingTeleportInterval", 120);
      WEDDING_SAMESEX = var0.getProperty("WeddingAllowSameSex", true);
      WEDDING_FORMALWEAR = var0.getProperty("WeddingFormalWear", true);
      WEDDING_DIVORCE_COSTS = var0.getProperty("WeddingDivorceCosts", 20);
      WEDDING_GIVE_SALVATION_BOW = var0.getProperty("GiveWeddingBow", true);
      WEDDING_ANNOUNCE = var0.getProperty("WeddingAnnouncement", false);
      WEDDING_USE_COLOR = var0.getProperty("WeddingUseNickColor", false);
      WEDDING_NORMAL_COLOR = Integer.decode("0x" + var0.getProperty("WeddingNormalCoupleNickColor", "BF0000"));
      WEDDING_GAY_COLOR = Integer.decode("0x" + var0.getProperty("WeddingGayCoupleNickColor", "0000BF"));
      WEDDING_LESBIAN_COLOR = Integer.decode("0x" + var0.getProperty("WeddingLesbianCoupleNickColor", "BF00BF"));
      L2TOPRU_DELAY = var0.getProperty("L2TopRuDelay", 0) * 1000 * 60;
      L2TOPRU_PREFIX = var0.getProperty("L2TopRuServerPrefix");
      L2TOPRU_WEB_VOTE_URL = var0.getProperty("L2TopRuWebURL");
      L2TOPRU_SMS_VOTE_URL = var0.getProperty("L2TopRuSMSURL");
      L2TOPRU_WEB_REWARD_ITEMID = var0.getProperty("L2TopRuWebRewardItemId", 6673);
      L2TOPRU_WEB_REWARD_ITEMCOUNT = var0.getProperty("L2TopRuWebRewardItemCount", 1);
      L2TOPRU_SMS_REWARD_ITEMID = var0.getProperty("L2TopRuSMSRewardItemId", 6673);
      L2TOPRU_SMS_REWARD_ITEMCOUNT = var0.getProperty("L2TopRuSMSRewardItemCount", 10);
      L2TOPRU_SMS_REWARD_VOTE_MULTI = var0.getProperty("L2TopRuSMSRewardMultiVote", false);
      L2TOPZONE_ENABLED = var0.getProperty("L2TopZoneServiceEnabled", false);
      L2TOPZONE_VOTE_TIME_TO_LIVE = var0.getProperty("L2TopZoneVoteTimeToLive", 12) * 3600;
      L2TOPZONE_API_KEY = var0.getProperty("L2TopZoneApiKey", "");
      L2TOPZONE_REWARD_ITEM_ID = var0.getProperty("L2TopZoneRewardItemId", ArrayUtils.EMPTY_INT_ARRAY);
      L2TOPZONE_REWARD_ITEM_COUNT = var0.getProperty("L2TopZoneRewardItemCount", ArrayUtils.EMPTY_INT_ARRAY);
      L2TOPZONE_REWARD_CHANCE = var0.getProperty("L2TopZoneRewardChance", ArrayUtils.EMPTY_INT_ARRAY);
      MMO_TOP_CRON_TASK = var0.getProperty("MMOTopRuCron", "");
      MMO_TOP_VOTES_LINK = var0.getProperty("MMOTopRuLink", "");
      MMO_TOP_REWARD = var0.getProperty("MMOTopRuReward", "");
      MMO_TOP_VOTE_WINDOW_DAYS = var0.getProperty("MMOTopRuWindowDays", 30);
      MMO_TOP_VOTE_REWARD_HOURS = var0.getProperty("MMOTopRuRewardDuration", 24);
      L2HOPZONE_ENABLED = var0.getProperty("L2HopZoneServiceEnabled", false);
      L2HOPZONE_VOTE_TIME_TO_LIVE = var0.getProperty("L2HopZoneVoteTimeToLive", 12) * 3600;
      L2HOPZONE_API_KEY = var0.getProperty("L2HopZoneApiKey", "");
      L2HOPZONE_REWARD_ITEM_ID = var0.getProperty("L2HopZoneRewardItemId", ArrayUtils.EMPTY_INT_ARRAY);
      L2HOPZONE_REWARD_ITEM_COUNT = var0.getProperty("L2HopZoneRewardItemCount", ArrayUtils.EMPTY_INT_ARRAY);
      L2HOPZONE_REWARD_CHANCE = var0.getProperty("L2HopZoneRewardChance", ArrayUtils.EMPTY_INT_ARRAY);
      L2HOPZONE_REUSE_TIME = var0.getProperty("L2HopZoneReuseCommandRequest", 3) * 60 * 1000;
      L2HOPZONE_ADD_COMMAND = var0.getProperty("L2HopZoneAddCommand", "l2hopzone");
      L2JBRAZIL_ENABLED = var0.getProperty("L2JBrazilVoteEnabled", false);
      L2JBRAZIL_VOTE_TIME_TO_LIVE = var0.getProperty("L2JBrazilVoteTimeToLive", 12) * 3600;
      L2JBRAZIL_API_KEY = var0.getProperty("L2JBrazilServerName", "");
      L2JBRAZIL_REWARD_ITEM_ID = var0.getProperty("L2JBrazilRewardItemId", ArrayUtils.EMPTY_INT_ARRAY);
      L2JBRAZIL_REWARD_ITEM_COUNT = var0.getProperty("L2JBrazilRewardItemCount", ArrayUtils.EMPTY_INT_ARRAY);
      L2JBRAZIL_REWARD_CHANCE = var0.getProperty("L2JBrazilZoneRewardChance", ArrayUtils.EMPTY_INT_ARRAY);
      SERVICE_FEATHER_REVIVE_ENABLE = var0.getProperty("InstantReviveFeatherEnable", false);
      SERVICE_FEATHER_ITEM_ID = var0.getProperty("InstantReviveFeatherItemId", 9218);
      SERVICE_DISABLE_FEATHER_ON_SIEGES_AND_EPIC = var0.getProperty("DisableFeatherOnSiegeAndEpic", false);
      SERVICE_JAIL_COORDINATES = Location.parseLoc(var0.getProperty("JailLocationCoordinates", "-114648, -249384, -2984"));
      SERVICE_ANNOUNCE_SERVER_LIFE_TIME = var0.getProperty("ServerLifeTimeAnnounce", false);
      WEAR_DELAY = var0.getProperty("WearDelay", 5);
      WEAING_NON_GRADE_PRICE = var0.getProperty("WearNonGradePrice", 10);
      WEAING_D_GRADE_PRICE = var0.getProperty("WearDGradePrice", 50);
      WEAING_C_GRADE_PRICE = var0.getProperty("WearCGradePrice", 100);
      WEAING_B_GRADE_PRICE = var0.getProperty("WearBGradePrice", 200);
      WEAING_A_GRADE_PRICE = var0.getProperty("WearAGradePrice", 500);
      WEAING_S_GRADE_PRICE = var0.getProperty("WearSGradePrice", 1000);
      ENCHANT_LIMIT_ZONE_NAMES = new HashSet<>(Arrays.asList(var0.getProperty("EnchantLimitZoneNames", ArrayUtils.EMPTY_STRING_ARRAY)));
      ENCHANT_LIMIT_ZONE_ARMOR_LEVEL = var0.getProperty("EnchantLimitZoneArmor", -1);
      ENCHANT_LIMIT_ZONE_WEAPON_MAGE = var0.getProperty("EnchantLimitZoneMageWeapon", -1);
      ENCHANT_LIMIT_ZONE_WEAPON_PHYS = var0.getProperty("EnchantLimitZoneWarriorWeapon", -1);
      ENCHANT_LIMIT_ZONE_ACCESSORY = var0.getProperty("EnchantLimitZoneAccessory", -1);
   }

   public static void loadPvPSettings() {
      ExProperties var0 = load("config/pvp.properties");
      KARMA_MIN_INCREASE = var0.getProperty("MinKarmaIncrease", 240);
      KARMA_STATIC_INCREASES = var0.getProperty("StaticKarmaIncrease", -1);
      KARMA_STATIC_LOST_ON_DEATH = var0.getProperty("StaticKarmaLostOnDeath", -1);
      RATE_KARMA_LOST_STATIC = var0.getProperty("KarmaLostStaticValue", -1);
      KARMA_RATE_LOST = var0.getProperty("RateKarmaLost", -1);
      SERVICES_PK_KILL_BONUS_INTERVAL = var0.getProperty("PvPPKRewardBonusInterval", 0L) * 60000L;
      SERVICES_PK_KILL_BONUS_ENABLE = var0.getProperty("PkKillEnable", false);
      SERVICES_PVP_KILL_BONUS_ENABLE = var0.getProperty("PvPKillEnable", false);
      SERVICES_PVP_KILL_BONUS_REWARD_ITEM = var0.getProperty("PvPkillRewardItem", ArrayUtils.EMPTY_INT_ARRAY);
      SERVICES_PVP_KILL_BONUS_REWARD_COUNT = var0.getProperty("PvPKillRewardCount", ArrayUtils.EMPTY_INT_ARRAY);
      SERVICES_PVP_KILL_BONUS_REWARD_CHANCE = var0.getProperty("PvPkillRewardChance", ArrayUtils.EMPTY_FLOAT_ARRAY);
      SERVICES_PK_KILL_BONUS_REWARD_ITEM = var0.getProperty("PkkillRewardItem", ArrayUtils.EMPTY_INT_ARRAY);
      SERVICES_PK_KILL_BONUS_REWARD_COUNT = var0.getProperty("PkKillRewardCount", ArrayUtils.EMPTY_INT_ARRAY);
      SERVICES_PK_KILL_BONUS_REWARD_CHANCE = var0.getProperty("PkKillRewardChance", ArrayUtils.EMPTY_FLOAT_ARRAY);
      SERVICES_PK_PVP_BONUS_TIE_IF_SAME_IP = var0.getProperty("PkPvPSameIPKiller", false);
      SERVICES_PK_PVP_BONUS_TIE_IF_SAME_HWID = var0.getProperty("PkPvPSameHWIDKiller", false);
      PVP_INCREASE_SAME_IP_CHECK = var0.getProperty("PvPIncreaseSameIpCheck", false);
      PVP_INCREASE_SAME_HWID_CHECK = var0.getProperty("PvPIncreaseSameHwidCheck", false);
      ALLOW_DUEL = var0.getProperty("AllowDuel", true);
      PVP_HERO_CHAT_SYSTEM = var0.getProperty("PvPCountHeroVoice", false);
      PVP_COUNT_TO_CHAT = var0.getProperty("PvPCountForHeroVoice", 2000);
      KARMA_DROP_GM = var0.getProperty("CanGMDropEquipment", false);
      KARMA_NEEDED_TO_DROP = var0.getProperty("KarmaNeededToDrop", true);
      DROP_ITEMS_ON_DIE = var0.getProperty("DropOnDie", false);
      DROP_ITEMS_AUGMENTED = var0.getProperty("DropAugmented", false);
      ITEM_ANTIDROP_FROM_PK = var0.getProperty("ItemAntiDropFromPK", 0);
      KARMA_DROP_ITEM_LIMIT = var0.getProperty("MaxItemsDroppable", 10);
      MIN_PK_TO_ITEMS_DROP = var0.getProperty("MinPKToDropItems", 5);
      KARMA_RANDOM_DROP_LOCATION_LIMIT = var0.getProperty("MaxDropThrowDistance", 70);
      KARMA_DROPCHANCE_BASE = var0.getProperty("ChanceOfPKDropBase", 20.0);
      KARMA_DROPCHANCE_MOD = var0.getProperty("ChanceOfPKsDropMod", 1.0);
      NORMAL_DROPCHANCE_BASE = var0.getProperty("ChanceOfNormalDropBase", 1.0);
      DROPCHANCE_EQUIPPED_WEAPON = var0.getProperty("ChanceOfDropWeapon", 3);
      DROPCHANCE_EQUIPMENT = var0.getProperty("ChanceOfDropEquippment", 17);
      DROPCHANCE_ITEM = var0.getProperty("ChanceOfDropOther", 80);
      PVP_POINTS_AMOUNT_ADD = var0.getProperty("PvPPointsAmountAdd", 1);
      FUN_ZONE_PVP_COUNT = var0.getProperty("PvPCountingInFunZone", false);
      BATTLE_ZONE_PVP_COUNT = var0.getProperty("PvPCountingInBattleZone", false);
      SIEGE_ZONE_PVP_COUNT = var0.getProperty("PvPCountingInSiegeZone", false);
      FUN_ZONE_FLAG_ON_ENTER = var0.getProperty("FlagOnEnterInFunZone", false);
      KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<>();

      for (int var4 : var0.getProperty(
         "ListOfNonDroppableItems",
         new int[]{
            57,
            1147,
            425,
            1146,
            461,
            10,
            2368,
            7,
            6,
            2370,
            2369,
            3500,
            3501,
            3502,
            4422,
            4423,
            4424,
            2375,
            6648,
            6649,
            6650,
            6842,
            6834,
            6835,
            6836,
            6837,
            6838,
            6839,
            6840,
            5575,
            7694,
            6841,
            8181
         }
      )) {
         KARMA_LIST_NONDROPPABLE_ITEMS.add(var4);
      }

      PVP_TIME = var0.getProperty("PvPFlagTime", 40000);
      PVP_FLAG_ON_UN_FLAG_TIME = var0.getProperty("PvPFlagTimeOnFlag", 20000);
      PVP_BLINKING_UNFLAG_TIME = var0.getProperty("PvPBlinkingFlagTime", 10000);
   }

   public static void loadAISettings() {
      ExProperties var0 = load("config/ai.properties");
      AI_TASK_MANAGER_COUNT = var0.getProperty("AiTaskManagers", 1);
      AI_TASK_ATTACK_DELAY = var0.getProperty("AiTaskDelay", 1000);
      AI_TASK_ACTIVE_DELAY = var0.getProperty("AiTaskActiveDelay", 1000);
      BLOCK_ACTIVE_TASKS = var0.getProperty("BlockActiveTasks", false);
      ALWAYS_TELEPORT_HOME = var0.getProperty("AlwaysTeleportHome", false);
      RND_WALK = var0.getProperty("RndWalk", true);
      RND_WALK_RATE = var0.getProperty("RndWalkRate", 1);
      RND_ANIMATION_RATE = var0.getProperty("RndAnimationRate", 2);
      AGGRO_CHECK_INTERVAL = var0.getProperty("AggroCheckInterval", 250);
      NONAGGRO_TIME_ONTELEPORT = var0.getProperty("NonAggroTimeOnTeleport", 15000);
      NONAGGRO_TIME_ONLOGIN = var0.getProperty("NonAggroTimeOnLogin", 15000);
      GLOBAL_AGGRO_CHECK_INTERVAL = var0.getProperty("GlobalAggroCheckInterval", 10000L);
      FACTION_NOTIFY_INTERVAL = var0.getProperty("FactionNotifyInterval", 3000L);
      MAX_DRIFT_RANGE = var0.getProperty("MaxDriftRange", 100);
      MAX_PURSUE_RANGE = var0.getProperty("MaxPursueRange", 4000);
      MAX_PURSUE_UNDERGROUND_RANGE = var0.getProperty("MaxPursueUndergoundRange", 2000);
      MAX_PURSUE_RANGE_RAID = var0.getProperty("MaxPursueRangeRaid", 5000);
      RESTORE_HP_MP_ON_TELEPORT_HOME = var0.getProperty("RestoreHealthOnTeleportToHome", true);
      RESTORE_HP_MP_ON_EXCLUDED_IDS = var0.getProperty("RestoreHealthOnExcludedIds", new int[]{29028, 29068, 29020, 29045, 29062, 29065, 29006, 29022});
   }

   public static void loadGeodataSettings() {
      ExProperties var0 = load("config/geodata.properties");
      GEO_X_FIRST = var0.getProperty("GeoFirstX", 15);
      GEO_Y_FIRST = var0.getProperty("GeoFirstY", 10);
      GEO_X_LAST = var0.getProperty("GeoLastX", 26);
      GEO_Y_LAST = var0.getProperty("GeoLastY", 26);
      GEOFILES_PATTERN = var0.getProperty("GeoFilesPattern", "(\\d{2}_\\d{2})\\.(?:l2j|l2g|l2br)");
      ALLOW_GEODATA = var0.getProperty("AllowGeodata", true);
      ALLOW_FALL_FROM_WALLS = var0.getProperty("AllowFallFromWalls", false);
      ALLOW_KEYBOARD_MOVE = var0.getProperty("AllowMoveWithKeyboard", true);
      ALLOW_PAWN_PATHFIND = var0.getProperty("AllowPawnPathFind", true);
      COMPACT_GEO = var0.getProperty("CompactGeoData", false);
      CLIENT_Z_SHIFT = var0.getProperty("ClientZShift", 16);
      PATHFIND_BOOST = var0.getProperty("PathFindBoost", 2);
      PATHFIND_DIAGONAL = var0.getProperty("PathFindDiagonal", true);
      PATH_CLEAN = var0.getProperty("PathClean", true);
      PATHFIND_MAX_Z_DIFF = var0.getProperty("PathFindMaxZDiff", 32);
      MAX_Z_DIFF = var0.getProperty("MaxZDiff", 64);
      MIN_LAYER_HEIGHT = var0.getProperty("MinLayerHeight", 64);
      PATHFIND_MAX_TIME = var0.getProperty("PathFindMaxTime", 10000000);
      PATHFIND_BUFFERS = var0.getProperty("PathFindBuffers", "8x96;8x128;8x160;8x192;4x224;4x256;4x288;2x320;2x384;2x352;1x512");
   }

   public static void loadEventsSettings() {
      ExProperties var0 = load("config/events.properties");
      EVENT_CofferOfShadowsPriceRate = var0.getProperty("CofferOfShadowsPriceRate", 1.0);
      EVENT_LastHeroItemID = var0.getProperty("LastHero_bonus_id", 57);
      EVENT_LastHeroItemCOUNT = var0.getProperty("LastHero_bonus_count", 5000.0);
      EVENT_LastHeroTime = var0.getProperty("LastHero_time", 3);
      EVENT_LastHeroRate = var0.getProperty("LastHero_rate", true);
      EVENT_LastHeroChanceToStart = var0.getProperty("LastHero_ChanceToStart", 5);
      EVENT_LastHeroItemCOUNTFinal = var0.getProperty("LastHero_bonus_count_final", 10000.0);
      EVENT_LastHeroRateFinal = var0.getProperty("LastHero_rate_final", true);
      EVENT_TvTItemID = var0.getProperty("TvT_bonus_id", 57);
      EVENT_TvTItemCOUNT = var0.getProperty("TvT_bonus_count", 5000.0);
      EVENT_TvTTime = var0.getProperty("TvT_time", 3);
      EVENT_TvT_rate = var0.getProperty("TvT_rate", true);
      EVENT_TvTChanceToStart = var0.getProperty("TvT_ChanceToStart", 5);
      EVENT_DropEvent_Items = var0.getProperty("DropEvent_Items", "");
      EVENT_DropEvent_PartyItems = var0.getProperty("DropEvent_PartyItems", "");
      EVENT_DropEvent_PartyItems_Distribute_Random = var0.getProperty("DropEvent_PartyItems_Distribute_Random", true);
      EVENT_DropEvent_Rate = var0.getProperty("DropEvent_Rated", false);
      Event_DropEvent_Level_Penalty = var0.getProperty("DropEvent_LevelDiffPenalty", 9);
      Event_DropEvent_Check_Hwid = var0.getProperty("DropEvent_PartyHwid_Check", false);
      EVENT_CtFItemID = var0.getProperty("CtF_bonus_id", 57);
      EVENT_CtFItemCOUNT = var0.getProperty("CtF_bonus_count", 5000.0);
      EVENT_CtFTime = var0.getProperty("CtF_time", 3);
      EVENT_CtF_rate = var0.getProperty("CtF_rate", true);
      EVENT_CtFChanceToStart = var0.getProperty("CtF_ChanceToStart", 5);
      EVENT_TFH_POLLEN_CHANCE = var0.getProperty("TFH_POLLEN_CHANCE", 5.0);
      EVENT_GLITTMEDAL_NORMAL_CHANCE = var0.getProperty("MEDAL_CHANCE", 10.0);
      EVENT_GLITTMEDAL_GLIT_CHANCE = var0.getProperty("GLITTMEDAL_CHANCE", 0.1);
      EVENT_L2DAY_LETTER_CHANCE = var0.getProperty("L2DAY_LETTER_CHANCE", 1.0);
      EVENT_CHANGE_OF_HEART_CHANCE = var0.getProperty("EVENT_CHANGE_OF_HEART_CHANCE", 5.0);
      EVENT_CHRISTMAS_CHANCE = var0.getProperty("EVENT_CHRISTMAS_CHANCE", 1.0);
      EVENT_APIL_FOOLS_DROP_CHANCE = var0.getProperty("AprilFollsDropChance", 50.0);
      EVENT_SAVING_SNOWMAN_LOTERY_PRICE = var0.getProperty("SavingSnowmanLoteryPrice", 50000);
      EVENT_SAVING_SNOWMAN_REWARDER_CHANCE = var0.getProperty("SavingSnowmanRewarderChance", 2);
      EVENT_SAVING_SNOWMAN_LOTERY_CURENCY = var0.getProperty("SavingSnowmanLoteryCurency", 20076);
      EVENT_FINDER_REWARD_ID = var0.getProperty("EventFinderRewardId", 57);
      EVENT_FINDER_ITEM_COUNT = var0.getProperty("EventFinderRewardCount", 10000);
      EVENT_TRICK_OF_TRANS_CHANCE = var0.getProperty("TRICK_OF_TRANS_CHANCE", 10.0);
      EVENT_MARCH8_DROP_CHANCE = var0.getProperty("March8DropChance", 10.0);
      EVENT_MARCH8_PRICE_RATE = var0.getProperty("March8PriceRate", 1.0);
      EVENT_StraightHands_Items = var0.getProperty("StraightHandsRestrictedItems", new int[]{13390});
      EVENT_FinderHostageStartTime = var0.getProperty("FinderHostageStartTime", new String[0], ",");
      GVG_REWARD_ID = var0.getProperty("EventGvGItemIdRewrd", 57);
      GVG_REWARD_AMOUNT = var0.getProperty("EventGvGRewardAmount", 100L);
      EVENT_GVG_START_TIME = var0.getProperty("EventGvGStartTime", "22:00:00");
      EVENT_GVG_MIN_LEVEL = var0.getProperty("EventGvGMinParticipantLevel", 76);
      EVENT_GVG_MAX_LEVEL = var0.getProperty("EventGvGMaxParticipantLevel", 81);
      EVENT_GVG_GROUPS_LIMIT = var0.getProperty("EventGvGMaxGroups", 50);
      EVENT_GVG_MIN_PARTY_SIZE = var0.getProperty("EventGvGMinPartySize", 6);
      EVENT_GVG_REG_TIME = Math.max(5, var0.getProperty("EventGvGRegistrationTime", 10)) * 60 * 1000L;
      EVENT_PUMPKIN_GHOST_ID = var0.getProperty("HalloweenEventPumpkinGhostNPC", 40028);
      EVENT_SKOOLDIE_REWARDER = var0.getProperty("HalloweenSkooldieRewarderNPC", 40029);
      EVENT_PUMPKIN_DELAY = var0.getProperty("HalloweenEventPumpkinDelay", 120) * 60 * 1000;
      EVENT_PUMPKIN_GHOST_SHOW_TIME = var0.getProperty("HalloweenEventPumpkinTownShowTime", 10) * 60 * 1000;
      EVENT_SKOOLDIE_TIME = var0.getProperty("HalloweenEventSkooldieSpawnTime", 20) * 60 * 1000;
      EVENT_HALLOWEEN_CANDY = var0.getProperty("HalloweenCandyId", 6633);
      EVENT_HALLOWEEN_CANDY_ITEM_COUNT_NEEDED = var0.getProperty("HalloweenCandyAmount", 1);
      EVENT_HALLOWEEN_TOY_CHEST = var0.getProperty("HalloweenRewardId", 6634);
      EVENT_HALLOWEEN_TOY_CHEST_REWARD_AMOUNT = var0.getProperty("HalloweenRewardAmount", 1);
      EVENT_PUMPKIN_DROP_ITEMS = var0.getProperty("HalloweenPumpkinGhosDropItemId", new int[]{6633, 6406, 6407});
      EVENT_PUMPKIN_DROP_CHANCE = var0.getProperty("HalloweenPumpkinGhosDropItemChance", 1);
      PVP_EVENTS_RESTRICTED_SKILL_IDS = var0.getProperty("PvPEventsRestrictedSkillIds", new int[0]);
      PVP_EVENTS_RESTRICTED_CLAN_SKILLS = var0.getProperty("PvPEventsRestrictClanSkills", false);
      PVP_EVENTS_RESTRICT_ENCHANT = var0.getProperty("PvPEventsRestrictEnchant", false);
      PVP_EVENTS_RESTRICT_ENCHANT_ARMOR_LEVEL = var0.getProperty("PvPEventsEnchantStatLevelArmor", -1);
      PVP_EVENTS_RESTRICT_ENCHANT_WEAPON_MAGE = var0.getProperty("PvPEventsEnchantStatLevelMageWeapon", -1);
      PVP_EVENTS_RESTRICT_ENCHANT_WEAPON_PHYS = var0.getProperty("PvPEventsEnchantStatLevelPhysWeapon", -1);
      PVP_EVENTS_RESTRICT_ENCHANT_ACCESSORY = var0.getProperty("PvPEventsEnchantStatLevelAccessory", -1);
      PVP_EVENTS_MAGE_BUFF = new ArrayList<>();

      for (String var4 : var0.getProperty("PvPEventMageBuff", ArrayUtils.EMPTY_STRING_ARRAY)) {
         if (!var4.trim().isEmpty()) {
            String[] var5 = StringUtils.split(var4, "-:");
            PVP_EVENTS_MAGE_BUFF.add(Pair.of(Integer.parseInt(var5[0]), Integer.parseInt(var5[1])));
         }
      }

      PVP_EVENTS_WARRIOR_BUFF = new ArrayList<>();

      for (String var9 : var0.getProperty("PvPEventWarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY)) {
         if (!var9.trim().isEmpty()) {
            String[] var10 = StringUtils.split(var9, "-:");
            PVP_EVENTS_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(var10[0]), Integer.parseInt(var10[1])));
         }
      }
   }

   public static void loadOlympiadSettings() {
      ExProperties var0 = load("config/olympiad.properties");
      OLY_ENABLED = var0.getProperty("OlympiadEnabled", true);
      OLY_SPECTATION_ALLOWED = var0.getProperty("SpectationgAllowed", true);
      NPC_OLYMPIAD_GAME_ANNOUNCE = var0.getProperty("OlympiadNpcAnnounceTeleportOnStdium", true);
      ANNOUNCE_OLYMPIAD_GAME_END = var0.getProperty("EndOlympiadAnnounceSeason", false);
      OLY_RESTRICT_HWID = var0.getProperty("OlympiadHWIDCheck", true);
      OLY_RESTRICT_IP = var0.getProperty("OlympiadIPCheck", true);
      OLY_RESTORE_HPCPMP_ON_START_MATCH = var0.getProperty("OlympiadRestoreCPHPMPOnMatchStart", false);
      OLY_RESTRICT_CLASS_IDS = var0.getProperty("OlympiadProhibitClassIds", ArrayUtils.EMPTY_INT_ARRAY);
      OLYMPIAD_COMPETITION_TIME = var0.getProperty("OlympiadCompetitionTime", 5) * 60 * 1000L;
      OLYMPIAD_STADIUM_TELEPORT_DELAY = var0.getProperty("OlympiadStadiumTeleportDelay", 45);
      OLY_MAX_SPECTATORS_PER_STADIUM = var0.getProperty("MaxSpectatorPerStadium", 18);
      OLY_SEASON_TIME_CALC_MODE = Config.OlySeasonTimeCalcMode.valueOf(var0.getProperty("SeasonTimeCalcMode", "NORMAL").toUpperCase());
      OLY_SEASON_START_TIME = var0.getProperty("SeasonStartTime", "2 00:00");
      OLY_SEASON_END_TIME = var0.getProperty("SeasonEndTime", "+1:1 00:00");
      OLY_COMPETITION_START_TIME = var0.getProperty("CompetitionStartTime", "18:00");
      OLY_COMPETITION_END_TIME = var0.getProperty("CompetitionEndTime", "+1 00:00");
      OLY_COMPETITION_CUSTOM_START_TIME = var0.getProperty("CompetitionCustomStartTime", 0);
      OLY_COMPETITION_CUSTOM_END_TIME = var0.getProperty("CompetitionCustomEndTime", 0);
      OLY_BONUS_TIME = var0.getProperty("WeaklyBonusTime", "+7 18:30");
      OLY_NOMINATE_TIME = var0.getProperty("NominateTime", "+1:1 12:00");
      OLY_SEASON_START_POINTS = var0.getProperty("SeasonStartPoints", 10);
      OLY_MIN_HERO_COMPS = var0.getProperty("MinRewardableHeroComps", 15);
      OLY_MIN_HERO_WIN = var0.getProperty("MinWinHeroComps", 1);
      OLY_MIN_NOBLE_COMPS = var0.getProperty("MinRewardableNobleComps", 15);
      OLY_POINTS_SETTLEMENT = var0.getProperty("PointSettlement", new int[]{100, 75, 55, 40, 30});
      String var1 = var0.getProperty("OlyBuffs", "99:30-1,31-2,3050-1;100:32-1,33-2");
      OLY_BUFFS = new TIntObjectHashMap();
      StringTokenizer var2 = new StringTokenizer(var1, ";", false);

      while (var2.hasMoreTokens()) {
         String var3 = var2.nextToken();
         if (!var3.isEmpty()) {
            int var4 = var3.indexOf(58);
            if (var4 < 0) {
               _log.warn("Can't parse \"" + var3 + "\"");
            } else {
               int var5 = Integer.parseInt(var3.substring(0, var4));
               TIntIntHashMap var6 = (TIntIntHashMap)OLY_BUFFS.get(var5);
               if (var6 == null) {
                  OLY_BUFFS.put(var5, var6 = new TIntIntHashMap());
               }

               String var7 = var3.substring(var4 + 1);
               StringTokenizer var8 = new StringTokenizer(var7, ",", false);

               while (var8.hasMoreTokens()) {
                  String var9 = var8.nextToken();
                  int var10 = var9.indexOf("-");
                  int var11 = Integer.parseInt(var9.substring(0, var10));
                  int var12 = Integer.parseInt(var9.substring(var10 + 1));
                  var6.put(var11, var12);
               }
            }
         }
      }

      OLY_ITEMS_SETTLEMENT_PER_POINT = var0.getProperty("ItemsSettlementPerPoint", 1000);
      OLY_HERO_POINT_BONUS = var0.getProperty("HeroPointBonus", 200);
      OLY_DEFAULT_POINTS = var0.getProperty("DefaultPoints", 10);
      OLY_WBONUS_POINTS = var0.getProperty("WeaklyBonusPoints", 10);
      OLY_LOOSE_POINTS_MUL = var0.getProperty("LoosePointsMultiplier", 0.2);
      OLY_RESTRICTED_SKILL_IDS = var0.getProperty("OlyRestrictedSkillIds", new int[0]);
      OLY_HERO_REWARD_RITEMID = var0.getProperty("HeroGainRewardItemID", 6651);
      OLY_VICTORY_RITEMID = var0.getProperty("VictoryRewardItemID", new int[]{6651});
      OLY_VICTORY_CFREE_RITEMCNT = var0.getProperty("VictoryRewardClassFreeCount", new int[]{40});
      OLY_VICTORY_CBASE_RITEMCNT = var0.getProperty("VictoryRewardClassBasedCount", new int[]{65});
      OLY_VICTORY_3TEAM_RITEMCNT = var0.getProperty("VictoryRewardTeamBasedCount", new int[]{85});
      OLY_VICTORY_RITEMID_CHANCE = var0.getProperty("VictoryRewardItemIDChance", new int[]{100});
      OLY_HERO_CUSTOM_REWARD_ID = var0.getProperty("HeroAdditionalRewardId", ArrayUtils.EMPTY_INT_ARRAY);
      OLY_HERO_CUSTOM_REWARD_AMOUNT = var0.getProperty("HeroAdditionalRewardAmount", ArrayUtils.EMPTY_INT_ARRAY);
      OLY_HERO_CUSTOM_REWARD_CHANCE = var0.getProperty("HeroAdditionalRewardChance", ArrayUtils.EMPTY_INT_ARRAY);
      OLY_PAID_REGISTRATION_FOR_CLASS_FREE = var0.getProperty("ChargeParticipationFeeForClassFree", false);
      OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_ID = var0.getProperty("ChargeParticipationFeeForClassFreeItemId", 4037);
      OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_COUNT = var0.getProperty("ChargeParticipationFeeForClassFreeItemCount", 1000);
      OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL = var0.getProperty("ChargeParticipationFeeForClassBased", false);
      OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_ID = var0.getProperty("ChargeParticipationFeeForClassBasedItemId", 4037);
      OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_ID = var0.getProperty("ChargeParticipationFeeForClassBasedItemCount", 1000);
      OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE = var0.getProperty("ChargeParticipationFeeForTeamFree", false);
      OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_ID = var0.getProperty("ChargeParticipationFeeForTeamFreeItemId", 4037);
      OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_COUNT = var0.getProperty("ChargeParticipationFeeForTeamFreeCount", 1000);
      OLY_MAX_TOTAL_MATCHES = var0.getProperty("MaxTotalMatches", 70);
      OLY_CF_MATCHES = var0.getProperty("MaxClassFreeMatches", 60);
      OLY_CB_MATCHES = var0.getProperty("MaxClassBaseMatches", 30);
      OLY_TB_MATCHES = var0.getProperty("MaxTeamBaseMatches", 10);
      OLY_MIN_CF_START = var0.getProperty("MinParticipantClassFree", 11);
      OLY_MIN_CB_START = var0.getProperty("MinParticipantClassBase", 11);
      OLY_MIN_TB_START = var0.getProperty("MinParticipantTeamBase", 6);
      OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR = var0.getProperty("LimitEnchantStatLevelArmor", -1);
      OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE = var0.getProperty("LimitEnchantStatLevelMageWeapon", -1);
      OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS = var0.getProperty("LimitEnchantStatLevelPhysWeapon", -1);
      OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY = var0.getProperty("LimitEnchantStatLevelAccessory", -1);
      OLYMPIAD_WINNER_ANNOUCE = var0.getProperty("OlympiadWinnerAnnounce", false);
      OLYMPIAD_WINNER_MESSAGE = var0.getProperty("OlympiadWinnerAnnounceMessage", "3:RAMPAGE_CUSTOM_MESSAGE;5:OTHER_MESSAGE");
      OLYMPIAD_REMOVE_SKILL_REUSE_ON_STADIUM = var0.getProperty("SkillReuseRemoveOnStadium", true);
      OLYMPIAD_REMOVE_SKILL_REUSE_FROM_STADIUM = var0.getProperty("SkillReuseRemoveFromStadium", true);
      ADD_CLAN_REPUTATION_POINT_ON_HERO_GAIN = var0.getProperty("OnHeroGainMakeClanReputationReward", true);
      OLY_REMOVE_FORCE_BUFFS = var0.getProperty("ForcesChargeRemoveOnStadium", false);
      AMOUNT_CLAN_REPUTATION_POINT_ON_HERO_GAIN = var0.getProperty("OnHeroGainAmountClanReputationReward", 1000);
      OLY_STATISTIC_PAST_PERIOD = var0.getProperty("OlympiadStatisticOnPastPeriod", true);
      ALT_OLY_COMPETITION_DAYS = new LinkedHashSet<>();

      for (String var16 : var0.getProperty(
         "CustomOlyCompetitionDays", new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"}
      )) {
         switch (var16.toLowerCase()) {
            case "monday":
            case "1":
               ALT_OLY_COMPETITION_DAYS.add(2);
               break;
            case "tuesday":
            case "2":
               ALT_OLY_COMPETITION_DAYS.add(3);
               break;
            case "wednesday":
            case "3":
               ALT_OLY_COMPETITION_DAYS.add(4);
               break;
            case "thursday":
            case "4":
               ALT_OLY_COMPETITION_DAYS.add(5);
               break;
            case "friday":
            case "5":
               ALT_OLY_COMPETITION_DAYS.add(6);
               break;
            case "saturday":
            case "6":
               ALT_OLY_COMPETITION_DAYS.add(7);
               break;
            case "sunday":
            case "0":
            case "7":
               ALT_OLY_COMPETITION_DAYS.add(1);
         }
      }
   }

   public static void loadQuestRateSettings() {
      ExProperties var0 = load("config/quest_rates.properties");
      HashMap var1 = new HashMap();

      for (Object var3 : var0.keySet()) {
         String var4 = var3.toString();
         int var5 = 0;

         for (int var6 = 0; var6 < var4.length(); var6++) {
            if (var4.charAt(var6) != '_') {
               if (!Character.isDigit(var4.charAt(var6))) {
                  break;
               }

               var5 = var5 * 10 + (var4.charAt(var6) - '0');
            }
         }

         if (var5 == 0) {
            _log.warn("Can't parse quest id for quest rate \"" + var4 + "\"");
         } else {
            int var11 = var4.indexOf(46);
            if (var11 < 0) {
               _log.warn("Can't parse quest rate param \"" + var4 + "\"");
            } else {
               String var7 = var4.substring(var11 + 1);
               QuestRates var8 = (QuestRates)var1.get(var5);
               if (var8 == null) {
                  var1.put(var5, var8 = new QuestRates(var5));
               }

               try {
                  var8.updateParam(var7, var0.getProperty(var4));
               } catch (Exception var10) {
                  _log.warn("Can't process quest rate setting \"" + var4 + "\"", var10);
               }
            }
         }
      }

      QUEST_RATES.clear();
      QUEST_RATES.putAll(var1);
   }

   public static void load() {
      loadServerConfig();
      loadTelnetConfig();
      loadResidenceConfig();
      loadOtherConfig();
      loadCustomConfig();
      loadSpoilConfig();
      loadClanConfig();
      loadFormulasConfig();
      loadExperienceConfig();
      loadAltSettings();
      loadBossSettings();
      loadServicesSettings();
      loadPvPSettings();
      loadAISettings();
      loadGeodataSettings();
      loadEventsSettings();
      loadOlympiadSettings();
      loadQuestRateSettings();
      loadChatFilters();
      loadGMAccess();
      loadFarmConfig();
   }

   private Config() {
   }

   public static void loadChatFilters() {
      ChatFilterParser.getInstance().load();
   }

   public static void loadPacketFilter() {
      PacketFilterParser.getInstance().load();
   }

   public static void loadGMAccess() {
      gmlist.clear();
      loadGMAccess(new File("config/GMAccess.xml"));
      File var0 = new File("config/GMAccess.d/");
      if (var0.exists() && var0.isDirectory()) {
         for (File var4 : var0.listFiles()) {
            if (!var4.isDirectory() && var4.getName().endsWith(".xml")) {
               loadGMAccess(var4);
            }
         }
      } else {
         _log.info("Dir " + var0.getAbsolutePath() + " not exists.");
      }
   }

   public static void loadGMAccess(File var0) {
      try {
         DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
         var2.setValidating(false);
         var2.setIgnoringComments(true);
         org.w3c.dom.Document var3 = var2.newDocumentBuilder().parse(var0);

         for (Node var4 = var3.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
            for (Node var5 = var4.getFirstChild(); var5 != null; var5 = var5.getNextSibling()) {
               if (var5.getNodeName().equalsIgnoreCase("char")) {
                  PlayerAccess var6 = new PlayerAccess();

                  for (Node var7 = var5.getFirstChild(); var7 != null; var7 = var7.getNextSibling()) {
                     Class var8 = var6.getClass();
                     String var9 = var7.getNodeName();
                     if (!"#text".equalsIgnoreCase(var9)) {
                        Field var1;
                        try {
                           var1 = var8.getField(var9);
                        } catch (NoSuchFieldException var11) {
                           _log.info("Not found desclarate ACCESS name: " + var9 + " in XML Player access Object");
                           continue;
                        }

                        if (var1.getType().getName().equalsIgnoreCase("boolean")) {
                           var1.setBoolean(var6, Boolean.parseBoolean(var7.getAttributes().getNamedItem("set").getNodeValue()));
                        } else if (var1.getType().getName().equalsIgnoreCase("int")) {
                           var1.setInt(var6, Integer.valueOf(var7.getAttributes().getNamedItem("set").getNodeValue()));
                        }
                     }
                  }

                  gmlist.put(var6.PlayerID, var6);
               }
            }
         }
      } catch (Exception var12) {
         var12.printStackTrace();
      }
   }

   public static String getField(String var0) {
      Field var1 = FieldUtils.getField(Config.class, var0);
      if (var1 == null) {
         return null;
      }

      try {
         return String.valueOf(var1.get(null));
      } catch (IllegalArgumentException var3) {
      } catch (IllegalAccessException var4) {
      }

      return null;
   }

   public static boolean setField(String var0, String var1) {
      Field var2 = FieldUtils.getField(Config.class, var0);
      if (var2 == null) {
         return false;
      }

      try {
         if (var2.getType() == boolean.class) {
            var2.setBoolean(null, BooleanUtils.toBoolean(var1));
         } else if (var2.getType() == int.class) {
            var2.setInt(null, NumberUtils.toInt(var1));
         } else if (var2.getType() == long.class) {
            var2.setLong(null, NumberUtils.toLong(var1));
         } else if (var2.getType() == double.class) {
            var2.setDouble(null, NumberUtils.toDouble(var1));
         } else {
            if (var2.getType() != String.class) {
               return false;
            }

            var2.set(null, var1);
         }

         return true;
      } catch (IllegalArgumentException var4) {
         return false;
      } catch (IllegalAccessException var5) {
         return false;
      }
   }

   public static ExProperties load(String var0) {
      return load(new File(var0));
   }

   public static ExProperties load(File var0) {
      ExProperties var1 = new ExProperties();

      try {
         var1.load(var0);
      } catch (IOException var3) {
         _log.error("Error loading config : " + var0.getName() + "!");
      }

      return var1;
   }

   public enum OlySeasonTimeCalcMode {
      NORMAL,
      CUSTOM;
   }

   public static class RateBonusInfo {
      public final int id;
      public final int consumeItemId;
      public final long consumeItemAmount;
      public float rateXp = 1.0F;
      public float rateSp = 1.0F;
      public float rateRaidXp = 1.0F;
      public float rateRaidSp = 1.0F;
      public float questRewardRate = 1.0F;
      public float questAdenaRewardRate = 1.0F;
      public float questDropRate = 1.0F;
      public float dropAdena = 1.0F;
      public float dropItems = 1.0F;
      public float dropRaidItems = 1.0F;
      public float dropSpoil = 1.0F;
      public float enchantItemMul = 1.0F;
      public int hwidLimits = 1;
      public List<Pair<Integer, Long>> rewardItem = new ArrayList<>();
      public Integer nameColor = null;
      public long bonusTimeSeconds = 0L;

      public RateBonusInfo(int var1, int var2, long var3) {
         this.id = var1;
         this.consumeItemId = var2;
         this.consumeItemAmount = var3;
      }

      public Bonus makeBonus() {
         Bonus var1 = new Bonus();
         var1.setBonusExpire(this.bonusTimeSeconds + System.currentTimeMillis() / 1000L);
         var1.setRateXp(this.rateXp);
         var1.setRateSp(this.rateSp);
         var1.setRateRaidXp(this.rateRaidXp);
         var1.setRateRaidSp(this.rateRaidSp);
         var1.setQuestRewardRate(this.questRewardRate);
         var1.setQuestRewardAdenaRate(this.questAdenaRewardRate);
         var1.setQuestDropRate(this.questDropRate);
         var1.setDropAdena(this.dropAdena);
         var1.setDropItems(this.dropItems);
         var1.setDropRaidItems(this.dropRaidItems);
         var1.setDropSpoil(this.dropSpoil);
         var1.setEnchantItem(this.enchantItemMul);
         var1.setHwidsLimit(this.hwidLimits);
         return var1;
      }
   }
}
