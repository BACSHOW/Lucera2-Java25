package events.DropEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.collections.CollectionUtils;
import l2.commons.lang.ArrayUtils;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DropEvent extends Functions implements OnDeathListener, ScriptFile {
   private static final Logger LOG = LoggerFactory.getLogger(DropEvent.class);
   private static final String EVENT_NAME = "DropEvent";
   private static final DropEvent gT = new DropEvent();
   private static final int gU = 100;
   private static boolean gV = false;
   private static DropEvent.DropEventItem[][] gW;
   private static Map<Integer, List<DropEvent.DropEventItem>> gX;
   private static DropEvent.DropEventItem[][] gY;
   private static Map<Integer, List<DropEvent.DropEventItem>> gZ;

   private static boolean isActive() {
      return IsActive("DropEvent");
   }

   private static DropEvent.DropEventItem a(String var0) {
      Pattern var1 = Pattern.compile("(\\d+)[:-](\\d+)\\-?(\\d+)?\\(([\\d+\\.]+)\\)(<(\\d+)-(\\d+)>)?(\\[([\\d,]+)\\])?");
      Matcher var2 = var1.matcher(var0);
      if (var2.matches()) {
         int var3 = Integer.parseInt(var2.group(1));
         long var4 = Long.parseLong(var2.group(2));
         long var6 = StringUtils.isBlank(var2.group(3)) ? var4 : Long.parseLong(var2.group(3));
         double var8 = Double.parseDouble(var2.group(4));
         int var10 = var2.group(6) != null ? Integer.parseInt(var2.group(6)) : 1;
         int var11 = var2.group(6) != null ? Integer.parseInt(var2.group(7)) : 100;
         ArrayList var12 = new ArrayList();
         if (var2.group(9) != null) {
            var10 = 0;
            var11 = -1;
            String[] var13 = var2.group(9).split(",");

            for (String var17 : var13) {
               var12.add(Integer.parseInt(var17));
            }
         }

         int[] var18 = new int[var12.size()];

         for (int var19 = 0; var19 < var18.length; var19++) {
            var18[var19] = (Integer)var12.get(var19);
         }

         return new DropEvent.DropEventItem(var3, var4, var6, var8, var18, var10, var11);
      } else {
         throw new RuntimeException("Can't parse drop event item \"" + var0 + "\"");
      }
   }

   private static List<DropEvent.DropEventItem> b(String var0) {
      ArrayList var1 = new ArrayList();
      StringTokenizer var2 = new StringTokenizer(var0, ";");

      while (var2.hasMoreTokens()) {
         String var3 = var2.nextToken();
         var1.add(a(var3));
      }

      return var1;
   }

   private static void a(Map<Integer, List<DropEvent.DropEventItem>> var0, DropEvent.DropEventItem[][] var1, String var2) {
      for (DropEvent.DropEventItem var5 : b(var2)) {
         for (int var6 = var5.getMinLvl(); var6 <= var5.getMaxLvl(); var6++) {
            if (var6 > 0) {
               DropEvent.DropEventItem[] var7 = var1[var6];
               var1[var6] = ArrayUtils.add(var7, var5);
            }
         }

         int[] var11 = var5.getNpcIds();
         int var12 = var11.length;

         for (int var8 = 0; var8 < var12; var8++) {
            Integer var9 = var11[var8];
            List var10 = (List)var0.get(var9);
            if (var10 == null) {
               var0.put(var9, var10 = new ArrayList());
            }

            var10.add(var5);
         }
      }
   }

   private static void M() {
      if (isActive()) {
         a(gX = new HashMap<>(), gW = new DropEvent.DropEventItem[100][], Config.EVENT_DropEvent_Items);
         a(gZ = new HashMap<>(), gY = new DropEvent.DropEventItem[100][], Config.EVENT_DropEvent_PartyItems);
      }
   }

   private void a(GameObject var1, List<Player> var2, Set<DropEvent.DropEventItem> var3) {
      if (var3 != null && !var3.isEmpty()) {
         if (Config.Event_DropEvent_Check_Hwid) {
            ArrayList var4 = new ArrayList(var2);
            var2 = new ArrayList(var4.size());

            for (Object __raw_var6_131 : (Iterable<?>)(Object)(var4)) {
               Player var6 = (Player)__raw_var6_131;
               boolean var7 = true;
               int var8 = 0;

               for (int var9 = var4.size(); var7 && var8 < var9; var8++) {
                  Player var10 = (Player)var4.get(var8);
                  if (var6 != var10
                     && var6.getNetConnection() != null
                     && var10.getNetConnection() != null
                     && Objects.equals(var6.getNetConnection().getHwid(), var10.getNetConnection().getHwid())) {
                     var7 = false;
                  }
               }

               if (var7) {
                  var2.add(var6);
               }
            }
         }

         if (Config.EVENT_DropEvent_PartyItems_Distribute_Random) {
            var2 = new ArrayList(var2);
            Collections.shuffle(var2);
         }

         for (DropEvent.DropEventItem var16 : var3) {
            if (Rnd.chance(var16.getChance())) {
               ArrayList var17 = new ArrayList(var2.size());

               for (Player var20 : var2) {
                  if (var20 != null && !var20.isDead() && !(var1.getDistance3D(var20) > Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                     var17.add(var20);
                  }
               }

               if (!var17.isEmpty()) {
                  long var19 = var16.getItemCount();
                  long var21 = Math.max(1L, var19 / var17.size());

                  while (var19 >= var21) {
                     for (Object __raw_var12_171 : (Iterable<?>)(Object)(var17)) {
                        Player var12 = (Player)__raw_var12_171;
                        long var13 = var21;
                        addItem(var12.getPlayer(), var16.getItemId(), Config.EVENT_DropEvent_Rate ? (long)(var13 * var12.getRateItems()) : var13);
                        var19 -= var13;
                        if (var19 <= 0L) {
                           break;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onDeath(Creature var1, Creature var2) {
      try {
         if (!a(var1, var2)) {
            return;
         }

         Player var3 = var2.getPlayer();
         if (var3 == null) {
            return;
         }

         Party var4 = var3.getParty();
         NpcInstance var5 = (NpcInstance)var1;
         if (var4 != null) {
            this.a(
               var5,
               var4.getPartyMembers(),
               new HashSet<>(
                  CollectionUtils.join(
                     gY[var5.getLevel()] != null ? Arrays.asList(gY[var5.getLevel()]) : Collections.emptyList(),
                     gZ.getOrDefault(var5.getNpcId(), Collections.emptyList())
                  )
               )
            );
         } else {
            this.a(
               var5,
               Collections.singletonList(var3),
               new HashSet<>(
                  CollectionUtils.join(
                     gW[var5.getLevel()] != null ? Arrays.asList(gW[var5.getLevel()]) : Collections.emptyList(),
                     gX.getOrDefault(var5.getNpcId(), Collections.emptyList())
                  )
               )
            );
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   private static boolean a(Creature var0, Creature var1) {
      return var0 != null
         && var0.isMonster()
         && var1 != null
         && var1.getPlayer() != null
         && var1.getLevel() - var0.getLevel() < Config.Event_DropEvent_Level_Penalty;
   }

   public void startEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive("DropEvent", true)) {
            if (!gV) {
               CharListenerList.addGlobal(gT);
            }

            var1.sendMessage("Event 'DropEvent' started.");
         } else {
            var1.sendMessage("Event 'DropEvent' already started.");
         }

         gV = true;
         this.show("admin/events/events.htm", var1);
      }
   }

   public void stopEvent() {
      Player var1 = this.getSelf();
      if (var1.getPlayerAccess().IsEventGm) {
         if (SetActive("DropEvent", false)) {
            if (gV) {
               CharListenerList.removeGlobal(gT);
            }

            System.out.println("Event: 'DropEvent' stopped.");
         } else {
            var1.sendMessage("Event: 'DropEvent' not started.");
         }

         gV = false;
         this.show("admin/events/events.htm", var1);
      }
   }

   @Override
   public void onLoad() {
      M();
      if (isActive()) {
         CharListenerList.addGlobal(gT);
         gV = true;
         LOG.info("Loaded Event: Drop Event [state: activated]");
      } else {
         LOG.debug("Loaded Event: Drop Event [state: deactivated]");
      }
   }

   @Override
   public void onReload() {
      this.onShutdown();
      this.onLoad();
   }

   @Override
   public void onShutdown() {
      if (gV) {
         CharListenerList.removeGlobal(gT);
         gV = false;
      }
   }

   private static class DropEventItem {
      private final int _itemId;
      private final long ha;
      private final long hb;
      private final double hc;
      private final int[] hd;
      private final int he;
      private final int hf;

      private DropEventItem(int var1, long var2, long var4, double var6, int[] var8, int var9, int var10) {
         Arrays.sort(var8);
         this._itemId = var1;
         this.ha = var2;
         this.hb = var4;
         this.hc = var6;
         this.hd = var8;
         this.he = var9;
         this.hf = var10;
      }

      public double getChance() {
         return this.hc;
      }

      public long getItemCountMin() {
         return this.ha;
      }

      public long getItemCountMax() {
         return this.hb;
      }

      public long getItemCount() {
         return this.getItemCountMin() == this.getItemCountMax() ? this.getItemCountMin() : Rnd.get(this.getItemCountMin(), this.getItemCountMax());
      }

      public int getItemId() {
         return this._itemId;
      }

      public int getMaxLvl() {
         return this.hf;
      }

      public int getMinLvl() {
         return this.he;
      }

      public int[] getNpcIds() {
         return this.hd;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            DropEvent.DropEventItem var2 = (DropEvent.DropEventItem)var1;
            return new EqualsBuilder()
               .append(this._itemId, var2._itemId)
               .append(this.ha, var2.ha)
               .append(this.hb, var2.hb)
               .append(this.hc, var2.hc)
               .append(this.he, var2.he)
               .append(this.hf, var2.hf)
               .append(this.hd, var2.hd)
               .isEquals();
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return new HashCodeBuilder(17, 37)
            .append(this._itemId)
            .append(this.ha)
            .append(this.hb)
            .append(this.hc)
            .append(this.hd)
            .append(this.he)
            .append(this.hf)
            .toHashCode();
      }
   }
}
