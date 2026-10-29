package l2.gameserver.geodata;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntIntIterator;
import gnu.trove.TIntObjectHashMap;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.lang.ArrayUtils;
import l2.commons.text.StrTable;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;

public class PathFindBuffers {
   public static final int MIN_MAP_SIZE = 64;
   public static final int STEP_MAP_SIZE = 32;
   public static final int MAX_MAP_SIZE = 512;
   private static TIntObjectHashMap<PathFindBuffers.PathFindBuffer[]> yc = new TIntObjectHashMap();
   private static int[] sizes = new int[0];
   private static Lock lock = new ReentrantLock();

   private static PathFindBuffers.PathFindBuffer q(int var0) {
      lock.lock();

      try {
         PathFindBuffers.PathFindBuffer[] var2 = (PathFindBuffers.PathFindBuffer[])yc.get(var0);
         PathFindBuffers.PathFindBuffer var1;
         if (var2 != null) {
            var2 = ArrayUtils.add(var2, var1 = new PathFindBuffers.PathFindBuffer(var0));
         } else {
            var2 = new PathFindBuffers.PathFindBuffer[]{var1 = new PathFindBuffers.PathFindBuffer(var0)};
            sizes = org.apache.commons.lang3.ArrayUtils.add(sizes, var0);
            Arrays.sort(sizes);
         }

         yc.put(var0, var2);
         var1.inUse = true;
         return var1;
      } finally {
         lock.unlock();
      }
   }

   private static PathFindBuffers.PathFindBuffer r(int var0) {
      lock.lock();

      try {
         PathFindBuffers.PathFindBuffer[] var1 = (PathFindBuffers.PathFindBuffer[])yc.get(var0);

         for (PathFindBuffers.PathFindBuffer var5 : var1) {
            if (!var5.inUse) {
               var5.inUse = true;
               return var5;
            }
         }

         return null;
      } finally {
         lock.unlock();
      }
   }

   public static PathFindBuffers.PathFindBuffer alloc(int var0) {
      if (var0 > 512) {
         return null;
      }

      var0 += 32;
      if (var0 < 64) {
         var0 = 64;
      }

      PathFindBuffers.PathFindBuffer var1 = null;

      for (int var2 = 0; var2 < sizes.length; var2++) {
         if (sizes[var2] >= var0) {
            var0 = sizes[var2];
            var1 = r(var0);
            break;
         }
      }

      if (var1 == null) {
         for (byte var5 = 64; var5 < 512; var5 += 32) {
            if (var5 >= var0) {
               byte var4 = var5;
               var1 = q(var4);
               break;
            }
         }
      }

      return var1;
   }

   public static void recycle(PathFindBuffers.PathFindBuffer var0) {
      lock.lock();

      try {
         var0.inUse = false;
      } finally {
         lock.unlock();
      }
   }

   public static StrTable getStats() {
      StrTable var0 = new StrTable("PathFind Buffers Stats");
      lock.lock();

      try {
         long var1 = 0L;
         long var3 = 0L;
         long var5 = 0L;
         int var7 = 0;

         for (int var24 : sizes) {
            var7++;
            int var8 = 0;
            long var9 = 0L;
            long var11 = 0L;
            long var13 = 0L;
            long var15 = 0L;
            long var17 = 0L;
            long var19 = 0L;

            for (PathFindBuffers.PathFindBuffer var28 : (PathFindBuffers.PathFindBuffer[])yc.get(var24)) {
               var8++;
               var9 += var28.totalUses;
               var11 += var28.playableUses;
               var15 += var28.successUses;
               var17 += var28.overtimeUses;
               var19 += var28.totalTime / 1000000L;
               var13 += var28.totalItr;
            }

            var1 += var9;
            var3 += var11;
            var5 += var19;
            var0.set(var7, "Size", var24);
            var0.set(var7, "Count", var8);
            var0.set(var7, "Uses (success%)", var9 + "(" + String.format("%2.2f", var9 > 0L ? var15 * 100.0 / var9 : 0.0) + "%)");
            var0.set(var7, "Uses, playble", var11 + "(" + String.format("%2.2f", var9 > 0L ? var11 * 100.0 / var9 : 0.0) + "%)");
            var0.set(var7, "Uses, overtime", var17 + "(" + String.format("%2.2f", var9 > 0L ? var17 * 100.0 / var9 : 0.0) + "%)");
            var0.set(var7, "Iter., avg", var9 > 0L ? var13 / var9 : 0L);
            var0.set(var7, "Time, avg (ms)", String.format("%1.3f", var9 > 0L ? (double)var19 / var9 : 0.0));
         }

         var0.addTitle("Uses, total / playable  : " + var1 + " / " + var3);
         var0.addTitle("Uses, total time / avg (ms) : " + var5 + " / " + String.format("%1.3f", var1 > 0L ? (double)var5 / var1 : 0.0));
      } finally {
         lock.unlock();
      }

      return var0;
   }

