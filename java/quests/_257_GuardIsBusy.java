/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;
import quests._255_Tutorial;

public class _257_GuardIsBusy
extends Quest
implements ScriptFile {
    private static final int bdl = 30039;
    private static final int bdm = 1084;
    private static final int ORC_AMULET = 752;
    private static final int ORC_NECKLACE = 1085;
    private static final int bdn = 1086;

    public _257_GuardIsBusy() {
        super(0);
        this.addStartNpc(30039);
        this.addKillId(20130, 20131, 20132, 20342, 20343, 20006, 20093, 20096, 20098);
        this.addQuestItem(752, 1085, 1086, 1084);
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

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("gilbert_q0257_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.takeItems(1084, -1L);
            questState.giveItems(1084, 1L);
        } else if (string.equalsIgnoreCase("257_2")) {
            string2 = "gilbert_q0257_05.htm";
            questState.takeItems(1084, -1L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        } else if (string.equalsIgnoreCase("257_3")) {
            string2 = "gilbert_q0257_06.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        switch (n) {
            case 1: {
                if (n2 != 30039) break;
                if (questState.getPlayer().getLevel() >= 6) {
                    string = "gilbert_q0257_02.htm";
                    break;
                }
                string = "gilbert_q0257_01.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 != 30039) break;
                if (questState.getQuestItemsCount(752) < 1L && questState.getQuestItemsCount(1085) < 1L && questState.getQuestItemsCount(1086) < 1L) {
                    string = "gilbert_q0257_04.htm";
                    break;
                }
                if (questState.getQuestItemsCount(752) <= 0L && questState.getQuestItemsCount(1085) <= 0L && questState.getQuestItemsCount(1086) <= 0L) break;
                string = "gilbert_q0257_07.htm";
                if (questState.getQuestItemsCount(752) + questState.getQuestItemsCount(1085) + questState.getQuestItemsCount(1086) >= 10L) {
                    questState.giveItems(57, 10L * questState.getQuestItemsCount(752) + 20L * questState.getQuestItemsCount(1085) + 20L * questState.getQuestItemsCount(1086) + 1000L);
                } else {
                    questState.giveItems(57, 10L * questState.getQuestItemsCount(752) + 20L * questState.getQuestItemsCount(1085) + 20L * questState.getQuestItemsCount(1086));
                }
                questState.takeAllItems(752, 1085, 1086);
                if (questState.getPlayer().getLevel() >= 25 || questState.getPlayer().getClassId().getLevel() != 1 || questState.getPlayer().getVarB("p1q2")) break;
                questState.getPlayer().setVar("p1q2", "1", -1L);
                questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n Go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                QuestState questState2 = questState.getPlayer().getQuestState(_255_Tutorial.class);
                if (questState2 == null || questState2.getInt("tutorial_quest_ex") == 10) break;
                questState.showQuestionMark(26);
                questState2.set("tutorial_quest_ex", "10");
                if (questState.getPlayer().getClassId().isMage()) {
                    questState.playTutorialVoice("tutorial_voice_027");
                    questState.giveItems(5790, 3000L);
                    break;
                }
                questState.playTutorialVoice("tutorial_voice_026");
                questState.giveItems(5789, 6000L);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (questState.getQuestItemsCount(1084) > 0L && questState.getCond() > 0) {
            if (n == 20130 || n == 20131 || n == 20006) {
                questState.rollAndGive(752, 1, 50.0);
            } else if (n == 20093 || n == 20096 || n == 20098) {
                questState.rollAndGive(1085, 1, 50.0);
            } else if (n == 20132) {
                questState.rollAndGive(1086, 1, 33.0);
            } else if (n == 20343) {
                questState.rollAndGive(1086, 1, 50.0);
            } else if (n == 20342) {
                questState.rollAndGive(1086, 1, 75.0);
            }
        }
        return null;
    }
}

