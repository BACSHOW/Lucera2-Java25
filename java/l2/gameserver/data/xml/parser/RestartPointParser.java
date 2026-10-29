package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import l2.commons.data.xml.AbstractFileParser;
import l2.commons.geometry.Polygon;
import l2.commons.geometry.Rectangle;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.model.Territory;
import l2.gameserver.model.World;
import l2.gameserver.model.base.Race;
import l2.gameserver.templates.mapregion.RestartArea;
import l2.gameserver.templates.mapregion.RestartPoint;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class RestartPointParser extends AbstractFileParser<MapRegionManager> {
   private static final RestartPointParser xz = new RestartPointParser();

   public static RestartPointParser getInstance() {
      return xz;
   }

   private RestartPointParser() {
      super(MapRegionManager.getInstance());
   }

   @Override
   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/mapregion/restart_points.xml");
   }

   @Override
   public String getDTDFileName() {
      return "restart_points.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      ArrayList var2 = new ArrayList();
      HashMap var3 = new HashMap();
      Iterator var4 = var1.elementIterator();

      while (var4.hasNext()) {
         Element var5 = (Element)var4.next();
         if ("restart_area".equals(var5.getName())) {
            Territory var23 = null;
            HashMap var25 = new HashMap();
            Iterator var27 = var5.elementIterator();

            while (var27.hasNext()) {
               Element var29 = (Element)var27.next();
               if ("region".equalsIgnoreCase(var29.getName())) {
                  Attribute var35 = var29.attribute("map");
                  String var37 = var35.getValue();
                  String[] var39 = var37.split("_");
                  int var41 = Integer.parseInt(var39[0]);
                  int var43 = Integer.parseInt(var39[1]);
                  int var16 = World.MAP_MIN_X + (var41 - Config.GEO_X_FIRST << 15);
                  int var17 = World.MAP_MIN_Y + (var43 - Config.GEO_Y_FIRST << 15);
                  int var18 = var16 + 32768 - 1;
                  int var19 = var17 + 32768 - 1;
                  Rectangle var31 = new Rectangle(var16, var17, var18, var19);
                  var31.setZmin(World.MAP_MIN_Z);
                  var31.setZmax(World.MAP_MAX_Z);
                  if (var23 == null) {
                     var23 = new Territory();
                  }

                  var23.add(var31);
               } else if ("polygon".equalsIgnoreCase(var29.getName())) {
                  Polygon var32 = ZoneParser.parsePolygon(var29);
                  if (!var32.validate()) {
                     this.error("RestartPointParser: invalid territory data : " + var32 + "!");
                  }

                  if (var23 == null) {
                     var23 = new Territory();
                  }

                  var23.add(var32);
               } else if ("restart".equalsIgnoreCase(var29.getName())) {
                  Race var33 = Race.valueOf(var29.attributeValue("race"));
                  String var36 = var29.attributeValue("loc");
                  var25.put(var33, var36);
               }
            }

            if (var23 == null) {
               throw new RuntimeException("RestartPointParser: empty territory!");
            }

            if (var25.isEmpty()) {
               throw new RuntimeException("RestartPointParser: restarts not defined!");
            }

            var2.add(new ImmutablePair(var23, var25));
         } else if ("restart_loc".equals(var5.getName())) {
            String var6 = var5.attributeValue("name");
            int var7 = Integer.parseInt(var5.attributeValue("bbs", "0"));
            int var8 = Integer.parseInt(var5.attributeValue("msg_id", "0"));
            ArrayList var9 = new ArrayList();
            ArrayList var10 = new ArrayList();
            Iterator var11 = var5.elementIterator();

            while (var11.hasNext()) {
               Element var12 = (Element)var11.next();
               if ("restart_point".equals(var12.getName())) {
                  Iterator var38 = var12.elementIterator();

                  while (var38.hasNext()) {
                     Element var40 = (Element)var38.next();
                     if ("coords".equalsIgnoreCase(var40.getName())) {
                        Location var42 = Location.parseLoc(var40.attribute("loc").getValue());
                        var9.add(var42);
                     }
                  }
               } else if ("PKrestart_point".equals(var12.getName())) {
                  Iterator var13 = var12.elementIterator();

                  while (var13.hasNext()) {
                     Element var14 = (Element)var13.next();
                     if ("coords".equalsIgnoreCase(var14.getName())) {
                        Location var15 = Location.parseLoc(var14.attribute("loc").getValue());
                        var10.add(var15);
                     }
                  }
               }
            }

            if (var9.isEmpty()) {
               throw new RuntimeException("RestartPointParser: restart_points not defined for restart_loc : " + var6 + "!");
            }

            if (var10.isEmpty()) {
               var10 = var9;
            }

            RestartPoint var34 = new RestartPoint(var6, var7, var8, var9, var10);
            var3.put(var6, var34);
         }
      }

      for (Object __raw_var22_152 : (Iterable<?>)(Object)(var2)) {
         Pair var22 = (Pair)__raw_var22_152;
         HashMap var24 = new HashMap();

         for (Object __raw_var28_155 : (Iterable<?>)(Object)(((Map)var22.getValue()).entrySet())) {
            Entry var28 = (Entry)__raw_var28_155;
            RestartPoint var30 = (RestartPoint)var3.get(var28.getValue());
            if (var30 == null) {
               throw new RuntimeException("RestartPointParser: restart_loc not found : " + (String)var28.getValue() + "!");
            }

            var24.put(var28.getKey(), var30);

            try {
               this.getHolder().addRegionData(new RestartArea((Territory)var22.getKey(), var24));
            } catch (Exception var20) {
               System.out.println("Cant add restart area \"" + (String)var28.getValue() + "\"");
               var20.printStackTrace();
            }
         }
      }
   }
}
