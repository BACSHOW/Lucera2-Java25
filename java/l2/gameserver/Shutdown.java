package l2.gameserver;

import com.lucera2.dbmsstruct.model.DBMSStructureSynchronizer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import l2.commons.net.nio.impl.SelectorThread;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.queue.DatabaseWriterQueue;
import l2.gameserver.instancemanager.CoupleManager;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.instancemanager.games.FishingChampionShipManager;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.entity.oly.StadiumPool;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.l2.CGModule;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.utils.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shutdown extends Thread {
   public static final int SHUTDOWN = 0;
   public static final int RESTART = 2;
   public static final int NONE = -1;
   private static final Logger _log = LoggerFactory.getLogger(Shutdown.class);
   private static final Shutdown tE = new Shutdown();
   private Timer tF;
   private int tG;
   private int tH;

   private Shutdown() {
      this.setName(this.getClass().getSimpleName());
      this.setDaemon(true);
      this.tG = -1;
   }

   public static final Shutdown getInstance() {
      return tE;
   }

   public int getSeconds() {
      return this.tG == -1 ? -1 : this.tH;
   }

   public int getMode() {
      return this.tG;
   }

   public synchronized void schedule(int var1, int var2) {
      if (var1 >= 0) {
         if (this.tF != null) {
            this.tF.cancel();
         }

         this.tG = var2;
         this.tH = var1;
         _log.info("Scheduled server " + (var2 == 0 ? "shutdown" : "restart") + " in " + Util.formatTime(var1) + ".");
         this.tF = new Timer("ShutdownCounter", true);
         this.tF.scheduleAtFixedRate(new Shutdown.ShutdownCounter(), 0L, 1000L);
      }
   }

   public void schedule(String var1, int var2) {
      SchedulingPattern var3;
      try {
         var3 = new SchedulingPattern(var1);
      } catch (SchedulingPattern.InvalidPatternException var5) {
         return;
      }

      int var4 = (int)(var3.next(System.currentTimeMillis()) / 1000L - System.currentTimeMillis() / 1000L);
      this.schedule(var4, var2);
   }

   public synchronized void cancel() {
      this.tG = -1;
      if (this.tF != null) {
         this.tF.cancel();
      }

      this.tF = null;
   }

   @Override
   public void run() {
      this.bi();
      if (GameServer.getInstance().getPendingShutdown().compareAndSet(false, true)) {
         _log.info("Shutting down LS/GS communication...");
         AuthServerCommunication.getInstance().shutdown();
         _log.info("Shutting down scripts...");
         GameServer.getInstance().getListeners().onShutdown();
         Scripts.getInstance().shutdown();
         _log.info("Disconnecting players...");
         this.bh();
         this.bg();
         _log.info("Saving data...");
         this.saveData();

         try {
            if (CGModule.getInstance().isActive()) {
               CGModule.getInstance().done();
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }

         try {
            _log.info("Shutting down thread pool...");
            ThreadPoolManager.getInstance().shutdown();
         } catch (Exception var9) {
            var9.printStackTrace();
         }

         if (Config.USE_NETTY) {
            _log.info("Shutting down Netty network engine...");
            try {
               l2.gameserver.network.l2.netty.NettyGameServer.getInstance().shutdown();
            } catch (Exception e) {
               e.printStackTrace();
            }
         } else if (GameServer.getInstance() != null) {
            _log.info("Shutting down selector...");
            for (SelectorThread var4 : GameServer.getInstance().getSelectorThreads()) {
               try {
                  var4.shutdown();
               } catch (Exception var8) {
                  var8.printStackTrace();
               }
            }
         }

         try {
            ThreadPoolManager.getInstance().shutdownNow();
         } catch (Exception var7) {
            var7.printStackTrace();
         }

         try {
            this.dumpTables();
         } catch (Throwable t) {
            _log.warn("Database dump skipped/failed during shutdown: {}", t.getMessage());
         }

         try {
            _log.info("Flushing and shutting down database writer queue...");
            DatabaseWriterQueue.getInstance().shutdown();
            _log.info("Shutting down database communication...");
            DatabaseFactory.getInstance().shutdown();
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         _log.info("Shutdown finished.");
      }
   }

   public void dumpTables() {
      long var1 = System.currentTimeMillis();
      if (Config.DATABASE_DUMP_TABLES != null && Config.DATABASE_DUMP_TABLES.length != 0 && !StringUtils.isBlank(Config.DATABASE_DUMP_FILENAME_FORMAT)) {
         try {
            _log.info("Database backup ...");
            SimpleDateFormat var3 = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat var4 = new SimpleDateFormat("HH-mm-ss");

            try (Connection var5 = DatabaseFactory.getInstance().getConnection()) {
               DBMSStructureSynchronizer var7 = GameServer.getInstance().getDbmsStructureSynchronizer(var5);
               ArrayList var8 = new ArrayList();

               for (String var12 : Config.DATABASE_DUMP_TABLES) {
                  if (StringUtils.equals(var12, "*")) {
                     var8.addAll(var7.getStructTableNames());
                  } else {
                     var8.add(var12);
                  }
               }

               LinkedHashMap var342 = new LinkedHashMap();
               ArrayList var343 = new ArrayList();

               for (Object __raw_var349_196 : (Iterable<?>)(Object)(var8)) {
                  String var349 = (String)__raw_var349_196;
                  String var13 = Config.DATABASE_DUMP_FILENAME_FORMAT;
                  var13 = StringUtils.replaceIgnoreCase(var13, "%table_name%", var349);
                  var13 = StringUtils.replaceIgnoreCase(var13, "%date%", var3.format(var1));
                  var13 = StringUtils.replaceIgnoreCase(var13, "%time%", var4.format(var1));
                  boolean var14 = var13.endsWith("gz");
                  File var15 = new File(var13);
                  _log.info("Dumping '{}' into {} ...", var349, var15.toString());
                  var15.getParentFile().mkdirs();

                  try (FileOutputStream var16 = new FileOutputStream(var15)) {
                     long var18 = 0L;
                     FilterOutputStream var20 = var14 ? new GZIPOutputStream(var16) : new BufferedOutputStream(var16);

                     try {
                        var18 = var7.dump(var349, var20);
                     } finally {
                        var20.close();
                     }

                     if (var18 > 0L) {
                        var342.put(var349, var15);
                     } else {
                        var343.add(var15);
                     }
                  } catch (Exception var337) {
                     _log.warn("{}", var337.getCause() != null ? var337.getCause().getMessage() : var337.getMessage());
                     var343.add(var15);
                  }
               }

               if (!StringUtils.isBlank(Config.DATABASE_DUMP_ZIP_OUT_FILENAME_FORMAT)) {
                  String var345 = Config.DATABASE_DUMP_ZIP_OUT_FILENAME_FORMAT;
                  var345 = StringUtils.replaceIgnoreCase(var345, "%date%", var3.format(var1));
                  var345 = StringUtils.replaceIgnoreCase(var345, "%time%", var4.format(var1));
                  File var350 = new File(var345);
                  var350.getParentFile().mkdirs();

                  try (
                     FileOutputStream var355 = new FileOutputStream(var350);
                     BufferedOutputStream var357 = new BufferedOutputStream(var355, 16777216);
                     ZipOutputStream var359 = new ZipOutputStream(var357, StandardCharsets.UTF_8);
                  ) {
                     for (Object __raw_var362_239 : (Iterable<?>)(Object)(var342.entrySet())) {
                        Entry var362 = (Entry)__raw_var362_239;
                        File var21 = (File)var362.getValue();
                        if (var21.exists()) {
                           _log.info("Zipping {} into {} ...", var21.toString(), var350.toString());
                           ZipEntry var22 = new ZipEntry(var21.getName());
                           var22.setMethod(8);
                           var22.setTime(var1);
                           var22.setComment((String)var362.getKey());
                           var359.putNextEntry(var22);

                           try {
                              try (
                                 FileInputStream var23 = new FileInputStream(var21);
                                 BufferedInputStream var25 = new BufferedInputStream(var23, 16777216);
                              ) {
                                 IOUtils.copyLarge(var25, var359);
                              }

                              var343.add(var21);
                           } finally {
                              var359.closeEntry();
                           }
                        }
                     }
                  }
               }

               if (!var343.isEmpty()) {
                  for (Object __raw_var351_267 : (Iterable<?>)(Object)(var343)) {
                     File var351 = (File)__raw_var351_267;
                     try {
                        var351.delete();
                     } catch (Exception var321) {
                     }
                  }
               }
            } catch (Exception var340) {
               throw new RuntimeException(var340);
            }
         } catch (Exception var341) {
            var341.printStackTrace();
         }
      }
   }

   private void saveData() {
      try {
         if (!SevenSigns.getInstance().isSealValidationPeriod()) {
            SevenSignsFestival.getInstance().saveFestivalData(false);
            _log.info("SevenSignsFestival: Data saved.");
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      try {
         SevenSigns.getInstance().saveSevenSignsData(0, true);
         _log.info("SevenSigns: Data saved.");
      } catch (Exception var10) {
         var10.printStackTrace();
      }

      if (Config.ALLOW_WEDDING) {
         try {
            CoupleManager.getInstance().store();
            _log.info("CoupleManager: Data saved.");
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }

      try {
         FishingChampionShipManager.getInstance().shutdown();
         _log.info("FishingChampionShipManager: Data saved.");
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      try {
         HeroController.getInstance().saveHeroes();
         _log.info("Hero: Data saved.");
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      try {
         for (Residence var3 : ResidenceHolder.getInstance().getResidences()) {
            var3.update();
         }

         _log.info("Residences: Data saved.");
      } catch (Exception var12) {
         var12.printStackTrace();
      }

      if (Config.ALLOW_CURSED_WEAPONS) {
         try {
            CursedWeaponsManager.getInstance().saveData();
            _log.info("CursedWeaponsManager: Data saved.");
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      try {
         NoblesController.getInstance().SaveNobleses();
         _log.info("NoblesController: Data saved.");
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      if (Config.OLY_ENABLED) {
         try {
            ParticipantPool.getInstance().FreePools();
            StadiumPool.getInstance().FreeStadiums();
            OlyController.getInstance().shutdown();
            _log.info("Olympiad System: Oly cleaned and data saved!");
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }
   }

   private void bg() {
      int var1 = 0;

      for (NpcInstance var3 : GameObjectsStorage.getAllNpcs()) {
         if (var3 != null && (var3.isRaid() || var3.isBoss()) && var3.isDead() && !var3.isDeleted()) {
            var1++;
         }
      }

      if (var1 > 0) {
         _log.info("Waiting decoy of " + var1 + " object(s).");

         try {
            Thread.sleep(20000L);
         } catch (Exception var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   private void bh() {
      for (Player var2 : GameObjectsStorage.getAllPlayersForIterate()) {
         try {
            var2.logout();
         } catch (Exception var4) {
            _log.error("Error while disconnecting: " + var2 + "!");
            var4.printStackTrace();
         }
      }
   }

   private void bi() {
      int var1 = GameServer.getInstance().getCurrentMaxOnline();
      int var2 = ServerVariables.getInt("maxTotalOnline", 0);
      if (var1 > var2) {
         ServerVariables.set("maxTotalOnline", var1);
      }
   }

   private class ShutdownCounter extends TimerTask {
      private ShutdownCounter() {
      }

      @Override
      public void run() {
         switch (Shutdown.this.tH) {
            case 0:
               switch (Shutdown.this.tG) {
                  case 0:
                     if (System.getProperty("pgo.instrument") != null) {
                        return;
                     }
                     Runtime.getRuntime().exit(0);
                     break;
                  case 2:
                     if (System.getProperty("pgo.instrument") != null) {
                        return;
                     }
                     Runtime.getRuntime().exit(2);
               }

               this.cancel();
               return;
            case 5:
            case 10:
            case 20:
            case 30:
               Announcements.getInstance().announceToAll(new SystemMessage(1).addNumber(Shutdown.this.tH));
               break;
            case 60:
            case 120:
            case 180:
            case 240:
            case 300:
            case 600:
            case 900:
            case 1800:
               switch (Shutdown.this.tG) {
                  case 0:
                     Announcements.getInstance()
                        .announceByCustomMessage("THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_MINUTES", new String[]{String.valueOf(Shutdown.this.tH / 60)});
                     break;
                  case 2:
                     Announcements.getInstance()
                        .announceByCustomMessage("THE_SERVER_WILL_BE_COMING_RESTARTED_IN_S1_MINUTES", new String[]{String.valueOf(Shutdown.this.tH / 60)});
               }
         }

         Shutdown.this.tH--;
      }
   }
}
