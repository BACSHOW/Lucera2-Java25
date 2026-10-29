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

public class _265_ChainsOfSlavery
extends Quest
implements ScriptFile {
    private static final int aIr = 30357;
    private static final int bdJ = 20004;
    private static final int bdK = 20005;
    private static final int bdL = 1368;

    public _265_ChainsOfSlavery() {
        super(0);
        this.addStartNpc(30357);
        this.addKillId(20004);
        this.addKillId(20005);
        this.addQuestItem(1368);
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
        if (string.equalsIgnoreCase("sentry_krpion_q0265_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("sentry_krpion_q0265_06.htm")) {
            questState.exitCurrentQuest(true);
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
                if (n != 30357) break;
                if (questState.getPlayer().getLevel() >= 6 && questState.getPlayer().getRace() == Race.darkelf) {
                    string = "sentry_krpion_q0265_02.htm";
                    break;
                }
                if (questState.getPlayer().getRace() != Race.darkelf) {
                    string = "sentry_krpion_q0265_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getPlayer().getLevel() >= 6) break;
                string = "sentry_krpion_q0265_01.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n != 30357) break;
                if (questState.getQuestItemsCount(1368) > 0L) {
                    if (questState.getQuestItemsCount(1368) >= 10L) {
                        questState.giveItems(57, 12L * questState.getQuestItemsCount(1368) + 500L);
                    } else {
                        questState.giveItems(57, 12L * questState.getQuestItemsCount(1368));
                    }
                    questState.takeItems(1368, -1L);
                    if (questState.getPlayer().getLevel() < 25 && questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("p1q2")) {
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
                    string = "sentry_krpion_q0265_05.htm";
                    break;
                }
                string = "sentry_krpion_q0265_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1 && Rnd.chance(55)) {
            questState.giveItems(1368, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

