package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.VariationGroupHolder;
import l2.gameserver.templates.item.support.VariationGroupData;
import org.dom4j.Element;

public class VariationGroupParser extends AbstractFileParser<VariationGroupHolder> {
   private static final VariationGroupParser xG = new VariationGroupParser();
   private HashMap<Integer, VariationGroupData> xH = new HashMap<>();

   public static VariationGroupParser getInstance() {
      return xG;
   }

   private VariationGroupParser() {
      super(VariationGroupHolder.getInstance());
   }

   @Override
   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/variation_group.xml");
   }

   @Override
   public String getDTDFileName() {
      return "variation_group.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         String var4 = var3.attributeValue("name");
         ArrayList var5 = new ArrayList();
         ArrayList var6 = new ArrayList();
         Iterator var7 = var3.elementIterator();

         while (var7.hasNext()) {
            Element var8 = (Element)var7.next();
            String var9 = var8.getName();
            if ("items".equalsIgnoreCase(var9)) {
               StringTokenizer var22 = new StringTokenizer(var8.getStringValue());

               while (var22.hasMoreTokens()) {
                  var5.add(Integer.parseInt(var22.nextToken()));
               }
            } else if ("fee".equalsIgnoreCase(var9)) {
               long var10 = Long.parseLong(var8.attributeValue("cancelPrice"));
               Iterator var12 = var8.elementIterator();

               while (var12.hasNext()) {
                  Element var13 = (Element)var12.next();
                  if ("mineral".equalsIgnoreCase(var13.getName())) {
                     int var14 = Integer.parseInt(var13.attributeValue("itemId"));
                     int var15 = Integer.parseInt(var13.attributeValue("gemstoneItemId"));
                     long var16 = Long.parseLong(var13.attributeValue("gemstoneItemCnt"));
                     var6.add(new VariationGroupData(var14, var15, var16, var10));
                  }
               }
            }
         }

         if (var6.isEmpty()) {
            throw new RuntimeException("Undefined fee for group " + var4);
         }

         int[] var18 = new int[var5.size()];

         for (int var19 = 0; var19 < var5.size(); var19++) {
            var18[var19] = (Integer)var5.get(var19);
         }

         Arrays.sort(var18);

         for (Object __raw_var21_85 : (Iterable<?>)(Object)(var6)) {
            VariationGroupData var21 = (VariationGroupData)__raw_var21_85;
            this.getHolder().addSorted(var18, var21);
         }
      }
   }
}
