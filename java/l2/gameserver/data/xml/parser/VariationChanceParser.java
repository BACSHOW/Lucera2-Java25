package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractFileParser;
import l2.commons.util.RandomUtils;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.VariationChanceHolder;
import l2.gameserver.templates.item.support.VariationChanceData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Element;

public class VariationChanceParser extends AbstractFileParser<VariationChanceHolder> {
   private static final VariationChanceParser xF = new VariationChanceParser();

   public static VariationChanceParser getInstance() {
      return xF;
   }

   private VariationChanceParser() {
      super(VariationChanceHolder.getInstance());
   }

   @Override
   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/variation_data.xml");
   }

   @Override
   public String getDTDFileName() {
      return "variation_data.dtd";
   }

   private List<Pair<List<Pair<Integer, Double>>, Double>> e(Element var1) throws Exception {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.elementIterator();

      while (var3.hasNext()) {
         Element var4 = (Element)var3.next();
         if ("group".equalsIgnoreCase(var4.getName())) {
            double var5 = Double.parseDouble(var4.attributeValue("chance"));
            ArrayList var7 = new ArrayList();
            Iterator var8 = var4.elementIterator();

            while (var8.hasNext()) {
               Element var9 = (Element)var8.next();
               int var10 = Integer.parseInt(var9.attributeValue("id"));
               double var11 = Double.parseDouble(var9.attributeValue("chance"));
               var7.add(new ImmutablePair(var10, var11));
            }

            Collections.sort(var7, RandomUtils.DOUBLE_GROUP_COMPARATOR);
            var2.add(new ImmutablePair(var7, var5));
         }
      }

      return var2;
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         int var4 = Integer.parseInt(var3.attributeValue("mineralId"));
         boolean var5 = false;
         VariationChanceData var6 = null;
         VariationChanceData var7 = null;
         Iterator var8 = var3.elementIterator();

         while (var8.hasNext()) {
            Element var9 = (Element)var8.next();
            String var10 = var9.attributeValue("type");
            List var11 = null;
            List var12 = null;
            Iterator var13 = var9.elementIterator();

            while (var13.hasNext()) {
               Element var14 = (Element)var13.next();
               if ("variation1".equalsIgnoreCase(var14.getName())) {
                  List var15 = this.e(var14);
                  Collections.sort(var15, RandomUtils.DOUBLE_GROUP_COMPARATOR);
                  var11 = var15;
               } else if ("variation2".equalsIgnoreCase(var14.getName())) {
                  List var16 = this.e(var14);
                  Collections.sort(var16, RandomUtils.DOUBLE_GROUP_COMPARATOR);
                  var12 = var16;
               }
            }

            if (var10.equalsIgnoreCase("WARRIOR")) {
               var6 = new VariationChanceData(var4, var11 != null ? var11 : Collections.emptyList(), var12 != null ? var12 : Collections.emptyList());
            } else {
               if (!var10.equalsIgnoreCase("MAGE")) {
                  throw new RuntimeException("Unknown type " + var10);
               }

               var7 = new VariationChanceData(var4, var11 != null ? var11 : Collections.emptyList(), var12 != null ? var12 : Collections.emptyList());
            }
         }

         this.getHolder().add(new ImmutablePair(var6, var7));
      }
   }
}
