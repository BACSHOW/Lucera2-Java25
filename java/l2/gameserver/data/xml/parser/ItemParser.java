package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.OptionDataTemplate;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ArmorTemplate;
import l2.gameserver.templates.item.Bodypart;
import l2.gameserver.templates.item.EtcItemTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import org.dom4j.Element;

public final class ItemParser extends StatParser<ItemHolder> {
   private static final ItemParser xt = new ItemParser();

   public static ItemParser getInstance() {
      return xt;
   }

   protected ItemParser() {
      super(ItemHolder.getInstance());
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/items/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "item.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      label166:
      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         StatsSet var4 = new StatsSet();
         var4.set("item_id", var3.attributeValue("id"));
         var4.set("name", var3.attributeValue("name"));
         var4.set("add_name", var3.attributeValue("add_name", ""));
         int var5 = 0;
         Iterator var6 = var3.elementIterator();
         ItemTemplate var20 = null;

         while (true) {
            if (!var6.hasNext()) {
               var4.set("bodypart", var5);
               try {
                  if (var3.getName().equalsIgnoreCase("weapon")) {
                     if (!var4.containsKey("class")) {
                        if ((var5 & 256) > 0) {
                           var4.set("class", ItemTemplate.ItemClass.ARMOR);
                        } else {
                           var4.set("class", ItemTemplate.ItemClass.WEAPON);
                        }
                     }

                     var20 = new WeaponTemplate(var4);
                  } else if (var3.getName().equalsIgnoreCase("armor")) {
                     if (!var4.containsKey("class")) {
                        if ((var5 & 180032) > 0) {
                           var4.set("class", ItemTemplate.ItemClass.ARMOR);
                        } else if ((var5 & 62) > 0) {
                           var4.set("class", ItemTemplate.ItemClass.JEWELRY);
                        } else {
                           var4.set("class", ItemTemplate.ItemClass.ACCESSORY);
                        }
                     }

                     var20 = new ArmorTemplate(var4);
                  } else {
                     var20 = new EtcItemTemplate(var4);
                  }
                  break;
               } catch (Exception var18) {
                  this.warn("Fail create item: " + var4.get("item_id"), var18);
                  continue label166;
               }
            }

            Element var7 = (Element)var6.next();
            String var8 = var7.getName();
            if (var8.equalsIgnoreCase("set")) {
               var4.set(var7.attributeValue("name"), var7.attributeValue("value"));
            } else if (var8.equalsIgnoreCase("equip")) {
               Iterator var9 = var7.elementIterator();

               while (var9.hasNext()) {
                  Element var10 = (Element)var9.next();
                  Bodypart var11 = Bodypart.valueOf(var10.attributeValue("id"));
                  if (var11.getReal() != null) {
                     var5 = var11.mask();
                  } else {
                     var5 |= var11.mask();
                  }
               }
            }
         }

         Iterator var21 = var3.elementIterator();

         while (var21.hasNext()) {
            Element var22 = (Element)var21.next();
            String var23 = var22.getName();
            if (var23.equalsIgnoreCase("for")) {
               this.parseFor(var22, var20);
            } else if (var23.equalsIgnoreCase("triggers")) {
               this.parseTriggers(var22, var20);
            } else if (var23.equalsIgnoreCase("skills")) {
               Iterator var28 = var22.elementIterator();

               while (var28.hasNext()) {
                  Element var33 = (Element)var28.next();
                  int var36 = Integer.parseInt(var33.attributeValue("id"));
                  int var38 = Integer.parseInt(var33.attributeValue("level"));
                  Skill var39 = SkillTable.getInstance().getInfo(var36, var38);
                  if (var39 != null) {
                     var20.attachSkill(var39);
                  } else {
                     this.info("Skill not found(" + var36 + "," + var38 + ") for item:" + var4.getObject("item_id") + "; file:" + this.getCurrentFileName());
                  }
               }
            } else if (var23.equalsIgnoreCase("enchant4_skill")) {
               int var27 = Integer.parseInt(var22.attributeValue("id"));
               int var32 = Integer.parseInt(var22.attributeValue("level"));
               Skill var35 = SkillTable.getInstance().getInfo(var27, var32);
               if (var35 != null) {
                  var20.setEnchant4Skill(var35);
               }
            } else if (var23.equalsIgnoreCase("cond")) {
               Condition var26 = this.parseFirstCond(var22);
               if (var26 != null) {
                  int var31 = this.parseNumber(var22.attributeValue("msgId")).intValue();
                  var26.setSystemMsg(var31);
                  var20.addCondition(var26);
               }
            } else if (var23.equalsIgnoreCase("attributes")) {
               int[] var25 = new int[6];
               Iterator var30 = var22.elementIterator();

               while (var30.hasNext()) {
                  Element var34 = (Element)var30.next();
                  if (var34.getName().equalsIgnoreCase("attribute")) {
                     l2.gameserver.model.base.Element var37 = l2.gameserver.model.base.Element.getElementByName(var34.attributeValue("element"));
                     var25[var37.getId()] = Integer.parseInt(var34.attributeValue("value"));
                  }
               }

               var20.setBaseAtributeElements(var25);
            } else if (var23.equalsIgnoreCase("enchant_options")) {
               Iterator var24 = var22.elementIterator();

               while (var24.hasNext()) {
                  Element var29 = (Element)var24.next();
                  if (var29.getName().equalsIgnoreCase("level")) {
                     int var12 = Integer.parseInt(var29.attributeValue("val"));
                     int var13 = 0;
                     int[] var14 = new int[3];

                     for (Element var16 : var29.elements()) {
                        OptionDataTemplate var17 = OptionDataHolder.getInstance().getTemplate(Integer.parseInt(var16.attributeValue("id")));
                        if (var17 == null) {
                           this.error("Not found option_data for id: " + var16.attributeValue("id") + "; item_id: " + var4.get("item_id"));
                        } else {
                           var14[var13++] = var17.getId();
                        }
                     }

                     var20.addEnchantOptions(var12, var14);
                  }
               }
            }
         }

         this.getHolder().addItem(var20);
      }
   }

   @Override
   protected Object getTableValue(String var1) {
      return null;
   }
}
