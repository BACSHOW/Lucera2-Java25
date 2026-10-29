/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _613_ProveYourCourage
extends Quest
implements ScriptFile {
    private static final int bLP = 31377;
    private static final int bKI = 25299;
    private static final int Uj = 7223;
    private static final int bLS = 7240;
    private static final int bLH = 7229;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _613_ProveYourCourage() {
        super(1);
        this.addStartNpc(31377);
        this.addKillId(25299);
        this.addQuestItem(7240);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("quest_accept")) {
            questState.setCond(1);
            questState.set("prove_your_courage_varka", String.valueOf(11), true);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "elder_ashas_barka_durai_q0613_0104.htm";
        } else if (string.equals("reply_3")) {
            if (questState.getQuestItemsCount(7240) >= 1L) {
                questState.takeItems(7240, -1L);
                questState.giveItems(7229, 1L);
                questState.addExpAndSp(10000L, 0L);
                questState.unset("prove_your_courage_varka");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "elder_ashas_barka_durai_q0613_0201.htm";
            } else {
                string2 = "elder_ashas_barka_durai_q0613_0202.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("prove_your_courage_varka");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31377) break;
                if (questState.getPlayer().getLevel() >= 75) {
                    if (questState.getQuestItemsCount(7223) >= 1L) {
                        string = "elder_ashas_barka_durai_q0613_0101.htm";
                        break;
                    }
                    questState.exitCurrentQuest(true);
                    string = "elder_ashas_barka_durai_q0613_0102.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "elder_ashas_barka_durai_q0613_0103.htm";
                break;
            }
            case 2: {
                if (n2 != 31377 || n < 11 || n > 12) break;
                string = n == 12 && questState.getQuestItemsCount(7240) >= 1L ? "elder_ashas_barka_durai_q0613_0105.htm" : "elder_ashas_barka_durai_q0613_0106.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        int n2 = questState.getInt("prove_your_courage_varka");
        int n3 = npcInstance.getNpcId();
        if (n2 == 11 && n3 == 25299 && (n = Rnd.get(1000)) < 1000) {
            if (questState.getQuestItemsCount(7240) + 1L >= 1L) {
                if (questState.getQuestItemsCount(7240) < 1L) {
                    questState.setCond(2);
                    questState.set("prove_your_courage_varka", String.valueOf(12), true);
                    questState.giveItems(7240, 1L);
                    questState.playSound("ItemSound.quest_middle");
                }
            } else {
                questState.giveItems(7240, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

