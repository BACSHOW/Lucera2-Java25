/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _267_WrathOfVerdure
extends Quest
implements ScriptFile {
    private static final int bdT = 31853;
    private static final int bdU = 20325;
    private static final int bdV = 1335;
    private static final int bdW = 1340;

    public _267_WrathOfVerdure() {
        super(0);
        this.addStartNpc(31853);
        this.addKillId(20325);
        this.addQuestItem(1335);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
            string2 = "bri_mec_tran_q0267_03.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            string2 = "bri_mec_tran_q0267_06.htm";
        } else if (string.equalsIgnoreCase("reply_2")) {
            string2 = "bri_mec_tran_q0267_07.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        Player player = questState.getPlayer();
        switch (n) {
            case 1: {
                if (player.getLevel() >= 4 && player.getRace() == Race.elf) {
                    string = "bri_mec_tran_q0267_02.htm";
                    break;
                }
                if (player.getRace() != Race.elf) {
                    string = "bri_mec_tran_q0267_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (player.getLevel() >= 4) break;
                string = "bri_mec_tran_q0267_01.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(1335) > 0L) {
                    questState.giveItems(1340, questState.getQuestItemsCount(1335));
                    if (questState.getQuestItemsCount(1335) >= 10L) {
                        questState.giveItems(57, 600L);
                    }
                    questState.takeItems(1335, -1L);
                    string = "bri_mec_tran_q0267_05.htm";
                    break;
                }
                string = "bri_mec_tran_q0267_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (Rnd.get(10) < 5) {
            questState.rollAndGive(1335, 1, 100.0);
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

