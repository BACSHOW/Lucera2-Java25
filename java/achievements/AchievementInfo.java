/*
 * Decompiled with CFR 0.152.
 */
package achievements;

import achievements.AchievementCondition;
import achievements.AchievementMetricType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import l2.commons.time.cron.NextTime;
import l2.gameserver.data.StringHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.reward.RewardData;

public class AchievementInfo {
    private final int _id;
    private final AchievementMetricType ac;
    private final long ad;
    private final String ae;
    private final NextTime af;
    private AchievementInfoCategory ag;
    private int p = Integer.MAX_VALUE;
    private int s = Integer.MIN_VALUE;
    private List<AchievementCondition> ah = new ArrayList<AchievementCondition>();
    private Map<Integer, AchievementInfoLevel> ai = new TreeMap<Integer, AchievementInfoLevel>();
    private List<AchievementInfoLevel> aj = new ArrayList<AchievementInfoLevel>();
    private String _icon = "icon.etc_plan_i00";

    public AchievementInfo(int n, AchievementMetricType achievementMetricType, long l, String string, NextTime nextTime) {
        this._id = n;
        this.ac = achievementMetricType;
        this.ad = l;
        this.ae = string;
        this.af = nextTime;
        this.s = 0;
    }

    public String getIcon() {
        return this._icon;
    }

    public void setIcon(String string) {
        this._icon = string;
    }

    public AchievementInfoCategory getCategory() {
        return this.ag;
    }

    public void setCategory(AchievementInfoCategory achievementInfoCategory) {
        this.ag = achievementInfoCategory;
    }

    public long nextResetTimeMills() {
        if (this.af == null) {
            return -1L;
        }
        return this.af.next(System.currentTimeMillis());
    }

    public int getId() {
        return this._id;
    }

    public void addCond(AchievementCondition achievementCondition) {
        this.ah.add(achievementCondition);
    }

    public void addLevel(AchievementInfoLevel achievementInfoLevel) {
        if (achievementInfoLevel.getLevel() > this.s) {
            this.s = achievementInfoLevel.getLevel();
        }
        if (achievementInfoLevel.getLevel() < this.p) {
            this.p = achievementInfoLevel.getLevel();
        }
        this.ai.put(achievementInfoLevel.getLevel(), achievementInfoLevel);
        this.aj.add(achievementInfoLevel);
        Collections.sort(this.aj);
    }

    public AchievementInfoLevel addLevel(int n, int n2, String string, boolean bl) {
        AchievementInfoLevel achievementInfoLevel = new AchievementInfoLevel(this, n, n2, string, bl);
        this.addLevel(achievementInfoLevel);
        return achievementInfoLevel;
    }

    public int getMaxLevel() {
        return this.s;
    }

    public List<AchievementCondition> getCondList() {
        return this.ah;
    }

    public void setCondList(List<AchievementCondition> list) {
        this.ah = list;
    }

    public AchievementMetricType getMetricType() {
        return this.ac;
    }

    public long getMetricNotifyDelay() {
        return this.ad;
    }

    public List<AchievementInfoLevel> getLevels() {
        return this.aj;
    }

    public AchievementInfoLevel getLevel(int n) {
        return this.ai.get(n);
    }

    public String getNameAddr() {
        return this.ae;
    }

    public String getName(Player player) {
        return StringHolder.getInstance().getNotNull(player, this.getNameAddr());
    }

    public boolean testConds(Player player, Object ... objectArray) {
        for (AchievementCondition achievementCondition : this.getCondList()) {
            if (achievementCondition.test(player, objectArray)) continue;
            return false;
        }
        return true;
    }

    public static class AchievementInfoLevel
    implements Comparable<AchievementInfoLevel> {
        private final AchievementInfo al;
        private final int _level;
        private final int _value;
        private final String am;
        private final boolean an;
        private List<RewardData> ao = new ArrayList<RewardData>();
        private List<AchievementCondition> ap = new ArrayList<AchievementCondition>();

        public AchievementInfoLevel(AchievementInfo achievementInfo, int n, int n2, String string, boolean bl) {
            this.al = achievementInfo;
            this._level = n;
            this._value = n2;
            this.am = string;
            this.an = bl;
        }

        public AchievementInfo getAchievementInfo() {
            return this.al;
        }

        public boolean isResetMetric() {
            return this.an;
        }

        public int getLevel() {
            return this._level;
        }

        public int getValue() {
            return this._value;
        }

        public String getDescAddr() {
            return this.am;
        }

        public List<RewardData> getRewardDataList() {
            return this.ao;
        }

        public List<AchievementCondition> getCondList() {
            return this.ap;
        }

        public void addCond(AchievementCondition achievementCondition) {
            this.ap.add(achievementCondition);
        }

        public void addRewardData(RewardData rewardData) {
            this.ao.add(rewardData);
        }

        public String getDesc(Player player) {
            return StringHolder.getInstance().getNotNull(player, this.getDescAddr());
        }

        public boolean testConds(Player player, Object ... objectArray) {
            for (AchievementCondition achievementCondition : this.getCondList()) {
                if (achievementCondition.test(player, objectArray)) continue;
                return false;
            }
            return true;
        }

        @Override
        public int compareTo(AchievementInfoLevel achievementInfoLevel) {
            if (this.getAchievementInfo().getId() != achievementInfoLevel.getAchievementInfo().getId()) {
                return Integer.compare(this.getAchievementInfo().getId(), achievementInfoLevel.getAchievementInfo().getId());
            }
            return Integer.compare(this.getLevel(), achievementInfoLevel.getLevel());
        }
    }

    public static class AchievementInfoCategory {
        private final String _name;
        private final String ak;

        public AchievementInfoCategory(String string, String string2) {
            this._name = string;
            this.ak = string2;
        }

        public String getName() {
            return this._name;
        }

        public String getTitle(Player player) {
            return StringHolder.getInstance().getNotNull(player, this.ak);
        }
    }
}

