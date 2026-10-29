package l2.gameserver.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class mysql {
   private static final Logger _log = LoggerFactory.getLogger(mysql.class);

   public static boolean setEx(DatabaseFactory var0, String var1, Object... var2) {
      Connection var3 = null;
      Statement var4 = null;
      PreparedStatement var5 = null;

      try {
         if (var0 == null) {
            var0 = DatabaseFactory.getInstance();
         }

         var3 = var0.getConnection();
         if (var2.length == 0) {
            var4 = var3.createStatement();
            var4.executeUpdate(var1);
         } else {
            var5 = var3.prepareStatement(var1);
            setVars(var5, var2);
            var5.executeUpdate();
         }
      } catch (Exception var11) {
         _log.warn("Could not execute update '" + var1 + "': " + var11);
         var11.printStackTrace();
         return false;
      } finally {
         DbUtils.closeQuietly(var3, var2.length == 0 ? var4 : var5);
      }

      return true;
   }

   public static void setVars(PreparedStatement var0, Object... var1) throws SQLException {
      for (int var7 = 0; var7 < var1.length; var7++) {
         if (var1[var7] instanceof Number) {
            Number var2 = (Number)var1[var7];
            long var3 = var2.longValue();
            double var5 = var2.doubleValue();
            if (var3 == var5) {
               var0.setLong(var7 + 1, var3);
            } else {
               var0.setDouble(var7 + 1, var5);
            }
         } else if (var1[var7] instanceof String) {
            var0.setString(var7 + 1, (String)var1[var7]);
         }
      }
   }

   public static boolean set(String var0, Object... var1) {
      return setEx(null, var0, var1);
   }

   public static boolean set(String var0) {
      return setEx(null, var0);
   }

   public static Object get(String var0) {
      Object var1 = null;
      Connection var2 = null;
      Statement var3 = null;
      ResultSet var4 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.createStatement();
         var4 = var3.executeQuery(var0 + " LIMIT 1");
         ResultSetMetaData var5 = var4.getMetaData();
         if (var4.next()) {
            if (var5.getColumnCount() > 1) {
               HashMap var6 = new HashMap();

               for (int var7 = var5.getColumnCount(); var7 > 0; var7--) {
                  var6.put(var5.getColumnName(var7), var4.getObject(var7));
               }

               var1 = var6;
            } else {
               var1 = var4.getObject(1);
            }
         }
      } catch (Exception var11) {
         _log.warn("Could not execute query '" + var0 + "': " + var11);
         var11.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var2, var3, var4);
      }

      return var1;
   }

   public static List<Map<String, Object>> getAll(String var0) {
      ArrayList var1 = new ArrayList();
      Connection var2 = null;
      Statement var3 = null;
      ResultSet var4 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.createStatement();
         var4 = var3.executeQuery(var0);
         ResultSetMetaData var5 = var4.getMetaData();

         while (var4.next()) {
            HashMap var6 = new HashMap();

            for (int var7 = var5.getColumnCount(); var7 > 0; var7--) {
               var6.put(var5.getColumnName(var7), var4.getObject(var7));
            }

            var1.add(var6);
         }
      } catch (Exception var11) {
         _log.warn("Could not execute query '" + var0 + "': " + var11);
         var11.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var2, var3, var4);
      }

      return var1;
   }

   public static List<Object> get_array(DatabaseFactory var0, String var1) {
      ArrayList var2 = new ArrayList();
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         if (var0 == null) {
            var0 = DatabaseFactory.getInstance();
         }

         var3 = var0.getConnection();
         var4 = var3.prepareStatement(var1);
         var5 = var4.executeQuery();
         ResultSetMetaData var6 = var5.getMetaData();

         while (var5.next()) {
            if (var6.getColumnCount() > 1) {
               HashMap var7 = new HashMap();

               for (int var8 = 0; var8 < var6.getColumnCount(); var8++) {
                  var7.put(var6.getColumnName(var8 + 1), var5.getObject(var8 + 1));
               }

               var2.add(var7);
            } else {
               var2.add(var5.getObject(1));
            }
         }
      } catch (Exception var12) {
         _log.warn("Could not execute query '" + var1 + "': " + var12);
         var12.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }

      return var2;
   }

   public static List<Object> get_array(String var0) {
      return get_array(null, var0);
   }

   public static int simple_get_int(String var0, String var1, String var2) {
      String var3 = "SELECT " + var0 + " FROM `" + var1 + "` WHERE " + var2 + " LIMIT 1;";
      int var4 = 0;
      Connection var5 = null;
      PreparedStatement var6 = null;
      ResultSet var7 = null;

      try {
         var5 = DatabaseFactory.getInstance().getConnection();
         var6 = var5.prepareStatement(var3);
         var7 = var6.executeQuery();
         if (var7.next()) {
            var4 = var7.getInt(1);
         }
      } catch (Exception var12) {
         _log.warn("mSGI: Error in query '" + var3 + "':" + var12);
         var12.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var5, var6, var7);
      }

      return var4;
   }

   public static Integer[][] simple_get_int_array(DatabaseFactory var0, String[] var1, String var2, String var3) {
      String var4 = null;

      for (String var8 : var1) {
         if (var4 != null) {
            var4 = var4 + ",";
            var4 = var4 + "`" + var8 + "`";
         } else {
            var4 = "`" + var8 + "`";
         }
      }

      String var20 = "SELECT " + var4 + " FROM `" + var2 + "` WHERE " + var3;
      Connection var21 = null;
      PreparedStatement var22 = null;
      ResultSet var23 = null;
      Integer[][] var9 = (Integer[][])null;

      try {
         if (var0 == null) {
            var0 = DatabaseFactory.getInstance();
         }

         var21 = var0.getConnection();
         var22 = var21.prepareStatement(var20);
         var23 = var22.executeQuery();
         ArrayList var10 = new ArrayList();

         int var11;
         for (var11 = 0; var23.next(); var11++) {
            Integer[] var12 = new Integer[var1.length];

            for (int var13 = 0; var13 < var1.length; var13++) {
               var12[var13] = var23.getInt(var13 + 1);
            }

            var10.add(var11, var12);
         }

         var9 = (Integer[][])var10.toArray(new Integer[var11][var1.length]);
      } catch (Exception var17) {
         _log.warn("mSGIA: Error in query '" + var20 + "':" + var17);
         var17.printStackTrace();
      } finally {
         DbUtils.closeQuietly(var21, var22, var23);
      }

      return var9;
   }

   public static Integer[][] simple_get_int_array(String[] var0, String var1, String var2) {
      return simple_get_int_array(null, var0, var1, var2);
   }
}
