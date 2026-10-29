package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;

public class PrivateStoreManageListSell extends L2GameServerPacket {
   private int acz;
   private long adQ;
   private boolean adj;
   private List<TradeItem> HM;
   private List<TradeItem> alB;

   public PrivateStoreManageListSell(Player var1, boolean var2) {
      this.acz = var1.getObjectId();
      this.adQ = var1.getAdena();
      this.adj = var2;
      this.alB = var1.getSellList(this.adj);
      this.HM = new ArrayList<>();

      for (TradeItem var4 : this.alB) {
         if (var4.getCount() <= 0L) {
            this.alB.remove(var4);
         } else {
            ItemInstance var5 = var1.getInventory().getItemByObjectId(var4.getObjectId());
            if (var5 == null) {
               var5 = var1.getInventory().getItemByItemId(var4.getItemId());
            }

            if (var5 != null && var5.canBeTraded(var1) && var5.getItemId() != 57) {
               var4.setCount(Math.min(var5.getCount(), var4.getCount()));
            } else {
               this.alB.remove(var4);
            }
         }
      }

      ItemInstance[] var11 = var1.getInventory().getItems();

      label44:
      for (ItemInstance var7 : var11) {
         if (var7.canBeTraded(var1) && var7.getItemId() != 57) {
            for (TradeItem var9 : this.alB) {
               if (var9.getObjectId() == var7.getObjectId()) {
                  if (var9.getCount() != var7.getCount()) {
                     TradeItem var10 = new TradeItem(var7);
                     var10.setCount(var7.getCount() - var9.getCount());
                     this.HM.add(var10);
                  }
                  continue label44;
               }
            }

            this.HM.add(new TradeItem(var7));
         }
      }
   }

   @Override
   protected final void writeImpl() {
      this.writeC(154);
      this.writeD(this.acz);
      this.writeD(this.adj ? 1 : 0);
      this.writeD((int)this.adQ);
      this.writeD(this.HM.size());

      for (TradeItem var2 : this.HM) {
         this.writeD(var2.getItem().getType2ForPackets());
         this.writeD(var2.getObjectId());
         this.writeD(var2.getItemId());
         this.writeD((int)var2.getCount());
         this.writeH(0);
         this.writeH(var2.getEnchantLevel());
         this.writeH(0);
         this.writeD(var2.getItem().getBodyPart());
         this.writeD((int)var2.getStorePrice());
      }

      this.writeD(this.alB.size());

      for (TradeItem var4 : this.alB) {
         this.writeD(var4.getItem().getType2ForPackets());
         this.writeD(var4.getObjectId());
         this.writeD(var4.getItemId());
         this.writeD((int)var4.getCount());
         this.writeH(0);
         this.writeH(var4.getEnchantLevel());
         this.writeH(0);
         this.writeD(var4.getItem().getBodyPart());
         this.writeD((int)var4.getOwnersPrice());
         this.writeD((int)var4.getStorePrice());
      }
   }
}
