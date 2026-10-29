package l2.commons.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StrTable {
   private final Map<Integer, Map<String, String>> rV = new HashMap<>();
   private final Map<String, Integer> rW = new LinkedHashMap<>();
   private final List<String> rX = new ArrayList<>();

   public StrTable(String var1) {
      if (var1 != null) {
         this.rX.add(var1);
      }
   }

   public StrTable() {
      this(null);
   }

   public StrTable set(int var1, String var2, boolean var3) {
      return this.set(var1, var2, Boolean.toString(var3));
   }

   public StrTable set(int var1, String var2, byte var3) {
      return this.set(var1, var2, Byte.toString(var3));
   }

   public StrTable set(int var1, String var2, char var3) {
      return this.set(var1, var2, String.valueOf(var3));
   }

   public StrTable set(int var1, String var2, short var3) {
      return this.set(var1, var2, Short.toString(var3));
   }

   public StrTable set(int var1, String var2, int var3) {
      return this.set(var1, var2, Integer.toString(var3));
   }

   public StrTable set(int var1, String var2, long var3) {
      return this.set(var1, var2, Long.toString(var3));
   }

   public StrTable set(int var1, String var2, float var3) {
      return this.set(var1, var2, Float.toString(var3));
   }

   public StrTable set(int var1, String var2, double var3) {
      return this.set(var1, var2, Double.toString(var3));
   }

   public StrTable set(int var1, String var2, Object var3) {
      return this.set(var1, var2, String.valueOf(var3));
   }

   public StrTable set(int var1, String var2, String var3) {
      Map var4;
      if (this.rV.containsKey(var1)) {
         var4 = this.rV.get(var1);
      } else {
         var4 = new HashMap();
         this.rV.put(var1, var4);
      }

      var4.put(var2, var3);
      int var5;
      if (!this.rW.containsKey(var2)) {
         var5 = Math.max(var2.length(), var3.length());
      } else if (this.rW.get(var2) >= (var5 = var3.length())) {
         return this;
      }

      this.rW.put(var2, var5);
      return this;
   }

   public StrTable addTitle(String var1) {
      this.rX.add(var1);
      return this;
   }

   private static StringBuilder a(StringBuilder var0, String var1, int var2) {
      var0.append(var1);
      if ((var2 = var2 - var1.length()) > 0) {
         for (int var3 = 0; var3 < var2; var3++) {
            var0.append(" ");
         }
      }

      return var0;
   }

   private static StringBuilder b(StringBuilder var0, String var1, int var2) {
      int var3 = var0.length();
      var0.append(var1);

      int var4;
      while ((var4 = var2 - (var0.length() - var3)) > 0) {
         var0.append(" ");
         if (var4 > 1) {
            var0.insert(var3, " ");
         }
      }

      return var0;
   }

   private static StringBuilder c(StringBuilder var0, String var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         var0.append(var1);
      }

      return var0;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.rW.isEmpty()) {
         return var1.toString();
      }

      StringBuilder var2 = new StringBuilder("|");
      StringBuilder var3 = new StringBuilder("|");

      for (String var5 : this.rW.keySet()) {
         b(var2, var5, this.rW.get(var5) + 2).append("|");
         c(var3, "-", this.rW.get(var5) + 2).append("|");
      }

      if (!this.rX.isEmpty()) {
         var1.append(" ");
         c(var1, "-", var2.length() - 2).append(" ").append("\n");

         for (String var10 : this.rX) {
            var1.append("| ");
            a(var1, var10, var2.length() - 3).append("|").append("\n");
         }
      }

      var1.append(" ");
      c(var1, "-", var2.length() - 2).append(" ").append("\n");
      var1.append(var2).append("\n");
      var1.append(var3).append("\n");

      for (Map var11 : this.rV.values()) {
         var1.append("|");

         for (String var7 : this.rW.keySet()) {
            b(var1, var11.containsKey(var7) ? (String)var11.get(var7) : "-", this.rW.get(var7) + 2).append("|");
         }

         var1.append("\n");
      }

      var1.append(" ");
      c(var1, "-", var2.length() - 2).append(" ").append("\n");
      return var1.toString();
   }
}
