/*
 * Decompiled with CFR 0.152.
 */
package achievements;

import achievements.AchievementCounter;
import achievements.AchievementInfo;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;

public class Achievement {
    public static final int BASE_ACHIEVEMENT_LEVEL = 1;
    private static final String b = "ex_achievement_%d_%s";
    private final AchievementInfo d;
    private final HardReference<Player> e;
    private final AchievementCounter f;
    private long g;

    public Achievement(AchievementInfo achievementInfo, Player player) {
        this.d = achievementInfo;
        this.e = player.getRef();
        this.f = achievementInfo.getMetricType().getCounter(player, achievementInfo);
        this.g = Long.parseLong(this.a("expire_time", String.valueOf(this.d.nextResetTimeMills())));
        this.checkExpire();
    }

    private Player getPlayer() {
        return this.e.get();
    }

    public AchievementInfo getAchInfo() {
        return this.d;
    }

    private int a() {
        return this.a("next_lvl", 1);
    }

    private void a(int n) {
        this.b("next_lvl", n);
    }

    public void checkExpire() {
        long l = System.currentTimeMillis();
        if (this.g >= 0L) {
            this.b("expire_time", String.valueOf(this.g));
        }
        if (this.g < 0L || this.g > l) {
            return;
        }
        this.f.setVal(0);
        this.f.store();
        this.a(1);
        for (AchievementInfo.AchievementInfoLevel achievementInfoLevel : this.d.getLevels()) {
            if (!this.isLevelRewarded(achievementInfoLevel)) continue;
            this.setLevelRewarded(achievementInfoLevel, false);
        }
        long l2 = this.d.nextResetTimeMills();
        this.b("expire_time", String.valueOf(l2));
    }

    private String a(String string, String string2) {
        Player player = this.getPlayer();
        if (player == null) {
            return string2;
        }
        String string3 = player.getVar(String.format(b, this.d.getId(), string));
        return string3 != null ? string3 : string2;
    }

    private int a(String string, int n) {
        return Integer.parseInt(this.a(string, String.valueOf(n)));
    }

    private void b(String string, String string2) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.setVar(String.format(b, this.d.getId(), string), string2, -1L);
    }

    private void b(String string, int n) {
        this.b(string, String.valueOf(n));
    }

    public AchievementInfo.AchievementInfoLevel getLevel() {
        return this.d.getLevel(this.a() - 1);
    }

    public boolean isRewardableLevel(AchievementInfo.AchievementInfoLevel achievementInfoLevel) {
        if (achievementInfoLevel == null || achievementInfoLevel.getAchievementInfo() != this.getAchInfo() || achievementInfoLevel.getLevel() >= this.a() || achievementInfoLevel.getLevel() < 1) {
            return false;
        }
        return !this.isLevelRewarded(achievementInfoLevel);
    }

    public boolean isLevelRewarded(int n) {
        return this.a(String.format("lvl_%d_rewarded", n), 0) != 0;
    }

    public boolean isLevelRewarded(AchievementInfo.AchievementInfoLevel achievementInfoLevel) {
        return this.isLevelRewarded(achievementInfoLevel.getLevel());
    }

    public void setLevelRewarded(int n, boolean bl) {
        this.b(String.format("lvl_%d_rewarded", n), bl ? 1 : 0);
    }

    public void setLevelRewarded(AchievementInfo.AchievementInfoLevel achievementInfoLevel, boolean bl) {
        this.setLevelRewarded(achievementInfoLevel.getLevel(), bl);
    }

    public AchievementInfo.AchievementInfoLevel getNextLevel() {
        return this.d.getLevel(this.a());
    }

    public boolean isCompleted() {
        int n = this.a();
        return n > this.d.getMaxLevel();
    }

    public AchievementCounter getCounter() {
        return this.f;
    }

    public void onMetricEvent(Object ... objectArray) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        if (!this.d.testConds(this.getPlayer(), objectArray)) {
            return;
        }
        if (this.d.getMetricNotifyDelay() > 0L) {
            ThreadPoolManager.getInstance().schedule(new EventMetric(player, objectArray), this.d.getMetricNotifyDelay() * 1000L);
        } else {
            ThreadPoolManager.getInstance().execute(new EventMetric(player, objectArray));
        }
    }

    private class EventMetric
    extends RunnableImpl {
        private final Object[] i;
        private final HardReference<Player> e;

        private EventMetric(Player player, Object[] objectArray) {
            this.i = objectArray;
            this.e = player.getRef();
        }

        @Override
        public void runImpl() throws Exception {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            AchievementInfo.AchievementInfoLevel achievementInfoLevel = Achievement.this.getNextLevel();
            if (achievementInfoLevel != null && achievementInfoLevel.testConds(player, this.i)) {
                AchievementCounter achievementCounter = Achievement.this.getCounter();
                int n = achievementCounter.incrementAndGetValue();
                AchievementInfo.AchievementInfoLevel achievementInfoLevel2 = null;
                if (n >= achievementInfoLevel.getValue()) {
                    achievementInfoLevel2 = achievementInfoLevel;
                    Achievement.this.a(achievementInfoLevel2.getLevel() + 1);
                    if (achievementInfoLevel2.isResetMetric()) {
                        achievementCounter.setVal(0);
                    }
                    String string = new CustomMessage("achievements.achievementS1Unlocked", player, Achievement.this.d.getName(player)).toString();
                    player.sendPacket((IStaticPacket)new ExShowScreenMessage(string, 5000, 0, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, true));
                    player.sendMessage(new CustomMessage("achievements.achievedS1LevelS2", player, Achievement.this.d.getName(player), achievementInfoLevel.getLevel()));
                }
                achievementCounter.store();
            }
        }
    }
}

