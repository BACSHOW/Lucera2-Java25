/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _306_CrystalOfFireice
extends Quest
implements ScriptFile {
    private static final int bfo = 30004;
    private static final int bfp = 20109;
    private static final int bfq = 20110;
    private static final int bfr = 20112;
    private static final int bfs = 20113;
    private static final int bft = 20114;
    private static final int bfu = 20115;
    private static final int bfv = 1020;
    private static final int bfw = 1021;

    public _306_CrystalOfFireice() {
        super(0);
        this.addStartNpc(30004);
        this.addKillId(20109, 20110, 20112, 20113, 20114, 20115);
        this.addQuestItem(1020);
        this.addQuestItem(1021);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setState(2);
            questState.setCond(1);
            string2 = "katrine_q0306_04.htm";
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("reply_2")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            string2 = "katrine_q0306_08.htm";
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
                if (n != 30004) break;
                if (questState.getPlayer().getLevel() >= 17) {
                    string = "katrine_q0306_03.htm";
                    break;
                }
                string = "katrine_q0306_02.htm";
                break;
            }
            case 2: {
                if (n != 30004) break;
                if (questState.getCond() == 1 && questState.getQuestItemsCount(1020) == 0L && questState.getQuestItemsCount(1021) == 0L) {
                    string = "katrine_q0306_05.htm";
                    break;
                }
                if (questState.getCond() != 1 || questState.getQuestItemsCount(1020) <= 0L && questState.getQuestItemsCount(1021) <= 0L) break;
                if (questState.getQuestItemsCount(1020) + questState.getQuestItemsCount(1021) >= 10L) {
                    questState.giveItems(57, 40L * questState.getQuestItemsCount(1020) + 40L * questState.getQuestItemsCount(1021) + 5000L);
                } else {
                    questState.giveItems(57, 40L * questState.getQuestItemsCount(1020) + 40L * questState.getQuestItemsCount(1021));
                }
                questState.takeItems(1020, -1L);
                questState.takeItems(1021, -1L);
                string = "katrine_q0306_07.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20109 && Rnd.get(1000) < 925) {
            questState.rollAndGive(1020, 1, 100.0);
        } else if (n == 20110 && Rnd.get(100) < 90) {
            questState.rollAndGive(1021, 1, 100.0);
        } else if (n == 20112 && Rnd.get(100) < 90) {
            questState.rollAndGive(1020, 1, 100.0);
        } else if (n == 20113 && Rnd.get(1000) < 925) {
            questState.rollAndGive(1021, 1, 100.0);
        } else if (n == 20114 && Rnd.get(1000) < 925) {
            questState.rollAndGive(1020, 1, 100.0);
        } else if (n == 20115 && Rnd.get(100) < 95) {
            questState.rollAndGive(1021, 1, 100.0);
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

