/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _377_ExplorationOfGiantsCavePart2
extends Quest
implements ScriptFile {
    private static final int bwk = 31147;
    private static final int bwU = 20654;
    private static final int bwV = 20656;
    private static final int bnT = 20657;
    private static final int bnU = 20658;
    private static final int bwW = 5950;
    private static final int bwX = 5951;
    private static final int bwY = 5952;
    private static final int bwZ = 5953;
    private static final int bxa = 5954;
    private static final int bxb = 5945;
    private static final int bxc = 5946;
    private static final int bxd = 5947;
    private static final int bxe = 5948;
    private static final int bxf = 5949;
    private static final int bxg = 5955;
    private static final int bxh = 5422;
    private static final int bxi = 5420;
    private static final int bxj = 5336;
    private static final int bxk = 5338;
    private static final int bxl = 5892;

    public _377_ExplorationOfGiantsCavePart2() {
        super(1);
        this.addKillId(20654, 20656, 20657, 20658);
        this.addStartNpc(31147);
        this.addTalkId(31147);
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        switch (n) {
            case 1: {
                if (questState.getPlayer().getLevel() >= 57 && questState.getQuestItemsCount(5892) > 0L) {
                    string = "sobling_q0377_01.htm";
                    break;
                }
                string = "sobling_q0377_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (!(questState.getCond() != 1 || questState.getQuestItemsCount(5950) >= 1L && questState.getQuestItemsCount(5951) >= 1L && questState.getQuestItemsCount(5952) >= 1L && questState.getQuestItemsCount(5953) >= 1L && questState.getQuestItemsCount(5954) >= 1L || questState.getQuestItemsCount(5945) >= 1L && questState.getQuestItemsCount(5946) >= 1L && questState.getQuestItemsCount(5947) >= 1L && questState.getQuestItemsCount(5948) >= 1L && questState.getQuestItemsCount(5949) >= 1L)) {
                    string = "sobling_q0377_04.htm";
                }
                if (questState.getCond() != 1 || (questState.getQuestItemsCount(5950) < 1L || questState.getQuestItemsCount(5951) < 1L || questState.getQuestItemsCount(5952) < 1L || questState.getQuestItemsCount(5953) < 1L || questState.getQuestItemsCount(5954) < 1L) && (questState.getQuestItemsCount(5945) < 1L || questState.getQuestItemsCount(5946) < 1L || questState.getQuestItemsCount(5947) < 1L || questState.getQuestItemsCount(5948) < 1L || questState.getQuestItemsCount(5949) < 1L)) break;
                if (questState.getQuestItemsCount(5950) > 0L && questState.getQuestItemsCount(5951) > 0L && questState.getQuestItemsCount(5952) > 0L && questState.getQuestItemsCount(5953) > 0L && questState.getQuestItemsCount(5954) > 0L) {
                    questState.takeItems(5950, 1L);
                    questState.takeItems(5951, 1L);
                    questState.takeItems(5952, 1L);
                    questState.takeItems(5953, 1L);
                    questState.takeItems(5954, 1L);
                    if (Rnd.get(2) == 0) {
                        questState.giveItems(5422, 1L);
                    } else {
                        questState.giveItems(5420, 1L);
                    }
                }
                if (questState.getQuestItemsCount(5945) > 0L && questState.getQuestItemsCount(5946) > 0L && questState.getQuestItemsCount(5947) > 0L && questState.getQuestItemsCount(5948) > 0L && questState.getQuestItemsCount(5949) > 0L) {
                    questState.takeItems(5945, 1L);
                    questState.takeItems(5946, 1L);
                    questState.takeItems(5947, 1L);
                    questState.takeItems(5948, 1L);
                    questState.takeItems(5949, 1L);
                    if (Rnd.get(2) == 0) {
                        questState.giveItems(5336, 1L);
                    } else {
                        questState.giveItems(5338, 1L);
                    }
                }
                string = "sobling_q0377_05.htm";
            }
        }
        return string;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "sobling_q0377_03.htm";
        } else if (string.equalsIgnoreCase("reply_1") && (questState.getQuestItemsCount(5950) >= 1L && questState.getQuestItemsCount(5951) >= 1L && questState.getQuestItemsCount(5952) >= 1L && questState.getQuestItemsCount(5953) >= 1L && questState.getQuestItemsCount(5954) >= 1L || questState.getQuestItemsCount(5945) >= 1L && questState.getQuestItemsCount(5946) >= 1L && questState.getQuestItemsCount(5947) >= 1L && questState.getQuestItemsCount(5948) >= 1L && questState.getQuestItemsCount(5949) >= 1L)) {
            if (questState.getQuestItemsCount(5950) > 0L && questState.getQuestItemsCount(5951) > 0L && questState.getQuestItemsCount(5952) > 0L && questState.getQuestItemsCount(5953) > 0L && questState.getQuestItemsCount(5954) > 0L) {
                questState.takeItems(5950, 1L);
                questState.takeItems(5951, 1L);
                questState.takeItems(5952, 1L);
                questState.takeItems(5953, 1L);
                questState.takeItems(5954, 1L);
                if (Rnd.get(2) == 0) {
                    questState.giveItems(5422, 1L);
                } else {
                    questState.giveItems(5420, 1L);
                }
            }
            if (questState.getQuestItemsCount(5945) > 0L && questState.getQuestItemsCount(5946) > 0L && questState.getQuestItemsCount(5947) > 0L && questState.getQuestItemsCount(5948) > 0L && questState.getQuestItemsCount(5949) > 0L) {
                questState.takeItems(5945, 1L);
                questState.takeItems(5946, 1L);
                questState.takeItems(5947, 1L);
                questState.takeItems(5948, 1L);
                questState.takeItems(5949, 1L);
                if (Rnd.get(2) == 0) {
                    questState.giveItems(5336, 1L);
                } else {
                    questState.giveItems(5338, 1L);
                }
            }
            string2 = "sobling_q0377_05.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = "sobling_q0377_04.htm";
        } else if (string.equalsIgnoreCase("reply_2")) {
            string2 = "sobling_q0377_06.htm";
        } else if (string.equalsIgnoreCase("reply_3")) {
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
            string2 = "sobling_q0377_07.htm";
        }
        return string2;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20654 || n == 20656) {
            questState.rollAndGive(5955, 1, 18.0);
        } else if (n == 20657) {
            questState.rollAndGive(5955, 1, 14.0);
        } else if (n == 20658) {
            questState.rollAndGive(5955, 1, 12.0);
        }
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

