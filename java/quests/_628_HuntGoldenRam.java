/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _628_HuntGoldenRam
extends Quest
implements ScriptFile {
    private static final int bQe = 31554;
    private static final int bbK = 21508;
    private static final int bbL = 21509;
    private static final int bbM = 21510;
    private static final int bbN = 21511;
    private static final int bGQ = 21512;
    private static final int bQf = 21513;
    private static final int bFZ = 21514;
    private static final int bQg = 21515;
    private static final int bFX = 21516;
    private static final int bFY = 21517;
    private static int bQh = 7246;
    private static int bQi = 7247;
    private static int bQj = 7248;
    private static int bQk = 7249;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _628_HuntGoldenRam() {
        super(1);
        this.addStartNpc(31554);
        this.addKillId(21508, 21509, 21510, 21511, 21512, 21513, 21514, 21515, 21516, 21517);
        this.addQuestItem(bQj, bQk);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            if (questState.getQuestItemsCount(bQh) < 1L && questState.getQuestItemsCount(bQi) < 1L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "merc_kahmun_q0628_03.htm";
            } else if (questState.getQuestItemsCount(bQh) >= 1L && questState.getQuestItemsCount(bQi) < 1L) {
                questState.setCond(2);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "merc_kahmun_q0628_04.htm";
            } else if (questState.getQuestItemsCount(bQi) >= 1L) {
                questState.setCond(3);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "merc_kahmun_q0628_05.htm";
            }
        } else if (string.equalsIgnoreCase("reply_1")) {
            if (questState.getQuestItemsCount(bQh) < 1L && questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQj) >= 100L) {
                questState.setCond(2);
                questState.giveItems(bQh, 1L);
                questState.takeItems(bQj, -1L);
                questState.getPlayer().updateRam();
                string2 = "merc_kahmun_q0628_08.htm";
            }
        } else if (string.equalsIgnoreCase("reply_3")) {
            questState.takeItems(bQh, -1L);
            questState.takeItems(bQi, -1L);
            questState.takeItems(bQj, -1L);
            questState.takeItems(bQk, -1L);
            questState.getPlayer().updateRam();
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            string2 = "merc_kahmun_q0628_13.htm";
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
                if (n != 31554) break;
                if (questState.getPlayer().getLevel() >= 66) {
                    string = "merc_kahmun_q0628_01.htm";
                    break;
                }
                string = "merc_kahmun_q0628_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n != 31554) break;
                if (questState.getQuestItemsCount(bQh) < 1L && questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQj) < 100L) {
                    string = "merc_kahmun_q0628_06.htm";
                    break;
                }
                if (questState.getQuestItemsCount(bQh) < 1L && questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQj) >= 100L) {
                    string = "merc_kahmun_q0628_07.htm";
                    break;
                }
                if (questState.getQuestItemsCount(bQh) >= 1L && questState.getQuestItemsCount(bQi) < 1L && (questState.getQuestItemsCount(bQj) < 100L || questState.getQuestItemsCount(bQk) < 100L)) {
                    string = "merc_kahmun_q0628_09.htm";
                    break;
                }
                if (questState.getQuestItemsCount(bQh) >= 1L && questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQj) >= 100L && questState.getQuestItemsCount(bQk) >= 100L) {
                    questState.setCond(3);
                    questState.giveItems(bQi, 1L);
                    questState.takeItems(bQh, -1L);
                    questState.takeItems(bQj, -1L);
                    questState.takeItems(bQk, -1L);
                    questState.playSound("ItemSound.quest_middle");
                    string = "merc_kahmun_q0628_10.htm";
                    break;
                }
                if (questState.getQuestItemsCount(bQi) < 1L) break;
                string = "merc_kahmun_q0628_11.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        if (questState.getState() != 2) {
            return null;
        }
        int n2 = npcInstance.getNpcId();
        if (n2 == 21508) {
            int n3;
            if (questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQj) < 100L && (n3 = Rnd.get(100)) < 50) {
                if (questState.getQuestItemsCount(bQh) >= 1L) {
                    if (questState.getQuestItemsCount(bQj) >= 99L) {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_itemget");
                    }
                } else if (questState.getQuestItemsCount(bQj) >= 99L) {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21509) {
            int n4;
            if (questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQj) < 100L && (n4 = Rnd.get(100)) < 43) {
                if (questState.getQuestItemsCount(bQh) >= 1L) {
                    if (questState.getQuestItemsCount(bQj) >= 99L) {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_itemget");
                    }
                } else if (questState.getQuestItemsCount(bQj) >= 99L) {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21510) {
            int n5;
            if (questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQj) < 100L && (n5 = Rnd.get(1000)) < 521) {
                if (questState.getQuestItemsCount(bQh) >= 1L) {
                    if (questState.getQuestItemsCount(bQj) >= 99L) {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_itemget");
                    }
                } else if (questState.getQuestItemsCount(bQj) >= 99L) {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21511) {
            int n6;
            if (questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQj) < 100L && (n6 = Rnd.get(1000)) < 575) {
                if (questState.getQuestItemsCount(bQh) >= 1L) {
                    if (questState.getQuestItemsCount(bQj) >= 99L) {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_itemget");
                    }
                } else if (questState.getQuestItemsCount(bQj) >= 99L) {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21512) {
            int n7;
            if (questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQj) < 100L && (n7 = Rnd.get(1000)) < 746) {
                if (questState.getQuestItemsCount(bQh) >= 1L) {
                    if (questState.getQuestItemsCount(bQj) >= 99L) {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.rollAndGive(bQj, 1, 100.0);
                        questState.playSound("ItemSound.quest_itemget");
                    }
                } else if (questState.getQuestItemsCount(bQj) >= 99L) {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.rollAndGive(bQj, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21513) {
            int n8;
            if (questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQh) >= 1L && questState.getQuestItemsCount(bQk) < 100L && (n8 = Rnd.get(100)) < 50) {
                if (questState.getQuestItemsCount(bQk) >= 99L) {
                    questState.rollAndGive(bQk, 1, 100.0);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.rollAndGive(bQk, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21514) {
            int n9;
            if (questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQh) >= 1L && questState.getQuestItemsCount(bQk) < 100L && (n9 = Rnd.get(100)) < 43) {
                if (questState.getQuestItemsCount(bQk) >= 99L) {
                    questState.rollAndGive(bQk, 1, 100.0);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.rollAndGive(bQk, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21515) {
            int n10;
            if (questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQh) >= 1L && questState.getQuestItemsCount(bQk) < 100L && (n10 = Rnd.get(100)) < 52) {
                if (questState.getQuestItemsCount(bQk) >= 99L) {
                    questState.rollAndGive(bQk, 1, 100.0);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.rollAndGive(bQk, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21516) {
            int n11;
            if (questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQh) >= 1L && questState.getQuestItemsCount(bQk) < 100L && (n11 = Rnd.get(1000)) < 531) {
                if (questState.getQuestItemsCount(bQk) >= 99L) {
                    questState.rollAndGive(bQk, 1, 100.0);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.rollAndGive(bQk, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21517 && questState.getQuestItemsCount(bQi) < 1L && questState.getQuestItemsCount(bQh) >= 1L && questState.getQuestItemsCount(bQk) < 100L && (n = Rnd.get(1000)) < 744) {
            if (questState.getQuestItemsCount(bQk) >= 99L) {
                questState.rollAndGive(bQk, 1, 100.0);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.rollAndGive(bQk, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

