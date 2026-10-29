package l2.gameserver.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.attachment.PickableAttachment;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.templates.item.ItemTemplate;

public final class ItemFunctions {
   public static final int[][] catalyst = new int[][]{
      {12362, 14078, 14702},
      {12363, 14079, 14703},
      {12364, 14080, 14704},
      {12365, 14081, 14705},
      {12366, 14082, 14706},
      {12367, 14083, 14707},
      {12368, 14084, 14708},
      {12369, 14085, 14709},
      {12370, 14086, 14710},
      {12371, 14087, 14711}
   };

   private ItemFunctions() {
   }

   public static ItemInstance createItem(int var0) {
      ItemInstance var1 = new ItemInstance(IdFactory.getInstance().getNextId(), var0);
      var1.setLocation(ItemInstance.ItemLocation.VOID);
      var1.setCount(1L);
      return var1;
   }

   public static void addItem(Playable var0, int var1, long var2, boolean var4) {
      if (var0 != null && var2 >= 1L) {
         Playable var5;
         if (var0.isSummon()) {
            var5 = var0.getPlayer();
         } else {
            var5 = var0;
         }

         ItemTemplate var6 = ItemHolder.getInstance().getTemplate(var1);
         if (var6.isStackable()) {
            var5.getInventory().addItem(var1, var2);
         } else {
            for (long var7 = 0L; var7 < var2; var7++) {
               var5.getInventory().addItem(var1, 1L);
            }
         }

         if (var4) {
            var5.sendPacket(SystemMessage2.obtainItems(var1, var2, 0));
         }
      }
   }

   public static List<ItemInstance> addItem(Playable var0, ItemTemplate var1, long var2, boolean var4) {
      if (var0 != null && var2 >= 1L) {
         Playable var5;
         if (var0.isSummon()) {
            var5 = var0.getPlayer();
         } else {
            var5 = var0;
         }

         List var6;
         if (var1.isStackable()) {
            var6 = Collections.singletonList(var5.getInventory().addItem(var1.getItemId(), var2));
         } else {
            var6 = new LinkedList();

            for (long var7 = 0L; var7 < var2; var7++) {
               var6.add(var5.getInventory().addItem(var1.getItemId(), 1L));
            }
         }

         if (var4) {
            var5.sendPacket(SystemMessage2.obtainItems(var1.getItemId(), var2, 0));
         }

         return var6;
      } else {
         return Collections.emptyList();
      }
   }

   public static long getItemCount(Playable var0, int var1) {
      if (var0 == null) {
         return 0L;
      }

      Player var2 = var0.getPlayer();
      return var2.getInventory().getCountOf(var1);
   }

   public static long removeItem(Playable var0, int var1, long var2, boolean var4) {
      return removeItem(var0, var1, var2, var4, true);
   }

   public static long removeItem(Playable var0, int var1, long var2, boolean var4, boolean var5) {
      long var6 = 0L;
      if (var0 != null && var2 >= 1L) {
         Player var8 = var0.getPlayer();
         ItemTemplate var9 = ItemHolder.getInstance().getTemplate(var1);
         if (var9.isStackable()) {
            for (ItemInstance var13 : var8.getInventory().getItems()) {
               if (var6 == var2) {
                  break;
               }

               if (var13 != null && var13.getTemplate() == var9) {
                  long var14 = Math.min(var2 - var6, var13.getCount());
                  if (var8.getInventory().destroyItem(var13, var14)) {
                     var6 += var14;
                  }
               }
            }
         } else {
            for (long var16 = 0L; var16 < var2; var16++) {
               if (var8.getInventory().destroyItemByItemId(var1, 1L)) {
                  var6++;
               }
            }
         }

         if (var6 > 0L && var4) {
            var8.sendPacket(SystemMessage2.removeItems(var1, var6));
         }

         if (var5) {
            Log.LogItem(var8, Log.ItemLog.RemoveItem, var1, var6);
         }

         return var6;
      } else {
         return var6;
      }
   }

   public static final SystemMessage checkIfCanEquip(PetInstance var0, ItemInstance var1) {
      if (!var1.isEquipable()) {
         return Msg.ITEM_NOT_AVAILABLE_FOR_PETS;
      }

      int var2 = var0.getNpcId();
      return !var1.getTemplate().isPendant()
            && (!PetDataTable.isWolf(var2) || !var1.getTemplate().isForWolf())
            && (!PetDataTable.isHatchling(var2) || !var1.getTemplate().isForHatchling())
            && (!PetDataTable.isStrider(var2) || !var1.getTemplate().isForStrider())
            && (!PetDataTable.isGWolf(var2) || !var1.getTemplate().isForGWolf())
            && (!PetDataTable.isBabyPet(var2) || !var1.getTemplate().isForPetBaby())
            && (!PetDataTable.isImprovedBabyPet(var2) || !var1.getTemplate().isForPetBaby())
         ? Msg.ITEM_NOT_AVAILABLE_FOR_PETS
         : null;
   }

