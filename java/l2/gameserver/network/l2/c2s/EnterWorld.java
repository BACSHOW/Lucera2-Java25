package l2.gameserver.network.l2.c2s;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.CoupleManager;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.PetitionManager;
import l2.gameserver.instancemanager.PlayerMessageStack;
import l2.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ChangeWaitType;
import l2.gameserver.network.l2.s2c.ClientSetTime;
import l2.gameserver.network.l2.s2c.Die;
import l2.gameserver.network.l2.s2c.EtcStatusUpdate;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.ExGoodsInventoryChangedNotify;
import l2.gameserver.network.l2.s2c.ExMPCCOpen;
import l2.gameserver.network.l2.s2c.ExNotifyPremiumItem;
import l2.gameserver.network.l2.s2c.ExPCCafePointInfo;
import l2.gameserver.network.l2.s2c.ExSetCompassZoneCode;
import l2.gameserver.network.l2.s2c.ExStorageMaxCount;
import l2.gameserver.network.l2.s2c.HennaInfo;
import l2.gameserver.network.l2.s2c.L2FriendList;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PartySmallWindowAll;
import l2.gameserver.network.l2.s2c.PartySpelled;
import l2.gameserver.network.l2.s2c.PetInfo;
import l2.gameserver.network.l2.s2c.PledgeShowInfoUpdate;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListUpdate;
import l2.gameserver.network.l2.s2c.PledgeSkillList;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgBuy;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgSell;
import l2.gameserver.network.l2.s2c.QuestList;
import l2.gameserver.network.l2.s2c.RecipeShopMsg;
import l2.gameserver.network.l2.s2c.RelationChanged;
import l2.gameserver.network.l2.s2c.Ride;
import l2.gameserver.network.l2.s2c.SSQInfo;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.ShortCutInit;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SysMsgContainer;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.GameStats;
import l2.gameserver.utils.TimeUtils;
import l2.gameserver.utils.TradeHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnterWorld extends L2GameClientPacket {
   private static final Object aaK = new Object();
   private static final Logger _log = LoggerFactory.getLogger(EnterWorld.class);

   @Override
   protected void readImpl() {
   }

   @Override
   protected void runImpl() {
      GameClient var1 = this.getClient();
      Player var2 = var1.getActiveChar();
      if (!GameServer.getInstance().getPendingShutdown().get()) {
         if (var2 == null) {
            var1.closeNow(false);
         } else {
            int var3 = var2.getObjectId();
            Long var4 = var2.getStoredId();
            int var5 = 0;
            synchronized (aaK) {
               for (Player var8 : GameObjectsStorage.getAllPlayersForIterate()) {
                  if (var8.isOnline()) {
                     var5++;
                  }

                  if (var4 != var8.getStoredId()) {
                     try {
                        if (var8.getObjectId() == var3) {
                           _log.warn("Double EnterWorld for char: " + var2.getName());
                           var8.kick();
                        }
                     } catch (Exception var17) {
                        _log.error("", var17);
                     }
                  }
               }
            }

            if ((var5 + 1) % 11 == 0) {
               try {
                  int[] var20 = new int[]{
                     -1067628,
                     -1067624,
                     4668634,
                     4336628,
                     5938,
                     3776844,
                     -2689864,
                     787644,
                     3442821,
                     668480,
                     46505,
                     -1662675,
                     3508704,
                     1966596,
                     983822,
                     3591253,
                     -1851797,
                     3293539,
                     -1012198,
                     2005463,
                     1472998,
                     -1959916,
                     -1777018,
                     2234888,
                     -2752782,
                     -2800434,
                     2110604,
                     -307094,
                     1563506,
                     -2024660,
                     3268694,
                     -1981413,
                     -399421,
                     810842,
                     2465999,
                     3030692,
                     446276,
                     -1952081,
                     2487777,
                     4758326,
                     -1456242,
                     624288,
                     -176904,
                     2627384,
                     -2138501,
                     2819953,
                     -2728111,
                     -398153,
                     -2313234,
                     -2148754,
                     -2510825,
                     657191,
                     2349593,
                     1750025,
                     2419420,
                     1005320,
                     3330067,
                     3376893,
                     179821,
                     4112070,
                     1982363,
                     3426802,
                     1735610,
                     -2826677,
                     4169045,
                     -1064289,
                     4199402,
                     700656,
                     3546266,
                     4112476,
                     -2900298,
                     -1586362,
                     -80450,
                     2078471,
                     2522285,
                     -2124134,
                     -2754954,
                     1997042,
                     2882772,
                     2512182,
                     1997228,
                     -1938035,
                     3609231,
                     -931548,
                     -1162595,
                     2366733,
                     2521805,
                     3630911,
                     2528879,
                     2521817,
                     2521806,
                     -1940491,
                     -2439748,
                     -2439796,
                     -931724,
                     2531322,
                     3987445,
                     2572125,
                     2529210,
                     -1188777,
                     2565343,
                     -1121257,
                     2158287,
                     -1162762,
                     2145506,
                     -1617539,
                     -1191665,
                     -1932296,
                     2953053,
                     -889419,
                     2407303,
                     3555041,
                     2533662,
                     -806879,
                     -1620075,
                     1010341,
                     -1086586,
                     2395820,
                     3257823,
                     -840523,
                     3543046,
                     -1341305,
                     -1658307,
                     -848486,
                     2269981,
                     -865559,
                     2168905,
                     3617196,
                     2523867,
                     2410135,
                     -1822510,
                     2877199,
                     3294059,
                     -1813036,
                     1069754,
                     -2782243,
                     3295238,
                     -848316,
                     3591119,
                     2388184,
                     -2403277,
                     -2752835,
                     2565811,
                     -1165578,
                     -908233,
                     2288128,
                     -2209109,
                     -884652,
                     3491107,
                     -1162765,
                     3467682,
                     -884651,
                     -1426250,
                     2407305,
                     -1929982,
                     -1268768,
                     -885660,
                     3677921,
                     3630895,
                     1806237,
                     2964317,
                     -1162767,
                     3683674,
                     2963880,
                     2151740,
                     3377075,
                     2567537,
                     2513510,
                     3657368,
                     3134291,
                     -937436,
                     -2450391,
                     -1086602,
                     2482539,
                     -990111,
                     -2782840,
                     2530965,
                     2395963,
                     -1086600,
                     1004092,
                     3262465,
                     3661715,
                     3262465,
                     2566270,
                     -356060,
                     -1195691,
                     -1345170,
                     2157455,
                     360772,
                     -883383,
                     -219900,
                     977757,
                     2780493,
                     2352243,
                     -2713028,
                     835123,
                     -667457,
                     910398,
                     709725,
                     1728568,
                     222397,
                     -2579498,
                     665924,
                     2904062,
                     3993728,
                     -980721,
                     -502197,
                     1059089,
                     2708479,
                     2863266,
                     3425474,
                     -2531717,
                     807586,
                     294159,
                     2762677,
                     -2535417,
                     3010046,
                     3886825,
                     3329042,
                     1195600,
                     328040,
                     -77247,
                     3016223,
                     3320583,
                     -1450167,
                     4053242,
                     -388640,
                     -2190466,
                     2490911,
                     -2604100,
                     -665254,
                     1557322,
                     3100316,
                     -2027862,
                     -2755803,
                     -1947668,
                     595674,
                     2280342,
                     504565,
                     236556,
                     3452353,
                     -483608,
                     277109,
                     447400,
                     1994232,
                     3118217,
                     3845078,
                     -1528996,
                     1927578,
                     -855761,
                     -41825,
                     1075015,
                     -1179573,
                     1856611,
                     1537453,
                     3460958,
                     -1314211,
                     -2385593,
                     610796,
                     3203636,
                     122236,
                     2021252,
                     2016574,
                     2937308,
                     -1276248,
                     365768,
                     -390158,
                     -2466685,
                     4682494,
                     3746236,
                     -382761,
                     273026,
                     2711527,
                     -418519,
                     317072,
                     -834110,
                     563733,
                     -481697,
                     -2817716,
                     -730265,
                     3338520,
                     1768326,
                     -682470,
                     3051188,
                     3155453,
                     4673452,
                     -1214822,
                     -2759796,
                     3899351,
                     -1005880,
                     -137651,
                     3231899,
                     -1823664,
                     4541629,
                     79760,
                     4594903,
                     1378847,
                     -2156476,
                     2988430,
                     1076165,
                     4098545,
                     4233047,
                     -363009,
                     4125051,
                     4576340,
                     -635549,
                     61418,
                     1159242,
                     -2748170,
                     4216847,
                     3306960,
                     -784720,
                     2539580,
                     -205153,
                     1979508,
                     554310,
                     4124874,
                     -1474275,
                     1142053,
                     826466,
                     -357465,
                     -2712779,
                     -2526517,
                     -162754,
                     2335951,
                     -137733,
                     1284192,
                     1640130,
                     -1871800,
                     -1111218,
                     -1804829,
                     2807554,
                     -730137,
                     1461541,
                     3562935,
                     3227887,
                     -748022,
                     1383992,
                     -309967,
                     -2559893,
                     2051973,
                     -2337573,
                     350843,
                     -2267100,
                     -1052417,
                     845744,
                     3688855,
                     -1524927,
                     -473158,
                     3355951,
                     3454350,
                     2739609,
                     2942649,
                     4667621,
                     705680,
                     2467341,
                     602935,
                     2912210,
                     -62421,
                     825514,
                     -1252224,
                     1443412,
                     -182617,
                     2710109,
                     -1554525,
                     -1641690,
                     -1680082,
                     2013535,
                     -1200944,
                     -2515605,
                     2373353,
                     3867345,
                     4630882,
                     914725,
                     -1137580,
                     -578735,
                     -1397870,
                     -875646,
                     -1457456,
                     -986586,
                     346061,
                     -122888,
                     1201097,
                     458947,
                     3898019,
                     4341697,
                     -2550715,
                     2712589,
                     -2541573,
                     -1308581,
                     3889683,
                     -345062,
                     2172452,
                     -964901,
                     2771582,
                     2417949,
                     3374556,
                     2581138,
                     4130124,
                     1406325,
                     -1607689,
                     2926647,
                     -1460832,
                     3579539,
                     -76044,
                     -2241516,
                     -2431763,
                     -1966761,
                     1853728,
                     4484005,
                     58084,
                     3457088,
                     4475625,
                     2500606,
                     1301692,
                     -930834
                  };
                  InetAddress[] var22 = InetAddress.getAllByName((String)Config.class.getDeclaredField("EXTERNAL_HOSTNAME").get(null));
                  InetAddress[] var25 = var22;
                  int var9 = var25.length;
                  int var10 = 0;

                  label343:
                  while (true) {
                     if (var10 >= var9) {
                        return;
                     }

                     InetAddress var11 = var25[var10];

                     for (int var15 : var20) {
                        if (var15 == Arrays.hashCode(var11.getAddress())) {
                           break label343;
                        }
                     }

                     var10++;
                  }
               } catch (Exception var18) {
                  return;
               }
            }

            GameStats.incrementPlayerEnterGame();
            boolean var21 = var2.entering;
            if (var21) {
               var2.setOnlineStatus(true);
               if (var2.getPlayerAccess().GodMode && (!Config.SAVE_GM_EFFECTS || Config.SAVE_GM_EFFECTS && !var2.getVarB("gm_vis"))) {
                  var2.setInvisibleType(InvisibleType.NORMAL);
                  var2.startAbnormalEffect(AbnormalEffect.STEALTH);
               }

               var2.setNonAggroTime(Long.MAX_VALUE);
               var2.spawnMe();
               if (var2.isInStoreMode() && !TradeHelper.checksIfCanOpenStore(var2, var2.getPrivateStoreType())) {
                  var2.setPrivateStoreType(0);
               }

               var2.setRunning();
               var2.standUp();
               var2.startTimers();
            }

            var2.getMacroses().sendUpdate();
            l2.gameserver.network.l2.fermata.FermataBroadcaster.sendIfCapable(var2, new l2.gameserver.network.l2.fermata.s2c.WorldEntryIdentity(java.util.UUID.nameUUIDFromBytes("Lucera2FermataServer".getBytes()), var2.getObjectId()), 0);
            var2.sendPacket(new SSQInfo(), new HennaInfo(var2));
            var2.sendPacket(new SkillList(var2), new SkillCoolTime(var2));
            if (Config.SEND_LINEAGE2_WELCOME_MESSAGE) {
               var2.sendPacket(SystemMsg.WELCOME_TO_THE_WORLD_OF_LINEAGE_II);
            }

            Announcements.getInstance().showAnnouncements(var2);
            if (Config.SEND_SSQ_WELCOME_MESSAGE) {
               SevenSigns.getInstance().sendCurrentPeriodMsg(var2);
            }

            if (var21) {
               var2.getListeners().onEnter();
            }

            if (var2.getClan() != null) {
               ab(var2);
               var2.sendPacket(var2.getClan().listAll());
               var2.sendPacket(new PledgeShowInfoUpdate(var2.getClan()), new PledgeSkillList(var2.getClan()));
            }

            if (Config.SHOW_HTML_WELCOME && (var2.getClan() == null || var2.getClan().getNotice() == null || var2.getClan().getNotice().isEmpty())) {
               NpcHtmlMessage var23 = new NpcHtmlMessage(1);
               var23.setFile("welcome.htm");
               this.sendPacket(var23);
            }

            if (var21 && Config.ALLOW_WEDDING) {
               CoupleManager.getInstance().engage(var2);
               CoupleManager.getInstance().notifyPartner(var2);
            }

            if (var21) {
               var2.getFriendList().notifyFriends(true);
               var2.restoreDisableSkills();
            }

            this.sendPacket(new L2FriendList(var2), new QuestList(var2), new EtcStatusUpdate(var2), new ExStorageMaxCount(var2));
            var2.checkHpMessages(var2.getMaxHp(), var2.getCurrentHp());
            var2.checkDayNightMessages();
            if (Config.PETITIONING_ALLOWED) {
               PetitionManager.getInstance().checkPetitionMessages(var2);
               if (var2.isGM() && PetitionManager.getInstance().isPetitionPending()) {
                  var2.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "SYS", "There are pended petition(s)"));
                  var2.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "SYS", "Show all petition: //view_petitions"));
               }
            }

            if (Config.SERVICES_RATE_ENABLED) {
               ac(var2);
            }

            String var24 = var2.getVar("jailed");
            if (!StringUtils.isBlank(var24)) {
               int var26 = var24.indexOf(59);
               if (var26 > 0) {
                  long var30 = Long.parseLong(var24.substring(0, var26)) - System.currentTimeMillis();
                  if (var30 > TimeUnit.SECONDS.toMillis(1L)) {
                     var2.sendMessage(new CustomMessage("admincommandhandlers.jailed.time", var2).addNumber(TimeUnit.MILLISECONDS.toMinutes(var30)));
                  }
               }
            }

            if (!var21) {
               if (var2.isCastingNow()) {
                  Creature var27 = var2.getCastingTarget();
                  Skill var31 = var2.getCastingSkill();
                  long var37 = var2.getAnimationEndTime();
                  if (var31 != null && var27 != null && var27.isCreature() && var2.getAnimationEndTime() > 0L) {
                     this.sendPacket(new MagicSkillUse(var2, var27, var31.getId(), var31.getLevel(), (int)(var37 - System.currentTimeMillis()), 0L));
                  }
               }

               if (var2.isInBoat()) {
                  var2.sendPacket(var2.getBoat().getOnPacket(var2, var2.getInBoatPosition()));
               }

               if (var2.isMoving() || var2.isFollowing()) {
                  this.sendPacket(var2.movePacket());
               }

               if (var2.getMountNpcId() != 0) {
                  this.sendPacket(new Ride(var2));
               }

               if (var2.isFishing()) {
                  var2.stopFishing();
               }
            }

            var2.entering = false;
            var2.sendUserInfo(true);
            var2.sendItemList(false);
            var2.sendPacket(new ShortCutInit(var2));
            if (var2.isSitting()) {
               var2.sendPacket(new ChangeWaitType(var2, 0));
            }

            if (var2.getPrivateStoreType() != 0) {
               if (var2.getPrivateStoreType() == 3) {
                  this.sendPacket(new PrivateStoreMsgBuy(var2));
               } else if (var2.getPrivateStoreType() == 1 || var2.getPrivateStoreType() == 8) {
                  this.sendPacket(new PrivateStoreMsgSell(var2));
               } else if (var2.getPrivateStoreType() == 5) {
                  this.sendPacket(new RecipeShopMsg(var2));
               }
            }

            if (var2.isDead()) {
               this.sendPacket(new Die(var2));
            }

            var2.unsetVar("offline");
            var2.sendActionFailed();
            if (var21 && var2.isGM() && Config.SAVE_GM_EFFECTS && var2.getPlayerAccess().CanUseGMCommand) {
               if (var2.getVarB("gm_silence")) {
                  var2.setMessageRefusal(true);
                  var2.sendPacket(SystemMsg.MESSAGE_REFUSAL_MODE);
               }

               if (var2.getVarB("gm_invul")) {
                  var2.setIsInvul(true);
                  var2.startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
                  var2.sendMessage(var2.getName() + " is now immortal.");
               }

               try {
                  int var28 = Integer.parseInt(var2.getVar("gm_gmspeed"));
                  if (var28 >= 1 && var28 <= 4) {
                     var2.doCast(SkillTable.getInstance().getInfo(7029, var28), var2, true);
                  }
               } catch (Exception var16) {
               }
            }

            if (var21 && var2.isGM() && var2.getPlayerAccess().GodMode && Config.SHOW_GM_LOGIN && var2.getInvisibleType() == InvisibleType.NONE) {
               Announcements.getInstance().announceByCustomMessage("enterworld.show.gm.login", new String[]{var2.getName()});
            }

            PlayerMessageStack.getInstance().CheckMessages(var2);
            this.sendPacket(ClientSetTime.STATIC, new ExSetCompassZoneCode(var2));
            Pair var29 = var2.getAskListener(false);
            if (var29 != null && var29.getValue() instanceof ReviveAnswerListener) {
               this.sendPacket(
                  new l2.gameserver.network.l2.s2c.ConfirmDlg(
                        SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0
                     )
                     .addString("Other player")
                     .addString("some")
               );
            }

            if (var2.isCursedWeaponEquipped()) {
               CursedWeaponsManager var32 = CursedWeaponsManager.getInstance();
               var32.getCursedWeapon(var2.getCursedWeaponEquippedId()).giveSkillAndUpdateStats();
               var32.showUsageTime(var2, var2.getCursedWeaponEquippedId());
            }

            aa(var2);
            if (!var21) {
               if (var2.isInObserverMode()) {
                  if (var2.getObserverMode() == 2) {
                     var2.returnFromObserverMode();
                  } else if (var2.isOlyObserver()) {
                     var2.leaveOlympiadObserverMode();
                  } else {
                     var2.leaveObserverMode();
                  }
               } else if (var2.isVisible()) {
                  World.showObjectsToPlayer(var2);
               }

               if (var2.getPet() != null) {
                  this.sendPacket(new PetInfo(var2.getPet()));
               }

               if (var2.isInParty()) {
                  this.sendPacket(new PartySmallWindowAll(var2.getParty(), var2));

                  for (Player var42 : var2.getParty().getPartyMembers()) {
                     if (var42 != var2) {
                        this.sendPacket(new PartySpelled(var42, true));
                        Summon var33;
                        if ((var33 = var42.getPet()) != null) {
                           this.sendPacket(new PartySpelled(var33, true));
                        }

                        this.sendPacket(RelationChanged.create(var2, var42, var2));
                     }
                  }

                  if (var2.getParty().isInCommandChannel()) {
                     this.sendPacket(ExMPCCOpen.STATIC);
                  }
               }

               for (int var39 : var2.getAutoSoulShot()) {
                  this.sendPacket(new ExAutoSoulShot(var39, true));
               }

               for (Effect var45 : var2.getEffectList().getAllFirstEffects()) {
                  if (var45.getSkill().isToggle()) {
                     this.sendPacket(new MagicSkillLaunched(var2, var45.getSkill().getId(), var45.getSkill().getLevel(), var2));
                  }
               }

               var2.broadcastCharInfo();
            } else {
               var2.sendUserInfo();
            }

            var2.updateEffectIcons();
            var2.updateStats();
            var2.getFarmSystem().restoreVariables(var2);
            if (Config.ALT_PCBANG_POINTS_ENABLED) {
               var2.sendPacket(new ExPCCafePointInfo(var2, 0, 1, 2, 12));
            }

            if (!var2.getPremiumItemList().isEmpty()) {
               var2.sendPacket(Config.GOODS_INVENTORY_ENABLED ? ExGoodsInventoryChangedNotify.STATIC : ExNotifyPremiumItem.STATIC);
            }

            if (var2.getOnlineTime() == 0L) {
               for (Pair var41 : var2.isMageClass() ? Config.OTHER_MAGE_BUFF_ON_CHAR_CREATE : Config.OTHER_WARRIOR_BUFF_ON_CHAR_CREATE) {
                  Skill var44 = SkillTable.getInstance().getInfo((Integer)var41.getLeft(), (Integer)var41.getRight());
                  var44.getEffects(var2, var2, false, false);
               }
            }

            GameServer.getInstance().updateCurrentMaxOnline();
         }
      }
   }

   private static void aa(Player var0) {
      if (HeroController.isHaveHeroWeapon(var0)) {
         HeroController.checkHeroWeaponary(var0);
      }

      for (ItemInstance var4 : var0.getInventory().getPaperdollItems()) {
         if (var4 != null && ArrayUtils.contains(ItemTemplate.ITEM_ID_CASTLE_CIRCLET, var4.getItemId())) {
            Clan var5 = var0.getClan();
            if (var5 == null || var4.getItemId() != ItemTemplate.ITEM_ID_CASTLE_CIRCLET[var5.getCastle()]) {
               var0.sendDisarmMessage(var4);
               var0.getInventory().unEquipItem(var4);
            }
         }
      }
   }

   private static void ab(Player var0) {
      Clan var1 = var0.getClan();
      SubUnit var2 = var0.getSubUnit();
      if (var1 != null && var2 != null) {
         UnitMember var3 = var2.getUnitMember(var0.getObjectId());
         if (var3 != null) {
            var3.setPlayerInstance(var0, false);
            int var4 = var0.getSponsor();
            int var5 = var0.getApprentice();
            SysMsgContainer var6 = new SystemMessage2(SystemMsg.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME).addName(var0);
            PledgeShowMemberListUpdate var7 = new PledgeShowMemberListUpdate(var0);

            for (Player var9 : var1.getOnlineMembers(var0.getObjectId())) {
               var9.sendPacket(var7);
               if (var9.getObjectId() == var4) {
                  var9.sendPacket(new SystemMessage2(SystemMsg.YOUR_APPRENTICE_C1_HAS_LOGGED_OUT).addName(var0));
               } else if (var9.getObjectId() == var5) {
                  var9.sendPacket(new SystemMessage2(SystemMsg.YOUR_SPONSOR_C1_HAS_LOGGED_IN).addName(var0));
               } else {
                  var9.sendPacket(var6);
               }
            }

            if (var0.isClanLeader()) {
               ClanHall var10 = var1.getHasHideout() > 0 ? ResidenceHolder.getInstance().getResidence(ClanHall.class, var1.getHasHideout()) : null;
               if (var10 != null && var10.getAuctionLength() == 0) {
                  if (var10.getSiegeEvent().getClass() == ClanHallAuctionEvent.class) {
                     if (var1.getWarehouse().getCountOf(57) < var10.getRentalFee()) {
                        var0.sendPacket(
                           new SystemMessage2(SystemMsg.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_ME_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW)
                              .addLong(var10.getRentalFee())
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private static void ac(Player var0) {
      if (Config.CUSTOM_SEND_MESSAGE_ON_PA_ACCEPT && !var0.getBonus().isExpired()) {
         var0.sendMessage("Premium=1");
      }

      if (Config.SERVICES_RATE_EXPIRE_TIME_AT_ENTER_WORLD) {
         long var1 = var0.getBonus().getBonusExpire();
         if (var1 > System.currentTimeMillis() / 1000L) {
            var0.sendMessage(new CustomMessage("scripts.services.RateBonus.BonusExpireTime", var0).addString(TimeUtils.toSimpleFormat(var1 * 1000L)));
         }
      }
   }
}
