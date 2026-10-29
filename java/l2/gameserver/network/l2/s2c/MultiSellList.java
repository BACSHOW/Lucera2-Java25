/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.model.base.MultiSellEntry;
import l2.gameserver.model.base.MultiSellIngredient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.item.ItemTemplate;

public class MultiSellList
extends L2GameServerPacket {
    private final int _page;
    private final int ajP;
    private final int wr;
    private final List<MultiSellEntry> adS;

    public MultiSellList(MultiSellHolder.MultiSellListContainer multiSellListContainer, int n, int n2) {
        this.adS = multiSellListContainer.getEntries();
        this.wr = multiSellListContainer.getListId();
        this._page = n;
        this.ajP = n2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(208);
        this.writeD(this.wr);
        this.writeD(this._page);
        this.writeD(this.ajP);
        this.writeD(Config.MULTISELL_SIZE);
        this.writeD(this.adS.size());
        for (MultiSellEntry multiSellEntry : this.adS) {
            ItemTemplate itemTemplate;
            int n;
            List<MultiSellIngredient> list = MultiSellList.l(multiSellEntry.getIngredients());
            this.writeD(multiSellEntry.getEntryId());
            this.writeD(0);
            this.writeD(0);
            this.writeC(1);
            this.writeH(multiSellEntry.getProduction().size());
            this.writeH(list.size());
            for (MultiSellIngredient multiSellIngredient : multiSellEntry.getProduction()) {
                n = multiSellIngredient.getItemId();
                itemTemplate = n > 0 ? ItemHolder.getInstance().getTemplate(multiSellIngredient.getItemId()) : null;
                this.writeH(n);
                this.writeD(n > 0 ? itemTemplate.getBodyPart() : 0);
                this.writeH(n > 0 ? itemTemplate.getType2ForPackets() : 0);
                this.writeD((int)multiSellIngredient.getItemCount());
                this.writeH(multiSellIngredient.getItemEnchant());
                this.writeD(0);
                this.writeD(0);
            }
            for (MultiSellIngredient multiSellIngredient : list) {
                n = multiSellIngredient.getItemId();
                itemTemplate = n > 0 ? ItemHolder.getInstance().getTemplate(multiSellIngredient.getItemId()) : null;
                this.writeH(n);
                this.writeH(n > 0 ? itemTemplate.getType2() : 65535);
                this.writeD((int)multiSellIngredient.getItemCount());
                this.writeH(multiSellIngredient.getItemEnchant());
                this.writeD(0);
                this.writeD(0);
            }
        }
    }

    private static List<MultiSellIngredient> l(List<MultiSellIngredient> list) {
        int n = 0;
        for (MultiSellIngredient cloneable2 : list) {
            if (cloneable2.getItemCount() <= Integer.MAX_VALUE) continue;
            ++n;
        }
        if (n == 0) {
            return list;
        }
        ArrayList<MultiSellIngredient> arrayList = new ArrayList<MultiSellIngredient>(list.size() + n);
        for (MultiSellIngredient multiSellIngredient : list) {
            multiSellIngredient = multiSellIngredient.clone();
            while (multiSellIngredient.getItemCount() > Integer.MAX_VALUE) {
                MultiSellIngredient multiSellIngredient2 = multiSellIngredient.clone();
                multiSellIngredient2.setItemCount(2000000000L);
                arrayList.add(multiSellIngredient2);
                multiSellIngredient.setItemCount(multiSellIngredient.getItemCount() - 2000000000L);
            }
            if (multiSellIngredient.getItemCount() <= 0L) continue;
            arrayList.add(multiSellIngredient);
        }
        return arrayList;
    }
}

