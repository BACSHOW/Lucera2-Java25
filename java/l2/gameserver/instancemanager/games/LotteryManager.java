package l2.gameserver.instancemanager.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LotteryManager {
   public static final long SECOND = 1000L;
   public static final long MINUTE = 60000L;
   private static LotteryManager Bx;
   private static final Logger _log = LoggerFactory.getLogger(LotteryManager.class);
   private static final String By = "INSERT INTO `games`(`id`, `idnr`, `enddate`, `prize`, `newprize`) VALUES (?, ?, ?, ?, ?)";
   private static final String Bz = "UPDATE `games` SET `prize`=?, `newprize`=? WHERE `id` = 1 AND `idnr` = ?";
   private static final String BA = "UPDATE `games` SET `finished`=1, `prize`=?, `newprize`=?, `number1`=?, `number2`=?, `prize1`=?, `prize2`=?, `prize3`=? WHERE `id`=1 AND `idnr`=?";
   private static final String BB = "SELECT `idnr`, `prize`, `newprize`, `enddate`, `finished` FROM `games` WHERE `id` = 1 ORDER BY `idnr` DESC LIMIT 1";
   private static final String BC = "SELECT `items`.`enchant` AS `enchant`, `items_options`.`damaged` AS `damaged` FROM `items`, `items_options` WHERE `items_options`.`blessed` = ? AND `items`.`item_id` = `items_options`.`item_id` AND `items`.`item_type` = 4442";
   private static final String BD = "SELECT `number1`, `number2`, `prize1`, `prize2`, `prize3` FROM `games` WHERE `id` = 1 AND `idnr` = ?";
   protected int _number = 1;
   protected int _prize = Config.SERVICES_LOTTERY_PRIZE;
   protected boolean _isSellingTickets = false;
   protected boolean _isStarted = false;
   protected long _enddate = System.currentTimeMillis();

   public LotteryManager() {
      if (Config.SERVICES_ALLOW_LOTTERY) {
         new LotteryManager.startLottery().run();
      }
   }

   public void increasePrize(int var1) {
      this._prize += var1;
      Connection var2 = null;
      PreparedStatement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("UPDATE `games` SET `prize`=?, `newprize`=? WHERE `id` = 1 AND `idnr` = ?");
         var3.setInt(1, this.getPrize());
         var3.setInt(2, this.getPrize());
         var3.setInt(3, this.getId());
         var3.execute();
      } catch (SQLException var8) {
         _log.warn("Lottery: Could not increase current lottery prize: " + var8);
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }
   }

   private boolean bH() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT `idnr`, `prize`, `newprize`, `enddate`, `finished` FROM `games` WHERE `id` = 1 ORDER BY `idnr` DESC LIMIT 1");
         var3 = var2.executeQuery();
         if (var3.next()) {
            this._number = var3.getInt("idnr");
            if (var3.getInt("finished") == 1) {
               this._number++;
               this._prize = var3.getInt("newprize");
            } else {
               this._prize = var3.getInt("prize");
               this._enddate = var3.getLong("enddate");
               if (this._enddate <= System.currentTimeMillis() + 120000L) {
                  new LotteryManager.finishLottery().run();
                  return false;
               }

               if (this._enddate > System.currentTimeMillis()) {
                  this._isStarted = true;
                  ThreadPoolManager.getInstance().schedule(new LotteryManager.finishLottery(), this._enddate - System.currentTimeMillis());
                  if (this._enddate > System.currentTimeMillis() + 720000L) {
                     this._isSellingTickets = true;
                     ThreadPoolManager.getInstance().schedule(new LotteryManager.stopSellingTickets(), this._enddate - System.currentTimeMillis() - 600000L);
                  }

                  return false;
               }
            }
         }
      } catch (SQLException var8) {
         _log.warn("Lottery: Could not restore lottery data: " + var8);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }

      return true;
   }

   private void bI() {
      if (Config.SERVICES_ALLOW_LOTTERY) {
         _log.info("Lottery: Starting ticket sell for lottery #" + this.getId() + ".");
      }

      this._isSellingTickets = true;
      this._isStarted = true;
      Announcements.getInstance().announceToAll("Lottery tickets are now available for Lucky Lottery #" + this.getId() + ".");
   }

   private void bJ() {
      Calendar var1 = Calendar.getInstance();
      var1.setTimeInMillis(this._enddate);
      var1.set(12, 0);
      var1.set(13, 0);
      if (var1.get(7) == 1) {
         var1.set(11, 19);
         this._enddate = var1.getTimeInMillis();
         this._enddate += 604800000L;
      } else {
         var1.set(7, 1);
         var1.set(11, 19);
         this._enddate = var1.getTimeInMillis();
      }

      ThreadPoolManager.getInstance().schedule(new LotteryManager.stopSellingTickets(), this._enddate - System.currentTimeMillis() - 600000L);
      ThreadPoolManager.getInstance().schedule(new LotteryManager.finishLottery(), this._enddate - System.currentTimeMillis());
   }

   private void bK() {
      Connection var1 = null;
      PreparedStatement var2 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("INSERT INTO `games`(`id`, `idnr`, `enddate`, `prize`, `newprize`) VALUES (?, ?, ?, ?, ?)");
         var2.setInt(1, 1);
         var2.setInt(2, this.getId());
         var2.setLong(3, this.getEndDate());
         var2.setInt(4, this.getPrize());
         var2.setInt(5, this.getPrize());
         var2.execute();
      } catch (SQLException var7) {
         _log.warn("Lottery: Could not store new lottery data: " + var7);
      } finally {
         DbUtils.closeQuietly(var1, var2);
      }
   }

   public int[] decodeNumbers(int var1, int var2) {
      int[] var3 = new int[5];
      int var4 = 0;

      for (int var5 = 1; var1 > 0; var5++) {
         int var6 = var1 / 2;
         if (var6 != var1 / 2.0) {
            var3[var4++] = var5;
         }

         var1 /= 2;
      }

      for (int var7 = 17; var2 > 0; var7++) {
         int var8 = var2 / 2;
         if (var8 != var2 / 2.0) {
            var3[var4++] = var7;
         }

         var2 /= 2;
      }

      return var3;
   }

   public int[] checkTicket(int var1, int var2, int var3) {
      int[] var4 = new int[]{0, 0};
      Connection var5 = null;
      PreparedStatement var6 = null;
      ResultSet var7 = null;

      try {
         var5 = DatabaseFactory.getInstance().getConnection();
         var6 = var5.prepareStatement("SELECT `number1`, `number2`, `prize1`, `prize2`, `prize3` FROM `games` WHERE `id` = 1 AND `idnr` = ?");
         var6.setInt(1, var1);
         var7 = var6.executeQuery();
         if (var7.next()) {
            int var8 = var7.getInt("number1") & var2;
            int var9 = var7.getInt("number2") & var3;
            if (var8 == 0 && var9 == 0) {
               return var4;
            }

            int var10 = 0;

            for (int var11 = 1; var11 <= 16; var11++) {
               int var12 = var8 / 2;
               if (var12 != var8 / 2.0) {
                  var10++;
               }

               int var13 = var9 / 2;
               if (var13 != var9 / 2.0) {
                  var10++;
               }

               var8 = var12;
               var9 = var13;
            }

            switch (var10) {
               case 0:
                  break;
               case 1:
               case 2:
               default:
                  var4[0] = 4;
                  var4[1] = 200;
                  break;
               case 3:
                  var4[0] = 3;
                  var4[1] = var7.getInt("prize3");
                  break;
               case 4:
                  var4[0] = 2;
                  var4[1] = var7.getInt("prize2");
                  break;
               case 5:
                  var4[0] = 1;
                  var4[1] = var7.getInt("prize1");
            }
         }
      } catch (SQLException var17) {
         _log.warn("Lottery: Could not check lottery ticket #" + var1 + ": " + var17);
      } finally {
         DbUtils.closeQuietly(var5, var6, var7);
      }

      return var4;
   }

   public int[] checkTicket(ItemInstance var1) {
      return this.checkTicket(var1.getBlessed(), var1.getEnchantLevel(), var1.getDamaged());
   }

   public boolean isSellableTickets() {
      return this._isSellingTickets;
   }

   public boolean isStarted() {
      return this._isStarted;
   }

   public static LotteryManager getInstance() {
      if (Bx == null) {
         Bx = new LotteryManager();
      }

      return Bx;
   }

   public int getId() {
      return this._number;
   }

   public int getPrize() {
      return this._prize;
   }

   public long getEndDate() {
      return this._enddate;
   }

   private class finishLottery extends RunnableImpl {
      protected finishLottery() {
      }

      @Override
      public void runImpl() throws Exception {
         if (Config.SERVICES_ALLOW_LOTTERY) {
            _log.info("Lottery: Ending lottery #" + LotteryManager.this.getId() + ".");
         }

         int[] var1 = new int[5];
         int var2 = 0;

         for (int var3 = 0; var3 < 5; var3++) {
            boolean var4 = true;

            while (var4) {
               var2 = Rnd.get(20) + 1;
               var4 = false;

               for (int var5 = 0; var5 < var3; var5++) {
                  if (var1[var5] == var2) {
                     var4 = true;
                  }
               }
            }

            var1[var3] = var2;
         }

         if (Config.SERVICES_ALLOW_LOTTERY) {
            _log.info("Lottery: The lucky numbers are " + var1[0] + ", " + var1[1] + ", " + var1[2] + ", " + var1[3] + ", " + var1[4] + ".");
         }

         int var32 = 0;
         int var33 = 0;

         for (int var34 = 0; var34 < 5; var34++) {
            if (var1[var34] < 17) {
               var32 = (int)(var32 + Math.pow(2.0, var1[var34] - 1));
            } else {
               var33 = (int)(var33 + Math.pow(2.0, var1[var34] - 17));
            }
         }

         if (Config.SERVICES_ALLOW_LOTTERY) {
            _log.info("Lottery: Encoded lucky numbers are " + var32 + ", " + var33);
         }

         int var35 = 0;
         int var6 = 0;
         int var7 = 0;
         int var8 = 0;
         Connection var9 = null;
         PreparedStatement var10 = null;
         ResultSet var11 = null;

         try {
            var9 = DatabaseFactory.getInstance().getConnection();
            var10 = var9.prepareStatement(
               "SELECT `items`.`enchant` AS `enchant`, `items_options`.`damaged` AS `damaged` FROM `items`, `items_options` WHERE `items_options`.`blessed` = ? AND `items`.`item_id` = `items_options`.`item_id` AND `items`.`item_type` = 4442"
            );
            var10.setInt(1, LotteryManager.this.getId());
            var11 = var10.executeQuery();

            while (var11.next()) {
               int var12 = var11.getInt("enchant") & var32;
               int var13 = var11.getInt("damaged") & var33;
               if (var12 != 0 || var13 != 0) {
                  int var14 = 0;

                  for (int var15 = 1; var15 <= 16; var15++) {
                     int var16 = var12 / 2;
                     if (var16 != var12 / 2.0) {
                        var14++;
                     }

                     int var17 = var13 / 2;
                     if (var17 != var13 / 2.0) {
                        var14++;
                     }

                     var12 = var16;
                     var13 = var17;
                  }

                  if (var14 == 5) {
                     var35++;
                  } else if (var14 == 4) {
                     var6++;
                  } else if (var14 == 3) {
                     var7++;
                  } else if (var14 > 0) {
                     var8++;
                  }
               }
            }
         } catch (SQLException var30) {
            _log.warn("Lottery: Could restore lottery data: " + var30);
         } finally {
            DbUtils.closeQuietly(var9, var10, var11);
         }

         int var36 = var8 * Config.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
         int var37 = 0;
         int var38 = 0;
         int var39 = 0;
         if (var35 > 0) {
            var37 = (int)((LotteryManager.this.getPrize() - var36) * Config.SERVICES_LOTTERY_5_NUMBER_RATE / var35);
         }

         if (var6 > 0) {
            var38 = (int)((LotteryManager.this.getPrize() - var36) * Config.SERVICES_LOTTERY_4_NUMBER_RATE / var6);
         }

         if (var7 > 0) {
            var39 = (int)((LotteryManager.this.getPrize() - var36) * Config.SERVICES_LOTTERY_3_NUMBER_RATE / var7);
         }

         int var40;
         if (var37 == 0 && var38 == 0 && var39 == 0) {
            var40 = LotteryManager.this.getPrize();
         } else {
            var40 = LotteryManager.this.getPrize() + var37 + var38 + var39;
         }

         if (Config.SERVICES_ALLOW_LOTTERY) {
            _log.info("Lottery: Jackpot for next lottery is " + var40 + ".");
         }

         if (var35 > 0) {
            SystemMessage var41 = new SystemMessage(1112);
            var41.addNumber(LotteryManager.this.getId());
            var41.addNumber(LotteryManager.this.getPrize());
            var41.addNumber(var35);
            Announcements.getInstance().announceToAll(var41);
         } else {
            SystemMessage var42 = new SystemMessage(1113);
            var42.addNumber(LotteryManager.this.getId());
            var42.addNumber(LotteryManager.this.getPrize());
            Announcements.getInstance().announceToAll(var42);
         }

         try {
            var9 = DatabaseFactory.getInstance().getConnection();
            var10 = var9.prepareStatement(
               "UPDATE `games` SET `finished`=1, `prize`=?, `newprize`=?, `number1`=?, `number2`=?, `prize1`=?, `prize2`=?, `prize3`=? WHERE `id`=1 AND `idnr`=?"
            );
            var10.setInt(1, LotteryManager.this.getPrize());
            var10.setInt(2, var40);
            var10.setInt(3, var32);
            var10.setInt(4, var33);
            var10.setInt(5, var37);
            var10.setInt(6, var38);
            var10.setInt(7, var39);
            var10.setInt(8, LotteryManager.this.getId());
            var10.execute();
         } catch (SQLException var28) {
            _log.warn("Lottery: Could not store finished lottery data: " + var28);
         } finally {
            DbUtils.closeQuietly(var9, var10);
         }

         ThreadPoolManager.getInstance().schedule(LotteryManager.this.new startLottery(), 60000L);
         LotteryManager.this._number++;
         LotteryManager.this._isStarted = false;
      }
   }

   private class startLottery extends RunnableImpl {
      protected startLottery() {
      }

      @Override
      public void runImpl() throws Exception {
         if (LotteryManager.this.bH()) {
            LotteryManager.this.bI();
            LotteryManager.this.bJ();
            LotteryManager.this.bK();
         }
      }
   }

   private class stopSellingTickets extends RunnableImpl {
      protected stopSellingTickets() {
      }

      @Override
      public void runImpl() throws Exception {
         if (Config.SERVICES_ALLOW_LOTTERY) {
            _log.info("Lottery: Stopping ticket sell for lottery #" + LotteryManager.this.getId() + ".");
         }

         LotteryManager.this._isSellingTickets = false;
         Announcements.getInstance().announceToAll(Msg.LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED);
      }
   }
}