   static {
      TIntIntHashMap var0 = new TIntIntHashMap();

      for (String var5 : Config.PATHFIND_BUFFERS.split(";")) {
         String[] var1;
         if (!var5.isEmpty() && (var1 = var5.split("x")).length == 2) {
            var0.put(Integer.valueOf(var1[1]), Integer.valueOf(var1[0]));
         }
      }

      TIntIntIterator var7 = var0.iterator();

      while (var7.hasNext()) {
         var7.advance();
         int var8 = var7.key();
         int var9 = var7.value();
         PathFindBuffers.PathFindBuffer[] var10 = new PathFindBuffers.PathFindBuffer[var9];

         for (int var6 = 0; var6 < var9; var6++) {
            var10[var6] = new PathFindBuffers.PathFindBuffer(var8);
         }

         yc.put(var8, var10);
      }

      sizes = var0.keys();
      Arrays.sort(sizes);
   }

   public static class GeoNode implements Comparable<PathFindBuffers.GeoNode> {
      public static final int NONE = 0;
      public static final int OPENED = 1;
      public static final int CLOSED = -1;
      public int x;
      public int y;
      public short z;
      public short nswe = -1;
      public float totalCost;
      public float costFromStart;
      public float costToEnd;
      public int state;
      public PathFindBuffers.GeoNode parent;

      public PathFindBuffers.GeoNode set(int var1, int var2, short var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         return this;
      }

      public boolean isSet() {
         return this.nswe != -1;
      }

      public void free() {
         this.nswe = -1;
         this.costFromStart = 0.0F;
         this.totalCost = 0.0F;
         this.costToEnd = 0.0F;
         this.parent = null;
         this.state = 0;
      }

      public Location getLoc() {
         return new Location(this.x, this.y, this.z);
      }

      @Override
      public String toString() {
         return "[" + this.x + "," + this.y + "," + this.z + "] f: " + this.totalCost;
      }

      public int compareTo(PathFindBuffers.GeoNode var1) {
         if (this.totalCost > var1.totalCost) {
            return 1;
         } else {
            return this.totalCost < var1.totalCost ? -1 : 0;
         }
      }
   }

   public static class PathFindBuffer {
      final int mapSize;
      final PathFindBuffers.GeoNode[][] nodes;
      final Queue<PathFindBuffers.GeoNode> open;
      int offsetX;
      int offsetY;
      boolean inUse;
      long totalUses;
      long successUses;
      long overtimeUses;
      long playableUses;
      long totalTime;
      long totalItr;

      public PathFindBuffer(int var1) {
         this.open = new PriorityQueue<>(var1);
         this.mapSize = var1;
         this.nodes = new PathFindBuffers.GeoNode[var1][var1];

         for (int var2 = 0; var2 < this.nodes.length; var2++) {
            for (int var3 = 0; var3 < this.nodes[var2].length; var3++) {
               this.nodes[var2][var3] = new PathFindBuffers.GeoNode();
            }
         }
      }

      public void free() {
         this.open.clear();

         for (int var1 = 0; var1 < this.nodes.length; var1++) {
            for (int var2 = 0; var2 < this.nodes[var1].length; var2++) {
               this.nodes[var1][var2].free();
            }
         }
      }
   }
}
