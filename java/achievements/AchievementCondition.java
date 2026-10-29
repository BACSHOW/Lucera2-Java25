/*
 * Decompiled with CFR 0.152.
 */
package achievements;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.model.AggroList;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;

public abstract class AchievementCondition {
    public static AchievementCondition makeCond(String string, String string2) {
        try {
            for (Class<?> clazz : AchievementCondition.class.getClasses()) {
                Constructor<?> constructor;
                Class<?> clazz2;
                AchievementConditionName achievementConditionName;
                if (!AchievementCondition.class.isAssignableFrom(clazz) || (achievementConditionName = (clazz2 = clazz).getAnnotation(AchievementConditionName.class)) == null || !string.equalsIgnoreCase(achievementConditionName.value()) || (constructor = clazz2.getConstructor(String.class)) == null) continue;
                return (AchievementCondition)constructor.newInstance(string2);
            }
        }
        catch (Exception exception) {
            throw new RuntimeException("Can't make condition " + string + "(" + string2 + ")", exception);
        }
        return null;
    }

    public abstract boolean test(Player var1, Object ... var2);

    @AchievementConditionName(value="self_min_online_time")
    public static class AchSelfMinOnlineTime
    extends AchievementCondition {
        private final long t;

        public AchSelfMinOnlineTime(String string) {
            this.t = Long.parseLong(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            long l = player.getOnlineBeginTime();
            long l2 = System.currentTimeMillis();
            if (l > 0L && l <= l2) {
                return (l2 - l) / 1000L >= this.t;
            }
            return false;
        }
    }

    @AchievementConditionName(value="self_quest_state_is")
    public static class AchSelfQuestStateIs
    extends AchievementCondition {
        private final int v;

        public AchSelfQuestStateIs(String string) {
            this.v = Quest.getStateId(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            for (Object object : objectArray) {
                if (object == null) continue;
                QuestState questState = null;
                if (object instanceof QuestState) {
                    questState = (QuestState)object;
                }
                if (object instanceof Quest) {
                    questState = player.getQuestState((Quest)object);
                }
                if (questState == null) continue;
                return questState.getState() == this.v;
            }
            return false;
        }
    }

    @AchievementConditionName(value="self_quest_id_in")
    public static class AchSelfQuestId
    extends AchievementCondition {
        private final Set<Integer> u = new HashSet<Integer>();

        public AchSelfQuestId(String string) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, ";,");
            while (stringTokenizer.hasMoreTokens()) {
                this.u.add(Integer.parseInt(stringTokenizer.nextToken()));
            }
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            for (Object object : objectArray) {
                if (object == null) continue;
                Quest quest = null;
                if (object instanceof Quest) {
                    quest = (Quest)object;
                }
                if (object instanceof QuestState) {
                    quest = ((QuestState)object).getQuest();
                }
                if (quest == null) continue;
                return this.u.contains(quest.getQuestIntId());
            }
            return false;
        }
    }

