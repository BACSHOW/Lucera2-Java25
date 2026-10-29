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

public class _273_InvadersOfHolyland
extends Quest
implements ScriptFile {
    private static final int bee = 30566;
    private static final int bef = 1475;
    private static final int beg = 1476;

    public _273_InvadersOfHolyland() {
        super(0);
        this.addStartNpc(30566);
        this.addKillId(20311, 20312, 20313);
        this.addQuestItem(1475, 1476);
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
        if (string.equals("atuba_chief_varkees_q0273_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("atuba_chief_varkees_q0273_07.htm")) {
            questState.setCond(0);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        } else if (string.equals("atuba_chief_varkees_q0273_08.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
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
                if (n != 30566) break;
                if (questState.getPlayer().getRace() != Race.orc) {
                    string = "atuba_chief_varkees_q0273_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getPlayer().getLevel() < 6) {
                    string = "atuba_chief_varkees_q0273_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "atuba_chief_varkees_q0273_02.htm";
                break;
            }
            case 2: {
                if (n != 30566) break;
                if (questState.getQuestItemsCount(1475) + questState.getQuestItemsCount(1476) == 0L) {
                    string = "atuba_chief_varkees_q0273_04.htm";
                    break;
                }
                if (questState.getQuestItemsCount(1476) == 0L) {
                    long l = questState.getQuestItemsCount(1475);
                    string = "atuba_chief_varkees_q0273_05.htm";
                    if (questState.getQuestItemsCount(1475) >= 10L) {
                        questState.giveItems(57, l * 3L + 1500L);
                    } else {
                        questState.giveItems(57, l * 3L);
                    }
                    questState.takeItems(1475, l);
                    if (questState.getPlayer().getClassId().getLevel() == 1 && questState.getPlayer().getLevel() < 25 && !questState.getPlayer().getVarB("p1q2")) {
                        questState.getPlayer().setVar("p1q2", "1", -1L);
                        questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n Go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                        QuestState questState2 = questState.getPlayer().getQuestState(_255_Tutorial.class);
                        if (questState2 != null && questState2.getInt("tutorial_quest_ex") != 10) {
                            questState.showQuestionMark(26);
                            questState2.set("tutorial_quest_ex", "10");
                            if (questState.getPlayer().getClassId().isMage()) {
                                questState.playTutorialVoice("tutorial_voice_027");
                                questState.giveItems(5790, 3000L);
                            } else {
                                questState.playTutorialVoice("tutorial_voice_026");
                                questState.giveItems(5789, 6000L);
                            }
                        }
                    }
                    questState.exitCurrentQuest(true);
                    questState.playSound("ItemSound.quest_finish");
                    break;
                }
                string = "atuba_chief_varkees_q0273_06.htm";
                if (questState.getQuestItemsCount(1475) + questState.getQuestItemsCount(1476) >= 10L) {
                    questState.giveItems(57, questState.getQuestItemsCount(1476) * 10L + questState.getQuestItemsCount(1475) * 3L + 1800L);
                } else {
                    questState.giveItems(57, questState.getQuestItemsCount(1476) * 10L + questState.getQuestItemsCount(1475) * 3L);
                }
                questState.takeAllItems(1475, 1476);
                if (questState.getPlayer().getClassId().getLevel() == 1 && questState.getPlayer().getLevel() < 25 && !questState.getPlayer().getVarB("p1q2")) {
                    questState.getPlayer().setVar("p1q2", "1", -1L);
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n Go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                    QuestState questState3 = questState.getPlayer().getQuestState(_255_Tutorial.class);
                    if (questState3 != null && questState3.getInt("tutorial_quest_ex") != 10) {
                        questState.showQuestionMark(26);
                        questState3.set("tutorial_quest_ex", "10");
                        if (questState.getPlayer().getClassId().isMage()) {
                            questState.playTutorialVoice("tutorial_voice_027");
                            questState.giveItems(5790, 3000L);
                        } else {
                            questState.playTutorialVoice("tutorial_voice_026");
                            questState.giveItems(5789, 6000L);
                        }
                    }
                }
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20311) {
            if (n2 == 1) {
                if (Rnd.chance(90)) {
                    questState.giveItems(1475, 1L);
                } else {
                    questState.giveItems(1476, 1L);
                }
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20312) {
            if (n2 == 1) {
                if (Rnd.chance(87)) {
                    questState.giveItems(1475, 1L);
                } else {
                    questState.giveItems(1476, 1L);
                }
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20313 && n2 == 1) {
            if (Rnd.chance(77)) {
                questState.giveItems(1475, 1L);
            } else {
                questState.giveItems(1476, 1L);
            }
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

