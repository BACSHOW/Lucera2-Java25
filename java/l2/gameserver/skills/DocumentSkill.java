package l2.gameserver.skills;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;
import l2.gameserver.data.xml.holder.EnchantSkillHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.templates.SkillEnchant;
import l2.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@Deprecated
public final class DocumentSkill extends DocumentBase {
   private static final Logger _log = LoggerFactory.getLogger(DocumentSkill.class);
   private static final String aoj = "enchant";
   private static final Comparator<Integer> aok = new Comparator<Integer>() {
      public int compare(Integer var1, Integer var2) {
         return var1 - var2;
      }
   };
   protected Map<String, Map<Integer, Object>> tables = new LinkedHashMap<>();
   private DocumentSkill.SkillLoad aol = null;
   private Set<String> aom = new HashSet<>();
   private List<Skill> aon = new LinkedList<>();

   DocumentSkill(File var1) {
      super(var1);
   }

   protected void resetTable() {
      if (!this.aom.isEmpty()) {
         for (String var2 : this.tables.keySet()) {
            if (!this.aom.contains(var2)) {
               _log.warn("Unused table " + var2 + " for skill " + this.aol.id);
            }
         }
      }

      this.aom.clear();
      this.tables.clear();
   }

   private void a(DocumentSkill.SkillLoad var1) {
      this.aol = var1;
   }

   protected List<Skill> getSkills() {
      return this.aon;
   }

   @Override
   protected Object getTableValue(String var1) {
      Map var2 = this.tables.get(var1);
      if (var2 == null) {
         _log.error("No table " + var1 + " for skill " + this.aol.id);
         return 0;
      } else if (!var2.containsKey(this.aol.currentLevel)) {
         _log.error("No value in table " + var1 + " for skill " + this.aol.id + " at level " + this.aol.currentLevel);
         return 0;
      } else {
         this.aom.add(var1);
         return var2.get(this.aol.currentLevel);
      }
   }

   @Override
   protected Object getTableValue(String var1, int var2) {
      Map var3 = this.tables.get(var1);
      if (var3 == null) {
         _log.error("No table " + var1 + " for skill " + this.aol.id);
         return 0;
      } else if (!var3.containsKey(var2)) {
         _log.error("No value in table " + var1 + " for skill " + this.aol.id + " at level " + var2);
         return 0;
      } else {
         this.aom.add(var1);
         return var3.get(var2);
      }
   }

   @Override
   protected void parseDocument(Document var1) {
      for (Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         if ("list".equalsIgnoreCase(var2.getNodeName())) {
            for (Node var3 = var2.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
               if ("skill".equalsIgnoreCase(var3.getNodeName())) {
                  this.parseSkill(var3);
                  this.aon.addAll(this.aol.skills);
                  this.resetTable();
               }
            }
         } else if ("skill".equalsIgnoreCase(var2.getNodeName())) {
            this.parseSkill(var2);
            this.aon.addAll(this.aol.skills);
         }
      }
   }

   private void a(Node var1, int var2, int var3) {
      NamedNodeMap var4 = var1.getAttributes();
      String var5 = var4.getNamedItem("name").getNodeValue();
      Object[] var6 = this.a(this.parseTable(var1), var3);
      Map var7 = this.tables.get(var5);
      if (var7 == null) {
         var7 = new TreeMap<>(aok);
         this.tables.put(var5, var7);
      }

      for (int var8 = 0; var8 < var6.length; var8++) {
         int var9 = var2 + var8;
         if (var7.containsKey(var9)) {
            _log.warn("Duplicate skill level " + var9 + " in table " + var5 + " in skill " + this.aol.id + ", skipping duplicate");
            continue;
         }

         var7.put(var9, var6[var8]);
      }
   }

