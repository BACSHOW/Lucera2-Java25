package l2.gameserver.network.l2.c2s;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.cache.ItemInfoCache;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.PetitionManager;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.chat.ChatFilters;
import l2.gameserver.model.chat.chatfilter.ChatFilter;
import l2.gameserver.model.chat.chatfilter.ChatMsg;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.MapUtils;
import l2.gameserver.utils.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Say2C extends L2GameClientPacket {
   private static final Logger _log = LoggerFactory.getLogger(Say2C.class);
   private static final Pattern adb = Pattern.compile(
      "[\b]\tType=[0-9]+[\\s]+\tID=([0-9]+)[\\s]+\tColor=[0-9]+[\\s]+\tUnderline=[0-9]+[\\s]+\tTitle=\u001b(.[^\u001b]*)[^\b]"
   );
   private static final Pattern adc = Pattern.compile("[\b]\tType=[0-9]+(.[^\b]*)[\b]");
   private String add;
   private ChatType ade;
   private String abZ;

   @Override
   protected void readImpl() {
      this.add = this.readS(Config.CHAT_MESSAGE_MAX_LEN);
      this.ade = ArrayUtils.valid(ChatType.VALUES, this.readD());
      this.abZ = this.ade == ChatType.TELL ? this.readS(Config.CNAME_MAXLEN) : null;
   }

   @Override
   protected void runImpl() {
      Player var1 = this.getClient().getActiveChar();
      if (var1 != null) {
         if (this.ade != null && this.add != null && this.add.length() != 0) {
            this.add = this.add.replaceAll("\\\\n", "\n");
            if (this.add.contains("\n")) {
               String[] var2 = this.add.split("\n");
               this.add = "";

               for (int var3 = 0; var3 < var2.length; var3++) {
                  var2[var3] = var2[var3].trim();
                  if (var2[var3].length() != 0) {
                     if (this.add.length() > 0) {
                        this.add = this.add + "\n  >";
                     }

                     this.add = this.add + var2[var3];
                  }
               }
            }

            if (this.add.length() == 0) {
               var1.sendActionFailed();
            } else if (this.add.startsWith(".")) {
               String var22 = this.add.substring(1).trim();
               String var24 = var22.split("\\s+")[0];
               String var4 = var22.substring(var24.length()).trim();
               if (var24.length() > 0) {
                  IVoicedCommandHandler var28 = VoicedCommandHandler.getInstance().getVoicedCommandHandler(var24);
                  if (var28 != null) {
                     var28.useVoicedCommand(var24, var1, var4);
                     return;
                  }
               }
            } else {
               Player var21 = this.abZ == null ? null : World.getPlayer(this.abZ);
               long var23 = System.currentTimeMillis();
               if (!var1.getPlayerAccess().CanAnnounce) {
                  label370:
                  for (ChatFilter var8 : ChatFilters.getinstance().getFilters()) {
                     if (var8.isMatch(var1, this.ade, this.add, var21)) {
                        switch (var8.getAction()) {
                           case 1:
                              var1.updateNoChannel(Integer.parseInt(var8.getValue()) * 1000L);
                              break label370;
                           case 2:
                              var1.sendMessage(new CustomMessage(var8.getValue(), var1));
                              return;
                           case 3:
                              this.add = var8.getValue();
                              break label370;
                           case 4:
                              this.ade = ChatType.valueOf(var8.getValue());
                        }
                     }
                  }
               }

               if (var1.getNoChannel() > 0L && org.apache.commons.lang3.ArrayUtils.contains(Config.BAN_CHANNEL_LIST, this.ade)) {
                  if (var1.getNoChannelRemained() > 0L) {
                     long var27 = var1.getNoChannelRemained() / 60000L;
                     var1.sendMessage(new CustomMessage("common.ChatBanned", var1).addNumber(var27));
                     return;
                  }

                  var1.updateNoChannel(0L);
               }

               if (!this.add.isEmpty()) {
                  Matcher var25 = adb.matcher(this.add);

                  while (var25.find()) {
                     int var30 = Integer.parseInt(var25.group(1));
                     ItemInstance var29 = var1.getInventory().getItemByObjectId(var30);
                     if (var29 == null) {
                        var1.sendActionFailed();
                        break;
                     }

                     ItemInfoCache.getInstance().put(var29);
                  }

                  String var31 = var1.getVar("translit");
                  if (var31 != null) {
                     var25 = adc.matcher(this.add);
                     StringBuilder var9 = new StringBuilder();
                     int var10 = 0;

                     while (var25.find()) {
                        int var34;
                        var9.append(Strings.fromTranslit(this.add.substring(var10, var34 = var25.start()), var31.equals("tl") ? 1 : 2));
                        var9.append(this.add.substring(var34, var10 = var25.end()));
                     }

                     this.add = var9.append(Strings.fromTranslit(this.add.substring(var10, this.add.length()), var31.equals("tl") ? 1 : 2)).toString();
                  }

                  Say2 var32 = new Say2(var1.getObjectId(), this.ade, var1.getName(), this.add);
                  if (this.ade != ChatType.ALL || !Config.ENABLE_WORLD_CHAT || this.add.isEmpty() || this.add.charAt(0) != Config.WORLD_CHAT_MESSAGE_HANDLER) {
                     switch (this.ade) {
                        case TELL:
                           if (var21 == null) {
                              var1.sendPacket(new SystemMessage(3).addString(this.abZ));
                              return;
                           }

                           if (var21.isInOfflineMode()) {
                              var1.sendMessage(new CustomMessage("common.PlayerInOfflineTrade", var1));
                              return;
                           }

                           if (var21.isInBlockList(var1) || var21.isBlockAll()) {
                              var1.sendPacket(Msg.YOU_HAVE_BEEN_BLOCKED_FROM_THE_CONTACT_YOU_SELECTED);
                              return;
                           }

                           if (var21.getMessageRefusal()) {
                              var1.sendPacket(Msg.THE_PERSON_IS_IN_A_MESSAGE_REFUSAL_MODE);
                              return;
                           }

                           if (var1.canTalkWith(var21)) {
                              var21.sendPacket(var32);
                           }

                           var32 = new Say2(var1.getObjectId(), this.ade, "->" + var21.getName(), this.add);
                           var1.sendPacket(var32);
                           break;
                        case SHOUT:
                           if (var1.isCursedWeaponEquipped()) {
                              var1.sendMessage(new CustomMessage("SHOUT_AND_TRADE_CHATING_CANNOT_BE_USED_SHILE_POSSESSING_A_CURSED_WEAPON", var1));
                              return;
                           }

                           if (var1.isInObserverMode()) {
                              var1.sendPacket(Msg.YOU_CANNOT_CHAT_LOCALLY_WHILE_OBSERVING);
                              return;
                           }

                           if (Config.GLOBAL_SHOUT
                              && var1.getLevel() > Config.GLOBAL_SHOUT_MIN_LEVEL
                              && var1.getPvpKills() >= Config.GLOBAL_SHOUT_MIN_PVP_COUNT) {
                              b(var1, var32);
                           } else {
                              a(var1, var32);
                           }

                           var1.sendPacket(var32);
                           break;
                        case TRADE:
                           if (var1.isCursedWeaponEquipped()) {
                              var1.sendMessage(new CustomMessage("SHOUT_AND_TRADE_CHATING_CANNOT_BE_USED_SHILE_POSSESSING_A_CURSED_WEAPON", var1));
                              return;
                           }

                           if (var1.isInObserverMode()) {
                              var1.sendPacket(Msg.YOU_CANNOT_CHAT_LOCALLY_WHILE_OBSERVING);
                              return;
                           }

                           if (Config.GLOBAL_TRADE_CHAT
                              && var1.getLevel() > Config.GLOBAL_TRADE_CHAT_MIN_LEVEL
                              && var1.getPvpKills() >= Config.GLOBAL_TRADE_MIN_PVP_COUNT) {
                              b(var1, var32);
                           } else {
                              a(var1, var32);
                           }

                           var1.sendPacket(var32);
                           break;
                        case ALL:
                           if (var1.isCursedWeaponEquipped()) {
                              var32 = new Say2(var1.getObjectId(), this.ade, var1.getTransformationName(), this.add);
                           }

                           Iterable var35 = a(var1, World.getAroundPlayers(var1));
                           if (var35 != null) {
                              for (Object __raw_var40_230 : (Iterable<?>)(Object)(var35)) {
                                 Player var40 = (Player)__raw_var40_230;
                                 if (var40 != var1 && var40.getReflection() == var1.getReflection() && !var40.isBlockAll() && !var40.isInBlockList(var1)) {
                                    var40.sendPacket(var32);
                                 }
                              }
                           }

                           var1.sendPacket(var32);
                           break;
                        case CLAN:
                           if (var1.getClan() != null) {
                              var1.getClan().broadcastToOnlineMembers(var32);
                           }
                           break;
                        case ALLIANCE:
                           if (var1.getClan() != null && var1.getClan().getAlliance() != null) {
                              var1.getClan().getAlliance().broadcastToOnlineMembers(var32);
                           }
                           break;
                        case PARTY:
                           if (var1.isInParty()) {
                              var1.getParty().broadCast(var32);
                           }
                           break;
                        case PARTY_ROOM:
                           MatchingRoom var11 = var1.getMatchingRoom();
                           if (var11 != null && var11.getType() == MatchingRoom.PARTY_MATCHING) {
                              var11.broadCast(var32);
                           }
                           break;
                        case COMMANDCHANNEL_ALL:
                           if (!var1.isInParty() || !var1.getParty().isInCommandChannel()) {
                              var1.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
                              return;
                           }

                           if (var1.getParty().getCommandChannel().getChannelLeader() == var1) {
                              var1.getParty().getCommandChannel().broadCast(var32);
                           } else {
                              var1.sendPacket(Msg.ONLY_CHANNEL_OPENER_CAN_GIVE_ALL_COMMAND);
                           }
                           break;
                        case COMMANDCHANNEL_COMMANDER:
                           if (!var1.isInParty() || !var1.getParty().isInCommandChannel()) {
                              var1.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
                              return;
                           }

                           if (var1.getParty().isLeader(var1)) {
                              var1.getParty().getCommandChannel().broadcastToChannelPartyLeaders(var32);
                           } else {
                              var1.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_ACCESS_THE_COMMAND_CHANNEL);
                           }
                           break;
                        case HERO_VOICE:
                           if (var1.isHero()
                              || var1.getPlayerAccess().CanAnnounce
                              || var1.getPvpKills() >= Config.PVP_COUNT_TO_CHAT && Config.PVP_HERO_CHAT_SYSTEM) {
                              for (Player var42 : a(var1, GameObjectsStorage.getAllPlayersForIterate())) {
                                 if (!var42.isInBlockList(var1) && !var42.isBlockAll()) {
                                    var42.sendPacket(var32);
                                 }
                              }
                           }
                           break;
                        case PETITION_PLAYER:
                        case PETITION_GM:
                           if (!PetitionManager.getInstance().isPlayerInConsultation(var1)) {
                              var1.sendPacket(new SystemMessage(745));
                              return;
                           }

                           PetitionManager.getInstance().sendActivePetitionMessage(var1, this.add);
                           break;
                        case BATTLEFIELD:
                           if (var1.getBattlefieldChatId() == 0) {
                              return;
                           }

                           for (Player var13 : GameObjectsStorage.getAllPlayersForIterate()) {
                              if (!var13.isInBlockList(var1) && !var13.isBlockAll() && var13.getBattlefieldChatId() == var1.getBattlefieldChatId()) {
                                 var13.sendPacket(var32);
                              }
                           }
                           break;
                        case MPCC_ROOM:
                           MatchingRoom var12 = var1.getMatchingRoom();
                           if (var12 != null && var12.getType() == MatchingRoom.CC_MATCHING) {
                              var12.broadCast(var32);
                           }
                           break;
                        default:
                           _log.warn("Character " + var1.getName() + " used unknown chat type: " + this.ade.ordinal() + ".");
                     }

                     Log.LogChat(this.ade.name(), var1.getName(), this.abZ, this.add, 0);
                     var1.getMessageBucket().addLast(new ChatMsg(this.ade, var21 == null ? 0 : var21.getObjectId(), this.add.hashCode(), (int)(var23 / 1000L)));
                     var1.getListeners().onSay(this.ade.ordinal(), this.abZ, this.add);
                  } else if (var1.getLevel() < Config.WORLD_CHAT_MIN_LEVEL && !var1.isGM()) {
                     var1.sendMessage(new CustomMessage("YOU_CAN_USE_WORLD_CHAT_FROM_LV_S1", var1).addNumber(Config.WORLD_CHAT_MIN_LEVEL));
                  } else if (!var1.hasBonus() && Config.WORLD_CHAT_FOR_PREMIUM_ONLY && !var1.isGM()) {
                     var1.sendMessage(new CustomMessage("common.WorldChatPremiumOnly", var1));
                  } else if (var1.isCursedWeaponEquipped()) {
                     var1.sendMessage(new CustomMessage("SHOUT_AND_TRADE_CHATING_CANNOT_BE_USED_SHILE_POSSESSING_A_CURSED_WEAPON", var1));
                  } else if (var1.isInObserverMode()) {
                     var1.sendPacket(Msg.YOU_CANNOT_CHAT_LOCALLY_WHILE_OBSERVING);
                  } else {
                     long var36 = System.currentTimeMillis();
                     long var41 = var1.getVarLong("world_chat_reuse", 0L);
                     if (var36 - var41 < Config.WORLD_CHAT_INTERVAL && !var1.isGM()) {
                        var1.sendMessage(
                           new CustomMessage("YOU_HAVE_S1_SEC_UNTIL_YOU_ARE_ABLE_TO_USE_WORLD_CHAT", var1)
                              .addNumber(TimeUnit.MILLISECONDS.toSeconds(Config.WORLD_CHAT_INTERVAL - (var36 - var41)))
                        );
                     } else {
                        int var14 = var1.getVarInt("used_world_chat_points", 0) + 1;
                        int var15 = var1.getInventory().getCountOf(Config.WORLD_CHAT_BONUS_ITEM) > 0L ? Config.WORLD_CHAT_BONUS_ITEM_CHAT_POINTS : 0;
                        int var16 = Config.WORLD_CHAT_MESSAGE_COUNT + var15 - var14;
                        if (var16 < 0) {
                           var1.sendMessage(
                              new CustomMessage(
                                 "TODAY_YOU_REACHED_THE_LIMIT_OF_USE_OF_THE_WORLD_CHAT__RESET_OF_THE_WORLD_USE_CHAT_IS_DONE_DAILY_AT_6_30_AM", var1
                              )
                           );
                        } else {
                           String var17 = this.add.substring(1);
                           Say2 var18 = new Say2(var1.getObjectId(), Config.WORLD_CHAT_MESSAGE_COLOR, Config.WORLD_CHAT_MESSAGE_APPEND + var1.getName(), var17);

                           for (Player var20 : a(var1, GameObjectsStorage.getAllPlayersForIterate())) {
                              if (!var20.isInBlockList(var1) && !var20.isBlockAll()) {
                                 var20.sendPacket(var18);
                              }
                           }

                           var1.sendMessage(new CustomMessage("WORLD_CHAT_POINTS_REMAINING_S1", var1).addNumber(var16));
                           var1.setVar("used_world_chat_points", var14, -1L);
                           var1.setVar("world_chat_reuse", var36, -1L);
                           Log.LogChat("GlobalChat", var1.getName(), this.abZ, this.add, 0);
                        }
                     }
                  }
               }
            }
         } else {
            var1.sendActionFailed();
         }
      }
   }

   private static Iterable<Player> a(Player var0, Iterable<Player> var1) {
      if (var0.getAntiSpam() != 0L && var0.getAntiSpamRemained() != 0L) {
         LinkedList var2 = new LinkedList();
         if (Objects.isNull(var0.getNetConnection())) {
            return Collections.emptyList();
         }

         for (Player var4 : var1) {
            if (var4 != null
               && var4.getNetConnection() != null
               && (
                  Config.NOSPAM_FILTER_IP
                        && StringUtils.isNotBlank(var0.getNetConnection().getIpAddr())
                        && Objects.equals(var0.getNetConnection().getIpAddr(), var4.getNetConnection().getIpAddr())
                     || Config.NOSPAM_FILTER_HWID
                        && StringUtils.isNotBlank(var0.getNetConnection().getHwid())
                        && Objects.equals(var0.getNetConnection().getHwid(), var4.getNetConnection().getHwid())
               )) {
               var2.add(var4);
            }
         }

         return var2;
      } else {
         return var1;
      }
   }

   private static void a(Player var0, Say2 var1) {
      int var2 = MapUtils.regionX(var0);
      int var3 = MapUtils.regionY(var0);
      int var4 = Config.SHOUT_OFFSET;

      for (Player var6 : a(var0, GameObjectsStorage.getAllPlayersForIterate())) {
         if (var6 != var0 && var0.getReflection() == var6.getReflection() && !var6.isBlockAll() && !var6.isInBlockList(var0)) {
            int var7 = MapUtils.regionX(var6);
            int var8 = MapUtils.regionY(var6);
            if (var7 >= var2 - var4 && var7 <= var2 + var4 && var8 >= var3 - var4 && var8 <= var3 + var4 || var0.isInRangeZ(var6, Config.CHAT_RANGE)) {
               var6.sendPacket(var1);
            }
         }
      }
   }

   private static void b(Player var0, Say2 var1) {
      for (Player var3 : a(var0, GameObjectsStorage.getAllPlayersForIterate())) {
         if (var3 != var0 && var0.getReflection() == var3.getReflection() && !var3.isBlockAll() && !var3.isInBlockList(var0)) {
            var3.sendPacket(var1);
         }
      }
   }
}
