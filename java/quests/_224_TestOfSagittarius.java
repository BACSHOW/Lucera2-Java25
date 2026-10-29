/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _224_TestOfSagittarius
extends Quest
implements ScriptFile {
    private static final int aTn = 3294;
    private static final int aTo = 3297;
    private static final int aTp = 3299;
    private static final int aTq = 3293;
    private static final int aTr = 3028;
    private static final int aTs = 3300;
    private static final int aTt = 3306;
    private static final int aTu = 3295;
    private static final int aTv = 3296;
    private static final int aTw = 3298;
    private static final int aTx = 3301;
    private static final int aTy = 3302;
    private static final int aTz = 3303;
    private static final int aTA = 3304;
    private static final int aTB = 3305;
    private static final int aTC = 17;
    private static final int aKb = 54726;
    private static final int aKc = 20250;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _224_TestOfSagittarius() {
        super(0);
        this.addStartNpc(30702);
        this.addTalkId(30514);
        this.addTalkId(30626);
        this.addTalkId(30653);
        this.addTalkId(30702);
        this.addTalkId(30717);
        this.addKillId(20230);
        this.addKillId(20232);
        this.addKillId(20233);
        this.addKillId(20234);
        this.addKillId(20269);
        this.addKillId(20270);
        this.addKillId(27090);
        this.addKillId(20551);
        this.addKillId(20563);
        this.addKillId(20577);
        this.addKillId(20578);
        this.addKillId(20579);
        this.addKillId(20580);
        this.addKillId(20581);
        this.addKillId(20582);
        this.addKillId(20079);
        this.addKillId(20080);
        this.addKillId(20081);
        this.addKillId(20082);
        this.addKillId(20084);
        this.addKillId(20086);
        this.addKillId(20089);
        this.addKillId(20090);
        this.addQuestItem(3299, 3028, 3300, 3306, 3294, 3298, 3295, 3301, 3296, 3297, 3302, 3303, 3304, 3305);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            string2 = "30702-04.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(3294, 1L);
        } else if (string.equals("30626_1")) {
            string2 = "30626-02.htm";
        } else if (string.equals("30626_2")) {
            string2 = "30626-03.htm";
            questState.takeItems(3294, questState.getQuestItemsCount(3294));
            questState.giveItems(3295, 1L);
            questState.setCond(2);
        } else if (string.equals("30626_3")) {
            string2 = "30626-06.htm";
        } else if (string.equals("30626_4")) {
            string2 = "30626-07.htm";
            questState.takeItems(3298, questState.getQuestItemsCount(3298));
            questState.giveItems(3296, 1L);
            questState.setCond(5);
        } else if (string.equals("30653_1")) {
            string2 = "30653-02.htm";
            questState.takeItems(3295, questState.getQuestItemsCount(3295));
            questState.setCond(3);
        } else if (string.equals("30514_1")) {
            string2 = "30514-02.htm";
            questState.takeItems(3296, questState.getQuestItemsCount(3296));
            questState.setCond(6);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(3293) > 0L) {
            questState.exitCurrentQuest(true);
            return "completed";
        }
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        if (n2 == 1) {
            questState.setState(2);
            questState.setCond(0);
            questState.set("id", "0");
        }
        if (n == 30702 && questState.getCond() == 0) {
            if (questState.getPlayer().getClassId().getId() == 7 || questState.getPlayer().getClassId().getId() == 22 || questState.getPlayer().getClassId().getId() == 35) {
                if (questState.getPlayer().getLevel() >= 39) {
                    string = "30702-03.htm";
                } else {
                    string = "30702-01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else {
                string = "30702-02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30702 && questState.getCond() == 1 && questState.getQuestItemsCount(3294) > 0L) {
            string = "30702-05.htm";
        } else if (n == 30626 && questState.getCond() == 1 && questState.getQuestItemsCount(3294) > 0L) {
            string = "30626-01.htm";
        } else if (n == 30626 && questState.getCond() == 2 && questState.getQuestItemsCount(3295) > 0L) {
            string = "30626-04.htm";
        } else if (n == 30626 && questState.getCond() == 4 && questState.getQuestItemsCount(3298) == 10L) {
            string = "30626-05.htm";
        } else if (n == 30626 && questState.getCond() == 5 && questState.getQuestItemsCount(3296) > 0L) {
            string = "30626-08.htm";
        } else if (n == 30626 && questState.getCond() == 8) {
            string = "30626-09.htm";
            questState.giveItems(3297, 1L);
            questState.setCond(9);
        } else if (n == 30626 && questState.getCond() == 9 && questState.getQuestItemsCount(3297) > 0L) {
            string = "30626-10.htm";
        } else if (n == 30626 && questState.getCond() == 12 && questState.getQuestItemsCount(3028) > 0L) {
            string = "30626-11.htm";
            questState.setCond(13);
        } else if (n == 30626 && questState.getCond() == 13) {
            string = "30626-12.htm";
        } else if (n == 30626 && questState.getCond() == 14 && questState.getQuestItemsCount(3300) > 0L) {
            string = "30626-13.htm";
            questState.takeItems(3028, -1L);
            questState.takeItems(3300, -1L);
            questState.takeItems(3306, -1L);
            questState.giveItems(3293, 1L);
            if (!questState.getPlayer().getVarB("prof2.3")) {
                questState.giveItems(7562, 8L);
                questState.addExpAndSp(54726L, 20250L);
                questState.getPlayer().setVar("prof2.3", "1", -1L);
            }
            questState.playSound("ItemSound.quest_finish");
            questState.unset("cond");
            questState.exitCurrentQuest(false);
        } else if (n == 30653 && questState.getCond() == 2 && questState.getQuestItemsCount(3295) > 0L) {
            string = "30653-01.htm";
        } else if (n == 30653 && questState.getCond() == 3) {
            string = "30653-03.htm";
        } else if (n == 30514 && questState.getCond() == 5 && questState.getQuestItemsCount(3296) > 0L) {
            string = "30514-01.htm";
        } else if (n == 30514 && questState.getCond() == 6) {
            string = "30514-03.htm";
        } else if (n == 30514 && questState.getCond() == 7 && questState.getQuestItemsCount(3301) > 0L) {
            string = "30514-04.htm";
            questState.takeItems(3301, questState.getQuestItemsCount(3301));
            questState.setCond(8);
        } else if (n == 30514 && questState.getCond() == 8) {
            string = "30514-05.htm";
        } else if (n == 30717 && questState.getCond() == 9 && questState.getQuestItemsCount(3297) > 0L) {
            string = "30717-01.htm";
            questState.takeItems(3297, questState.getQuestItemsCount(3297));
            questState.setCond(10);
        } else if (n == 30717 && questState.getCond() == 10) {
            string = "30717-03.htm";
        } else if (n == 30717 && questState.getCond() == 12) {
            string = "30717-04.htm";
        } else if (n == 30717 && questState.getCond() == 11 && questState.getQuestItemsCount(3303) > 0L && questState.getQuestItemsCount(3302) > 0L && questState.getQuestItemsCount(3304) > 0L && questState.getQuestItemsCount(3305) > 0L) {
            string = "30717-02.htm";
            questState.takeItems(3302, questState.getQuestItemsCount(3302));
            questState.takeItems(3303, questState.getQuestItemsCount(3303));
            questState.takeItems(3304, questState.getQuestItemsCount(3304));
            questState.takeItems(3305, questState.getQuestItemsCount(3305));
            questState.giveItems(3028, 1L);
            questState.giveItems(17, 10L);
            questState.setCond(12);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20079 || n == 20080 || n == 20081 || n == 20084 || n == 20086 || n == 20089 || n == 20090) {
            if (questState.getCond() == 3 && questState.getQuestItemsCount(3298) < 10L && Rnd.chance(50)) {
                questState.giveItems(3298, 1L);
                if (questState.getQuestItemsCount(3298) == 10L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20269 || n == 20270) {
            if (questState.getCond() == 6 && questState.getQuestItemsCount(3299) < 10L && Rnd.chance(50)) {
                questState.giveItems(3299, 1L);
                if (questState.getQuestItemsCount(3299) == 10L) {
                    questState.takeItems(3299, 10L);
                    questState.giveItems(3301, 1L);
                    questState.setCond(7);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20230 || n == 20232 || n == 20234) {
            if (questState.getCond() == 10 && questState.getQuestItemsCount(3303) == 0L && Rnd.chance(10)) {
                questState.giveItems(3303, 1L);
                if (questState.getQuestItemsCount(3302) > 0L && questState.getQuestItemsCount(3304) > 0L && questState.getQuestItemsCount(3305) > 0L) {
                    questState.setCond(11);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20563) {
            if (questState.getCond() == 10 && questState.getQuestItemsCount(3305) == 0L && Rnd.chance(10)) {
                questState.giveItems(3305, 1L);
                if (questState.getQuestItemsCount(3302) > 0L && questState.getQuestItemsCount(3304) > 0L && questState.getQuestItemsCount(3303) > 0L) {
                    questState.setCond(11);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20233) {
            if (questState.getCond() == 10 && questState.getQuestItemsCount(3304) == 0L && Rnd.chance(10)) {
                questState.giveItems(3304, 1L);
                if (questState.getQuestItemsCount(3302) > 0L && questState.getQuestItemsCount(3305) > 0L && questState.getQuestItemsCount(3303) > 0L) {
                    questState.setCond(11);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20551) {
            if (questState.getCond() == 10 && questState.getQuestItemsCount(3302) == 0L && Rnd.chance(10)) {
                questState.giveItems(3302, 1L);
                if (questState.getQuestItemsCount(3304) > 0L && questState.getQuestItemsCount(3305) > 0L && questState.getQuestItemsCount(3303) > 0L) {
                    questState.setCond(11);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20551) {
            if (questState.getCond() == 10 && questState.getQuestItemsCount(3302) == 0L && Rnd.chance(10)) {
                if (questState.getQuestItemsCount(3304) > 0L && questState.getQuestItemsCount(3305) > 0L && questState.getQuestItemsCount(3303) > 0L) {
                    questState.giveItems(3302, 1L);
                    questState.setCond(11);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.giveItems(3302, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20577 || n == 20578 || n == 20579 || n == 20580 || n == 20581 || n == 20582) {
            if (questState.getCond() == 13) {
                if (Rnd.chance((questState.getQuestItemsCount(3306) - 120L) * 5L)) {
                    questState.addSpawn(27090);
                    questState.takeItems(3306, questState.getQuestItemsCount(3306));
                    questState.playSound("Itemsound.quest_before_battle");
                } else {
                    questState.giveItems(3306, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 27090 && questState.getCond() == 13 && questState.getQuestItemsCount(3300) == 0L) {
            if (questState.getItemEquipped(7) == 3028) {
                questState.giveItems(3300, 1L);
                questState.setCond(14);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.addSpawn(27090);
            }
        }
        return null;
    }
}

