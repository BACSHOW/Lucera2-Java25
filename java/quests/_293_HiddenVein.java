/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;
import quests._255_Tutorial;

public class _293_HiddenVein
extends Quest
implements ScriptFile {
    private static final int beI = 30535;
    private static final int beJ = 30539;
    private static final int beK = 20446;
    private static final int beL = 20447;
    private static final int beM = 20448;
    private static final int beN = 1488;
    private static final int beO = 1489;
    private static final int beP = 1490;

    public _293_HiddenVein() {
        super(0);
        this.addStartNpc(30535);
        this.addTalkId(30539);
        this.addKillId(20446, 20447, 20448);
        this.addQuestItem(1488, 1489, 1490);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        if (string.equalsIgnoreCase("elder_filaur_q0293_03.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("elder_filaur_q0293_06.htm") && n == 2) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        } else if (string.equalsIgnoreCase("chichirin_q0293_03.htm") && n == 2) {
            if (questState.getQuestItemsCount(1489) < 4L) {
                return "chichirin_q0293_02.htm";
            }
            questState.takeItems(1489, 4L);
            questState.giveItems(1490, 1L);
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        String string = "no-quest";
        switch (n) {
            case 1: {
                if (n2 != 30535) break;
                if (questState.getPlayer().getRace() != Race.dwarf) {
                    string = "elder_filaur_q0293_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getPlayer().getLevel() >= 6) {
                    string = "elder_filaur_q0293_02.htm";
                    break;
                }
                string = "elder_filaur_q0293_01.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 != 30535) break;
                if (questState.getQuestItemsCount(1488) < 1L && questState.getQuestItemsCount(1490) < 1L) {
                    string = "elder_filaur_q0293_04.htm";
                    break;
                }
                if (questState.getQuestItemsCount(1488) < 1L && questState.getQuestItemsCount(1490) >= 1L) {
                    string = "elder_filaur_q0293_08.htm";
                    if (questState.getQuestItemsCount(1490) >= 10L) {
                        questState.giveItems(57, questState.getQuestItemsCount(1490) * 500L + 2000L);
                    } else {
                        questState.giveItems(57, questState.getQuestItemsCount(1490) * 500L);
                    }
                    questState.takeItems(1490, -1L);
                    if (questState.getPlayer().getLevel() >= 25 || questState.getPlayer().getClassId().getLevel() != 1 || questState.getPlayer().getVarB("p1q2")) break;
                    questState.getPlayer().setVar("p1q2", "1", -1L);
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n Go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                    QuestState questState2 = questState.getPlayer().getQuestState(_255_Tutorial.class);
                    if (questState2 == null || questState2.getInt("tutorial_quest_ex") == 10) break;
                    questState.showQuestionMark(26);
                    questState2.set("tutorial_quest_ex", "10");
                    questState.playTutorialVoice("tutorial_voice_026");
                    questState.giveItems(5789, 6000L);
                    break;
                }
                if (questState.getQuestItemsCount(1488) >= 1L && questState.getQuestItemsCount(1490) < 1L) {
                    string = "elder_filaur_q0293_05.htm";
                    if (questState.getQuestItemsCount(1488) >= 10L) {
                        questState.giveItems(57, questState.getQuestItemsCount(1488) * 5L + 2000L);
                    } else {
                        questState.giveItems(57, questState.getQuestItemsCount(1488) * 5L);
                    }
                    questState.takeItems(1488, -1L);
                    if (questState.getPlayer().getLevel() >= 25 || questState.getPlayer().getClassId().getLevel() != 1 || questState.getPlayer().getVarB("p1q2")) break;
                    questState.getPlayer().setVar("p1q2", "1", -1L);
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n Go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                    QuestState questState3 = questState.getPlayer().getQuestState(_255_Tutorial.class);
                    if (questState3 == null || questState3.getInt("tutorial_quest_ex") == 10) break;
                    questState.showQuestionMark(26);
                    questState3.set("tutorial_quest_ex", "10");
                    questState.playTutorialVoice("tutorial_voice_026");
                    questState.giveItems(5789, 6000L);
                    break;
                }
                if (questState.getQuestItemsCount(1488) < 1L || questState.getQuestItemsCount(1490) < 1L) break;
                string = "elder_filaur_q0293_09.htm";
                if (questState.getQuestItemsCount(1488) + questState.getQuestItemsCount(1490) >= 10L) {
                    questState.giveItems(57, questState.getQuestItemsCount(1488) * 5L + questState.getQuestItemsCount(1490) * 500L + 2000L);
                } else {
                    questState.giveItems(57, questState.getQuestItemsCount(1488) * 5L + questState.getQuestItemsCount(1490) * 500L);
                }
                questState.takeItems(1490, -1L);
                questState.takeItems(1488, -1L);
                if (questState.getPlayer().getLevel() >= 25 || questState.getPlayer().getClassId().getLevel() != 1 || questState.getPlayer().getVarB("p1q2")) break;
                questState.getPlayer().setVar("p1q2", "1", -1L);
                questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n Go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                QuestState questState4 = questState.getPlayer().getQuestState(_255_Tutorial.class);
                if (questState4 == null || questState4.getInt("tutorial_quest_ex") == 10) break;
                questState.showQuestionMark(26);
                questState4.set("tutorial_quest_ex", "10");
                questState.playTutorialVoice("tutorial_voice_026");
                questState.giveItems(5789, 6000L);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = Rnd.get(100);
        if (n > 50) {
            questState.rollAndGive(1488, 1, 100.0);
        } else if (n < 5) {
            questState.rollAndGive(1489, 1, 100.0);
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

