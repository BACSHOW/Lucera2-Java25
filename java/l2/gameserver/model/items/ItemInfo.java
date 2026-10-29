/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;

public class ItemInfo {
    private int WD;
    private int WE;
    private int WF;
    private int objectId;
    private int itemId;
    private long count;
    private int WG;
    private int WH;
    private boolean WI;
    private int WJ;
    private int WK;
    private int WL;
    private int WM;
    private int WN;
    private int WO;
    private int WP = Element.NONE.getId();
    private int WQ;
    private int WR;
    private int WS;
    private int WT;
    private int WU;
    private int WV;
    private int WW;
    private int WX;
    private int WY;
    private int[] WZ = ItemInstance.EMPTY_ENCHANT_OPTIONS;
    private ItemTemplate Xa;

    public ItemInfo() {
    }

    public ItemInfo(ItemInstance itemInstance) {
        this.setOwnerId(itemInstance.getOwnerId());
        this.setObjectId(itemInstance.getObjectId());
        this.setItemId(itemInstance.getItemId());
        this.setCount(itemInstance.getCount());
        this.setCustomType1(itemInstance.getBlessed());
        this.setEquipped(itemInstance.isEquipped());
        this.setEnchantLevel(itemInstance.getEnchantLevel());
        this.setCustomType2(itemInstance.getDamaged());
        this.setVariationStat1(itemInstance.getVariationStat1());
        this.setVariationStat2(itemInstance.getVariationStat2());
        this.setShadowLifeTime(itemInstance.getDuration());
        this.setAttackElement(itemInstance.getAttackElement().getId());
        this.setAttackElementValue(itemInstance.getAttackElementValue());
        this.setDefenceFire(itemInstance.getDefenceFire());
        this.setDefenceWater(itemInstance.getDefenceWater());
        this.setDefenceWind(itemInstance.getDefenceWind());
        this.setDefenceEarth(itemInstance.getDefenceEarth());
        this.setDefenceHoly(itemInstance.getDefenceHoly());
        this.setDefenceUnholy(itemInstance.getDefenceUnholy());
        this.setEquipSlot(itemInstance.getEquipSlot());
        this.setTemporalLifeTime(itemInstance.getPeriod());
        this.setEnchantOptions(itemInstance.getEnchantOptions());
    }

    public ItemTemplate getItem() {
        return this.Xa;
    }

    public void setOwnerId(int n) {
        this.WD = n;
    }

    public void setLastChange(int n) {
        this.WE = n;
    }

    public void setType1(int n) {
        this.WF = n;
    }

    public void setObjectId(int n) {
        this.objectId = n;
    }

    public void setItemId(int n) {
        this.itemId = n;
        this.Xa = n > 0 ? ItemHolder.getInstance().getTemplate(this.getItemId()) : null;
        if (this.Xa != null) {
            this.setType1(this.Xa.getType1());
            this.setType2(this.Xa.getType2ForPackets());
            this.setBodyPart(this.Xa.getBodyPart());
        }
    }

    public void setCount(long l) {
        this.count = l;
    }

    public void setType2(int n) {
        this.WG = n;
    }

    public void setCustomType1(int n) {
        this.WH = n;
    }

    public void setEquipped(boolean bl) {
        this.WI = bl;
    }

    public void setBodyPart(int n) {
        this.WJ = n;
    }

    public void setEnchantLevel(int n) {
        this.WK = n;
    }

    public void setCustomType2(int n) {
        this.WL = n;
    }

    public void setVariationStat1(int n) {
        this.WM = n;
    }

    public void setVariationStat2(int n) {
        this.WN = n;
    }

    public void setShadowLifeTime(int n) {
        this.WO = n;
    }

    public void setAttackElement(int n) {
        this.WP = n;
    }

    public void setAttackElementValue(int n) {
        this.WQ = n;
    }

    public void setDefenceFire(int n) {
        this.WR = n;
    }

    public void setDefenceWater(int n) {
        this.WS = n;
    }

    public void setDefenceWind(int n) {
        this.WT = n;
    }

    public void setDefenceEarth(int n) {
        this.WU = n;
    }

    public void setDefenceHoly(int n) {
        this.WV = n;
    }

    public void setDefenceUnholy(int n) {
        this.WW = n;
    }

    public void setEquipSlot(int n) {
        this.WX = n;
    }

    public void setTemporalLifeTime(int n) {
        this.WY = n;
    }

    public int getOwnerId() {
        return this.WD;
    }

    public int getLastChange() {
        return this.WE;
    }

    public int getType1() {
        return this.WF;
    }

    public int getObjectId() {
        return this.objectId;
    }

    public int getItemId() {
        return this.itemId;
    }

    public long getCount() {
        return this.count;
    }

    public int getType2() {
        return this.WG;
    }

    public int getCustomType1() {
        return this.WH;
    }

    public boolean isEquipped() {
        return this.WI;
    }

    public int getBodyPart() {
        return this.WJ;
    }

    public int getEnchantLevel() {
        return this.WK;
    }

    public int getVariationStat1() {
        return this.WM;
    }

    public int getVariationStat2() {
        return this.WN;
    }

    public int getShadowLifeTime() {
        return this.WO;
    }

    public int getCustomType2() {
        return this.WL;
    }

    public int getAttackElement() {
        return this.WP;
    }

    public int getAttackElementValue() {
        return this.WQ;
    }

    public int getDefenceFire() {
        return this.WR;
    }

    public int getDefenceWater() {
        return this.WS;
    }

    public int getDefenceWind() {
        return this.WT;
    }

    public int getDefenceEarth() {
        return this.WU;
    }

    public int getDefenceHoly() {
        return this.WV;
    }

    public int getDefenceUnholy() {
        return this.WW;
    }

    public int getEquipSlot() {
        return this.WX;
    }

    public int getTemporalLifeTime() {
        return this.WY;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        if (this.getObjectId() == 0) {
            return this.getItemId() == ((ItemInfo)object).getItemId();
        }
        return this.getObjectId() == ((ItemInfo)object).getObjectId();
    }

    public int[] getEnchantOptions() {
        return this.WZ;
    }

    public void setEnchantOptions(int[] nArray) {
        this.WZ = nArray;
    }
}

