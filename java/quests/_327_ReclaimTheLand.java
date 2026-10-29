/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.HashMap;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Drop;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _327_ReclaimTheLand
extends Quest
implements ScriptFile {
    private static int aQL = 30597;
    private static int bgy = 30034;
    private static int bgz = 30313;
    private static int bgA = 1846;
    private static int bgB = 1847;
    private static int bgC = 1848;
    private static int bgD = 1849;
    private static int bgE = 1850;
    private static int bgF = 1851;
    private static int bgG = 1852;
    private static int bgH = 1853;
    private static int bgI = 1854;
    private static int bgJ = 1855;
    private static int bgK = 80;
    private static Map<Integer, Drop> aUW = new HashMap<Integer, Drop>();
    private static Map<Integer, Integer> bgL = new HashMap<Integer, Integer>();

    public _327_ReclaimTheLand() {
        super(0);
        this.addStartNpc(aQL);
        this.addTalkId(bgy);
        this.addTalkId(bgz);
        aUW.put(20495, new Drop(1, 65535, 13).addItem(bgB));
        aUW.put(20496, new Drop(1, 65535, 9).addItem(bgA));
        aUW.put(20497, new Drop(1, 65535, 11).addItem(bgB));
        aUW.put(20498, new Drop(1, 65535, 10).addItem(bgA));
        aUW.put(20499, new Drop(1, 65535, 8).addItem(bgA));
        aUW.put(20500, new Drop(1, 65535, 7).addItem(bgA));
        aUW.put(20501, new Drop(1, 65535, 12).addItem(bgB));
        bgL.put(bgG, 913);
        bgL.put(bgH, 1065);
        bgL.put(bgI, 1065);
        bgL.put(bgJ, 1294);
        for (int n : aUW.keySet()) {
            this.addKillId(n);
        }
        this.addQuestItem(bgB);
        this.addQuestItem(bgA);
    }

    private static boolean a(QuestState questState, int n) {
        long l;
        Integer n2 = bgL.get(n);
        if (n2 == null) {
            n2 = 182;
        }
        if ((l = questState.getQuestItemsCount(n * n2)) == 0L) {
            return false;
        }
        questState.takeItems(n, -1L);
        questState.addExpAndSp(l, 0L);
        questState.playSound("ItemSound.quest_middle");
        return true;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        if (string.equalsIgnoreCase("piotur_q0327_03.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("piotur_q0327_06.htm") && n == 2) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        } else {
            if (string.equalsIgnoreCase("trader_acellopy_q0327_02.htm") && n == 2 && questState.getQuestItemsCount(bgC) >= 5L) {
                questState.takeItems(bgC, 5L);
                if (!Rnd.chance(bgK)) {
                    return "trader_acellopy_q0327_10.htm";
                }
                questState.giveItems(bgG, 1L);
                questState.playSound("ItemSound.quest_middle");
                return "trader_acellopy_q0327_03.htm";
            }
            if (string.equalsIgnoreCase("trader_acellopy_q0327_04.htm") && n == 2 && questState.getQuestItemsCount(bgD) >= 5L) {
                questState.takeItems(bgD, 5L);
                if (!Rnd.chance(bgK)) {
                    return "trader_acellopy_q0327_10.htm";
                }
                questState.giveItems(bgH, 1L);
                questState.playSound("ItemSound.quest_middle");
                return "trader_acellopy_q0327_05.htm";
            }
            if (string.equalsIgnoreCase("trader_acellopy_q0327_06.htm") && n == 2 && questState.getQuestItemsCount(bgE) >= 5L) {
                questState.takeItems(bgE, 5L);
                if (!Rnd.chance(bgK)) {
                    return "trader_acellopy_q0327_10.htm";
                }
                questState.giveItems(bgI, 1L);
                questState.playSound("ItemSound.quest_middle");
                return "trader_acellopy_q0327_07.htm";
            }
            if (string.equalsIgnoreCase("trader_acellopy_q0327_08.htm") && n == 2 && questState.getQuestItemsCount(bgF) >= 5L) {
                questState.takeItems(bgF, 5L);
                if (!Rnd.chance(bgK)) {
                    return "trader_acellopy_q0327_09.htm";
                }
                questState.giveItems(bgJ, 1L);
                questState.playSound("ItemSound.quest_middle");
                return "trader_acellopy_q0327_07.htm";
            }
            if (string.equalsIgnoreCase("iris_q0327_03.htm") && n == 2 ? !_327_ReclaimTheLand.a(questState, bgC) : (string.equalsIgnoreCase("iris_q0327_04.htm") && n == 2 ? !_327_ReclaimTheLand.a(questState, bgD) : (string.equalsIgnoreCase("iris_q0327_05.htm") && n == 2 ? !_327_ReclaimTheLand.a(questState, bgE) : (string.equalsIgnoreCase("iris_q0327_06.htm") && n == 2 ? !_327_ReclaimTheLand.a(questState, bgF) : string.equalsIgnoreCase("iris_q0327_07.htm") && n == 2 && !_327_ReclaimTheLand.a(questState, bgG) && !_327_ReclaimTheLand.a(questState, bgH) && !_327_ReclaimTheLand.a(questState, bgI) && !_327_ReclaimTheLand.a(questState, bgJ))))) {
                return "iris_q0327_02.htm";
            }
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        if (n == 1) {
            if (n2 != aQL) {
                return "noquest";
            }
            if (questState.getPlayer().getLevel() < 25) {
                questState.exitCurrentQuest(true);
                return "piotur_q0327_01.htm";
            }
            questState.setCond(0);
            return "piotur_q0327_02.htm";
        }
        if (n != 2) {
            return "noquest";
        }
        if (n2 == aQL) {
            long l = questState.getQuestItemsCount(bgA) * 40L + questState.getQuestItemsCount(bgB) * 50L;
            if (l == 0L) {
                return "piotur_q0327_04.htm";
            }
            questState.takeItems(bgA, -1L);
            questState.takeItems(bgB, -1L);
            questState.giveItems(57, l);
            questState.playSound("ItemSound.quest_middle");
            return "piotur_q0327_05.htm";
        }
        if (n2 == bgy) {
            return "iris_q0327_01.htm";
        }
        if (n2 == bgz) {
            return "trader_acellopy_q0327_01.htm";
        }
        return "noquest";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        int n = npcInstance.getNpcId();
        Drop drop = aUW.get(n);
        if (drop == null) {
            return null;
        }
        if (Rnd.chance(drop.chance)) {
            int n2 = Rnd.get(100);
            if (n2 < 25) {
                questState.giveItems(bgC, 1L);
            } else if (n2 < 50) {
                questState.giveItems(bgD, 1L);
            } else if (n2 < 75) {
                questState.giveItems(bgE, 1L);
            } else {
                questState.giveItems(bgF, 1L);
            }
        }
        questState.giveItems(drop.itemList[0], 1L);
        questState.playSound("ItemSound.quest_itemget");
        return null;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

