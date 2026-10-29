/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _606_WarwithVarkaSilenos
extends Quest
implements ScriptFile {
    private static final int bKB = 21371;
    private static final int bKv = 21365;
    private static final int bKt = 21369;
    private static final int bKj = 21350;
    private static final int bKm = 21354;
    private static final int bKq = 21360;
    private static final int bKw = 21366;
    private static final int bKx = 21368;
    private static final int bKo = 21357;
    private static final int bKl = 21353;
    private static final int bLc = 21364;
    private static final int bKs = 21362;
    private static final int bKn = 21355;
    private static final int bKp = 21358;
    private static final int bKy = 21373;
    private static final int bLd = 31370;
    private static final int bLe = 7233;
    private static final int bLf = 7186;

    public _606_WarwithVarkaSilenos() {
        super(1);
        this.addStartNpc(31370);
        this.addKillId(21350, 21353, 21354, 21355, 21357, 21358, 21360, 21362, 21364, 21365, 21366, 21368, 21369, 21371, 21373);
        this.addQuestItem(7233);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("quest_accept")) {
            string2 = "elder_kadun_zu_ketra_q0606_0104.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("606_3")) {
            long l = questState.getQuestItemsCount(7233) / 5L;
            if (l > 0L) {
                string2 = "elder_kadun_zu_ketra_q0606_0202.htm";
                questState.takeItems(7233, l * 5L);
                questState.giveItems(7186, l);
            } else {
                string2 = "elder_kadun_zu_ketra_q0606_0203.htm";
            }
        } else if (string.equals("606_4")) {
            string2 = "elder_kadun_zu_ketra_q0606_0204.htm";
            questState.takeItems(7233, -1L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            if (questState.getPlayer().getLevel() >= 74) {
                string = "elder_kadun_zu_ketra_q0606_0101.htm";
            } else {
                string = "elder_kadun_zu_ketra_q0606_0103.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 1 && questState.getQuestItemsCount(7233) == 0L) {
            string = "elder_kadun_zu_ketra_q0606_0106.htm";
        } else if (n == 1 && questState.getQuestItemsCount(7233) > 0L) {
            string = "elder_kadun_zu_ketra_q0606_0105.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (questState.getCond() == 1) {
            int n2;
            if (n == 21371) {
                int n3 = Rnd.get(1000);
                if (n3 < 713) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21365 || n == 21366 || n == 21368) {
                int n4 = Rnd.get(1000);
                if (n4 < 568) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21369) {
                int n5 = Rnd.get(1000);
                if (n5 < 664) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21350) {
                int n6 = Rnd.get(1000);
                if (n6 < 500) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21354) {
                int n7 = Rnd.get(1000);
                if (n7 < 522) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21360 || n == 21362) {
                int n8 = Rnd.get(1000);
                if (n8 < 539) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21357 || n == 21358) {
                int n9 = Rnd.get(1000);
                if (n9 < 529) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21353) {
                int n10 = Rnd.get(1000);
                if (n10 < 510) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21364) {
                int n11 = Rnd.get(1000);
                if (n11 < 558) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21355) {
                int n12 = Rnd.get(1000);
                if (n12 < 519) {
                    questState.rollAndGive(7233, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 21373 && (n2 = Rnd.get(1000)) < 738) {
                questState.rollAndGive(7233, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
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

