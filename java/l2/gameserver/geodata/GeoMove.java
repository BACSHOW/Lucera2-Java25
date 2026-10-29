package l2.gameserver.geodata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;

public class GeoMove {
   private static List<Location> b(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, int var7) {
      if (Math.abs(var2 - var5) > 256) {
         return Collections.emptyList();
      }

      var2 = GeoEngine.getHeight(var0, var1, var2, var7);
      var5 = GeoEngine.getHeight(var3, var4, var5, var7);
      PathFind var8 = new PathFind(var0, var1, var2, var3, var4, var5, var6, var7);
      if (var8.getPath() != null && !var8.getPath().isEmpty()) {
         ArrayList var9 = new ArrayList(var8.getPath().size() + 2);
         var9.add(new Location(var0, var1, var2));

         for (Location var11 : var8.getPath()) {
            var9.add(var11.geo2world());
         }

         var9.add(new Location(var3, var4, var5));
         if (Config.PATH_CLEAN) {
            c(var9, var7);
         }

         return var9;
      } else {
         return Collections.emptyList();
      }
   }

   public static List<List<Location>> findMovePath(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, int var7) {
      // 1. Tenta motor de alta performance Zero-GC Bidirectional A* (NBA*)
      List<Location> nbaPath = l2.gameserver.geodata.pathfinding.NativeBidirectionalPathFinder.getInstance()
         .findPath(var0, var1, var2, var3, var4, var5, var7);
      if (!nbaPath.isEmpty()) {
         if (nbaPath.size() > 2) {
            nbaPath = l2.gameserver.ai.util.RepositioningMath.suppressCollinear(nbaPath);
            nbaPath = l2.gameserver.ai.util.RepositioningMath.stringPull(nbaPath, (from, to) -> GeoEngine.canMoveWithCollision(from.x, from.y, from.z, to.x, to.y, to.z, var7));
         }
         return b(nbaPath, var7);
      }
      // 2. Fallback clássico
      return b(b(var0, var1, var2, var3, var4, var5, var6, var7), var7);
   }

   private static List<List<Location>> b(List<Location> var0, int var1) {
      int var2 = var0.size();
      if (var2 <= 1) {
         return Collections.emptyList();
      }

      ArrayList var3 = new ArrayList(var2);

      for (int var4 = 1; var4 < var2; var4++) {
         Location var5 = (Location)var0.get(var4);
         Location var6 = (Location)var0.get(var4 - 1);
         List var7 = GeoEngine.MoveList(var6.x, var6.y, var6.z, var5.x, var5.y, var1, true);
         if (var7 == null) {
            return Collections.emptyList();
         }

         if (!var7.isEmpty()) {
            var3.add(var7);
         }
      }

      return var3;
   }

   public static List<Location> constructMoveList(Location var0, Location var1) {
      var0 = var0.world2geo();
      var1 = var1.world2geo();
      int var2 = var1.x - var0.x;
      int var3 = var1.y - var0.y;
      int var4 = var1.z - var0.z;
      int var5 = Math.abs(var2);
      int var6 = Math.abs(var3);
      int var7 = Math.abs(var4);
      float var8 = Math.max(Math.max(var5, var6), var7);
      if (var8 == 0.0F) {
         return Collections.emptyList();
      }

      float var9 = var2 / var8;
      float var10 = var3 / var8;
      float var11 = var4 / var8;
      float var12 = var0.x;
      float var13 = var0.y;
      float var14 = var0.z;
      ArrayList var15 = new ArrayList((int)var8 + 1);
      var15.add(new Location(var0.x, var0.y, var0.z));

      for (int var16 = 0; var16 < var8; var16++) {
         var12 += var9;
         var13 += var10;
         var14 += var11;
         var15.add(new Location((int)(var12 + 0.5F), (int)(var13 + 0.5F), (int)(var14 + 0.5F)));
      }

      return var15;
   }

   private static void c(List<Location> var0, int var1) {
      int var2 = var0.size();
      if (var2 > 2) {
         for (int var3 = 2; var3 < var2; var3++) {
            Location var4 = (Location)var0.get(var3);
            Location var5 = (Location)var0.get(var3 - 1);
            Location var6 = (Location)var0.get(var3 - 2);
            if (var6.equals(var5) || var4.equals(var5) || a(var6, var5, var4)) {
               var0.remove(var3 - 1);
               var2--;
               var3 = Math.max(2, var3 - 2);
            }
         }
      }

      for (int var7 = 0; var7 < var0.size() - 2; var7++) {
         Location var9 = (Location)var0.get(var7);

         for (int var8 = var7 + 2; var8 < var0.size(); var8++) {
            Location var10 = (Location)var0.get(var8);
            if (var9.equals(var10) || GeoEngine.canMoveWithCollision(var9.x, var9.y, var9.z, var10.x, var10.y, var10.z, var1)) {
               while (var7 + 1 < var8) {
                  var0.remove(var7 + 1);
                  var8--;
               }
            }
         }
      }
   }

