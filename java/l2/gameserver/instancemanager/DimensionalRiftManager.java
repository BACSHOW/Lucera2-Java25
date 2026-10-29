package l2.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.commons.geometry.Rectangle;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Territory;
import l2.gameserver.model.entity.DimensionalRift;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.TeleportToLocation;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DimensionalRiftManager {
   private static final Logger _log = LoggerFactory.getLogger(DimensionalRiftManager.class);
   private static DimensionalRiftManager Ar;
   private Map<Integer, Map<Integer, DimensionalRiftManager.DimensionalRiftRoom>> As = new ConcurrentHashMap<>();
   private static final int At = 7079;

   public static DimensionalRiftManager getInstance() {
      if (Ar == null) {
         Ar = new DimensionalRiftManager();
      }

      return Ar;
   }

   public DimensionalRiftManager() {
      this.load();
   }

   public DimensionalRiftManager.DimensionalRiftRoom getRoom(int var1, int var2) {
      return this.As.get(var1).get(var2);
   }

   public Map<Integer, DimensionalRiftManager.DimensionalRiftRoom> getRooms(int var1) {
      return this.As.get(var1);
   }

   public void load() {
      int var1 = 0;
      int var2 = 0;

      try {
         DocumentBuilderFactory var3 = DocumentBuilderFactory.newInstance();
         var3.setValidating(false);
         var3.setIgnoringComments(true);
         File var4 = new File(Config.DATAPACK_ROOT, "data/dimensional_rift.xml");
         if (!var4.exists()) {
            throw new IOException();
         }

         Document var5 = var3.newDocumentBuilder().parse(var4);
         Location var14 = new Location();
         int var15 = 0;
         int var16 = 0;
         int var17 = 0;
         int var18 = 0;
         int var19 = 0;
         int var20 = 0;

         for (Node var22 = var5.getFirstChild(); var22 != null; var22 = var22.getNextSibling()) {
            if ("rift".equalsIgnoreCase(var22.getNodeName())) {
               for (Node var23 = var22.getFirstChild(); var23 != null; var23 = var23.getNextSibling()) {
                  if ("area".equalsIgnoreCase(var23.getNodeName())) {
                     NamedNodeMap var6 = var23.getAttributes();
                     int var7 = Integer.parseInt(var6.getNamedItem("type").getNodeValue());

                     for (Node var24 = var23.getFirstChild(); var24 != null; var24 = var24.getNextSibling()) {
                        if ("room".equalsIgnoreCase(var24.getNodeName())) {
                           var6 = var24.getAttributes();
                           int var8 = Integer.parseInt(var6.getNamedItem("id").getNodeValue());
                           Node var25 = var6.getNamedItem("isBossRoom");
                           boolean var21 = var25 != null ? Boolean.parseBoolean(var25.getNodeValue()) : false;
                           Territory var26 = null;

                           for (Node var27 = var24.getFirstChild(); var27 != null; var27 = var27.getNextSibling()) {
                              if ("teleport".equalsIgnoreCase(var27.getNodeName())) {
                                 var6 = var27.getAttributes();
                                 var14 = Location.parseLoc(var6.getNamedItem("loc").getNodeValue());
                              } else if ("zone".equalsIgnoreCase(var27.getNodeName())) {
                                 var6 = var27.getAttributes();
                                 var15 = Integer.parseInt(var6.getNamedItem("xMin").getNodeValue());
                                 var16 = Integer.parseInt(var6.getNamedItem("xMax").getNodeValue());
                                 var17 = Integer.parseInt(var6.getNamedItem("yMin").getNodeValue());
                                 var18 = Integer.parseInt(var6.getNamedItem("yMax").getNodeValue());
                                 var19 = Integer.parseInt(var6.getNamedItem("zMin").getNodeValue());
                                 var20 = Integer.parseInt(var6.getNamedItem("zMax").getNodeValue());
                                 var26 = new Territory().add(new Rectangle(var15, var17, var16, var18).setZmin(var19).setZmax(var20));
                              }
                           }

                           if (var26 == null) {
                              _log.error("DimensionalRiftManager: invalid spawn data for room id " + var8 + "!");
                           }

                           if (!this.As.containsKey(var7)) {
                              this.As.put(var7, new ConcurrentHashMap<>());
                           }

                           this.As.get(var7).put(var8, new DimensionalRiftManager.DimensionalRiftRoom(var26, var14, var21));

                           for (Node var43 = var24.getFirstChild(); var43 != null; var43 = var43.getNextSibling()) {
                              if ("spawn".equalsIgnoreCase(var43.getNodeName())) {
                                 var6 = var43.getAttributes();
                                 int var9 = Integer.parseInt(var6.getNamedItem("mobId").getNodeValue());
                                 int var10 = Integer.parseInt(var6.getNamedItem("delay").getNodeValue());
                                 int var11 = Integer.parseInt(var6.getNamedItem("count").getNodeValue());
                                 NpcTemplate var13 = NpcHolder.getInstance().getTemplate(var9);
                                 if (var13 == null) {
                                    _log.warn("Template " + var9 + " not found!");
                                 }

                                 if (!this.As.containsKey(var7)) {
                                    _log.warn("Type " + var7 + " not found!");
                                 } else if (!this.As.get(var7).containsKey(var8)) {
                                    _log.warn("Room " + var8 + " in Type " + var7 + " not found!");
                                 }

                                 if (var13 != null && this.As.containsKey(var7) && this.As.get(var7).containsKey(var8)) {
                                    SimpleSpawner var12 = new SimpleSpawner(var13);
                                    var12.setTerritory(var26);
                                    var12.setHeading(-1);
                                    var12.setRespawnDelay(var10);
                                    var12.setAmount(var11);
                                    this.As.get(var7).get(var8).getSpawns().add(var12);
                                    var1++;
                                 } else {
                                    var2++;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var28) {
         _log.error("DimensionalRiftManager: Error on loading dimensional rift spawns!", var28);
      }

      int var29 = this.As.keySet().size();
      int var30 = 0;

      for (int var36 : this.As.keySet()) {
         var30 += this.As.get(var36).keySet().size();
      }

      _log.info("DimensionalRiftManager: Loaded " + var29 + " room types with " + var30 + " rooms.");
      _log.info("DimensionalRiftManager: Loaded " + var1 + " dimensional rift spawns, " + var2 + " errors.");
   }

   public void reload() {
      for (int var2 : this.As.keySet()) {
         this.As.get(var2).clear();
      }

      this.As.clear();
      this.load();
   }

   public boolean checkIfInRiftZone(Location var1, boolean var2) {
      return var2 ? this.As.get(0).get(1).checkIfInZone(var1) : this.As.get(0).get(1).checkIfInZone(var1) && !this.As.get(0).get(0).checkIfInZone(var1);
   }

   public boolean checkIfInPeaceZone(Location var1) {
      return this.As.get(0).get(0).checkIfInZone(var1);
   }

   public void teleportToWaitingRoom(Player var1) {
      teleToLocation(var1, Location.findPointToStay(this.getRoom(0, 0).getTeleportCoords(), 0, 250, ReflectionManager.DEFAULT.getGeoIndex()), null);
   }

   public void start(Player var1, int var2, NpcInstance var3) {
      if (!var1.isInParty()) {
         this.showHtmlFile(var1, "rift/NoParty.htm", var3);
      } else {
         if (!var1.isGM()) {
            if (!var1.getParty().isLeader(var1)) {
               this.showHtmlFile(var1, "rift/NotPartyLeader.htm", var3);
               return;
            }

            if (var1.getParty().isInDimensionalRift()) {
               this.showHtmlFile(var1, "rift/Cheater.htm", var3);
               if (!var1.isGM()) {
                  _log.warn("Player " + var1.getName() + "(" + var1.getObjectId() + ") was cheating in dimension rift area!");
               }

               return;
            }

            if (var1.getParty().getMemberCount() < Config.RIFT_MIN_PARTY_SIZE) {
               this.showHtmlFile(var1, "rift/SmallParty.htm", var3);
               return;
            }

            for (Player var5 : var1.getParty().getPartyMembers()) {
               if (!this.checkIfInPeaceZone(var5.getLoc())) {
                  this.showHtmlFile(var1, "rift/NotInWaitingRoom.htm", var3);
                  return;
               }
            }

            for (Player var6 : var1.getParty().getPartyMembers()) {
               ItemInstance var7 = var6.getInventory().getItemByItemId(7079);
               if (var7 == null || var7.getCount() < this.v(var2)) {
                  this.showHtmlFile(var1, "rift/NoFragments.htm", var3);
                  return;
               }
            }

            for (Player var10 : var1.getParty().getPartyMembers()) {
               var10.getInventory().destroyItemByItemId(7079, this.v(var2));
            }
         }

         new DimensionalRift(var1.getParty(), var2, Rnd.get(1, this.As.get(var2).size() - 1));
      }
   }

   private long v(int var1) {
      switch (var1) {
         case 1:
            return Config.RIFT_ENTER_COST_RECRUIT;
         case 2:
            return Config.RIFT_ENTER_COST_SOLDIER;
         case 3:
            return Config.RIFT_ENTER_COST_OFFICER;
         case 4:
            return Config.RIFT_ENTER_COST_CAPTAIN;
         case 5:
            return Config.RIFT_ENTER_COST_COMMANDER;
         case 6:
            return Config.RIFT_ENTER_COST_HERO;
         default:
            return Long.MAX_VALUE;
      }
   }

   public void showHtmlFile(Player var1, String var2, NpcInstance var3) {
      NpcHtmlMessage var4 = new NpcHtmlMessage(var1, var3);
      var4.setFile(var2);
      var4.replace("%t_name%", var3.getName());
      var1.sendPacket(var4);
   }

   public static void teleToLocation(Player var0, Location var1, Reflection var2) {
      if (!var0.isTeleporting() && !var0.isDeleted()) {
         var0.setIsTeleporting(true);
         var0.setTarget(null);
         var0.stopMove();
         if (var0.isInBoat()) {
            var0.setBoat(null);
         }

         var0.breakFakeDeath();
         var0.decayMe();
         var0.setLoc(var1);
         if (var2 == null) {
            var0.setReflection(ReflectionManager.DEFAULT);
         }

         var0.setLastClientPosition(null);
         var0.setLastServerPosition(null);
         var0.sendPacket(new TeleportToLocation(var0, var1));
      }
   }

   public class DimensionalRiftRoom {
      private final Territory Au;
      private final Location Av;
      private final boolean Aw;
      private final List<SimpleSpawner> Ax;

      public DimensionalRiftRoom(Territory var2, Location var3, boolean var4) {
         this.Au = var2;
         this.Av = var3;
         this.Aw = var4;
         this.Ax = new ArrayList<>();
      }

      public Location getTeleportCoords() {
         return this.Av;
      }

      public boolean checkIfInZone(Location var1) {
         return this.checkIfInZone(var1.x, var1.y, var1.z);
      }

      public boolean checkIfInZone(int var1, int var2, int var3) {
         return this.Au.isInside(var1, var2, var3);
      }

      public boolean isBossRoom() {
         return this.Aw;
      }

      public List<SimpleSpawner> getSpawns() {
         return this.Ax;
      }
   }
}
