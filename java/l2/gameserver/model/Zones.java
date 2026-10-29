package l2.gameserver.model;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import l2.commons.collections.LazyArrayList;
import org.apache.commons.lang3.ArrayUtils;

public class Zones implements Iterable<Zone> {
   private final AtomicReference<Zone[]> Ml = new AtomicReference<>(Zone.EMPTY_L2ZONE_ARRAY);

   @Override
   public Iterator<Zone> iterator() {
      return new Iterator<Zone>() {
         private final Zone[] Mm = Zones.this.Ml.get();
         private int cursor = 0;

         @Override
         public boolean hasNext() {
            return this.cursor < this.Mm.length;
         }

         public Zone next() {
            try {
               return this.Mm[this.cursor++];
            } catch (IndexOutOfBoundsException var2) {
               throw new NoSuchElementException();
            }
         }
      };
   }

   public Stream<Zone> stream() {
      return Stream.of(this.Ml.get());
   }

   public boolean update(Creature var1) {
      Zone[] var2 = var1.isVisible() ? var1.getCurrentRegion().getZones() : Zone.EMPTY_L2ZONE_ARRAY;
      Zone[] var3 = this.Ml.get();
      Zone[] var4 = var3;
      if (var3.length > 0) {
         for (int var6 = 0; var6 < var3.length; var6++) {
            Zone var5 = var3[var6];
            if (!ArrayUtils.contains(var2, var5) || !var5.checkIfInZone(var1.getLoc(), var1.getReflection())) {
               var4 = (Zone[])ArrayUtils.removeElement(var4, var5);
            }
         }
      }

      if (var2.length > 0) {
         for (int var15 = 0; var15 < var2.length; var15++) {
            Zone var12 = var2[var15];
            if (!ArrayUtils.contains(var3, var12) && var12.checkIfInZone(var1.getLoc(), var1.getReflection())) {
               var4 = (Zone[])ArrayUtils.add(var4, var12);
            }
         }
      }

      if (var3 != var4 && this.Ml.compareAndSet(var3, var4)) {
         LazyArrayList var16 = LazyArrayList.newInstance();
         LazyArrayList var7 = LazyArrayList.newInstance();

         try {
            for (int var8 = 0; var8 < var3.length; var8++) {
               Zone var13 = var3[var8];
               if (!ArrayUtils.contains(var4, var13)) {
                  var16.add(var13);
               }
            }

            for (int var17 = 0; var17 < var4.length; var17++) {
               Zone var14 = var4[var17];
               if (!ArrayUtils.contains(var3, var14)) {
                  var7.add(var14);
               }
            }

            var1.onUpdateZones(var16, var7);
         } finally {
            LazyArrayList.recycle(var16);
            LazyArrayList.recycle(var7);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isInZone(Zone.ZoneType var1) {
      for (Zone var3 : this) {
         if (var3.isType(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInZone(String var1) {
      for (Zone var3 : this) {
         if (var3.getName().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInZone(Zone var1) {
      return ArrayUtils.contains(this.Ml.get(), var1);
   }

   public Zone getZone(Zone.ZoneType var1) {
      for (Zone var3 : this) {
         if (var3.isType(var1)) {
            return var3;
         }
      }

      return null;
   }

   public boolean isInAnyZone(Zone.ZoneType... var1) {
      for (Zone var3 : this) {
         if (var3.isAnyType(var1)) {
            return true;
         }
      }

      return false;
   }

   public int getWaterZ() {
      int var1 = Integer.MIN_VALUE;

      for (Zone var3 : this) {
         if (var3.isType(Zone.ZoneType.water) && (var1 == Integer.MIN_VALUE || var1 < var3.getTerritory().getZmax())) {
            var1 = var3.getTerritory().getZmax();
         }
      }

      return var1;
   }
}
