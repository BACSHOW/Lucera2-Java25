package l2.gameserver.model.items.listeners;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.data.xml.holder.ArmorSetsHolder;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.ArmorSet;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.SkillList;

public final class ArmorSetListener implements OnEquipListener {
   private static final ArmorSetListener XQ = new ArmorSetListener();

   public static ArmorSetListener getInstance() {
      return XQ;
   }

   @Override
   public void onEquip(int var1, ItemInstance var2, Playable var3) {
      if (var2.isEquipable()) {
         Player var4 = (Player)var3;
         ItemInstance var5 = var4.getInventory().getPaperdollItem(10);
         if (var5 != null) {
            ArmorSet var6 = ArmorSetsHolder.getInstance().getArmorSet(var5.getItemId());
            if (var6 != null) {
               boolean var7 = false;
               if (var6.containItem(var1, var2.getItemId())) {
                  if (var6.containAll(var4)) {
                     for (Skill var10 : var6.getSkills()) {
                        var4.addSkill(var10, false);
                        var7 = true;
                     }

                     if (var6.containShield(var4)) {
                        for (Skill var19 : var6.getShieldSkills()) {
                           var4.addSkill(var19, false);
                           var7 = true;
                        }
                     }

                     if (var6.isEnchanted6(var4)) {
                        for (Skill var20 : var6.getEnchant6skills()) {
                           var4.addSkill(var20, false);
                           var7 = true;
                        }
                     }

                     if (var6.isEnchanted10(var4)) {
                        for (Skill var21 : var6.getEnchant10skills()) {
                           var4.addSkill(var21, false);
                           var7 = true;
                        }
                     }
                  }
               } else if (var6.containShield(var2.getItemId()) && var6.containAll(var4)) {
                  for (Skill var22 : var6.getShieldSkills()) {
                     var4.addSkill(var22, false);
                     var7 = true;
                  }
               }

               if (var7) {
                  var4.sendPacket(new SkillList(var4));
                  var4.updateStats();
               }
            }
         }
      }
   }

   @Override
   public void onUnequip(int var1, ItemInstance var2, Playable var3) {
      if (var2.isEquipable()) {
         Player var4 = (Player)var3;
         boolean var5 = false;
         List var6 = new ArrayList(1);
         List var7 = new ArrayList(1);
         List var8 = new ArrayList(1);
         List var9 = new ArrayList(1);
         if (var1 == 10) {
            ArmorSet var10 = ArmorSetsHolder.getInstance().getArmorSet(var2.getItemId());
            if (var10 == null) {
               return;
            }

            var5 = true;
            var6 = var10.getSkills();
            var7 = var10.getShieldSkills();
            var8 = var10.getEnchant6skills();
            var9 = var10.getEnchant10skills();
         } else {
            ItemInstance var13 = var4.getInventory().getPaperdollItem(10);
            if (var13 == null) {
               return;
            }

            ArmorSet var11 = ArmorSetsHolder.getInstance().getArmorSet(var13.getItemId());
            if (var11 == null) {
               return;
            }

            if (var11.containItem(var1, var2.getItemId())) {
               var5 = true;
               var6 = var11.getSkills();
               var7 = var11.getShieldSkills();
               var8 = var11.getEnchant6skills();
               var9 = var11.getEnchant10skills();
            } else if (var11.containShield(var2.getItemId())) {
               var5 = true;
               var7 = var11.getShieldSkills();
            }
         }

         boolean var14 = false;
         if (var5) {
            for (Object __raw_var12_119 : (Iterable<?>)(Object)(var6)) {
               Skill var12 = (Skill)__raw_var12_119;
               var4.removeSkill(var12, false);
               var14 = true;
            }

            for (Object __raw_var19_124 : (Iterable<?>)(Object)(var7)) {
               Skill var19 = (Skill)__raw_var19_124;
               var4.removeSkill(var19);
               var14 = true;
            }

            for (Object __raw_var20_129 : (Iterable<?>)(Object)(var8)) {
               Skill var20 = (Skill)__raw_var20_129;
               var4.removeSkill(var20);
               var14 = true;
            }

            for (Object __raw_var21_134 : (Iterable<?>)(Object)(var9)) {
               Skill var21 = (Skill)__raw_var21_134;
               var4.removeSkill(var21);
               var14 = true;
            }
         }

         if (var14) {
            var4.sendPacket(new SkillList(var4));
            var4.updateStats();
         }
      }
   }
}
