package l2.commons.geometry;

import java.util.Arrays;
import java.util.List;
import l2.commons.lang.ArrayUtils;

public class Polygon extends AbstractShape {
   protected Point2D[] points = Point2D.EMPTY_ARRAY;

   public Polygon add(int var1, int var2) {
      this.add(new Point2D(var1, var2));
      return this;
   }

   public Polygon add(Point2D var1) {
      if (this.points.length == 0) {
         this.min.y = var1.y;
         this.min.x = var1.x;
         this.max.x = var1.x;
         this.max.y = var1.y;
      } else {
         this.min.y = Math.min(this.min.y, var1.y);
         this.min.x = Math.min(this.min.x, var1.x);
         this.max.x = Math.max(this.max.x, var1.x);
         this.max.y = Math.max(this.max.y, var1.y);
      }

      this.points = ArrayUtils.add(this.points, var1);
      return this;
   }

   public List<Point2D> getPoints() {
      return Arrays.asList(this.points);
   }

   public Polygon setZmax(int var1) {
      this.max.z = var1;
      return this;
   }

   public Polygon setZmin(int var1) {
      this.min.z = var1;
      return this;
   }

   @Override
   public boolean isInside(int var1, int var2) {
      if (var1 >= this.min.x && var1 <= this.max.x && var2 >= this.min.y && var2 <= this.max.y) {
         int var3 = 0;
         int var4 = this.points.length;
         Point2D var5 = this.points[var4 - 1];

         for (int var7 = 0; var7 < var4; var7++) {
            Point2D var6;
            var6 = this.points[var7];
            label67:
            if (var6.y != var5.y) {
               int var8;
               if (var6.x < var5.x) {
                  if (var1 >= var5.x) {
                     break label67;
                  }

                  var8 = var6.x;
               } else {
                  if (var1 >= var6.x) {
                     break label67;
                  }

                  var8 = var5.x;
               }

               double var9;
               double var11;
               if (var6.y < var5.y) {
                  if (var2 < var6.y || var2 >= var5.y) {
                     break label67;
                  }

                  if (var1 < var8) {
                     var3++;
                     break label67;
                  }

                  var9 = var1 - var6.x;
                  var11 = var2 - var6.y;
               } else {
                  if (var2 < var5.y || var2 >= var6.y) {
                     break label67;
                  }

                  if (var1 < var8) {
                     var3++;
                     break label67;
                  }

                  var9 = var1 - var5.x;
                  var11 = var2 - var5.y;
               }

               if (var9 < var11 / (var5.y - var6.y) * (var5.x - var6.x)) {
                  var3++;
               }
            }

            var5 = var6;
         }

         return (var3 & 1) != 0;
      } else {
         return false;
      }
   }

   public boolean validate() {
      if (this.points.length < 3) {
         return false;
      }

      if (this.points.length > 3) {
         for (int var1 = 1; var1 < this.points.length; var1++) {
            int var2 = var1 + 1 < this.points.length ? var1 + 1 : 0;

            for (int var3 = var1; var3 < this.points.length; var3++) {
               if (Math.abs(var3 - var1) > 1) {
                  int var4 = var3 + 1 < this.points.length ? var3 + 1 : 0;
                  if (GeometryUtils.checkIfLineSegementsIntersects(this.points[var1], this.points[var2], this.points[var3], this.points[var4])) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("[");

      for (int var2 = 0; var2 < this.points.length; var2++) {
         var1.append(this.points[var2]);
         if (var2 < this.points.length - 1) {
            var1.append(",");
         }
      }

      var1.append(";[").append(this.getZmin()).append("-").append(this.getZmax()).append("]");
      var1.append("]");
      return var1.toString();
   }
}
