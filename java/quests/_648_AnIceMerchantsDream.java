/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.ArrayList;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._115_TheOtherSideOfTruth;

public class _648_AnIceMerchantsDream
extends Quest
implements ScriptFile {
    private static int bTL = 32020;
    private static int bTM = 32023;
    private static int bTN = 8057;
    private static int bTO = 8077;
    private static int bTP = 8078;
    private static int bTQ = 10;
    private static int bTR = 30;
    private static List<Integer> bTS = new ArrayList<Integer>();

    public _648_AnIceMerchantsDream() {
        super(1);
        this.addStartNpc(bTL);
        this.addStartNpc(bTM);
        for (int i = 22080; i <= 22098; ++i) {
            if (i == 22095) continue;
            this.addKillId(i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        if (string.equalsIgnoreCase("repre_q0648_04.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("repre_q0648_22.htm") && n == 2) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        if (n != 2) {
            return string;
        }
        long l = questState.getQuestItemsCount(bTO);
        long l2 = questState.getQuestItemsCount(bTP);
        if (string.equalsIgnoreCase("repre_q0648_14.htm")) {
            long l3 = l * 300L + l2 * 1200L;
            if (l3 <= 0L) return "repre_q0648_15.htm";
            questState.takeItems(bTO, -1L);
            questState.takeItems(bTP, -1L);
            questState.giveItems(57, l3);
            return string;
        }
        if (string.equalsIgnoreCase("ice_lathe_q0648_06.htm")) {
            int n2 = questState.getPlayer().getObjectId();
            List<Integer> list = bTS;
            synchronized (list) {
                if (bTS.contains(n2)) {
                    return string;
                }
                if (l <= 0L) {
                    return "cheat.htm";
                }
                bTS.add(n2);
            }
            questState.takeItems(bTO, 1L);
            questState.playSound("ItemSound2.broken_key");
            return string;
        }
        if (!string.equalsIgnoreCase("ice_lathe_q0648_08.htm")) return string;
        Integer n3 = questState.getPlayer().getObjectId();
        List<Integer> list = bTS;
        synchronized (list) {
            if (!bTS.contains(n3)) return "cheat.htm";
            while (bTS.contains(n3)) {
                bTS.remove(n3);
            }
        }
        if (Rnd.chance(bTR)) {
            questState.giveItems(bTP, 1L);
            questState.playSound("ItemSound3.sys_enchant_sucess");
            return string;
        } else {
            questState.playSound("ItemSound3.sys_enchant_failed");
            return "ice_lathe_q0648_09.htm";
        }
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getCond();
        if (n == 1) {
            if (n2 == bTL) {
                if (questState.getPlayer().getLevel() >= 53) {
                    questState.setCond(0);
                    return "repre_q0648_03.htm";
                }
                questState.exitCurrentQuest(true);
                return "repre_q0648_01.htm";
            }
            if (n2 == bTM) {
                return "ice_lathe_q0648_01.htm";
            }
        }
        if (n != 2) {
            return "noquest";
        }
        long l = questState.getQuestItemsCount(bTO);
        if (n2 == bTM) {
            return l > 0L ? "ice_lathe_q0648_03.htm" : "ice_lathe_q0648_02.htm";
        }
        long l2 = questState.getQuestItemsCount(bTP);
        if (n2 == bTL) {
            QuestState questState2 = questState.getPlayer().getQuestState(_115_TheOtherSideOfTruth.class);
            if (questState2 != null && questState2.isCompleted()) {
                n3 = 2;
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            }
            if (n3 == 1) {
                if (l > 0L || l2 > 0L) {
                    return "repre_q0648_10.htm";
                }
                return "repre_q0648_08.htm";
            }
            if (n3 == 2) {
                return l > 0L || l2 > 0L ? "repre_q0648_11.htm" : "repre_q0648_09.htm";
            }
        }
        return "noquest";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getCond();
        if (n > 0) {
            questState.rollAndGive(bTO, 1, npcInstance.getNpcId() - 22050);
            if (n == 2) {
                questState.rollAndGive(bTN, 1, bTQ);
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