   private static boolean a(Location var0, Location var1, Location var2) {
      return (var0.x != var2.x || var2.x != var1.x) && (var0.y != var2.y || var2.y != var1.y)
         ? (var0.x - var1.x) * (var0.y - var1.y) == (var1.x - var2.x) * (var1.y - var2.y)
         : true;
   }

   public static List<Location> applyGeoIndent(List<Location> var0, int var1) {
      if (var1 <= 0) {
         return var0;
      }

      long var2 = ((Location)var0.get(var0.size() - 1)).getX() - ((Location)var0.get(0)).getX();
      long var4 = ((Location)var0.get(var0.size() - 1)).getY() - ((Location)var0.get(0)).getY();
      long var6 = ((Location)var0.get(var0.size() - 1)).getZ() - ((Location)var0.get(0)).getZ();
      double var8 = Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
      if (var8 <= var1) {
         Location var14 = (Location)var0.get(0);
         var0.clear();
         var0.add(var14);
         return var0;
      }

      if (var8 >= 1.0) {
         double var10 = var1 / var8;
         int var12 = (int)(var0.size() * var10 + 0.5);

         for (int var13 = 1; var13 <= var12 && var0.size() > 0; var13++) {
            var0.remove(var0.size() - 1);
         }
      }

      return var0;
   }

   public static List<Location> straightLineGeoPath(Location var0, Location var1) {
      int var2 = var1.getX() - var0.getX();
      int var3 = var1.getY() - var0.getY();
      int var4 = var1.getZ() - var0.getZ();
      int var5 = Math.abs(var2);
      int var6 = Math.abs(var3);
      int var7 = Math.abs(var4);
      float var8 = Math.max(Math.max(var5, var6), var7);
      if (var8 == 0.0F) {
         return Collections.emptyList();
      }

      float var9 = var2 / var8;
      float var10 = var3 / var8;
      float var11 = var4 / var8;
      float var12 = var0.getX();
      float var13 = var0.getY();
      float var14 = var0.getZ();
      ArrayList var15 = new ArrayList((int)var8 + 1);
      var15.add(new Location(var0.getX(), var0.getY(), var0.getZ()));

      for (int var16 = 0; var16 < var8; var16++) {
         var12 += var9;
         var13 += var10;
         var14 += var11;
         var15.add(new Location((int)(var12 + 0.5F), (int)(var13 + 0.5F), (int)(var14 + 0.5F)));
      }

      return var15;
   }

   public static Location getIntersectPoint(Location var0, Location var1, int var2, int var3) {
      if (var3 != 0 && var2 != 0 && PositionUtils.isFacing(var0, var1, 90)) {
         double var4 = PositionUtils.convertHeadingToDegree(var1.getHeading());
         double var6 = Math.toRadians(var4 - 90.0);
         double var8 = var3 * (var2 / 1000.0);
         return new Location((int)(var1.getX() - var8 * Math.sin(var6)), (int)(var1.getY() + var8 * Math.cos(var6)), var1.getZ());
      } else {
         return new Location(var1.getX(), var1.getY(), var1.getZ());
      }
   }

   public static List<Location> buildGeoLine(Location var0, Location var1, int var2, boolean var3, boolean var4, int var5, int var6) {
      if (var0.equals(var1)) {
         return Collections.emptyList();
      }

      Location var7 = var0.clone().geo2world();
      Location var8 = var1.clone().geo2world();
      if (var4) {
         Location var13 = var6 > 0 ? var8.clone().indent(var7, var6, true) : var8;
         List var15 = straightLineGeoPath(var0, var13.clone().world2geo());
         return !var15.isEmpty() ? var15 : null;
      }

      if (var3) {
         Location var12 = var6 > 0 ? var8.clone().indent(var7, var6, true) : var8;
         Location var14 = GeoEngine.moveCheckInAir(var7.getX(), var7.getY(), var7.getZ(), var12.getX(), var12.getY(), var12.getZ(), 16.0, var2);
         if (var14 != null && !var14.equals(var7)) {
            List var11 = straightLineGeoPath(var0, var14.world2geo());
            return var11.isEmpty() ? null : var11;
         } else {
            return null;
         }
      } else {
         Location var9 = var6 > 0 ? var8.clone().indent(var7, var6, false) : var8;
         List var10 = GeoEngine.MoveList(var7.getX(), var7.getY(), var7.getZ(), var9.getX(), var9.getY(), var2, false);
         if (var10 != null) {
            return var10.isEmpty() ? null : var10;
         } else {
            return null;
         }
      }
   }

