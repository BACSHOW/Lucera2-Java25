/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.Summon;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _422_RepentYourSins
extends Quest
implements ScriptFile {
    private static final int bCE = 4326;
    private static final int bCF = 4327;
    private static final int bCG = 4328;
    private static final int bCH = 4329;
    private static final int bCI = 4331;
    private static final int bCJ = 4425;
    private static final int bCK = 4330;
    private static final int bCL = 4426;
    private static final int SILVER_NUGGET = 1873;
    private static final int bCM = 1877;
    private static final int bCN = 1892;
    private static final int bCO = 1879;
    private static final int bCP = 1880;
    private static final int bCQ = 30981;
    private static final int aUZ = 30668;
    private static final int aQL = 30597;
    private static final int aLu = 30612;
    private static final int bCR = 30718;
    private static final int bCS = 30300;
    private static final int bCT = 12564;
    private static final int bCU = 20039;
    private static final int bCV = 20494;
    private static final int bCW = 20193;
    private static final int bCX = 20561;

    public int findPetLvl(QuestState questState) {
        ItemInstance itemInstance = questState.getPlayer().getInventory().getItemByItemId(4425);
        if (itemInstance == null) {
            return 0;
        }
        Summon summon = questState.getPlayer().getPet();
        if (summon == null) {
            return itemInstance.getEnchantLevel();
        }
        if (summon.getNpcId() != 12564) {
            return 0;
        }
        return summon.getLevel();
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

    public _422_RepentYourSins() {
        super(0);
        this.addStartNpc(30981);
        this.addTalkId(30668);
        this.addTalkId(30597);
        this.addTalkId(30612);
        this.addTalkId(30718);
        this.addTalkId(30300);
        this.addKillId(20039);
        this.addKillId(20494);
        this.addKillId(20193);
        this.addKillId(20561);
        this.addQuestItem(4326);
        this.addQuestItem(4327);
        this.addQuestItem(4328);
        this.addQuestItem(4329);
        this.addQuestItem(4331);
        this.addQuestItem(4425);
        this.addQuestItem(4330);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equalsIgnoreCase("Start")) {
            questState.playSound("ItemSound.quest_accept");
            questState.setState(2);
            if (questState.getPlayer().getLevel() <= 20) {
                questState.setCond(1);
                questState.setCond(2);
                return "black_judge_q0422_03.htm";
            }
            if (questState.getPlayer().getLevel() <= 30) {
                questState.setCond(3);
                return "black_judge_q0422_04.htm";
            }
            if (questState.getPlayer().getLevel() <= 40) {
                questState.setCond(4);
                return "black_judge_q0422_05.htm";
            }
            questState.setCond(5);
            return "black_judge_q0422_06.htm";
        }
        if (string.equalsIgnoreCase("1")) {
            if (questState.getQuestItemsCount(4330) >= 1L) {
                questState.takeItems(4330, -1L);
            }
            if (questState.getQuestItemsCount(4425) >= 1L) {
                questState.takeItems(4425, -1L);
            }
            questState.setCond(16);
            questState.set("level", String.valueOf(questState.getPlayer().getLevel()));
            questState.giveItems(4425, 1L);
            return "black_judge_q0422_11.htm";
        }
        if (string.equalsIgnoreCase("2")) {
            return "black_judge_q0422_14.htm";
        }
        if (string.equalsIgnoreCase("3")) {
            int n = this.findPetLvl(questState);
            int n2 = questState.getPlayer().getLevel();
            int n3 = questState.getInt("level");
            Summon summon = questState.getPlayer().getPet();
            if (summon != null) {
                if (summon.getNpcId() == 12564) {
                    return "black_judge_q0422_15t.htm";
                }
            } else {
                int n4 = n2 > n3 ? n - n2 : n - n3;
                if (n4 < 0) {
                    n4 = 0;
                }
                n4 = Rnd.get(10 + n4) + 1;
                if (questState.getPlayer().getPkKills() <= n4) {
                    questState.getPlayer().setPkKills(0);
                    questState.playSound("ItemSound.quest_finish");
                    if (questState.getQuestItemsCount(4426) < 1L) {
                        questState.giveItems(4426, 1L);
                    }
                    questState.exitCurrentQuest(true);
                    return "black_judge_q0422_15.htm";
                }
                questState.takeItems(4425, 1L);
                int n5 = questState.getPlayer().getPkKills() - n4;
                questState.getPlayer().setPkKills(n5);
                questState.set("level", "0");
                return "black_judge_q0422_16.htm";
            }
        }
        if (string.equalsIgnoreCase("4")) {
            return "black_judge_q0422_17.htm";
        }
        if (string.equalsIgnoreCase("Quit")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            return "black_judge_q0422_18.htm";
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getCond();
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        if (n2 == 30981) {
            if (n3 == 1) {
                if (questState.getPlayer().getPkKills() >= 1) {
                    return "black_judge_q0422_02.htm";
                }
                questState.exitCurrentQuest(true);
                return "black_judge_q0422_01.htm";
            }
            if (n <= 9) {
                return "black_judge_q0422_07.htm";
            }
            if (n <= 13 && n > 9 && questState.getQuestItemsCount(4331) < 1L) {
                if (questState.getQuestItemsCount(4426) < 1L) {
                    questState.setCond(14);
                    questState.giveItems(4331, 1L);
                    return "black_judge_q0422_08.htm";
                }
                questState.takeItems(4426, -1L);
                if (questState.getQuestItemsCount(4425) < 1L) {
                    questState.giveItems(4425, 1L, false);
                }
                questState.setCond(16);
                n = 16;
            }
            if (n == 14 && questState.getQuestItemsCount(4331) > 0L) {
                return "black_judge_q0422_09.htm";
            }
            if (n == 15 && questState.getQuestItemsCount(4330) > 0L) {
                return "black_judge_q0422_10.htm";
            }
            if (n >= 16) {
                if (questState.getQuestItemsCount(4425) > 0L) {
                    int n4 = this.findPetLvl(questState);
                    int n5 = questState.getPlayer().getLevel();
                    if (questState.getInt("level") > n5) {
                        n5 = questState.getInt("level");
                    }
                    if (n4 > 0) {
                        if (n4 > n5) {
                            return "black_judge_q0422_13.htm";
                        }
                        return "black_judge_q0422_12.htm";
                    }
                    return "black_judge_q0422_12.htm";
                }
                return "black_judge_q0422_16t.htm";
            }
        }
        if (n2 == 30668) {
            if (n == 2) {
                questState.setCond(6);
                return "katari_q0422_01.htm";
            }
            if (n == 6 && questState.getQuestItemsCount(4326) < 10L) {
                return "katari_q0422_02.htm";
            }
            if (n == 10) {
                return "katari_q0422_04.htm";
            }
            questState.setCond(10);
            questState.takeItems(4326, -1L);
            return "katari_q0422_03.htm";
        }
        if (n2 == 30597) {
            if (n == 3) {
                questState.setCond(7);
                return "piotur_q0422_01.htm";
            }
            if (n == 7 && questState.getQuestItemsCount(4327) < 10L) {
                return "piotur_q0422_02.htm";
            }
            if (n == 11) {
                return "piotur_q0422_04.htm";
            }
            questState.setCond(11);
            questState.takeItems(4327, -1L);
            return "piotur_q0422_03.htm";
        }
        if (n2 == 30612) {
            if (n == 4) {
                questState.setCond(8);
                return "sage_kasian_q0422_01.htm";
            }
            if (n == 8) {
                if (questState.getQuestItemsCount(4328) < 1L) {
                    return "sage_kasian_q0422_02.htm";
                }
                questState.setCond(12);
                questState.takeItems(4328, -1L);
                return "sage_kasian_q0422_03.htm";
            }
            if (n == 12) {
                return "sage_kasian_q0422_04.htm";
            }
        }
        if (n2 == 30718) {
            if (n == 5) {
                questState.setCond(9);
                return "magister_joan_q0422_01.htm";
            }
            if (n == 9 && questState.getQuestItemsCount(4329) < 3L) {
                return "magister_joan_q0422_02.htm";
            }
            if (questState.getQuestItemsCount(4329) >= 3L) {
                questState.setCond(13);
                questState.takeItems(4329, -1L);
                return "magister_joan_q0422_03.htm";
            }
            if (n == 13) {
                return "magister_joan_q0422_04.htm";
            }
        }
        if (n2 == 30300 && n >= 14) {
            if (questState.getQuestItemsCount(4331) == 1L) {
                if (questState.getQuestItemsCount(1873) < 10L || questState.getQuestItemsCount(1880) < 5L || questState.getQuestItemsCount(1877) < 2L || questState.getQuestItemsCount(1879) < 10L || questState.getQuestItemsCount(1892) < 1L) {
                    return "blacksmith_pushkin_q0422_02.htm";
                }
                if (questState.getQuestItemsCount(1873) >= 10L && questState.getQuestItemsCount(1880) >= 5L && questState.getQuestItemsCount(1877) >= 2L && questState.getQuestItemsCount(1879) >= 10L && questState.getQuestItemsCount(1892) >= 1L) {
                    questState.setCond(15);
                    questState.takeItems(4331, 1L);
                    questState.takeItems(1873, 10L);
                    questState.takeItems(1877, 2L);
                    questState.takeItems(1879, 10L);
                    questState.takeItems(1880, 5L);
                    questState.takeItems(1892, 1L);
                    questState.giveItems(4330, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    return "blacksmith_pushkin_q0422_02.htm";
                }
            }
            if (questState.getQuestItemsCount(4330) > 0L || questState.getQuestItemsCount(4425) > 0L || questState.getQuestItemsCount(4426) > 0L) {
                return "blacksmith_pushkin_q0422_03.htm";
            }
        }
        return "noquest";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        int n = questState.getCond();
        int n2 = npcInstance.getNpcId();
        long l = questState.getQuestItemsCount(4326);
        long l2 = questState.getQuestItemsCount(4327);
        long l3 = questState.getQuestItemsCount(4328);
        long l4 = questState.getQuestItemsCount(4329);
        if (n2 == 20039 && n == 6 && l < 10L) {
            questState.giveItems(4326, 1L);
            if (l == 10L) {
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        if (n2 == 20494 && n == 7 && l2 < 10L) {
            questState.giveItems(4327, 1L);
            if (l2 == 10L) {
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        if (n2 == 20193 && n == 8 && l3 < 1L) {
            questState.giveItems(4328, 1L);
            questState.playSound("ItemSound.quest_middle");
        }
        if (n2 == 20561 && n == 9 && l4 < 3L) {
            questState.giveItems(4329, 1L);
            if (l == 3L) {
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