   public static final L2GameServerPacket checkIfCanEquip(Player var0, ItemInstance var1) {
      int var2 = var1.getItemId();
      int var3 = var1.getTemplate().getBodyPart();
      if (var3 == 16384 || var3 == 256 || var3 == 128) {
         if (var2 != var0.getInventory().getPaperdollItemId(7) && CursedWeaponsManager.getInstance().isCursed(var0.getInventory().getPaperdollItemId(7))) {
            return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
         }

         if (var0.isCursedWeaponEquipped() && var2 != var0.getCursedWeaponEquippedId()) {
            return Msg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
         }
      }

      return null;
   }

   public static boolean checkIfCanPickup(Playable var0, ItemInstance var1) {
      Player var2 = var0.getPlayer();
      return var1.getDropTimeOwner() <= System.currentTimeMillis() || var1.getDropPlayers().contains(var2.getObjectId());
   }

   public static boolean canAddItem(Player var0, ItemInstance var1) {
      if (!var0.getInventory().validateWeight(var1)) {
         var0.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
         return false;
      }

      if (!var0.getInventory().validateCapacity(var1)) {
         var0.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
         return false;
      }

      if (!var1.getTemplate().getHandler().pickupItem(var0, var1)) {
         return false;
      }

      PickableAttachment var2 = var1.getAttachment() instanceof PickableAttachment ? (PickableAttachment)var1.getAttachment() : null;
      return var2 == null || var2.canPickUp(var0);
   }

   public static boolean canAddItem(Player var0, ItemTemplate var1, long var2) {
      if (!var0.getInventory().validateWeight(var1, var2)) {
         var0.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
         return false;
      } else if (!var0.getInventory().validateCapacity(var1, var2)) {
         var0.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
         return false;
      } else {
         return true;
      }
   }

   public static final boolean checkIfCanDiscard(Player var0, ItemInstance var1) {
      if (var1.isHeroWeapon()) {
         return false;
      } else if (PetDataTable.isPetControlItem(var1) && var0.isMounted()) {
         return false;
      } else if (var0.getPetControlItem() == var1) {
         return false;
      } else if (var0.getEnchantScroll() == var1) {
         return false;
      } else {
         return var1.isCursed() ? false : !var1.getTemplate().isQuest();
      }
   }

   public static final boolean isBlessedEnchantScroll(int var0) {
      switch (var0) {
         case 6569:
         case 6570:
         case 6571:
         case 6572:
         case 6573:
         case 6574:
         case 6575:
         case 6576:
         case 6577:
         case 6578:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isCrystallEnchantScroll(int var0) {
      switch (var0) {
         case 731:
         case 732:
         case 949:
         case 950:
         case 953:
         case 954:
         case 957:
         case 958:
         case 961:
         case 962:
            return true;
         default:
            return false;
      }
   }

   public static final int getEnchantCrystalId(ItemInstance var0, ItemInstance var1, ItemInstance var2) {
      boolean var3 = false;
      boolean var4 = false;

      for (int var8 : getEnchantScrollId(var0)) {
         if (var1.getItemId() == var8) {
            var3 = true;
            break;
         }
      }

      if (var2 == null) {
         var4 = true;
      } else {
         for (int var12 : getEnchantCatalystId(var0)) {
            if (var12 == var2.getItemId()) {
               var4 = true;
               break;
            }
         }
      }

      if (var3 && var4) {
         switch (var0.getCrystalType().cry) {
            case 0:
               return 0;
            case 1458:
               return 1458;
            case 1459:
               return 1459;
            case 1460:
               return 1460;
            case 1461:
               return 1461;
            case 1462:
               return 1462;
         }
      }

      return -1;
   }

   public static final int[] getEnchantScrollId(ItemInstance var0) {
      if (var0.getTemplate().getType2() == 0) {
         switch (var0.getCrystalType().cry) {
            case 0:
               return new int[]{13540};
            case 1458:
               return new int[]{955, 6575, 957};
            case 1459:
               return new int[]{951, 6573, 953};
            case 1460:
               return new int[]{947, 6571, 949};
            case 1461:
               return new int[]{729, 6569, 731};
            case 1462:
               return new int[]{959, 6577, 961};
         }
      } else if (var0.getTemplate().getType2() == 1 || var0.getTemplate().getType2() == 2) {
         switch (var0.getCrystalType().cry) {
            case 1458:
               return new int[]{956, 6576, 958};
            case 1459:
               return new int[]{952, 6574, 954};
            case 1460:
               return new int[]{948, 6572, 950};
            case 1461:
               return new int[]{730, 6570, 732};
            case 1462:
               return new int[]{960, 6578, 962};
         }
      }

      return new int[0];
   }

   public static final int[] getEnchantCatalystId(ItemInstance var0) {
      if (var0.getTemplate().getType2() == 0) {
         switch (var0.getCrystalType().cry) {
            case 1458:
               return catalyst[0];
            case 1459:
               return catalyst[1];
            case 1460:
               return catalyst[2];
            case 1461:
               return catalyst[3];
            case 1462:
               return catalyst[4];
         }
      } else if (var0.getTemplate().getType2() == 1 || var0.getTemplate().getType2() == 2) {
         switch (var0.getCrystalType().cry) {
            case 1458:
               return catalyst[5];
            case 1459:
               return catalyst[6];
            case 1460:
               return catalyst[7];
            case 1461:
               return catalyst[8];
            case 1462:
               return catalyst[9];
         }
      }

      return new int[]{0, 0, 0};
   }
}
