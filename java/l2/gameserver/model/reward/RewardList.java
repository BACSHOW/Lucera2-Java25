/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.reward;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.model.reward.RewardGroup;
import l2.gameserver.model.reward.RewardItem;
import l2.gameserver.model.reward.RewardType;

public class RewardList
extends ArrayList<RewardGroup> {
    public static final int MAX_CHANCE = 1000000;
    private final RewardType Zt;
    private final boolean Ht;

    public RewardList(RewardType rewardType, boolean bl) {
        super(5);
        this.Zt = rewardType;
        this.Ht = bl;
    }

    public List<RewardItem> roll(Player player) {
        return this.roll(player, 1.0, false, false);
    }

    public List<RewardItem> roll(Player player, double d) {
        return this.roll(player, d, false, false);
    }

    public List<RewardItem> roll(Player player, double d, boolean bl) {
        return this.roll(player, d, bl, false);
    }

    public List<RewardItem> roll(Player player, double d, boolean bl, boolean bl2) {
        ArrayList<RewardItem> arrayList = new ArrayList<RewardItem>(this.size());
        for (RewardGroup rewardGroup : this) {
            List<RewardItem> list = rewardGroup.roll(this.Zt, player, d, bl, bl2);
            if (list.isEmpty()) continue;
            for (RewardItem rewardItem : list) {
                arrayList.add(rewardItem);
            }
        }
        return arrayList;
    }

    public boolean validate() {
        for (RewardGroup rewardGroup : this) {
            int n = 0;
            for (RewardData rewardData : rewardGroup.getItems()) {
                n = (int)((double)n + rewardData.getChance());
            }
            if (n <= 1000000) {
                return true;
            }
            double d = 1000000 / n;
            for (RewardData rewardData : rewardGroup.getItems()) {
                double d2 = rewardData.getChance() * d;
                rewardData.setChance(d2);
                rewardGroup.setChance(1000000.0);
            }
        }
        return false;
    }

    public boolean isAutoLoot() {
        return this.Ht;
    }

    public RewardType getType() {
        return this.Zt;
    }
}

