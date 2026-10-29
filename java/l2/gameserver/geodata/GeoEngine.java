package l2.gameserver.geodata;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.geometry.Shape;
import l2.commons.util.NaturalOrderComparator;
import l2.commons.threading.ParallelBoot;
import l2.gameserver.Config;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.World;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoEngine {
   private static final Logger _log = LoggerFactory.getLogger(GeoEngine.class);
   public static final byte EAST = 1;
   public static final byte WEST = 2;
   public static final byte SOUTH = 4;
   public static final byte NORTH = 8;
   public static final byte NSWE_ALL = 15;
   public static final byte NSWE_NONE = 0;
   public static final byte BLOCKTYPE_FLAT = 0;
   public static final byte BLOCKTYPE_COMPLEX = 1;
   public static final byte BLOCKTYPE_MULTILEVEL = 2;
   public static final int BLOCKS_IN_MAP = 65536;
   public static int MAX_LAYERS = 1;
   private static final ByteBuffer[][] xK = new ByteBuffer[World.WORLD_SIZE_X][World.WORLD_SIZE_Y];
   private static final byte[][][][][] xL = new byte[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][1][][];

   public static short getType(int var0, int var1, int var2) {
      return a(var0 - World.MAP_MIN_X >> 4, var1 - World.MAP_MIN_Y >> 4, var2);
   }

   public static int getHeight(Location var0, int var1) {
      return getHeight(var0.x, var0.y, var0.z, var1);
   }

   public static int getHeight(int var0, int var1, int var2, int var3) {
      return NgetHeight(var0 - World.MAP_MIN_X >> 4, var1 - World.MAP_MIN_Y >> 4, var2, var3);
   }

   public static boolean canMoveToCoord(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      return a(var0, var1, var2, var3, var4, var5, false, var6) == 0;
   }

   public static byte getNSWE(int var0, int var1, int var2, int var3) {
      return NgetNSWE(var0 - World.MAP_MIN_X >> 4, var1 - World.MAP_MIN_Y >> 4, var2, var3);
   }

   public static Location moveCheck(int var0, int var1, int var2, int var3, int var4, int var5) {
      return a(var0, var1, var2, var3, var4, false, false, false, var5);
   }

   public static Location moveCheck(int var0, int var1, int var2, int var3, int var4, boolean var5, int var6) {
      return a(var0, var1, var2, var3, var4, false, false, var5, var6);
   }

   public static Location moveCheckWithCollision(int var0, int var1, int var2, int var3, int var4, int var5) {
      return a(var0, var1, var2, var3, var4, true, false, false, var5);
   }

   public static Location moveCheckWithCollision(int var0, int var1, int var2, int var3, int var4, boolean var5, int var6) {
      return a(var0, var1, var2, var3, var4, true, false, var5, var6);
   }

   public static Location moveCheckBackward(int var0, int var1, int var2, int var3, int var4, int var5) {
      return a(var0, var1, var2, var3, var4, false, true, false, var5);
   }

   public static Location moveCheckBackward(int var0, int var1, int var2, int var3, int var4, boolean var5, int var6) {
      return a(var0, var1, var2, var3, var4, false, true, var5, var6);
   }

   public static Location moveCheckBackwardWithCollision(int var0, int var1, int var2, int var3, int var4, int var5) {
      return a(var0, var1, var2, var3, var4, true, true, false, var5);
   }

   public static Location moveCheckBackwardWithCollision(int var0, int var1, int var2, int var3, int var4, boolean var5, int var6) {
      return a(var0, var1, var2, var3, var4, true, true, var5, var6);
   }

   public static Location moveInWaterCheck(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return a(var0 - World.MAP_MIN_X >> 4, var1 - World.MAP_MIN_Y >> 4, var2, var3 - World.MAP_MIN_X >> 4, var4 - World.MAP_MIN_Y >> 4, var5, var6, var7);
   }

   public static Location moveCheckForAI(Location var0, Location var1, int var2) {
      return a(var0.x - World.MAP_MIN_X >> 4, var0.y - World.MAP_MIN_Y >> 4, var0.z, var1.x - World.MAP_MIN_X >> 4, var1.y - World.MAP_MIN_Y >> 4, var2);
   }

   public static Location moveCheckInAir(int var0, int var1, int var2, int var3, int var4, int var5, double var6, int var8) {
      int var9 = var0 - World.MAP_MIN_X >> 4;
      int var10 = var1 - World.MAP_MIN_Y >> 4;
      int var11 = var3 - World.MAP_MIN_X >> 4;
      int var12 = var4 - World.MAP_MIN_Y >> 4;
      int var13 = NgetHeight(var11, var12, var5, var8);
      if (var5 <= var13 + 32) {
         var5 = var13 + 32;
      }

      Location var14 = canSee(var9, var10, var2, var11, var12, var5, true, var8);
      return var14.equals(var9, var10, var2) ? null : var14.geo2world();
   }

   public static boolean canSeeTarget(GameObject var0, GameObject var1, boolean var2) {
      if (var1 == null) {
         return false;
      } else {
         return !(var1 instanceof GeoCollision) && !var0.equals(var1)
            ? canSeeCoord(var0, var1.getX(), var1.getY(), var1.getZ() + (int)var1.getColHeight() + 64, var2)
            : true;
      }
   }

   public static boolean canSeeCoord(GameObject var0, int var1, int var2, int var3, boolean var4) {
      return var0 != null && canSeeCoord(var0.getX(), var0.getY(), var0.getZ() + (int)var0.getColHeight() + 64, var1, var2, var3, var4, var0.getGeoIndex());
   }

   public static boolean canSeeCoord(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, int var7) {
      int var8 = var0 - World.MAP_MIN_X >> 4;
      int var9 = var1 - World.MAP_MIN_Y >> 4;
      int var10 = var3 - World.MAP_MIN_X >> 4;
      int var11 = var4 - World.MAP_MIN_Y >> 4;
      return canSee(var8, var9, var2, var10, var11, var5, var6, var7).equals(var10, var11, var5)
         && canSee(var10, var11, var5, var8, var9, var2, var6, var7).equals(var8, var9, var2);
   }

   public static boolean canMoveWithCollision(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      return a(var0, var1, var2, var3, var4, var5, true, var6) == 0;
   }

   public static boolean checkNSWE(byte var0, int var1, int var2, int var3, int var4) {
      if (var0 == 15) {
         return true;
      }

      if (var0 == 0) {
         return false;
      }

      if (var3 > var1) {
         if ((var0 & 1) == 0) {
            return false;
         }
      } else if (var3 < var1 && (var0 & 2) == 0) {
         return false;
      }

      if (var4 > var2) {
         if ((var0 & 4) == 0) {
            return false;
         }
      } else if (var4 < var2 && (var0 & 8) == 0) {
         return false;
      }

      return true;
   }

   public static String geoXYZ2Str(int var0, int var1, int var2) {
      return "(" + String.valueOf((var0 << 4) + World.MAP_MIN_X + 8) + " " + ((var1 << 4) + World.MAP_MIN_Y + 8) + " " + var2 + ")";
   }

   public static String NSWE2Str(byte var0) {
      String var1 = "";
      if ((var0 & 8) == 8) {
         var1 = var1 + "N";
      }

      if ((var0 & 4) == 4) {
         var1 = var1 + "S";
      }

      if ((var0 & 2) == 2) {
         var1 = var1 + "W";
      }

      if ((var0 & 1) == 1) {
         var1 = var1 + "E";
      }

      return var1.isEmpty() ? "X" : var1;
   }

   private static boolean a(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      short[] var7 = new short[MAX_LAYERS + 1];
      short[] var8 = new short[MAX_LAYERS + 1];
      NGetLayers(var0, var1, var7, var6);
      NGetLayers(var3, var4, var8, var6);
      if (var7[0] != 0 && var8[0] != 0) {
         short var10 = -32768;

         for (int var11 = 1; var11 <= var8[0]; var11++) {
            short var9 = (short)((short)(var8[var11] & 65520) >> 1);
            if (Math.abs(var5 - var10) > Math.abs(var5 - var9)) {
               var10 = var9;
            }
         }

         if (var5 + 32 >= var10) {
            return true;
         }

         short var17 = -32768;

         for (int var12 = 1; var12 <= var8[0]; var12++) {
            short var15 = (short)((short)(var8[var12] & 65520) >> 1);
            if (var15 < var10 + Config.MIN_LAYER_HEIGHT && Math.abs(var5 - var17) > Math.abs(var5 - var15)) {
               var17 = var15;
            }
         }

         if (var17 == -32768) {
            return false;
         }

         short var18 = -32768;
         byte var13 = 15;

         for (int var14 = 1; var14 <= var7[0]; var14++) {
            short var16 = (short)((short)(var7[var14] & 65520) >> 1);
            if (var16 < var2 + Config.MIN_LAYER_HEIGHT && Math.abs(var2 - var18) > Math.abs(var2 - var16)) {
               var18 = var16;
               var13 = (byte)(var7[var14] & 15);
            }
         }

         return checkNSWE(var13, var0, var1, var3, var4);
      } else {
         return true;
      }
   }

   private static int a(short[] var0, int var1) {
      short var3 = -32768;
      int var4 = Integer.MIN_VALUE;

      for (int var5 = 1; var5 <= var0[0]; var5++) {
         short var2 = (short)((short)(var0[var5] & 65520) >> 1);
         if (var2 < var1 && var3 < var2) {
            var3 = var2;
            var4 = var0[var5];
         }
      }

      return var4;
   }

   private static short a(short[] var0, int var1, int var2) {
      int var3;
      int var4;
      if (var1 > var2) {
         var3 = var2;
         var4 = var1;
      } else {
         var3 = var1;
         var4 = var2;
      }

      short var6 = -32768;
      short var7 = -32768;

      for (int var8 = 1; var8 <= var0[0]; var8++) {
         short var5 = (short)((short)(var0[var8] & 65520) >> 1);
         if (var3 <= var5 && var5 <= var4) {
            return -32768;
         }

         if (var5 < var1 && var7 < var5) {
            var7 = var5;
            var6 = var0[var8];
         }
      }

      return var6;
   }

   public static boolean canSeeWallCheck(short var0, short var1, byte var2, int var3, boolean var4) {
      short var5 = (short)((short)(var1 & 65520) >> 1);
      if (var4) {
         return var5 < var3;
      }

      short var6 = (short)((short)(var0 & 65520) >> 1);
      int var7 = var5 - var6;
      return (var0 & 15 & var2) != 0 || var7 > -Config.MAX_Z_DIFF && var7 != 0;
   }

   public static Location canSee(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, int var7) {
      int var8 = var3 - var0;
      int var9 = var4 - var1;
      int var10 = var5 - var2;
      int var11 = Math.abs(var8);
      int var12 = Math.abs(var9);
      float var13 = Math.max(var11, var12);
      int var14 = var0;
      int var15 = var1;
      int var16 = var2;
      short[] var17 = new short[MAX_LAYERS + 1];
      NGetLayers(var14, var15, var17, var7);
      Location var18 = new Location(var0, var1, var2, -1);
      if (var13 == 0.0F) {
         if (a(var17, var16, var16 + var10) != -32768) {
            var18.set(var3, var4, var5, 1);
         }

         return var18;
      } else {
         float var19 = var8 / var13;
         float var20 = var9 / var13;
         float var21 = var10 / var13;
         float var22 = var21 / 2.0F;
         float var23 = var14;
         float var24 = var15;
         float var25 = var16;
         short[] var30 = new short[MAX_LAYERS + 1];

         for (int var34 = 0; var34 < var13; var34++) {
            if (var17[0] == 0) {
               var18.set(var3, var4, var5, 0);
               return var18;
            }

            var23 += var19;
            var24 += var20;
            var25 += var21;
            int var26 = (int)(var23 + 0.5F);
            int var27 = (int)(var24 + 0.5F);
            int var28 = (int)(var25 + 0.5F);
            int var29 = (int)(var16 + var22);
            short var31;
            if ((var31 = a(var17, var16, var29)) == -32768) {
               return var18.setH(-10);
            }

            NGetLayers(var14, var15, var17, var7);
            if (var17[0] == 0) {
               var18.set(var3, var4, var5, 0);
               return var18;
            }

            short var32;
            if ((var32 = a(var17, var28, var29)) == -32768) {
               return var18.setH(-11);
            }

            if (var14 == var26) {
               if (!canSeeWallCheck(var31, var32, (byte)(var27 > var15 ? 4 : 8), var16, var6)) {
                  return var18.setH(-20);
               }
            } else if (var15 == var27) {
               if (!canSeeWallCheck(var31, var32, (byte)(var26 > var14 ? 1 : 2), var16, var6)) {
                  return var18.setH(-21);
               }
            } else {
               NGetLayers(var14, var27, var30, var7);
               if (var30[0] == 0) {
                  var18.set(var3, var4, var5, 0);
                  return var18;
               }

               short var33;
               if ((var33 = a(var30, var28, var29)) == -32768) {
                  return var18.setH(-30);
               }

               if (!canSeeWallCheck(var31, var33, (byte)(var27 > var15 ? 4 : 8), var16, var6)
                  || !canSeeWallCheck(var33, var32, (byte)(var26 > var14 ? 1 : 2), var16, var6)) {
                  NGetLayers(var26, var15, var30, var7);
                  if (var30[0] == 0) {
                     var18.set(var3, var4, var5, 0);
                     return var18;
                  }

                  if ((var33 = a(var30, var28, var29)) == -32768) {
                     return var18.setH(-31);
                  }

                  if (!canSeeWallCheck(var31, var33, (byte)(var26 > var14 ? 1 : 2), var16, var6)) {
                     return var18.setH(-32);
                  }

                  if (!canSeeWallCheck(var33, var32, (byte)(var26 > var14 ? 1 : 2), var16, var6)) {
                     return var18.setH(-33);
                  }
               }
            }

            var18.set(var14, var15, var16);
            var14 = var26;
            var15 = var27;
            var16 = var28;
         }

         var18.set(var3, var4, var5, 255);
         return var18;
      }
   }

   private static Location a(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      int var8 = var3 - var0;
      int var9 = var4 - var1;
      int var10 = var5 - var2;
      byte var11 = o(var8);
      byte var12 = o(var9);
      var8 = Math.abs(var8);
      var9 = Math.abs(var9);
      if (var8 + var9 == 0) {
         return new Location(var0, var1, var2).geo2world();
      }

      float var13 = var8 == 0 ? 0.0F : var10 / var8;
      float var14 = var9 == 0 ? 0.0F : var10 / var9;
      float var18 = var0;
      float var19 = var1;
      float var20 = var2;
      if (var8 >= var9) {
         int var21 = 2 * var9;
         int var22 = var21 - var8;
         int var23 = var21 - 2 * var8;

         for (int var24 = 0; var24 < var8; var24++) {
            int var15 = var0;
            int var16 = var1;
            int var17 = var2;
            var0 = (int)var18;
            var1 = (int)var19;
            var2 = (int)var20;
            if (var22 > 0) {
               var22 += var23;
               var18 += var11;
               var20 += var13;
               var19 += var12;
               var20 += var14;
            } else {
               var22 += var21;
               var18 += var11;
               var20 += var13;
            }

            if (var20 >= var6 || !a(var0, var1, var2, (int)var18, (int)var19, (int)var20, var7)) {
               return new Location(var15, var16, var17).geo2world();
            }
         }
      } else {
         int var32 = 2 * var8;
         int var33 = var32 - var9;
         int var34 = var32 - 2 * var9;

         for (int var35 = 0; var35 < var9; var35++) {
            int var27 = var0;
            int var28 = var1;
            int var29 = var2;
            var0 = (int)var18;
            var1 = (int)var19;
            var2 = (int)var20;
            if (var33 > 0) {
               var33 += var34;
               var18 += var11;
               var20 += var13;
               var19 += var12;
               var20 += var14;
            } else {
               var33 += var32;
               var19 += var12;
               var20 += var14;
            }

            if (var20 >= var6 || !a(var0, var1, var2, (int)var18, (int)var19, (int)var20, var7)) {
               return new Location(var27, var28, var29).geo2world();
            }
         }
      }

      return new Location((int)var18, (int)var19, (int)var20).geo2world();
   }

   private static int a(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, int var7) {
      int var8 = var0 - World.MAP_MIN_X >> 4;
      int var9 = var1 - World.MAP_MIN_Y >> 4;
      int var10 = var3 - World.MAP_MIN_X >> 4;
      int var11 = var4 - World.MAP_MIN_Y >> 4;
      int var12 = var10 - var8;
      int var13 = var11 - var9;
      int var14 = var5 - var2;
      int var15 = Math.abs(var12);
      int var16 = Math.abs(var13);
      int var17 = Math.abs(var14);
      float var18 = Math.max(var15, var16);
      if (var18 == 0.0F) {
         return -5;
      }

      int var19 = var8;
      int var20 = var9;
      int var21 = var2;
      short[] var22 = new short[MAX_LAYERS + 1];
      NGetLayers(var19, var20, var22, var7);
      if (var22[0] == 0) {
         return 0;
      }

      float var23 = var12 / var18;
      float var24 = var13 / var18;
      float var25 = var19;
      float var26 = var20;
      short[] var29 = new short[MAX_LAYERS + 1];
      short[] var30 = new short[MAX_LAYERS + 1];

      for (int var32 = 0; var32 < var18; var32++) {
         var25 += var23;
         var26 += var24;
         int var27 = (int)(var25 + 0.5F);
         int var28 = (int)(var26 + 0.5F);
         NGetLayers(var27, var28, var29, var7);
         if ((var21 = NcanMoveNext(var19, var20, var21, var22, var27, var28, var29, var30, var6, var7)) == Integer.MIN_VALUE) {
            return 1;
         }

         short[] var31 = var22;
         var22 = var29;
         var29 = var31;
         var19 = var27;
         var20 = var28;
      }

      var14 = var21 - var5;
      var17 = Math.abs(var14);
      if (Config.ALLOW_FALL_FROM_WALLS) {
         return var14 < Config.MAX_Z_DIFF ? 0 : var14 * 10000;
      } else {
         return var17 > Config.MAX_Z_DIFF ? var17 * 1000 : 0;
      }
   }

   private static Location a(int var0, int var1, int var2, int var3, int var4, boolean var5, boolean var6, boolean var7, int var8) {
      int var9 = var0 - World.MAP_MIN_X >> 4;
      int var10 = var1 - World.MAP_MIN_Y >> 4;
      int var11 = var3 - World.MAP_MIN_X >> 4;
      int var12 = var4 - World.MAP_MIN_Y >> 4;
      int var13 = var11 - var9;
      int var14 = var12 - var10;
      int var15 = Math.abs(var13);
      int var16 = Math.abs(var14);
      float var17 = Math.max(var15, var16);
      if (var17 == 0.0F) {
         return new Location(var0, var1, var2);
      }

      float var18 = var13 / var17;
      float var19 = var14 / var17;
      int var20 = var9;
      int var21 = var10;
      int var22 = var2;
      float var23 = var20;
      float var24 = var21;
      short[] var28 = new short[MAX_LAYERS + 1];
      short[] var29 = new short[MAX_LAYERS + 1];
      short[] var30 = new short[MAX_LAYERS + 1];
      NGetLayers(var20, var21, var30, var8);
      int var32 = var20;
      int var33 = var21;
      int var34 = var22;

      for (int var35 = 0; var35 < var17; var35++) {
         var23 += var18;
         var24 += var19;
         int var25 = (int)(var23 + 0.5F);
         int var26 = (int)(var24 + 0.5F);
         NGetLayers(var25, var26, var28, var8);
         int var27;
         if ((var27 = NcanMoveNext(var20, var21, var22, var30, var25, var26, var28, var29, var5, var8)) == Integer.MIN_VALUE
            || var6 && NcanMoveNext(var25, var26, var27, var28, var20, var21, var30, var29, var5, var8) == Integer.MIN_VALUE) {
            break;
         }

         short[] var31 = var30;
         var30 = var28;
         var28 = var31;
         if (var7) {
            var32 = var20;
            var33 = var21;
            var34 = var22;
         }

         var20 = var25;
         var21 = var26;
         var22 = var27;
      }

      if (var7) {
         var20 = var32;
         var21 = var33;
         var22 = var34;
      }

      return new Location(var20, var21, var22).geo2world();
   }

   public static List<Location> MoveList(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6) {
      int var7 = var0 - World.MAP_MIN_X >> 4;
      int var8 = var1 - World.MAP_MIN_Y >> 4;
      int var9 = var3 - World.MAP_MIN_X >> 4;
      int var10 = var4 - World.MAP_MIN_Y >> 4;
      int var11 = var9 - var7;
      int var12 = var10 - var8;
      byte var13 = o(var11);
      byte var14 = o(var12);
      if (var11 < 0) {
         var11 = -var11;
      }

      if (var12 < 0) {
         var12 = -var12;
      }

      byte var15;
      byte var16;
      int var17;
      int var18;
      if (var11 > var12) {
         var15 = var13;
         var16 = 0;
         var17 = var12;
         var18 = var11;
      } else {
         var15 = 0;
         var16 = var14;
         var17 = var11;
         var18 = var12;
      }

      int var19 = var18 / 2;
      int var20 = var7;
      int var21 = var8;
      int var22 = var2;
      int var23 = var20;
      int var24 = var21;
      short[] var26 = new short[MAX_LAYERS + 1];
      short[] var27 = new short[MAX_LAYERS + 1];
      short[] var28 = new short[MAX_LAYERS + 1];
      NGetLayers(var20, var21, var28, var5);
      if (var28[0] == 0) {
         return null;
      }

      ArrayList var29 = new ArrayList(Math.min(1024, var18 + 1));
      var29.add(new Location(var20, var21, var22));

      for (int var30 = 0; var30 < var18; var30++) {
         var19 -= var17;
         if (var19 < 0) {
            var19 += var18;
            var23 += var13;
            var24 += var14;
         } else {
            var23 += var15;
            var24 += var16;
         }

         NGetLayers(var23, var24, var26, var5);
         int var25;
         if ((var25 = NcanMoveNext(var20, var21, var22, var28, var23, var24, var26, var27, false, var5)) == Integer.MIN_VALUE) {
            if (var6) {
               return null;
            }
            break;
         }

         short[] var31 = var28;
         var28 = var26;
         var26 = var31;
         var20 = var23;
         var21 = var24;
         var22 = var25;
         var29.add(new Location(var20, var21, var22));
      }

      return var29;
   }

   private static Location a(int var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = var3 - var0;
      int var7 = var4 - var1;
      byte var8 = o(var6);
      byte var9 = o(var7);
      var6 = Math.abs(var6);
      var7 = Math.abs(var7);
      if (var6 + var7 >= 2 && (var6 != 2 || var7 != 0) && (var6 != 0 || var7 != 2)) {
         int var13 = var0;
         int var14 = var1;
         int var15 = var2;
         if (var6 >= var7) {
            int var16 = 2 * var7;
            int var17 = var16 - var6;
            int var18 = var16 - 2 * var6;

            for (int var19 = 0; var19 < var6; var19++) {
               int var10 = var0;
               int var11 = var1;
               int var12 = var2;
               var0 = var13;
               var1 = var14;
               var2 = var15;
               if (var17 > 0) {
                  var17 += var18;
                  var13 += var8;
                  var14 += var9;
               } else {
                  var17 += var16;
                  var13 += var8;
               }

               var15 = NcanMoveNextForAI(var0, var1, var2, var13, var14, var5);
               if (var15 == 0) {
                  return new Location(var10, var11, var12).geo2world();
               }
            }
         } else {
            int var25 = 2 * var6;
            int var26 = var25 - var7;
            int var27 = var25 - 2 * var7;

            for (int var28 = 0; var28 < var7; var28++) {
               int var22 = var0;
               int var23 = var1;
               int var24 = var2;
               var0 = var13;
               var1 = var14;
               var2 = var15;
               if (var26 > 0) {
                  var26 += var27;
                  var13 += var8;
                  var14 += var9;
               } else {
                  var26 += var25;
                  var14 += var9;
               }

               var15 = NcanMoveNextForAI(var0, var1, var2, var13, var14, var5);
               if (var15 == 0) {
                  return new Location(var22, var23, var24).geo2world();
               }
            }
         }

         return new Location(var13, var14, var15).geo2world();
      } else {
         return new Location(var0, var1, var2).geo2world();
      }
   }

   private static boolean a(int var0, int var1, int var2, int var3, int var4, int var5, short[] var6, int var7) {
      NGetLayers(var0, var1, var6, var7);
      if (var6[0] == 0) {
         return true;
      }

      int var8;
      if ((var8 = a(var6, var2 + Config.MIN_LAYER_HEIGHT)) == Integer.MIN_VALUE) {
         return false;
      }

      short var9 = (short)((short)(var8 & 65520) >> 1);
      return Math.abs(var9 - var5) < Config.MAX_Z_DIFF && Math.abs(var9 - var2) < Config.MAX_Z_DIFF
         ? checkNSWE((byte)(var8 & 15), var0, var1, var3, var4)
         : false;
   }

   public static int NcanMoveNext(int var0, int var1, int var2, short[] var3, int var4, int var5, short[] var6, short[] var7, boolean var8, int var9) {
      if (var3[0] != 0 && var6[0] != 0) {
         int var10;
         if ((var10 = a(var3, var2 + Config.MIN_LAYER_HEIGHT)) == Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
         }

         byte var12 = (byte)(var10 & 15);
         if (!checkNSWE(var12, var0, var1, var4, var5)) {
            return Integer.MIN_VALUE;
         }

         short var13 = (short)((short)(var10 & 65520) >> 1);
         int var11;
         if ((var11 = a(var6, var13 + Config.MIN_LAYER_HEIGHT)) == Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
         }

         short var14 = (short)((short)(var11 & 65520) >> 1);
         if (var0 == var4 || var1 == var5) {
            if (var8) {
               if (var0 == var4) {
                  NgetHeightAndNSWE(var0 - 1, var1, var13, var7, var9);
                  if (Math.abs(var7[0] - var13) <= 15
                     && checkNSWE(var12, var0 - 1, var1, var0, var1)
                     && checkNSWE((byte)var7[1], var0 - 1, var1, var0 - 1, var5)) {
                     NgetHeightAndNSWE(var0 + 1, var1, var13, var7, var9);
                     if (Math.abs(var7[0] - var13) <= 15
                        && checkNSWE(var12, var0 + 1, var1, var0, var1)
                        && checkNSWE((byte)var7[1], var0 + 1, var1, var0 + 1, var5)) {
                        return var14;
                     }

                     return Integer.MIN_VALUE;
                  }

                  return Integer.MIN_VALUE;
               }

               NgetHeightAndNSWE(var0, var1 - 1, var13, var7, var9);
               if (Math.abs(var7[0] - var13) >= Config.MAX_Z_DIFF
                  || !checkNSWE(var12, var0, var1 - 1, var0, var1)
                  || !checkNSWE((byte)var7[1], var0, var1 - 1, var4, var1 - 1)) {
                  return Integer.MIN_VALUE;
               }

               NgetHeightAndNSWE(var0, var1 + 1, var13, var7, var9);
               if (Math.abs(var7[0] - var13) >= Config.MAX_Z_DIFF
                  || !checkNSWE(var12, var0, var1 + 1, var0, var1)
                  || !checkNSWE((byte)var7[1], var0, var1 + 1, var4, var1 + 1)) {
                  return Integer.MIN_VALUE;
               }
            }

            return var14;
         } else if (!a(var0, var5, var13, var4, var5, var14, var7, var9)) {
            return Integer.MIN_VALUE;
         } else {
            return !a(var4, var1, var13, var4, var5, var14, var7, var9) ? Integer.MIN_VALUE : var14;
         }
      } else {
         return var2;
      }
   }

   public static int NcanMoveNextForAI(int var0, int var1, int var2, int var3, int var4, int var5) {
      short[] var6 = new short[MAX_LAYERS + 1];
      short[] var7 = new short[MAX_LAYERS + 1];
      NGetLayers(var0, var1, var6, var5);
      NGetLayers(var3, var4, var7, var5);
      if (var6[0] != 0 && var7[0] != 0) {
         short var9 = -32768;
         byte var10 = 15;

         for (int var11 = 1; var11 <= var6[0]; var11++) {
            short var8 = (short)((short)(var6[var11] & 65520) >> 1);
            if (Math.abs(var2 - var9) > Math.abs(var2 - var8)) {
               var9 = var8;
               var10 = (byte)(var6[var11] & 15);
            }
         }

         if (var9 == -32768) {
            return 0;
         }

         short var15 = -32768;
         byte var12 = 15;

         for (int var13 = 1; var13 <= var7[0]; var13++) {
            short var14 = (short)((short)(var7[var13] & 65520) >> 1);
            if (Math.abs(var2 - var15) > Math.abs(var2 - var14)) {
               var15 = var14;
               var12 = (byte)(var7[var13] & 15);
            }
         }

         if (var15 == -32768) {
            return 0;
         } else if (var9 > var15 && var9 - var15 > Config.MAX_Z_DIFF) {
            return 0;
         } else if (checkNSWE(var10, var0, var1, var3, var4) && checkNSWE(var12, var3, var4, var0, var1)) {
            return var15 == 0 ? 1 : var15;
         } else {
            return 0;
         }
      } else {
         return var2 == 0 ? 1 : var2;
      }
   }

   public static void NGetLayers(int var0, int var1, short[] var2, int var3) {
      var2[0] = 0;
      byte[] var4 = b(var0, var1, var3);
      if (var4 != null) {
         int var7 = 0;
         byte var8 = var4[var7];
         var7++;
         switch (var8) {
            case 0:
               short var17 = makeShort(var4[var7 + 1], var4[var7]);
               var17 = (short)(var17 & 65520);
               var2[0]++;
               var2[1] = (short)((short)(var17 << 1) | 15);
               return;
            case 1:
               int var12 = getCell(var0);
               int var13 = getCell(var1);
               var7 += (var12 << 3) + var13 << 1;
               short var9 = makeShort(var4[var7 + 1], var4[var7]);
               var2[0]++;
               var2[1] = var9;
               return;
            case 2:
               int var5 = getCell(var0);
               int var6 = getCell(var1);

               for (int var10 = (var5 << 3) + var6; var10 > 0; var10--) {
                  byte var11 = var4[var7];
                  var7 += (var11 << 1) + 1;
               }

               byte var19 = var4[var7];
               var7++;
               if (var19 > 0 && var19 <= MAX_LAYERS) {
                  for (var2[0] = var19; var19 > 0; var7 += 2) {
                     var2[var19] = makeShort(var4[var7 + 1], var4[var7]);
                     var19--;
                  }

                  return;
               } else {
                  return;
               }
            default:
               _log.error("GeoEngine: Unknown block type");
         }
      }
   }

   private static short a(int var0, int var1, int var2) {
      byte[] var3 = b(var0, var1, var2);
      return var3 == null ? 0 : var3[0];
   }

   public static int NgetHeight(int var0, int var1, int var2, int var3) {
      byte[] var4 = b(var0, var1, var3);
      if (var4 == null) {
         return var2;
      }

      int var7 = 0;
      byte var8 = var4[var7];
      var7++;
      switch (var8) {
         case 0:
            short var21 = makeShort(var4[var7 + 1], var4[var7]);
            return (short)(var21 & 65520);
         case 1:
            int var15 = getCell(var0);
            int var16 = getCell(var1);
            var7 += (var15 << 3) + var16 << 1;
            short var20 = makeShort(var4[var7 + 1], var4[var7]);
            return (short)((short)(var20 & 65520) >> 1);
         case 2:
            int var5 = getCell(var0);
            int var6 = getCell(var1);

            for (int var10 = (var5 << 3) + var6; var10 > 0; var10--) {
               byte var11 = var4[var7];
               var7 += (var11 << 1) + 1;
            }

            byte var22 = var4[var7];
            var7++;
            if (var22 > 0 && var22 <= MAX_LAYERS) {
               int var12 = var2 + Config.MIN_LAYER_HEIGHT;
               int var13 = Integer.MIN_VALUE;
               int var14 = Integer.MIN_VALUE;

               while (var22 > 0) {
                  short var9 = (short)((short)(makeShort(var4[var7 + 1], var4[var7]) & 65520) >> 1);
                  if (var9 < var12) {
                     var13 = Math.max(var13, var9);
                  } else if (Math.abs(var2 - var9) < Math.abs(var2 - var14)) {
                     var14 = var9;
                  }

                  var22--;
                  var7 += 2;
               }

               return var13 != Integer.MIN_VALUE ? var13 : var14;
            } else {
               return (short)var2;
            }
         default:
            _log.error("GeoEngine: Unknown blockType");
            return var2;
      }
   }

   public static byte NgetNSWE(int var0, int var1, int var2, int var3) {
      byte[] var4 = b(var0, var1, var3);
      if (var4 == null) {
         return 15;
      }

      int var7 = 0;
      byte var8 = var4[var7];
      var7++;
      switch (var8) {
         case 0:
            return 15;
         case 1:
            int var17 = getCell(var0);
            int var18 = getCell(var1);
            var7 += (var17 << 3) + var18 << 1;
            short var22 = makeShort(var4[var7 + 1], var4[var7]);
            return (byte)(var22 & 15);
         case 2:
            int var5 = getCell(var0);
            int var6 = getCell(var1);

            for (int var10 = (var5 << 3) + var6; var10 > 0; var10--) {
               byte var11 = var4[var7];
               var7 += (var11 << 1) + 1;
            }

            byte var23 = var4[var7];
            var7++;
            if (var23 > 0 && var23 <= MAX_LAYERS) {
               short var12 = -32768;
               short var13 = -32768;
               int var14 = 0;
               int var15 = 0;
               int var16 = var2 + Config.MIN_LAYER_HEIGHT;

               while (var23 > 0) {
                  short var9 = (short)((short)(makeShort(var4[var7 + 1], var4[var7]) & 65520) >> 1);
                  if (var9 < var16) {
                     if (var9 > var12) {
                        var12 = var9;
                        var14 = var7;
                     }
                  } else if (Math.abs(var2 - var9) < Math.abs(var2 - var13)) {
                     var13 = var9;
                     var15 = var7;
                  }

                  var23--;
                  var7 += 2;
               }

               if (var14 > 0) {
                  return (byte)(makeShort(var4[var14 + 1], var4[var14]) & 15);
               } else {
                  return var15 > 0 ? (byte)(makeShort(var4[var15 + 1], var4[var15]) & 15) : 15;
               }
            } else {
               return 15;
            }
         default:
            _log.error("GeoEngine: Unknown block type.");
            return 15;
      }
   }

   public static void NgetHeightAndNSWE(int var0, int var1, short var2, short[] var3, int var4) {
      byte[] var5 = b(var0, var1, var4);
      if (var5 == null) {
         var3[0] = var2;
         var3[1] = 15;
      } else {
         int var8 = 0;
         short var10 = 15;
         byte var11 = var5[var8];
         var8++;
         switch (var11) {
            case 0:
               short var25 = makeShort(var5[var8 + 1], var5[var8]);
               var3[0] = (short)(var25 & 65520);
               var3[1] = 15;
               return;
            case 1:
               int var19 = getCell(var0);
               int var20 = getCell(var1);
               var8 += (var19 << 3) + var20 << 1;
               short var24 = makeShort(var5[var8 + 1], var5[var8]);
               var3[0] = (short)((short)(var24 & 65520) >> 1);
               var3[1] = (short)(var24 & 15);
               return;
            case 2:
               int var6 = getCell(var0);
               int var7 = getCell(var1);

               for (int var12 = (var6 << 3) + var7; var12 > 0; var12--) {
                  byte var13 = var5[var8];
                  var8 += (var13 << 1) + 1;
               }

               byte var28 = var5[var8];
               var8++;
               if (var28 > 0 && var28 <= MAX_LAYERS) {
                  short var14 = -32768;
                  short var15 = -32768;
                  int var16 = 0;
                  int var17 = 0;
                  int var18 = var2 + Config.MIN_LAYER_HEIGHT;

                  while (var28 > 0) {
                     short var9 = (short)((short)(makeShort(var5[var8 + 1], var5[var8]) & 65520) >> 1);
                     if (var9 < var18) {
                        if (var9 > var14) {
                           var14 = var9;
                           var16 = var8;
                        }
                     } else if (Math.abs(var2 - var9) < Math.abs(var2 - var15)) {
                        var15 = var9;
                        var17 = var8;
                     }

                     var28--;
                     var8 += 2;
                  }

                  if (var16 > 0) {
                     var10 = makeShort(var5[var16 + 1], var5[var16]);
                     var10 = (short)(var10 & 15);
                  } else if (var17 > 0) {
                     var10 = makeShort(var5[var17 + 1], var5[var17]);
                     var10 = (short)(var10 & 15);
                  }

                  var3[0] = var14 > -32768 ? var14 : var15;
                  var3[1] = var10;
                  return;
               } else {
                  var3[0] = var2;
                  var3[1] = 15;
                  return;
               }
            default:
               _log.error("GeoEngine: Unknown block type.");
               var3[0] = var2;
               var3[1] = 15;
         }
      }
   }

   protected static short makeShort(byte var0, byte var1) {
      return (short)(var0 << 8 | var1 & 0xFF);
   }

   protected static int getBlock(int var0) {
      return (var0 >> 3) % 256;
   }

   protected static int getCell(int var0) {
      return var0 % 8;
   }

   protected static int getBlockIndex(int var0, int var1) {
      return (var0 << 8) + var1;
   }

   private static byte o(int var0) {
      return (byte)(var0 >= 0 ? 1 : -1);
   }

   private static byte[] b(int var0, int var1, int var2) {
      if (!Config.ALLOW_GEODATA) {
         return null;
      }

      int var3 = var0 >> 11;
      int var4 = var1 >> 11;
      if (var3 >= 0 && var3 < World.WORLD_SIZE_X && var4 >= 0 && var4 < World.WORLD_SIZE_Y) {
         byte[][][] var5 = xL[var3][var4];
         int var6 = getBlock(var0);
         int var7 = getBlock(var1);
         int var8 = 0;
         if ((var2 & 251658240) == 251658240) {
            int var9 = (var2 & 0xFF0000) >> 16;
            int var10 = (var2 & 0xFF00) >> 8;
            if (var3 == var9 && var4 == var10) {
               var8 = var2 & 0xFF;
            }
         }

         return var5[var8][getBlockIndex(var6, var7)];
      } else {
         return null;
      }
   }

   public static void load() {
      if (Config.ALLOW_GEODATA) {
         _log.info("GeoEngine: Loading 3D geodata from data/geodata/ (166 regions)...");
         File var0 = new File(Config.DATAPACK_ROOT, "geodata");
         if (var0.exists() && var0.isDirectory()) {
            int var1 = 0;
            Pattern var2 = Pattern.compile(Config.GEOFILES_PATTERN);
            ArrayList var3 = new ArrayList();

            for (File var7 : var0.listFiles()) {
               if (!var7.isDirectory()) {
                  String var8 = var7.getName();
                  Matcher var9 = var2.matcher(var8);
                  if (var9.matches()) {
                     var3.add(var7);
                  }
               }
            }

            Collections.sort(var3, NaturalOrderComparator.FILE_NAME_COMPARATOR);

            int totalFiles = var3.size();
            int cpuCores = Math.max(2, Runtime.getRuntime().availableProcessors());
            int batchCount = Math.min(cpuCores, 6);
            int batchSize = (totalFiles + batchCount - 1) / batchCount;

            long geoStart = System.currentTimeMillis();
            List<ParallelBoot.Step> steps = new ArrayList<>();
            for (int i = 0; i < totalFiles; i += batchSize) {
                int start = i;
                int end = Math.min(i + batchSize, totalFiles);
                List subList = var3.subList(start, end);
                steps.add(ParallelBoot.stepIO("GeoBatch", () -> {
                    for (Object obj : subList) {
                        File file = (File) obj;
                        String name = file.getName().substring(0, 5);
                        String[] parts = name.split("_");
                        byte rx = Byte.parseByte(parts[0]);
                        byte ry = Byte.parseByte(parts[1]);
                        if (LoadGeodataFile(file, rx, ry)) {
                            LoadGeodata(rx, ry, 0);
                        }
                    }
                }));
            }

            ParallelBoot.runBatch("Geodata", steps.toArray(new ParallelBoot.Step[0]));
            long geoDuration = System.currentTimeMillis() - geoStart;
            _log.info("GeoEngine: Successfully loaded " + totalFiles + " map regions in " + geoDuration + "ms (max layers: " + MAX_LAYERS + ") [NBA* Pathfinder Ready]");
            if (Config.COMPACT_GEO) {
               compact();
            }
         } else {
            _log.info("GeoEngine: Files missing, loading aborted.");
         }
      }
   }

   public static boolean LoadGeodataFile(File var0, byte var1, byte var2) {
      int var3 = var1 - Config.GEO_X_FIRST;
      int var4 = var2 - Config.GEO_Y_FIRST;
      String var5 = var0.getName();
      if (var3 >= 0
         && var4 >= 0
         && var3 <= (World.MAP_MAX_X >> 15) + Math.abs(World.MAP_MIN_X >> 15)
         && var4 <= (World.MAP_MAX_Y >> 15) + Math.abs(World.MAP_MIN_Y >> 15)) {
         // Carregamento silencioso dentro dos lotes paralelos

         try {
            FileChannel var6 = new RandomAccessFile(var0, "r").getChannel();
            ByteBuffer var7 = null;
            int var8 = 0;
            int var9 = 0;
            if (var5.endsWith(".l2j")) {
               var8 = (int)var6.size();
               var7 = ByteBuffer.allocate(var8).order(ByteOrder.LITTLE_ENDIAN);
               var6.read(var7);
            } else if (var5.endsWith(".l2br")) {
               try (java.io.InputStream rawIn = java.nio.channels.Channels.newInputStream(var6);
                    com.github.luben.zstd.ZstdInputStream zstdIn = new com.github.luben.zstd.ZstdInputStream(rawIn);
                    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream(262144)) {
                  byte[] buffer = new byte[8192];
                  int read;
                  while ((read = zstdIn.read(buffer)) != -1) {
                     baos.write(buffer, 0, read);
                  }
                  byte[] decompressed = baos.toByteArray();
                  var8 = decompressed.length;
                  var7 = ByteBuffer.wrap(decompressed).order(ByteOrder.LITTLE_ENDIAN);
               }
            } else if (var5.endsWith(".l2g")) {
               var8 = Math.max(4, (int)var6.size() - 4);
               var7 = ByteBuffer.allocate(var8).order(ByteOrder.LITTLE_ENDIAN);
               ((Buffer)var7).limit(4);
               var6.read(var7);
               ((Buffer)var7).flip();
               var9 = -2126429781 ^ var7.getInt();
               ((Buffer)var7).clear();
               var6.read(var7);
               ((Buffer)var7).rewind();

               for (byte var10 = (byte)(var9 >> 24 & 0xFF ^ var9 >> 16 & 0xFF ^ var9 >> 8 & 0xFF ^ var9 >> 0 & 0xFF);
                  var7.hasRemaining();
                  var9 -= var10 = var7.get(var7.position() - 1)
               ) {
                  var7.put(var7.position(), (byte)(var7.get() ^ var10));
               }

               ((Buffer)var7).rewind();
               if (var9 != 0) {
                  throw new RuntimeException("Test failed!");
               }
            }

            xK[var3][var4] = var7;
            if (var8 >= 196608 && var9 == 0) {
               return true;
            } else {
               throw new RuntimeException("Invalid geodata : " + var5 + "!");
            }
         } catch (IOException var11) {
            _log.error("", var11);
            return false;
         }
      } else {
         _log.info("GeoEngine: File " + var5 + " was not loaded!!! ");
         return false;
      }
   }

   public static void LoadGeodata(int var0, int var1, int var2) {
      int var3 = var0 - Config.GEO_X_FIRST;
      int var4 = var1 - Config.GEO_Y_FIRST;
      ByteBuffer var5 = xK[var3][var4];
      int var6 = 0;
      boolean var8 = false;
      byte var9 = 0;
      byte[][] var10;
      synchronized (xL) {
         if ((var10 = xL[var3][var4][var2]) == null) {
            xL[var3][var4][var2] = var10 = new byte[65536][];
         }
      }

      for (int var16 = 0; var16 < 65536; var16++) {
         byte var17 = var5.get(var6);
         var6++;
         switch (var17) {
            case 0:
               byte[] var19 = new byte[]{var17, var5.get(var6), var5.get(var6 + 1)};
               var6 += 2;
               var10[var16] = var19;
               break;
            case 1:
               byte[] var18 = new byte[129];
               var18[0] = var17;
               ((Buffer)var5).position(var6);
               var5.get(var18, 1, 128);
               var6 += 128;
               var10[var16] = var18;
               break;
            case 2:
               int var7 = var6;

               for (int var13 = 0; var13 < 64; var13++) {
                  byte var14 = var5.get(var6);
                  MAX_LAYERS = Math.max(MAX_LAYERS, var14);
                  var6 += (var14 << 1) + 1;
                  if (var14 > var9) {
                     var9 = var14;
                  }
               }

               int var20 = var6 - var7;
               byte[] var12 = new byte[var20 + 1];
               var12[0] = var17;
               ((Buffer)var5).position(var7);
               var5.get(var12, 1, var20);
               var10[var16] = var12;
               break;
            default:
               throw new RuntimeException("Invalid geodata: " + var0 + "_" + var1 + "!");
         }
      }
   }

   public static int NextGeoIndex(int var0, int var1, int var2) {
      if (!Config.ALLOW_GEODATA) {
         return 0;
      }

      int var3 = var0 - Config.GEO_X_FIRST;
      int var4 = var1 - Config.GEO_Y_FIRST;
      int var5 = -1;
      synchronized (xL) {
         byte[][][] var7 = xL[var3][var4];

         for (int var8 = 0; var8 < var7.length; var8++) {
            if (var7[var8] == null) {
               var5 = var8;
               break;
            }
         }

         if (var5 == -1) {
            byte[][][] var12 = new byte[(var5 = var7.length) + 1][][];

            for (int var9 = 0; var9 < var7.length; var9++) {
               var12[var9] = var7[var9];
            }

            xL[var3][var4] = var12;
         }

         LoadGeodata(var0, var1, var5);
      }

      return 251658240 | var3 << 16 | var4 << 8 | var5;
   }

   public static void FreeGeoIndex(int var0) {
      if (Config.ALLOW_GEODATA) {
         if ((var0 & 251658240) == 251658240) {
            int var1 = (var0 & 0xFF0000) >> 16;
            int var2 = (var0 & 0xFF00) >> 8;
            int var3 = var0 & 0xFF;
            synchronized (xL) {
               xL[var1][var2][var3] = (byte[][])null;
            }
         }
      }
   }

   public static void removeGeoCollision(GeoCollision var0, int var1) {
      Shape var2 = var0.getShape();
      byte[][] var3 = var0.getGeoAround();
      if (var3 == null) {
         throw new RuntimeException("Attempt to remove unitialized collision: " + var0);
      }

      int var4 = var2.getXmin() - World.MAP_MIN_X - 16 >> 4;
      int var5 = var2.getYmin() - World.MAP_MIN_Y - 16 >> 4;
      int var6 = var2.getZmin();
      int var7 = var2.getZmax();

      for (int var10 = 0; var10 < var3.length; var10++) {
         for (int var11 = 0; var11 < var3[var10].length; var11++) {
            int var12 = var4 + var10;
            int var13 = var5 + var11;
            byte[] var14 = b(var12, var13, var1);
            if (var14 != null) {
               int var15 = getCell(var12);
               int var16 = getCell(var13);
               int var17 = 0;
               byte var18 = var14[var17];
               var17++;
               switch (var18) {
                  case 1:
                     var17 += (var15 << 3) + var16 << 1;
                     short var28 = makeShort(var14[var17 + 1], var14[var17]);
                     byte var35 = (byte)(var28 & 15);
                     var28 = (short)(var28 & 65520);
                     var28 = (short)(var28 >> 1);
                     if (var28 >= var6 && var28 <= var7) {
                        var28 = (short)(var28 << 1);
                        var28 = (short)(var28 & 65520);
                        var28 = (short)(var28 | var35);
                        if (var0.isConcrete()) {
                           var28 = (short)(var28 | var3[var10][var11]);
                        } else {
                           var28 = (short)(var28 & ~var3[var10][var11]);
                        }

                        var14[var17 + 1] = (byte)(var28 >> 8);
                        var14[var17] = (byte)(var28 & 0xFF);
                     }
                     break;
                  case 2:
                     int var19 = -1;

                     for (int var20 = (var15 << 3) + var16; var20 > 0; var20--) {
                        byte var21 = var14[var17];
                        var17 += (var21 << 1) + 1;
                     }

                     byte var39 = var14[var17];
                     var17++;
                     if (var39 > 0 && var39 <= MAX_LAYERS) {
                        short var22 = -32768;
                        byte var9 = 15;

                        while (var39 > 0) {
                           short var8 = makeShort(var14[var17 + 1], var14[var17]);
                           byte var23 = (byte)(var8 & 15);
                           var8 = (short)(var8 & 65520);
                           var8 = (short)(var8 >> 1);
                           int var24 = Math.abs(var6 - var22);
                           int var25 = Math.abs(var7 - var8);
                           if (var24 > var25) {
                              var9 = var23;
                              var22 = var8;
                              var19 = var17;
                           }

                           var39--;
                           var17 += 2;
                        }

                        if (var22 != -32768 && var22 >= var6 && var22 <= var7) {
                           var22 = (short)(var22 << 1);
                           var22 = (short)(var22 & 65520);
                           var22 = (short)(var22 | var9);
                           if (var0.isConcrete()) {
                              var22 = (short)(var22 | var3[var10][var11]);
                           } else {
                              var22 = (short)(var22 & ~var3[var10][var11]);
                           }

                           var14[var19 + 1] = (byte)(var22 >> 8);
                           var14[var19] = (byte)(var22 & 0xFF);
                        }
                     }
               }
            }
         }
      }
   }

   public static void applyGeoCollision(GeoCollision var0, int var1) {
      Shape var2 = var0.getShape();
      if (var2.getXmax() == var2.getYmax() && var2.getXmax() == 0) {
         throw new RuntimeException("Attempt to add incorrect collision: " + var0);
      }

      boolean var3 = false;
      int var4 = var2.getXmin() - World.MAP_MIN_X - 16 >> 4;
      int var5 = var2.getXmax() - World.MAP_MIN_X + 16 >> 4;
      int var6 = var2.getYmin() - World.MAP_MIN_Y - 16 >> 4;
      int var7 = var2.getYmax() - World.MAP_MIN_Y + 16 >> 4;
      int var8 = var2.getZmin();
      int var9 = var2.getZmax();
      byte[][] var10 = var0.getGeoAround();
      if (var10 == null) {
         var3 = true;
         byte[][] var11 = new byte[var5 - var4 + 1][var7 - var6 + 1];

         for (int var12 = var4; var12 <= var5; var12++) {
            label186:
            for (int var13 = var6; var13 <= var7; var13++) {
               int var14 = (var12 << 4) + World.MAP_MIN_X;
               int var15 = (var13 << 4) + World.MAP_MIN_Y;

               for (int var16 = var14; var16 < var14 + 16; var16++) {
                  for (int var17 = var15; var17 < var15 + 16; var17++) {
                     if (var2.isInside(var16, var17)) {
                        var11[var12 - var4][var13 - var6] = 1;
                        continue label186;
                     }
                  }
               }
            }
         }

         var10 = new byte[var5 - var4 + 1][var7 - var6 + 1];

         for (int var40 = 0; var40 < var11.length; var40++) {
            for (int var43 = 0; var43 < var11[var40].length; var43++) {
               if (var11[var40][var43] == 1) {
                  var10[var40][var43] = 15;
                  if (var43 > 0 && var11[var40][var43 - 1] == 0) {
                     byte var46 = var10[var40][var43 - 1];
                     var46 = (byte)(var46 | 4);
                     var10[var40][var43 - 1] = var46;
                  }

                  if (var43 + 1 < var11[var40].length && var11[var40][var43 + 1] == 0) {
                     byte var48 = var10[var40][var43 + 1];
                     var48 = (byte)(var48 | 8);
                     var10[var40][var43 + 1] = var48;
                  }

                  if (var40 > 0 && var11[var40 - 1][var43] == 0) {
                     byte var50 = var10[var40 - 1][var43];
                     var50 = (byte)(var50 | 1);
                     var10[var40 - 1][var43] = var50;
                  }

                  if (var40 + 1 < var11.length && var11[var40 + 1][var43] == 0) {
                     byte var52 = var10[var40 + 1][var43];
                     var52 = (byte)(var52 | 2);
                     var10[var40 + 1][var43] = var52;
                  }
               }
            }
         }

         var0.setGeoAround(var10);
      }

      for (int var54 = 0; var54 < var10.length; var54++) {
         for (int var55 = 0; var55 < var10[var54].length; var55++) {
            int var56 = var4 + var54;
            int var57 = var6 + var55;
            byte[] var18 = b(var56, var57, var1);
            if (var18 != null) {
               int var19 = getCell(var56);
               int var20 = getCell(var57);
               int var21 = 0;
               byte var22 = var18[var21];
               var21++;
               switch (var22) {
                  case 1:
                     var21 += (var19 << 3) + var20 << 1;
                     short var33 = makeShort(var18[var21 + 1], var18[var21]);
                     byte var42 = (byte)(var33 & 15);
                     var33 = (short)(var33 & 65520);
                     var33 = (short)(var33 >> 1);
                     if (var33 >= var8 && var33 <= var9) {
                        byte var45 = var10[var54][var55];
                        if (var3) {
                           if (var0.isConcrete()) {
                              var45 &= var42;
                           } else {
                              var45 = (byte)(var45 & ~var42);
                           }

                           var10[var54][var55] = var45;
                        }

                        var33 = (short)(var33 << 1);
                        var33 = (short)(var33 & 65520);
                        var33 = (short)(var33 | var42);
                        if (var0.isConcrete()) {
                           var33 = (short)(var33 & ~var45);
                        } else {
                           var33 = (short)(var33 | var45);
                        }

                        var18[var21 + 1] = (byte)(var33 >> 8);
                        var18[var21] = (byte)(var33 & 0xFF);
                     }
                     break;
                  case 2:
                     int var23 = -1;

                     for (int var24 = (var19 << 3) + var20; var24 > 0; var24--) {
                        byte var25 = var18[var21];
                        var21 += (var25 << 1) + 1;
                     }

                     byte var61 = var18[var21];
                     var21++;
                     if (var61 > 0 && var61 <= MAX_LAYERS) {
                        short var26 = -32768;
                        byte var41 = 15;

                        while (var61 > 0) {
                           short var30 = makeShort(var18[var21 + 1], var18[var21]);
                           byte var27 = (byte)(var30 & 15);
                           var30 = (short)(var30 & 65520);
                           var30 = (short)(var30 >> 1);
                           int var28 = Math.abs(var8 - var26);
                           int var29 = Math.abs(var9 - var30);
                           if (var28 > var29) {
                              var41 = var27;
                              var26 = var30;
                              var23 = var21;
                           }

                           var61--;
                           var21 += 2;
                        }

                        if (var26 != -32768 && var26 >= var8 && var26 <= var9) {
                           byte var44 = var10[var54][var55];
                           if (var3) {
                              if (var0.isConcrete()) {
                                 var44 &= var41;
                              } else {
                                 var44 = (byte)(var44 & ~var41);
                              }

                              var10[var54][var55] = var44;
                           }

                           var26 = (short)(var26 << 1);
                           var26 = (short)(var26 & 65520);
                           var26 = (short)(var26 | var41);
                           if (var0.isConcrete()) {
                              var26 = (short)(var26 & ~var44);
                           } else {
                              var26 = (short)(var26 | var44);
                           }

                           var18[var23 + 1] = (byte)(var26 >> 8);
                           var18[var23] = (byte)(var26 & 0xFF);
                        }
                     }
               }
            }
         }
      }
   }

   public static void compact() {
      long var0 = 0L;
      long var2 = 0L;

      for (int var6 = 0; var6 < World.WORLD_SIZE_X; var6++) {
         for (int var7 = 0; var7 < World.WORLD_SIZE_Y; var7++) {
            if (xL[var6][var7] != null) {
               var0 += 65536L;
               GeoOptimizer.BlockLink[] var4 = GeoOptimizer.loadBlockMatches(
                  "geodata/matches/" + (var6 + Config.GEO_X_FIRST) + "_" + (var7 + Config.GEO_Y_FIRST) + ".matches"
               );
               if (var4 != null) {
                  for (int var8 = 0; var8 < var4.length; var8++) {
                     byte[][][] var5 = xL[var4[var8].linkMapX][var4[var8].linkMapY];
                     if (var5 != null) {
                        var5[var4[var8].linkBlockIndex][0] = xL[var6][var7][var4[var8].blockIndex][0];
                        var2++;
                     }
                  }
               }
            }
         }
      }

      _log.info(String.format("GeoEngine: - Compacted %d of %d blocks...", var2, var0));
   }

   public static boolean equalsData(byte[] var0, byte[] var1) {
      if (var0.length != var1.length) {
         return false;
      }

      for (int var2 = 0; var2 < var0.length; var2++) {
         if (var0[var2] != var1[var2]) {
            return false;
         }
      }

      return true;
   }

   public static boolean compareGeoBlocks(int var0, int var1, int var2, int var3, int var4, int var5) {
      return equalsData(xL[var0][var1][var2][0], xL[var3][var4][var5][0]);
   }

   private static void bq() {
      _log.info("GeoEngine: - Generating Checksums...");
      new File(Config.DATAPACK_ROOT, "geodata/checksum").mkdirs();
      ExecutorService var0 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      GeoOptimizer.checkSums = new int[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][];

      for (int var1 = 0; var1 < World.WORLD_SIZE_X; var1++) {
         for (int var2 = 0; var2 < World.WORLD_SIZE_Y; var2++) {
            if (xL[var1][var2] != null) {
               var0.execute(new GeoOptimizer.CheckSumLoader(var1, var2, xL[var1][var2]));
            }
         }
      }

      try {
         var0.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
      } catch (InterruptedException var3) {
         _log.error("", var3);
      }
   }

   private static void p(int var0) {
      _log.info("GeoEngine: Generating Block Matches...");
      new File(Config.DATAPACK_ROOT, "geodata/matches").mkdirs();
      ExecutorService var1 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

      for (int var2 = 0; var2 < World.WORLD_SIZE_X; var2++) {
         for (int var3 = 0; var3 < World.WORLD_SIZE_Y; var3++) {
            if (xL[var2][var3] != null && GeoOptimizer.checkSums != null && GeoOptimizer.checkSums[var2][var3] != null) {
               var1.execute(new GeoOptimizer.GeoBlocksMatchFinder(var2, var3, var0));
            }
         }
      }

      try {
         var1.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
      } catch (InterruptedException var4) {
         _log.error("", var4);
      }
   }

   public static void deleteChecksumFiles() {
      for (int var0 = 0; var0 < World.WORLD_SIZE_X; var0++) {
         for (int var1 = 0; var1 < World.WORLD_SIZE_Y; var1++) {
            if (xL[var0][var1] != null) {
               new File(Config.DATAPACK_ROOT, "geodata/checksum/" + (var0 + Config.GEO_X_FIRST) + "_" + (var1 + Config.GEO_Y_FIRST) + ".crc").delete();
            }
         }
      }
   }

   public static void genBlockMatches(int var0) {
      bq();
      p(var0);
   }

   public static void unload() {
      for (int var0 = 0; var0 < World.WORLD_SIZE_X; var0++) {
         for (int var1 = 0; var1 < World.WORLD_SIZE_Y; var1++) {
            xL[var0][var1] = (byte[][][])null;
         }
      }
   }
}
