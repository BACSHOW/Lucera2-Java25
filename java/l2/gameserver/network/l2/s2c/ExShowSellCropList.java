package l2.gameserver.network.l2.s2c;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.manor.CropProcure;

public class ExShowSellCropList extends L2GameServerPacket {
   private int Gh = 1;
   private Map<Integer, ItemInstance> ain;
   private Map<Integer, CropProcure> aie;

   public ExShowSellCropList(Player var1, int var2, List<CropProcure> var3) {
      this.Gh = var2;
      this.aie = new TreeMap<>();
      this.ain = new TreeMap<>();

      for (int var6 : Manor.getInstance().getAllCrops()) {
         ItemInstance var7 = var1.getInventory().getItemByItemId(var6);
         if (var7 != null) {
            this.ain.put(var6, var7);
         }
      }

      for (CropProcure var9 : var3) {
         if (this.ain.containsKey(var9.getId()) && var9.getAmount() > 0L) {
            this.aie.put(var9.getId(), var9);
         }
      }
   }

   @Override
   public void writeImpl() {
      this.writeEx(33);
      this.writeD(this.Gh);
      this.writeD(this.ain.size());

      for (ItemInstance var2 : this.ain.values()) {
         this.writeD(var2.getObjectId());
         this.writeD(var2.getItemId());
         this.writeD(Manor.getInstance().getSeedLevelByCrop(var2.getItemId()));
         this.writeC(1);
         this.writeD(Manor.getInstance().getRewardItem(var2.getItemId(), 1));
         this.writeC(1);
         this.writeD(Manor.getInstance().getRewardItem(var2.getItemId(), 2));
         if (this.aie.containsKey(var2.getItemId())) {
            CropProcure var3 = this.aie.get(var2.getItemId());
            this.writeD(this.Gh);
            this.writeD((int)var3.getAmount());
            this.writeD((int)var3.getPrice());
            this.writeC(var3.getReward());
         } else {
            this.writeD(-1);
            this.writeD(0);
            this.writeD(0);
            this.writeC(0);
         }

         this.writeD((int)var2.getCount());
      }
   }
}
