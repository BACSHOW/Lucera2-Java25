package npc.model.residences.clanhall;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;

public class RainbowYetiInstance extends NpcInstance {
   private static final int ayM = 8035;
   private static final int ayN = 8036;
   private static final int ayO = 8037;
   private static final int ayP = 8038;
   private static final int ayQ = 8039;
   private static final int ayR = 8040;
   private static final int ayS = 8041;
   private static final int ayT = 8042;
   private static final int ayU = 8043;
   private static final int ayV = 8045;
   private static final int ayW = 8046;
   private static final int ayX = 8047;
   private static final int ayY = 8048;
   private static final int ayZ = 8049;
   private static final int aza = 8050;
   private static final int azb = 8051;
   private static final int azc = 8052;
   private static final int azd = 8053;
   private static final int aze = 8054;
   private static final int azf = 8055;
   private static final RainbowYetiInstance.Word[] azh = new RainbowYetiInstance.Word[8];
   private List<GameObject> azi = new ArrayList<>();
   private int azj = -1;
   private Future<?> to = null;

   public RainbowYetiInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
      this._hasRandomWalk = false;
   }

   @Override
   public void onSpawn() {
      super.onSpawn();
      ClanHallMiniGameEvent var1 = this.getEvent(ClanHallMiniGameEvent.class);
      if (var1 != null) {
         for (Player var4 : World.getAroundPlayers(this, 750, 100)) {
            CMGSiegeClanObject var5 = var1.getSiegeClan("attackers", var4.getClan());
            if (var5 == null || !var5.getPlayers().contains(var4.getObjectId())) {
               var4.teleToLocation(var1.getResidence().getOtherRestartPoint());
            }
         }

         this.to = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RainbowYetiInstance.GenerateTask(), 10000L, 300000L);
      }
   }

   @Override
   public void onDelete() {
      super.onDelete();
      if (this.to != null) {
         this.to.cancel(false);
         this.to = null;
      }

      for (GameObject var2 : this.azi) {
         var2.deleteMe();
      }

      this.azi.clear();
   }

   public void teleportFromArena() {
      ClanHallMiniGameEvent var1 = this.getEvent(ClanHallMiniGameEvent.class);
      if (var1 != null) {
         for (Player var4 : World.getAroundPlayers(this, 750, 100)) {
            var4.teleToLocation(var1.getResidence().getOtherRestartPoint());
         }
      }
   }

   @Override
   public void onBypassFeedback(Player var1, String var2) {
      if (canBypassCheck(var1, this)) {
         if (var2.equalsIgnoreCase("get")) {
            NpcHtmlMessage var3 = new NpcHtmlMessage(var1, this);
            boolean var4 = true;
            if (this.azj == -1) {
               var4 = false;
            } else {
               RainbowYetiInstance.Word var5 = azh[this.azj];

               for (int[] var9 : var5.getItems()) {
                  if (var1.getInventory().getCountOf(var9[0]) < var9[1]) {
                     var4 = false;
                  }
               }

               if (var4) {
                  for (int[] var15 : var5.getItems()) {
                     if (!var1.consumeItem(var15[0], var15[1])) {
                        return;
                     }
                  }

                  int var12 = Rnd.get(100);
                  if (this.azj >= 0 && this.azj <= 5) {
                     if (var12 < 70) {
                        this.m(var1, 8030);
                     } else if (var12 < 80) {
                        this.m(var1, 8031);
                     } else if (var12 < 90) {
                        this.m(var1, 8032);
                     } else {
                        this.m(var1, 8033);
                     }
                  } else if (var12 < 10) {
                     this.m(var1, 8030);
                  } else if (var12 < 40) {
                     this.m(var1, 8031);
                  } else if (var12 < 70) {
                     this.m(var1, 8032);
                  } else {
                     this.m(var1, 8033);
                  }
               }
            }

            if (!var4) {
               var3.setFile("residence2/clanhall/watering_manager002.htm");
            } else {
               var3.setFile("residence2/clanhall/watering_manager004.htm");
            }

            var1.sendPacket(var3);
         } else if (var2.equalsIgnoreCase("see")) {
            NpcHtmlMessage var10 = new NpcHtmlMessage(var1, this);
            var10.setFile("residence2/clanhall/watering_manager005.htm");
            if (this.azj == -1) {
               var10.replaceNpcString("%word%", NpcString.UNDECIDED);
            } else {
               var10.replace("%word%", azh[this.azj].getName());
            }

            var1.sendPacket(var10);
         } else {
            super.onBypassFeedback(var1, var2);
         }
      }
   }

   private void m(Player var1, int var2) {
      ClanHallMiniGameEvent var3 = this.getEvent(ClanHallMiniGameEvent.class);
      if (var3 != null) {
         ItemInstance var4 = ItemFunctions.createItem(var2);
         var4.addEvent(var3);
         var1.getInventory().addItem(var4);
         var1.sendPacket(SystemMessage2.obtainItems(var4));
      }
   }

   @Override
   public void showChatWindow(Player var1, int var2, Object... var3) {
      this.showChatWindow(var1, "residence2/clanhall/watering_manager001.htm");
   }

   public void addMob(GameObject var1) {
      this.azi.add(var1);
   }

   static {
      azh[0] = new RainbowYetiInstance.Word(
         "BABYDUCK", new int[]{8036, 2}, new int[]{8035, 1}, new int[]{8055, 1}, new int[]{8038, 1}, new int[]{8053, 1}, new int[]{8037, 1}, new int[]{8045, 1}
      );
      azh[1] = new RainbowYetiInstance.Word(
         "ALBATROS", new int[]{8035, 2}, new int[]{8046, 1}, new int[]{8036, 1}, new int[]{8052, 1}, new int[]{8050, 1}, new int[]{8048, 1}, new int[]{8051, 1}
      );
      azh[2] = new RainbowYetiInstance.Word(
         "PELICAN", new int[]{8049, 1}, new int[]{8039, 1}, new int[]{8046, 1}, new int[]{8043, 1}, new int[]{8037, 1}, new int[]{8035, 1}, new int[]{8047, 1}
      );
      azh[3] = new RainbowYetiInstance.Word(
         "KINGFISHER",
         new int[]{8045, 1},
         new int[]{8043, 1},
         new int[]{8047, 1},
         new int[]{8041, 1},
         new int[]{8040, 1},
         new int[]{8043, 1},
         new int[]{8051, 1},
         new int[]{8042, 1},
         new int[]{8039, 1},
         new int[]{8050, 1}
      );
      azh[4] = new RainbowYetiInstance.Word(
         "CYGNUS", new int[]{8037, 1}, new int[]{8055, 1}, new int[]{8041, 1}, new int[]{8047, 1}, new int[]{8053, 1}, new int[]{8051, 1}
      );
      azh[5] = new RainbowYetiInstance.Word("TRITON", new int[]{8052, 2}, new int[]{8050, 1}, new int[]{8043, 1}, new int[]{8047, 1});
      azh[6] = new RainbowYetiInstance.Word(
         "RAINBOW", new int[]{8050, 1}, new int[]{8035, 1}, new int[]{8043, 1}, new int[]{8047, 1}, new int[]{8036, 1}, new int[]{8048, 1}, new int[]{8054, 1}
      );
      azh[7] = new RainbowYetiInstance.Word(
         "SPRING", new int[]{8051, 1}, new int[]{8049, 1}, new int[]{8050, 1}, new int[]{8043, 1}, new int[]{8047, 1}, new int[]{8041, 1}
      );
   }

   private class GenerateTask extends RunnableImpl {
      private GenerateTask() {
      }

      @Override
      public void runImpl() throws Exception {
         RainbowYetiInstance.this.azj = Rnd.get(RainbowYetiInstance.azh.length);
         RainbowYetiInstance.Word var1 = RainbowYetiInstance.azh[RainbowYetiInstance.this.azj];
         List var2 = World.getAroundPlayers(RainbowYetiInstance.this, 750, 100);
         ExShowScreenMessage var3 = new ExShowScreenMessage(var1.getName(), 5000, ScreenMessageAlign.TOP_CENTER, true);

         for (Object __raw_var5_230 : (Iterable<?>)(Object)(var2)) {
            Player var5 = (Player)__raw_var5_230;
            var5.sendPacket(var3);
         }
      }
   }

   private static class Word {
      private final String _name;
      private final int[][] _items;

      public Word(String var1, int[]... var2) {
         this._name = var1;
         this._items = var2;
      }

      public String getName() {
         return this._name;
      }

      public int[][] getItems() {
         return this._items;
      }
   }
}
