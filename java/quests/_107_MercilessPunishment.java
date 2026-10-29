/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;

public class _107_MercilessPunishment
extends Quest
implements ScriptFile {
    private static final int aDV = 1553;
    private static final int aDW = 1554;
    private static final int aDX = 1555;
    private static final int aDY = 1557;
    private static final int aDZ = 1556;
    private static final int aEa = 1558;
    private static final int aEb = 1510;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _107_MercilessPunishment() {
        super(0);
        this.addStartNpc(30568);
        this.addTalkId(30580);
        this.addKillId(27041);
        this.addQuestItem(1556, 1557, 1558, 1553, 1554, 1555);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("urutu_chief_hatos_q0107_03.htm")) {
            questState.giveItems(1553, 1L);
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("urutu_chief_hatos_q0107_06.htm")) {
            questState.takeItems(1554, 1L);
            questState.takeItems(1556, 1L);
            questState.takeItems(1557, 1L);
            questState.takeItems(1558, 1L);
            questState.takeItems(1553, 1L);
            questState.takeItems(1554, 1L);
            questState.takeItems(1555, 1L);
            questState.giveItems(57, 200L);
            questState.unset("cond");
            questState.playSound("ItemSound.quest_giveup");
        } else if (string.equalsIgnoreCase("urutu_chief_hatos_q0107_07.htm")) {
            questState.takeItems(1553, 1L);
            if (questState.getQuestItemsCount(1554) == 0L) {
                questState.giveItems(1554, 1L);
            }
        } else if (string.equalsIgnoreCase("urutu_chief_hatos_q0107_09.htm")) {
            questState.takeItems(1554, 1L);
            if (questState.getQuestItemsCount(1555) == 0L) {
                questState.giveItems(1555, 1L);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        int n3 = 0;
        if (n2 != 1) {
            n3 = questState.getCond();
        }
        if (n == 30568) {
            if (n2 == 1) {
                if (questState.getPlayer().getRace() != Race.orc) {
                    string = "urutu_chief_hatos_q0107_00.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() >= 10) {
                    string = "urutu_chief_hatos_q0107_02.htm";
                } else {
                    string = "urutu_chief_hatos_q0107_01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n3 == 1 && questState.getQuestItemsCount(1553) > 0L) {
                string = "urutu_chief_hatos_q0107_04.htm";
            } else if (n3 == 2 && questState.getQuestItemsCount(1553) > 0L && questState.getQuestItemsCount(1557) == 0L) {
                string = "urutu_chief_hatos_q0107_04.htm";
            } else if (n3 == 3 && questState.getQuestItemsCount(1553) > 0L && questState.getQuestItemsCount(1557) >= 1L) {
                string = "urutu_chief_hatos_q0107_05.htm";
                questState.setCond(4);
            } else if (n3 == 4 && questState.getQuestItemsCount(1554) > 0L && questState.getQuestItemsCount(1556) == 0L) {
                string = "urutu_chief_hatos_q0107_05.htm";
            } else if (n3 == 5 && questState.getQuestItemsCount(1554) > 0L && questState.getQuestItemsCount(1556) >= 1L) {
                string = "urutu_chief_hatos_q0107_08.htm";
                questState.setCond(6);
            } else if (n3 == 6 && questState.getQuestItemsCount(1555) > 0L && questState.getQuestItemsCount(1558) == 0L) {
                string = "urutu_chief_hatos_q0107_08.htm";
            } else if (n3 == 7 && questState.getQuestItemsCount(1555) > 0L && questState.getQuestItemsCount(1558) + questState.getQuestItemsCount(1557) + questState.getQuestItemsCount(1556) == 3L) {
                string = "urutu_chief_hatos_q0107_10.htm";
                questState.takeItems(1556, -1L);
                questState.takeItems(1557, -1L);
                questState.takeItems(1558, -1L);
                questState.takeItems(1555, -1L);
                questState.giveItems(1510, 1L);
                if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("p1q3")) {
                    questState.getPlayer().setVar("p1q3", "1", -1L);
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Acquisition of race-specific weapon complete.\n Go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                    questState.giveItems(1060, 100L);
                    for (int i = 4412; i <= 4417; ++i) {
                        questState.giveItems(i, 10L);
                    }
                    questState.playTutorialVoice("tutorial_voice_026");
                    questState.giveItems(5789, 7000L);
                }
                questState.exitCurrentQuest(false);
                questState.playSound("ItemSound.quest_finish");
            }
        } else if (n == 30580 && n3 >= 1 && (questState.getQuestItemsCount(1553) > 0L || questState.getQuestItemsCount(1554) > 0L || questState.getQuestItemsCount(1555) > 0L)) {
            if (n3 == 1) {
                questState.setCond(2);
            }
            string = "centurion_parugon_q0107_01.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 27041) {
            if (n2 == 2 && questState.getQuestItemsCount(1553) > 0L && questState.getQuestItemsCount(1557) == 0L) {
                questState.giveItems(1557, 1L);
                questState.setCond(3);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 == 4 && questState.getQuestItemsCount(1554) > 0L && questState.getQuestItemsCount(1556) == 0L) {
                questState.giveItems(1556, 1L);
                questState.setCond(5);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 == 6 && questState.getQuestItemsCount(1555) > 0L && questState.getQuestItemsCount(1558) == 0L) {
                questState.giveItems(1558, 1L);
                questState.setCond(7);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

