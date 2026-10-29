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

public class _101_SwordOfSolidarity
extends Quest
implements ScriptFile {
    private static final int aCO = 796;
    private static final int aCP = 937;
    private static final int aCQ = 739;
    private static final int aCR = 740;
    private static final int aCS = 741;
    private static final int aCT = 742;
    private static final int aCU = 738;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _101_SwordOfSolidarity() {
        super(0);
        this.addStartNpc(30008);
        this.addTalkId(30283);
        this.addKillId(20361);
        this.addKillId(20362);
        this.addQuestItem(742, 937, 741, 740, 796, 739);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("roien_q0101_04.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(796, 1L);
        } else if (string.equalsIgnoreCase("blacksmith_alltran_q0101_02.htm")) {
            questState.setCond(2);
            questState.takeItems(796, -1L);
            questState.giveItems(937, 1L);
        } else if (string.equalsIgnoreCase("blacksmith_alltran_q0101_07.htm")) {
            questState.takeItems(739, -1L);
            questState.giveItems(738, 1L);
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
                } else {
                    questState.playTutorialVoice("tutorial_voice_026");
                    questState.giveItems(5789, 7000L);
                }
            }
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30008) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() != Race.human) {
                    string = "roien_q0101_00.htm";
                } else {
                    if (questState.getPlayer().getLevel() >= 9) {
                        string = "roien_q0101_02.htm";
                        return string;
                    }
                    string = "roien_q0101_08.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1 && questState.getQuestItemsCount(796) == 1L) {
                string = "roien_q0101_05.htm";
            } else if (n2 >= 2 && questState.getQuestItemsCount(796) == 0L && questState.getQuestItemsCount(742) == 0L) {
                if (questState.getQuestItemsCount(741) > 0L && questState.getQuestItemsCount(740) > 0L) {
                    string = "roien_q0101_12.htm";
                }
                if (questState.getQuestItemsCount(741) + questState.getQuestItemsCount(740) <= 1L) {
                    string = "roien_q0101_11.htm";
                }
                if (questState.getQuestItemsCount(739) > 0L) {
                    string = "roien_q0101_07.htm";
                }
                if (questState.getQuestItemsCount(937) == 1L) {
                    string = "roien_q0101_10.htm";
                }
            } else if (n2 == 4 && questState.getQuestItemsCount(742) > 0L) {
                string = "roien_q0101_06.htm";
                questState.setCond(5);
                questState.takeItems(742, -1L);
                questState.giveItems(739, 1L);
            }
        } else if (n == 30283) {
            if (n2 == 1 && questState.getQuestItemsCount(796) > 0L) {
                string = "blacksmith_alltran_q0101_01.htm";
            } else if (n2 >= 2 && questState.getQuestItemsCount(937) == 1L) {
                if (questState.getQuestItemsCount(741) + questState.getQuestItemsCount(740) == 1L) {
                    string = "blacksmith_alltran_q0101_08.htm";
                } else if (questState.getQuestItemsCount(741) + questState.getQuestItemsCount(740) == 0L) {
                    string = "blacksmith_alltran_q0101_03.htm";
                } else if (questState.getQuestItemsCount(741) > 0L && questState.getQuestItemsCount(740) > 0L) {
                    string = "blacksmith_alltran_q0101_04.htm";
                    questState.setCond(4);
                    questState.takeItems(937, -1L);
                    questState.takeItems(741, -1L);
                    questState.takeItems(740, -1L);
                    questState.giveItems(742, 1L);
                } else if (n2 == 4 && questState.getQuestItemsCount(742) > 0L) {
                    string = "blacksmith_alltran_q0101_05.htm";
                }
            } else if (n2 == 5 && questState.getQuestItemsCount(739) > 0L) {
                string = "blacksmith_alltran_q0101_06.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if ((n == 20361 || n == 20362) && questState.getQuestItemsCount(937) > 0L) {
            if (questState.getQuestItemsCount(741) == 0L && Rnd.chance(60)) {
                questState.giveItems(741, 1L);
                questState.playSound("ItemSound.quest_middle");
            } else if (questState.getQuestItemsCount(740) == 0L && Rnd.chance(60)) {
                questState.giveItems(740, 1L);
                questState.playSound("ItemSound.quest_middle");
            }
            if (questState.getQuestItemsCount(741) > 0L && questState.getQuestItemsCount(740) > 0L) {
                questState.setCond(3);
            }
        }
        return null;
    }
}

