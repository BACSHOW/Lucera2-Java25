/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _274_SkirmishWithTheWerewolves
extends Quest
implements ScriptFile {
    private static final int beh = 1477;
    private static final int bei = 1507;
    private static final int bej = 1506;
    private static final int bek = 1501;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _274_SkirmishWithTheWerewolves() {
        super(0);
        this.addStartNpc(30569);
        this.addKillId(20363);
        this.addKillId(20364);
        this.addQuestItem(1477);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("prefect_brukurse_q0274_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getState();
        int n2 = questState.getCond();
        if (n == 1) {
            if (questState.getPlayer().getRace() != Race.orc) {
                string = "prefect_brukurse_q0274_00.htm";
                questState.exitCurrentQuest(true);
            } else if (questState.getPlayer().getLevel() < 9) {
                string = "prefect_brukurse_q0274_01.htm";
                questState.exitCurrentQuest(true);
            } else {
                if (questState.getQuestItemsCount(1507) > 0L || questState.getQuestItemsCount(1506) > 0L) {
                    string = "prefect_brukurse_q0274_02.htm";
                    return string;
                }
                string = "prefect_brukurse_q0274_07.htm";
            }
        } else if (n2 == 1) {
            string = "prefect_brukurse_q0274_04.htm";
        } else if (n2 == 2) {
            if (questState.getQuestItemsCount(1477) < 40L) {
                string = "prefect_brukurse_q0274_04.htm";
            } else {
                questState.takeItems(1477, -1L);
                questState.giveItems(57, 3500L, true);
                if (questState.getQuestItemsCount(1501) >= 1L) {
                    questState.giveItems(57, questState.getQuestItemsCount(1501) * 600L, true);
                    questState.takeItems(1501, -1L);
                }
                string = "prefect_brukurse_q0274_05.htm";
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1 && questState.getQuestItemsCount(1477) < 40L) {
            if (questState.getQuestItemsCount(1477) < 39L) {
                questState.playSound("ItemSound.quest_itemget");
            } else {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            }
            questState.giveItems(1477, 1L);
        }
        if (Rnd.chance(5)) {
            questState.giveItems(1501, 1L);
        }
        return null;
    }
}

