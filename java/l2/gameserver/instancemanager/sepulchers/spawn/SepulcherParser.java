package l2.gameserver.instancemanager.sepulchers.spawn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.commons.geometry.Polygon;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.sepulchers.model.WayData;
import l2.gameserver.model.Territory;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SepulcherParser {
   private static final Logger log = LoggerFactory.getLogger(SepulcherParser.class);
   private static final String Cc = "data/events/sepulchers/sepulcher_territories.xml";
   private static final String Cd = "data/events/sepulchers/sepulcher_makers.xml";
   private static final String Ce = "data/events/sepulchers/sepulcher_way_data.xml";
   private static final String Cf = "data/events/sepulchers/sepulcher_victim_bosses.xml";
   private static final Pattern Cg = Pattern.compile("\\{(\\-?\\d+);(\\-?\\d+);(\\-?\\d+);(\\-?\\d+);?(.+?\\%)?\\}");
   private static final DocumentBuilderFactory Ch = DocumentBuilderFactory.newInstance();
   private static final SepulcherParser Ci = new SepulcherParser();
   private Map<String, Territory> Cj;
   private Map<String, SepulcherSpawnHandler> Ck;
   private Map<String, WayData> Cl;
   private Map<String, Integer> Cm;

   public static SepulcherParser getInstance() {
      return Ci;
   }

   public void load() {
      try {
         this.bM();
      } catch (Exception var5) {
         log.error("Failed to load sepulcher territories", var5);
      }

      try {
         this.bN();
      } catch (Exception var4) {
         log.error("Failed to load sepulcher spawns", var4);
      }

      try {
         this.bO();
      } catch (Exception var3) {
         log.error("Failed to load sepulcher way data", var3);
      }

      try {
         this.bP();
      } catch (Exception var2) {
         log.error("Failed to load sepulcher victim bosses", var2);
      }
   }

   private void bM() throws Exception {
      this.Cj = new HashMap<>();
      Document var1 = Ch.newDocumentBuilder().parse("data/events/sepulchers/sepulcher_territories.xml");

      for (Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         if (var2.getNodeName().equalsIgnoreCase("list")) {
            for (Node var3 = var2.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
               if (var3.getNodeName().equalsIgnoreCase("territory")) {
                  String var4 = this.a(var3, "name");
                  String var5 = this.a(var3, "points");
                  Matcher var6 = Cg.matcher(var5);
                  Territory var7 = new Territory();
                  Polygon var8 = new Polygon();
                  int var9 = 999999;
                  int var10 = -999999;

                  while (var6.find()) {
                     int var11 = Integer.parseInt(var6.group(1));
                     int var12 = Integer.parseInt(var6.group(2));
                     int var13 = Integer.parseInt(var6.group(3));
                     int var14 = Integer.parseInt(var6.group(4));
                     var8.add(var11, var12);
                     if (var13 < var9) {
                        var9 = var13;
                        var8.setZmin(var13);
                     }

                     if (var14 > var10) {
                        var10 = var14;
                        var8.setZmax(var14);
                     }
                  }

                  var7.add(var8);
                  this.Cj.put(var4, var7);
               }
            }
         }
      }
   }

   private void bN() throws Exception {
      this.Ck = new HashMap<>();
      Document var1 = Ch.newDocumentBuilder().parse("data/events/sepulchers/sepulcher_makers.xml");

      for (Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         if (var2.getNodeName().equalsIgnoreCase("list")) {
            for (Node var3 = var2.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
               if (var3.getNodeName().equalsIgnoreCase("npcmaker")) {
                  String var4 = this.a(var3, "name");
                  String var5 = this.b(var3, "territory");
                  Territory var6 = var5 != null ? this.Cj.get(var5) : null;
                  ArrayList var7 = new ArrayList();
                  SepulcherSpawnHandler var8 = new SepulcherSpawnHandler(var4, var6, var7);

                  for (Node var9 = var3.getFirstChild(); var9 != null; var9 = var9.getNextSibling()) {
                     if (var9.getNodeName().equalsIgnoreCase("npc")) {
                        int var10 = Integer.parseInt(this.a(var9, "id"));
                        String var11 = this.a(var9, "pos");
                        int var12 = Integer.parseInt(this.a(var9, "respawn"));
                        int var13 = Integer.parseInt(this.a(var9, "total"));
                        Location var14 = null;
                        Matcher var15 = Cg.matcher(var11);
                        if (var15.find() && var15.group(4) != null) {
                           var14 = new Location(
                              Integer.parseInt(var15.group(1)),
                              Integer.parseInt(var15.group(2)),
                              Integer.parseInt(var15.group(3)),
                              Integer.parseInt(var15.group(4))
                           );
                        }

                        var7.add(new SepulcherSpawnDefine(var10, var14, var12, var13, var8));
                     }
                  }

                  this.Ck.put(var4, var8);
               }
            }
         }
      }
   }

   private void bO() throws Exception {
      this.Cl = new HashMap<>();
      Document var1 = Ch.newDocumentBuilder().parse("data/events/sepulchers/sepulcher_way_data.xml");

      for (Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         if (var2.getNodeName().equalsIgnoreCase("list")) {
            for (Node var3 = var2.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
               if (var3.getNodeName().equalsIgnoreCase("way")) {
                  String var4 = this.a(var3, "prefix");
                  Location var5 = null;
                  Location var6 = null;
                  ArrayList var7 = new ArrayList();
                  String var8 = null;

                  for (Node var9 = var3.getFirstChild(); var9 != null; var9 = var9.getNextSibling()) {
                     if (var9.getNodeName().equalsIgnoreCase("points")) {
                        for (Node var12 = var9.getFirstChild(); var12 != null; var12 = var12.getNextSibling()) {
                           if (var12.getNodeName().equalsIgnoreCase("point")) {
                              if (this.a(var12, "type").equalsIgnoreCase("entry")) {
                                 var5 = this.a(var12);
                              } else if (this.a(var12, "type").equalsIgnoreCase("escape")) {
                                 var6 = this.a(var12);
                              }
                           }
                        }
                     } else if (var9.getNodeName().equalsIgnoreCase("doors")) {
                        for (Node var10 = var9.getFirstChild(); var10 != null; var10 = var10.getNextSibling()) {
                           if (var10.getNodeName().equalsIgnoreCase("door")) {
                              var7.add(this.a(var10, "name"));
                           }
                        }
                     } else if (var9.getNodeName().equalsIgnoreCase("collapse_zone")) {
                        var8 = this.a(var9, "name");
                     }
                  }

                  Zone var11 = ReflectionManager.DEFAULT.getZone(var8);
                  this.Cl.put(var4, new WayData(var5, var6, this.j(var7), var11));
               }
            }
         }
      }
   }

   private void bP() throws Exception {
      this.Cm = new HashMap<>();
      Document var1 = Ch.newDocumentBuilder().parse("data/events/sepulchers/sepulcher_victim_bosses.xml");

      for (Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         if (var2.getNodeName().equalsIgnoreCase("list")) {
            for (Node var3 = var2.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
               if (var3.getNodeName().equalsIgnoreCase("boss")) {
                  this.Cm.put(this.a(var3, "handler_name"), Integer.parseInt(this.a(var3, "id")));
               }
            }
         }
      }
   }

   private Location a(Node var1) {
      return new Location(Integer.parseInt(this.a(var1, "x")), Integer.parseInt(this.a(var1, "y")), Integer.parseInt(this.a(var1, "z")));
   }

   private List<DoorInstance> j(List<String> var1) {
      ArrayList var2 = new ArrayList();
      Collection<DoorInstance> var3 = ReflectionManager.DEFAULT.getDoors();
      var1.forEach(
         var2x -> var2.add(
            var3.stream()
               .filter(var1xx -> var1xx.getName().equalsIgnoreCase(var2x))
               .findAny()
               .orElseThrow(() -> new NullPointerException("Cannot find door named " + var2x))
         )
      );
      return var2;
   }

   private String a(Node var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("Null node " + var2);
      } else {
         Node var3 = var1.getAttributes().getNamedItem(var2);
         if (var3 == null) {
            throw new NullPointerException("No attribute '" + var2 + "' found for " + var1.getNodeName());
         } else {
            return var3.getNodeValue();
         }
      }
   }

   private String b(Node var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("Null node " + var2);
      }

      Node var3 = var1.getAttributes().getNamedItem(var2);
      return var3 == null ? null : var3.getNodeValue();
   }

   public Map<String, Territory> getTerritories() {
      return this.Cj;
   }

   public Map<String, SepulcherSpawnHandler> getSpawns() {
      return this.Ck;
   }

   public Map<String, WayData> getWayData() {
      return this.Cl;
   }

   public Map<String, Integer> getVictimBossData() {
      return this.Cm;
   }

   static {
      Ch.setValidating(false);
      Ch.setIgnoringComments(true);
   }
}
