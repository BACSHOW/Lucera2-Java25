package l2.gameserver.data.xml.parser;

import gnu.trove.TIntObjectHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.ClassType2;
import org.dom4j.Element;

public final class SkillAcquireParser extends AbstractDirParser<SkillAcquireHolder> {
   private static final SkillAcquireParser xA = new SkillAcquireParser();

   public static SkillAcquireParser getInstance() {
      return xA;
   }

   protected SkillAcquireParser() {
      super(SkillAcquireHolder.getInstance());
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/skill_tree/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "tree.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator("pledge_skill_tree");

      while (var2.hasNext()) {
         this.getHolder().addAllPledgeLearns(this.c((Element)var2.next()));
      }

      var2 = var1.elementIterator("fishing_skill_tree");

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         Iterator var4 = var3.elementIterator("race");

         while (var4.hasNext()) {
            Element var5 = (Element)var4.next();
            int var6 = Integer.parseInt(var5.attributeValue("id"));
            List var7 = this.c(var5);
            this.getHolder().addAllFishingLearns(var6, var7);
         }
      }

      var2 = var1.elementIterator("normal_skill_tree");

      while (var2.hasNext()) {
         TIntObjectHashMap var14 = new TIntObjectHashMap();
         Element var15 = (Element)var2.next();
         Iterator var16 = var15.elementIterator("class");

         while (var16.hasNext()) {
            Element var17 = (Element)var16.next();
            int var18 = Integer.parseInt(var17.attributeValue("id"));
            List var8 = this.c(var17);
            var14.put(var18, var8);
         }

         this.getHolder().addAllNormalSkillLearns(var14);
      }

      var2 = var1.elementIterator("nobles_skill_tree");

      while (var2.hasNext()) {
         this.getHolder().addAllNoblesseLearns(this.c((Element)var2.next()));
      }

      var2 = var1.elementIterator("hero_skill_tree");

      while (var2.hasNext()) {
         this.getHolder().addAllHeroLearns(this.c((Element)var2.next()));
      }

      var2 = var1.elementIterator("clan_leader_skill_tree");

      while (var2.hasNext()) {
         this.getHolder().addAllClanLeaderLearns(this.c((Element)var2.next()));
      }
   }

   private List<SkillLearn> c(Element var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.elementIterator("skill");

      while (var3.hasNext()) {
         Element var4 = (Element)var3.next();
         int var5 = Integer.parseInt(var4.attributeValue("id"));
         int var6 = Integer.parseInt(var4.attributeValue("level"));
         int var7 = var4.attributeValue("cost") == null ? 0 : Integer.parseInt(var4.attributeValue("cost"));
         int var8 = Integer.parseInt(var4.attributeValue("min_level"));
         int var9 = var4.attributeValue("item_id") == null ? 0 : Integer.parseInt(var4.attributeValue("item_id"));
         long var10 = var4.attributeValue("item_count") == null ? 1L : Long.parseLong(var4.attributeValue("item_count"));
         boolean var12 = var4.attributeValue("clicked") != null && Boolean.parseBoolean(var4.attributeValue("clicked"));
         boolean var13 = Boolean.parseBoolean(var4.attributeValue("auto_learn", "true"));
         ClassType2 var14 = ClassType2.valueOf(var4.attributeValue("classtype2", "None"));
         var2.add(new SkillLearn(var5, var6, var8, var7 * Config.SKILL_COST_RATE, var9, var10, var12, var14, var13));
      }

      return var2;
   }
}
