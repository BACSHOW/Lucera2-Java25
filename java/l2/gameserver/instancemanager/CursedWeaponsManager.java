package l2.gameserver.instancemanager;

import gnu.trove.TIntObjectHashMap;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.CursedWeapon;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class CursedWeaponsManager {
   private static final Logger _log = LoggerFactory.getLogger(CursedWeaponsManager.class);
   private static final CursedWeaponsManager Am = new CursedWeaponsManager();
   private CursedWeapon[] An;
   private TIntObjectHashMap<CursedWeapon> Ao = new TIntObjectHashMap();
   private ScheduledFuture<?> Ap;
   private static final int Aq = 300000;

   public static final CursedWeaponsManager getInstance() {
      return Am;
   }

   public CursedWeaponsManager() {
      this.An = new CursedWeapon[0];
      if (Config.ALLOW_CURSED_WEAPONS) {
         this.load();
         this.restore();
         this.bx();
         this.cancelTask();
         this.Ap = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CursedWeaponsManager.RemoveTask(), 300000L, 300000L);
         _log.info("CursedWeaponsManager: Loaded " + this.An.length + " cursed weapon(s).");
      }
   }

   @Deprecated
   public final void reload() {
   }

   private void load() {
      try {
         DocumentBuilderFactory var1 = DocumentBuilderFactory.newInstance();
         var1.setValidating(false);
         var1.setIgnoringComments(true);
         File var2 = new File(Config.DATAPACK_ROOT, "data/cursed_weapons.xml");
         if (!var2.exists()) {
            return;
         }

         Document var3 = var1.newDocumentBuilder().parse(var2);

         for (Node var4 = var3.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
            if ("list".equalsIgnoreCase(var4.getNodeName())) {
               for (Node var5 = var4.getFirstChild(); var5 != null; var5 = var5.getNextSibling()) {
                  if ("item".equalsIgnoreCase(var5.getNodeName())) {
                     NamedNodeMap var6 = var5.getAttributes();
                     int var7 = Integer.parseInt(var6.getNamedItem("id").getNodeValue());
                     int var8 = Integer.parseInt(var6.getNamedItem("skillId").getNodeValue());
                     String var9 = "Unknown cursed weapon";
                     if (var6.getNamedItem("name") != null) {
                        var9 = var6.getNamedItem("name").getNodeValue();
                     } else if (ItemHolder.getInstance().getTemplate(var7) != null) {
                        var9 = ItemHolder.getInstance().getTemplate(var7).getName();
                     }

                     if (var7 != 0) {
                        CursedWeapon var10 = new CursedWeapon(var7, var8, var9);

                        for (Node var11 = var5.getFirstChild(); var11 != null; var11 = var11.getNextSibling()) {
                           if ("dropRate".equalsIgnoreCase(var11.getNodeName())) {
                              var10.setDropRate(Integer.parseInt(var11.getAttributes().getNamedItem("val").getNodeValue()));
                           } else if ("duration".equalsIgnoreCase(var11.getNodeName())) {
                              var6 = var11.getAttributes();
                              var10.setDurationMin(Integer.parseInt(var6.getNamedItem("min").getNodeValue()));
                              var10.setDurationMax(Integer.parseInt(var6.getNamedItem("max").getNodeValue()));
                           } else if ("durationLost".equalsIgnoreCase(var11.getNodeName())) {
                              var10.setDurationLost(Integer.parseInt(var11.getAttributes().getNamedItem("val").getNodeValue()));
                           } else if ("disapearChance".equalsIgnoreCase(var11.getNodeName())) {
                              var10.setDisapearChance(Integer.parseInt(var11.getAttributes().getNamedItem("val").getNodeValue()));
                           } else if ("stageKills".equalsIgnoreCase(var11.getNodeName())) {
                              var10.setStageKills(Integer.parseInt(var11.getAttributes().getNamedItem("val").getNodeValue()));
                           } else if ("fakeName".equalsIgnoreCase(var11.getNodeName())) {
                              var10.setTransformationName(var11.getAttributes().getNamedItem("val").getNodeValue());
                           }
                        }

                        this.Ao.put(var7, var10);
                     }
                  }
               }
            }
         }

         this.An = (CursedWeapon[])this.Ao.getValues(new CursedWeapon[this.Ao.size()]);
      } catch (Exception var12) {
         _log.error("CursedWeaponsManager: Error parsing cursed_weapons file. " + var12);
      }
   }

   private void restore() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT * FROM `cursed_weapons`");
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("item_id");
            CursedWeapon var5 = (CursedWeapon)this.Ao.get(var4);
            if (var5 != null) {
               var5.setPlayerId(var3.getInt("player_id"));
               var5.setPlayerKarma(var3.getInt("player_karma"));
               var5.setPlayerPkKills(var3.getInt("player_pkkills"));
               var5.setNbKills(var3.getInt("nb_kills"));
               var5.setLoc(new Location(var3.getInt("x"), var3.getInt("y"), var3.getInt("z")));
               var5.setEndTime(var3.getLong("end_time") * 1000L);
               var5.giveSkillAndUpdateStats();
               if (!var5.reActivate()) {
                  this.endOfLife(var5);
               }
            } else {
               this.removeFromDb(var4);
               _log.warn("CursedWeaponsManager: Unknown cursed weapon " + var4 + ", deleted");
            }
         }
      } catch (Exception var9) {
         _log.warn("CursedWeaponsManager: Could not restore cursed_weapons data: " + var9);
         _log.error("", var9);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   private void bx() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      PreparedStatement var3 = null;
      ResultSet var4 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("DELETE FROM `character_skills` WHERE `skill_id`=?");
         var3 = var1.prepareStatement("SELECT `owner_id` FROM `items` WHERE `item_type`=?");

         for (CursedWeapon var8 : this.An) {
            int var9 = var8.getItemId();
            int var10 = var8.getSkillId();
            boolean var11 = false;
            var2.setInt(1, var10);
            var2.executeUpdate();
            var3.setInt(1, var9);
            var4 = var3.executeQuery();

            while (var4.next()) {
               int var12 = var4.getInt("owner_id");
               if (!var11) {
                  if (var12 == var8.getPlayerId() && var8.getPlayerId() != 0) {
                     var11 = true;
                  } else {
                     this.a(var12, var9, var8);
                     _log.info("CursedWeaponsManager[254]: Player " + var12 + " owns the cursed weapon " + var9 + " but he shouldn't.");
                  }
               } else {
                  this.a(var12, var9, var8);
                  _log.info("CursedWeaponsManager[262]: Player " + var12 + " owns the cursed weapon " + var9 + " but he shouldn't.");
               }
            }

            if (!var11 && var8.getPlayerId() != 0) {
               this.removeFromDb(var8.getItemId());
               _log.info("CursedWeaponsManager: Unownered weapon, removing from table...");
            }
         }

         return;
      } catch (Exception var16) {
         _log.warn("CursedWeaponsManager: Could not check cursed_weapons data: " + var16);
      } finally {
         DbUtils.closeQuietly(var2);
         DbUtils.closeQuietly(var1, var3, var4);
      }
   }

   private void a(int var1, int var2, CursedWeapon var3) {
      Connection var4 = null;
      PreparedStatement var5 = null;

      try {
         var4 = DatabaseFactory.getInstance().getConnection();
         var5 = var4.prepareStatement("DELETE FROM `items` WHERE `owner_id`=? AND `item_type`=?");
         var5.setInt(1, var1);
         var5.setInt(2, var2);
         var5.executeUpdate();
         DbUtils.close(var5);
         var5 = var4.prepareStatement("UPDATE `characters` SET `karma`=?, `pkkills`=? WHERE `obj_id`=?");
         var5.setInt(1, var3.getPlayerKarma());
         var5.setInt(2, var3.getPlayerPkKills());
         var5.setInt(3, var1);
         if (var5.executeUpdate() != 1) {
            _log.warn("Error while updating karma & pkkills for userId " + var3.getPlayerId());
         }

         this.removeFromDb(var2);
      } catch (SQLException var10) {
         _log.error("", var10);
      } finally {
         DbUtils.closeQuietly(var4, var5);
      }
   }

   public void removeFromDb(int var1) {
      Connection var2 = null;
      PreparedStatement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("DELETE FROM `cursed_weapons` WHERE `item_id` = ?");
         var3.setInt(1, var1);
         var3.executeUpdate();
         if (this.getCursedWeapon(var1) != null) {
            this.getCursedWeapon(var1).initWeapon();
         }
      } catch (SQLException var8) {
         _log.error("CursedWeaponsManager: Failed to remove data: " + var8);
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }
   }

   private void cancelTask() {
      if (this.Ap != null) {
         this.Ap.cancel(false);
         this.Ap = null;
      }
   }

   public void endOfLife(CursedWeapon var1) {
      if (var1.isActivated()) {
         Player var2 = var1.getOnlineOwner();
         if (var2 != null) {
            _log.info("CursedWeaponsManager: " + var1.getName() + " being removed online from " + var2 + ".");
            var2.abortAttack(true, true);
            var2.setKarma(var1.getPlayerKarma(), true);
            var2.setPkKills(var1.getPlayerPkKills());
            var2.setCursedWeaponEquippedId(0);
            var2.setTransformationName(null);
            var2.removeSkill(SkillTable.getInstance().getInfo(var1.getSkillId(), var2.getSkillLevel(var1.getSkillId())), false);
            var1.clearSkills(var2);
            ItemInstance var3 = var2.getInventory().getPaperdollItem(7);
            if (var1.getItem() == var3) {
               var2.getInventory().setPaperdollItem(8, null);
               var2.getInventory().setPaperdollItem(7, null);
               var2.getInventory().unEquipItem(var1.getItem());
               var2.getInventory().destroyItemByItemId(var1.getItemId(), 1L);
               var2.sendPacket(new InventoryUpdate().addRemovedItem(var3));
            }

            var2.broadcastCharInfo();
         } else {
            _log.info("CursedWeaponsManager: " + var1.getName() + " being removed offline.");
            Connection var12 = null;
            PreparedStatement var4 = null;

            try {
               var12 = DatabaseFactory.getInstance().getConnection();
               var4 = var12.prepareStatement("DELETE FROM `items` WHERE `owner_id`=? AND `item_type`=?");
               var4.setInt(1, var1.getPlayerId());
               var4.setInt(2, var1.getItemId());
               var4.executeUpdate();
               DbUtils.close(var4);
               var4 = var12.prepareStatement("DELETE FROM `character_skills` WHERE `char_obj_id`=? AND `skill_id`=?");
               var4.setInt(1, var1.getPlayerId());
               var4.setInt(2, var1.getSkillId());
               var4.executeUpdate();
               DbUtils.close(var4);
               var4 = var12.prepareStatement("UPDATE `characters` SET `karma`=?, `pkkills`=? WHERE `obj_Id`=?");
               var4.setInt(1, var1.getPlayerKarma());
               var4.setInt(2, var1.getPlayerPkKills());
               var4.setInt(3, var1.getPlayerId());
               var4.executeUpdate();
            } catch (SQLException var9) {
               _log.warn("CursedWeaponsManager: Could not delete : " + var9);
            } finally {
               DbUtils.closeQuietly(var12, var4);
            }
         }
      } else if (var1.getPlayer() != null && var1.getPlayer().getInventory().getItemByItemId(var1.getItemId()) != null) {
         Player var11 = var1.getPlayer();
         if (!var1.getPlayer().getInventory().destroyItemByItemId(var1.getItemId(), 1L)) {
            _log.info("CursedWeaponsManager[453]: Error! Cursed weapon not found!!!");
         }

         var11.sendChanges();
         var11.broadcastUserInfo(true);
      } else if (var1.getItem() != null) {
         var1.getItem().deleteMe();
         var1.getItem().delete();
         _log.info("CursedWeaponsManager: " + var1.getName() + " item has been removed from World.");
      }

      var1.initWeapon();
      this.removeFromDb(var1.getItemId());
      this.announce(new SystemMessage(1818).addString(var1.getName()));
   }

   public void saveData(CursedWeapon var1) {
      Connection var2 = null;
      PreparedStatement var3 = null;
      synchronized (var1) {
         try {
            var2 = DatabaseFactory.getInstance().getConnection();
            var3 = var2.prepareStatement("DELETE FROM `cursed_weapons` WHERE `item_id` = ?");
            var3.setInt(1, var1.getItemId());
            var3.executeUpdate();
            if (var1.isActive()) {
               DbUtils.close(var3);
               var3 = var2.prepareStatement(
                  "REPLACE INTO `cursed_weapons` (`item_id`, `player_id`, `player_karma`, `player_pkkills`, `nb_kills`, `x`, `y`, `z`, `end_time`) VALUES (?,?,?,?,?,?,?,?,?)"
               );
               var3.setInt(1, var1.getItemId());
               var3.setInt(2, var1.getPlayerId());
               var3.setInt(3, var1.getPlayerKarma());
               var3.setInt(4, var1.getPlayerPkKills());
               var3.setInt(5, var1.getNbKills());
               var3.setInt(6, var1.getLoc().x);
               var3.setInt(7, var1.getLoc().y);
               var3.setInt(8, var1.getLoc().z);
               var3.setLong(9, var1.getEndTime() / 1000L);
               var3.executeUpdate();
            }
         } catch (SQLException var11) {
            _log.error("CursedWeapon: Failed to save data: " + var11);
         } finally {
            DbUtils.closeQuietly(var2, var3);
         }
      }
   }

   public void saveData() {
      for (CursedWeapon var4 : this.An) {
         this.saveData(var4);
      }
   }

   public void checkPlayer(Player var1, ItemInstance var2) {
      if (var1 != null && var2 != null && !var1.isOlyParticipant()) {
         CursedWeapon var3 = (CursedWeapon)this.Ao.get(var2.getItemId());
         if (var3 != null) {
            if (var1.getObjectId() != var3.getPlayerId() && var3.getPlayerId() != 0 && !var3.isDropped()) {
               _log.warn("CursedWeaponsManager: " + var1 + " tried to obtain " + var2 + " in wrong way");
               var1.getInventory().destroyItem(var2, var2.getCount());
            } else {
               this.activate(var1, var2);
               this.showUsageTime(var1, var3);
            }
         }
      }
   }

   public void activate(Player var1, ItemInstance var2) {
      if (var1 != null && !var1.isOlyParticipant()) {
         CursedWeapon var3 = (CursedWeapon)this.Ao.get(var2.getItemId());
         if (var3 != null) {
            if (var1.isCursedWeaponEquipped()) {
               if (var1.getCursedWeaponEquippedId() != var2.getItemId()) {
                  CursedWeapon var4 = (CursedWeapon)this.Ao.get(var1.getCursedWeaponEquippedId());
                  var4.setNbKills(var4.getStageKills() - 1);
                  var4.increaseKills();
               }

               this.endOfLife(var3);
               var1.getInventory().destroyItem(var2, 1L);
            } else if (var3.getTimeLeft() > 0L) {
               var3.activate(var1, var2);
               this.saveData(var3);
               this.announce(new SystemMessage(1816).addZoneName(var1.getLoc()).addItemName(var3.getItemId()));
            } else {
               this.endOfLife(var3);
               var1.getInventory().destroyItem(var2, 1L);
            }
         }
      }
   }

   public void doLogout(Player var1) {
      for (CursedWeapon var5 : this.An) {
         if (var1.getInventory().getItemByItemId(var5.getItemId()) != null) {
            var5.setPlayer(null);
            var5.setItem(null);
         }
      }
   }

   public void dropAttackable(NpcInstance var1, Player var2) {
      if (!var2.isOlyParticipant() && !var2.isCursedWeaponEquipped() && this.An.length != 0 && var2.getReflection() == ReflectionManager.DEFAULT) {
         synchronized (this.An) {
            CursedWeapon[] var4 = new CursedWeapon[0];

            for (CursedWeapon var8 : this.An) {
               if (!var8.isActive()) {
                  var4 = (CursedWeapon[])ArrayUtils.add(var4, var8);
               }
            }

            if (var4.length > 0) {
               CursedWeapon var11 = var4[Rnd.get(var4.length)];
               if (Rnd.get(100000000) <= var11.getDropRate()) {
                  var11.create(var1, var2);
               }
            }
         }
      }
   }

   public void dropPlayer(Player var1) {
      CursedWeapon var2 = (CursedWeapon)this.Ao.get(var1.getCursedWeaponEquippedId());
      if (var2 != null) {
         if (var2.dropIt(null, null, var1)) {
            this.saveData(var2);
            this.announce(new SystemMessage(1815).addZoneName(var1.getLoc()).addItemName(var2.getItemId()));
         } else {
            this.endOfLife(var2);
         }
      }
   }

   public void increaseKills(int var1) {
      CursedWeapon var2 = (CursedWeapon)this.Ao.get(var1);
      if (var2 != null) {
         var2.increaseKills();
         this.saveData(var2);
      }
   }

   public int getLevel(int var1) {
      CursedWeapon var2 = (CursedWeapon)this.Ao.get(var1);
      return var2 != null ? var2.getLevel() : 0;
   }

   public void announce(SystemMessage var1) {
      for (Player var3 : GameObjectsStorage.getAllPlayersForIterate()) {
         var3.sendPacket(var1);
      }
   }

   public void showUsageTime(Player var1, int var2) {
      CursedWeapon var3 = (CursedWeapon)this.Ao.get(var2);
      if (var3 != null) {
         this.showUsageTime(var1, var3);
      }
   }

   public void showUsageTime(Player var1, CursedWeapon var2) {
      SystemMessage var3 = new SystemMessage(1814);
      var3.addItemName(var2.getItemId());
      var3.addNumber(Long.valueOf(var2.getTimeLeft() / 60000L).intValue());
      var1.sendPacket(var3);
   }

   public boolean isCursed(int var1) {
      return this.Ao.containsKey(var1);
   }

   public CursedWeapon[] getCursedWeapons() {
      return this.An;
   }

   public int[] getCursedWeaponsIds() {
      return this.Ao.keys();
   }

   public CursedWeapon getCursedWeapon(int var1) {
      return (CursedWeapon)this.Ao.get(var1);
   }

   private class RemoveTask extends RunnableImpl {
      private RemoveTask() {
      }

      @Override
      public void runImpl() throws Exception {
         for (CursedWeapon var4 : CursedWeaponsManager.this.An) {
            if (var4.isActive() && var4.getTimeLeft() <= 0L) {
               CursedWeaponsManager.this.endOfLife(var4);
            }
         }
      }
   }
}
