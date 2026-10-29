/*
 * Decompiled with CFR 0.152.
 */
package achievements;

import achievements.Achievement;
import achievements.AchievementInfo;
import achievements.AchievementMetricType;
import achievements.Achievements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import l2.commons.listener.Listener;
import l2.gameserver.listener.CharListener;
import l2.gameserver.listener.PlayerListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.OnKillListener;
import l2.gameserver.listener.actor.player.OnGainExpSpListener;
import l2.gameserver.listener.actor.player.OnOlyCompetitionListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.listener.actor.player.OnPvpPkKillListener;
import l2.gameserver.listener.actor.player.OnQuestStateChangeListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.utils.Location;

public class AchievementMetricListeners {
    private static final AchievementMetricListeners aq = new AchievementMetricListeners();
    private List<? extends Listener<?>> ar = Arrays.asList(new AchievementOnPlayerEnter(), new AchievementOnKill(), new AchievementOnDeath(), new AchievementOnPvPPkKill(), new AchievementOnGainExpSp(), new AchievementOnOlyCompetitionCompleted(), new AchievementOnQuestStateChange());

    private AchievementMetricListeners() {
    }

    public static AchievementMetricListeners getInstance() {
        return aq;
    }

    public void metricEvent(Player player, AchievementMetricType achievementMetricType, Object ... objectArray) {
        if (!Achievements.getInstance().isEnabled()) {
            return;
        }
        List<AchievementInfo> list = Achievements.getInstance().getAchievementInfosByMetric(achievementMetricType);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (AchievementInfo achievementInfo : list) {
            Achievement achievement = new Achievement(achievementInfo, player);
            if (achievement.isCompleted()) continue;
            achievement.onMetricEvent(objectArray);
        }
    }

    public void init() {
        for (Listener<?> listener : this.ar) {
            if (listener instanceof PlayerListener) {
                PlayerListenerList.addGlobal((PlayerListener)listener);
                continue;
            }
            if (listener instanceof CharListener) {
                CharListenerList.addGlobal((CharListener)listener);
                continue;
            }
            throw new IllegalStateException("Unknown listener " + listener.getClass());
        }
    }

    public void done() {
        for (Listener<?> listener : this.ar) {
            if (listener instanceof PlayerListener) {
                PlayerListenerList.removeGlobal((PlayerListener)listener);
                continue;
            }
            if (!(listener instanceof CharListener)) continue;
            CharListenerList.removeGlobal((CharListener)listener);
        }
    }

    public static class AchievementOnQuestStateChange
    implements OnQuestStateChangeListener {
        @Override
        public void onQuestStateChange(Player player, QuestState questState) {
            AchievementMetricListeners.getInstance().metricEvent(player, AchievementMetricType.QUEST_STATE, questState);
        }
    }

    public static class AchievementOnOlyCompetitionCompleted
    implements OnOlyCompetitionListener {
        @Override
        public void onOlyCompetitionCompleted(Player player, Competition competition, boolean bl) {
            AchievementMetricListeners.getInstance().metricEvent(player, AchievementMetricType.OLYMPIAD, competition, bl);
        }
    }

    public static class AchievementOnGainExpSp
    implements OnGainExpSpListener {
        @Override
        public void onGainExpSp(Player player, long l, long l2) {
            AchievementMetricListeners.getInstance().metricEvent(player, AchievementMetricType.LEVEL, player.getLevel());
        }
    }

    public static class AchievementOnPvPPkKill
    implements OnPvpPkKillListener {
        @Override
        public void onPvpPkKill(Player player, Player player2, boolean bl) {
            if (!bl) {
                AchievementMetricListeners.getInstance().metricEvent(player, AchievementMetricType.PVP_KILL, player.getPvpKills(), player2);
            }
        }
    }

    public static class AchievementOnDeath
    implements OnDeathListener {
        @Override
        public void onDeath(Creature creature, Creature creature2) {
            if (creature.isPlayer()) {
                AchievementMetricListeners.getInstance().metricEvent(creature.getPlayer(), AchievementMetricType.DEATH, creature2);
            }
        }
    }

    public static class AchievementOnKill
    implements OnKillListener {
        @Override
        public void onKill(Creature creature, Creature creature2) {
            if (creature == null || !creature.isPlayer() || creature2 == null) {
                return;
            }
            if (creature2.isNpc()) {
                AchievementMetricListeners.getInstance().metricEvent(creature.getPlayer(), AchievementMetricType.NPC_KILL, creature2);
                if (creature2 instanceof RaidBossInstance) {
                    RaidBossInstance raidBossInstance = (RaidBossInstance)creature2;
                    Location location = raidBossInstance.getLoc();
                    ArrayList<Creature> arrayList = new ArrayList<Creature>(raidBossInstance.getAggroList().getCharMap().keySet());
                    LinkedHashSet<Player> linkedHashSet = new LinkedHashSet<Player>();
                    for (Creature creature3 : arrayList) {
                        if (creature3 == null || location.distance3D(creature3.getLoc()) > 1500.0 || !(creature3 instanceof Player)) continue;
                        linkedHashSet.add((Player)creature3);
                    }
                    for (Player player : linkedHashSet) {
                        AchievementMetricListeners.getInstance().metricEvent(player, AchievementMetricType.RAID_PARTICIPATION, creature2);
                    }
                }
            }
        }

        @Override
        public boolean ignorePetOrSummon() {
            return true;
        }
    }

    public static class AchievementOnPlayerEnter
    implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            AchievementMetricListeners.getInstance().metricEvent(player, AchievementMetricType.LOGIN, player);
        }
    }
}

