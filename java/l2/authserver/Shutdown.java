package l2.authserver;

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
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import l2.authserver.database.L2DatabaseFactory;
import l2.commons.net.nio.impl.SelectorThread;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shutdown extends Thread {
   public static final int SHUTDOWN = 0;
   public static final int RESTART = 2;
   public static final int NONE = -1;
   private static final Logger _log = LoggerFactory.getLogger(Shutdown.class);
   private static final Shutdown nk = new Shutdown();

   private Shutdown() {
      this.setName(this.getClass().getSimpleName());
      this.setDaemon(true);
   }

   public static final Shutdown getInstance() {
      return nk;
   }

   @Override
   public void run() {
      try {
         _log.info("Shutting down thread pool...");
         ThreadPoolManager.getInstance().shutdown();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      _log.info("Shutting down selector...");
      if (AuthServer.getInstance() != null && AuthServer.getInstance().getSelectorThreads() != null) {
         for (SelectorThread var2 : AuthServer.getInstance().getSelectorThreads()) {
            try {
               var2.shutdown();
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }
      }

      try {
         ThreadPoolManager.getInstance().shutdownNow();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      this.dumpTables();

      try {
         _log.info("Shutting down database communication...");
         L2DatabaseFactory.getInstance().shutdown();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      _log.info("Shutdown finished.");
   }

   public void dumpTables() {
      long var1 = System.currentTimeMillis();
      if (Config.DATABASE_DUMP_TABLES != null && Config.DATABASE_DUMP_TABLES.length != 0 && !StringUtils.isBlank(Config.DATABASE_DUMP_FILENAME_FORMAT)) {
         try {
            _log.info("Database backup ...");
            SimpleDateFormat var3 = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat var4 = new SimpleDateFormat("HH-mm-ss");

            try (Connection var5 = L2DatabaseFactory.getInstance().getConnection()) {
               DBMSStructureSynchronizer var7 = new DBMSStructureSynchronizer(var5, Config.DATABASE_NAME, AuthServer.class.getResourceAsStream("/authd.json"));
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

               for (Object __raw_var349_103 : (Iterable<?>)(Object)(var8)) {
                  String var349 = (String)__raw_var349_103;
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
                     for (Object __raw_var362_146 : (Iterable<?>)(Object)(var342.entrySet())) {
                        Entry var362 = (Entry)__raw_var362_146;
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
                  for (Object __raw_var351_174 : (Iterable<?>)(Object)(var343)) {
                     File var351 = (File)__raw_var351_174;
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
}
