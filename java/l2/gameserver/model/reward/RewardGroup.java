/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.model.reward.RewardItem;
import l2.gameserver.model.reward.RewardType;

public class RewardGroup
implements Cloneable {
    private double hc;
    private boolean Zq = false;
    private boolean Zr = false;
    private boolean Zl = false;
    private List<RewardData> _items = new ArrayList<RewardData>();
    private double Zs;

    public RewardGroup(double d) {
        this.setChance(d);
    }

    public boolean notRate() {
        return this.Zl;
    }

    public void setNotRate(boolean bl) {
        this.Zl = bl;
    }

    public double getChance() {
        return this.hc;
    }

    public void setChance(double d) {
        this.hc = d;
    }

    public boolean isAdena() {
        return this.Zq;
    }

    public boolean isSealStone() {
        return this.Zr;
    }

    public void setIsAdena(boolean bl) {
        this.Zq = bl;
    }

    public void addData(RewardData rewardData) {
        if (rewardData.getItem().isAdena()) {
            this.Zq = true;
        } else if (rewardData.getItem().isSealStone()) {
            this.Zr = true;
        }
        this.Zs += rewardData.getChance();
        rewardData.setChanceInGroup(this.Zs);
        this._items.add(rewardData);
    }

    public List<RewardData> getItems() {
        return this._items;
    }

    public RewardGroup clone() {
        RewardGroup rewardGroup = new RewardGroup(this.hc);
        for (RewardData rewardData : this._items) {
            rewardGroup.addData(rewardData.clone());
        }
        return rewardGroup;
    }

    public List<RewardItem> roll(RewardType rewardType, Player player, double d, boolean bl, boolean bl2) {
        switch (rewardType) {
            case NOT_RATED_GROUPED: 
            case NOT_RATED_NOT_GROUPED: {
                return this.rollItems(d, 1.0, 1.0);
            }
            case SWEEP: {
                return this.a(Config.RATE_DROP_SPOIL, player.getRateSpoil(), d);
            }
            case RATED_GROUPED: {
                if (this.Zq) {
                    return this.b(d, player.getRateAdena());
                }
                if (this.Zr) {
                    return this.a(d, player.getRateItems());
                }
                if (bl) {
                    return this.rollItems(d, Config.RATE_DROP_RAIDBOSS * (double)player.getBonus().getDropRaidItems(), 1.0);
                }
                if (bl2) {
                    return this.rollItems(d, Config.RATE_DROP_SIEGE_GUARD, 1.0);
                }
                return this.rollItems(d, Config.RATE_DROP_ITEMS, player.getRateItems());
            }
        }
        return Collections.emptyList();
    }

    private List<RewardItem> a(double d, double d2) {
        List<RewardItem> list = this.rollItems(d, Config.RATE_DROP_SEAL_STONES, d2);
        for (RewardItem rewardItem : list) {
            rewardItem.isSealStone = true;
        }
        return list;
    }

    public List<RewardItem> rollItems(double d, double d2, double d3) {
        if (d <= 0.0) {
            return Collections.emptyList();
        }
        double d4 = this.Zl ? Math.min(d, 1.0) : d2 * d3 * d;
        double d5 = Math.ceil(d4);
        boolean bl = true;
        ArrayList<RewardItem> arrayList = new ArrayList<RewardItem>(this._items.size() * 3 / 2);
        long l = 0L;
        while ((double)l < d5) {
            double d6 = d4 - (double)l;
            if ((double)Rnd.get(1, 1000000) <= this.hc * Math.min(d6, 1.0)) {
                if (Config.ALT_MULTI_DROP) {
                    this.a(this._items, arrayList, 1.0, bl);
                } else {
                    this.a(this._items, arrayList, Math.max(d6, 1.0), bl);
                    break;
                }
            }
            bl = false;
            ++l;
        }
        return arrayList;
    }

    private List<RewardItem> a(double d, double d2, double d3) {
        if (d3 <= 0.0) {
            return Collections.emptyList();
        }
        double d4 = this.Zl ? Math.min(d3, 1.0) : d * d2 * d3;
        double d5 = Math.ceil(d4);
        boolean bl = true;
        ArrayList<RewardItem> arrayList = new ArrayList<RewardItem>(this._items.size() * 3 / 2);
        long l = 0L;
        while ((double)l < d5) {
            if ((double)Rnd.get(1, 1000000) <= this.hc * Math.min(d4 - (double)l, 1.0)) {
                this.a(this._items, arrayList, 1.0, bl);
            }
            bl = false;
            ++l;
        }
        return arrayList;
    }

    private List<RewardItem> b(double d, double d2) {
        return this.b(d, Config.RATE_DROP_ADENA, d2);
    }

    private List<RewardItem> b(double d, double d2, double d3) {
        double d4 = this.hc;
        if (d > 10.0) {
            d *= this.hc / 1000000.0;
            d4 = 1000000.0;
        }
        if (d <= 0.0) {
            return Collections.emptyList();
        }
        if ((double)Rnd.get(1, 1000000) > d4) {
            return Collections.emptyList();
        }
        double d5 = d2 * d3 * d;
        ArrayList<RewardItem> arrayList = new ArrayList<RewardItem>(this._items.size());
        this.a(this._items, arrayList, d5, true);
        for (RewardItem rewardItem : arrayList) {
            rewardItem.isAdena = true;
        }
        return arrayList;
    }

    private void a(List<RewardData> list, List<RewardItem> list2, double d, boolean bl) {
        int n = Rnd.get(0, (int)Math.max(this.Zs, 1000000.0));
        for (RewardData rewardData : list) {
            long l;
            if (!bl && rewardData.onePassOnly() || !((double)n < rewardData.getChanceInGroup()) || !((double)n > rewardData.getChanceInGroup() - rewardData.getChance())) continue;
            double d2 = rewardData.notRate() ? 1.0 : d;
            long l2 = (long)Math.floor((double)rewardData.getMinDrop() * d2);
            if (l2 != (l = (long)Math.ceil((double)rewardData.getMaxDrop() * d2))) {
                l2 = Rnd.get(l2, l);
            }
            RewardItem rewardItem = null;
            for (RewardItem rewardItem2 : list2) {
                if (rewardData.getItemId() != rewardItem2.itemId) continue;
                rewardItem = rewardItem2;
                break;
            }
            if (rewardItem == null) {
                rewardItem = new RewardItem(rewardData.getItemId());
                list2.add(rewardItem);
                rewardItem.count = l2;
                break;
            }
            if (rewardData.notRate()) break;
            rewardItem.count = SafeMath.addAndLimit(rewardItem.count, l2);
            break;
        }
    }
}

