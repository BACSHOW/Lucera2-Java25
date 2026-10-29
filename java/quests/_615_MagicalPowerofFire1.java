/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;

public class _615_MagicalPowerofFire1
extends Quest
implements ScriptFile {
    private final int bMc = 31378;
    private final int bMd = 31379;
    private final int bMe = 31559;
    private final int bMf = 7242;
    private final int bLv = 7081;
    private final int bMg = 7243;
    private final int bLW = 7222;
    private final int bLX = 7223;
    private final int bLY = 7224;
    private final int bLZ = 7225;
    private final int bLx = 1661;
    private final int[] bMh = new int[19];

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _615_MagicalPowerofFire1() {
        super(0);
        this.addStartNpc(31378);
        this.addTalkId(31378);
        this.addTalkId(31379);
        this.addTalkId(31559);
        this.bMh[0] = 21324;
        this.bMh[1] = 21325;
        this.bMh[2] = 21327;
        this.bMh[3] = 21328;
        this.bMh[4] = 21329;
        this.bMh[5] = 21331;
        this.bMh[6] = 21332;
        this.bMh[7] = 21334;
        this.bMh[8] = 21335;
        this.bMh[9] = 21336;
        this.bMh[10] = 21338;
        this.bMh[11] = 21339;
        this.bMh[12] = 21340;
        this.bMh[13] = 21342;
        this.bMh[14] = 21343;
        this.bMh[15] = 21344;
        this.bMh[16] = 21345;
        this.bMh[17] = 21346;
        this.bMh[18] = 21347;
        for (int n : this.bMh) {
            this.addAttackId(n);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "herald_naran_q0615_02.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("615_1") && questState.getCond() == 2) {
            if (questState.getQuestItemsCount(1661) < 1L) {
                string2 = "asefas_box_q0615_02.htm";
            } else if (questState.getInt("proval") == 1) {
                string2 = "asefas_box_q0615_04.htm";
                questState.takeItems(1661, 1L);
            } else {
                questState.takeItems(1661, 1L);
                questState.giveItems(7242, 1L);
                string2 = "asefas_box_q0615_03.htm";
                questState.setCond(3);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        int n3 = questState.getInt("proval");
        switch (n) {
            case 31378: {
                if (n2 == 0) {
                    if (questState.getPlayer().getLevel() >= 74) {
                        if (questState.getQuestItemsCount(7222) == 1L || questState.getQuestItemsCount(7223) == 1L || questState.getQuestItemsCount(7224) == 1L || questState.getQuestItemsCount(7225) == 1L) {
                            if (questState.getQuestItemsCount(7081) == 0L) {
                                string = "herald_naran_q0615_01.htm";
                                break;
                            }
                            string = "completed";
                            questState.exitCurrentQuest(true);
                            break;
                        }
                        string = "herald_naran_q0615_01a.htm";
                        questState.exitCurrentQuest(true);
                        break;
                    }
                    string = "herald_naran_q0615_01b.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (n2 != 1) break;
                string = "herald_naran_q0615_03.htm";
                break;
            }
            case 31379: {
                if (n2 == 1) {
                    string = "shaman_udan_q0615_01.htm";
                    questState.setCond(2);
                    break;
                }
                if (n2 == 2 && n3 == 1) {
                    string = "shaman_udan_q0615_03.htm";
                    npcInstance.doCast(SkillTable.getInstance().getInfo(4548, 1), questState.getPlayer(), true);
                    questState.set("proval", "0");
                    break;
                }
                if (n2 != 3 || questState.getQuestItemsCount(7242) < 1L) break;
                string = "shaman_udan_q0615_04.htm";
                questState.takeItems(7242, questState.getQuestItemsCount(7242));
                questState.giveItems(7243, 1L);
                questState.giveItems(7081, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                break;
            }
            case 31559: {
                if (n2 != 2) break;
                string = "asefas_box_q0615_01.htm";
            }
        }
        return string;
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getCond();
        int n2 = questState.getInt("proval");
        if (n == 2 && n2 == 0) {
            npcInstance.doCast(SkillTable.getInstance().getInfo(4547, 1), questState.getPlayer(), true);
            questState.set("proval", "1");
        }
        return null;
    }
}

