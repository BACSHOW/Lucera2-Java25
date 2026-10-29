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

public class _102_SeaofSporesFever
extends Quest
implements ScriptFile {
    private static final int aCV = 964;
    private static final int aCW = 965;
    private static final int aCX = 966;
    private static final int aCY = 1130;
    private static final int aCZ = 1131;
    private static final int aDa = 1132;
    private static final int aDb = 1133;
    private static final int aDc = 1134;
    private static final int aDd = 743;
    private static final int aDe = 744;
    private static final int aDf = 746;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _102_SeaofSporesFever() {
        super(0);
        this.addStartNpc(30284);
        this.addTalkId(30156);
        this.addTalkId(30217);
        this.addTalkId(30219);
        this.addTalkId(30221);
        this.addTalkId(30284);
        this.addTalkId(30285);
        this.addKillId(20013);
        this.addKillId(20019);
        this.addQuestItem(964, 965, 966, 1130, 1131, 1132, 1133, 1134, 746);
    }

    private void a(QuestState questState) {
        if (questState.getQuestItemsCount(1131) == 0L && questState.getQuestItemsCount(1132) == 0L && questState.getQuestItemsCount(1133) == 0L && questState.getQuestItemsCount(1134) == 0L) {
            questState.setCond(6);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("alberryus_q0102_02.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.giveItems(964, 1L);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30284) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() != Race.elf) {
                    string = "alberryus_q0102_00.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    if (questState.getPlayer().getLevel() >= 12) {
                        string = "alberryus_q0102_07.htm";
                        return string;
                    }
                    string = "alberryus_q0102_08.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1 && questState.getQuestItemsCount(964) == 1L) {
                string = "alberryus_q0102_03.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(965) == 1L) {
                string = "alberryus_q0102_09.htm";
            } else if (n2 == 4 && questState.getQuestItemsCount(1130) == 1L) {
                questState.setCond(5);
                questState.takeItems(1130, 1L);
                questState.giveItems(746, 1L);
                string = "alberryus_q0102_04.htm";
            } else if (n2 == 5) {
                string = "alberryus_q0102_05.htm";
            } else if (n2 == 6 && questState.getQuestItemsCount(746) == 1L) {
                questState.takeItems(746, 1L);
                if (questState.getPlayer().getClassId().isMage()) {
                    questState.giveItems(744, 1L);
                } else {
                    questState.giveItems(743, 1L);
                }
                if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("p1q3")) {
                    questState.getPlayer().setVar("p1q3", "1", -1L);
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                    questState.giveItems(1060, 100L);
                    for (int i = 4412; i <= 4417; ++i) {
                        questState.giveItems(i, 10L);
                    }
                    if (questState.getPlayer().getClassId().isMage()) {
                        questState.playTutorialVoice("tutorial_voice_027");
                        questState.giveItems(744, 1L);
                        questState.giveItems(2509, 500L);
                    } else {
                        questState.playTutorialVoice("tutorial_voice_026");
                        questState.giveItems(743, 1L);
                        questState.giveItems(1835, 1000L);
                    }
                }
                string = "alberryus_q0102_06.htm";
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        } else if (n == 30156) {
            if (n2 == 1 && questState.getQuestItemsCount(964) == 1L) {
                questState.takeItems(964, 1L);
                questState.giveItems(965, 1L);
                questState.setCond(2);
                string = "cob_q0102_03.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(965) > 0L && questState.getQuestItemsCount(966) < 10L) {
                string = "cob_q0102_04.htm";
            } else if (n2 > 3 && questState.getQuestItemsCount(746) > 0L) {
                string = "cob_q0102_07.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(965) > 0L && questState.getQuestItemsCount(966) >= 10L) {
                questState.takeItems(965, 1L);
                questState.takeItems(966, -1L);
                questState.giveItems(1130, 1L);
                questState.giveItems(1131, 1L);
                questState.giveItems(1132, 1L);
                questState.giveItems(1133, 1L);
                questState.giveItems(1134, 1L);
                questState.setCond(4);
                string = "cob_q0102_05.htm";
            } else if (n2 == 4) {
                string = "cob_q0102_06.htm";
            }
        } else if (n == 30217 && n2 == 5 && questState.getQuestItemsCount(746) == 1L && questState.getQuestItemsCount(1131) == 1L) {
            questState.takeItems(1131, 1L);
            string = "sentinel_berryos_q0102_01.htm";
            this.a(questState);
        } else if (n == 30219 && n2 == 5 && questState.getQuestItemsCount(746) == 1L && questState.getQuestItemsCount(1132) == 1L) {
            questState.takeItems(1132, 1L);
            string = "sentinel_veltress_q0102_01.htm";
            this.a(questState);
        } else if (n == 30221 && n2 == 5 && questState.getQuestItemsCount(746) == 1L && questState.getQuestItemsCount(1133) == 1L) {
            questState.takeItems(1133, 1L);
            string = "sentinel_rayjien_q0102_01.htm";
            this.a(questState);
        } else if (n == 30285 && n2 == 5 && questState.getQuestItemsCount(746) == 1L && questState.getQuestItemsCount(1134) == 1L) {
            questState.takeItems(1134, 1L);
            string = "sentinel_gartrandell_q0102_01.htm";
            this.a(questState);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if ((n == 20013 || n == 20019) && Rnd.chance(33) && questState.getQuestItemsCount(965) > 0L && questState.getQuestItemsCount(966) < 10L) {
            questState.giveItems(966, 1L);
            if (questState.getQuestItemsCount(966) == 10L) {
                questState.setCond(3);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

