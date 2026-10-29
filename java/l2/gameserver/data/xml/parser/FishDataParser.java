package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import l2.commons.collections.MultiValueSet;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.FishDataHolder;
import l2.gameserver.templates.item.support.FishGroup;
import l2.gameserver.templates.item.support.FishTemplate;
import l2.gameserver.templates.item.support.LureTemplate;
import l2.gameserver.templates.item.support.LureType;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class FishDataParser extends AbstractFileParser<FishDataHolder> {
   private static final FishDataParser xp = new FishDataParser();

   public static FishDataParser getInstance() {
      return xp;
   }

   private FishDataParser() {
      super(FishDataHolder.getInstance());
   }

   @Override
   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/fishdata.xml");
   }

   @Override
   public String getDTDFileName() {
      return "fishdata.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         if ("fish".equals(var3.getName())) {
            MultiValueSet var12 = new MultiValueSet();
            Iterator var15 = var3.attributeIterator();

            while (var15.hasNext()) {
               Attribute var18 = (Attribute)var15.next();
               var12.put(var18.getName(), var18.getValue());
            }

            this.getHolder().addFish(new FishTemplate(var12));
         } else if ("lure".equals(var3.getName())) {
            MultiValueSet var11 = new MultiValueSet();
            Iterator var13 = var3.attributeIterator();

            while (var13.hasNext()) {
               Attribute var16 = (Attribute)var13.next();
               var11.put(var16.getName(), var16.getValue());
            }

            HashMap var14 = new HashMap();
            Iterator var17 = var3.elementIterator();

            while (var17.hasNext()) {
               Element var19 = (Element)var17.next();
               var14.put(FishGroup.valueOf(var19.attributeValue("type")), Integer.parseInt(var19.attributeValue("value")));
            }

            var11.put("chances", var14);
            this.getHolder().addLure(new LureTemplate(var11));
         } else if ("distribution".equals(var3.getName())) {
            int var4 = Integer.parseInt(var3.attributeValue("id"));
            Iterator var5 = var3.elementIterator();

            while (var5.hasNext()) {
               Element var6 = (Element)var5.next();
               LureType var7 = LureType.valueOf(var6.attributeValue("type"));
               HashMap var8 = new HashMap();
               Iterator var9 = var6.elementIterator();

               while (var9.hasNext()) {
                  Element var10 = (Element)var9.next();
                  var8.put(FishGroup.valueOf(var10.attributeValue("type")), Integer.parseInt(var10.attributeValue("value")));
               }

               this.getHolder().addDistribution(var4, var7, var8);
            }
         }
      }
   }
}