   protected void parseSkill(Node var1) {
      NamedNodeMap var2 = var1.getAttributes();
      int var3 = Integer.parseInt(var2.getNamedItem("id").getNodeValue());
      String var4 = var2.getNamedItem("name").getNodeValue();
      int var5 = Integer.parseInt(var2.getNamedItem("levels").getNodeValue());
      this.a(new DocumentSkill.SkillLoad(var3, var4));
      ArrayList var6 = new ArrayList();

      try {
         for (int var7 = 1; var7 <= var5; var7++) {
            var6.add(var7);
         }

         Node var21 = var1.cloneNode(true);
         int var8 = 0;

         for (int var9 = var21.getChildNodes().getLength(); var8 < var9; var8++) {
            int var10 = 0;
            Node var11 = var21.getChildNodes().item(var8);
            String var12 = var11.getNodeName();
            if (var12.startsWith("enchant")) {
               int var13;
               try {
                  var13 = Integer.parseInt(var12.substring("enchant".length()));
               } catch (NumberFormatException var19) {
                  _log.error("Wrong enchant " + var12 + " in skill " + var3);
                  break;
               }

               int var14 = EnchantSkillHolder.getInstance().getFirstSkillLevelOf(var3, var13);
               Node var15 = var11.getAttributes().getNamedItem("levels");
               if (var15 != null) {
                  var10 = Integer.parseInt(var15.getNodeValue());
               } else {
                  _log.warn("Skill " + var3 + " have no enchant levels in route " + var13 + ".");
                  var10 = EnchantSkillHolder.getInstance().getMaxEnchantLevelOf(var3);
               }

               int var16 = EnchantSkillHolder.getInstance().getMaxEnchantLevelOf(var3);
               if (var10 != var16) {
                  _log.warn("Unknown enchant levels " + var10 + " for skill " + var3 + ". Actual " + var16);
               }

               for (int var17 = 1; var17 <= var10; var17++) {
                  SkillEnchant var18 = EnchantSkillHolder.getInstance().getSkillEnchant(var3, var13, var17);
                  if (var18 == null) {
                     _log.error("No enchant level " + var17 + " in route " + var13 + " for skill " + var3);
                     break;
                  }

                  var6.add(var18.getSkillLevel());
               }

               for (Node var48 = var11.getFirstChild(); var48 != null; var48 = var48.getNextSibling()) {
                  if ("table".equalsIgnoreCase(var48.getNodeName())) {
                     this.a(var48, var14, var10);
                  } else if (var48.getNodeType() == 1) {
                     _log.error("Unknown element of enchant \"" + var48.getNodeName() + "\" in skill " + var3);
                  }
               }
            }
         }

         for (Node var22 = var1.getFirstChild(); var22 != null; var22 = var22.getNextSibling()) {
            if ("table".equalsIgnoreCase(var22.getNodeName())) {
               this.a(var22, 1, var5);
            }
         }

         for (Entry var28 : this.tables.entrySet()) {
            Map var34 = (Map)var28.getValue();
            Object var39 = var34.get(var5);

            for (Object __raw_var44_207 : (Iterable<?>)(Object)(var6)) {
               Integer var44 = (Integer)__raw_var44_207;
               if (var44 > var5 && !var34.containsKey(var44)) {
                  var34.put(var44, var39);
               }
            }
         }

         for (Object __raw_var29_214 : (Iterable<?>)(Object)(var6)) {
            Integer var29 = (Integer)__raw_var29_214;
            StatsSet var35 = new StatsSet();
            var35.set("skill_id", this.aol.id);
            var35.set("level", var29);
            var35.set("name", this.aol.name);
            var35.set("base_level", var5);
            this.aol.sets.put(var29, var35);
         }

         for (Object __raw_var30_223 : (Iterable<?>)(Object)(var6)) {
            Integer var30 = (Integer)__raw_var30_223;
            for (Node var36 = var1.getFirstChild(); var36 != null; var36 = var36.getNextSibling()) {
               if ("set".equalsIgnoreCase(var36.getNodeName())) {
                  StatsSet var40 = this.aol.sets.get(var30);
                  this.aol.currentLevel = var30;
                  this.parseBeanSet(var36, var40, var30);
               }
            }
         }

         for (StatsSet var31 : this.aol.sets.values()) {
            Skill var37 = var31.getEnum("skillType", Skill.SkillType.class).makeSkill(var31);
            this.aol.currentSkills.put(var37.getLevel(), var37);
         }

         for (Object __raw_var32_238 : (Iterable<?>)(Object)(var6)) {
            Integer var32 = (Integer)__raw_var32_238;
            this.aol.currentLevel = var32;
            Skill var38 = this.aol.currentSkills.get(var32);
            if (var38 == null) {
               _log.error("Undefined skill id " + var3 + " level " + var32);
               return;
            }

            var38.setDisplayLevel(var32);

            for (Node var41 = var1.getFirstChild(); var41 != null; var41 = var41.getNextSibling()) {
               String var43 = var41.getNodeName();
               if ("cond".equalsIgnoreCase(var43)) {
                  Condition var45 = this.parseCondition(var41.getFirstChild());
                  if (var45 != null) {
                     Node var46 = var41.getAttributes().getNamedItem("msgId");
                     if (var46 != null) {
                        int var47 = this.parseNumber(var46.getNodeValue()).intValue();
                        var45.setSystemMsg(var47);
                     }

                     var38.attach(var45);
                  }
               } else if ("for".equalsIgnoreCase(var43)) {
                  this.parseTemplate(var41, var38);
               } else if ("triggers".equalsIgnoreCase(var43)) {
                  this.parseTrigger(var41, var38);
               }
            }
         }

         this.aol.skills.addAll(this.aol.currentSkills.values());
      } catch (Exception var20) {
         _log.error("Error loading skill " + var3, var20);
      }
   }

   protected Object[] parseTable(Node var1) {
      NamedNodeMap var2 = var1.getAttributes();
      String var3 = var2.getNamedItem("name").getNodeValue();
      if (var3.charAt(0) != '#') {
         throw new IllegalArgumentException("Table name must start with #");
      }

      StringTokenizer var4 = new StringTokenizer(var1.getFirstChild().getNodeValue());
      ArrayList var5 = new ArrayList();

      while (var4.hasMoreTokens()) {
         var5.add(var4.nextToken());
      }

      return var5.toArray(new Object[var5.size()]);
   }

   private Object[] a(Object[] var1, int var2) {
      if (var1.length < var2) {
         Object[] var3 = new Object[var2];
         System.arraycopy(var1, 0, var3, 0, var1.length);
         var1 = var3;
      }

      for (int var4 = 1; var4 < var2; var4++) {
         if (var1[var4] == null) {
            var1[var4] = var1[var4 - 1];
         }
      }

      return var1;
   }

   public class SkillLoad {
      public final int id;
      public final String name;
      public final Map<Integer, StatsSet> sets;
      public final List<Skill> skills;
      public final Map<Integer, Skill> currentSkills;
      public int currentLevel;

      public SkillLoad(int var2, String var3) {
         this.id = var2;
         this.name = var3;
         this.sets = new TreeMap<>(DocumentSkill.aok);
         this.skills = new ArrayList<>();
         this.currentSkills = new TreeMap<>(DocumentSkill.aok);
      }
   }
}
