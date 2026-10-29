package zones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.player.OnPvpPkKillListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KillRewardZone implements ScriptFile {
   private static final Logger sQ = LoggerFactory.getLogger(KillRewardZone.class);
   private static final String cad = "playerKillReward";
   private static final String cae = "karmaPlayerKillReward";
   private static final String caf = "playerKillCheck";
   private static final String cag = "ZoneRewardInterval";
   private static final String LAST_PVP_PK_KILL_VAR_NAME = "LastZoneRewardKill";
   private static final List<KillRewardZone.ZoneListener> cah = new CopyOnWriteArrayList<>();

   private static List<Pair<Pair<ItemTemplate, Long>, Integer>> T(String var0) {
      if (var0 != null && !(var0 = var0.trim()).isEmpty()) {
         StringTokenizer var1 = new StringTokenizer(var0, ";");
         ArrayList var2 = new ArrayList();

         while (var1.hasMoreTokens()) {
            String var3 = var1.nextToken().trim();
            if (!var3.isEmpty()) {
               int var4 = var3.indexOf(58);
               if (var4 <= 0) {
                  throw new IllegalArgumentException("Wrong reward definition: \"" + var3 + "\"");
               }

               int var5 = var3.indexOf(44, var4);
               int var6 = Integer.parseInt(var3.substring(0, var4).trim());
               ItemTemplate var7 = ItemHolder.getInstance().getTemplate(var6);
               if (var5 <= 0) {
                  var2.add(Pair.of(Pair.of(var7, Long.parseLong(var3.substring(var4 + 1).trim())), 100));
               } else {
                  var2.add(Pair.of(Pair.of(var7, Long.parseLong(var3.substring(var4 + 1, var5).trim())), Integer.parseInt(var3.substring(var5 + 1).trim())));
               }
            }
         }

         return Collections.unmodifiableList(var2);
      } else {
         return Collections.emptyList();
      }
   }

   @Override
   public void onLoad() {
      for (Zone var3 : ReflectionManager.DEFAULT.getZones()) {
         boolean var4 = var3.getParams().isSet("playerKillReward");
         boolean var5 = var3.getParams().isSet("karmaPlayerKillReward");
         if (var4 || var5) {
            List var7 = var3.getParams().isSet("playerKillCheck")
               ? Stream.of(StringUtils.split(var3.getParams().getString("playerKillCheck", "none"), '|'))
                  .map(var0 -> var0.trim().toLowerCase())
                  .filter(var0 -> !var0.isEmpty())
                  .map(var0 -> KillRewardZone.PlayerCheck.valueOf(var0.toLowerCase()))
                  .collect(Collectors.toList())
               : Collections.emptyList();
            KillRewardZone.ZoneListener var6;
            var3.addListener(
               var6 = new KillRewardZone.ZoneListener(
                  T(var3.getParams().getString("playerKillReward", "")),
                  T(var3.getParams().getString("karmaPlayerKillReward", "")),
                  var7,
                  var3.getParams().getLong("ZoneRewardInterval", 0L) * 60L * 1000L
               )
            );
            cah.add(var6);
            var3.getInsidePlayers().forEach(var2 -> var6.onZoneEnter(var3, var2));
         }
      }

      sQ.info("KillRewardZone: Loaded " + cah.size() + " player kill reward zone(s).");
   }

   @Override
   public void onReload() {
      this.onShutdown();
      this.onLoad();
   }

   @Override
   public void onShutdown() {
      for (Zone var3 : ReflectionManager.DEFAULT.getZones()) {
         LinkedList var4 = new LinkedList();

         for (KillRewardZone.ZoneListener var6 : cah) {
            if (var3.removeListener(var6)) {
               var4.add(var6);
               var3.getInsidePlayers().forEach(var2 -> var6.onZoneLeave(var3, var2));
            }
         }

         if (!var4.isEmpty()) {
            cah.removeAll(var4);
         }
      }
   }

   enum PlayerCheck {
      hwid {
         @Override
         boolean check(Player var1, Player var2) {
            if (!var1.isConnected() || !var2.isConnected()) {
               return false;
            } else {
               return var1.getNetConnection().getHwid() != null
                  ? !StringUtils.equals(var1.getNetConnection().getHwid(), var2.getNetConnection().getHwid())
                  : true;
            }
         }
      },
      ip {
         @Override
         boolean check(Player var1, Player var2) {
            return var1.isConnected() && var2.isConnected() ? !StringUtils.equals(var1.getIP(), var2.getIP()) : false;
         }
      };

      PlayerCheck() {
      }

      abstract boolean check(Player var1, Player var2);
   }

   private static class ZoneListener implements OnZoneEnterLeaveListener {
      private final List<Pair<Pair<ItemTemplate, Long>, Integer>> caj;
      private final List<Pair<Pair<ItemTemplate, Long>, Integer>> cak;
      private final List<KillRewardZone.PlayerCheck> cal;
      private final long cam;
      private final OnPvpPkKillListener can = new OnPvpPkKillListener() {
         private boolean e(Player var1, Player var2) {
            if (var1.isConnected() && var2.isConnected()) {
               if (StringUtils.equals(var1.getIP(), var2.getIP())) {
                  return true;
               } else {
                  return var1.getNetConnection().getHwid() != null
                     ? StringUtils.equals(var1.getNetConnection().getHwid(), var2.getNetConnection().getHwid())
                     : false;
               }
            } else {
               return false;
            }
         }

         @Override
         public void onPvpPkKill(Player var1, Player var2, boolean var3) {
            if (!ZoneListener.this.cal.isEmpty()) {
               for (KillRewardZone.PlayerCheck var5 : ZoneListener.this.cal) {
                  if (!var5.check(var1, var2)) {
                     return;
                  }
               }
            }

            List var12 = var3 ? ZoneListener.this.cak : ZoneListener.this.caj;
            if (!var12.isEmpty()) {
               long var13 = var1.getVarLong("LastZoneRewardKill", 0L);
               long var7 = System.currentTimeMillis();
               if (var7 - var13 >= ZoneListener.this.cam) {
                  for (Object __raw_var10_181 : (Iterable<?>)(Object)(var12)) {
                     Pair var10 = (Pair)__raw_var10_181;
                     if (Rnd.chance((Integer)var10.getRight())) {
                        Pair var11 = (Pair)var10.getLeft();
                        if (!ItemFunctions.canAddItem(var1, (ItemTemplate)var11.getKey(), (Long)var11.getValue())) {
                           return;
                        }

                        ItemFunctions.addItem(var1, (ItemTemplate)var11.getKey(), (Long)var11.getValue(), true);
                     }
                  }

                  var1.setVar("LastZoneRewardKill", var7, -1L);
               }
            }
         }
      };

      private ZoneListener(
         List<Pair<Pair<ItemTemplate, Long>, Integer>> var1,
         List<Pair<Pair<ItemTemplate, Long>, Integer>> var2,
         List<KillRewardZone.PlayerCheck> var3,
         long var4
      ) {
         this.caj = var1;
         this.cak = var2;
         this.cal = var3;
         this.cam = var4;
      }

      @Override
      public void onZoneEnter(Zone var1, Creature var2) {
         if (var2 != null && var2.isPlayer()) {
            Player var3 = var2.getPlayer();
            var3.addListener(this.can);
         }
      }

      @Override
      public void onZoneLeave(Zone var1, Creature var2) {
         if (var2 != null && var2.isPlayer()) {
            Player var3 = var2.getPlayer();
            var3.removeListener(this.can);
         }
      }
   }
}
