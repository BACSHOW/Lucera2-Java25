package l2.gameserver.data.xml.parser;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.entity.residence.ResidenceFunction;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.support.MerchantGuard;
import l2.gameserver.utils.Location;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.napile.primitive.sets.impl.HashIntSet;

public final class ResidenceParser extends AbstractDirParser<ResidenceHolder> {
   private static ResidenceParser _instance = new ResidenceParser();

   public static ResidenceParser getInstance() {
      return _instance;
   }

   private ResidenceParser() {
      super(ResidenceHolder.getInstance());
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/residences/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "residence.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      String var2 = var1.attributeValue("impl");
      Class var3 = null;
      StatsSet var4 = new StatsSet();
      Iterator var5 = var1.attributeIterator();

      while (var5.hasNext()) {
         Attribute var6 = (Attribute)var5.next();
         var4.set(var6.getName(), var6.getValue());
      }

      Residence var27 = null;

      try {
         var3 = Class.forName("l2.gameserver.model.entity.residence." + var2);
         Constructor var29;
         try {
            var29 = var3.getConstructor(StatsSet.class);
         } catch (NoSuchMethodException e) {
            var29 = var3.getDeclaredConstructor(StatsSet.class);
         }
         var29.setAccessible(true);
         var27 = (Residence)var29.newInstance(var4);
         this.getHolder().addResidence(var27);
      } catch (Exception var25) {
         this.error("fail to init: " + this.getCurrentFileName(), var25);
         return;
      }

      Iterator var30 = var1.elementIterator();

      while (var30.hasNext()) {
         Element var7 = (Element)var30.next();
         String var8 = var7.getName();
         int var9 = var7.attributeValue("level") == null ? 0 : Integer.valueOf(var7.attributeValue("level"));
         int var10 = (int)((var7.attributeValue("lease") == null ? 0 : Integer.valueOf(var7.attributeValue("lease"))) * Config.RESIDENCE_LEASE_FUNC_MULTIPLIER);
         int var11 = var7.attributeValue("npcId") == null ? 0 : Integer.valueOf(var7.attributeValue("npcId"));
         int var12 = var7.attributeValue("listId") == null ? 0 : Integer.valueOf(var7.attributeValue("listId"));
         ResidenceFunction var13 = null;
         if (var8.equalsIgnoreCase("teleport")) {
            var13 = this.checkAndGetFunction(var27, 1);
            ArrayList<TeleportLocation> var36 = new ArrayList<TeleportLocation>();
            Iterator var42 = var7.elementIterator();

            while (var42.hasNext()) {
               Element var44 = (Element)var42.next();
               if ("target".equalsIgnoreCase(var44.getName())) {
                  String var46 = var44.attributeValue("name");
                  long var48 = Long.parseLong(var44.attributeValue("price"));
                  int var49 = var44.attributeValue("item") == null ? 57 : Integer.parseInt(var44.attributeValue("item"));
                  TeleportLocation var50 = new TeleportLocation(var49, var48, var46, 0, 0);
                  var50.set(Location.parseLoc(var44.attributeValue("loc")));
                  var36.add(var50);
               }
            }

            var13.addTeleports(var9, var36.toArray(new TeleportLocation[var36.size()]));
         } else if (var8.equalsIgnoreCase("support")) {
            var13 = this.checkAndGetFunction(var27, 6);
            var13.addBuffs(var9);
         } else if (var8.equalsIgnoreCase("item_create")) {
            var13 = this.checkAndGetFunction(var27, 2);
            var13.addBuylist(var9, new int[]{var11, var12});
         } else if (var8.equalsIgnoreCase("curtain")) {
            var13 = this.checkAndGetFunction(var27, 7);
         } else if (var8.equalsIgnoreCase("platform")) {
            var13 = this.checkAndGetFunction(var27, 8);
         } else if (var8.equalsIgnoreCase("restore_exp")) {
            var13 = this.checkAndGetFunction(var27, 5);
         } else if (var8.equalsIgnoreCase("restore_hp")) {
            var13 = this.checkAndGetFunction(var27, 3);
         } else if (var8.equalsIgnoreCase("restore_mp")) {
            var13 = this.checkAndGetFunction(var27, 4);
         } else if (var8.equalsIgnoreCase("skills")) {
            Iterator var35 = var7.elementIterator();

            while (var35.hasNext()) {
               Element var41 = (Element)var35.next();
               int var43 = Integer.parseInt(var41.attributeValue("id"));
               int var45 = Integer.parseInt(var41.attributeValue("level"));
               Skill var47 = SkillTable.getInstance().getInfo(var43, var45);
               if (var47 != null) {
                  var27.addSkill(var47);
               }
            }
         } else if (var8.equalsIgnoreCase("banish_points")) {
            Iterator var34 = var7.elementIterator();

            while (var34.hasNext()) {
               Location var40 = Location.parse((Element)var34.next());
               var27.addBanishPoint(var40);
            }
         } else if (var8.equalsIgnoreCase("owner_restart_points")) {
            Iterator var33 = var7.elementIterator();

            while (var33.hasNext()) {
               Location var39 = Location.parse((Element)var33.next());
               var27.addOwnerRestartPoint(var39);
            }
         } else if (var8.equalsIgnoreCase("other_restart_points")) {
            Iterator var32 = var7.elementIterator();

            while (var32.hasNext()) {
               Location var38 = Location.parse((Element)var32.next());
               var27.addOtherRestartPoint(var38);
            }
         } else if (var8.equalsIgnoreCase("chaos_restart_points")) {
            Iterator var31 = var7.elementIterator();

            while (var31.hasNext()) {
               Location var37 = Location.parse((Element)var31.next());
               var27.addChaosRestartPoint(var37);
            }
         } else if (var8.equalsIgnoreCase("merchant_guards")) {
            Iterator var14 = var7.elementIterator();

            while (var14.hasNext()) {
               Element var15 = (Element)var14.next();
               int var16 = Integer.parseInt(var15.attributeValue("item_id"));
               int var17 = Integer.parseInt(var15.attributeValue("npc_id"));
               int var18 = Integer.parseInt(var15.attributeValue("max"));
               HashIntSet var19 = new HashIntSet(3);
               String[] var20 = var15.attributeValue("ssq").split(";");

               for (String var24 : var20) {
                  if (var24.equalsIgnoreCase("cabal_null")) {
                     var19.add(0);
                  } else if (var24.equalsIgnoreCase("cabal_dusk")) {
                     var19.add(1);
                  } else if (var24.equalsIgnoreCase("cabal_dawn")) {
                     var19.add(2);
                  } else {
                     this.error("Unknown ssq type: " + var24 + "; file: " + this.getCurrentFileName());
                  }
               }

               if (var27 instanceof Castle) {
                  ((Castle)var27).addMerchantGuard(new MerchantGuard(var16, var17, var18, var19));
               }
            }
         }

         if (var13 != null) {
            var13.addLease(var9, var10);
         }
      }
   }

   private ResidenceFunction checkAndGetFunction(Residence var1, int var2) {
      ResidenceFunction var3 = var1.getFunction(var2);
      if (var3 == null) {
         var3 = new ResidenceFunction(var1.getId(), var2);
         var1.addFunction(var3);
      }

      return var3;
   }
}
