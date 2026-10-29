/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _370_AnElderSowsSeeds
extends Quest
implements ScriptFile {
    private static int btd = 30612;
    private static int[] MOBS = new int[]{20082, 20084, 20086, 20089, 20090};
    private static int bte = 5916;
    private static int btf = 736;
    private static int[] btg = new int[]{5917, 5918, 5919, 5920};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _370_AnElderSowsSeeds() {
        super(0);
        this.addStartNpc(btd);
        for (int n : MOBS) {
            this.addKillId(n);
        }
        this.addQuestItem(bte);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("30612-1.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30612-6.htm")) {
            if (questState.getQuestItemsCount(btg[0]) > 0L && questState.getQuestItemsCount(btg[1]) > 0L && questState.getQuestItemsCount(btg[2]) > 0L && questState.getQuestItemsCount(btg[3]) > 0L) {
                long l = questState.getQuestItemsCount(btg[0]);
                for (int n : btg) {
                    l = Math.min(l, questState.getQuestItemsCount(n));
                }
                for (int n : btg) {
                    questState.takeItems(n, l);
                }
                questState.giveItems(57, 3600L * l);
                string2 = "30612-8.htm";
            } else {
                string2 = "30612-4.htm";
            }
        } else if (string.equalsIgnoreCase("30612-9.htm")) {
            questState.giveItems(btf, 1L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (questState.getState() == 1) {
            if (questState.getPlayer().getLevel() < 28) {
                string = "30612-0a.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "30612-0.htm";
            }
        } else if (n == 1) {
            string = "30612-4.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        if (Rnd.chance(Math.min((int)(15.0 * questState.getRateQuestsReward()), 100))) {
            questState.giveItems(bte, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

