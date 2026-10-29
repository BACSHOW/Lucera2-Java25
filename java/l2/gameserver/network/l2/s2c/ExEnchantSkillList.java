package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import l2.gameserver.data.xml.holder.EnchantSkillHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.SkillEnchant;

public class ExEnchantSkillList extends L2GameServerPacket {
   private final List<ExEnchantSkillList.SkillEnchantEntry> _skills = new ArrayList<>();

   public static ExEnchantSkillList packetFor(Player var0) {
      Collection var1 = var0.getAllSkills();
      ExEnchantSkillList var2 = new ExEnchantSkillList();

      for (Object __raw_var4_19 : (Iterable<?>)(Object)(var1)) {
         Skill var4 = (Skill)__raw_var4_19;
         int var5 = var4.getId();
         int var6 = var4.getLevel();
         int var7 = var4.getBaseLevel();
         if (var6 >= var7) {
            Map var8 = EnchantSkillHolder.getInstance().getRoutesOf(var5);
            if (var8 != null && !var8.isEmpty()) {
               SkillEnchant var9 = EnchantSkillHolder.getInstance().getSkillEnchant(var5, var6);
               if (var6 == var7) {
                  for (Object __raw_var15_28 : (Iterable<?>)(Object)(var8.values())) {
                     Map var15 = (Map)__raw_var15_28;
                     for (Object __raw_var13_29 : (Iterable<?>)(Object)(var15.values())) {
                        SkillEnchant var13 = (SkillEnchant)__raw_var13_29;
                        if (var13.getEnchantLevel() == 1) {
                           var2.addSkill(var13.getSkillId(), var13.getSkillLevel(), var13.getSp(), var13.getExp());
                        }
                     }
                  }
               } else if (var9 != null) {
                  Map var10 = (Map)var8.get(var9.getRouteId());
                  int var11 = var6 + 1;
                  SkillEnchant var12 = (SkillEnchant)var10.get(var11);
                  if (var12 != null) {
                     var2.addSkill(var12.getSkillId(), var12.getSkillLevel(), var12.getSp(), var12.getExp());
                  }
               }
            }
         }
      }

      return var2;
   }

   public void addSkill(int var1, int var2, int var3, long var4) {
      this._skills.add(new ExEnchantSkillList.SkillEnchantEntry(var1, var2, var3, var4));
   }

   @Override
   protected final void writeImpl() {
      this.writeEx(23);
      this.writeD(this._skills.size());

      for (ExEnchantSkillList.SkillEnchantEntry var2 : this._skills) {
         var2.write();
      }
   }

   class SkillEnchantEntry {
      private final int _skillId;
      private final int _skillLevel;
      private final int agB;
      private final long agC;

      public SkillEnchantEntry(int var2, int var3, int var4, long var5) {
         this._skillId = var2;
         this._skillLevel = var3;
         this.agB = var4;
         this.agC = var5;
      }

      private void write() {
         ExEnchantSkillList.this.writeD(this._skillId);
         ExEnchantSkillList.this.writeD(this._skillLevel);
         ExEnchantSkillList.this.writeD(this.agB);
         ExEnchantSkillList.this.writeQ(this.agC);
      }
   }
}
