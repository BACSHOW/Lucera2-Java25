package l2.gameserver.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ArabicConv {
   private static final char awr = 'آ';
   private static final char aws = 'أ';
   private static final char awt = 'إ';
   private static final char awu = 'ا';
   private static final char awv = 'ل';
   private static final char[][] aww = new char[][]{
      {'آ', 'ﺁ', 'ﺁ', 'ﺂ', 'ﺂ', '\u0002'},
      {'أ', 'ﺂ', 'ﺃ', 'ﺄ', 'ﺄ', '\u0002'},
      {'ؤ', 'ﺅ', 'ﺅ', 'ﺆ', 'ﺆ', '\u0002'},
      {'إ', 'ﺇ', 'ﺇ', 'ﺈ', 'ﺈ', '\u0002'},
      {'ئ', 'ﺉ', 'ﺋ', 'ﺌ', 'ﺊ', '\u0004'},
      {'ا', 'ا', 'ا', 'ﺎ', 'ﺎ', '\u0002'},
      {'ب', 'ﺏ', 'ﺑ', 'ﺒ', 'ﺐ', '\u0004'},
      {'ة', 'ﺓ', 'ﺓ', 'ﺔ', 'ﺔ', '\u0002'},
      {'ت', 'ﺕ', 'ﺗ', 'ﺘ', 'ﺖ', '\u0004'},
      {'ث', 'ﺙ', 'ﺛ', 'ﺜ', 'ﺚ', '\u0004'},
      {'ج', 'ﺝ', 'ﺟ', 'ﺠ', 'ﺞ', '\u0004'},
      {'ح', 'ﺡ', 'ﺣ', 'ﺤ', 'ﺢ', '\u0004'},
      {'خ', 'ﺥ', 'ﺧ', 'ﺨ', 'ﺦ', '\u0004'},
      {'د', 'ﺩ', 'ﺩ', 'ﺪ', 'ﺪ', '\u0002'},
      {'ذ', 'ﺫ', 'ﺫ', 'ﺬ', 'ﺬ', '\u0002'},
      {'ر', 'ﺭ', 'ﺭ', 'ﺮ', 'ﺮ', '\u0002'},
      {'ز', 'ﺯ', 'ﺯ', 'ﺰ', 'ﺰ', '\u0002'},
      {'س', 'ﺱ', 'ﺳ', 'ﺴ', 'ﺲ', '\u0004'},
      {'ش', 'ﺵ', 'ﺷ', 'ﺸ', 'ﺶ', '\u0004'},
      {'ص', 'ﺹ', 'ﺻ', 'ﺼ', 'ﺺ', '\u0004'},
      {'ض', 'ﺽ', 'ﺿ', 'ﻀ', 'ﺾ', '\u0004'},
      {'ط', 'ﻁ', 'ﻃ', 'ﻂ', 'ﻄ', '\u0004'},
      {'ظ', 'ﻅ', 'ﻇ', 'ﻆ', 'ﻆ', '\u0004'},
      {'ع', 'ﻉ', 'ﻋ', 'ﻌ', 'ﻊ', '\u0004'},
      {'غ', 'ﻍ', 'ﻏ', 'ﻐ', 'ﻎ', '\u0004'},
      {'ف', 'ﻑ', 'ﻓ', 'ﻔ', 'ﻒ', '\u0004'},
      {'ق', 'ﻕ', 'ﻗ', 'ﻘ', 'ﻖ', '\u0004'},
      {'ك', 'ﻙ', 'ﻛ', 'ﻜ', 'ﻚ', '\u0004'},
      {'ل', 'ﻝ', 'ﻟ', 'ﻠ', 'ﻞ', '\u0004'},
      {'م', 'ﻡ', 'ﻣ', 'ﻤ', 'ﻢ', '\u0004'},
      {'ن', 'ﻥ', 'ﻧ', 'ﻨ', 'ﻦ', '\u0004'},
      {'ه', 'ﻩ', 'ﻫ', 'ﻬ', 'ﻪ', '\u0004'},
      {'و', 'ﻭ', 'ﻭ', 'ﻮ', 'ﻮ', '\u0002'},
      {'ى', 'ﻯ', 'ﻯ', 'ﻰ', 'ﻰ', '\u0002'},
      {'ٱ', 'ٱ', 'ٱ', 'ﭑ', 'ﭑ', '\u0002'},
      {'ي', 'ﻱ', 'ﻳ', 'ﻴ', 'ﻲ', '\u0004'},
      {'ٮ', 'ﯤ', 'ﯨ', 'ﯩ', 'ﯥ', '\u0004'},
      {'ٱ', 'ٱ', 'ٱ', 'ﭑ', 'ﭑ', '\u0002'},
      {'ڪ', 'ﮎ', 'ﮐ', 'ﮑ', 'ﮏ', '\u0004'},
      {'ہ', 'ﮦ', 'ﮨ', 'ﮩ', 'ﮧ', '\u0004'},
      {'ۤ', 'ۤ', 'ۤ', 'ۤ', 'ﻮ', '\u0002'},
      {'چ', 'ﭺ', 'ﭼ', 'ﭽ', 'ﭻ', '\u0004'},
      {'پ', 'ﭖ', 'ﭘ', 'ﭙ', 'ﭗ', '\u0004'},
      {'ژ', 'ﮊ', 'ﮊ', 'ﮋ', 'ﮋ', '\u0002'},
      {'گ', 'ﮒ', 'ﮔ', 'ﮕ', 'ﮓ', '\u0004'},
      {'ی', 'ﻯ', 'ﻳ', 'ﻴ', 'ﻰ', '\u0004'},
      {'ک', 'ﮎ', 'ﮐ', 'ﮑ', 'ﮏ', '\u0004'}
   };
   private static final Map<Character, char[]> awx;
   private static final char[] awy = new char[]{'ً', 'ٌ', 'ٍ', 'َ', 'ُ', 'ِ', 'ّ', 'ْ', 'ٓ', 'ٔ', 'ٕ', 'ٖ'};
   private static final char[][] awz = new char[][]{{'㮦', 'ﻶ', 'ﻵ'}, {'㮧', 'ﻸ', 'ﻷ'}, {'إ', 'ﻺ', 'ﻹ'}, {'ا', 'ﻼ', 'ﻻ'}};

   private static char a(char var0, char var1, boolean var2) {
      int var3 = 1;
      if (var2) {
         var3++;
      }

      if (1604 == var1) {
         switch (var0) {
            case 'آ':
               return awz[0][var3];
            case 'أ':
               return awz[1][var3];
            case 'ؤ':
            case 'ئ':
            default:
               break;
            case 'إ':
               return awz[2][var3];
            case 'ا':
               return awz[3][var3];
         }
      }

      return '\u0000';
   }

   private static final char a(char var0, int var1) {
      char[] var2 = awx.get(var0);
      if (var2 != null) {
         if (var0 != var2[0]) {
            throw new RuntimeException();
         } else {
            return var2[var1];
         }
      } else {
         return var0;
      }
   }

   private static final char b(char var0) {
      char[] var1 = awx.get(var0);
      if (var1 != null) {
         if (var0 != var1[0]) {
            throw new RuntimeException();
         } else {
            return var1[5];
         }
      } else {
         return '\u0002';
      }
   }

   private static String C(String var0) {
      if (var0.isEmpty()) {
         return "";
      }

      switch (var0.length()) {
         case 0:
            return "";
         case 1:
            return new String(new char[]{a(var0.charAt(0), 0)});
         case 2:
            char var1 = var0.charAt(0);
            char var2 = var0.charAt(1);
            char var3 = a(var2, var1, true);
            if (var3 > 0) {
               return new String(new char[]{var3});
            }
         default:
            char[] var10 = new char[var0.length()];
            char var11 = var0.charAt(0);
            var10[0] = a(var11, 2);

            for (int var12 = 1; var12 < var0.length() - 1; var12++) {
               char var4 = a(var0.charAt(var12), var11, true);
               if (var4 > 0) {
                  if (var12 - 2 >= 0 && (var12 - 2 < 0 || b(var0.charAt(var12 - 2)) != 2)) {
                     var10[var12 - 1] = 0;
                     var10[var12] = a(var0.charAt(var12), var11, false);
                  } else {
                     var10[var12 - 1] = 0;
                     var10[var12] = var4;
                  }
               } else if (b(var0.charAt(var12 - 1)) == 2) {
                  var10[var12] = a(var0.charAt(var12), 2);
               } else {
                  var10[var12] = a(var0.charAt(var12), 3);
               }

               var11 = var0.charAt(var12);
            }

            int var13 = var0.length();
            char var14 = a(var0.charAt(var13 - 1), var0.charAt(var13 - 2), true);
            if (var14 > 0) {
               if (var13 > 3 && b(var0.charAt(var13 - 3)) == 2) {
                  var10[var13 - 2] = 0;
                  var10[var13 - 1] = var14;
               } else {
                  var10[var13 - 2] = 0;
                  var10[var13 - 1] = a(var0.charAt(var13 - 1), var0.charAt(var13 - 2), false);
               }
            } else if (b(var0.charAt(var13 - 2)) == 2) {
               var10[var13 - 1] = a(var0.charAt(var13 - 1), 1);
            } else {
               var10[var13 - 1] = a(var0.charAt(var13 - 1), 4);
            }

            StringBuilder var5 = new StringBuilder();

            for (char var9 : var10) {
               if (var9 != 0) {
                  var5.append(var9);
               }
            }

            return var5.toString();
      }
   }

   public static boolean isArChar(char var0) {
      char[] var1 = awx.get(var0);
      return var1 != null;
   }

   public static String shapeArabic(String var0) {
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;

      for (int var3 = var0.length(); var2 < var3; var2++) {
         if (!isArChar(var0.charAt(var2))) {
            var1.append(var0.charAt(var2));
         } else {
            int var4 = var2;

            while (var2 < var3 && isArChar(var0.charAt(var2))) {
               var2++;
            }

            var1.append(C(var0.substring(var4, var2)));
            if (var2 < var3) {
               var1.append(var0.charAt(var2));
            }
         }
      }

      return var1.toString();
   }

   public static final void main(String... var0) {
   }

   static {
      HashMap var0 = new HashMap();

      for (char[] var4 : aww) {
         var0.put(var4[0], var4);
      }

      awx = Collections.unmodifiableMap(var0);
   }
}