   public static boolean buildGeoPath(
      List<List<Location>> var0,
      Location var1,
      Location var2,
      int var3,
      int var4,
      int var5,
      int var6,
      boolean var7,
      boolean var8,
      boolean var9,
      boolean var10,
      int var11,
      boolean var12
   ) {
      var0.clear();
      if (var1.equals(var2)) {
         return true;
      }

      Location var13 = var1.clone().geo2world();
      Location var14 = var2.clone().geo2world();
      Location var15 = var6 > 0 ? var14.clone().indent(var13, var6, !var10 && !var9) : var14;
      if (var12 || !Config.ALLOW_GEODATA) {
         List var26 = straightLineGeoPath(var1, var15.world2geo());
         if (var26.isEmpty()) {
            return false;
         }

         var0.add(var26);
         return true;
      } else if (var9) {
         if (GeoEngine.canSeeCoord(var13.getX(), var13.getY(), var13.getZ() + var5 + 64, var15.getX(), var15.getY(), var15.getZ(), true, var3)) {
            List var25 = straightLineGeoPath(var1, var15.world2geo());
            if (var25.isEmpty()) {
               return false;
            }

            var0.add(var25);
            return true;
         } else {
            Location var24 = GeoEngine.moveCheckInAir(var13.getX(), var13.getY(), var13.getZ(), var15.getX(), var15.getY(), var15.getZ(), var4, var3);
            if (var24 != null && !var24.equals(var13)) {
               List var28 = straightLineGeoPath(var1, var24.world2geo());
               if (var28.isEmpty()) {
                  return false;
               }

               var0.add(var28);
               return true;
            } else {
               return false;
            }
         }
      } else if (var10) {
         Location var23 = GeoEngine.moveInWaterCheck(var13.getX(), var13.getY(), var13.getZ(), var15.getX(), var15.getY(), var15.getZ(), var11, var3);
         List var27 = straightLineGeoPath(var1, var23.clone().world2geo());
         if (!var27.isEmpty()) {
            var0.add(var27);
         }

         int var30 = var15.getZ() - var23.getZ();
         if (!var23.clone().world2geo().equals(var15.clone().world2geo())) {
            if (var7) {
               List var19 = findMovePath(var23.getX(), var23.getY(), var23.getZ(), var14.getX(), var14.getY(), var14.getZ(), var8, var3);
               if (!var19.isEmpty()) {
                  if (var6 > 0) {
                     List var20 = (List)var19.remove(var19.size() - 1);
                     var20 = applyGeoIndent(var20, var6 >> 4);
                     if (!var20.isEmpty()) {
                        var19.add(var20);
                     }
                  }

                  if (!var19.isEmpty()) {
                     var0.addAll(var19);
                  }
               }
            } else {
               List var31 = GeoEngine.MoveList(var23.getX(), var23.getY(), var23.getZ(), var14.getX(), var14.getY(), var3, false);
               if (var31 != null && !var31.isEmpty()) {
                  var0.add(var31);
               }
            }
         }

         return !var0.isEmpty();
      } else {
         List var16 = GeoEngine.MoveList(var13.getX(), var13.getY(), var13.getZ(), var14.getX(), var14.getY(), var3, true);
         if (var16 != null) {
            if (var16.isEmpty()) {
               return false;
            }

            var16 = applyGeoIndent(var16, var6 >> 4);
            if (var16.isEmpty()) {
               return false;
            }

            var0.add(var16);
            return true;
         } else {
            if (var7) {
               List var17 = findMovePath(var13.getX(), var13.getY(), var13.getZ(), var14.getX(), var14.getY(), var14.getZ(), var8, var3);
               if (!var17.isEmpty()) {
                  if (var6 > 0) {
                     List var18 = (List)var17.remove(var17.size() - 1);
                     var18 = applyGeoIndent(var18, var6 >> 4);
                     if (!var18.isEmpty()) {
                        var17.add(var18);
                     }
                  }

                  if (!var17.isEmpty()) {
                     var0.addAll(var17);
                     return true;
                  }
               }
            }

            var16 = GeoEngine.MoveList(var13.getX(), var13.getY(), var13.getZ(), var15.getX(), var15.getY(), var3, false);
            if (var16 != null) {
               if (var16.size() < 2) {
                  return false;
               }

               var0.add(var16);
               return true;
            } else {
               return false;
            }
         }
      }
   }
}
