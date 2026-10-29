package l2.commons.text;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrintfFormat {
   private List<PrintfFormat.ConversionSpecification> ru = new ArrayList<>();
   private int rv = 0;
   private DecimalFormatSymbols rw = null;

   public PrintfFormat(String var1) throws IllegalArgumentException {
      this(Locale.getDefault(), var1);
   }

   public PrintfFormat(Locale var1, String var2) throws IllegalArgumentException {
      this.rw = new DecimalFormatSymbols(var1);
      int var3 = 0;
      PrintfFormat.ConversionSpecification var4 = null;
      String var5 = this.c(var2, 0);
      if (var5 != null) {
         var4 = new PrintfFormat.ConversionSpecification();
         var4.setLiteral(var5);
         this.ru.add(var4);
      }

      while (this.rv != -1 && this.rv < var2.length()) {
         var3 = this.rv + 1;

         while (true) {
            if (var3 < var2.length()) {
               char var6 = '\u0000';
               var6 = var2.charAt(var3);
               if (var6 != 'i'
                  && var6 != 'd'
                  && var6 != 'f'
                  && var6 != 'g'
                  && var6 != 'G'
                  && var6 != 'o'
                  && var6 != 'x'
                  && var6 != 'X'
                  && var6 != 'e'
                  && var6 != 'E'
                  && var6 != 'c'
                  && var6 != 's'
                  && var6 != '%') {
                  var3++;
                  continue;
               }
            }

            var3 = Math.min(var3 + 1, var2.length());
            var4 = new PrintfFormat.ConversionSpecification(var2.substring(this.rv, var3));
            this.ru.add(var4);
            var5 = this.c(var2, var3);
            if (var5 != null) {
               var4 = new PrintfFormat.ConversionSpecification();
               var4.setLiteral(var5);
               this.ru.add(var4);
            }
            break;
         }
      }
   }

   private String c(String var1, int var2) {
      this.rv = var1.indexOf("%", var2);
      if (this.rv == -1) {
         this.rv = var1.length();
      }

      return var1.substring(var2, this.rv);
   }

   public String sprintf(Object... var1) {
      char var2 = '\u0000';
      int var3 = 0;
      StringBuilder var4 = new StringBuilder();

      for (PrintfFormat.ConversionSpecification var6 : this.ru) {
         var2 = var6.getConversionCharacter();
         if (var2 == 0) {
            var4.append(var6.getLiteral());
         } else if (var2 == '%') {
            var4.append("%");
         } else {
            if (var6.isPositionalSpecification()) {
               var3 = var6.getArgumentPosition() - 1;
               if (var6.isPositionalFieldWidth()) {
                  int var7 = var6.getArgumentPositionForFieldWidth() - 1;
                  var6.setFieldWidthWithArg((Integer)var1[var7]);
               }

               if (var6.isPositionalPrecision()) {
                  int var9 = var6.getArgumentPositionForPrecision() - 1;
                  var6.setPrecisionWithArg((Integer)var1[var9]);
               }
            } else {
               if (var6.isVariableFieldWidth()) {
                  var6.setFieldWidthWithArg((Integer)var1[var3]);
                  var3++;
               }

               if (var6.isVariablePrecision()) {
                  var6.setPrecisionWithArg((Integer)var1[var3]);
                  var3++;
               }
            }

            if (var1[var3] instanceof Byte) {
               var4.append(var6.internalsprintf(((Byte)var1[var3]).byteValue()));
            } else if (var1[var3] instanceof Short) {
               var4.append(var6.internalsprintf(((Short)var1[var3]).shortValue()));
            } else if (var1[var3] instanceof Integer) {
               var4.append(var6.internalsprintf(((Integer)var1[var3]).intValue()));
            } else if (var1[var3] instanceof Long) {
               var4.append(var6.internalsprintf(((Long)var1[var3]).longValue()));
            } else if (var1[var3] instanceof Float) {
               var4.append(var6.internalsprintf(((Float)var1[var3]).floatValue()));
            } else if (var1[var3] instanceof Double) {
               var4.append(var6.internalsprintf(((Double)var1[var3]).doubleValue()));
            } else if (var1[var3] instanceof Character) {
               var4.append(var6.internalsprintf(((Character)var1[var3]).charValue()));
            } else if (var1[var3] instanceof String) {
               var4.append(var6.internalsprintf((String)var1[var3]));
            } else {
               var4.append(var6.internalsprintf(var1[var3]));
            }

            if (!var6.isPositionalSpecification()) {
               var3++;
            }
         }
      }

      return var4.toString();
   }

   public String sprintf() {
      char var1 = '\u0000';
      StringBuilder var2 = new StringBuilder();

      for (PrintfFormat.ConversionSpecification var4 : this.ru) {
         var1 = var4.getConversionCharacter();
         if (var1 == 0) {
            var2.append(var4.getLiteral());
         } else if (var1 == '%') {
            var2.append("%");
         }
      }

      return var2.toString();
   }

   public String sprintf(int var1) throws IllegalArgumentException {
      char var2 = '\u0000';
      StringBuilder var3 = new StringBuilder();

      for (PrintfFormat.ConversionSpecification var5 : this.ru) {
         var2 = var5.getConversionCharacter();
         if (var2 == 0) {
            var3.append(var5.getLiteral());
         } else if (var2 == '%') {
            var3.append("%");
         } else {
            var3.append(var5.internalsprintf(var1));
         }
      }

      return var3.toString();
   }

   public String sprintf(long var1) throws IllegalArgumentException {
      char var3 = '\u0000';
      StringBuilder var4 = new StringBuilder();

      for (PrintfFormat.ConversionSpecification var6 : this.ru) {
         var3 = var6.getConversionCharacter();
         if (var3 == 0) {
            var4.append(var6.getLiteral());
         } else if (var3 == '%') {
            var4.append("%");
         } else {
            var4.append(var6.internalsprintf(var1));
         }
      }

      return var4.toString();
   }

   public String sprintf(double var1) throws IllegalArgumentException {
      char var3 = '\u0000';
      StringBuilder var4 = new StringBuilder();

      for (PrintfFormat.ConversionSpecification var6 : this.ru) {
         var3 = var6.getConversionCharacter();
         if (var3 == 0) {
            var4.append(var6.getLiteral());
         } else if (var3 == '%') {
            var4.append("%");
         } else {
            var4.append(var6.internalsprintf(var1));
         }
      }

      return var4.toString();
   }

   public String sprintf(String var1) throws IllegalArgumentException {
      char var2 = '\u0000';
      StringBuilder var3 = new StringBuilder();

      for (PrintfFormat.ConversionSpecification var5 : this.ru) {
         var2 = var5.getConversionCharacter();
         if (var2 == 0) {
            var3.append(var5.getLiteral());
         } else if (var2 == '%') {
            var3.append("%");
         } else {
            var3.append(var5.internalsprintf(var1));
         }
      }

      return var3.toString();
   }

   public String sprintf(Object var1) throws IllegalArgumentException {
      char var2 = '\u0000';
      StringBuilder var3 = new StringBuilder();

      for (PrintfFormat.ConversionSpecification var5 : this.ru) {
         var2 = var5.getConversionCharacter();
         if (var2 == 0) {
            var3.append(var5.getLiteral());
         } else if (var2 == '%') {
            var3.append("%");
         } else if (var1 instanceof Byte) {
            var3.append(var5.internalsprintf(((Byte)var1).byteValue()));
         } else if (var1 instanceof Short) {
            var3.append(var5.internalsprintf(((Short)var1).shortValue()));
         } else if (var1 instanceof Integer) {
            var3.append(var5.internalsprintf(((Integer)var1).intValue()));
         } else if (var1 instanceof Long) {
            var3.append(var5.internalsprintf(((Long)var1).longValue()));
         } else if (var1 instanceof Float) {
            var3.append(var5.internalsprintf(((Float)var1).floatValue()));
         } else if (var1 instanceof Double) {
            var3.append(var5.internalsprintf(((Double)var1).doubleValue()));
         } else if (var1 instanceof Character) {
            var3.append(var5.internalsprintf(((Character)var1).charValue()));
         } else if (var1 instanceof String) {
            var3.append(var5.internalsprintf((String)var1));
         } else {
            var3.append(var5.internalsprintf(var1));
         }
      }

      return var3.toString();
   }

   private class ConversionSpecification {
      private boolean rx = false;
      private boolean ry = false;
      private boolean rz = false;
      private boolean rA = false;
      private boolean rB = false;
      private boolean rC = false;
      private boolean rD = false;
      private int rE = 0;
      private boolean rF = false;
      private int rG = 0;
      private static final int rH = 6;
      private boolean rI = false;
      private boolean rJ = false;
      private boolean rK = false;
      private int rL = 0;
      private boolean rM = false;
      private int rN = 0;
      private boolean rO = false;
      private int rP = 0;
      private boolean rQ = false;
      private boolean rR = false;
      private boolean rS = false;
      private char rT = 0;
      private int pos = 0;
      private String rU;

      ConversionSpecification() {
      }

      ConversionSpecification(String var2) throws IllegalArgumentException {
         if (var2 == null) {
            throw new NullPointerException();
         }

         if (var2.length() == 0) {
            throw new IllegalArgumentException("Control strings must have positive lengths.");
         }

         if (var2.charAt(0) == '%') {
            this.rU = var2;
            this.pos = 1;
            this.aV();
            this.aY();
            this.aU();
            this.aT();
            this.aS();
            if (!this.aR()) {
               throw new IllegalArgumentException("Malformed conversion specification=" + var2);
            }

            if (this.pos == var2.length()) {
               if (this.rC && this.ry) {
                  this.rC = false;
               }

               if (this.rJ && this.rC && (this.rT == 'd' || this.rT == 'i' || this.rT == 'o' || this.rT == 'x')) {
                  this.rC = false;
               }
            } else {
               throw new IllegalArgumentException("Malformed conversion specification=" + var2);
            }
         } else {
            throw new IllegalArgumentException("Control strings must begin with %.");
         }
      }

      void setLiteral(String var1) {
         this.rU = var1;
      }

      String getLiteral() {
         StringBuilder var1 = new StringBuilder();
         int var2 = 0;

         while (var2 < this.rU.length()) {
            if (this.rU.charAt(var2) == '\\') {
               if (++var2 < this.rU.length()) {
                  char var3 = this.rU.charAt(var2);
                  switch (var3) {
                     case '\\':
                        var1.append('\\');
                     case ']':
                     case '^':
                     case '_':
                     case '`':
                     case 'c':
                     case 'd':
                     case 'e':
                     case 'g':
                     case 'h':
                     case 'i':
                     case 'j':
                     case 'k':
                     case 'l':
                     case 'm':
                     case 'o':
                     case 'p':
                     case 'q':
                     case 's':
                     case 'u':
                     default:
                        break;
                     case 'a':
                        var1.append('\u0007');
                        break;
                     case 'b':
                        var1.append('\b');
                        break;
                     case 'f':
                        var1.append('\f');
                        break;
                     case 'n':
                        var1.append(System.getProperty("line.separator"));
                        break;
                     case 'r':
                        var1.append('\r');
                        break;
                     case 't':
                        var1.append('\t');
                        break;
                     case 'v':
                        var1.append('\u000b');
                  }

                  var2++;
               } else {
                  var1.append('\\');
               }
            } else {
               var2++;
            }
         }

         return this.rU;
      }

      char getConversionCharacter() {
         return this.rT;
      }

      boolean isVariableFieldWidth() {
         return this.rD;
      }

      void setFieldWidthWithArg(int var1) {
         if (var1 < 0) {
            this.ry = true;
         }

         this.rF = true;
         this.rE = Math.abs(var1);
      }

      boolean isVariablePrecision() {
         return this.rI;
      }

      void setPrecisionWithArg(int var1) {
         this.rJ = true;
         this.rG = Math.max(var1, 0);
      }

      String internalsprintf(int var1) throws IllegalArgumentException {
         String var2 = "";
         switch (this.rT) {
            case 'C':
            case 'c':
               var2 = this.a((char)var1);
               break;
            case 'X':
            case 'x':
               if (this.rQ) {
                  var2 = this.b((short)var1);
               } else if (this.rR) {
                  var2 = this.c(var1);
               } else {
                  var2 = this.k(var1);
               }
               break;
            case 'd':
            case 'i':
               if (this.rQ) {
                  var2 = this.a((short)var1);
               } else if (this.rR) {
                  var2 = this.b(var1);
               } else {
                  var2 = this.j(var1);
               }
               break;
            case 'o':
               if (this.rQ) {
                  var2 = this.c((short)var1);
               } else if (this.rR) {
                  var2 = this.d(var1);
               } else {
                  var2 = this.l(var1);
               }
               break;
            default:
               throw new IllegalArgumentException("Cannot format a int with a format using a " + this.rT + " conversion character.");
         }

         return var2;
      }

      String internalsprintf(long var1) throws IllegalArgumentException {
         String var3 = "";
         switch (this.rT) {
            case 'C':
            case 'c':
               var3 = this.a((char)var1);
               break;
            case 'X':
            case 'x':
               if (this.rQ) {
                  var3 = this.b((short)var1);
               } else if (this.rR) {
                  var3 = this.c(var1);
               } else {
                  var3 = this.k((int)var1);
               }
               break;
            case 'd':
            case 'i':
               if (this.rQ) {
                  var3 = this.a((short)var1);
               } else if (this.rR) {
                  var3 = this.b(var1);
               } else {
                  var3 = this.j((int)var1);
               }
               break;
            case 'o':
               if (this.rQ) {
                  var3 = this.c((short)var1);
               } else if (this.rR) {
                  var3 = this.d(var1);
               } else {
                  var3 = this.l((int)var1);
               }
               break;
            default:
               throw new IllegalArgumentException("Cannot format a long with a format using a " + this.rT + " conversion character.");
         }

         return var3;
      }

      String internalsprintf(double var1) throws IllegalArgumentException {
         String var3 = "";
         switch (this.rT) {
            case 'E':
            case 'e':
               var3 = this.d(var1);
               break;
            case 'G':
            case 'g':
               var3 = this.e(var1);
               break;
            case 'f':
               var3 = this.c(var1);
               break;
            default:
               throw new IllegalArgumentException("Cannot format a double with a format using a " + this.rT + " conversion character.");
         }

         return var3;
      }

      String internalsprintf(String var1) throws IllegalArgumentException {
         String var2 = "";
         if (this.rT != 's' && this.rT != 'S') {
            throw new IllegalArgumentException("Cannot format a String with a format using a " + this.rT + " conversion character.");
         } else {
            return this.g(var1);
         }
      }

      String internalsprintf(Object var1) {
         String var2 = "";
         if (this.rT != 's' && this.rT != 'S') {
            throw new IllegalArgumentException("Cannot format a String with a format using a " + this.rT + " conversion character.");
         } else {
            return this.g(var1.toString());
         }
      }

      private char[] a(double var1) {
         int var9 = 0;
         boolean var10 = false;
         String var3;
         if (var1 > 0.0) {
            var3 = Double.toString(var1);
         } else if (var1 < 0.0) {
            var3 = Double.toString(-var1);
            var10 = true;
         } else {
            var3 = Double.toString(var1);
            if (var3.charAt(0) == '-') {
               var10 = true;
               var3 = var3.substring(1);
            }
         }

         int var11 = var3.indexOf(69);
         int var12 = var3.indexOf(46);
         int var7;
         if (var12 != -1) {
            var7 = var12;
         } else if (var11 != -1) {
            var7 = var11;
         } else {
            var7 = var3.length();
         }

         int var8;
         if (var12 != -1) {
            if (var11 != -1) {
               var8 = var11 - var12 - 1;
            } else {
               var8 = var3.length() - var12 - 1;
            }
         } else {
            var8 = 0;
         }

         if (var11 != -1) {
            int var13 = var11 + 1;
            var9 = 0;
            if (var3.charAt(var13) == '-') {
               var13++;

               while (var13 < var3.length() && var3.charAt(var13) == '0') {
                  var13++;
               }

               if (var13 < var3.length()) {
                  var9 = -Integer.parseInt(var3.substring(var13));
               }
            } else {
               if (var3.charAt(var13) == '+') {
                  var13++;
               }

               while (var13 < var3.length() && var3.charAt(var13) == '0') {
                  var13++;
               }

               if (var13 < var3.length()) {
                  var9 = Integer.parseInt(var3.substring(var13));
               }
            }
         }

         int var41;
         if (this.rJ) {
            var41 = this.rG;
         } else {
            var41 = 5;
         }

         char[] var14 = var3.toCharArray();
         char[] var15 = new char[var7 + var8];

         int var5;
         for (var5 = 0; var5 < var7; var5++) {
            var15[var5] = var14[var5];
         }

         int var4 = var5 + 1;

         for (int var6 = 0; var6 < var8; var6++) {
            var15[var5] = var14[var4];
            var5++;
            var4++;
         }

         char[] var16;
         if (var7 + var9 <= 0) {
            var16 = new char[-var9 + var8];
            var5 = 0;

            for (int var37 = 0; var37 < -var7 - var9; var5++) {
               var16[var5] = '0';
               var37++;
            }

            for (int var25 = 0; var25 < var7 + var8; var5++) {
               var16[var5] = var15[var25];
               var25++;
            }
         } else {
            var16 = var15;
         }

         boolean var19 = false;
         if (var41 < -var9 + var8) {
            if (var9 < 0) {
               var4 = var41;
            } else {
               var4 = var41 + var7;
            }

            var19 = this.a(var16, var4);
            if (var19) {
               var19 = this.a(var16, var4 - 1, 0);
            }
         }

         char[] var17;
         if (var7 + var9 <= 0) {
            var17 = new char[2 + var41];
            if (!var19) {
               var17[0] = '0';
            } else {
               var17[0] = '1';
            }

            if (this.rB || !this.rJ || this.rG != 0) {
               var17[1] = '.';
               var4 = 0;

               for (var5 = 2; var4 < Math.min(var41, var16.length); var5++) {
                  var17[var5] = var16[var4];
                  var4++;
               }

               while (var5 < var17.length) {
                  var17[var5] = '0';
                  var5++;
               }
            }
         } else {
            if (!var19) {
               if (!this.rB && this.rJ && this.rG == 0) {
                  var17 = new char[var7 + var9];
               } else {
                  var17 = new char[var7 + var9 + var41 + 1];
               }

               var5 = 0;
            } else {
               if (!this.rB && this.rJ && this.rG == 0) {
                  var17 = new char[var7 + var9 + 1];
               } else {
                  var17 = new char[var7 + var9 + var41 + 2];
               }

               var17[0] = '1';
               var5 = 1;
            }

            for (var4 = 0; var4 < Math.min(var7 + var9, var16.length); var5++) {
               var17[var5] = var16[var4];
               var4++;
            }

            while (var4 < var7 + var9) {
               var17[var5] = '0';
               var4++;
               var5++;
            }

            if (this.rB || !this.rJ || this.rG != 0) {
               var17[var5] = '.';
               var5++;

               for (int var38 = 0; var4 < var16.length && var38 < var41; var38++) {
                  var17[var5] = var16[var4];
                  var4++;
                  var5++;
               }

               while (var5 < var17.length) {
                  var17[var5] = '0';
                  var5++;
               }
            }
         }

         int var20 = 0;
         if (!this.ry && this.rC) {
            int var21 = 0;
            if (this.rx) {
               byte var22 = 0;
               if (var17[0] == '+' || var17[0] == '-' || var17[0] == ' ') {
                  var22 = 1;
               }

               int var23 = var22;

               while (var23 < var17.length && var17[var23] != '.') {
                  var23++;
               }

               var21 = (var23 - var22) / 3;
            }

            if (this.rF) {
               var20 = this.rE - var17.length;
            }

            if (!var10 && (this.rz || this.rA) || var10) {
               var20--;
            }

            var20 -= var21;
            if (var20 < 0) {
               var20 = 0;
            }
         }

         var5 = 0;
         char[] var18;
         if ((var10 || !this.rz && !this.rA) && !var10) {
            var18 = new char[var17.length + var20];
         } else {
            var18 = new char[var17.length + var20 + 1];
            var5++;
         }

         if (!var10) {
            if (this.rz) {
               var18[0] = '+';
            }

            if (this.rA) {
               var18[0] = ' ';
            }
         } else {
            var18[0] = '-';
         }

         for (int var29 = 0; var29 < var20; var5++) {
            var18[var5] = '0';
            var29++;
         }

         for (int var30 = 0; var30 < var17.length; var5++) {
            var18[var5] = var17[var30];
            var30++;
         }

         byte var42 = 0;
         if (var18[0] == '+' || var18[0] == '-' || var18[0] == ' ') {
            var42 = 1;
         }

         int var43 = var42;

         while (var43 < var18.length && var18[var43] != '.') {
            var43++;
         }

         int var44 = (var43 - var42) / 3;
         if (var43 < var18.length) {
            var18[var43] = PrintfFormat.this.rw.getDecimalSeparator();
         }

         char[] var24 = var18;
         if (this.rx && var44 > 0) {
            var24 = new char[var18.length + var44 + var42];
            var24[0] = var18[0];
            var4 = var42;
            int var39 = var42;

            while (var4 < var43) {
               if (var4 > 0 && (var43 - var4) % 3 == 0) {
                  var24[var39] = PrintfFormat.this.rw.getGroupingSeparator();
                  var24[var39 + 1] = var18[var4];
                  var39 += 2;
               } else {
                  var24[var39] = var18[var4];
                  var39++;
               }

               var4++;
            }

            while (var4 < var18.length) {
               var24[var39] = var18[var4];
               var4++;
               var39++;
            }
         }

         return var24;
      }

      private String b(double var1) {
         char[] var3;
         if (Double.isInfinite(var1)) {
            if (var1 == Double.POSITIVE_INFINITY) {
               if (this.rz) {
                  var3 = "+Inf".toCharArray();
               } else if (this.rA) {
                  var3 = " Inf".toCharArray();
               } else {
                  var3 = "Inf".toCharArray();
               }
            } else {
               var3 = "-Inf".toCharArray();
            }
         } else if (Double.isNaN(var1)) {
            if (this.rz) {
               var3 = "+NaN".toCharArray();
            } else if (this.rA) {
               var3 = " NaN".toCharArray();
            } else {
               var3 = "NaN".toCharArray();
            }
         } else {
            var3 = this.a(var1);
         }

         char[] var4 = this.a(var3, false);
         return new String(var4);
      }

      private char[] a(double var1, char var3) {
         int var12 = 0;
         boolean var16 = false;
         String var7;
         if (var1 > 0.0) {
            var7 = Double.toString(var1);
         } else if (var1 < 0.0) {
            var7 = Double.toString(-var1);
            var16 = true;
         } else {
            var7 = Double.toString(var1);
            if (var7.charAt(0) == '-') {
               var16 = true;
               var7 = var7.substring(1);
            }
         }

         int var13 = var7.indexOf(69);
         if (var13 == -1) {
            var13 = var7.indexOf(101);
         }

         int var14 = var7.indexOf(46);
         if (var13 != -1) {
            int var17 = var13 + 1;
            var12 = 0;
            if (var7.charAt(var17) == '-') {
               var17++;

               while (var17 < var7.length() && var7.charAt(var17) == '0') {
                  var17++;
               }

               if (var17 < var7.length()) {
                  var12 = -Integer.parseInt(var7.substring(var17));
               }
            } else {
               if (var7.charAt(var17) == '+') {
                  var17++;
               }

               while (var17 < var7.length() && var7.charAt(var17) == '0') {
                  var17++;
               }

               if (var17 < var7.length()) {
                  var12 = Integer.parseInt(var7.substring(var17));
               }
            }
         }

         if (var14 != -1) {
            var12 += var14 - 1;
         }

         int var11;
         if (this.rJ) {
            var11 = this.rG;
         } else {
            var11 = 5;
         }

         char[] var4;
         if (var14 != -1 && var13 != -1) {
            var4 = (var7.substring(0, var14) + var7.substring(var14 + 1, var13)).toCharArray();
         } else if (var14 != -1) {
            var4 = (var7.substring(0, var14) + var7.substring(var14 + 1)).toCharArray();
         } else if (var13 != -1) {
            var4 = var7.substring(0, var13).toCharArray();
         } else {
            var4 = var7.toCharArray();
         }

         boolean var39 = false;
         int var18 = 0;
         if (var4[0] != '0') {
            var18 = 0;
         } else {
            var18 = 0;

            while (var18 < var4.length && var4[var18] == '0') {
               var18++;
            }
         }

         if (var18 + var11 < var4.length - 1) {
            var39 = this.a(var4, var18 + var11 + 1);
            if (var39) {
               var39 = this.a(var4, var18 + var11, var18);
            }

            if (var39) {
               char[] var5 = new char[var18 + var11 + 1];
               var5[var18] = '1';

               for (int var9 = 0; var9 < var18; var9++) {
                  var5[var9] = '0';
               }

               int var8 = var18;

               for (int var31 = var18 + 1; var31 < var11 + 1; var31++) {
                  var5[var31] = var4[var8];
                  var8++;
               }

               var12++;
               var4 = var5;
            }
         }

         byte var15;
         if (Math.abs(var12) < 100 && !this.rS) {
            var15 = 4;
         } else {
            var15 = 5;
         }

         char[] var24;
         if (!this.rB && this.rJ && this.rG == 0) {
            var24 = new char[1 + var15];
         } else {
            var24 = new char[2 + var11 + var15];
         }

         int var33;
         if (var4[0] != '0') {
            var24[0] = var4[0];
            var33 = 1;
         } else {
            var33 = 1;

            while (var33 < (var13 == -1 ? var4.length : var13) && var4[var33] == '0') {
               var33++;
            }

            if ((var13 == -1 || var33 >= var13) && (var13 != -1 || var33 >= var4.length)) {
               var24[0] = '0';
               var33 = 2;
            } else {
               var24[0] = var4[var33];
               var12 -= var33;
               var33++;
            }
         }

         int var25;
         if (!this.rB && this.rJ && this.rG == 0) {
            var25 = 1;
         } else {
            var24[1] = '.';
            var25 = 2;
         }

         for (int var10 = 0; var10 < var11 && var33 < var4.length; var10++) {
            var24[var25] = var4[var33];
            var33++;
            var25++;
         }

         while (var25 < var24.length - var15) {
            var24[var25] = '0';
            var25++;
         }

         var24[var25++] = var3;
         if (var12 < 0) {
            var24[var25++] = '-';
         } else {
            var24[var25++] = '+';
         }

         var12 = Math.abs(var12);
         if (var12 >= 100) {
            switch (var12 / 100) {
               case 1:
                  var24[var25] = '1';
                  break;
               case 2:
                  var24[var25] = '2';
                  break;
               case 3:
                  var24[var25] = '3';
                  break;
               case 4:
                  var24[var25] = '4';
                  break;
               case 5:
                  var24[var25] = '5';
                  break;
               case 6:
                  var24[var25] = '6';
                  break;
               case 7:
                  var24[var25] = '7';
                  break;
               case 8:
                  var24[var25] = '8';
                  break;
               case 9:
                  var24[var25] = '9';
            }

            var25++;
         }

         switch (var12 % 100 / 10) {
            case 0:
               var24[var25] = '0';
               break;
            case 1:
               var24[var25] = '1';
               break;
            case 2:
               var24[var25] = '2';
               break;
            case 3:
               var24[var25] = '3';
               break;
            case 4:
               var24[var25] = '4';
               break;
            case 5:
               var24[var25] = '5';
               break;
            case 6:
               var24[var25] = '6';
               break;
            case 7:
               var24[var25] = '7';
               break;
            case 8:
               var24[var25] = '8';
               break;
            case 9:
               var24[var25] = '9';
         }

         var25++;
         switch (var12 % 10) {
            case 0:
               var24[var25] = '0';
               break;
            case 1:
               var24[var25] = '1';
               break;
            case 2:
               var24[var25] = '2';
               break;
            case 3:
               var24[var25] = '3';
               break;
            case 4:
               var24[var25] = '4';
               break;
            case 5:
               var24[var25] = '5';
               break;
            case 6:
               var24[var25] = '6';
               break;
            case 7:
               var24[var25] = '7';
               break;
            case 8:
               var24[var25] = '8';
               break;
            case 9:
               var24[var25] = '9';
         }

         int var19 = 0;
         if (!this.ry && this.rC) {
            int var20 = 0;
            if (this.rx) {
               byte var21 = 0;
               if (var24[0] == '+' || var24[0] == '-' || var24[0] == ' ') {
                  var21 = 1;
               }

               int var22 = var21;

               while (var22 < var24.length && var24[var22] != '.') {
                  var22++;
               }

               var20 = (var22 - var21) / 3;
            }

            if (this.rF) {
               var19 = this.rE - var24.length;
            }

            if (!var16 && (this.rz || this.rA) || var16) {
               var19--;
            }

            var19 -= var20;
            if (var19 < 0) {
               var19 = 0;
            }
         }

         var33 = 0;
         char[] var6;
         if ((var16 || !this.rz && !this.rA) && !var16) {
            var6 = new char[var24.length + var19];
         } else {
            var6 = new char[var24.length + var19 + 1];
            var33++;
         }

         if (!var16) {
            if (this.rz) {
               var6[0] = '+';
            }

            if (this.rA) {
               var6[0] = ' ';
            }
         } else {
            var6[0] = '-';
         }

         for (int var35 = 0; var35 < var19; var35++) {
            var6[var33] = '0';
            var33++;
         }

         for (int var29 = 0; var29 < var24.length && var33 < var6.length; var33++) {
            var6[var33] = var24[var29];
            var29++;
         }

         byte var42 = 0;
         if (var6[0] == '+' || var6[0] == '-' || var6[0] == ' ') {
            var42 = 1;
         }

         int var43 = var42;

         while (var43 < var6.length && var6[var43] != '.') {
            var43++;
         }

         int var44 = var43 / 3;
         if (var43 < var6.length) {
            var6[var43] = PrintfFormat.this.rw.getDecimalSeparator();
         }

         char[] var23 = var6;
         if (this.rx && var44 > 0) {
            var23 = new char[var6.length + var44 + var42];
            var23[0] = var6[0];
            var25 = var42;
            int var36 = var42;

            while (var25 < var43) {
               if (var25 > 0 && (var43 - var25) % 3 == 0) {
                  var23[var36] = PrintfFormat.this.rw.getGroupingSeparator();
                  var23[var36 + 1] = var6[var25];
                  var36 += 2;
               } else {
                  var23[var36] = var6[var25];
                  var36++;
               }

               var25++;
            }

            while (var25 < var6.length) {
               var23[var36] = var6[var25];
               var25++;
               var36++;
            }
         }

         return var23;
      }

      private boolean a(char[] var1, int var2) {
         boolean var3 = false;
         if (var2 < var1.length) {
            if (var1[var2] == '6' || var1[var2] == '7' || var1[var2] == '8' || var1[var2] == '9') {
               var3 = true;
            } else if (var1[var2] == '5') {
               int var4 = var2 + 1;

               while (var4 < var1.length && var1[var4] == '0') {
                  var4++;
               }

               var3 = var4 < var1.length;
               if (!var3 && var2 > 0) {
                  var3 = var1[var2 - 1] == '1' || var1[var2 - 1] == '3' || var1[var2 - 1] == '5' || var1[var2 - 1] == '7' || var1[var2 - 1] == '9';
               }
            }
         }

         return var3;
      }

      private boolean a(char[] var1, int var2, int var3) {
         boolean var4 = true;

         for (int var5 = var2; var4 && var5 >= var3; var5--) {
            var4 = false;
            switch (var1[var5]) {
               case '0':
                  var1[var5] = '1';
                  break;
               case '1':
                  var1[var5] = '2';
                  break;
               case '2':
                  var1[var5] = '3';
                  break;
               case '3':
                  var1[var5] = '4';
                  break;
               case '4':
                  var1[var5] = '5';
                  break;
               case '5':
                  var1[var5] = '6';
                  break;
               case '6':
                  var1[var5] = '7';
                  break;
               case '7':
                  var1[var5] = '8';
                  break;
               case '8':
                  var1[var5] = '9';
                  break;
               case '9':
                  var1[var5] = '0';
                  var4 = true;
            }
         }

         return var4;
      }

      private String b(double var1, char var3) {
         char[] var4;
         if (Double.isInfinite(var1)) {
            if (var1 == Double.POSITIVE_INFINITY) {
               if (this.rz) {
                  var4 = "+Inf".toCharArray();
               } else if (this.rA) {
                  var4 = " Inf".toCharArray();
               } else {
                  var4 = "Inf".toCharArray();
               }
            } else {
               var4 = "-Inf".toCharArray();
            }
         } else if (Double.isNaN(var1)) {
            if (this.rz) {
               var4 = "+NaN".toCharArray();
            } else if (this.rA) {
               var4 = " NaN".toCharArray();
            } else {
               var4 = "NaN".toCharArray();
            }
         } else {
            var4 = this.a(var1, var3);
         }

         char[] var5 = this.a(var4, false);
         return new String(var5);
      }

      private char[] a(char[] var1, boolean var2) {
         char[] var3 = var1;
         if (this.rF) {
            if (this.ry) {
               int var6 = this.rE - var1.length;
               if (var6 > 0) {
                  var3 = new char[var1.length + var6];

                  int var4;
                  for (var4 = 0; var4 < var1.length; var4++) {
                     var3[var4] = var1[var4];
                  }

                  for (int var5 = 0; var5 < var6; var4++) {
                     var3[var4] = ' ';
                     var5++;
                  }
               }
            } else if (!this.rC || var2) {
               int var13 = this.rE - var1.length;
               if (var13 > 0) {
                  var3 = new char[var1.length + var13];

                  int var9;
                  for (var9 = 0; var9 < var13; var9++) {
                     var3[var9] = ' ';
                  }

                  for (int var11 = 0; var11 < var1.length; var11++) {
                     var3[var9] = var1[var11];
                     var9++;
                  }
               }
            } else if (this.rC) {
               int var12 = this.rE - var1.length;
               if (var12 > 0) {
                  var3 = new char[var1.length + var12];
                  int var8 = 0;
                  int var10 = 0;
                  if (var1[0] == '-') {
                     var3[0] = '-';
                     var8++;
                     var10++;
                  }

                  for (int var7 = 0; var7 < var12; var7++) {
                     var3[var8] = '0';
                     var8++;
                  }

                  while (var10 < var1.length) {
                     var3[var8] = var1[var10];
                     var8++;
                     var10++;
                  }
               }
            }
         }

         return var3;
      }

      private String c(double var1) {
         return this.b(var1);
      }

      private String d(double var1) {
         return this.rT == 'e' ? this.b(var1, 'e') : this.b(var1, 'E');
      }

      private String e(double var1) {
         int var7 = this.rG;
         char[] var9;
         if (Double.isInfinite(var1)) {
            if (var1 == Double.POSITIVE_INFINITY) {
               if (this.rz) {
                  var9 = "+Inf".toCharArray();
               } else if (this.rA) {
                  var9 = " Inf".toCharArray();
               } else {
                  var9 = "Inf".toCharArray();
               }
            } else {
               var9 = "-Inf".toCharArray();
            }
         } else if (Double.isNaN(var1)) {
            if (this.rz) {
               var9 = "+NaN".toCharArray();
            } else if (this.rA) {
               var9 = " NaN".toCharArray();
            } else {
               var9 = "NaN".toCharArray();
            }
         } else {
            if (!this.rJ) {
               this.rG = 6;
            }

            if (this.rG == 0) {
               this.rG = 1;
            }

            int var11 = -1;
            String var3;
            if (this.rT == 'g') {
               var3 = this.b(var1, 'e').trim();
               var11 = var3.indexOf(101);
            } else {
               var3 = this.b(var1, 'E').trim();
               var11 = var3.indexOf(69);
            }

            int var8 = var11 + 1;
            int var12 = 0;
            if (var3.charAt(var8) == '-') {
               var8++;

               while (var8 < var3.length() && var3.charAt(var8) == '0') {
                  var8++;
               }

               if (var8 < var3.length()) {
                  var12 = -Integer.parseInt(var3.substring(var8));
               }
            } else {
               if (var3.charAt(var8) == '+') {
                  var8++;
               }

               while (var8 < var3.length() && var3.charAt(var8) == '0') {
                  var8++;
               }

               if (var8 < var3.length()) {
                  var12 = Integer.parseInt(var3.substring(var8));
               }
            }

            String var6;
            if (this.rB) {
               if (var12 >= -4 && var12 < this.rG) {
                  var6 = this.b(var1).trim();
               } else {
                  var6 = var3;
               }
            } else {
               String var4;
               if (var12 >= -4 && var12 < this.rG) {
                  var4 = this.b(var1).trim();
               } else {
                  var4 = var3.substring(0, var11);
               }

               var8 = var4.length() - 1;

               while (var8 >= 0 && var4.charAt(var8) == '0') {
                  var8--;
               }

               if (var8 >= 0 && var4.charAt(var8) == '.') {
                  var8--;
               }

               String var5;
               if (var8 == -1) {
                  var5 = "0";
               } else if (!Character.isDigit(var4.charAt(var8))) {
                  var5 = var4.substring(0, var8 + 1) + "0";
               } else {
                  var5 = var4.substring(0, var8 + 1);
               }

               if (var12 >= -4 && var12 < this.rG) {
                  var6 = var5;
               } else {
                  var6 = var5 + var3.substring(var11);
               }
            }

            if (this.rA && var1 >= 0.0) {
               var6 = " " + var6;
            }

            var9 = var6.toCharArray();
         }

         char[] var10 = this.a(var9, false);
         this.rG = var7;
         return new String(var10);
      }

      private String a(short var1) {
         return this.d(Short.toString(var1));
      }

      private String b(long var1) {
         return this.d(Long.toString(var1));
      }

      private String j(int var1) {
         return this.d(Integer.toString(var1));
      }

      private String d(String var1) {
         int var2 = 0;
         int var3 = 0;
         int var4 = 0;
         int var5 = 0;
         int var6 = 0;
         boolean var7 = var1.charAt(0) == '-';
         if (var1.equals("0") && this.rJ && this.rG == 0) {
            var1 = "";
         }

         if (!var7) {
            if (this.rJ && var1.length() < this.rG) {
               var2 = this.rG - var1.length();
            }
         } else if (this.rJ && var1.length() - 1 < this.rG) {
            var2 = this.rG - var1.length() + 1;
         }

         if (var2 < 0) {
            var2 = 0;
         }

         if (this.rF) {
            var3 = this.rE - var2 - var1.length();
            if (!var7 && (this.rz || this.rA)) {
               var3--;
            }
         }

         if (var3 < 0) {
            var3 = 0;
         }

         if (this.rz) {
            var4++;
         } else if (this.rA) {
            var4++;
         }

         var4 += var3;
         var4 += var2;
         var4 += var1.length();
         char[] var8 = new char[var4];
         if (this.ry) {
            if (var7) {
               var8[var5++] = '-';
            } else if (this.rz) {
               var8[var5++] = '+';
            } else if (this.rA) {
               var8[var5++] = ' ';
            }

            char[] var9 = var1.toCharArray();
            var6 = var7 ? 1 : 0;

            for (int var10 = 0; var10 < var2; var10++) {
               var8[var5] = '0';
               var5++;
            }

            for (int var19 = var6; var19 < var9.length; var5++) {
               var8[var5] = var9[var19];
               var19++;
            }

            for (int var20 = 0; var20 < var3; var20++) {
               var8[var5] = ' ';
               var5++;
            }
         } else {
            if (this.rC) {
               if (var7) {
                  var8[var5++] = '-';
               } else if (this.rz) {
                  var8[var5++] = '+';
               } else if (this.rA) {
                  var8[var5++] = ' ';
               }

               for (int var16 = 0; var16 < var3; var5++) {
                  var8[var5] = '0';
                  var16++;
               }
            } else {
               for (var5 = 0; var5 < var3; var5++) {
                  var8[var5] = ' ';
               }

               if (var7) {
                  var8[var5++] = '-';
               } else if (this.rz) {
                  var8[var5++] = '+';
               } else if (this.rA) {
                  var8[var5++] = ' ';
               }
            }

            for (int var17 = 0; var17 < var2; var5++) {
               var8[var5] = '0';
               var17++;
            }

            char[] var18 = var1.toCharArray();
            var6 = var7 ? 1 : 0;

            for (int var21 = var6; var21 < var18.length; var5++) {
               var8[var5] = var18[var21];
               var21++;
            }
         }

         return new String(var8);
      }

      private String b(short var1) {
         String var2 = null;
         if (var1 == -32768) {
            var2 = "8000";
         } else if (var1 < 0) {
            String var3;
            if (var1 == -32768) {
               var3 = "0";
            } else {
               var3 = Integer.toString(~(-var1 - 1) ^ -32768, 16);
               if (var3.charAt(0) == 'F' || var3.charAt(0) == 'f') {
                  var3 = var3.substring(16, 32);
               }
            }

            switch (var3.length()) {
               case 1:
                  var2 = "800" + var3;
                  break;
               case 2:
                  var2 = "80" + var3;
                  break;
               case 3:
                  var2 = "8" + var3;
                  break;
               case 4:
                  switch (var3.charAt(0)) {
                     case '1':
                        var2 = "9" + var3.substring(1, 4);
                        break;
                     case '2':
                        var2 = "a" + var3.substring(1, 4);
                        break;
                     case '3':
                        var2 = "b" + var3.substring(1, 4);
                        break;
                     case '4':
                        var2 = "c" + var3.substring(1, 4);
                        break;
                     case '5':
                        var2 = "d" + var3.substring(1, 4);
                        break;
                     case '6':
                        var2 = "e" + var3.substring(1, 4);
                        break;
                     case '7':
                        var2 = "f" + var3.substring(1, 4);
                  }
            }
         } else {
            var2 = Integer.toString(var1, 16);
         }

         return this.e(var2);
      }

      private String c(long var1) {
         String var3 = null;
         if (var1 == Long.MIN_VALUE) {
            var3 = "8000000000000000";
         } else if (var1 < 0L) {
            String var4 = Long.toString(~(-var1 - 1L) ^ Long.MIN_VALUE, 16);
            switch (var4.length()) {
               case 1:
                  var3 = "800000000000000" + var4;
                  break;
               case 2:
                  var3 = "80000000000000" + var4;
                  break;
               case 3:
                  var3 = "8000000000000" + var4;
                  break;
               case 4:
                  var3 = "800000000000" + var4;
                  break;
               case 5:
                  var3 = "80000000000" + var4;
                  break;
               case 6:
                  var3 = "8000000000" + var4;
                  break;
               case 7:
                  var3 = "800000000" + var4;
                  break;
               case 8:
                  var3 = "80000000" + var4;
                  break;
               case 9:
                  var3 = "8000000" + var4;
                  break;
               case 10:
                  var3 = "800000" + var4;
                  break;
               case 11:
                  var3 = "80000" + var4;
                  break;
               case 12:
                  var3 = "8000" + var4;
                  break;
               case 13:
                  var3 = "800" + var4;
                  break;
               case 14:
                  var3 = "80" + var4;
                  break;
               case 15:
                  var3 = "8" + var4;
                  break;
               case 16:
                  switch (var4.charAt(0)) {
                     case '1':
                        var3 = "9" + var4.substring(1, 16);
                        break;
                     case '2':
                        var3 = "a" + var4.substring(1, 16);
                        break;
                     case '3':
                        var3 = "b" + var4.substring(1, 16);
                        break;
                     case '4':
                        var3 = "c" + var4.substring(1, 16);
                        break;
                     case '5':
                        var3 = "d" + var4.substring(1, 16);
                        break;
                     case '6':
                        var3 = "e" + var4.substring(1, 16);
                        break;
                     case '7':
                        var3 = "f" + var4.substring(1, 16);
                  }
            }
         } else {
            var3 = Long.toString(var1, 16);
         }

         return this.e(var3);
      }

      private String k(int var1) {
         String var2 = null;
         if (var1 == Integer.MIN_VALUE) {
            var2 = "80000000";
         } else if (var1 < 0) {
            String var3 = Integer.toString(~(-var1 - 1) ^ Integer.MIN_VALUE, 16);
            switch (var3.length()) {
               case 1:
                  var2 = "8000000" + var3;
                  break;
               case 2:
                  var2 = "800000" + var3;
                  break;
               case 3:
                  var2 = "80000" + var3;
                  break;
               case 4:
                  var2 = "8000" + var3;
                  break;
               case 5:
                  var2 = "800" + var3;
                  break;
               case 6:
                  var2 = "80" + var3;
                  break;
               case 7:
                  var2 = "8" + var3;
                  break;
               case 8:
                  switch (var3.charAt(0)) {
                     case '1':
                        var2 = "9" + var3.substring(1, 8);
                        break;
                     case '2':
                        var2 = "a" + var3.substring(1, 8);
                        break;
                     case '3':
                        var2 = "b" + var3.substring(1, 8);
                        break;
                     case '4':
                        var2 = "c" + var3.substring(1, 8);
                        break;
                     case '5':
                        var2 = "d" + var3.substring(1, 8);
                        break;
                     case '6':
                        var2 = "e" + var3.substring(1, 8);
                        break;
                     case '7':
                        var2 = "f" + var3.substring(1, 8);
                  }
            }
         } else {
            var2 = Integer.toString(var1, 16);
         }

         return this.e(var2);
      }

      private String e(String var1) {
         int var2 = 0;
         int var3 = 0;
         if (var1.equals("0") && this.rJ && this.rG == 0) {
            var1 = "";
         }

         if (this.rJ) {
            var2 = this.rG - var1.length();
         }

         if (var2 < 0) {
            var2 = 0;
         }

         if (this.rF) {
            var3 = this.rE - var2 - var1.length();
            if (this.rB) {
               var3 -= 2;
            }
         }

         if (var3 < 0) {
            var3 = 0;
         }

         int var4 = 0;
         if (this.rB) {
            var4 += 2;
         }

         var4 += var2;
         var4 += var1.length();
         var4 += var3;
         char[] var5 = new char[var4];
         int var6 = 0;
         if (this.ry) {
            if (this.rB) {
               var5[var6++] = '0';
               var5[var6++] = 'x';
            }

            for (int var7 = 0; var7 < var2; var6++) {
               var5[var6] = '0';
               var7++;
            }

            char[] var14 = var1.toCharArray();

            for (int var8 = 0; var8 < var14.length; var6++) {
               var5[var6] = var14[var8];
               var8++;
            }

            for (int var20 = 0; var20 < var3; var6++) {
               var5[var6] = ' ';
               var20++;
            }
         } else {
            if (!this.rC) {
               for (int var15 = 0; var15 < var3; var6++) {
                  var5[var6] = ' ';
                  var15++;
               }
            }

            if (this.rB) {
               var5[var6++] = '0';
               var5[var6++] = 'x';
            }

            if (this.rC) {
               for (int var16 = 0; var16 < var3; var6++) {
                  var5[var6] = '0';
                  var16++;
               }
            }

            for (int var17 = 0; var17 < var2; var6++) {
               var5[var6] = '0';
               var17++;
            }

            char[] var18 = var1.toCharArray();

            for (int var21 = 0; var21 < var18.length; var6++) {
               var5[var6] = var18[var21];
               var21++;
            }
         }

         String var19 = new String(var5);
         if (this.rT == 'X') {
            var19 = var19.toUpperCase();
         }

         return var19;
      }

      private String c(short var1) {
         String var2 = null;
         if (var1 == -32768) {
            var2 = "100000";
         } else if (var1 < 0) {
            String var3 = Integer.toString(~(-var1 - 1) ^ -32768, 8);
            switch (var3.length()) {
               case 1:
                  var2 = "10000" + var3;
                  break;
               case 2:
                  var2 = "1000" + var3;
                  break;
               case 3:
                  var2 = "100" + var3;
                  break;
               case 4:
                  var2 = "10" + var3;
                  break;
               case 5:
                  var2 = "1" + var3;
            }
         } else {
            var2 = Integer.toString(var1, 8);
         }

         return this.f(var2);
      }

      private String d(long var1) {
         String var3 = null;
         if (var1 == Long.MIN_VALUE) {
            var3 = "1000000000000000000000";
         } else if (var1 < 0L) {
            String var4 = Long.toString(~(-var1 - 1L) ^ Long.MIN_VALUE, 8);
            switch (var4.length()) {
               case 1:
                  var3 = "100000000000000000000" + var4;
                  break;
               case 2:
                  var3 = "10000000000000000000" + var4;
                  break;
               case 3:
                  var3 = "1000000000000000000" + var4;
                  break;
               case 4:
                  var3 = "100000000000000000" + var4;
                  break;
               case 5:
                  var3 = "10000000000000000" + var4;
                  break;
               case 6:
                  var3 = "1000000000000000" + var4;
                  break;
               case 7:
                  var3 = "100000000000000" + var4;
                  break;
               case 8:
                  var3 = "10000000000000" + var4;
                  break;
               case 9:
                  var3 = "1000000000000" + var4;
                  break;
               case 10:
                  var3 = "100000000000" + var4;
                  break;
               case 11:
                  var3 = "10000000000" + var4;
                  break;
               case 12:
                  var3 = "1000000000" + var4;
                  break;
               case 13:
                  var3 = "100000000" + var4;
                  break;
               case 14:
                  var3 = "10000000" + var4;
                  break;
               case 15:
                  var3 = "1000000" + var4;
                  break;
               case 16:
                  var3 = "100000" + var4;
                  break;
               case 17:
                  var3 = "10000" + var4;
                  break;
               case 18:
                  var3 = "1000" + var4;
                  break;
               case 19:
                  var3 = "100" + var4;
                  break;
               case 20:
                  var3 = "10" + var4;
                  break;
               case 21:
                  var3 = "1" + var4;
            }
         } else {
            var3 = Long.toString(var1, 8);
         }

         return this.f(var3);
      }

      private String l(int var1) {
         String var2 = null;
         if (var1 == Integer.MIN_VALUE) {
            var2 = "20000000000";
         } else if (var1 < 0) {
            String var3 = Integer.toString(~(-var1 - 1) ^ Integer.MIN_VALUE, 8);
            switch (var3.length()) {
               case 1:
                  var2 = "2000000000" + var3;
                  break;
               case 2:
                  var2 = "200000000" + var3;
                  break;
               case 3:
                  var2 = "20000000" + var3;
                  break;
               case 4:
                  var2 = "2000000" + var3;
                  break;
               case 5:
                  var2 = "200000" + var3;
                  break;
               case 6:
                  var2 = "20000" + var3;
                  break;
               case 7:
                  var2 = "2000" + var3;
                  break;
               case 8:
                  var2 = "200" + var3;
                  break;
               case 9:
                  var2 = "20" + var3;
                  break;
               case 10:
                  var2 = "2" + var3;
                  break;
               case 11:
                  var2 = "3" + var3.substring(1);
            }
         } else {
            var2 = Integer.toString(var1, 8);
         }

         return this.f(var2);
      }

      private String f(String var1) {
         int var2 = 0;
         int var3 = 0;
         if (var1.equals("0") && this.rJ && this.rG == 0) {
            var1 = "";
         }

         if (this.rJ) {
            var2 = this.rG - var1.length();
         }

         if (this.rB) {
            var2++;
         }

         if (var2 < 0) {
            var2 = 0;
         }

         if (this.rF) {
            var3 = this.rE - var2 - var1.length();
         }

         if (var3 < 0) {
            var3 = 0;
         }

         int var4 = var2 + var1.length() + var3;
         char[] var5 = new char[var4];
         if (this.ry) {
            int var6;
            for (var6 = 0; var6 < var2; var6++) {
               var5[var6] = '0';
            }

            char[] var7 = var1.toCharArray();

            for (int var8 = 0; var8 < var7.length; var6++) {
               var5[var6] = var7[var8];
               var8++;
            }

            for (int var12 = 0; var12 < var3; var6++) {
               var5[var6] = ' ';
               var12++;
            }
         } else {
            int var9;
            if (this.rC) {
               for (var9 = 0; var9 < var3; var9++) {
                  var5[var9] = '0';
               }
            } else {
               for (var9 = 0; var9 < var3; var9++) {
                  var5[var9] = ' ';
               }
            }

            for (int var10 = 0; var10 < var2; var9++) {
               var5[var9] = '0';
               var10++;
            }

            char[] var11 = var1.toCharArray();

            for (int var13 = 0; var13 < var11.length; var9++) {
               var5[var9] = var11[var13];
               var13++;
            }
         }

         return new String(var5);
      }

      private String a(char var1) {
         byte var2 = 1;
         int var3 = this.rE;
         if (!this.rF) {
            var3 = var2;
         }

         char[] var4 = new char[var3];
         int var5 = 0;
         if (this.ry) {
            var4[0] = var1;

            for (int var6 = 1; var6 <= var3 - var2; var6++) {
               var4[var6] = ' ';
            }
         } else {
            for (var5 = 0; var5 < var3 - var2; var5++) {
               var4[var5] = ' ';
            }

            var4[var5] = var1;
         }

         return new String(var4);
      }

      private String g(String var1) {
         int var2 = var1.length();
         int var3 = this.rE;
         if (this.rJ && var2 > this.rG) {
            var2 = this.rG;
         }

         if (!this.rF) {
            var3 = var2;
         }

         int var4 = 0;
         if (var3 > var2) {
            var4 += var3 - var2;
         }

         if (var2 >= var1.length()) {
            var4 += var1.length();
         } else {
            var4 += var2;
         }

         char[] var5 = new char[var4];
         int var6 = 0;
         if (this.ry) {
            if (var2 >= var1.length()) {
               char[] var12 = var1.toCharArray();

               for (var6 = 0; var6 < var1.length(); var6++) {
                  var5[var6] = var12[var6];
               }
            } else {
               char[] var7 = var1.substring(0, var2).toCharArray();

               for (var6 = 0; var6 < var2; var6++) {
                  var5[var6] = var7[var6];
               }
            }

            for (int var13 = 0; var13 < var3 - var2; var6++) {
               var5[var6] = ' ';
               var13++;
            }
         } else {
            for (var6 = 0; var6 < var3 - var2; var6++) {
               var5[var6] = ' ';
            }

            if (var2 >= var1.length()) {
               char[] var14 = var1.toCharArray();

               for (int var8 = 0; var8 < var1.length(); var8++) {
                  var5[var6] = var14[var8];
                  var6++;
               }
            } else {
               char[] var15 = var1.substring(0, var2).toCharArray();

               for (int var16 = 0; var16 < var2; var16++) {
                  var5[var6] = var15[var16];
                  var6++;
               }
            }
         }

         return new String(var5);
      }

      private boolean aR() {
         boolean var1 = false;
         this.rT = 0;
         if (this.pos < this.rU.length()) {
            char var2 = this.rU.charAt(this.pos);
            if (var2 == 'i'
               || var2 == 'd'
               || var2 == 'f'
               || var2 == 'g'
               || var2 == 'G'
               || var2 == 'o'
               || var2 == 'x'
               || var2 == 'X'
               || var2 == 'e'
               || var2 == 'E'
               || var2 == 'c'
               || var2 == 's'
               || var2 == '%') {
               this.rT = var2;
               this.pos++;
               var1 = true;
            }
         }

         return var1;
      }

      private void aS() {
         this.rQ = false;
         this.rR = false;
         this.rS = false;
         if (this.pos < this.rU.length()) {
            char var1 = this.rU.charAt(this.pos);
            if (var1 == 'h') {
               this.rQ = true;
               this.pos++;
            } else if (var1 == 'l') {
               this.rR = true;
               this.pos++;
            } else if (var1 == 'L') {
               this.rS = true;
               this.pos++;
            }
         }
      }

      private void aT() {
         int var1 = this.pos;
         this.rJ = false;
         if (this.pos < this.rU.length() && this.rU.charAt(this.pos) == '.') {
            this.pos++;
            if (this.pos < this.rU.length() && this.rU.charAt(this.pos) == '*') {
               this.pos++;
               if (!this.aX()) {
                  this.rI = true;
                  this.rJ = true;
               }

               return;
            }

            while (this.pos < this.rU.length()) {
               char var2 = this.rU.charAt(this.pos);
               if (!Character.isDigit(var2)) {
                  break;
               }

               this.pos++;
            }

            if (this.pos > var1 + 1) {
               String var3 = this.rU.substring(var1 + 1, this.pos);
               this.rG = Integer.parseInt(var3);
               this.rJ = true;
            }
         }
      }

      private void aU() {
         int var1 = this.pos;
         this.rE = 0;
         this.rF = false;
         if (this.pos < this.rU.length() && this.rU.charAt(this.pos) == '*') {
            this.pos++;
            if (!this.aW()) {
               this.rD = true;
               this.rF = true;
            }
         } else {
            while (true) {
               if (this.pos < this.rU.length()) {
                  char var2 = this.rU.charAt(this.pos);
                  if (Character.isDigit(var2)) {
                     this.pos++;
                     continue;
                  }
               }

               if (var1 < this.pos && var1 < this.rU.length()) {
                  String var3 = this.rU.substring(var1, this.pos);
                  this.rE = Integer.parseInt(var3);
                  this.rF = true;
               }
               break;
            }
         }
      }

      private void aV() {
         int var1 = this.pos;

         while (var1 < this.rU.length() && Character.isDigit(this.rU.charAt(var1))) {
            var1++;
         }

         if (var1 > this.pos && var1 < this.rU.length() && this.rU.charAt(var1) == '$') {
            this.rK = true;
            this.rL = Integer.parseInt(this.rU.substring(this.pos, var1));
            this.pos = var1 + 1;
         }
      }

      private boolean aW() {
         boolean var1 = false;
         int var2 = this.pos;

         while (var2 < this.rU.length() && Character.isDigit(this.rU.charAt(var2))) {
            var2++;
         }

         if (var2 > this.pos && var2 < this.rU.length() && this.rU.charAt(var2) == '$') {
            this.rM = true;
            this.rN = Integer.parseInt(this.rU.substring(this.pos, var2));
            this.pos = var2 + 1;
            var1 = true;
         }

         return var1;
      }

      private boolean aX() {
         boolean var1 = false;
         int var2 = this.pos;

         while (var2 < this.rU.length() && Character.isDigit(this.rU.charAt(var2))) {
            var2++;
         }

         if (var2 > this.pos && var2 < this.rU.length() && this.rU.charAt(var2) == '$') {
            this.rO = true;
            this.rP = Integer.parseInt(this.rU.substring(this.pos, var2));
            this.pos = var2 + 1;
            var1 = true;
         }

         return var1;
      }

      boolean isPositionalSpecification() {
         return this.rK;
      }

      int getArgumentPosition() {
         return this.rL;
      }

      boolean isPositionalFieldWidth() {
         return this.rM;
      }

      int getArgumentPositionForFieldWidth() {
         return this.rN;
      }

      boolean isPositionalPrecision() {
         return this.rO;
      }

      int getArgumentPositionForPrecision() {
         return this.rP;
      }

      private void aY() {
         this.rx = false;
         this.ry = false;
         this.rz = false;
         this.rA = false;
         this.rB = false;

         for (this.rC = false; this.pos < this.rU.length(); this.pos++) {
            char var1 = this.rU.charAt(this.pos);
            if (var1 == '\'') {
               this.rx = true;
            } else if (var1 == '-') {
               this.ry = true;
               this.rC = false;
            } else if (var1 == '+') {
               this.rz = true;
               this.rA = false;
            } else if (var1 == ' ') {
               if (!this.rz) {
                  this.rA = true;
               }
            } else if (var1 == '#') {
               this.rB = true;
            } else {
               if (var1 != '0') {
                  break;
               }

               if (!this.ry) {
                  this.rC = true;
               }
            }
         }
      }
   }
}
