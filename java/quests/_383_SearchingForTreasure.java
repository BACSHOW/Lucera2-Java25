/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _383_SearchingForTreasure
extends Quest
implements ScriptFile {
    private static final int bxQ = 30890;
    private static final int bxR = 31148;
    private static final int bqR = 5915;
    private static final int bxS = 1661;
    private static final int bxT = 2450;
    private static final int bxU = 2451;
    private static final int azF = 956;
    private static final int bxV = 952;
    private static final int bxW = 4481;
    private static final int bxX = 4482;
    private static final int bxY = 4483;
    private static final int bxZ = 4484;
    private static final int bya = 4485;
    private static final int byb = 4486;
    private static final int byc = 4487;
    private static final int byd = 4488;
    private static final int bye = 4489;
    private static final int byf = 4490;
    private static final int byg = 4491;
    private static final int byh = 4492;
    private static final int byi = 1337;
    private static final int byj = 1338;
    private static final int byk = 1339;
    private static final int biP = 3447;
    private static final int biS = 3450;
    private static final int biV = 3453;
    private static final int biY = 3456;
    private static final int bka = 4408;
    private static final int bkb = 4409;
    private static final int byl = 4418;
    private static final int bym = 4419;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _383_SearchingForTreasure() {
        super(0);
        this.addStartNpc(30890);
        this.addTalkId(31148);
        this.addQuestItem(5915);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("treasure_hunt");
        int n2 = npcInstance.getNpcId();
        if (n2 == 30890) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("treasure_hunt", String.valueOf(1), true);
                questState.takeItems(5915, 1L);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "trader_espen_q0383_08.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "trader_espen_q0383_04.htm";
            } else if (string.equalsIgnoreCase("reply_2") && questState.getQuestItemsCount(5915) > 0L) {
                questState.giveItems(57, 1000L);
                questState.unset("treasure_hunt");
                questState.takeItems(5915, 1L);
                string2 = "trader_espen_q0383_05.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = questState.getQuestItemsCount(5915) > 0L ? "trader_espen_q0383_06.htm" : "trader_espen_q0383_07.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "trader_espen_q0383_09.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = "trader_espen_q0383_10.htm";
            } else if (string.equalsIgnoreCase("reply_6")) {
                string2 = "trader_espen_q0383_11.htm";
            } else if (string.equalsIgnoreCase("reply_7") && n == 1) {
                questState.setCond(2);
                questState.set("treasure_hunt", String.valueOf(2), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "trader_espen_q0383_12.htm";
            }
        } else if (n2 == 31148 && string.equalsIgnoreCase("reply_1")) {
            if (questState.getQuestItemsCount(1661) == 0L) {
                string2 = "pirates_t_chest_q0383_02.htm";
            } else if (n == 2 && questState.getQuestItemsCount(1661) >= 1L) {
                questState.takeItems(1661, 1L);
                questState.unset("treasure_hunt");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "pirates_t_chest_q0383_03.htm";
                int n3 = 0;
                int n4 = Rnd.get(100);
                if (n4 < 5) {
                    questState.giveItems(2450, 1L);
                } else if (n4 < 6) {
                    questState.giveItems(2451, 1L);
                } else if (n4 < 18) {
                    questState.giveItems(956, 1L);
                } else if (n4 < 28) {
                    questState.giveItems(952, 1L);
                } else {
                    n3 += 500;
                }
                int n5 = Rnd.get(1000);
                if (n5 < 25) {
                    questState.giveItems(4481, 1L);
                } else if (n5 < 50) {
                    questState.giveItems(4482, 1L);
                } else if (n5 < 75) {
                    questState.giveItems(4483, 1L);
                } else if (n5 < 100) {
                    questState.giveItems(4484, 1L);
                } else if (n5 < 125) {
                    questState.giveItems(4485, 1L);
                } else if (n5 < 150) {
                    questState.giveItems(4486, 1L);
                } else if (n5 < 175) {
                    questState.giveItems(4487, 1L);
                } else if (n5 < 200) {
                    questState.giveItems(4488, 1L);
                } else if (n5 < 225) {
                    questState.giveItems(4489, 1L);
                } else if (n5 < 250) {
                    questState.giveItems(4490, 1L);
                } else if (n5 < 275) {
                    questState.giveItems(4491, 1L);
                } else if (n5 < 300) {
                    questState.giveItems(4492, 1L);
                } else {
                    n3 += 300;
                }
                int n6 = Rnd.get(100);
                if (n6 < 4) {
                    questState.giveItems(1337, 1L);
                } else if (n6 < 8) {
                    questState.giveItems(1338, 2L);
                } else if (n6 < 12) {
                    questState.giveItems(1339, 2L);
                } else if (n6 < 16) {
                    questState.giveItems(3447, 2L);
                } else if (n6 < 20) {
                    questState.giveItems(3450, 1L);
                } else if (n6 < 25) {
                    questState.giveItems(3453, 1L);
                } else if (n6 < 27) {
                    questState.giveItems(3456, 1L);
                } else {
                    n3 += 500;
                }
                int n7 = Rnd.get(100);
                if (n7 < 20) {
                    questState.giveItems(4408, 1L);
                } else if (n7 < 40) {
                    questState.giveItems(4409, 1L);
                } else if (n7 < 60) {
                    questState.giveItems(4418, 1L);
                } else if (n7 < 80) {
                    questState.giveItems(4419, 1L);
                } else {
                    n3 += 500;
                }
                questState.giveItems(57, n3);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("treasure_hunt");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30890) break;
                if (questState.getPlayer().getLevel() < 42) {
                    string = "trader_espen_q0383_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getPlayer().getLevel() >= 42 && questState.getQuestItemsCount(5915) == 0L) {
                    string = "trader_espen_q0383_02.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getPlayer().getLevel() < 42 || questState.getQuestItemsCount(5915) <= 0L) break;
                string = "trader_espen_q0383_03.htm";
                break;
            }
            case 2: {
                if (n2 == 30890) {
                    if (n == 1) {
                        string = "trader_espen_q0383_13.htm";
                        break;
                    }
                    if (n != 2) break;
                    string = "trader_espen_q0383_14.htm";
                    break;
                }
                if (n2 != 31148 || n != 2) break;
                string = "pirates_t_chest_q0383_01.htm";
            }
        }
        return string;
    }
}

