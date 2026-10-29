package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import l2.commons.data.xml.AbstractDirParser;
import l2.commons.geometry.Circle;
import l2.commons.geometry.Polygon;
import l2.commons.geometry.Rectangle;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ZoneHolder;
import l2.gameserver.model.Territory;
import l2.gameserver.model.World;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.ZoneTemplate;
import l2.gameserver.utils.Location;
import org.dom4j.Element;

public class ZoneParser extends AbstractDirParser<ZoneHolder> {
   private static final ZoneParser xI = new ZoneParser();

   public static ZoneParser getInstance() {
      return xI;
   }

   protected ZoneParser() {
      super(ZoneHolder.getInstance());
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/zone/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "zone.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         StatsSet var3 = new StatsSet();
         Element var4 = (Element)var2.next();
         if ("zone".equals(var4.getName())) {
            var3.set("name", var4.attribute("name").getValue());
            var3.set("type", var4.attribute("type").getValue());
            Territory var5 = null;
            Iterator var7 = var4.elementIterator();

            while (var7.hasNext()) {
               Element var8 = (Element)var7.next();
               if ("set".equals(var8.getName())) {
                  var3.set(var8.attributeValue("name"), var8.attributeValue("val"));
               } else if ("restart_point".equals(var8.getName())) {
                  ArrayList var19 = new ArrayList();
                  Iterator var20 = var8.elementIterator();

                  while (var20.hasNext()) {
                     Element var21 = (Element)var20.next();
                     if ("coords".equalsIgnoreCase(var21.getName())) {
                        Location var22 = Location.parseLoc(var21.attribute("loc").getValue());
                        var19.add(var22);
                     }
                  }

                  var3.set("restart_points", var19);
               } else if (!"PKrestart_point".equals(var8.getName())) {
                  boolean var6;
                  if (!(var6 = "rectangle".equalsIgnoreCase(var8.getName())) && !"banned_rectangle".equalsIgnoreCase(var8.getName())) {
                     if (!(var6 = "circle".equalsIgnoreCase(var8.getName())) && !"banned_cicrcle".equalsIgnoreCase(var8.getName())) {
                        if ((var6 = "polygon".equalsIgnoreCase(var8.getName())) || "banned_polygon".equalsIgnoreCase(var8.getName())) {
                           Polygon var18 = parsePolygon(var8);
                           if (!var18.validate()) {
                              this.error("ZoneParser: invalid territory data : " + var18 + ", zone: " + var3.getString("name") + "!");
                           }

                           if (var5 == null) {
                              var5 = new Territory();
                              var3.set("territory", var5);
                           }

                           if (var6) {
                              var5.add(var18);
                           } else {
                              var5.addBanned(var18);
                           }
                        }
                     } else {
                        Circle var17 = parseCircle(var8);
                        if (var5 == null) {
                           var5 = new Territory();
                           var3.set("territory", var5);
                        }

                        if (var6) {
                           var5.add(var17);
                        } else {
                           var5.addBanned(var17);
                        }
                     }
                  } else {
                     Rectangle var16 = parseRectangle(var8);
                     if (var5 == null) {
                        var5 = new Territory();
                        var3.set("territory", var5);
                     }

                     if (var6) {
                        var5.add(var16);
                     } else {
                        var5.addBanned(var16);
                     }
                  }
               } else {
                  ArrayList var9 = new ArrayList();
                  Iterator var10 = var8.elementIterator();

                  while (var10.hasNext()) {
                     Element var11 = (Element)var10.next();
                     if ("coords".equalsIgnoreCase(var11.getName())) {
                        Location var12 = Location.parseLoc(var11.attribute("loc").getValue());
                        var9.add(var12);
                     }
                  }

                  var3.set("PKrestart_points", var9);
               }
            }

            if (var5 == null || var5.getTerritories().isEmpty()) {
               this.error("Empty territory for zone: " + var3.get("name"));
            }

            ZoneTemplate var15 = new ZoneTemplate(var3);
            this.getHolder().addTemplate(var15);
         }
      }
   }

   public static Rectangle parseRectangle(Element var0) throws Exception {
      int var5 = World.MAP_MIN_Z;
      int var6 = World.MAP_MAX_Z;
      Iterator var7 = var0.elementIterator();
      Element var8 = (Element)var7.next();
      String[] var9 = var8.attributeValue("loc").split("[\\s,;]+");
      int var1 = Integer.parseInt(var9[0]);
      int var2 = Integer.parseInt(var9[1]);
      if (var9.length > 2) {
         var5 = Integer.parseInt(var9[2]);
         var6 = Integer.parseInt(var9[3]);
      }

      var8 = (Element)var7.next();
      var9 = var8.attributeValue("loc").split("[\\s,;]+");
      int var3 = Integer.parseInt(var9[0]);
      int var4 = Integer.parseInt(var9[1]);
      if (var9.length > 2) {
         var5 = Integer.parseInt(var9[2]);
         var6 = Integer.parseInt(var9[3]);
      }

      Rectangle var10 = new Rectangle(var1, var2, var3, var4);
      var10.setZmin(var5);
      var10.setZmax(var6);
      return var10;
   }

   public static Polygon parsePolygon(Element var0) throws Exception {
      Polygon var1 = new Polygon();
      Iterator var2 = var0.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         if ("coords".equals(var3.getName())) {
            String[] var4 = var3.attributeValue("loc").split("[\\s,;]+");
            if (var4.length < 4) {
               var1.add(Integer.parseInt(var4[0]), Integer.parseInt(var4[1])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
            } else {
               var1.add(Integer.parseInt(var4[0]), Integer.parseInt(var4[1])).setZmin(Integer.parseInt(var4[2])).setZmax(Integer.parseInt(var4[3]));
            }
         }
      }

      return var1;
   }

   public static Circle parseCircle(Element var0) throws Exception {
      String[] var2 = var0.attribute("loc").getValue().split("[\\s,;]+");
      Circle var1;
      if (var2.length < 5) {
         var1 = new Circle(Integer.parseInt(var2[0]), Integer.parseInt(var2[1]), Integer.parseInt(var2[2])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
      } else {
         var1 = new Circle(Integer.parseInt(var2[0]), Integer.parseInt(var2[1]), Integer.parseInt(var2[2]))
            .setZmin(Integer.parseInt(var2[3]))
            .setZmax(Integer.parseInt(var2[4]));
      }

      return var1;
   }
}
