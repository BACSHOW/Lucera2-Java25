package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Playable;

public class PartySpelled extends L2GameServerPacket {
   private final int _type;
   private final int aeo;
   private final List<PartySpelled.Effect> Fe;

   public PartySpelled(Playable var1, boolean var2) {
      this.aeo = var1.getObjectId();
      this._type = var1.isPet() ? 1 : (var1.isSummon() ? 2 : 0);
      this.Fe = new ArrayList<>();
      if (var2) {
         l2.gameserver.model.Effect[] var3 = var1.getEffectList().getAllFirstEffects();

         for (l2.gameserver.model.Effect.EEffectSlot var7 : l2.gameserver.model.Effect.EEffectSlot.VALUES) {
            for (l2.gameserver.model.Effect var11 : var3) {
               if (var11 != null && var11.isInUse() && var11.getEffectSlot() == var7) {
                  var11.addPartySpelledIcon(this);
               }
            }
         }
      }
   }

   @Override
   protected final void writeImpl() {
      this.writeC(238);
      this.writeD(this._type);
      this.writeD(this.aeo);
      this.writeD(this.Fe.size());

      for (PartySpelled.Effect var2 : this.Fe) {
         this.writeD(var2._skillId);
         this.writeH(var2._level);
         this.writeD(var2._duration);
      }
   }

   public void addPartySpelledEffect(int var1, int var2, int var3) {
      this.Fe.add(new PartySpelled.Effect(var1, var2, var3));
   }

   static class Effect {
      final int _skillId;
      final int _level;
      final int _duration;

      public Effect(int var1, int var2, int var3) {
         this._skillId = var1;
         this._level = var2;
         this._duration = var3;
      }
   }
}
