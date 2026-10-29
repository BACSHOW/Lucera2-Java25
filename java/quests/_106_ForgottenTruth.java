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

public class _106_ForgottenTruth
extends Quest
implements ScriptFile {
    private static final int aDP = 984;
    private static final int aDQ = 985;
    private static final int ANCIENT_SCROLL = 986;
    private static final int aDR = 987;
    private static final int aDS = 988;
    private static final int aDT = 989;
    private static final int aDU = 2373;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _106_ForgottenTruth() {
        super(0);
        this.addStartNpc(30358);
        this.addTalkId(30133);
        this.addKillId(27070);
        this.addQuestItem(988, 984, 985, 986, 987);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("tetrarch_thifiell_q0106_05.htm")) {
            questState.giveItems(984, 1L);
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30358) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() != Race.darkelf) {
                    string = "tetrarch_thifiell_q0106_00.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() >= 10) {
                    string = "tetrarch_thifiell_q0106_03.htm";
                } else {
                    string = "tetrarch_thifiell_q0106_02.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 > 0 && (questState.getQuestItemsCount(984) > 0L || questState.getQuestItemsCount(985) > 0L) && questState.getQuestItemsCount(988) == 0L) {
                string = "tetrarch_thifiell_q0106_06.htm";
            } else if (n2 == 4 && questState.getQuestItemsCount(988) > 0L) {
                string = "tetrarch_thifiell_q0106_07.htm";
                questState.takeItems(988, -1L);
                if (questState.getPlayer().isMageClass()) {
                    questState.giveItems(2373, 1L);
                } else {
                    questState.giveItems(989, 1L);
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
                        questState.giveItems(5790, 3000L);
                        questState.giveItems(2509, 500L);
                    } else {
                        questState.playTutorialVoice("tutorial_voice_026");
                        questState.giveItems(5789, 7000L);
                        questState.giveItems(1835, 1000L);
                    }
                }
                questState.exitCurrentQuest(false);
                questState.playSound("ItemSound.quest_finish");
            }
        } else if (n == 30133) {
            if (n2 == 1 && questState.getQuestItemsCount(984) > 0L) {
                string = "karta_q0106_01.htm";
                questState.takeItems(984, -1L);
                questState.giveItems(985, 1L);
                questState.setCond(2);
            } else if (n2 == 2 && questState.getQuestItemsCount(985) > 0L && (questState.getQuestItemsCount(986) == 0L || questState.getQuestItemsCount(987) == 0L)) {
                string = "karta_q0106_02.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(986) > 0L && questState.getQuestItemsCount(987) > 0L) {
                string = "karta_q0106_03.htm";
                questState.takeItems(985, -1L);
                questState.takeItems(986, -1L);
                questState.takeItems(987, -1L);
                questState.giveItems(988, 1L);
                questState.setCond(4);
            } else if (n2 == 4 && questState.getQuestItemsCount(988) > 0L) {
                string = "karta_q0106_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 27070 && questState.getCond() == 2 && questState.getQuestItemsCount(985) > 0L) {
            if (Rnd.chance(20) && questState.getQuestItemsCount(986) == 0L) {
                questState.giveItems(986, 1L);
                questState.playSound("ItemSound.quest_middle");
            } else if (Rnd.chance(10) && questState.getQuestItemsCount(987) == 0L) {
                questState.giveItems(987, 1L);
                questState.playSound("ItemSound.quest_middle");
            }
        }
        if (questState.getQuestItemsCount(986) > 0L && questState.getQuestItemsCount(987) > 0L) {
            questState.setCond(3);
        }
        return null;
    }
}

