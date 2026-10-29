package l2.gameserver.tables;

import gnu.trove.TIntObjectHashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.PetData;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.items.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetDataTable {
   private static final Logger _log = LoggerFactory.getLogger(PetDataTable.class);
   private static final PetDataTable aqK = new PetDataTable();
   public static final int PET_WOLF_ID = 12077;
   public static final int HATCHLING_WIND_ID = 12311;
   public static final int HATCHLING_STAR_ID = 12312;
   public static final int HATCHLING_TWILIGHT_ID = 12313;
   public static final int STRIDER_WIND_ID = 12526;
   public static final int STRIDER_STAR_ID = 12527;
   public static final int STRIDER_TWILIGHT_ID = 12528;
   public static final int RED_STRIDER_WIND_ID = 16038;
   public static final int RED_STRIDER_STAR_ID = 16039;
   public static final int RED_STRIDER_TWILIGHT_ID = 16040;
   public static final int WYVERN_ID = 12621;
   public static final int BABY_BUFFALO_ID = 12780;
   public static final int BABY_KOOKABURRA_ID = 12781;
   public static final int BABY_COUGAR_ID = 12782;
   public static final int IMPROVED_BABY_BUFFALO_ID = 16034;
   public static final int IMPROVED_BABY_KOOKABURRA_ID = 16035;
   public static final int IMPROVED_BABY_COUGAR_ID = 16036;
   public static final int SIN_EATER_ID = 12564;
   public static final int GREAT_WOLF_ID = 16025;
   public static final int WGREAT_WOLF_ID = 16037;
   public static final int FENRIR_WOLF_ID = 16041;
   public static final int WFENRIR_WOLF_ID = 16042;
   public static final int GUARDIANS_STRIDER_ID = 16068;
   private final TIntObjectHashMap<PetData> aqL = new TIntObjectHashMap();

   public static final PetDataTable getInstance() {
      return aqK;
   }

   private PetDataTable() {
      this.load();
   }

   public void reload() {
      this.load();
   }

   public PetData getInfo(int var1, int var2) {
      PetData var3 = null;

      while (var3 == null && var2 < 100) {
         var3 = (PetData)this.aqL.get(var1 * 100 + var2);
         var2++;
      }

      return var3;
   }

   private void load() {
      Connection var2 = null;
      PreparedStatement var3 = null;
      ResultSet var4 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement(
            "SELECT `id`, `level`, `exp`, `hp`, `mp`, `patk`, `pdef`, `matk`, `mdef`, `acc`, `evasion`, `crit`, `speed`, `atk_speed`, `cast_speed`, `max_meal`, `battle_meal`, `normal_meal`, `loadMax`, `hpregen`, `mpregen` FROM `pet_data`"
         );
         var4 = var3.executeQuery();

         while (var4.next()) {
            PetData var1 = new PetData();
            var1.setID(var4.getInt("id"));
            var1.setLevel(var4.getInt("level"));
            var1.setExp(var4.getLong("exp"));
            var1.setHP(var4.getInt("hp"));
            var1.setMP(var4.getInt("mp"));
            var1.setPAtk(var4.getInt("patk"));
            var1.setPDef(var4.getInt("pdef"));
            var1.setMAtk(var4.getInt("matk"));
            var1.setMDef(var4.getInt("mdef"));
            var1.setAccuracy(var4.getInt("acc"));
            var1.setEvasion(var4.getInt("evasion"));
            var1.setCritical(var4.getInt("crit"));
            var1.setSpeed(var4.getInt("speed"));
            var1.setAtkSpeed(var4.getInt("atk_speed"));
            var1.setCastSpeed(var4.getInt("cast_speed"));
            var1.setFeedMax(var4.getInt("max_meal"));
            var1.setFeedBattle(var4.getInt("battle_meal"));
            var1.setFeedNormal(var4.getInt("normal_meal"));
            var1.setMaxLoad(var4.getInt("loadMax"));
            var1.setHpRegen(var4.getInt("hpregen"));
            var1.setMpRegen(var4.getInt("mpregen"));
            var1.setControlItemId(getControlItemId(var1.getID()));
            var1.setFoodId(getFoodId(var1.getID()));
            var1.setMountable(isMountable(var1.getID()));
            var1.setMinLevel(getMinLevel(var1.getID()));
            var1.setAddFed(getAddFed(var1.getID()));
            this.aqL.put(var1.getID() * 100 + var1.getLevel(), var1);
         }
      } catch (Exception var9) {
         _log.error("", var9);
      } finally {
         DbUtils.closeQuietly(var2, var3, var4);
      }

      _log.info("PetDataTable: Loaded " + this.aqL.size() + " pets.");
   }

   public static void deletePet(ItemInstance var0, Creature var1) {
      int var2 = 0;
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("SELECT `objId` FROM `pets` WHERE `item_obj_id`=?");
         var4.setInt(1, var0.getObjectId());
         var5 = var4.executeQuery();

         while (var5.next()) {
            var2 = var5.getInt("objId");
         }

         DbUtils.close(var4, var5);
         Summon var6 = var1.getPet();
         if (var6 != null && var6.getObjectId() == var2) {
            var6.unSummon();
         }

         Player var7 = var1.getPlayer();
         if (var7 != null && var7.isMounted() && var7.getMountObjId() == var2) {
            var7.setMount(0, 0, 0);
         }

         var4 = var3.prepareStatement("DELETE FROM `pets` WHERE `item_obj_id`=?");
         var4.setInt(1, var0.getObjectId());
         var4.execute();
      } catch (Exception var11) {
         _log.error("could not restore pet objectid:", var11);
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }
   }

   public static void unSummonPet(ItemInstance var0, Creature var1) {
      int var2 = 0;
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("SELECT `objId` FROM `pets` WHERE `item_obj_id`=?");
         var4.setInt(1, var0.getObjectId());
         var5 = var4.executeQuery();

         while (var5.next()) {
            var2 = var5.getInt("objId");
         }

         if (var1 != null) {
            Summon var6 = var1.getPet();
            if (var6 != null && var6.getObjectId() == var2) {
               var6.unSummon();
            }

            Player var7 = var1.getPlayer();
            if (var7 != null && var7.isMounted() && var7.getMountObjId() == var2) {
               var7.setMount(0, 0, 0);
            }

            return;
         }
      } catch (Exception var11) {
         _log.error("could not restore pet objectid:", var11);
         return;
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }
   }

   public static int getControlItemId(int var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getNpcId() == var0) {
            return var4.getControlItemId();
         }
      }

      return 1;
   }

   public static int getFoodId(int var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getNpcId() == var0) {
            return var4.getFoodId();
         }
      }

      return 1;
   }

   public static boolean isMountable(int var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getNpcId() == var0) {
            return var4.isMountable();
         }
      }

      return false;
   }

   public static int getMinLevel(int var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getNpcId() == var0) {
            return var4.getMinLevel();
         }
      }

      return 1;
   }

   public static int getAddFed(int var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getNpcId() == var0) {
            return var4.getAddFed();
         }
      }

      return 1;
   }

   public static double getExpPenalty(int var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getNpcId() == var0) {
            return var4.getExpPenalty();
         }
      }

      return 0.0;
   }

   public static int getSoulshots(int var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getNpcId() == var0) {
            return var4.getSoulshots();
         }
      }

      return 2;
   }

   public static int getSpiritshots(int var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getNpcId() == var0) {
            return var4.getSpiritshots();
         }
      }

      return 2;
   }

   public static int getSummonId(ItemInstance var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getControlItemId() == var0.getItemId()) {
            return var4.getNpcId();
         }
      }

      return 0;
   }

   public static int[] getPetControlItems() {
      int[] var0 = new int[PetDataTable.L2Pet.values().length];
      int var1 = 0;

      for (PetDataTable.L2Pet var5 : PetDataTable.L2Pet.values()) {
         var0[var1++] = var5.getControlItemId();
      }

      return var0;
   }

   public static boolean isPetControlItem(ItemInstance var0) {
      for (PetDataTable.L2Pet var4 : PetDataTable.L2Pet.values()) {
         if (var4.getControlItemId() == var0.getItemId()) {
            return true;
         }
      }

      return false;
   }

   public static boolean isBabyPet(int var0) {
      switch (var0) {
         case 12780:
         case 12781:
         case 12782:
            return true;
         default:
            return false;
      }
   }

   public static boolean isImprovedBabyPet(int var0) {
      switch (var0) {
         case 16034:
         case 16035:
         case 16036:
            return true;
         default:
            return false;
      }
   }

   public static boolean isWolf(int var0) {
      return var0 == 12077;
   }

   public static boolean isHatchling(int var0) {
      switch (var0) {
         case 12311:
         case 12312:
         case 12313:
            return true;
         default:
            return false;
      }
   }

   public static boolean isStrider(int var0) {
      switch (var0) {
         case 12526:
         case 12527:
         case 12528:
         case 16038:
         case 16039:
         case 16040:
         case 16068:
            return true;
         default:
            return false;
      }
   }

   public static boolean isGWolf(int var0) {
      switch (var0) {
         case 16025:
         case 16037:
         case 16041:
         case 16042:
            return true;
         default:
            return false;
      }
   }

   public enum L2Pet {
      WOLF(12077, 2375, 2515, false, 1, 12, 0.3, 2, 2),
      HATCHLING_WIND(12311, 3500, 4038, false, 1, 12, 0.3, 2, 2),
      HATCHLING_STAR(12312, 3501, 4038, false, 1, 12, 0.3, 2, 2),
      HATCHLING_TWILIGHT(12313, 3502, 4038, false, 1, 100, 0.3, 2, 2),
      STRIDER_WIND(12526, 4422, 5168, true, 1, 12, 0.3, 2, 2),
      STRIDER_STAR(12527, 4423, 5168, true, 1, 12, 0.3, 2, 2),
      STRIDER_TWILIGHT(12528, 4424, 5168, true, 1, 100, 0.3, 2, 2),
      WYVERN(12621, 5249, 6316, true, 1, 12, 0.0, 2, 2),
      BABY_BUFFALO(12780, 6648, 7582, false, 1, 12, 0.05, 2, 2),
      BABY_KOOKABURRA(12781, 6650, 7582, false, 1, 12, 0.05, 2, 2),
      BABY_COUGAR(12782, 6649, 7582, false, 1, 12, 0.05, 2, 2),
      SIN_EATER(12564, 4425, 2515, false, 1, 12, 0.0, 2, 2);

      private final int _npcId;
      private final int GO;
      private final int GP;
      private final boolean GR;
      private final int p;
      private final int GQ;
      private final double VV;
      private final int aqM;
      private final int aqN;

      L2Pet(int var3, int var4, int var5, boolean var6, int var7, int var8, double var9, int var11, int var12) {
         this._npcId = var3;
         this.GO = var4;
         this.GP = var5;
         this.GR = var6;
         this.p = var7;
         this.GQ = var8;
         this.VV = var9;
         this.aqM = var11;
         this.aqN = var12;
      }

      public int getNpcId() {
         return this._npcId;
      }

      public int getControlItemId() {
         return this.GO;
      }

      public int getFoodId() {
         return this.GP;
      }

      public boolean isMountable() {
         return this.GR;
      }

      public int getMinLevel() {
         return this.p;
      }

      public int getAddFed() {
         return this.GQ;
      }

      public double getExpPenalty() {
         return this.VV;
      }

      public int getSoulshots() {
         return this.aqM;
      }

      public int getSpiritshots() {
         return this.aqN;
      }
   }
}
