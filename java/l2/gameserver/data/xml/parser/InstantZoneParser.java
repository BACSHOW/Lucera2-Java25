package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractDirParser;
import l2.commons.geometry.Polygon;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.DoorHolder;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.data.xml.holder.SpawnHolder;
import l2.gameserver.data.xml.holder.ZoneHolder;
import l2.gameserver.model.Territory;
import l2.gameserver.templates.DoorTemplate;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.ZoneTemplate;
import l2.gameserver.utils.Location;
import org.dom4j.Element;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public class InstantZoneParser extends AbstractDirParser<InstantZoneHolder> {
   private static final InstantZoneParser xs = new InstantZoneParser();

   public InstantZoneParser() {
      super(InstantZoneHolder.getInstance());
   }

   public static InstantZoneParser getInstance() {
      return xs;
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/instances/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "instances.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         SchedulingPattern var6 = new SchedulingPattern("30 6 * * *");
         int var7 = -1;
         int var8 = 60;
         int var9 = -1;
         int var10 = -1;
         boolean var11 = false;
         boolean var12 = true;
         int var17 = 0;
         int var18 = 0;
         byte var19 = 0;
         Object var20 = null;
         int var21 = 0;
         int var22 = 0;
         int var23 = 0;
         int var24 = 0;
         int var25 = 0;
         int var26 = 20;
         boolean var27 = false;
         boolean var28 = true;
         StatsSet var29 = new StatsSet();
         ArrayList var30 = new ArrayList();
         IntObjectMap<InstantZone.DoorInfo> var31 = Containers.emptyIntObjectMap();
         Map var32 = Collections.emptyMap();
         Map var33 = Collections.emptyMap();
         int var4 = Integer.parseInt(var3.attributeValue("id"));
         String var5 = var3.attributeValue("name");
         String var34 = var3.attributeValue("timelimit");
         if (var34 != null) {
            var7 = Integer.parseInt(var34);
         }

         var34 = var3.attributeValue("collapseIfEmpty");
         var18 = Integer.parseInt(var34);
         var34 = var3.attributeValue("maxChannels");
         var26 = Integer.parseInt(var34);
         var34 = var3.attributeValue("dispelBuffs");
         var11 = var34 != null && Boolean.parseBoolean(var34);
         int var35 = 0;
         int var36 = 0;
         int var37 = 1;
         int var38 = 9;
         List var39 = Collections.emptyList();
         Location var40 = null;
         Iterator var41 = var3.elementIterator();

         while (var41.hasNext()) {
            Element var42 = (Element)var41.next();
            if ("level".equalsIgnoreCase(var42.getName())) {
               var35 = Integer.parseInt(var42.attributeValue("min"));
               var36 = Integer.parseInt(var42.attributeValue("max"));
            } else if ("collapse".equalsIgnoreCase(var42.getName())) {
               var12 = Boolean.parseBoolean(var42.attributeValue("on-party-dismiss"));
               var8 = Integer.parseInt(var42.attributeValue("timer"));
            } else if ("party".equalsIgnoreCase(var42.getName())) {
               var37 = Integer.parseInt(var42.attributeValue("min"));
               var38 = Integer.parseInt(var42.attributeValue("max"));
            } else if ("return".equalsIgnoreCase(var42.getName())) {
               var40 = Location.parseLoc(var42.attributeValue("loc"));
            } else if ("teleport".equalsIgnoreCase(var42.getName())) {
               if (var39.isEmpty()) {
                  var39 = new ArrayList(1);
               }

               var39.add(Location.parseLoc(var42.attributeValue("loc")));
            } else if ("remove".equalsIgnoreCase(var42.getName())) {
               var21 = Integer.parseInt(var42.attributeValue("itemId"));
               var22 = Integer.parseInt(var42.attributeValue("count"));
               var27 = Boolean.parseBoolean(var42.attributeValue("necessary"));
            } else if ("give".equalsIgnoreCase(var42.getName())) {
               var23 = Integer.parseInt(var42.attributeValue("itemId"));
               var24 = Integer.parseInt(var42.attributeValue("count"));
            } else if ("quest".equalsIgnoreCase(var42.getName())) {
               var25 = Integer.parseInt(var42.attributeValue("id"));
            } else if ("reuse".equalsIgnoreCase(var42.getName())) {
               var6 = new SchedulingPattern(var42.attributeValue("resetReuse"));
               var17 = Integer.parseInt(var42.attributeValue("sharedReuseGroup"));
               var28 = Boolean.parseBoolean(var42.attributeValue("setUponEntry"));
            } else if ("geodata".equalsIgnoreCase(var42.getName())) {
               String[] var68 = var42.attributeValue("map").split("_");
               var9 = Integer.parseInt(var68[0]);
               var10 = Integer.parseInt(var68[1]);
            } else if ("doors".equalsIgnoreCase(var42.getName())) {
               for (Element var71 : var42.elements()) {
                  if (var31.isEmpty()) {
                     var31 = new HashIntObjectMap<InstantZone.DoorInfo>();
                  }

                  boolean var74 = var71.attributeValue("opened") != null && Boolean.parseBoolean(var71.attributeValue("opened"));
                  boolean var77 = var71.attributeValue("invul") == null || Boolean.parseBoolean(var71.attributeValue("invul"));
                  DoorTemplate var79 = DoorHolder.getInstance().getTemplate(Integer.parseInt(var71.attributeValue("id")));
                  var31.put(var79.getNpcId(), new InstantZone.DoorInfo(var79, var74, var77));
               }
            } else if ("zones".equalsIgnoreCase(var42.getName())) {
               for (Element var70 : var42.elements()) {
                  if (var32.isEmpty()) {
                     var32 = new HashMap();
                  }

                  boolean var73 = var70.attributeValue("active") != null && Boolean.parseBoolean(var70.attributeValue("active"));
                  ZoneTemplate var76 = ZoneHolder.getInstance().getTemplate(var70.attributeValue("name"));
                  if (var76 == null) {
                     this.error("Zone: " + var70.attributeValue("name") + " not found; file: " + this.getCurrentFileName());
                  } else {
                     var32.put(var76.getName(), new InstantZone.ZoneInfo(var76, var73));
                  }
               }
            } else if ("add_parameters".equalsIgnoreCase(var42.getName())) {
               for (Element var69 : var42.elements()) {
                  if ("param".equalsIgnoreCase(var69.getName())) {
                     var29.set(var69.attributeValue("name"), var69.attributeValue("value"));
                  }
               }
            } else if ("spawns".equalsIgnoreCase(var42.getName())) {
               for (Element var44 : var42.elements()) {
                  if ("group".equalsIgnoreCase(var44.getName())) {
                     String var72 = var44.attributeValue("name");
                     boolean var75 = var44.attributeValue("spawned") != null && Boolean.parseBoolean(var44.attributeValue("spawned"));
                     List var78 = SpawnHolder.getInstance().getSpawn(var72);
                     if (var78 == null) {
                        this.info("not find spawn group: " + var72 + " in file: " + this.getCurrentFileName());
                     } else {
                        if (var33.isEmpty()) {
                           var33 = new Hashtable();
                        }

                        var33.put(var72, new InstantZone.SpawnInfo2(var78, var75));
                     }
                  } else if ("spawn".equalsIgnoreCase(var44.getName())) {
                     String[] var45 = var44.attributeValue("mobId").split(" ");
                     String var46 = var44.attributeValue("respawn");
                     int var14 = var46 != null ? Integer.parseInt(var46) : 0;
                     String var47 = var44.attributeValue("respawnRnd");
                     int var15 = var47 != null ? Integer.parseInt(var47) : 0;
                     String var48 = var44.attributeValue("count");
                     int var16 = var48 != null ? Integer.parseInt(var48) : 1;
                     ArrayList var49 = new ArrayList();
                     var19 = 0;
                     String var50 = var44.attributeValue("type");
                     if (var50 == null || var50.equalsIgnoreCase("point")) {
                        var19 = 0;
                     } else if (var50.equalsIgnoreCase("rnd")) {
                        var19 = 1;
                     } else if (var50.equalsIgnoreCase("loc")) {
                        var19 = 2;
                     } else {
                        this.error("Spawn type  '" + var50 + "' is unknown!");
                     }

                     for (Element var52 : var44.elements()) {
                        if ("coords".equalsIgnoreCase(var52.getName())) {
                           var49.add(Location.parseLoc(var52.attributeValue("loc")));
                        }
                     }

                     Territory var80 = null;
                     if (var19 == 2) {
                        Polygon var81 = new Polygon();

                        for (Object __raw_var54_220 : (Iterable<?>)(Object)(var49)) {
                           Location var54 = (Location)__raw_var54_220;
                           var81.add(var54.x, var54.y).setZmin(var54.z).setZmax(var54.z);
                        }

                        if (!var81.validate()) {
                           this.error("invalid spawn territory for instance id : " + var4 + " - " + var81 + "!");
                        }

                        var80 = new Territory().add(var81);
                     }

                     for (String var55 : var45) {
                        int var13 = Integer.parseInt(var55);
                        var20 = new InstantZone.SpawnInfo(var19, var13, var16, var14, var15, var49, var80);
                        var30.add(var20);
                     }
                  }
               }
            }
         }

         InstantZone var64 = new InstantZone.Builder()
            .setInstanceId(var4)
            .setName(var5)
            .setResetReuse(var6)
            .setSharedReuseGroup(var17)
            .setTimelimit(var7)
            .setDispelBuffs(var11)
            .setMinLevel(var35)
            .setMaxLevel(var36)
            .setMinParty(var37)
            .setMaxParty(var38)
            .setTimer(var8)
            .setOnPartyDismiss(var12)
            .addTeleportLocs(var39)
            .setRet(var40)
            .setMapx(var9)
            .setMapy(var10)
            .setDoors((IntObjectMap<InstantZone.DoorInfo>)var31)
            .setZones(var32)
            .setSpawns2(var33)
            .setSpawns(var30)
            .setCollapseIfEmpty(var18)
            .setMaxChannels(var26)
            .setRemovedItemId(var21)
            .setRemovedItemCount(var22)
            .setRemovedItemNecessity(var27)
            .setGiveItemId(var23)
            .setGivedItemCount(var24)
            .setRequiredQuestId(var25)
            .setSetReuseUponEntry(var28)
            .setParams(var29)
            .build();
         this.getHolder().addInstantZone(var64);
      }
   }
}
