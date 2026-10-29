/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;

public class _365_DevilsLegacy
extends Quest
implements ScriptFile {
    private static final int bsk = 30095;
    private static final int blQ = 30092;
    int[] MOBS = new int[]{20836, 29027, 20845, 21629, 21630, 29026};
    private static final int bsl = 25;
    private static final int bsm = 5070;
    private static final int bsn = 5873;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _365_DevilsLegacy() {
        super(0);
        this.addStartNpc(30095);
        this.addTalkId(30092);
        this.addKillId(this.MOBS);
        this.addQuestItem(5873);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("30095-1.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30095-5.htm")) {
            long l = questState.getQuestItemsCount(5873);
            if (l > 0L) {
                long l2 = l * 5070L;
                questState.takeItems(5873, -1L);
                questState.giveItems(57, l2);
            } else {
                string2 = "You don't have required items";
            }
        } else if (string.equalsIgnoreCase("30095-6.htm")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        } else if (string.equalsIgnoreCase("30092_reward")) {
            if (questState.getQuestItemsCount(5873) < 1L) {
                string2 = "collob_q0365_03.htm";
            } else if (questState.getQuestItemsCount(57) < 600L) {
                string2 = "collob_q0365_04.htm";
            } else if (questState.getInt("cond") == 0) {
                string2 = "collob_q0365_05.htm";
            } else if (questState.getQuestItemsCount(5873) >= 1L && questState.getQuestItemsCount(57) >= 600L && questState.getInt("cond") == 1) {
                if (Rnd.get(100) < 80) {
                    int n = Rnd.get(100);
                    if (n < 1) {
                        questState.giveItems(995, 1L);
                    } else if (n < 4) {
                        questState.giveItems(956, 1L);
                    } else if (n < 36) {
                        questState.giveItems(1868, 1L);
                    } else if (n < 68) {
                        questState.giveItems(1884, 1L);
                    } else {
                        questState.giveItems(1872, 1L);
                    }
                    questState.takeItems(5873, 1L);
                    questState.takeItems(57, 600L);
                    string2 = "collob_q0365_06.htm";
                } else {
                    int n = Rnd.get(1000);
                    if (n < 10) {
                        questState.giveItems(951, 1L);
                    } else if (n < 40) {
                        questState.giveItems(952, 1L);
                    } else if (n < 60) {
                        questState.giveItems(955, 1L);
                    } else if (n < 260) {
                        questState.giveItems(956, 1L);
                    } else if (n < 445) {
                        questState.giveItems(1879, 1L);
                    } else if (n < 630) {
                        questState.giveItems(1880, 1L);
                    } else if (n < 815) {
                        questState.giveItems(1882, 1L);
                    } else {
                        questState.giveItems(1881, 1L);
                    }
                    SkillTable.getInstance().getInfo(4035, 2).getEffects(questState.getPlayer(), questState.getPlayer(), false, false);
                    questState.takeItems(5873, 1L);
                    questState.takeItems(57, 600L);
                    string2 = "collob_q0365_07.htm";
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        int n2 = npcInstance.getNpcId();
        if (n2 == 30095) {
            if (n == 0) {
                if (questState.getPlayer().getLevel() >= 39) {
                    string = "30095-0.htm";
                } else {
                    string = "30095-0a.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n == 1) {
                string = questState.getQuestItemsCount(5873) == 0L ? "30095-2.htm" : "30095-4.htm";
            }
        }
        if (n2 == 30092) {
            if (n == 0) {
                string = "collob_q0365_02.htm";
            } else if (n == 1) {
                string = "collob_q0365_01.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (Rnd.chance(25)) {
            questState.giveItems(5873, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

