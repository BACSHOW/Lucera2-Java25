/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.reward;

import java.util.ArrayList;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.reward.RewardItem;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.ArrayUtils;

public class RewardData
implements Cloneable {
    private ItemTemplate KP;
    private boolean Zl;
    private boolean Zm;
    private long Zn;
    private long Zo;
    private double hc;
    private double Zp;

    public RewardData(int n) {
        this.KP = ItemHolder.getInstance().getTemplate(n);
        this.setNotRate(this.KP.isArrow() || Config.NO_RATE_EQUIPMENT && this.KP.isEquipment() || Config.NO_RATE_KEY_MATERIAL && this.KP.isKeyMatherial() || Config.NO_RATE_RECIPES && this.KP.isRecipe(), ArrayUtils.contains(Config.NO_RATE_ITEMS, n));
    }

    public RewardData(int n, long l, long l2, double d) {
        this(n);
        this.Zn = l;
        this.Zo = l2;
        this.hc = d;
    }

    public boolean notRate() {
        return this.Zl;
    }

    public boolean onePassOnly() {
        return this.Zm;
    }

    public void setNotRate(boolean bl, boolean bl2) {
        this.Zl = bl || bl2;
        this.Zm = bl2;
    }

    public int getItemId() {
        return this.KP.getItemId();
    }

    public ItemTemplate getItem() {
        return this.KP;
    }

    public long getMinDrop() {
        return this.Zn;
    }

    public void setMinDrop(long l) {
        this.Zn = l;
    }

    public long getMaxDrop() {
        return this.Zo;
    }

    public void setMaxDrop(long l) {
        this.Zo = l;
    }

    public double getChance() {
        return this.hc;
    }

    public void setChance(double d) {
        this.hc = d;
    }

    public double getChanceInGroup() {
        return this.Zp;
    }

    public void setChanceInGroup(double d) {
        this.Zp = d;
    }

    public String toString() {
        return "ItemID: " + this.getItem() + " Min: " + this.getMinDrop() + " Max: " + this.getMaxDrop() + " Chance: " + this.getChance() / 10000.0 + "%";
    }

    public RewardData clone() {
        return new RewardData(this.getItemId(), this.getMinDrop(), this.getMaxDrop(), this.getChance());
    }

    public boolean equals(Object object) {
        if (object instanceof RewardData) {
            RewardData rewardData = (RewardData)object;
            return rewardData.getItemId() == this.getItemId();
        }
        return false;
    }

    public List<RewardItem> roll(Player player, double d) {
        double d2 = 1.0;
        d2 = this.KP.isAdena() ? Config.RATE_DROP_ADENA * player.getRateAdena() : Config.RATE_DROP_ITEMS * (player != null ? player.getRateItems() : 1.0);
        return this.roll(d2 * d);
    }

    public List<RewardItem> roll(double d) {
        double d2 = Math.ceil(d);
        ArrayList<RewardItem> arrayList = new ArrayList<RewardItem>(1);
        RewardItem rewardItem = null;
        int n = 0;
        while ((double)n < d2) {
            if ((double)Rnd.get(1000000) <= this.hc * Math.min(d - (double)n, 1.0)) {
                long l = this.getMinDrop() >= this.getMaxDrop() ? this.getMinDrop() : Rnd.get(this.getMinDrop(), this.getMaxDrop());
                if (rewardItem == null) {
                    rewardItem = new RewardItem(this.KP.getItemId());
                    arrayList.add(rewardItem);
                    rewardItem.count = l;
                } else {
                    rewardItem.count = SafeMath.addAndLimit(rewardItem.count, l);
                }
            }
            ++n;
        }
        return arrayList;
    }
}

