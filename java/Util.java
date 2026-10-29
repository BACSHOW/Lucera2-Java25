import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExPledgeCrestLarge;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.utils.CapchaUtil;
import l2.gameserver.utils.Location;

public class Util extends Functions {
   private static final int a = 67108864;

   public void Gatekeeper(String[] var1) {
      if (var1.length < 4) {
         throw new IllegalArgumentException();
      }

      Player var2 = this.getSelf();
      if (var2 != null) {
         long var3 = Long.parseLong(var1[var1.length - 1]);
         if (NpcInstance.canBypassCheck(var2, var2.getLastNpc())) {
            if (var3 > 0L && var2.getAdena() < var3) {
               var2.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else if (var2.getMountType() == 2) {
               this.show("scripts/wyvern-no.htm", var2);
            } else {
               int var5 = Integer.parseInt(var1[0]);
               int var6 = Integer.parseInt(var1[1]);
               int var7 = Integer.parseInt(var1[2]);
               int var8 = var1.length > 4 ? Integer.parseInt(var1[3]) : 0;
               if (var2.getLastNpc() != null) {
                  TeleportLocation var9 = null;
                  TeleportLocation[][] var10 = (TeleportLocation[][])var2.getLastNpc()
                     .getTemplate()
                     .getTeleportList()
                     .getValues(new TeleportLocation[var2.getLastNpc().getTemplate().getTeleportList().size()][]);

                  label88:
                  for (TeleportLocation[] var14 : var10) {
                     for (TeleportLocation var18 : var14) {
                        if (var18.getX() == var5 && var18.getY() == var6 && var18.getZ() == var7) {
                           var9 = var18;
                           break label88;
                        }
                     }
                  }

                  if (var9 != null) {
                     if (var9.getMinLevel() > 0 && var2.getLevel() < var9.getMinLevel()) {
                        var2.sendMessage(new CustomMessage("Gatekeeper.LevelToLow", var2, var9.getMinLevel()));
                        return;
                     }

                     if (var9.getMaxLevel() > 0 && var2.getLevel() > var9.getMaxLevel()) {
                        var2.sendMessage(new CustomMessage("Gatekeeper.LevelToHigh", var2, var9.getMaxLevel()));
                        return;
                     }

                     if (var9.getKeyItemId() > 0 && var2.getInventory().getCountOf(var9.getKeyItemId()) == 0L) {
                        var2.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                        return;
                     }
                  }
               }

               if (var2.getReflection().isDefault()) {
                  Castle var19 = var8 > 0 ? ResidenceHolder.getInstance().getResidence(Castle.class, var8) : null;
                  if (var19 != null && var19.<SiegeEvent>getSiegeEvent().isInProgress()) {
                     var2.sendPacket(Msg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
                     return;
                  }
               }

               Location var20;
               if (Config.ALT_SPREADING_AFTER_TELEPORT) {
                  var20 = Location.findPointToStay(var5, var6, var7, 20, 70, var2.getGeoIndex());
               } else {
                  var20 = new Location(var5, var6, var7).correctGeoZ();
               }

               if (var3 > 0L) {
                  var2.reduceAdena(var3, true);
               }

               var2.teleToLocation(var20);
            }
         }
      }
   }

   public void SSGatekeeper(String[] var1) {
      if (var1.length < 4) {
         throw new IllegalArgumentException();
      }

      Player var2 = this.getSelf();
      if (var2 != null) {
         int var3 = Integer.parseInt(var1[3]);
         if (NpcInstance.canBypassCheck(var2, var2.getLastNpc())) {
            if (Config.ALT_ENABLE_SEVEN_SING_TELEPORTER_PROTECTION && var3 > 0) {
               int var4 = SevenSigns.getInstance().getPlayerCabal(var2);
               int var5 = SevenSigns.getInstance().getCurrentPeriod();
               if (var5 == 1 && var4 == 0) {
                  var2.sendPacket(Msg.USED_ONLY_DURING_A_QUEST_EVENT_PERIOD);
                  return;
               }

               int var6;
               if (var5 == 3 && (var6 = SevenSigns.getInstance().getCabalHighestScore()) != 0) {
                  if (var6 != var4) {
                     return;
                  }

                  if (var3 == 1 && SevenSigns.getInstance().getSealOwner(1) != var4) {
                     return;
                  }

                  if (var3 == 2 && SevenSigns.getInstance().getSealOwner(2) != var4) {
                     return;
                  }
               }
            }

            var2.teleToLocation(Integer.parseInt(var1[0]), Integer.parseInt(var1[1]), Integer.parseInt(var1[2]));
         }
      }
   }

   public void QuestGatekeeper(String[] var1) {
      if (var1.length < 5) {
         throw new IllegalArgumentException();
      }

      Player var2 = this.getSelf();
      if (var2 != null) {
         long var3 = Long.parseLong(var1[3]);
         int var5 = Integer.parseInt(var1[4]);
         if (NpcInstance.canBypassCheck(var2, var2.getLastNpc())) {
            int var6 = Integer.parseInt(var1[0]);
            int var7 = Integer.parseInt(var1[1]);
            int var8 = Integer.parseInt(var1[2]);
            if (var2.getLastNpc() != null) {
               TeleportLocation var9 = null;
               TeleportLocation[][] var10 = (TeleportLocation[][])var2.getLastNpc()
                  .getTemplate()
                  .getTeleportList()
                  .getValues(new TeleportLocation[var2.getLastNpc().getTemplate().getTeleportList().size()][]);

               label69:
               for (TeleportLocation[] var14 : var10) {
                  for (TeleportLocation var18 : var14) {
                     if (var18.getX() == var6 && var18.getY() == var7 && var18.getZ() == var8) {
                        var9 = var18;
                        break label69;
                     }
                  }
               }

               if (var9 != null) {
                  if (var9.getMinLevel() > 0 && var2.getLevel() < var9.getMinLevel()) {
                     var2.sendMessage(new CustomMessage("Gatekeeper.LevelToLow", var2, var9.getMinLevel()));
                     return;
                  }

                  if (var9.getMaxLevel() > 0 && var2.getLevel() > var9.getMaxLevel()) {
                     var2.sendMessage(new CustomMessage("Gatekeeper.LevelToHigh", var2, var9.getMaxLevel()));
                     return;
                  }

                  if (var9.getKeyItemId() > 0 && var2.getInventory().getCountOf(var9.getKeyItemId()) == 0L) {
                     var2.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                     return;
                  }
               }
            }

            if (var3 > 0L) {
               if (!var2.getInventory().destroyItemByItemId(var5, var3)) {
                  var2.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                  return;
               }

               var2.sendPacket(SystemMessage2.removeItems(var5, var3));
            }

            Location var19;
            if (Config.ALT_SPREADING_AFTER_TELEPORT) {
               var19 = Location.findPointToStay(var6, var7, var8, 20, 70, var2.getGeoIndex());
            } else {
               var19 = new Location(var6, var7, var8).correctGeoZ();
            }

            var2.teleToLocation(var19);
         }
      }
   }

   public void ReflectionGatekeeper(String[] var1) {
      if (var1.length < 5) {
         throw new IllegalArgumentException();
      }

      Player var2 = this.getSelf();
      if (var2 != null) {
         var2.setReflection(Integer.parseInt(var1[4]));
         this.Gatekeeper(var1);
      }
   }

   public void NoblessTeleport() {
      Player var1 = this.getSelf();
      if (var1 != null) {
         if (!var1.isNoble() && !Config.ALLOW_NOBLE_TP_TO_ALL) {
            this.show("scripts/nobleteleporter-no.htm", var1);
         } else {
            this.show("scripts/noble.htm", var1);
         }
      }
   }

   public void MakeEchoCrystal(String[] var1) {
      if (var1.length < 2) {
         throw new IllegalArgumentException();
      }

      Player var2 = this.getSelf();
      if (var2 != null) {
         if (NpcInstance.canBypassCheck(var2, var2.getLastNpc())) {
            int var3 = Integer.parseInt(var1[0]);
            int var4 = Integer.parseInt(var1[1]);
            if (var3 >= 4411 && var3 <= 4417) {
               if (getItemCount(var2, var4) == 0L) {
                  var2.getLastNpc().onBypassFeedback(var2, "Chat 1");
               } else if (getItemCount(var2, 57) < 200L) {
                  var2.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
               } else {
                  removeItem(var2, 57, 200L);
                  addItem(var2, var3, 1L);
               }
            }
         }
      }
   }

   public void enter_dc() {
      Player var1 = this.getSelf();
      NpcInstance var2 = this.getNpc();
      if (var1 != null && var2 != null) {
         if (NpcInstance.canBypassCheck(var1, var2)) {
            var1.setVar("DCBackCoords", var1.getLoc().toXYZString(), -1L);
            var1.teleToLocation(-114582, -152635, -6742);
         }
      }
   }

   public void exit_dc() {
      Player var1 = this.getSelf();
      NpcInstance var2 = this.getNpc();
      if (var1 != null && var2 != null) {
         if (NpcInstance.canBypassCheck(var1, var2)) {
            String var3 = var1.getVar("DCBackCoords");
            if (var3 != null && !var3.isEmpty()) {
               var1.teleToLocation(Location.parseLoc(var3), 0);
               var1.unsetVar("DCBackCoords");
            } else {
               var1.teleToLocation(new Location(43768, -48232, -800), 0);
            }
         }
      }
   }

   public static void RequestCapcha(String var0, Long var1, Integer var2) {
      Player var3 = GameObjectsStorage.getAsPlayer(var1);
      if (var3 != null && var3.isOnline() && !var3.isLogoutStarted()) {
         int var4 = Config.REQUEST_ID;
         if (var3.isConnected() && var3.getNetConnection() != null) {
            var4 = var3.getNetConnection().getServerId();
         }

         int var5 = CapchaUtil.RndCapcha();
         int var6 = CapchaUtil.RndRGB888Color();
         int var7 = CapchaUtil.getId(var5) | 67108864;
         byte[] var8 = CapchaUtil.getCapchaImage(var5, var6);
         var3.sendPacket(new ExPledgeCrestLarge(var7, var8));
         NpcHtmlMessage var9 = new NpcHtmlMessage(var3, null);
         var9.setFile("capcha.htm");
         var9 = var9.replace("%SN%", String.valueOf(var4));
         var9 = var9.replace("%CID%", String.valueOf(var7));
         var3.setVar("capacha-code", String.valueOf(var5), -1L);
         var3.setVar("capacha-time", String.valueOf(System.currentTimeMillis() / 1000L + var2.intValue()), -1L);
         var3.setVar("capacha-success", var0, -1L);
         var3.sendPacket(var9);
         var3.sendMessage(new CustomMessage("scripts.Util.CapchaConfirm.RequestCapcha", var3, var2));
      }
   }

   public void CapchaConfirm(String[] var1) {
      if (var1.length < 1) {
         throw new IllegalArgumentException();
      }

      Player var2 = this.getSelf();
      if (var2 != null) {
         String var3 = var2.getVar("capacha-code");
         String var4 = var2.getVar("capacha-time");
         String var5 = var2.getVar("capacha-success");
         if (var3 != null && var4 != null && var5 != null) {
            try {
               int var6 = Integer.parseInt(var3);
               long var7 = Long.parseLong(var4);
               String var9 = var1[0];
               if (var7 < System.currentTimeMillis() / 1000L) {
                  var2.sendMessage(new CustomMessage("scripts.Util.CapchaConfirm.TimeExpired", var2));
                  return;
               }

               if (!CapchaUtil.IsValidEntry(var6, var9)) {
                  var2.sendMessage(new CustomMessage("scripts.Util.CapchaConfirm.WrongCode", var2));
                  return;
               }

               Scripts.getInstance().callScripts(var2, var5.split(":")[0], var5.split(":")[1]);
               var2.sendMessage(new CustomMessage("scripts.Util.CapchaConfirm.Success", var2));
            } catch (Exception var10) {
            }
         }
      }
   }

   public void QuestCaptchaSuccessHandler() {
      Player var1 = this.getSelf();
      if (var1 != null) {
         var1.setVar("QuestCaptchaLastCheck", System.currentTimeMillis(), -1L);
         NpcInstance var2 = var1.getLastNpc();
         if (var2 != null && var2.isNpc() && var2.isInActingRange(var1)) {
            var2.showQuestWindow(var1);
         }
      }
   }
}
