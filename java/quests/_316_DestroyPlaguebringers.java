/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

public class _316_DestroyPlaguebringers
extends Quest
implements ScriptFile {
    private static final int bfx = 30155;
    private static final int bfy = 20040;
    private static final int bfz = 20047;
    private static final int bfA = 27020;
    private static final int bfB = 1042;
    private static final int bfC = 1043;

    public _316_DestroyPlaguebringers() {
        super(0);
        this.addStartNpc(30155);
        this.addKillId(20040, 20047, 27020);
        this.addQuestItem(1042, 1043);
        this.addAttackId(27020);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "elliasin_q0316_04.htm";
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("reply_2")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            string2 = "elliasin_q0316_08.htm";
        } else if (string.equalsIgnoreCase("reply_3")) {
            string2 = "elliasin_q0316_09.htm";
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
                if (n != 30155) break;
                if (questState.getPlayer().getRace() != Race.elf) {
                    string = "elliasin_q0316_00.htm";
                    break;
                }
                if (questState.getPlayer().getLevel() >= 18) {
                    string = "elliasin_q0316_03.htm";
                    break;
                }
                string = "elliasin_q0316_02.htm";
                break;
            }
            case 2: {
                if (n != 30155) break;
                if (questState.getQuestItemsCount(1042) >= 1L || questState.getQuestItemsCount(1043) >= 1L) {
                    string = "elliasin_q0316_07.htm";
                    if (questState.getQuestItemsCount(1042) + questState.getQuestItemsCount(1043) >= 10L) {
                        questState.giveItems(57, questState.getQuestItemsCount(1042) * 30L + questState.getQuestItemsCount(1043) * 10000L + 5000L);
                    } else {
                        questState.giveItems(57, questState.getQuestItemsCount(1042) * 30L + questState.getQuestItemsCount(1043) * 10000L);
                    }
                    questState.takeItems(1042, -1L);
                    questState.takeItems(1043, -1L);
                    break;
                }
                string = "elliasin_q0316_05.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if ((n == 20040 || n == 20047) && Rnd.get(10) > 5) {
            questState.rollAndGive(1042, 1, 100.0);
        } else if (n == 27020 && Rnd.get(10) > 7) {
            questState.rollAndGive(1043, 1, 100.0);
        }
        return null;
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("cry");
        if (n == 0) {
            Functions.npcSayCustomMessage(npcInstance, "WHY_DO_YOU_OPPRESS_US_SO", new Object[0]);
            questState.set("cry", 1);
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