    @AchievementConditionName(value="target_npc_min_damage_to_me")
    public static class AchTargetAggroMinDamageToMe
    extends AchievementCondition {
        private final int J;

        public AchTargetAggroMinDamageToMe(String string) {
            this.J = Integer.parseInt(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            for (Object object : objectArray) {
                if (object == null || !(object instanceof NpcInstance)) continue;
                NpcInstance npcInstance = (NpcInstance)object;
                AggroList.AggroInfo aggroInfo = npcInstance.getAggroList().get(player);
                if (aggroInfo == null) {
                    return false;
                }
                return aggroInfo.damage >= this.J;
            }
            return false;
        }
    }

    @AchievementConditionName(value="target_npc_min_hate_to_me")
    public static class AchTargetAggroMinHateToMe
    extends AchievementCondition {
        private final int K;

        public AchTargetAggroMinHateToMe(String string) {
            this.K = Integer.parseInt(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            for (Object object : objectArray) {
                if (object == null || !(object instanceof NpcInstance)) continue;
                NpcInstance npcInstance = (NpcInstance)object;
                AggroList.AggroInfo aggroInfo = npcInstance.getAggroList().get(player);
                if (aggroInfo == null) {
                    return false;
                }
                return aggroInfo.hate >= this.K;
            }
            return false;
        }
    }

    @AchievementConditionName(value="self_target_max_lvl_diff")
    public static class AchSelfTargetMaxLvlDiff
    extends AchievementCondition {
        private final int w;

        public AchSelfTargetMaxLvlDiff(String string) {
            this.w = Integer.parseInt(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            for (Object object : objectArray) {
                if (object == null || !(object instanceof Creature)) continue;
                Creature creature = (Creature)object;
                return Math.abs(creature.getLevel() - player.getLevel()) <= this.w;
            }
            return false;
        }
    }

    @AchievementConditionName(value="self_is_hero")
    public static class AchSelfIsHero
    extends AchievementCondition {
        private final boolean m;

        public AchSelfIsHero(String string) {
            this.m = Boolean.parseBoolean(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            return player.isHero() == this.m;
        }
    }

    @AchievementConditionName(value="self_is_noble")
    public static class AchSelfIsNoble
    extends AchievementCondition {
        private final boolean n;

        public AchSelfIsNoble(String string) {
            this.n = Boolean.parseBoolean(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            return player.isNoble() == this.n;
        }
    }

    @AchievementConditionName(value="self_level_in_range")
    public static class AchSelfLevelInRange
    extends AchievementCondition {
        private final int p;
        private final int s;

        public AchSelfLevelInRange(String string) {
            int n = string.indexOf(45);
            this.p = Integer.parseInt(string.substring(0, n).trim());
            this.s = Integer.parseInt(string.substring(n + 1).trim());
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            return player.getLevel() >= this.p && player.getLevel() < this.s;
        }
    }

    @AchievementConditionName(value="self_is_clan_leader")
    public static class AchSelfIsClanLeader
    extends AchievementCondition {
        private final boolean l;

        public AchSelfIsClanLeader(String string) {
            this.l = Boolean.parseBoolean(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            return player.isClanLeader() == this.l;
        }
    }

    @AchievementConditionName(value="self_is_subclass_active")
    public static class AchSelfIsSubclassActive
    extends AchievementCondition {
        private final boolean o;

        public AchSelfIsSubclassActive(String string) {
            this.o = Boolean.parseBoolean(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            return player.isSubClassActive() == this.o;
        }
    }

    @AchievementConditionName(value="is_target_player_class_id_in")
    public static class AchTargetPlayerIsActiveClass
    extends AchievementCondition {
        private final Set<Integer> k = new HashSet<Integer>();

        public AchTargetPlayerIsActiveClass(String string) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, ";,");
            while (stringTokenizer.hasMoreTokens()) {
                this.k.add(Integer.parseInt(stringTokenizer.nextToken()));
            }
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            for (Object object : objectArray) {
                if (object == null || !(object instanceof Player)) continue;
                Player player2 = (Player)object;
                return this.k.contains((Object)player2.getClassId());
            }
            return false;
        }
    }

    @AchievementConditionName(value="self_is_class_id_in")
    public static class AchSelfIsActiveClass
    extends AchievementCondition {
        private final Set<Integer> k = new HashSet<Integer>();

        public AchSelfIsActiveClass(String string) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, ";,");
            while (stringTokenizer.hasMoreTokens()) {
                this.k.add(Integer.parseInt(stringTokenizer.nextToken()));
            }
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            return this.k.contains((Object)player.getClassId());
        }
    }

    @AchievementConditionName(value="is_oly_winner")
    public static class AchIsOlyWinner
    extends AchievementCondition {
        private final boolean j;

        public AchIsOlyWinner(String string) {
            this.j = Boolean.parseBoolean(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            boolean bl = false;
            for (Object object : objectArray) {
                if (!(object instanceof Competition)) continue;
                bl = true;
            }
            if (bl) {
                for (Object object : objectArray) {
                    if (!(object instanceof Boolean)) continue;
                    return this.j == (Boolean)object;
                }
            }
            return false;
        }
    }

    @AchievementConditionName(value="now_match_cron")
    public static class AchievementConditionIsNowMatchCron
    extends AchievementCondition {
        private final SchedulingPattern Q;

        public AchievementConditionIsNowMatchCron(String string) {
            this.Q = new SchedulingPattern(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            return this.Q.match(System.currentTimeMillis());
        }
    }

    @AchievementConditionName(value="npc_id_in_list")
    public static class AchievementConditionNpcIdInList
    extends AchievementCondition {
        private Set<Integer> U = new HashSet<Integer>();

        public AchievementConditionNpcIdInList(String string) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, ";,");
            while (stringTokenizer.hasMoreTokens()) {
                this.U.add(Integer.parseInt(stringTokenizer.nextToken()));
            }
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            for (Object object : objectArray) {
                NpcInstance npcInstance;
                if (object == null || !(object instanceof NpcInstance) || !this.U.contains((npcInstance = (NpcInstance)object).getNpcId())) continue;
                return true;
            }
            return false;
        }
    }

    @AchievementConditionName(value="is_karma_player")
    public static class AchievementConditionIsKarmaPlayer
    extends AchievementCondition {
        private final boolean P;

        public AchievementConditionIsKarmaPlayer(String string) {
            this.P = Boolean.parseBoolean(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            for (Object object : objectArray) {
                if (object == null || !(this.P ? object instanceof Player && ((Player)object).getKarma() > 0 : object instanceof Player && ((Player)object).getKarma() == 0)) continue;
                return true;
            }
            return false;
        }
    }

    @AchievementConditionName(value="is_raid_boss")
    public static class AchievementConditionHaveRaid
    extends AchievementCondition {
        private final boolean P;

        public AchievementConditionHaveRaid(String string) {
            this.P = Boolean.parseBoolean(string);
        }

        @Override
        public boolean test(Player player, Object ... objectArray) {
            for (Object object : objectArray) {
                if (object == null || !(this.P ? object instanceof RaidBossInstance : !(object instanceof RaidBossInstance))) continue;
                return true;
            }
            return false;
        }
    }

    @Target(value={ElementType.TYPE})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface AchievementConditionName {
        public String value();
    }
}

