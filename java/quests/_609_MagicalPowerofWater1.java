/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;

public class _609_MagicalPowerofWater1
extends Quest
implements ScriptFile {
    private static final int WAHKAN = 31371;
    private static final int bLs = 31372;
    private static final int bLt = 31561;
    private static final int bLu = 7237;
    private static final int bLv = 7081;
    private static final int bLw = 7238;
    private static final int bLl = 7212;
    private static final int bLm = 7213;
    private static final int bLn = 7214;
    private static final int bLo = 7215;
    private static final int bLx = 1661;
    private final int[] bLy = new int[20];

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _609_MagicalPowerofWater1() {
        super(0);
        this.addStartNpc(31371);
        this.addTalkId(31372);
        this.addTalkId(31561);
        this.bLy[0] = 21350;
        this.bLy[1] = 21351;
        this.bLy[2] = 21353;
        this.bLy[3] = 21354;
        this.bLy[4] = 21355;
        this.bLy[5] = 21357;
        this.bLy[6] = 21358;
        this.bLy[7] = 21360;
        this.bLy[8] = 21361;
        this.bLy[9] = 21362;
        this.bLy[10] = 21364;
        this.bLy[11] = 21365;
        this.bLy[12] = 21366;
        this.bLy[13] = 21368;
        this.bLy[14] = 21369;
        this.bLy[15] = 21370;
        this.bLy[16] = 21371;
        this.bLy[17] = 21372;
        this.bLy[18] = 21373;
        this.bLy[19] = 21374;
        for (int n : this.bLy) {
            this.addAttackId(n);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("quest_accept")) {
            string2 = "herald_wakan_q0609_02.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("609_1") && questState.getCond() == 2) {
            if (questState.getQuestItemsCount(1661) < 1L) {
                string2 = "udans_box_q0609_02.htm";
            } else if (questState.getInt("proval") == 1) {
                string2 = "udans_box_q0609_04.htm";
                questState.takeItems(1661, 1L);
            } else {
                questState.takeItems(1661, 1L);
                questState.giveItems(7237, 1L);
                string2 = "udans_box_q0609_03.htm";
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
        if (n == 31371) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 74) {
                    if (questState.getQuestItemsCount(7212) == 1L || questState.getQuestItemsCount(7213) == 1L || questState.getQuestItemsCount(7214) == 1L || questState.getQuestItemsCount(7215) == 1L) {
                        if (questState.getQuestItemsCount(7081) == 0L) {
                            string = "herald_wakan_q0609_01.htm";
                        } else {
                            string = "completed";
                            questState.exitCurrentQuest(true);
                        }
                    } else {
                        string = "herald_wakan_q0609_01a.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "herald_wakan_q0609_01b.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "herald_wakan_q0609_03.htm";
            }
        } else if (n == 31372) {
            if (n2 == 1) {
                string = "shaman_asefa_q0609_01.htm";
                questState.setCond(2);
            } else if (n2 == 2 && n3 == 1) {
                string = "shaman_asefa_q0609_03.htm";
                npcInstance.doCast(SkillTable.getInstance().getInfo(4548, 1), questState.getPlayer(), true);
                questState.set("proval", "0");
            } else if (n2 == 3 && questState.getQuestItemsCount(7237) >= 1L) {
                string = "shaman_asefa_q0609_04.htm";
                questState.takeItems(7237, questState.getQuestItemsCount(7237));
                questState.giveItems(7238, 1L);
                questState.giveItems(7081, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        } else if (n == 31561 && n2 == 2) {
            string = "udans_box_q0609_01.htm";
        }
        return string;
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 2 && questState.getInt("proval") == 0) {
            npcInstance.doCast(SkillTable.getInstance().getInfo(4547, 1), questState.getPlayer(), true);
            questState.set("proval", "1");
        }
        return null;
    }
}

