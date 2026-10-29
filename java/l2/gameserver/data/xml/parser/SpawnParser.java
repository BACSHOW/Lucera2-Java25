package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import l2.commons.collections.MultiValueSet;
import l2.commons.data.xml.AbstractDirParser;
import l2.commons.geometry.Point2D;
import l2.commons.geometry.Polygon;
import l2.commons.time.cron.AddPattern;
import l2.commons.time.cron.NextTime;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SpawnHolder;
import l2.gameserver.model.Territory;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.spawn.PeriodOfDay;
import l2.gameserver.templates.spawn.SpawnNpcInfo;
import l2.gameserver.templates.spawn.SpawnTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.SpawnMesh;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SpawnParser extends AbstractDirParser<SpawnHolder> {
   private static final Logger LOG = LoggerFactory.getLogger(SpawnParser.class);
   private static final SpawnParser xD = new SpawnParser();

   public static SpawnParser getInstance() {
      return xD;
   }

   protected SpawnParser() {
      super(SpawnHolder.getInstance());
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/spawn/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "spawn.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         if ("spawn".equalsIgnoreCase(var3.getName())) {
            String var4 = var3.attributeValue("name");
            String var5 = var3.attributeValue("event_name");
            SpawnMesh var6 = null;
            Iterator var7 = var3.elementIterator();

            while (var7.hasNext()) {
               Element var8 = (Element)var7.next();
               if ("mesh".equalsIgnoreCase(var8.getName())) {
                  var6 = this.d(var8);
               } else if ("npc".equalsIgnoreCase(var8.getName())) {
                  int var9 = Integer.parseInt(var8.attributeValue("id", "0"));
                  int var10 = Integer.parseInt(var8.attributeValue("count"));
                  long var11 = Long.parseLong(var8.attributeValue("respawn", "60"));
                  long var13 = Long.parseLong(var8.attributeValue("respawn_rand", "0"));
                  if (var13 > var11) {
                     throw new RuntimeException("Invalid respawn respawn_rand > respawn of " + var3.asXML());
                  }

                  String var15 = var8.attributeValue("respawn_cron");
                  NextTime var16 = null;
                  if (var15 != null) {
                     try {
                        var16 = new SchedulingPattern(var15);
                     } catch (SchedulingPattern.InvalidPatternException var26) {
                        try {
                           var16 = new AddPattern(var15);
                        } catch (Exception var25) {
                           throw new RuntimeException("Invalid respawn data of " + var3.asXML(), var25);
                        }

                        if (var16 == null) {
                           throw new RuntimeException("Invalid respawn data of " + var3.asXML(), var26);
                        }
                     }
                  }

                  PeriodOfDay var17 = PeriodOfDay.valueOf(var8.attributeValue("period_of_day", PeriodOfDay.ALL.name()));
                  Location var18 = null;
                  if (var8.attributeValue("pos") != null) {
                     var18 = Location.parseLoc(var8.attributeValue("pos"));
                  } else if (var6 == null) {
                     throw new RuntimeException("Neither mesh nor pos defined " + var3.asXML());
                  }

                  MultiValueSet var19 = StatsSet.EMPTY;
                  Iterator var20 = var8.elementIterator();

                  while (var20.hasNext()) {
                     Element var21 = (Element)var20.next();
                     if ("ai_params".equalsIgnoreCase(var21.getName())) {
                        Iterator var22 = var21.elementIterator();

                        while (var22.hasNext()) {
                           Element var23 = (Element)var22.next();
                           if ("set".equalsIgnoreCase(var23.getName())) {
                              if (var19 == StatsSet.EMPTY) {
                                 var19 = new MultiValueSet();
                              }

                              var19.set(var23.attributeValue("name"), var23.attributeValue("val"));
                           }
                        }
                     }
                  }

                  try {
                     SpawnTemplate var27 = new SpawnTemplate(var4, var5, var17, var10, var11, var13, var16);
                     SpawnNpcInfo var28 = new SpawnNpcInfo(var9, var10, var19);
                     var27.addNpc(var28);
                     var27.addSpawnRange(var18 != null ? var18 : var6);
                     this.getHolder().addSpawn(var5 != null ? var5 : PeriodOfDay.ALL.name(), var27);
                  } catch (Exception var24) {
                     var24.printStackTrace();
                  }
               }
            }
         }
      }
   }

   private Territory a(String var1, Element var2) {
      Territory var3 = new Territory();
      var3.add(this.b(var1, var2));
      Iterator var4 = var2.elementIterator("banned_territory");

      while (var4.hasNext()) {
         var3.addBanned(this.b(var1, (Element)var4.next()));
      }

      return var3;
   }

   private Polygon b(String var1, Element var2) {
      Polygon var3 = new Polygon();
      Iterator var4 = var2.elementIterator("add");

      while (var4.hasNext()) {
         Element var5 = (Element)var4.next();
         int var6 = Integer.parseInt(var5.attributeValue("x"));
         int var7 = Integer.parseInt(var5.attributeValue("y"));
         int var8 = Integer.parseInt(var5.attributeValue("zmin"));
         int var9 = Integer.parseInt(var5.attributeValue("zmax"));
         var3.add(var6, var7).setZmin(var8).setZmax(var9);
      }

      if (!var3.validate()) {
         this.error("Invalid polygon: " + var1 + "{" + var3 + "}. File: " + this.getCurrentFileName());
      }

      return var3;
   }

   private SpawnMesh d(Element var1) {
      short var2 = 32767;
      short var3 = -32768;
      Iterator var4 = var1.elementIterator("vertex");
      LinkedList var5 = new LinkedList();

      while (var4.hasNext()) {
         Element var6 = (Element)var4.next();
         int var7 = Integer.parseInt(var6.attributeValue("x"));
         int var8 = Integer.parseInt(var6.attributeValue("y"));
         var2 = (short)Math.min(var2, Short.parseShort(var6.attributeValue("minz")));
         var3 = (short)Math.max(var3, Short.parseShort(var6.attributeValue("maxz")));
         var5.add(new Point2D(var7, var8));
      }

      SpawnMesh var9 = new SpawnMesh();

      for (Object __raw_var11_189 : (Iterable<?>)(Object)(var5)) {
         Point2D var11 = (Point2D)__raw_var11_189;
         var9.add(var11.getX(), var11.getY());
      }

      assert var3 >= var2;
      var9.setZmax(var3);
      var9.setZmin(var2);
      if (var9.validate() && var9.getZmin() <= var9.getZmax()) {
         return var9;
      } else {
         throw new RuntimeException("Invalid spawn mesh " + var9 + " defined for the node " + var1.asXML());
      }
   }
}
