/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import gnu.trove.TIntArrayList;
import gnu.trove.TIntObjectHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.TamedBeastInstance;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedableBeastInstance
extends MonsterInstance {
    private static final Logger _log;
    private static int TG;
    private static int TH;
    private static int TI;
    private static int TJ;
    public static final TIntObjectHashMap<growthInfo> growthCapableMobs;
    public static final TIntArrayList tamedBeasts;
    public static final TIntArrayList feedableBeasts;
    public static Map<Integer, Integer> feedInfo;

    private boolean L(int n) {
        return n == 2188;
    }

    private boolean M(int n) {
        return n == 2189;
    }

    private int N(int n) {
        if (this.L(n)) {
            return 6643;
        }
        return 6644;
    }

    public int getItemIdBySkillId(int n) {
        int n2 = 0;
        switch (n) {
            case 2188: {
                n2 = 6643;
                break;
            }
            case 2189: {
                n2 = 6644;
                break;
            }
            default: {
                n2 = 0;
            }
        }
        return n2;
    }

    public FeedableBeastInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    private void b(Player player, int n, int n2) {
        int n3 = this.getNpcId();
        int n4 = 0;
        n4 = FeedableBeastInstance.growthCapableMobs.get((int)n3).spice[n2][Rnd.get(FeedableBeastInstance.growthCapableMobs.get((int)n3).spice[n2].length)];
        feedInfo.remove(this.getObjectId());
        if (FeedableBeastInstance.growthCapableMobs.get((int)n3).growth_level == 0) {
            this.onDecay();
        } else {
            this.deleteMe();
        }
        if (tamedBeasts.contains(n4)) {
            if (player.getTrainedBeast() != null) {
                player.getTrainedBeast().doDespawn();
            }
            NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n4);
            TamedBeastInstance tamedBeastInstance = new TamedBeastInstance(IdFactory.getInstance().getNextId(), npcTemplate);
            Location location = player.getLoc();
            location.x += Rnd.get(-50, 50);
            location.y += Rnd.get(-50, 50);
            tamedBeastInstance.spawnMe(location);
            tamedBeastInstance.setTameType(player);
            tamedBeastInstance.setFoodType(this.N(n2 == TG ? TI : TJ));
            tamedBeastInstance.setRunning();
            tamedBeastInstance.setOwner(player);
            QuestState questState = player.getQuestState("_020_BringUpWithLove");
            if (questState != null && !questState.isCompleted() && Rnd.chance(5) && questState.getQuestItemsCount(7185) == 0L) {
                questState.giveItems(7185, 1L);
                questState.setCond(2);
            }
            if ((questState = player.getQuestState("_655_AGrandPlanForTamingWildBeasts")) != null && !questState.isCompleted() && questState.getCond() == 1 && questState.getQuestItemsCount(8084) < 10L) {
                questState.giveItems(8084, 1L);
            }
        } else {
            MonsterInstance monsterInstance = this.spawn(n4, this.getX(), this.getY(), this.getZ());
            feedInfo.put(monsterInstance.getObjectId(), player.getObjectId());
            player.setObjectTarget(monsterInstance);
            ThreadPoolManager.getInstance().schedule(new AggrPlayer(monsterInstance, player), 3000L);
        }
    }

    @Override
    protected void onDeath(Creature creature) {
        feedInfo.remove(this.getObjectId());
        super.onDeath(creature);
    }

    public MonsterInstance spawn(int n, int n2, int n3, int n4) {
        try {
            MonsterInstance monsterInstance = (MonsterInstance)NpcHolder.getInstance().getTemplate(n).getInstanceConstructor().newInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(n));
            monsterInstance.setSpawnedLoc(new Location(n2, n3, n4));
            monsterInstance.spawnMe(monsterInstance.getSpawnedLoc());
            return monsterInstance;
        }
        catch (Exception exception) {
            _log.error("Could not spawn Npc " + n, exception);
            return null;
        }
    }

    public void onSkillUse(Player player, int n) {
        int n2 = this.getNpcId();
        if (!feedableBeasts.contains(n2)) {
            return;
        }
        if (this.L(n) && this.M(n)) {
            return;
        }
        int n3 = this.L(n) ? 0 : 1;
        int n4 = this.getObjectId();
        this.broadcastPacket(new SocialAction(n4, 2));
        if (growthCapableMobs.containsKey(n2)) {
            if (FeedableBeastInstance.growthCapableMobs.get((int)n2).spice[n3].length == 0) {
                return;
            }
            int n5 = FeedableBeastInstance.growthCapableMobs.get((int)n2).growth_level;
            if (n5 > 0 && feedInfo.get(n4) != null && feedInfo.get(n4).intValue() != player.getObjectId()) {
                return;
            }
            if (Rnd.chance(FeedableBeastInstance.growthCapableMobs.get((int)n2).growth_chance)) {
                this.b(player, n5, n3);
            }
        } else if (Rnd.chance(60)) {
            this.dropItem(player, this.getItemIdBySkillId(n), 1L);
        }
    }

    static {
        Integer n;
        Integer n2;
        _log = LoggerFactory.getLogger(NpcInstance.class);
        TG = 0;
        TH = 1;
        TI = 2188;
        TJ = 2189;
        growthCapableMobs = new TIntObjectHashMap();
        tamedBeasts = new TIntArrayList();
        feedableBeasts = new TIntArrayList();
        growthCapableMobs.put(21451, new growthInfo(0, new int[][]{{21452, 21453, 21454, 21455}, {21456, 21457, 21458, 21459}}, 100));
        growthCapableMobs.put(21452, new growthInfo(1, new int[][]{{21460, 21462}, new int[0]}, 40));
        growthCapableMobs.put(21453, new growthInfo(1, new int[][]{{21461, 21463}, new int[0]}, 40));
        growthCapableMobs.put(21454, new growthInfo(1, new int[][]{{21460, 21462}, new int[0]}, 40));
        growthCapableMobs.put(21455, new growthInfo(1, new int[][]{{21461, 21463}, new int[0]}, 40));
        growthCapableMobs.put(21456, new growthInfo(1, new int[][]{new int[0], {21464, 21466}}, 40));
        growthCapableMobs.put(21457, new growthInfo(1, new int[][]{new int[0], {21465, 21467}}, 40));
        growthCapableMobs.put(21458, new growthInfo(1, new int[][]{new int[0], {21464, 21466}}, 40));
        growthCapableMobs.put(21459, new growthInfo(1, new int[][]{new int[0], {21465, 21467}}, 40));
        growthCapableMobs.put(21460, new growthInfo(2, new int[][]{{21468, 16017}, new int[0]}, 25));
        growthCapableMobs.put(21461, new growthInfo(2, new int[][]{{21469, 16018}, new int[0]}, 25));
        growthCapableMobs.put(21462, new growthInfo(2, new int[][]{{21468, 16017}, new int[0]}, 25));
        growthCapableMobs.put(21463, new growthInfo(2, new int[][]{{21469, 16018}, new int[0]}, 25));
        growthCapableMobs.put(21464, new growthInfo(2, new int[][]{new int[0], {21468, 16017}}, 25));
        growthCapableMobs.put(21465, new growthInfo(2, new int[][]{new int[0], {21469, 16018}}, 25));
        growthCapableMobs.put(21466, new growthInfo(2, new int[][]{new int[0], {21468, 16017}}, 25));
        growthCapableMobs.put(21467, new growthInfo(2, new int[][]{new int[0], {21469, 16018}}, 25));
        growthCapableMobs.put(21470, new growthInfo(0, new int[][]{{21472, 21474, 21471, 21473}, {21475, 21476, 21477, 21478}}, 100));
        growthCapableMobs.put(21471, new growthInfo(1, new int[][]{{21479, 21481}, new int[0]}, 40));
        growthCapableMobs.put(21472, new growthInfo(1, new int[][]{{21480, 21482}, new int[0]}, 40));
        growthCapableMobs.put(21473, new growthInfo(1, new int[][]{{21479, 21481}, new int[0]}, 40));
        growthCapableMobs.put(21474, new growthInfo(1, new int[][]{{21480, 21482}, new int[0]}, 40));
        growthCapableMobs.put(21475, new growthInfo(1, new int[][]{new int[0], {21483, 21485}}, 40));
        growthCapableMobs.put(21476, new growthInfo(1, new int[][]{new int[0], {21484, 21486}}, 40));
        growthCapableMobs.put(21477, new growthInfo(1, new int[][]{new int[0], {21483, 21485}}, 40));
        growthCapableMobs.put(21478, new growthInfo(1, new int[][]{new int[0], {21484, 21486}}, 40));
        growthCapableMobs.put(21479, new growthInfo(2, new int[][]{{21487, 16014}, new int[0]}, 25));
        growthCapableMobs.put(21480, new growthInfo(2, new int[][]{{21488, 16013}, new int[0]}, 25));
        growthCapableMobs.put(21481, new growthInfo(2, new int[][]{{21487, 16014}, new int[0]}, 25));
        growthCapableMobs.put(21482, new growthInfo(2, new int[][]{{21488, 16013}, new int[0]}, 25));
        growthCapableMobs.put(21483, new growthInfo(2, new int[][]{new int[0], {21487, 16014}}, 25));
        growthCapableMobs.put(21484, new growthInfo(2, new int[][]{new int[0], {21488, 16013}}, 25));
        growthCapableMobs.put(21485, new growthInfo(2, new int[][]{new int[0], {21487, 16014}}, 25));
        growthCapableMobs.put(21486, new growthInfo(2, new int[][]{new int[0], {21488, 16013}}, 25));
        growthCapableMobs.put(21489, new growthInfo(0, new int[][]{{21491, 21493, 21490, 21492}, {21495, 21497, 21494, 21496}}, 100));
        growthCapableMobs.put(21490, new growthInfo(1, new int[][]{{21498, 21500}, new int[0]}, 40));
        growthCapableMobs.put(21491, new growthInfo(1, new int[][]{{21499, 21501}, new int[0]}, 40));
        growthCapableMobs.put(21492, new growthInfo(1, new int[][]{{21498, 21500}, new int[0]}, 40));
        growthCapableMobs.put(21493, new growthInfo(1, new int[][]{{21499, 21501}, new int[0]}, 40));
        growthCapableMobs.put(21494, new growthInfo(1, new int[][]{new int[0], {21502, 21504}}, 40));
        growthCapableMobs.put(21495, new growthInfo(1, new int[][]{new int[0], {21503, 21505}}, 40));
        growthCapableMobs.put(21496, new growthInfo(1, new int[][]{new int[0], {21502, 21504}}, 40));
        growthCapableMobs.put(21497, new growthInfo(1, new int[][]{new int[0], {21503, 21505}}, 40));
        growthCapableMobs.put(21498, new growthInfo(2, new int[][]{{21506, 16015}, new int[0]}, 25));
        growthCapableMobs.put(21499, new growthInfo(2, new int[][]{{21507, 16016}, new int[0]}, 25));
        growthCapableMobs.put(21500, new growthInfo(2, new int[][]{{21506, 16015}, new int[0]}, 25));
        growthCapableMobs.put(21501, new growthInfo(2, new int[][]{{21507, 16015}, new int[0]}, 25));
        growthCapableMobs.put(21502, new growthInfo(2, new int[][]{new int[0], {21506, 16015}}, 25));
        growthCapableMobs.put(21503, new growthInfo(2, new int[][]{new int[0], {21507, 16016}}, 25));
        growthCapableMobs.put(21504, new growthInfo(2, new int[][]{new int[0], {21506, 16015}}, 25));
        growthCapableMobs.put(21505, new growthInfo(2, new int[][]{new int[0], {21507, 16016}}, 25));
        Integer n3 = 16013;
        while (n3 <= 16018) {
            tamedBeasts.add(n3);
            n2 = n3;
            n = n3 = Integer.valueOf(n3 + 1);
        }
        n3 = 16013;
        while (n3 <= 16019) {
            feedableBeasts.add(n3);
            n2 = n3;
            n = n3 = Integer.valueOf(n3 + 1);
        }
        n3 = 21451;
        while (n3 <= 21507) {
            feedableBeasts.add(n3);
            n2 = n3;
            n = n3 = Integer.valueOf(n3 + 1);
        }
        n3 = 21824;
        while (n3 <= 21829) {
            feedableBeasts.add(n3);
            n2 = n3;
            n = n3 = Integer.valueOf(n3 + 1);
        }
        feedInfo = new ConcurrentHashMap<Integer, Integer>();
    }

    public static class AggrPlayer
    extends RunnableImpl {
        private NpcInstance TK;
        private Player TL;

        public AggrPlayer(NpcInstance npcInstance, Player player) {
            this.TK = npcInstance;
            this.TL = player;
        }

        @Override
        public void runImpl() throws Exception {
            this.TK.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this.TL, 1000);
        }
    }

    private static class growthInfo {
        public int growth_level;
        public int growth_chance;
        public int[][] spice;

        public growthInfo(int n, int[][] nArray, int n2) {
            this.growth_level = n;
            this.spice = nArray;
            this.growth_chance = n2;
        }
    }
}

