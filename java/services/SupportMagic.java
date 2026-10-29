package services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.tables.SkillTable;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SupportMagic extends Functions implements ScriptFile {
   private static final Logger LOG = LoggerFactory.getLogger(SupportMagic.class);
   private static final File bYf = new File(Config.DATAPACK_ROOT, "data/newbie_buffs.xml");
   private static List<SupportMagic.NewbieBuffsList> bYg;

   public void getSupportMagic() {
      Player var1 = this.getSelf();
      NpcInstance var2 = this.getNpc();
      doSupportMagic(var2, var1, false);
   }

   public void getSupportServitorMagic() {
      Player var1 = this.getSelf();
      NpcInstance var2 = this.getNpc();
      doSupportMagic(var2, var1, true);
   }

   public void getProtectionBlessing() {
      Player var1 = this.getSelf();
      NpcInstance var2 = this.getNpc();
      if (var1.getKarma() <= 0) {
         if (var1.getLevel() <= 24 && var1.getClassId().getLevel() < 3) {
            var2.doCast(SkillTable.getInstance().getInfo(5182, 1), var1, true);
         } else {
            show("newbiehelper/pk_protect002.htm", var1, var2);
         }
      }
   }

   private static boolean q(Player var0, String var1) {
      if (var0.isSharedGroupDisabled(-99999)) {
         var0.sendPacket(new SystemMessage(48).addString(var1));
         return false;
      } else {
         TimeStamp var2 = new TimeStamp(-99999, System.currentTimeMillis() + Config.ALT_NPC_BUFFER_REUSE_DELAY, Config.ALT_NPC_BUFFER_REUSE_DELAY);
         var0.addSharedGroupReuse(-99999, var2);
         return true;
      }
   }

   public static void doSupportMagic(NpcInstance var0, Player var1, boolean var2) {
      if (!var1.isCursedWeaponEquipped()) {
         if (q(var1, var0.getName())) {
            for (SupportMagic.NewbieBuffsList var4 : bYg) {
               if (var4.getType().canUse(var1)) {
                  int var5 = var1.getLevel();
                  if (var5 < var4.getMinLevel()) {
                     show("newbiehelper/guide_for_newbie002.htm", var1, var0, "%min_level%", var4.getMinLevel());
                     return;
                  }

                  if (var5 > var4.getMaxLevel()) {
                     show("newbiehelper/guide_for_newbie003.htm", var1, var0, "%max_level%", var4.getMaxLevel());
                     return;
                  }

                  var4.apply(var0, var1);
                  return;
               }
            }
         }
      }
   }

   private static SupportMagic.NewbieBuffsList a(SupportMagic.NewbieBuffsListType var0, Node var1) {
      int var2 = Integer.parseInt(var1.getAttributes().getNamedItem("max_level").getNodeValue());
      int var3 = var2;
      ArrayList var4 = new ArrayList();

      for (Node var5 = var1.getFirstChild(); var5 != null; var5 = var5.getNextSibling()) {
         if ("buff".equalsIgnoreCase(var5.getNodeName())) {
            NamedNodeMap var6 = var5.getAttributes();
            int var7 = Integer.parseInt(var6.getNamedItem("skill_id").getNodeValue());
            int var8 = Integer.parseInt(var6.getNamedItem("skill_level").getNodeValue());
            int var9 = Integer.parseInt(var6.getNamedItem("min_level").getNodeValue());
            int var10 = Integer.parseInt(var6.getNamedItem("max_level").getNodeValue());
            if (var9 < var3) {
               var3 = var9;
            }

            if (var10 >= var2) {
               var2 = var10;
            }

            Pair var11 = Pair.of(Pair.of(var9, var10), SkillTable.getInstance().getInfo(var7, var8));
            var4.add(var11);
         }
      }

      return new SupportMagic.NewbieBuffsList(var0, var3, var2, var4);
   }

   private static List<SupportMagic.NewbieBuffsList> a(Document var0) {
      ArrayList var1 = new ArrayList();

      for (Node var2 = var0.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         if ("list".equalsIgnoreCase(var2.getNodeName())) {
            for (Node var3 = var2.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
               if ("warrior".equalsIgnoreCase(var3.getNodeName())) {
                  SupportMagic.NewbieBuffsList var4 = a(SupportMagic.NewbieBuffsListType.WARRIOR, var3);
                  var1.add(var4);
               } else if ("mage".equalsIgnoreCase(var3.getNodeName())) {
                  SupportMagic.NewbieBuffsList var5 = a(SupportMagic.NewbieBuffsListType.MAGE, var3);
                  var1.add(var5);
               }
            }
         }
      }

      return Collections.unmodifiableList(var1);
   }

   private static List<SupportMagic.NewbieBuffsList> b(File var0) {
      Document var1;
      try {
         DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
         var2.setValidating(false);
         var2.setIgnoringComments(true);
         var1 = var2.newDocumentBuilder().parse(var0);
      } catch (Exception var6) {
         LOG.error("Error loading file " + var0, var6);
         return Collections.emptyList();
      }

      try {
         List var8 = a(var1);
         int var3 = 0;

         for (Object __raw_var5_155 : (Iterable<?>)(Object)(var8)) {
            SupportMagic.NewbieBuffsList var5 = (SupportMagic.NewbieBuffsList)__raw_var5_155;
            var3 += var5.getBuffs().size();
         }

         LOG.info("SupportMagic: Loaded " + var3 + " newbie buff(s).");
         return var8;
      } catch (Exception var7) {
         LOG.error("Error in file " + var0, var7);
         return Collections.emptyList();
      }
   }

   @Override
   public void onLoad() {
      bYg = b(bYf);
   }

   @Override
   public void onReload() {
      this.onLoad();
   }

   @Override
   public void onShutdown() {
   }

   private static class NewbieBuffsList {
      private final SupportMagic.NewbieBuffsListType bYh;
      private final int p;
      private final int s;
      private final List<Pair<Pair<Integer, Integer>, Skill>> bWx;

      private NewbieBuffsList(SupportMagic.NewbieBuffsListType var1, int var2, int var3, List<Pair<Pair<Integer, Integer>, Skill>> var4) {
         this.bYh = var1;
         this.p = var2;
         this.s = var3;
         this.bWx = var4;
      }

      public SupportMagic.NewbieBuffsListType getType() {
         return this.bYh;
      }

      public int getMinLevel() {
         return this.p;
      }

      public int getMaxLevel() {
         return this.s;
      }

      public List<Pair<Pair<Integer, Integer>, Skill>> getBuffs() {
         return this.bWx;
      }

      public void apply(Creature var1, Creature var2) {
         int var3 = var2.getLevel();

         for (Pair var5 : this.getBuffs()) {
            Pair var6 = (Pair)var5.getKey();
            if (var3 >= (Integer)var6.getLeft() && var3 <= (Integer)var6.getRight()) {
               Skill var7 = (Skill)var5.getValue();
               var1.broadcastPacket(new MagicSkillUse(var1, var2, var7, 0, 0L));
               var1.callSkill(var7, Collections.singletonList(var2), true);
            }
         }
      }
   }

   private enum NewbieBuffsListType {
      WARRIOR {
         @Override
         public boolean canUse(Player var1) {
            return !var1.isMageClass() || var1.getTemplate().race == Race.orc;
         }
      },
      MAGE {
         @Override
         public boolean canUse(Player var1) {
            return var1.isMageClass() && var1.getTemplate().race != Race.orc;
         }
      };

      NewbieBuffsListType() {
      }

      public abstract boolean canUse(Player var1);
   }
}
