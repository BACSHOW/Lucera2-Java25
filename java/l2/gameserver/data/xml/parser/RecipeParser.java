package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Recipe;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeParser extends AbstractFileParser<RecipeHolder> {
   private static final Logger LOG = LoggerFactory.getLogger(RecipeParser.class);
   private static final RecipeParser xy = new RecipeParser(RecipeHolder.getInstance());

   protected RecipeParser(RecipeHolder var1) {
      super(var1);
   }

   public static RecipeParser getInstance() {
      return xy;
   }

   @Override
   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/recipe.xml");
   }

   @Override
   public String getDTDFileName() {
      return "recipes.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         if ("recipe".equalsIgnoreCase(var3.getName())) {
            int var4 = Integer.parseInt(var3.attributeValue("id"));
            int var5 = Integer.parseInt(var3.attributeValue("level"));
            int var6 = Integer.parseInt(var3.attributeValue("mp_consume"));
            int var7 = Integer.parseInt(var3.attributeValue("success_rate"));
            int var8 = Integer.parseInt(var3.attributeValue("item_id"));
            ItemTemplate var9 = ItemHolder.getInstance().getTemplate(var8);
            Recipe.ERecipeType var10 = Boolean.parseBoolean(var3.attributeValue("is_common")) ? Recipe.ERecipeType.ERT_COMMON : Recipe.ERecipeType.ERT_DWARF;
            ArrayList var11 = new ArrayList();
            ArrayList var12 = new ArrayList();
            ArrayList var13 = new ArrayList();
            Iterator var14 = var3.elementIterator();

            while (var14.hasNext()) {
               Element var15 = (Element)var14.next();
               if ("materials".equalsIgnoreCase(var15.getName())) {
                  Iterator var21 = var15.elementIterator();

                  while (var21.hasNext()) {
                     Element var23 = (Element)var21.next();
                     Pair var25 = this.a(var23);
                     if (var25 != null) {
                        var11.add(var25);
                     }
                  }
               } else if ("products".equalsIgnoreCase(var15.getName())) {
                  Iterator var20 = var15.elementIterator();

                  while (var20.hasNext()) {
                     Element var22 = (Element)var20.next();
                     Pair var24 = this.b(var22);
                     if (var24 != null) {
                        var12.add(var24);
                     }
                  }
               } else if ("npc_fee".equalsIgnoreCase(var15.getName())) {
                  Iterator var16 = var15.elementIterator();

                  while (var16.hasNext()) {
                     Element var17 = (Element)var16.next();
                     Pair var18 = this.a(var17);
                     if (var18 != null) {
                        var13.add(var18);
                     }
                  }
               }
            }

            if (var9 == null) {
               LOG.warn("Skip recipe " + var4);
            } else if (var12.isEmpty()) {
               LOG.warn("Recipe " + var4 + " have empty product list. Skip");
            } else if (var11.isEmpty()) {
               LOG.warn("Recipe " + var4 + " have empty material list. Skip");
            } else {
               Recipe var19 = new Recipe(
                  var4,
                  var9,
                  var10,
                  var5,
                  var6,
                  var7,
                  Collections.unmodifiableList(var11),
                  Collections.unmodifiableList(var12),
                  Collections.unmodifiableList(var13)
               );
               this.getHolder().addRecipe(var19);
            }
         }
      }
   }

   private Pair<ItemTemplate, Long> a(Element var1) {
      if (!"item".equalsIgnoreCase(var1.getName())) {
         return null;
      }

      int var2 = Integer.parseInt(var1.attributeValue("id"));
      ItemTemplate var3 = ItemHolder.getInstance().getTemplate(var2);
      if (var1 == null) {
         return null;
      }

      long var4 = Long.parseLong(var1.attributeValue("count"));
      return Pair.of(var3, var4);
   }

   private Pair<ItemTemplate, Pair<Long, Double>> b(Element var1) {
      if (!"item".equalsIgnoreCase(var1.getName())) {
         return null;
      }

      int var2 = Integer.parseInt(var1.attributeValue("id"));
      ItemTemplate var3 = ItemHolder.getInstance().getTemplate(var2);
      if (var1 == null) {
         return null;
      }

      long var4 = Long.parseLong(var1.attributeValue("count"));
      double var6 = Double.parseDouble(var1.attributeValue("chance", "-1"));
      return Pair.of(var3, Pair.of(var4, var6));
   }
}
