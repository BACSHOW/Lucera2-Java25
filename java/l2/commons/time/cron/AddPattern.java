package l2.commons.time.cron;

import java.util.GregorianCalendar;
import java.util.TimeZone;

public class AddPattern implements NextTime {
   private int sp = -1;
   private int sq = -1;
   private int sr = -1;
   private int ss = -1;
   private int su = -1;
   private int sv = -1;
   private int sw = -1;
   private int sx = -1;

   public AddPattern(String var1) {
      String[] var2 = var1.split("\\s+");
      if (var2.length == 2) {
         String var3 = var2[0];
         String[] var4 = var3.split(":");
         if (var4.length == 2) {
            if (var4[0].startsWith("+")) {
               this.sp = Integer.parseInt(var4[0].substring(1));
            } else {
               this.sq = Integer.parseInt(var4[0]) - 1;
            }
         }

         String var5 = var4[var4.length - 1];
         if (var5.startsWith("+")) {
            this.sr = Integer.parseInt(var5.substring(1));
         } else {
            this.ss = Integer.parseInt(var5);
         }
      }

      String[] var6 = var2[var2.length - 1].split(":");
      if (var6[0].startsWith("+")) {
         this.su = Integer.parseInt(var6[0].substring(1));
      } else {
         this.sv = Integer.parseInt(var6[0]);
      }

      if (var6[1].startsWith("+")) {
         this.sw = Integer.parseInt(var6[1].substring(1));
      } else {
         this.sx = Integer.parseInt(var6[1]);
      }
   }

   @Override
   public long next(long var1) {
      GregorianCalendar var3 = new GregorianCalendar(TimeZone.getDefault());
      var3.setTimeInMillis(var1);
      if (this.sp >= 0) {
         var3.add(2, this.sp);
      }

      if (this.sq >= 0) {
         var3.set(2, this.sq);
      }

      if (this.sr >= 0) {
         var3.add(5, this.sr);
      }

      if (this.ss >= 0) {
         var3.set(5, this.ss);
      }

      if (this.su >= 0) {
         var3.add(11, this.su);
      }

      if (this.sv >= 0) {
         var3.set(11, this.sv);
      }

      if (this.sw >= 0) {
         var3.add(12, this.sw);
      }

      if (this.sx >= 0) {
         var3.set(12, this.sx);
      }

      return var3.getTimeInMillis();
   }
}
