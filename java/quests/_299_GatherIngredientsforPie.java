/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _299_GatherIngredientsforPie
extends Quest
implements ScriptFile {
    private static final int aQK = 30620;
    private static final int Lara = 30063;
    private static final int aQJ = 30466;
    private static final int bfb = 20934;
    private static final int bfc = 20935;
    private static final int bfd = 1865;
    private static final int bfe = 7136;
    private static final int bff = 7137;
    private static final int bfg = 7138;
    private static final int bfh = 55;
    private static final int bfi = 70;

    public _299_GatherIngredientsforPie() {
        super(0);
        this.addStartNpc(30620);
        this.addTalkId(30063);
        this.addTalkId(30466);
        this.addKillId(20934);
        this.addKillId(20935);
        this.addQuestItem(7136);
        this.addQuestItem(7137);
        this.addQuestItem(7138);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        int n2 = questState.getCond();
        if (string.equalsIgnoreCase("emilly_q0299_0104.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("emilly_q0299_0201.htm") && n == 2) {
            if (questState.getQuestItemsCount(7138) < 100L) {
                return "emilly_q0299_0202.htm";
            }
            questState.takeItems(7138, -1L);
            questState.setCond(3);
        } else if (string.equalsIgnoreCase("lars_q0299_0301.htm") && n == 2 && n2 == 3) {
            questState.giveItems(7137, 1L);
            questState.setCond(4);
        } else if (string.equalsIgnoreCase("emilly_q0299_0401.htm") && n == 2) {
            if (questState.getQuestItemsCount(7137) < 1L) {
                return "emilly_q0299_0402.htm";
            }
            questState.takeItems(7137, -1L);
            questState.setCond(5);
        } else if (string.equalsIgnoreCase("guard_bright_q0299_0501.htm") && n == 2 && n2 == 5) {
            questState.giveItems(7136, 1L);
            questState.setCond(6);
        } else if (string.equalsIgnoreCase("emilly_q0299_0601.htm") && n == 2) {
            if (questState.getQuestItemsCount(7136) < 1L) {
                return "emilly_q0299_0602.htm";
            }
            int n3 = Rnd.get(1000);
            if (n3 < 400) {
                questState.giveItems(57, 25000L);
            } else if (n3 < 550) {
                questState.giveItems(1865, 50L);
            } else if (n3 < 700) {
                questState.giveItems(1870, 50L);
            } else if (n3 < 850) {
                questState.giveItems(1869, 50L);
            } else if (n3 < 1000) {
                questState.giveItems(1871, 50L);
            }
            questState.takeItems(7136, -1L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        if (n == 1) {
            if (n2 != 30620) {
                return "noquest";
            }
            if (questState.getPlayer().getLevel() >= 34) {
                questState.setCond(0);
                return "emilly_q0299_0101.htm";
            }
            questState.exitCurrentQuest(true);
            return "emilly_q0299_0102.htm";
        }
        int n3 = questState.getCond();
        if (n2 == 30620 && n == 2) {
            if (n3 == 1 && questState.getQuestItemsCount(7138) <= 99L) {
                return "emilly_q0299_0106.htm";
            }
            if (n3 == 2 && questState.getQuestItemsCount(7138) >= 100L) {
                return "emilly_q0299_0105.htm";
            }
            if (n3 == 3 && questState.getQuestItemsCount(7137) == 0L) {
                return "emilly_q0299_0203.htm";
            }
            if (n3 == 4 && questState.getQuestItemsCount(7137) == 1L) {
                return "emilly_q0299_0301.htm";
            }
            if (n3 == 5 && questState.getQuestItemsCount(7136) == 0L) {
                return "emilly_q0299_0403.htm";
            }
            if (n3 == 6 && questState.getQuestItemsCount(7136) == 1L) {
                return "emilly_q0299_0501.htm";
            }
        }
        if (n2 == 30063 && n == 2 && n3 == 3) {
            return "lars_q0299_0201.htm";
        }
        if (n2 == 30063 && n == 2 && n3 == 4) {
            return "lars_q0299_0302.htm";
        }
        if (n2 == 30466 && n == 2 && n3 == 5) {
            return "guard_bright_q0299_0401.htm";
        }
        if (n2 == 30466 && n == 2 && n3 == 5) {
            return "guard_bright_q0299_0502.htm";
        }
        return "noquest";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2 || questState.getCond() != 1 || questState.getQuestItemsCount(7138) >= 100L) {
            return null;
        }
        int n = npcInstance.getNpcId();
        if (n == 20934 && Rnd.chance(55) || n == 20935 && Rnd.chance(70)) {
            questState.giveItems(7138, 1L);
            if (questState.getQuestItemsCount(7138) < 100L) {
                questState.playSound("ItemSound.quest_itemget");
            } else {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            }
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

