/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.HashMap;
import java.util.Map;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _378_MagnificentFeast
extends Quest
implements ScriptFile {
    private static int bxm = 30594;
    private static int bxn = 5956;
    private static int bxo = 5957;
    private static int bxp = 5958;
    private static int bxq = 4421;
    private static int bxr = 5959;
    private static int bxs = 1455;
    private static int bxt = 1456;
    private static int bxu = 1457;
    private Map<Integer, int[]> bxv = new HashMap<Integer, int[]>();

    public _378_MagnificentFeast() {
        super(0);
        this.addStartNpc(bxm);
        this.bxv.put(9, new int[]{847, 1, 5700});
        this.bxv.put(10, new int[]{846, 2, 0});
        this.bxv.put(12, new int[]{909, 1, 25400});
        this.bxv.put(17, new int[]{846, 2, 1200});
        this.bxv.put(18, new int[]{879, 1, 6900});
        this.bxv.put(20, new int[]{890, 2, 8500});
        this.bxv.put(33, new int[]{879, 1, 8100});
        this.bxv.put(34, new int[]{910, 1, 0});
        this.bxv.put(36, new int[]{910, 1, 0});
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getState();
        int n2 = questState.getCond();
        int n3 = questState.getInt("score");
        if (string.equalsIgnoreCase("quest_accept") && n == 1) {
            string2 = "warehouse_chief_ranspo_q0378_03.htm";
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("378_1") && n == 2) {
            if (n2 == 1 && questState.getQuestItemsCount(bxn) > 0L) {
                string2 = "warehouse_chief_ranspo_q0378_05.htm";
                questState.takeItems(bxn, 1L);
                questState.setCond(2);
                questState.set("score", String.valueOf(n3 + 1));
            } else {
                string2 = "warehouse_chief_ranspo_q0378_08.htm";
            }
        } else if (string.equalsIgnoreCase("378_2") && n == 2) {
            if (n2 == 1 && questState.getQuestItemsCount(bxo) > 0L) {
                string2 = "warehouse_chief_ranspo_q0378_06.htm";
                questState.takeItems(bxo, 1L);
                questState.setCond(2);
                questState.set("score", String.valueOf(n3 + 2));
            } else {
                string2 = "warehouse_chief_ranspo_q0378_08.htm";
            }
        } else if (string.equalsIgnoreCase("378_3") && n == 2) {
            if (n2 == 1 && questState.getQuestItemsCount(bxp) > 0L) {
                string2 = "warehouse_chief_ranspo_q0378_07.htm";
                questState.takeItems(bxp, 1L);
                questState.setCond(2);
                questState.set("score", String.valueOf(n3 + 4));
            } else {
                string2 = "warehouse_chief_ranspo_q0378_08.htm";
            }
        } else if (string.equalsIgnoreCase("378_5") && n == 2) {
            if (n2 == 2 && questState.getQuestItemsCount(bxq) > 0L) {
                string2 = "warehouse_chief_ranspo_q0378_12.htm";
                questState.takeItems(bxq, 1L);
                questState.setCond(3);
            } else {
                string2 = "warehouse_chief_ranspo_q0378_10.htm";
            }
        } else if (string.equalsIgnoreCase("378_6") && n == 2) {
            if (n2 == 3 && questState.getQuestItemsCount(bxs) > 0L) {
                string2 = "warehouse_chief_ranspo_q0378_14.htm";
                questState.takeItems(bxs, 1L);
                questState.setCond(4);
                questState.set("score", String.valueOf(n3 + 8));
            } else {
                string2 = "warehouse_chief_ranspo_q0378_17.htm";
            }
        } else if (string.equalsIgnoreCase("378_7") && n == 2) {
            if (n2 == 3 && questState.getQuestItemsCount(bxt) > 0L) {
                string2 = "warehouse_chief_ranspo_q0378_15.htm";
                questState.takeItems(bxt, 1L);
                questState.setCond(4);
                questState.set("score", String.valueOf(n3 + 16));
            } else {
                string2 = "warehouse_chief_ranspo_q0378_17.htm";
            }
        } else if (string.equalsIgnoreCase("378_8") && n == 2) {
            if (n2 == 3 && questState.getQuestItemsCount(bxu) > 0L) {
                string2 = "warehouse_chief_ranspo_q0378_16.htm";
                questState.takeItems(bxu, 1L);
                questState.setCond(4);
                questState.set("score", String.valueOf(n3 + 32));
            } else {
                string2 = "warehouse_chief_ranspo_q0378_17.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        if (npcInstance.getNpcId() != bxm) {
            return string;
        }
        int n = questState.getState();
        int n2 = questState.getCond();
        if (n == 1) {
            if (questState.getPlayer().getLevel() < 20) {
                string = "warehouse_chief_ranspo_q0378_01.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "warehouse_chief_ranspo_q0378_02.htm";
                questState.setCond(0);
            }
        } else if (n2 == 1 && n == 2) {
            string = "warehouse_chief_ranspo_q0378_04.htm";
        } else if (n2 == 2 && n == 2) {
            string = questState.getQuestItemsCount(bxq) > 0L ? "warehouse_chief_ranspo_q0378_11.htm" : "warehouse_chief_ranspo_q0378_10.htm";
        } else if (n2 == 3 && n == 2) {
            string = "warehouse_chief_ranspo_q0378_13.htm";
        } else if (n2 == 4 && n == 2) {
            int[] nArray = this.bxv.get(questState.getInt("score"));
            if (questState.getQuestItemsCount(bxr) > 0L && nArray != null) {
                string = "warehouse_chief_ranspo_q0378_20.htm";
                questState.takeItems(bxr, 1L);
                questState.giveItems(nArray[0], nArray[1]);
                if (nArray[2] > 0) {
                    questState.giveItems(57, nArray[2]);
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string = "warehouse_chief_ranspo_q0378_19.htm";
            }
        }
        return string;
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

