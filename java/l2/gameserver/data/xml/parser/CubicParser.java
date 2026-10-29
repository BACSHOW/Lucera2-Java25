package l2.gameserver.data.xml.parser;

import gnu.trove.TIntIntHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.CubicHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.CubicTemplate;
import org.dom4j.Element;

public final class CubicParser extends AbstractFileParser<CubicHolder> {
   private static CubicParser xk = new CubicParser();

   public static CubicParser getInstance() {
      return xk;
   }

   protected CubicParser() {
      super(CubicHolder.getInstance());
   }

   @Override
   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/cubics.xml");
   }

   @Override
   public String getDTDFileName() {
      return "cubics.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var3 = var1.elementIterator();

      while (var3.hasNext()) {
         Element var4 = (Element)var3.next();
         int var5 = Integer.parseInt(var4.attributeValue("id"));
         int var6 = Integer.parseInt(var4.attributeValue("level"));
         int var7 = Integer.parseInt(var4.attributeValue("delay"));
         CubicTemplate var8 = new CubicTemplate(var5, var6, var7);
         this.getHolder().addCubicTemplate(var8);
         Iterator var9 = var4.elementIterator();

         while (var9.hasNext()) {
            Element var10 = (Element)var9.next();
            int var11 = Integer.parseInt(var10.attributeValue("chance"));
            ArrayList var12 = new ArrayList(1);
            Iterator var13 = var10.elementIterator();

            while (var13.hasNext()) {
               Element var14 = (Element)var13.next();
               int var15 = Integer.parseInt(var14.attributeValue("id"));
               int var16 = Integer.parseInt(var14.attributeValue("level"));
               String var2 = var14.attributeValue("chance");
               int var17 = var2 == null ? 0 : Integer.parseInt(var2);
               boolean var18 = Boolean.parseBoolean(var14.attributeValue("can_attack_door"));
               var2 = var14.attributeValue("min_hp");
               int var19 = var2 == null ? 0 : Integer.parseInt(var2);
               var2 = var14.attributeValue("min_hp_per");
               int var20 = var2 == null ? 0 : Integer.parseInt(var2);
               CubicTemplate.ActionType var21 = CubicTemplate.ActionType.valueOf(var14.attributeValue("action_type"));
               TIntIntHashMap var22 = new TIntIntHashMap();
               Iterator var23 = var14.elementIterator();

               while (var23.hasNext()) {
                  Element var24 = (Element)var23.next();
                  int var25 = Integer.parseInt(var24.attributeValue("min"));
                  int var26 = Integer.parseInt(var24.attributeValue("max"));
                  int var27 = Integer.parseInt(var24.attributeValue("value"));

                  for (int var28 = var25; var28 <= var26; var28++) {
                     var22.put(var28, var27);
                  }
               }

               if (var17 == 0 && var22.isEmpty()) {
                  this.warn("Wrong skill chance. Cubic: " + var5 + "/" + var6);
               }

               Skill var31 = SkillTable.getInstance().getInfo(var15, var16);
               if (var31 != null) {
                  var31.setCubicSkill(true);
                  var12.add(new CubicTemplate.SkillInfo(var31, var17, var21, var18, var19, var20, var22));
               }
            }

            var8.putSkills(var11, var12);
         }
      }
   }
}
