/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _259_RanchersPlea
extends Quest
implements ScriptFile {
    private static final int bdo = 1495;
    private static final int bdp = 1061;
    private static final int bdq = 17;
    private static final int bdr = 1835;
    private static final int bds = 2509;
    private static final int bdt = 30497;
    private static final int bdu = 30405;
    private static final int bdv = 20103;
    private static final int bdw = 20106;
    private static final int bdx = 20108;

    public _259_RanchersPlea() {
        super(0);
        this.addStartNpc(30497);
        this.addTalkId(30405);
        this.addKillId(20103, 20106, 20108);
        this.addQuestItem(1495);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30497) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.playSound("ItemSound.quest_accept");
                questState.setState(2);
                string2 = "edmond_q0259_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "edmond_q0259_06.htm";
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "edmond_q0259_07.htm";
            }
        } else if (n == 30405) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "marius_q0259_03.htm";
            } else if (string.equalsIgnoreCase("reply_2") && questState.getQuestItemsCount(1495) >= 10L) {
                string2 = "marius_q0259_04.htm";
                questState.giveItems(1061, 2L);
                questState.takeItems(1495, 10L);
            } else if (string.equalsIgnoreCase("reply_3") && questState.getQuestItemsCount(1495) >= 10L) {
                string2 = "marius_q0259_05.htm";
                questState.giveItems(17, 250L);
                questState.takeItems(1495, 10L);
            } else if (string.equalsIgnoreCase("reply_4")) {
                if (questState.getQuestItemsCount(1495) >= 10L) {
                    string2 = "marius_q0259_06.htm";
                } else if (questState.getQuestItemsCount(1495) < 10L) {
                    string2 = "marius_q0259_07.htm";
                }
            } else if (string.equalsIgnoreCase("reply_5") && questState.getQuestItemsCount(1495) >= 10L) {
                string2 = "marius_q0259_05a.htm";
                questState.giveItems(1835, 60L);
                questState.takeItems(1495, 10L);
            } else if (string.equalsIgnoreCase("reply_6") && questState.getQuestItemsCount(1495) >= 10L) {
                string2 = "marius_q0259_05c.htm";
                questState.giveItems(2509, 30L);
                questState.takeItems(1495, 10L);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        switch (n2) {
            case 1: {
                if (n != 30497) break;
                if (questState.getPlayer().getLevel() >= 15) {
                    string = "edmond_q0259_02.htm";
                    break;
                }
                string = "edmond_q0259_01.htm";
                break;
            }
            case 2: {
                if (n == 30497) {
                    if (questState.getCond() == 1 && questState.getQuestItemsCount(1495) < 1L) {
                        string = "edmond_q0259_04.htm";
                        break;
                    }
                    if (questState.getCond() != 1 || questState.getQuestItemsCount(1495) < 1L) break;
                    string = "edmond_q0259_05.htm";
                    if (questState.getQuestItemsCount(1495) >= 10L) {
                        questState.giveItems(57, questState.getQuestItemsCount(1495) * 25L + 250L);
                    } else {
                        questState.giveItems(57, questState.getQuestItemsCount(1495) * 25L);
                    }
                    questState.takeItems(1495, -1L);
                    break;
                }
                if (n != 30405) break;
                if (questState.getCond() == 1 && questState.getQuestItemsCount(1495) < 10L) {
                    string = "marius_q0259_01.htm";
                    break;
                }
                if (questState.getCond() != 1 || questState.getQuestItemsCount(1495) < 10L) break;
                string = "marius_q0259_02.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() > 0) {
            questState.rollAndGive(1495, 1, 100.0);
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

