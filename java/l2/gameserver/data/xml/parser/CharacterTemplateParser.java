package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.CharacterTemplateHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.templates.PlayerTemplate;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Triple;
import org.dom4j.Element;

public class CharacterTemplateParser extends AbstractDirParser<CharacterTemplateHolder> {
   private static final CharacterTemplateParser xh = new CharacterTemplateParser();

   public static final CharacterTemplateParser getInstance() {
      return xh;
   }

   protected CharacterTemplateParser() {
      super(CharacterTemplateHolder.getInstance());
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/stats/player/pcparams/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "pcparams.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         if ("pcparams".equalsIgnoreCase(var3.getName())) {
            ClassId var4 = null;
            String var5 = var3.attributeValue("classId");
            if (var5 != null) {
               var4 = ClassId.getClassById(Integer.parseInt(var5));
            } else {
               var4 = ClassId.valueOf(var3.attributeValue("classType"));
            }

            String var6 = var3.attributeValue("name");
            Iterator var7 = var3.elementIterator();

            while (var7.hasNext()) {
               StatsSet var8 = new StatsSet();
               ArrayList var9 = new ArrayList();
               ArrayList var10 = new ArrayList();
               Location var11 = null;
               boolean var12 = false;
               Element var13 = (Element)var7.next();
               if ("male".equals(var13.getName())) {
                  var12 = true;
               } else {
                  if (!"female".equals(var13.getName())) {
                     throw new IllegalStateException();
                  }

                  var12 = false;
               }

               Iterator var14 = var13.elementIterator();

               while (var14.hasNext()) {
                  Element var15 = (Element)var14.next();
                  if ("items".equals(var15.getName())) {
                     for (Element var27 : var15.elements()) {
                        if ("item".equals(var27.getName())) {
                           boolean var18 = Boolean.parseBoolean(var27.attributeValue("equipped", "false"));
                           int var19 = Integer.parseInt(var27.attributeValue("enchant", "0"));
                           ItemTemplate var20 = ItemHolder.getInstance().getTemplate(Integer.parseInt(var27.attributeValue("id")));
                           long var21 = Long.parseLong(var27.attributeValue("amount", "1"));
                           if (var18 && !var20.isEquipable()) {
                              this.warn("Item " + var20.getItemId() + " can't be equipped.");
                              var18 = false;
                           }

                           if (var19 > 0 && !var20.canBeEnchanted(false)) {
                              this.warn("Item " + var20.getItemId() + " can't be enchanted.");
                              var19 = 0;
                           }

                           if (var18) {
                              var10.add(Triple.of(var20, var21, var19));
                           } else {
                              var9.add(Triple.of(var20, var21, var19));
                           }
                        }
                     }
                  } else if ("spawn".equals(var15.getName())) {
                     var11 = Location.parse(var15);
                  } else if ("stats".equals(var15.getName())) {
                     for (Element var17 : var15.elements()) {
                        if ("set".equals(var17.getName())) {
                           var8.set(var17.attributeValue("name"), var17.attributeValue("value"));
                        }
                     }
                  }
               }

               PlayerTemplate var25 = new PlayerTemplate(var4, var8, var6, var11, var12);
               var25.getInitialItems().addAll(var9);
               var25.getInitialEquipItems().addAll(var10);
               this.getHolder().addTemplate(var25, var12);
            }
         }
      }
   }
}
