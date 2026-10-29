/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _295_DreamsOfTheSkies
extends Quest
implements ScriptFile {
    public static int FLOATING_STONE = 1492;
    public static int RING_OF_FIREFLY = 1509;
    public static int Arin = 30536;
    public static int MagicalWeaver = 20153;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _295_DreamsOfTheSkies() {
        super(0);
        this.addStartNpc(Arin);
        this.addTalkId(Arin);
        this.addKillId(MagicalWeaver);
        this.addQuestItem(FLOATING_STONE);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("elder_arin_q0295_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n;
        String string = "noquest";
        int n2 = questState.getState();
        if (n2 == 1) {
            questState.setCond(0);
        }
        if ((n = questState.getCond()) == 0) {
            if (questState.getPlayer().getLevel() >= 11) {
                string = "elder_arin_q0295_02.htm";
                return string;
            }
            string = "elder_arin_q0295_01.htm";
            questState.exitCurrentQuest(true);
        } else if (n == 1 || questState.getQuestItemsCount(FLOATING_STONE) < 50L) {
            string = "elder_arin_q0295_04.htm";
        } else if (n == 2 && questState.getQuestItemsCount(FLOATING_STONE) == 50L) {
            questState.addExpAndSp(0L, 500L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            if (questState.getQuestItemsCount(RING_OF_FIREFLY) < 1L) {
                string = "elder_arin_q0295_05.htm";
                questState.giveItems(RING_OF_FIREFLY, 1L);
            } else {
                string = "elder_arin_q0295_06.htm";
                questState.giveItems(57, 2400L);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1 && questState.getQuestItemsCount(FLOATING_STONE) < 50L) {
            if (Rnd.chance(25)) {
                questState.giveItems(FLOATING_STONE, 1L);
                if (questState.getQuestItemsCount(FLOATING_STONE) == 50L) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(2);
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (questState.getQuestItemsCount(FLOATING_STONE) >= 48L) {
                questState.giveItems(FLOATING_STONE, 50L - questState.getQuestItemsCount(FLOATING_STONE));
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            } else {
                questState.giveItems(FLOATING_STONE, 2L);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

