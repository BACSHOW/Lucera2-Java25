/*
 * Decompiled with CFR 0.152.
 */
package achievements;

import achievements.AchievementCounter;
import achievements.AchievementInfo;
import l2.gameserver.model.Player;

public enum AchievementMetricType {
    LOGIN,
    NPC_KILL,
    PVP_KILL,
    DEATH,
    LEVEL{

        @Override
        public AchievementCounter getCounter(Player player, AchievementInfo achievementInfo) {
            final int n = player.getLevel();
            return new AchievementCounter(player.getObjectId(), achievementInfo.getId()){

                @Override
                public int getVal() {
                    return n;
                }

                @Override
                public void setVal(int n2) {
                }

                @Override
                public int incrementAndGetValue() {
                    return n;
                }

                @Override
                public void store() {
                }

                @Override
                public boolean isStorable() {
                    return false;
                }
            };
        }
    }
    ,
    OLYMPIAD,
    RAID_PARTICIPATION,
    QUEST_STATE;


    public AchievementCounter getCounter(Player player, AchievementInfo achievementInfo) {
        return AchievementCounter.makeDBStorableCounter(player.getObjectId(), achievementInfo.getId());
    }
}

