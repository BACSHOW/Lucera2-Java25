package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractDirParser;
import l2.commons.geometry.Polygon;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.DoorHolder;
import l2.gameserver.templates.DoorTemplate;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.Location;
import org.dom4j.Element;

public final class DoorParser extends AbstractDirParser<DoorHolder> {
   private static final DoorParser xm = new DoorParser();

   public static DoorParser getInstance() {
      return xm;
   }

   protected DoorParser() {
      super(DoorHolder.getInstance());
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/doors/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "doors.dtd";
   }

   private StatsSet bp() {
      StatsSet var1 = new StatsSet();
      var1.set("level", 0);
      var1.set("baseSTR", 0);
      var1.set("baseCON", 0);
      var1.set("baseDEX", 0);
      var1.set("baseINT", 0);
      var1.set("baseWIT", 0);
      var1.set("baseMEN", 0);
      var1.set("baseShldDef", 0);
      var1.set("baseShldRate", 0);
      var1.set("baseAccCombat", 38);
      var1.set("baseEvasRate", 38);
      var1.set("baseCritRate", 38);
      var1.set("baseAtkRange", 0);
      var1.set("baseMpMax", 0);
      var1.set("baseCpMax", 0);
      var1.set("basePAtk", 0);
      var1.set("baseMAtk", 0);
      var1.set("basePAtkSpd", 0);
      var1.set("baseMAtkSpd", 0);
      var1.set("baseWalkSpd", 0);
      var1.set("baseRunSpd", 0);
      var1.set("baseHpReg", 0);
      var1.set("baseCpReg", 0);
      var1.set("baseMpReg", 0);
      return var1;
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         if ("door".equals(var3.getName())) {
            StatsSet var4 = this.bp();
            StatsSet var5 = null;
            var4.set("door_type", var3.attributeValue("type"));
            Element var6 = var3.element("pos");
            int var8 = Integer.parseInt(var6.attributeValue("x"));
            int var9 = Integer.parseInt(var6.attributeValue("y"));
            int var10 = Integer.parseInt(var6.attributeValue("z"));
            Location var7;
            var4.set("pos", var7 = new Location(var8, var9, var10));
            Polygon var11 = new Polygon();
            int var12 = 0;
            int var13 = 0;
            Element var14 = var3.element("shape");
            var12 = Integer.parseInt(var14.attributeValue("minz"));
            var13 = Integer.parseInt(var14.attributeValue("maxz"));
            var11.add(Integer.parseInt(var14.attributeValue("ax")), Integer.parseInt(var14.attributeValue("ay")));
            var11.add(Integer.parseInt(var14.attributeValue("bx")), Integer.parseInt(var14.attributeValue("by")));
            var11.add(Integer.parseInt(var14.attributeValue("cx")), Integer.parseInt(var14.attributeValue("cy")));
            var11.add(Integer.parseInt(var14.attributeValue("dx")), Integer.parseInt(var14.attributeValue("dy")));
            var11.setZmin(var12);
            var11.setZmax(var13);
            var4.set("shape", var11);
            var7.setZ(var12 + 32);
            Iterator var15 = var3.elementIterator();

            while (var15.hasNext()) {
               Element var16 = (Element)var15.next();
               if ("set".equals(var16.getName())) {
                  var4.set(var16.attributeValue("name"), var16.attributeValue("value"));
               } else if ("ai_params".equals(var16.getName())) {
                  if (var5 == null) {
                     var5 = new StatsSet();
                     var4.set("ai_params", var5);
                  }

                  Iterator var17 = var16.elementIterator();

                  while (var17.hasNext()) {
                     Element var18 = (Element)var17.next();
                     var5.set(var18.attributeValue("name"), var18.attributeValue("value"));
                  }
               }
            }

            var4.set("uid", var3.attributeValue("id"));
            var4.set("name", var3.attributeValue("name"));
            var4.set("baseHpMax", var3.attributeValue("hp"));
            var4.set("basePDef", var3.attributeValue("pdef"));
            var4.set("baseMDef", var3.attributeValue("mdef"));
            var4.set("collision_height", var13 - var12 & 65520);
            var4.set("collision_radius", Math.max(50, Math.min(var7.x - var11.getXmin(), var7.y - var11.getYmin())));
            DoorTemplate var21 = new DoorTemplate(var4);
            this.getHolder().addTemplate(var21);
         }
      }
   }
}
