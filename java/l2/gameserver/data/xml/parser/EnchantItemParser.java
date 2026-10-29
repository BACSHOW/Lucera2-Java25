package l2.gameserver.data.xml.parser;

import gnu.trove.TDoubleArrayList;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.EnchantItemHolder;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.support.EnchantChanceType;
import l2.gameserver.templates.item.support.EnchantScroll;
import l2.gameserver.templates.item.support.EnchantScrollOnFailAction;
import l2.gameserver.templates.item.support.EnchantTargetType;
import org.dom4j.Element;

public class EnchantItemParser extends AbstractFileParser<EnchantItemHolder> {
   private static EnchantItemParser xn = new EnchantItemParser();

   public static EnchantItemParser getInstance() {
      return xn;
   }

   private EnchantItemParser() {
      super(EnchantItemHolder.getInstance());
   }

   @Override
   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/enchant_items.xml");
   }

   @Override
   public String getDTDFileName() {
      return "enchant_items.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         if (var3.getName().equals("scroll")) {
            int var4 = Integer.parseInt(var3.attributeValue("id"));
            boolean var5 = Boolean.parseBoolean(var3.attributeValue("infallible", "false"));
            EnchantScrollOnFailAction var6 = EnchantScrollOnFailAction.NONE;
            int var7 = 0;
            if (!var5) {
               var6 = EnchantScrollOnFailAction.valueOf(var3.attributeValue("on_fail"));
               var7 = Integer.parseInt(var3.attributeValue("reset_lvl", "0"));
            }

            double var8 = Double.parseDouble(var3.attributeValue("chance_bonus", "0"));
            ItemTemplate.Grade var10 = ItemTemplate.Grade.valueOf(var3.attributeValue("grade"));
            int var11 = 0;
            int var12 = Config.ENCHANT_MAX;
            int var13 = Integer.parseInt(var3.attributeValue("increment", "1"));
            EnchantTargetType var14 = EnchantTargetType.ALL;
            ArrayList var15 = new ArrayList();
            LinkedHashMap var16 = new LinkedHashMap();
            Iterator var17 = var3.elementIterator();

            while (var17.hasNext()) {
               Element var18 = (Element)var17.next();
               if (var18.getName().equals("levels")) {
                  var11 = Integer.parseInt(var18.attributeValue("min"));
                  var12 = Integer.parseInt(var18.attributeValue("max"));
               } else if (var18.getName().equals("items_restrict")) {
                  var14 = EnchantTargetType.valueOf(var18.attributeValue("type"));
                  Iterator var24 = var18.elementIterator("item");

                  while (var24.hasNext()) {
                     var15.add(Integer.valueOf(((Element)var24.next()).attributeValue("id")));
                  }
               } else if (var18.getName().equals("chances")) {
                  EnchantChanceType var19 = EnchantChanceType.valueOf(var18.attributeValue("type"));
                  TDoubleArrayList var20 = new TDoubleArrayList();
                  Iterator var21 = var18.elementIterator("chance");

                  while (var21.hasNext()) {
                     var20.add(Double.valueOf(((Element)var21.next()).attributeValue("val")));
                  }

                  var16.put(var19, var20.toNativeArray());
               }
            }

            EnchantScroll var22 = new EnchantScroll(var4, var13, var8, var10, var11, var12, var14, var6, var7, var5, false, var16);
            if (!var15.isEmpty()) {
               for (Object __raw_var25_92 : (Iterable<?>)(Object)(var15)) {
                  Integer var25 = (Integer)__raw_var25_92;
                  var22.addItemRestrict(var25);
               }
            }

            this.getHolder().addEnchantItem(var22);
         } else {
            this.error("Unknown entry " + var3.getName());
         }
      }
   }
}
