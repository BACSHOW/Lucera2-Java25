/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _607_ProveYourCourage
extends Quest
implements ScriptFile {
    private static final int bLg = 31370;
    private static final int bLh = 25309;
    private static final int bLi = 7235;
    private static final int bLj = 7219;
    private static final int bLk = 7211;
    private static final int bLl = 7212;
    private static final int bLm = 7213;
    private static final int bLn = 7214;
    private static final int bLo = 7215;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _607_ProveYourCourage() {
        super(1);
        this.addStartNpc(31370);
        this.addKillId(25309);
        this.addQuestItem(7235);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("quest_accept")) {
            string2 = "elder_kadun_zu_ketra_q0607_0104.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("607_3")) {
            if (questState.getQuestItemsCount(7235) >= 1L) {
                string2 = "elder_kadun_zu_ketra_q0607_0201.htm";
                questState.takeItems(7235, -1L);
                questState.giveItems(7219, 1L);
                questState.addExpAndSp(0L, 10000L);
                questState.unset("cond");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string2 = "elder_kadun_zu_ketra_q0607_0106.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            if (questState.getPlayer().getLevel() >= 75) {
                if (questState.getQuestItemsCount(7213) == 1L || questState.getQuestItemsCount(7214) == 1L || questState.getQuestItemsCount(7215) == 1L) {
                    string = "elder_kadun_zu_ketra_q0607_0101.htm";
                } else {
                    string = "elder_kadun_zu_ketra_q0607_0102.htm";
                    questState.exitCurrentQuest(true);
                }
            } else {
                string = "elder_kadun_zu_ketra_q0607_0103.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 1 && questState.getQuestItemsCount(7235) == 0L) {
            string = "elder_kadun_zu_ketra_q0607_0106.htm";
        } else if (n == 2 && questState.getQuestItemsCount(7235) >= 1L) {
            string = "elder_kadun_zu_ketra_q0607_0105.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 25309 && questState.getCond() == 1) {
            questState.giveItems(7235, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

