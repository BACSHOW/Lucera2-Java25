/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _020_BringUpWithLove
extends Quest
implements ScriptFile {
    private static final int azR = 31537;
    private static final int azS = 7185;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _020_BringUpWithLove() {
        super(0);
        this.addStartNpc(31537);
        this.addTalkId(31537);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("givemelove");
        int n2 = npcInstance.getNpcId();
        if (n2 == 31537) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("givemelove", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "beast_herder_tunatun_q0020_09.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "beast_herder_tunatun_q0020_03.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "beast_herder_tunatun_q0020_04.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "beast_herder_tunatun_q0020_05.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "beast_herder_tunatun_q0020_06.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = "beast_herder_tunatun_q0020_08.htm";
            } else if (string.equalsIgnoreCase("reply_6") && n == 1 && questState.getQuestItemsCount(7185) >= 1L) {
                questState.takeItems(7185, -1L);
                questState.giveItems(57, 68500L);
                questState.unset("givemelove");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
                string2 = "beast_herder_tunatun_q0020_12.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("givemelove");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31537) break;
                if (questState.getPlayer().getLevel() >= 65) {
                    string = "beast_herder_tunatun_q0020_01.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "beast_herder_tunatun_q0020_02.htm";
                break;
            }
            case 2: {
                if (n2 != 31537) break;
                if (n == 1 && questState.getQuestItemsCount(7185) == 0L) {
                    string = "beast_herder_tunatun_q0020_10.htm";
                    break;
                }
                if (n != 1 || questState.getQuestItemsCount(7185) < 1L) break;
                string = "beast_herder_tunatun_q0020_11.htm";
            }
        }
        return string;
    }
}

